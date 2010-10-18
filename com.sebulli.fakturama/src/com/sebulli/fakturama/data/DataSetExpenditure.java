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

import com.sebulli.fakturama.calculate.ExpenditureSummary;
import com.sebulli.fakturama.logger.Logger;

/**
 * UniDataSet for all expenditures. 
 * 
 * @author Gerd Bartelt
 */
public class DataSetExpenditure extends UniDataSet {
	ExpenditureSummary summary = new ExpenditureSummary();

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
	 * Get all the expenditure items.
	 * Generate the list by the items string
	 * 
	 * @return All items of this expenditure
	 */
	public DataSetArray<DataSetExpenditureItem> getItems() {
		DataSetArray<DataSetExpenditureItem> items = new DataSetArray<DataSetExpenditureItem>();
		
		// Split the items string
		String itemsString = this.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");
		
		// Get all items
		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				} catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}
				items.getDatasets().add(Data.INSTANCE.getExpenditureItems().getDatasetById(id));
			}
		}
		return items;
	}

	/**
	 * Get one expenditure item as array 
	 * Generate the list with only one entry
	 * 
	 * @return One items of this expenditure
	 */
	public DataSetArray<DataSetExpenditureItem> getItems(int index) {
		DataSetArray<DataSetExpenditureItem> items = new DataSetArray<DataSetExpenditureItem>();

		// Get one item and add it to the list
		items.getDatasets().add(getItem(index));
		return items;
	}

	/**
	 * Get one expenditure item
	 * 
	 * @return One items of this expenditure
	 */
	public DataSetExpenditureItem getItem(int index) {

		// Split the items string
		String itemsString = this.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");
		String itemsStringPart = itemsStringParts [index];
		int id;
		if (itemsStringPart.length() > 0) {
			try {
				id = Integer.parseInt(itemsStringPart);
			} catch (NumberFormatException e) {
				Logger.logError(e, "Error parsing item string");
				id = 0;
			}
			return (Data.INSTANCE.getExpenditureItems().getDatasetById(id));
		}

		Logger.logError("Expenditure item not found:" + index);
		return null;
		
	}
	
	
	/**
	 * Recalculate the expenditure total values
	 */
	public void calculate() {
		calculate(this.getItems(), false);
	}

	/**
	 * Recalculate the expenditure total values
	 * 
	 * @param items Expenditure items as DataSetArray
	 * @param useCategory If true, the category is also used for the vat summary as a description
	 */
	public void calculate(DataSetArray<DataSetExpenditureItem> items, boolean useCategory) {
		summary.calculate(null, items, useCategory);
	}
	
	/**
	 * Getter for the expenditure summary
	 * 
	 * @return Summary
	 */
	public ExpenditureSummary getSummary() {
		return this.summary;
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
