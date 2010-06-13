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

public class LogListener implements ILogListener {
	private static final int MAXLINES = 1000;
	private File logFile;
	private String errorString = "";
	private boolean showerrorview = false;

	public LogListener() {
	}

	public void showErrorView() {

		if (!showerrorview)
			return;

		try {

			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ErrorView.ID);
			ErrorView view = (ErrorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ErrorView.ID);
			view.setErrorText(errorString);

		} catch (PartInitException e) {
		}

	}

	@Override
	public void logging(IStatus status, String plugin) {
		try {
			String declaringClass = "";
			String lineNumber = "";
			String methodName = "";
			String lineArray[] = new String[MAXLINES];
			String exceptionMessage = "";

			if (status.getSeverity() == IStatus.INFO) {
				errorString += "I:";
			} else {
				showerrorview = true;
				errorString += "E:";
			}

			if (status.getException() != null) {

				for (StackTraceElement element : status.getException().getStackTrace()) {
					if (element.getClassName().startsWith(plugin)) {
						declaringClass = element.getClassName();
						lineNumber = Integer.toString(element.getLineNumber());
						methodName = element.getMethodName();
						break;
					}
				}

				exceptionMessage = status.getMessage() + " : " + ((Exception) status.getException()).getLocalizedMessage() + " in: " + declaringClass + "/"
						+ methodName + "(" + lineNumber + ")" + "\n";

				errorString += exceptionMessage;
			} else
				errorString += status.getMessage() + "\n";

			showErrorView();

			String filename = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");
			if (filename.isEmpty())
				return; 
			filename += "/Error.log";

			logFile = new File(filename);

			int lines = 0;
			int lineIndex = 0;

			if (logFile.exists()) {
				BufferedReader in = new BufferedReader(new FileReader(filename));
				String line = "";
				while ((line = in.readLine()) != null) {
					lineArray[lineIndex] = line;
					lines++;
					lineIndex++;
					lineIndex = lineIndex % MAXLINES;
				}
			}

			if (lines > MAXLINES) {
				logFile.delete();
				logFile = new File(filename);
			}

			logFile.createNewFile();
			BufferedWriter bos = new BufferedWriter(new FileWriter(logFile, true));

			if (lines > MAXLINES) {
				for (int i = 0; i < MAXLINES; i++) {
					bos.write(lineArray[lineIndex] + "\n");
					lineIndex++;
					lineIndex = lineIndex % MAXLINES;
				}
			}

			StringBuffer str = new StringBuffer(plugin);
			str.append(": ");
			str.append(status.getMessage());
			final Writer stackTrace = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(stackTrace);
			status.getException().printStackTrace(printWriter);
			stackTrace.toString();
			str.append(stackTrace.toString());
			str.append("\n");
			bos.write(str.toString());
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
