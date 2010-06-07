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

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.preferences.ProjectSettings;

public enum Data {
	INSTANCE;
	private String workingDirectory = "";
	private DataSetArray<DataSetProperty> properties;
	private DataSetArray<DataSetProduct> products;
	private DataSetArray<DataSetContact> contacts;
	private DataSetArray<DataSetVAT> vats;
	private DataSetArray<DataSetItem> items;
	private DataSetArray<DataSetDocument> documents;
	private DataSetArray<DataSetShipping> shippings;
	private DataSetArray<DataSetPayment> payments;
	private DataSetArray<DataSetText> texts;
	DataBase db = null;
	boolean newDBcreated = false;
	boolean dataBaseOpened = false;

	Data() {
		workingDirectory = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");
		ProjectSettings.showWorkingDirInTitleBar();

		this.db = new DataBase();
		newDBcreated = this.db.connect(workingDirectory);

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

			if (newDBcreated)
				fillWithInitialData();

			dataBaseOpened = true;
			ProjectSettings.SETTINGS.setDataBaseOpened();
		} else {

			properties = new DataSetArray<DataSetProperty>(null, new DataSetProperty());
			products = new DataSetArray<DataSetProduct>(null, new DataSetProduct());
			contacts = new DataSetArray<DataSetContact>(null, new DataSetContact());
			vats = new DataSetArray<DataSetVAT>(null, new DataSetVAT());
			documents = new DataSetArray<DataSetDocument>(null, new DataSetDocument());
			items = new DataSetArray<DataSetItem>(null, new DataSetItem());
			shippings = new DataSetArray<DataSetShipping>(null, new DataSetShipping());
			payments = new DataSetArray<DataSetPayment>(null, new DataSetPayment());
			texts = new DataSetArray<DataSetText>(null, new DataSetText());

			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION);
			messageBox.setText("Hinweis");
			messageBox.setMessage("Keine Verbindung zur Datenbank möglich.");
			messageBox.open();

			// PlatformUI.getWorkbench().close();

		}
	}

	public void fillWithInitialData() {

		products.addNewDataSet(new DataSetProduct("Hase", "Hase", "", "Ich bin ein Hase", 42.20, 1, "", ""));
		products.addNewDataSet(new DataSetProduct("Igel", "Igel", "", "Ich bin ein Igel", 95.10, 2, "", ""));
		products.addNewDataSet(new DataSetProduct("Katze", "Katze", "", "Ich bin eine Katze", 9995.0, 2, "", ""));

		contacts.addNewDataSet(new DataSetContact(false, "", "Max", "Mustermann", "Maxweg 1", "12345", "Maxau"));
		contacts.addNewDataSet(new DataSetContact(false, "", "Julia", "Katze", "Katzweg 2", "23456", "Katzendorf"));
		contacts.addNewDataSet(new DataSetContact(false, "", "Romeo", "Kater", "Katergasse 3", "34567", "Katerhausen"));

		vats.addNewDataSet(new DataSetVAT("keine", "", "keine MwSt.", 0.0));
		vats.addNewDataSet(new DataSetVAT("MwSt.", "", "19% MwSt. Deutschland", 0.19));
		vats.addNewDataSet(new DataSetVAT("erm. MwSt", "", "7% MwSt. ermäßigt", 0.073));

		items.addNewDataSet(new DataSetItem("etwas", "etwas", "", 1.0, "das ist etwas ", 42.0, 0));
		items.addNewDataSet(new DataSetItem("nochwas", "nochwas", "", 2.5, "das ist nochwas ", 99.50, 0));

		documents.addNewDataSet(new DataSetDocument(0, "Max Mustermann", DocumentType.INVOICE));
		documents.addNewDataSet(new DataSetDocument(1, "Juuuulia", DocumentType.INVOICE));
		documents.getDatasetById(1).setBooleanValueByKey("payed", true);
		this.updateDataSet(documents.getDatasetById(1));

		shippings.addNewDataSet(new DataSetShipping("frei", "", "frei Haus", 0.0, 0, 1));
		shippings.addNewDataSet(new DataSetShipping("Post", "", "Post Paket versichert", 6.90, 1, 1));

		payments.addNewDataSet(new DataSetPayment("sofort", "", "sofort oder Vorkasse", 0, false));
		payments.addNewDataSet(new DataSetPayment("30T", "", "30 Tage netto", 30, false));
		payments.addNewDataSet(new DataSetPayment("15T/30T", "", "15 Tage 3%, 30 Tage netto", 0.03, 15, 30, false));

		texts.addNewDataSet(new DataSetText("Hallo", "Kat", "Ich bin ein Text"));

		setProperty("standardvat", "0");
		setProperty("standardshipping", "0");
		setProperty("standardpayment", "0");
		setProperty("standarddiscount", "0");
	}

	public void close() {
		if (db != null)
			db.close();
	}

	public boolean getNewDBCreated() {
		return newDBcreated;
	}

	public boolean getDataBaseOpened() {
		return dataBaseOpened;
	}

	public String getProperty(String key) {
		for (DataSetProperty property : properties.getDatasets()) {
			if (property.getStringValueByKey("name").equalsIgnoreCase(key))
				return property.getStringValueByKey("value");
		}
		Logger.logError("Key " + key + " not in property list");
		return "";
	}

	public int getPropertyAsInt(String key) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public void setProperty(String key, String value) {
		for (DataSetProperty property : properties.getDatasets()) {
			if (property.getStringValueByKey("name").equalsIgnoreCase(key)) {
				property.setStringValueByKey("value", value);
				properties.updateDataSet(property);
				return;
			}
		}
		properties.addNewDataSet(new DataSetProperty(key, value));
		Logger.logInfo("New property " + key + " added");
	}

	public void setPreferenceValue(String key, String value) {
		for (DataSetProperty preferenceValue : properties.getDatasets()) {
			if (preferenceValue.getStringValueByKey("name").equalsIgnoreCase(key)) {
				preferenceValue.setStringValueByKey("value", value);
				properties.updateDataSet(preferenceValue);
				return;
			}
		}
		properties.addNewDataSet(new DataSetProperty(key, value));
		Logger.logInfo("New preference " + key + " added");
	}

	public String getPreferenceValue(String key) {
		for (DataSetProperty preferenceValue : properties.getDatasets()) {
			if (preferenceValue.getStringValueByKey("name").equalsIgnoreCase(key))
				return preferenceValue.getStringValueByKey("value");
		}
		Logger.logInfo("Key " + key + " not in preference list");
		return "";
	}

	public DataSetArray<DataSetProduct> getProducts() {
		return products;
	}

	public DataSetArray<DataSetContact> getContacts() {
		return contacts;
	}

	public DataSetArray<DataSetVAT> getVATs() {
		return vats;
	}

	public DataSetArray<DataSetDocument> getDocuments() {
		return documents;
	}

	public DataSetArray<DataSetItem> getItems() {
		return items;
	}

	public DataSetArray<DataSetShipping> getShippings() {
		return shippings;
	}

	public DataSetArray<DataSetPayment> getPayments() {
		return payments;
	}

	public DataSetArray<DataSetText> getTexts() {
		return texts;
	}

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
		return null;

	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void updateDataSet(UniDataSet uds) {
		db.updateUniDataSet(uds);
	}

}
