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

package com.sebulli.fakturama.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.sebulli.fakturama.logger.Logger;

/**
 * Web Browser Editor
 * 
 * @author Gerd Bartelt
 */
public class BrowserEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.browserEditor";
	String url;

	/**
	 * Constructor
	 */
	public BrowserEditor() {
	}

	/**
	 * In the web browser editor there is nothing to save
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/**
	 * In the web browser editor there is nothing to save
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * Initialize the editor.
	 * Set the URL as part name
	 * 
	 * @param site Editor's site
	 * @param input Editor's input
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		url = ((BrowserEditorInput) input).getUrl();
		setPartName(input.getName());
	}

	/**
	 * An web editor is not saved, so there is nothing 
	 * that could be dirty
	 * 
	 * @return Always false
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/**
	 * Do not save anything
	 * 
	 * @return Always false
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Creates the content of the editor
	 * 
	 * @param parent Parent control element
	 */
	@Override
	public void createPartControl(Composite parent) {
		final Browser browser;
		
		Composite comp = new Composite(parent, SWT.NONE);
		Color color = comp.getBackground();
		comp.dispose();
		
		// Create a new web browser control
		try {
			browser = new Browser(parent, SWT.NONE);
			browser.setBackground(color);
		} catch (Exception e) {
			Logger.logError(e, "Error opening browser");
			return;
		}

		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);
		
		// Open the website: url
		browser.setUrl(url);
	}

}
