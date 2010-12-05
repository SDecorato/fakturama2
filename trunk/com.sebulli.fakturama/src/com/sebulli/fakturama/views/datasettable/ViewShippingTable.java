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
import com.sebulli.fakturama.actions.NewShippingAction;
import com.sebulli.fakturama.data.Data;

/**
 * View with the table of all shipping
 * 
 * @author Gerd Bartelt
 * 
 */
public class ViewShippingTable extends ViewDataSetTable {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewShippingTable";

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Set the standard key
		stdPropertyKey = "standardshipping";

		// Add the action to create a new entry
		addNewAction = new NewShippingAction();

		// Mark the columns that are used by the search function.
		searchColumns = new String[3];
		searchColumns[0] = "name";
		searchColumns[1] = "description";
		searchColumns[2] = "value";

		super.createPartControl(parent, false, true, ContextHelpConstants.SHIPPING_TABLE_VIEW);

		// Name of this view
		this.setPartName(_("Shippings"));

		// Create the context menu
		super.createDefaultContextMenu();

		// Name of the editor
		editor = "Shipping";

		// Create the table columns
		// new TableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "ID", 30, 0, true, "id");
		stdIconColumn = new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Standard", 55, 0, true, "$stdId");
		refreshStdId();
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Name"), 120, 0, true, "name");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Description"), 200, 50, false, "description");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Value"), 70, 0, true, "value");

		// Set the input of the table viewer and the tree viewer
		tableViewer.setInput(Data.INSTANCE.getShippings());
		topicTreeViewer.setInput(Data.INSTANCE.getShippings());

	}

}
