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
import com.sebulli.fakturama.actions.MarkDocumentAsPayedAction;
import com.sebulli.fakturama.actions.MarkOrderAsAction;
import com.sebulli.fakturama.actions.NewDocumentAction;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;

/**
 * View with the table of all documents
 * 
 * @author Gerd Bartelt
 *
 */
public class ViewDocumentTable extends ViewDataSetTable {

	// ID of this view
	public static final String ID = "com.sebulli.fakturama.views.datasettable.viewDocumentTable";
	
	// The document type that corresponds with the selected category
	private DocumentType documentType = DocumentType.NONE;
	
	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Add the action to create a new entry
		addNewAction = new NewDocumentAction();

		
		// Mark the columns that are used by the search function.
		searchColumns = new String[4];
		searchColumns[0] = "name";
		searchColumns[1] = "date";
		searchColumns[2] = "addressfirstline";
		searchColumns[3] = "total";

		super.createPartControl(parent, true, false);

		// Create the context menu
		createContextMenu();
		
		// Name of the editor
		editor = "Document";

		// Create the table columns
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

		// Add a selection listener
		hookSelect();

		// Set the input of the table viewer and the tree viewer
		tableViewer.setInput(Data.INSTANCE.getDocuments());
		topicTreeViewer.setInput(Data.INSTANCE.getDocuments());

		// On creating, set the unpayed invoices
		topicTreeViewer.selectItemByName(DocumentType.getPluralString(DocumentType.INVOICE) + "/" + DataSetDocument.getStringNOTPAYED());

	}

	/**
	 * Create a context menu
	 */
	private void createContextMenu() {
		super.createMenuManager();
		
		// Add the entries for orders
		if (documentType.equals(DocumentType.ORDER)) {
			menuManager.add(new MarkOrderAsAction("als \"offen\" markieren", 10));
			menuManager.add(new MarkOrderAsAction("als \"in Bearbeitung\" markieren", 50));
			menuManager.add(new MarkOrderAsAction("als \"versendet\" markieren", 90));
		}
		// Add the entries to mark a document as payed
		else if (documentType.hasPayed()) {
			menuManager.add(new MarkDocumentAsPayedAction("als \"unbezahlt\" markieren", false));
			menuManager.add(new MarkDocumentAsPayedAction("als \"bezahlt\" markieren", true));
		}

		menuManager.add(new Separator());

		// Add an entry to create a new document of each document type
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

	/**
	 * Add a selection listener.
	 * The document view is the only view that contains a entry in the
	 * tree viewer for "THIS TRANSACTION" and "THIS CONTACT".
	 * If an entry is selected, it's possible to filter all documents from
	 * the same contact or with the same transaction ID.
	 */
	private void hookSelect() {

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			/**
			 * Notifies that the selection has changed
			 * 
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// Get the selection
				ISelection selection = event.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					DataSetDocument uds = (DataSetDocument) ((IStructuredSelection) selection).getFirstElement();
					
					// Set the transaction and the contact filter
					if ((uds != null)) {
						topicTreeViewer.setTransaction(uds.getStringValueByKey("name"), uds.getIntValueByKey("transaction"));
						topicTreeViewer.setContact(uds.getStringValueByKey("addressfirstline"), uds.getIntValueByKey("addressid"));
					}
				}
			}
		});
	}

	/**
	 * Recreate the context menu
	 * 
	 * @see com.sebulli.fakturama.views.datasettable.ViewDataSetTable#setCategoryFilter(java.lang.String)
	 */
	@Override
	public void setCategoryFilter(String filter) {
		super.setCategoryFilter(filter);
		
		// Get the document of the filter string
		for (int i = 0; i < DocumentType.MAXID; i++ ) {
			if (filter.startsWith(DocumentType.getPluralString(i)))
				documentType = DocumentType.getType(i);
		}
		
		// Recreate the context menu
		menuManager.removeAll();
		createContextMenu();
	}

}
