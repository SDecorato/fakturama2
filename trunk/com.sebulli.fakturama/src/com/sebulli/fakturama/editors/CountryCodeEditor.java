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
import com.sebulli.fakturama.data.DataSetCountryCode;
import com.sebulli.fakturama.views.datasettable.ViewCountryCodeTable;

/**
 * The text editor
 * 
 * @author Gerd Bartelt
 */
public class CountryCodeEditor extends Editor {
	
	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.countryCodeEditor";

	// This UniDataSet represents the editor's input 
	private DataSetCountryCode countryCode;

	// SWT widgets of the editor
	private Text textName;
	private Text textCode;
	private Text txtCategory;

	// defines, if the text is new created
	private boolean newCountryCode;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public CountryCodeEditor() {
		tableViewID = ViewCountryCodeTable.ID;
		editorID = "countrycode";
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
		countryCode.setBooleanValueByKey("deleted", false);
		
		// Set the text data
		countryCode.setStringValueByKey("name", textName.getText());
		countryCode.setStringValueByKey("code", textCode.getText());
		countryCode.setStringValueByKey("category", txtCategory.getText());

		// If it is a new text, add it to the text list and
		// to the data base
		if (newCountryCode) {
			countryCode = Data.INSTANCE.getCountryCodes().addNewDataSet(countryCode);
			newCountryCode = false;
		}
		// If it's not new, update at least the data base
		else {
			Data.INSTANCE.getCountryCodes().updateDataSet(countryCode);
		}

		// Refresh the table view of all country codes
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
	 * If an existing data set is opened, the local variable "text" is set to
	 * This data set.
	 * If the editor is opened to create a new one, a new data set is created and
	 * the local variable "text" is set to this one.
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
		countryCode = (DataSetCountryCode) ((UniDataSetEditorInput) input).getUniDataSet();

		// test, if the editor is opened to create a new data set. This is,
		// if there is no input set.
		newCountryCode = (countryCode == null);

		// If new ..
		if (newCountryCode) {
			
			// Create a new data set
			countryCode = new DataSetCountryCode(((UniDataSetEditorInput) input). getCategory());
			setPartName("neuer Ländercode");
		} else {
			
			// Set the Editor's name to the shipping name.
			setPartName(countryCode.getStringValueByKey("name"));
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

		if (countryCode.getBooleanValueByKey("deleted")) { return true; }
		if (newCountryCode) { return true; }

		if (!countryCode.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!countryCode.getStringValueByKey("code").equals(textCode.getText())) { return true; }
		if (!countryCode.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }

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

		// Create the title
		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Ländercodes");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		// The name
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Ländername");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(countryCode.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		// The category
		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(countryCode.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		// The code
		Label labelCode = new Label(top, SWT.NONE);
		labelCode.setText("Ländercode");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCode);
		textCode = new Text(top, SWT.BORDER );
		textCode.setText(countryCode.getStringValueByKey("code"));
		superviceControl(textCode, 100);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textCode);
	}

}
