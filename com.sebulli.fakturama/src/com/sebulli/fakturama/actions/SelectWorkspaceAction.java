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

import com.sebulli.fakturama.Workspace;

/**
 * This action opens a dialog to select the workspace.
 * 
 * @author Gerd Bartelt
 */
public class SelectWorkspaceAction extends Action {

	/**
	 * Constructor
	 */
	public SelectWorkspaceAction() {

		//T: Text of the action to select the workspace
		super(_("Select Workspace"));

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SELECT_WORKSPACE);

		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_SELECT_WORKSPACE);
	}

	/**
	 * Run the action
	 * 
	 * Open a dialog to select a new workspace. If a valid folder is selected, a
	 * request is set. The new workspace is used, after the application has been
	 * restarted.
	 */
	@Override
	public void run() {

		// Select a new workspace
		Workspace.INSTANCE.selectWorkspace();
	}
}