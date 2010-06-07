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
import org.eclipse.jface.viewers.ViewerSorter;

import com.sebulli.fakturama.data.UniDataSet;

public class TableSorter extends ViewerSorter {

	private boolean descending = false;
	private String dataKey;
	private boolean isNumeric = false;
	private boolean isDate = false;

	public TableSorter() {
		this.descending = false;
		this.dataKey = "";
	}

	public void setDataKey(UniDataSet uds, String dataKey) {
		if (this.dataKey.equals(dataKey)) {
			descending = !descending;
		} else {
			this.dataKey = dataKey;
			this.descending = false;
		}
		isDate = UniDataSetTableColumn.isDate(uds, dataKey);
		isNumeric = UniDataSetTableColumn.isNumeric(uds, dataKey);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (dataKey.isEmpty())
			return 0;
		UniDataSet uds1 = (UniDataSet) e1;
		UniDataSet uds2 = (UniDataSet) e2;
		int retval = 0;
		if (isDate) {
			retval = uds1.getStringValueByKey(dataKey).compareTo(uds2.getStringValueByKey(dataKey));
		} else if (isNumeric)
			retval = UniDataSetTableColumn.getDoubleValue(uds1, dataKey).compareTo(UniDataSetTableColumn.getDoubleValue(uds2, dataKey));
		else
			retval = UniDataSetTableColumn.getText(uds1, dataKey).compareTo(UniDataSetTableColumn.getText(uds2, dataKey));

		if (!this.descending) {
			retval = -retval;
		}
		return retval;
	}

}
