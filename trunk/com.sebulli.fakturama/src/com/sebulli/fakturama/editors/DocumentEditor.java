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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.actions.CreateOODocumentAction;
import com.sebulli.fakturama.actions.NewDocumentAction;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.DataSetContact;
import com.sebulli.fakturama.data.DataSetDocument;
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DataSetPayment;
import com.sebulli.fakturama.data.DataSetProduct;
import com.sebulli.fakturama.data.DataSetShipping;
import com.sebulli.fakturama.data.DataSetText;
import com.sebulli.fakturama.data.DataSetVAT;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.data.UniData;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.data.UniDataType;
import com.sebulli.fakturama.dialogs.SelectContactDialog;
import com.sebulli.fakturama.dialogs.SelectProductDialog;
import com.sebulli.fakturama.dialogs.SelectTextDialog;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.UniDataSetTableColumn;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTableContentProvider;
import com.sebulli.fakturama.views.datasettable.ViewDocumentTable;

/**
 * The document editor for all types of document like
 * letter, order, confirmation, invoice, delivery,
 * credit and dunning
 * 
 * @author Gerd Bartelt
 */
public class DocumentEditor extends Editor {

	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.documentEditor";

	// This UniDataSet represents the editor's input 
	private DataSetDocument document;


	// SWT components of the editor
	private Text txtName;
	private DateTime dtDate;
	private Text txtCustomerRef;
	private Text txtAddress;
	private Combo comboNoVat;
	private ComboViewer comboViewerNoVat;
	private Text txtInvoiceRef;
	private TableViewer tableViewerItems;
	private Text txtMessage;
	private Button bPayed;
	private Composite payedContainer;
	private Composite payedDataContainer = null;
	private Combo comboPayment;
	private ComboViewer comboViewerPayment;
	private Spinner spDueDays;
	private DateTime dtIssueDate;
	private DateTime dtPayedDate;
	private Label itemsSum;
	private Text itemsDiscount;
	private Combo comboShipping;
	private ComboViewer comboViewerShipping;
	private Text shippingValue;
	private Label vatValue;
	private Label totalValue;

	// These flags are set by the preference settings.
	// They define, if elements of the editor are displayed, or not.
	private boolean useGross;

	// The items of this document
	private DataSetArray<DataSetItem> items;

	// The type of this document
	private DocumentType documentType;

	// These are (non visible) values of the document
	private int addressId;
	private boolean noVat;
	private String noVatName;
	private String noVatDescription;
	private int paymentId;
	private UniData payedValue = new UniData(UniDataType.DOUBLE, 0.0);
	private int shippingId;
	private Double shipping = 0.0;
	private Double shippingVat = 0.0;
	private String shippingVatDescription = "";
	private int shippingAutoVat = DataSetShipping.SHIPPINGVATGROSS;
	private Double total = 0.0;
	private int dunningLevel = 0;
	
	// Flag, if item editing is active
	ItemEditingSupport itemEditingSupport = null;
	
	// Action to print this document's content.
	// Print means: Export the document in an OpenOffice document
	CreateOODocumentAction printAction;
	
	// defines, if the contact is new created
	private boolean newDocument;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public DocumentEditor() {
		tableViewID = ViewDocumentTable.ID;
		editorID = "document";
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
		 * - progress (not modified by editor) 
		 * - transaction (not modified by editor)
		 * - webshopid (not modified by editor)
		 *  - webshopdate (not modified by editor)
		 *   ITEMS: - id (constant)
		 *          - deleted (is checked by the items string)
		 *          - shared (not modified by editor)
		 */
		
		// Cancel the item editing
		if (itemEditingSupport != null)
			itemEditingSupport.cancelAndSave();
		
		boolean wasDirty = isDirty();

		if (newDocument) {
			// Check, if the document number is the next one
			if (documentType != DocumentType.LETTER) {
				int result = setNextNr(txtName.getText(),"name", Data.INSTANCE.getDocuments() );

				// It's not the next free ID
				if (result == ERROR_NOT_NEXT_ID) {
					// Display an error message
					MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
					messageBox.setText("Fehler in Dokumentennummer");
					messageBox.setMessage("Dokument hat nicht die nächste freie Nummer: " + getNextNr() + 
											"\nSiehe unter Einstellungen/Nummernkreise.");
					messageBox.open();
				}
			}

		}
		
		// Exit save if there is a document with the same number
		if (thereIsOneWithSameNumber())
			return;

		
		// Always set the editor's data set to "undeleted"
		document.setBooleanValueByKey("deleted", false);

		// Set the document type
		document.setIntValueByKey("category", documentType.getInt());

		// Set name and date
		document.setStringValueByKey("name", txtName.getText());
		document.setStringValueByKey("date", DataUtils.getDateTimeAsString(dtDate));
		document.setStringValueByKey("servicedate", document.getStringValueByKey("date"));

		
		document.setIntValueByKey("addressid", addressId);
		String addressById = "";

		// Test, if the txtAddress field was modified
		// and write the content of the txtAddress to the documents address or
		// delivery address 
		boolean addressModified = false;
		// if it's a delivery note, compare the delivery address 
		if (documentType == DocumentType.DELIVERY) {
			if (!document.getStringValueByKey("deliveryaddress").equals(txtAddress.getText()))
				addressModified = true;
			document.setStringValueByKey("deliveryaddress", txtAddress.getText());
			if (addressId > 0)
				addressById = Data.INSTANCE.getContacts().getDatasetById(addressId).getAddress(true);
		} else {
			if (!document.getStringValueByKey("address").equals(txtAddress.getText()))
				addressModified = true;
			document.setStringValueByKey("address", txtAddress.getText());
			if (addressId > 0)
				addressById = Data.INSTANCE.getContacts().getDatasetById(addressId).getAddress(false);
		}

		// Show a warning, if the entered address is not similar to the address
		// of the document, set by the address ID.
		if ((addressId > 0) && (addressModified)) {
			if (DataUtils.similarity(addressById, txtAddress.getText()) < 0.75) {
				MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
				messageBox.setText("Bitte überprüfen");
				messageBox.setMessage("Diesem Dokument ist folgende Adresse zugeordnet:\n\n" + addressById
						+ "\n\nSie haben eine davon abweichende Adresse eingegeben.");
				messageBox.open();
			}
		}

		// Set the customer reference number
		document.setStringValueByKey("customerref", txtCustomerRef.getText());

		// Set the payment values depending on if the document is payed or not
		// Set the shipping values
		if (comboPayment != null) {
			document.setStringValueByKey("paymentdescription", comboPayment.getText());
		}
		document.setIntValueByKey("paymentid", paymentId);
		
		if (bPayed != null) {
			String paymentText = "";
			
			if (bPayed.getSelection()) {
				document.setBooleanValueByKey("payed", true);
				document.setStringValueByKey("paydate", DataUtils.getDateTimeAsString(dtPayedDate));
				document.setDoubleValueByKey("payvalue", payedValue.getValueAsDouble());
				
				// Use the text for "payed" from the current payment 
				if (paymentId >= 0) {
					paymentText = Data.INSTANCE.getPayments().getDatasetById(paymentId).getStringValueByKey("payedtext"); 
				}

			} else {
				document.setBooleanValueByKey("payed", false);
				document.setIntValueByKey("duedays", spDueDays.getSelection());
				document.setDoubleValueByKey("payvalue", 0.0);
			
				// Use the text for "unpayed" from the current payment 
				if (paymentId >= 0) {
					paymentText = Data.INSTANCE.getPayments().getDatasetById(paymentId).getStringValueByKey("unpayedtext"); 
				}

			}
			document.setStringValueByKey("paymenttext", paymentText);
			
		}
		
		// Set the shipping values
		if (comboShipping != null) {
			document.setStringValueByKey("shippingdescription", comboShipping.getText());
		}
		document.setIntValueByKey("shippingid", shippingId);
		document.setDoubleValueByKey("shipping", shipping);
		document.setDoubleValueByKey("shippingvat", shippingVat);
		document.setStringValueByKey("shippingvatdescription", shippingVatDescription);
		document.setIntValueByKey("shippingautovat", shippingAutoVat);
		
		// Set the discount value
		if (itemsDiscount != null)
			document.setDoubleValueByKey("itemsdiscount", DataUtils.StringToDoubleDiscount(itemsDiscount.getText()));
		
		// Set the total value.
		document.setDoubleValueByKey("total", total);
		
		// Set the message
		document.setStringValueByKey("message", txtMessage.getText());

		// Set the whole vat of the document to zero
		document.setBooleanValueByKey("novat", noVat);
		document.setStringValueByKey("novatname", noVatName);
		document.setStringValueByKey("novatdescription", noVatDescription);
		
		// Set the dunning level
		document.setIntValueByKey("dunninglevel", dunningLevel);

		// Create a new document ID, if this is a new document
		int documentId = document.getIntValueByKey("id");
		if (newDocument) {
			documentId = Data.INSTANCE.getDocuments().getNextFreeId();
		}

		// Set all the items
		ArrayList<DataSetItem> itemDatasets = items.getActiveDatasets();
		String itemsString = "";

		for (DataSetItem itemDataset : itemDatasets) {

			// Get the ID of this item and
			int id = itemDataset.getIntValueByKey("id");
			// the ID of the owner document
			int owner = itemDataset.getIntValueByKey("owner");
			
			boolean saveNewItem = true;
			DataSetItem item = null;

			// If the ID of this item is -1, this was a new item
			if (id >= 0) {
				item = Data.INSTANCE.getItems().getDatasetById(id);
				// Compare all data of the item in this document editor
				// with the item in the document.
				boolean modified = ((!item.getStringValueByKey("name").equals(itemDataset.getStringValueByKey("name")))
						|| (!item.getStringValueByKey("itemnr").equals(itemDataset.getStringValueByKey("itemnr")))
						|| (!item.getStringValueByKey("description").equals(itemDataset.getStringValueByKey("description")))
						|| (!item.getStringValueByKey("category").equals(itemDataset.getStringValueByKey("category")))
						|| (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("quantity"), itemDataset.getDoubleValueByKey("quantity")))
						|| (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("price"), itemDataset.getDoubleValueByKey("price")))
						|| (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("discount"), itemDataset.getDoubleValueByKey("discount")))
						|| (item.getIntValueByKey("owner") != itemDataset.getIntValueByKey("owner"))
						|| (item.getIntValueByKey("vatid") != itemDataset.getIntValueByKey("vatid"))
						|| (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("vatvalue"), itemDataset.getDoubleValueByKey("vatvalue")))
						|| (item.getBooleanValueByKey("novat") != itemDataset.getBooleanValueByKey("novat"))
						|| (!item.getStringValueByKey("vatname").equals(itemDataset.getStringValueByKey("vatname"))) || (!item.getStringValueByKey(
						"vatdescription").equals(itemDataset.getStringValueByKey("vatdescription"))));
				
				// If the item was modified and was shared with other documents,
				// than we should make a copy and save it new.
				// We also save it, if it was a new item with no owner yet, 
				saveNewItem = ((owner < 0) || (modified && ((owner != document.getIntValueByKey("id")) || item.getBooleanValueByKey("shared"))));
			} else {
				// It was a new item with no ID set
				saveNewItem = true;
			}

			// Create a new item
			// The owner of this new item is the document from this editor.
			// And because it's new, it is not shared with other documents.
			if (saveNewItem) {
				itemDataset.setIntValueByKey("owner", documentId);
				itemDataset.setBooleanValueByKey("shared", false);
				itemDataset = Data.INSTANCE.getItems().addNewDataSet(itemDataset);
				id = itemDataset.getIntValueByKey("id");
			}
			// If it's not new, copy the items's data from the editor to the
			// items in the data base
			else {
				item.setStringValueByKey("name", itemDataset.getStringValueByKey("name"));
				item.setStringValueByKey("itemnr", itemDataset.getStringValueByKey("itemnr"));
				item.setStringValueByKey("description", itemDataset.getStringValueByKey("description"));
				item.setStringValueByKey("category", itemDataset.getStringValueByKey("category"));
				item.setDoubleValueByKey("quantity", itemDataset.getDoubleValueByKey("quantity"));
				item.setDoubleValueByKey("price", itemDataset.getDoubleValueByKey("price"));
				item.setDoubleValueByKey("discount", itemDataset.getDoubleValueByKey("discount"));
				item.setIntValueByKey("owner", itemDataset.getIntValueByKey("owner"));
				item.setIntValueByKey("vatid", itemDataset.getIntValueByKey("vatid"));
				item.setBooleanValueByKey("novat", itemDataset.getBooleanValueByKey("novat"));
				item.setDoubleValueByKey("vatvalue", itemDataset.getDoubleValueByKey("vatvalue"));
				item.setStringValueByKey("vatname", itemDataset.getStringValueByKey("vatname"));
				item.setStringValueByKey("vatdescription", itemDataset.getStringValueByKey("vatdescription"));
				Data.INSTANCE.getItems().updateDataSet(item);
			}
			
			// Collect all item IDs in a sting and separate them by a comma
			if (itemsString.length() > 0)
				itemsString += ",";
			itemsString += Integer.toString(id);
		}
		// Set the string value
		document.setStringValueByKey("items", itemsString);

		// Set the "addressfirstline" value  to the first line of the
		// contact address
		if (addressId > 0) {
			document.setStringValueByKey("addressfirstline", Data.INSTANCE.getContacts().getDatasetById(addressId).getName());
		} else {
			String s = txtAddress.getText().split("\n")[0];
			document.setStringValueByKey("addressfirstline", s);
		}

		// Mark the (modified) document as "not printed"
		if (wasDirty)
			document.setBooleanValueByKey("printed", false);
		
		// If it is a new document,
		if (newDocument) {
			
			// Create this in the data base
			document = Data.INSTANCE.getDocuments().addNewDataSet(document);
			
			// If it's an invoice, set the "invoiceid" to the ID.
			// So all documents will inherit this ID
			if ((documentType == DocumentType.INVOICE) && (document.getIntValueByKey("id") != document.getIntValueByKey("invoiceid"))) {
				document.setIntValueByKey("invoiceid", document.getIntValueByKey("id"));
				Data.INSTANCE.getDocuments().updateDataSet(document);
			}

			// Now, it is no longer new.
			newDocument = false;
			
			// Create a new editor input.
			// So it's no longer the parent data
			this.setInput(new UniDataSetEditorInput(document));

		} else {
			// Do not create a new data set - just update the old one
			Data.INSTANCE.getDocuments().updateDataSet(document);
		}

		// Refresh the table view
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
	 * If an existing data set is opened, the local variable "document" is 
	 * set to This data set.
	 * If the editor is opened to create a new one, a new data set is created
	 * and the local variable "contact" is set to this one.
	 * 
	 * @param input The editor's input
	 * @param site The editor's site
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		// Set the site and the input
		setSite(site);
		setInput(input);

		// Set the editors data set to the editors input
		document = (DataSetDocument) ((UniDataSetEditorInput) input).getUniDataSet();

		// If the document is a duplicate of an other document,
		// the input is the parent document.
		DataSetDocument parent = document;
		boolean duplicated = ((UniDataSetEditorInput) input).getDuplicate();

		// The document is new, if there is no document, or if the
		// flag for duplicated was set.
		newDocument = (document == null) || duplicated;

		// If new ..
		if (newDocument) {

			//  .. get the document type (=the category) to ..
			String category = ((UniDataSetEditorInput) input).getCategory();
			documentType = DocumentType.getType(category);
			if (documentType == DocumentType.NONE)
				documentType = DocumentType.ORDER;

			// create a new data set in this document type
			if (duplicated)
				document = new DataSetDocument(documentType, parent);
			else
				document = new DataSetDocument(documentType);

			// Copy the entry "message", or reset it to ""
			if (!Activator.getDefault().getPreferenceStore().getBoolean("DOCUMENT_COPY_MESSAGE_FROM_PARENT"))
				document.setStringValueByKey("message", "");
			
			
			// Set the editor ID to the document type
			editorID = documentType.getTypeAsString();
			
			// get the parents document type
			DocumentType documentTypeParent = DocumentType.NONE;
			if (parent != null)
				documentTypeParent = DocumentType.getType(parent.getIntValueByKey("category"));

			// If it's a dunning, increase the dunning level by 1
			if (documentType == DocumentType.DUNNING) {
				if (documentTypeParent == DocumentType.DUNNING)
					dunningLevel = document.getIntValueByKey("dunninglevel") + 1;
				else
					dunningLevel = 1;
			}
			
			// Set the editors name
			setPartName(documentType.getNewText());
			
			// In a new document, set some standard values
			if (!duplicated) {
				// Default shipping
				shippingId = Data.INSTANCE.getPropertyAsInt("standardshipping");
				shipping = Data.INSTANCE.getShippings().getDatasetById(shippingId).getDoubleValueByKey("value");
				document.setStringValueByKey("shippingdescription", Data.INSTANCE.getShippings().getDatasetById(shippingId).getStringValueByKey("description"));
				shippingAutoVat = Data.INSTANCE.getShippings().getDatasetById(shippingId).getIntValueByKey("autovat");

				// Default payment
				paymentId = Data.INSTANCE.getPropertyAsInt("standardpayment");
				document.setStringValueByKey("paymentdescription", Data.INSTANCE.getPayments().getDatasetById(paymentId).getStringValueByKey("description"));

			}

			// Get the next document number
			document.setStringValueByKey("name", getNextNr());

		} 
		// If an existing document was opened ..
		else {
			
			// Get document type, set editorID 
			documentType = DocumentType.getType(document.getIntValueByKey("category"));
			editorID = documentType.getTypeAsString();

			// and the editor's part name
			setPartName(document.getStringValueByKey("name"));
		}

		// Create a set of new temporary items.
		// These items exist only in the memory.
		// If the editor is opened, the items from the document are
		// copied to this item set. If the editor is closed or saved,
		// these items are copied back to the document and to the data base.
		items = new DataSetArray<DataSetItem>();
		
		// Get all items by ID from the item string
		String itemsString = document.getStringValueByKey("items");
		String[] itemsStringParts = itemsString.split(",");
		
		// Parse the item string ..
		for (String itemsStringPart : itemsStringParts) {
			int id;
			if (itemsStringPart.length() > 0) {
				try {
					id = Integer.parseInt(itemsStringPart);
				} catch (NumberFormatException e) {
					Logger.logError(e, "Error parsing item string");
					id = 0;
				}
				int parentSign = DocumentType.getType(parent.getIntValueByKey("category")).sign();
				
				// And copy the item to a new one 
				DataSetItem item = Data.INSTANCE.getItems().getDatasetById(id);
				if (parentSign != documentType.sign())
					items.getDatasets().add(new DataSetItem(item, -1));
				else
					items.getDatasets().add(new DataSetItem(item));
			}
		}
	}

	/**
	 * Returns whether the contents of this part have changed since the last save operation
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {

		/*
		 * the following parameters are not checked:
		 * - id (constant)
		 * - addressfirstline (generated by editor)
		 * - progress (not modified by editor)
		 * - transaction (not modified by editor)
		 * - webshopid (not modified by editor)
		 * - webshopdate (not modified by editor)
		 *  TODO: - servicedate 
		 *  - total (generated by editor)
		 * 
		 * ITEMS: 
		 * - id (constant)
		 * - deleted (is checked by the items string)
		 * - shared (not modified by editor)
		 */
		
		// Check, if a cell is being modified at this moment
		if (tableViewerItems != null)
			if (tableViewerItems.isCellEditorActive() && (itemEditingSupport != null)) 
				return true;

		
		// Test all the document parameters
		if (document.getBooleanValueByKey("deleted")) { return true; }

		if (newDocument) { return true; }
		if (document.getIntValueByKey("category") != documentType.getInt()) { return true; }
		if (!document.getStringValueByKey("name").equals(txtName.getText())) { return true; }
		if (!document.getStringValueByKey("date").equals(DataUtils.getDateTimeAsString(dtDate))) { return true; }
		if (document.getIntValueByKey("addressid") != addressId) { return true; }
		if (documentType == DocumentType.DELIVERY) {
			if (!document.getStringValueByKey("deliveryaddress").equals(txtAddress.getText())) { return true; }
		} else {
			if (!document.getStringValueByKey("address").equals(txtAddress.getText())) { return true; }
		}

		if (!document.getStringValueByKey("customerref").equals(txtCustomerRef.getText())) { return true; }

		if (spDueDays != null)
			if (document.getBooleanValueByKey("payed") != bPayed.getSelection()) { return true; }
		if (bPayed != null) {
			if (bPayed.getSelection()) {
				if (!document.getStringValueByKey("paydate").equals(DataUtils.getDateTimeAsString(dtPayedDate))) { return true; }
				if (!DataUtils.DoublesAreEqual(payedValue.getValueAsDouble(), document.getDoubleValueByKey("payvalue"))) { return true; }
			} else {
				if (document.getIntValueByKey("duedays") != spDueDays.getSelection()) { return true; }
			}
		}
		if (comboPayment != null)
			if (!document.getStringValueByKey("paymentdescription").equals(comboPayment.getText())) { return true; }
		if (document.getIntValueByKey("paymentid") != paymentId) { return true; }

		if (itemsDiscount != null)
			if (!DataUtils.DoublesAreEqual(DataUtils.StringToDoubleDiscount(itemsDiscount.getText()), document.getDoubleValueByKey("itemsdiscount"))) { return true; }

		if (document.getIntValueByKey("shippingid") != shippingId) { return true; }
		if (!DataUtils.DoublesAreEqual(shipping, document.getDoubleValueByKey("shipping"))) { return true; }
		if (!DataUtils.DoublesAreEqual(shippingVat, document.getDoubleValueByKey("shippingvat"))) { return true; }
		if (comboShipping != null)
			if (!document.getStringValueByKey("shippingdescription").equals(comboShipping.getText())) { return true; }
		if (!document.getStringValueByKey("message").equals(txtMessage.getText())) { return true; }
		if (!document.getStringValueByKey("shippingvatdescription").equals(shippingVatDescription)) { return true; }
		if (document.getIntValueByKey("shippingautovat") != shippingAutoVat) { return true; }
		if (document.getBooleanValueByKey("novat") != noVat) { return true; }
		if (!document.getStringValueByKey("novatname").equals(noVatName)) { return true; }
		if (!document.getStringValueByKey("novatdescription").equals(noVatDescription)) { return true; }

		// Test all the document items
		String itemsString = "";
		ArrayList<DataSetItem> itemDatasets = items.getActiveDatasets();
		for (DataSetItem itemDataset : itemDatasets) {
			int id = itemDataset.getIntValueByKey("id");

			// If the owner is -1, it was a new item.
			// New items are always saved.
			if (itemDataset.getIntValueByKey("owner") < 0) {
					return true;
			} else {

				DataSetItem item = Data.INSTANCE.getItems().getDatasetById(id);
				if (!item.getStringValueByKey("name").equals(itemDataset.getStringValueByKey("name"))) { return true; }
				if (!item.getStringValueByKey("itemnr").equals(itemDataset.getStringValueByKey("itemnr"))) { return true; }
				if (!item.getStringValueByKey("description").equals(itemDataset.getStringValueByKey("description"))) { return true; }
				if (!item.getStringValueByKey("category").equals(itemDataset.getStringValueByKey("category"))) { return true; }
				if (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("quantity"), itemDataset.getDoubleValueByKey("quantity"))) { return true; }
				if (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("price"), itemDataset.getDoubleValueByKey("price"))) { return true; }
				if (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("discount"), itemDataset.getDoubleValueByKey("discount"))) { return true; }
				if (item.getIntValueByKey("owner") != itemDataset.getIntValueByKey("owner")) { return true; }
				if (item.getIntValueByKey("vatid") != itemDataset.getIntValueByKey("vatid")) { return true; }
				if (item.getBooleanValueByKey("novat") != itemDataset.getBooleanValueByKey("novat")) { return true; }
				if (!DataUtils.DoublesAreEqual(item.getDoubleValueByKey("vatvalue"), itemDataset.getDoubleValueByKey("vatvalue"))) { return true; }
				if (!item.getStringValueByKey("vatname").equals(itemDataset.getStringValueByKey("vatname"))) { return true; }
				if (!item.getStringValueByKey("vatdescription").equals(itemDataset.getStringValueByKey("vatdescription"))) { return true; }
			}
			if (itemsString.length() > 0)
				itemsString += ",";
			itemsString += Integer.toString(id);
		}

		// Compare also the items string.
		// So the document is dirty, if new items are added or items have
		// been deleted.
		if (!document.getStringValueByKey("items").equals(itemsString)) { return true; }

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
	 * Sets a flag, if item editing is active
	 * 
	 * @param active, TRUE, if editing is active
	 */
	public void setItemEditing(ItemEditingSupport itemEditingSupport) {
		this.itemEditingSupport = itemEditingSupport;
	}
	
	/**
	 * Set the "novat" in all items.
	 * If a document is marks as "novat", the VAT of all items
	 * is set to "0.0%"
	 */
	private void setItemsNoVat() {
		ArrayList<DataSetItem> itemDatasets = items.getActiveDatasets();
		for (DataSetItem itemDataset : itemDatasets) {
			itemDataset.setBooleanValueByKey("novat", noVat);
		}
	}

	/**
	 * Adds an empty item
	 * 
	 * @param newItem The new item
	 */
	private void addNewItem(DataSetItem newItem) {
		newItem.setIntValueByKey("id",- (items.getDatasets().size()+1) );
		items.getDatasets().add(newItem);
	}

	
	/**
	 * Returns the document
	 * 
	 * @return The document
	 */
	public DataSetDocument getDocument() {
		return document;
	}

	/**
	 * Returns the document type
	 *  
	 * @return The document type
	 */
	public DocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * If this document is duplicated, set the documents progress
	 * from 0% to 50%
	 * 
	 */
	public void childDocumentGenerated() {
		if (document.getIntValueByKey("progress") == 0) {
			document.setIntValueByKey("progress", 50);
			Data.INSTANCE.updateDataSet(document);
		}
	}

	/**
	 * Recalculate the total sum of this editor and write the
	 * result to the corresponding fields.
	 * 
	 */
	public void calculate() {
		
		// Recalculate only documents that contains price values.
		if (!documentType.hasPrice())
			return;

		// Get the sign of this document ( + or -)
		int sign = DocumentType.getType(document.getIntValueByKey("category")).sign();
		// Do the calculation
		document.calculate(items, shipping * sign, shippingVat, shippingVatDescription, shippingAutoVat, DataUtils.StringToDoubleDiscount(itemsDiscount
				.getText()), noVat, noVatDescription, 1.0);
		
		// Get the total result
		total = document.getSummary().getTotalGross().asDouble();
		
		// Set the items sum
		if (itemsSum != null) {
			if (useGross)
				itemsSum.setText(document.getSummary().getItemsGross().asFormatedString());
			else
				itemsSum.setText(document.getSummary().getItemsNet().asFormatedString());
		}

		// Set the shipping
		if (shippingValue != null) {
			if (useGross)
				shippingValue.setText(document.getSummary().getShippingGross().asFormatedString());
			else
				shippingValue.setText(document.getSummary().getShippingNet().asFormatedString());
		}

		// Set the VAT
		if (vatValue != null)
			vatValue.setText(document.getSummary().getTotalVat().asFormatedString());

		// Set the total value
		if (totalValue != null)
			totalValue.setText(document.getSummary().getTotalGross().asFormatedString());
	
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
	 * The shipping value has changed. So take the absolute value
	 * and recalculate the document's total sum.
	 */
	private void changeShippingValue() {
		
		// Get the new value and take the absolute value
		Double newShippingValue = DataUtils.StringToDouble(shippingValue.getText());
		if (newShippingValue < 0)
			newShippingValue = -newShippingValue;
		
		// If the shipping value has changed:
		// Set the shippingAutoVat to net or gross, depending on the
		// settings of this editor.
		if (!DataUtils.DoublesAreEqual(newShippingValue, shipping)) {
			if (useGross)
				shippingAutoVat = DataSetShipping.SHIPPINGVATGROSS;
			else
				shippingAutoVat = DataSetShipping.SHIPPINGVATNET;
		}
		
		// Recalculate the sum
		shipping = newShippingValue;
		calculate();
	}

	/**
	 * Create a SWT composite witch contains other SWT widgets
	 * like the payment date or the payed value.
	 * Depending on the parameter "payed" widgets are created to
	 * set the due values or the payed values. 
	 * 
	 * @param payed If true, the widgets for "payed" are generated
	 */
	private void createPayedComposite(boolean payed) {
		
		// If this widget exists yet, remove it to create it new.
		boolean changed = false;
		if ((payedDataContainer != null) && (!payedDataContainer.isDisposed())) {
			payedDataContainer.dispose();
			changed = true;
		}

		// Create the new payed container
		payedDataContainer = new Composite(payedContainer, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(4).applyTo(payedDataContainer);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BOTTOM).applyTo(payedDataContainer);

		// Should this container have the widgets for the state "payed" ?
		if (payed) {
			
			// Create the widget for the date, when the invoice was payed
			Label payedDateLabel = new Label(payedDataContainer, SWT.NONE);
			payedDateLabel.setText("am");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(payedDateLabel);

			dtPayedDate = new DateTime(payedDataContainer, SWT.DATE);
			GridDataFactory.swtDefaults().applyTo(dtPayedDate);
			
			// Set the payed date to the documents "paydate" parameter
			GregorianCalendar calendar = new GregorianCalendar();
			calendar = DataUtils.getCalendarFromDateString(document.getStringValueByKey("paydate"));
			dtPayedDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

			superviceControl(dtPayedDate);

			// Create the widget for the value
			Label payedValueLabel = new Label(payedDataContainer, SWT.NONE);
			payedValueLabel.setText("Betrag");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(payedValueLabel);

			// If it's the first time, that this document is marked as payed
			// (if the value is 0.0), than also set the date to "today"
			if (payedValue.getValueAsDouble() == 0.0) {
				payedValue.setValue(total);
				calendar = new GregorianCalendar();
				dtPayedDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			}
			CurrencyText txtPayValue = new CurrencyText(this, payedDataContainer, SWT.BORDER | SWT.RIGHT, payedValue);
			GridDataFactory.swtDefaults().hint(60, SWT.DEFAULT).applyTo(txtPayValue.getText());
		
		}
		// The container is created with the widgets that are shown,
		// if the invoice is not payed.
		else {
			
			// Reset the payed value to 0
			payedValue.setValue(0.0);
			
			// Create the due days label
			Label dueDaysLabel = new Label(payedDataContainer, SWT.NONE);
			dueDaysLabel.setText("zahlbar in");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(dueDaysLabel);

			// Creates the due days spinner
			spDueDays = new Spinner(payedDataContainer, SWT.BORDER | SWT.RIGHT);
			spDueDays.setMinimum(0);
			spDueDays.setMaximum(365);
			spDueDays.setSelection(document.getIntValueByKey("duedays"));
			spDueDays.setIncrement(1);
			spDueDays.setPageIncrement(10);
			GridDataFactory.swtDefaults().hint(50, SWT.DEFAULT).applyTo(spDueDays);
			
			// If the spinner's value changes, add the due days to the
			// day of today.
			spDueDays.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					GregorianCalendar calendar = new GregorianCalendar(dtDate.getYear(), dtDate.getMonth(), dtDate.getDay());
					calendar.add(Calendar.DAY_OF_MONTH, spDueDays.getSelection());
					dtIssueDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					checkDirty();
				}
			});
			
			// Create the issue date label
			Label issueDateLabel = new Label(payedDataContainer, SWT.NONE);
			issueDateLabel.setText("Tagen bis");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(issueDateLabel);

			// Create the issue date widget
			dtIssueDate = new DateTime(payedDataContainer, SWT.DATE);
			GridDataFactory.swtDefaults().applyTo(dtIssueDate);
			dtIssueDate.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// Calculate the difference between the date of the
					// isse date widget and the documents date,
					// calculate is in "days" and set the due day spinner
					GregorianCalendar calendarIssue = new GregorianCalendar(dtIssueDate.getYear(), dtIssueDate.getMonth(), dtIssueDate.getDay());
					GregorianCalendar calendarDocument = new GregorianCalendar(dtDate.getYear(), dtDate.getMonth(), dtDate.getDay());
					long difference = calendarIssue.getTimeInMillis() - calendarDocument.getTimeInMillis();
					// Calculate from milliseconds to days
					int days = (int) (difference / (1000 * 60 * 60 * 24));
					spDueDays.setSelection(days);
					checkDirty();
				}
			});
			
			// Add date and due days and set the issue date to the sum.
			GregorianCalendar calendar = new GregorianCalendar(dtDate.getYear(), dtDate.getMonth(), dtDate.getDay());
			calendar.add(Calendar.DAY_OF_MONTH, spDueDays.getSelection());
			dtIssueDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}

		// Resize the container
		payedContainer.layout(changed);
		payedContainer.pack(changed);
	}

	/**
	* Creates the SWT controls for this workbench part
	* 
	* @param the parent control
	* @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	*/
	@Override
	public void createPartControl(Composite parent) {
		
		// Printing an document from the document editor means:
		// Start OpenOffice in the background and export the document as
		// an OpenOffice document.
		printAction = new CreateOODocumentAction();
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);

		// Get the some settings from the preference store
		useGross = (Activator.getDefault().getPreferenceStore().getInt("DOCUMENT_USE_NET_GROSS") == 1);

		// These variables contain settings, that are not in
		// visible SWT widgets.
		addressId = document.getIntValueByKey("addressid");
		shippingId = document.getIntValueByKey("shippingid");
		paymentId = document.getIntValueByKey("paymentid");
		noVat = document.getBooleanValueByKey("novat");
		noVatName = document.getStringValueByKey("novatname");
		noVatDescription = document.getStringValueByKey("novatdescription");
		payedValue.setValue(document.getDoubleValueByKey("payvalue"));
		if (dunningLevel <= 0)
			dunningLevel = document.getIntValueByKey("dunninglevel");

		// Create the top composite of the editor
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(top);

		// Create an invisible container for all hidden components
		Composite invisible = new Composite(top, SWT.NONE);
		invisible.setVisible(false);
		GridDataFactory.fillDefaults().hint(0, 0).span(4, 1).applyTo(invisible);

		// Document number label
		Label labelName = new Label(top, SWT.NONE);
		labelName.setText("Nr.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);

		// Container for the document number and the date
		Composite nrDateComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(3).applyTo(nrDateComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, false).applyTo(nrDateComposite);

		// The document number is the document name
		txtName = new Text(nrDateComposite, SWT.BORDER);
		txtName.setText(document.getStringValueByKey("name"));
		superviceControl(txtName, 32);
		GridDataFactory.swtDefaults().hint(100, SWT.DEFAULT).applyTo(txtName);

		// Document date
		Label labelDate = new Label(nrDateComposite, SWT.NONE);
		labelDate.setText("Datum");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDate);

		// Document date
		dtDate = new DateTime(nrDateComposite, SWT.DATE);
		dtDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// If the date is modified, also modify the issue date.
				// (Let the due days constant).
				if (dtIssueDate != null) {
					GregorianCalendar calendar = new GregorianCalendar(dtDate.getYear(), dtDate.getMonth(), dtDate.getDay());
					calendar.add(Calendar.DAY_OF_MONTH, spDueDays.getSelection());
					dtIssueDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					checkDirty();
				}
			}
		});
		GridDataFactory.swtDefaults().applyTo(dtDate);
		superviceControl(dtDate);
		
		// Set the dtDate widget to the documents date
		GregorianCalendar calendar = new GregorianCalendar();
		calendar = DataUtils.getCalendarFromDateString(document.getStringValueByKey("date"));
		dtDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		
		
		// The titleComposite contains the tile and the document icon
		Composite titleComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(titleComposite);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BOTTOM).span(2, 1).grab(true, false).applyTo(titleComposite);

		// Set the title in large letters
		Label labelDocumentType = new Label(titleComposite, SWT.NONE);
		String documentTypeString = DocumentType.getString(document.getIntValueByKey("category"));
		if (documentType == DocumentType.DUNNING)
			documentTypeString = Integer.toString(dunningLevel) + "." + documentTypeString;
		labelDocumentType.setText(documentTypeString);
		makeLargeLabel(labelDocumentType);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(labelDocumentType);

		// Set the document icon
		Label labelDocumentTypeIcon = new Label(titleComposite, SWT.NONE);
		try {
			labelDocumentTypeIcon
					.setImage((Activator.getImageDescriptor("/icons/32/" + documentType.getTypeAsString().toLowerCase() + "_32.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.TOP).grab(true, false).applyTo(labelDocumentTypeIcon);

		
		// Customer reference label
		Label labelCustomerRef = new Label(top, SWT.NONE);
		labelCustomerRef.setText("Kd-Ref.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCustomerRef);

		// Customer reference
		txtCustomerRef = new Text(top, SWT.BORDER);
		txtCustomerRef.setText(document.getStringValueByKey("customerref"));
		superviceControl(txtCustomerRef, 32);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(txtCustomerRef);

		
		
		
		// The extra settings composite contains additional fields like
		// the no-Vat widget or a refernce to the invoice
		Composite xtraSettingsComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(xtraSettingsComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).span(1,2).grab(true, false).applyTo(xtraSettingsComposite);

		// A reference to the invoice
		Label labelInvoiceRef = new Label(documentType.hasInvoiceReference() ? xtraSettingsComposite : invisible, SWT.NONE);
		labelInvoiceRef.setText("Rechnung:");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BOTTOM).applyTo(labelInvoiceRef);
		txtInvoiceRef = new Text(documentType.hasInvoiceReference() ? xtraSettingsComposite : invisible, SWT.BORDER);
		int invoiceId = document.getIntValueByKey("invoiceid");
		if (invoiceId >= 0)
			txtInvoiceRef.setText(Data.INSTANCE.getDocuments().getDatasetById(invoiceId).getStringValueByKey("name"));
		else
			txtInvoiceRef.setText("---");
		txtInvoiceRef.setEditable(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(txtInvoiceRef);


		
		// This document should use a VAT of 0%
		Label labelNoVat = new Label(documentType.hasPrice() ? xtraSettingsComposite : invisible, SWT.NONE);
		labelNoVat.setText("MwSt:");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(labelNoVat);

		// combo list with all 0% VATs
		comboNoVat = new Combo(documentType.hasPrice() ? xtraSettingsComposite : invisible, SWT.BORDER);
		comboViewerNoVat = new ComboViewer(comboNoVat);
		comboViewerNoVat.setContentProvider(new NoVatContentProvider());
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(comboNoVat);
		comboViewerNoVat.addSelectionChangedListener(new ISelectionChangedListener() {

			// A combo entry is selected
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (!structuredSelection.isEmpty()) {
					
					// get first element ...
					Object firstElement = structuredSelection.getFirstElement();
					DataSetVAT dataSetVat = (DataSetVAT) firstElement;
					int id = dataSetVat.getIntValueByKey("id");
					
					// get the "no-VAT" values
					if (id >= 0) {
						noVat = true;
						noVatName = dataSetVat.getStringValueByKey("name");
						noVatDescription = dataSetVat.getStringValueByKey("description");
					} else {
						noVat = false;
						noVatName = "";
						noVatDescription = "";
					}
					
					// set all items to 0%
					setItemsNoVat();
					tableViewerItems.refresh();
					
					// recalculate the total sum
					calculate();
					checkDirty();
				}
			}
		});
		
		// Selects the no VAT entry
		comboViewerNoVat.setInput(Data.INSTANCE.getVATs().getDatasets());
		if (noVat)
			comboNoVat.setText(noVatName);
		else
			comboNoVat.select(0);
		
		
		
		// Group with tool bar with buttons to generate 
		// a new document from this document
		Group copyGroup = new Group(top, SWT.NONE);
		copyGroup.setText("aus " + documentType.getString() + " erzeugen");
		GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(copyGroup);
		GridDataFactory.fillDefaults().minSize(200, SWT.DEFAULT).align(SWT.END, SWT.BOTTOM).grab(true, false).span(1, 2).applyTo(copyGroup);

		// Toolbar
		ToolBar toolBarDuplicateDocument = new ToolBar(copyGroup, SWT.FLAT | SWT.WRAP);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.TOP).applyTo(toolBarDuplicateDocument);
		ToolBarManager tbmDuplicate = new ToolBarManager(toolBarDuplicateDocument);

		// Add buttons, depending on the document type
		switch (documentType) {
		case OFFER:
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.ORDER, this, 32)));
			break;
		case ORDER:
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.CONFIRMATION, this, 32)));
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.INVOICE, this, 32)));
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.DELIVERY, this, 32)));
			break;
		case CONFIRMATION:
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.INVOICE, this, 32)));
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.DELIVERY, this, 32)));
			break;
		case INVOICE:
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.DELIVERY, this, 32)));
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.CREDIT, this, 32)));
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.DUNNING, this, 32)));
			break;
		case DELIVERY:
			tbmDuplicate.add(new NewDocumentActionContributionItem(new NewDocumentAction(DocumentType.INVOICE, this, 32)));
			break;
		case DUNNING:
			NewDocumentAction action = new NewDocumentAction(DocumentType.DUNNING, this, 32);
			action.setText(Integer.toString(dunningLevel + 1) + "." + action.getText());
			tbmDuplicate.add(new NewDocumentActionContributionItem(action));
			break;
		default:
			copyGroup.setVisible(false);
		}

		// Resize the toolbar
		tbmDuplicate.update(true);

		
		
		
		
		// Composite that contains the address label and the address icon
		Composite addressComposite = new Composite(top, SWT.NONE | SWT.RIGHT);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(addressComposite);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addressComposite);

		// Address label
		Label labelAddress = new Label(addressComposite, SWT.NONE | SWT.RIGHT);
		labelAddress.setText("Adresse");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(labelAddress);

		// Address icon
		Label selectAddressButton = new Label(addressComposite, SWT.NONE | SWT.RIGHT);
		try {
			selectAddressButton.setImage((Activator.getImageDescriptor("/icons/16/contact_16.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(selectAddressButton);
		selectAddressButton.addMouseListener(new MouseAdapter() {
			
			// Open the address dialog, if the icon is clicked.
			public void mouseDown(MouseEvent e) {
				SelectContactDialog dialog = new SelectContactDialog("Adresse auswählen");
				DataSetContact contact;
				if (dialog.open() == Dialog.OK) {
					contact = (DataSetContact) dialog.getSelection();
					if (contact != null) {
						
						// Use delivery address, if it's a delivery note
						if (documentType == DocumentType.DELIVERY)
							txtAddress.setText(contact.getAddress(true));
						else
							txtAddress.setText(contact.getAddress(false));
						addressId = contact.getIntValueByKey("id");
						
						// Use the customers discount
						if (itemsDiscount != null)
							itemsDiscount.setText(DataUtils.DoubleToFormatedPercent(contact.getDoubleValueByKey("discount")));
					}
				}
			}
		});

		// The address field
		txtAddress = new Text(top, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		if (documentType == DocumentType.DELIVERY)
			txtAddress.setText(document.getStringValueByKey("deliveryaddress"));
		else
			txtAddress.setText(document.getStringValueByKey("address"));
		superviceControl(txtAddress, 250);
		GridDataFactory.fillDefaults().minSize(180, 80).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(txtAddress);
		
		
		


		// Add the item table, if the document is one with items.
		if (documentType.hasItems()) {

			// Container for the label and the add and delete button.
			Composite addButtonComposite = new Composite(top, SWT.NONE | SWT.RIGHT);
			GridLayoutFactory.fillDefaults().numColumns(1).applyTo(addButtonComposite);
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addButtonComposite);

			// Items label
			Label labelItems = new Label(addButtonComposite, SWT.NONE | SWT.RIGHT);
			labelItems.setText("Artikel");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(labelItems);

			// Item add button
			Label addFromListButton = new Label(addButtonComposite, SWT.NONE);
			try {
				addFromListButton.setImage((Activator.getImageDescriptor("/icons/20/plus_list_20.png").createImage()));
			} catch (Exception e) {
				Logger.logError(e, "Icon not found");
			}
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addFromListButton);
			addFromListButton.addMouseListener(new MouseAdapter() {
				
				// Open the product dialog and add the 
				// selected product as new item.
				public void mouseDown(MouseEvent e) {
					SelectProductDialog dialog = new SelectProductDialog("Artikel auswählen");
					DataSetProduct product;
					if (dialog.open() == Dialog.OK) {
						product = (DataSetProduct) dialog.getSelection();
						if (product != null) {
							DataSetItem newItem = new DataSetItem(documentType.sign() * 1.0, product);
							addNewItem(newItem);
							tableViewerItems.refresh();
							calculate();
							checkDirty();
						}
					}
				}
			});

			// Item add button
			Label addButton = new Label(addButtonComposite, SWT.NONE);
			try {
				addButton.setImage((Activator.getImageDescriptor("/icons/16/plus_16.png").createImage()));
			} catch (Exception e) {
				Logger.logError(e, "Icon not found");
			}
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addButton);
			addButton.addMouseListener(new MouseAdapter() {
				
				// Add a new item with default properties 
				public void mouseDown(MouseEvent e) {
					DataSetItem newItem = new DataSetItem("Name", "Art.Nr.","", documentType.sign() * 1.0, "", 0.0, 0);

					// Use the standard VAT value
					newItem.setVat(Integer.parseInt(Data.INSTANCE.getProperty("standardvat")));
					addNewItem(newItem);
					
					tableViewerItems.refresh();
					calculate();
					checkDirty();
				}
			});
			
			// Item delete button
			Label deleteButton = new Label(addButtonComposite, SWT.NONE);
			try {
				deleteButton.setImage((Activator.getImageDescriptor("/icons/16/delete_16.png").createImage()));
			} catch (Exception e) {
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
						calculate();
						checkDirty();
					}
				}
			});

			// Composite that contains the table
			Composite tableComposite = new Composite(top, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(tableComposite);
			TableColumnLayout tableColumnLayout = new TableColumnLayout();
			tableComposite.setLayout(tableColumnLayout);

			// The table viewer
			tableViewerItems = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
			tableViewerItems.getTable().setLinesVisible(true);
			tableViewerItems.getTable().setHeaderVisible(true);
			tableViewerItems.setContentProvider(new ViewDataSetTableContentProvider(tableViewerItems));

			// Create the table columns
			new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.CENTER, "Menge", 60, 0, true, "quantity", new ItemEditingSupport(this, 
					tableViewerItems, 1));
			if (Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_ITEMNR"))
				new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.LEFT, "ArtNr.", 80, 0, true, "itemnr", new ItemEditingSupport(this,
					tableViewerItems, 2));
			new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.LEFT, "Name", 100, 0, true, "name", new ItemEditingSupport(this,
					tableViewerItems, 3));
			new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.LEFT, "Beschreibung", 100, 30, false, "description", new ItemEditingSupport(
					this, tableViewerItems, 4));
			if (documentType.hasPrice()) {
				new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, "MwSt.", 50, 0, true, "$ItemVatPercent", new ItemEditingSupport(this,
						tableViewerItems, 5));
				if (useGross)
					new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, "E.Preis", 85, 0, true, "$ItemGrossPrice",
							new ItemEditingSupport(this, tableViewerItems, 6));
				else
					new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, "E.Preis", 85, 0, true, "price", new ItemEditingSupport(
							this, tableViewerItems, 6));
				new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, "Rabatt", 60, 0, true, "discount", new ItemEditingSupport(this,
						tableViewerItems, 7));
				if (useGross)
					new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, "Preis", 85, 0, true, "$ItemGrossTotal",
							new ItemEditingSupport(this, tableViewerItems, 8));
				else
					new UniDataSetTableColumn(tableColumnLayout, tableViewerItems, SWT.RIGHT, "Preis", 85, 0, true, "$ItemNetTotal",
							new ItemEditingSupport(this, tableViewerItems, 8));
			}
			// Fill the table with the items
			tableViewerItems.setInput(items);
		}

		
		// Container for the message label and the add button
		Composite addMessageButtonComposite = new Composite(top, SWT.NONE | SWT.RIGHT);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(addMessageButtonComposite);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addMessageButtonComposite);

		// The message label
		Label messageLabel = new Label(addMessageButtonComposite, SWT.NONE);
		if (documentType.hasItems())
			messageLabel.setText("Bemerkung");
		else
			messageLabel.setText("Text");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(messageLabel);
		
		// The add message button
		Label addMessageButton = new Label(addMessageButtonComposite, SWT.NONE);
		try {
			addMessageButton.setImage((Activator.getImageDescriptor("/icons/20/plus_list_20.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(addMessageButton);
		addMessageButton.addMouseListener(new MouseAdapter() {
			
			// Open the text dialog and select a text
			public void mouseDown(MouseEvent e) {
				SelectTextDialog dialog = new SelectTextDialog("Text auswählen");
				DataSetText text;
				if (dialog.open() == Dialog.OK) {
					text = (DataSetText) dialog.getSelection();
					
					// Insert the selected text in the message text
					if ((text != null) && (txtMessage != null)) {
						int begin = txtMessage.getSelection().x;
						int end = txtMessage.getSelection().y;
						String s = txtMessage.getText();
						String s1 = s.substring(0, begin);
						String s2 = text.getStringValueByKey("text");
						String s3 = s.substring(end, s.length());

						txtMessage.setText(s1 + s2 + s3);

						txtMessage.setSelection(s1.length() + s2.length());
						checkDirty();
					}
				}

			}
		});

		// Add a multi line text field for the message.
		txtMessage = new Text(top, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		txtMessage.setText(document.getStringValueByKey("message"));
		superviceControl(txtMessage, 10000);

		// Set the tab order
		if (documentType.hasInvoiceReference())
			setTabOrder(txtAddress,txtInvoiceRef);
		else if (documentType.hasPrice())
			setTabOrder(txtAddress,comboNoVat);
		else if (documentType.hasItems())
			setTabOrder(txtAddress,tableViewerItems.getTable());
		else 
			setTabOrder(txtAddress,txtMessage);

		
		// Depending on if the document has price values.
		if (!documentType.hasPrice()) {
			
			// If not, fill the columns for the price with the message field.
			if (documentType.hasItems())
				GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 65).span(3, 1).grab(true, false).applyTo(txtMessage);
			else
				GridDataFactory.fillDefaults().span(3, 1).grab(true, true).applyTo(txtMessage);
		}

		else {

			if (documentType.hasPayed())
				GridDataFactory.fillDefaults().span(2, 1).hint(SWT.DEFAULT, 70).grab(true, false).applyTo(txtMessage);
			else
				GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(txtMessage);

			// Create a column for the documents subtotal, shipping and total 
			Composite totalComposite = new Composite(top, SWT.NONE);
			GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(totalComposite);
			GridDataFactory.fillDefaults().align(SWT.END, SWT.TOP).grab(true, false).span(1, 2).applyTo(totalComposite);

			// Label sub total
			Label netLabel = new Label(totalComposite, SWT.NONE);
			if (useGross)
				netLabel.setText("Summe brutto:");
			else
				netLabel.setText("Summe netto:");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(netLabel);

			// Sub total
			itemsSum = new Label(totalComposite, SWT.NONE | SWT.RIGHT);
			itemsSum.setText("---");
			GridDataFactory.swtDefaults().hint(70, SWT.DEFAULT).align(SWT.END, SWT.TOP).applyTo(itemsSum);

			// Label discount
			Label discountLabel = new Label(totalComposite, SWT.NONE);
			discountLabel.setText("Rabatt:");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(discountLabel);

			// Discount field
			itemsDiscount = new Text(totalComposite, SWT.NONE | SWT.RIGHT);
			itemsDiscount.setText(document.getFormatedStringValueByKey("itemsdiscount"));
			GridDataFactory.swtDefaults().hint(70, SWT.DEFAULT).align(SWT.END, SWT.TOP).applyTo(itemsDiscount);

			// Set the tab order
			setTabOrder(txtMessage,itemsDiscount);
			
			// Recalculate, if the discount field looses the focus.
			itemsDiscount.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					calculate();
					checkDirty();
					itemsDiscount.setText(DataUtils.DoubleToFormatedPercent(DataUtils.StringToDoubleDiscount(itemsDiscount.getText())));
					
				}
			});
			
			// Recalculate, if the discount is modified.
			itemsDiscount.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 13) {
						itemsDiscount.setText(DataUtils.DoubleToFormatedPercent(DataUtils.StringToDoubleDiscount(itemsDiscount.getText())));
						calculate();
						checkDirty();
					}
				}
			});

			// Shipping composite contains label and combo.
			Composite shippingComposite = new Composite(totalComposite, SWT.NONE);
			GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(shippingComposite);
			GridDataFactory.fillDefaults().align(SWT.END, SWT.TOP).grab(true, false).applyTo(shippingComposite);

			// Shipping label
			Label shippingLabel = new Label(shippingComposite, SWT.NONE);
			shippingLabel.setText("Versand:");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(shippingLabel);

			// Shipping combo
			comboShipping = new Combo(shippingComposite, SWT.BORDER);
			comboViewerShipping = new ComboViewer(comboShipping);
			comboViewerShipping.setContentProvider(new UniDataSetContentProvider());
			comboViewerShipping.setLabelProvider(new UniDataSetLabelProvider("description"));
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(comboShipping);
			comboViewerShipping.addSelectionChangedListener(new ISelectionChangedListener() {

				// If a new shipping is selected, recalculate the total sum,
				// and update the shipping VAT.
				public void selectionChanged(SelectionChangedEvent event) {
					// Get the selected element.
					ISelection selection = event.getSelection();
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					if (!structuredSelection.isEmpty()) {
						// Get first selected element.
						Object firstElement = structuredSelection.getFirstElement();
						DataSetShipping dataSetShipping = (DataSetShipping) firstElement;
						shipping = dataSetShipping.getDoubleValueByKey("value");
						shippingId = dataSetShipping.getIntValueByKey("id");
						
						// Update the shipping VAT
						int shippungVatId = dataSetShipping.getIntValueByKey("vatid");
						shippingVatDescription = Data.INSTANCE.getVATs().getDatasetById(shippungVatId).getStringValueByKey("description");
						shippingVat = Data.INSTANCE.getVATs().getDatasetById(shippungVatId).getDoubleValueByKey("value");
						shippingAutoVat = Data.INSTANCE.getShippings().getDatasetById(shippingId).getIntValueByKey("autovat");
						calculate();
						checkDirty();
					}
				}
			});
			
			// Fill the shipping combo with the shipping values.
			comboViewerShipping.setInput(Data.INSTANCE.getShippings().getDatasets());

			// Get the documents'shipping values.
			shipping = document.getDoubleValueByKey("shipping");
			shippingVat = document.getDoubleValueByKey("shippingvat");
			shippingAutoVat = document.getIntValueByKey("shippingautovat");
			shippingVatDescription = document.getStringValueByKey("shippingvatdescription");
			
			// Set the combo
			comboShipping.setText(document.getStringValueByKey("shippingdescription"));
			superviceControl(comboShipping);

			// Shipping value field
			shippingValue = new Text(totalComposite, SWT.NONE | SWT.RIGHT);
			shippingValue.setText(DataUtils.DoubleToFormatedPrice(shipping));
			GridDataFactory.swtDefaults().hint(70, SWT.DEFAULT).align(SWT.END, SWT.CENTER).applyTo(shippingValue);

			// Recalculate, if the discount field looses the focus.
			shippingValue.addFocusListener(new FocusAdapter() {

				public void focusLost(FocusEvent e) {
					changeShippingValue();
					checkDirty();
				}
			});
			
			// Recalculate, if the shipping is modified
			shippingValue.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 13) {
						changeShippingValue();
						checkDirty();
					}
				}
			});

			superviceControl(shippingValue, 12);

			// VAT label
			Label vatLabel = new Label(totalComposite, SWT.NONE);
			vatLabel.setText("MwSt:");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(vatLabel);

			// VAT value
			vatValue = new Label(totalComposite, SWT.NONE | SWT.RIGHT);
			vatValue.setText("---");
			GridDataFactory.swtDefaults().hint(70, SWT.DEFAULT).align(SWT.END, SWT.TOP).applyTo(vatValue);

			// Total label
			Label totalLabel = new Label(totalComposite, SWT.NONE);
			totalLabel.setText("Gesamtsumme:");
			GridDataFactory.swtDefaults().align(SWT.END, SWT.TOP).applyTo(totalLabel);
			
			// Total value
			totalValue = new Label(totalComposite, SWT.NONE | SWT.RIGHT);
			totalValue.setText("---");
			GridDataFactory.swtDefaults().hint(70, SWT.DEFAULT).align(SWT.END, SWT.TOP).applyTo(totalValue);

			// Create the "payed"-controls, only if the document type allows this.
			if (documentType.hasPayed()) {

				// The payed label
				bPayed = new Button(top, SWT.CHECK | SWT.LEFT);
				bPayed.setSelection(document.getBooleanValueByKey("payed"));
				bPayed.setText("bezahlt");
				GridDataFactory.swtDefaults().applyTo(bPayed);

				// Container for the payment and the payed state
				payedContainer = new Composite(top, SWT.NONE);
				GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(payedContainer);
				GridDataFactory.swtDefaults().span(2, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(payedContainer);

				// If the payed check box is selected ...
				bPayed.addSelectionListener(new SelectionAdapter() {

					// ... Recreate the payed composite
					public void widgetSelected(SelectionEvent e) {
						createPayedComposite(bPayed.getSelection());
						checkDirty();
					}
				});

				// Combo to select the payment
				comboPayment = new Combo(payedContainer, SWT.BORDER);
				comboViewerPayment = new ComboViewer(comboPayment);
				comboViewerPayment.setContentProvider(new UniDataSetContentProvider());
				comboViewerPayment.setLabelProvider(new UniDataSetLabelProvider("description"));
				GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(comboPayment);
				
				// If a new payment is selected ...
				comboViewerPayment.addSelectionChangedListener(new ISelectionChangedListener() {

					// change the paymentId to the selected element
					public void selectionChanged(SelectionChangedEvent event) {

						// Get the selected elemen
						ISelection selection = event.getSelection();
						IStructuredSelection structuredSelection = (IStructuredSelection) selection;
						if (!structuredSelection.isEmpty()) {
							// Get first selected element.
							Object firstElement = structuredSelection.getFirstElement();
							DataSetPayment dataSetPayment = (DataSetPayment) firstElement;
							paymentId = dataSetPayment.getIntValueByKey("id");
							checkDirty();
						}
					}
				});

				// Fill the payment combo with the payments
				comboViewerPayment.setInput(Data.INSTANCE.getPayments().getDatasets());
				superviceControl(comboPayment);

				// Create a default payed composite with the document's
				// state for "payed"
				createPayedComposite(document.getBooleanValueByKey("payed"));

				// Set the combo
				comboPayment.setText(document.getStringValueByKey("paymentdescription"));
				
			}
		}
		
		// Calculate the total sum
		calculate();

	}

	/**
	 * Asks this part to take focus within the workbench.
	 * 
	 * @see com.sebulli.fakturama.editors.Editor#setFocus()
	 */
	@Override
	public void setFocus() {
		super.setFocus();
	}

	
	/**
	 * Test, if there is a document with the same number
	 * 
	 * @return TRUE, if one with the same number is found
	 */
	public boolean thereIsOneWithSameNumber () {
		// Letters do not have to be checked
		if (documentType == DocumentType.LETTER)
			return false;

		// Cancel, if there is already a document with the same ID
		if (Data.INSTANCE.getDocuments().isExistingDataSet(document, "name", txtName.getText())) {
			// Display an error message
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setText("Fehler in Dokumentennummer");
			messageBox.setMessage("Es existiert bereits ein Dokument mit dieser Nummer: " + txtName.getText());
			messageBox.open();
			
			return true;
		}

		return false;
	}

	/**
	 * Returns, if save is allowed
	 * 
	 * @return TRUE, if save is allowed
	 * 
	 * @see com.sebulli.fakturama.editors.Editor#saveAllowed()
	 */
	@Override
	protected boolean saveAllowed() {
		// Save is allowed, if there is no document with the same number
		return !thereIsOneWithSameNumber();
	}

}
