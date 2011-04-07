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

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.sebulli.fakturama.backup.BackupManager;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.openoffice.OOManager;
import com.sebulli.fakturama.preferences.PreferencesInDatabase;

/**
 * Applications workbench window advisor. Here are some methods that are called
 * after a window is opened or before it is closed.
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
	 * @param configurer
	 *            configurer
	 * @return the new action bar advisor
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	/**
	 * Called before the window is opened.
	 * 
	 * The initial size of the window is set and the cool bar and status bar is
	 * created.
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
		Workspace.INSTANCE.showWorkingDirInTitleBar();
	}

	/**
	 * Called before the window shell is closed. The open views are closed
	 */
	@Override
	public boolean preWindowShellClose() {
		return true;
	}

	/**
	 * Called after the window shell is closed. All OpenOffice documents are
	 * closed Some (not all) of the preferences are stored in the data base.
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
