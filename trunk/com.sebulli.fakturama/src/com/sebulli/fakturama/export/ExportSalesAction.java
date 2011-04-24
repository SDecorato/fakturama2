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

package com.sebulli.fakturama.export;

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

		//T: Tool Tip Text
		setToolTipText(_("Export a list of sales as an OpenOffice table") );

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
