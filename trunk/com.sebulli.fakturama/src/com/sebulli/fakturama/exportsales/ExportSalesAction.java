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

package com.sebulli.fakturama.exportsales;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.actions.ICommandIds;
import com.sebulli.fakturama.data.Data;

public class ExportSalesAction extends Action {

	public ExportSalesAction() {
		super("Export USt-Liste");
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT_VAT_SUMMARY);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_EXPORT_VAT_SUMMARY);
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/export_sales_16.png"));
	}

	@Override
	public void run() {
		if (!Data.INSTANCE.getDataBaseOpened())
			return;

		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		WizardDialog exportVatSummaryWizard = new WizardDialog(workbenchWindow.getShell(), new ExportSalesWizard());
		exportVatSummaryWizard.open();
	}
}