/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.misc.CountryCodes;

/**
 * Does some update jobs
 * 
 * @author Gerd Bartelt
 *
 */
public class Updater {

	
	/**
	 * Default constructor
	 */
	public Updater() {
	}
	
	/**
	 * Check, if something should be updated
	 */
	public void checkVersion() {
		// Get the actual version from the bundle
		Version plugInVersion = new Version(Platform.getBundle("com.sebulli.fakturama").getHeaders().get("Bundle-Version").toString());
		// Get the last version from the data base
		Version dataBaseVersion = new Version(Data.INSTANCE.getProperty("bundleversion"));
		
		// The plugin version is newer
		if (plugInVersion.compareTo(dataBaseVersion) >= 1) 
		{
			// Load the country codes
			CountryCodes.update("1.5");
			// Update the entry in the data base
			Data.INSTANCE.setProperty("bundleversion", plugInVersion.toString());
		}
	}
	
	
}