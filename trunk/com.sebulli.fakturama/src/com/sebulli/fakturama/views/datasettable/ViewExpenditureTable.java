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

package com.sebulli.fakturama.views.datasettable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

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

		super.createPartControl(parent, false, true);

		// Create the context menu
		super.createDefaultContextMenu();

		// Name of the editor
		editor = "Expenditure";

		// Create the table columns
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Datum", 80, 0, true, "date");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Belegnr.", 100, 0, true, "nr");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Dokumentnr.", 150, 0, true, "documentnr");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Lieferant", 200, 50, false, "name");

		// Set the input of the table viewer and the tree viewer
		tableViewer.setInput(Data.INSTANCE.getExpenditures());
		topicTreeViewer.setInput(Data.INSTANCE.getExpenditures());

	}

}
