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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.logger.Logger;

/**
 * Array List of UniDataSets
 * This list is used to store all the data sets like documents, contacts, products ...
 * 
 * @author Gerd Bartelt
 *
 * @param <T>
 */
public class DataSetArray<T> {
	
	// The Array List to store the data
	private ArrayList<T> datasets = new ArrayList<T>();
	
	// Reference to the data base
	private DataBase db;
	
	private UniDataSet udsTemplate;
	private int categoryStringsCode = 0;
	private Properties oldProps = new Properties();

	/**
	 *	Constructor 
	 */
	public DataSetArray() {
	}

	/**
	 * Constructor
	 * When this constructor is used, the data base table is copied into this ArrayList
	 * 
	 * @param db Data base
	 * @param udsTemplate Template of the UniDataSet 
	 */
	public DataSetArray(DataBase db, UniDataSet udsTemplate) {
		this.db = db;
		if (this.db != null)
			this.db.getTable(datasets, udsTemplate);
		this.udsTemplate = udsTemplate;
	}

	/**
	 * Gets the Template of this ArrayList
	 * 
	 * @return UniDataSet template
	 */
	public UniDataSet getTemplate() {
		return this.udsTemplate;
	}

	/**
	 * Get the next free ID
	 * 
	 * @return next free ID
	 */
	public int getNextFreeId() {
		int maxId = -1;
		for (T dataset : datasets) {
			if (maxId < ((UniDataSet) dataset).getIntValueByKey("id")) {
				maxId = ((UniDataSet) dataset).getIntValueByKey("id");
			}
		}
		return maxId + 1;
	}

	/**
	 * Add a data set to the list of data sets.
	 * The ID of the new data set is set to the next free id.
	 * Also this new data set is insert into the data base
	 * 
	 * @param dataset New data set
	 * @return the new data set (with modified ID)
	 */
	public T addNewDataSet(T dataset) {
		((UniDataSet) dataset).setIntValueByKey("id", getNextFreeId());
		datasets.add(dataset);
		if (this.db != null)
			this.db.insertUniDataSet((UniDataSet) dataset);
		return datasets.get(datasets.size() - 1);
	}

	
	/**
	 * Add a data set to the list of data set, but
	 * do it only, if the data set is not yet existing.
	 * 
	 * @param dataset New data set
	 * @return the new data set, or an the existing one
	 */
	public T addNewDataSetIfNew(T dataset) {

		// get an existing data set
		T testdataset = getExistingDataSet(dataset);
		if (testdataset != null)
			return testdataset;

		// create a new one, if it is not in the list
		return addNewDataSet(dataset);
	}

	/**
	 * get an existing data set
	 * 
	 * @param dataset Search for this data set
	 * @return The data set that was found, or null
	 */
	public T getExistingDataSet (T dataset) {

		// Search the list for an existing data set and return it
		ArrayList<T> undeletedDatasets = getActiveDatasets();
		for (T testdataset : undeletedDatasets) {
			if (((UniDataSet) testdataset).isTheSameAs((UniDataSet) dataset)) { return testdataset; }
		}
		
		// nothing found
		return null;
	}
	
	/**
	 * Test, if the data set is a new data set
	 * 
	 * @param dataset Test this data set
	 * @return True, if it is new and not in the list
	 */
	public boolean isNew(T dataset) {
		return (getExistingDataSet(dataset) == null);
	}

	/**
	 * Update the data set in the data base
	 * 
	 * @param dataset Data set to update
	 */
	public void updateDataSet(T dataset) {
		if (this.db != null)
			db.updateUniDataSet((UniDataSet) dataset);
	}

	/**
	 * Get the List of all data sets 
	 * 
	 * @return all data sets
	 */
	public ArrayList<T> getDatasets() {
		return datasets;
	}

	/**
	 * Get a data set by its ID
	 * 
	 * @param id ID of the data set
	 * @return The data set
	 */
	@SuppressWarnings("unchecked")
	public T getDatasetById(int id) {
		try {
			return datasets.get(id);
		} catch (Exception e) {
			Logger.logError(e, "Fatal Error: ID " + Integer.toString(id) + " not in Dataset");
			// Return index 0 is not correct, but if index 0 exists, the system
			// is at least stable.
			// And if there is no data set - create a dummy one (DataSetText)
			if (datasets.size() > 0)
				return datasets.get(0);
			else
				return (T) new DataSetText();
		}
	}

	/**
	 * Get an array of strings with all undeleted data sets
	 * 
	 * @param key Key of the UniData value
	 * @return Array of strings
	 */
	public String[] getStrings(String key) {

		// get all undeleted data sets
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<T> undeletedDatasets = getActiveDatasets();

		// collect all Strings in a list ..
		for (T dataset : undeletedDatasets) {
			UniDataSet uds = (UniDataSet) dataset;
			list.add(uds.getStringValueByKey(key));
		}
		
		// .. and convert this list to an array
		return list.toArray(new String[0]);
	}

	/**
	 * Get an array of strings of all category strings.
	 * 
	 * If this ArraySet is an set of documents, then only the categories
	 * of the document types are returned, that are in use.
	 * e.g. If there is an type "invoice", the categories
	 * "invoice/payed" and "invoice/unpayed" are returned.  
	 * 
	 * @return Array of all category strings
	 */
	public Object[] getCategoryStrings() {
		Properties props = new Properties();
		oldProps = new Properties();
		String category;
		ArrayList<T> undeletedDatasets = getActiveDatasets();
		
		// Remember, which document types are used
		boolean usedDocuments[] = { false, false, false, false, false, false, false, false, false };

		// It's an ArraySet of documents
		if (udsTemplate instanceof DataSetDocument) {
			
			// Scan all documents and mark all used document types.
			for (T dataset : undeletedDatasets) {
				DataSetDocument document = (DataSetDocument) dataset;
				int docType = document.getIntValueByKey("category");
				if (docType >= 0 && docType <= 8) {
					usedDocuments[docType] = true;
					categoryStringsCode |= 1 << docType;
				}
			}
			
			// Get the category strings of all marked document types.
			return DataSetDocument.getCategoryStrings(usedDocuments);

		}
		// It's not an ArraySet of documents - so collect all category strings
		else {
			
			// Copy all strings to a Property object.
			// In a property object, there are no duplicate objects
			for (T dataset : undeletedDatasets) {
				UniDataSet uds = (UniDataSet) dataset;
				category = uds.getCategory();
				if ( !category.isEmpty() ) {
					oldProps.setProperty(category, category);
					props.setProperty(category, category);
				}
			}
		}

		// return the category strings
		return props.stringPropertyNames().toArray();
	}

	/**
	 * Test, if the category strings have changed
	 * 
	 * @return True, if they have changed
	 */
	public boolean getCategoryStringsChanged() {
		Properties props = new Properties();
		String category;

		// If the DataSetArray contains a set of document, 
		// test, if the code of used documents has changed. 
		if (udsTemplate instanceof DataSetDocument) {
			
			int oldCcategoryStringsCode = categoryStringsCode;
			// generate the new code
			getCategoryStrings();
			// compare the new code with the old one
			return oldCcategoryStringsCode != categoryStringsCode;
			
		} 
		// If it's not a set of documents, compare all category strings
		// This is done by filling a property object with the strings ..
		else {
			ArrayList<T> undeletedDatasets = getActiveDatasets();
			for (T dataset : undeletedDatasets) {
				UniDataSet uds = (UniDataSet) dataset;
				category = uds.getCategory();
				if (!category.isEmpty()) {
					props.setProperty(category, category);
				}
			}
		}

		// .. an testing, if all the entries are in the old Property object ..
		for (Iterator<Object> iterator = props.keySet().iterator(); iterator.hasNext();) {
			if (!oldProps.containsKey(iterator.next()))
				return true;
		}

		// .. and if alle the old entries are in the new one.
		for (Iterator<Object> iterator = oldProps.keySet().iterator(); iterator.hasNext();) {
			if (!props.containsKey(iterator.next()))
				return true;
		}
		return false;
	}

	/**
	 * Resets the memory with the old category strings
	 */
	public void resetCategoryChanged() {

		// reset the categoryStringCode, if this is a set of documents
		categoryStringsCode = 0;

		// reset the old properties for all the rest
		oldProps = new Properties();
		
	}

	/**
	 * Get a data set by a double value
	 * 
	 * @param key Key to use for the search
	 * @param value Double value to search for
	 * @return ID of the first data set with the same value (or -1, if there is nothing)
	 */
	public int getDataSetByDoubleValue(String key, Double value) {
		ArrayList<T> undeletedDatasets = getActiveDatasets();

		for (T dataset : undeletedDatasets) {
			UniDataSet uds = (UniDataSet) dataset;
			if (DataUtils.DoublesAreEqual(uds.getDoubleValueByKey(key), value)) {
				int i = ((UniDataSet) dataset).getIntValueByKey("id");
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get a data set by a string value
	 * 
	 * @param key Key to use for the search
	 * @param value String value to search for
	 * @return ID of the first data set with the same value (or -1, if there is nothing)
	 */
	public int getDataSetByStringValue(String key, String value) {
		ArrayList<T> undeletedDatasets = getActiveDatasets();

		for (T dataset : undeletedDatasets) {
			UniDataSet uds = (UniDataSet) dataset;
			if (uds.getStringValueByKey(key).equals(value)) {
				int i = ((UniDataSet) dataset).getIntValueByKey("id");
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get all active (undeleted) data sets
	 * 
	 * @return ArrayList with all undeleted data sets
	 */
	public ArrayList<T> getActiveDatasets() {
		ArrayList<T> undeletedDatasets = new ArrayList<T>();
		for (T dataset : datasets) {
			UniDataSet uds = (UniDataSet) dataset;
			if (!uds.getBooleanValueByKey("deleted")) {
				undeletedDatasets.add(dataset);
			}
		}
		return undeletedDatasets;
	}

}