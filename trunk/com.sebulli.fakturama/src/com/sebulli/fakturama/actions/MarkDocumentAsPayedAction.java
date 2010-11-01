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

package com.sebulli.fakturama.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;

/**
 * This action marks an entry in the invoice table as unpayed or payed.
 * 
 * @author Gerd Bartelt
 */
public class MarkDocumentAsPayedAction extends Action {

	// progress of the order. Value from 0 to 100 (percent)
	boolean payed;

	/**
	 * Constructor Instead of using a value for the states "unpayed" or "payed"
	 * a progress value from 0 to 100 (percent) is used.
	 * 
	 * So it's possible to insert states between these.
	 * 
	 * @param text
	 * @param progress
	 */
	public MarkDocumentAsPayedAction(String text, boolean payed) {
		super(text);
		this.payed = payed;

		// Correlation between progress value and state.
		// Depending on the state, the icon and the command ID is selected.
		if (payed) {
			setSettings(ICommandIds.CMD_MARK_DOCUMENT_AS_PAYED, "/16/checked_16.png");
		}
		else {
			setSettings(ICommandIds.CMD_MARK_DOCUMENT_AS_PAYED, "/16/error_16.png");
		}

	}

	/**
	 * Set command ID and icon for this action.
	 * 
	 * @param cmd
	 *            command ID
	 * @param image
	 *            Actions's icon
	 */
	private void setSettings(String cmd, String image) {
		setId(cmd);
		setActionDefinitionId(cmd);
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor(image));
	}

	/**
	 * Run the action Search all views to get the selected element. If a view
	 * with an selection is found, change the state, if it was an order.
	 */
	@Override
	public void run() {

		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		// Get the active part (view)
		IWorkbenchPart part = null;
		if (page != null)
			part = page.getActivePart();

		ISelection selection;

		// Cast the part to ViewDataSetTable
		if (part instanceof ViewDataSetTable) {

			ViewDataSetTable view = (ViewDataSetTable) part;

			// does the view exist ?
			if (view != null) {

				//get the selection
				selection = view.getSite().getSelectionProvider().getSelection();

				if (selection != null && selection instanceof IStructuredSelection) {

					Object obj = ((IStructuredSelection) selection).getFirstElement();

					// If we had a selection let change the state
					if (obj != null) {
						DataSetDocument uds = (DataSetDocument) obj;
						if (uds instanceof DataSetDocument) {

							// Do it only, if it is allowed to mark this kind of document as payed.
							if (DocumentType.getType(uds.getIntValueByKey("category")).hasPayed()) {

								// change the state
								uds.setPayed(payed);

								// also in the database
								Data.INSTANCE.updateDataSet(uds);

								// Refresh the table with orders.
								view.refresh();
							}
						}
					}
				}
			}
		}
	}
}