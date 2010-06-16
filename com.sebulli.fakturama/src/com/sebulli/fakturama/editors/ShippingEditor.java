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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetShipping;
import com.sebulli.fakturama.data.UniData;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.data.UniDataType;
import com.sebulli.fakturama.views.datasettable.ViewShippingTable;

public class ShippingEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.shippingEditor";
	private DataSetShipping shipping;
	private Text textName;
	private Text textDescription;
	private Combo comboVat;
	private ComboViewer comboViewer;
	private Combo comboAutoVat;
	private NetText netText;
	private GrossText grossText;
	private Text txtCategory;
	private boolean useNet;
	private boolean useGross;
	private UniData net;
	private Double vat = 0.0;
	private int vatId = 0;
	private int autoVat = 1;

	private boolean newShipping;

	public ShippingEditor() {
		tableViewID = ViewShippingTable.ID;
		editorID = "shipping";

	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		/*
		 * the following parameters are not saved: - id (constant)
		 */

		shipping.setBooleanValueByKey("deleted", false);
		shipping.setStringValueByKey("name", textName.getText());
		shipping.setStringValueByKey("category", txtCategory.getText());
		shipping.setStringValueByKey("description", textDescription.getText());
		shipping.setDoubleValueByKey("value", net.getValueAsDouble());
		shipping.setIntValueByKey("vatid", vatId);
		shipping.setIntValueByKey("autovat", autoVat);

		if (newShipping) {
			shipping = Data.INSTANCE.getShippings().addNewDataSet(shipping);
			newShipping = false;
		} else {
			Data.INSTANCE.getShippings().updateDataSet(shipping);
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
		shipping = (DataSetShipping) ((UniDataSetEditorInput) input).getUniDataSet();
		newShipping = (shipping == null);

		if (newShipping) {
			shipping = new DataSetShipping(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue Versandkosten");
		} else {
			setPartName(shipping.getStringValueByKey("name"));
		}
	}

	@Override
	public boolean isDirty() {

		/*
		 * the following parameters are not checked: - id (constant)
		 */

		if (shipping.getBooleanValueByKey("deleted")) { return true; }

		if (!shipping.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!shipping.getStringValueByKey("description").equals(textDescription.getText())) { return true; }
		if (!DataUtils.DoublesAreEqual(shipping.getDoubleValueByKey("value"), net.getValueAsDouble())) { return true; }
		if (!shipping.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }
		if (shipping.getIntValueByKey("vatid") != vatId) { return true; }
		if (shipping.getIntValueByKey("autovat") != autoVat) { return true; }

		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void autoVatChanged() {
		switch (autoVat) {
		case DataSetShipping.SHIPPINGVATFIX:
			comboVat.setVisible(true);
			if (netText != null) {
				netText.setVisible(true);
				netText.setVatValue(vat);
			}
			if (grossText != null) {
				grossText.setVisible(true);
				grossText.setVatValue(vat);
			}
			break;
		case DataSetShipping.SHIPPINGVATGROSS:
			comboVat.setVisible(false);
			if (netText != null) {
				netText.setVisible(false);
				netText.setVatValue(0.0);
			}
			if (grossText != null) {
				grossText.setVisible(true);
				grossText.setVatValue(0.0);
			}
			break;
		case DataSetShipping.SHIPPINGVATNET:
			comboVat.setVisible(false);
			if (netText != null) {
				netText.setVisible(true);
				netText.setVatValue(0.0);
			}
			if (grossText != null) {
				grossText.setVisible(false);
				grossText.setVatValue(0.0);
			}
			break;
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		useNet = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 2);
		useGross = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 1);

		autoVat = shipping.getIntValueByKey("autovat");

		if (autoVat == DataSetShipping.SHIPPINGVATGROSS)
			useGross = true;
		if (autoVat == DataSetShipping.SHIPPINGVATNET)
			useNet = true;

		vatId = shipping.getIntValueByKey("vatid");

		try {
			vat = Data.INSTANCE.getVATs().getDatasetById(vatId).getDoubleValueByKey("value");
		} catch (IndexOutOfBoundsException e) {
			vat = 0.0;
		}
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);

		Composite invisible = new Composite(top, SWT.NONE);
		invisible.setVisible(false);
		GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Versandkosten");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(shipping.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(shipping.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(shipping.getStringValueByKey("description"));
		superviceControl(textDescription, 250);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDescription);

		Label labelValue = new Label(top, SWT.NONE);
		labelValue.setText("Wert");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelValue);

		net = new UniData(UniDataType.STRING, shipping.getDoubleValueByKey("value"));

		Composite netGrossComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns((useNet && useGross) ? 2 : 1).applyTo(netGrossComposite);

		if (useNet) {
			Label netValueLabel = new Label(netGrossComposite, SWT.NONE);
			netValueLabel.setText("Netto");
			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(netValueLabel);
		}

		if (useGross) {
			Label grossValueLabel = new Label(netGrossComposite, SWT.NONE);
			grossValueLabel.setText("Brutto");
			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(grossValueLabel);
		}

		if (useNet) {
			netText = new NetText(this, netGrossComposite, SWT.BORDER | SWT.RIGHT, net, vat);
			GridDataFactory.swtDefaults().hint(60, SWT.DEFAULT).applyTo(netText.getNetText());
		}

		if (useGross) {
			grossText = new GrossText(this, netGrossComposite, SWT.BORDER | SWT.RIGHT, net, vat);
			GridDataFactory.swtDefaults().hint(60, SWT.DEFAULT).applyTo(grossText.getGrossText());
		}

		if (useNet && useGross) {
			netText.setGrossText(grossText.getGrossText());
			grossText.setNetText(netText.getNetText());
		}

		Label labelVat = new Label(top, SWT.NONE);
		labelVat.setText("MwSt.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelVat);

		comboVat = new Combo(top, SWT.BORDER);
		comboViewer = new ComboViewer(comboVat);
		comboViewer.setContentProvider(new UniDataSetContentProvider());
		comboViewer.setLabelProvider(new UniDataSetLabelProvider());

		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				// Handle selection changed event here
				ISelection selection = event.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (!structuredSelection.isEmpty()) {
					// get first element ...
					Object firstElement = structuredSelection.getFirstElement();
					UniDataSet uds = (UniDataSet) firstElement;
					Double oldVat = vat;

					vatId = uds.getIntValueByKey("id");
					vat = uds.getDoubleValueByKey("value");
					if (!useNet) {
						net.setValue(net.getValueAsDouble() * ((1 + oldVat) / (1 + vat)));
					}

					if (netText != null)
						netText.setVatValue(vat);
					if (grossText != null)
						grossText.setVatValue(vat);
					checkDirty();
				}
			}
		});

		comboViewer.setInput(Data.INSTANCE.getVATs().getActiveDatasets());
		try {
			comboViewer.setSelection(new StructuredSelection(Data.INSTANCE.getVATs().getDatasetById(vatId)), true);
		} catch (IndexOutOfBoundsException e) {
			comboVat.setText("invalid");
			vatId = -1;
		}

		Label labelAutoVat = new Label(top, SWT.NONE);
		labelAutoVat.setText("Berechnung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelAutoVat);

		comboAutoVat = new Combo(top, SWT.BORDER);
		comboAutoVat.add("konstaner Mehrwersteuersatz");
		if (useGross)
			comboAutoVat.add("MwSt. wird aus MwSt. der Waren berechnet - Bruttowert bleibt konstant");
		if (useNet)
			comboAutoVat.add("MwSt. wird aus MwSt. der Waren berechnet - Nettowert bleibt konstant");

		comboAutoVat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoVat = comboAutoVat.getSelectionIndex();
				if (!useGross && (autoVat == DataSetShipping.SHIPPINGVATGROSS))
					autoVat = DataSetShipping.SHIPPINGVATNET;
				autoVatChanged();
				checkDirty();
			}
		});

		try {
			comboAutoVat.select(autoVat);
			autoVatChanged();
		} catch (IndexOutOfBoundsException e) {
			comboAutoVat.setText("invalid");
			autoVat = DataSetShipping.SHIPPINGVATGROSS;
		}

		Label labelStdVat = new Label(top, SWT.NONE);
		labelStdVat.setText("Standard");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStdVat);

		stdComposite = new StdComposite(top, shipping, Data.INSTANCE.getShippings(), "standardshipping", "diese Versandkosten");

	}

}
