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

import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.editors.CountryCodeEditor;
import com.sebulli.fakturama.editors.TextEditor;
import com.sebulli.fakturama.editors.UniDataSetEditorInput;
import com.sebulli.fakturama.logger.Logger;

/**
 * This action creates a new country code in an editor.
 *  
 * @author Gerd Bartelt
 */
public class NewCountryCodeAction extends NewEditorAction {

	/**
	 * Constructor
	 */
	public NewCountryCodeAction() {
		super("neuer Ländercode");

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_NEW_COUNTRYCODE);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_NEW_COUNTRYCODE);
		
		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/country_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Open a new text editor. 
	 */
	@Override
	public void run() {
		
		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		// Sets the editors input
		UniDataSetEditorInput input = new UniDataSetEditorInput(category);

		// Open a new text Editor 
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, CountryCodeEditor.ID);
		} catch (PartInitException e) {
			Logger.logError(e, "Error opening Editor: " + TextEditor.ID);
		}
	}
}