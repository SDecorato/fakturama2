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

public class VatEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.vatEditor";
	private DataSetVAT vat;
	private Text textName;
	private Text textDescription;
	private Text textValue;
	private Text txtCategory;

	private boolean newVat;

	public VatEditor() {
		tableViewID = ViewVatTable.ID;
		editorID = "vat";

	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		/*
		 * the following parameters are not saved: - id (constant)
		 */

		vat.setBooleanValueByKey("deleted", false);
		vat.setStringValueByKey("name", textName.getText());
		vat.setStringValueByKey("category", txtCategory.getText());
		vat.setStringValueByKey("description", textDescription.getText());
		vat.setDoubleValueByKey("value", DataUtils.StringToDouble(textValue.getText() + "%"));

		if (newVat) {
			vat = Data.INSTANCE.getVATs().addNewDataSet(vat);
			newVat = false;
		} else {
			Data.INSTANCE.getVATs().updateDataSet(vat);
		}

		refreshView();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		vat = (DataSetVAT) ((UniDataSetEditorInput) input).getUniDataSet();
		newVat = (vat == null);

		if (newVat) {
			vat = new DataSetVAT(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue MwSt.");
		} else {
			setPartName(vat.getStringValueByKey("name"));
		}
	}

	@Override
	public boolean isDirty() {
		/*
		 * the following parameters are not checked: - id (constant)
		 */

		if (vat.getBooleanValueByKey("deleted")) { return true; }

		if (!vat.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!vat.getStringValueByKey("description").equals(textDescription.getText())) { return true; }
		if (!DataUtils.DoublesAreEqual(vat.getDoubleValueByKey("value"), DataUtils.StringToDouble(textValue.getText() + "%"))) { return true; }
		if (!vat.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }

		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);

		Composite invisible = new Composite(top, SWT.NONE);
		invisible.setVisible(false);
		GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Steuersatz");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(vat.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(vat.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(vat.getStringValueByKey("description"));
		superviceControl(textDescription, 250);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDescription);

		Label labelValue = new Label(top, SWT.NONE);
		labelValue.setText("Wert");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelValue);
		textValue = new Text(top, SWT.BORDER);
		textValue.setText(DataUtils.DoubleToFormatedPercent(vat.getDoubleValueByKey("value")));
		superviceControl(textValue, 16);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textValue);

		Label labelStdVat = new Label(top, SWT.NONE);
		labelStdVat.setText("Standard");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStdVat);

		stdComposite = new StdComposite(top, vat, Data.INSTANCE.getVATs(), "standardvat", "dieser Steuersatz");

	}

}