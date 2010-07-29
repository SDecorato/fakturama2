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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.views.ErrorView;

/**
 * A log listener for all exceptions.
 * The error messages are written into a logfile and displayed in an error
 * view in the workbench.
 * 
 * @author Gerd Bartelt
 */
public class LogListener implements ILogListener {
	
	// Maximum lines of the logfile
	private static final int MAXLINES = 2000;
	
	// The logile
	private File logFile;
	
	// The errortext of the errorview
	private String errorString = "";
	
	// Display or hide the errorview
	private boolean showerrorview = false;
	
	// Store the error log file in the workspace
	String filename;
	

	/**
	 * Constructor
	 */
	public LogListener() {
		
		// Get the directory of the workspace
		filename = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");
		
		// Do not save logfiles, of there is no workspace set
		if (filename.isEmpty())
			return;
		
		// Create a subfolder "Log", if it does not exist yet.
		filename += "/Log/";
		File directory = new File(filename);
		if (!directory.exists())
			directory.mkdirs();
		
		// Name of the logfile
		filename += "Error.log";
	}

	/**
	 * Shows the error view and sets the error text
	 */
	public void showErrorView() {

		// Do it not, if showerrorview flag is not set
		if (!showerrorview)
			return;

		try {

			// Find the error view
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ErrorView.ID);
			ErrorView view = (ErrorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ErrorView.ID);
			
			// Set the error text
			view.setErrorText(errorString);

		} catch (PartInitException e) {
		}

	}

	/**
	 * Notifies this listener that given status has been logged by a plug-in
	 * 
	 * @see org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime.IStatus, java.lang.String)
	 */
	@Override
	public void logging(IStatus status, String plugin) {
		try {
			String declaringClass = "";
			String lineNumber = "";
			String methodName = "";
			String lineArray[] = new String[MAXLINES];
			String exceptionMessage = "";
			String newErrorString = "";

			// Add an "I:" or an "E:", depending if it is an information or
			// an error.
			if (status.getSeverity() == IStatus.INFO) {

				// Information.
				// Do not open the error view
				newErrorString += "I:";
				
			} else {
				
				// Error
				// Open the error view
				showerrorview = true;
				newErrorString += "E:";
			}

			if (status.getException() != null) {

				// Get all elements of the stack trace and search for the first
				// element, that starts with the plugin name.
				for (StackTraceElement element : status.getException().getStackTrace()) {
					if (element.getClassName().startsWith(plugin)) {
						declaringClass = element.getClassName();
						lineNumber = Integer.toString(element.getLineNumber());
						methodName = element.getMethodName();
						break;
					}
				}

				// Generate the exception message.
				exceptionMessage = status.getMessage() + " : " + 
						((Exception) status.getException()).getLocalizedMessage() +
						" in: " + declaringClass + "/"
						+ methodName + "(" + lineNumber + ")" + "\n";

				// Generate the error string
				newErrorString += exceptionMessage;
			} else
				// Generate the error string
				newErrorString += status.getMessage() + "\n";

			errorString += newErrorString;
			System.out.print(newErrorString);
			
			// Show the error view (only if it is not just an information message)
			showErrorView();

			// Do not log, if no workspace is set.
			if (filename.isEmpty())
				return;			
			
			// Create a File object
			logFile = new File(filename);

			int lines = 0;
			int lineIndex = 0;

			// If the log file exists read the content
			if (logFile.exists()) {
				
				// Open the existing file
				BufferedReader in = new BufferedReader(new FileReader(filename));
				String line = "";
				
				// Read the existing file and store it in a buffer
				// with a fix size. Only the newest lines are kept.
				while ((line = in.readLine()) != null) {
					lineArray[lineIndex] = line;
					lines++;
					lineIndex++;
					lineIndex = lineIndex % MAXLINES;
				}
			}

			// If the existing logile has more than the MAXINES,
			// delete it and create a new one.
			if (lines > MAXLINES) {
				logFile.delete();
				logFile = new File(filename);
			}

			// Create a new file
			logFile.createNewFile();
			BufferedWriter bos = new BufferedWriter(new FileWriter(logFile, true));

			// Write the data to the new file.
			if (lines > MAXLINES) {
				for (int i = 0; i < MAXLINES; i++) {
					bos.write(lineArray[lineIndex] + "\n");
					lineIndex++;
					lineIndex = lineIndex % MAXLINES;
				}
			}

			// Create a new string buffer and add the error message
			StringBuffer str = new StringBuffer(plugin);
			str.append(": ");
			str.append(status.getMessage());
			
			// Add the stack trace
			final Writer stackTrace = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(stackTrace);
			if (status.getException() != null)
				status.getException().printStackTrace(printWriter);
			stackTrace.toString();
			str.append(stackTrace.toString());
			str.append("\n");
			
			// Write the stack trace to the log file
			bos.write(str.toString());
			bos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}