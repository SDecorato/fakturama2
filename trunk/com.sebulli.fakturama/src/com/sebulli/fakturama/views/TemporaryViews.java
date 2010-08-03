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

package com.sebulli.fakturama.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.editors.DummyEditor;
import com.sebulli.fakturama.editors.DummyEditorInput;
import com.sebulli.fakturama.logger.Logger;

/**
 * Create temporary views. This is a list of views.
 * In the facturama project this views are displayed under the editor view.
 * If a new view is added, all the other views of this collection will be
 * hidden. So only one of this views will be visible
 * 
 * @author Gerd Bartelt
 */
public enum TemporaryViews {
	INSTANCE;
	
	// The list with all views of this collection.
	private List<String> list = new ArrayList<String>();
	private boolean hideAndShow = false;

	TemporaryViews() {
	}

	public void showView(String viewId) {
/*
		// Do not show a view, if the data base is not opened
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		// Temporary variable to test, if there is at least one view that is
		// open (visible)
		boolean openViews = false;

		
		// Scan all views and check, of one is open.
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iterator.next());
			if (view != null)
				openViews = true;
		}
		
		// Get the editors
		IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		// If no view was opened, this is the first view.
		// Maximize it, if also no editor is open.
		// The only editor is the browser editor. This can be minimized.
		if (!openViews) {
			
			// No editor is open
			if (editors.length == 0) {
				
				// To minimize the editor area, is is necessary, to have at
				// least one editor. If there is no one, we create a dummy
				// editor. This is a workaround, until there is a better
				// solution.
				try {
					page.openEditor(new DummyEditorInput(), DummyEditor.ID);
				} catch (PartInitException e) {
					Logger.logError(e, "Error opening Dummy Editor");
				}
				
				// Minimize the editor area
				editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				page.setPartState(editors[0], IWorkbenchPage.STATE_MINIMIZED);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
			}
			// If the only editor is the browser, minimize it.
			else if (editors.length == 1) {
				if (editors[0].getName().equals("www.sebulli.com")) {
					page.setPartState(editors[0], IWorkbenchPage.STATE_MINIMIZED);
				}
			}
		}

		// Hide all views.
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {

			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iterator.next());
			
			// Set a marker to prevent, that the editor area is restored, only
			// because a new view opens.
			hideAndShow = true;
			if (view != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
		}
		hideAndShow = false;

		// Add the new view to the list of open views
		list.clear();
		list.add(viewId);

		*/
		// Show the new view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		} catch (PartInitException e) {
			Logger.logError(e, "Error showing view " + viewId);
		}

	}

	/**
	 * If a view is disposed, this function is called to restore the state of
	 * the editor area to "not minimized".
	 */
	public void unMinimizeEditorPart() {
		
		// Do it not, if this is the same view is closed, because an other
		// view is opened.
		if (hideAndShow)
			return;
		
		// Get the editors 
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// Exit, if there is no page set
		if (page == null)
			return;
		
		IEditorReference[] editors = page.getEditorReferences();

		// Test, whether there is an editor or not
		boolean noEditor = true;
		if (editors != null)
			if (editors.length != 0)
				noEditor = false;
		
		// If no editor is opened, create a dummy editor to do the restore job.
		if (noEditor) {
			
			// Create a dummy editor
			try {
				page.openEditor(new DummyEditorInput(), DummyEditor.ID);
			} catch (PartInitException e) {
				Logger.logError(e, "Error opening Dummy Editor");
			}
			
			// Restore the state
			editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
			page.setPartState(editors[0], IWorkbenchPage.STATE_RESTORED);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		}
		// Restore the state - but it's not necessary to use a dummy editor
		else {
			page.setPartState(editors[0], IWorkbenchPage.STATE_RESTORED);
		}

	}

	/**
	 * Returns the list of views
	 * 
	 * @return The temporary views
	 */
	public List<String> getViews() {
		return list;
	}

	/**
	 * Close all views
	 */
	public void closeAll() {
		
		// Close all views in the list
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iterator.next());
			if (view != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
		}
		
		// Never close the navigation view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NavigationView.ID);
		} catch (PartInitException e) {
		}
	}

}
