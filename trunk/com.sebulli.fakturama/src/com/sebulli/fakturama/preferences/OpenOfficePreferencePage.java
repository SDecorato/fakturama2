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
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.OSDependent;

/**
 * Preference page for the OpenOffice settings
 * 
 * @author Gerd Bartelt
 */
public class OpenOfficePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public OpenOfficePreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		String defaultValue = Activator.getDefault().getPreferenceStore().getDefaultString("OPENOFFICE_PATH");
		if (!defaultValue.isEmpty())
			//T: Preference page "OpenOffice" - Label: Example of the default path. Format: (e.g. PATH ).
			//T: Only the "e.g." is translated
			defaultValue = " (" + _("z.B.:") + " " + defaultValue + ")";

		if (OSDependent.isOOApp())
			//T: Preference page "OpenOffice" - Label: OpenOffice App
			addField(new AppFieldEditor("OPENOFFICE_PATH", _("OpenOffice App"), getFieldEditorParent()));
		else
			//T: Preference page "OpenOffice" - Label: OpenOffice folder
			addField(new DirectoryFieldEditor("OPENOFFICE_PATH", _("OpenOffice folder") + defaultValue, getFieldEditorParent()));

		//T: Preference page "OpenOffice" - Label: Export documents as ODT or as PDF / only ODT/PDF or both
		addField(new RadioGroupFieldEditor("OPENOFFICE_ODT_PDF", _("Export document as ODT or PDF:"), 3, new String[][] { { _("only as ODT"), "ODT" },
				{ _("only as PDF"), "PDF" }, { _("ODT and PDF"), "ODT+PDF" } }, getFieldEditorParent()));

	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//T: Preference page "OpenOffice" - Title"
		setDescription(_("OpenOffice Settings"));
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("OPENOFFICE_PATH", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("OPENOFFICE_ODT_PDF", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("OPENOFFICE_PATH", OSDependent.getOODefaultPath());
		node.put("OPENOFFICE_ODT_PDF", "ODT+PDF");
	}

}
