/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.preferences;

import java.io.File;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import com.sebulli.fakturama.OSDependent;
import com.sebulli.fakturama.openoffice.OpenOfficeStarter;

/**
 * A field editor for a file path type preference. A standard file dialog
 * appears when the user presses the change button.
 * 
 * @author Gerd Bartelt
 */
public class AppFieldEditor extends StringButtonFieldEditor {

	/**
	 * Creates a new file field editor
	 */
	protected AppFieldEditor() {
	}

	/**
	 * Creates a file field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public AppFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		setErrorMessage(JFaceResources.getString("FileFieldEditor.errorMessage"));
		setChangeButtonText(JFaceResources.getString("openBrowse"));
		setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
		createControl(parent);
	}

	/**
	 * Method declared on StringButtonFieldEditor. Opens the file chooser dialog
	 * and returns the selected file. Start with the OS dependent program
	 * directory
	 */
	@Override
	protected String changePressed() {
		String startingDir = "";

		// Start with the last URL
		if (!getTextControl().getText().isEmpty())
			startingDir = getTextControl().getText();

		// Remove everything after the last "/"
		if (!startingDir.isEmpty())
			if (startingDir.contains("/"))
				startingDir = startingDir.substring(0, 1 + startingDir.lastIndexOf("/"));

		// use the OS dependent program folder
		if (startingDir.isEmpty())
			startingDir = OSDependent.getProgramFolder();

		// Checks whether the selected folder exists
		File f = new File(startingDir);
		if (!f.exists())
			f = null;
		File d = getFile(f);
		if (d == null)
			return null;

		return d.getAbsolutePath();
	}

	/**
	 * Method declared on StringFieldEditor. Checks whether the text input field
	 * specifies an existing folder to an OpenOffice application or to an
	 * OpenOffice App on a Mac OS
	 */
	@Override
	protected boolean checkState() {

		String msg = null;

		String path = getTextControl().getText();

		if (path != null)
			path = path.trim();
		else
			path = "";

		// Check whether it is a valid application
		if (path.length() != 0) {
			if (!OpenOfficeStarter.isValidPath(path)) {
				if (OSDependent.isOOApp())
					msg = "keine gültige OpenOffice App";
				else
					msg = "keine gültiger OpenOffice Programmordner";
			}
		}

		// Display an error message
		if (msg != null) {
			showErrorMessage(msg);
			return false;
		}

		// OK!
		clearErrorMessage();
		return true;
	}

	/**
	 * Helper to open the file chooser dialog.
	 * 
	 * @param startingDirectory
	 *            the directory to open the dialog on.
	 * @return File The File the user selected or null if they do not.
	 */
	private File getFile(File startingDirectory) {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		if (startingDirectory != null)
			dialog.setFileName(startingDirectory.getPath());
		String file = dialog.open();
		if (file != null) {
			file = file.trim();
			if (file.length() > 0)
				return new File(file);
		}

		return null;
	}

}