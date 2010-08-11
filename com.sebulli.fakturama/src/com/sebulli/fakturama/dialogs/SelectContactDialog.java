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

package com.sebulli.fakturama.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.views.datasettable.UniDataSetTableColumn;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTableContentProvider;

/**
 * Dialog to select a contact from a table
 * 
 * @author Gerd Bartelt
 */
public class SelectContactDialog extends SelectDataSetDialog {

	/**
	 * Constructor 
	 * 
	 * @param string Dialog title
	 */
	public SelectContactDialog(String string) {
		super(string);
	}

	/**
	 * Create the dialog area
	 * 
	 * @param parent Parent composite
	 * @return The new created dialog area
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// Mark the columns that are used by the search function.
		searchColumns = new String[6];
		searchColumns[0] = "nr";
		searchColumns[1] = "firstname";
		searchColumns[2] = "name";
		searchColumns[3] = "company";
		searchColumns[4] = "zip";
		searchColumns[5] = "city";

		// Create the dialog area
		Control control = super.createDialogArea(parent);

		// Set the content provider
		tableViewer.setContentProvider(new ViewDataSetTableContentProvider(tableViewer));

		// Create the table columns
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Kundennr", 80, 0, true, "nr");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Vorname", 120, 0, true, "firstname");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 120, 100, false, "name");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Firma", 150, 0, true, "company");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "PLZ", 50, 0, true, "zip");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Ort", 100, 0, true, "city");

		// Set the input
		tableViewer.setInput(Data.INSTANCE.getContacts());
		
		return control;
	}

}
