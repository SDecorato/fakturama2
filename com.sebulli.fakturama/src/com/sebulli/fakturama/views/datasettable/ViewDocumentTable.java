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

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sebulli.fakturama.actions.DeleteDataSetAction;
import com.sebulli.fakturama.actions.MarkOrderAsAction;
import com.sebulli.fakturama.actions.NewDocumentAction;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;

public class ViewDocumentTable extends ViewDataSetTable {

	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewDocumentTable";

	@Override
	public void createPartControl(Composite parent) {
		addNewAction = new NewDocumentAction();
		searchColumns = new String[4];
		searchColumns[0] = "name";
		searchColumns[1] = "date";
		searchColumns[2] = "addressfirstline";
		searchColumns[3] = "total";

		super.createPartControl(parent, true, false);
		createContextMenu();
		editor = "Document";

		// new TableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "ID", 30,
		// 0, true, "id");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "", 20, 0, true, "$documenttype");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Dokument", 80, 0, true, "name");
		// new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT,
		// "Transaction", 80, 0, true, "transaction");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Datum", 80, 0, true, "date");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Name", 200, 50, false, "addressfirstline");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.LEFT, "Status", 100, 0, true, "$status");
		new UniDataSetTableColumn(tableColumnLayout, tableViewer, SWT.RIGHT, "Summe", 70, 0, true, "total");

		hookSelect();

		tableViewer.setInput(Data.INSTANCE.getDocuments());
		topicTreeViewer.setInput(Data.INSTANCE.getDocuments());
		topicTreeViewer.selectItemByName(DocumentType.getPluralString(DocumentType.INVOICE) + "/" + DataSetDocument.getStringNOTPAYED());

	}

	private void createContextMenu() {
		super.createMenuManager();
		menuManager.add(new MarkOrderAsAction("als \"offen\" markieren", 10));
		menuManager.add(new MarkOrderAsAction("als \"in Bearbeitung\" markieren", 50));
		menuManager.add(new MarkOrderAsAction("als \"versendet\" markieren", 90));
		// menuManager.add(new
		// MarkOrderAsAction("als \"abgeschlossen\" markieren", 100));
		menuManager.add(new Separator());
		menuManager.add(new NewDocumentAction(DocumentType.LETTER));
		menuManager.add(new NewDocumentAction(DocumentType.OFFER));
		menuManager.add(new NewDocumentAction(DocumentType.ORDER));
		menuManager.add(new NewDocumentAction(DocumentType.CONFIRMATION));
		menuManager.add(new NewDocumentAction(DocumentType.INVOICE));
		menuManager.add(new NewDocumentAction(DocumentType.DELIVERY));
		menuManager.add(new NewDocumentAction(DocumentType.CREDIT));
		menuManager.add(new NewDocumentAction(DocumentType.DUNNING));
		menuManager.add(new Separator());
		menuManager.add(new DeleteDataSetAction());
	}

	private void hookSelect() {

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				ISelection selection = event.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					DataSetDocument uds = (DataSetDocument) ((IStructuredSelection) selection).getFirstElement();
					if ((uds != null)) {
						topicTreeViewer.setTransaction(uds.getStringValueByKey("name"), uds.getIntValueByKey("transaction"));
						topicTreeViewer.setContact(uds.getStringValueByKey("addressfirstline"), uds.getIntValueByKey("addressid"));
					}
				}
			}
		});
	}

}
