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

package com.sebulli.fakturama.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sebulli.fakturama.Workspace;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.logger.Logger;

public class BackupManager {

	public static void createBackup() {

		// Get the path to the workspace
		String workspacePath = Workspace.INSTANCE.getWorkspace();
		if (workspacePath.length() == 0)
			return;

		workspacePath += "/";
		String backupPath = workspacePath + "Backup";

		// Create the backup folder, if it dosn't exist.
		File directory = new File(backupPath);
		if (!directory.exists())
			directory.mkdirs();

		// Filename of the zip file
		String dateString = DataUtils.DateAndTimeOfNowAsLocalString();
		dateString = dateString.replace(" ", "_");
		dateString = dateString.replace(":", "");

		backupPath += "/Backup_" + dateString + ".zip";

		// The file to add to the ZIP archive
		ArrayList<String> backupedFiles = new ArrayList<String>();
		backupedFiles.add("Database/Database.properties");
		backupedFiles.add("Database/Database.script");

		FileInputStream in;
		byte[] data = new byte[1024];
		int read = 0;

		try {
			// Connect ZIP archive with stream
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(backupPath));

			// Set mode
			zip.setMethod(ZipOutputStream.DEFLATED);

			// Zip all files
			for (int i = 0; i < backupedFiles.size(); i++) {

				String backupedFile = backupedFiles.get(i);

				try {

					File inFile = new File(workspacePath + backupedFile);
					if (inFile.exists()) {
						in = new FileInputStream(workspacePath + backupedFile);

						if (in != null) {

							// Create a new entry
							ZipEntry entry = new ZipEntry(backupedFile);

							// Add a new entry to the archive
							zip.putNextEntry(entry);

							// Add the data
							while ((read = in.read(data, 0, 1024)) != -1)
								zip.write(data, 0, read);

							zip.closeEntry(); // Close the entry
							in.close();
						}
					}
				}
				catch (Exception e) {
					Logger.logError(e, "Error during file backup:" + backupedFile);
				}
			}
			zip.close();
		}
		catch (IOException ex) {
			Logger.logError(ex, "Error during backup");
		}
	}
}
