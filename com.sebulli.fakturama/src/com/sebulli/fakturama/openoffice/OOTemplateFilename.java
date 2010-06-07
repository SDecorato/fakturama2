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

public class OOTemplateFilename {
	private String path;
	private String name;
	private String extension;

	public OOTemplateFilename(String path, String filename) {
		this.path = path;
		int pPos = filename.lastIndexOf(".");
		if (pPos > 0) {
			this.name = filename.substring(0, pPos);
			this.extension = filename.substring(pPos, filename.length());
		} else {
			this.name = filename;
			this.extension = "";
		}
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	public String getPathAndFilename() {
		return path + name + extension;
	}

}
