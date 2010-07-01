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
import com.sebulli.fakturama.actions.SelectWorkspaceAction;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.logger.Logger;
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

				// Checks, if the workspace request is set.
				// If yes, the workspace is set to this value and the request value is cleared.
				// This mechanism is used, because the workspace can only be changed by restarting the application.
				String requestedWorkspace = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE_REQUEST");
				if (!requestedWorkspace.isEmpty()) {
					Activator.getDefault().getPreferenceStore().setValue("GENERAL_WORKSPACE_REQUEST", "");
					Activator.getDefault().getPreferenceStore().setValue("GENERAL_WORKSPACE", requestedWorkspace);
				}

				// Checks, if the workspace is set.
				// If not, the SelectWorkspaceAction is started to select it.
				if (Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE").isEmpty()) {
					SelectWorkspaceAction selectWorkspaceAction = new SelectWorkspaceAction();
					selectWorkspaceAction.run();
				}

				// If the data base is connected and if it is not new, then some preferences are loaded
				// from the data base.
				if (!Data.INSTANCE.getNewDBCreated() && Data.INSTANCE.getDataBaseOpened())
					PreferencesInDatabase.loadPreferencesFromDatabase();
				
				// Show the working directory in the title bar
				showWorkingDirInTitleBar();
			}
		});
	}
	
	/**
	 * Displays the current workspace in the title bar
	 */
	public static void showWorkingDirInTitleBar() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell().setText(
					"Fakturama - " + Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE"));
		} catch (Exception e) {
			Logger.logError("Pages");
			System.out.println("Pages:");
			System.out.println(PlatformUI.getWorkbench());
			System.out.println(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			System.out.println(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages().length);
			System.out.println(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
			
		}
	}

}