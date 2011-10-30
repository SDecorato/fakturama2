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

package com.sebulli.fakturama.parcelService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IEditorInput;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.editors.ParcelServiceBrowserEditorInput;

/**
 * Fills the form of the parcel service
 * 
 * @author Gerd Bartelt
 *
 */
public class ParcelServiceFormFiller {
	
	private Browser browser;
	
	private String provider = "";
	
	private String senderCompany = "";
	private String senderOwnerFirstName = "";
	private String senderOwnerLastName = "";
	private String senderOwner = "";
	private String senderAddressStreet = "";
	private String senderAddressNo = "";
	private String senderZIP = "";
	private String senderCity = "";
//	private String senderCountry = "";
	private String senderEmail ="";
	private String consigneeName = "";
	private String consigneeFirstName = "";
	private String consigneeLastName = "";
	private String consigneeAddressLine = "";
	private String consigneeAddressStreet = "";
	private String consigneeAddressNo = "";
	private String consigneeZIP = "";
	private String consigneeCity = "";
	private String consigneeCountry = "";
	private String consigneeEmail ="";
	
	private String consigneeNameValue = "";
	private String consigneeFirstNameValue = "";
	private String consigneeLastNameValue = "";
	private String consigneeAddressLineValue = "";
	private String consigneeAddressStreetValue = "";
	private String consigneeAddressNoValue = "";
	private String consigneeZIPValue = "";
	private String consigneeCityValue = "";
	private String consigneeCountryValue = "";
	private String consigneeEmailValue = "";
	
	
	private boolean filled;

	
	/**
	 * Constructor
	 */
	public ParcelServiceFormFiller() {
		filled = false;
	}

	/**
	 * Fill a field with a value
	 * 
	 * @param fieldName
	 * 		The name of the field
	 * @param value
	 * 		The value
	 */
	private void fillFormField (String fieldName, String pvalue) {
		
		if (fieldName.isEmpty())
			return;
		
		String value = pvalue.trim();
		
		// Script that counts the fields with this name
		String script = "" +
			
			// Trim the string 
			"function trim (s) {" +
			"  return s.replace (/^\\s+/, '').replace (/\\s+$/, '');" +
			"}" +

			// Compare two strings ignore case
			"function mycompare (s1, s2) {" +
			"  return trim(s1).toUpperCase() == trim(s2).toUpperCase();" +
			"}" +
		
			// select an option by the value or by the text
			"function selectOptionByValue(selObj, val){" +
			"  var A= selObj.options, L= A.length;" +
			"  while(L){" +
			"    if (A[--L].value == '899'){" +
			"      selObj.selectedIndex= L;" +
			"      L= 0;" +
			"    }" +
			"    if (mycompare( A[--L].text, '" + value + "')){" +
			"      selObj.selectedIndex= L;" +
			"      L= 0;" +
			"    }" +
			"  }" +
			"}" +

			// Fill the form field
			"function fillField() {"+
			"  var cnt = 0;" +
			"  documentForms = document.getElementsByTagName('form');" +
			"  for (var i = 0; i < documentForms.length; i++) {" +
			"    for(var ii = 0; ii < documentForms[i].elements.length; ii++) {" +
			"      if (documentForms[i].elements[ii].name == '" + fieldName +"') {" +
			"       if (documentForms[i].elements[ii].options) {" +
			"		   if (documentForms[i].elements[ii].options.length > 0) {" +
			"            selectOptionByValue(documentForms[i].elements[ii], '" + value + "');" +
			"          }" + 
			"        } else {" + 
			"          documentForms[i].elements[ii].value = '" + value + "';" +
			"        }" + 
			"      }" + 
			"    }" +
			"  }" +
			" return String(cnt);" +
			"};" +
			"fillField();"; 

			// Execute the script to fill the field.
			browser.execute(script);
	}
	
	/**
	 * Tests, whether a form field exists
	 * 
	 * @param fieldName
	 * 		The name of the field to test
	 * @return
	 * 		TRUE, if it exists.
	 */
	private boolean formFieldExists (String fieldName) {
		
		// Script that counts the fields with this name
		String script = "" +
			"function getNumberOfFields() {"+
			"  var cnt = 0;" +
			"  documentForms = document.getElementsByTagName('form');" +
			"  for (var i = 0; i < documentForms.length; i++) {" +
			"    for(var ii = 0; ii < documentForms[i].elements.length; ii++) {" +
			"      if (documentForms[i].elements[ii].name == '" + fieldName +"') {" +
			"        cnt++;" +
			"      }" + 
			"    }" +
			"  }" +
			" return String(cnt);" +
			"};" +
			"return getNumberOfFields();"; 

			// Convert the result to an integer
			String value = (String)browser.evaluate(script);
			Integer ivalue = Integer.valueOf(value);  
			
			// It exists, if there is at least one value
			return (ivalue >= 1);
	}
	
	
	/**
	 * Returns the first name of a complete name
	 * 
	 * @param name
	 * 		First name and last name
	 * @return
	 * 		Only the first name
	 */
	private String getFirstName (String name) {
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
	private String getLastName (String name) {
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
	private String getStreetName (String streetWithNo) {
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
	private String getStreetNo (String streetWithNo) {
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
	
	/**
	 * Fills the form of the parcel service with the address data
	 */
	public void fillForm(Browser browser, IEditorInput editorInput) {
		this.browser = browser;

		provider			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_PROVIDER");
		senderCompany 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_COMPANY");
		senderOwnerFirstName= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_OWNER_FIRST_NAME");
		senderOwnerLastName = Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_OWNER_LAST_NAME");
		senderOwner 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_OWNER");
		senderAddressStreet = Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_STREET");
		senderAddressNo 	= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_NO");
		senderZIP 			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_ZIP");
		senderCity 			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_CITY");
		//senderCountry 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_COUNTRY");
		senderEmail			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_SENDER_EMAIL");

		consigneeName 			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_NAME");
		consigneeFirstName 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_FIRST_NAME");
		consigneeLastName 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_LAST_NAME");
		consigneeAddressLine 	= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEER_ADDITIONAL_ADDRESS");
		consigneeAddressStreet 	= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_STREET");
		consigneeAddressNo 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_NO");
		consigneeZIP 			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_ZIP");
		consigneeCity 			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_CITY");
		consigneeCountry 		= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_COUNTRY");
		consigneeEmail			= Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_CONSIGNEE_EMAIL");

		if (provider.equals("EFILIALE.DE") || provider.equals("DHL")) {
			senderCompany = "senderCompanyName";
			senderOwnerFirstName = "senderFirstName";
			senderOwnerLastName = "senderLastName";
			senderOwner = "senderAddressLine1";
			senderAddressStreet = "senderAddressStreet";
			senderAddressNo = "senderAddressHouseNo";
			senderZIP = "senderPLZ";
			senderCity = "senderOrt";
			senderEmail = "";

			//senderCountry = "senderCountry";
			consigneeName = "consigneeCompanyName";
			consigneeFirstName = "consigneeFirstName";
			consigneeLastName = "consigneeLastName";
			consigneeAddressLine = "consigneeAddressLine1";
			consigneeAddressStreet = "consigneeAddressStreet";
			consigneeAddressNo = "consigneeAddressHouseNo";
			consigneeZIP = "consigneePLZ";
			consigneeCity = "consigneeOrt";
			consigneeCountry = "receiverCountry";
			consigneeEmail = "";
		}

		if (provider.equals("DHL.DE")) {
			senderCompany = "formModel.sender.name";
			senderOwnerFirstName = "";
			senderOwnerLastName = "";
			senderOwner = "formModel.sender.addressExt";
			senderAddressStreet = "formModel.sender.street";
			senderAddressNo = "formModel.sender.houseNumber";
			senderZIP = "formModel.sender.zip";
			senderCity = "formModel.sender.city";
			senderEmail = "";

			//senderCountry = "";
			consigneeName = "formModel.receiver.name";
			consigneeFirstName = "";
			consigneeLastName = "";
			consigneeAddressLine = "formModel.receiver.addressExt";
			consigneeAddressStreet = "formModel.receiver.street";
			consigneeAddressNo = "formModel.receiver.houseNumber";
			consigneeZIP = "formModel.receiver.zip";
			consigneeCity = "formModel.receiver.city";
			consigneeCountry = "";
			consigneeEmail = "";
		}
		
		
		if (provider.equals("HERMES")) {
			senderCompany = "absender(NACHNAME)";
			senderOwnerFirstName = "";
			senderOwnerLastName = "";
			senderOwner = "absender(ADRESSZUSATZ)";
			senderAddressStreet = "absender(STRASSE)";
			senderAddressNo = "absender(HAUSNUMMER)";
			senderZIP = "absender(PLZ)";
			senderCity = "absender(ORT)";
			senderEmail = "absender(EMAIL)";
			//senderCountry = "senderCountry";
			
			consigneeName = "";
			consigneeFirstName = "empfaenger(VORNAME)";
			consigneeLastName = "empfaenger(NACHNAME)";
			consigneeAddressLine = "empfaenger(ADRESSZUSATZ)";
			consigneeAddressStreet = "empfaenger(STRASSE)";
			consigneeAddressNo = "empfaenger(HAUSNUMMER)";
			consigneeZIP = "empfaenger(PLZ)";
			consigneeCity = "empfaenger(ORT)";
			consigneeCountry = "empfaenger(LAND)";
			consigneeEmail = "";

		}
		
		

		// Fill the fields
		if ( ( formFieldExists(consigneeLastName) || formFieldExists(consigneeName)  )&&
				!filled ) {
			filled = true;
			fillFormField(senderCompany, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_NAME"));

			String owner = Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_OWNER");
			fillFormField(senderOwnerFirstName, getFirstName(owner));
			fillFormField(senderOwnerLastName, getLastName(owner));
			fillFormField(senderOwner, owner);
			
			String streetWithNo = Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_STREET");
			fillFormField(senderAddressStreet, getStreetName(streetWithNo));
			fillFormField(senderAddressNo, getStreetNo(streetWithNo));
			fillFormField(senderZIP, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_ZIP"));
			fillFormField(senderCity, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_CITY"));
			//fillFormField(senderCountry, .. );
			fillFormField(senderEmail, Activator.getDefault().getPreferenceStore().getString("YOURCOMPANY_COMPANY_EMAIL"));
			
			
			String deliveryAddress = ((ParcelServiceBrowserEditorInput)editorInput).getDocument().getStringValueByKey("deliveryaddress");
			String[] addressLines = deliveryAddress.split("\\n");
			
			Boolean countryFound = false;
			Boolean cityFound = false;
			Boolean streetFound = false;
			String line = "";
			consigneeAddressLineValue = "";
			
			// The first line is the name
			consigneeNameValue = addressLines[0];
			consigneeFirstNameValue = getFirstName(consigneeNameValue);
			consigneeLastNameValue = getLastName(consigneeNameValue);
			
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
									consigneeZIPValue = matcher.group();

									// and the city
									consigneeCityValue = line.substring(matcher.end()+1).trim();
									
								}
								cityFound = true;
								countryFound = true;
							}
						}
						else {
							// It must be the country
							consigneeCountryValue =  line;
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
								consigneeAddressNoValue = line.substring(matcher.start()).trim();
								consigneeAddressStreetValue = line.substring(0, matcher.start()).trim();
								streetFound = true;
							}
						}
					}
					// Street, city and maybe country were found. 
					// Search now for additional address information
					else {
						if (!consigneeAddressLineValue.isEmpty())
							consigneeAddressLineValue +=" ";
						consigneeAddressLineValue = line;
					}
					
				}
			}
			
			// Get the email address
			consigneeEmailValue = ((ParcelServiceBrowserEditorInput)editorInput).getDocument().
					getStringValueByKeyFromOtherTable("addressid.CONTACTS:email");
			
			fillFormField(consigneeName, consigneeNameValue);
			fillFormField(consigneeFirstName, consigneeFirstNameValue);
			fillFormField(consigneeLastName, consigneeLastNameValue);
			fillFormField(consigneeAddressLine, consigneeAddressLineValue);
			fillFormField(consigneeAddressStreet, consigneeAddressStreetValue);
			fillFormField(consigneeAddressNo, consigneeAddressNoValue);
			fillFormField(consigneeZIP, consigneeZIPValue);
			fillFormField(consigneeCity, consigneeCityValue);
			fillFormField(consigneeCountry, consigneeCountryValue);
			fillFormField(consigneeEmail, consigneeEmailValue);
			
			
		}
	}

}
