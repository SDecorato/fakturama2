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

package com.sebulli.fakturama.dialogs;

import static com.sebulli.fakturama.Translate._;

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
	 * @param string
	 *            Dialog title
	 */
	public SelectProductDialog(String string) {
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
			//T: Used as heading of a table. Keep the word short.
			new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Item No."), 80, 0, true, "itemnr");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Name"), 150, 0, true, "name");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, _("Description"), 200, 50, false, "description");
		if (Activator.getDefault().getPreferenceStore().getInt("DOCUMENT_USE_NET_GROSS") == 1)
			//T: Used as heading of a table. Keep the word short.
			new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Price"), 70, 0, true, "$Price1Gross");
		else
			//T: Used as heading of a table. Keep the word short.
			new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("Price"), 70, 0, true, "$Price1Net");
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, _("VAT"), 40, 0, true, "$vatbyid");

		// Set the input
		tableViewer.setInput(Data.INSTANCE.getProducts());

		return control;
	}

}
