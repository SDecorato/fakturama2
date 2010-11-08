/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.editors;

import static com.sebulli.fakturama.Translate._;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.DataSetExpenditure;
import com.sebulli.fakturama.data.DataSetExpenditureItem;
import com.sebulli.fakturama.data.DataSetList;
import com.sebulli.fakturama.data.DataSetVAT;
import com.sebulli.fakturama.data.UniData;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.data.UniDataType;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.UniDataSetTableColumn;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTableContentProvider;
import com.sebulli.fakturama.views.datasettable.ViewExpenditureTable;
import com.sebulli.fakturama.views.datasettable.ViewListTable;

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
	private Combo comboCategory;
	private DateTime dtDate;
	private Text textName;
	private Text textNr;
	private Text textDocumentNr;
	private TableViewer tableViewerItems;
	private CurrencyText textPaidValue;
	private CurrencyText textTotalValue;
	private UniData paidValue = new UniData(UniDataType.DOUBLE, 0.0);
	private UniData totalValue = new UniData(UniDataType.DOUBLE, 0.0);


	// The items of this document
	private DataSetArray<DataSetExpenditureItem> expenditureItems;

	// Flag, if item editing is active
	ExpenditureItemEditingSupport itemEditingSupport = null;

	// These flags are set by the preference settings.
	// They define, if elements of the editor are displayed, or not.
	private boolean useGross;

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
	 * @param monitor
	 *            Progress monitor
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		/*
		 * the following parameters are not saved: - id (constant)
		 */

		// Cancel the item editing
		if (itemEditingSupport != null)
			itemEditingSupport.cancelAndSave();

		// Always set the editor's data set to "undeleted"
		expenditure.setBooleanValueByKey("deleted", false);

		// Set the payment data
		expenditure.setStringValueByKey("name", textName.getText());
		expenditure.setStringValueByKey("category", comboCategory.getText());
		expenditure.setStringValueByKey("nr", textNr.getText());
		expenditure.setStringValueByKey("documentnr", textDocumentNr.getText());

		// Set all the items
		ArrayList<DataSetExpenditureItem> itemDatasets = expenditureItems.getActiveDatasets();
		String itemsString = "";

		for (DataSetExpenditureItem itemDataset : itemDatasets) {

			// Get the ID of this expenditure item and
			int id = itemDataset.getIntValueByKey("id");

			DataSetExpenditureItem item = null;

			// Get an existing item, or use the temporary item
			if (id >= 0) {
				item = Data.INSTANCE.getExpenditureItems().getDatasetById(id);

				// Copy the values to the existing expenditure item.
				item.setStringValueByKey("name", itemDataset.getStringValueByKey("name"));
				item.setStringValueByKey("category", itemDataset.getStringValueByKey("category"));
				item.setDoubleValueByKey("price", itemDataset.getDoubleValueByKey("price"));
				item.setIntValueByKey("vatid", itemDataset.getIntValueByKey("vatid"));
			}
			else
				item = itemDataset;

			// Updates the list of billing account
			updateBillingAccount(item);
			
			
			// If the ID of this item is -1, this was a new item.
			// In this case, update the existing one
			if (id >= 0) {
				Data.INSTANCE.getExpenditureItems().updateDataSet(item);
			}
			else {
				// Create a new expenditure item
				itemDataset = Data.INSTANCE.getExpenditureItems().addNewDataSet(itemDataset);
				id = itemDataset.getIntValueByKey("id");
			}

			// Collect all item IDs in a sting and separate them by a comma
			if (itemsString.length() > 0)
				itemsString += ",";
			itemsString += Integer.toString(id);
		}
		// Set the string value
		expenditure.setStringValueByKey("items", itemsString);
		
		// Set total and paid value
		expenditure.setDoubleValueByKey("paid",paidValue.getValueAsDouble() );
		expenditure.setDoubleValueByKey("total",totalValue.getValueAsDouble() );

		// If it is a new expenditure, add it to the expenditure list and
		// to the data base
		if (newExpenditure) {
			expenditure = Data.INSTANCE.getExpenditures().addNewDataSet(expenditure);
			newExpenditure = false;
		}
		// If it's not new, update at least the data base
		else {
			Data.INSTANCE.getExpenditures().updateDataSet(expenditure);
		}

		// Refresh the table view of all expenditures
		refreshView(ViewListTable.ID);
		refreshView();
		checkDirty();
	}

	/**
	 * Updates the list of billing account. Check, whether there is already
	 * an entry with the same name 
	 * 
	 * @param vatID
	 * 			VAT id of the expenditure item
	 * @param category
	 * 			Category of the expenditure item
	 */
	public static void updateBillingAccount(DataSetExpenditureItem item) {
		
		// Get list of all billing accounts
		ArrayList<DataSetList> billing_accounts = Data.INSTANCE.getListEntries().getActiveDatasetsByCategory("billing_accounts");

		boolean found = false;

		// Get the VAT of this item
		int vatID  = item.getIntValueByKey("vatid");
		DataSetVAT vat = null;
		String vatName = "";
		if (vatID >= 0) {
			vat = Data.INSTANCE.getVATs().getDatasetById(vatID);
			vatName = vat.getStringValueByKey("name");
		}

		// Search for the billing account with the same name as the item
		for (DataSetList billing_account : billing_accounts) {
			if (billing_account.getStringValueByKey("name").equals(item.getStringValueByKey("category"))) {

				found = true;

				// Get the VAT value from the billing account list
				String billingVatName = billing_account.getStringValueByKey("value");

				// If the vat is set to another value, refresh it.
				if (!vatName.equalsIgnoreCase(billingVatName)) {
					billing_account.setStringValueByKey("value", vatName);
					Data.INSTANCE.getListEntries().updateDataSet(billing_account);
				}
				break;
			}
		}

		// Entry not found in the billing accounts list, so create a new
		// one.
		if (!found) {
			DataSetList billing_account;
			billing_account = new DataSetList("billing_accounts", item.getStringValueByKey("category"), vatName);
			Data.INSTANCE.getListEntries().addNewDataSet(billing_account);
		}

	}
	
	
	/**
	 * There is no saveAs function
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * Initializes the editor. If an existing data set is opened, the local
	 * variable "payment" is set to This data set. If the editor is opened to
	 * create a new one, a new data set is created and the local variable
	 * "payment" is set to this one.
	 * 
	 * @param input
	 *            The editor's input
	 * @param site
	 *            The editor's site
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
			
			//T: Expenditure Editor: Name of a new Expenditure
			setPartName(_("New Expenditure"));

			// Us the last category
			int lastExpenditureSize = Data.INSTANCE.getExpenditures().getActiveDatasets().size();
			if (lastExpenditureSize > 0) {
				DataSetExpenditure lastExpenditure = Data.INSTANCE.getExpenditures().getActiveDatasets().get(lastExpenditureSize - 1);
				expenditure.setStringValueByKey("category", lastExpenditure.getStringValueByKey("category"));

			}

		}
		else {

			// Set the Editor's name to the payment name.
			setPartName(expenditure.getStringValueByKey("name"));
		}

		// Create a set of new temporary items.
		// These items exist only in the memory.
		// If the editor is opened, the items from the document are
		// copied to this item set. If the editor is closed or saved,
		// these items are copied back to the document and to the data base.
		expenditureItems = new DataSetArray<DataSetExpenditureItem>();

		// Get all items by ID from the item string
		String itemsString = expenditure.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");

		// Parse the item string ..
		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				}
				catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}

				// And copy the item to a new one
				DataSetExpenditureItem item = new DataSetExpenditureItem(Data.INSTANCE.getExpenditureItems().getDatasetById(id));
				expenditureItems.getDatasets().add(item);
			}
		}

		// If the expenditure list is empty, add one entry
		if (expenditureItems.getDatasets().isEmpty())
			addNewItem();

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
		 * - total (this value is calculated during the idDirty() method
		 */

		// Calculate the total sum of all items
		calculateTotal();
		
		// Check, if a cell is being modified at this moment
		if (tableViewerItems != null)
			if (tableViewerItems.isCellEditorActive() && (itemEditingSupport != null))
				return true;

		if (expenditure.getBooleanValueByKey("deleted")) { return true; }
		if (newExpenditure) { return true; }

		if (!expenditure.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!expenditure.getStringValueByKey("category").equals(comboCategory.getText())) { return true; }
		if (!expenditure.getStringValueByKey("nr").equals(textNr.getText())) { return true; }
		if (!expenditure.getStringValueByKey("documentnr").equals(textDocumentNr.getText())) { return true; }

		// Test all the expenditure items
		String itemsString = "";
		ArrayList<DataSetExpenditureItem> itemDatasets = expenditureItems.getActiveDatasets();
		for (DataSetExpenditureItem itemDataset : itemDatasets) {
			int id = itemDataset.getIntValueByKey("id");

			// There is no existing item
			if (id < 0)
				return true;

			DataSetExpenditureItem item = Data.INSTANCE.getExpenditureItems().getDatasetById(id);

			if (!item.getStringValueByKey("name").equals(itemDataset.getStringValueByKey("name"))) { return true; }
			if (!item.getStringValueByKey("category").equals(itemDataset.getStringValueByKey("category"))) { return true; }
			if (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("price"), itemDataset.getDoubleValueByKey("price"))) { return true; }
			if (item.getIntValueByKey("vatid") != itemDataset.getIntValueByKey("vatid")) { return true; }

			if (itemsString.length() > 0)
				itemsString += ",";
			itemsString += Integer.toString(id);
		}

		// Compare also the items string.
		// So the expenditure is dirty, if new items are added or items have
		// been deleted.
		if (!expenditure.getStringValueByKey("items").equals(itemsString)) { return true; }

		// Compare paid value
		if (!DataUtils.DoublesAreEqual(expenditure.getDoubleValueByKey("paid"), paidValue.getValueAsDouble() )) { return true; }
		
		return false;
	}

	/**
	 * Calculate the total sum of all expenditure items
	 */
	private void calculateTotal() {

		// Get all the items
		ArrayList<DataSetExpenditureItem> itemDatasets = expenditureItems.getActiveDatasets();

		Double total = 0.0;
		
		for (DataSetExpenditureItem itemDataset : itemDatasets) {

			// Add the value of the item to the total sum
			total += itemDataset.getDoubleValueByKey("price");
		}
		
		// Update the text widget
		totalValue.setValue(total);
		textTotalValue.update();
		
	}
	
	
	/**
	 * Returns whether the "Save As" operation is supported by this part.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 * @return False, SaveAs is not allowed
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Returns, if this editor used net or gross values.
	 * 
	 * @return True, if the document uses gross values.
	 */
	public boolean getUseGross() {
		return useGross;
	}

	/**
	 * Sets a flag, if item editing is active
	 * 
	 * @param active
	 *            , TRUE, if editing is active
	 */
	public void setItemEditing(ExpenditureItemEditingSupport itemEditingSupport) {
		this.itemEditingSupport = itemEditingSupport;
	}

	/**
	 * Adds an empty expenditure item
	 */
	private void addNewItem() {

		DataSetExpenditureItem newItem = new DataSetExpenditureItem("Name", "", 0.0, 0);

		// Use the standard VAT value
		newItem.setIntValueByKey("id", -(expenditureItems.getDatasets().size() + 1));
		newItem.setIntValueByKey("vatid", Integer.parseInt(Data.INSTANCE.getProperty("standardvat")));
		expenditureItems.getDatasets().add(newItem);

	}

	/**
	 * Creates the SWT controls for this workbench part
	 * 
	 * @param the
	 *            parent control
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Get the some settings from the preference store
		useGross = (Activator.getDefault().getPreferenceStore().getInt("DOCUMENT_USE_NET_GROSS") == 1);

		// Create the top Composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);

		// There is no invisible component, so no container has to be created
		// Composite invisible = new Composite(top, SWT.NONE);
		// invisible.setVisible(false);
		// GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		// Large title
		Label labelTitle = new Label(top, SWT.NONE);
		
		//T: ExpenditureEditor - Title
		labelTitle.setText(_("Expense Voucher"));
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelTitle);
		makeLargeLabel(labelTitle);

		// Expenditure category
		Label labelCategory = new Label(top, SWT.NONE);

		labelCategory.setText(_("Category"));
		//T: Tool Tip Text
		labelCategory.setToolTipText(_("Category of this expense voucher. E.g. 'Bank', 'Cash', 'Credit Card'"));
		
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		comboCategory = new Combo(top, SWT.BORDER);
		comboCategory.setToolTipText(labelCategory.getToolTipText());
		comboCategory.setText(expenditure.getStringValueByKey("category"));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboCategory);

		// Add all category strings to the combo
		Object[] categories = Data.INSTANCE.getExpenditures().getCategoryStrings();
		for (Object category : categories) {
			comboCategory.add(category.toString());
		}

		superviceControl(comboCategory);

		// Document date
		Label labelDate = new Label(top, SWT.NONE);
		labelDate.setText(_("Date"));
		//T: Tool Tip Text
		labelDate.setToolTipText(_("Date of the voucher"));

		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDate);

		// Document date
		dtDate = new DateTime(top, SWT.DATE);
		dtDate.setToolTipText(labelDate.getToolTipText());
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(dtDate);

		// Set the dtDate widget to the expenditures date
		GregorianCalendar calendar = new GregorianCalendar();
		calendar = DataUtils.getCalendarFromDateString(expenditure.getStringValueByKey("date"));
		dtDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

		// Number
		Label labelNr = new Label(top, SWT.NONE);
		labelNr.setText(_("Voucher No."));
		//T: Tool Tip Text
		labelNr.setToolTipText(_("Consecutive number of all vouchers"));

		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelNr);
		textNr = new Text(top, SWT.BORDER);
		textNr.setText(expenditure.getStringValueByKey("nr"));
		textNr.setToolTipText(labelNr.getToolTipText());
		superviceControl(textNr, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textNr);

		// Document number
		Label labelDocumentNr = new Label(top, SWT.NONE);
		labelDocumentNr.setText(_("Document No."));
		//T: Tool Tip Text
		labelDocumentNr.setToolTipText(_("Number found on the voucher. (Document No of the supplier)"));

		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDocumentNr);
		textDocumentNr = new Text(top, SWT.BORDER);
		textDocumentNr.setText(expenditure.getStringValueByKey("documentnr"));
		textDocumentNr.setToolTipText(labelDocumentNr.getToolTipText());
		superviceControl(textDocumentNr, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDocumentNr);

		// Supplier name
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText(_("Supplier"));
		//T: Tool Tip Text
		labelName.setToolTipText(_("Name of the supplier"));
		
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(top, SWT.BORDER);
		textName.setText(expenditure.getStringValueByKey("name"));
		textName.setToolTipText(labelName.getToolTipText());
		superviceControl(textName, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		// Container for the label and the add and delete button.
		Composite addButtonComposite = new Composite(top, SWT.NONE | SWT.RIGHT);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(addButtonComposite);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addButtonComposite);

		// Items label
		Label labelItems = new Label(addButtonComposite, SWT.NONE | SWT.RIGHT);
		//T: ExpenditureEditor - Label Items
		labelItems.setText(_("Items", "EXPENDITURE"));
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(labelItems);

		// Item add button
		Label addButton = new Label(addButtonComposite, SWT.NONE);
		try {
			addButton.setImage((Activator.getImageDescriptor("/icons/16/plus_16.png").createImage()));
			//T: Tool Tip Text
			addButton.setToolTipText(_("Add a new item"));

		}
		catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addButton);
		addButton.addMouseListener(new MouseAdapter() {

			// Add a new item with default properties
			public void mouseDown(MouseEvent e) {
				tableViewerItems.cancelEditing();

				addNewItem();

				tableViewerItems.refresh();
				checkDirty();
			}
		});

		// Item delete button
		Label deleteButton = new Label(addButtonComposite, SWT.NONE);
		try {
			deleteButton.setImage((Activator.getImageDescriptor("/icons/16/delete_16.png").createImage()));
			//T: Tool Tip Text
			deleteButton.setToolTipText(_("Delete the selected item"));
		}
		catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(deleteButton);
		deleteButton.addMouseListener(new MouseAdapter() {

			// Delete the selected item
			public void mouseDown(MouseEvent e) {
				ISelection selection = tableViewerItems.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (!structuredSelection.isEmpty()) {
					// get first element ...
					Object firstElement = structuredSelection.getFirstElement();
					UniDataSet uds = (UniDataSet) firstElement;
					// Delete it (mark it as deleted)
					uds.setBooleanValueByKey("deleted", true);
					tableViewerItems.refresh();
					checkDirty();
				}
			}
		});

		// Composite that contains the table
		Composite tableComposite = new Composite(top, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		// The table viewer
		tableViewerItems = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewerItems.getTable().setLinesVisible(true);
		tableViewerItems.getTable().setHeaderVisible(true);
		tableViewerItems.setContentProvider(new ViewDataSetTableContentProvider(tableViewerItems));

		// Create the table columns
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.LEFT, _("Name"), 200, 100, false, "name", new ExpenditureItemEditingSupport(this,
				tableViewerItems, 1));
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.LEFT, _("Type"), 200, 0, true, "category", new ExpenditureItemEditingSupport(this,
				tableViewerItems, 2));
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, _("VAT"), 50, 0, true, "$ExpenditureItemVatPercent",
				new ExpenditureItemEditingSupport(this, tableViewerItems, 3));
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, _("Net"), 85, 0, true, "price", new ExpenditureItemEditingSupport(this,
				tableViewerItems, 4));
		//T: Used as heading of a table. Keep the word short.
		new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, _("Gross"), 85, 0, true, "$ExpenditureItemGrossPrice",
				new ExpenditureItemEditingSupport(this, tableViewerItems, 5));

		// Create the top Composite
		Composite bottom = new Composite(top, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2,1).applyTo(bottom);
		
		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(bottom);

		// Paid value
		Label labelPaidValue = new Label(bottom, SWT.NONE);
		labelPaidValue.setText(_("Paid Value") + ":");
		//T: Tool Tip Text
		labelPaidValue.setToolTipText(_("The paid value (e.g. 97$, if the total value was 100$ with 3% discount."));
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelPaidValue);

		paidValue.setValue(expenditure.getStringValueByKey("paid"));

		textPaidValue = new CurrencyText(this, bottom, SWT.BORDER | SWT.RIGHT, paidValue);
		textPaidValue.setToolTipText(labelPaidValue.getToolTipText());
		superviceControl(textPaidValue.getText(), 32);
		GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).align(SWT.END, SWT.CENTER).applyTo(textPaidValue.getText());

		// Total value
		Label labelTotalValue = new Label(bottom, SWT.NONE);
		labelTotalValue.setText(_("Total Value") + ":");
		//T: Tool Tip Text
		labelTotalValue.setToolTipText(_("The total value of the voucher (without discount)."));
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelTotalValue);

		totalValue.setValue(expenditure.getStringValueByKey("total"));

		textTotalValue = new CurrencyText(this, bottom, SWT.BORDER | SWT.RIGHT, totalValue);
		textTotalValue.getText().setEditable(false);
		textTotalValue.setToolTipText(labelTotalValue.getToolTipText());
//		superviceControl(textTotalValue.getText(), 32);
		GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).align(SWT.END, SWT.CENTER).applyTo(textTotalValue.getText());


		// Fill the table with the items
		tableViewerItems.setInput(expenditureItems);

	}

	/**
	 * Return the table items
	 * 
	 * @return The table items
	 */
	public TableViewer getTableViewerItems() {
		return tableViewerItems;
	}

}
