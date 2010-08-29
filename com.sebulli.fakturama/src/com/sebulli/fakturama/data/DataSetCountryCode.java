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

/**
 * UniDataSet for all country codes 
 * 
 * @author Gerd Bartelt
 */
public class DataSetCountryCode extends UniDataSet {


	/**
	 * Constructor
	 * Creates an new country code
	 */
	public DataSetCountryCode() {
		this("");
	}

	/**
	 * Constructor
	 * Creates a new country code
	 * 
	 * @param category Category of the new text
	 */
	public DataSetCountryCode(String category) {
		this("", category, "");
	}

	/**
	 * Constructor
	 * Creates a new country code
	 * 
	 * @param name
	 * @param category
	 * @param code
	 */
	public DataSetCountryCode(String name, String category, String code) {
		this(0, name, false, category, code);
	}	
	
	
	/**
	 * Constructor
	 * Creates a country code
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param category
	 * @param text
	 */
	public DataSetCountryCode(int id, String name, boolean deleted, String category, String code) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("code", new UniData(UniDataType.STRING, code));

		// Name of the table in the data base
		sqlTabeName = "CountryCodes";
	}


}
