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

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.editors.PaymentEditor;
import com.sebulli.fakturama.editors.UniDataSetEditorInput;
import com.sebulli.fakturama.logger.Logger;

/**
 * This action creates a new payment in an editor.
 *  
 * @author Gerd Bartelt
 */
public class NewPaymentAction extends NewEditorAction {

	/**
	 * Default Constructor
	 */
	public NewPaymentAction() {
		super("neue Zahlmethode");
		
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_NEW_PAYMENT);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_NEW_PAYMENT);
		
		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/payment_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open a new payment editor. 
	 */
	@Override
	public void run() {

		// cancel, if the data base is not opened.
		if (!Data.INSTANCE.getDataBaseOpened())
			return;

		// Sets the editors input
		UniDataSetEditorInput input = new UniDataSetEditorInput(category);

		// Open a new Contact Editor 
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, PaymentEditor.ID);
		} catch (PartInitException e) {
			Logger.logError(e, "Error opening Editor: " + PaymentEditor.ID);
		}
	}
}