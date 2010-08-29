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

package com.sebulli.fakturama;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.sebulli.fakturama.actions.MarkOrderAsAction;
import com.sebulli.fakturama.actions.NewContactAction;
import com.sebulli.fakturama.actions.NewCountryCodeAction;
import com.sebulli.fakturama.actions.NewDocumentAction;
import com.sebulli.fakturama.actions.NewPaymentAction;
import com.sebulli.fakturama.actions.NewProductAction;
import com.sebulli.fakturama.actions.NewShippingAction;
import com.sebulli.fakturama.actions.NewTextAction;
import com.sebulli.fakturama.actions.NewVatAction;
import com.sebulli.fakturama.actions.OpenBrowserEditorAction;
import com.sebulli.fakturama.actions.OpenCalculatorAction;
import com.sebulli.fakturama.actions.OpenContactsAction;
import com.sebulli.fakturama.actions.OpenCountryCodesAction;
import com.sebulli.fakturama.actions.OpenDocumentsAction;
import com.sebulli.fakturama.actions.OpenPaymentsAction;
import com.sebulli.fakturama.actions.OpenProductsAction;
import com.sebulli.fakturama.actions.OpenShippingsAction;
import com.sebulli.fakturama.actions.OpenTextsAction;
import com.sebulli.fakturama.actions.OpenVatsAction;
import com.sebulli.fakturama.actions.SelectWorkspaceAction;
import com.sebulli.fakturama.actions.WebShopImportAction;
import com.sebulli.fakturama.data.DocumentType;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 * 
 * If the action is in the tool bar and in the menu, 2 actions have to be defined:
 * one with a 16x16 pixel icon for the menu and one with 32x32 pixel in the tool bar.
 * 
 * The tool bar version of an action is called xxTB
 * 
 * @author Gerd Bartelt
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction printAction;
	private IWorkbenchAction printActionTB;
	private IWorkbenchAction closeAction;
	private IWorkbenchAction closeAllAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveActionTB;
	private IWorkbenchAction saveAllAction;
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction exportWizardAction;
	private IWorkbenchAction openPreferencesAction;
	private IWorkbenchAction resetViewAction;
	private IWorkbenchAction helpAction;
	private OpenBrowserEditorAction openBrowserEditorAction;
	private OpenBrowserEditorAction openBrowserEditorActionTB;
	private OpenCalculatorAction openCalculatorAction;
	private OpenContactsAction openContactsAction;
	private OpenProductsAction openProductsAction;
	private OpenVatsAction openVatsAction;
	private OpenDocumentsAction openDocumentsAction;
	private OpenShippingsAction openShippingsAction;
	private OpenPaymentsAction openPaymentsAction;
	private OpenTextsAction openTextsAction;
	private OpenCountryCodesAction openCountryCodesAction;
	private NewProductAction newProductAction;
	private NewContactAction newContactAction;
	private NewVatAction newVatAction;
	private NewShippingAction newShippingAction;
	private NewPaymentAction newPaymentAction;
	private NewDocumentAction newLetterAction;
	private NewDocumentAction newOfferAction;
	private NewDocumentAction newOrderAction;
	private NewDocumentAction newConfirmationAction;
	private NewDocumentAction newInvoiceAction;
	private NewDocumentAction newDeliveryAction;
	private NewDocumentAction newCreditAction;
	private NewDocumentAction newDunningAction;
	private NewTextAction newTextAction;
	private NewCountryCodeAction newCountryCodeAction;
	private SelectWorkspaceAction selectWorkspaceAction;
	private WebShopImportAction webShopImportAction;
	private WebShopImportAction webShopImportActionTB;
	private MarkOrderAsAction markAsProcessingAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * Creates the actions and registers them.
	 * Registering is needed to ensure that key bindings work.
	 * The corresponding commands key bindings are defined in the plugin.xml
	 * file.
	 * Registering also provides automatic disposal of the actions when
	 * the window is closed.
	 * 
	 * @param window Workbench Window
	 */
	@Override
	protected void makeActions(final IWorkbenchWindow window) {

		exportWizardAction = ActionFactory.EXPORT.create(window);
		exportWizardAction.setText("Daten exportieren ..");
		register(exportWizardAction);

		openPreferencesAction = ActionFactory.PREFERENCES.create(window);
		openPreferencesAction.setText("Einstellungen");
		register(openPreferencesAction);

		exitAction = ActionFactory.QUIT.create(window);
		exitAction.setText("Fakturama beenden");
		register(exitAction);

		printAction = ActionFactory.PRINT.create(window);
		printAction.setText("Drucken");
		register(printAction);

		printActionTB = ActionFactory.PRINT.create(window);
		printActionTB.setText("Drucken");
		register(printActionTB);

		closeAction = ActionFactory.CLOSE.create(window);
		closeAction.setText("Schließen");
		register(closeAction);

		closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		closeAllAction.setText("Alle schließen");
		register(closeAllAction);

		saveAction = ActionFactory.SAVE.create(window);
		saveAction.setText("Speichern");
		register(saveAction);

		saveActionTB = ActionFactory.SAVE.create(window);
		saveActionTB.setText("Speichern");
		register(saveActionTB);

		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		saveAllAction.setText("Alle speichern");
		register(saveAllAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		aboutAction.setText("Über Fakturama");
		register(aboutAction);

		helpAction = ActionFactory.HELP_CONTENTS.create(window);
		helpAction.setText("Hilfe");
		register(helpAction);

		resetViewAction	= ActionFactory.RESET_PERSPECTIVE.create(window);	
		resetViewAction.setText("Perspektive zurücksetzen");
		register(resetViewAction);
		
		openBrowserEditorAction = new OpenBrowserEditorAction();
		register(openBrowserEditorAction);

		openBrowserEditorActionTB = new OpenBrowserEditorAction();
		register(openBrowserEditorActionTB);

		openCalculatorAction = new OpenCalculatorAction();
		register(openCalculatorAction);

		webShopImportAction = new WebShopImportAction();
		register(webShopImportAction);
		webShopImportActionTB = new WebShopImportAction();
		register(webShopImportActionTB);

		openProductsAction = new OpenProductsAction();
		register(openProductsAction);
		newProductAction = new NewProductAction();
		register(newProductAction);

		openContactsAction = new OpenContactsAction();
		register(openContactsAction);
		newContactAction = new NewContactAction(null);
		register(newContactAction);

		openVatsAction = new OpenVatsAction();
		register(openVatsAction);
		newVatAction = new NewVatAction();
		register(newVatAction);

		openShippingsAction = new OpenShippingsAction();
		register(openShippingsAction);
		newShippingAction = new NewShippingAction();
		register(newShippingAction);

		openPaymentsAction = new OpenPaymentsAction();
		register(openPaymentsAction);
		newPaymentAction = new NewPaymentAction();
		register(newPaymentAction);

		openTextsAction = new OpenTextsAction();
		register(openTextsAction);
		newTextAction = new NewTextAction();
		register(newTextAction);

		openCountryCodesAction = new OpenCountryCodesAction();
		register(openCountryCodesAction);
		newCountryCodeAction = new NewCountryCodeAction();
		register(newCountryCodeAction);

		
		
		
		
		openDocumentsAction = new OpenDocumentsAction();
		register(openDocumentsAction);

		newLetterAction = new NewDocumentAction(DocumentType.LETTER);
		register(newLetterAction);
		newOfferAction = new NewDocumentAction(DocumentType.OFFER);
		register(newOfferAction);
		newOrderAction = new NewDocumentAction(DocumentType.ORDER);
		register(newOrderAction);
		newConfirmationAction = new NewDocumentAction(DocumentType.CONFIRMATION);
		register(newConfirmationAction);
		newInvoiceAction = new NewDocumentAction(DocumentType.INVOICE);
		register(newInvoiceAction);
		newDeliveryAction = new NewDocumentAction(DocumentType.DELIVERY);
		register(newDeliveryAction);
		newCreditAction = new NewDocumentAction(DocumentType.CREDIT);
		register(newCreditAction);
		newDunningAction = new NewDocumentAction(DocumentType.DUNNING);
		register(newDunningAction);

		selectWorkspaceAction = new SelectWorkspaceAction();
		register(selectWorkspaceAction);

		markAsProcessingAction = new MarkOrderAsAction("", 50);
		register(markAsProcessingAction);

	}

	/**
	 * Fill the menu bar.
	 * 
	 * On MAC OS X the entries "about" and "preferences" are in a special menu.
	 * So on this OS, the entries are not added to the menu.
	 * 
	 * @param menuBar menu bar to fill
	 */
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&Datei", IWorkbenchActionConstants.M_FILE);
		MenuManager windowMenu = new MenuManager("&Fenster", IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("&Hilfe", IWorkbenchActionConstants.M_HELP);
		MenuManager hiddenMenu = new MenuManager("Hidden", "com.sebulli.faktura.menu.hidden");
		hiddenMenu.setVisible(false);

		menuBar.add(fileMenu);
		// Add a group marker indicating where action set menus will appear.
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(windowMenu);
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(helpMenu);
		menuBar.add(hiddenMenu);

		// File menu
		fileMenu.add(closeAction);
		fileMenu.add(closeAllAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.CLOSE_EXT));
		fileMenu.add(new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(saveAllAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
		fileMenu.add(new Separator());
		fileMenu.add(printAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
		fileMenu.add(new Separator());
		fileMenu.add(selectWorkspaceAction);
		if (OSDependent.canAddPreferenceAboutMenu()) {
			fileMenu.add(new Separator());
			fileMenu.add(openPreferencesAction);
		} else {
			hiddenMenu.add(openPreferencesAction);
		}
		fileMenu.add(new Separator());
		fileMenu.add(webShopImportAction);
		fileMenu.add(exportWizardAction);
		fileMenu.add(new GroupMarker(ActionFactory.EXPORT.getId()));
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		// window menu
		windowMenu.add(resetViewAction);
		
		// Help menu
		helpMenu.add(openBrowserEditorAction);
		helpMenu.add(helpAction);

		if (OSDependent.canAddAboutMenuItem()) {
			helpMenu.add(new Separator());
			helpMenu.add(aboutAction);
		} else {
			hiddenMenu.add(aboutAction);
		}
		
	}

	/**
	 * Fill the cool bar with 3 Toolbars.
	 * 
	 * 1st with general tool items like save and print.
	 * 2nd with tool items to create a new document
	 * 3rd with some extra items like calculator
	 * 
	 * The icons of the actions are replaced by 32x32 pixel icons.
	 * If the action is in the tool bar and in the menu, 2 actions have to be defined:
	 * one with a 16x16 pixel icon for the menu and one with 32x32 pixel in the tool bar.
	 * 
	 * @param collBar cool bar to fill
	 */
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar1 = new ToolBarManager(SWT.FLAT);
		IToolBarManager toolbar2 = new ToolBarManager(SWT.FLAT);
		IToolBarManager toolbar3 = new ToolBarManager(SWT.FLAT);

		coolBar.add(new ToolBarContributionItem(toolbar1, "main1"));
		coolBar.add(new ToolBarContributionItem(toolbar2, "main2"));
		coolBar.add(new ToolBarContributionItem(toolbar3, "main3"));

		webShopImportActionTB.setImageDescriptor(Activator.getImageDescriptor("/icons/32/shop_32.png"));
		webShopImportActionTB.setText("Webshop");
		ActionContributionItem webShopImportCI = new ActionContributionItem(webShopImportActionTB);
		webShopImportCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar1.add(webShopImportCI);

		printActionTB.setImageDescriptor(Activator.getImageDescriptor("/icons/32/printoo_32.png"));
		printActionTB.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/32/printoo_dis_32.png"));
		printActionTB.setText("Drucken");
		ActionContributionItem printActionTBCI = new ActionContributionItem(printActionTB);
		printActionTBCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar1.add(printActionTBCI);

		saveActionTB.setImageDescriptor(Activator.getImageDescriptor("/icons/32/save_32.png"));
		saveActionTB.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/32/save_dis_32.png"));
		saveActionTB.setText("Speichern");
		ActionContributionItem saveCI = new ActionContributionItem(saveActionTB);
		saveCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar1.add(saveCI);

		newLetterAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/letter_new_32.png"));
		newLetterAction.setText("Brief");
		ActionContributionItem newLetterCI = new ActionContributionItem(newLetterAction);
		newLetterCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newLetterCI);

		newOfferAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/offer_new_32.png"));
		newOfferAction.setText("Angebot");
		ActionContributionItem newOfferCI = new ActionContributionItem(newOfferAction);
		newOfferCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newOfferCI);

		newOrderAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/order_new_32.png"));
		newOrderAction.setText("Bestellung");
		ActionContributionItem newOrderCI = new ActionContributionItem(newOrderAction);
		newOrderCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newOrderCI);

		newConfirmationAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/confirmation_new_32.png"));
		newConfirmationAction.setText("Auftragsb.");
		ActionContributionItem newConfirmationCI = new ActionContributionItem(newConfirmationAction);
		newConfirmationCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newConfirmationCI);

		newInvoiceAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/invoice_new_32.png"));
		newInvoiceAction.setText("Rechnung");
		ActionContributionItem newInvoiceCI = new ActionContributionItem(newInvoiceAction);
		newInvoiceCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newInvoiceCI);

		newDeliveryAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/delivery_new_32.png"));
		newDeliveryAction.setText("Lieferschein");
		ActionContributionItem newDeliveryCI = new ActionContributionItem(newDeliveryAction);
		newDeliveryCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newDeliveryCI);

		newCreditAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/credit_new_32.png"));
		newCreditAction.setText("Gutschr.");
		ActionContributionItem newCreditCI = new ActionContributionItem(newCreditAction);
		newCreditCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newCreditCI);

		newDunningAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/dunning_new_32.png"));
		newDunningAction.setText("Mahnung");
		ActionContributionItem newDunningCI = new ActionContributionItem(newDunningAction);
		newDunningCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newDunningCI);

		newContactAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/contact_new_32.png"));
		newContactAction.setText("Kontakt");
		ActionContributionItem newContactCI = new ActionContributionItem(newContactAction);
		newContactCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newContactCI);

		newProductAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/product_new_32.png"));
		newProductAction.setText("Produkt");
		ActionContributionItem newProductCI = new ActionContributionItem(newProductAction);
		newProductCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar2.add(newProductCI);

		openBrowserEditorActionTB.setImageDescriptor(Activator.getImageDescriptor("/icons/32/www_32.png"));
		openBrowserEditorActionTB.setText("www");
		ActionContributionItem openBrowserEditorCI = new ActionContributionItem(openBrowserEditorActionTB);
		openBrowserEditorCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar3.add(openBrowserEditorCI);

		openCalculatorAction.setImageDescriptor(Activator.getImageDescriptor("/icons/32/calculator_32.png"));
		openCalculatorAction.setText("Rechner");
		ActionContributionItem openCalculatorCI = new ActionContributionItem(openCalculatorAction);
		openCalculatorCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar3.add(openCalculatorCI);

	}
}
