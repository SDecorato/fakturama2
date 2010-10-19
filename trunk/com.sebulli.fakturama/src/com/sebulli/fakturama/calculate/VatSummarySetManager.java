/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.calculate;

import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;

/**
 * Stores one VatSummarySet object and provides some methods e.g. to add an
 * UniDataSet document
 * 
 * @author Gerd Bartelt
 */
public class VatSummarySetManager {
	VatSummarySet vatSummarySet;

	/**
	 * Constructor Creates a new VatSummarySet
	 */
	public VatSummarySetManager() {
		vatSummarySet = new VatSummarySet();
	}

	/**
	 * Add an UniDataSet document to the VatSummarySet
	 * 
	 * @param document
	 *            Document to add
	 */
	public void add(DataSetDocument document, Double scaleFactor) {
		int parentSign = DocumentType.getType(document.getIntValueByKey("category")).sign();

		// Create a new summary object and start the calculation.
		// This will add all the entries to the VatSummarySet
		DocumentSummary summary = new DocumentSummary();
		summary.calculate(vatSummarySet, document.getItems(), document.getDoubleValueByKey("shipping") * parentSign,
				document.getDoubleValueByKey("shippingvat"), document.getStringValueByKey("shippingvatdescription"),
				document.getIntValueByKey("shippingautovat"), document.getDoubleValueByKey("itemsdiscount"), document.getBooleanValueByKey("novat"),
				document.getStringValueByKey("novatdescription"), scaleFactor);
	}

	/**
	 * Getter for the VatSummarySet
	 * 
	 * @return The VatSummarySet
	 */
	public VatSummarySet getVatSummaryItems() {
		return vatSummarySet;
	}

	/**
	 * Get the size of the
	 * 
	 * @return The size of the VatSummarySet
	 */
	public int size() {
		return vatSummarySet.size();
	}

	/**
	 * Get the index of a VatSummaryItem
	 * 
	 * @param vatSummaryItem
	 *            Item to search for
	 * @return Index of the item or -1, of none was found
	 */
	public int getIndex(VatSummaryItem vatSummaryItem) {
		return vatSummarySet.getIndex(vatSummaryItem);
	}
}
