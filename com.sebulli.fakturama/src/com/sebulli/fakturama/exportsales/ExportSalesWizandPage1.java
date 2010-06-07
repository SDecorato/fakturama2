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

package com.sebulli.fakturama.exportsales;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

public class ExportSalesWizandPage1 extends WizardPage {
	private DateTime dtBeginDate;
	private DateTime dtEndDate;

	protected ExportSalesWizandPage1() {
		super("ExportVatSummaryWizandPage1");
		setTitle("USt. Liste als Tabelle");
		setMessage("Zeitraum wählen");
	}

	public void createControl(Composite parent) {

		// createControl verlangt es, genau ein Control zu erstellen
		// und dieses mit setControl zu setzen

		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);
		setControl(top);

		Label labelDescription = new Label(top, SWT.BORDER);
		labelDescription.setText("Wählen Sie den Zeitraum der Zahlungseingänge.\n" + "Es werden alle Rechnungen exportiert, die bezahlt sind,\n"
				+ "und deren Zahlungseingänge in diesem Zeitraum liegen.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1).indent(0, 10).applyTo(labelDescription);

		Label labelSpacer = new Label(top, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1).indent(0, 10).applyTo(labelSpacer);

		Label labelBegin = new Label(top, SWT.BORDER);
		labelBegin.setText("Anfang:");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(labelBegin);

		Label labelEnd = new Label(top, SWT.BORDER);
		labelEnd.setText("Ende:");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(20, 0).applyTo(labelEnd);

		dtBeginDate = new DateTime(top, SWT.CALENDAR | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(dtBeginDate);

		dtEndDate = new DateTime(top, SWT.CALENDAR | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(20, 0).applyTo(dtEndDate);

		GregorianCalendar calendar = new GregorianCalendar(dtEndDate.getYear(), dtEndDate.getMonth(), 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		dtEndDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		calendar = new GregorianCalendar(dtEndDate.getYear(), dtEndDate.getMonth(), 1);
		dtBeginDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	public GregorianCalendar getBeginDate() {
		return new GregorianCalendar(dtBeginDate.getYear(), dtBeginDate.getMonth(), dtBeginDate.getDay());
	}

	public GregorianCalendar getEndDate() {
		return new GregorianCalendar(dtEndDate.getYear(), dtEndDate.getMonth(), dtEndDate.getDay());
	}
}