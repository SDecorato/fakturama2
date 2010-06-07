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

public class DataSetPayment extends UniDataSet {

	public DataSetPayment() {
		this("");
	}

	public DataSetPayment(String category) {
		this("", category, "", 0, false);
	}

	public DataSetPayment(String name, String category, String description, int netdays, boolean defaultPayed) {
		this(0, name, false, category, description, 0.0, 0, netdays, defaultPayed);
	}

	public DataSetPayment(String name, String category, String description, double discountvalue, int discountdays, int netdays, boolean defaultPayed) {
		this(0, name, false, category, description, discountvalue, discountdays, netdays, defaultPayed);
	}

	public DataSetPayment(int id, String name, boolean deleted, String category, String description, double discountvalue, int discountdays, int netdays,
			boolean defaultPayed) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("description", new UniData(UniDataType.STRING, description));
		this.hashMap.put("discountvalue", new UniData(UniDataType.PERCENT, discountvalue));
		this.hashMap.put("discountdays", new UniData(UniDataType.INT, discountdays));
		this.hashMap.put("netdays", new UniData(UniDataType.INT, netdays));
		this.hashMap.put("defaultpayed", new UniData(UniDataType.BOOLEAN, defaultPayed));
		sqlTabeName = "Payments";
	}

	@Override
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
			return false;
		return true;
	}

}
