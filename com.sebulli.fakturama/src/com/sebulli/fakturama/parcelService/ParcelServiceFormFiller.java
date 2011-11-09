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

import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IEditorInput;

import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.Placeholders;
import com.sebulli.fakturama.editors.ParcelServiceBrowserEditorInput;

/**
 * Fills the form of the parcel service
 * 
 * @author Gerd Bartelt
 *
 */
public class ParcelServiceFormFiller {
	
	private Browser browser;
	
	private String allFields = null;
	
	
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
		
		if (fieldName == null)
			return;

		if (pvalue == null)
			return;
		
		if (fieldName.isEmpty())
			return;
		
		System.out.println("fill: " + fieldName + " with: "+ pvalue);
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
		
		
		if (allFields == null) {
			// Script that counts the fields with this name
			String script = "" +
				"function getAllFields() {"+
				"  var s = ':';" +
				"  documentForms = document.getElementsByTagName('form');" +
				"  for (var i = 0; i < documentForms.length; i++) {" +
				"    for(var ii = 0; ii < documentForms[i].elements.length; ii++) {" +
				"      if (documentForms[i].elements[ii].name) {" +
				"        s = s + documentForms[i].elements[ii].name + ':' ;" +
				"      }" + 
				"    }" +
				"  }" +
				" return s;" +
				"};" +
				"return getAllFields();"; 

				// Convert the result to an integer
				allFields = (String)browser.evaluate(script);
		}
		
		return allFields.contains(":" + fieldName + ":");
		
	}
	
	/**
	 * Tests, whether a form field exists
	 * 
	 * @param fieldName
	 * 		The name of the field to test
	 * @return
	 * 		TRUE, if it exists.
	 */
	/*
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
	*/
	
	
	/**
	 * Fills the form of the parcel service with the address data
	 */
	public void fillForm(Browser browser, IEditorInput editorInput) {
		this.browser = browser;

		Properties inputProperties = ((ParcelServiceBrowserEditorInput)editorInput).getProperties();
		DataSetDocument document =((ParcelServiceBrowserEditorInput)editorInput).getDocument();
		Properties p = new Properties();
		
		for (Map.Entry<Object, Object> propItem : inputProperties.entrySet())
		{
		    String key = (String) propItem.getKey();
		    String value = (String) propItem.getValue();
			if ((!key.equalsIgnoreCase("url")) && (!key.equalsIgnoreCase("url")) && (!value.isEmpty()) ) {
				p.put(value, key);
			}
		}
		
		// Fill the fields
		// At least this fields must exist in the website's form
		if ( ( formFieldExists(p.getProperty("DELIVERY.ADDRESS.NAME")) ||
			   formFieldExists(p.getProperty("DELIVERY.ADDRESS.LASTNAME")) ||
			   formFieldExists(p.getProperty("ADDRESS.NAME")) ||
			   formFieldExists(p.getProperty("ADDRESS.LASTNAME"))   )&&
				!filled ) {
			filled = true;
			
			
			// Get all placeholders and set them
			for (String placeholder: Placeholders.getPlaceholders()) {
				fillFormField(p.getProperty(placeholder),Placeholders.getDocumentInfo(document, placeholder));
			}

		}
	}

}
