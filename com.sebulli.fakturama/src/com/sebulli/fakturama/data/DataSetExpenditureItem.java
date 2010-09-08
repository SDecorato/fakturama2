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
public class DataSetExpenditureItem extends UniDataSet {

	/**
	 * Constructor
	 * Creates a new expenditure
	 * 
	 */
	public DataSetExpenditureItem() {
		this("");
	}

	/**
	 * Constructor
	 * Creates a new expenditure item
	 * 
	 * @param category Category of the new expenditure
	 */
	public DataSetExpenditureItem(String category) {
		this ("",  category, 0.0, -1);
	}


	/**
	 * Constructor
	 * Creates a new expenditure item
	 * 
	 * @param name
	 * @param category
	 * @param date
	 * @param documentnr
	 * @param items
	 */
	public DataSetExpenditureItem(String name,  String category, Double price, int vatId){
		this (-1, name, false,  category, price, vatId);
	}
	/**
	 * Constructor
	 * Creates a new expenditure
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param category
	 * @param date
	 * @param documentnr
	 * @param items
	 */
	public DataSetExpenditureItem(int id, String name, boolean deleted, String category, 
			Double price, int vatId){
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("price", new UniData(UniDataType.PRICE, price));
		this.hashMap.put("vatid", new UniData(UniDataType.ID, vatId));

		// Name of the table in the data base
		sqlTabeName = "ExpenditureItems";

	}
}
