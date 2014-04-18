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

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Log error or warnings to the console and to a log file
 * 
 * @author Gerd Bartelt
 */
public class Logger {
	
	// Singleton
	private static Logger instance = null;
	
	// The line number in Database.script
	private int lineNr = 0;
	// Count the errors
	private int errors;
	
	// Show also warnings
	private boolean showWarnings = false;
	
	// The log file
	PrintWriter out;
	
	/**
	 * Constructor
	 * Open the log file
	 */
	private Logger() {
		errors = 0;
		try {
			out = new PrintWriter("database_check.txt");
		} catch (FileNotFoundException e) {
		}
	}
	
	/**
	 * Singleton mechanism
	 * @return
	 */
	public static Logger getInstance() {
		if (instance == null) {
			synchronized (Logger.class) {
				instance = new Logger();
			}
		}
		return instance;
	}

	/**
	 * Show a warning
	 * 
	 * @param showWarnings warning text
	 */
	public void config (boolean showWarnings)  {
		this.showWarnings = showWarnings;
	}
	
	/**
	 * Output a text to console and logfile
	 * @param line
	 */
	private void logLine (String line) {
		System.out.println(line);
		out.println(line);
	}
	
	/**
	 * Output an error text to error console and logfile
	 * @param line
	 */
	private void logErrorLine (String line) {
		System.err.println(line);
		out.println(line);
	}
	
	/**
	 * Output a text without line number
	 * @param text
	 */
	public void logText (String text) {
		logLine(text);
	}
	
	/**
	 * Log an error and count the error
	 * 
	 * @param error Text
	 */
	public void logError (String error) {
		logErrorLine("ERROR in " + lineNr +": " + error);
		errors ++;
	}
	
	/**
	 * Log a warning without counting it like an error
	 * 
	 * @param warning Text
	 */
	public void logWarning (String warning) {
		if (showWarnings)
			logLine("WARNING in " + lineNr +": " + warning);
	}
	
	/**
	 * Set the line number of the Database.script
	 * 
	 * @param lineNr
	 */
	public void setLineNr (int lineNr) {
		this.lineNr = lineNr;
	}
	
	/**
	 * Log the result and close the log file
	 */
	public void logFinal() {
		if (errors > 0) {
			logLine (errors + " errors found.");
		}
		else {
			logLine ("Database seems to be ok.");
		}
		
		out.close();
	}
	

}
