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

package com.sebulli.fakturama.calculate;

import com.sebulli.fakturama.data.DataSetExpenditure;

/**
 * Stores one VatSummarySet object and provides some methods e.g. to add an
 * UniDataSet document
 * 
 * @author Gerd Bartelt
 */
public class ExpenditureSummarySetManager {
	VatSummarySet expenditureSummarySet;

	/**
	 * Constructor Creates a new ExpenditureSummarySet
	 */
	public ExpenditureSummarySetManager() {
		expenditureSummarySet = new VatSummarySet();
	}

	/**
	 * Add an expenditure to the ExpenditureSummarySet
	 * 
	 * @param document
	 *            Document to add
	 * @param useCategory
	 *            If true, the category is also used for the vat summary as a
	 *            description
	 */
	public void add(DataSetExpenditure expenditure, boolean useCategory) {

		// Create a new summary object and start the calculation.
		// This will add all the entries to the VatSummarySet
		ExpenditureSummary summary = new ExpenditureSummary();
		summary.calculate(expenditureSummarySet, expenditure.getItems(), useCategory,
				expenditure.getDoubleValueByKey("paid"),expenditure.getDoubleValueByKey("total"), expenditure.getBooleanValueByKey("discounted"));
	}

	/**
	 * Add an expenditure to the ExpenditureSummarySet
	 * 
	 * @param document
	 *            Document to add
	 * @param useCategory
	 *            If true, the category is also used for the vat summary as a
	 *            description
	 * @itemNr index of one item
	 */
	public void add(DataSetExpenditure expenditure, boolean useCategory, int itemNr) {

		// Create a new summary object and start the calculation.
		// This will add all the entries to the VatSummarySet
		ExpenditureSummary summary = new ExpenditureSummary();
		summary.calculate(expenditureSummarySet, expenditure.getItems(itemNr), useCategory,
				expenditure.getDoubleValueByKey("paid"),expenditure.getDoubleValueByKey("total"), expenditure.getBooleanValueByKey("discounted"));
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
	 * @param expenditureSummaryItem
	 *            Item to search for
	 * @return Index of the item or -1, of none was found
	 */
	public int getIndex(VatSummaryItem expenditureSummaryItem) {
		return expenditureSummarySet.getIndex(expenditureSummaryItem);
	}
}
