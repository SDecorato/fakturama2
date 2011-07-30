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

package com.sebulli.fakturama.openoffice;

import java.util.ArrayList;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.DataSetDocument;

/**
 * Manages the OpenOffice documents. Stores a list of all open documents and
 * closes all when closing Fakturama.
 * 
 * @author Gerd Bartelt
 */
public enum OOManager {
	INSTANCE;

	// List with all open documents
	ArrayList<OODocument> oODocuments = new ArrayList<OODocument>();

	/**
	 * Opens a document and add it to the list
	 * 
	 * @param document
	 *            The UniDataSet that will be exported into an OpenOffice Writer
	 *            document
	 * @param template
	 *            The Filename of the OpenOffice Template
	 */
	public void openOODocument(final DataSetDocument document, final String template,final boolean forceRecreation) {
		
		
		// Start OpenOffice in a new thread, depending in the settings
		if (Activator.getDefault().getPreferenceStore().getBoolean("OPENOFFICE_START_IN_NEW_THREAD")) {

			// Start OpenOffice in a new thread
			new Thread(new Runnable() {
				public void run() {
					oODocuments.add(new OODocument(document, template, forceRecreation));
				}
			}).start();
			
		}
		else {
			
			// Start OpenOffice in this thread
			oODocuments.add(new OODocument(document, template, forceRecreation));
		}

		
	}

	/**
	 * Closes all open OpenOffice documents
	 */
	public void closeAll() {
		// Close all documents
		for (int i = 0; i < oODocuments.size(); i++) {
			oODocuments.get(i).close();
		}

		// Clear the list
		oODocuments.clear();
	}
}
