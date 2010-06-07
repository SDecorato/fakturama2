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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.UniDataSet;

public class ViewDataSetTableContentProvider implements IStructuredContentProvider, PropertyChangeListener {

	private final Viewer viewer;
	private String categoryFilter = "";
	private int transactionFilter = -1;
	private int contactFilter = -1;

	public ViewDataSetTableContentProvider(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<UniDataSet> contentFiltered = new ArrayList<UniDataSet>();
		if (inputElement instanceof DataSetArray<?>) {
			@SuppressWarnings("unchecked")
			ArrayList<UniDataSet> content = (ArrayList<UniDataSet>) ((DataSetArray<?>) inputElement).getDatasets();
			for (UniDataSet uds : content) {
				if (!uds.getBooleanValueByKey("deleted")) {
					if ((uds.getCategory().startsWith(categoryFilter + "/") || uds.getCategory().equals(categoryFilter) || categoryFilter.isEmpty())
							&& ((transactionFilter < 0) || (uds.getIntValueByKey("transaction") == transactionFilter))
							&& ((contactFilter < 0) || (uds.getIntValueByKey("addressid") == contactFilter))) {
						// uds.setStringValueByKey("name",
						// uds.getStringValueByKey("name"));
						contentFiltered.add(uds);
					}
				}
			}
		}

		return contentFiltered.toArray();

	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		viewer.refresh();
	}

	public void setTransactionFilter(int filter) {
		this.transactionFilter = filter;
	}

	public void setContactFilter(int filter) {
		this.contactFilter = filter;
	}

	public void setCategoryFilter(String filter) {
		this.categoryFilter = filter;
	}

	public int getTransactionFilter() {
		return this.transactionFilter;

	}

	public String getCategoryFilter() {
		return this.categoryFilter;
	}
}