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

import com.sebulli.fakturama.OSDependent;

/**
 * UniDataSet for all contacts 
 * 
 * @author Gerd Bartelt
 */
public class DataSetContact extends UniDataSet {

	/**
	 * Constructor
	 */
	public DataSetContact() {
		this("");
	}

	/**
	 * Constructor
	 * Create a new contact
	 * 
	 * @param category Category
	 */
	public DataSetContact(String category) {
		this(false, category, "", "", "", "", "");
	}

	/**
	 * Constructor
	 * Create a new contact
	 * 
	 * @param deleted
	 * @param category 
	 * @param firstname
	 * @param name
	 * @param street
	 * @param zip
	 * @param city
	 */
	public DataSetContact(boolean deleted, String category, String firstname, String name, String street, String zip, String city) {
		this(-1, deleted, category, 0, "", firstname, name, "", street, zip, city, "", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
				-1, 0, "", "", "", "", "", "", 0, 0.0);
	}

	/**
	 * Constructor
	 * Create a new contact
	 * 
	 * @param id
	 * @param deleted
	 * @param category
	 * @param gender
	 * @param title
	 * @param firstname
	 * @param name
	 * @param company
	 * @param street
	 * @param zip
	 * @param city
	 * @param country
	 * @param delivery_gender
	 * @param delivery_title
	 * @param delivery_firstname
	 * @param delivery_name
	 * @param delivery_company
	 * @param delivery_street
	 * @param delivery_zip
	 * @param delivery_city
	 * @param delivery_country
	 * @param account_holder
	 * @param account
	 * @param bank_code
	 * @param bank_name
	 * @param iban
	 * @param bic
	 * @param nr
	 * @param note
	 * @param date_added
	 * @param payment
	 * @param reliability
	 * @param phone
	 * @param fax
	 * @param mobile
	 * @param email
	 * @param website
	 * @param vatnr
	 * @param vatnrvalid
	 * @param discount
	 */
	public DataSetContact(int id, boolean deleted, String category, int gender, String title, String firstname, String name, String company, String street,
			String zip, String city, String country, int delivery_gender, String delivery_title, String delivery_firstname, String delivery_name,
			String delivery_company, String delivery_street, String delivery_zip, String delivery_city, String delivery_country, String account_holder,
			String account, String bank_code, String bank_name, String iban, String bic, String nr, String note, String date_added, int payment,
			int reliability, String phone, String fax, String mobile, String email, String website, String vatnr, int vatnrvalid, double discount) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));

		this.hashMap.put("gender", new UniData(UniDataType.INT, gender));
		this.hashMap.put("title", new UniData(UniDataType.STRING, title));
		this.hashMap.put("firstname", new UniData(UniDataType.STRING, firstname));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("company", new UniData(UniDataType.STRING, company));
		this.hashMap.put("street", new UniData(UniDataType.STRING, street));
		this.hashMap.put("zip", new UniData(UniDataType.STRING, zip));
		this.hashMap.put("city", new UniData(UniDataType.STRING, city));
		this.hashMap.put("country", new UniData(UniDataType.STRING, country));

		this.hashMap.put("delivery_gender", new UniData(UniDataType.INT, delivery_gender));
		this.hashMap.put("delivery_title", new UniData(UniDataType.STRING, delivery_title));
		this.hashMap.put("delivery_firstname", new UniData(UniDataType.STRING, delivery_firstname));
		this.hashMap.put("delivery_name", new UniData(UniDataType.STRING, delivery_name));
		this.hashMap.put("delivery_company", new UniData(UniDataType.STRING, delivery_company));
		this.hashMap.put("delivery_street", new UniData(UniDataType.STRING, delivery_street));
		this.hashMap.put("delivery_zip", new UniData(UniDataType.STRING, delivery_zip));
		this.hashMap.put("delivery_city", new UniData(UniDataType.STRING, delivery_city));
		this.hashMap.put("delivery_country", new UniData(UniDataType.STRING, delivery_country));

		this.hashMap.put("account_holder", new UniData(UniDataType.STRING, account_holder));
		this.hashMap.put("account", new UniData(UniDataType.STRING, account));
		this.hashMap.put("bank_code", new UniData(UniDataType.STRING, bank_code));
		this.hashMap.put("bank_name", new UniData(UniDataType.STRING, bank_name));
		this.hashMap.put("iban", new UniData(UniDataType.STRING, iban));
		this.hashMap.put("bic", new UniData(UniDataType.STRING, bic));

		this.hashMap.put("nr", new UniData(UniDataType.STRING, nr));
		this.hashMap.put("note", new UniData(UniDataType.STRING, note));
		this.hashMap.put("date_added", new UniData(UniDataType.STRING, date_added));
		this.hashMap.put("payment", new UniData(UniDataType.ID, payment));
		this.hashMap.put("reliability", new UniData(UniDataType.INT, reliability));
		this.hashMap.put("phone", new UniData(UniDataType.STRING, phone));
		this.hashMap.put("fax", new UniData(UniDataType.STRING, fax));
		this.hashMap.put("mobile", new UniData(UniDataType.STRING, mobile));
		this.hashMap.put("email", new UniData(UniDataType.STRING, email));
		this.hashMap.put("website", new UniData(UniDataType.STRING, website));
		this.hashMap.put("vatnr", new UniData(UniDataType.STRING, vatnr));
		this.hashMap.put("vatnrvalid", new UniData(UniDataType.INT, vatnrvalid));
		this.hashMap.put("discount", new UniData(UniDataType.PERCENT, discount));

		// Name of the table in the data base
		sqlTabeName = "Contacts";
	}

	/**
	 * Get the delivery address
	 * 
	 * @return Complete delivery address
	 */
	public String getDeliveryAddress() {
		String address = "";
		String line = "";

		// add company
		line = this.getStringValueByKey("delivery_company");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add name
		line = "";
		if (!this.getStringValueByKey("delivery_title").isEmpty())
			line += this.getStringValueByKey("delivery_title") + " ";
		line += this.getStringValueByKey("delivery_firstname");

		if (!this.getStringValueByKey("delivery_name").isEmpty())
			line += " " + this.getStringValueByKey("delivery_name");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add street
		line = this.getStringValueByKey("delivery_street");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add ZIP code
		line = this.getStringValueByKey("delivery_zip");
		if (!this.getStringValueByKey("delivery_city").isEmpty())
			line += " " + this.getStringValueByKey("delivery_city");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add country
		line = this.getStringValueByKey("delivery_country");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// return the complete address
		return address;
	}

	/**
	 * Get the address
	 * 
	 * @return Complete address
	 */
	public String getAddress() {
		String address = "";
		String line = "";

		// add company
		line = this.getStringValueByKey("company");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add name
		line = "";
		if (!this.getStringValueByKey("title").isEmpty())
			line += this.getStringValueByKey("title") + " ";
		line += this.getStringValueByKey("firstname");

		if (!this.getStringValueByKey("name").isEmpty())
			line += " " + this.getStringValueByKey("name");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add street
		line = this.getStringValueByKey("street");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add zip
		line = this.getStringValueByKey("zip");
		if (!this.getStringValueByKey("city").isEmpty())
			line += " " + this.getStringValueByKey("city");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// add county
		line = this.getStringValueByKey("country");
		if (!line.isEmpty()) {
			if (!address.isEmpty())
				address += OSDependent.getNewLine();
			address += line;
		}

		// return the complete address
		return address;
	}

	/**
	 * Get the first and the last name
	 * 
	 * @return First and last name
	 */
	public String getName() {
		String line = "";

		line = this.getStringValueByKey("firstname");
		if (!this.getStringValueByKey("name").isEmpty())
			line += " " + this.getStringValueByKey("name");

		return line;
	}

	/**
	 * Get the gender String
	 * 
	 * @return Gender as String
	 */
	public String getGenderString() {
		return DataSetContact.getGenderString(this.getIntValueByKey("gender"));
	}

	
	/**
	 * Get the gender String
	 * 
	 * @return Gender as String
	 */
	public String getDeliveryGenderString() {
		return DataSetContact.getGenderString(this.getIntValueByKey("delivery_gender"));
	}

	/**
	 * Get the gender String by the gender number
	 * 
	 * @param i Gender number
	 * @return Gender as string
	 */
	public static String getGenderString(int i) {
		switch (i) {
		case 0:
			return "---";
		case 1:
			return "Herr";
		case 2:
			return "Frau";
		case 3:
			return "Firma";
		}
		return "";
	}

	/**
	 * Test, if this is equal to an other UniDataSet
	 * Only first name, name and ZIP are compared
	 * 
	 * @param uds Other UniDataSet
	 * @return True, if it's equal
	 */
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("firstname").equals(this.getStringValueByKey("firstname")))
			return false;
		if (!uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
			return false;
		if (!uds.getStringValueByKey("zip").equals(this.getStringValueByKey("zip")))
			return false;
		return true;
	}

}
