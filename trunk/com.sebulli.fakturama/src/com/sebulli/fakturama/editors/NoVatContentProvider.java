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

package com.sebulli.fakturama.editors;

import static com.sebulli.fakturama.Translate._;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.DataSetVAT;

/**
 * Provides a list with all VAT entries with a value of "0%"
 * 
 * @author Gerd Bartelt
 */
public class NoVatContentProvider implements IStructuredContentProvider {

	/**
	 * Returns the elements to display in the viewer when its input is set to
	 * the given element. Only the elements with a VAT value of 0% are returned.
	 * 
	 * @return All elements with 0% VAT (+ one WITH VAT)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements
	 *      (java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {

		// Create 2 lists. One with all VAT entries and one with only
		// those entries with 0% VAT.
		ArrayList<DataSetVAT> contentFiltered = new ArrayList<DataSetVAT>();
		ArrayList<DataSetVAT> content = (ArrayList<DataSetVAT>) inputElement;

		// Add one entry WITH VAT. If this is selected, VAT is used.
		// All the other entries are with 0% VAT
		
		//T: Name of a VAT entry that indicates, that VAT is not 0%
		contentFiltered.add(new DataSetVAT(-1, _("With VAT"), false, "", "", 0.0));

		// Get all entries with 0%
		for (DataSetVAT vat : content) {
			if ((!vat.getBooleanValueByKey("deleted")) && (DataUtils.DoublesAreEqual(vat.getDoubleValueByKey("value"), 0.0))) {
				contentFiltered.add(vat);
			}

		}

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
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged
	 *      (org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
