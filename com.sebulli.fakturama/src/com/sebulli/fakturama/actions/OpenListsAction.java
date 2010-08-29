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

import org.eclipse.jface.action.Action;

import com.sebulli.fakturama.views.ViewManager;
import com.sebulli.fakturama.views.datasettable.ViewListTable;

/**
 * This action opens the lists in a table view.
 *  
 * @author Gerd Bartelt
 */
public class OpenListsAction extends Action {

	/**
	 * Constructor
	 */
	public OpenListsAction() {
		super("Listen");
		
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_LISTS);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_LISTS);
		
		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/list_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the texts in an table view
	 * and close the other table views. 
	 */
	@Override
	public void run() {
		ViewManager.showView(ViewListTable.ID);
	}
}