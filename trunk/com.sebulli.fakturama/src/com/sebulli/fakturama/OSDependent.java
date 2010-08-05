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

import org.eclipse.core.runtime.Platform;

/**
 * These are the OS-dependent settings.
 * 
 * @author Gerd Bartelt
 */
public class OSDependent {
	
	/**
	 * Test, if it is a Mac OSX
	 * @return TRUE, if it one
	 */
	private static boolean isMacOSX () {
		return Platform.getOS().equalsIgnoreCase("macosx");
	}
	
	/**
	 * Test, if it is a Linux system
	 * @return TRUE, if it one
	 */
	private static boolean isLinux () {
		return Platform.getOS().equalsIgnoreCase("linux");
	}

	/**
	 * Test, if it is Windows System
	 * @return TRUE, if it one
	 */
	private static boolean isWin() {
		return Platform.getOS().toLowerCase().startsWith("win");
	}

	/**
	 * Returns the os dependent program folder
	 * 
	 * @return Program folder as string
	 */
	public static String getProgramFolder () {

		if (isMacOSX())
			return "/Applications/";
		
		if (isLinux())
			return "/usr/lib/";

		if (isWin())
			return "C:\\Program Files\\";

		return "";
		
	}
	
	/**
	 * Returns the os dependent default path of the OpenOffice installation
	 * 
	 * @return Default path as string
	 */
	public static String getOODefaultPath () {

		if (isMacOSX())
			return getProgramFolder() + "OpenOffice.org.app";
		
		if (isLinux())
			return getProgramFolder() + "openoffice";

		if (isWin())
			return getProgramFolder() +"OpenOffice.org 3";

		return "";
		
	}

	/**
	 * Returns the os dependent default path.
	 * If it is in an .app archive, add the extension.
	 * 
	 * @param path Default path without extension
	 * @return Default path with extension
	 */
	static public String getOOExtendetPath (String path) {

		if (isMacOSX()) 
			path += "/Contents/MacOS";
		
		return path;
	}
	
	/**
	 * Returns the OpenOffice binary-
	 * 
	 * @param path of the OpenOffice folder
	 * @return Full Path of the the binary.
	 */
	public static String getOOBinary (String path) {
			
		if (isMacOSX())	
			return getOOExtendetPath(path) + "/soffice";

		if (isLinux())	
			return getOOExtendetPath(path) + "/program/soffice";
			
		if (isWin())	
			return getOOExtendetPath(path) + "\\program\\soffice.exe";
		
		return "";
	}
	
	/**
	 * Test, if it is allowed to add an about menu to the menu bar.
	 * In some os the about menu is set to the menu bar by the os.
	 * So, it is not necessary to add it twice.
	 * 
	 * @return TRUE, if it is necessary
	 */
	public static boolean canAddAboutMenuItem () {
		return !isMacOSX();
	}

	/**
	 * Test, if it is allowed to add an preference menu to the menu bar.
	 * In some os the about menu is set to the menu bar by the os.
	 * So, it is not necessary to add it twice.
	 * 
	 * @return TRUE, if it is necessary
	 */
	public static boolean canAddPreferenceAboutMenu () {
		return !isMacOSX();
	}
	/**
	 * Test, if OpenOffice is in an app archive instead 
	 * a program folder.
	 * 
	 * @return TRUE, if it an app
	 */
	public static boolean isOOApp () {
		return isMacOSX();
	}
	
	/**
	 * Returns the os dependent new line sequence
	 * 
	 * @return new line sequence
	 */
	public static String getNewLine () {

		if (isMacOSX())
			return "\n";
		
		if (isLinux())
			return "\n";

		if (isWin())
			return "\r\n";

		return "";
		
	}
	
	
}
