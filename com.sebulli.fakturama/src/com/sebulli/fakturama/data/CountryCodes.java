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

import java.util.Properties;

import com.sebulli.fakturama.logger.Logger;

/**
 * Convert the countries to a county code
 * 
 * @author Gerd Bartelt
 */
public class CountryCodes {

	/**
	 * Return the county code (post code) of a country
	 * 
	 * @param country
	 * @return
	 */
	public static String getCode(String country) {

		// Create a collection of postcodes
		Properties postcodes = new Properties();

		// Read the file postcodes.txt from the resource
		try {
			// load the postcodes with the file contents 
			postcodes.load(CountryCodes.class.getResourceAsStream("/resources/postcodes.txt"));
			// Get the code of a country
			return postcodes.getProperty(country.toUpperCase(),""); 
			
		} catch (Exception e) {
			Logger.logError(e, "Error reading postcodes");
			return "";
		}
		
	}
	
}
