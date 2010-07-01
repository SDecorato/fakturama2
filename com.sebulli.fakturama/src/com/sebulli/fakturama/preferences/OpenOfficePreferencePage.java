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
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.OSDependent;

public class OpenOfficePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public OpenOfficePreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		String defaultValue = Activator.getDefault().getPreferenceStore().getDefaultString("OPENOFFICE_PATH");
		if ( !defaultValue.isEmpty() )
			defaultValue = " (z.B.: " + defaultValue + ")";
		
		if (OSDependent.isOOApp())
			addField(new AppFieldEditor("OPENOFFICE_PATH", "OpenOffice App", getFieldEditorParent()));
		else 
			addField(new DirectoryFieldEditor("OPENOFFICE_PATH", "OpenOffice Ordner" + defaultValue, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Open Office Einstellungen");
	}

	public static void syncWithPreferencesFromDatabase(boolean write) {
	}

	
	public static void getProgramFolder () {
		
	}
	
	public static void setInitValues(IEclipsePreferences node) {
		node.put("OPENOFFICE_PATH", OSDependent.getOODefaultPath());
	}

}