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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetContact;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.views.datasettable.ViewContactTable;

public class ContactEditor extends Editor {
	public static final String ID = "com.sebulli.fakturama.editors.contactEditor";
	private DataSetContact contact;
	private TabFolder tabFolder;

	private Text textNote;
	private Combo comboGender;
	private Text txtTitle;
	private Text txtFirstname;
	private Text txtName;
	private Text txtCompany;
	private Text txtStreet;
	private Text txtZip;
	private Text txtCity;
	private Text txtCountry;
	private Combo comboDeliveryGender;
	private Text txtDeliveryTitle;
	private Text txtDeliveryFirstname;
	private Text txtDeliveryName;
	private Text txtDeliveryCompany;
	private Text txtDeliveryStreet;
	private Text txtDeliveryZip;
	private Text txtDeliveryCity;
	private Text txtDeliveryCountry;
	private Text txtAccountHolder;
	private Text txtAccount;
	private Text txtBankCode;
	private Text txtBankName;
	private Text txtIBAN;
	private Text txtBIC;
	private Text txtNr;
	private Combo comboPayment;
	private ComboViewer comboPaymentViewer;
	private Combo comboReliability;
	private Text txtPhone;
	private Text txtFax;
	private Text txtMobile;
	private Text txtEmail;
	private Text txtWebsite;
	private Text txtVatNr;
	private Text txtDiscount;
	private Text txtCategory;
	private Group deliveryGroup;
	private boolean useDelivery;
	private boolean useBank;
	private boolean useMisc;
	private boolean useNote;
	private boolean useGender;
	private boolean useTitle;
	private boolean useLastNameFirst;
	private boolean useCompany;
	private boolean useCountry;
	private Button bDelAddrEquAddr;
	private boolean newContact;

	public ContactEditor() {
		tableViewID = ViewContactTable.ID;
		editorID = "contact";
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		/*
		 * the following parameters are not saved: - id (constant) - date_added
		 * (constant)
		 */

		if (bDelAddrEquAddr.getSelection())
			copyAddressToDeliveryAdress();
		contact.setBooleanValueByKey("deleted", false);

		contact.setIntValueByKey("gender", comboGender.getSelectionIndex());
		contact.setStringValueByKey("title", txtTitle.getText());
		contact.setStringValueByKey("firstname", txtFirstname.getText());
		contact.setStringValueByKey("name", txtName.getText());
		contact.setStringValueByKey("company", txtCompany.getText());
		contact.setStringValueByKey("street", txtStreet.getText());
		contact.setStringValueByKey("zip", txtZip.getText());
		contact.setStringValueByKey("city", txtCity.getText());
		contact.setStringValueByKey("country", txtCountry.getText());

		contact.setIntValueByKey("delivery_gender", comboDeliveryGender.getSelectionIndex());
		contact.setStringValueByKey("delivery_title", txtDeliveryTitle.getText());
		contact.setStringValueByKey("delivery_firstname", txtDeliveryFirstname.getText());
		contact.setStringValueByKey("delivery_name", txtDeliveryName.getText());
		contact.setStringValueByKey("delivery_company", txtDeliveryCompany.getText());
		contact.setStringValueByKey("delivery_street", txtDeliveryStreet.getText());
		contact.setStringValueByKey("delivery_zip", txtDeliveryZip.getText());
		contact.setStringValueByKey("delivery_city", txtDeliveryCity.getText());
		contact.setStringValueByKey("delivery_country", txtDeliveryCountry.getText());

		contact.setStringValueByKey("account_holder", txtAccountHolder.getText());
		contact.setStringValueByKey("account", txtAccount.getText());
		contact.setStringValueByKey("bank_code", txtBankCode.getText());
		contact.setStringValueByKey("bank_name", txtBankName.getText());
		contact.setStringValueByKey("iban", txtIBAN.getText());
		contact.setStringValueByKey("bic", txtBIC.getText());

		contact.setStringValueByKey("nr", txtNr.getText());

		IStructuredSelection structuredSelection = (IStructuredSelection) comboPaymentViewer.getSelection();
		if (!structuredSelection.isEmpty()) {
			contact.setIntValueByKey("payment", ((UniDataSet) structuredSelection.getFirstElement()).getIntValueByKey("id"));
		}

		contact.setIntValueByKey("reliability", comboReliability.getSelectionIndex());
		contact.setStringValueByKey("phone", txtPhone.getText());
		contact.setStringValueByKey("fax", txtFax.getText());
		contact.setStringValueByKey("mobile", txtMobile.getText());
		contact.setStringValueByKey("email", txtEmail.getText());
		contact.setStringValueByKey("website", txtWebsite.getText());
		contact.setStringValueByKey("vatnr", txtVatNr.getText());
		contact.setDoubleValueByKey("discount", DataUtils.StringToDoubleDiscount(txtDiscount.getText()));
		contact.setStringValueByKey("category", txtCategory.getText());

		contact.setStringValueByKey("note", textNote.getText());

		if (newContact) {
			contact = Data.INSTANCE.getContacts().addNewDataSet(contact);
			newContact = false;
		} else {
			Data.INSTANCE.getContacts().updateDataSet(contact);
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
		contact = (DataSetContact) ((UniDataSetEditorInput) input).getUniDataSet();

		newContact = (contact == null);

		if (newContact) {
			contact = new DataSetContact(((UniDataSetEditorInput) input).getCategory());
			setPartName("neuer Kontakt");
			contact.setIntValueByKey("payment", Data.INSTANCE.getPropertyAsInt("standardpayment"));

		} else {
			setPartName(contact.getStringValueByKey("firstname") + " " + contact.getStringValueByKey("name"));
		}

	}

	@Override
	public boolean isDirty() {

		/*
		 * the following parameters are not checked: - id (constant) -
		 * date_added (constant) - servicedate:
		 */

		if (contact.getBooleanValueByKey("deleted")) { return true; }

		if (contact.getIntValueByKey("gender") != comboGender.getSelectionIndex()) { return true; }
		;
		if (!contact.getStringValueByKey("title").equals(txtTitle.getText())) { return true; }
		if (!contact.getStringValueByKey("firstname").equals(txtFirstname.getText())) { return true; }
		if (!contact.getStringValueByKey("name").equals(txtName.getText())) { return true; }
		if (!contact.getStringValueByKey("company").equals(txtCompany.getText())) { return true; }
		if (!contact.getStringValueByKey("street").equals(txtStreet.getText())) { return true; }
		if (!contact.getStringValueByKey("zip").equals(txtZip.getText())) { return true; }
		if (!contact.getStringValueByKey("city").equals(txtCity.getText())) { return true; }
		if (!contact.getStringValueByKey("country").equals(txtCountry.getText())) { return true; }

		if (contact.getIntValueByKey("delivery_gender") != comboDeliveryGender.getSelectionIndex()) { return true; }
		if (!contact.getStringValueByKey("delivery_title").equals(txtDeliveryTitle.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_firstname").equals(txtDeliveryFirstname.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_name").equals(txtDeliveryName.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_company").equals(txtDeliveryCompany.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_street").equals(txtDeliveryStreet.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_zip").equals(txtDeliveryZip.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_city").equals(txtDeliveryCity.getText())) { return true; }
		if (!contact.getStringValueByKey("delivery_country").equals(txtDeliveryCountry.getText())) { return true; }

		if (!contact.getStringValueByKey("account_holder").equals(txtAccountHolder.getText())) { return true; }
		if (!contact.getStringValueByKey("account").equals(txtAccount.getText())) { return true; }
		if (!contact.getStringValueByKey("bank_code").equals(txtBankCode.getText())) { return true; }
		if (!contact.getStringValueByKey("bank_name").equals(txtBankName.getText())) { return true; }
		if (!contact.getStringValueByKey("iban").equals(txtIBAN.getText())) { return true; }
		if (!contact.getStringValueByKey("bic").equals(txtBIC.getText())) { return true; }

		if (!contact.getStringValueByKey("nr").equals(txtNr.getText())) { return true; }

		IStructuredSelection structuredSelection = (IStructuredSelection) comboPaymentViewer.getSelection();
		if (!structuredSelection.isEmpty()) {
			if (contact.getIntValueByKey("payment") != ((UniDataSet) structuredSelection.getFirstElement()).getIntValueByKey("id")) { return true; }
		}

		if (contact.getIntValueByKey("reliability") != comboReliability.getSelectionIndex()) { return true; }
		if (!contact.getStringValueByKey("phone").equals(txtPhone.getText())) { return true; }
		if (!contact.getStringValueByKey("fax").equals(txtFax.getText())) { return true; }
		if (!contact.getStringValueByKey("mobile").equals(txtMobile.getText())) { return true; }
		if (!contact.getStringValueByKey("email").equals(txtEmail.getText())) { return true; }
		if (!contact.getStringValueByKey("website").equals(txtWebsite.getText())) { return true; }
		if (!contact.getStringValueByKey("vatnr").equals(txtVatNr.getText())) { return true; }
		if (!contact.getStringValueByKey("category").equals(txtCategory.getText())) { return true; }
		if (!DataUtils.DoublesAreEqual(contact.getDoubleValueByKey("discount"), DataUtils.StringToDoubleDiscount(txtDiscount.getText()))) { return true; }

		if (!contact.getStringValueByKey("note").equals(textNote.getText())) { return true; }

		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void deliveryAdressIsEqual(boolean isEqual) {
		deliveryGroup.setVisible(!isEqual);
		if (isEqual)
			copyAddressToDeliveryAdress();

	}

	private void copyAddressToDeliveryAdress() {
		comboDeliveryGender.select(comboGender.getSelectionIndex());
		txtDeliveryTitle.setText(txtTitle.getText());
		txtDeliveryFirstname.setText(txtFirstname.getText());
		txtDeliveryName.setText(txtName.getText());
		txtDeliveryCompany.setText(txtCompany.getText());
		txtDeliveryStreet.setText(txtStreet.getText());
		txtDeliveryZip.setText(txtZip.getText());
		txtDeliveryCity.setText(txtCity.getText());
		txtDeliveryCountry.setText(txtCountry.getText());
	}

	private boolean isAddressEqualToDeliveryAdress() {
		if (comboDeliveryGender.getSelectionIndex() != comboGender.getSelectionIndex()) { return false; }
		if (!txtDeliveryTitle.getText().equals(txtTitle.getText())) { return false; }
		if (!txtDeliveryFirstname.getText().equals(txtFirstname.getText())) { return false; }
		if (!txtDeliveryName.getText().equals(txtName.getText())) { return false; }
		if (!txtDeliveryCompany.getText().equals(txtCompany.getText())) { return false; }
		if (!txtDeliveryStreet.getText().equals(txtStreet.getText())) { return false; }
		if (!txtDeliveryZip.getText().equals(txtZip.getText())) { return false; }
		if (!txtDeliveryCity.getText().equals(txtCity.getText())) { return false; }
		if (!txtDeliveryCountry.getText().equals(txtCountry.getText())) { return false; }

		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		useDelivery = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_DELIVERY");
		useBank = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_BANK");
		useMisc = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_MISC");
		useNote = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_NOTE");
		useGender = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_GENDER");
		useTitle = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_TITLE");
		useLastNameFirst = (Activator.getDefault().getPreferenceStore().getInt("CONTACT_NAME_FORMAT") == 1);
		useCompany = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_COMPANY");
		useCountry = Activator.getDefault().getPreferenceStore().getBoolean("CONTACT_USE_COUNTRY");

		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(top);

		Composite invisible = new Composite(top, SWT.NONE);
		invisible.setVisible(false);
		GridDataFactory.fillDefaults().hint(0, 0).applyTo(invisible);

		Composite tabAddress;
		if (useDelivery || useBank || useMisc || useNote) {
			tabFolder = new TabFolder(top, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFolder);

			TabItem item1 = new TabItem(tabFolder, SWT.NONE);
			item1.setText("Adresse");
			tabAddress = new Composite(tabFolder, SWT.NONE);
			item1.setControl(tabAddress);
		} else {
			tabAddress = new Composite(top, SWT.NONE);
		}
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(tabAddress);

		Composite tabBank;
		if (useBank) {
			TabItem item3 = new TabItem(tabFolder, SWT.NONE);
			item3.setText("Bankdaten");
			tabBank = new Composite(tabFolder, SWT.NONE);
			item3.setControl(tabBank);
		} else {
			tabBank = new Composite(invisible, SWT.NONE);
		}
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(tabBank);

		Composite tabMisc;
		if (useMisc) {
			TabItem item4 = new TabItem(tabFolder, SWT.NONE);
			item4.setText("Sonstiges");
			tabMisc = new Composite(tabFolder, SWT.NONE);
			item4.setControl(tabMisc);
		} else {
			tabMisc = new Composite(invisible, SWT.NONE);
		}
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(tabMisc);

		TabItem item5 = null;
		Composite tabNote;
		if (useNote) {
			item5 = new TabItem(tabFolder, SWT.NONE);
			item5.setText("Hinweis");
			tabNote = new Composite(tabFolder, SWT.NONE);
			item5.setControl(tabNote);
		} else {
			tabNote = new Composite(invisible, SWT.NONE);
		}
		tabNote.setLayout(new FillLayout());

		Composite customerNrComposite = new Composite(tabAddress, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(customerNrComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(customerNrComposite);

		Composite useDeliveryAdressComposite = new Composite(tabAddress, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(useDeliveryAdressComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(useDeliveryAdressComposite);

		Label labelNr = new Label(customerNrComposite, SWT.NONE);
		labelNr.setText("Kundennummer");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelNr);
		txtNr = new Text(customerNrComposite, SWT.BORDER);
		txtNr.setText(contact.getStringValueByKey("nr"));
		superviceControl(txtNr, 32);
		GridDataFactory.swtDefaults().hint(100, -1).applyTo(txtNr);

		bDelAddrEquAddr = new Button(useDeliveryAdressComposite, SWT.CHECK);
		bDelAddrEquAddr.setText("Lieferadresse ist Rechnungsadresse");
		GridDataFactory.swtDefaults().applyTo(bDelAddrEquAddr);
		bDelAddrEquAddr.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deliveryAdressIsEqual(bDelAddrEquAddr.getSelection());
				checkDirty();
			}
		});

		Group addressGroup = new Group(tabAddress, SWT.BORDER);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(addressGroup);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(addressGroup);
		addressGroup.setText("Adresse");

		// Controls in tab "Address"
		Label labelTitle = new Label((useGender || useTitle) ? addressGroup : invisible, SWT.NONE);
		if (useGender)
			labelTitle.setText("Geschlecht");
		if (useGender && useTitle)
			labelTitle.setText(labelTitle.getText() + ", ");
		if (useTitle)
			labelTitle.setText(labelTitle.getText() + "Titel");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelTitle);
		comboGender = new Combo(useGender ? addressGroup : invisible, SWT.BORDER);
		for (int i = 0; i < 4; i++)
			comboGender.add(DataSetContact.getGenderString(i), i);
		comboGender.select(contact.getIntValueByKey("gender"));
		superviceControl(comboGender);

		GridDataFactory.swtDefaults().hint(100, -1).span(useTitle ? 1 : 2, 1).applyTo(comboGender);
		txtTitle = new Text(useTitle ? addressGroup : invisible, SWT.BORDER);
		txtTitle.setText(contact.getStringValueByKey("title"));
		superviceControl(txtTitle, 32);

		GridDataFactory.fillDefaults().grab(true, false).span(useGender ? 1 : 2, 1).applyTo(txtTitle);
		Label labelName = new Label(addressGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelName);
		if (useLastNameFirst) {
			labelName.setText("Nachname, Vorname");
			txtName = new Text(addressGroup, SWT.BORDER);
			GridDataFactory.swtDefaults().hint(100, -1).applyTo(txtName);
			txtFirstname = new Text(addressGroup, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(txtFirstname);
		} else {
			labelName.setText("Vorname Nachname");
			txtFirstname = new Text(addressGroup, SWT.BORDER);
			GridDataFactory.swtDefaults().hint(100, -1).applyTo(txtFirstname);
			txtName = new Text(addressGroup, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(txtName);
		}
		txtFirstname.setText(contact.getStringValueByKey("firstname"));
		txtName.setText(contact.getStringValueByKey("name"));
		superviceControl(txtFirstname, 64);
		superviceControl(txtName, 64);

		Label labelCompany = new Label(useCompany ? addressGroup : invisible, SWT.NONE);
		labelCompany.setText("Firma");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCompany);
		txtCompany = new Text(useCompany ? addressGroup : invisible, SWT.BORDER | SWT.MULTI);
		txtCompany.setText(contact.getStringValueByKey("company"));
		superviceControl(txtCompany, 64);
		GridDataFactory.fillDefaults().hint(210, 40).grab(true, false).span(2, 1).applyTo(txtCompany);

		Label labelStreet = new Label(addressGroup, SWT.NONE);
		labelStreet.setText("Strasse");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelStreet);
		txtStreet = new Text(addressGroup, SWT.BORDER);
		txtStreet.setText(contact.getStringValueByKey("street"));
		superviceControl(txtStreet, 64);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(txtStreet);

		Label labelCity = new Label(addressGroup, SWT.NONE);
		labelCity.setText("PLZ, Ort");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCity);
		txtZip = new Text(addressGroup, SWT.BORDER);
		txtZip.setText(contact.getStringValueByKey("zip"));
		superviceControl(txtZip, 16);
		GridDataFactory.swtDefaults().hint(100, -1).applyTo(txtZip);
		txtCity = new Text(addressGroup, SWT.BORDER);
		txtCity.setText(contact.getStringValueByKey("city"));
		superviceControl(txtCity, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCity);

		Label labelCountry = new Label(useCountry ? addressGroup : invisible, SWT.NONE);
		labelCountry.setText("Land");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCountry);
		txtCountry = new Text(useCountry ? addressGroup : invisible, SWT.BORDER);
		txtCountry.setText(contact.getStringValueByKey("country"));
		superviceControl(txtCountry, 32);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(txtCountry);

		deliveryGroup = new Group(tabAddress, SWT.BORDER);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(deliveryGroup);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(deliveryGroup);
		deliveryGroup.setText("Lieferadresse");

		// Controls in tab "Delivery"

		Label labelDeliveryTitle = new Label((useGender || useTitle) ? deliveryGroup : invisible, SWT.NONE);
		if (useGender)
			labelDeliveryTitle.setText("Geschlecht");
		if (useGender && useTitle)
			labelDeliveryTitle.setText(labelDeliveryTitle.getText() + ", ");
		if (useTitle)
			labelDeliveryTitle.setText(labelDeliveryTitle.getText() + "Titel");

		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDeliveryTitle);
		comboDeliveryGender = new Combo(useGender ? deliveryGroup : invisible, SWT.BORDER);
		for (int i = 0; i < 4; i++)
			comboDeliveryGender.add(DataSetContact.getGenderString(i), i);
		comboDeliveryGender.select(contact.getIntValueByKey("delivery_gender"));
		superviceControl(comboDeliveryGender);
		GridDataFactory.fillDefaults().grab(false, false).hint(100, -1).span(useTitle ? 1 : 2, 1).applyTo(comboDeliveryGender);
		txtDeliveryTitle = new Text(useTitle ? deliveryGroup : invisible, SWT.BORDER);
		txtDeliveryTitle.setText(contact.getStringValueByKey("delivery_title"));
		superviceControl(txtDeliveryTitle, 32);
		GridDataFactory.fillDefaults().grab(true, false).span(useGender ? 1 : 2, 1).applyTo(txtDeliveryTitle);

		Label labelDeliveryName = new Label(deliveryGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDeliveryName);
		if (useLastNameFirst) {
			labelDeliveryName.setText("Nachname, Vorname");
			txtDeliveryName = new Text(deliveryGroup, SWT.BORDER);
			GridDataFactory.swtDefaults().hint(100, -1).applyTo(labelDeliveryName);
			txtDeliveryFirstname = new Text(deliveryGroup, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(txtDeliveryFirstname);
		} else {
			labelDeliveryName.setText("Vorname Nachname");
			txtDeliveryFirstname = new Text(deliveryGroup, SWT.BORDER);
			GridDataFactory.swtDefaults().hint(100, -1).applyTo(txtDeliveryFirstname);
			txtDeliveryName = new Text(deliveryGroup, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(txtDeliveryName);

		}
		txtDeliveryFirstname.setText(contact.getStringValueByKey("delivery_firstname"));
		txtDeliveryName.setText(contact.getStringValueByKey("delivery_name"));
		superviceControl(txtDeliveryFirstname, 64);
		superviceControl(txtDeliveryName, 64);

		Label labelDeliveryCompany = new Label(useCompany ? deliveryGroup : invisible, SWT.NONE);
		labelDeliveryCompany.setText("Firma");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDeliveryCompany);
		txtDeliveryCompany = new Text(useCompany ? deliveryGroup : invisible, SWT.BORDER | SWT.MULTI);
		txtDeliveryCompany.setText(contact.getStringValueByKey("delivery_company"));
		superviceControl(txtDeliveryCompany, 64);
		GridDataFactory.fillDefaults().hint(210, 40).grab(true, false).span(2, 1).applyTo(txtDeliveryCompany);

		Label labelDeliveryStreet = new Label(deliveryGroup, SWT.NONE);
		labelDeliveryStreet.setText("Strasse");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDeliveryStreet);
		txtDeliveryStreet = new Text(deliveryGroup, SWT.BORDER);
		txtDeliveryStreet.setText(contact.getStringValueByKey("delivery_street"));
		superviceControl(txtDeliveryStreet, 64);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(txtDeliveryStreet);

		Label labelDeliveryCity = new Label(deliveryGroup, SWT.NONE);
		labelDeliveryCity.setText("PLZ, Ort");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDeliveryCity);
		txtDeliveryZip = new Text(deliveryGroup, SWT.BORDER);
		txtDeliveryZip.setText(contact.getStringValueByKey("delivery_zip"));
		superviceControl(txtDeliveryZip, 16);
		GridDataFactory.swtDefaults().hint(100, -1).applyTo(txtDeliveryZip);
		txtDeliveryCity = new Text(deliveryGroup, SWT.BORDER);
		txtDeliveryCity.setText(contact.getStringValueByKey("delivery_city"));
		superviceControl(txtDeliveryCity, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtDeliveryCity);

		Label labelDeliveryCountry = new Label(useCountry ? deliveryGroup : invisible, SWT.NONE);
		labelDeliveryCountry.setText("Land");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDeliveryCountry);
		txtDeliveryCountry = new Text(useCountry ? deliveryGroup : invisible, SWT.BORDER);
		txtDeliveryCountry.setText(contact.getStringValueByKey("delivery_country"));
		superviceControl(txtDeliveryZip, 32);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(txtDeliveryCountry);

		// Controls in tab "Bank"

		Label labelAccountHolder = new Label(tabBank, SWT.NONE);
		labelAccountHolder.setText("Kontoinhaber");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelAccountHolder);
		txtAccountHolder = new Text(tabBank, SWT.BORDER);
		txtAccountHolder.setText(contact.getStringValueByKey("account_holder"));
		superviceControl(txtAccountHolder, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtAccountHolder);

		Label labelAccount = new Label(tabBank, SWT.NONE);
		labelAccount.setText("Kontonummer");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelAccount);
		txtAccount = new Text(tabBank, SWT.BORDER);
		txtAccount.setText(contact.getStringValueByKey("account"));
		superviceControl(txtAccount, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtAccount);

		Label labelBankCode = new Label(tabBank, SWT.NONE);
		labelBankCode.setText("Bankleitzahl");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelBankCode);
		txtBankCode = new Text(tabBank, SWT.BORDER);
		txtBankCode.setText(contact.getStringValueByKey("bank_code"));
		superviceControl(txtBankCode, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtBankCode);

		Label labelBankName = new Label(tabBank, SWT.NONE);
		labelBankName.setText("Name der Bank");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelBankName);
		txtBankName = new Text(tabBank, SWT.BORDER);
		txtBankName.setText(contact.getStringValueByKey("bank_name"));
		superviceControl(txtBankName, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtBankName);

		Label labelIBAN = new Label(tabBank, SWT.NONE);
		labelIBAN.setText("IBAN");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelIBAN);
		txtIBAN = new Text(tabBank, SWT.BORDER);
		txtIBAN.setText(contact.getStringValueByKey("iban"));
		superviceControl(txtIBAN, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtIBAN);

		Label labelBIC = new Label(tabBank, SWT.NONE);
		labelBIC.setText("BIC");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelBIC);
		txtBIC = new Text(tabBank, SWT.BORDER);
		txtBIC.setText(contact.getStringValueByKey("bic"));
		superviceControl(txtBIC, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtBIC);

		// Controls in tab "Misc"

		Label labelCategory = new Label(tabMisc, SWT.NONE);
		labelCategory.setText("Kategorie");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelCategory);
		txtCategory = new Text(tabMisc, SWT.BORDER);
		txtCategory.setText(contact.getStringValueByKey("category"));
		superviceControl(txtCategory, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtCategory);

		Label labelEmail = new Label(tabMisc, SWT.NONE);
		labelEmail.setText("eMail");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelEmail);
		txtEmail = new Text(tabMisc, SWT.BORDER);
		txtEmail.setText(contact.getStringValueByKey("email"));
		superviceControl(txtEmail, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtEmail);

		Label labelTel = new Label(tabMisc, SWT.NONE);
		labelTel.setText("Telefon");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelTel);
		txtPhone = new Text(tabMisc, SWT.BORDER);
		txtPhone.setText(contact.getStringValueByKey("phone"));
		superviceControl(txtPhone, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtPhone);

		Label labelFax = new Label(tabMisc, SWT.NONE);
		labelFax.setText("Telefax");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelFax);
		txtFax = new Text(tabMisc, SWT.BORDER);
		txtFax.setText(contact.getStringValueByKey("fax"));
		superviceControl(txtFax, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtFax);

		Label labelMobile = new Label(tabMisc, SWT.NONE);
		labelMobile.setText("Mobil");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelMobile);
		txtMobile = new Text(tabMisc, SWT.BORDER);
		txtMobile.setText(contact.getStringValueByKey("mobile"));
		superviceControl(txtMobile, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtMobile);

		Label labelWebsite = new Label(tabMisc, SWT.NONE);
		labelWebsite.setText("Webseite");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelWebsite);
		txtWebsite = new Text(tabMisc, SWT.BORDER);
		txtWebsite.setText(contact.getStringValueByKey("website"));
		superviceControl(txtWebsite, 64);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtWebsite);

		Label labelPayment = new Label(tabMisc, SWT.NONE);
		labelPayment.setText("Bezahlung");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelPayment);
		comboPayment = new Combo(tabMisc, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboPayment);
		comboPaymentViewer = new ComboViewer(comboPayment);
		comboPaymentViewer.setContentProvider(new UniDataSetContentProvider());
		comboPaymentViewer.setLabelProvider(new UniDataSetLabelProvider());
		comboPaymentViewer.setInput(Data.INSTANCE.getPayments().getDatasets());

		int paymentId = contact.getIntValueByKey("payment");
		try {
			if (paymentId >= 0)
				comboPaymentViewer.setSelection(new StructuredSelection(Data.INSTANCE.getPayments().getDatasetById(paymentId)), true);
			else
				comboPayment.setText("");
		} catch (IndexOutOfBoundsException e) {
			comboPayment.setText("invalid");
		}
		superviceControl(comboPayment);

		Label labelReliability = new Label(tabMisc, SWT.NONE);
		labelReliability.setText("Bonität");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelReliability);
		comboReliability = new Combo(tabMisc, SWT.BORDER);
		comboReliability.add("---", 0);
		comboReliability.add("schlecht", 1);
		comboReliability.add("mittel", 2);
		comboReliability.add("gut", 3);
		comboReliability.select(contact.getIntValueByKey("reliability"));
		superviceControl(comboReliability);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboReliability);

		Label labelVatNr = new Label(tabMisc, SWT.NONE);
		labelVatNr.setText("USt.ID-Nr.");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelVatNr);
		txtVatNr = new Text(tabMisc, SWT.BORDER);
		txtVatNr.setText(contact.getStringValueByKey("vatnr"));
		superviceControl(txtVatNr, 32);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtVatNr);

		Label labelDiscount = new Label(tabMisc, SWT.NONE);
		labelDiscount.setText("Kd.Rabatt");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(labelDiscount);
		txtDiscount = new Text(tabMisc, SWT.BORDER);
		txtDiscount.setText(DataUtils.DoubleToFormatedPercent(contact.getDoubleValueByKey("discount")));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtDiscount);
		txtDiscount.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				txtDiscount.setText(DataUtils.DoubleToFormatedPercent(DataUtils.StringToDoubleDiscount(txtDiscount.getText())));
				checkDirty();
			}
		});
		txtDiscount.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					txtDiscount.setText(DataUtils.DoubleToFormatedPercent(DataUtils.StringToDoubleDiscount(txtDiscount.getText())));
					checkDirty();
				}
			}
		});

		// Controls in tab "Note"

		String note = contact.getStringValueByKey("note");
		textNote = new Text(tabNote, SWT.BORDER | SWT.MULTI);
		textNote.setText(note);
		superviceControl(textNote, 10000);

		if (useNote && note.length() > 0)
			tabFolder.setSelection(item5);

		Boolean isEqual = isAddressEqualToDeliveryAdress();
		bDelAddrEquAddr.setSelection(isEqual);
		deliveryGroup.setVisible(!isEqual);

	}

	@Override
	public void setFocus() {
	}

}