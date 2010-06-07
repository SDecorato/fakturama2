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

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.editors.DummyEditor;
import com.sebulli.fakturama.editors.DummyEditorInput;
import com.sebulli.fakturama.logger.Logger;

public enum TemporaryViews {
	INSTANCE;
	private List<String> list = new ArrayList<String>();
	private boolean hideAndShow = false;

	TemporaryViews() {
	}

	public void showView(String viewId) {
		if (!Data.INSTANCE.getDataBaseOpened())
			return;

		boolean openViews = false;
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iterator.next());
			if (view != null)
				openViews = true;
		}

		IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (!openViews) {
			if (editors.length == 0) {
				try {
					page.openEditor(new DummyEditorInput(), DummyEditor.ID);
				} catch (PartInitException e) {
					Logger.logError(e, "Error opening Dummy Editor");
				}
				editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				page.setPartState(editors[0], IWorkbenchPage.STATE_MINIMIZED);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
			} else if (editors.length == 1) {
				if (editors[0].getName().equals("www.sebulli.com")) {
					page.setPartState(editors[0], IWorkbenchPage.STATE_MINIMIZED);
				}
			}
		}

		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {

			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iterator.next());
			hideAndShow = true;
			if (view != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
		}
		hideAndShow = false;

		list.clear();
		list.add(viewId);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		} catch (PartInitException e) {
			Logger.logError(e, "Error showing view " + viewId);
		}

	}

	public void unMinimizeEditorPart() {
		if (hideAndShow)
			return;
		IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (editors.length == 0) {
			try {
				page.openEditor(new DummyEditorInput(), DummyEditor.ID);
			} catch (PartInitException e) {
				Logger.logError(e, "Error opening Dummy Editor");
			}
			editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
			page.setPartState(editors[0], IWorkbenchPage.STATE_RESTORED);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		} else {
			page.setPartState(editors[0], IWorkbenchPage.STATE_RESTORED);
		}

	}

	public List<String> getViews() {
		return list;

	}

	public void closeAll() {
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iterator.next());
			if (view != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
		}
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NavigationView.ID);
		} catch (PartInitException e) {
		}
	}

}
