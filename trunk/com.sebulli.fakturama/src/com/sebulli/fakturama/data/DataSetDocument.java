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

package com.sebulli.fakturama.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.sebulli.fakturama.calculate.DocumentSummary;
import com.sebulli.fakturama.logger.Logger;

public class DataSetDocument extends UniDataSet {
	DocumentSummary summary = new DocumentSummary();

	public DataSetDocument() {
		this(DocumentType.LETTER);
	}

	public DataSetDocument(DocumentType documentType) {
		this(-1, "", documentType);
	}

	public DataSetDocument(DocumentType documentType, String webshopid, String webshopdate) {
		this(-1, "000000", false, documentType, -1, "", "", "", 0, "", "2000-01-01", "2000-01-01", -1, "", 0, false, "2000-01-01", 0.0, "", 0, "", 0.0, 0.0,
				"", 1, 0.0, "", 0, webshopid, webshopdate, false, "", "", 0.0, 0, -1);

		DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
		this.hashMap.put("date", new UniData(UniDataType.DATE, dfmt.format(new Date())));
		this.hashMap.put("transaction", new UniData(UniDataType.INT, Math.abs(UUID.randomUUID().hashCode())));
	}

	public DataSetDocument(int addressid, String address, DocumentType documentType) {
		this(-1, "000000", false, documentType, addressid, address, address, address, 0, "", "2000-01-01", "2000-01-01", -1, "", 0, false, "2000-01-01", 0.0,
				"", 0, "", 0.0, 0.0, "", 1, 0.0, "", 0, "", "2000-01-01", false, "", "", 0.0, 0, -1);

		DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
		this.hashMap.put("date", new UniData(UniDataType.DATE, dfmt.format(new Date())));
		this.hashMap.put("transaction", new UniData(UniDataType.INT, Math.abs(UUID.randomUUID().hashCode())));

	}

	public DataSetDocument(DocumentType documentType, DataSetDocument parent) {
		this(-1, parent.getStringValueByKey("name"), false, documentType, parent.getIntValueByKey("addressid"), parent.getStringValueByKey("address"), parent
				.getStringValueByKey("deliveryaddress"), parent.getStringValueByKey("addressfirstline"), parent.getIntValueByKey("progress"), parent
				.getStringValueByKey("customerref"), (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()), (new SimpleDateFormat("yyyy-MM-dd"))
				.format(new Date()), parent.getIntValueByKey("paymentid"), parent.getStringValueByKey("paymentname"), parent.getIntValueByKey("duedays"),
				false, parent.getStringValueByKey("paydate"), 0.0, parent.getStringValueByKey("items"), parent.getIntValueByKey("shippingid"), parent
						.getStringValueByKey("shippingname"), parent.getDoubleValueByKey("shipping"), parent.getDoubleValueByKey("shippingvat"), parent
						.getStringValueByKey("shippingvatdescription"), parent.getIntValueByKey("shippingautovat"), parent.getDoubleValueByKey("total"), parent
						.getStringValueByKey("message"), parent.getIntValueByKey("transaction"), parent.getStringValueByKey("webshopid"), parent
						.getStringValueByKey("webshopdate"), parent.getBooleanValueByKey("no"), parent.getStringValueByKey("novatname"), parent
						.getStringValueByKey("novatdescription"), parent.getDoubleValueByKey("itemsdiscount"), parent.getIntValueByKey("dunninglevel"), parent
						.getIntValueByKey("invoiceid"));

		// items = new DataSetArray<DataSetItem>();

		String itemsString = this.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");

		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				} catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}
				DataSetItem item = Data.INSTANCE.getItems().getDatasetById(id);
				item.setBooleanValueByKey("shared", true);
				Data.INSTANCE.updateDataSet(item);
			}
		}
	}

	public DataSetDocument(int id, String name, boolean deleted, DocumentType documentType, int addressid, String address, String deliveryaddress,
			String addressfirstline, int progress, String customerref, String date, String servicedate, int paymentid, String paymentname, int duedays,
			boolean payed, String paydate, Double payvalue, String items, int shippingid, String shippingname, Double shipping, Double shippingvat,
			String shippingvatdescription, int shippingautovat, Double total, String message, int transaction, String webshopid, String webshopdate,
			boolean noVat, String noVatName, String noVatDescription, double itemsdiscount, int dunninglevel, int invoiceid) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.INT, documentType.getInt()));
		this.hashMap.put("addressid", new UniData(UniDataType.ID, addressid));
		this.hashMap.put("address", new UniData(UniDataType.STRING, address));
		this.hashMap.put("deliveryaddress", new UniData(UniDataType.STRING, deliveryaddress));
		this.hashMap.put("addressfirstline", new UniData(UniDataType.STRING, addressfirstline));
		this.hashMap.put("progress", new UniData(UniDataType.INT, progress));
		this.hashMap.put("customerref", new UniData(UniDataType.STRING, customerref));
		this.hashMap.put("date", new UniData(UniDataType.DATE, date));
		this.hashMap.put("servicedate", new UniData(UniDataType.DATE, servicedate));
		this.hashMap.put("paymentid", new UniData(UniDataType.ID, paymentid));
		this.hashMap.put("paymentname", new UniData(UniDataType.STRING, paymentname));
		this.hashMap.put("duedays", new UniData(UniDataType.INT, duedays));
		this.hashMap.put("payed", new UniData(UniDataType.BOOLEAN, payed));
		this.hashMap.put("paydate", new UniData(UniDataType.DATE, paydate));
		this.hashMap.put("payvalue", new UniData(UniDataType.PRICE, payvalue));
		this.hashMap.put("items", new UniData(UniDataType.STRING, items));
		this.hashMap.put("shippingid", new UniData(UniDataType.ID, shippingid));
		this.hashMap.put("shippingname", new UniData(UniDataType.STRING, shippingname));
		this.hashMap.put("shipping", new UniData(UniDataType.PRICE, shipping));
		this.hashMap.put("shippingvat", new UniData(UniDataType.PERCENT, shippingvat));
		this.hashMap.put("shippingvatdescription", new UniData(UniDataType.STRING, shippingvatdescription));
		this.hashMap.put("shippingautovat", new UniData(UniDataType.INT, shippingautovat));
		this.hashMap.put("total", new UniData(UniDataType.PRICE, total));
		this.hashMap.put("message", new UniData(UniDataType.STRING, message));
		this.hashMap.put("transaction", new UniData(UniDataType.INT, transaction));
		this.hashMap.put("webshopid", new UniData(UniDataType.STRING, webshopid));
		this.hashMap.put("webshopdate", new UniData(UniDataType.DATE, webshopdate));
		this.hashMap.put("novat", new UniData(UniDataType.BOOLEAN, noVat));
		this.hashMap.put("novatname", new UniData(UniDataType.STRING, noVatName));
		this.hashMap.put("novatdescription", new UniData(UniDataType.STRING, noVatDescription));
		this.hashMap.put("itemsdiscount", new UniData(UniDataType.PERCENT, itemsdiscount));
		this.hashMap.put("dunninglevel", new UniData(UniDataType.INT, dunninglevel));
		this.hashMap.put("invoiceid", new UniData(UniDataType.ID, invoiceid));
		sqlTabeName = "Documents";
	}

	public static String getStringPAYED() {
		return "bezahlt";
	};

	public static String getStringNOTPAYED() {
		return "offen";
	};

	public static String getStringSHIPPED() {
		return "versendet";
	};

	public static String getStringNOTSHIPPED() {
		return "offen";
	};

	public String getCategory() {
		try {
			String category = DocumentType.getPluralString(hashMap.get("category").getValueAsInteger());
			DocumentType documentType = DocumentType.getType(hashMap.get("category").getValueAsInteger());
			switch (documentType) {
			case INVOICE:
			case CREDIT:
			case DUNNING:
				if (this.hashMap.get("payed").getValueAsBoolean())
					category += "/" + DataSetDocument.getStringPAYED();
				else
					category += "/" + DataSetDocument.getStringNOTPAYED();
				break;
			case ORDER:
				switch (this.hashMap.get("progress").getValueAsInteger()) {
				case 0:
				case 10:
				case 50:
					category += "/" + DataSetDocument.getStringNOTSHIPPED();
					break;
				case 90:
				case 100:
					category += "/" + DataSetDocument.getStringSHIPPED();
					break;
				}
				break;
			}
			return category;
		} catch (Exception e) {
			Logger.logError(e, "Error getting key category.");
		}
		return "";
	}

	static public Object[] getCategoryStrings(boolean usedDocuments[]) {

		List<String> list = new ArrayList<String>();

		if (usedDocuments[DocumentType.LETTER.getInt()])
			list.add(DocumentType.LETTER.getPluralString());

		if (usedDocuments[DocumentType.OFFER.getInt()])
			list.add(DocumentType.OFFER.getPluralString());

		if (usedDocuments[DocumentType.ORDER.getInt()]) {
			list.add(DocumentType.ORDER.getPluralString() + "/" + getStringNOTSHIPPED());
			list.add(DocumentType.ORDER.getPluralString() + "/" + getStringSHIPPED());
		}

		if (usedDocuments[DocumentType.CONFIRMATION.getInt()])
			list.add(DocumentType.CONFIRMATION.getPluralString());

		if (usedDocuments[DocumentType.INVOICE.getInt()]) {
			list.add(DocumentType.INVOICE.getPluralString() + "/" + getStringNOTPAYED());
			list.add(DocumentType.INVOICE.getPluralString() + "/" + getStringPAYED());
		}

		if (usedDocuments[DocumentType.DELIVERY.getInt()])
			list.add(DocumentType.DELIVERY.getPluralString());

		if (usedDocuments[DocumentType.CREDIT.getInt()]) {
			list.add(DocumentType.CREDIT.getPluralString() + "/" + getStringNOTPAYED());
			list.add(DocumentType.CREDIT.getPluralString() + "/" + getStringPAYED());
		}

		if (usedDocuments[DocumentType.DUNNING.getInt()]) {
			list.add(DocumentType.DUNNING.getPluralString() + "/" + getStringNOTPAYED());
			list.add(DocumentType.DUNNING.getPluralString() + "/" + getStringPAYED());
		}

		return list.toArray();

	}

	public DataSetArray<DataSetItem> getItems() {
		DataSetArray<DataSetItem> items = new DataSetArray<DataSetItem>();
		String itemsString = this.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");
		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				} catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}
				items.addNewDataSetKeepId(Data.INSTANCE.getItems().getDatasetById(id));
			}
		}
		return items;
	}

	public void calculate() {
		int sign = DocumentType.getType(this.getIntValueByKey("category")).sign();
		calculate(this.getItems(), this.getDoubleValueByKey("shipping") * sign, this.getDoubleValueByKey("shippingvat"), this
				.getStringValueByKey("shippingvatdescription"), this.getIntValueByKey("shippingautovat"), this.getDoubleValueByKey("itemsdiscount"), this
				.getBooleanValueByKey("novat"), this.getStringValueByKey("novatdescription"));
	}

	public void calculate(DataSetArray<DataSetItem> items, double shippingNet, double shippingVat, String shippingVatDescription, int shippingAutoVat,
			Double itemsDiscount, boolean noVat, String noVatDescription) {
		summary.calculate(null, items, shippingNet, shippingVat, shippingVatDescription, shippingAutoVat, itemsDiscount, noVat, noVatDescription);
	}

	public DocumentSummary getSummary() {
		return this.summary;
	}

	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("webshopid").equals(this.getStringValueByKey("webshopid")))
			return false;
		if (!uds.getStringValueByKey("webshopdate").equals(this.getStringValueByKey("webshopdate")))
			return false;
		return true;
	}

}
