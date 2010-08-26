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

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.views.datasettable.UniDataSetTableColumn;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTableContentProvider;

/**
 * Dialog to select a product from a table
 * 
 * @author Gerd Bartelt
 */
public class SelectProductDialog extends SelectDataSetDialog {

	/**
	 * Constructor 
	 * 
	 * @param string Dialog title
	 */
	public SelectProductDialog(String string) {
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
		searchColumns = new String[3];
		searchColumns[0] = "itemnr";
		searchColumns[1] = "name";
		searchColumns[2] = "description";

		// Create the dialog area
		Control control = super.createDialogArea(parent);

		// Set the content provider
		tableViewer.setContentProvider(new ViewDataSetTableContentProvider(tableViewer));

		// Create the table columns
		if (Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_ITEMNR"))
			new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Artikelnr", 80, 0, true, "itemnr");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 150, 0, true, "name");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Beschreibung", 200, 50, false, "description");
		if (Activator.getDefault().getPreferenceStore().getInt("DOCUMENT_USE_NET_GROSS") == 1)
			new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Preis", 70, 0, true, "$Price1Gross");
		else
			new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Preis", 70, 0, true, "$Price1Net");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "MwSt.", 40, 0, true, "$vatbyid");

		// Set the input
		tableViewer.setInput(Data.INSTANCE.getProducts());

		return control;
	}

}