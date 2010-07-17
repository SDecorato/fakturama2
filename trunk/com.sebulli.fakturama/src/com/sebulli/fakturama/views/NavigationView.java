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

package com.sebulli.fakturama.views;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.sebulli.fakturama.actions.ICommandIds;
import com.sebulli.fakturama.actions.NewContactAction;
import com.sebulli.fakturama.actions.NewProductAction;
import com.sebulli.fakturama.actions.OpenBrowserEditorAction;
import com.sebulli.fakturama.actions.OpenCalculatorAction;
import com.sebulli.fakturama.actions.OpenContactsAction;
import com.sebulli.fakturama.actions.OpenDocumentsAction;
import com.sebulli.fakturama.actions.OpenPaymentsAction;
import com.sebulli.fakturama.actions.OpenProductsAction;
import com.sebulli.fakturama.actions.OpenShippingsAction;
import com.sebulli.fakturama.actions.OpenTextsAction;
import com.sebulli.fakturama.actions.OpenVatsAction;
import com.sebulli.fakturama.actions.WebShopImportAction;
import com.sebulli.fakturama.exportsales.ExportSalesAction;

/**
 * This class represents the navigation view of the workbench
 * 
 * @author Gerd Bartelt
 */
public class NavigationView extends ViewPart implements ICommandIds {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.navigationView";

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Create a new expand bar manager.
		ExpandBarManager expandBarManager = new ExpandBarManager();
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(top);

		// Create the first expand bar "Import"
		final ExpandBar bar1 = new ExpandBar(expandBarManager, top, SWT.NONE,
				"Importieren", "/icons/16/import_16.png");

		bar1.addAction(new WebShopImportAction());

		// Create the 2nd expand bar "Data"
		final ExpandBar bar2 = new ExpandBar(expandBarManager, top, SWT.NONE,
				"Daten", "/icons/16/data_16.png");

		bar2.addAction(new OpenDocumentsAction());
		bar2.addAction(new OpenProductsAction());
		bar2.addAction(new OpenContactsAction());
		bar2.addAction(new OpenPaymentsAction());
		bar2.addAction(new OpenShippingsAction());
		bar2.addAction(new OpenVatsAction());
		bar2.addAction(new OpenTextsAction());

		// Create the 3rd expand bar "Create new"
		final ExpandBar bar3 = new ExpandBar(expandBarManager, top, SWT.NONE,
				"Neu erstellen", "/icons/16/plus_16.png");

		bar3.addAction(new NewProductAction());
		bar3.addAction(new NewContactAction(null));

		// Create the 4th expand bar "export"
		final ExpandBar bar4 = new ExpandBar(expandBarManager, top, SWT.NONE,
				"exportieren", "/icons/16/export_16.png");

		bar4.addAction(new ExportSalesAction());

		// Create the 5th expand bar "Miscellaneous"
		final ExpandBar bar5 = new ExpandBar(expandBarManager, top, SWT.NONE,
				"sonstiges", "/icons/16/misc_16.png");

		bar5.addAction(new OpenBrowserEditorAction());
		bar5.addAction(new OpenCalculatorAction());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
	}
}