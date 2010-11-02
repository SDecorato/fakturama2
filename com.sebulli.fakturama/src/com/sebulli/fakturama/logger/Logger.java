/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
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
	 * @param e
	 *            The Exception
	 * @param message
	 *            The message that will be logged
	 */
	static public void logError(Exception e, String message) {
		ILog logger = Activator.getDefault().getLog();
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
		logger.log(status);
	}

	/**
	 * Log an an error
	 * 
	 * @param message
	 *            The message that will be logged
	 */
	static public void logError(String message) {
		ILog logger = Activator.getDefault().getLog();
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
		logger.log(status);
	}

	/**
	 * Log an an information
	 * 
	 * @param message
	 *            The message that will be logged
	 */
	static public void logInfo(String message) {
		ILog logger = Activator.getDefault().getLog();
		IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, message);
		logger.log(status);
	}

}
