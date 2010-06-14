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

package com.sebulli.fakturama.webshopimport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetContact;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DataSetPayment;
import com.sebulli.fakturama.data.DataSetProduct;
import com.sebulli.fakturama.data.DataSetShipping;
import com.sebulli.fakturama.data.DataSetVAT;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.data.UniDataSet;

public class WebShopImportManager extends Thread implements IRunnableWithProgress {

	private DocumentBuilderFactory factory = null;
	private DocumentBuilder builder = null;
	private Document document = null;
	private String importXMLContent = "";
	// List of all orders, which are out of sync with the web shop.
	private static Properties orderstosynchronize = null;
	private String runResult = "";

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		runResult = "";

		String address = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_URL");
		// TODO: remove
		// String address =
		// "http://www.sebulli.com/fakturama/xtdemoshop/admin/webshop_export.php";
		// String address =
		// "http://www.aline-gerd.de/demoshop/catalog/admin/webshop_export.php";
		String user = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_USER");
		String password = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_PASSWORD");

		if (address.contains("yourdomain.com"))
			address = "";
		if (!address.toLowerCase().startsWith("http://"))
			address = "http://" + address;

		readOrdersToSynchronize();
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		try {
			// connect to web shop
			URLConnection conn = null;
			monitor.beginTask("Connection_to_web_shop", 100);
			monitor.subTask("Connected to: " + address);
			monitor.worked(10);
			URL url = new URL(address);
			conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setConnectTimeout(4000);

			// send username , password and a list of unsynchronized orders to
			// the shop
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			monitor.worked(10);
			String postString = "username=" + user + "&password=" + password + "&action=getorders&setstate=" + orderstosynchronize.toString();
			writer.write(postString);// &getshipped="+shippedinterval+");
			writer.flush();
			String line;
			monitor.worked(10);

			// read the xml answer (the orders)
			importXMLContent = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			monitor.subTask("Loading_data");
			int iprogress;
			int worked = 30;
			double progress = worked;
			// read line by line and set the progress bar
			while (((line = reader.readLine()) != null) && (!monitor.isCanceled())) {
				System.out.println(line);
				importXMLContent += line;

				// exponential function to 100%
				progress += (100 - progress) * 0.02;
				iprogress = (int) progress;

				if (iprogress > worked) {
					monitor.worked(iprogress - worked);
					worked = iprogress;
				}
			}

			// parse the XML stream
			if (!monitor.isCanceled()) {
				ByteArrayInputStream importInputStream = new ByteArrayInputStream(importXMLContent.getBytes());
				document = builder.parse(importInputStream);

				NodeList ndList = document.getElementsByTagName("webshopexport");

				if (ndList.getLength() != 0) {
					orderstosynchronize = new Properties();
					// saveOrdersToSynchronize();
					// for (int ii = 0; ii< 100;ii++)
					// interpretWebShopData();
				} else {
					runResult = importXMLContent;
				}

				ndList = document.getElementsByTagName("error");
				if (ndList.getLength() > 0) {
					runResult = ndList.item(0).getTextContent();
				}
			}
			// cancel the download
			else {
			}

			writer.close();
			reader.close();

			monitor.done();

		} catch (SAXException e) {
			runResult = importXMLContent;
		} catch (IOException e) {
			if (address.isEmpty())
				runResult = "keine Webshop URL angegeben";
			else {
				runResult = "Fehler beim Öffnen von:\n" + address;
				if (!importXMLContent.isEmpty())
					runResult += "\n\n" + importXMLContent;
			}
		}
	}

	public String getRunResult() {
		return runResult.replaceAll("\\<.*?\\>", "");
	}

	/**
	 * Read the list of all orders, which are out of sync with the web shop from
	 * the file system
	 * 
	 */
	public static void readOrdersToSynchronize() {
		Reader reader = null;

		orderstosynchronize = new Properties();

		try {
			reader = new FileReader(Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE") + "/orders2sync.txt");
			orderstosynchronize.load(reader);
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Save the list of all orders, which are out of sync with the web shop to
	 * file system
	 * 
	 */
	public static void saveOrdersToSynchronize() {
		Writer writer = null;
		if (orderstosynchronize.isEmpty())
			return;
		try {
			writer = new FileWriter(Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE") + "/orders2sync.txt");
			orderstosynchronize.store(writer, "OrdersNotInSyncWithWebshop");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	static public void updateOrderProgress(UniDataSet uds) {
		readOrdersToSynchronize();
		int orderId = uds.getIntValueByKey("name");
		int progress = uds.getIntValueByKey("progress");
		int webshopState;
		if (progress >= 90)
			webshopState = 3;
		else if (progress >= 50)
			webshopState = 2;
		else
			webshopState = 1;
		orderstosynchronize.setProperty(Integer.toString(orderId), Integer.toString(webshopState));
		saveOrdersToSynchronize();
	}

	static public void allOrdersAreInSync() {
		orderstosynchronize = new Properties();
		File f = new File(Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE") + "/orders2sync.txt");
		f.delete();
	}

	/**
	 * Get an attribute's value and return an empty string, of the attribute is
	 * not specified
	 * 
	 * @param attributes
	 *            Attributes node
	 * @param name
	 *            Name of the attribute
	 * @return Attributes value
	 */
	private static String getAttributeAsString(NamedNodeMap attributes, String name) {
		Attr attribute;
		String value = "";
		attribute = (Attr) attributes.getNamedItem(name);
		if (attribute != null) {
			value = attribute.getValue();
		}
		return value;
	}

	/**
	 * Convert the payment method to a readable (and localized) text.
	 * 
	 * @param intext
	 *            order status
	 * @return payment method as readable (and localized) text
	 */
	public String getPaymentMethodText(String intext) {
		String paymentstatustext = intext;

		if (intext.equalsIgnoreCase("cod"))
			paymentstatustext = "Cash_on_Delivery";
		else if (intext.equalsIgnoreCase("prepayment"))
			paymentstatustext = "Prepayment";
		else if (intext.equalsIgnoreCase("creditcard"))
			paymentstatustext = "Credit_Card";
		else if (intext.equalsIgnoreCase("check"))
			paymentstatustext = "Check";

		return paymentstatustext;

	}

	public void createOrderFromXMLOrderNode(Node orderNode) {
		String firstname;
		String id;
		String genderString;
		int genderInt = 0;
		String deliveryGenderString;
		int deliveryGenderInt = 0;
		String lastname;
		String company;
		String street;
		String zip;
		String city;
		String country;
		String phone;
		String email;

		String delivery_firstname;
		String delivery_lastname;
		String delivery_company;
		String delivery_street;
		String delivery_zip;
		String delivery_city;
		String delivery_country;

		String item_quantity;
		String item_model;
		String item_name;
		String item_gross;
		String item_category;
		String item_vatpercent;
		String item_vatname;

		String order_id;
		String order_date;
		// String order_status;
		String paymentCode;
		String paymentName;
		// String currency;
		String order_total;
		Double order_totalDouble = 0.0;

		String shipping_vatpercent;
		String shipping_vatname;
		String shipping_name;
		String shipping_gross;

		String commentDate;
		String comment;
		String commentText;

		int documentId;

		NamedNodeMap attributes;

		attributes = orderNode.getAttributes();
		order_id = getAttributeAsString(attributes, "id");
		order_date = getAttributeAsString(attributes, "date");

		if (!Data.INSTANCE.getDocuments().isNew(new DataSetDocument(DocumentType.ORDER, order_id, DataUtils.DateAsISO8601String(order_date))))
			return;

		DataSetDocument dataSetDocument = Data.INSTANCE.getDocuments().addNewDataSet(new DataSetDocument(DocumentType.ORDER));
		documentId = dataSetDocument.getIntValueByKey("id");

		// order_status = getAttributeAsString(attributes,"status");
		// currency = getAttributeAsString(attributes,"currency");
		dataSetDocument.setStringValueByKey("name", order_id);
		dataSetDocument.setStringValueByKey("webshopid", order_id);
		dataSetDocument.setStringValueByKey("webshopdate", DataUtils.DateAsISO8601String(order_date));

		NodeList childnodes = orderNode.getChildNodes();
		// First get all contacts. Normally there is only one
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			if (childnode.getNodeName().equalsIgnoreCase("contact")) {
				id = getAttributeAsString(attributes, "id");
				genderString = getAttributeAsString(attributes, "gender");
				firstname = getAttributeAsString(attributes, "firstname");
				lastname = getAttributeAsString(attributes, "lastname");
				company = getAttributeAsString(attributes, "company");
				street = getAttributeAsString(attributes, "street");
				zip = getAttributeAsString(attributes, "zip");
				city = getAttributeAsString(attributes, "city");
				country = getAttributeAsString(attributes, "country");
				deliveryGenderString = getAttributeAsString(attributes, "delivery_gender");
				delivery_firstname = getAttributeAsString(attributes, "delivery_firstname");
				delivery_lastname = getAttributeAsString(attributes, "delivery_lastname");
				delivery_company = getAttributeAsString(attributes, "delivery_company");
				delivery_street = getAttributeAsString(attributes, "delivery_street");
				delivery_zip = getAttributeAsString(attributes, "delivery_zip");
				delivery_city = getAttributeAsString(attributes, "delivery_city");
				delivery_country = getAttributeAsString(attributes, "delivery_country");
				phone = getAttributeAsString(attributes, "phone");
				email = getAttributeAsString(attributes, "email");

				if (genderString.equals("m"))
					genderInt = 1;
				if (genderString.equals("f"))
					genderInt = 2;
				if (deliveryGenderString.equals("m"))
					deliveryGenderInt = 1;
				if (deliveryGenderString.equals("f"))
					deliveryGenderInt = 2;

				String shopCategory = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_CONTACT_CATEGORY");

				// use existing contact, or create new one
				DataSetContact contact = Data.INSTANCE.getContacts()
						.addNewDataSetIfNew(
								new DataSetContact(-1, false, shopCategory, genderInt, "", firstname, lastname, company, street, zip, city, country,
										deliveryGenderInt, "", delivery_firstname, delivery_lastname, delivery_company, delivery_street, delivery_zip,
										delivery_city, delivery_country, "", "", "", "", "", "", id, "", "", Data.INSTANCE.getPropertyAsInt("standardpayment"),
										0, phone, "", "", email, "", "", 0, 0.0));

				// set explicit the customers data
				contact.setIntValueByKey("gender", genderInt);
				contact.setStringValueByKey("firstname", firstname);
				contact.setStringValueByKey("name", lastname);
				contact.setStringValueByKey("company", company);
				contact.setStringValueByKey("street", street);
				contact.setStringValueByKey("zip", zip);
				contact.setStringValueByKey("city", city);
				contact.setStringValueByKey("country", country);
				contact.setIntValueByKey("delivery_gender", deliveryGenderInt);
				contact.setStringValueByKey("delivery_firstname", delivery_firstname);
				contact.setStringValueByKey("delivery_name", delivery_lastname);
				contact.setStringValueByKey("delivery_company", delivery_company);
				contact.setStringValueByKey("delivery_street", delivery_street);
				contact.setStringValueByKey("delivery_zip", delivery_zip);
				contact.setStringValueByKey("delivery_city", delivery_city);
				contact.setStringValueByKey("delivery_country", delivery_country);
				contact.setStringValueByKey("nr", id);
				Data.INSTANCE.getContacts().updateDataSet(contact);

				dataSetDocument.setIntValueByKey("addressid", contact.getIntValueByKey("id"));
				dataSetDocument.setStringValueByKey("address", contact.getAddress());
				dataSetDocument.setStringValueByKey("deliveryaddress", contact.getDeliveryAddress());
				dataSetDocument.setStringValueByKey("addressfirstline", contact.getName());

			}
		}

		// get comments
		comment = "";
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			if (childnode.getNodeName().equalsIgnoreCase("comment")) {
				commentDate = DataUtils.DateAndTimeAsLocalString(getAttributeAsString(attributes, "date"));
				commentText = childnode.getTextContent();
				if (!comment.isEmpty())
					comment += "\n";
				comment += commentDate + " :\n";
				comment += commentText + "\n";
			}
		}

		String itemString = "";
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			if (childnode.getNodeName().equalsIgnoreCase("item")) {
				item_quantity = getAttributeAsString(attributes, "quantity");
				item_model = getAttributeAsString(attributes, "model");
				item_name = getAttributeAsString(attributes, "name");
				item_category = getAttributeAsString(attributes, "category");
				item_gross = getAttributeAsString(attributes, "gross");
				item_vatpercent = getAttributeAsString(attributes, "vatpercent");
				item_vatname = getAttributeAsString(attributes, "vatname");

				Double vat_percentDouble = 0.0;
				try {
					vat_percentDouble = Double.valueOf(item_vatpercent).doubleValue() / 100;
				} catch (Exception e) {
				}

				Double priceNet = 0.0;
				try {
					priceNet = Double.valueOf(item_gross).doubleValue() / (1 + vat_percentDouble);
				} catch (Exception e) {
				}

				DataSetVAT vat = Data.INSTANCE.getVATs().addNewDataSetIfNew(new DataSetVAT(item_vatname, "", item_vatname, vat_percentDouble));
				int vatId = vat.getIntValueByKey("id");
				// TODO: was ist, wenn Produkt mit anderem Preis oder MwSt
				// bereits existiert ??
				DataSetProduct product;
				int itemNrAndNameFormat = Activator.getDefault().getPreferenceStore().getInt("WEBSHOP_ITEMNR_AND_NAME");
				String shopCategory = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_PRODUCT_CATEGORY");
				if (!shopCategory.isEmpty())
					if (!shopCategory.endsWith("/"))
						shopCategory += "/";

				if (itemNrAndNameFormat == 0) {
					Data.INSTANCE.getProducts().addNewDataSetIfNew(
							product = new DataSetProduct(item_model, item_model, shopCategory + item_category, item_name, priceNet, vatId, "", ""));
				} else {
					Data.INSTANCE.getProducts().addNewDataSetIfNew(
							product = new DataSetProduct(item_name, item_model, shopCategory + item_category, item_name, priceNet, vatId, "", ""));
				}

				DataSetItem item = Data.INSTANCE.getItems().addNewDataSet(new DataSetItem(Double.valueOf(item_quantity), product));
				item.setIntValueByKey("owner", documentId);

				Data.INSTANCE.getItems().updateDataSet(item);

				if (!itemString.isEmpty())
					itemString += ",";
				itemString += item.getStringValueByKey("id");

			}
		}

		// ...
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			if (childnode.getNodeName().equalsIgnoreCase("shipping")) {
				shipping_name = getAttributeAsString(attributes, "name");
				shipping_gross = getAttributeAsString(attributes, "gross");

				shipping_vatpercent = getAttributeAsString(attributes, "vatpercent");
				shipping_vatname = getAttributeAsString(attributes, "vatname");

				Double shippingvat_percentDouble = 0.0;
				try {
					shippingvat_percentDouble = Double.valueOf(shipping_vatpercent).doubleValue() / 100;
				} catch (Exception e) {
				}

				Double shippingGross = 0.0;
				try {
					shippingGross = Double.valueOf(shipping_gross).doubleValue();
				} catch (Exception e) {
				}

				String shopCategory = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_SHIPPING_CATEGORY");
				DataSetVAT vat = Data.INSTANCE.getVATs().addNewDataSetIfNew(new DataSetVAT(shipping_vatname, "", shipping_vatname, shippingvat_percentDouble));
				int vatId = vat.getIntValueByKey("id");
				DataSetShipping shipping = Data.INSTANCE.getShippings().addNewDataSetIfNew(
						new DataSetShipping(shipping_name, shopCategory, shipping_name, shippingGross, vatId, 1));
				dataSetDocument.setIntValueByKey("shippingid", shipping.getIntValueByKey("id"));
				dataSetDocument.setDoubleValueByKey("shipping", shippingGross);

				dataSetDocument.setStringValueByKey("shippingname", shipping_name);
				dataSetDocument.setDoubleValueByKey("shippingvat", shippingvat_percentDouble);
				dataSetDocument.setStringValueByKey("shippingvatdescription", vat.getStringValueByKey("description"));
				String s = "";
				if (order_id.length() <= 5)
					s = "00000".substring(order_id.length(), 5);
				s += order_id;
				dataSetDocument.setStringValueByKey("customerref", "Webshop Nr. " + s);
			}
		}

		// ...
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			if (childnode.getNodeName().equalsIgnoreCase("payment")) {
				order_total = getAttributeAsString(attributes, "total");
				paymentCode = getAttributeAsString(attributes, "id");
				paymentName = getAttributeAsString(attributes, "name");
				try {
					order_totalDouble = Double.valueOf(order_total).doubleValue();
				} catch (Exception e) {
				}

				DataSetPayment payment = Data.INSTANCE.getPayments().addNewDataSetIfNew(
						new DataSetPayment(paymentName, "", paymentName + " (" + paymentCode + ")",0.0, 0, 0, false));
				dataSetDocument.setIntValueByKey("paymentid", payment.getIntValueByKey("id"));

			}
		}

		dataSetDocument.setIntValueByKey("progress", 10);
		// updateOrderProgress(dataSetDocument);
		dataSetDocument.setStringValueByKey("date", DataUtils.DateAsISO8601String(order_date));
		comment = dataSetDocument.getStringValueByKey("message") + comment;
		dataSetDocument.setStringValueByKey("message", comment);

		dataSetDocument.setStringValueByKey("items", itemString);
		dataSetDocument.setDoubleValueByKey("total", order_totalDouble);
		Data.INSTANCE.getDocuments().updateDataSet(dataSetDocument);

		dataSetDocument.calculate();
		Double calcTotal = dataSetDocument.getSummary().getTotalGross().asDouble();

		// TODO: kann auch passieren, wenn bereits ein Produkt mit anderer MwSt.
		// oder Preis existiert. Wie damit umgehen ?

		if (!DataUtils.DoublesAreEqual(order_totalDouble, calcTotal)) {
			String error = "Bestellung: ";
			error += order_id + "\n";
			error += "Gesamtsumme aus Webshop:\n";
			error += DataUtils.DoubleToFormatedPriceRound(order_totalDouble) + "\n";
			error += "stimmt nicht mit berechneter Summe:\n";
			error += DataUtils.DoubleToFormatedPriceRound(calcTotal) + "\n";
			;
			error += "überein.\n\n";
			error += "Bitte prüfen !";
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR);
			messageBox.setText("Fehler beim Importieren vom Webshop");
			messageBox.setMessage(error);
			messageBox.open();

			// runResult.add(error);
		}

	}

	public void interpretWebShopData() {
		allOrdersAreInSync();
		if (document == null)
			return;

		NodeList ndList = document.getElementsByTagName("order");
		for (int orderIndex = 0; orderIndex < ndList.getLength(); orderIndex++) {
			Node order = ndList.item(orderIndex);
			createOrderFromXMLOrderNode(order);
		}
		saveOrdersToSynchronize();
	}

}
