/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.openoffice;

import static com.sebulli.fakturama.Translate._;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextContentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextDocumentImage;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.document.URLAdapter;
import ag.ion.noa.frame.IDispatchDelegate;
import ag.ion.noa.graphic.GraphicInfo;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.Workspace;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.calculate.Price;
import com.sebulli.fakturama.calculate.VatSummaryItem;
import com.sebulli.fakturama.calculate.VatSummarySet;
import com.sebulli.fakturama.calculate.VatSummarySetManager;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetContact;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.logger.Logger;
import com.sun.star.awt.XTopWindow;
import com.sun.star.frame.XFrame;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.VertOrientation;
import com.sun.star.uno.UnoRuntime;

/**
 * This class opens an OpenOffice Writer template and replaces all the
 * placeholders with the document data.
 * 
 * @author Gerd Bartelt
 */
public class OODocument extends Object {

	
	static boolean WITH_FILENAME = true;
	static boolean NO_FILENAME = false;
	static boolean WITH_EXTENSION = true;
	static boolean NO_EXTENSION = false;
	static boolean PDF = true;
	static boolean NO_PDF = false;
	
	
	
	// The UniDataSet document, that is used to fill the OpenOffice document 
	private DataSetDocument document;

	// The UniDataSet contact of the document
	private DataSetContact contact;

	// A list of properties that represents the placeholders of the
	// OpenOffice Writer template
	private Properties properties;

	// OpenOffice objects
	private IOfficeApplication officeApplication;
	private IDocument oOdocument;
	private ITextDocument textDocument;
	private IFrame officeFrame;

	private ITextFieldService textFieldService;

	// Template name
	private String template;	
	
	/**
	 * Constructor Create a new OpenOffice document. Open it by using a template
	 * and replace the placehlders with the UniDataSet document
	 * 
	 * @param document
	 *            The UniDataSet document that will be converted to an
	 *            OpenOffice Writer document
	 * @param template
	 *            OpenOffice template file name
	 */
	public OODocument(DataSetDocument document, String template, boolean forceRecreation) {

		// URL of the template file
		String url = null;
		this.template = template;
		//Open an existing document instead of creating a new one
		boolean openExisting = false;

		// Set a reference to the UniDatSet document
		this.document = document;

		// Try to generate the OpenOffice document
		try {

			// Get the OpenOffice application
			officeApplication = OpenOfficeStarter.openOfficeApplication();
			if (officeApplication == null)
				return;
			
			// Check, whether there is already a document then do not 
			// generate one by the data, but open the existing one.
			if (testOpenAsExisting(document, template) && !forceRecreation) {
				openExisting = true;
				template = getDocumentPath(WITH_FILENAME, WITH_EXTENSION, NO_PDF, document);
			}
			
			// Get the template file (*ott)
			try {
				url = URLAdapter.adaptURL(template);
			}
			catch (Exception e) {
				Logger.logError(e, "Error in template filename:" + template);
			}

			// Load the template
			oOdocument = officeApplication.getDocumentService().loadDocument(url);
			textDocument = (ITextDocument) oOdocument;

			// Bring the open office window on top.
			officeFrame = textDocument.getFrame();
			XFrame xFrame = officeFrame.getXFrame();
			XTopWindow topWindow = (XTopWindow) UnoRuntime.queryInterface(XTopWindow.class, xFrame.getContainerWindow());
			topWindow.toFront();
			xFrame.activate();

			// Override the "SAVE" command of the OpenOffice application
			officeFrame.addDispatchDelegate(GlobalCommands.SAVE, new IDispatchDelegate() {

				@Override
				public void dispatch(Object[] objects) {

					// Save the document as *.odt and *.pdf
					saveOODocument(textDocument);

				}

			});
			officeFrame.updateDispatches();

			// Stop here and do not fill the document's placeholders, if it's an existing document
			if (openExisting)
				return;

			// Get the contact of the UniDataSet document
			int addressId = document.getIntValueByKey("addressid");

			contact = null;
			if (addressId >= 0) {
				try {
					contact = Data.INSTANCE.getContacts().getDatasetById(addressId);
				}
				catch (Exception e) {
				}
			}

			// Recalculate the sum of the document before exporting
			this.document.calculate();

			// Get the placeholders of the OpenOffice template
			textFieldService = textDocument.getTextFieldService();
			ITextField[] placeholders = textFieldService.getPlaceholderFields();

			// Fill the property list with the placeholder values
			properties = new Properties();
			setCommonProperties();

			// A reference to the item and vat table
			ITextTable itemsTable = null;
			ITextTable vatListTable = null;
			ITextTableCell itemCell = null;
			ITextTableCell vatListCell = null;
			ArrayList<ITextTableCell> discountCellList = new ArrayList<ITextTableCell>();

			// Scan all placeholders to find the item and the vat table
			for (int i = 0; i < placeholders.length; i++) {

				// Get the placeholder's text
				ITextField placeholder = placeholders[i];
				String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();

				// Find the item table
				if (placeholderDisplayText.equals("<ITEM.NAME>") || placeholderDisplayText.equals("<ITEM.DESCRIPTION>")) {
					itemCell = placeholder.getTextRange().getCell();
					itemsTable = itemCell.getTextTable();
				}

				// Find the vat table
				if (placeholderDisplayText.equals("<VATLIST.VALUES>") || placeholderDisplayText.equals("<VATLIST.DESCRIPTIONS>")) {
					vatListCell = placeholder.getTextRange().getCell();
					vatListTable = vatListCell.getTextTable();
				}

				// Find the discount placeholders
				if (placeholderDisplayText.startsWith("<ITEMS.DISCOUNT.")) {
					discountCellList.add(placeholder.getTextRange().getCell());
				}

			}

			// Get the items of the UniDataSet document
			ArrayList<DataSetItem> itemDataSets = document.getItems().getActiveDatasets();
			int lastItemTemplateRow = 0;
			int lastVatTemplateRow = 0;

			// Fill the item table with the items
			if (itemsTable != null) {

				// Add the necessary rows for the items
				int itemCellRow = itemCell.getName().getRowIndex();
				lastItemTemplateRow = itemCellRow + itemDataSets.size();
				itemsTable.addRow(itemCellRow, itemDataSets.size());

				for (int i = 0; i < placeholders.length; i++) {

					// Get each placeholder
					ITextField placeholder = placeholders[i];
					String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();

					if (placeholder.getTextRange().getCell() != null) {

						// Do it only, if the placeholder is in the items table
						ITextTable textTable = placeholder.getTextRange().getCell().getTextTable();
						if (textTable.getName().equals(itemsTable.getName())) {

							// Fill the corresponding table column with the
							// item's data.
							int column = placeholder.getTextRange().getCell().getName().getColumnIndex();
							ITextTableCell itemC = placeholder.getTextRange().getCell();
							String itemCellText = itemC.getTextService().getText().getText();
							fillItemTableWithData(placeholderDisplayText, column, itemDataSets, itemsTable, itemCellRow, itemCellText);
						}
					}
				}
			}

			// Get the VAT summary of the UniDataSet document
			VatSummarySetManager vatSummarySetManager = new VatSummarySetManager();
			vatSummarySetManager.add(this.document, 1.0);

			int vatListTemplateRow = 0;
			if (vatListTable != null) {

				// Add the necessary rows for the VAT entries
				vatListTemplateRow = vatListCell.getName().getRowIndex();
				lastVatTemplateRow = vatListTemplateRow + vatSummarySetManager.size();
				vatListTable.addRow(vatListTemplateRow, vatSummarySetManager.size());

				// Scan all placeholders for the VAT placeholders
				for (int i = 0; i < placeholders.length; i++) {

					// Get the placeholder text
					ITextField placeholder = placeholders[i];
					String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();

					if (placeholder.getTextRange().getCell() != null) {

						// Test, if the placeholder is in the VAT table
						ITextTable textTable = placeholder.getTextRange().getCell().getTextTable();
						if (textTable.getName().equals(vatListTable.getName())) {

							// Fill the corresponding table column with the
							// VAT data.
							int column = placeholder.getTextRange().getCell().getName().getColumnIndex();
							ITextTableCell c = placeholder.getTextRange().getCell();
							String cellText = c.getTextService().getText().getText();
							replaceVatListPlaceholder(placeholderDisplayText, column, vatSummarySetManager.getVatSummaryItems(), vatListTable,
									vatListTemplateRow, cellText);
						}
					}
				}
			}

			// Replace all other placeholders
			for (int i = 0; i < placeholders.length; i++) {
				replaceText(placeholders[i]);
			}

			// remove the temporary row of the item table
			if (itemsTable != null) {
				itemsTable.removeRow(lastItemTemplateRow);
			}

			// remove the temporary row of the VAT table
			if (vatListTable != null) {
				vatListTable.removeRow(lastVatTemplateRow);
			}

			// Remove the discount cells, if there is no discount set
			if (DataUtils.DoublesAreEqual(document.getSummary().getDiscountNet().asDouble(), 0.0)) {
				for (int i = 0; i < discountCellList.size(); i++) {
					ITextTableCell cell = discountCellList.get(i);
					try {
						if (cell != null) {
							ITextTable table = cell.getTextTable();
							if (table != null)
								table.removeRow(cell.getName().getRowIndex());
						}
					}
					catch (TextException te) {
					}
				}
			}

			// Save the document
			saveOODocument(textDocument);

			// Print and close the OpenOffice document
			/*
			textDocument.getFrame().getDispatch(GlobalCommands.PRINT_DOCUMENT_DIRECT).dispatch();
			try {
			    Thread.sleep(2000);
			}
			catch (Exception e1) {
			    e1.printStackTrace();
			}
			textDocument.close();
			*/

			//officeAplication.deactivate();

		}
		catch (Exception e) {
			Logger.logError(e, "Error starting OpenOffice from " + url);
		}
	}

	/**
	 * Close the connection to the OpenOffice Document
	 */
	public void close() {

		// Remove the SAVE dispatcher
		if (officeFrame != null)
			officeFrame.removeDispatchDelegate(GlobalCommands.SAVE);

		// Close only open document
		if (oOdocument.isOpen())
			oOdocument.close();
		
		// Get the remaining documents
		int remainingDocuments = 0;
		
		try {
			remainingDocuments = officeApplication.getDocumentService().getCurrentDocuments().length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// Close the OpenOffice document
		try {
			if (officeApplication != null)
				if (remainingDocuments == 0 ) {
					officeApplication.deactivate();
					System.out.println("OpenOffice closed");
				}
		}
		catch (OfficeApplicationException e) {
			Logger.logError(e, "Error closing OpenOffice");
		}
	}

	/**
	 * Returns the filename (with path) of the OpenOffice document
	 * 
	 * @param inclFilename
	 *            True, if also the filename should be used
	 * @param inclExtension
	 *            True, if also the extension should be used
	 * @param PDF
	 *            True, if it's the PDF filename
	 * @return The filename
	 */
	public static String getDocumentPath(boolean inclFilename, boolean inclExtension, boolean isPDF, DataSetDocument document) {
		String savePath = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");

		//T: Subdirectory of the OpenOffice documents
		savePath += _("/Documents");

		if (isPDF)
			savePath += "/PDF/";
		else
			savePath += "/OpenOffice/";

		savePath += DocumentType.getPluralString(document.getIntValueByKey("category")) + "/";

		// Use the document name as filename
		if (inclFilename)
			savePath += document.getStringValueByKey("name");

		// Use the document name as filename
		if (inclExtension) {
			if (isPDF)
				savePath += ".pdf";
			else
				savePath += ".odt";
		}

		return savePath;

	}

	/**
	 * Save an OpenOffice document as *.odt and as *.pdf
	 * 
	 * @param textDocument
	 *            The document
	 */
	public void saveOODocument(ITextDocument textDocument) {

		boolean wasSaved = false;

		if (Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_ODT_PDF").contains("PDF")) {

			// Create the directories, if they don't exist.
			File directory = new File(getDocumentPath(NO_FILENAME, NO_EXTENSION, PDF, document));
			if (!directory.exists())
				directory.mkdirs();

			// Add the time String, if this file is still existing
			/*
			File file = new File(savePath + ".odt");
			if (file.exists()) {
				DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
				savePath += "_" + dfmt.format(new Date());
			}
			*/

			// Save the document
			try {
				FileOutputStream fs = new FileOutputStream(new File(getDocumentPath(WITH_FILENAME, WITH_EXTENSION, PDF, document)));
				textDocument.getPersistenceService().export(fs, new PDFFilter());

				wasSaved = true;

			}
			catch (FileNotFoundException e) {
				Logger.logError(e, "Error saving the OpenOffice Document");
			}
			catch (NOAException e) {
				Logger.logError(e, "Error saving the OpenOffice Document");
			}

		}

		if (Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_ODT_PDF").contains("ODT")) {

			// Create the directories, if they don't exist.
			File directory = new File(getDocumentPath(NO_FILENAME, NO_EXTENSION, NO_PDF, document));
			if (!directory.exists())
				directory.mkdirs();

			// Add the time String, if this file is still existing
			/*
			File file = new File(savePath + ".odt");
			if (file.exists()) {
				DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
				savePath += "_" + dfmt.format(new Date());
			}
			*/

			// Save the document
			try {
				FileOutputStream fs = new FileOutputStream(new File(getDocumentPath(WITH_FILENAME, WITH_EXTENSION, NO_PDF, document)));
				textDocument.getPersistenceService().storeAs(fs);
				

				wasSaved = true;

			}
			catch (FileNotFoundException e) {
				Logger.logError(e, "Error saving the OpenOffice Document");
			}
			catch (NOAException e) {
				Logger.logError(e, "Error saving the OpenOffice Document");
			}

		}


		// Mark the document as printed, if it was saved as ODT or PDF
		if (wasSaved) {
			// Mark the document as "printed"
			document.setBooleanValueByKey("printed", true);
			document.setStringValueByKey("printedtemplate", template);
			Data.INSTANCE.getDocuments().updateDataSet(document);
		}

	}

	/**
	 * Replace one column of the VAT table with the VAT entries
	 * 
	 * @param placeholderDisplayText
	 *            Name of the column, and of the VAT property
	 * @param column
	 *            Number of the column in the table
	 * @param vatSummarySet
	 *            VAT data
	 * @param vatListTable
	 *            The VAT table to fill
	 * @param templateRow
	 *            The first row of the table
	 * @param cellText
	 *            The cell's text.
	 */
	private void replaceVatListPlaceholder(String placeholderDisplayText, int column, VatSummarySet vatSummarySet, ITextTable vatListTable, int templateRow,
			String cellText) {
		int i = 0;

		// Get all VATs
		for (Iterator<VatSummaryItem> iterator = vatSummarySet.iterator(); iterator.hasNext(); i++) {
			VatSummaryItem vatSummaryItem = iterator.next();
			try {

				// Get the cell and fill the cell content
				IText iText = vatListTable.getCell(column, templateRow + i).getTextService().getText();
				fillVatTableWithData(placeholderDisplayText, vatSummaryItem.getVatName(), Double.toString(vatSummaryItem.getVat()), iText, i, cellText);

			}
			catch (TextException e) {
				Logger.logError(e, "Error replacing Vat List Placeholders");
			}
		}
	}

	/**
	 * Add a user text field to the OpenOffice document
	 * 
	 * @param key
	 *            The key of the user text field
	 * @param value
	 *            The value of the user text field
	 */
	private void addUserTextField(String key, String value) {
		try {
			textFieldService.addUserTextField(key, value);
		}
		catch (TextException e) {
			Logger.logError(e, "Error setting User Text Field: " + key + " to " + value);
		}
	}

	/**
	 * Add a user text field to the OpenOffice document The key contains an
	 * additional index.
	 * 
	 * @param key
	 *            The key of the user text field
	 * @param value
	 *            The value of the user text field
	 * @param i
	 *            Additional index, added to the key
	 */
	private void addUserTextField(String key, String value, int i) {
		key = key + "." + Integer.toString(i);
		addUserTextField(key, value);
	}

	/**
	 * Fill the cell of the VAT table with the VAT data
	 * 
	 * @param placeholderDisplayText
	 *            Column header
	 * @param key
	 *            VAT key (VAT description)
	 * @param value
	 *            VAT value
	 * @param iText
	 *            The Text that is set
	 * @param index
	 *            Index of the VAT entry
	 * @param cellText
	 *            The cell's text.
	 */
	private void fillVatTableWithData(String placeholderDisplayText, String key, String value, IText iText, int index, String cellText) {

		// Get the text of the column. This is to determine, if it is the column
		// with the VAT description or with the VAT value
		String textValue;
		String textKey = placeholderDisplayText.substring(1, placeholderDisplayText.length() - 1);

		// It's the VAT description
		if (placeholderDisplayText.equals("<VATLIST.DESCRIPTIONS>")) {
			textValue = key;
		}
		// It's the VAT value
		else if (placeholderDisplayText.equals("<VATLIST.VALUES>")) {
			textValue = DataUtils.DoubleToFormatedPriceRound(Double.parseDouble(value));
		}

		else
			return;

		// Set the text
		iText.setText(cellText.replaceAll(placeholderDisplayText, textValue));

		// And also add it to the user defined text fields in the OpenOffice
		// Writer document.
		addUserTextField(textKey, textValue, index);

	}

	/**
	 * Fill all cells of the item table with the item data
	 * 
	 * @param placeholderDisplayText
	 *            Column header
	 * @param column
	 *            The index of the column
	 * @param itemDataSets
	 *            Item data
	 * @param itemsTable
	 *            The item table
	 * @param lastTemplateRow
	 *            Counts the last row of the table
	 * @param cellText
	 *            The cell's text.
	 */
	private void fillItemTableWithData(String placeholderDisplayText, int column, ArrayList<DataSetItem> itemDataSets, ITextTable itemsTable,
			int lastTemplateRow, String cellText) {

		// Get all items
		for (int row = 0; row < itemDataSets.size(); row++) {
			try {

				// Get a reference to the cell content
				IText iText = itemsTable.getCell(column, lastTemplateRow + row).getTextService().getText();

				// Get the item
				DataSetItem item = itemDataSets.get(row);

				// Set the cell content
				fillItemTableWithData(row, placeholderDisplayText, item, iText, row, cellText);

			}
			catch (TextException e) {
				Logger.logError(e, "Error replacing Placeholders");
			}
		}

	}

	/**
	 * Converts all \r\n to \n
	 * \r\n are Generated by SWT text controls on a windows system.
	 * 
	 * @param s
	 * 		The string to convert
	 * @return
	 * 		The converted string
	 */
	private String convertCRLR2CL(String s){
		s = s.replaceAll("\\r\\n", "\n");
		return s;
	}
	
	/**
	 * Fill the cell of the item table with the item data
	 * 
	 * @param placeholderDisplayText
	 *            Column header
	 * @param item
	 * @param iText
	 *            The Text that is set
	 * @param index
	 *            Index of the VAT entry
	 * @param cellText
	 *            The cell's text.
	 */
	private void fillItemTableWithData(int row, String placeholderDisplayText, DataSetItem item, IText iText, int index, String cellText) {

		String value = "";
		String format = "";
		
		// Get the column's header
		String key = placeholderDisplayText.substring(1, placeholderDisplayText.length() - 1);

		Price price = new Price(item);

		// Get the item quantity
		if (placeholderDisplayText.equals("<ITEM.QUANTITY>")) {
			value = DataUtils.DoubleToFormatedQuantity(item.getDoubleValueByKey("quantity"));
		}

		else if (placeholderDisplayText.startsWith("<ITEM.QUANTITY$") && placeholderDisplayText.endsWith(">")) {
			format = placeholderDisplayText.substring(15, placeholderDisplayText.length()-1);
			try {
				value = DataUtils.DoubleToDecimalFormatedValue(item.getDoubleValueByKey("quantity"), format);
			}
			catch (Exception e) {
				value = DataUtils.DoubleToFormatedQuantity(item.getDoubleValueByKey("quantity"));
			}
		}
		
		// The position
		else if (placeholderDisplayText.equals("<ITEM.POS>")) {
			value = Integer.toString(row + 1);
		}

		// The text for optional items
		else if (placeholderDisplayText.equals("<ITEM.OPTIONAL.TEXT>")) {
			if (item.getBooleanValueByKey("optional")) {
				value = Activator.getDefault().getPreferenceStore().getString("OPTIONALITEMS_OPTIONALITEM_TEXT");
				value = value.replaceAll("<br>", "\n");
			}
		}
		
		// Get the item name
		else if (placeholderDisplayText.equals("<ITEM.NAME>")) {
			value = item.getStringValueByKey("name");
		}

		// Get the item number
		else if (placeholderDisplayText.equals("<ITEM.NR>")) {
			value = item.getStringValueByKey("itemnr");
		}

		// Get the item description
		else if (placeholderDisplayText.equals("<ITEM.DESCRIPTION>")) {
			// 2011-06-24 sbauer@eumedio.de
			// Remove pre linebreak if description if description is empty to avoid empty lines
			if( item.getStringValueByKey("description").isEmpty() )
				cellText = cellText.replaceFirst("\n<ITEM.DESCRIPTION>", "");
			
			value = item.getStringValueByKey("description");
		}

		// Get the item discount
		else if (placeholderDisplayText.equals("<ITEM.DISCOUNT.PERCENT>")) {
			value = DataUtils.DoubleToFormatedPercent(item.getDoubleValueByKey("discount"));
		}

		// Get the item's VAT
		else if (placeholderDisplayText.equals("<ITEM.VAT.PERCENT>")) {
			value = DataUtils.DoubleToFormatedPercent(item.getDoubleValueByKey("vatvalue"));
		}

		// Get the item's VAT name
		else if (placeholderDisplayText.equals("<ITEM.VAT.NAME>")) {
			value = item.getStringValueByKey("vatname");
		}

		// Get the item's VAT description
		else if (placeholderDisplayText.equals("<ITEM.VAT.DESCRIPTION>")) {
			value = item.getStringValueByKey("vatdescription");
		}

		// Get the item net value
		else if (placeholderDisplayText.equals("<ITEM.UNIT.NET>")) {
			value = price.getUnitNetRounded().asFormatedString();
		}

		// Get the item VAT
		else if (placeholderDisplayText.equals("<ITEM.UNIT.VAT>")) {
			value = price.getUnitVatRounded().asFormatedString();
		}

		// Get the item gross value
		else if (placeholderDisplayText.equals("<ITEM.UNIT.GROSS>")) {
			value = price.getUnitGrossRounded().asFormatedString();
		}

		// Get the discounted item net value
		else if (placeholderDisplayText.equals("<ITEM.UNIT.NET.DISCOUNTED>")) {
			value = price.getUnitNetDiscountedRounded().asFormatedString();
		}

		// Get the discounted item VAT
		else if (placeholderDisplayText.equals("<ITEM.UNIT.VAT.DISCOUNTED>")) {
			value = price.getUnitVatDiscountedRounded().asFormatedString();
		}

		// Get the discounted item gross value
		else if (placeholderDisplayText.equals("<ITEM.UNIT.GROSS.DISCOUNTED>")) {
			value = price.getUnitGrossDiscountedRounded().asFormatedString();
		}

		// Get the total net value
		else if (placeholderDisplayText.equals("<ITEM.TOTAL.NET>")) {
			value = price.getTotalNetRounded().asFormatedString();
			if (item.getBooleanValueByKey("optional")) {
				if (Activator.getDefault().getPreferenceStore().getBoolean("OPTIONALITEMS_REPLACE_PRICE"))
					value = Activator.getDefault().getPreferenceStore().getString("OPTIONALITEMS_PRICE_REPLACEMENT");
			}

		}

		// Get the total VAT
		else if (placeholderDisplayText.equals("<ITEM.TOTAL.VAT>")) {
			value = price.getTotalVatRounded().asFormatedString();
			if (item.getBooleanValueByKey("optional")) {
				if (Activator.getDefault().getPreferenceStore().getBoolean("OPTIONALITEMS_REPLACE_PRICE"))
					value = Activator.getDefault().getPreferenceStore().getString("OPTIONALITEMS_PRICE_REPLACEMENT");
			}
		}

		// Get the total gross value
		else if (placeholderDisplayText.equals("<ITEM.TOTAL.GROSS>")) {
			value = price.getTotalGrossRounded().asFormatedString();
			if (item.getBooleanValueByKey("optional")) {
				if (Activator.getDefault().getPreferenceStore().getBoolean("OPTIONALITEMS_REPLACE_PRICE"))
					value = Activator.getDefault().getPreferenceStore().getString("OPTIONALITEMS_PRICE_REPLACEMENT");
			}
		}
		
		// Get product picture
		else if (placeholderDisplayText.startsWith("<ITEM.PICTURE") && placeholderDisplayText.endsWith(">") ){
			
			// Is the image width and height set ?
			if (placeholderDisplayText.startsWith("<ITEM.PICTURE$") && placeholderDisplayText.endsWith(">")) 
				format = placeholderDisplayText.substring(14, placeholderDisplayText.length()-1);

			if (!item.getStringValueByKey("picturename").isEmpty()) {
				// Default height and with
				int pixelWidth = 150;
				int pixelHeight = 100;

				// Get the width and height from the format string
				String formatParts[] = format.split(",");
				if (formatParts.length >=2) {
					try {
						pixelWidth = Integer.parseInt(formatParts[0]);
						pixelHeight = Integer.parseInt(formatParts[1]);
					}
					catch (NumberFormatException e) {
					}
				}

				String imagePath = Workspace.INSTANCE.getWorkspace() + 
				 					Workspace.productPictureFolderName + 
				 					item.getStringValueByKey("picturename");
				int pictureHeight = 100;
				int pictureWidth = 100;
				double pictureRatio = 1.0;
				double pixelRatio = 1.0;
			      
				// Read the image a first time to get width and height
				try {
					File f = new File(imagePath);
					BufferedImage image = ImageIO.read(f);
					pictureHeight = image.getHeight();
					pictureWidth = image.getWidth();

					// Calculate the ratio of the original image
					if (pictureHeight > 0) {
						pictureRatio = (double)pictureWidth/(double)pictureHeight;
					}
					
					// Calculate the ratio of the placeholder
					if (pixelHeight > 0) {
						pixelRatio = (double)pixelWidth/(double)pixelHeight;
					}
					
					// Correct the height and width of the placeholder 
					// to match the original image
					if ((pictureRatio > pixelRatio) &&  (pictureRatio != 0.0)) {
						pixelHeight = (int) Math.round(((double)pixelWidth / pictureRatio));
					}
					if ((pictureRatio < pixelRatio) &&  (pictureRatio != 0.0)) {
						pixelWidth = (int) Math.round(((double)pixelHeight * pictureRatio));
					}
					
					// Generate the image
					GraphicInfo graphicInfo = null;
					graphicInfo = new GraphicInfo(new FileInputStream(imagePath),
						    pixelWidth,
						    true,
						    pixelHeight,
						    true,
						    VertOrientation.TOP,
						    HoriOrientation.LEFT,
						    TextContentAnchorType.AT_PARAGRAPH);

					ITextContentService textContentService = textDocument.getTextService().getTextContentService();
					ITextDocumentImage textDocumentImage = textContentService.constructNewImage(graphicInfo);
					textContentService.insertTextContent(iText.getTextCursorService().getTextCursor().getEnd(), textDocumentImage);

					return;
				}
				catch (IOException e) {
				}
				catch (NOAException e) {
				}
				catch (TextException e) {
					e.printStackTrace();
				}
				
			}
			
			value = "";
		}
		
		else
			return;

		// Convert CRLF to LF 
		value = convertCRLR2CL(value);

		// If iText's string is not empty, use that string instead of the template
		String iTextString = iText.getText();
		if (!iTextString.isEmpty()) {
			cellText = iTextString;
		}
		
		// Set the text of the cell
		iText.setText(cellText.replaceAll(Matcher.quoteReplacement(placeholderDisplayText), value));

		// And also add it to the user defined text fields in the OpenOffice
		// Writer document.
		addUserTextField(key, value, index);
	}

	/**
	 * Set a property and add it to the user defined text fields in the
	 * OpenOffice Writer document.
	 * 
	 * @param key
	 *            The property key
	 * @param value
	 *            The property value
	 */
	private void setProperty(String key, String value) {

		// Convert CRLF to LF 
		value = convertCRLR2CL(value);
		
		// Set the user defined text field
		addUserTextField(key, value);

		// Add the value and use a key with brackets
		properties.setProperty("<" + key + ">", value);
	}

	/**
	 * Fill the property list with the placeholder values
	 */
	private void setCommonProperties() {

		if (document != null) {
			document.calculate();
			setProperty("DOCUMENT.DATE", document.getFormatedStringValueByKey("date"));
			setProperty("DOCUMENT.ADDRESS", document.getStringValueByKey("address"));
			setProperty("DOCUMENT.DELIVERYADDRESS", document.getStringValueByKey("deliveryaddress"));
			setProperty("DOCUMENT.TYPE", DocumentType.getString(document.getIntValueByKey("category")));
			setProperty("DOCUMENT.NAME", document.getStringValueByKey("name"));
			setProperty("DOCUMENT.CUSTOMERREF", document.getStringValueByKey("customerref"));
			setProperty("DOCUMENT.SERVICEDATE", document.getFormatedStringValueByKey("servicedate"));
			setProperty("DOCUMENT.MESSAGE", document.getStringValueByKey("message"));
			setProperty("DOCUMENT.MESSAGE1", document.getStringValueByKey("message"));
			setProperty("DOCUMENT.MESSAGE2", document.getStringValueByKey("message2"));
			setProperty("DOCUMENT.MESSAGE3", document.getStringValueByKey("message3"));
			setProperty("DOCUMENT.TRANSACTION", document.getStringValueByKey("transaction"));
			setProperty("DOCUMENT.INVOICE", document.getStringValueByKeyFromOtherTable("invoiceid.DOCUMENTS:name"));
			setProperty("DOCUMENT.WEBSHOP.ID", document.getStringValueByKey("webshopid"));
			setProperty("DOCUMENT.WEBSHOP.DATE", document.getFormatedStringValueByKey("webshopdate"));
			setProperty("DOCUMENT.ORDER.DATE", document.getFormatedStringValueByKey("orderdate"));
			setProperty("DOCUMENT.ITEMS.GROSS", document.getSummary().getItemsGross().asFormatedRoundedString());
			setProperty("DOCUMENT.ITEMS.NET", document.getSummary().getItemsNet().asFormatedRoundedString());
			setProperty("DOCUMENT.TOTAL.VAT", document.getSummary().getTotalVat().asFormatedRoundedString());
			setProperty("DOCUMENT.TOTAL.GROSS", document.getSummary().getTotalGross().asFormatedString());
			setProperty("ITEMS.DISCOUNT.PERCENT", document.getFormatedStringValueByKey("itemsdiscount"));
			setProperty("ITEMS.DISCOUNT.NET", document.getSummary().getDiscountNet().asFormatedRoundedString());
			setProperty("ITEMS.DISCOUNT.GROSS", document.getSummary().getDiscountGross().asFormatedRoundedString());
			setProperty("SHIPPING.NET", document.getSummary().getShippingNet().asFormatedString());
			setProperty("SHIPPING.VAT", document.getSummary().getShippingVat().asFormatedString());
			setProperty("SHIPPING.GROSS", document.getSummary().getShippingGross().asFormatedString());
			setProperty("SHIPPING.NAME", document.getStringValueByKey("shippingname"));
			setProperty("SHIPPING.DESCRIPTION", document.getStringValueByKey("shippingdescription"));
			setProperty("SHIPPING.VAT.DESCRIPTION", document.getStringValueByKey("shippingvatdescription"));
			setProperty("DOCUMENT.DUNNING.LEVEL", document.getStringValueByKey("dunninglevel"));
			
			
			// Replace the placeholders in the payment text
			String pamenttext = document.getStringValueByKey("paymenttext");
			pamenttext = pamenttext.replace("<PAID.VALUE>", DataUtils.DoubleToFormatedPriceRound(document.getDoubleValueByKey("payvalue")));
			pamenttext = pamenttext.replace("<PAID.DATE>", document.getFormatedStringValueByKey("paydate"));
			pamenttext = pamenttext.replace("<DUE.DAYS>", Integer.toString(document.getIntValueByKey("duedays")));
			pamenttext = pamenttext.replace("<DUE.DATE>", DataUtils.DateAsLocalString(DataUtils.AddToDate(document.getStringValueByKey("date"), document.getIntValueByKey("duedays"))));
			
			// 2011-06-24 sbauer@eumedio.de
			// New placeholder for bank
			if (contact != null) {
				pamenttext = pamenttext.replace("<BANK.ACCOUNT.HOLDER>", contact.getStringValueByKey("account_holder"));
				pamenttext = pamenttext.replace("<BANK.ACCOUNT>", contact.getStringValueByKey("account"));
				pamenttext = pamenttext.replace("<BANK.CODE>", contact.getStringValueByKey("bank_code"));
				pamenttext = pamenttext.replace("<BANK.NAME>", contact.getStringValueByKey("bank_name"));
				
				// 2011-06-24 sbauer@eumedio.de
				// Additional placeholer for censored bank account
				Integer bankAccountLength = contact.getStringValueByKey("account").length();
				
				// Only set placeholder if bank account exists
				if( bankAccountLength > 0 ) {
					
					// Show only the last 3 digits
					Integer bankAccountCensoredLength = bankAccountLength - 3;
					String censoredDigits = "";
					
					for( int i = 1; i <= bankAccountCensoredLength; i++ ) {
						censoredDigits += "*";
					}
					
					pamenttext = pamenttext.replace("<BANK.ACCOUNT.CENSORED>", censoredDigits + contact.getStringValueByKey("account").substring( bankAccountCensoredLength ));
					
				}
			}

			
			// 2011-06-24 sbauer@eumedio.de
			// New placeholder for total sum
			pamenttext = pamenttext.replace("<DOCUMENT.TOTAL>", document.getSummary().getTotalGross().asFormatedString());
			
			setProperty("PAYMENT.TEXT", pamenttext);
			setProperty("PAYMENT.NAME", document.getStringValueByKey("paymentname"));
			setProperty("PAYMENT.DESCRIPTION", document.getStringValueByKey("paymentdescription"));
			setProperty("PAYMENT.PAID.VALUE", DataUtils.DoubleToFormatedPriceRound(document.getDoubleValueByKey("payvalue")));
			setProperty("PAYMENT.PAID.DATE", document.getFormatedStringValueByKey("paydate"));
			setProperty("PAYMENT.DUE.DAYS", Integer.toString(document.getIntValueByKey("duedays")));
			setProperty("PAYMENT.DUE.DATE",
					DataUtils.DateAsLocalString(DataUtils.AddToDate(document.getStringValueByKey("date"), document.getIntValueByKey("duedays"))));
			setProperty("PAYMENT.PAID", document.getStringValueByKey("paid"));
		}

		if (contact != null) {
			setProperty("ADDRESS", contact.getAddress(false));
			setProperty("ADDRESS.GENDER", contact.getGenderString(false));
			setProperty("ADDRESS.GREETING", contact.getGreeting(false));
			setProperty("ADDRESS.TITLE", contact.getStringValueByKey("title"));
			setProperty("ADDRESS.FIRSTNAME", contact.getStringValueByKey("firstname"));
			setProperty("ADDRESS.LASTNAME", contact.getStringValueByKey("name"));
			setProperty("ADDRESS.NAME", contact.getStringValueByKey("name"));
			setProperty("ADDRESS.COMPANY", contact.getStringValueByKey("company"));
			setProperty("ADDRESS.STREET", contact.getStringValueByKey("street"));
			setProperty("ADDRESS.ZIP", contact.getStringValueByKey("zip"));
			setProperty("ADDRESS.CITY", contact.getStringValueByKey("city"));
			setProperty("ADDRESS.COUNTRY", contact.getStringValueByKey("country"));
			setProperty("DELIVERY.ADDRESS", contact.getAddress(true));
			setProperty("DELIVERY.ADDRESS.GENDER", contact.getGenderString(true));
			setProperty("DELIVERY.ADDRESS.GREETING", contact.getGreeting(true));
			setProperty("DELIVERY.ADDRESS.TITLE", contact.getStringValueByKey("delivery_title"));
			setProperty("DELIVERY.ADDRESS.FIRSTNAME", contact.getStringValueByKey("delivery_firstname"));
			setProperty("DELIVERY.ADDRESS.NAME", contact.getStringValueByKey("delivery_name"));
			setProperty("DELIVERY.ADDRESS.COMPANY", contact.getStringValueByKey("delivery_company"));
			setProperty("DELIVERY.ADDRESS.STREET", contact.getStringValueByKey("delivery_street"));
			setProperty("DELIVERY.ADDRESS.ZIP", contact.getStringValueByKey("delivery_zip"));
			setProperty("DELIVERY.ADDRESS.CITY", contact.getStringValueByKey("delivery_city"));
			setProperty("DELIVERY.ADDRESS.COUNTRY", contact.getStringValueByKey("delivery_country"));
			setProperty("ADDRESS.BANK.ACCOUNT.HOLDER", contact.getStringValueByKey("account_holder"));
			setProperty("ADDRESS.BANK.ACCOUNT", contact.getStringValueByKey("account"));
			setProperty("ADDRESS.BANK.CODE", contact.getStringValueByKey("bank_code"));
			setProperty("ADDRESS.BANK.NAME", contact.getStringValueByKey("bank_name"));
			setProperty("ADDRESS.BANK.IBAN", contact.getStringValueByKey("iban"));
			setProperty("ADDRESS.BANK.BIC", contact.getStringValueByKey("bic"));
			setProperty("ADDRESS.NR", contact.getStringValueByKey("nr"));
			setProperty("ADDRESS.PHONE", contact.getStringValueByKey("phone"));
			setProperty("ADDRESS.FAX", contact.getStringValueByKey("fax"));
			setProperty("ADDRESS.MOBILE", contact.getStringValueByKey("mobile"));
			setProperty("ADDRESS.EMAIL", contact.getStringValueByKey("email"));
			setProperty("ADDRESS.WEBSITE", contact.getStringValueByKey("website"));
			setProperty("ADDRESS.VATNR", contact.getStringValueByKey("vatnr"));
			setProperty("ADDRESS.NOTE", contact.getStringValueByKey("note"));
			setProperty("ADDRESS.DISCOUNT", contact.getFormatedStringValueByKey("discount"));
		}
		else {
			setProperty("ADDRESS", "");
			setProperty("ADDRESS.GENDER", "");
			setProperty("ADDRESS.GREETING", DataSetContact.getCommonGreeting());
			setProperty("ADDRESS.TITLE", "");
			setProperty("ADDRESS.FIRSTNAME", "");
			setProperty("ADDRESS.NAME", "");
			setProperty("ADDRESS.LASTNAME", "");
			setProperty("ADDRESS.COMPANY", "");
			setProperty("ADDRESS.STREET", "");
			setProperty("ADDRESS.ZIP", "");
			setProperty("ADDRESS.CITY", "");
			setProperty("ADDRESS.COUNTRY", "");
			setProperty("DELIVERY.ADDRESS", "");
			setProperty("DELIVERY.ADDRESS.GENDER", "");
			setProperty("DELIVERY.ADDRESS.GREETING", DataSetContact.getCommonGreeting());
			setProperty("DELIVERY.ADDRESS.TITLE", "");
			setProperty("DELIVERY.ADDRESS.FIRSTNAME", "");
			setProperty("DELIVERY.ADDRESS.NAME", "");
			setProperty("DELIVERY.ADDRESS.COMPANY", "");
			setProperty("DELIVERY.ADDRESS.STREET", "");
			setProperty("DELIVERY.ADDRESS.ZIP", "");
			setProperty("DELIVERY.ADDRESS.CITY", "");
			setProperty("DELIVERY.ADDRESS.COUNTRY", "");
			setProperty("ADDRESS.BANK.ACCOUNT.HOLDER", "");
			setProperty("ADDRESS.BANK.ACCOUNT", "");
			setProperty("ADDRESS.BANK.CODE", "");
			setProperty("ADDRESS.BANK.NAME", "");
			setProperty("ADDRESS.BANK.IBAN", "");
			setProperty("ADDRESS.BANK.BIC", "");
			setProperty("ADDRESS.NR", "");
			setProperty("ADDRESS.PHONE", "");
			setProperty("ADDRESS.FAX", "");
			setProperty("ADDRESS.MOBILE", "");
			setProperty("ADDRESS.EMAIL", "");
			setProperty("ADDRESS.WEBSITE", "");
			setProperty("ADDRESS.VATNR", "");
			setProperty("ADDRESS.NOTE", "");
			setProperty("ADDRESS.DISCOUNT", "");

		}

	}

	/**
	 * Replace a placeholder with the content of the property in the property
	 * list.
	 * 
	 * @param placeholder
	 *            The placeholder and the name of the key in the property list
	 */
	private void replaceText(ITextField placeholder) {
		// Get the placeholder's text
		String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();

		// Replace it with the value of the property list.
		placeholder.getTextRange().setText(properties.getProperty(placeholderDisplayText));
	}
	
	static public boolean testOpenAsExisting(DataSetDocument document,String template) {
		// Check, whether there is already a document then do not 
		// generate one by the data, but open the existing one.
		File oODocumentFile = new File(getDocumentPath(WITH_FILENAME, WITH_EXTENSION, NO_PDF, document));
		
		if (oODocumentFile.exists() && document.getBooleanValueByKey("printed") &&
				document.getStringValueByKey("printedtemplate").equals(template)) {
			return true;
		}
		
		return false;
	
	}

}
