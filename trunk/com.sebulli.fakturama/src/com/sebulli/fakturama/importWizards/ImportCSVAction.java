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

package com.sebulli.fakturama.importWizards;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.actions.ICommandIds;
import com.sebulli.fakturama.data.DataBaseConnectionState;

/**
 * This action imports data from a CSV file.
 *  
 * @author Gerd Bartelt
 */
public class ImportCSVAction extends Action {

	/**
	 * Constructor
	 */
	public ImportCSVAction() {
		super("Import CSV Tabelle");
		
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_IMPORT_CSV);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_IMPORT_CSV);

		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/import_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the import dialog
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		// Create a new import wizard
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		WizardDialog importCSVWizard = new WizardDialog(workbenchWindow.getShell(), new ImportCSVWizard());
		importCSVWizard.open();
	}
}
