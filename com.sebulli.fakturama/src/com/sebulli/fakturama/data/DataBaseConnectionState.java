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

package com.sebulli.fakturama.data;

/**
 * Thsi singleton stores the state of the connection to the data base in a local
 * variable. A singleton enum is used to read the state, without accessing to
 * the Data class.
 * 
 * @author Gerd Bartelt
 */
public enum DataBaseConnectionState {
	INSTANCE;

	private boolean connected = false;

	/**
	 * Test whether the data base is connected
	 * 
	 * @return True, if the data base is connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Set the state of the connection to the data base to "connected"
	 */
	public void setConnected() {
		connected = true;
	}

}
