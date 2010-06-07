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

public class DataSetShipping extends UniDataSet {
	public static final int SHIPPINGVATAUTO = 0;
	public static final int SHIPPINGVATGROSS = 1;
	public static final int SHIPPINGVATNET = 2;

	public DataSetShipping() {
		this("");
	}

	public DataSetShipping(String category) {
		this("", category, "", 0.0, 0, 1);
	}

	public DataSetShipping(String name, String category, String description, Double value, int vatId, int autovat) {
		this(-1, name, false, category, description, value, vatId, autovat);
	}

	public DataSetShipping(int id, String name, boolean deleted, String category, String description, Double value, int vatId, int autovat) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("description", new UniData(UniDataType.STRING, description));
		this.hashMap.put("vatid", new UniData(UniDataType.ID, vatId));
		this.hashMap.put("value", new UniData(UniDataType.PRICE, value));
		this.hashMap.put("autovat", new UniData(UniDataType.INT, autovat));

		sqlTabeName = "Shippings";
	}

	public Double getVatAsDouble() {
		Double vat;
		Double net;
		int id;
		id = hashMap.get("vatid").getValueAsInteger();
		net = hashMap.get("value").getValueAsDouble();
		vat = Data.INSTANCE.getUniDataSetByTableNameAndId("vats", id).getDoubleValueByKey("value");
		return (vat * net);
	}

	@Override
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
			return false;
		if (!uds.getStringValueByKey("value").equals(this.getStringValueByKey("value")))
			return false;
		return true;
	}

}
