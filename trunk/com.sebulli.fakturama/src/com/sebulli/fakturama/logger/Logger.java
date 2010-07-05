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

package com.sebulli.fakturama.logger;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sebulli.fakturama.Activator;

/**
 * Provides functions to log exceptions as an error, or as in information
 * 
 * @author Gerd Bartelt
 *
 */
public class Logger {

	/**
	 * Log an exception as an error
	 * 
	 * @param e The Exception
	 * @param message The message that will be logged
	 */
	static public void logError(Exception e, String message) {
		ILog logger = Activator.getDefault().getLog();
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
		logger.log(status);
	}

	/**
	 * Log an an error
	 * 
	 * @param message The message that will be logged
	 */
	static public void logError(String message) {
		ILog logger = Activator.getDefault().getLog();
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
		logger.log(status);
	}

	/**
	 * Log an an information
	 * 
	 * @param message The message that will be logged
	 */
	static public void logInfo(String message) {
		ILog logger = Activator.getDefault().getLog();
		IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, message);
		logger.log(status);
	}

}
