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

/**
 * Label provider for unidataset lists
 *  
 * A label provider implementation which, by default, uses an element's
 * toString value for its text and null for its image 
 *  
 * @author Gerd Bartelt
 */
public class UniDataSetLabelProvider extends LabelProvider {

	// This entries of the UniDataSet will be displayed
	private String key = "name";
	
	/**
	 * Default constructor
	 */
	public UniDataSetLabelProvider () {
		key = "name";
	}
	
	/**
	 * Constructor with parameter to set the key
	 * 
	 * @param key The key to the UniDataSet entry
	 * 
	 */
	public UniDataSetLabelProvider (String key) {
		this.key = key;
	}
	
	/**
	 * Returns the text string used to label the element, or null if there is
	 * no text label for the given object
	 * 
	 * Returns the name, and maybe the first name and the company 
	 */
	@Override
	public String getText(Object element) {

		// The element is always an UniDataSet
		UniDataSet uds = (UniDataSet) element;
		
		String s = "";
		
		// Add the first name, if it exists
		if (uds.containsKey("firstname"))
			s += uds.getStringValueByKey("firstname") + " ";

		// Add always the key entry
		s += uds.getStringValueByKey(key);

		// Add the company, if it exists
		if (uds.containsKey("company"))
			s += " " + uds.getStringValueByKey("company");
		
		// Return the complete string
		return s;
	}

}
