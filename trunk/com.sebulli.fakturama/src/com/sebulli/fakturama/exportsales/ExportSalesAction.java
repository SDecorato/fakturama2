/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.exportsales;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.actions.ICommandIds;
import com.sebulli.fakturama.data.DataBaseConnectionState;

/**
 * This action exports the sales date to an OpenOffice Calc document.
 * 
 * @author Gerd Bartelt
 */
public class ExportSalesAction extends Action {

	/**
	 * Constructor
	 */
	public ExportSalesAction() {
		//T: Text of the action to export the list of sales 
		super(_("Export List of Sales"));

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT_SALES_SUMMARY);

		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_EXPORT_SALES_SUMMARY);

		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/export_sales_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the export dialog
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		// Create a new export wizard
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		WizardDialog exportSalesWizard = new WizardDialog(workbenchWindow.getShell(), new ExportSalesWizard());
		exportSalesWizard.open();
	}
}