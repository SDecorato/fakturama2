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

import com.sebulli.fakturama.actions.NewProductAction;
import com.sebulli.fakturama.data.Data;

public class ViewProductTable extends ViewDataSetTable {

	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewProductTable";

	@Override
	public void createPartControl(Composite parent) {

		addNewAction = new NewProductAction();
		searchColumns = new String[4];
		searchColumns[0] = "itemnr";
		searchColumns[1] = "name";
		searchColumns[2] = "description";
		searchColumns[3] = "price1";

		super.createPartControl(parent, false, true);
		super.createDefaultContextMenu();

		editor = "Product";

		// new TableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "ID", 30,
		// 0, true, "id");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Artikelnr", 50, 0, true, "itemnr");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 120, 0, true, "name");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Bezeichnung", 200, 50, false, "description");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Preis", 70, 0, true, "price1");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "MwSt.", 40, 0, true, "$vatbyid");

		tableViewer.setInput(Data.INSTANCE.getProducts());
		topicTreeViewer.setInput(Data.INSTANCE.getProducts());

	}

}
