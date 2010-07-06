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

package com.sebulli.fakturama.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;

/**
 * Preference page for the number settings
 * 
 * @author Gerd Bartelt
 */
public class NumberRangePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public NumberRangePreferencePage() {
		super(GRID);

	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor("NUMBERRANGE_CONTACT_FORMAT", "Format Kundennummer:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_CONTACT_NR", "nächste Kundennummer:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_PRODUCT_FORMAT", "Format Artikelnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_PRODUCT_NR", "nächste Artikelnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_INVOICE_FORMAT", "Format Rechnungsnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_INVOICE_NR", "nächste Rechnungsnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_DELIVERY_FORMAT", "Format Lieferscheinnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_DELIVERY_NR", "nächste Lieferscheinnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_OFFER_FORMAT", "Format Angebotsnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_OFFER_NR", "nächste Angebotsnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_ORDER_FORMAT", "Format Bestellungnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_ORDER_NR", "nächste Bestellungnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_CONFIRMATION_FORMAT", "Format Auftragsbestätigungsnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_CONFIRMATION_NR", "nächste Auftragsbestätigungsnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_CREDIT_FORMAT", "Format Gutschriftnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_CREDIT_NR", "nächste Gutschriftnr:", getFieldEditorParent()));

		addField(new StringFieldEditor("NUMBERRANGE_DUNNING_FORMAT", "Format Mahnungsnr.:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_DUNNING_NR", "nächste Mahnungsnr:", getFieldEditorParent()));
	}

	/**
	 * Initializes this preference page for the given workbench. 
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Nummernkreise für Dokumente" + "\n\n"
				+ "Beispiel für Format:  RE{6nr}\nNummer wird 6 stellig angezeigt, mit führendem \"RE\" : RE000001" + "\n");
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {

		// TODO: remove (for debugging only)
		if (Activator.getDefault().getPreferenceStore().getString("NUMBERRANGE_CONTACT_FORMAT").isEmpty()) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			messageBox.setText("Fehler");
			messageBox.setMessage("leere NUMBERRANGE_CONTACT_FORMAT");
			messageBox.open();
		}

		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CONTACT_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CONTACT_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_PRODUCT_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_PRODUCT_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_INVOICE_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_INVOICE_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_DELIVERY_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_DELIVERY_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_OFFER_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_OFFER_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_ORDER_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_ORDER_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CREDIT_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CREDIT_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CONFIRMATION_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CONFIRMATION_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_DUNNING_FORMAT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_DUNNING_NR", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("NUMBERRANGE_CONTACT_FORMAT", "");
		node.put("NUMBERRANGE_CONTACT_FORMAT", "KD{6nr}");
		node.put("NUMBERRANGE_PRODUCT_FORMAT", "{6nr}");
		node.put("NUMBERRANGE_INVOICE_FORMAT", "RE{6nr}");
		node.put("NUMBERRANGE_DELIVERY_FORMAT", "LS{6nr}");
		node.put("NUMBERRANGE_OFFER_FORMAT", "AG{6nr}");
		node.put("NUMBERRANGE_ORDER_FORMAT", "BS{6nr}");
		node.put("NUMBERRANGE_CREDIT_FORMAT", "GS{6nr}");
		node.put("NUMBERRANGE_CONFIRMATION_FORMAT", "AB{6nr}");
		node.put("NUMBERRANGE_DUNNING_FORMAT", "MG{6nr}");
		node.put("NUMBERRANGE_CONTACT_NR", "0");
		node.put("NUMBERRANGE_PRODUCT_NR", "0");
		node.put("NUMBERRANGE_INVOICE_NR", "0");
		node.put("NUMBERRANGE_DELIVERY_NR", "0");
		node.put("NUMBERRANGE_OFFER_NR", "0");
		node.put("NUMBERRANGE_ORDER_NR", "0");
		node.put("NUMBERRANGE_CREDIT_NR", "0");
		node.put("NUMBERRANGE_CONFIRMATION_NR", "0");
		node.put("NUMBERRANGE_DUNNING_NR", "0");
	}

}
