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

import java.util.ArrayList;

import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.DataSetExpenditureItem;

/**
 * Calculates the tax, gross and sum of one document. This is the central
 * calculation used by the document editors and the export functions.
 * 
 * @author Gerd Bartelt
 */
public class ExpenditureSummary {

	// total sum
	private PriceValue totalNet;
	private PriceValue totalVat;
	private PriceValue totalGross;

	/**
	 * Default constructor. Resets all value to 0.
	 */
	public ExpenditureSummary() {
		resetValues();
	}

	/**
	 * Reset all values to 0
	 */
	private void resetValues() {
		totalNet = new PriceValue(0.0);
		totalVat = new PriceValue(0.0);
		totalGross = new PriceValue(0.0);
	}

	/**
	 * Calculates the tax, gross and sum of an expenditure
	 * 
	 * @param globalVatSummarySet
	 *            The documents vat is added to this global VAT summary set.
	 * @param items
	 *            Document's items
	 * @param useCategory
	 *            If true, the category is also used for the vat summary as a
	 *            description
	 */
	public void calculate(VatSummarySet globalExpenditureSummarySet, DataSetArray<DataSetExpenditureItem> items, boolean useCategory) {

		Double vatPercent;
		String vatDescription;

		// This Vat summary contains only the VAT entries of this document,
		// whereas the the parameter vatSummaryItems is a global VAT summary
		// and contains entries from this document and from others.
		VatSummarySet expenditureSummaryItems = new VatSummarySet();

		// Set the values to 0.0
		resetValues();

		// Use all non-deleted items
		ArrayList<DataSetExpenditureItem> itemDataset = items.getActiveDatasets();
		for (DataSetExpenditureItem item : itemDataset) {

			// Get the data from each item
			vatDescription = item.getStringValueByKeyFromOtherTable("vatid.VATS:description");
			vatPercent = item.getDoubleValueByKeyFromOtherTable("vatid.VATS:value");

			Price price = new Price(item);
			Double itemVat = price.getTotalVat().asDouble();

			// Add the total net value of this item to the sum of net items
			this.totalNet.add(price.getTotalNet().asDouble());

			// Add the VAT to the sum of VATs
			this.totalVat.add(itemVat);

			VatSummaryItem expenditureSummaryItem;
			if (useCategory) {
				// Add the VAT summary item to the ... 
				expenditureSummaryItem = new VatSummaryItem(vatDescription, vatPercent, price.getTotalNet().asDouble(), itemVat,
						item.getStringValueByKey("category"));
			}
			else {
				// Add the VAT summary item to the ... 
				expenditureSummaryItem = new VatSummaryItem(vatDescription, vatPercent, price.getTotalNet().asDouble(), itemVat, "");

			}

			// .. VAT summary of the expenditure ..
			expenditureSummaryItems.add(expenditureSummaryItem);

		}

		// Gross value is the sum of net and VAT value
		this.totalGross.set(this.totalNet.asDouble() + this.totalVat.asDouble());

		// Finally, round the values

		this.totalGross.round();
		this.totalNet.round();
		this.totalVat.set(this.totalGross.asDouble() - this.totalNet.asDouble());

		// Round also the Vat summaries
		expenditureSummaryItems.roundAllEntries();

		// Add the entries of the document summary set also to the global one
		if (globalExpenditureSummarySet != null)
			globalExpenditureSummarySet.addVatSummarySet(expenditureSummaryItems);

	}

	/**
	 * Getter for total document sum (net)
	 * 
	 * @return Sum as PriceValue
	 */
	public PriceValue getTotalNet() {
		return this.totalNet;
	}

	/**
	 * Getter for total document sum (vat)
	 * 
	 * @return Sum as PriceValue
	 */
	public PriceValue getTotalVat() {
		return this.totalVat;
	}

	/**
	 * Getter for total document sum (gross)
	 * 
	 * @return Sum as PriceValue
	 */
	public PriceValue getTotalGross() {
		return this.totalGross;
	}

}