/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.export.csv.products;

import static com.sebulli.fakturama.Translate._;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sebulli.fakturama.OSDependent;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetProduct;


/**
 * This class generates a list with all products
 * 
 * @author Gerd Bartelt
 */
public class Exporter {
	
	/**
	 * Constructor
	 * 
	 */
	public Exporter() {
		super();
	}

	/**
	 * 	Do the export job.
	 * 
	 * @param filename
	 * 			The name of the export file
	 * @return
	 * 			True, if the export was successful
	 */
	public boolean export(String filename) {

		String NEW_LINE = OSDependent.getNewLine();
		
		// Create a File object
		File csvFile = new File(filename);
		BufferedWriter bos = null;
		// Create a new file
		try {
			csvFile.createNewFile();
			bos = new BufferedWriter(new FileWriter(csvFile, false));
			bos.write(
					//T: Used as heading of a table. Keep the word short.
					"\""+ "ID" + "\";"+ 
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Item Number") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Name") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Category") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Description") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Price")+ "(1)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Price")+ "(2)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Price")+ "(3)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Price")+ "(4)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Price")+ "(5)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Quantity")+ "(1)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Quantity")+ "(2)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Quantity")+ "(3)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Quantity")+ "(4)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Quantity")+ "(5)" + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("VAT") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Options") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Weight (kg)") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Unit") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Date") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Product Picture") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Quantity") + "\";"+
					//T: Used as heading of a table. Keep the word short.
					"\""+ _("Web Shop") + "\""+
					NEW_LINE);

		
		
			// Get all undeleted products
			ArrayList<DataSetProduct> products = Data.INSTANCE.getProducts().getActiveDatasets();
			
			// Export the product data
			for (DataSetProduct product : products) {
				
				
				// Place the products information into the table
				bos.write(
						product.getStringValueByKey("id")+ ";" +
						"\"" + product.getStringValueByKey("itemnr")+ "\";" +
						"\"" + product.getStringValueByKey("name")+ "\";" +
						"\"" + product.getStringValueByKey("category")+ "\";" +
						"\"" + product.getStringValueByKey("description")+ "\";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("price1"),"0.00")+ ";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("price2"),"0.00")+ ";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("price3"),"0.00")+ ";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("price4"),"0.00")+ ";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("price5"),"0.00")+ ";" +
						product.getStringValueByKey("block1")+ ";" +
						product.getStringValueByKey("block2")+ ";" +
						product.getStringValueByKey("block3")+ ";" +
						product.getStringValueByKey("block4")+ ";" +
						product.getStringValueByKey("block5")+ ";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKeyFromOtherTable("vatid.VATS:value"),"0.00")+ ";" +
						"\"" + product.getStringValueByKey("options")+ "\";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("weight"),"0.00")+ ";" +
						product.getStringValueByKey("unit")+ ";" +
						"\"" + product.getStringValueByKey("date_added")+ "\";" +
						"\"" + product.getStringValueByKey("picturename")+ "\";" +
						DataUtils.DoubleToDecimalFormatedValue(product.getDoubleValueByKey("quantity"),"0.00")+ ";" +
						product.getStringValueByKey("webshopid")+ "" +
						NEW_LINE);
			}
		
		}
		catch (IOException e) {
			return false;
		}

		try {
			if (bos!= null)
				bos.close();
		}
		catch (Exception e) {}

		// True = Export was successful
		return true;
	}

}
