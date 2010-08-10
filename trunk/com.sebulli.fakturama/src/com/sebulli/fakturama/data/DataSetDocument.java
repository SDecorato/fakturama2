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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.sebulli.fakturama.calculate.DocumentSummary;
import com.sebulli.fakturama.logger.Logger;

/**
 * UniDataSet for all documents 
 * 
 * @author Gerd Bartelt
 */
public class DataSetDocument extends UniDataSet {
	DocumentSummary summary = new DocumentSummary();

	/**
	 * Constructor
	 * Creates an new letter
	 */
	public DataSetDocument() {
		this(DocumentType.LETTER);
	}

	/**
	 * Constructor
	 * Create a new document
	 * 
	 * @param documentType Type of new document
	 */
	public DataSetDocument(DocumentType documentType) {
		this(documentType, "", (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));

	}

	/**
	 * Constructor
	 * Create a new document, set the date to now and create a transaction ID
	 * 
	 * @param documentType Type of the new document
	 * @param webshopid Web shop ID (order number)
	 * @param webshopdate Web shop date (date of order)
	 */
	public DataSetDocument(DocumentType documentType, String webshopid, String webshopdate) {
		this(-1, "000000", false, documentType, -1, "", "", "", 0, "", (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()), (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()), -1, "", 0, false, "2000-01-01", 0.0, "", "", 0, "", 0.0,
				0.0, "", 1, 0.0, "", 0, webshopid, webshopdate, false, "", "", 0.0, 0, -1);

		this.hashMap.put("transaction", new UniData(UniDataType.INT, Math.abs(UUID.randomUUID().hashCode())));
	}


	/**
	 * Constructor
	 * Create a new document from an other document
	 * Also mark all of the items as "shared"
	 * 
	 * @param documentType Type of the new document
	 * @param parent Parent document
	 */
	public DataSetDocument(DocumentType documentType, DataSetDocument parent) {
		// create a copy
		this(-1, parent.getStringValueByKey("name"), false, documentType, parent.getIntValueByKey("addressid"), parent.getStringValueByKey("address"), parent
				.getStringValueByKey("deliveryaddress"), parent.getStringValueByKey("addressfirstline"), parent.getIntValueByKey("progress"), parent
				.getStringValueByKey("customerref"), (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()), (new SimpleDateFormat("yyyy-MM-dd"))
				.format(new Date()), parent.getIntValueByKey("paymentid"), parent.getStringValueByKey("paymentname"), parent.getIntValueByKey("duedays"),
				false, parent.getStringValueByKey("paydate"), 0.0, parent.getStringValueByKey("paymenttext"),parent.getStringValueByKey("items"), parent.getIntValueByKey("shippingid"), parent
						.getStringValueByKey("shippingname"), parent.getDoubleValueByKey("shipping"), parent.getDoubleValueByKey("shippingvat"), parent
						.getStringValueByKey("shippingvatdescription"), parent.getIntValueByKey("shippingautovat"), parent.getDoubleValueByKey("total"), parent
						.getStringValueByKey("message"), parent.getIntValueByKey("transaction"), parent.getStringValueByKey("webshopid"), parent
						.getStringValueByKey("webshopdate"), parent.getBooleanValueByKey("novat"), parent.getStringValueByKey("novatname"), parent
						.getStringValueByKey("novatdescription"), parent.getDoubleValueByKey("itemsdiscount"), parent.getIntValueByKey("dunninglevel"), parent
						.getIntValueByKey("invoiceid"));


		// Get the Items string, split it ..
		String itemsString = this.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");

		// .. and get all the items.
		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				} catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}
				
				// Mark all items as "shared"
				DataSetItem item = Data.INSTANCE.getItems().getDatasetById(id);
				item.setBooleanValueByKey("shared", true);
				Data.INSTANCE.updateDataSet(item);
			}
		}
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param documentType
	 * @param addressid
	 * @param address
	 * @param deliveryaddress
	 * @param addressfirstline
	 * @param progress
	 * @param customerref
	 * @param date
	 * @param servicedate
	 * @param paymentid
	 * @param paymentname
	 * @param duedays
	 * @param payed
	 * @param paydate
	 * @param payvalue
	 * @param paymenttext
	 * @param items
	 * @param shippingid
	 * @param shippingname
	 * @param shipping
	 * @param shippingvat
	 * @param shippingvatdescription
	 * @param shippingautovat
	 * @param total
	 * @param message
	 * @param transaction
	 * @param webshopid
	 * @param webshopdate
	 * @param noVat
	 * @param noVatName
	 * @param noVatDescription
	 * @param itemsdiscount
	 * @param dunninglevel
	 * @param invoiceid
	 */
	public DataSetDocument(int id, String name, boolean deleted, DocumentType documentType, int addressid, String address, String deliveryaddress,
			String addressfirstline, int progress, String customerref, String date, String servicedate, int paymentid, String paymentname, int duedays,
			boolean payed, String paydate, Double payvalue, String paymenttext, String items, int shippingid, String shippingname, Double shipping, Double shippingvat,
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
		this.hashMap.put("paymenttext", new UniData(UniDataType.STRING, paymenttext));
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
		this.hashMap.put("printed", new UniData(UniDataType.BOOLEAN, false));
		
		// Name of the table in the data base
		sqlTabeName = "Documents";
	}

	/**
	 * Get the payment state as localized string 
	 * 
	 * @return String for "payed"
	 */
	public static String getStringPAYED() {
		return "bezahlt";
	};

	/**
	 * Get the payment state as localized string 
	 * 
	 * @return String for "not payed"
	 */
	public static String getStringNOTPAYED() {
		return "offen";
	};

	/**
	 * Get the shipping state as localized string 
	 * 
	 * @return String for "shipped"
	 */
	public static String getStringSHIPPED() {
		return "versendet";
	};

	/**
	 * Get the shipping state as localized string 
	 * 
	 * @return String for "not shipped"
	 */
	public static String getStringNOTSHIPPED() {
		return "offen";
	};

	/**
	 * Get the catehory as string
	 * 
	 * @return category as string
	 */
	public String getCategory() {
		try {
			String category = DocumentType.getPluralString(hashMap.get("category").getValueAsInteger());
			DocumentType documentType = DocumentType.getType(hashMap.get("category").getValueAsInteger());
			
			// use the document type to generate the category string ..
			switch (documentType) {
			case INVOICE:
			case CREDIT:
			case DUNNING:
				// .. the state of the payment ..
				if (this.hashMap.get("payed").getValueAsBoolean())
					category += "/" + DataSetDocument.getStringPAYED();
				else
					category += "/" + DataSetDocument.getStringNOTPAYED();
				break;
			case ORDER:
				// .. and the state of the shipping
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

	/**
	 * Get the category strings.
	 * Generate only categories of document types, that are existing.
	 * This is used to generate the tree in the documents view.
	 * 
	 * @param usedDocuments Array with all document types, that are used
	 * @return Array with all category strings
	 */
	static public Object[] getCategoryStrings(boolean usedDocuments[]) {

		List<String> list = new ArrayList<String>();

		if (usedDocuments[DocumentType.LETTER.getInt()])
			list.add(DocumentType.LETTER.getPluralString());

		if (usedDocuments[DocumentType.OFFER.getInt()])
			list.add(DocumentType.OFFER.getPluralString());

		if (usedDocuments[DocumentType.ORDER.getInt()]) {
			// add shipping state
			list.add(DocumentType.ORDER.getPluralString() + "/" + getStringNOTSHIPPED());
			list.add(DocumentType.ORDER.getPluralString() + "/" + getStringSHIPPED());
		}

		if (usedDocuments[DocumentType.CONFIRMATION.getInt()])
			list.add(DocumentType.CONFIRMATION.getPluralString());

		if (usedDocuments[DocumentType.INVOICE.getInt()]) {
			// add payment state
			list.add(DocumentType.INVOICE.getPluralString() + "/" + getStringNOTPAYED());
			list.add(DocumentType.INVOICE.getPluralString() + "/" + getStringPAYED());
		}

		if (usedDocuments[DocumentType.DELIVERY.getInt()])
			list.add(DocumentType.DELIVERY.getPluralString());

		if (usedDocuments[DocumentType.CREDIT.getInt()]) {
			// add payment state
			list.add(DocumentType.CREDIT.getPluralString() + "/" + getStringNOTPAYED());
			list.add(DocumentType.CREDIT.getPluralString() + "/" + getStringPAYED());
		}

		if (usedDocuments[DocumentType.DUNNING.getInt()]) {
			// add payment state
			list.add(DocumentType.DUNNING.getPluralString() + "/" + getStringNOTPAYED());
			list.add(DocumentType.DUNNING.getPluralString() + "/" + getStringPAYED());
		}

		return list.toArray();
	}

	/**
	 * Get all the document items.
	 * Generate the list by the items string
	 * 
	 * @return All items of this document
	 */
	public DataSetArray<DataSetItem> getItems() {
		DataSetArray<DataSetItem> items = new DataSetArray<DataSetItem>();
		
		// Split the items string
		String itemsString = this.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");
		
		// Get all items
		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				} catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}
				items.getDatasets().add(Data.INSTANCE.getItems().getDatasetById(id));
			}
		}
		return items;
	}

	/**
	 * Recalculate the document total values
	 */
	public void calculate() {
		int sign = DocumentType.getType(this.getIntValueByKey("category")).sign();
		calculate(this.getItems(), this.getDoubleValueByKey("shipping") * sign, this.getDoubleValueByKey("shippingvat"), this
				.getStringValueByKey("shippingvatdescription"), this.getIntValueByKey("shippingautovat"), this.getDoubleValueByKey("itemsdiscount"), this
				.getBooleanValueByKey("novat"), this.getStringValueByKey("novatdescription"));
	}

	/**
	 * Recalculate the document total values
	 * 
	 * @param items Items as DataSetArray
	 * @param shippingNet Net value
	 * @param shippingVat Shipping vat
	 * @param shippingVatDescription Shipping vat name
	 * @param shippingAutoVat Way of calculating the shipping vat
	 * @param itemsDiscount Discount
	 * @param noVat True, if 0% vat is used
	 * @param noVatDescription Name of the vat, if 0% is used
	 */
	public void calculate(DataSetArray<DataSetItem> items, double shippingNet, double shippingVat, String shippingVatDescription, int shippingAutoVat,
			Double itemsDiscount, boolean noVat, String noVatDescription) {
		summary.calculate(null, items, shippingNet, shippingVat, shippingVatDescription, shippingAutoVat, itemsDiscount, noVat, noVatDescription);
	}

	/**
	 * Getter for the documents summary
	 * 
	 * @return Summary
	 */
	public DocumentSummary getSummary() {
		return this.summary;
	}

	/**
	 * Sets the state of the document to payed or unpayed
	 * Take the total value as payed value and the date of today.
	 * 
	 * @param payed
	 */
	public void setPayed(boolean payed) {
		this.setBooleanValueByKey("payed", payed);
		if (payed) {
			this.setStringValueByKey("paydate",(new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) );
			this.setDoubleValueByKey("payvalue", this.getDoubleValueByKey("total"));
		}
	}
	
	
	/**
	 * Test, if this is equal to an other UniDataSet
	 * Only web shop id and web shop date are compared
	 * 
	 * @param uds Other UniDataSet
	 * @return True, if it's equal
	 */
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("webshopid").equals(this.getStringValueByKey("webshopid")))
			return false;
		if (!uds.getStringValueByKey("webshopdate").equals(this.getStringValueByKey("webshopdate")))
			return false;
		return true;
	}

}
