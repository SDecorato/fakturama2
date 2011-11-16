/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
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


public class DataSetExpenditureVoucher extends DataSetVoucher{
	
	// Name of the table in the data base
	public String sqlTabeName = "Expenditures";

	/**
	 * Constructor
	 */
	public DataSetExpenditureVoucher() {
		super();
	}
	
	/**
	 * Constructor Creates a new expenditure voucher
	 * 
	 * @param category
	 *            Category of the new expenditure voucher
	 */
	public DataSetExpenditureVoucher(String category) {
		super (category);
	}


	
	/**
	 * Returns the voucher by its ID
	 * 
	 * @param id
	 * 	Id if the data set
	 * @return
	 * 	The data set from the data object
	 */
	protected DataSetVoucherItem getVoucherByID(int id) {
		return (Data.INSTANCE.getExpenditureVoucherItems().getDatasetById(id));
	}

	
}
