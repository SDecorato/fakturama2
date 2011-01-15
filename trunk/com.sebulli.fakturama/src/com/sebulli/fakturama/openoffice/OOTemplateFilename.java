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

/**
 * This class provides functionality to get the path, the filename and the
 * filename extension of an OpenOffice document template file.
 * 
 * @author Gerd Bartelt
 */
public class OOTemplateFilename {

	// Path, name and extension of the filename
	private String path;
	private String name;
	private String extension;

	/**
	 * Constructor Create the file name and extract the extension
	 * 
	 * @param path
	 *            Path to the file
	 * @param filename
	 *            The file name with extension
	 */
	public OOTemplateFilename(String path, String filename) {
		this.path = path;
		int pPos = filename.lastIndexOf(".");
		if (pPos > 0) {

			// Extract name and extension
			this.name = filename.substring(0, pPos);
			this.extension = filename.substring(pPos, filename.length());

		}
		else {

			// There is no extension
			this.name = filename;
			this.extension = "";
		}
	}

	/**
	 * Returns the path
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the file name without the extension
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the extension
	 * 
	 * @return
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Returns the path and name with extension
	 * 
	 * @return
	 */
	public String getPathAndFilename() {
		return path + name + extension;
	}

}
