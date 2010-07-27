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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.Workspace;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetProduct;
import com.sebulli.fakturama.data.UniData;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.data.UniDataType;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.ViewProductTable;

/**
 * The product editor
 * 
 * @author Gerd Bartelt
 */
public class ProductEditor extends Editor {

	// Editor's ID
	public static final String ID = "com.sebulli.fakturama.editors.productEditor";
	
	// This UniDataSet represents the editor's input 
	private DataSetProduct product;

	// SWT widgets of the editor
	private Text textItemNr;
	private Text textName;
	private Text textDescription;
	private Combo comboVat;
	private Text textWeight;
	private ComboViewer comboViewer;
	private Text txtCategory;
	private Label labelProductPicture;
	private Composite photoComposite;
	private Text textProductPicturePath;

	// Widgets (and variables) for the scaled price.
	private Label[] labelBlock = new Label[5];
	private Text[] textBlock = new Text[5];
	private NetText[] netText = new NetText[5];
	private GrossText[] grossText = new GrossText[5];
	private UniData[] net = new UniData[5];
	private int scaledPrices;

	// These flags are set by the preference settings.
	// They define, if elements of the editor are displayed, or not.
	private boolean useWeight;
	private boolean useItemNr;
	private boolean useNet;
	private boolean useGross;
	private boolean useVat;
	private boolean useDescription;
	private boolean usePicture;
	
	// These are (non visible) values of the document
	private Double vat = 0.0;
	private int vatId = 0;
	private String filename1 = "";
	private String filename2 = "";
	private String picturePath = "";
	private Display display;
	private String pictureName = "";
	
	// defines, if the product is new created
	private boolean newProduct;

	/**
	 * Constructor
	 * 
	 * Associate the table view with the editor
	 */
	public ProductEditor() {
		tableViewID = ViewProductTable.ID;
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
		 * - options (not yet implemented)
		 * - date_added (not modified by editor)
		 */

		// Always set the editor's data set to "undeleted"
		product.setBooleanValueByKey("deleted", false);

		// Set the product data
		product.setStringValueByKey("itemnr", textItemNr.getText());
		product.setStringValueByKey("name", textName.getText());
		product.setStringValueByKey("category", txtCategory.getText());
		product.setStringValueByKey("description", textDescription.getText());

		// Set all of the scaled prices
		for (int i = 0; i < scaledPrices; i++) {
			String indexNr = Integer.toString(i + 1);
			product.setDoubleValueByKey("price" + indexNr, net[i].getValueAsDouble());
			product.setStringValueByKey("block" + indexNr, textBlock[i].getText());
		}

		// Set the product data
		product.setIntValueByKey("vatid", vatId);
		product.setStringValueByKey("weight", textWeight.getText());
		product.setStringValueByKey("picturename", pictureName);

		// If it is a new product, add it to the product list and
		// to the data base
		if (newProduct) {
			product = Data.INSTANCE.getProducts().addNewDataSet(product);
			newProduct = false;
		}
		// If it's not new, update at least the data base
		else {
			Data.INSTANCE.getProducts().updateDataSet(product);
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
	 * If an existing data set is opened, the local variable "product" is set to
	 * This data set.
	 * If the editor is opened to create a new one, a new data set is created and
	 * the local variable "product" is set to this one.
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
		product = (DataSetProduct) ((UniDataSetEditorInput) input).getUniDataSet();

		// Test, if the editor is opened to create a new data set. This is,
		// if there is no input set.
		newProduct = (product == null);

		// If new ..
		if (newProduct) {
			
			// Create a new data set
			product = new DataSetProduct(((UniDataSetEditorInput) input).getCategory());
			setPartName("neues Produkt");

			// Set the vat to the standard value
			product.setIntValueByKey("vatid", Data.INSTANCE.getPropertyAsInt("standardvat"));

		} else {

			// Set the Editor's name to the product name.
			setPartName(product.getStringValueByKey("name"));
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
		 * - options (not yet implemented)
		 * - date_added (not modified by editor)
		 */

		if (product.getBooleanValueByKey("deleted")) { return true; }

		if (!product.getStringValueByKey("itemnr").equals(textItemNr.getText())) { return true; }
		if (!product.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!product.getStringValueByKey("description").equals(textDescription.getText())) { return true; }

		// Test all scaled prices
		for (int i = 0; i < scaledPrices; i++) {
			String indexNr = Integer.toString(i + 1);
			if (product.getDoubleValueByKey("price" + indexNr) != net[i].getValueAsDouble()) { return true; }
			if (!product.getStringValueByKey("block" + indexNr).equals(textBlock[i].getText())) { return true; }
		}

		if (product.getIntValueByKey("vatid") != vatId) { return true; }
		if (!product.getStringValueByKey("weight").equals(textWeight.getText())) { return true; }
		if (!product.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }
		if (!product.getStringValueByKey("picturename").equals(pictureName)) { return true; }

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
	 * Set the variable picturePath to the path of the product picture,
	 * which is a combination of the selected workspace, the /pics/products/
	 * folder and the product name.
	 * 
	 * Also update the text widget textProductPicturePath which is displayed
	 * under the product picture.
	 */
	private void createPicturePathFromPictureName() {
		
		// Get the workspace
		filename1 = Workspace.INSTANCE.getWorkspace();
		
		// add the picture subfolder
		filename2 = Workspace.productPictureFolderName;
		
		// Set the variables
		picturePath = filename1 + filename2;
		filename2 += pictureName;
		
		// Display the text under the product picture
		if (textProductPicturePath != null) {
			textProductPicturePath.setText(filename2);
		}
	}

	/**
	 * Create the picture name based on the product's item number
	 */
	private void createPictureName() {
		
		pictureName = createPictureName(textName.getText(), textItemNr.getText());
		
		// Add the full path.
		createPicturePathFromPictureName();
	}

	/**
	 * Create the picture name based on the product's item number
	 * Remove illegal characters and add an ".jpg"
	 * 
	 * @param name The name of the product
	 * @param itemNr The item number of the product
	 * @return Picture name as String
	 */
	public static String createPictureName(String name, String itemNr) {
		
		String pictureName;
		
		// Get the product's item number
		pictureName = itemNr;
		
		// If the product name is different to the item number,
		// add also the product name to the pictures name
		if (!name.equals(itemNr))
			pictureName += "_" + name;

		// Remove all illegal characters that are not allowed as file name.
		final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', ' ', '.' };
		for (char c : ILLEGAL_CHARACTERS)
			pictureName = pictureName.replace(c, '_');
		
		// Add the .*jpg
		pictureName += ".jpg";
		
		return pictureName;
	}

	
	
	
	/**
	 * Reload the product picture  
	 */
	private void setPicture() {
		
		try { 
			// Display the picture, if a product picture is set.
			if (!pictureName.isEmpty()) {
				
				// Load the image, based on the picture name
				Image image = new Image(display, filename1 + filename2);
				
				// Get the pictures size
				int width = image.getBounds().width;
				int height = image.getBounds().height;
				
				// Maximum picture width is 200px
				if (width > 200) {
					height = 200 * height / width;
					width = 200;
				}
				
				// Rescale the picture to maximum 200px width
				Image scaledImage = new Image(display, image.getImageData().scaledTo(width, height));
				labelProductPicture.setImage(scaledImage);
			} 
			// Display an empty background, if no picture is set.
			else {
				try {
					labelProductPicture.setImage((Activator.getImageDescriptor("/icons/product/nopicture.png").createImage()));
				} catch (Exception e1) {
					Logger.logError(e1, "Icon not found");
				}
			}
		} catch (Exception e) {
			
			// Show an error icon, if the picture is not found
			try {
				labelProductPicture.setImage((Activator.getImageDescriptor("/icons/product/picturenotfound.png").createImage()));
			} catch (Exception e1) {
				Logger.logError(e1, "Icon not found");
			}
		}

	}

	/**
	* Creates the SWT controls for this workbench part
	* 
	* @param the parent control
	* @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	*/
	@Override
	public void createPartControl(final Composite parent) {
		
		// Get a reference to the display
		display = parent.getDisplay();

		// Some of this editos's control elements can be hidden.
		// Get the these settings from the preference store
		useItemNr = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_ITEMNR");
		useDescription = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_DESCRIPTION");
		scaledPrices = Activator.getDefault().getPreferenceStore().getInt("PRODUCT_SCALED_PRICES");
		useWeight = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_WEIGHT");
		useNet = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 2);
		useGross = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 1);
		useVat = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_VAT");
		usePicture = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_PICTURE");

		// Get the product VAT
		vatId = product.getIntValueByKey("vatid");
		try {
			vat = Data.INSTANCE.getVATs().getDatasetById(vatId).getDoubleValueByKey("value");
		} catch (IndexOutOfBoundsException e) {
			vat = 0.0;
		}

		// Create the top Composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(top);

		// Create an invisible container for all hidden components
		Composite invisible = new Composite(top, SWT.NONE);
		invisible.setVisible(false);
		GridDataFactory.fillDefaults().hint(0, 0).span(2, 1).applyTo(invisible);

		// Group: Product description
		Group productDescGroup = new Group(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(productDescGroup);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(productDescGroup);
		productDescGroup.setText("Beschreibung");

		// Item number
		Label labelItemNr = new Label(useItemNr ? productDescGroup : invisible, SWT.NONE);
		labelItemNr.setText("Artikelnummer");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelItemNr);
		textItemNr = new Text(useItemNr ? productDescGroup : invisible, SWT.BORDER);
		textItemNr.setText(product.getStringValueByKey("itemnr"));
		superviceControl(textItemNr, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textItemNr);

		// Product name
		Label labelName = new Label(productDescGroup, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(productDescGroup, SWT.BORDER);
		textName.setText(product.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		// Product category
		Label labelCategory = new Label(productDescGroup, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(productDescGroup, SWT.BORDER);
		txtCategory.setText(product.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		// Product description
		Label labelDescription = new Label(useDescription ? productDescGroup : invisible, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(useDescription ? productDescGroup : invisible, SWT.BORDER | SWT.MULTI);
		textDescription.setText(product.getStringValueByKey("description"));
		superviceControl(textDescription, 250);
		GridDataFactory.fillDefaults().hint(10, 80).grab(true, false).applyTo(textDescription);

		// Product price
		Label labelPrice = new Label(productDescGroup, SWT.NONE);

		// Use net or gross price
		if (useNet && useGross)
			labelPrice.setText("Preis");
		else if (useNet)
			labelPrice.setText("Preis (netto)");
		else if (useGross)
			labelPrice.setText("Preis (brutto)");

		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelPrice);

		// Create a container composite for the scaled price
		Composite pricetable = new Composite(productDescGroup, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns((scaledPrices > 1) ? (useNet && useGross) ? 4 : 3 : 2).applyTo(pricetable);

		// If there is a net and gross column, and 2 columns for the quantity
		// there are 2 cells in the top left corner, that are empty
		if ((scaledPrices >= 2) && useNet && useGross) {
			new Label(pricetable, SWT.NONE);
			new Label(pricetable, SWT.NONE);
		}

		// Display the heading for the net and gross columns
		if (useNet && useGross) {
			Label labelNet = new Label(pricetable, SWT.CENTER);
			labelNet.setText("Netto");

			Label labelGross = new Label(pricetable, SWT.CENTER);
			labelGross.setText("Brutto");
		}

		// Create a row for each entry of the scaled price table
		for (int i = 0; i < 5; i++) {
			
			String indexNr = Integer.toString(i + 1);

			// Get the net price scaled price
			net[i] = new UniData(UniDataType.STRING, product.getDoubleValueByKey("price" + indexNr));

			// Create the columns for the quantity
			labelBlock[i] = new Label(((i < scaledPrices) && (scaledPrices >= 2)) ? pricetable : invisible, SWT.NONE);
			labelBlock[i].setText("ab");

			textBlock[i] = new Text(((i < scaledPrices) && (scaledPrices >= 2)) ? pricetable : invisible, SWT.BORDER | SWT.RIGHT);
			textBlock[i].setText(product.getFormatedStringValueByKey("block" + indexNr));
			superviceControl(textBlock[i], 6);
			GridDataFactory.swtDefaults().hint(40, SWT.DEFAULT).applyTo(textBlock[i]);

			// Create the net columns
			if (useNet) {
				netText[i] = new NetText(this, (i < scaledPrices) ? pricetable : invisible, SWT.BORDER | SWT.RIGHT, net[i], vat);
				GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(netText[i].getNetText());
			}

			// Create the gross columns
			if (useGross) {
				grossText[i] = new GrossText(this, (i < scaledPrices) ? pricetable : invisible, SWT.BORDER | SWT.RIGHT, net[i], vat);
				GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(grossText[i].getGrossText());
			}

			// If a net and gross column was created, link both together,
			// so, if one is modified, the other will be recalculated.
			if (useNet && useGross) {
				netText[i].setGrossText(grossText[i].getGrossText());
				grossText[i].setNetText(netText[i].getNetText());
			}
		}

		// product VAT
		Label labelVat = new Label(useVat ? productDescGroup : invisible, SWT.NONE);
		labelVat.setText("MwSt.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelVat);
		comboVat = new Combo(useVat ? productDescGroup : invisible, SWT.BORDER);
		comboViewer = new ComboViewer(comboVat);
		comboViewer.setContentProvider(new UniDataSetContentProvider());
		comboViewer.setLabelProvider(new UniDataSetLabelProvider());
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				// Handle selection changed event 
				ISelection selection = event.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (!structuredSelection.isEmpty()) {

					// Get the first element ...
					Object firstElement = structuredSelection.getFirstElement();
					
					// Get the selected VAT
					UniDataSet selectedVat = (UniDataSet) firstElement;

					// Store the old value
					Double oldVat = vat;

					// Get the new value
					vatId = selectedVat.getIntValueByKey("id");
					vat = selectedVat.getDoubleValueByKey("value");
					
					// Recalculate all the price values
					for (int i = 0; i < scaledPrices; i++) {
						
						// Recalculate the price values if gross is selected,
						// So the gross value will stay constant.
						if (!useNet) {
							net[i].setValue(net[i].getValueAsDouble() * ((1 + oldVat) / (1 + vat)));
						}

						// Update net and gross text widget
						if (netText[i] != null)
							netText[i].setVatValue(vat);
						if (grossText[i] != null)
							grossText[i].setVatValue(vat);
					}
				}
				
				// Check, if the document has changed.
				checkDirty();
			}
		});

		// Create a JFace combo viewer for the VAT list
		comboViewer.setInput(Data.INSTANCE.getVATs().getDatasets());
		try {
			comboViewer.setSelection(new StructuredSelection(Data.INSTANCE.getVATs().getDatasetById(vatId)), true);
		} catch (IndexOutOfBoundsException e) {
			comboVat.setText("invalid");
			vatId = -1;
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboVat);

		// Product weight
		Label labelWeight = new Label(useWeight ? productDescGroup : invisible, SWT.NONE);
		labelWeight.setText("Gewicht (kg)");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelWeight);
		textWeight = new Text(useWeight ? productDescGroup : invisible, SWT.BORDER);
		textWeight.setText(product.getStringValueByKey("weight"));
		superviceControl(textWeight, 16);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textWeight);

		// Group: Product picture
		Group productPictureGroup = new Group(usePicture ? top : invisible, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(productPictureGroup);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(productPictureGroup);
		productPictureGroup.setText("Produktbild");

		// The photo
		photoComposite = new Composite(productPictureGroup, SWT.BORDER);
		GridLayoutFactory.swtDefaults().margins(10, 10).numColumns(1).applyTo(photoComposite);
		GridDataFactory.fillDefaults().indent(0, 10).align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(photoComposite);
		photoComposite.setBackground(new Color(null, 255, 255, 255));
		
		// The picture name label
		labelProductPicture = new Label(photoComposite, SWT.NONE);
		pictureName = product.getStringValueByKey("picturename");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(labelProductPicture);

		// The picture pathe
		textProductPicturePath = new Text(photoComposite, SWT.NONE);
		textProductPicturePath.setEditable(false);
		textProductPicturePath.setBackground(new Color(null, 255, 255, 255));
		superviceControl(textProductPicturePath, 250);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(textProductPicturePath);
		
		// Load the picture from the picture path
		createPicturePathFromPictureName();
		setPicture();

		// Add a button to select a new picture
		Button selectPictureButton = new Button(productPictureGroup, SWT.PUSH);
		selectPictureButton.setText("Bild auswählen");
		selectPictureButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// Open a file dialog to select the picture
				FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				fileDialog.setFilterPath(Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE"));
				fileDialog.setText("Produktbild auswählen");
				String selectedFile = fileDialog.open();

				if (selectedFile != null) {
					
					// Create a new subfolder, if it does not exists yet.
					createPictureName();
					File directory = new File(picturePath);
					if (!directory.exists())
						directory.mkdirs();

					// Copy the picture into the picture folder
					File inputFile = new File(selectedFile);
					File outputFile = new File(filename1 + filename2);

					try {
						
						// Copy it
			            FileOutputStream out = new FileOutputStream(outputFile);
			            FileInputStream ins = new FileInputStream(inputFile);
			            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						int c;
						
						// Read the file to an input bufer
						while ((c = ins.read()) != -1) {
		                    byteArrayOutputStream.write((byte)c);
						}
						
						// Write it to an file
						out.write(byteArrayOutputStream.toByteArray());

						// Close the streams
						byteArrayOutputStream.close();
						ins.close();
						out.close();
						
					} catch (IOException e1) {
						Logger.logError(e1, "Error copying picture from " + selectedFile + " to " + filename1 + filename2);
					}
					
					// Display the new picture
					setPicture();
					checkDirty();
				}

			}
		});
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(selectPictureButton);

	}
	/**
	 * Asks this part to take focus within the workbench.
	 * 
	 * @see com.sebulli.fakturama.editors.Editor#setFocus()
	 */
	@Override
	public void setFocus() {
	}

}