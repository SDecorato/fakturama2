/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.data;

/**
 * UniDataSet for all properties
 * 
 * @author Gerd Bartelt
 */
public class DataSetProperty extends UniDataSet {

	/**
	 * Constructor Creates an new property
	 */
	public DataSetProperty() {
		this(-1, "", "");
	}

	/**
	 * Constructor Creates an new property
	 * 
	 * @param name
	 * @param value
	 */
	public DataSetProperty(String name, String value) {
		this(-1, name, value);
	}

	/**
	 * Constructor Creates an new property
	 * 
	 * @param id
	 * @param name
	 * @param value
	 */
	public DataSetProperty(int id, String name, String value) {
		this.hashMap.put("id", new UniData(UniDataType.INT, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("value", new UniData(UniDataType.TEXT, value));

		// Name of the table in the data base
		sqlTabeName = "Properties";
	}

}
