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

package com.sebulli.fakturama.importWizards;

import static com.sebulli.fakturama.Translate._;

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
		
		//T: Text of the Import CSV Wizard Page
		super(_("ImportCSVWizardPage"));
		//T: Text of the Import CSV Wizard Page
		setTitle(_("Import Table as CSV"));
		setMessage("Message");
		setDescription("Description");
	}

	/**
	 * Displays the status of the import in the status text field
	 * 
	 * @param text
	 *            The status text to display
	 */
	public void setStatusText(String text) {
		statusTextString = text;
		if (statusText != null)
			statusText.setText(statusTextString);
	}

	/**
	 * Creates the top level control for this dialog page under the given parent
	 * composite.
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
		//T: Import progress of the CSV import
		labelDescription.setText(_("Import Progress:"));
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(labelDescription);

		// Create the label with the status text
		statusText = new Text(top, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(statusText);
		statusText.setText(statusTextString);
	}
}
