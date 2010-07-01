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

package com.sebulli.fakturama.actions;

/**
 * Interface defining the application's command IDs. Key bindings can be defined
 * for specific commands. To associate an action with a command, use
 * IAction.setActionDefinitionId(commandId).
 * 
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

	public static final String CMD_OPEN_CONTACTS = "com.sebulli.fakturama.actions.openContacts";
	public static final String CMD_OPEN_PRODUCTS = "com.sebulli.fakturama.actions.openProducts";
	public static final String CMD_OPEN_VATS = "com.sebulli.fakturama.actions.openVats";
	public static final String CMD_OPEN_DOCUMENTS = "com.sebulli.fakturama.actions.openDocuments";
	public static final String CMD_OPEN_PAYMENTS = "com.sebulli.fakturama.actions.openPayments";
	public static final String CMD_OPEN_SHIPPINGS = "com.sebulli.fakturama.actions.openShippings";
	public static final String CMD_OPEN_TEXTS = "com.sebulli.fakturama.actions.openTexts";

	public static final String CMD_NEW_CONTACT = "com.sebulli.fakturama.actions.newContact";
	public static final String CMD_NEW_PRODUCT = "com.sebulli.fakturama.actions.newProduct";
	public static final String CMD_NEW_VAT = "com.sebulli.fakturama.actions.newVat";
	public static final String CMD_NEW_DOCUMENT = "com.sebulli.fakturama.actions.newDocument";
	public static final String CMD_NEW_PAYMENT = "com.sebulli.fakturama.actions.newPayment";
	public static final String CMD_NEW_SHIPPING = "com.sebulli.fakturama.actions.newShipping";
	public static final String CMD_NEW_TEXT = "com.sebulli.fakturama.actions.newText";

	public static final String CMD_NEW_ = "com.sebulli.fakturama.actions.new";
	public static final String CMD_NEW_LETTER = "com.sebulli.fakturama.actions.newLetter";
	public static final String CMD_NEW_OFFER = "com.sebulli.fakturama.actions.newOffer";
	public static final String CMD_NEW_ORDER = "com.sebulli.fakturama.actions.newOrder";
	public static final String CMD_NEW_CONFIRMATION = "com.sebulli.fakturama.actions.newConfirmation";
	public static final String CMD_NEW_INVOICE = "com.sebulli.fakturama.actions.newInvoice";
	public static final String CMD_NEW_DELIVERY = "com.sebulli.fakturama.actions.newDelivery";
	public static final String CMD_NEW_CREDIT = "com.sebulli.fakturama.actions.newCredit";
	public static final String CMD_NEW_DUNNING = "com.sebulli.fakturama.actions.newDunning";

	public static final String CMD_CREATE_OODOCUMENT = "com.sebulli.fakturama.actions.createOODocument";
	public static final String CMD_SAVE = "com.sebulli.fakturama.actions.save";

	public static final String CMD_DELETE_DATASET = "com.sebulli.fakturama.actions.deleteDataSet";

	public static final String CMD_SELECT_WORKSPACE = "com.sebulli.fakturama.actions.selectWorkspace";

	public static final String CMD_WEBSHOP_IMPORT = "com.sebulli.fakturama.actions.webShopImport";

	public static final String CMD_MARK_ORDER_AS_PENDING = "com.sebulli.fakturama.actions.markOrderAsPending";
	public static final String CMD_MARK_ORDER_AS_PROCESSING = "com.sebulli.fakturama.actions.markOrderAsProcessing";
	public static final String CMD_MARK_ORDER_AS_SHIPPED = "com.sebulli.fakturama.actions.markOrderAsShipped";
	public static final String CMD_MARK_ORDER_AS_FINISHED = "com.sebulli.fakturama.actions.markOrderAsFinished";

	public static final String CMD_EXPORT_VAT_SUMMARY = "com.sebulli.fakturama.actions.exportVatSummary";

	public static final String CMD_OPEN_BROWSER_EDITOR = "com.sebulli.fakturama.actions.openBrowserEditor";
	public static final String CMD_OPEN_CALCULATOR = "com.sebulli.fakturama.actions.openCalculator";
}