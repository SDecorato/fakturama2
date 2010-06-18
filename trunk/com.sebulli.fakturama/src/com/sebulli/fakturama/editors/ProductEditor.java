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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetProduct;
import com.sebulli.fakturama.data.UniData;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.data.UniDataType;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.ViewProductTable;

public class ProductEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.productEditor";
	private DataSetProduct product;
	private Text textItemNr;
	private Text textName;
	private Text textDescription;
	private Combo comboVat;
	private Text textWeight;
	private Label[] labelBlock = new Label[5];
	private Text[] textBlock = new Text[5];
	private UniData[] net = new UniData[5];
	private NetText[] netText = new NetText[5];
	private GrossText[] grossText = new GrossText[5];
	private int scaledPrices;
	private boolean useWeight;
	private boolean useItemNr;
	private boolean useNet;
	private boolean useGross;
	private boolean useVat;
	private boolean useDescription;
	private boolean usePicture;
	private Double vat = 0.0;
	private int vatId = 0;
	private ComboViewer comboViewer;
	private Text txtCategory;
	private Label labelProductPicture;
	private String filename1 = "";
	private String filename2 = "";
	private String picturePath = "";
	private Display display;
	private String pictureName = "";
	private boolean newProduct;
	private Composite photoComposite;
	private Text textProductPicturePath;

	public ProductEditor() {
		tableViewID = ViewProductTable.ID;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		/*
		 * the following parameters are not saved: - id (constant) options (not
		 * yet implemented) date_added (not modified by editor)
		 */

		product.setBooleanValueByKey("deleted", false);
		product.setStringValueByKey("itemnr", textItemNr.getText());
		product.setStringValueByKey("name", textName.getText());
		product.setStringValueByKey("category", txtCategory.getText());
		product.setStringValueByKey("description", textDescription.getText());

		for (int i = 0; i < scaledPrices; i++) {
			String indexNr = Integer.toString(i + 1);
			product.setDoubleValueByKey("price" + indexNr, net[i].getValueAsDouble());
			product.setStringValueByKey("block" + indexNr, textBlock[i].getText());
		}

		product.setIntValueByKey("vatid", vatId);
		product.setStringValueByKey("weight", textWeight.getText());
		product.setStringValueByKey("picturename", pictureName);

		if (newProduct) {
			product = Data.INSTANCE.getProducts().addNewDataSet(product);
			newProduct = false;
		} else {
			Data.INSTANCE.getProducts().updateDataSet(product);
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
		product = (DataSetProduct) ((UniDataSetEditorInput) input).getUniDataSet();
		newProduct = (product == null);

		if (newProduct) {
			product = new DataSetProduct(((UniDataSetEditorInput) input).getCategory());
			setPartName("neues Produkt");
			product.setIntValueByKey("vatid", Data.INSTANCE.getPropertyAsInt("standardvat"));

		} else {
			setPartName(product.getStringValueByKey("name"));
		}
	}

	@Override
	public boolean isDirty() {
		/*
		 * the following parameters are not checked: - id (constant) options
		 * (not yet implemented) date_added (not modified by editor)
		 */

		if (product.getBooleanValueByKey("deleted")) { return true; }

		if (!product.getStringValueByKey("itemnr").equals(textItemNr.getText())) { return true; }

		if (!product.getStringValueByKey("name").equals(textName.getText())) { return true; }
		if (!product.getStringValueByKey("description").equals(textDescription.getText())) { return true; }

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

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void createPicturePathFromPictureName() {
		filename1 = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");
		filename2 = "/pics/products/";
		picturePath = filename1 + filename2;
		filename2 += pictureName;
		if (textProductPicturePath != null) {
			textProductPicturePath.setText(filename2);
		}
	}

	private void createPictureName() {
		pictureName = textItemNr.getText();
		if (!textName.getText().equals(textItemNr.getText()))
			pictureName += "_" + textName.getText();

		final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', ' ', '.' };
		for (char c : ILLEGAL_CHARACTERS)
			pictureName = pictureName.replace(c, '_');
		pictureName += ".jpg";
		createPicturePathFromPictureName();
	}

	private void setPicture() {
		try {
			if (!pictureName.isEmpty()) {
				Image image = new Image(display, filename1 + filename2);
				int width = image.getBounds().width;
				int height = image.getBounds().height;
				if (width > 200) {
					height = 200 * height / width;
					width = 200;
				}
				Image scaledImage = new Image(display, image.getImageData().scaledTo(width, height));
				labelProductPicture.setImage(scaledImage);
			} else {
				try {
					labelProductPicture.setImage((Activator.getImageDescriptor("/icons/product/nopicture.png").createImage()));
				} catch (Exception e1) {
					Logger.logError(e1, "Icon not found");
				}
			}
		} catch (Exception e) {
			try {
				labelProductPicture.setImage((Activator.getImageDescriptor("/icons/product/picturenotfound.png").createImage()));
			} catch (Exception e1) {
				Logger.logError(e1, "Icon not found");
			}
		}

	}

	@Override
	public void createPartControl(final Composite parent) {
		display = parent.getDisplay();
		useItemNr = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_ITEMNR");
		useDescription = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_DESCRIPTION");
		scaledPrices = Activator.getDefault().getPreferenceStore().getInt("PRODUCT_SCALED_PRICES");
		useWeight = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_WEIGHT");
		useNet = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 2);
		useGross = (Activator.getDefault().getPreferenceStore().getInt("PRODUCT_USE_NET_GROSS") != 1);
		useVat = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_VAT");
		usePicture = Activator.getDefault().getPreferenceStore().getBoolean("PRODUCT_USE_PICTURE");

		vatId = product.getIntValueByKey("vatid");
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

		Group productDescGroup = new Group(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(productDescGroup);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(productDescGroup);
		productDescGroup.setText("Beschreibung");

		Label labelItemNr = new Label(useItemNr ? productDescGroup : invisible, SWT.NONE);
		labelItemNr.setText("Artikelnummer");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelItemNr);
		textItemNr = new Text(useItemNr ? productDescGroup : invisible, SWT.BORDER);
		textItemNr.setText(product.getStringValueByKey("itemnr"));
		superviceControl(textItemNr, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textItemNr);

		Label labelName = new Label(productDescGroup, SWT.NONE);
		labelName.setText("Name");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		textName = new Text(productDescGroup, SWT.BORDER);
		textName.setText(product.getStringValueByKey("name"));
		superviceControl(textName, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textName);

		Label labelCategory = new Label(productDescGroup, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(productDescGroup, SWT.BORDER);
		txtCategory.setText(product.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		Label labelDescription = new Label(useDescription ? productDescGroup : invisible, SWT.NONE);
		labelDescription.setText("Beschreibung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDescription);
		textDescription = new Text(useDescription ? productDescGroup : invisible, SWT.BORDER | SWT.MULTI);
		textDescription.setText(product.getStringValueByKey("description"));
		superviceControl(textDescription, 250);
		GridDataFactory.fillDefaults().hint(10, 80).grab(true, false).applyTo(textDescription);

		Label labelPrice = new Label(productDescGroup, SWT.NONE);

		if (useNet && useGross)
			labelPrice.setText("Preis");
		else if (useNet)
			labelPrice.setText("Preis (netto)");
		else if (useGross)
			labelPrice.setText("Preis (brutto)");

		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelPrice);

		Composite pricetable = new Composite(productDescGroup, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns((scaledPrices > 1) ? (useNet && useGross) ? 4 : 3 : 2).applyTo(pricetable);

		if ((scaledPrices > 1) && useNet && useGross) {
			new Label(pricetable, SWT.NONE);
			new Label(pricetable, SWT.NONE);
		}

		if (useNet && useGross) {
			Label labelNet = new Label(pricetable, SWT.CENTER);
			labelNet.setText("Netto");

			Label labelGross = new Label(pricetable, SWT.CENTER);
			labelGross.setText("Brutto");
		}

		for (int i = 0; i < 5; i++) {
			String indexNr = Integer.toString(i + 1);

			net[i] = new UniData(UniDataType.STRING, product.getDoubleValueByKey("price" + indexNr));

			labelBlock[i] = new Label(((i < scaledPrices) && (scaledPrices > 2)) ? pricetable : invisible, SWT.NONE);
			labelBlock[i].setText("ab");

			textBlock[i] = new Text(((i < scaledPrices) && (scaledPrices > 2)) ? pricetable : invisible, SWT.BORDER | SWT.RIGHT);
			textBlock[i].setText(product.getFormatedStringValueByKey("block" + indexNr));
			superviceControl(textBlock[i], 6);
			GridDataFactory.swtDefaults().hint(40, SWT.DEFAULT).applyTo(textBlock[i]);

			if (useNet) {
				netText[i] = new NetText(this, (i < scaledPrices) ? pricetable : invisible, SWT.BORDER | SWT.RIGHT, net[i], vat);
				GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(netText[i].getNetText());
			}

			if (useGross) {
				grossText[i] = new GrossText(this, (i < scaledPrices) ? pricetable : invisible, SWT.BORDER | SWT.RIGHT, net[i], vat);
				GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(grossText[i].getGrossText());
			}

			if (useNet && useGross) {
				netText[i].setGrossText(grossText[i].getGrossText());
				grossText[i].setNetText(netText[i].getNetText());
			}
		}

		Label labelVat = new Label(useVat ? productDescGroup : invisible, SWT.NONE);
		labelVat.setText("MwSt.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelVat);

		comboVat = new Combo(useVat ? productDescGroup : invisible, SWT.BORDER);
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
					UniDataSet selectedVat = (UniDataSet) firstElement;
					Double oldVat = vat;

					vatId = selectedVat.getIntValueByKey("id");
					vat = selectedVat.getDoubleValueByKey("value");
					for (int i = 0; i < scaledPrices; i++) {
						if (!useNet) {
							net[i].setValue(net[i].getValueAsDouble() * ((1 + oldVat) / (1 + vat)));
						}

						if (netText[i] != null)
							netText[i].setVatValue(vat);
						if (grossText[i] != null)
							grossText[i].setVatValue(vat);
					}
				}
				checkDirty();
			}
		});

		comboViewer.setInput(Data.INSTANCE.getVATs().getDatasets());
		try {
			comboViewer.setSelection(new StructuredSelection(Data.INSTANCE.getVATs().getDatasetById(vatId)), true);
		} catch (IndexOutOfBoundsException e) {
			comboVat.setText("invalid");
			vatId = -1;
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboVat);

		Label labelWeight = new Label(useWeight ? productDescGroup : invisible, SWT.NONE);
		labelWeight.setText("Gewicht (kg)");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelWeight);
		textWeight = new Text(useWeight ? productDescGroup : invisible, SWT.BORDER);
		textWeight.setText(product.getStringValueByKey("weight"));
		superviceControl(textWeight, 16);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(textWeight);

		Group productPictureGroup = new Group(usePicture ? top : invisible, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(productPictureGroup);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(productPictureGroup);
		productPictureGroup.setText("Produktbild");

		photoComposite = new Composite(productPictureGroup, SWT.BORDER);
		GridLayoutFactory.swtDefaults().margins(10, 10).numColumns(1).applyTo(photoComposite);
		GridDataFactory.fillDefaults().indent(0, 10).align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(photoComposite);
		photoComposite.setBackground(new Color(null, 255, 255, 255));
		
		labelProductPicture = new Label(photoComposite, SWT.NONE);
		pictureName = product.getStringValueByKey("picturename");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(labelProductPicture);

		textProductPicturePath = new Text(photoComposite, SWT.NONE);
		textProductPicturePath.setEditable(false);
		textProductPicturePath.setBackground(new Color(null, 255, 255, 255));
		superviceControl(textProductPicturePath, 250);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(textProductPicturePath);
		createPicturePathFromPictureName();
		setPicture();

		Button selectPictureButton = new Button(productPictureGroup, SWT.PUSH);
		selectPictureButton.setText("Bild auswählen");
		selectPictureButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				fileDialog.setFilterPath(Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE"));
				fileDialog.setText("Produktbild auswählen");
				String selectedFile = fileDialog.open();

				if (selectedFile != null) {
					createPictureName();
					File directory = new File(picturePath);
					if (!directory.exists())
						directory.mkdirs();

					File inputFile = new File(selectedFile);
					File outputFile = new File(filename1 + filename2);

					try {
						FileReader in = new FileReader(inputFile);
						FileWriter out = new FileWriter(outputFile);
						int c;

						while ((c = in.read()) != -1)
							out.write(c);

						in.close();
						out.close();
					} catch (IOException e1) {
						Logger.logError(e1, "Error copying picture from " + selectedFile + " to " + filename1 + filename2);
					}
					setPicture();
					checkDirty();
				}

			}
		});
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(selectPictureButton);

	}

	@Override
	public void setFocus() {
	}

}
