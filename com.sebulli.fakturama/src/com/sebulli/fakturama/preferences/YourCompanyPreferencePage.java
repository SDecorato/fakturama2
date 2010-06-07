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
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

public class YourCompanyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public YourCompanyPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_NAME", "Firmenname", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_OWNER", "Inhaber", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_STREET", "Stra§e Nr.", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_ZIP", "PLZ.", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_CITY", "Ort", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_TEL", "Telefon", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_FAX", "Telefax", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_EMAIL", "E-Mail", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_WEBSITE", "Webseite", getFieldEditorParent()));

		addField(new StringFieldEditor("YOURCOMPANY_COMPANY_VATNR", "USt.Id-Nr.", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Ihre Firmendaten");
	}

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

	public static void setInitValues(IEclipsePreferences node) {
	}

}
