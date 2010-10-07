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

package com.sebulli.fakturama.calculate;

import com.sebulli.fakturama.data.DataSetExpenditure;

/**
 * Stores one VatSummarySet object and provides some methods 
 * e.g. to add an UniDataSet document
 * 
 * @author Gerd Bartelt
 */
public class ExpenditureSummarySetManager {
	VatSummarySet expenditureSummarySet;

	/**
	 * Constructor
	 * Creates a new ExpenditureSummarySet
	 */
	public ExpenditureSummarySetManager() {
		expenditureSummarySet = new VatSummarySet();
	}

	/**
	 * Add an expenditure to the ExpenditureSummarySet
	 * 
	 * @param document Document to add
	 * @param useCategory If true, the category is also used for the vat summary as a description
	 */
	public void add(DataSetExpenditure expenditure, boolean useCategory) {

		// Create a new summary object and start the calculation.
		// This will add all the entries to the VatSummarySet
		ExpenditureSummary summary = new ExpenditureSummary();
		summary.calculate(expenditureSummarySet, expenditure.getItems(), useCategory);
	}

	/**
	 * Getter for the expenditureSummarySet
	 * 
	 * @return The expenditureSummarySet
	 */
	public VatSummarySet getExpenditureSummaryItems() {
		return expenditureSummarySet;
	}

	/**
	 * Get the size of the 
	 * 
	 * @return The size of the ExpenditureSummarySet
	 */
	public int size() {
		return expenditureSummarySet.size();
	}

	/**
	 * Get the index of a ExpenditureSummaryItem
	 * 
	 * @param expenditureSummaryItem Item to search for
	 * @return Index of the item or -1, of none was found
	 */
	public int getIndex(VatSummaryItem expenditureSummaryItem) {
		return expenditureSummarySet.getIndex(expenditureSummaryItem);
	}
}
