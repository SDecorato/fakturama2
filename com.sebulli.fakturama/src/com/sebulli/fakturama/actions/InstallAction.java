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

package com.sebulli.fakturama.actions;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.sebulli.fakturama.backup.BackupManager;

/**
 * This action opens the calculator in a view.
 * 
 * @author Gerd Bartelt
 */
public class InstallAction extends Action {

	/**
	 * Constructor
	 */
	public InstallAction() {

		//T: Text of the action to open the calculator
		super(_("Install New Software"));

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_P2_INSTALL);

		// Associate the action with a predefined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_P2_INSTALL);

		// sets a default 16x16 pixel icon.
		// setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/...png"));
	}

	/**
	 * Run the action
	 * 
	 * Install new software
	 */
	@Override
	public void run() {

		// Create a backup
		BackupManager.createBackup();

		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IHandlerService handlerService = (IHandlerService) workbenchWindow.getService(IHandlerService.class);
		try {
			handlerService.executeCommand("org.eclipselabs.p2.rcpupdate.install", null);
		} catch (Exception ex) {
			throw new RuntimeException("org.eclipselabs.p2.rcpupdate.install");
		}

	}
}
