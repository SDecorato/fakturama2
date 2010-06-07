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

package com.sebulli.fakturama.openoffice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.document.URLAdapter;

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

public class OODocument {
	private DataSetDocument document;
	private DataSetContact contact;
	private Properties properties;
	ITextFieldService textFieldService;

	public OODocument(DataSetDocument document, String template) {
		String url = null;
		this.document = document;
		try {
			final IOfficeApplication officeAplication = OpenOfficeStarter.openOfficeAplication();
			if (officeAplication == null)
				return;

			// IDocument oOdocument =
			// officeAplication.getDocumentService().constructNewDocument(IDocument.WRITER,
			// DocumentDescriptor.DEFAULT);

			try {
				url = URLAdapter.adaptURL(template);
			} catch (Exception e) {
				Logger.logError(e, "Error in template filename:" + template);
			}
			IDocument oOdocument = officeAplication.getDocumentService().loadDocument(url);

			ITextDocument textDocument = (ITextDocument) oOdocument;
			int addressId = document.getIntValueByKey("addressid");

			contact = null;
			if (addressId >= 0) {
				try {
					contact = Data.INSTANCE.getContacts().getDatasetById(addressId);
				} catch (Exception e) {
				}
			}

			this.document.calculate();
			textFieldService = textDocument.getTextFieldService();
			ITextField[] placeholders = textFieldService.getPlaceholderFields();

			properties = new Properties();
			setCommonProperties();

			ITextTable itemsTable = null;
			ITextTable vatListTable = null;
			ITextTableCell vatListCell = null;
			for (int i = 0; i < placeholders.length; i++) {
				ITextField placeholder = placeholders[i];
				String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();

				if (placeholderDisplayText.equals("<ITEM.NAME>") || placeholderDisplayText.equals("<ITEM.DESCRIPTION>")) {
					itemsTable = placeholder.getTextRange().getCell().getTextTable();
				}
				if (placeholderDisplayText.equals("<VATLIST.VALUES>") || placeholderDisplayText.equals("<VATLIST.DESCRIPTIONS>")) {
					vatListCell = placeholder.getTextRange().getCell();
					vatListTable = vatListCell.getTextTable();
				}
			}

			ArrayList<DataSetItem> itemDataSets = document.getItems().getActiveDatasets();
			int lastTemplateRow = 0;

			if (itemsTable != null) {
				lastTemplateRow = itemsTable.getRowCount();
				itemsTable.addRow(itemDataSets.size());
				for (int i = 0; i < placeholders.length; i++) {
					ITextField placeholder = placeholders[i];
					String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();
					if (placeholder.getTextRange().getCell() != null) {
						ITextTable textTable = placeholder.getTextRange().getCell().getTextTable();
						if (textTable.getName().equals(itemsTable.getName())) {
							int column = placeholder.getTextRange().getCell().getName().getColumnIndex();
							replaceItemPlaceholder(placeholderDisplayText, column, itemDataSets, itemsTable, lastTemplateRow);
						}
					}
				}
			}
			// Properties vatList = new Properties();
			// document.addVatsToList(vatList);

			VatSummarySetManager vatSummarySetManager = new VatSummarySetManager();
			vatSummarySetManager.add(this.document);

			int vatListTemplateRow = 0;
			if (vatListTable != null) {
				vatListTemplateRow = vatListCell.getName().getRowIndex();
				vatListTable.addRow(vatListTemplateRow + 1, vatSummarySetManager.size());

				for (int i = 0; i < placeholders.length; i++) {
					ITextField placeholder = placeholders[i];
					String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();
					if (placeholder.getTextRange().getCell() != null) {
						ITextTable textTable = placeholder.getTextRange().getCell().getTextTable();
						if (textTable.getName().equals(vatListTable.getName())) {
							int column = placeholder.getTextRange().getCell().getName().getColumnIndex();
							replaceVatListPlaceholder(placeholderDisplayText, column, vatSummarySetManager.getVatSummaryItems(), vatListTable,
									vatListTemplateRow);
						}
					}
				}
			}

			for (int i = 0; i < placeholders.length; i++) {
				replaceText(placeholders[i]);
			}

			if (itemsTable != null) {
				itemsTable.removeRow(lastTemplateRow - 1);
			}

			if (vatListTable != null) {
				vatListTable.removeRow(vatListTemplateRow);
			}
			// textDocument.getFrame().getDispatch(GlobalCommands.PRINT_DOCUMENT_DIRECT).dispatch();
			// textDocument.close();
			// officeAplication.deactivate();
		} catch (Exception e) {
			Logger.logError(e, "Error starting OpenOffice from " + url);
		}
	}

	private void replaceVatListPlaceholder(String placeholderDisplayText, int column, VatSummarySet vatSummarySet, ITextTable vatListTable, int templateRow) {
		int i = 0;
		for (Iterator<VatSummaryItem> iterator = vatSummarySet.iterator(); iterator.hasNext(); i++) {
			VatSummaryItem vatSummaryItem = iterator.next();
			try {
				IText iText = vatListTable.getCell(column, templateRow + i + 1).getTextService().getText();
				replaceVatListPlaceholder(placeholderDisplayText, vatSummaryItem.getVatName(), Double.toString(vatSummaryItem.getVat()), iText, i);
			} catch (TextException e) {
				Logger.logError(e, "Error replacing Vat List Placeholders");
			}
		}
	}

	private void addUserTextField(String key, String value) {
		try {
			textFieldService.addUserTextField(key, value);
		} catch (TextException e) {
			Logger.logError(e, "Error settung User Text Field: " + key + " to " + value);
		}
	}

	private void addUserTextField(String key, String value, int i) {
		key = key + "." + Integer.toString(i);
		addUserTextField(key, value);
	}

	private void replaceVatListPlaceholder(String placeholderDisplayText, String key, String value, IText iText, int index) {
		String textValue;
		String textKey = placeholderDisplayText.substring(1, placeholderDisplayText.length() - 1);

		if (placeholderDisplayText.equals("<VATLIST.DESCRIPTIONS>")) {
			textValue = key;
		}

		else if (placeholderDisplayText.equals("<VATLIST.VALUES>")) {
			textValue = DataUtils.DoubleToFormatedPriceRound(Double.parseDouble(value));
		}

		else
			return;

		iText.setText(textValue);
		addUserTextField(textKey, textValue, index);

	}

	private void replaceItemPlaceholder(String placeholderDisplayText, int column, ArrayList<DataSetItem> itemDataSets, ITextTable itemsTable,
			int lastTemplateRow) {
		for (int row = 0; row < itemDataSets.size(); row++) {
			try {
				IText iText = itemsTable.getCell(column, lastTemplateRow + row).getTextService().getText();
				DataSetItem item = itemDataSets.get(row);
				replaceItemPlaceholder(placeholderDisplayText, item, iText, row);

			} catch (TextException e) {
				Logger.logError(e, "Error replacing Placeholders");
			}
		}

	}

	private void replaceItemPlaceholder(String placeholderDisplayText, DataSetItem item, IText iText, int index) {

		String value;
		String key = placeholderDisplayText.substring(1, placeholderDisplayText.length() - 1);
		Price price = new Price(item);

		if (placeholderDisplayText.equals("<ITEM.QUANTITY>")) {
			value = DataUtils.DoubleToFormatedQuantity(item.getDoubleValueByKey("quantity"));
		}

		else if (placeholderDisplayText.equals("<ITEM.NAME>")) {
			value = item.getStringValueByKey("name");
		}

		else if (placeholderDisplayText.equals("<ITEM.NR>")) {
			value = item.getStringValueByKey("itemnr");
		}

		else if (placeholderDisplayText.equals("<ITEM.DESCRIPTION>")) {
			value = item.getStringValueByKey("description");
		}

		else if (placeholderDisplayText.equals("<ITEM.VAT.PERCENT>")) {
			value = DataUtils.DoubleToFormatedPercent(item.getDoubleValueByKey("vatvalue"));
		}

		else if (placeholderDisplayText.equals("<ITEM.VAT.NAME>")) {
			value = item.getStringValueByKey("vatname");
		}

		else if (placeholderDisplayText.equals("<ITEM.VAT.DESCRIPTION>")) {
			value = item.getStringValueByKey("vatdescription");
		}

		else if (placeholderDisplayText.equals("<ITEM.UNIT.NET>")) {
			value = price.getUnitNetRounded().asFormatedString();
		}

		else if (placeholderDisplayText.equals("<ITEM.UNIT.VAT>")) {
			value = price.getUnitVatRounded().asFormatedString();
		}

		else if (placeholderDisplayText.equals("<ITEM.UNIT.GROSS>")) {
			value = price.getUnitGrossRounded().asFormatedString();
		}

		else if (placeholderDisplayText.equals("<ITEM.TOTAL.NET>")) {
			value = price.getTotalNetRounded().asFormatedString();
		}

		else if (placeholderDisplayText.equals("<ITEM.TOTAL.VAT>")) {
			value = price.getTotalVatRounded().asFormatedString();
		}

		else if (placeholderDisplayText.equals("<ITEM.TOTAL.GROSS>")) {
			value = price.getTotalGrossRounded().asFormatedString();
		} else
			return;

		iText.setText(value);
		addUserTextField(key, value, index);
	}

	private void setProperty(String key, String value) {
		addUserTextField(key, value);
		properties.setProperty("<" + key + ">", value);
	}

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
			setProperty("DOCUMENT.ITEMS.NET", document.getSummary().getItemsNet().asFormatedString());
			setProperty("DOCUMENT.TOTAL.VAT", document.getSummary().getTotalVat().asFormatedString());
			setProperty("DOCUMENT.TOTAL.GROSS", document.getSummary().getTotalGross().asFormatedString());
			setProperty("ITEMS.DISCOUNT.PERCENT", document.getFormatedStringValueByKey("itemsdiscount"));

			setProperty("SHIPPING.NET", document.getSummary().getShipping().getUnitNetRounded().asFormatedString());
			setProperty("SHIPPING.VAT", document.getSummary().getShipping().getUnitVatRounded().asFormatedString());
			setProperty("SHIPPING.GROSS", document.getSummary().getShipping().getUnitGrossRounded().asFormatedString());
			setProperty("SHIPPING.NAME", document.getStringValueByKey("shippingname"));
			setProperty("SHIPPING.VAT.DESCRIPTION", document.getStringValueByKey("shippingvatdescription"));

			setProperty("PAYMENT.NAME", document.getStringValueByKey("paymentname"));
			/*
			 * setProperty("PAYMENT.DISCOUNT.PERCENT",
			 * document.getFormatedStringValueByKey("paymentdiscount"));
			 * setProperty("PAYMENT.DISCOUNT.VALUE",
			 * DataUtils.DoubleToFormatedPriceRound
			 * (document.getDoubleValueByKey("paymentdiscount")
			 * document.getSummary().getTotalGross().asDouble()));
			 */
			setProperty("PAYMENT.PAYED.VALUE", DataUtils.DoubleToFormatedPriceRound(document.getDoubleValueByKey("payvalue")));
			setProperty("PAYMENT.PAYED.DATE", document.getFormatedStringValueByKey("paydate"));
			setProperty("PAYMENT.DUE.DAYS", Integer.toString(document.getIntValueByKey("duedays")));
			setProperty("PAYMENT.DUE.DATE", DataUtils.DateAsLocalString(DataUtils.AddToDate(document.getStringValueByKey("date"), document
					.getIntValueByKey("duedays"))));
			setProperty("PAYMENT.PAYED", document.getStringValueByKey("payed"));
		}

		if (contact != null) {
			setProperty("ADDRESS", contact.getAddress());
			setProperty("ADDRESS.GENDER", contact.getGenderString());
			setProperty("ADDRESS.TITLE", contact.getStringValueByKey("title"));
			setProperty("ADDRESS.FIRSTNAME", contact.getStringValueByKey("firstname"));
			setProperty("ADDRESS.NAME", contact.getStringValueByKey("name"));
			setProperty("ADDRESS.COMPANY", contact.getStringValueByKey("company"));
			setProperty("ADDRESS.STREET", contact.getStringValueByKey("street"));
			setProperty("ADDRESS.ZIP", contact.getStringValueByKey("zip"));
			setProperty("ADDRESS.CITY", contact.getStringValueByKey("city"));
			setProperty("ADDRESS.COUNTRY", contact.getStringValueByKey("country"));
			setProperty("DELIVERY.ADDRESS", contact.getDeliveryAddress());
			setProperty("DELIVERY.ADDRESS.GENDER", contact.getDeliveryGenderString());
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

	}

	private void replaceText(ITextField placeholder) {
		String placeholderDisplayText = placeholder.getDisplayText().toUpperCase();
		placeholder.getTextRange().setText(properties.getProperty(placeholderDisplayText));
	}
}
