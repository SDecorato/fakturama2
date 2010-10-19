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
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

/**
 * Preference page for the greetings
 * 
 * @author Gerd Bartelt
 */
public class ContactFormatPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public ContactFormatPreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {

		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_COMMON", "Allgemeine Grußformel", getFieldEditorParent()));

		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_MR", "Grußformel Herr", getFieldEditorParent()));

		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_MRS", "Grußformel Frau", getFieldEditorParent()));

		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_COMPANY", "Grußformel Firma", getFieldEditorParent()));

		addField(new StringFieldEditor("CONTACT_FORMAT_ADDRESS", "Adressfeld", getFieldEditorParent()));
		addField(new StringFieldEditor("CONTACT_FORMAT_HIDE_COUNTRIES", "Länder ausblenden", getFieldEditorParent()));

	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Grußformeln\n\n" + "Beispiel für Format:\nSehr geehrter Herr {title} {firstname} {lastname}\n");

	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_GREETING_COMMON", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_GREETING_MR", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_GREETING_MRS", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_GREETING_COMPANY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_ADDRESS", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_HIDE_COUNTRIES", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("CONTACT_FORMAT_GREETING_COMMON", "Sehr geehrter Damen und Herren");
		node.put("CONTACT_FORMAT_GREETING_MR", "Sehr geehrter Herr {firstname} {lastname}");
		node.put("CONTACT_FORMAT_GREETING_MRS", "Sehr geehrte Frau {firstname} {lastname}");
		node.put("CONTACT_FORMAT_GREETING_COMPANY", "Sehr geehrter Damen und Herren");
		node.put("CONTACT_FORMAT_ADDRESS", "{company}<br>{title} {firstname} {lastname}<br>{street}<br>{countrycode}{zip} {city}<br>{country}");
		node.put("CONTACT_FORMAT_HIDE_COUNTRIES", "Deutschland,Germany");

	}

}
