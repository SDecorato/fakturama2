/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.sebulli.fakturama.Activator;
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
		DocumentPreferencePage.setInitValues(node);
		GeneralPreferencePage.setInitValues(node);
		NumberRangeValuesPreferencePage.setInitValues(node);
		NumberRangeFormatPreferencePage.setInitValues(node);
		OpenOfficePreferencePage.setInitValues(node);
		ProductPreferencePage.setInitValues(node);
		WebShopImportPreferencePage.setInitValues(node);
		YourCompanyPreferencePage.setInitValues(node);
		ExportSalesPreferencePage.setInitValues(node);

	}
}
