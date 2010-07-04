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

package com.sebulli.fakturama.editors;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sebulli.fakturama.data.UniDataSet;

/**
 * Content provider for all lists with unidatasets.
 * 
 * @author Gerd Bartelt
 */
public class UniDataSetContentProvider implements IStructuredContentProvider {

	/**
	 * Returns the elements to display in the viewer when its input is set to
	 * the given element.
	 * 
	 * Only those elements are returned, that are not marked as "deleted"
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		
		// Create 2 lists. One with all entries and one with only
		// those entries which are not marked as "deleted".
		ArrayList<UniDataSet> contentFiltered = new ArrayList<UniDataSet>();
		ArrayList<UniDataSet> content = (ArrayList<UniDataSet>) inputElement;
		
		// Copy only the "undeleted" entries to the final list.
		for (UniDataSet uds : content) {
			if (!uds.getBooleanValueByKey("deleted")) {
				contentFiltered.add(uds);
			}

		}
		
		// Return an array with only undeleted entries
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
	 *  switched to a different element. 
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged
	 * (org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
