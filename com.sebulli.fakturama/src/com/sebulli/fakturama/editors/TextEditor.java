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
import com.sebulli.fakturama.data.DataSetText;
import com.sebulli.fakturama.views.datasettable.ViewTextTable;

public class TextEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.textEditor";
	private DataSetText text;
	private Text textName;
	private Text textText;
	private Text txtCategory;

	private boolean newText;

	public TextEditor() {
		tableViewID = ViewTextTable.ID;
		editorID = "text";

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		/*
		 * the following parameters are not saved: - id (constant)
		 */

		text.setBooleanValueByKey("deleted", false);
		text.setStringValueByKey("name", textName.getText());
		text.setStringValueByKey("text", textText.getText());
		text.setStringValueByKey("category", txtCategory.getText());

		if (newText) {
			text = Data.INSTANCE.getTexts().addNewDataSet(text);
			newText = false;
		} else {
			Data.INSTANCE.getTexts().updateDataSet(text);
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
		text = (DataSetText) ((UniDataSetEditorInput) input).getUniDataSet();
		newText = (text == null);

		if (newText) {
			text = new DataSetText(((UniDataSetEditorInput) input).getCategory());
			setPartName("neuer Text");
		} else {
			setPartName(text.getStringValueByKey("name"));
		}
	}

	@Override
	public boolean isDirty() {
		/*
		 * the following parameters are not checked: - id (constant)
		 */

		if (text.getBooleanValueByKey("deleted")) { return true; }

		if (!text.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!text.getStringValueByKey("text").equals(textText.getText())) { return true; }
		if (!text.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }

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

		Label labelTitle = new Label(top, SWT.BORDER);
		labelTitle.setText("Text");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		Label labelName = new Label(top, SWT.BORDER);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(text.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		Label labelCategory = new Label(top, SWT.BORDER);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(text.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		Label labelText = new Label(top, SWT.BORDER);
		labelText.setText("Text");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelText);
		textText = new Text(top, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		textText.setText(text.getStringValueByKey("text"));
		superviceControl(textText, 10000);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(textText);
	}

}
