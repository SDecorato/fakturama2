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

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.DataSetVAT;

public class NoVatContentProvider implements IStructuredContentProvider {
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		ArrayList<DataSetVAT> contentFiltered = new ArrayList<DataSetVAT>();
		ArrayList<DataSetVAT> content = (ArrayList<DataSetVAT>) inputElement;
		contentFiltered.add(new DataSetVAT(-1, "mit MwSt.", false, "", "", 0.0));
		for (DataSetVAT vat : content) {
			if ((!vat.getBooleanValueByKey("deleted")) && (DataUtils.DoublesAreEqual(vat.getDoubleValueByKey("value"), 0.0))) {
				contentFiltered.add(vat);
			}

		}
		return contentFiltered.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
