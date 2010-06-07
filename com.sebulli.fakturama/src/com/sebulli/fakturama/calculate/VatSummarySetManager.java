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

import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;

public class VatSummarySetManager {
	VatSummarySet vatSummaryItems;

	public VatSummarySetManager() {
		vatSummaryItems = new VatSummarySet();
	}

	public void add(DataSetDocument document) {
		int parentSign = DocumentType.getType(document.getIntValueByKey("category")).sign();

		DocumentSummary summary = new DocumentSummary();
		summary.calculate(vatSummaryItems, document.getItems(), document.getDoubleValueByKey("shipping") * parentSign, document
				.getDoubleValueByKey("shippingvat"), document.getStringValueByKey("shippingvatdescription"), document.getIntValueByKey("shippingautovat"),
				document.getDoubleValueByKey("itemsdiscount"), document.getBooleanValueByKey("novat"), document.getStringValueByKey("novatdescription"));
	}

	public VatSummarySet getVatSummaryItems() {
		return vatSummaryItems;
	}

	public int size() {
		return vatSummaryItems.size();
	}

	public int getIndex(VatSummaryItem vatSummaryItem) {
		return vatSummaryItems.getIndex(vatSummaryItem);
	}
}
