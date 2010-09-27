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

import java.text.SimpleDateFormat;
import java.util.Date;

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
		this ("",  category, (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()), "", "", "");
	}


	/**
	 * Constructor
	 * Creates a new expenditure
	 * 
	 * @param name
	 * @param category
	 * @param date
	 * @param nr
	 * @param documentnr
	 * @param items
	 */
	public DataSetExpenditure(String name,  String category, String date,
			String nr, String documentnr, String items){
		this (-1, name, false,  category, date, nr, documentnr, items);
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
	 * @param nr
	 * @param documentnr
	 * @param items
	 */
	public DataSetExpenditure(int id, String name, boolean deleted, String category,
			String date, String nr, String documentnr, String items){
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("date", new UniData(UniDataType.DATE, date));
		this.hashMap.put("nr", new UniData(UniDataType.STRING, documentnr));
		this.hashMap.put("documentnr", new UniData(UniDataType.STRING, documentnr));
		this.hashMap.put("items", new UniData(UniDataType.STRING, items));

		// Name of the table in the data base
		sqlTabeName = "Expenditures";

	}
	
	/**
	 * Test, if this is equal to an other UniDataSet
	 * Only the names and the item numbers are compared
	 * 
	 * @param uds Other UniDataSet
	 * @return True, if it's equal
	 */
	@Override
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
			return false;
		if (!uds.getStringValueByKey("category").equals(this.getStringValueByKey("category")))
			return false;
		if (!uds.getStringValueByKey("date").equals(this.getStringValueByKey("date")))
			return false;
		if (!uds.getStringValueByKey("nr").equals(this.getStringValueByKey("nr")))
			return false;
		if (!uds.getStringValueByKey("documentnr").equals(this.getStringValueByKey("documentnr")))
			return false;

		return true;
	}

}
