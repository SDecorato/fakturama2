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

package com.sebulli.fakturama.openoffice;

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

public class OpenOfficeStarter {


	
	static public boolean isValidPath(String preferencePath) {
		File file = new File(OSDependent.getOOBinary(preferencePath));
		return file.isFile();
	}
	
	
	static public IOfficeApplication openOfficeAplication() {
		String preferencePath = Activator.getDefault().getPreferenceStore().getString("OPENOFFICE_PATH");
		
		
		if (!isValidPath(preferencePath)) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			messageBox.setText("Fehler");
			messageBox.setMessage("OpenOffice-Pfad:\n\n" + preferencePath + "\n\nist nicht gültig");
			messageBox.open();
			return null;
		}

		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, OSDependent.getOOExtendetPath(preferencePath));
		configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY, "local");
		IOfficeApplication officeAplication = null;
		try {
			officeAplication = OfficeApplicationRuntime.getApplication(configuration);
			try {
				officeAplication.setConfiguration(configuration);
			} catch (OfficeApplicationException e) {
				Logger.logError(e, "Error configuring OpenOffice");
			}
			try {
				officeAplication.activate();
			} catch (OfficeApplicationException e) {
				Logger.logError(e, "Error activating OpenOffice");
			}
		} catch (OfficeApplicationException e) {
			Logger.logError(e, "Error starting OpenOffice");
		}
		return officeAplication;
	}

}
