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
import org.eclipselabs.p2.rcpupdate.utils.P2Util;

import com.sebulli.fakturama.backup.BackupManager;

/**
 * This action opens the calculator in a view.
 * 
 * @author Gerd Bartelt
 */
public class UpdateAction extends Action {

	/**
	 * Constructor
	 */
	public UpdateAction() {

		//T: Text of the action to open the calculator
		super(_("Check for Updates"));

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_P2_UPDATE);

		// Associate the action with a predefined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_P2_UPDATE);

		// sets a default 16x16 pixel icon.
		// setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/...png"));
	}

	/**
	 * Run the action
	 * 
	 * Check for new updates
	 */
	@Override
	public void run() {

		// Create a backup
		BackupManager.createBackup();

		// Check for updates
        P2Util.checkForUpdates();
	}
}
