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

package com.sebulli.fakturama.views.datasettable;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sebulli.fakturama.ContextHelpConstants;
import com.sebulli.fakturama.actions.NewExpenditureAction;
import com.sebulli.fakturama.data.Data;

/**
 * View with the table of all expenditures
 * 
 * @author Gerd Bartelt
 * 
 */
public class ViewExpenditureTable extends ViewDataSetTable {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewExpenditureTable";

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Add the action to create a new entry
		addNewAction = new NewExpenditureAction();

		// Mark the columns that are used by the search function.
		searchColumns = new String[4];
		searchColumns[0] = "name";
		searchColumns[1] = "nr";
		searchColumns[2] = "documentnr";
		searchColumns[3] = "date";

		super.createPartControl(parent, false, true, ContextHelpConstants.EXPENDITURE_TABLE_VIEW);

		// Name of this view
		this.setPartName(_("Expenditures"));

		// Create the context menu
		super.createDefaultContextMenu();

		// Name of the editor
		editor = "Expenditure";

		// Create the table columns
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Date"), 80, 0, true, "date");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Voucher"), 100, 0, true, "nr");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Document"), 150, 0, true, "documentnr");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Supplier"), 200, 50, false, "name");

		// Set the input of the table viewer and the tree viewer
		tableViewer.setInput(Data.INSTANCE.getExpenditures());
		topicTreeViewer.setInput(Data.INSTANCE.getExpenditures());

	}

}