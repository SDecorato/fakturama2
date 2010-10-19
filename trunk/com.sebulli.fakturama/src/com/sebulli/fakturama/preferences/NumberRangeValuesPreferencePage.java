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

package com.sebulli.fakturama.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

/**
 * Preference page for the number settings
 * 
 * @author Gerd Bartelt
 */
public class NumberRangeValuesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public NumberRangeValuesPreferencePage() {
		super(GRID);

	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		addField(new IntegerFieldEditor("NUMBERRANGE_CONTACT_NR", "nächste Kundennummer:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_PRODUCT_NR", "nächste Artikelnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_INVOICE_NR", "nächste Rechnungsnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_DELIVERY_NR", "nächste Lieferscheinnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_OFFER_NR", "nächste Angebotsnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_ORDER_NR", "nächste Bestellungnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_CONFIRMATION_NR", "nächste Auftragsbestätigungsnr:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("NUMBERRANGE_CREDIT_NR", "nächste Gutschriftnr:", getFieldEditorParent()));
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
		setDescription("Nächste Nummer");
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {

		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CONTACT_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_PRODUCT_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_INVOICE_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_DELIVERY_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_OFFER_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_ORDER_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CREDIT_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_CONFIRMATION_NR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("NUMBERRANGE_DUNNING_NR", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
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
