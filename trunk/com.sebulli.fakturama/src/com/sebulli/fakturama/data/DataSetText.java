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

package com.sebulli.fakturama.data;

/**
 * UniDataSet for all texts
 * 
 * @author Gerd Bartelt
 */
public class DataSetText extends UniDataSet {

	/**
	 * Constructor Creates a new text
	 */
	public DataSetText() {
		this("", "", "");
	}

	/**
	 * Constructor Creates a new text
	 * 
	 * @param category
	 *            Category of the new text
	 */
	public DataSetText(String category) {
		this("", category, "");
	}

	/**
	 * Constructor Creates a new text
	 * 
	 * @param name
	 * @param category
	 * @param text
	 */
	public DataSetText(String name, String category, String text) {
		this(0, name, false, category, text);
	}

	/**
	 * Constructor Creates a new text
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param category
	 * @param text
	 */
	public DataSetText(int id, String name, boolean deleted, String category, String text) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("text", new UniData(UniDataType.TEXT, text));

		// Name of the table in the data base
		sqlTabeName = "Texts";
	}

	/**
	 * Test, if this is equal to an other UniDataSet Only the names are compared
	 * 
	 * @param uds
	 *            Other UniDataSet
	 * @return True, if it's equal
	 */
	@Override
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
			return false;
		return true;
	}

}
