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

package com.sebulli.fakturama.calculate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.logger.Logger;

public class CustomerStatistics {

	private boolean isRegularCustomer = false;
	private Integer ordersCount = 0;
	private GregorianCalendar lastOrderDate = null;
	private Double total = 0.0;
	
	public CustomerStatistics (int contactID) {
		
		if (contactID < 0)
			return;

		// Get all undeleted documents
		ArrayList<DataSetDocument> documents = Data.INSTANCE.getDocuments().getActiveDatasets();

		// Export the document data
		for (DataSetDocument document : documents) {

			// Only paid invoiced from this customer will be used for the statistics
			if ( (document.getIntValueByKey("category") == DocumentType.INVOICE.getInt())
					 && document.getBooleanValueByKey("paid") && 
					 document.getIntValueByKey("addressid") == contactID ) {
				
				// It's a regular customer
				isRegularCustomer = true;

				// Increment the count of orders
				ordersCount ++;
				
				// Increase the total
				total += document.getDoubleValueByKey("payvalue");
				
				// Get the date of the document and convert it to a
				// GregorianCalendar object.
				GregorianCalendar documentDate = new GregorianCalendar();
				try {
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

					String expenditureDateString = "";

					// Use date 
					expenditureDateString = document.getStringValueByKey("orderdate");

					documentDate.setTime(formatter.parse(expenditureDateString));
				}
				catch (ParseException e) {
					Logger.logError(e, "Error parsing Date");
				}

				// Set the last order date
				if (lastOrderDate == null) {
					lastOrderDate = documentDate;
				} else {
					documentDate.after(lastOrderDate);
					lastOrderDate = documentDate;
				}
				
			}
		}

	}
	
	public boolean isRegularCustomer() {
		return isRegularCustomer;
	}
	
	public Integer getOrdersCount () {
		return ordersCount;
	}
	
	public Double getTotal () {
		return total;
	}
	
	public String getLastOrderDate() {
		return  DataUtils.getDateTimeAsLocalString(lastOrderDate);
	}
}
