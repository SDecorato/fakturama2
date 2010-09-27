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

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetExpenditure;
import com.sebulli.fakturama.data.DataSetExpenditureItem;
import com.sebulli.fakturama.data.DataSetVAT;

public class CSVImporter {

	private String[] requiredHeaders = {"category",
										"date",
										"nr",
										"documentnr",
										"name",
										"item name",
										"item category",
										"item price",
										"item vat"};
	
	public CSVImporter () {
		
	}
	
	private boolean isRequiredColumn (String columnName) {
		for (int i = 0; i < requiredHeaders.length; i++) {
			if (columnName.equalsIgnoreCase(requiredHeaders[i]))
				return true;
		}
		return false;
	}
	
	private String removeQuotes(String s) {
		
		if (s.length()<2)
			return s;
		
		if (s.startsWith("\""))
			s = s.substring(1);
		
		if (s.endsWith("\""))
			s = s.substring(0, s.length()-1);
		
		return s;
	}
	
	public void importCSV (String fileName) {

		// Create a File object
		File file = new File(fileName);

		// If the log file exists read the content
		if (file.exists()) {
			
			// Open the existing file
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e1) {
				return;
			}
			String line = "";
			String[] columns;
			
			// Read the first line
			try {
				if ((line = in.readLine()) != null) {
					columns = line.split(";");
					for (int i=0; i<columns.length; i++) {
						columns[i] = removeQuotes(columns[i]);
					}
					
				}
				else {
					return;
				}
			} catch (IOException e1) {
				return;
			}
			
			// Read the existing file and store it in a buffer
			// with a fix size. Only the newest lines are kept.
			try {
				while ((line = in.readLine()) != null) {
					String[] cells = line.split(";");
					
					DataSetExpenditure expenditure = new DataSetExpenditure();
					DataSetExpenditureItem expenditureItem = new DataSetExpenditureItem();

					Properties prop = new Properties();
					
					for (int col = 0; col < cells.length; col++) {
						if (col < columns.length) {
							
							if (isRequiredColumn (columns[col]) ) {
								prop.setProperty(columns[col].toLowerCase(), removeQuotes(cells[col]));
							}
						}
					}
					
					if (!prop.getProperty("date").isEmpty()) {
						expenditureItem.setStringValueByKey("name", prop.getProperty("item name"));
						expenditureItem.setStringValueByKey("category", prop.getProperty("item category"));
						expenditureItem.setStringValueByKey("price", prop.getProperty("item price"));
						
						String vatName = prop.getProperty("item vat");
						
						Double vatValue = DataUtils.StringToDouble(vatName);
						DataSetVAT vat = new DataSetVAT(vatName, "", vatName, vatValue);
						vat = Data.INSTANCE.getVATs().addNewDataSetIfNew(vat);
						expenditureItem.setIntValueByKey("vatid", vat.getIntValueByKey("id"));
						
						expenditureItem = Data.INSTANCE.getExpenditureItems().addNewDataSet(expenditureItem);
						
						expenditure.setStringValueByKey("name", prop.getProperty("name"));
						expenditure.setStringValueByKey("category", prop.getProperty("category"));
						expenditure.setStringValueByKey("date", DataUtils.DateAsISO8601String( prop.getProperty("date")));
						expenditure.setStringValueByKey("nr", prop.getProperty("nr"));
						expenditure.setStringValueByKey("documentnr", prop.getProperty("documentnr"));
						
						expenditure = Data.INSTANCE.getExpenditures().addNewDataSetIfNew(expenditure);

						String oldItems = expenditure.getStringValueByKey("items");
						String newItem = expenditureItem.getStringValueByKey("id");
						if (!oldItems.isEmpty())
							oldItems += ",";
						
						expenditure.setStringValueByKey("items", oldItems + newItem);
						Data.INSTANCE.getExpenditures().updateDataSet(expenditure);
					}
					
				}
			} catch (IOException e) {
			}
		}

	}
}
