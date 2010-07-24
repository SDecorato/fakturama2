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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.views.TemporaryViews;

public enum Workspace {
	INSTANCE;
	
	String workspace = "";
	IPreferenceStore preferences;
	
	Workspace () {
		
		preferences = Activator.getDefault().getPreferenceStore();
		workspace = preferences.getString("GENERAL_WORKSPACE");
			
		// Checks, if the workspace request is set.
		// If yes, the workspace is set to this value and the request value is cleared.
		// This mechanism is used, because the workspace can only be changed by restarting the application.
		String requestedWorkspace = preferences.getString("GENERAL_WORKSPACE_REQUEST");
		if (!requestedWorkspace.isEmpty()) {
			preferences.setValue("GENERAL_WORKSPACE_REQUEST", "");
			setWorkspace (requestedWorkspace);
		}

		// Checks, if the workspace is set.
		// If not, the SelectWorkspaceAction is started to select it.
		if (workspace.isEmpty()) {
			selectWorkspace();
		}

		showWorkingDirInTitleBar();

	}
	
	public void initWorkspace() {
		
		if (workspace.isEmpty())
			return;
		
		String templatesDir = workspace + "/Templates/Invoice";
		String templatesFile = templatesDir + "/" + "Document.ott";
		
		File directory = new File(templatesDir);
		if (!directory.exists())
			directory.mkdirs();
		

		
		try {
			InputStream in = Activator.getDefault().getBundle().getResource("Templates/Invoice/Document.ott").openStream();
			File fout = new File(templatesFile);
			OutputStream out;
				out = new FileOutputStream(fout);
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0){
		    	out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setWorkspace (String workspace) {
		this.workspace = workspace;
		preferences.setValue("GENERAL_WORKSPACE", workspace);
	}

	public String getWorkspace () {
		return this.workspace;
		
	}
	
	public void selectWorkspace() {
		// Open a directory dialog 
		DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		directoryDialog.setFilterPath( System.getProperty("user.home") );
		directoryDialog.setMessage("Bitte wählen Sie ein Dateiverzeichnis aus, in dem die Firmendaten abgelegt werden.");
		directoryDialog.setText("Arbeitsverzeichnis auswählen");
		String selectedDirectory = directoryDialog.open();

		if (selectedDirectory != null) {
			
			// test, if it is valid
			if (selectedDirectory.equals("/"))
				selectedDirectory = "";
			if (selectedDirectory.equals("\\"))
				selectedDirectory = "";
			if (!selectedDirectory.isEmpty()) {
				
				// If there is a connection to the database,
				// use the new working directory after a restart.
				if (DataBaseConnectionState.INSTANCE.isConnected()) {
					
					// Store the requested directory in a preference value
					Activator.getDefault().getPreferenceStore().setValue("GENERAL_WORKSPACE_REQUEST", selectedDirectory);
					MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION);
					messageBox.setText("Hinweis");
					messageBox.setMessage("Das Arbeitsverzeichnis wird gewechselt.\nBitte starten Sie Fakturama neu !");
					messageBox.open();
					
					// Close the workbench
					TemporaryViews.INSTANCE.closeAll();
					PlatformUI.getWorkbench().close();
				}
				// if there is no connection, use it immediately
				else {
					setWorkspace (selectedDirectory);
					showWorkingDirInTitleBar();
				}
			}
		}

		// Close the workbench, if no workspace is set.
		if (workspace.isEmpty())
			PlatformUI.getWorkbench().close();

	}
	
	/**
	 * Displays the current workspace in the title bar
	 */
	public void showWorkingDirInTitleBar() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell().setText(
					"Fakturama - " + workspace);
		}
	}

	
	
}
