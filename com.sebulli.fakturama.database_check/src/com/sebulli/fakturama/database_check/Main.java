/* 
 * Fakturama - database checker - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2014 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.database_check;

public class Main {

	/**
	 * Main program
	 * 
	 * @param args program arguments
	 */
	public static void main(String[] args) {
		
		// Show also warnings
		boolean showWarnings = false;
		
		// analyse all program arguments
		for (String arg: args) {
			
			// Show also warnings with -w
			if (arg.equals("-w"))
				showWarnings = true;
		}
		
		// Output program version
		Logger.getInstance().logText("Database checker version 1.0.1");
		Logger.getInstance().logText("2014 - Gerd Bartelt - www.sebulli.com");
		
		// Configure logger
		Logger.getInstance().config(showWarnings);
		
		// Import the database
		Database database = new Database();
		Importer importer = new Importer(database);
		
		// Check the database for errors
		Checker checker = new Checker(database);
		if (importer.run()) {
			checker.checkAll();
			Logger.getInstance().logFinal();
		}
	}

}
