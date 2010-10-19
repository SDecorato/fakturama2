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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.exportsales.ExportSalesPreferencePage;
import com.sebulli.fakturama.webshopimport.WebShopImportPreferencePage;

/**
 * Initializes the preference pages with default values
 * 
 * @author Gerd Bartelt
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * This method is called by the preference initializer to initialize default
	 * preference values. Clients should get the correct node for their bundle
	 * and then set the default values on it.
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
	 *      #initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {

		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);

		// Initialize every single preference page
		ContactPreferencePage.setInitValues(node);
		ContactFormatPreferencePage.setInitValues(node);
		NumberRangeValuesPreferencePage.setInitValues(node);
		NumberRangeFormatPreferencePage.setInitValues(node);
		ProductPreferencePage.setInitValues(node);
		YourCompanyPreferencePage.setInitValues(node);
		WebShopImportPreferencePage.setInitValues(node);
		DocumentPreferencePage.setInitValues(node);
		OpenOfficePreferencePage.setInitValues(node);
		ExportSalesPreferencePage.setInitValues(node);

	}
}