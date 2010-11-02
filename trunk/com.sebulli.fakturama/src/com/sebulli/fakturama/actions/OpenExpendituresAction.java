/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.actions;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.jface.action.Action;

import com.sebulli.fakturama.views.ViewManager;
import com.sebulli.fakturama.views.datasettable.ViewExpenditureTable;

/**
 * This action opens the expenditures in a table view.
 * 
 * @author Gerd Bartelt
 */
public class OpenExpendituresAction extends Action {

	/**
	 * Constructor
	 */
	public OpenExpendituresAction() {

		//T: Text of the action to open the expenditures
		super(_("Expenditures"));

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_EXPENDITURES);

		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_EXPENDITURES);

		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/expenditure_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open the expenditures in an table view and close the other table views.
	 */
	@Override
	public void run() {
		ViewManager.showView(ViewExpenditureTable.ID);
	}
}
