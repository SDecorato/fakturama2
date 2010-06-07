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

public class DataSetProduct extends UniDataSet {

	public DataSetProduct() {
		this("");
	}

	public DataSetProduct(String category) {
		this("", "", category, "", 0.0, -1, "", "");
	}

	public DataSetProduct(String name, String itemnr, String category, String description, Double price, int vatId, String options, String picturename) {
		this(-1, name, itemnr, false, category, description, price, price, price, price, price, 1, 10, 100, 1000, 10000, vatId, options, 0.0, -1, "",
				picturename);
	}

	public DataSetProduct(int id, String name, String itemnr, boolean deleted, String category, String description, Double price1, Double price2,
			Double price3, Double price4, Double price5, int block1, int block2, int block3, int block4, int block5, int vatId, String options, Double weight,
			int unit, String date_added, String picturename) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("itemnr", new UniData(UniDataType.STRING, itemnr));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("description", new UniData(UniDataType.STRING, description));
		this.hashMap.put("price1", new UniData(UniDataType.PRICE, price1));
		this.hashMap.put("price2", new UniData(UniDataType.PRICE, price2));
		this.hashMap.put("price3", new UniData(UniDataType.PRICE, price3));
		this.hashMap.put("price4", new UniData(UniDataType.PRICE, price4));
		this.hashMap.put("price5", new UniData(UniDataType.PRICE, price5));
		this.hashMap.put("block1", new UniData(UniDataType.INT, block1));
		this.hashMap.put("block2", new UniData(UniDataType.INT, block2));
		this.hashMap.put("block3", new UniData(UniDataType.INT, block3));
		this.hashMap.put("block4", new UniData(UniDataType.INT, block4));
		this.hashMap.put("block5", new UniData(UniDataType.INT, block5));
		this.hashMap.put("vatid", new UniData(UniDataType.INT, vatId));
		this.hashMap.put("options", new UniData(UniDataType.STRING, options));
		this.hashMap.put("weight", new UniData(UniDataType.DOUBLE, weight));
		this.hashMap.put("unit", new UniData(UniDataType.INT, unit));
		this.hashMap.put("date_added", new UniData(UniDataType.STRING, date_added));
		this.hashMap.put("picturename", new UniData(UniDataType.STRING, picturename));
		sqlTabeName = "Products";
	}

	public double getPriceByQuantity(Double quantity) {
		Double price = this.getDoubleValueByKey("price1");
		int blockQuantity = 0;
		int newQuantity;

		for (int i = 1; i <= 5; i++) {
			newQuantity = this.getIntValueByKey("block" + Integer.toString(i));
			if ((newQuantity > blockQuantity) && (quantity >= (newQuantity - 0.000001))) {
				blockQuantity = newQuantity;
				price = this.getDoubleValueByKey("price" + Integer.toString(i));
			}
		}
		return price;
	}

	@Override
	public boolean isTheSameAs(UniDataSet uds) {
		if (!uds.getStringValueByKey("itemnr").equals(this.getStringValueByKey("itemnr")))
			return false;
		if (!uds.getStringValueByKey("name").equals(this.getStringValueByKey("name")))
			return false;

		return true;
	}

}
