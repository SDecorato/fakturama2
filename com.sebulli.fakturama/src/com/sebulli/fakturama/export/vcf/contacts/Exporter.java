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

package com.sebulli.fakturama.export.vcf.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sebulli.fakturama.OSDependent;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetContact;


/**
 * This class generates a list with all contacts
 * 
 * @author Gerd Bartelt
 */
public class Exporter {
	
	private BufferedWriter bos  = null;
	private String NEW_LINE;
	/**
	 * Constructor
	 * 
	 */
	public Exporter() {
		super();
		NEW_LINE = OSDependent.getNewLine();
	}

	private String encodeVCardString(String s) {
		s = s.replace("\n", "\\n");
		s = s.replace(":", "\\:");
		s = s.replace(",", "\\,");
		s = s.replace(";", "\\;");
		return s;
	}
	
	private void writeVCard(String property, String s) {
		writeVCard(property, s, null);
	}

	private void writeVCard(String property, String s1, String s2) {
		writeVCard(property, s1, s2, null, null, null, null, null);
	}
	
	private void writeAttribute(String s, boolean first) {

		if (s== null)
			return;
		
		try {
			if (!first)
				bos.write(";");
			bos.write(encodeVCardString(s));
		}
		catch (IOException e) {}

	}
	
	private void writeVCard(String property, String s1, String s2, 
			String s3, String s4, String s5, String s6, String s7) {
		
		boolean hasInformation = false;
		
		if (s1 != null) 
			if (!s1.isEmpty())
				hasInformation = true;
		if (s2 != null) 
			if (!s2.isEmpty())
				hasInformation = true;
		if (s3 != null) 
			if (!s3.isEmpty())
				hasInformation = true;
		if (s4 != null) 
			if (!s4.isEmpty())
				hasInformation = true;
		if (s5 != null) 
			if (!s5.isEmpty())
				hasInformation = true;
		if (s6 != null) 
			if (!s6.isEmpty())
				hasInformation = true;
		if (s7 != null) 
			if (!s7.isEmpty())
				hasInformation = true;
			
		if (!hasInformation)
			return;
		
		try {
			bos.write(property);
			writeAttribute(s1, true);
			writeAttribute(s2, false);
			writeAttribute(s3, false);
			writeAttribute(s4, false);
			writeAttribute(s5, false);
			writeAttribute(s6, false);
			writeAttribute(s7, false);
			bos.write(NEW_LINE);
		}
		catch (IOException e) {
		}
	}
	
	// Do the export job.
	public boolean export(String filename) {

		
		// Create a File object
		File csvFile = new File(filename);
		
		// Create a new file
		try {
			csvFile.createNewFile();
			bos = new BufferedWriter(new FileWriter(csvFile, false));
			// Get all undeleted contacts
			ArrayList<DataSetContact> contacts = Data.INSTANCE.getContacts().getActiveDatasets();
			
			// Export the product data
			for (DataSetContact contact : contacts) {
				
				System.out.println(contact.getStringValueByKey("company"));
				
				writeVCard("BEGIN:","VCARD");
				writeVCard("VERSION:","3.0");
				writeVCard("N:", contact.getStringValueByKey("name"),
						contact.getStringValueByKey("firstname"));
				writeVCard("FN:", contact.getName(false));
				writeVCard("ADR;TYPE=home:",
						"",
						contact.getStringValueByKey("company"),
						contact.getStringValueByKey("street"),
						contact.getStringValueByKey("city"),
						"",
						contact.getStringValueByKey("zip"),
						contact.getStringValueByKey("country")
						);
				writeVCard("ADR;TYPE=postal:",
						"",
						contact.getStringValueByKey("delivery_company"),
						contact.getStringValueByKey("delivery_street"),
						contact.getStringValueByKey("delivery_city"),
						"",
						contact.getStringValueByKey("delivery_zip"),
						contact.getStringValueByKey("delivery_country")
						);
				
				writeVCard("ADR;TYPE=other:",
						contact.getName(true),
						contact.getStringValueByKey("delivery_company"),
						contact.getStringValueByKey("delivery_street"),
						contact.getStringValueByKey("delivery_city"),
						"",
						contact.getStringValueByKey("delivery_zip"),
						contact.getStringValueByKey("delivery_country")
						);
				
				writeVCard("TEL;TYPE=HOME,WORK,VOICE:",contact.getStringValueByKey("phone"));
				writeVCard("TEL;TYPE=HOME,WORK,FAX:",contact.getStringValueByKey("fax"));
				writeVCard("TEL;TYPE=HOME,WORK,CELL:",contact.getStringValueByKey("mobile"));
				writeVCard("EMAIL;TYPE=internet:",contact.getStringValueByKey("email"));
				writeVCard("URL:",contact.getStringValueByKey("website"));

				writeVCard("NOTE:",contact.getStringValueByKey("note"));
				writeVCard("CATEGORIES:",contact.getStringValueByKey("category"));
				
				writeVCard("END:","VCARD");

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
