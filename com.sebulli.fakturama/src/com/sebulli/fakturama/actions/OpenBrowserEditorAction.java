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

import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.editors.BrowserEditor;
import com.sebulli.fakturama.editors.BrowserEditorInput;
import com.sebulli.fakturama.logger.Logger;

/**
 * This action opens the project website in an editor.
 * 
 * @author Gerd Bartelt
 */
public class OpenBrowserEditorAction extends Action {

	/**
	 * Constructor
	 */
	public OpenBrowserEditorAction() {
		super("fakturama.sebulli.com");

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_BROWSER_EDITOR);

		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_BROWSER_EDITOR);

		// sets a default 16x16 pixel icon.
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/16/www_16.png"));
	}

	/**
	 * Run the action
	 * 
	 * Set the URL and open the editor.
	 */
	@Override
	public void run() {

		// Get the active workbench window
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		// Sets the URL
		String url = "http://fakturama.sebulli.com/app.php";

		// Add version and language a a GET parameter
		// The language is uses, if the project website can generate
		// localized content.
		url += "?version=" + Activator.getDefault().getBundle().getVersion();
		url += "&lang=" + Locale.getDefault().getCountry();

		// Sets the URL as input for the editor.
		BrowserEditorInput input = new BrowserEditorInput(url);

		// Open the editor
		try {
			if (workbenchWindow != null) {
				IWorkbenchPage page = workbenchWindow.getActivePage();
				if (page != null) {

					// If the browser editor is already open, reset the URL
					BrowserEditor browserEditor = (BrowserEditor) page.findEditor(input);
					if (browserEditor != null)
						browserEditor.resetUrl();

					page.openEditor(input, BrowserEditor.ID);
				}
			}
		}
		catch (PartInitException e) {
			Logger.logError(e, "Error opening Editor: " + BrowserEditor.ID);
		}
	}
}