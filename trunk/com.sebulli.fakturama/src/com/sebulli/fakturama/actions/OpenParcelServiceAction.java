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

package com.sebulli.fakturama.actions;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.editors.DocumentEditor;
import com.sebulli.fakturama.editors.Editor;
import com.sebulli.fakturama.editors.ParcelServiceBrowserEditor;
import com.sebulli.fakturama.editors.ParcelServiceBrowserEditorInput;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;
import com.sebulli.fakturama.views.datasettable.ViewDocumentTable;

/**
 * This action opens the project website in an editor.
 * 
 * @author Gerd Bartelt
 */
public class OpenParcelServiceAction extends Action {

	private DocumentEditor documentEditor;

	/**
	 * Constructor
	 */
	public OpenParcelServiceAction() {
		//T: Text of the action
		super(_("Parcel Service"));

		//T: Tool Tip Text
		setToolTipText(_("Open the web site of the parcel service.") );

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_PARCEL_SERVICE);

		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_PARCEL_SERVICE);

		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/parcel_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Set the URL and open the editor.
	 */
	@Override
	public void run() {

		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		DataSetDocument dataSetDocument = null;
		
		// Get the active workbench window
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		// Get the active part (view)
		IWorkbenchPart part = null;
		if (page != null)
			part = page.getActivePart();
		
		// Get the active editor
		Editor editor = (Editor) page.getActiveEditor();
		
		// Do not use the editor, if the ViewDocumentTable is selected
		if (part instanceof ViewDocumentTable) {
			editor = null;
		}
			
		if (editor != null) {
			if (editor instanceof DocumentEditor) {

				// Search in the folder "Templates" and also in the folder with the localized  name
				documentEditor = (DocumentEditor) editor;

				// Get the document of the editor
				dataSetDocument = documentEditor.getDocument();
			}
		}
		else if (part != null){
			ISelection selection;

			// Cast the part to ViewDataSetTable
			if (part instanceof ViewDataSetTable) {

				ViewDataSetTable view = (ViewDataSetTable) part;

				// does the view exist ?
				if (view != null) {

					//get the selection
					selection = view.getSite().getSelectionProvider().getSelection();

					if (selection != null && selection instanceof IStructuredSelection) {

						Object obj = ((IStructuredSelection) selection).getFirstElement();

						// If we had a selection let change the state
						if (obj != null) {
							dataSetDocument = (DataSetDocument) obj;
						}
					}
				}
			}
		}

		// Set the editor's input and open a new editor 
		if (dataSetDocument != null) {
			if (dataSetDocument instanceof DataSetDocument) {

				// Sets the document with the address data as input for the editor.
				ParcelServiceBrowserEditorInput input = new ParcelServiceBrowserEditorInput(dataSetDocument);

				// Open the editor
				try {
					if (page != null) {

						// If the browser editor is already open, reset the URL
						ParcelServiceBrowserEditor parcelServiceBrowserEditor = (ParcelServiceBrowserEditor) page.findEditor(input);
						if (parcelServiceBrowserEditor != null)
							parcelServiceBrowserEditor.resetUrl();

						page.openEditor(input, ParcelServiceBrowserEditor.ID);
					}
				}
				catch (PartInitException e) {
					Logger.logError(e, "Error opening Editor: " + ParcelServiceBrowserEditor.ID);
				}
			}
		}
		else {
			// Show an information dialog, if no document is selected
			MessageBox messageBox = new MessageBox(workbenchWindow.getShell(), SWT.ICON_WARNING | SWT.OK );
			//T: Title of the dialog 
			messageBox.setText(_("Information"));
			//T: Text of the dialog
			messageBox.setMessage(_("You have to open or select a document."));
			messageBox.open();
		}
	}
}
