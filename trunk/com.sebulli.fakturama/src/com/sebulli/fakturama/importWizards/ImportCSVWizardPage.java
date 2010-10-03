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

package com.sebulli.fakturama.importWizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * First and only page of the CSV import wizards
 * 
 * @author Gerd Bartelt
 */
public class ImportCSVWizardPage extends WizardSelectionPage {
	
	private Text statusText;
	private String statusTextString = "";
	
	/**
	 * Constructor
	 * 
	 * Creates the wizard page
	 */
	public ImportCSVWizardPage() {
		super("ImportCSVWizardPage");
		setTitle("Tabelle als CSV importieren");
		setMessage("Message");
		setDescription("Description");
	}

	/**
	 * Displays the status of the import in the status text field
	 * 
	 * @param text The status text to display
	 */
	public void setStatusText (String text) {
		statusTextString = text;
		if (statusText != null)
			statusText.setText(statusTextString);
	}
	
	/**
	 * Creates the top level control for this dialog page under the given parent composite.
	 *  
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		
		// Create the top composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(top);
		setControl(top);

		// Create the label with the help text
		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Importverlauf:");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(labelDescription);

		// Create the label with the status text
		statusText = new Text(top, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(statusText);
		statusText.setText(statusTextString);
	}
}
