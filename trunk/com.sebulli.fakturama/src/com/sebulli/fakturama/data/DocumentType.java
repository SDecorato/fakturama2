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

package com.sebulli.fakturama.data;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Enumeration of all 8 data types, a document can be.
 * 
 * @author Gerd Bartelt
 */
public enum DocumentType {
	// all 8 data types
	NONE, LETTER, OFFER, ORDER, CONFIRMATION, INVOICE, DELIVERY, CREDIT, DUNNING;

	// 8 types.
	public final static int MAXID = 8;

	/**
	 * Convert from a DocumentType to the corresponding integer
	 * 
	 * @param documentType Document type to convert
	 * @return The integer that corresponds to the DocumentType
	 */
	public static int getInt(DocumentType documentType) {
		switch (documentType) {
		case LETTER:
			return 1;
		case OFFER:
			return 2;
		case ORDER:
			return 3;
		case CONFIRMATION:
			return 4;
		case INVOICE:
			return 5;
		case DELIVERY:
			return 6;
		case CREDIT:
			return 7;
		case DUNNING:
			return 8;
		}
		return 0;
	}

	/**
	 * Convert from a document type String to the corresponding integer
	 * 
	 * @param documentType Document type as string to convert
	 * @return The integer that corresponds to the DocumentType
	 */
	public static int getInt(String documentType) {
		if (getString(LETTER).equals(documentType))
			return getInt(LETTER);
		if (getString(OFFER).equals(documentType))
			return getInt(OFFER);
		if (getString(ORDER).equals(documentType))
			return getInt(ORDER);
		if (getString(CONFIRMATION).equals(documentType))
			return getInt(CONFIRMATION);
		if (getString(INVOICE).equals(documentType))
			return getInt(INVOICE);
		if (getString(DELIVERY).equals(documentType))
			return getInt(DELIVERY);
		if (getString(CREDIT).equals(documentType))
			return getInt(CREDIT);
		if (getString(DUNNING).equals(documentType))
			return getInt(DUNNING);

		return getInt(NONE);
	}

	/**
	 * Gets the corresponding integer of an DocumentType
	 * 
	 * @return The integer that corresponds to the DocumentType
	 */
	public int getInt() {
		return getInt(this);
	}

	/**
	 * Convert from a document type string to a DocumentType 
	 * 
	 * @param documentType String to convert
	 * @return  The DocumentType that corresponds to the String
	 */
	public static DocumentType getType(String documentType) {
		if (isDocumentTypeString(LETTER, documentType))
			return LETTER;
		if (isDocumentTypeString(OFFER, documentType))
			return OFFER;
		if (isDocumentTypeString(ORDER, documentType))
			return ORDER;
		if (isDocumentTypeString(CONFIRMATION, documentType))
			return CONFIRMATION;
		if (isDocumentTypeString(INVOICE, documentType))
			return INVOICE;
		if (isDocumentTypeString(DELIVERY, documentType))
			return DELIVERY;
		if (isDocumentTypeString(CREDIT, documentType))
			return CREDIT;
		if (isDocumentTypeString(DUNNING, documentType))
			return DUNNING;

		return NONE;
	}

	/**
	 * Convert from an integer to a document type localized string
	 * The singular style is used. 
	 * 
	 * @param i Integer to convert
	 * @return  The DocumentType as localized string
	 */
	public static String getString(int i) {
		switch (i) {
		case 1:
			return "Brief";
		case 2:
			return "Angebot";
		case 3:
			return "Bestellung";
		case 4:
			return "Auftragsbestätigung";
		case 5:
			return "Rechnung";
		case 6:
			return "Lieferschein";
		case 7:
			return "Gutschrift";
		case 8:
			return "Mahnung";
		}
		return "";
	}

	/**
	 * Gets the document type as localized string
	 * The singular style is used. 
	 * 
	 * @return The DocumentType as localized string
	 */
	public String getString() {
		return getString(this.getInt());
	}

	/**
	 * Convert from an integer to a document type localized string
	 * The plural style is used. 
	 * 
	 * @param i Integer to convert
	 * @return  The DocumentType as localized string
	 */
	public static String getPluralString(int i) {
		switch (i) {
		case 1:
			return "Briefe";
		case 2:
			return "Angebote";
		case 3:
			return "Bestellungen";
		case 4:
			return "Auftragsbestätigungen";
		case 5:
			return "Rechnungen";
		case 6:
			return "Lieferscheine";
		case 7:
			return "Gutschriften";
		case 8:
			return "Mahnungen";
		}
		return "";
	}

	/**
	 * Convert from an integer to a DocumentType
	 * 
	 * @param i Integer to convert
	 * @return  The DocumentType
	 */
	public static DocumentType getType(int i) {
		switch (i) {
		case 1:
			return LETTER;
		case 2:
			return OFFER;
		case 3:
			return ORDER;
		case 4:
			return CONFIRMATION;
		case 5:
			return INVOICE;
		case 6:
			return DELIVERY;
		case 7:
			return CREDIT;
		case 8:
			return DUNNING;
		}
		return NONE;
	}

	/**
	 * Convert from an integer to a document type non-localized string
	 * The singular style is used. 
	 * 
	 * @param i Integer to convert
	 * @return  The DocumentType as non-localized string
	 */
	public static String getTypeAsString(int i) {
		// do not translate !!
		switch (i) {
		case 1:
			return "Letter";
		case 2:
			return "Offer";
		case 3:
			return "Order";
		case 4:
			return "Confirmation";
		case 5:
			return "Invoice";
		case 6:
			return "Delivery";
		case 7:
			return "Credit";
		case 8:
			return "Dunning";
		}
		return "NONE";
	}

	/**
	 * Convert from Document Type to a document type non-localized string
	 * The singular style is used. 
	 * 
	 * @param documentType DocumentType to convert
	 * @return The DocumentType as non-localized string
	 */
	public static String getTypeAsString(DocumentType documentType) {
		return getTypeAsString(getInt(documentType));
	}

	/**
	 * Get the type as non-localized string
	 * 
	 * @return The DocumentType as non-localized string
	 */
	public String getTypeAsString() {
		return DocumentType.getTypeAsString(this);
	}

	/**
	 * Convert from DocumentType to a document type localized string
	 * The singular style is used. 
	 * 
	 * @param documentType DocumentType to convert
	 * @return The DocumentType as localized string
	 */
	public static String getString(DocumentType documentType) {
		return getString(getInt(documentType));
	}

	/**
	 * Convert from DocumentType to a document type localized string
	 * The plural style is used. 
	 * 
	 * @param documentType DocumentType to convert
	 * @return  The DocumentType as localized string
	 */
	public static String getPluralString(DocumentType documentType) {
		return getPluralString(getInt(documentType));
	}

	/**
	 * Get the DocumentType as plural localized string
	 * 
	 * @return DocumentType as localized string
	 */
	public String getPluralString() {
		return getPluralString(this);
	}

	/**
	 * Compares an DocumentType and a document type String.
	 * The string can describe the type as a singular or plural.
	 * 
	 * @param documentType First compare parameter as DocumentType
	 * @param documentTypeString Second compare parameter as String
	 * @return True, of both are equal
	 */
	private static boolean isDocumentTypeString(DocumentType documentType, String documentTypeString) {
		
		// Remove all trailed signs starting from "/" 
		if (documentTypeString.contains("/") && (documentTypeString.length() > 1))
			documentTypeString = documentTypeString.substring(0, documentTypeString.indexOf("/"));

		// Test, if it is as singular
		if (getString(documentType).equals(documentTypeString))
			return true;

		// Test, if it is as plural
		if (getPluralString(documentType).equals(documentTypeString))
			return true;

		return false;
	}

	/**
	 * JFace DocumentType content provider
	 * Provides all Document types as an String array
	 * 
	 * @author Gerd Bartelt
	 */
	public static class DocumentTypeContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			
			// Get all document types
			ArrayList<String> strings = new ArrayList<String>();
			for (int i = 1; i <= MAXID; i++)
				strings.add(getString(i));
			
			// Convert them to an Array
			return strings.toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
		}

	}

	/**
	 * Defines all Document Types that contains an item table
	 * 
	 * @return True for all types with item table
	 */
	public boolean hasItems() {
		switch (this) {
		case LETTER:
			return false;
		case OFFER:
			return true;
		case ORDER:
			return true;
		case CONFIRMATION:
			return true;
		case INVOICE:
			return true;
		case DELIVERY:
			return true;
		case CREDIT:
			return true;
		case DUNNING:
			return false;
		}
		return false;
	}

	/**
	 * Defines all Document Types that contains a price
	 * 
	 * @return True for all types with a price
	 */
	public boolean hasPrice() {
		switch (this) {
		case LETTER:
			return false;
		case OFFER:
			return true;
		case ORDER:
			return true;
		case CONFIRMATION:
			return true;
		case INVOICE:
			return true;
		case DELIVERY:
			return false;
		case CREDIT:
			return true;
		case DUNNING:
			return false;
		}
		return false;
	}
	
	/**
	 * Defines all Document Types that can be marked as payed
	 * 
	 * @return True for all types with a price
	 */
	public boolean hasPayed() {
		switch (this) {
		case LETTER:
			return false;
		case OFFER:
			return false;
		case ORDER:
			return false;
		case CONFIRMATION:
			return false;
		case INVOICE:
			return true;
		case DELIVERY:
			return false;
		case CREDIT:
			return true;
		case DUNNING:
			return false;
		}
		return false;
	}
	

	/**
	 * Defines all Document Types that contains a reference to
	 * an invoice document.
	 * 
	 * @return True for all types with a reference to an invoice document.
	 */
	public boolean hasInvoiceReference() {
		switch (this) {
		case LETTER:
			return false;
		case OFFER:
			return false;
		case ORDER:
			return false;
		case CONFIRMATION:
			return false;
		case INVOICE:
			return false;
		case DELIVERY:
			return true;
		case CREDIT:
			return true;
		case DUNNING:
			return true;
		}
		return false;
	}

	/**
	 * Defines the sign of a document
	 * 
	 * @return 1 for documents with positive sign, -1 for those with negative sign.
	 */
	public int sign() {
		switch (this) {
		case LETTER:
			return 1;
		case OFFER:
			return 1;
		case ORDER:
			return 1;
		case CONFIRMATION:
			return 1;
		case INVOICE:
			return 1;
		case DELIVERY:
			return 1;
		case CREDIT:
			return -1;
		case DUNNING:
			return 1;
		}
		return 1;
	}

	/**
	 * Get the text to create a new instance of this document
	 * 
	 * @return Text as localized string.
	 */
	public String getNewText() {
		switch (this) {
		case LETTER:
			return "neuer Brief";
		case OFFER:
			return "neues Angebot";
		case ORDER:
			return "neue Bestellung";
		case CONFIRMATION:
			return "neue Auftragsbestätigung";
		case INVOICE:
			return "neue Rechnung";
		case DELIVERY:
			return "neuer Lieferschein";
		case CREDIT:
			return "neue Gutschrift";
		case DUNNING:
			return "neue Mahnung";
		}
		return "neues Dokument";
	}

}
