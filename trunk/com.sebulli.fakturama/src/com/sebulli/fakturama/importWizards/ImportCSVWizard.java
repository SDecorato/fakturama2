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

package com.sebulli.fakturama.importWizards;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;
import com.sebulli.fakturama.views.datasettable.ViewExpenditureTable;
import com.sebulli.fakturama.views.datasettable.ViewVatTable;

/**
 * A wizard to import tables in CSV file format
 * 
 * @author Gerd Bartelt
 */
public class ImportCSVWizard extends Wizard implements IImportWizard {

	// The first (and only) wizard page
	ImportCSVWizardPage mainPage;

	// The selected file to import
	String selectedFile = "";

	/**
	 * Constructor
	 * 
	 * Creates a new wizard with one page
	 */
	public ImportCSVWizard() {
		//T: Title of the CSV Import wizard
		setWindowTitle(_("Import CSV"));
		mainPage = new ImportCSVWizardPage();
		mainPage.setPageComplete(true);
		addPage(mainPage);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Performs any actions appropriate in response to the user having pressed
	 * the Finish button
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		return true;
	}

	/**
	 * Initializes this creation wizard using the passed workbench and object
	 * selection
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		FileDialog fileDialog = new FileDialog(workbench.getActiveWorkbenchWindow().getShell());
		fileDialog.setFilterPath("/");
		fileDialog.setFilterExtensions(new String[] { "*.csv" });
		
		//T: CSV Import File Dialog Title
		fileDialog.setText(_("Select file to import"));

		//T: CSV Import File Filter
		fileDialog.setFilterNames(new String[] { _("Table as CSV")+ " (*.csv)" });
		selectedFile = fileDialog.open();
		if (selectedFile != null) {

			// Import the selected file
			if (!selectedFile.isEmpty()) {

				CSVImporter csvImporter = new CSVImporter();
				csvImporter.importCSV(selectedFile, false);

				mainPage.setStatusText(csvImporter.getResult());

				// Find the expenditure table view
				ViewDataSetTable view = (ViewDataSetTable) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.findView(ViewExpenditureTable.ID);

				// Refresh it
				if (view != null)
					view.refresh();

				// Find the VAT table view
				view = (ViewDataSetTable) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewVatTable.ID);

				// Refresh it
				if (view != null)
					view.refresh();

			}
		}

	}

}
