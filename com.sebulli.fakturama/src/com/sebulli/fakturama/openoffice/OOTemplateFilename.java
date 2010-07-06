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

package com.sebulli.fakturama.openoffice;

/**
 * This class provides functionality to get the path, the filename and
 * the filename extension of an OpenOffice document template file.
 * 
 * @author Gerd Bartelt
 */
public class OOTemplateFilename {

	// Path, name and extension of the filename
	private String path;
	private String name;
	private String extension;

	/**
	 * Constructor
	 * Create the file name and extract the extension
	 * 
	 * @param path Path to the file
	 * @param filename The file name with extension
	 */
	public OOTemplateFilename(String path, String filename) {
		this.path = path;
		int pPos = filename.lastIndexOf(".");
		if (pPos > 0) {
			
			// Extract name and extension
			this.name = filename.substring(0, pPos);
			this.extension = filename.substring(pPos, filename.length());
			
		} else {
			
			// There is no extension
			this.name = filename;
			this.extension = "";
		}
	}

	/**
	 * Returns the path
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the file name without the extension
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the extension
	 * @return
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Returns the path and name with extension
	 * @return
	 */
	public String getPathAndFilename() {
		return path + name + extension;
	}

}
