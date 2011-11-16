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

package com.sebulli.fakturama.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;

import com.sebulli.fakturama.ContextHelpConstants;
import com.sebulli.fakturama.data.DataSetExpenditureVoucher;
import com.sebulli.fakturama.data.DataSetExpenditureVoucherItem;
import com.sebulli.fakturama.data.DataSetVoucherItem;
import com.sebulli.fakturama.views.datasettable.ViewExpenditureVoucherTable;

public class ExpenditureVoucherEditor extends VoucherEditor {
	
	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.expenditureVoucherEditor";

	public ExpenditureVoucherEditor () {
		super();
		tableViewID = ViewExpenditureVoucherTable.ID;
		editorID = "voucher";

	}
	
	/**
	 * Creates a new Voucher
	 */
	protected void CreateNewVoucher(IEditorInput input) {
		// Create a new data set
		voucher = new DataSetExpenditureVoucher(((UniDataSetEditorInput) input).getCategory());
	}

	protected DataSetVoucherItem createNewVoucherItem (DataSetVoucherItem item) {
		return new DataSetExpenditureVoucherItem(item); 
	}

	/**
	 * Creates a new voucher item
	 * @param name
	 * @param category
	 * @param price
	 * @param vatId
	 * @return
	 */
	protected DataSetVoucherItem createNewVoucherItem(String name, String category, Double price, int vatId) {
		return new DataSetExpenditureVoucherItem(name, category,price, vatId);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent, ContextHelpConstants.EXPENDITURE_VOUCHER_EDITOR);
	}


	
}
