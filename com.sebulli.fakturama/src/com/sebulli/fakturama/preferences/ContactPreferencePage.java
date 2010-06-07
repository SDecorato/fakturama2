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
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

public class ContactPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ContactPreferencePage() {
		super(GRID);

	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor("CONTACT_USE_DELIVERY", "Lieferadresse benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_BANK", "Bankdaten benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_MISC", "Seite Sonstiges benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_NOTE", "Seite Hinweis benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_GENDER", "Feld Geschlecht benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_TITLE", "Feld Titel benutzen", getFieldEditorParent()));

		addField(new RadioGroupFieldEditor("CONTACT_NAME_FORMAT", "Format des Namens:", 2, new String[][] { { "Vorname Nachname", "0" },
				{ "Nachname, Vorname", "1" } }, getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_COMPANY", "Feld Firma benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("CONTACT_USE_COUNTRY", "Feld Land benutzen", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Einstellungen für Kontaktdaten");
	}

	public static void setInitValues(IEclipsePreferences node) {
		node.putBoolean("CONTACT_USE_DELIVERY", true);
		node.putBoolean("CONTACT_USE_BANK", false);
		node.putBoolean("CONTACT_USE_MISC", false);
		node.putBoolean("CONTACT_USE_NOTE", true);
		node.putBoolean("CONTACT_USE_GENDER", true);
		node.putBoolean("CONTACT_USE_TITLE", false);
		node.put("CONTACT_NAME_FORMAT", "0");
		node.putBoolean("CONTACT_USE_COMPANY", true);
		node.putBoolean("CONTACT_USE_COUNTRY", true);
	}

}
