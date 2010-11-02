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

package com.sebulli.fakturama.openoffice;

import static com.sebulli.fakturama.Translate._;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;

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
		File file = new File(OSDependent.getOOBinary(preferencePath));
		return file.isFile();
	}

	/**
	 * Opens the OpenOffice application
	 * 
	 * @return Reference to the OpenOffice application object
	 */
	static public IOfficeApplication openOfficeAplication() {

		// Get the path to the application set in the preference store
		String preferencePath = Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_PATH");

		// Show a message (and exit), if there is no OpenOffice found
		if (!isValidPath(preferencePath)) {
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
			return null;
		}

		// Activate the OpenOffice Application
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, preferencePath);
		configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY, "local");
		IOfficeApplication officeAplication = null;
		try {

			// Get the application
			officeAplication = OfficeApplicationRuntime.getApplication(configuration);

			// Configure it
			try {
				officeAplication.setConfiguration(configuration);
			}
			catch (OfficeApplicationException e) {
				Logger.logError(e, "Error configuring OpenOffice");
			}

			// And activate it
			try {
				officeAplication.activate();
			}
			catch (OfficeApplicationException e) {
				Logger.logError(e, "Error activating OpenOffice");
			}
		}
		catch (OfficeApplicationException e) {
			Logger.logError(e, "Error starting OpenOffice");
		}

		//Return the Application
		return officeAplication;
	}

}
