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

import org.eclipse.jface.viewers.LabelProvider;

import com.sebulli.fakturama.data.UniDataSet;

public class UniDataSetLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		UniDataSet uds = (UniDataSet) element;
		String s = "";
		if (uds.containsKey("firstname"))
			s += uds.getStringValueByKey("firstname") + " ";

		s += uds.getStringValueByKey("name");

		if (uds.containsKey("company"))
			s += " " + uds.getStringValueByKey("company");
		return s;
	}

}
