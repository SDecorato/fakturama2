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

package com.sebulli.fakturama.openoffice;

import java.util.ArrayList;

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
	public void openOODocument(DataSetDocument document, String template) {
		oODocuments.add(new OODocument(document, template));
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
