/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.export;

import static com.sebulli.fakturama.Translate._;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

/**
 * Create the first (and only) page of the sales export wizard. This page is
 * used to select the start and end date.
 * 
 * @author Gerd Bartelt
 */
public class ExportWizardPageStartEndDate extends WizardPage {

	// start and end date
	private DateTime dtStartDate;
	private DateTime dtEndDate;

	private String label;
	
	/**
	 * Constructor Create the page and set title and message.
	 */
	public ExportWizardPageStartEndDate(String title, String label) {
		super("ExportWizandPageStartEndDate");
		//T: Title of the Sales Export Wizard Page 1
		setTitle(title);
		//T: Text of the Sales Export Wizard Page 1
		setMessage(_("Select a Periode") );
		this.label = label;
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
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(top);
		setControl(top);

		// Create the label with the help text
		Label labelDescription = new Label(top, SWT.NONE);
		
		//T: Export Sales Wizard Page 1 - Long description.
		labelDescription.setText(label);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).span(2, 1).indent(0, 10).applyTo(labelDescription);

		// Create a spacer
		Label labelSpacer = new Label(top, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1).indent(0, 10).applyTo(labelSpacer);

		// Label for start date
		Label labelStart = new Label(top, SWT.NONE);
		
		//T: Export Sales Wizard - Label Start Date of the period
		labelStart.setText(_("Start Date:"));
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(labelStart);

		// Label for end date
		Label labelEnd = new Label(top, SWT.NONE);
		//T: Export Sales Wizard - Label End Date of the period
		labelEnd.setText(_("End Date:"));
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(20, 0).applyTo(labelEnd);

		// Start date
		dtStartDate = new DateTime(top, SWT.DROP_DOWN);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(dtStartDate);

		// End date
		dtEndDate = new DateTime(top, SWT.DROP_DOWN);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(20, 0).applyTo(dtEndDate);

		// Set the start and end date to the 1st and last day of the
		// last month.
		GregorianCalendar calendar = new GregorianCalendar(dtEndDate.getYear(), dtEndDate.getMonth(), 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		dtEndDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		calendar = new GregorianCalendar(dtEndDate.getYear(), dtEndDate.getMonth(), 1);
		dtStartDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Return the start date as a GregorianCalendar object
	 * 
	 * @return Start date as a GregorianCalendar object
	 */
	public GregorianCalendar getStartDate() {
		return new GregorianCalendar(dtStartDate.getYear(), dtStartDate.getMonth(), dtStartDate.getDay());
	}

	/**
	 * Return the end date as a GregorianCalendar object
	 * 
	 * @return End date as a GregorianCalendar object
	 */
	public GregorianCalendar getEndDate() {
		return new GregorianCalendar(dtEndDate.getYear(), dtEndDate.getMonth(), dtEndDate.getDay());
	}
}
