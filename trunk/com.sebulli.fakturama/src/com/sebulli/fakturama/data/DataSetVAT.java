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

import com.sebulli.fakturama.calculate.DataUtils;

public class DataSetVAT extends UniDataSet {

	public DataSetVAT() {
		this("");
	}

	public DataSetVAT(String category) {
		this("", category, "", 0.0);
	}

	public DataSetVAT(String name, String category, String description, Double value) {
		this(-1, name, false, category, description, value);
	}

	public DataSetVAT(int id, String name, boolean deleted, String category, String description, Double value) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("description", new UniData(UniDataType.STRING, description));
		this.hashMap.put("value", new UniData(UniDataType.PERCENT, value));
		sqlTabeName = "Vats";
	}

	@Override
	public boolean isTheSameAs(UniDataSet uds) {
		// TODO: auch auf Name prüfen in Preference Page übernehmen
		// if (!
		// uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
		// return false;
		if (!DataUtils.DoublesAreEqual(uds.getDoubleValueByKey("value"), this.getDoubleValueByKey("value")))
			return false;
		return true;
	}

}
