/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.data;

/**
 * UniDataSet for all payments.
 * 
 * @author Gerd Bartelt
 */
public class DataSetPayment extends UniDataSet {

	/**
	 * Constructor Creates a new payment
	 */
	public DataSetPayment() {
		this("");
	}

	/**
	 * Constructor Creates a new payment
	 * 
	 * @param category
	 *            Category of the new payment
	 */
	public DataSetPayment(String category) {
		this("", category, "", 0.0, 0, 0, "", "", false);
	}

	/**
	 * Constructor Creates a new payment
	 * 
	 * @param name
	 * @param category
	 * @param description
	 * @param discountvalue
	 * @param discountdays
	 * @param netdays
	 * @param payedtext
	 * @param unpayedtext
	 * @param defaultPayed
	 */
	public DataSetPayment(String name, String category, String description, double discountvalue, int discountdays, int netdays, String payedtext,
			String unpayedtext, boolean defaultPayed) {
		this(0, name, false, category, description, discountvalue, discountdays, netdays, payedtext, unpayedtext, defaultPayed);
	}

	/**
	 * Constructor Creates a new payment
	 * 
	 * @param id
	 * @param name
	 * @param deleted
	 * @param category
	 * @param description
	 * @param discountvalue
	 * @param discountdays
	 * @param netdays
	 * @param payedtext
	 * @param unpayedtext
	 * @param defaultPayed
	 */
	public DataSetPayment(int id, String name, boolean deleted, String category, String description, double discountvalue, int discountdays, int netdays,
			String payedtext, String unpayedtext, boolean defaultPayed) {
		this.hashMap.put("id", new UniData(UniDataType.ID, id));
		this.hashMap.put("name", new UniData(UniDataType.STRING, name));
		this.hashMap.put("deleted", new UniData(UniDataType.BOOLEAN, deleted));
		this.hashMap.put("category", new UniData(UniDataType.STRING, category));
		this.hashMap.put("description", new UniData(UniDataType.TEXT, description));
		this.hashMap.put("discountvalue", new UniData(UniDataType.PERCENT, discountvalue));
		this.hashMap.put("discountdays", new UniData(UniDataType.INT, discountdays));
		this.hashMap.put("netdays", new UniData(UniDataType.INT, netdays));
		this.hashMap.put("payedtext", new UniData(UniDataType.TEXT, payedtext));
		this.hashMap.put("unpayedtext", new UniData(UniDataType.TEXT, unpayedtext));
		this.hashMap.put("defaultpayed", new UniData(UniDataType.BOOLEAN, defaultPayed));

		// Name of the table in the data base
		sqlTabeName = "Payments";
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
