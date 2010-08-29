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
 * UniDataSet for all list entries 
 * 
 * @author Gerd Bartelt
 */
public class DataSetList extends UniDataSet {


	/**
	 * Constructor
	 * Creates an new list entry
	 */
	public DataSetList() {
		this("");
	}

	/**
	 * Constructor
	 * Creates a new list entry
	 * 
	 * @param category Category of the new list entry
	 */
	public DataSetList(String category) {
		this("", category, "");
	}

	/**
	 * Constructor
	 * Creates a new list entry
	 * 
	 * @param name
	 * @param category
	 * @param value
	 */
	public DataSetList(String name, String category, String value) {
		this(0, name, false, category, value);
	}	
	
	
	/**
	 * Constructor
	 * Creates a list entry
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param category
	 * @param value
	 */
	public DataSetList(int id, String name, boolean deleted, String category, String value) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("value", new UniData(UniDataType.STRING, value));

		// Name of the table in the data base
		sqlTabeName = "List";
	}


}
