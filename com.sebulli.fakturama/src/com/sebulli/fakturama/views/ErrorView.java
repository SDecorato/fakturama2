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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * This class represents the error view of the workbench
 * 
 * @author Gerd Bartelt
 */
public class ErrorView extends ViewPart {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.views.errorView";

	// The text of the view
	private Text errorText;

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Create top composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(top);

		// create the label
		Label labelItemNr = new Label(top, SWT.NONE);
		labelItemNr.setText("Fehler:");
		
		// fill the rest of the view with the text field
		errorText = new Text(top, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(errorText);
	}

	/**
	 * Asks this part to take focus within the workbench.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

	/**
	 * Set the error text
	 * 
	 * @param errorMessage
	 */
	public void setErrorText(String errorMessage) {
		errorText.setText(errorMessage);
	}
}
