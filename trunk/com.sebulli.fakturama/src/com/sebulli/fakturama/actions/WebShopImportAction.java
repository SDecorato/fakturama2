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

package com.sebulli.fakturama.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.TemporaryViews;
import com.sebulli.fakturama.views.datasettable.ViewDocumentTable;
import com.sebulli.fakturama.webshopimport.WebShopImportManager;

/**
 * This action opens the documents in a table view.
 *  
 * @author Gerd Bartelt
 */
public class WebShopImportAction extends Action {

	/**
	 * Constructor
	 */
	public WebShopImportAction() {
		super("Webshop Import");

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_WEBSHOP_IMPORT);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_WEBSHOP_IMPORT);
		
		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/shop_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the web shop import manager.
	 */
	@Override
	public void run() {
		
		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		// Start a new web shop import manager in a
		// progress Monitor Dialog
		WebShopImportManager webShopImportManager = new WebShopImportManager();
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			new ProgressMonitorDialog(workbenchWindow.getShell()).run(true, true, webShopImportManager);

			// If there is no error - interpret the data.
			if (webShopImportManager.getRunResult().isEmpty())
				webShopImportManager.interpretWebShopData();
			else {
				// If there is an error - display it in a message box
				MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR);
				messageBox.setText("Fehler beim Importieren vom Webshop");
				messageBox.setMessage(webShopImportManager.getRunResult());
				messageBox.open();
			}
		} catch (InvocationTargetException e) {
			Logger.logError(e, "Error running web shop import manager.");
		} catch (InterruptedException e) {
			Logger.logError(e, "Web shop import manager was interrupted.");
		}
		
		// After the web shop import, open the document view
		// and set the focus to the new imported orders.
		TemporaryViews.INSTANCE.showView(ViewDocumentTable.ID);
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewDocumentTable.ID);
		ViewDocumentTable viewDocumentTable = (ViewDocumentTable) view;
		viewDocumentTable.getTopicTreeViewer().selectItemByName(DocumentType.ORDER.getPluralString() + "/" + DataSetDocument.getStringNOTSHIPPED());
	}
}