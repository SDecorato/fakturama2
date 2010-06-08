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

package com.sebulli.fakturama.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.preferences.ProjectSettings;

public class SelectWorkspaceAction extends Action {

	public SelectWorkspaceAction() {
		super("Arbeitsverzeichnis auswählen");
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SELECT_WORKSPACE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_SELECT_WORKSPACE);
		// setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/products_16.png"));
	}

	@Override
	public void run() {
		DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		directoryDialog.setFilterPath("/");
		directoryDialog.setMessage("Bitte wählen Sie ein Dateiverzeichnis aus, in dem die Firmendaten abgelegt werden.");
		directoryDialog.setText("Arbeitsverzeichnis auswählen");
		String selectedDirectory = directoryDialog.open();

		if (selectedDirectory != null) {
			if (selectedDirectory.equals("/"))
				selectedDirectory = "";
			if (selectedDirectory.equals("\\"))
				selectedDirectory = "";
			if (!selectedDirectory.isEmpty()) {
				if (ProjectSettings.SETTINGS.getDataBaseOpened()) {
					Activator.getDefault().getPreferenceStore().setValue("GENERAL_WORKSPACE_REQUEST", selectedDirectory);
					MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION);
					messageBox.setText("Hinweis");
					messageBox.setMessage("Das Arbeitsverzeichnis wird gewechselt.\nBitte starten Sie Fakturama neu !");
					messageBox.open();

					PlatformUI.getWorkbench().close();
				} else {
					Activator.getDefault().getPreferenceStore().setValue("GENERAL_WORKSPACE", selectedDirectory);
					ProjectSettings.showWorkingDirInTitleBar();
				}
			}
		}

		if (Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE").isEmpty())
			PlatformUI.getWorkbench().close();
	}
}