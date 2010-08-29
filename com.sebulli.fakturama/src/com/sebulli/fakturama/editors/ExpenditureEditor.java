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

package com.sebulli.fakturama.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetExpenditure;
import com.sebulli.fakturama.views.datasettable.ViewExpenditureTable;

/**
 * The payment editor
 * 
 * @author Gerd Bartelt
 */
public class ExpenditureEditor extends Editor {

	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.expenditureEditor";

	// This UniDataSet represents the editor's input 
	private DataSetExpenditure expenditure;
	
	// SWT widgets of the editor
	private Text textName;
	private Text textDescription;
	private Text txtCategory;

	// defines, if the payment is new created
	private boolean newExpenditure;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public ExpenditureEditor() {
		tableViewID = ViewExpenditureTable.ID;
		editorID = "expenditure";
	}

	/**
	 * Saves the contents of this part
	 * 
	 * @param monitor Progress monitor
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		/*
		 * the following parameters are not saved:
		 * - id (constant)
		 */

		// Always set the editor's data set to "undeleted"
		expenditure.setBooleanValueByKey("deleted", false);

		// Set the payment data
		expenditure.setStringValueByKey("name", textName.getText());
		expenditure.setStringValueByKey("description", textDescription.getText());
		expenditure.setStringValueByKey("category", txtCategory.getText());

		// If it is a new payment, add it to the payment list and
		// to the data base
		if (newExpenditure) {
			expenditure = Data.INSTANCE.getExpenditures().addNewDataSet(expenditure);
			newExpenditure = false;
		}
		// If it's not new, update at least the data base
		else {
			Data.INSTANCE.getExpenditures().updateDataSet(expenditure);
		}

		// Refresh the table view of all payments
		refreshView();
		checkDirty();
	}

	/**
	 * There is no saveAs function
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * Initializes the editor. 
	 * If an existing data set is opened, the local variable "payment" is set to
	 * This data set.
	 * If the editor is opened to create a new one, a new data set is created and
	 * the local variable "payment" is set to this one.
	 * 
	 * @param input The editor's input
	 * @param site The editor's site
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		// Set the site and the input
		setSite(site);
		setInput(input);
		
		// Set the editor's data set to the editor's input
		expenditure = (DataSetExpenditure) ((UniDataSetEditorInput) input).getUniDataSet();

		// test, if the editor is opened to create a new data set. This is,
		// if there is no input set.
		newExpenditure = (expenditure == null);

		// If new ..
		if (newExpenditure) {

			// Create a new data set
			expenditure = new DataSetExpenditure(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue Ausgabe");
			
		} else {

			// Set the Editor's name to the payment name.
			setPartName(expenditure.getStringValueByKey("name"));
		}
	}

	/**
	 * Returns whether the contents of this part have changed since the last
	 * save operation
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		/*
		 * the following parameters are not checked:
		 * - id (constant)
		 */

		if (expenditure.getBooleanValueByKey("deleted")) { return true; }
		if (newExpenditure) { return true; }

		if (!expenditure.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!expenditure.getStringValueByKey("description").equals(textDescription.getText())) { return true; }

		return false;
	}

	/**
	 * Returns whether the "Save As" operation is supported by this part.

	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 * @return False, SaveAs is not allowed
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	* Creates the SWT controls for this workbench part
	* 
	* @param the parent control
	* @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	*/
	@Override
	public void createPartControl(Composite parent) {

		// Create the top Composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(top);

		// There is no invisible component, so no container has to be created
		// Composite invisible = new Composite(top, SWT.NONE);
		// invisible.setVisible(false);
		// GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		// Large payment label
		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Ausgaben");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(4, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		// Payment name
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(expenditure.getStringValueByKey("name"));
		superviceControl(textName, 32);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(textName);

		// Payment category
		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(expenditure.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(txtCategory);

		// Payment description
		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(expenditure.getStringValueByKey("description"));
		superviceControl(textDescription, 64);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(textDescription);

	}

}
