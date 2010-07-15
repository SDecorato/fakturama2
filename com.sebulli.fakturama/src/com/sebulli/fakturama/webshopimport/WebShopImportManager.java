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
import java.io.OutputStream;
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

/**
 * Web shop import manager
 * This class provides the functionality to connect to the web shop and import
 * the data, which is transmitted as a XML File 
 * 
 * @author Gerd Bartelt
 *
 */
public class WebShopImportManager extends Thread implements IRunnableWithProgress {

	// Data model
	private DocumentBuilderFactory factory = null;
	private DocumentBuilder builder = null;
	private Document document = null;
	
	// The XML data
	private String importXMLContent = "";
	
	// List of all orders, which are out of sync with the web shop.
	private static Properties orderstosynchronize = null;
	
	// The result of this import process
	private String runResult = "";

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		runResult = "";
		
		// Get ULR, username and password from the preference store
		String address = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_URL");
		String user = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_USER");
		String password = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_PASSWORD");

		
		// remove the default setting "yourdomain.com"
		if (address.contains("yourdomain.com")) {
			runResult = "Keine gültige Webshop Adresse.";
			return;
		}
		
		// Add "http://"
		if (!address.toLowerCase().startsWith("http://"))
			address = "http://" + address;

		// Get the open order IDs that are out of sync with the webshop
		// from the file system
		readOrdersToSynchronize();
		
		// Create a new document builder
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		try {
			
			// Connect to web shop
			URLConnection conn = null;
			monitor.beginTask("Connection_to_web_shop", 100);
			monitor.subTask("Connected to: " + address);
			monitor.worked(10);
			URL url = new URL(address);
			conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setConnectTimeout(4000);

			// Send username , password and a list of unsynchronized orders to
			// the shop
			OutputStream outputStream = null;
			outputStream = conn.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
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

				// Clear the list of orders to sync, if the data was sent
				if (ndList.getLength() != 0) {
					orderstosynchronize = new Properties();
				} else {
					runResult = importXMLContent;
				}

				// Get the error elements and add them to the run result list
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
		} catch (Exception e) {
			if (address.isEmpty())
				runResult = "keine Webshop URL angegeben";
			else {
				runResult = "Fehler beim Öffnen von:\n" + address;
				if (!importXMLContent.isEmpty())
					runResult += "\n\n" + importXMLContent;
			}
		}
	}

	/**
	 * Remove the HTML tags from the result
	 * 
	 * @return The formated run result string
	 */
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

	/**
	 * Update the progress of an order
	 * 
	 * @param uds The UniDataSet with the new progress value
	 */
	static public void updateOrderProgress(UniDataSet uds) {

		// Get the orders that are out of sync with the shop
		readOrdersToSynchronize();
		
		// Get the progress value of the UniDataSet
		int orderId = uds.getIntValueByKey("name");
		int progress = uds.getIntValueByKey("progress");
		int webshopState;
		
		// Convert a percent value of 0..100% to a state of 1,2,3
		if (progress >= 90)
			webshopState = 3;
		else if (progress >= 50)
			webshopState = 2;
		else
			webshopState = 1;
		
		// Set the new progress state 
		orderstosynchronize.setProperty(Integer.toString(orderId), Integer.toString(webshopState));
		saveOrdersToSynchronize();
	}

	/**
	 * Mark all orders as "in sync" with the web shop
	 */
	static public void allOrdersAreInSync() {
		orderstosynchronize = new Properties();
		File f = new File(Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE") + "/orders2sync.txt");
		f.delete();
	}

	/**
	 * Get an attribute's value and return an empty string, of the attribute is
	 * not specified
	 * 
	 * @param attributes  Attributes node
	 * @param name  Name of the attribute
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
	 * @param intext  order status
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
	
	/**
	 * Parse an XML node and create a new product for each product entry
	 * 
	 * @param productNode The node with the products to import
	 */
	public void createProductFromXMLOrderNode(Node productNode) {
		
		// Temporary variables to store the products data which will be imported
		String productModel;
		String productName;
		String productCategory;
		String productNet;
		String productGross;
		String productVatPercent;
		String productVatName;
		String productDescription;
		
		// Get the attributes ID and date of this order
		NamedNodeMap attributes = productNode.getAttributes();
		productModel 		= getAttributeAsString(attributes, "model");
		productName 		= getAttributeAsString(attributes, "name");
		productCategory 	= getAttributeAsString(attributes, "category");
		productNet 			= getAttributeAsString(attributes, "net");
		productGross 			= getAttributeAsString(attributes, "gross");
		productVatPercent 	= getAttributeAsString(attributes, "vatpercent");
		productVatName		= getAttributeAsString(attributes, "vatname");

		// Get the product description as plain text.
		productDescription = "";
		for (int index = 0; index < productNode.getChildNodes().getLength(); index++) {
			Node productChild = productNode.getChildNodes().item(index);
			if (productChild.getNodeName().equals("short_description"))
				productDescription += productChild.getTextContent();
		}
		
		// Convert VAT percent value to a factor (100% -> 1.00)
		Double vatPercentDouble = 0.0;
		try {
			vatPercentDouble = Double.valueOf(productVatPercent).doubleValue() / 100;
		} catch (Exception e) {
		}

		// Convert the gross or net string to a double value
		Double priceNet = 0.0;
		try {

			// Use the net string, if it is set
			if (!productNet.isEmpty()) {
				priceNet = Double.valueOf(productNet).doubleValue();
			}
			
			// Use the gross string, if it is set
			if (!productGross.isEmpty()) {
				priceNet = Double.valueOf(productGross).doubleValue() /  (1 + vatPercentDouble);
			}
			
		} catch (Exception e) {
		}

		
		// Add the VAT value to the data base, if it is a new one 
		DataSetVAT vat = Data.INSTANCE.getVATs().addNewDataSetIfNew( new DataSetVAT(productVatName, "", productVatName, vatPercentDouble) );
		int vatId = vat.getIntValueByKey("id");
		
		// Import the item as a new product
		DataSetProduct product;
		
		
		// Get the category of the imported products from the preferences
		String shopCategory = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_PRODUCT_CATEGORY");
		
		// If the category is not set, use the shop category
		if (!shopCategory.isEmpty())
			if (!shopCategory.endsWith("/"))
				shopCategory += "/";

		// Use product name as product model, if model is empty
		if (productModel.isEmpty() && !productName.isEmpty())
			productModel = productName;

		// Use product model as product name, if name is empty
		if (productName.isEmpty() && !productModel.isEmpty())
			productName = productModel;
		
		
		// Create a new product object
		product = new DataSetProduct(productName, productModel, shopCategory + productCategory, productDescription, priceNet, vatId, "", "");

		// Add a new product to the data base, if it's not existing yet
		if (Data.INSTANCE.getProducts().isNew(product)) {
			Data.INSTANCE.getProducts().addNewDataSet(product);
		}
		else {
			// Update data
			DataSetProduct existingProduct = Data.INSTANCE.getProducts().getExistingDataSet(product);
			existingProduct.setStringValueByKey("category", product.getStringValueByKey("category"));
			existingProduct.setStringValueByKey("description", product.getStringValueByKey("description"));
			existingProduct.setDoubleValueByKey("price1", product.getDoubleValueByKey("price1"));
			existingProduct.setIntValueByKey("vatid", product.getIntValueByKey("vatid"));

			// Update the modified product data
			Data.INSTANCE.getProducts().updateDataSet(existingProduct);
		}
			
	}
	
	
	/**
	 * Parse an XML node and create a new order for each order entry
	 * 
	 * @param orderNode The node with the orders to import
	 */
	public void createOrderFromXMLOrderNode(Node orderNode) {

		// Temporary variables to store the contact data which will be imported
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

		// The delivery data
		String delivery_firstname;
		String delivery_lastname;
		String delivery_company;
		String delivery_street;
		String delivery_zip;
		String delivery_city;
		String delivery_country;

		// Item data
		String itemQuantity;
		String itemDescription;
		String itemModel;
		String itemName;
		String itemGross;
		String itemCategory;
		String itemVatpercent;
		String itemVatname;
		

		// Order data
		String order_id;
		String order_date;
		// String order_status;
		String paymentCode;
		String paymentName;
		// String currency;
		String order_total;
		Double order_totalDouble = 0.0;

		// Shipping data
		String shipping_vatpercent;
		String shipping_vatname;
		String shipping_name;
		String shipping_gross;

		// Comments
		String commentDate;
		String comment;
		String commentText;

		// The document id
		int documentId;

		// Get the attributes ID and date of this order
		NamedNodeMap attributes = orderNode.getAttributes();
		order_id = getAttributeAsString(attributes, "id");
		order_date = getAttributeAsString(attributes, "date");

		// Check, if this order is still existing
		if (!Data.INSTANCE.getDocuments().isNew(new DataSetDocument(DocumentType.ORDER, order_id, DataUtils.DateAsISO8601String(order_date))))
			return;

		// Create a new order
		DataSetDocument dataSetDocument = Data.INSTANCE.getDocuments().addNewDataSet(new DataSetDocument(DocumentType.ORDER));
		documentId = dataSetDocument.getIntValueByKey("id");

		// Set name, webshop order id and date
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

			// Get the contact data
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

				// Convert a gender character "m" or "f" to the gender number 
				// 1 or 2
				if (genderString.equals("m"))
					genderInt = 1;
				if (genderString.equals("f"))
					genderInt = 2;
				if (deliveryGenderString.equals("m"))
					deliveryGenderInt = 1;
				if (deliveryGenderString.equals("f"))
					deliveryGenderInt = 2;

				// Get the category for new contacts from the preferences
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

		// Get the comments
		comment = "";
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			// Get the comment text
			if (childnode.getNodeName().equalsIgnoreCase("comment")) {
				commentDate = DataUtils.DateAndTimeAsLocalString(getAttributeAsString(attributes, "date"));
				commentText = childnode.getTextContent();
				if (!comment.isEmpty())
					comment += "\n";

				// Add the date
				comment += commentDate + " :\n";
				comment += commentText + "\n";
			}
		}

		// Get all the items of this order
		String itemString = "";
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			// Get the item data
			if (childnode.getNodeName().equalsIgnoreCase("item")) {
				itemQuantity = getAttributeAsString(attributes, "quantity");
				itemModel = getAttributeAsString(attributes, "model");
				itemName = getAttributeAsString(attributes, "name");
				itemCategory = getAttributeAsString(attributes, "category");
				itemGross = getAttributeAsString(attributes, "gross");
				itemVatpercent = getAttributeAsString(attributes, "vatpercent");
				itemVatname = getAttributeAsString(attributes, "vatname");

				// Convert VAT percent value to a factor (100% -> 1.00)
				Double vat_percentDouble = 0.0;
				try {
					vat_percentDouble = Double.valueOf(itemVatpercent).doubleValue() / 100;
				} catch (Exception e) {
				}

				// Calculate the net value of the price
				Double priceNet = 0.0;
				try {
					priceNet = Double.valueOf(itemGross).doubleValue() / (1 + vat_percentDouble);
				} catch (Exception e) {
				}

				// Add the VAT value to the data base, if it is a new one
				DataSetVAT vat = Data.INSTANCE.getVATs().addNewDataSetIfNew( new DataSetVAT(itemVatname, "", itemVatname, vat_percentDouble) );
				int vatId = vat.getIntValueByKey("id");
				
				// Import the item as a new product
				DataSetProduct product;
				
				// Get the category of the imported products from the preferences
				String shopCategory = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_PRODUCT_CATEGORY");
				
				// If the category is not set, use the shop category
				if (!shopCategory.isEmpty())
					if (!shopCategory.endsWith("/"))
						shopCategory += "/";

				// Use item name as item model, if model is empty
				if (itemModel.isEmpty() && !itemName.isEmpty())
					itemModel = itemName;

				// Use item model as item name, if name is empty
				if (itemName.isEmpty() && !itemModel.isEmpty())
					itemName = itemModel;
				
				
				itemDescription = "";
				for (int index = 0; index < childnode.getChildNodes().getLength(); index++) {
					Node itemChild = childnode.getChildNodes().item(index);
					if (itemChild.getNodeName().equals("attribute")) {
						attributes = itemChild.getAttributes();
						if (!itemDescription.isEmpty())
							itemDescription += ", ";
						itemDescription += getAttributeAsString(attributes, "option") + ": ";
						itemDescription += getAttributeAsString(attributes, "value");
						System.out.println(itemName+"-"+itemDescription);
					}
				}

				
				
				// Create a new product
				product = new DataSetProduct(itemName, itemModel, shopCategory + itemCategory, itemDescription, priceNet, vatId, "", "");
				
				// Add the new product to the data base, if it's not existing yet
				Data.INSTANCE.getProducts().addNewDataSetIfNew(product);

				// Add this product to the list of items
				DataSetItem item = Data.INSTANCE.getItems().addNewDataSet(new DataSetItem(Double.valueOf(itemQuantity), product));
				item.setIntValueByKey("owner", documentId);

				// Update the modified item data
				Data.INSTANCE.getItems().updateDataSet(item);

				// Add the item ID to the list of items in the order document
				if (!itemString.isEmpty())
					itemString += ",";
				itemString += item.getStringValueByKey("id");

			}
		}

		// Get the shipping(s)
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			// Import the shipping data
			if (childnode.getNodeName().equalsIgnoreCase("shipping")) {
				shipping_name = getAttributeAsString(attributes, "name");
				shipping_gross = getAttributeAsString(attributes, "gross");

				shipping_vatpercent = getAttributeAsString(attributes, "vatpercent");
				shipping_vatname = getAttributeAsString(attributes, "vatname");

				// Get the VAT value as double
				Double shippingvat_percentDouble = 0.0;
				try {
					shippingvat_percentDouble = Double.valueOf(shipping_vatpercent).doubleValue() / 100;
				} catch (Exception e) {
				}

				// Get the shipping gross value
				Double shippingGross = 0.0;
				try {
					shippingGross = Double.valueOf(shipping_gross).doubleValue();
				} catch (Exception e) {
				}

				// Get the category of the imported shippings from the preferences
				String shopCategory = Activator.getDefault().getPreferenceStore().getString("WEBSHOP_SHIPPING_CATEGORY");
				
				// Add the VAT entry to the data base, if there is not yet one
				// with the same values
				DataSetVAT vat = Data.INSTANCE.getVATs().addNewDataSetIfNew( new DataSetVAT(shipping_vatname, "", shipping_vatname, shippingvat_percentDouble) );
				int vatId = vat.getIntValueByKey("id");
				
				// Add the shipping to the data base, if it's a new shipping
				DataSetShipping shipping = Data.INSTANCE.getShippings().addNewDataSetIfNew(
						new DataSetShipping(shipping_name, shopCategory, shipping_name, shippingGross, vatId, 1));
				
				// Set the document entries for the shipping
				dataSetDocument.setIntValueByKey("shippingid", shipping.getIntValueByKey("id"));
				dataSetDocument.setDoubleValueByKey("shipping", shippingGross);
				dataSetDocument.setStringValueByKey("shippingname", shipping_name);
				dataSetDocument.setDoubleValueByKey("shippingvat", shippingvat_percentDouble);
				dataSetDocument.setStringValueByKey("shippingvatdescription", vat.getStringValueByKey("description"));
				String s = "";

				// Use the order ID of the web shop as customer reference for
				// importes web shop orders
				if (order_id.length() <= 5)
					s = "00000".substring(order_id.length(), 5);
				s += order_id;
				dataSetDocument.setStringValueByKey("customerref", "Webshop Nr. " + s);
			}
		}

		// Get the payment (s)
		for (int childnodeIndex = 0; childnodeIndex < childnodes.getLength(); childnodeIndex++) {
			Node childnode = childnodes.item(childnodeIndex);
			attributes = childnode.getAttributes();

			// Get the payment data
			if (childnode.getNodeName().equalsIgnoreCase("payment")) {
				order_total = getAttributeAsString(attributes, "total");
				paymentCode = getAttributeAsString(attributes, "id");
				paymentName = getAttributeAsString(attributes, "name");

				// Get the value of the payment
				try {
					order_totalDouble = Double.valueOf(order_total).doubleValue();
				} catch (Exception e) {
				}

				// TODO: Get the unpayed text from the web shop
				// Add the payment to the data base, if it's a new one
				DataSetPayment payment = Data.INSTANCE.getPayments().addNewDataSetIfNew(
						new DataSetPayment(paymentName, "", paymentName + " (" + paymentCode + ")",0.0, 0, 0,"Zahlung dankend erhalten.","", false));
				dataSetDocument.setIntValueByKey("paymentid", payment.getIntValueByKey("id"));

			}
		}

		// Set the progess of an imported order to 10%
		dataSetDocument.setIntValueByKey("progress", 10);
		
		// Set the document data
		dataSetDocument.setStringValueByKey("date", DataUtils.DateAsISO8601String(order_date));
		comment = dataSetDocument.getStringValueByKey("message") + comment;
		dataSetDocument.setStringValueByKey("message", comment);

		dataSetDocument.setStringValueByKey("items", itemString);
		dataSetDocument.setDoubleValueByKey("total", order_totalDouble);
		
		// Update the data base with the new document data
		Data.INSTANCE.getDocuments().updateDataSet(dataSetDocument);

		// Re-calculate the document's total sum and check it.
		// It must be the same total value as in the web shop
		dataSetDocument.calculate();
		Double calcTotal = dataSetDocument.getSummary().getTotalGross().asDouble();

		// TODO: There will be a difference, if there is an still a product in
		// the data base with the same name but a different price or VAT value.  

		// If there is a difference, show a warning.
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
		}

	}

	/**
	 * Interpret the complete node of all orders and import them
	 */
	public void interpretWebShopData() {
		
		// Mark all orders as "in synch with the webshop"
		allOrdersAreInSync();
		
		// There is no order
		if (document == null)
			return;

		NodeList ndList;
		
		// Get all products and import them
		ndList = document.getElementsByTagName("product");
		for (int productIndex = 0; productIndex < ndList.getLength(); productIndex++) {
			Node product = ndList.item(productIndex);
			createProductFromXMLOrderNode(product);
		}

		// Get order by order and import it
		ndList = document.getElementsByTagName("order");
		for (int orderIndex = 0; orderIndex < ndList.getLength(); orderIndex++) {
			Node order = ndList.item(orderIndex);
			createOrderFromXMLOrderNode(order);
		}
		
		// Save the new list of orders that are not in synch with the shop
		saveOrdersToSynchronize();
	}

}
