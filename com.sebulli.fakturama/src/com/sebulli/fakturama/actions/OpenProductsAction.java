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

import com.sebulli.fakturama.views.TemporaryViews;
import com.sebulli.fakturama.views.datasettable.ViewProductTable;

/**
 * This action opens the products in a table view.
 *  
 * @author Gerd Bartelt
 */
public class OpenProductsAction extends Action {

	/**
	 * Constructor
	 */
	public OpenProductsAction() {
		super("Produkte");
		
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_PRODUCTS);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_PRODUCTS);
		
		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/product_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the products in an table view
	 * and close the other table views. 
	 */
	@Override
	public void run() {
		TemporaryViews.INSTANCE.showView(ViewProductTable.ID);
	}
}