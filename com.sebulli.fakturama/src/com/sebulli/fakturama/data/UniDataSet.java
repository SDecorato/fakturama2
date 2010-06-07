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

import java.util.HashMap;

import com.sebulli.fakturama.logger.Logger;

public abstract class UniDataSet {
	public static final String ID = "com.sebulli.fakturama.data.UniDataSet";
	public String sqlTabeName = "";
	protected HashMap<String, UniData> hashMap = new HashMap<String, UniData>();
	private String key;
	private UniDataSet uds;
	private UniData ud;
	final UniData defaultUniData = new UniData();

	public HashMap<String, UniData> getHashMap() {
		return this.hashMap;
	}

	public UniDataType getUniDataTypeByKey(String key) {
		extractUniDataSetByUniDataSetAndExtendedKey(this, key);
		return ud.getDataType();
	}

	public int getIntValueByKey(String key) {
		extractUniDataSetByUniDataSetAndExtendedKey(this, key);
		return ud.getValueAsInteger();
	}

	public boolean getBooleanValueByKey(String key) {
		extractUniDataSetByUniDataSetAndExtendedKey(this, key);
		return ud.getValueAsBoolean();
	}

	public Double getDoubleValueByKey(String key) {
		extractUniDataSetByUniDataSetAndExtendedKey(this, key);
		return ud.getValueAsDouble();
	}

	public String getStringValueByKey(String key) {
		try {
			return this.hashMap.get(key).getValueAsString();
		} catch (Exception e) {
			Logger.logError(e, "Error getting key. Key " + key + " not in dataset");
			return "";
		}

	}

	public String getStringValueByKeyFromOtherTable(String key) {
		extractUniDataSetByUniDataSetAndExtendedKey(this, key);
		return ud.getValueAsString();
	}

	public String getFormatedStringValueByKey(String key) {
		try {
			return this.hashMap.get(key).getValueAsFormatedString();
		} catch (Exception e) {
			Logger.logError(e, "Error getting key. Key " + key + " not in dataset");
			return "";
		}
	}

	public String getFormatedStringValueByKeyFromOtherTable(String key) {
		extractUniDataSetByUniDataSetAndExtendedKey(this, key);
		return ud.getValueAsFormatedString();
	}

	public boolean isTheSameAs(UniDataSet uds) {
		return false;
	}

	public boolean containsKey(String key) {
		return (hashMap.containsKey(key));
	}

	public void setIntValueByKey(String key, int i) {
		try {
			hashMap.get(key).setValue(i);
		} catch (Exception e) {
			Logger.logError(e, "Error setting key. Key " + key + " not in dataset");
		}

	}

	public void setBooleanValueByKey(String key, boolean b) {
		try {
			hashMap.get(key).setValue(b);
		} catch (Exception e) {
			Logger.logError(e, "Error setting key. Key " + key + " not in dataset");
		}
	}

	public void setDoubleValueByKey(String key, double d) {
		try {
			hashMap.get(key).setValue(d);
		} catch (Exception e) {
			Logger.logError(e, "Error setting key. Key " + key + " not in dataset");
		}
	}

	public void setStringValueByKey(String key, String s) {
		try {
			hashMap.get(key).setValue(s);
		} catch (Exception e) {
			Logger.logError(e, "Error setting key. Key " + key + " not in dataset");
		}
	}

	public String getCategory() {
		try {
			return hashMap.get("category").getValueAsString();
		} catch (Exception e) {
			Logger.logError(e, "Error getting key category.");
		}
		return "";
	}

	@Override
	public String toString() {
		return this.hashMap.get("name").getValueAsString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.hashMap.get("id").getValueAsString().hashCode() + this.sqlTabeName.hashCode() + this.hashMap.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniDataSet other = (UniDataSet) obj;

		for (String key : this.hashMap.keySet()) {
			if (this.hashMap.get(key) == null) {
				if (other.hashMap.get(key) != null)
					return false;
			} else if (!this.hashMap.get(key).getValueAsString().equals(other.hashMap.get(key).getValueAsString()))
				return false;
		}

		return true;
	}

	// vatId.VAT:name
	private void extractUniDataSetByUniDataSetAndExtendedKey(UniDataSet uniDataSet, String path) {
		String[] pathParts = path.split("\\.");
		String[] tableAndKey;
		String table;
		int id = 0;

		if (pathParts.length <= 1) {
			this.key = path;
			this.uds = uniDataSet;
			this.ud = this.uds.hashMap.get(this.key);
			if (this.ud == null) {
				this.ud = defaultUniData;
			}
			return;
		}

		uds = uniDataSet;

		for (String pathPart : pathParts) {

			tableAndKey = pathPart.split(":");

			if (tableAndKey.length == 2) {
				table = tableAndKey[0];
				this.key = tableAndKey[1];
				this.uds = Data.INSTANCE.getUniDataSetByTableNameAndId(table, id);
				if (this.uds != null)
					id = uds.getIntValueByKey(this.key);
				else {
					id = 0;

				}
			} else {
				table = "";
				this.key = pathPart;
				id = uds.getIntValueByKey(this.key);
			}
		}

		if (this.uds != null)
			this.ud = this.uds.hashMap.get(this.key);
		else
			this.ud = null;

		if (this.ud == null)
			this.ud = defaultUniData;

	}

}
