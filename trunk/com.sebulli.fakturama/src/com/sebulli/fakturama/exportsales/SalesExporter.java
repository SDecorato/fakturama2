/*
 * 
 *	Fakturama - Free Invoicing Software 
 *  Copyright (C) 2010  Gerd Bartelt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.sebulli.fakturama.exportsales;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.sebulli.fakturama.calculate.PriceValue;
import com.sebulli.fakturama.calculate.VatSummaryItem;
import com.sebulli.fakturama.calculate.VatSummarySetManager;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;
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

public class SalesExporter {
	private GregorianCalendar beginDate;
	private GregorianCalendar endDate;

	public SalesExporter() {
		this.beginDate = null;
		this.endDate = null;
	}

	public SalesExporter(GregorianCalendar beginDate, GregorianCalendar endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	private boolean documentShouldBeExported(DataSetDocument document) {
		boolean isInIntervall = true;

		GregorianCalendar documentDate = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			documentDate.setTime(formatter.parse(document.getStringValueByKey("date")));
		} catch (ParseException e) {
			Logger.logError(e, "Error parsing Date");
		}
		if ((beginDate != null) && (endDate != null)) {
			if (beginDate.after(documentDate))
				isInIntervall = false;
			if (endDate.before(documentDate))
				isInIntervall = false;
		}
		return ((document.getIntValueByKey("category") == DocumentType.INVOICE.getInt()) || (document.getIntValueByKey("category") == DocumentType.CREDIT
				.getInt()))
				&& document.getBooleanValueByKey("payed") && isInIntervall;
	}

	public boolean export() {

		final IOfficeApplication officeAplication = OpenOfficeStarter.openOfficeAplication();
		if (officeAplication == null)
			return false;
		IDocument oOdocument = null;
		try {
			oOdocument = officeAplication.getDocumentService().constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
		} catch (NOAException e) {
			Logger.logError(e, "NOA Error opening CALC");
			return false;
		} catch (OfficeApplicationException e) {
			Logger.logError(e, "OO Error opening CALC");
			return false;
		}

		ISpreadsheetDocument spreadDocument = (ISpreadsheetDocument) oOdocument;
		XSpreadsheetDocument xSpreadsheetDocument = spreadDocument.getSpreadsheetDocument();
		XSpreadsheets spreadsheets = xSpreadsheetDocument.getSheets();

		XSpreadsheet spreadsheet1 = null;
		try {
			String tableName = "Export";
			spreadsheets.insertNewByName(tableName, (short) 0);
			String names[] = spreadsheets.getElementNames();
			for (String name : names) {
				if (!name.equals(tableName))
					spreadsheets.removeByName(name);
			}
			spreadsheet1 = (XSpreadsheet) UnoRuntime.queryInterface(XSpreadsheet.class, spreadsheets.getByName(tableName));
		} catch (NoSuchElementException e) {
			Logger.logError(e, "Error getting spreadsheet");
		} catch (WrappedTargetException e) {
			Logger.logError(e, "Error getting spreadsheet");
		}

		ArrayList<DataSetDocument> documents = Data.INSTANCE.getDocuments().getActiveDatasets();

		VatSummarySetManager vatSummarySetAllDocuments = new VatSummarySetManager();

		int row = 0;
		int col = 0;
		int columnsWithVatHeading = 0;
		int zeroVatColumns = 0;

		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_NAME"));
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_OWNER"));
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_STREET"));
		setCellTextInItalic(spreadsheet1, row++, 0, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_ZIP") + " "
				+ Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_CITY"));
		row++;

		setCellTextInBold(spreadsheet1, row++, 0, "Zeitraum");
		setCellText(spreadsheet1, row, 0, "von:");
		setCellText(spreadsheet1, row++, 1, DataUtils.getDateTimeAsLocalString(beginDate));
		setCellText(spreadsheet1, row, 0, "bis:");
		setCellText(spreadsheet1, row++, 1, DataUtils.getDateTimeAsLocalString(endDate));
		row++;

		setCellTextInBold(spreadsheet1, row++, 0, "Einnahmen");
		row++;

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

		for (DataSetDocument document : documents) {

			if (documentShouldBeExported(document)) {
				document.calculate();
				vatSummarySetAllDocuments.add(document);
			}
		}

		col = 11;
		columnsWithVatHeading = 0;
		boolean vatIsNotZero = false;
		for (Iterator<VatSummaryItem> iterator = vatSummarySetAllDocuments.getVatSummaryItems().iterator(); iterator.hasNext();) {
			VatSummaryItem item = iterator.next();
			if ((item.getVat().doubleValue() > 0.001) || vatIsNotZero) {
				vatIsNotZero = true;
				columnsWithVatHeading++;
				int column = vatSummarySetAllDocuments.getIndex(item) - zeroVatColumns;
				setCellTextInBold(spreadsheet1, headLine, column + col, item.getVatName());
			} else
				zeroVatColumns++;
		}

		for (DataSetDocument document : documents) {

			if (documentShouldBeExported(document)) {
				VatSummarySetManager vatSummarySetOneDocument = new VatSummarySetManager();
				document.calculate();
				vatSummarySetOneDocument.add(document);

				Double payedFactor = document.getDoubleValueByKey("payvalue") / document.getDoubleValueByKey("total");

				col = 0;
				setCellText(spreadsheet1, row, col++, DataUtils.DateAsLocalString(document.getStringValueByKey("paydate")));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKey("name"));
				setCellText(spreadsheet1, row, col++, DataUtils.DateAsLocalString(document.getStringValueByKey("date")));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:firstname"));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:name"));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:company"));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:vatnr"));
				setCellText(spreadsheet1, row, col++, document.getStringValueByKeyFromOtherTable("addressid.CONTACTS:country"));
				setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, document.getDoubleValueByKey("total"));
				setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, document.getDoubleValueByKey("payvalue"));

				PriceValue totalVat = new PriceValue(0.0);
				for (Iterator<VatSummaryItem> iterator = vatSummarySetOneDocument.getVatSummaryItems().iterator(); iterator.hasNext();) {
					VatSummaryItem item = iterator.next();
					int column = vatSummarySetAllDocuments.getIndex(item) - zeroVatColumns;
					if (column >= 0) {
						PriceValue vat = new PriceValue(item.getVat() * payedFactor);
						totalVat.add(vat.asRoundedDouble());
						setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, column + (col + 1), vat.asRoundedDouble());
					}
				}
				Double net = document.getDoubleValueByKey("payvalue") - totalVat.asRoundedDouble();
				setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row, col++, net);
				Double roundingError = document.getSummary().getTotalNet().asDouble() * payedFactor - net;
				if (Math.abs(roundingError) > 0.01)
					setCellTextInRedBold(spreadsheet1, row, col + columnsWithVatHeading, "Runden prŸfen");
				row++;
			}
		}

		int sumrow = row;
		if (sumrow > (headLine + 1)) {
			for (int i = -1; i < columnsWithVatHeading; i++) {
				col = 11 + i;
				try {
					String cellNameBegin = CellFormatter.getCellName(headLine + 1, col);
					String cellNameEnd = CellFormatter.getCellName(row - 1, col);
					spreadsheet1.getCellByPosition(col, sumrow).setFormula("=SUM(" + cellNameBegin + ":" + cellNameEnd + ")");
					CellFormatter.setBold(spreadsheet1, sumrow, col);
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}

		for (col = 0; col < columnsWithVatHeading + 11; col++) {
			CellFormatter.setBorder(spreadsheet1, headLine, col, 0x000000, false, false, true, false);
			CellFormatter.setBorder(spreadsheet1, sumrow, col, 0x000000, true, false, false, false);
		}

		for (row = headLine + 1; row < sumrow; row++) {
			for (col = 0; col < columnsWithVatHeading + 11; col++) {
				if ((row % 2) == 0)
					CellFormatter.setBackgroundColor(spreadsheet1, row, col, 0x00e8ebed);
			}
		}

		/*
		 * col = 0; row += 5; setCellText(spreadsheet1, row, col++,
		 * "MwSt Name"); setCellText(spreadsheet1, row, col++, "Netto Betrag");
		 * setCellText(spreadsheet1, row, col++, "MwSt Betrag");
		 * setCellText(spreadsheet1, row, col++, "Brutto Betrag"); row ++;
		 * 
		 * for ( Iterator<VatSummaryItem> iterator =
		 * vatSummarySetAllDocuments.getVatSummaryItems().iterator();
		 * iterator.hasNext(); ) { col = 0; VatSummaryItem item =
		 * iterator.next(); setCellText(spreadsheet1, row, col++,
		 * item.getVatName()); setCellValueAsLocalCurrency(xSpreadsheetDocument,
		 * spreadsheet1, row, col++, item.getNet());
		 * setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row,
		 * col++, item.getVat());
		 * setCellValueAsLocalCurrency(xSpreadsheetDocument, spreadsheet1, row,
		 * col++, item.getNet() + item.getVat());
		 * 
		 * row ++; }
		 */

		return true;

	}

	private void setCellText(XSpreadsheet spreadsheet, int row, int column, String text) {
		XText cellText = (XText) UnoRuntime.queryInterface(XText.class, CellFormatter.getCell(spreadsheet, row, column));
		cellText.setString(text);
	}

	private void setCellTextInBold(XSpreadsheet spreadsheet, int row, int column, String text) {
		setCellText(spreadsheet, row, column, text);
		CellFormatter.setBold(spreadsheet, row, column);
	}

	private void setCellTextInItalic(XSpreadsheet spreadsheet, int row, int column, String text) {
		setCellText(spreadsheet, row, column, text);
		CellFormatter.setItalic(spreadsheet, row, column);
	}

	private void setCellTextInRedBold(XSpreadsheet spreadsheet, int row, int column, String text) {
		setCellText(spreadsheet, row, column, text);
		CellFormatter.setBold(spreadsheet, row, column);
		CellFormatter.setColor(spreadsheet, row, column, 0x00FF0000);
	}

	private void setCellValueAsLocalCurrency(XSpreadsheetDocument xSpreadsheetDocument, XSpreadsheet spreadsheet, int row, int column, Double d) {
		CellFormatter.getCell(spreadsheet, row, column).setValue(d);
		CellFormatter.setLocalCurrency(xSpreadsheetDocument, spreadsheet, row, column);
	}

}
