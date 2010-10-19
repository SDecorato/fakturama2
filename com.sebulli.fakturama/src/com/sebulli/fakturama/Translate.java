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

package com.sebulli.fakturama;

import java.util.ResourceBundle;

/**
 * Translate strings This class wraps the
 * ResourceBundle.getBundle("Messages").getString() call to be used by the GNU
 * gettext @see http://www.gnu.org/software/gettext/
 * 
 * @author Gerd Bartelt
 */
public class Translate {
	private static ResourceBundle catalog;
	private static boolean initialized = false;

	public static String _(String s) {
		if (!initialized) {
			try {
				catalog = ResourceBundle.getBundle("Messages");
			}
			catch (Exception e) {
			}
			initialized = true;
		}

		if (catalog == null)
			return s;
		else
			return catalog.getString(s);
	}
}