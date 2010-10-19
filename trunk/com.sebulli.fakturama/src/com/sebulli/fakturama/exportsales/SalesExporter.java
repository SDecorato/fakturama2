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

package com.sebulli.fakturama.exportsales;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.noa.NOAException;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.calculate.ExpenditureSummarySetManager;
import com.sebulli.fakturama.calculate.PriceValue;
import com.sebulli.fakturama.calculate.VatSummaryItem;
import com.sebulli.fakturama.calculate.VatSummarySetManager;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DataSetExpenditure;
import com.sebulli.fakturama.data.DataSetExpenditureItem;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.data.UniDataSetSorter;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.openoffice.OpenOfficeStarter;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.text.XText;
import com.sun.star.uno.UnoRuntime;

/**
 * The sales exporter. This class collects all the sales and fills a Calc table
 * with the data
 * 
 * @author Gerd Bartelt
 */
public class SalesExporter {

	// The begin and end date to specify the export periode
	private GregorianCalendar beginDate;
	private GregorianCalendar endDate;

	// the date key to sort the documents
	private String documentDateKey;

	// Settings from the preference page
	boolean showExpenditureSumColumn;
	boolean showZeroVatColumn;
	boolean usePayedDate;

	/**
	 * Default constructor
	 */
	public SalesExporter() {
		this.beginDate = null;
		this.endDate = null;
	}

	/**
	 * Constructor Sets the begin and end date
	 * 
	 * @param beginDate
	 *            Begin date
	 * @param endDate
	 *            Begin date
	 */
	public SalesExporter(GregorianCalendar beginDate, GregorianCalendar endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	/**
	 * Returns, if a given document should be used to export. Only invoice and
	 * credit documents that are payed in the specified time interval are
	 * exported.
	 * 
	 * @param document
	 *            The document that is tested
	 * @return True, if the document should be exported
	 */
	private boolean documentShouldBeExported(DataSetDocument document) {

		// By default, the document will be exported.
		boolean isInIntervall = true;

		// Get the date of the document and convert it to a
		// GregorianCalendar object.
		GregorianCalendar documentDate = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			String documentDateString = document.getStringValueByKey(documentDateKey);

			documentDate.setTime(formatter.parse(documentDateString));
		}
		catch (ParseException e) {
			Logger.logError(e, "Error parsing Date");
		}

		// Test, if the document's date is in the interval
		if ((beginDate != null) && (endDate != null)) {
			if (beginDate.after(documentDate))
				isInIntervall = false;
			if (endDate.before(documentDate))
				isInIntervall = false;
		}

		// Only payed invoiced and credits in the interval
		// will be exported.
		return ((document.getIntValueByKey("category") == DocumentType.INVOICE.getInt()) || (document.getIntValueByKey("category") == DocumentType.CREDIT
				.getInt())) && document.getBooleanValueByKey("payed") && isInIntervall;
	}

	/**
	 * Returns, if a given expenditure should be used to export. Only
	 * expenditures in the specified time interval are exported.
	 * 
	 * @param expenditure
	 *            The expenditure that is tested
	 * @return True, if the expenditure should be exported
	 */
	private boolean expenditureShouldBeExported(DataSetExpenditure expenditure) {

		// By default, the document will be exported.
		boolean isInIntervall = true;

		// Get the date of the document and convert it to a
		// GregorianCalendar object.
		GregorianCalendar documentDate = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			String expenditureDateString = "";

			// Use date 
			expenditureDateString = expenditure.getStringValueByKey("date");

			documentDate.setTime(formatter.parse(expenditureDateString));
		}
		catch (ParseException e) {
			Logger.logError(e, "Error parsing Date");
		}

		// Test, if the document's date is in the interval
		if ((beginDate != null) && (endDate != null)) {
			if (beginDate.after(documentDate))
				isInIntervall = false;
			if (endDate.before(documentDate))
				isInIntervall = false;
		}

		// Return, if expenditure is in the interval
		return isInIntervall;
	}

	// Do the export job.
	public boolean export() {

		// Get the OpenOffice application
		final IOfficeApplication officeAplication = OpenOfficeStarter.openOfficeAplication();
		if (officeAplication == null)
			return false;

		// Create a new OpenOffice Calc document
		IDocument oOdocument = null;
		try {
			oOdocument = officeAplication.getDocumentService().constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
		}
		catch (NOAException e) {
			Logger.logError(e, "NOA Error opening CALC");
			return false;
		}
		catch (OfficeApplicationException e) {
			Logger.logError(e, "OO Error opening CALC");
			return false;
		}

		// Get the spreadsheets
		ISpreadsheetDocument spreadDocument = (ISpreadsheetDocument) oOdocument;
		XSpreadsheetDocument xSpreadsheetDocument = spreadDocument.getSpreadsheetDocument();
		XSpreadsheets spreadsheets = xSpreadsheetDocument.getSheets();

		// Insert an "Export" spreadsheet
		XSpreadsheet spreadsheet1 = null;
		try {
			String tableName = "Export";
			spreadsheets.insertNewByName(tableName, (short) 0);

			// Remove all other spreadsheets
			String names[] = spreadsheets.getElementNames();
			for (String name : names) {
				if (!name.equals(tableName))
					spreadsheets.removeByName(name);
			}

			// Get a reference to the Export sheet
			spreadsheet1 = (XSpreadsheet) UnoRuntime.queryInterface(XSpreadsheet.class, spreadsheets.getByName(tableName));

		}
		catch (NoSuchElementException e) {
			Logger.logError(e, "Error getting spreadsheet");
		}
		catch (WrappedTargetException e) {
			Logger.logError(e, "Error getting spreadsheet");
		}

		usePayedDate = Activator.getDefault().getPreferenceStore().getBoolean("EXPORTSALES_PAYEDDATE");
		showExpenditureSumColumn = Activator.getDefault().getPreferenceStore().getBoolean("EXPORTSALES_SHOW_EXPENDITURE_SUM_COLUMN");
		showZeroVatColumn = Activator.getDefault().getPreferenceStore().getBoolean("EXPORTSALES_SHOW_ZERO_VAT_COLUMN");

		// Use pay date or document date
		if (usePayedDate)
			documentDateKey = "paydate";
		else
			documentDateKey = "date";

		// Get all undeleted documents
		ArrayList<DataSetDocument> documents = Data.INSTANCE.getDocuments().getActiveDatasets();
		// Get all undeleted expenditures
		ArrayList<DataSetExpenditure> expenditures = Data.INSTANCE.getExpenditures().getActiveDatasets();

		// Sort the documents by the pay date
		Collections.sort(documents, new UniDataSetSorter(documentDateKey));

		// Sort the expenditures by category and date
		Collections.sort(expenditures, new UniDataSetSorter("category", "date"));

		// Counter for the current row and columns in the Calc document
		int row = 0;
		int col = 0;

		// Count the columns that contain a VAT and net value 
		int columnsWithVatHeading = 0;
		int columnsWithNetHeading = 0;

		// Count the columns that contain a VAT value of 0% 
		int zeroVatColumns = 0;

		// Fill the first cells with company data
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_NAME"));
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_OWNER"));
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_STREET"));
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_ZIP") + " "
				+ Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_CITY"));
		row++;

		// Display the time interval
		setCellTextInBold(spreadsheet1, row++, 0, "Zeitraum");
		setCellText(spreadsheet1, row, 0, "von:");
		setCellText(spreadsheet1, row++, 1, DataUtils.getDateTimeAsLocalString(beginDate));
		setCellText(spreadsheet1, row, 0, "bis:");
		setCellText(spreadsheet1, row++, 1, DataUtils.getDateTimeAsLocalString(endDate));
		row++;

		// Create a VAT summary set manager that collects all VAT
		// values of all documents
		VatSummarySetManager vatSummarySetAllDocuments = new VatSummarySetManager();

		// Table heading
		setCellTextInBold(spreadsheet1, row++, 0, "Einnahmen");
		row++;

		// Table column headings
		int headLine = row;
		setCellTextInBold(spreadsheet1, row, col++, "Zahldatum");
		setCellTextInBold(spreadsheet1, row, col++, "Rg-Nummer");
		setCellTextInBold(spreadsheet1, row, col++, "Rg-Datum");
		setCellTextInBold(spreadsheet1, row, col++, "Vorname");
		setCellTextInBold(spreadsheet1, row, col++, "Nachname");
		setCellTextInBold(spreadsheet1, row, col++, "Firma");
		setCellTextInBold(spreadsheet1, row, col++, "USt-ID.");
		setCellTextInBold(spreadsheet1, row, col++, "Land");
		setCellTextInBold(spreadsheet1, row, col++, "Rg-Betrag");
		setCellTextInBold(spreadsheet1, row, col++, "Zahlbetrag");
		setCellTextInBold(spreadsheet1, row, col++, "Nettobetrag");
		row++;

		// The documents are exported in 2 runs.
		// First, only the VAT summary of all documents is calculated and
		// the columns are created.
		// Later all the documents are analyzed a second time and then they
		// are exported document by document into the table.
		for (DataSetDocument document : documents) {

			if (documentShouldBeExported(document)) {
				document.calculate();
				vatSummarySetAllDocuments.add(document, 1.0);
			}
		}

		col = 11;
		columnsWithVatHeading = 0;
		columnsWithNetHeading = 0;
		boolean vatIsNotZero = false;

		// A column for each Vat value is created 
		// The VAT summary items are sorted. So first ignore the VAT entries
		// with 0%. 
		// If the VAT value is >0%, create a column with heading.
		for (Iterator<VatSummaryItem> iterator = vatSummarySetAllDocuments.getVatSummaryItems().iterator(); iterator.hasNext();) {
			VatSummaryItem item = iterator.next();

			// Create a column, if the value is not 0%
			if ((item.getVat().doubleValue() > 0.001) || vatIsNotZero || showZeroVatColumn) {

				// If the first non-zero VAT column is created,
				// do not check the value any more.
				vatIsNotZero = true;

				// Count the columns
				columnsWithVatHeading++;

				// Create a column heading in bold
				int column = vatSummarySetAllDocuments.getIndex(item) - zeroVatColumns;
				setCellTextInBold(spreadsheet1, headLine, column + col, item.getVatName());

			}
			else
				// Count the columns with 0% VAT
				zeroVatColumns++;
		}

		// A column for each Net value is created 
		// The Net summary items are sorted. 
		for (Iterator<VatSummaryItem> iterator = vatSummarySetAllDocuments.getVatSummaryItems().iterator(); iterator.hasNext();) {
			VatSummaryItem item = iterator.next();

			// Count the columns
			columnsWithNetHeading++;

			// Create a column heading in bold
			int column = vatSummarySetAllDocuments.getIndex(item);
			setCellTextInBold(spreadsheet1, headLine, columnsWithVatHeading + column + col, "Netto \n" + item.getVatName());
		}

		// Second run.
		// Export the document data
		for (DataSetDocument document : documents) {

			if (documentShouldBeExported(document)) {

				// Now analyze document by document
				VatSummarySetManager vatSummarySetOneDocument = new VatSummarySetManager();
				document.calculate();

				// Calculate the relation between payed value and the value
				// of the invoice. This is used to calculate the VAT.
				// Example.
				// The net sum of the invoice is 100€.
				// Plus 20% VAT: +20€ = Total: 120€.
				//
				// The customer pays only 115€.
				// 
				// Then the payedFactor is 115/120 = 0.9583333..
				// The VAT value in the invoice is also scaled by this 0.958333...
				// to 19.17€
				Double payedFactor = document.getDoubleValueByKey("payvalue") / document.getDoubleValueByKey("total");

				// Use the payed value
				vatSummarySetOneDocument.add(document, payedFactor);

				// Fill the row with the document data
				col = 0;
				setCellText(spreadsheet1, row, col++, DataUtils.DateAsLocalString(document.getStringValueByKey("paydate")));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKey("name"));
				setCellText(spreadsheet1, row, col++, DataUtils.DateAsLocalString(document.getStringValueByKey("date")));
				int addressid = document.getIntValueByKey("addressid");

				// Fill the address columns with the contact that corresponds to the addressid
				if (addressid >= 0) {
					setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:firstname"));
					setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:name"));
					setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:company"));
					setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:vatnr"));
					setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:country"));
				}
				// ... or use the documents first line
				else {
					setCellText(spreadsheet1, row, col++, document.getStringValueByKey("addressfirstline"));
					col += 4;
				}

				setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, document.getDoubleValueByKey("total"));
				setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, document.getDoubleValueByKey("payvalue"));

				// Calculate the total VAT of the document
				PriceValue totalVat = new PriceValue(0.0);

				// Get all VAT entries of this document and place them into the
				// corresponding column.
				for (Iterator<VatSummaryItem> iterator = vatSummarySetOneDocument.getVatSummaryItems().iterator(); iterator.hasNext();) {
					VatSummaryItem item = iterator.next();

					// Get the column
					int column = vatSummarySetAllDocuments.getIndex(item) - zeroVatColumns;

					// If column is <0, it was a VAT entry with 0%
					if (column >= 0) {

						// Round the VAT and add fill the table cell
						PriceValue vat = new PriceValue(item.getVat());
						totalVat.add(vat.asRoundedDouble());
						setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, column + (col + 1), vat.asRoundedDouble());
					}
				}

				// Get all net entries of this document and place them into the
				// corresponding column.
				for (Iterator<VatSummaryItem> iterator = vatSummarySetOneDocument.getVatSummaryItems().iterator(); iterator.hasNext();) {
					VatSummaryItem item = iterator.next();

					// Get the column
					int column = vatSummarySetAllDocuments.getIndex(item);

					// If column is <0, it was a VAT entry with 0%
					if (column >= 0) {

						// Round the net and add fill the table cell
						PriceValue net = new PriceValue(item.getNet());
						//totalVat.add(net.asRoundedDouble());
						setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, columnsWithVatHeading + column + (col + 1), net.asRoundedDouble());
					}
				}

				// Calculate the documents net total (incl. shipping) 
				// by the documents total value and the sum of all VAT values.
				Double net = document.getDoubleValueByKey("payvalue") - totalVat.asRoundedDouble();
				setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, net);

				// Calculate the documents net total (incl. shipping)
				// a second time, but now use the documents net value,
				// and scale it by the scale factor.
				Double totalNet = document.getSummary().getTotalNet().asDouble();
				//totalNet += document.getSummary().getShipping().getUnitNet().asDouble();

				Double roundingError = totalNet * payedFactor - net;

				// Normally both results must be equal.
				// If the difference is grater than 1 Cent, display a warning.
				// It could be a rounding error.
				if (Math.abs(roundingError) > 0.01)
					setCellTextInRedBold(spreadsheet1, row, col + columnsWithVatHeading + columnsWithNetHeading, "Runden prüfen");

				// Set the background of the table rows. Use an light and
				// alternating blue color.
				if ((row % 2) == 0)
					CellFormatter.setBackgroundColor(spreadsheet1, 0, row, col + columnsWithVatHeading + columnsWithNetHeading - 1, row, 0x00e8ebed);

				row++;
			}
		}

		// Insert a formula to calculate the sum of a column.
		// "sumrow" is the row under the table.
		int sumrow = row;

		// Show the sum only, if there are values in the table
		if (sumrow > (headLine + 1)) {
			for (int i = -1; i < (columnsWithVatHeading + columnsWithNetHeading); i++) {
				col = 11 + i;
				try {
					// Create formula for the sum. 
					String cellNameBegin = CellFormatter.getCellName(headLine + 1, col);
					String cellNameEnd = CellFormatter.getCellName(row - 1, col);
					spreadsheet1.getCellByPosition(col, sumrow).setFormula("=SUM(" + cellNameBegin + ":" + cellNameEnd + ")");
					CellFormatter.setBold(spreadsheet1, sumrow, col);
				}
				catch (IndexOutOfBoundsException e) {
				}
			}
		}

		// Draw a horizontal line (set the border of the top and the bottom
		// of the table).
		for (col = 0; col < (columnsWithVatHeading + columnsWithNetHeading) + 11; col++) {
			CellFormatter.setBorder(spreadsheet1, headLine, col, 0x000000, false, false, true, false);
			CellFormatter.setBorder(spreadsheet1, sumrow, col, 0x000000, true, false, false, false);
		}

		// Create a expenditure summary set manager that collects all expenditure VAT
		// values of all documents
		ExpenditureSummarySetManager expenditureSummarySetAllExpenditures = new ExpenditureSummarySetManager();

		// Table heading
		row += 3;
		col = 0;

		setCellTextInBold(spreadsheet1, row++, 0, "Ausgaben");
		row++;

		// Table column headings
		headLine = row;
		setCellTextInBold(spreadsheet1, row, col++, "Kategorie");
		setCellTextInBold(spreadsheet1, row, col++, "Datum");
		setCellTextInBold(spreadsheet1, row, col++, "Belegnr.");
		setCellTextInBold(spreadsheet1, row, col++, "Dokumentnr.");
		setCellTextInBold(spreadsheet1, row, col++, "Lieferant");
		setCellTextInBold(spreadsheet1, row, col++, "Art");

		if (showExpenditureSumColumn) {
			setCellTextInBold(spreadsheet1, row, col++, "Netto");
			setCellTextInBold(spreadsheet1, row, col++, "Brutto");
		}

		row++;
		int columnOffset = col;

		// The expenditures are exported in 2 runs.
		// First, only the summary of all expenditures is calculated and
		// the columns are created.
		// Later all the expenditures are analyzed a second time and then they
		// are exported expenditure by expenditure into the table.
		for (DataSetExpenditure expenditure : expenditures) {

			if (expenditureShouldBeExported(expenditure)) {
				expenditureSummarySetAllExpenditures.add(expenditure, false);
			}
		}

		vatIsNotZero = false;
		col = columnOffset;
		columnsWithVatHeading = 0;
		columnsWithNetHeading = 0;

		// A column for each Vat value is created 
		// The VAT summary items are sorted. So first ignore the VAT entries
		// with 0%. 
		// If the VAT value is >0%, create a column with heading.
		for (Iterator<VatSummaryItem> iterator = expenditureSummarySetAllExpenditures.getExpenditureSummaryItems().iterator(); iterator.hasNext();) {
			VatSummaryItem item = iterator.next();

			// Create a column, if the value is not 0%
			if ((item.getVat().doubleValue() > 0.001) || vatIsNotZero || showZeroVatColumn) {

				// If the first non-zero VAT column is created,
				// do not check the value any more.
				vatIsNotZero = true;

				// Count the columns
				columnsWithVatHeading++;

				// Create a column heading in bold
				int column = expenditureSummarySetAllExpenditures.getIndex(item) - zeroVatColumns;

				// Add VAT name and description and use 2 lines
				String text = item.getVatName();
				String description = item.getDescription();

				if (!description.isEmpty())
					text += "\n" + description;

				setCellTextInBold(spreadsheet1, headLine, column + columnOffset, text);

			}
			else
				// Count the columns with 0% VAT
				zeroVatColumns++;
		}

		// A column for each Net value is created 
		// The Net summary items are sorted. 
		for (Iterator<VatSummaryItem> iterator = expenditureSummarySetAllExpenditures.getExpenditureSummaryItems().iterator(); iterator.hasNext();) {
			VatSummaryItem item = iterator.next();

			// Count the columns
			columnsWithNetHeading++;

			// Create a column heading in bold
			int column = expenditureSummarySetAllExpenditures.getIndex(item);

			// Add VAT name and description and use 2 lines
			String text = "Netto" + "\n" + item.getVatName();
			String description = item.getDescription();

			if (!description.isEmpty())
				text += "\n" + description;

			setCellTextInBold(spreadsheet1, headLine, columnsWithVatHeading + column + columnOffset, text);
		}

		int expenditureIndex = 0;

		// Second run.
		// Export the expenditure data
		for (DataSetExpenditure expenditure : expenditures) {

			if (expenditureShouldBeExported(expenditure)) {

				for (int expenditureItemIndex = 0; expenditureItemIndex < expenditure.getItems().getDatasets().size(); expenditureItemIndex++) {

					DataSetExpenditureItem expenditureItem = expenditure.getItem(expenditureItemIndex);

					// Now analyze expenditure by expenditure
					ExpenditureSummarySetManager vatSummarySetOneExpenditure = new ExpenditureSummarySetManager();
					expenditure.calculate();

					// Add the expenditure to the VAT summary
					vatSummarySetOneExpenditure.add(expenditure, false, expenditureItemIndex);

					// Fill the row with the expenditure data
					col = 0;

					if (expenditureItemIndex == 0) {
						setCellText(spreadsheet1, row, col++, expenditure.getStringValueByKey("category"));
						setCellText(spreadsheet1, row, col++, DataUtils.DateAsLocalString(expenditure.getStringValueByKey("date")));
						setCellText(spreadsheet1, row, col++, expenditure.getStringValueByKey("nr"));
						setCellText(spreadsheet1, row, col++, expenditure.getStringValueByKey("documentnr"));
						setCellText(spreadsheet1, row, col++, expenditure.getStringValueByKey("name"));
					}

					col = 5;
					setCellText(spreadsheet1, row, col++, expenditureItem.getStringValueByKey("name"));

					//setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, document.getDoubleValueByKey("total"));

					// Calculate the total VAT of the expenditure
					PriceValue totalVat = new PriceValue(0.0);

					// Get all VAT entries of this expenditure and place them into the
					// corresponding column.
					for (Iterator<VatSummaryItem> iterator = vatSummarySetOneExpenditure.getExpenditureSummaryItems().iterator(); iterator.hasNext();) {
						VatSummaryItem item = iterator.next();

						// Get the column
						int column = expenditureSummarySetAllExpenditures.getIndex(item) - zeroVatColumns;

						// If column is <0, it was a VAT entry with 0%
						if (column >= 0) {

							// Round the VAT and add fill the table cell
							PriceValue vat = new PriceValue(item.getVat());
							totalVat.add(vat.asRoundedDouble());
							setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, column + columnOffset, vat.asRoundedDouble());
						}
					}

					// Get all net entries of this expenditure and place them into the
					// corresponding column.
					for (Iterator<VatSummaryItem> iterator = vatSummarySetOneExpenditure.getExpenditureSummaryItems().iterator(); iterator.hasNext();) {
						VatSummaryItem item = iterator.next();

						// Get the column
						int column = expenditureSummarySetAllExpenditures.getIndex(item);

						// If column is <0, it was a VAT entry with 0%
						if (column >= 0) {

							// Round the net and add fill the table cell
							PriceValue net = new PriceValue(item.getNet());
							//totalVat.add(net.asRoundedDouble());
							setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, columnsWithVatHeading + column + columnOffset,
									net.asRoundedDouble());
						}
					}

					// Display the sum of an expenditure only in the row of the first
					// expenditure item
					if (showExpenditureSumColumn) {
						if (expenditureItemIndex == 0) {
							col = columnOffset - 2;
							// Calculate the expenditures net and gross total 
							setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, expenditure.getSummary().getTotalNet().asDouble());
							setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, expenditure.getSummary().getTotalGross().asDouble());
						}
					}

					// Set the background of the table rows. Use an light and
					// alternating blue color.
					if ((expenditureIndex % 2) == 0)
						CellFormatter.setBackgroundColor(spreadsheet1, 0, row, columnsWithVatHeading + columnsWithNetHeading + columnOffset - 1, row,
								0x00e8ebed);

					row++;

				}
				expenditureIndex++;
			}
		}

		// Insert a formula to calculate the sum of a column.
		// "sumrow" is the row under the table.
		sumrow = row;

		// Show the sum only, if there are values in the table
		if (sumrow > (headLine + 1)) {
			for (int i = (showExpenditureSumColumn ? -2 : 0); i < (columnsWithVatHeading + columnsWithNetHeading); i++) {
				col = columnOffset + i;
				try {
					// Create formula for the sum. 
					String cellNameBegin = CellFormatter.getCellName(headLine + 1, col);
					String cellNameEnd = CellFormatter.getCellName(row - 1, col);
					spreadsheet1.getCellByPosition(col, sumrow).setFormula("=SUM(" + cellNameBegin + ":" + cellNameEnd + ")");
					CellFormatter.setBold(spreadsheet1, sumrow, col);
				}
				catch (IndexOutOfBoundsException e) {
				}
			}
		}

		// Draw a horizontal line (set the border of the top and the bottom
		// of the table).
		for (col = 0; col < (columnsWithVatHeading + columnsWithNetHeading) + columnOffset; col++) {
			CellFormatter.setBorder(spreadsheet1, headLine, col, 0x000000, false, false, true, false);
			CellFormatter.setBorder(spreadsheet1, sumrow, col, 0x000000, true, false, false, false);
		}

		// Create a expenditure summary set manager that collects all 
		// categories of expenditure items
		ExpenditureSummarySetManager expenditureSummaryCategories = new ExpenditureSummarySetManager();

		// Calculate the summary
		for (DataSetExpenditure expenditure : expenditures) {

			if (expenditureShouldBeExported(expenditure)) {
				expenditureSummaryCategories.add(expenditure, true);
			}
		}

		row += 3;
		// Table heading
		setCellTextInBold(spreadsheet1, row++, 0, "Zusammenfassung Ausgaben:");
		row++;

		col = 0;

		//Heading for the categories
		setCellTextInBold(spreadsheet1, row, col++, "Art");
		setCellTextInBold(spreadsheet1, row, col++, "Vorsteuer");
		setCellTextInBold(spreadsheet1, row, col++, "Vorsteuer");
		setCellTextInBold(spreadsheet1, row, col++, "Netto");

		// Draw a horizontal line
		for (col = 0; col < 4; col++) {
			CellFormatter.setBorder(spreadsheet1, row, col, 0x000000, false, false, true, false);
		}

		row++;

		// A column for each Vat value is created 
		// The VAT summary items are sorted. So first ignore the VAT entries
		// with 0%. 
		// If the VAT value is >0%, create a column with heading.
		for (Iterator<VatSummaryItem> iterator = expenditureSummaryCategories.getExpenditureSummaryItems().iterator(); iterator.hasNext();) {
			VatSummaryItem item = iterator.next();

			col = 0;
			// Round the net and add fill the table cell
			PriceValue vat = new PriceValue(item.getVat());
			PriceValue net = new PriceValue(item.getNet());

			setCellText(spreadsheet1, row, col++, item.getDescription());
			setCellText(spreadsheet1, row, col++, item.getVatName());
			setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, vat.asRoundedDouble());
			setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, net.asRoundedDouble());

			// Set the background of the table rows. Use an light and
			// alternating blue color.
			if ((row % 2) == 0)
				CellFormatter.setBackgroundColor(spreadsheet1, 0, row, 3, row, 0x00e8ebed);

			row++;

		}

		// Draw a horizontal line
		for (col = 0; col < 4; col++) {
			CellFormatter.setBorder(spreadsheet1, row - 1, col, 0x000000, false, false, true, false);
		}

		// True = Export was successful
		return true;
	}

	/**
	 * Fill a cell with a text
	 * 
	 * @param spreadsheet
	 *            The spreadsheet that contains the cell
	 * @param row
	 *            The cell row
	 * @param column
	 *            The cell column
	 * @param text
	 *            The text that will be insert
	 */
	private void setCellText(XSpreadsheet spreadsheet, int row, int column, String text) {
		XText cellText = (XText) UnoRuntime.queryInterface(XText.class, CellFormatter.getCell(spreadsheet, row, column));
		cellText.setString(text);
	}

	/**
	 * Fill a cell with a text. Use a bold font.
	 * 
	 * @param spreadsheet
	 *            The spreadsheet that contains the cell
	 * @param row
	 *            The cell row
	 * @param column
	 *            The cell column
	 * @param text
	 *            The text that will be insert
	 */
	private void setCellTextInBold(XSpreadsheet spreadsheet, int row, int column, String text) {
		setCellText(spreadsheet, row, column, text);
		CellFormatter.setBold(spreadsheet, row, column);
	}

	/**
	 * Fill a cell with a text. Use an italic font style.
	 * 
	 * @param spreadsheet
	 *            The spreadsheet that contains the cell
	 * @param row
	 *            The cell row
	 * @param column
	 *            The cell column
	 * @param text
	 *            The text that will be insert
	 */
	private void setCellTextInItalic(XSpreadsheet spreadsheet, int row, int column, String text) {
		setCellText(spreadsheet, row, column, text);
		CellFormatter.setItalic(spreadsheet, row, column);
	}

	/**
	 * Fill a cell with a text. Use a red and bold font
	 * 
	 * @param spreadsheet
	 *            The spreadsheet that contains the cell
	 * @param row
	 *            The cell row
	 * @param column
	 *            The cell column
	 * @param text
	 *            The text that will be insert
	 */
	private void setCellTextInRedBold(XSpreadsheet spreadsheet, int row, int column, String text) {
		setCellText(spreadsheet, row, column, text);
		CellFormatter.setBold(spreadsheet, row, column);
		CellFormatter.setColor(spreadsheet, row, column, 0x00FF0000);
	}

	/**
	 * Set a cell to a double value and format it with the local currency.
	 * 
	 * @param xSpreadsheetDocument
	 *            The spreadsheet document
	 * @param spreadsheet
	 *            The spreadsheet that contains the cell
	 * @param row
	 *            The cell row
	 * @param column
	 *            The cell column
	 * @param d
	 *            The value that will be inserted.
	 */
	private void setCellValueAsLocalCurrency(XSpreadsheetDocument xSpreadsheetDocument, XSpreadsheet spreadsheet, int row, int column, Double d) {
		CellFormatter.getCell(spreadsheet, row, column).setValue(d);
		CellFormatter.setLocalCurrency(xSpreadsheetDocument, spreadsheet, row, column);
	}

}
