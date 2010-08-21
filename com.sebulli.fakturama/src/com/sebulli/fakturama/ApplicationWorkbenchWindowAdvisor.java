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

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.sebulli.fakturama.backup.BackupManager;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.openoffice.OOManager;
import com.sebulli.fakturama.preferences.PreferencesInDatabase;

/**
 * Applications workbench window advisor.
 * Here are some methods that are called after a window is opened or
 * before it is closed.
 * 
 * @author Gerd Bartelt
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	/**
	 * Creates a new action bar advisor.
	 * 
	 * @param configurer configurer
	 * @return the new action bar advisor
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	/**
	 * Called before the window is opened.
	 * 
	 * The initial size of the window is set and the 
	 * cool bar and status bar is created.
	 */
	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1200, 800));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);

	}

	/**
	 * Called after the window is opened.
	 * 
	 * The logger gets the information, that the workbench is now opened.
	 */
	@Override
	public void postWindowOpen() {
		Logger.logInfo("Fakturama Workbench opened");
	}

	/**
	 * Called before the window shell is closed.
	 * The open views are closed 
	 */
	@Override
	public boolean preWindowShellClose() {
		return true;
	}
	
	/**
	 * Called after the window shell is closed.
	 * All OpenOffice documents are closed
	 * Some (not all) of the preferences are stored in the data base.
	 * Then the data base is closed.
	 */
	@Override
	public void postWindowClose() {
		
		//Closes all OpenOffice documents 
		OOManager.INSTANCE.closeAll();

		PreferencesInDatabase.savePreferencesInDatabase();
		if (Data.INSTANCE != null)
			Data.INSTANCE.close();
		
		// Create a database backup 
		BackupManager.createBackup();
	}

}
