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

import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.logger.Logger;

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
	public void openOODocument(DataSetDocument document, String template) {
		oODocuments.add(new OODocument(document, template));
		Logger.logInfo("OOdebug:103");

	}

	/**
	 * Closes all open OpenOffice documents
	 */
	public void closeAll() {
		Logger.logInfo("OOdebug:100");

		// Close all documents
		for (int i = 0; i < oODocuments.size(); i++) {
			oODocuments.get(i).close();
			Logger.logInfo("OOdebug:101");

		}

		// Clear the list
		oODocuments.clear();
		Logger.logInfo("OOdebug:102");

	}
}
