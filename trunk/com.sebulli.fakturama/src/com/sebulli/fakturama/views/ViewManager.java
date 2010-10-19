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

package com.sebulli.fakturama.views;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.logger.Logger;

/**
 * Shows a view
 * 
 * @author Gerd Bartelt
 */
public class ViewManager {

	/**
	 * Shows a new view
	 * 
	 * @param viewId
	 *            ID of the view
	 */
	public static void showView(String viewId) {
		// Show a new view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		}
		catch (PartInitException e) {
			Logger.logError(e, "Error showing view " + viewId);
		}

	}

}
