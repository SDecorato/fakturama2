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

/*
 * Possible Types for class UniData
 */
public enum DocumentType {
	NONE, LETTER, OFFER, ORDER, CONFIRMATION, INVOICE, DELIVERY, CREDIT, DUNNING;

	private final static int MAXID = 8;

	private Integer[] documentTypes = { 1, 2, 3, 4, 5, 6, 7, 8 };

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

	public int getInt() {
		return getInt(this);
	}

	private static boolean isDocumentTypeString(DocumentType documentType, String documentTypeString) {
		if (documentTypeString.contains("/") && (documentTypeString.length() > 1))
			documentTypeString = documentTypeString.substring(0, documentTypeString.indexOf("/"));

		if (getString(documentType).equals(documentTypeString))
			return true;

		if (getPluralString(documentType).equals(documentTypeString))
			return true;

		return false;
	}

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

	public String getString() {
		return getString(this.getInt());
	}

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

	public static String getTypeAsString(DocumentType documentType) {
		return getTypeAsString(getInt(documentType));
	}

	public String getTypeAsString() {
		return DocumentType.getTypeAsString(this);
	}

	public static String getString(DocumentType documentType) {
		return getString(getInt(documentType));
	}

	public static String getPluralString(DocumentType documentType) {
		return getPluralString(getInt(documentType));
	}

	public String getPluralString() {
		return getPluralString(this);
	}

	public static class DocumentTypeContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			ArrayList<String> strings = new ArrayList<String>();
			for (int i = 1; i <= MAXID; i++)
				strings.add(getString(i));
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

	public Integer[] getDocumentTypes() {
		return documentTypes;

	}

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
