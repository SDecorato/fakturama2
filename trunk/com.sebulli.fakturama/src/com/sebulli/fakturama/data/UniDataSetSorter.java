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

package com.sebulli.fakturama.data;

/**
 * Sorter for UniDataSets 
 * 
 * @author Gerd Bartelt
 */
public class UniDataSetSorter implements java.util.Comparator<UniDataSet> {

	private String key1 = "name";
	private String key2 = "";
	
	/**
	 * Constructor.
	 * Sets the key of the UniData element to compare
	 * 
	 * @param key1 The key of the UniData element to compare
	 */
	public UniDataSetSorter(String key1) {
		this.key1 = key1;
	}

	/**
	 * An other constructor.
	 * Sets 2 keys of the UniData element to compare
	 * 
	 * @param key1 The first key of the UniData element to compare
	 * @param key2 The second key of the UniData element to compare
	 */
	public UniDataSetSorter(String key1, String key2) {
		this.key1 = key1;
		this.key2 = key2;
	}
	
	/**
	 * Compare two UniData elements 
	 * 
	 * @param uds1 The first UniData element
	 * @param uds2 The second UniData element
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(UniDataSet uds1, UniDataSet uds2) {
		
		// First compare the element with the first key
		int result = (uds1.getStringValueByKey(key1).compareToIgnoreCase(uds2.getStringValueByKey(key1)));
		
		// Return, if both are not equal
		if (result != 0)
			return result;

		// Return "equal", if there is no second key.
		if (key2.isEmpty())
			return result;
		
		// Otherwise return the result of the comparison with the second key
		return (uds1.getStringValueByKey(key2).compareTo(uds2.getStringValueByKey(key2)));
	}

}
