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

/**
 * Preference page for the webshop settings
 * 
 * @author Gerd Bartelt
 */
public class WebShopImportPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public WebShopImportPreferencePage() {
		super(GRID);

	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor("WEBSHOP_URL", "Webshop Url", getFieldEditorParent()));

		addField(new StringFieldEditor("WEBSHOP_USER", "Username", getFieldEditorParent()));

		addField(new StringFieldEditor("WEBSHOP_PASSWORD", "Passwort", getFieldEditorParent()));

		addField(new StringFieldEditor("WEBSHOP_PRODUCT_CATEGORY", "Produkte in Kategorie:", getFieldEditorParent()));

		addField(new StringFieldEditor("WEBSHOP_CONTACT_CATEGORY", "Kunden in Kategorie:", getFieldEditorParent()));

		addField(new StringFieldEditor("WEBSHOP_SHIPPING_CATEGORY", "Versandart in Kategorie:", getFieldEditorParent()));
	}

	/**
	 * Initializes this preference page for the given workbench. 
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Einstellungen zum Importieren aus dem Webshop");
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("WEBSHOP_URL", "www.yourdomain.com/catalog/admin/webshop_export.php");
		node.put("WEBSHOP_USER", "user");
		node.put("WEBSHOP_PASSWORD", "password");
		node.put("WEBSHOP_PRODUCT_CATEGORY", "Shop");
		node.put("WEBSHOP_CONTACT_CATEGORY", "Shop Kunden");
		node.put("WEBSHOP_SHIPPING_CATEGORY", "Shop");
	}

}
