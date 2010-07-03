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
import com.sebulli.fakturama.data.DataSetPayment;
import com.sebulli.fakturama.views.datasettable.ViewPaymentTable;

/**
 * The payment editor
 * 
 * @author Gerd Bartelt
 */
public class PaymentEditor extends Editor {

	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.paymentEditor";

	// This UniDataSet represents the editor's input 
	private DataSetPayment payment;
	
	// SWT widgets of the editor
	private Text textName;
	private Text textDescription;
	private Text textDiscountValue;
	private Text textDiscountDays;
	private Text textNetDays;
	private Text txtCategory;

	// defines, if the contact is new created
	private boolean newPayment;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public PaymentEditor() {
		tableViewID = ViewPaymentTable.ID;
		editorID = "payment";
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
		payment.setBooleanValueByKey("deleted", false);

		// Set the payment data
		payment.setStringValueByKey("name", textName.getText());
		payment.setStringValueByKey("description", textDescription.getText());
		payment.setStringValueByKey("category", txtCategory.getText());
		payment.setDoubleValueByKey("discountvalue", DataUtils.StringToDouble(textDiscountValue.getText()));
		payment.setStringValueByKey("discountdays", textDiscountDays.getText());
		payment.setStringValueByKey("netdays", textNetDays.getText());

		// If it is a new payment, add it to the payment list and
		// to the data base
		if (newPayment) {
			payment = Data.INSTANCE.getPayments().addNewDataSet(payment);
			newPayment = false;
		} else {
			Data.INSTANCE.getPayments().updateDataSet(payment);
		}

		// Refresh the table view of all contacts
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
		payment = (DataSetPayment) ((UniDataSetEditorInput) input).getUniDataSet();

		// test, if the editor is opened to create a new data set. This is,
		// if there is no input set.
		newPayment = (payment == null);

		// If new ..
		if (newPayment) {

			// Create a new data set
			payment = new DataSetPayment(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue Zahlungsmethode");
			
		} else {

			// Set the Editor's name to the payment name.
			setPartName(payment.getStringValueByKey("name"));
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

		if (payment.getBooleanValueByKey("deleted")) { return true; }

		if (!payment.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!payment.getStringValueByKey("description").equals(textDescription.getText())) { return true; }
		if (!DataUtils.DoublesAreEqual(payment.getDoubleValueByKey("discountvalue"), DataUtils.StringToDouble(textDiscountValue.getText()))) { return true; }
		if (!payment.getStringValueByKey("discountdays").equals(textDiscountDays.getText())) { return true; }
		if (!payment.getStringValueByKey("netdays").equals(textNetDays.getText())) { return true; }
		if (!payment.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }

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
		// Composite invisible = new Composite(top, SWT.NONE);
		// invisible.setVisible(false);
		// GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		// Large payment label
		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Zahlungsmethode");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		// Payment name
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(payment.getStringValueByKey("name"));
		superviceControl(textName, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		// Payment category
		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(payment.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		// Payment description
		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(payment.getStringValueByKey("description"));
		superviceControl(textDescription, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDescription);

		// Payment discount value
		Label labelDiscountValue = new Label(top, SWT.NONE);
		labelDiscountValue.setText("Skonto");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDiscountValue);
		textDiscountValue = new Text(top, SWT.BORDER);
		textDiscountValue.setText(DataUtils.DoubleToFormatedPercent(payment.getDoubleValueByKey("discountvalue")));
		superviceControl(textDiscountValue, 12);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDiscountValue);

		// Payment days to pay the discount
		Label labelDiscountDays = new Label(top, SWT.NONE);
		labelDiscountDays.setText("Tage Skonto");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDiscountDays);
		textDiscountDays = new Text(top, SWT.BORDER);
		textDiscountDays.setText(payment.getStringValueByKey("discountdays"));
		superviceControl(textDiscountDays, 8);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDiscountDays);

		// Payment days to pay the net value
		Label labelNetDays = new Label(top, SWT.NONE);
		labelNetDays.setText("Tage Netto");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelNetDays);
		textNetDays = new Text(top, SWT.BORDER);
		textNetDays.setText(payment.getStringValueByKey("netdays"));
		superviceControl(textNetDays, 8);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textNetDays);

		// Create the composite to make this payment to the standard payment. 
		Label labelStdVat = new Label(top, SWT.NONE);
		labelStdVat.setText("Standard");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStdVat);
		stdComposite = new StdComposite(top, payment, Data.INSTANCE.getPayments(), "standardpayment", "diese Zahlmethode");

	}

}
