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

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetVAT;
import com.sebulli.fakturama.views.datasettable.ViewVatTable;

/**
 * The VAT editor
 * 
 * @author Gerd Bartelt
 */
public class VatEditor extends Editor {
	
	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.vatEditor";

	// This UniDataSet represents the editor's input 
	private DataSetVAT vat;

	// SWT widgets of the editor
	private Text textName;
	private Text textDescription;
	private Text textValue;
	private Text txtCategory;

	// defines, if the payment is new created
	private boolean newVat;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public VatEditor() {
		tableViewID = ViewVatTable.ID;
		editorID = "vat";
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
		vat.setBooleanValueByKey("deleted", false);

		// Set the payment data
		vat.setStringValueByKey("name", textName.getText());
		vat.setStringValueByKey("category", txtCategory.getText());
		vat.setStringValueByKey("description", textDescription.getText());
		vat.setDoubleValueByKey("value", DataUtils.StringToDouble(textValue.getText() + "%"));


		// If it is a new VAT, add it to the VAT list and
		// to the data base
		if (newVat) {
			vat = Data.INSTANCE.getVATs().addNewDataSet(vat);
			newVat = false;
		}
		// If it's not new, update at least the data base
		else {
			Data.INSTANCE.getVATs().updateDataSet(vat);
		}

		// Refresh the table view of all payments
		refreshView();
	}

	/**
	 * There is no saveAs function
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * Initializes the editor. 
	 * If an existing data set is opened, the local variable "vat" is set to
	 * This data set.
	 * If the editor is opened to create a new one, a new data set is created and
	 * the local variable "vat" is set to this one.
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
		vat = (DataSetVAT) ((UniDataSetEditorInput) input).getUniDataSet();

		// test, if the editor is opened to create a new data set. This is,
		// if there is no input set.
		newVat = (vat == null);

		// If new ..
		if (newVat) {

			// Create a new data set
			vat = new DataSetVAT(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue MwSt.");

		} else {

			// Set the Editor's name to the payment name.
			setPartName(vat.getStringValueByKey("name"));
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
		 *  - id (constant)
		 */

		if (vat.getBooleanValueByKey("deleted")) { return true; }

		if (!vat.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!vat.getStringValueByKey("description").equals(textDescription.getText())) { return true; }
		if (!DataUtils.DoublesAreEqual(vat.getDoubleValueByKey("value"), DataUtils.StringToDouble(textValue.getText() + "%"))) { return true; }
		if (!vat.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }

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
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);

		// There is no invisible component, so no container has to be created
		//Composite invisible = new Composite(top, SWT.NONE);
		//invisible.setVisible(false);
		//GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		// Large VAT label
		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Steuersatz");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		// Name of the VAT
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(vat.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		// Category of the VAT
		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(vat.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		// The description
		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(vat.getStringValueByKey("description"));
		superviceControl(textDescription, 250);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDescription);

		// The value
		Label labelValue = new Label(top, SWT.NONE);
		labelValue.setText("Wert");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelValue);
		textValue = new Text(top, SWT.BORDER);
		textValue.setText(DataUtils.DoubleToFormatedPercent(vat.getDoubleValueByKey("value")));
		superviceControl(textValue, 16);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textValue);

		// Create the composite to make this payment to the standard payment. 
		Label labelStdVat = new Label(top, SWT.NONE);
		labelStdVat.setText("Standard");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStdVat);
		stdComposite = new StdComposite(top, vat, Data.INSTANCE.getVATs(), "standardvat", "dieser Steuersatz");

	}

}
