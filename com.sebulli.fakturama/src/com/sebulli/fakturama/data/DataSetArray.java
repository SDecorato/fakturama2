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

public class DataSetArray<T> {
	private ArrayList<T> datasets = new ArrayList<T>();
	private DataBase db;
	private Properties oldProps = new Properties();
	private UniDataSet udsTemplate;
	private int categoryStringsCode = 0;

	public DataSetArray() {
	}

	public DataSetArray(DataBase db, UniDataSet udsTemplate) {
		this.db = db;
		if (this.db != null)
			this.db.getTable(datasets, udsTemplate);
		this.udsTemplate = udsTemplate;
	}

	public UniDataSet getTemplate() {
		return this.udsTemplate;
	}

	public int getNextFreeId() {
		int maxId = -1;
		for (T dataset : datasets) {
			if (maxId < ((UniDataSet) dataset).getIntValueByKey("id")) {
				maxId = ((UniDataSet) dataset).getIntValueByKey("id");
			}
		}
		return maxId + 1;
	}

	public T addNewDataSet(T dataset) {
		((UniDataSet) dataset).setIntValueByKey("id", getNextFreeId());
		datasets.add(dataset);
		if (this.db != null)
			this.db.insertUniDataSet((UniDataSet) dataset);
		return datasets.get(datasets.size() - 1);
	}

	public T addNewDataSetIfNew(T dataset) {
		ArrayList<T> undeletedDatasets = getActiveDatasets();
		for (T testdataset : undeletedDatasets) {
			if (((UniDataSet) testdataset).isTheSameAs((UniDataSet) dataset)) { return testdataset; }
		}

		((UniDataSet) dataset).setIntValueByKey("id", getNextFreeId());
		datasets.add(dataset);
		if (this.db != null)
			this.db.insertUniDataSet((UniDataSet) dataset);
		return datasets.get(datasets.size() - 1);
	}

	public boolean isNew(T dataset) {
		ArrayList<T> undeletedDatasets = getActiveDatasets();
		for (T testdataset : undeletedDatasets) {
			if (((UniDataSet) testdataset).isTheSameAs((UniDataSet) dataset)) { return false; }
		}
		return true;
	}

	public void addNewDataSetKeepId(T dataset) {
		datasets.add(dataset);
	}

	public void updateDataSet(T dataset) {
		if (this.db != null)
			db.updateUniDataSet((UniDataSet) dataset);
	}

	public ArrayList<T> getDatasets() {
		return datasets;
	}

	@SuppressWarnings("unchecked")
	public T getDatasetById(int id) {
		try {
			return datasets.get(id);
		} catch (Exception e) {
			Logger.logError(e, "Fatal Error: ID " + Integer.toString(id) + " not in Dataset");
			// Return index 0 is not correct, but if index 0 exists, the system
			// is at least stable.
			// And if there is no dataset - create a dummy one (DataSetText)
			if (datasets.size() > 0)
				return datasets.get(0);
			else
				return (T) new DataSetText();
		}
	}

	public String[] getStrings(String key) {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<T> undeletedDatasets = getActiveDatasets();

		for (T dataset : undeletedDatasets) {
			UniDataSet uds = (UniDataSet) dataset;
			list.add(uds.getStringValueByKey(key));
		}
		return list.toArray(new String[0]);
	}

	public Object[] getCategoryStrings() {
		Properties props = new Properties();
		oldProps = new Properties();
		String category;
		ArrayList<T> undeletedDatasets = getActiveDatasets();
		boolean usedDocuments[] = { false, false, false, false, false, false, false, false, false };

		if (udsTemplate instanceof DataSetDocument) {
			for (T dataset : undeletedDatasets) {
				DataSetDocument document = (DataSetDocument) dataset;
				int docType = document.getIntValueByKey("category");
				if (docType >= 0 && docType <= 8) {
					usedDocuments[docType] = true;
					categoryStringsCode |= 1 << docType;
				}
			}
			return DataSetDocument.getCategoryStrings(usedDocuments);

		} else {
			for (T dataset : undeletedDatasets) {
				UniDataSet uds = (UniDataSet) dataset;
				category = uds.getCategory();
				if (!category.isEmpty()) {
					oldProps.setProperty(category, category);
					props.setProperty(category, category);
				}
			}
		}

		return props.stringPropertyNames().toArray();
	}

	public boolean getCategoryStringsChanged() {
		Properties props = new Properties();
		String category;

		if (udsTemplate instanceof DataSetDocument) {
			/*
			 * boolean retval = categoryChanged; categoryChanged = false; return
			 * retval;
			 */
			int oldCcategoryStringsCode = categoryStringsCode;
			getCategoryStrings();
			boolean retval = oldCcategoryStringsCode != categoryStringsCode;
			return retval;
		} else {
			ArrayList<T> undeletedDatasets = getActiveDatasets();
			for (T dataset : undeletedDatasets) {
				UniDataSet uds = (UniDataSet) dataset;
				category = uds.getCategory();
				if (!category.isEmpty()) {
					props.setProperty(category, category);
				}
			}
		}

		for (Iterator<Object> iterator = props.keySet().iterator(); iterator.hasNext();) {
			if (!oldProps.containsKey(iterator.next()))
				return true;
		}

		for (Iterator<Object> iterator = oldProps.keySet().iterator(); iterator.hasNext();) {
			if (!props.containsKey(iterator.next()))
				return true;
		}
		return false;
	}

	public void resetCategoryChanged() {
		oldProps = new Properties();
		categoryStringsCode = 0;
	}

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
