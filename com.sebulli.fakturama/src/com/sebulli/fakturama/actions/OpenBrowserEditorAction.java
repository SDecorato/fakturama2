/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
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

import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.Workspace;
import com.sebulli.fakturama.editors.BrowserEditor;
import com.sebulli.fakturama.editors.BrowserEditorInput;
import com.sebulli.fakturama.logger.Logger;

/**
 * This action opens the project website in an editor.
 * 
 * @author Gerd Bartelt
 */
public class OpenBrowserEditorAction extends Action {

	// URL of the Fakturama project site
	public final static String FAKTURAMA_PROJECT_URL = "http://fakturama.sebulli.com/app.php";
	
	// Open the Fakturama forum
	private boolean useFakturamaProjectURL;
	
	/**
	 * Constructor
	 */
	public OpenBrowserEditorAction(boolean useFakturamaProjectURL) {
		super("fakturama.sebulli.com");

		this.useFakturamaProjectURL = useFakturamaProjectURL;
		
		//T: Tool Tip Text
		setToolTipText(_("Open the project web site fakturama.sebulli.com") );

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
		String url;
		if (this.useFakturamaProjectURL)
			url = FAKTURAMA_PROJECT_URL;
		else {
			url = Activator.getDefault().getPreferenceStore().getString("GENERAL_WEBBROWSER_URL");

			// In case of an empty URL: use the project URL
			if (url.isEmpty() || url.equals("http://fakturama.sebulli.com/app.php"));
				url = "file://" +
					Workspace.INSTANCE.getWorkspace() + "/" +
					Workspace.INSTANCE.getTemplateFolderName() +  
					"/Start/start.html";
			
		}

		// In case of an URL with only "-" do not show an editor
		if (url.equals("-"))
			return;

		
		// Add the "http://" or "file://"
		if ((!url.toLowerCase().startsWith("http://")) && 
			(!url.toLowerCase().startsWith("file://")) )
			url = "http://" + url;
		
		// Check, if the URL is the Fakturama project
		boolean isFakturamaProjectUrl = url.equalsIgnoreCase(FAKTURAMA_PROJECT_URL);
			
		// Add version and language a a GET parameter
		// The language is uses, if the project website can generate
		// localized content.
		if (isFakturamaProjectUrl) {
			url += "?version=" + Activator.getDefault().getBundle().getVersion();
			url += "&lang=" + Locale.getDefault().getCountry();
		}

		// Sets the URL as input for the editor.
		BrowserEditorInput input;
		if (isFakturamaProjectUrl)
			input = new BrowserEditorInput(url, "Fakturama Project", true);
		else
			input = new BrowserEditorInput(url, "Start", false);

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
