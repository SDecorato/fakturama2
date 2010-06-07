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
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

public class DocumentPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DocumentPreferencePage() {
		super(GRID);

	}

	@Override
	public void createFieldEditors() {

		addField(new RadioGroupFieldEditor("DOCUMENT_USE_NET_GROSS", "Preise in Artikelliste:", 2, new String[][] { { "Netto", "0" }, { "Brutto", "1" } },
				getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Einstellungen der Dokumente");
	}

	public static void setInitValues(IEclipsePreferences node) {
		node.put("DOCUMENT_USE_NET_GROSS", "0");
	}

}
