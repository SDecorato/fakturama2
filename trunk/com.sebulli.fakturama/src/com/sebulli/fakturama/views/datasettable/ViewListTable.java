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

import com.sebulli.fakturama.actions.NewListEntryAction;
import com.sebulli.fakturama.data.Data;

/**
 * View with the table of all lists
 * 
 * @author Gerd Bartelt
 *
 */
public class ViewListTable extends ViewDataSetTable {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewListTable";

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Add the action to create a new entry
		addNewAction = new NewListEntryAction();

		// Mark the columns that are used by the search function.
		searchColumns = new String[2];
		searchColumns[0] = "name";
		searchColumns[1] = "value";

		super.createPartControl(parent, false, true);

		// Create the context menu
		super.createDefaultContextMenu();

		// Name of the editor
		editor = "List";

		// Create the table columns
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 200, 0, true, "name");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Wert", 200, 200, false, "value");

		// Set the input of the table viewer and the tree viewer
		tableViewer.setInput(Data.INSTANCE.getListEntries());
		topicTreeViewer.setInput(Data.INSTANCE.getListEntries());

	}

}
