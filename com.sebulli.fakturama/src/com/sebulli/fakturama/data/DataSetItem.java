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

public class DataSetItem extends UniDataSet {

	public DataSetItem() {
		this("", "", "", 1.0, "", 0.0, 0);
	}

	public DataSetItem(DataSetItem parent) {
		this(parent, 1);
	}

	public DataSetItem(DataSetItem parent, int sign) {
		this(parent.getIntValueByKey("id"), parent.getStringValueByKey("name"), parent.getIntValueByKey("productid"), parent.getStringValueByKey("itemnr"),
				parent.getBooleanValueByKey("deleted"), parent.getStringValueByKey("category"), parent.getIntValueByKey("owner"), parent
						.getBooleanValueByKey("shared"), sign * parent.getDoubleValueByKey("quantity"), parent.getStringValueByKey("description"), parent
						.getDoubleValueByKey("price"), parent.getIntValueByKey("vatid"), parent.getDoubleValueByKey("discount"), parent
						.getDoubleValueByKey("vatvalue"), parent.getStringValueByKey("vatname"), parent.getStringValueByKey("vatdescription"), parent
						.getBooleanValueByKey("novat"));

	}

	public DataSetItem(String name, String itemnr, String category, Double quantity, String description, Double price, int vatId) {
		this(-1, name, -1, itemnr, false, category, -1, false, quantity, description, price, vatId, 0.0, 0.0, "", "", false);
	}

	public DataSetItem(Double quantity, DataSetProduct product) {
		this(-1, product.getStringValueByKey("name"), product.getIntValueByKey("id"), product.getStringValueByKey("itemnr"), false, "", -1, false, quantity,
				product.getStringValueByKey("description"), product.getPriceByQuantity(quantity), product.getIntValueByKey("vatid"), 0.0, 0.0, "", "", false);
		this.setVat(product.getIntValueByKey("vatid"));
	}

	public DataSetItem(int id, String name, int productid, String itemnr, boolean deleted, String category, int owner, boolean shared, Double quantity,
			String description, Double price, int vatId, double discount, double vatvalue, String vatname, String vatdescription, boolean noVat) {

		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("productid", new UniData(UniDataType.ID, productid));
		this.hashMap.put("itemnr", new UniData(UniDataType.STRING, itemnr));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("owner", new UniData(UniDataType.ID, owner));
		this.hashMap.put("shared", new UniData(UniDataType.BOOLEAN, shared));
		this.hashMap.put("quantity", new UniData(UniDataType.QUANTITY, quantity));
		this.hashMap.put("description", new UniData(UniDataType.STRING, description));
		this.hashMap.put("price", new UniData(UniDataType.PRICE, price));

		this.hashMap.put("vatid", new UniData(UniDataType.ID, vatId));
		this.hashMap.put("vatvalue", new UniData(UniDataType.PERCENT, vatvalue));
		this.hashMap.put("vatname", new UniData(UniDataType.STRING, vatname));
		this.hashMap.put("vatdescription", new UniData(UniDataType.STRING, vatdescription));
		this.hashMap.put("novat", new UniData(UniDataType.BOOLEAN, noVat));
		this.hashMap.put("discount", new UniData(UniDataType.PERCENT, discount));

		sqlTabeName = "Items";

	}

	public void setVat(int vatId) {
		DataSetVAT dsVat = Data.INSTANCE.getVATs().getDatasetById(vatId);
		this.setIntValueByKey("vatid", vatId);
		this.setDoubleValueByKey("vatvalue", dsVat.getDoubleValueByKey("value"));
		this.setStringValueByKey("vatname", dsVat.getStringValueByKey("name"));
		this.setStringValueByKey("vatdescription", dsVat.getStringValueByKey("description"));
		this.setBooleanValueByKey("novat", false);
	}

}
