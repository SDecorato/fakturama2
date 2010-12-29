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

package com.sebulli.fakturama.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.ContextHelpConstants;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.parcelService.ParcelServiceFormFiller;

/**
 * Parcel Service Web Browser Editor
 * 
 * @author Gerd Bartelt
 */
public class ParcelServiceBrowserEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.parcelServiceBrowserEditor";
	private String url;

	// SWT components of the editor
	private Composite top;
	private Browser browser;
	private ParcelServiceBrowserEditor editor;
	
	// The form filler
	private ParcelServiceFormFiller parcelServiceFormFiller;
	
	/**
	 * Constructor
	 */
	public ParcelServiceBrowserEditor() {
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
	 * Initialize the editor. Set the URL as part name
	 * 
	 * @param site
	 *            Editor's site
	 * @param input
	 *            Editor's input
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		
		parcelServiceFormFiller = new ParcelServiceFormFiller();

		// Get the default URL
		url = Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_URL");
		String name = url;
		
		// Get the parcel service provider
		String provider = Activator.getDefault().getPreferenceStore().getString("PARCEL_SERVICE_PROVIDER");

		if (provider.equalsIgnoreCase("DHL")) {
			name = "DHL";
			url = "https://www.efiliale.de/efiliale/pop/produktauswahl.jsp";
		}
		
		if (provider.equalsIgnoreCase("HERMES")) {
			name = "Hermes";
			url = "https://www.myhermes.de/wps/portal/PRIPS_DEU/PAKETVERSAND";
		}

		setPartName(name);
		editor = this;
	}

	/**
	 * An web editor is not saved, so there is nothing that could be dirty
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
	 * @param parent
	 *            Parent control element
	 */
	@Override
	public void createPartControl(final Composite parent) {

		GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 0).applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

		// Format the top composite
		top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 0).applyTo(top);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(top);

		// Add context help reference 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(top, ContextHelpConstants.BROWSER_EDITOR);

		// Create a new web browser control
		try {
			browser = new Browser(top, SWT.NONE);
			Color color = new Color(null, 0xff, 0xff, 0xff);
			browser.setBackground(color);
			color.dispose();

			browser.addProgressListener(new ProgressListener() {
				@Override
				public void completed(ProgressEvent event) {
						parcelServiceFormFiller.fillForm(browser, editor.getEditorInput() );
				}

				@Override
				public void changed(ProgressEvent event) {
				}

			});
			GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);

			// Open the web site: URL
			browser.setUrl(url);

		}
		catch (Exception e) {
			Logger.logError(e, "Error opening parcel service browser");
			return;
		}

	}

	/**
	 * Go to the start page (fakturama.sebulli.com)
	 */
	public void resetUrl() {

		// set the URL
		if (browser != null)
			browser.setUrl(url);
	}

	
	/**
	 * Set the focus to the top composite.
	 * 
	 * @see com.sebulli.fakturama.editors.Editor#setFocus()
	 */
	@Override
	public void setFocus() {
		if(top != null) 
			top.setFocus();
	}


}
