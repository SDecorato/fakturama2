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
import com.sebulli.fakturama.editors.ProductEditor;
import com.sebulli.fakturama.editors.UniDataSetEditorInput;
import com.sebulli.fakturama.logger.Logger;

public class NewProductAction extends NewEditorAction {

	public NewProductAction() {
		super("neues Produkt");
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_NEW_PRODUCT);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_NEW_PRODUCT);
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/product_16.png"));
	}

	@Override
	public void run() {
		if (!Data.INSTANCE.getDataBaseOpened())
			return;

		UniDataSetEditorInput input = new UniDataSetEditorInput(category);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ProductEditor.ID);

		} catch (PartInitException e) {
			Logger.logError(e, "Error opening Editor: " + ProductEditor.ID);
		}
	}
}