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

import static com.sebulli.fakturama.Translate._;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.ContextHelpConstants;
import com.sebulli.fakturama.logger.Logger;

/**
 * Web Browser Editor
 * 
 * @author Gerd Bartelt
 */
public class BrowserEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.browserEditor";
	private String url;

	// SWT components of the editor
	private Composite top;
	private Browser browser;

	// Button, to go home to the fakturama website
	private Button homeButton;

	// URL of the last site on fakturama.sebulli.com
	private String lastFakturamaURL = "";

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
		url = ((BrowserEditorInput) input).getUrl();
		setPartName(input.getName());
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

		// Create a composite that will contain the home button
		final Composite homeButtonComposite = new Composite(top, SWT.NONE);
		Color color = new Color(null, 0xc8, 0xda, 0xe4);
		homeButtonComposite.setBackground(color);
		GridDataFactory.fillDefaults().grab(true, false).hint(0, 0).applyTo(homeButtonComposite);
		GridLayoutFactory.fillDefaults().applyTo(homeButtonComposite);
		color.dispose();

		// Create a new web browser control
		try {
			browser = new Browser(top, SWT.NONE);
			color = new Color(null, 0xff, 0xff, 0xff);
			browser.setBackground(color);
			color.dispose();

			browser.addProgressListener(new ProgressListener() {
				@Override
				public void completed(ProgressEvent event) {
				}

				// If the website has changes, add a "go back" button
				@Override
				public void changed(ProgressEvent event) {
					String browserURL = browser.getUrl();
					boolean isValidURL = browserURL.startsWith("http://");

					// We are back at home - remove the button (if it exists)
					if (browserURL.startsWith("http://fakturama.sebulli.com") || !isValidURL) {

						// Store this URL as last URL
						lastFakturamaURL = browserURL;

						if (homeButton != null) {
							homeButton.dispose();
							homeButton = null;
							GridDataFactory.fillDefaults().grab(true, false).hint(0, 0).applyTo(homeButtonComposite);
							top.layout(true);
						}
					}
					// We are on an other web site - add the back to home button
					else if (isValidURL) {
						if (homeButton == null) {
							homeButton = new Button(homeButtonComposite, SWT.NONE);
							//T: Button to go back to the Fakturama home page
							homeButton.setText(_("<< Back to fakturama.sebulli.com"));
							homeButton.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {

									// Restore the last URL
									browser.setUrl(lastFakturamaURL);
								}
							});
							GridDataFactory.swtDefaults().applyTo(homeButton);
							GridDataFactory.fillDefaults().grab(true, false).applyTo(homeButtonComposite);
							top.layout(true);
						}
					}
				}

			});
			GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);

			// Open the website: url
			browser.setUrl(url);

		}
		catch (Exception e) {
			Logger.logError(e, "Error opening browser");
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
