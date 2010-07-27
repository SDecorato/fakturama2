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

/**
 * The payment editor
 * 
 * @author Gerd Bartelt
 */
public class ShippingEditor extends Editor {

	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.shippingEditor";

	// This UniDataSet represents the editor's input 
	private DataSetShipping shipping;
	
	// SWT widgets of the editor
	private Text textName;
	private Text textDescription;
	private Combo comboVat;
	private ComboViewer comboViewer;
	private Combo comboAutoVat;
	private NetText netText;
	private GrossText grossText;
	private Text txtCategory;

	// These flags are set by the preference settings.
	// They define, if elements of the editor are displayed, or not.
	private boolean useNet;
	private boolean useGross;

	// These are (non visible) values of the document
	private UniData net;
	private Double vat = 0.0;
	private int vatId = 0;
	private int autoVat = 1;

	// defines, if the shipping is new created
	private boolean newShipping;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public ShippingEditor() {
		tableViewID = ViewShippingTable.ID;
		editorID = "shipping";
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
		shipping.setBooleanValueByKey("deleted", false);

		// Set the shipping data
		shipping.setStringValueByKey("name", textName.getText());
		shipping.setStringValueByKey("category", txtCategory.getText());
		shipping.setStringValueByKey("description", textDescription.getText());
		shipping.setDoubleValueByKey("value", net.getValueAsDouble());
		shipping.setIntValueByKey("vatid", vatId);
		shipping.setIntValueByKey("autovat", autoVat);

		// If it is a new shipping, add it to the shipping list and
		// to the data base
		if (newShipping) {
			shipping = Data.INSTANCE.getShippings().addNewDataSet(shipping);
			newShipping = false;
		}
		// If it's not new, update at least the data base
		else {
			Data.INSTANCE.getShippings().updateDataSet(shipping);
		}

		// Refresh the table view of all contacts
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
	 * If an existing data set is opened, the local variable "shipping" is set to
	 * This data set.
	 * If the editor is opened to create a new one, a new data set is created and
	 * the local variable "shipping" is set to this one.
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
		shipping = (DataSetShipping) ((UniDataSetEditorInput) input).getUniDataSet();

		// test, if the editor is opened to create a new data set. This is,
		// if there is no input set.
		newShipping = (shipping == null);

		// If new ..
		if (newShipping) {

			// Create a new data set
			shipping = new DataSetShipping(((UniDataSetEditorInput) input).getCategory());
			setPartName("neue Versandkosten");
			
		} else {

			// Set the Editor's name to the shipping name.
			setPartName(shipping.getStringValueByKey("name"));
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

		if (shipping.getBooleanValueByKey("deleted")) { return true; }

		if (!shipping.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!shipping.getStringValueByKey("description").equals(textDescription.getText())) { return true; }
		if (!DataUtils.DoublesAreEqual(shipping.getDoubleValueByKey("value"), net.getValueAsDouble())) { return true; }
		if (!shipping.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }
		if (shipping.getIntValueByKey("vatid") != vatId) { return true; }
		if (shipping.getIntValueByKey("autovat") != autoVat) { return true; }

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
	 * Show or hide the netText and grossText widget, depending on the
	 * setting "autoVat".
	 */
	private void autoVatChanged() {
		switch (autoVat) {
		
		// The gross value is based on the net value by using
		// a constant Vat factor
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
			
		// The shipping net value is based on the gross value using the
		// same VAT factor as the items. The gross value is kept constant.
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
		
		// The shipping gross value is based on the net value using the
		// same VAT factor as the items. The net value is kept constant.
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

	/**
	* Creates the SWT controls for this workbench part
	* 
	* @param the parent control
	* @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	*/
	@Override
	public void createPartControl(Composite parent) {
		
		// Some of this editos's control elements can be hidden.
		// Get the these settings from the preference store
		useNet = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 2);
		useGross = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 1);

		// Get the auto VAT setting
		autoVat = shipping.getIntValueByKey("autovat");

		if (autoVat == DataSetShipping.SHIPPINGVATGROSS)
			useGross = true;
		if (autoVat == DataSetShipping.SHIPPINGVATNET)
			useNet = true;

		// Get the VAT ID
		vatId = shipping.getIntValueByKey("vatid");

		// Get the VAT by the VAT ID
		try {
			vat = Data.INSTANCE.getVATs().getDatasetById(vatId).getDoubleValueByKey("value");
		} catch (IndexOutOfBoundsException e) {
			vat = 0.0;
		}
		
		// Create the top Composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);

		// Create an invisible container for all hidden components
		// There is no invisible component, so no container has to be created
		//Composite invisible = new Composite(top, SWT.NONE);
		//invisible.setVisible(false);
		//GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		// Create the title
		Label labelTitle = new Label(top, SWT.NONE);
		labelTitle.setText("Versandkosten");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		// Shipping name
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(shipping.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		// Shipping category
		Label labelCategory = new Label(top, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(top, SWT.BORDER);
		txtCategory.setText(shipping.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		// Shipping description
		Label labelDescription = new Label(top, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(top, SWT.BORDER);
		textDescription.setText(shipping.getStringValueByKey("description"));
		superviceControl(textDescription, 250);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDescription);

		// Shipping value
		Label labelValue = new Label(top, SWT.NONE);
		labelValue.setText("Wert");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelValue);

		// Variable to store the net value
		net = new UniData(UniDataType.STRING, shipping.getDoubleValueByKey("value"));

		// Create a composite that contains a widget for the net and gross value
		Composite netGrossComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns((useNet && useGross) ? 2 : 1).applyTo(netGrossComposite);

		// Create a net label
		if (useNet) {
			Label netValueLabel = new Label(netGrossComposite, SWT.NONE);
			netValueLabel.setText("Netto");
			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(netValueLabel);
		}

		// Create a gross label
		if (useGross) {
			Label grossValueLabel = new Label(netGrossComposite, SWT.NONE);
			grossValueLabel.setText("Brutto");
			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(grossValueLabel);
		}

		// Create a net text widget
		if (useNet) {
			netText = new NetText(this, netGrossComposite, SWT.BORDER | SWT.RIGHT, net, vat);
			GridDataFactory.swtDefaults().hint(60, SWT.DEFAULT).applyTo(netText.getNetText());
		}

		// Create a gross text widget
		if (useGross) {
			grossText = new GrossText(this, netGrossComposite, SWT.BORDER | SWT.RIGHT, net, vat);
			GridDataFactory.swtDefaults().hint(60, SWT.DEFAULT).applyTo(grossText.getGrossText());
		}

		// If net and gross were created, link both together
		// so, if one is modified, the other will be recalculated.
		if (useNet && useGross) {
			netText.setGrossText(grossText.getGrossText());
			grossText.setNetText(netText.getNetText());
		}

		// VAT Label
		Label labelVat = new Label(top, SWT.NONE);
		labelVat.setText("MwSt.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelVat);

		// VAT combo list
		comboVat = new Combo(top, SWT.BORDER);
		comboViewer = new ComboViewer(comboVat);
		comboViewer.setContentProvider(new UniDataSetContentProvider());
		comboViewer.setLabelProvider(new UniDataSetLabelProvider());

		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				// Handle selection changed event 
				ISelection selection = event.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				
				// If one element is selected
				if (!structuredSelection.isEmpty()) {

					// Get the first element ...
					Object firstElement = structuredSelection.getFirstElement();

					// Get the selected VAT
					UniDataSet uds = (UniDataSet) firstElement;

					// Store the old value
					Double oldVat = vat;

					// Get the new value
					vatId = uds.getIntValueByKey("id");
					vat = uds.getDoubleValueByKey("value");

					// Recalculate the price values if gross is selected,
					// So the gross value will stay constant.
					if (!useNet) {
						net.setValue(net.getValueAsDouble() * ((1 + oldVat) / (1 + vat)));
					}

					// Update net and gross text widget
					if (netText != null)
						netText.setVatValue(vat);
					if (grossText != null)
						grossText.setVatValue(vat);

					// Check, if the document has changed.
					checkDirty();
				}
			}
		});

		// Create a JFace combo viewer for the VAT list
		comboViewer.setInput(Data.INSTANCE.getVATs().getActiveDatasets());
		try {
			comboViewer.setSelection(new StructuredSelection(Data.INSTANCE.getVATs().getDatasetById(vatId)), true);
		} catch (IndexOutOfBoundsException e) {
			comboVat.setText("invalid");
			vatId = -1;
		}

		// Create a label for the automatic VAT calculation
		Label labelAutoVat = new Label(top, SWT.NONE);
		labelAutoVat.setText("Berechnung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelAutoVat);

		// Create a combox list box for the automatic VAT calculation
		comboAutoVat = new Combo(top, SWT.BORDER);
		comboAutoVat.add("konstaner Mehrwersteuersatz");
		if (useGross)
			comboAutoVat.add("MwSt. wird aus MwSt. der Waren berechnet - Bruttowert bleibt konstant");
		if (useNet)
			comboAutoVat.add("MwSt. wird aus MwSt. der Waren berechnet - Nettowert bleibt konstant");

		comboAutoVat.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			
				// Get the selected list entry
				autoVat = comboAutoVat.getSelectionIndex();
				
				// If no gross values are used, do not allow to select
				// the entry "SHIPPINGVATGROSS"
				if (!useGross && (autoVat == DataSetShipping.SHIPPINGVATGROSS))
					autoVat = DataSetShipping.SHIPPINGVATNET;
				
				// Display or hide the net and gross widgets
				autoVatChanged();

				// Check, if the document has changed.
				checkDirty();
			}
		});

		// On creating this editor, select the entry of the autoVat list,
		// that is set by the shipping.
		try {
			comboAutoVat.select(autoVat);
			autoVatChanged();
		} catch (IndexOutOfBoundsException e) {
			comboAutoVat.setText("invalid");
			autoVat = DataSetShipping.SHIPPINGVATGROSS;
		}

		// Create the composite to make this payment to the standard payment. 
		Label labelStdVat = new Label(top, SWT.NONE);
		labelStdVat.setText("Standard");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStdVat);

		stdComposite = new StdComposite(top, shipping, Data.INSTANCE.getShippings(), "standardshipping", "diese Versandkosten", 1);

	}

}