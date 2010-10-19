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

package com.sebulli.fakturama.views.datasettable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.UniDataSet;

/**
 * Content provider for the UniDataSet table viewer
 * 
 * @author Gerd Bartelt
 * 
 */
public class ViewDataSetTableContentProvider implements IStructuredContentProvider, PropertyChangeListener {

	// The viewer
	private final Viewer viewer;

	// The filters
	private String categoryFilter = "";
	private int transactionFilter = -1;
	private int contactFilter = -1;

	/**
	 * Constructor Sets the table viewer
	 * 
	 * @param viewer
	 *            The table viewer
	 */
	public ViewDataSetTableContentProvider(Viewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Returns the elements to display in the viewer when its input is set to
	 * the given element.
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		// Create a 2nd list, which will contain only those elements,
		// that are not deleted and match the filters.
		ArrayList<UniDataSet> contentFiltered = new ArrayList<UniDataSet>();

		// Get the content
		if (inputElement instanceof DataSetArray<?>) {
			@SuppressWarnings("unchecked")
			ArrayList<UniDataSet> content = (ArrayList<UniDataSet>) ((DataSetArray<?>) inputElement).getDatasets();

			// Check all entries
			for (UniDataSet uds : content) {

				// Use only the undeleted entries
				if (!uds.getBooleanValueByKey("deleted")) {

					// Use only those entries, that match the filters
					if ((uds.getCategory().toLowerCase().startsWith(categoryFilter.toLowerCase() + "/") || uds.getCategory().equalsIgnoreCase(categoryFilter) || categoryFilter
							.isEmpty())
							&& ((transactionFilter < 0) || (uds.getIntValueByKey("transaction") == transactionFilter))
							&& ((contactFilter < 0) || (uds.getIntValueByKey("addressid") == contactFilter))) {
						contentFiltered.add(uds);
					}
				}
			}
		}

		// The filters elements
		return contentFiltered.toArray();

	}

	/**
	 * Disposes of this content provider.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/**
	 * Notifies this content provider that the given viewer's input has been
	 * switched to a different element.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * This method gets called when a bound property is changed.
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		viewer.refresh();
	}

	/**
	 * Sets the transaction filter
	 * 
	 * @param filter
	 *            The transaction filter
	 */
	public void setTransactionFilter(int filter) {
		this.transactionFilter = filter;
	}

	/**
	 * Sets the contact filter
	 * 
	 * @param filter
	 *            The contact filter
	 */
	public void setContactFilter(int filter) {
		this.contactFilter = filter;
	}

	/**
	 * Sets the category filter
	 * 
	 * @param filter
	 *            The category filter
	 */
	public void setCategoryFilter(String filter) {
		this.categoryFilter = filter;
	}

	/**
	 * Returns the transaction filter
	 * 
	 * @return The current transaction filter
	 */
	public int getTransactionFilter() {
		return this.transactionFilter;

	}

	/**
	 * Returns the category filter
	 * 
	 * @return The current category filter
	 */
	public String getCategoryFilter() {
		return this.categoryFilter;
	}
}