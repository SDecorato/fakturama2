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
 * Preference page for the company settings
 * 
 * @author Gerd Bartelt
 */
public class YourCompanyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public YourCompanyPreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_NAME", "Firmenname", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_OWNER", "Inhaber", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_STREET", "Straße Nr.", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_ZIP", "PLZ.", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_CITY", "Ort", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_TEL", "Telefon", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_FAX", "Telefax", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_EMAIL", "E-Mail", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_WEBSITE", "Webseite", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_VATNR", "USt.Id-Nr.", getFieldEditorParent()));

	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Ihre Firmendaten");
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_OWNER", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_STREET", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_ZIP", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_CITY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_TEL", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_FAX", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_EMAIL", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_WEBSITE", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("YOURCOMPANY_COMPANY_VATNR", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
	}

}
