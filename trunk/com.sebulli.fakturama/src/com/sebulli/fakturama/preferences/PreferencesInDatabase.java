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

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;

public class PreferencesInDatabase {

	private static void loadPreferenceValue(String key) {
		Activator.getDefault().getPreferenceStore().setValue(key, Data.INSTANCE.getPreferenceValue(key));
	}

	private static void savePreferenceValue(String key) {
		Data.INSTANCE.setPreferenceValue(key, Activator.getDefault().getPreferenceStore().getString(key));
	}

	public static void syncWithPreferencesFromDatabase(String key, boolean write) {
		if (write)
			savePreferenceValue(key);
		else
			loadPreferenceValue(key);
	}

	public static void loadPreferencesFromDatabase() {
		YourCompanyPreferencePage.syncWithPreferencesFromDatabase(false);
		NumberRangePreferencePage.syncWithPreferencesFromDatabase(false);
	}

	public static void savePreferencesInDatabase() {
		YourCompanyPreferencePage.syncWithPreferencesFromDatabase(true);
		NumberRangePreferencePage.syncWithPreferencesFromDatabase(true);
	}

}
