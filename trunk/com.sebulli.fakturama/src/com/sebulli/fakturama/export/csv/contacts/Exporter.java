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

package com.sebulli.fakturama.export.csv.contacts;

import static com.sebulli.fakturama.Translate._;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sebulli.fakturama.OSDependent;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetContact;


/**
 * This class generates a list with all contacts
 * 
 * @author Gerd Bartelt
 */
public class Exporter {
	
	/**
	 * Constructor
	 * 
	 */
	public Exporter() {
		super();
	}

	// Do the export job.
	public boolean export(String filename) {

		String NEW_LINE = OSDependent.getNewLine();
		
		// Create a File object
		File csvFile = new File(filename);
		BufferedWriter bos  = null;
		// Create a new file
		try {
			csvFile.createNewFile();
			bos = new BufferedWriter(new FileWriter(csvFile, false));
			bos.write(
					//T: Used as heading of a table. Keep the word short.
					"\""+ "ID" + "\";"+ 
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Category") + "\";"+

					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Gender") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Title","ADDRESS") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("First Name") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Last Name") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Company") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Street") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("ZIP") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("City") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Country") + "\";"+
					
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Gender") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Title","ADDRESS") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("First Name") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Last Name") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Company") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Street") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("ZIP") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("City") + " ("+_("Delivery Address")+")" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Country") + " ("+_("Delivery Address")+")" + "\";"+
					
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Account Holder") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Account Number") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Bank Code") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Name of the Bank") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("IBAN") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("BIC") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Customer ID") + "\";"+

					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Notice") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Date") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Payment") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Reliability") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Telephone") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Telefax") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Mobile") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("E-Mail") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Web Site") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("VAT Number") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("VAT Number valid") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Discount","CUSTOMER") + "\""+
					NEW_LINE);
		
			// Get all undeleted contacts
			ArrayList<DataSetContact> contacts = Data.INSTANCE.getContacts().getActiveDatasets();
			
			// Export the product data
			for (DataSetContact contact : contacts) {
				
				
				// Place the products information into the table
				bos.write(
						contact.getStringValueByKey("id")+ ";" +
						"\"" + contact.getStringValueByKey("category")+ "\";" +
						
						"\"" + DataSetContact.getGenderString(contact.getIntValueByKey("gender"))+ "\";" +
						"\"" + contact.getStringValueByKey("title")+ "\";" +
						"\"" + contact.getStringValueByKey("firstname")+ "\";" +
						"\"" + contact.getStringValueByKey("name")+ "\";" +
						"\"" + contact.getStringValueByKey("company")+ "\";" +
						"\"" + contact.getStringValueByKey("street")+ "\";" +
						"\"" + contact.getStringValueByKey("zip")+ "\";" +
						"\"" + contact.getStringValueByKey("city")+ "\";" +
						"\"" + contact.getStringValueByKey("country")+ "\";" +

						"\"" + DataSetContact.getGenderString(contact.getIntValueByKey("delivery_gender"))+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_title")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_firstname")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_name")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_company")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_street")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_zip")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_city")+ "\";" +
						"\"" + contact.getStringValueByKey("delivery_country")+ "\";" +

						
						"\"" + contact.getStringValueByKey("account_holder")+ "\";" +
						"\"" + contact.getStringValueByKey("account")+ "\";" +
						"\"" + contact.getStringValueByKey("bank_code")+ "\";" +
						"\"" + contact.getStringValueByKey("bank_name")+ "\";" +
						"\"" + contact.getStringValueByKey("iban")+ "\";" +
						"\"" + contact.getStringValueByKey("bic")+ "\";" +
						"\"" + contact.getStringValueByKey("nr")+ "\";" +
						"\"" + contact.getStringValueByKey("note")+ "\";" +
						"\"" + contact.getStringValueByKey("date_added")+ "\";" +
						"\"" + contact.getFormatedStringValueByKeyFromOtherTable("payment.PAYMENTS:description")+ "\";" +
						"\"" + DataSetContact.getReliabilityString(contact.getIntValueByKey("reliability"))+ "\";" +
						"\"" + contact.getStringValueByKey("phone")+ "\";" +
						"\"" + contact.getStringValueByKey("fax")+ "\";" +
						"\"" + contact.getStringValueByKey("mobile")+ "\";" +
						"\"" + contact.getStringValueByKey("email")+ "\";" +
						"\"" + contact.getStringValueByKey("website")+ "\";" +
						"\"" + contact.getStringValueByKey("vatnr")+ "\";" +
						contact.getStringValueByKey("vatnrvalid")+ ";" +
						"\"" + DataUtils.DoubleToDecimalFormatedValue(contact.getDoubleValueByKey("discount"),"0.00")+ "\"" +
						NEW_LINE);
			}
		
		}
		catch (IOException e) {
			return false;
		}

		try {
			if (bos!= null)
				bos.close();
		}
		catch (Exception e) {}

		// True = Export was successful
		return true;
	}

}
