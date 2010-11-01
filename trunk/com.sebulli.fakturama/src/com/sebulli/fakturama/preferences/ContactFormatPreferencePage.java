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

import static com.sebulli.fakturama.Translate._;

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

		//T: Preference page "Contact Format" - label "Common Salutation"
		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_COMMON", _("Common Salutation"), getFieldEditorParent()));

		//T: Preference page "Contact Format" - label "Salutation for men"
		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_MR", _("Salutation Men"), getFieldEditorParent()));

		//T: Preference page "Contact Format" - label "Salutation for woman"
		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_MS", _("Salutation Women"), getFieldEditorParent()));

		//T: Preference page "Contact Format" - label "Salutation for companies"
		addField(new StringFieldEditor("CONTACT_FORMAT_GREETING_COMPANY", _("Salutation Company"), getFieldEditorParent()));

		//T: Preference page "Contact Format" - label "Format of the address field"
		addField(new StringFieldEditor("CONTACT_FORMAT_ADDRESS", _("Address Field"), getFieldEditorParent()));

		//T: Preference page "Contact Format" - label "List of the countries whose names are not printed in the address label"
		addField(new StringFieldEditor("CONTACT_FORMAT_HIDE_COUNTRIES", _("Hide this Countries"), getFieldEditorParent()));

	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());

		//T: Preference page "Contact Format" - Title of this page with an example
		//T: how to format the address field. Use \n to separate lines.
		setDescription(_("Format of the address field\n\nEample:\nDear Mr. {title} {firstname} {lastname}\n"));

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
		PreferencesInDatabase.syncWithPreferencesFromDatabase("CONTACT_FORMAT_GREETING_MS", write);
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
		
		//T: Preference page "Contact Format" - Example format Strings (Common Salutation)
		node.put("CONTACT_FORMAT_GREETING_COMMON", "Sehr geehrter Damen und Herren");

		//T: Preference page "Contact Format" - Example format Strings (Salutation Men)
		node.put("CONTACT_FORMAT_GREETING_MR", "Sehr geehrter Herr {firstname} {lastname}");

		//T: Preference page "Contact Format" - Example format Strings (Salutation Women)
		node.put("CONTACT_FORMAT_GREETING_MS", "Sehr geehrte Frau {firstname} {lastname}");

		//T: Preference page "Contact Format" - Example format Strings (Salutation Company)
		node.put("CONTACT_FORMAT_GREETING_COMPANY", "Sehr geehrter Damen und Herren");
		
		//T: Preference page "Contact Format" - Example format Strings (Address format)
		node.put("CONTACT_FORMAT_ADDRESS", "{company}<br>{title} {firstname} {lastname}<br>{street}<br>{countrycode}{zip} {city}<br>{country}");
		
		//T: Preference page "Contact Format" - Example format Strings (Hidden countries)
		//T: Separate the country by a comma. 
		//T: If the county name is one in this list, is won't be displayed in the address
		//T: field. E.g. for a German language you should enter "Deutschland,Germany".
		//T: There should be at least 2 names, separated by a comma. So that the user
		//T: can see the format. Even if 2 countries don't make much sense like 
		//T: USA,U.S.A. for the English language.
		node.put("CONTACT_FORMAT_HIDE_COUNTRIES", _("USA,U.S.A."));

	}

}
