/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.importWizards;

import static com.sebulli.fakturama.Translate._;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.sebulli.fakturama.OSDependent;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetExpenditure;
import com.sebulli.fakturama.data.DataSetExpenditureItem;
import com.sebulli.fakturama.data.DataSetVAT;
import com.sebulli.fakturama.editors.Editor;
import com.sebulli.fakturama.editors.ExpenditureEditor;
import com.sebulli.fakturama.views.datasettable.ViewExpenditureTable;
import com.sebulli.fakturama.views.datasettable.ViewListTable;

/**
 * CSV importer
 * 
 * @author Gerd Bartelt
 */
public class CSVImporter {

	// Defines all columns that are used and imported
	private String[] requiredHeaders = { "category", "date", "nr", "documentnr", "name", "item name", "item category", "item price", "item vat" };
	// The result string
	String result = "";

	// NewLine
	String NL = "";

	/**
	 * Contstructor
	 */
	public CSVImporter() {
		// String for a new line
		NL = OSDependent.getNewLine();
	}

	/**
	 * Returns, if a column is in the list of required columns
	 * 
	 * @param columnName
	 *            The name of the columns to test
	 * @return TRUE, if this column is in the list of required columns
	 */
	private boolean isRequiredColumn(String columnName) {

		// Test all columns
		for (int i = 0; i < requiredHeaders.length; i++) {
			if (columnName.equalsIgnoreCase(requiredHeaders[i]))
				return true;
		}
		return false;
	}

	/**
	 * Removes the leading and trailing quotes of a string
	 * 
	 * @param s
	 *            Input string with quotes
	 * @return The string without quotes
	 * 
	 */
	private String removeQuotes(String s) {

		// To short
		if (s.length() < 2)
			return s;

		// Removes the leading quotes
		if (s.startsWith("\""))
			s = s.substring(1);

		// Removes the trailing quotes
		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);

		return s;
	}

	/**
	 * The import procedure
	 * 
	 * @param fileName
	 *            Name of the file to import
	 * @param test
	 *            if true, the dataset are not imported (currently not used)
	 */
	public void importCSV(final String fileName, boolean test) {

		// Result string
		//T: Importing + .. FILENAME
		result = _("Importing") + " " + fileName;

		// Count the imported expenditures
		int importedExpenditures = 0;

		// Count the line of the import file
		int lineNr = 0;

		// Create a File object
		File file = new File(fileName);

		// If the log file exists read the content
		if (file.exists()) {

			// Open the existing file
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(file));
			}
			catch (FileNotFoundException e1) {
				//T: Error message
				result += NL + _("File not found.");
				return;
			}

			String line = "";
			String[] columns;

			// Read the first line
			try {
				if ((line = in.readLine()) != null) {
					lineNr++;

					// Get the headers of the columns
					columns = line.split(";");
					for (int i = 0; i < columns.length; i++) {
						columns[i] = removeQuotes(columns[i]);
					}

				}
				else {
					return;
				}
			}
			catch (IOException e1) {
				//T: Error message
				result += NL + _("Error reading the first line");
				return;
			}

			// Read the existing file and store it in a buffer
			// with a fix size. Only the newest lines are kept.
			try {

				// Store the last expenditure. This is used to import
				// 2 lines with 2 expenditure items but only one expenditure.
				DataSetExpenditure lastExpenditure = null;

				// Read line by line
				while ((line = in.readLine()) != null) {
					lineNr++;

					DataSetExpenditure expenditure = new DataSetExpenditure();
					DataSetExpenditureItem expenditureItem = new DataSetExpenditureItem();
					Properties prop = new Properties();

					// Get the cells
					String[] cells = line.split(";");

					// Dispatch all the cells into a property
					for (int col = 0; col < cells.length; col++) {
						if (col < columns.length) {

							if (isRequiredColumn(columns[col])) {
								prop.setProperty(columns[col].toLowerCase(), removeQuotes(cells[col]));
							}
						}
					}

					// Test, if all columns are used
					if ((prop.size() > 0) && (prop.size() != requiredHeaders.length)) {
						for (int i = 0; i < requiredHeaders.length; i++) {
							if (!prop.containsKey(requiredHeaders[i]))
								//T: Format: LINE: xx: NO DATA IN COLUMN yy FOUND.
								result += NL + _("Line") + ": " + Integer.toString(lineNr) + ": " + 
								//T: Format: LINE: xx: NO DATA IN COLUMN yy FOUND.
								_("No Data in Column") + " \"" + requiredHeaders[i] + "\" " + 
								//T: Format: LINE: xx: NO DATA IN COLUMN yy FOUND.
								_("found.");
						}
					}
					else {

						// Date is a must.
						if (!prop.getProperty("date").isEmpty()) {

							// Fill the expenditure data set
							expenditure.setStringValueByKey("name", prop.getProperty("name"));
							expenditure.setStringValueByKey("category", prop.getProperty("category"));
							expenditure.setStringValueByKey("date", DataUtils.DateAsISO8601String(prop.getProperty("date")));
							expenditure.setStringValueByKey("nr", prop.getProperty("nr"));
							expenditure.setStringValueByKey("documentnr", prop.getProperty("documentnr"));

							// Test, if the last line was the same expenditure
							boolean repeatedExpenditure = false;

							if (lastExpenditure != null)
								if (lastExpenditure.isTheSameAs(expenditure))
									repeatedExpenditure = true;

							// If the data set is already existing, stop the CSV import
							if (!repeatedExpenditure)
								if (!Data.INSTANCE.getExpenditures().isNew(expenditure)) {
									//T: Error message Dataset is already imported
									result += NL + _("Dataset is already imported");
									//T: Error message: DATASET xx FROM date
									result += NL + _("Dataset")+ " " + prop.getProperty("name") + " " +
										//T: DATASET xx FROM date
										_("from", "DATE") + " " + prop.getProperty("date");
									//T: Error message Import stopped
									result += NL + _("Import stopped");
									break;
								}

							// Fill the expenditure item with data
							expenditureItem.setStringValueByKey("name", prop.getProperty("item name"));
							expenditureItem.setStringValueByKey("category", prop.getProperty("item category"));
							expenditureItem.setStringValueByKey("price", prop.getProperty("item price"));

							String vatName = prop.getProperty("item vat");

							Double vatValue = DataUtils.StringToDouble(vatName);
							DataSetVAT vat = new DataSetVAT(vatName, DataSetVAT.getPurchaseTaxString(), vatName, vatValue);
							vat = Data.INSTANCE.getVATs().addNewDataSetIfNew(vat);
							expenditureItem.setIntValueByKey("vatid", vat.getIntValueByKey("id"));

							// Add the expenditure and expenditure item to the data base
							expenditureItem = Data.INSTANCE.getExpenditureItems().addNewDataSet(expenditureItem);
							ExpenditureEditor.updateBillingAccount (expenditureItem);
							expenditure = Data.INSTANCE.getExpenditures().addNewDataSetIfNew(expenditure);

							// Add the item to the item string
							String oldItems = expenditure.getStringValueByKey("items");
							String newItem = expenditureItem.getStringValueByKey("id");
							if (!oldItems.isEmpty())
								oldItems += ",";
							else {
								importedExpenditures++;
							}
							expenditure.setStringValueByKey("items", oldItems + newItem);

							Data.INSTANCE.getExpenditures().updateDataSet(expenditure);

							// Set the reference of the last expenditure to this one
							lastExpenditure = expenditure;
						}

					}

				}
				
				// Refresh the views
				Editor.refreshView(ViewListTable.ID);
				Editor.refreshView(ViewExpenditureTable.ID);
				
				// The result string
				//T: Message: xx VOUCHERS HAVE BEEN IMPORTED 
				result += NL + Integer.toString(importedExpenditures) + " " + _("Vouchers have been imported.");

			}
			catch (IOException e) {
				//T: Error message
				result += NL + _("Error opening the file.");
			}
		}

	}

	public String getResult() {
		return result;
	}

}
