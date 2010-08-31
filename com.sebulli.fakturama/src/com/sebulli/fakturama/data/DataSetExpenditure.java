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
 * UniDataSet for all expenditures. 
 * 
 * @author Gerd Bartelt
 */
public class DataSetExpenditure extends UniDataSet {

	/**
	 * Constructor
	 * Creates a new expenditure
	 * 
	 */
	public DataSetExpenditure() {
		this("");
	}

	/**
	 * Constructor
	 * Creates a new expenditure
	 * 
	 * @param category Category of the new expenditure
	 */
	public DataSetExpenditure(String category) {
		this ("",  category, "", "", "", 0.0, 0);
	}


	/**
	 * Constructor
	 * Creates a new expenditure
	 * 
	 * @param name
	 * @param category
	 * @param date
	 * @param documentnr
	 * @param type
	 * @param price
	 * @param vatId
	 */
	public DataSetExpenditure(String name,  String category, String date, String documentnr,
			String type, Double price, int vatId){
		this (-1, name, false,  category, date, documentnr, type, price, vatId);
	}

	/**
	 * Constructor
	 * Creates a new expenditure from a product and the quantity
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param category
	 * @param date
	 * @param documentnr
	 * @param type
	 * @param price
	 * @param vatId
	 */
	public DataSetExpenditure(int id, String name, boolean deleted, String category, String date, String documentnr,
			String type, Double price, int vatId){
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("date", new UniData(UniDataType.DATE, date));
		this.hashMap.put("documentnr", new UniData(UniDataType.STRING, documentnr));
		this.hashMap.put("type", new UniData(UniDataType.STRING, type));
		this.hashMap.put("price", new UniData(UniDataType.PRICE, price));
		this.hashMap.put("vatid", new UniData(UniDataType.ID, vatId));

		// Name of the table in the data base
		sqlTabeName = "Expenditures";

	}
}
