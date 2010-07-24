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

package com.sebulli.fakturama;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.actions.OpenBrowserEditorAction;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.preferences.PreferencesInDatabase;

/**
 * The earlyStartup Member is called after the Fakturama application is started.
 * 
 * @author Gerd Bartelt
 */
public class Startup implements IStartup {

	/**
	 * called after startup of the application
	 */
	@Override
	public void earlyStartup() {

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

				// Opens the web browser editor.
				if (window != null) {
					OpenBrowserEditorAction action = new OpenBrowserEditorAction();
					action.run();
				} else
					window = null;

				
				// If the data base is connected and if it is not new, then some preferences are loaded
				// from the data base.
				boolean connectedToDB = DataBaseConnectionState.INSTANCE.isConnected();
				if (!Data.INSTANCE.getNewDBCreated())
					if (connectedToDB)
						PreferencesInDatabase.loadPreferencesFromDatabase();
			}
		});
	}
	

}
