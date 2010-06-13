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

package com.sebulli.fakturama.data;

/**
 * Possible Types for class UniData
 * 
 * @author Gerd Bartelt
*/
public enum UniDataType {
	/**
	 * All types
	 */
	NONE, INT, ID, QUANTITY, BOOLEAN, DOUBLE, STRING, PRICE, PERCENT, DATE;
	
	/**
	 * Test, if a type is a numeric one
	 * 
	 * @return True for numeric types
	 */
	public boolean isNumeric() {
		switch (this) {
		case NONE:
			return false;
		case INT:
			return true;
		case ID:
			return true;
		case QUANTITY:
			return true;
		case BOOLEAN:
			return false;
		case DOUBLE:
			return true;
		case STRING:
			return false;
		case PRICE:
			return true;
		case PERCENT:
			return true;
		case DATE:
			return true;
		}
		return false;
	}

	/**
	 * Test, if a type is a date 
	 * 
	 * @return True for date types
	 */
	public boolean isDate() {
		return (this == DATE);
	}
}
