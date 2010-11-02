/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.views.datasettable;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sebulli.fakturama.actions.NewPaymentAction;
import com.sebulli.fakturama.data.Data;

/**
 * View with the table of all payments
 * 
 * @author Gerd Bartelt
 * 
 */
public class ViewPaymentTable extends ViewDataSetTable {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewPaymentTable";

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Set the standard key
		stdPropertyKey = "standardpayment";

		// Add the action to create a new entry
		addNewAction = new NewPaymentAction();

		// Mark the columns that are used by the search function.
		searchColumns = new String[5];
		searchColumns[0] = "name";
		searchColumns[1] = "description";
		searchColumns[2] = "discountvalue";
		searchColumns[3] = "discountdays";
		searchColumns[4] = "netdays";

		super.createPartControl(parent, false, true);

		// Name of this view
		this.setPartName(_("Payments"));

		// Create the context menu
		super.createDefaultContextMenu();

		// Name of the editor
		editor = "Payment";

		// Create the table columns
		// new TableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "ID", 30, 0, true, "id");
		//T: Used as heading of a table. Keep the word short.
		stdIconColumn = new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Standard"), 55, 0, true, "$stdId");
		refreshStdId();
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Name"), 120, 0, true, "name");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Description"), 200, 50, false, "description");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Discount"), 50, 0, true, "discountvalue");
		//T: Discount Days
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Disc. Days"), 70, 0, true, "discountdays");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Net Days"), 70, 0, true, "netdays");

		// Set the input of the table viewer and the tree viewer
		tableViewer.setInput(Data.INSTANCE.getPayments());
		topicTreeViewer.setInput(Data.INSTANCE.getPayments());

	}

}
