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
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

public class ProductPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ProductPreferencePage() {
		super(GRID);

	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor("PRODUCT_USE_ITEMNR", "Artikelnummer benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("PRODUCT_USE_DESCRIPTION", "Beschreibung benutzen", getFieldEditorParent()));

		addField(new RadioGroupFieldEditor("PRODUCT_USE_NET_GROSS", "Eingabe von Netto oder Brutto Preisen:", 3, new String[][] { { "Netto", "1" },
				{ "Brutto", "2" }, { "Netto und Brutto", "0" } }, getFieldEditorParent()));

		addField(new ComboFieldEditor("PRODUCT_SCALED_PRICES", "Staffelpreise:", new String[][] { { "keine", "1" }, { "2", "2" }, { "3", "3" }, { "4", "4" },
				{ "5", "5" } }, getFieldEditorParent()));

		addField(new BooleanFieldEditor("PRODUCT_USE_VAT", "Mehrwertsteuer auswählen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("PRODUCT_USE_WEIGHT", "Gewichtsangabe benutzen", getFieldEditorParent()));

		addField(new BooleanFieldEditor("PRODUCT_USE_PICTURE", "Produktbild benutzen", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Einstellungen zum Produktdialog");
	}

	public static void setInitValues(IEclipsePreferences node) {
		node.putBoolean("PRODUCT_USE_ITEMNR", true);
		node.putBoolean("PRODUCT_USE_DESCRIPTION", true);
		node.put("PRODUCT_USE_NET_GROSS", "2");
		node.put("PRODUCT_SCALED_PRICES", "1");
		node.putBoolean("PRODUCT_USE_VAT", true);
		node.putBoolean("PRODUCT_USE_WEIGHT", false);
		node.putBoolean("PRODUCT_USE_PICTURE", true);
	}

}
