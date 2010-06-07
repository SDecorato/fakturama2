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

package com.sebulli.fakturama.views.datasettable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sebulli.fakturama.actions.NewContactAction;
import com.sebulli.fakturama.data.Data;

public class ViewContactTable extends ViewDataSetTable {

	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewContactTable";

	@Override
	public void createPartControl(Composite parent) {
		addNewAction = new NewContactAction(null);
		searchColumns = new String[6];
		searchColumns[0] = "nr";
		searchColumns[1] = "firstname";
		searchColumns[2] = "name";
		searchColumns[3] = "company";
		searchColumns[4] = "zip";
		searchColumns[5] = "city";

		super.createPartControl(parent, false, true);
		super.createDefaultContextMenu();

		editor = "Contact";

		// new TableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "ID", 30,
		// 0, true, "id");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Kundennr", 60, 0, true, "nr");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Vorname", 200, 50, false, "firstname");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 120, 0, true, "name");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Firma", 150, 0, true, "company");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "PLZ", 50, 0, true, "zip");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Ort", 80, 0, true, "city");

		tableViewer.setInput(Data.INSTANCE.getContacts());
		topicTreeViewer.setInput(Data.INSTANCE.getContacts());
	}

}
