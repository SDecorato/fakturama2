/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.openoffice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.document.URLAdapter;
import ag.ion.noa.frame.IDispatchDelegate;

import com.sebulli.fakturama.Activator;
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
import com.sun.star.uno.UnoRuntime;

/**
 * This class opens an OpenOffice Writer template and replaces all the
 * placeholders with the document data.
 * 
 * @author Gerd Bartelt
 */
public class OODocument extends Object {

	// The UniDataSet document, that is used to fill the OpenOffice document 
	private DataSetDocument document;

	// The UniDataSet contact of the document
	private DataSetContact contact;

	// A list of properties that represents the placeholders of the
	// OpenOffice Writer template
	private Properties properties;

	// OpenOffice objects
	IOfficeApplication officeApplication;
	IDocument oOdocument;
	ITextDocument textDocument;
	IFrame officeFrame;

	ITextFieldService textFieldService;

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
	public OODocument(DataSetDocument document, String template) {

		// Url of the template file
		String url = null;

		//Open an existing document instead of creating a new one
		boolean openExisting = false;

		// Set a reference to the UniDatSet document
		this.document = document;

		// Try to generate the OpenOffice document
		try {

			// Get the OpenOffice application
			officeApplication = OpenOfficeStarter.openOfficeAplication();
			if (officeApplication == null)
				return;

			// Check, whether there is already a document then do not 
			// generate one by the data, but open the existing one.
			File oODocumentFile = new File(getDocumentPath(true, true, false));
			if (oODocumentFile.exists() && document.getBooleanValueByKey("printed")) {
				openExisting = true;
				template = getDocumentPath(true, true, false);
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

		// Close the OpenOffice document
		try {
			if (officeApplication != null)
				officeApplication.deactivate();
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
	public String getDocumentPath(boolean inclFilename, boolean inclExtension, boolean PDF) {
		String savePath = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");

		savePath += "/Dokumente";

		if (PDF)
			savePath += "/PDF/";
		else
			savePath += "/OpenOffice/";

		savePath += DocumentType.getPluralString(this.document.getIntValueByKey("category")) + "/";

		// Use the document name as filename
		if (inclFilename)
			savePath += this.document.getStringValueByKey("name");

		// Use the document name as filename
		if (inclExtension) {
			if (PDF)
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

		if (Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_ODT_PDF").contains("ODT")) {

			// Create the directories, if they don't exist.
			File directory = new File(getDocumentPath(false, false, false));
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
				FileOutputStream fs = new FileOutputStream(new File(getDocumentPath(true, true, false)));
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

		if (Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_ODT_PDF").contains("PDF")) {

			// Create the directories, if they don't exist.
			File directory = new File(getDocumentPath(false, false, true));
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
				FileOutputStream fs = new FileOutputStream(new File(getDocumentPath(true, true, true)));
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

		// Mark the document as printed, if it was saved as ODT or PDF
		if (wasSaved) {
			// Mark the document as "printed"
			document.setBooleanValueByKey("printed", true);
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
				fillItemTableWithData(placeholderDisplayText, item, iText, row, cellText);

			}
			catch (TextException e) {
				Logger.logError(e, "Error replacing Placeholders");
			}
		}

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
	private void fillItemTableWithData(String placeholderDisplayText, DataSetItem item, IText iText, int index, String cellText) {

		String value;

		// Get the column's header
		String key = placeholderDisplayText.substring(1, placeholderDisplayText.length() - 1);

		Price price = new Price(item);

		// Get the item quantity
		if (placeholderDisplayText.equals("<ITEM.QUANTITY>")) {
			value = DataUtils.DoubleToFormatedQuantity(item.getDoubleValueByKey("quantity"));
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
			value = item.getStringValueByKey("description");
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

		// Get the total net value
		else if (placeholderDisplayText.equals("<ITEM.TOTAL.NET>")) {
			value = price.getTotalNetRounded().asFormatedString();
		}

		// Get the total VAT
		else if (placeholderDisplayText.equals("<ITEM.TOTAL.VAT>")) {
			value = price.getTotalVatRounded().asFormatedString();
		}

		// Get the total gross value
		else if (placeholderDisplayText.equals("<ITEM.TOTAL.GROSS>")) {
			value = price.getTotalGrossRounded().asFormatedString();
		}
		else
			return;

		// Set the text of the cell
		iText.setText(cellText.replaceAll(placeholderDisplayText, value));

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
			setProperty("DOCUMENT.TRANSACTION", document.getStringValueByKey("transaction"));
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

			/*
			 * setProperty("PAYMENT.DISCOUNT.PERCENT",
			 * document.getFormatedStringValueByKey("paymentdiscount"));
			 * setProperty("PAYMENT.DISCOUNT.VALUE",
			 * DataUtils.DoubleToFormatedPriceRound
			 * (document.getDoubleValueByKey("paymentdiscount")
			 * document.getSummary().getTotalGross().asDouble()));
			 */
			setProperty("PAYMENT.NAME", document.getStringValueByKey("paymentname"));
			setProperty("PAYMENT.DESCRIPTION", document.getStringValueByKey("paymentdescription"));
			setProperty("PAYMENT.TEXT", document.getStringValueByKey("paymenttext"));
			setProperty("PAYMENT.PAYED.VALUE", DataUtils.DoubleToFormatedPriceRound(document.getDoubleValueByKey("payvalue")));
			setProperty("PAYMENT.PAYED.DATE", document.getFormatedStringValueByKey("paydate"));
			setProperty("PAYMENT.DUE.DAYS", Integer.toString(document.getIntValueByKey("duedays")));
			setProperty("PAYMENT.DUE.DATE",
					DataUtils.DateAsLocalString(DataUtils.AddToDate(document.getStringValueByKey("date"), document.getIntValueByKey("duedays"))));
			setProperty("PAYMENT.PAYED", document.getStringValueByKey("payed"));
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

}
