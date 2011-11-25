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

package com.sebulli.fakturama.export.accounts;

import static com.sebulli.fakturama.Translate._;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import com.sebulli.fakturama.calculate.AccountSummary;
import com.sebulli.fakturama.data.DataSetAccountEntry;
import com.sebulli.fakturama.data.UniDataSetSorter;
import com.sebulli.fakturama.export.OOCalcExporter;
import com.sebulli.fakturama.misc.DataUtils;


/**
 * This class exports all vouchers in an OpenOffice.org 
 * Calc table. 
 * 
 * @author Gerd Bartelt
 */
public class Exporter extends OOCalcExporter{

	/**
	 * Constructor Sets the begin and end date
	 * 
	 * @param startDate
	 *            Begin date
	 * @param endDate
	 *            Begin date
	 */
	public Exporter(GregorianCalendar startDate, GregorianCalendar endDate,
			 boolean doNotUseTimePeriod) {
		super(startDate, endDate, doNotUseTimePeriod);
		
	}

	/**
	 * 	Do the export job.
	 * 
	 * @return
	 * 			True, if the export was successful
	 */
	public boolean export(String account) {

		// Array with all entries of one account
		ArrayList<DataSetAccountEntry> accountEntries;

		
		// Try to generate a spreadsheet
		if (!createSpreadSheet())
			return false;

		AccountSummary accountSummary = new AccountSummary();
		accountSummary.collectEntries(account);
		accountEntries = accountSummary.getAccountEntries();
		
		// Sort the vouchers by category and date
		Collections.sort(accountEntries, new UniDataSetSorter("date"));

		// Fill the first 4 rows with the company information
		fillCompanyInformation(0);
		fillTimeIntervall(5);
		
		// Counter for the current row and columns in the Calc document
		int row = 9;
		int col = 0;



		// Set the title
		setCellTextInBold(row++, 0, account);
		row++;

		//T: Used as heading of a table. Keep the word short.
		setCellTextInBold(row, col++, _("Date"));
		//T: Used as heading of a table. Keep the word short.
		setCellTextInBold(row, col++, _("Name"));
		//T: Used as heading of a table. Keep the word short.
		setCellTextInBold(row, col++, _("Text"));
		//T: Used as heading of a table. Keep the word short.
		setCellTextInBold(row, col++, _("Value"));
		//T: Used as heading of a table. Keep the word short.
		setCellTextInBold(row, col++, _("Balance"));

		row++;

		double balance = 0.0;
		
		// The vouchers are exported in 2 runs.
		// First, only the summary of all vouchers is calculated and
		// the columns are created.
		// Later all the vouchers are analyzed a second time and then they
		// are exported voucher by voucher into the table.
		for (DataSetAccountEntry accountEntry : accountEntries) {

			
			if (isInTimeIntervall(accountEntry)) {
				// Fill the row with the accountEntry data
				col = 0;

				double value = accountEntry.getDoubleValueByKey("value");
				balance += value;

				setCellText(row, col++, DataUtils.DateAsLocalString(accountEntry.getStringValueByKey("date")));
				setCellText(row, col++, accountEntry.getStringValueByKey("name"));
				setCellText(row, col++, accountEntry.getStringValueByKey("text"));
				setCellValueAsLocalCurrency(row, col++,value);
				setCellValueAsLocalCurrency(row, col++,balance);
				
				row++;
				
			}
		}

		// Draw a horizontal line
		for (col = 0; col < 4; col++) {
			setBorder(row - 1, col, 0x000000, false, false, true, false);
		}

		// True = Export was successful
		return true;
	}


}
