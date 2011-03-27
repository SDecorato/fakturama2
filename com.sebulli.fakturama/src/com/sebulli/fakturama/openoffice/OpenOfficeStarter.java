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

package com.sebulli.fakturama.openoffice;

import static com.sebulli.fakturama.Translate._;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.internal.application.ApplicationAssistant;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.OSDependent;
import com.sebulli.fakturama.logger.Logger;

/**
 * Starts the OpenOffice Application from the application's path
 * 
 * @author Gerd Bartelt
 */
public class OpenOfficeStarter {

	/**
	 * Returns, if the Application exists
	 * 
	 * @param preferencePath
	 *            The path from the preference store
	 * @return TRUE, if it exists.
	 */
	static public boolean isValidPath(String preferencePath) {
		Logger.logInfo("OOdebug:200");

		File file = new File(OSDependent.getOOBinary(preferencePath));
		Logger.logInfo("OOdebug:201");
		return file.isFile();
	}

	/**
	 * Get the path of the OpenOffice installation 
	 * 
	 * @return
	 */
	static public String getHome () {
		Logger.logInfo("OOdebug:202");

		IApplicationAssistant applicationAssistant;
		
		// Return an empty string, if no OpenOffice was found
		String home = "";
		
		try {
			Logger.logInfo("OOdebug:203");

			applicationAssistant = new ApplicationAssistant();
			ILazyApplicationInfo appInfo = applicationAssistant.getLatestLocalApplication();
			Logger.logInfo("OOdebug:204");

			// An OpenOffice installation was found
			if (appInfo!=null) { 
				Logger.logInfo("OOdebug:205");
				home = appInfo.getHome();
			}
			Logger.logInfo("OOdebug:206");

		}
		catch (OfficeApplicationException e) {
			Logger.logInfo("OOdebug:207");
			Logger.logError(e, "207");
		}
		Logger.logInfo("OOdebug:208");

		return home;
	}

	
	/**
	 * Opens the OpenOffice application
	 * 
	 * @return Reference to the OpenOffice application object
	 */
	static public IOfficeApplication openOfficeAplication() {

		// Get the path to the application set in the preference store
		String preferencePath = Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_PATH");
		Logger.logInfo("OOdebug:209");

		// Show a message (and exit), if there is no OpenOffice found
		if (!isValidPath(preferencePath)) {
			Logger.logInfo("OOdebug:210");

			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			
			//T: Title of the Message Box that appears if the OpenOffice path is invalid.
			messageBox.setText(_("Error"));

			//T: Text of the Message Box that appears if the OpenOffice path is invalid.
			//T: Format: OpenOffice path ... is invalid.
			messageBox.setMessage(_("OpenOffice-Path:") + "\n\n" + preferencePath + "\n\n"+
					//T: Text of the Message Box that appears if the OpenOffice path is invalid.
					//T: Format: OpenOffice path ... is invalid.
					_("is invalid"));
			messageBox.open();
			Logger.logInfo("OOdebug:211");

			return null;
		}
		Logger.logInfo("OOdebug:212");

		// Activate the OpenOffice Application
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, preferencePath);
		configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY, "local");
		IOfficeApplication officeAplication = null;
		try {
			Logger.logInfo("OOdebug:213");

			// Get the application
			officeAplication = OfficeApplicationRuntime.getApplication(configuration);
			Logger.logInfo("OOdebug:214");

			// Configure it
			try {
				officeAplication.setConfiguration(configuration);
				Logger.logInfo("OOdebug:215");

			}
			catch (OfficeApplicationException e) {
				Logger.logError(e, "Error configuring OpenOffice");
			}

			// And activate it
			try {
				Logger.logInfo("OOdebug:216");

				officeAplication.activate();
				Logger.logInfo("OOdebug:217");

			}
			catch (OfficeApplicationException e) {
				Logger.logError(e, "Error activating OpenOffice");
			}
		}
		catch (OfficeApplicationException e) {
			Logger.logError(e, "Error starting OpenOffice");
		}
		Logger.logInfo("OOdebug:218");

		//Return the Application
		return officeAplication;
	}

}
