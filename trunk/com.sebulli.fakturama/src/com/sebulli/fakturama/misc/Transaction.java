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

package com.sebulli.fakturama.misc;

import java.util.ArrayList;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;

/**
 * Organize all document of a specified transaction
 * 
 * @author Gerd Bartelt
 *
 */
public class Transaction {
		
	// The transcation no.
	int transaction = -1;
	// An list with all documents with the same transaction number
	ArrayList<DataSetDocument> documents = null;
	
	/**
	 * Constructor
	 * 
	 * Collects all documents with the same transaction number
	 * 
	 * @param document
	 * 	The document with the paranet transaction number
	 */
	public Transaction (DataSetDocument document) {
		
		// Get the transaction number
		transaction = document.getIntValueByKey("transaction");

		// Exit, if there is no number
		if (transaction == -1)
			return;

		// Create a new list
		documents = new ArrayList<DataSetDocument>();
		
		// Get all documents
		ArrayList<DataSetDocument> allDocuments;
		allDocuments = Data.INSTANCE.getDocuments().getActiveDatasets();
		
		// Search for all documents with the same number
		for (DataSetDocument oneDocument: allDocuments) {
			if (oneDocument.getIntValueByKey("transaction") == transaction ) {
				// Add the documents to the list
				documents.add(oneDocument);
			}
		}
	}
	
	/**
	 * Returns a string with all documents with the same transaction
	 *  
	 * @param docType
	 * 		Only those documents will be returned
	 * @return
	 * 		String with the document names
	 */
	public String getReference (DocumentType docType) {
		
		// Start with an empty string
		String reference = "";
		
		// Get all documents
		for (DataSetDocument document: documents) {
			
			// Has this document the same type
			if (document.getIntValueByKey("category") == docType.getInt()) {

				// Separate multiple reference names by a komma
				if (!reference.isEmpty())
					reference += ", ";

				// Add the name to the reference string
				reference += document.getStringValueByKey("name");
			}
		}
		
		// Return the reference string
		return reference;
	}
}
