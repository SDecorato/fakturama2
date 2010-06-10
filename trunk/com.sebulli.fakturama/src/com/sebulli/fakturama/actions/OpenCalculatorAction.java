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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.Calculator;

/**
 * This action opens the calculator in a view.
 *  
 * @author Gerd Bartelt
 */
public class OpenCalculatorAction extends Action {

	/**
	 * Constructor
	 */
	public OpenCalculatorAction() {
		super("Rechner");
		
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_CALCULATOR);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_CALCULATOR);
		
		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/calculator_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the calculators view. 
	 */
	@Override
	public void run() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Calculator.ID);
		} catch (PartInitException e) {
			Logger.logError(e, "Error opening Calculator");
		}
	}
}