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

package com.sebulli.fakturama.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;

public class Placeholders {
	
	// all placeholders
	private static String placeholders[] = {
			"YOURCOMPANY.COMPANY",
			"YOURCOMPANY.OWNER",
			"YOURCOMPANY.OWNER.FIRSTNAME",
			"YOURCOMPANY.OWNER.LASTNAME",
			"YOURCOMPANY.STREET",
			"YOURCOMPANY.STREET.NAME",
			"YOURCOMPANY.STREET.NO",
			"YOURCOMPANY.STREET.ZIP",
			"YOURCOMPANY.STREET.CITY",
			"YOURCOMPANY.STREET.COUNTRY",
			"YOURCOMPANY.STREET.EMAIL",
			"DOCUMENT.DATE",
			"DOCUMENT.ADDRESSES.EQUAL",
			"DOCUMENT.ADDRESS",
			"DOCUMENT.DELIVERYADDRESS",
			"DOCUMENT.ADDRESS.INONELINE",
			"DOCUMENT.DELIVERYADDRESS.INONELINE",
			"DOCUMENT.DIFFERENT.ADDRESS",
			"DOCUMENT.DIFFERENT.DELIVERYADDRESS",
			"DOCUMENT.DIFFERENT.ADDRESS.INONELINE",
			"DOCUMENT.DIFFERENT.DELIVERYADDRESS.INONELINE",
			"DOCUMENT.TYPE",
			"DOCUMENT.NAME",
			"DOCUMENT.CUSTOMERREF",
			"DOCUMENT.SERVICEDATE",
			"DOCUMENT.MESSAGE",
			"DOCUMENT.MESSAGE1",
			"DOCUMENT.MESSAGE2",
			"DOCUMENT.MESSAGE3",
			"DOCUMENT.TRANSACTION",
			"DOCUMENT.INVOICE",
			"DOCUMENT.WEBSHOP.ID",
			"DOCUMENT.WEBSHOP.DATE",
			"DOCUMENT.ORDER.DATE",
			"DOCUMENT.ITEMS.GROSS",
			"DOCUMENT.ITEMS.NET",
			"DOCUMENT.TOTAL.VAT",
			"DOCUMENT.TOTAL.GROSS",
			"ITEMS.DISCOUNT.PERCENT",
			"ITEMS.DISCOUNT.NET",
			"ITEMS.DISCOUNT.GROSS",
			"SHIPPING.NET",
			"SHIPPING.VAT",
			"SHIPPING.GROSS",
			"SHIPPING.DESCRIPTION",
			"SHIPPING.VAT.DESCRIPTION",
			"DOCUMENT.DUNNING.LEVEL",
			"PAYMENT.TEXT",
			"DOCUMENT.DUNNING.LEVEL",
			"PAYMENT.DESCRIPTION",
			"PAYMENT.PAID.VALUE",
			"PAYMENT.PAID.DATE",
			"PAYMENT.DUE.DAYS",
			"PAYMENT.PAID",
			"ADDRESS.FIRSTLINE",
			"ADDRESS",
			"ADDRESS.GENDER",
			"ADDRESS.GREETING",
			"ADDRESS.TITLE",
			"ADDRESS.NAME",
			"ADDRESS.FIRSTNAME",
			"ADDRESS.LASTNAME",
			"ADDRESS.COMPANY",
			"ADDRESS.STREET",
			"ADDRESS.STREETNAME",
			"ADDRESS.STREETNO",
			"ADDRESS.ZIP",
			"ADDRESS.CITY",
			"ADDRESS.COUNTRY",
			"DELIVERY.ADDRESS.FIRSTLINE",
			"DELIVERY.ADDRESS",
			"DELIVERY.ADDRESS.GENDER",
			"DELIVERY.ADDRESS.GREETING",
			"DELIVERY.ADDRESS.TITLE",
			"DELIVERY.ADDRESS.NAME",
			"DELIVERY.ADDRESS.FIRSTNAME",
			"DELIVERY.ADDRESS.LASTNAME",
			"DELIVERY.ADDRESS.COMPANY",
			"DELIVERY.ADDRESS.STREET",
			"DELIVERY.ADDRESS.STREETNAME",
			"DELIVERY.ADDRESS.STREETNO",
			"DELIVERY.ADDRESS.ZIP",
			"DELIVERY.ADDRESS.CITY",
			"DELIVERY.ADDRESS.COUNTRY",
			"ADDRESS.BANK.ACCOUNT.HOLDER",
			"ADDRESS.BANK.ACCOUNT",
			"ADDRESS.BANK.CODE",
			"ADDRESS.BANK.NAME",
			"ADDRESS.BANK.IBAN",
			"ADDRESS.BANK.BIC",
			"ADDRESS.NR",
			"ADDRESS.PHONE",
			"ADDRESS.FAX",
			"ADDRESS.MOBILE",
			"ADDRESS.EMAIL",
			"ADDRESS.WEBSITE",
			"ADDRESS.VATNR",
			"ADDRESS.NOTE",
			"ADDRESS.DISCOUNT"			
	};

	
	/**
	 * Returns the first name of a complete name
	 * 
	 * @param name
	 * 		First name and last name
	 * @return
	 * 		Only the first name
	 */
	public static String getFirstName (String name) {
		String s = name.trim();
		int lastSpace = s.lastIndexOf(" ");
		if (lastSpace > 0)
			return s.substring(0, lastSpace).trim();
		else
			return "";
	}
	
	/**
	 * Returns the last name of a complete name
	 * 
	 * @param name
	 * 		First name and last name
	 * @return
	 * 		Only the last name
	 */
	public static String getLastName (String name) {
		String s = name.trim();
		int lastSpace = s.lastIndexOf(" ");
		if (lastSpace > 0)
			return s.substring(lastSpace + 1).trim();
		else
			return "";
	}
	
	/**
	 * Returns the street name without the number
	 * 
	 * @param streetWithNo
	 * 		
	 * @return
	 * 		Only the street name
	 */
	public static String getStreetName (String streetWithNo) {
		String s = streetWithNo.trim();
		int indexNo = 0;
		
		// Search for the number
		Matcher matcher = Pattern.compile( "\\d+" ).matcher( s );
		if ( matcher.find() ) {
			indexNo = matcher.start();
		}
		
		// Extract the Number
		if (indexNo > 0)
			return s.substring(0, indexNo).trim();
		else
			return "";
	}

	/**
	 * Returns the street number without the name
	 * 
	 * @param streetWithNo
	 * 		
	 * @return
	 * 		Only the street No
	 */
	public static String getStreetNo (String streetWithNo) {
		String s = streetWithNo.trim();
		int indexNo = 0;
		
		// Search for the number
		Matcher matcher = Pattern.compile( "\\d+" ).matcher( s );
		if ( matcher.find() ) {
			indexNo = matcher.start();
		}
		
		// Extract the Number
		if (indexNo > 0)
			return s.substring(indexNo).trim();
		else
			return "";
	}
	

	
	static public String getDataFromAddressField(String address, String key) {
		
		String addressName = "";
		String addressFirstName = "";
		String addressLastName = "";
		String addressLine = "";
		String addressStreet = "";
		String addressZIP = "";
		String addressCity = "";
		String addressCountry = "";

		
		String[] addressLines = address.split("\\n");
		
		Boolean countryFound = false;
		Boolean cityFound = false;
		Boolean streetFound = false;
		String line = "";
		addressLine = "";
		
		// The first line is the name
		addressName = addressLines[0];
		addressFirstName = getFirstName(addressName);
		addressLastName = getLastName(addressName);
		
		// Analyze all the other lines. Start with the last
		for (int lineNr = addressLines.length -1; lineNr >= 1;lineNr--) {
			
			// Get one line
			line = addressLines[lineNr].trim();
			
			// Use only non-empty lines
			if (!line.isEmpty()) {
				
				if (!countryFound || !cityFound) {
					Matcher matcher = Pattern.compile( "\\d+" ).matcher( line );
					
					// A Number was found. So this line was not the country, it must be the ZIP code
					if ( matcher.find() ) {
						if (matcher.start() < 4)  {
							int codelen = matcher.end() - matcher.start();
							
							// Extract the ZIP code
							if (codelen >= 4 && codelen <=5 ) {
								addressZIP = matcher.group();

								// and the city
								addressCity = line.substring(matcher.end()+1).trim();
								
							}
							cityFound = true;
							countryFound = true;
						}
					}
					else {
						// It must be the country
						addressCountry =  line;
						countryFound = true;
					}
				}
				// City and maybe country were found. Search now for the street.
				else if (!streetFound){
					Matcher matcher = Pattern.compile( "\\d+" ).matcher( line );
					
					// A Number was found. This must be the street number
					if ( matcher.find() ) {
						if (matcher.start() > 3)  {
							// Extract the street number
							addressStreet  = line;
							streetFound = true;
						}
					}
				}
				// Street, city and maybe country were found. 
				// Search now for additional address information
				else {
					if (!addressLine.isEmpty())
						addressLine +=" ";
					addressLine = line;
				}
				
			}
		}

		if (key.equals("name")) return addressName;
		if (key.equals("firstname")) return addressFirstName;
		if (key.equals("lastname")) return addressLastName;
		if (key.equals("addressfirstline")) return addressLine;
		if (key.equals("street")) return addressStreet;
		if (key.equals("streetname")) return getStreetName(addressStreet);
		if (key.equals("streetno")) return getStreetNo(addressStreet);
		if (key.equals("zip")) return addressZIP;
		if (key.equals("city")) return addressCity;
		if (key.equals("county")) return addressCountry;
		return "";
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
	private static String convertCRLF2LF(String s){
		s = s.replaceAll("\\r\\n", "\n");
		return s;
	}

	
	/**
	 * Replaces all line breaks by a "-"
	 * 
	 * @param s
	 * 	The string in multiple lines
	 * @return
	 * 	The string in one line, seperated by a "-"
	 */
	private static String StringInOneLine(String s) {
		// Convert CRLF to LF 
		s = convertCRLF2LF(s).trim();
		// Replace line feeds by a " - "
		s = s.replaceAll("\\n", " - ");
		return s;
	}

	
	public static String getDocumentInfo(DataSetDocument document, String key) {

		
		if (key.startsWith("YOURCOMPANY")) {
			if (key.equals("YOURCOMPANY.COMPANY")) return  Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_NAME");

			String owner = Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_OWNER");
			if (key.equals("YOURCOMPANY.OWNER")) return  owner;
			if (key.equals("YOURCOMPANY.OWNER.FIRSTNAME")) return  Placeholders.getFirstName(owner);
			if (key.equals("YOURCOMPANY.OWNER.LASTNAME")) return  Placeholders.getLastName(owner);

			String streetWithNo = Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_STREET");
			if (key.equals("YOURCOMPANY.STREET")) return  streetWithNo;
			if (key.equals("YOURCOMPANY.STREET.NAME")) return  Placeholders.getStreetName(streetWithNo);
			if (key.equals("YOURCOMPANY.STREET.NO")) return  Placeholders.getStreetNo(streetWithNo);

			if (key.equals("YOURCOMPANY.STREET.ZIP")) return  Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_ZIP");
			if (key.equals("YOURCOMPANY.STREET.CITY")) return  Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_CITY");
			if (key.equals("YOURCOMPANY.STREET.COUNTRY")) return  Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_COUNTRY");
			if (key.equals("YOURCOMPANY.STREET.EMAIL")) return  Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_EMAIL");
		}

		
		
		if (document == null)
			return null;
		
		DataSetContact contact;
		
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


		
		if (key.equals("DOCUMENT.DATE")) return document.getFormatedStringValueByKey("date");
		if (key.equals("DOCUMENT.ADDRESSES.EQUAL")) return ((Boolean)document.deliveryAddressEqualsBillingAddress()).toString();

		// Get address and delivery address
		// with option "INONELINE" and without
		// with option "DIFFERENT" and without
		String deliverystring;
		String inonelinestring;
		String differentstring;
		// address and delivery address
		for (int i = 0;i<2 ; i++) {
			if (i==1)
				deliverystring = "delivery";
			else
				deliverystring = "";
			
			// with option "INONELINE" and without
			for (int i2 = 0; i2<2; i2++) {
				if (i2==1)
					inonelinestring = ".INONELINE";
				else
					inonelinestring = "";
				String s = document.getStringValueByKey(deliverystring + "address");
				if (i2==1)
					s = StringInOneLine(s);
				
				//  with option "DIFFERENT" and without
				for (int i3 = 0 ; i3<2; i3++) {
					if (i3==1)
						differentstring = ".DIFFERENT";
					else
						differentstring = "";
					if (i3==1) {
						if (document.deliveryAddressEqualsBillingAddress())
							s="";
					}
					if (key.equals("DOCUMENT" + differentstring +"."+ deliverystring.toUpperCase()+ "ADDRESS" + inonelinestring)) return s;
					
				}
			}
		}
		
		if (key.equals("DOCUMENT.TYPE")) return DocumentType.getString(document.getIntValueByKey("category"));
		if (key.equals("DOCUMENT.NAME")) return document.getStringValueByKey("name");
		if (key.equals("DOCUMENT.CUSTOMERREF")) return document.getStringValueByKey("customerref");
		if (key.equals("DOCUMENT.SERVICEDATE")) return document.getFormatedStringValueByKey("servicedate");
		if (key.equals("DOCUMENT.MESSAGE")) return document.getStringValueByKey("message");
		if (key.equals("DOCUMENT.MESSAGE1")) return document.getStringValueByKey("message");
		if (key.equals("DOCUMENT.MESSAGE2")) return document.getStringValueByKey("message2");
		if (key.equals("DOCUMENT.MESSAGE3")) return document.getStringValueByKey("message3");
		if (key.equals("DOCUMENT.TRANSACTION")) return document.getStringValueByKey("transaction");
		if (key.equals("DOCUMENT.INVOICE")) return document.getStringValueByKeyFromOtherTable("invoiceid.DOCUMENTS:name");
		if (key.equals("DOCUMENT.WEBSHOP.ID")) return document.getStringValueByKey("webshopid");
		if (key.equals("DOCUMENT.WEBSHOP.DATE")) return document.getFormatedStringValueByKey("webshopdate");
		if (key.equals("DOCUMENT.ORDER.DATE")) return document.getFormatedStringValueByKey("orderdate");
		if (key.equals("DOCUMENT.ITEMS.GROSS")) return document.getSummary().getItemsGross().asFormatedRoundedString();
		if (key.equals("DOCUMENT.ITEMS.NET")) return document.getSummary().getItemsNet().asFormatedRoundedString();
		if (key.equals("DOCUMENT.TOTAL.VAT")) return document.getSummary().getTotalVat().asFormatedRoundedString();
		if (key.equals("DOCUMENT.TOTAL.GROSS")) return document.getSummary().getTotalGross().asFormatedString();
		if (key.equals("ITEMS.DISCOUNT.PERCENT")) return document.getFormatedStringValueByKey("itemsdiscount");
		if (key.equals("ITEMS.DISCOUNT.NET")) return document.getSummary().getDiscountNet().asFormatedRoundedString();
		if (key.equals("ITEMS.DISCOUNT.GROSS")) return document.getSummary().getDiscountGross().asFormatedRoundedString();
		if (key.equals("SHIPPING.NET")) return document.getSummary().getShippingNet().asFormatedString();
		if (key.equals("SHIPPING.VAT")) return document.getSummary().getShippingVat().asFormatedString();
		if (key.equals("SHIPPING.GROSS")) return document.getSummary().getShippingGross().asFormatedString();
//		if (key.equals("SHIPPING.NAME")) return document.getStringValueByKey("shippingname");
		if (key.equals("SHIPPING.DESCRIPTION")) return document.getStringValueByKey("shippingdescription");
		if (key.equals("SHIPPING.VAT.DESCRIPTION")) return document.getStringValueByKey("shippingvatdescription");
		if (key.equals("DOCUMENT.DUNNING.LEVEL")) return document.getStringValueByKey("dunninglevel");

		if (key.equals("PAYMENT.TEXT")) {
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

		}
		
		if (key.equals("DOCUMENT.DUNNING.LEVEL")) return document.getStringValueByKey("dunninglevel");

		//setProperty("PAYMENT.NAME", document.getStringValueByKey("paymentname"));
		if (key.equals("PAYMENT.DESCRIPTION")) return document.getStringValueByKey("paymentdescription");
		if (key.equals("PAYMENT.PAID.VALUE")) return DataUtils.DoubleToFormatedPriceRound(document.getDoubleValueByKey("payvalue"));
		if (key.equals("PAYMENT.PAID.DATE")) return document.getFormatedStringValueByKey("paydate");
		if (key.equals("PAYMENT.DUE.DAYS")) return Integer.toString(document.getIntValueByKey("duedays"));
		if (key.equals("PAYMENT.DUE.DATE")) return
				DataUtils.DateAsLocalString(DataUtils.AddToDate(document.getStringValueByKey("date"), document.getIntValueByKey("duedays")));
		if (key.equals("PAYMENT.PAID")) return document.getStringValueByKey("paid");

		
		String key2;
		String addressField;
		
		if (key.startsWith("DELIVERY.")) {
			key2 = key.substring(9);
			addressField = document.getStringValueByKey("deliveryaddress");
		}
		else {
			key2 = key;
			addressField = document.getStringValueByKey("address");
		}

		if (key2.equals("ADDRESS.FIRSTLINE")) return getDataFromAddressField(addressField,"addressfirstline");
		
		if (contact != null) {
			if (key.equals("ADDRESS")) return contact.getAddress(false);
			if (key.equals("ADDRESS.GENDER")) return contact.getGenderString(false);
			if (key.equals("ADDRESS.GREETING")) return contact.getGreeting(false);
			if (key.equals("ADDRESS.TITLE")) return contact.getStringValueByKey("title");
			if (key.equals("ADDRESS.NAME")) return contact.getStringValueByKey("name");
			if (key.equals("ADDRESS.FIRSTNAME")) return contact.getStringValueByKey("firstname");
			if (key.equals("ADDRESS.LASTNAME")) return contact.getStringValueByKey("name");
			if (key.equals("ADDRESS.COMPANY")) return contact.getStringValueByKey("company");
			if (key.equals("ADDRESS.STREET")) return contact.getStringValueByKey("street");
			if (key.equals("ADDRESS.STREETNAME")) return getStreetName(contact.getStringValueByKey("street"));
			if (key.equals("ADDRESS.STREETNO")) return getStreetNo(contact.getStringValueByKey("street"));
			if (key.equals("ADDRESS.ZIP")) return contact.getStringValueByKey("zip");
			if (key.equals("ADDRESS.CITY")) return contact.getStringValueByKey("city");
			if (key.equals("ADDRESS.COUNTRY")) return contact.getStringValueByKey("country");
			if (key.equals("DELIVERY.ADDRESS")) return contact.getAddress(true);
			if (key.equals("DELIVERY.ADDRESS.GENDER")) return contact.getGenderString(true);
			if (key.equals("DELIVERY.ADDRESS.GREETING")) return contact.getGreeting(true);
			if (key.equals("DELIVERY.ADDRESS.TITLE")) return contact.getStringValueByKey("delivery_title");
			if (key.equals("DELIVERY.ADDRESS.NAME")) return contact.getStringValueByKey("delivery_name");
			if (key.equals("DELIVERY.ADDRESS.FIRSTNAME")) return contact.getStringValueByKey("delivery_firstname");
			if (key.equals("DELIVERY.ADDRESS.LASTNAME")) return contact.getStringValueByKey("delivery_name");
			if (key.equals("DELIVERY.ADDRESS.COMPANY")) return contact.getStringValueByKey("delivery_company");
			if (key.equals("DELIVERY.ADDRESS.STREET")) return contact.getStringValueByKey("delivery_street");
			if (key.equals("DELIVERY.ADDRESS.STREETNAME")) return getStreetName(contact.getStringValueByKey("delivery_street"));
			if (key.equals("DELIVERY.ADDRESS.STREETNO")) return getStreetNo(contact.getStringValueByKey("delivery_street"));
			if (key.equals("DELIVERY.ADDRESS.ZIP")) return contact.getStringValueByKey("delivery_zip");
			if (key.equals("DELIVERY.ADDRESS.CITY")) return contact.getStringValueByKey("delivery_city");
			if (key.equals("DELIVERY.ADDRESS.COUNTRY")) return contact.getStringValueByKey("delivery_country");
			if (key.equals("ADDRESS.BANK.ACCOUNT.HOLDER")) return contact.getStringValueByKey("account_holder");
			if (key.equals("ADDRESS.BANK.ACCOUNT")) return contact.getStringValueByKey("account");
			if (key.equals("ADDRESS.BANK.CODE")) return contact.getStringValueByKey("bank_code");
			if (key.equals("ADDRESS.BANK.NAME")) return contact.getStringValueByKey("bank_name");
			if (key.equals("ADDRESS.BANK.IBAN")) return contact.getStringValueByKey("iban");
			if (key.equals("ADDRESS.BANK.BIC")) return contact.getStringValueByKey("bic");
			if (key.equals("ADDRESS.NR")) return contact.getStringValueByKey("nr");
			if (key.equals("ADDRESS.PHONE")) return contact.getStringValueByKey("phone");
			if (key.equals("ADDRESS.FAX")) return contact.getStringValueByKey("fax");
			if (key.equals("ADDRESS.MOBILE")) return contact.getStringValueByKey("mobile");
			if (key.equals("ADDRESS.EMAIL")) return contact.getStringValueByKey("email");
			if (key.equals("ADDRESS.WEBSITE")) return contact.getStringValueByKey("website");
			if (key.equals("ADDRESS.VATNR")) return contact.getStringValueByKey("vatnr");
			if (key.equals("ADDRESS.NOTE")) return contact.getStringValueByKey("note");
			if (key.equals("ADDRESS.DISCOUNT")) return contact.getFormatedStringValueByKey("discount");
		}
		else {
			if (key2.equals("ADDRESS.GENDER")) return "";
			if (key2.equals("ADDRESS.TITLE")) return "";
			if (key2.equals("ADDRESS.NAME")) return getDataFromAddressField(addressField,"name");
			if (key2.equals("ADDRESS.FIRSTNAME")) return getDataFromAddressField(addressField,"firstname");
			if (key2.equals("ADDRESS.LASTNAME")) return getDataFromAddressField(addressField,"lastname");
			if (key2.equals("ADDRESS.COMPANY")) return getDataFromAddressField(addressField,"company");
			if (key2.equals("ADDRESS.STREET")) return getDataFromAddressField(addressField,"street");
			if (key2.equals("ADDRESS.STREETNAME")) return getDataFromAddressField(addressField,"streetname");
			if (key2.equals("ADDRESS.STREETNO")) return getDataFromAddressField(addressField,"streetno");
			if (key2.equals("ADDRESS.ZIP")) return getDataFromAddressField(addressField,"zip");
			if (key2.equals("ADDRESS.CITY")) return getDataFromAddressField(addressField,"city");
			if (key2.equals("ADDRESS.COUNTRY")) return getDataFromAddressField(addressField,"country");

			if (key2.equals("ADDRESS.GREETING")) return DataSetContact.getCommonGreeting();

			if (key.equals("ADDRESS.BANK.ACCOUNT.HOLDER")) return "";
			if (key.equals("ADDRESS.BANK.ACCOUNT")) return "";
			if (key.equals("ADDRESS.BANK.CODE")) return "";
			if (key.equals("ADDRESS.BANK.NAME")) return "";
			if (key.equals("ADDRESS.BANK.IBAN")) return "";
			if (key.equals("ADDRESS.BANK.BIC")) return "";
			if (key.equals("ADDRESS.NR")) return "";
			if (key.equals("ADDRESS.PHONE")) return "";
			if (key.equals("ADDRESS.FAX")) return "";
			if (key.equals("ADDRESS.MOBILE")) return "";
			if (key.equals("ADDRESS.EMAIL")) return "";
			if (key.equals("ADDRESS.WEBSITE")) return "";
			if (key.equals("ADDRESS.VATNR")) return "";
			if (key.equals("ADDRESS.NOTE")) return "";
			if (key.equals("ADDRESS.DISCOUNT")) return "";
		}
		return null;
	}
	
	/**
	 * Getter for all placeholders
	 * @return
	 * 	String array with all placeholders
	 */
	public static String[] getPlaceholders() {
		return placeholders;
	}
}
