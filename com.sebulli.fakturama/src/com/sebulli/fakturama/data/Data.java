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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Workspace;
import com.sebulli.fakturama.logger.Logger;

/**
 * Contains the data model
 * 
 * @author Gerd Bartelt
 */
public enum Data {
	INSTANCE;
	
	// Data Model
	private DataSetArray<DataSetProperty> properties;
	private DataSetArray<DataSetProduct> products;
	private DataSetArray<DataSetContact> contacts;
	private DataSetArray<DataSetVAT> vats;
	private DataSetArray<DataSetItem> items;
	private DataSetArray<DataSetDocument> documents;
	private DataSetArray<DataSetShipping> shippings;
	private DataSetArray<DataSetPayment> payments;
	private DataSetArray<DataSetText> texts;
	
	// Reference to data base
	DataBase db = null;
	
	// True, if a new data base was created
	boolean newDBcreated = false;
	
	/**
	 * Constructor
	 * Connect to the data base and
	 * copy the data from the data base
	 */
	Data() {
		// connect to the data base
		this.db = new DataBase();
		
		// Get the workspace
		Workspace.INSTANCE.initWorkspace();
		String workspace = Workspace.INSTANCE.getWorkspace();
		
		// do not try to create a data base, if the workspace is not set.
		if (!workspace.isEmpty())
			newDBcreated = this.db.connect(workspace);
		
		// If there is a connection to the data base
		// read all the tables
		if (this.db.isConnected()) {
			properties = new DataSetArray<DataSetProperty>(db, new DataSetProperty());
			products = new DataSetArray<DataSetProduct>(db, new DataSetProduct());
			contacts = new DataSetArray<DataSetContact>(db, new DataSetContact());
			vats = new DataSetArray<DataSetVAT>(db, new DataSetVAT());
			documents = new DataSetArray<DataSetDocument>(db, new DataSetDocument());
			items = new DataSetArray<DataSetItem>(db, new DataSetItem());
			shippings = new DataSetArray<DataSetShipping>(db, new DataSetShipping());
			payments = new DataSetArray<DataSetPayment>(db, new DataSetPayment());
			texts = new DataSetArray<DataSetText>(db, new DataSetText());

			// If the data base is new, create some default entries
			if (newDBcreated)
				fillWithInitialData();

			// Set the data base as connected
			DataBaseConnectionState.INSTANCE.setConnected();

		} 
		// No connection, so create empty data sets
		else {

			properties = new DataSetArray<DataSetProperty>(null, new DataSetProperty());
			products = new DataSetArray<DataSetProduct>(null, new DataSetProduct());
			contacts = new DataSetArray<DataSetContact>(null, new DataSetContact());
			vats = new DataSetArray<DataSetVAT>(null, new DataSetVAT());
			documents = new DataSetArray<DataSetDocument>(null, new DataSetDocument());
			items = new DataSetArray<DataSetItem>(null, new DataSetItem());
			shippings = new DataSetArray<DataSetShipping>(null, new DataSetShipping());
			payments = new DataSetArray<DataSetPayment>(null, new DataSetPayment());
			texts = new DataSetArray<DataSetText>(null, new DataSetText());

			// Display a warning
			if (!workspace.isEmpty()) {
				MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION);
				messageBox.setText("Hinweis");
				messageBox.setMessage("Keine Verbindung zur Datenbank möglich.\n\n" +
									  "Ist Datenbank von einem anderen Prozess geöffnet ?");
				messageBox.open();
			}
		}
	}

	/**
	 * If a new data base was created, fill some data with initial values
	 */
	public void fillWithInitialData() {

		// Fill some UniDataSets
		vats.addNewDataSet(new DataSetVAT("keine", "", "keine MwSt.", 0.0));
		shippings.addNewDataSet(new DataSetShipping("frei", "", "frei Haus", 0.0, 0, 1));
		payments.addNewDataSet(new DataSetPayment("sofort", "", "sofort oder Vorkasse",0.0, 0, 0,"Zahlung dankend erhalten.","Zahlbar: sofort", false));
		
		// Set the dafault value to this entries
		setProperty("standardvat", "0");
		setProperty("standardshipping", "0");
		setProperty("standardpayment", "0");
	}

	/**
	 * Close the data base
	 */
	public void close() {
		if (db != null)
			db.close();
	}

	/**
	 * Test if a new data base was created
	 * 
	 * @return True, if a new data base was created
	 */
	public boolean getNewDBCreated() {
		return newDBcreated;
	}

	/**
	 * Returns, if the property exists.
	 * 
	 * @param key Property key
	 * @return Value as String
	 */
	public boolean isExistingProperty(String key) {
		for (DataSetProperty property : properties.getDatasets()) {
			if (property.getStringValueByKey("name").equalsIgnoreCase(key))
				return true;
		}
		
		return false;
	}

	/**
	 * Get a property value
	 * 
	 * @param key Property key
	 * @return Value as String
	 */
	public String getProperty(String key) {
		for (DataSetProperty property : properties.getDatasets()) {
			if (property.getStringValueByKey("name").equalsIgnoreCase(key))
				return property.getStringValueByKey("value");
		}
		Logger.logError("Key " + key + " not in property list");
		return "";
	}

	/**
	 * Get a property value as integer
	 * 
	 * @param key Property key
	 * @return Value as integer
	 */
	public int getPropertyAsInt(String key) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Set a property value
	 * 
	 * @param key Property key
	 * @param value Property value
	 */
	public void setProperty(String key, String value) {
		
		// Set an existing property entry
		for (DataSetProperty property : properties.getDatasets()) {
			if (property.getStringValueByKey("name").equalsIgnoreCase(key)) {
				property.setStringValueByKey("value", value);
				properties.updateDataSet(property);
				return;
			}
		}
		
		// Add a new one, if it is not yet existing
		properties.addNewDataSet(new DataSetProperty(key, value));
		Logger.logInfo("New property " + key + " added");
	}

	/**
	 * Getter for the DataSetArray products
	 * 
	 * @return All products
	 */
	public DataSetArray<DataSetProduct> getProducts() {
		return products;
	}

	/**
	 * Getter for the DataSetArray contacts
	 * 
	 * @return All contacts
	 */
	public DataSetArray<DataSetContact> getContacts() {
		return contacts;
	}

	/**
	 * Getter for the DataSetArray vats
	 * 
	 * @return All vats
	 */
	public DataSetArray<DataSetVAT> getVATs() {
		return vats;
	}

	/**
	 * Getter for the DataSetArray documents
	 * 
	 * @return All documents
	 */
	public DataSetArray<DataSetDocument> getDocuments() {
		return documents;
	}

	/**
	 * Getter for the DataSetArray items
	 * 
	 * @return All items
	 */
	public DataSetArray<DataSetItem> getItems() {
		return items;
	}

	/**
	 * Getter for the DataSetArray shippings
	 * 
	 * @return All shippings
	 */
	public DataSetArray<DataSetShipping> getShippings() {
		return shippings;
	}

	/**
	 * Getter for the DataSetArray payments
	 * 
	 * @return All payments
	 */
	public DataSetArray<DataSetPayment> getPayments() {
		return payments;
	}

	/**
	 * Getter for the DataSetArray texts
	 * 
	 * @return All texts
	 */
	public DataSetArray<DataSetText> getTexts() {
		return texts;
	}

	/**
	 * Get a UniDataSet value by table Name and ID.
	 * 
	 * @param tableName Table name
	 * @param id ID of the table entry
	 * @return The UniDataSet
	 */
	public UniDataSet getUniDataSetByTableNameAndId(String tableName, int id) {
		try {
			if (tableName.equalsIgnoreCase("products")) { return getProducts().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("vats")) { return getVATs().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("contacts")) { return getContacts().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("documents")) { return getDocuments().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("items")) { return getItems().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("shippings")) { return getShippings().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("payments")) { return getPayments().getDatasetById(id); }
			if (tableName.equalsIgnoreCase("texts")) { return getTexts().getDatasetById(id); }
		} catch (IndexOutOfBoundsException e) {
			Logger.logError(e, "Index out of bounds: " + "TableName: " + tableName + " ID:" + Integer.toString(id));
		}
		
		// not found
		return null;
	}

	/**
	 * Update the data base with the new value
	 * 
	 * @param uds UniDataSet to update
	 */
	public void updateDataSet(UniDataSet uds) {
		db.updateUniDataSet(uds);
	}

}
