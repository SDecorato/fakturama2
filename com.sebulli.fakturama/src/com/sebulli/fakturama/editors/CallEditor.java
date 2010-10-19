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

package com.sebulli.fakturama.editors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;

/**
 * Universal Handler to open an UniDataSet editor
 * 
 * @author Gerd Bartelt
 */
public class CallEditor extends AbstractHandler implements IHandler {

	/**
	 * Execute the command
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the parameter of the action that calls this handler
		String param = event.getParameter("com.sebulli.fakturama.editors.callEditorParameter");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		// Get the corresponding table view
		IWorkbenchPage page = window.getActivePage();
		String viewId = "com.sebulli.fakturama.views.datasettable.view" + param + "Table";
		ViewDataSetTable view = (ViewDataSetTable) page.findView(viewId);

		// Get the selection in the table view
		ISelection selection = view.getSite().getSelectionProvider().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			// If we had a selection lets open the editor
			if (obj != null) {

				// Define  the editor
				String editor = "com.sebulli.fakturama.editors." + param.toLowerCase() + "Editor";
				UniDataSet uds = (UniDataSet) obj;
				UniDataSetEditorInput input = new UniDataSetEditorInput(uds);

				// And try to open it
				try {
					page.openEditor(input, editor);
				}
				catch (PartInitException e) {
					Logger.logError(e, "Error opening Editor: " + editor);
				}
			}
		}
		return null;
	}

}
