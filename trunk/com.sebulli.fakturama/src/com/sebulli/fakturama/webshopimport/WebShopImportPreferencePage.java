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

package com.sebulli.fakturama.webshopimport;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.preferences.PreferencesInDatabase;

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

		//T: Preference page "Web Shop Import" - Label
		addField(new StringFieldEditor("WEBSHOP_URL", _("Webshop URL"), getFieldEditorParent()));

		//T: Preference page "Web Shop Import" - Label
		addField(new StringFieldEditor("WEBSHOP_USER", _("Username"), getFieldEditorParent()));

		//T: Preference page "Web Shop Import" - Label
		addField(new StringFieldEditor("WEBSHOP_PASSWORD", _("Password"), getFieldEditorParent()));

		//T: Preference page "Web Shop Import" - Label
		addField(new StringFieldEditor("WEBSHOP_PRODUCT_CATEGORY", _("Products in category:"), getFieldEditorParent()));

		//T: Preference page "Web Shop Import" - Label
		addField(new StringFieldEditor("WEBSHOP_CONTACT_CATEGORY", _("Customers in category:"), getFieldEditorParent()));

		//T: Preference page "Web Shop Import" - Label
		addField(new StringFieldEditor("WEBSHOP_SHIPPING_CATEGORY", _("Shippings in category:"), getFieldEditorParent()));

		//T: Preference page "Web Shop Import" - Label
		addField(new BooleanFieldEditor("WEBSHOP_NOTIFY_PROCESSING", _("Notify customer on 'In Work'"), getFieldEditorParent()));
		//T: Preference page "Web Shop Import" - Label
		addField(new BooleanFieldEditor("WEBSHOP_NOTIFY_SHIPPED", _("Notify customer on 'Shipped'"), getFieldEditorParent()));
	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//T: Preference page "Web Shop Import" - Title
		setDescription(_("Import from web shop"));
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_URL", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_USER", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_PASSWORD", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_PRODUCT_CATEGORY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_CONTACT_CATEGORY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_SHIPPING_CATEGORY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_NOTIFY_PROCESSING", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("WEBSHOP_NOTIFY_SHIPPED", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("WEBSHOP_URL", "fakturama.sebulli.com/shop/admin/webshop_export.php");
		node.put("WEBSHOP_USER", "user");
		node.put("WEBSHOP_PASSWORD", "password");
		//T: Preference page "Web Shop Import" - Default value "Product Category"
		node.put("WEBSHOP_PRODUCT_CATEGORY", _("Shop"));
		//T: Preference page "Web Shop Import" - Default value "Contact Category"
		node.put("WEBSHOP_CONTACT_CATEGORY", _("Shop Customer"));
		//T: Preference page "Web Shop Import" - Default value "Shipping Category"
		node.put("WEBSHOP_SHIPPING_CATEGORY", _("Shop"));
		node.putBoolean("WEBSHOP_NOTIFY_PROCESSING", false);
		node.putBoolean("WEBSHOP_NOTIFY_SHIPPED", true);

	}

}
