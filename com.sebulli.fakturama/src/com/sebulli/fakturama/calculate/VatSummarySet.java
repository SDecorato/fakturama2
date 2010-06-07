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

public class VatSummarySet extends TreeSet<VatSummaryItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(VatSummaryItem vatSummaryItem) {
		boolean added = super.add(vatSummaryItem);
		if (!added) {
			VatSummaryItem existing = super.ceiling(vatSummaryItem);
			existing.add(vatSummaryItem);
		}
		return added;
	}

	public int getIndex(VatSummaryItem vatSummaryItem) {
		int i = -1;
		for (Iterator<VatSummaryItem> iterator = this.iterator(); iterator.hasNext();) {
			i++;
			VatSummaryItem item = iterator.next();
			if (item.compareTo(vatSummaryItem) == 0)
				break;
		}
		return i;
	}

}
