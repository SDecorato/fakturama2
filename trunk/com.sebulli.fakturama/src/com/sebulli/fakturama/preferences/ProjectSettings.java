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

import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.logger.Logger;

public enum ProjectSettings {
	SETTINGS;

	private boolean dbOpened = false;

	ProjectSettings() {
	}

	public static void showWorkingDirInTitleBar() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell().setText(
					"Fakturama - " + Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE"));
		} catch (Exception e) {
			Logger.logError("Pages");
			System.out.println("Pages:");
			System.out.println(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
			System.out.println(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages().length);
			
		}
		;
	}

	public void setDataBaseOpened() {
		dbOpened = true;
	}

	public boolean getDataBaseOpened() {
		return (dbOpened);
	}

}
