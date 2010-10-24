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

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.webshopimport.WebShopImportPreferencePage;

/**
 * Write or read preference settings to or from the data base
 * 
 * @author Gerd Bartelt
 */
public class PreferencesInDatabase {

	/**
	 * Load one preference from the data base
	 * 
	 * @param key
	 *            The key of the preference value
	 */
	private static void loadPreferenceValue(String key) {
		if (Data.INSTANCE.isExistingProperty(key))
			Activator.getDefault().getPreferenceStore().setValue(key, Data.INSTANCE.getProperty(key));
	}

	/**
	 * Save one preference to the data base
	 * 
	 * @param key
	 *            The key of the preference value
	 */
	private static void savePreferenceValue(String key) {
		String s = Activator.getDefault().getPreferenceStore().getString(key);
		if (s != null && Data.INSTANCE != null)
			Data.INSTANCE.setProperty(key, s);
	}

	/**
	 * Write to or read from the data base
	 * 
	 * @param key
	 *            The key to read or to write
	 * @param write
	 *            True, if the value should be written
	 */
	public static void syncWithPreferencesFromDatabase(String key, boolean write) {
		if (write)
			savePreferenceValue(key);
		else
			loadPreferenceValue(key);
	}

	/**
	 * Load or save all preference values from database of the following
	 * preference pages.
	 */
	public static void loadOrSavePreferencesFromOrInDatabase(boolean save) {
		ContactFormatPreferencePage.syncWithPreferencesFromDatabase(save);
		ContactPreferencePage.syncWithPreferencesFromDatabase(save);
		DocumentPreferencePage.syncWithPreferencesFromDatabase(save);
		GeneralPreferencePage.syncWithPreferencesFromDatabase(save);
		NumberRangeFormatPreferencePage.syncWithPreferencesFromDatabase(save);
		NumberRangeValuesPreferencePage.syncWithPreferencesFromDatabase(save);
		OpenOfficePreferencePage.syncWithPreferencesFromDatabase(save);
		ProductPreferencePage.syncWithPreferencesFromDatabase(save);
		WebShopImportPreferencePage.syncWithPreferencesFromDatabase(save);
		YourCompanyPreferencePage.syncWithPreferencesFromDatabase(save);
		ExportSalesPreferencePage.syncWithPreferencesFromDatabase(save);
	}

	/**
	 * Load all preference values from database of the following preference
	 * pages.
	 */
	public static void loadPreferencesFromDatabase() {
		loadOrSavePreferencesFromOrInDatabase(false);
	}

	/**
	 * Write all preference values to database of the following preference
	 * pages.
	 */
	public static void savePreferencesInDatabase() {
		loadOrSavePreferencesFromOrInDatabase(true);
	}

}
