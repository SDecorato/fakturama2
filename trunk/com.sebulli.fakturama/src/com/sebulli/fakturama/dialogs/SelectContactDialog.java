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

package com.sebulli.fakturama.dialogs;

import static com.sebulli.fakturama.Translate._;

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
	 * @param string
	 *            Dialog title
	 */
	public SelectContactDialog(String string) {
		super(string);
	}

	/**
	 * Create the dialog area
	 * 
	 * @param parent
	 *            Parent composite
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
		//T: Heading of the contact table in the "SelectContactDialog"
		//T: This dialog appears, if you edit a document and click on the
		//T: button to add a new address.
		//T: The words should be short (max. 8-10 characters)
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT,_("Customer ID"), 80, 0, true, "nr");
		//T: Heading of the contact table in the "SelectContactDialog"
		//T: This dialog appears, if you edit a document and click on the
		//T: button to add a new address.
		//T: The words should be short (max. 8-10 characters)
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("First Name"), 120, 0, true, "firstname");
		//T: Heading of the contact table in the "SelectContactDialog"
		//T: This dialog appears, if you edit a document and click on the
		//T: button to add a new address.
		//T: The words should be short (max. 8-10 characters)
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Last Name"), 120, 100, false, "name");
		//T: Heading of the contact table in the "SelectContactDialog"
		//T: This dialog appears, if you edit a document and click on the
		//T: button to add a new address.
		//T: The words should be short (max. 8-10 characters)
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Company"), 150, 0, true, "company");
		//T: Heading of the contact table in the "SelectContactDialog"
		//T: This dialog appears, if you edit a document and click on the
		//T: button to add a new address.
		//T: The words should be short (max. 8-10 characters)
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("ZIP"), 50, 0, true, "zip");
		//T: Heading of the contact table in the "SelectContactDialog"
		//T: This dialog appears, if you edit a document and click on the
		//T: button to add a new address.
		//T: The words should be short (max. 8-10 characters)
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("City"), 100, 0, true, "city");

		// Set the input
		tableViewer.setInput(Data.INSTANCE.getContacts());

		return control;
	}

}
