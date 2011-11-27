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

package com.sebulli.fakturama.openoffice;

import static com.sebulli.fakturama.Translate._;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.widgets.Display;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.misc.DocumentType;

public class FileOrganizer {

	final public static boolean WITH_FILENAME = true;
	final public static boolean NO_FILENAME = false;
	final public static boolean WITH_EXTENSION = true;
	final public static boolean NO_EXTENSION = false;
	final public static boolean PDF = true;
	final public static boolean ODT = false;

	static private int i;

	/**
	 * Returns the filename (relative to the workspace) of the OpenOffice
	 * document
	 * 
	 * @param inclFilename
	 *            True, if also the filename should be used
	 * @param inclExtension
	 *            True, if also the extension should be used
	 * @param PDF
	 *            True, if it's the PDF filename
	 * @return The filename
	 */
	public static String getRelativeDocumentPath(boolean inclFilename,
			boolean inclExtension, boolean isPDF, DataSetDocument document) {

		String savePath = "";

		// T: Subdirectory of the OpenOffice documents
		savePath += _("/Documents");

		if (isPDF)
			savePath += "/PDF/";
		else
			savePath += "/OpenOffice/";

		savePath += DocumentType.getPluralString(document
				.getIntValueByKey("category")) + "/";

		// Use the document name as filename
		if (inclFilename)
			savePath += document.getStringValueByKey("name");

		// Use the document name as filename
		if (inclExtension) {
			if (isPDF)
				savePath += ".pdf";
			else
				savePath += ".odt";
		}

		return savePath;

	}

	/**
	 * Returns the filename (with path) of the OpenOffice document
	 * 
	 * @param inclFilename
	 *            True, if also the filename should be used
	 * @param inclExtension
	 *            True, if also the extension should be used
	 * @param PDF
	 *            True, if it's the PDF filename
	 * @return The filename
	 */
	public static String getDocumentPath(boolean inclFilename,
			boolean inclExtension, boolean isPDF, DataSetDocument document) {
		String savePath = Activator.getDefault().getPreferenceStore()
				.getString("GENERAL_WORKSPACE");

		return savePath
				+ getRelativeDocumentPath(inclFilename, inclExtension, isPDF,
						document);
	}

	/**
	 * Updates the country codes list
	 * 
	 * @param version
	 *            The new Version
	 */
	public static void update(String version, final StatusLineManager slm) {
		if (version.equals("1.5")) {

			// Start OpenOffice in a new thread
			new Thread(new Runnable() {
				public void run() {

					ArrayList<DataSetDocument> documents = Data.INSTANCE
							.getDocuments().getActiveDatasets();
					String savePath = Activator.getDefault()
							.getPreferenceStore()
							.getString("GENERAL_WORKSPACE");

					i = 0;

					for (DataSetDocument document : documents) {
						
						boolean updated = false;

						if (document.getStringValueByKey("odtpath").isEmpty()
								&& document.getStringValueByKey("pdfpath")
										.isEmpty()) {

							
							// Update the document entry "odtpath"
							String filename = FileOrganizer
									.getRelativeDocumentPath(
											FileOrganizer.WITH_FILENAME,
											FileOrganizer.WITH_EXTENSION,
											FileOrganizer.ODT, document);

							if ((new File(savePath + filename)).exists()) {
								updated = true;
								document.setStringValueByKey("odtpath",
										filename);
							}

							// Update the document entry "pdfpath"
							filename = FileOrganizer.getRelativeDocumentPath(
									FileOrganizer.WITH_FILENAME,
									FileOrganizer.WITH_EXTENSION,
									FileOrganizer.PDF, document);

							if ((new File(savePath + filename)).exists()) {
								updated = true;
								document.setStringValueByKey("pdfpath",
										filename);
							}

							// Show a message in the status bar
							if (slm != null && updated) {
								i++;

								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										// T: Message in the status bar
										slm.setMessage(_("Updating documents "
												+ i));
									}
								});
							}

						}

						Data.INSTANCE.updateDataSet(document);

					}

				}
			}).start();

		}
	}

}
