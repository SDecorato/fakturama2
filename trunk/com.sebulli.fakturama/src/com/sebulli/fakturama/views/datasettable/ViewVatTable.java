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

import com.sebulli.fakturama.actions.NewVatAction;
import com.sebulli.fakturama.data.Data;

public class ViewVatTable extends ViewDataSetTable {

	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewVatTable";

	@Override
	public void createPartControl(Composite parent) {
		stdPropertyKey = "standardvat";
		addNewAction = new NewVatAction();
		searchColumns = new String[3];
		searchColumns[0] = "name";
		searchColumns[1] = "description";
		searchColumns[2] = "value";

		super.createPartControl(parent, false, true);
		super.createDefaultContextMenu();

		editor = "Vat";

		// new TableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "ID", 30,
		// 0, true, "id");
		stdIconColumn = new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Standard", 55, 0, true, "$stdId");
		refreshStdId();
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 120, 0, true, "name");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Bezeichnung", 200, 50, false, "description");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Wert", 40, 0, true, "value");

		tableViewer.setInput(Data.INSTANCE.getVATs());
		topicTreeViewer.setInput(Data.INSTANCE.getVATs());
	}

}
