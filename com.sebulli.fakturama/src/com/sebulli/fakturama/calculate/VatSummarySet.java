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

import java.util.Iterator;
import java.util.TreeSet;

/**
 * This Class can contain multiple VatSummaryItems.
 * 
 * If an item is added, and an other VatSummaryItem with the same
 * name and the same vat value in percent is existing, the absolute
 * net and vat values of this item are added to the existing.
 * 
 * If there is no entry with the same name and vat percent value,
 * a new one is created. 
 * 
 * @author Gerd Bartelt
 */
public class VatSummarySet extends TreeSet<VatSummaryItem> {

	private static final long serialVersionUID = 1L;

	/**
	 * Add a new VatSummaryItem to this tree
	 * 
	 * @param vatSummaryItem The new Item
	 * @return True, if it was added as new item
	 */
	@Override
	public boolean add(VatSummaryItem vatSummaryItem) {
		// try to add it
		boolean added = super.add(vatSummaryItem);
		
		// If there was already an item with the same value and name ..
		if (!added) {
			
			// add the net and vat to the existing one
			VatSummaryItem existing = super.ceiling(vatSummaryItem);
			existing.add(vatSummaryItem);
		}
		
		return added;
	}

	/**
	 * Returns the index of a VatSummaryItem
	 * 
	 * @param vatSummaryItem to Search for
	 * @return index or -1, if it was not found.
	 */
	public int getIndex(VatSummaryItem vatSummaryItem) {
		int i = -1;
		
		// Search all items
		for (Iterator<VatSummaryItem> iterator = this.iterator(); iterator.hasNext();) {
			i++;
			VatSummaryItem item = iterator.next();
			
			// Returns the item, if it is the same
			if (item.compareTo(vatSummaryItem) == 0)
				break;
		}
		return i;
	}

}
