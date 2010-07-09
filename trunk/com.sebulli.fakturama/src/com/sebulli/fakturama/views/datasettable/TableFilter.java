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

package com.sebulli.fakturama.views.datasettable;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.sebulli.fakturama.data.UniDataSet;

/**
 * Filters the contents of the table view
 * 
 * @author Gerd Bartelt
 */
public class TableFilter extends ViewerFilter {

	private String searchColumns[];
	private String searchString;

	/**
	 * Constructor
	 * Set the search columns. Only these columns are compared with
	 * the search filter.
	 * 
	 * @param searchColumns
	 */
	public TableFilter(String searchColumns[]) {
		this.searchColumns = searchColumns;
	}

	/**
	 * Set the search string and add a wildcard character to the beginning
	 * and the end
	 * 
	 * @param s The search string
	 */
	public void setSearchText(String s) {
		this.searchString = ".*" + s + ".*";
	}

	/**
	 * Returns whether the given element makes it through this filter.
	 *
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		// If the filter is empty, show all elements
		if (searchString == null || searchString.length() == 0) { return true; }

		// Get the element
		UniDataSet uds = (UniDataSet) element;

		// Search all the columns
		for (int i = 0; i < searchColumns.length; i++) {
			if (uds.getStringValueByKey(searchColumns[i]).matches(searchString)) { return true; }
		}

		return false;
	}
}