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

public class PaymentEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.paymentEditor";
	private DataSetPayment payment;
	private Text textName;
	private Text textDescription;
	private Text textDiscountValue;
	private Text textDiscountDays;
	private Text textNetDays;
	private Text txtCategory;

	private boolean newPayment;

	public PaymentEditor() {
		tableViewID = ViewPaymentTable.ID;
		editorID = "payment";

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		/*
		 * the following parameters are not saved: - id (constant)
		 */

		payment.setBooleanValueByKey("deleted", false);
		payment.setStringValueByKey("name", textName.getText());
		payment.setStringValueByKey("description", textDescription.getText());
		payment.setStringValueByKey("category", txtCategory.getText());
		payment.setDoubleValueByKey("discountvalue", DataUtils.StringToDouble(textDiscountValue.getText()));
		payment.setStringValueByKey("discountdays", textDiscountDays.getText());
		payment.setStringValueByKey("netdays", textNetDays.getText());

		if (newPayment) {
			payment = Data.INSTANCE.getPayments().addNewDataSet(payment);
			newPayment = false;
		} else {
			Data.INSTANCE.getPayments().updateDataSet(payment);
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
		payment = (DataSetPayment) ((UniDataSetEditorInput) input).getUniDataSet();
		newPayment = (payment == null);

		if (newPayment) {
			payment = new DataSetPayment(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue Zahlungsmethode");
		} else {
			setPartName(payment.getStringValueByKey("name"));
		}
	}

	@Override
	public boolean isDirty() {
		/*
		 * the following parameters are not checked: - id (constant)
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
		labelTitle.setText("Zahlungsmethode");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(payment.getStringValueByKey("name"));
		superviceControl(textName, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(payment.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(payment.getStringValueByKey("description"));
		superviceControl(textDescription, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDescription);

		Label labelDiscountValue = new Label(top, SWT.NONE);
		labelDiscountValue.setText("Skonto");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDiscountValue);
		textDiscountValue = new Text(top, SWT.BORDER);
		textDiscountValue.setText(DataUtils.DoubleToFormatedPercent(payment.getDoubleValueByKey("discountvalue")));
		superviceControl(textDiscountValue, 12);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDiscountValue);

		Label labelDiscountDays = new Label(top, SWT.NONE);
		labelDiscountDays.setText("Tage Skonto");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDiscountDays);
		textDiscountDays = new Text(top, SWT.BORDER);
		textDiscountDays.setText(payment.getStringValueByKey("discountdays"));
		superviceControl(textDiscountDays, 8);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDiscountDays);

		Label labelNetDays = new Label(top, SWT.NONE);
		labelNetDays.setText("Tage Netto");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelNetDays);
		textNetDays = new Text(top, SWT.BORDER);
		textNetDays.setText(payment.getStringValueByKey("netdays"));
		superviceControl(textNetDays, 8);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(textNetDays);

		Label labelStdVat = new Label(top, SWT.NONE);
		labelStdVat.setText("Standard");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStdVat);

		stdComposite = new StdComposite(top, payment, Data.INSTANCE.getPayments(), "standardpayment", "diese Zahlmethode");

	}

}
