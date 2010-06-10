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

package com.sebulli.fakturama.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.views.TemporaryViews;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;

/**
 * This action deletes an selected data set.
 * An dialog appears to confirm the deletion.
 *  
 * @author Gerd Bartelt
 */
public class DeleteDataSetAction extends Action {

	/**
	 * default constructor
	 */
	public DeleteDataSetAction() {
		super("löschen");
		
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_DELETE_DATASET);
		
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_DELETE_DATASET);
		setImageDescriptor(Activator.getImageDescriptor("/icons/16/delete_16.png"));
	}

	/**
	 * Run the action
	 * Search all views to get the selected element.
	 * If a view with an selection is found, display the dialog before deleting the element
	 */
	@Override
	public void run() {
		
		// Cancel, if the data base is not opened.
		if (!Data.INSTANCE.getDataBaseOpened())
			return;
	
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection;

		// Search all views in the list of the TemporaryViews Class.
		for (Iterator<String> iterator = TemporaryViews.INSTANCE.getViews().iterator(); iterator.hasNext();) {
			String ViewID = iterator.next();
			ViewDataSetTable view = (ViewDataSetTable) workbenchWindow.getActivePage().findView(ViewID);

			// Does the view exist ?
			if (view != null) {

				// Get the selection
				selection = view.getSite().getSelectionProvider().getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();
				
					// If we had a selection let us delete the element
					if (obj != null) {
						UniDataSet uds = (UniDataSet) obj;

						// before deleting: ask !
						MessageBox messageBox = new MessageBox(workbenchWindow.getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						messageBox.setText("Löschen bestätigen");
						messageBox.setMessage("Eintrag " + uds.getStringValueByKey("name") + " wird entgültig gelöscht !");
						
						// We can delete now.
						if (messageBox.open() == SWT.OK) {
							
							// Instead of deleting is completely from the database, the element is just marked
							// as deleted. So a document which still refers to this element would not cause an error.
							uds.setBooleanValueByKey("deleted", true);
							Data.INSTANCE.updateDataSet(uds);
						}

						// Refresh the table
						view.refresh();
					}
				}
			}
		}
	}
}