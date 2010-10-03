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

package com.sebulli.fakturama.importWizards;

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

/**
 * CSV importer
 * 
 * @author Gerd Bartelt
 */
public class CSVImporter {

	// Defines all columns that are used and imported
	private String[] requiredHeaders = {"category",
										"date",
										"nr",
										"documentnr",
										"name",
										"item name",
										"item category",
										"item price",
										"item vat"};
	// The result string
	String result = "";
	
	// NewLine
	String NL = "";
	
	/**
	 * Contstructor
	 */
	public CSVImporter () {
		// String for a new line
		NL = OSDependent.getNewLine();
	}
	
	/**
	 * Returns, if a column is in the list of required columns
	 * 
	 * @param columnName The name of the columns to test
	 * @return TRUE, if this column is in the list of required columns
	 */
	private boolean isRequiredColumn (String columnName) {
		
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
	 * @param s Input string with quotes
	 * @return The string without quotes
	 * 
	 */
	private String removeQuotes(String s) {
		
		// To short
		if (s.length()<2)
			return s;
		
		// Removes the leading quotes
		if (s.startsWith("\""))
			s = s.substring(1);
		
		// Removes the trailing quotes
		if (s.endsWith("\""))
			s = s.substring(0, s.length()-1);
		
		return s;
	}
	
	/**
	 * The import procedure
	 * 
	 * @param fileName Name of the file to import
	 * @param test if true, the dataset are not imported (currently not used)
	 */
	public void importCSV (final String fileName, boolean test ) {

		// Result string
		result = "Importiere " + fileName;
		
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
			} catch (FileNotFoundException e1) {
				result += NL + "Datei nicht gefunden.";
				return;
			}
			
			
			String line = "";
			String[] columns;
			
			// Read the first line
			try {
				if ((line = in.readLine()) != null) {
					lineNr ++;
					
					// Get the headers of the columns
					columns = line.split(";");
					for (int i=0; i<columns.length; i++) {
						columns[i] = removeQuotes(columns[i]);
					}
					
				}
				else {
					return;
				}
			} catch (IOException e1) {
				result += NL + "Fehler beim Lesen der ersten Zeile";
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
					lineNr ++;

					DataSetExpenditure expenditure = new DataSetExpenditure();
					DataSetExpenditureItem expenditureItem = new DataSetExpenditureItem();
					Properties prop = new Properties();
					
					// Get the cells
					String[] cells = line.split(";");
					
					// Dispatch all the cells into a property
					for (int col = 0; col < cells.length; col++) {
						if (col < columns.length) {
							
							if (isRequiredColumn (columns[col]) ) {
								prop.setProperty(columns[col].toLowerCase(), removeQuotes(cells[col]));
							}
						}
					}
					
					// Test, if all columns are used
					if ( (prop.size() > 0) && (prop.size() != requiredHeaders.length) ) {
						for (int i = 0; i < requiredHeaders.length; i ++) {
							if (!prop.containsKey(requiredHeaders[i]))
								result += NL + "Zeile: "+ Integer.toString(lineNr)+": Keine Daten in Spalte \"" + requiredHeaders[i] + "\" gefunden.";
						}
					}
					else {
						
						// Date is a must.
						if (!prop.getProperty("date").isEmpty()) {
							
							// Fill the expenditure data set
							expenditure.setStringValueByKey("name", prop.getProperty("name"));
							expenditure.setStringValueByKey("category", prop.getProperty("category"));
							expenditure.setStringValueByKey("date", DataUtils.DateAsISO8601String( prop.getProperty("date")));
							expenditure.setStringValueByKey("nr", prop.getProperty("nr"));
							expenditure.setStringValueByKey("documentnr", prop.getProperty("documentnr"));
							
							// Test, if the last line was the same expenditure
							boolean repeatedExpenditure = false;
							
							if (lastExpenditure != null) 
								if (lastExpenditure.isTheSameAs(expenditure))
									repeatedExpenditure = true;
							
							// If the data set is already existing, stop the CSV import
							if (!repeatedExpenditure)
								if (!Data.INSTANCE.getExpenditures().isNew(expenditure) ) {
									result += NL + "Fehler: Datensatz wurde bereits importiert" ;
									result += NL + "Datensatz " + prop.getProperty("name") + " vom " + prop.getProperty("date") ;
									result += NL + "Import wird abgebrochen" ;
									break;
								}
							
							// Fill the expenditure item with data
							expenditureItem.setStringValueByKey("name", prop.getProperty("item name"));
							expenditureItem.setStringValueByKey("category", prop.getProperty("item category"));
							expenditureItem.setStringValueByKey("price", prop.getProperty("item price"));
							
							String vatName = prop.getProperty("item vat");
							
							Double vatValue = DataUtils.StringToDouble(vatName);
							DataSetVAT vat = new DataSetVAT(vatName, "Vorsteuer", vatName, vatValue);
							vat = Data.INSTANCE.getVATs().addNewDataSetIfNew(vat);
							expenditureItem.setIntValueByKey("vatid", vat.getIntValueByKey("id"));
							
							// Add the expenditure and expenditure item to the data base
							expenditureItem = Data.INSTANCE.getExpenditureItems().addNewDataSet(expenditureItem);
							expenditure = Data.INSTANCE.getExpenditures().addNewDataSetIfNew(expenditure);

							// Add the item to the item string
							String oldItems = expenditure.getStringValueByKey("items");
							String newItem = expenditureItem.getStringValueByKey("id");
							if (!oldItems.isEmpty())
								oldItems += ",";
							else {
								importedExpenditures ++;
							}
							expenditure.setStringValueByKey("items", oldItems + newItem);

							Data.INSTANCE.getExpenditures().updateDataSet(expenditure);

							// Set the reference of the last expenditure to this one
							lastExpenditure = expenditure;
						}
						
					}

				}
				
				// The result string
				result += NL + Integer.toString(importedExpenditures) + " Belege wurden importiert.";

			} catch (IOException e) {
				result += NL + "Fehler beim Dateizugriff";
			}
		}
        
		
	}
	
	public String getResult () {
		return result;
	}
	
}
