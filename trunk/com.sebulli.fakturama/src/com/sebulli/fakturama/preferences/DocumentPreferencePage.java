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

package com.sebulli.fakturama.preferences;

import static com.sebulli.fakturama.Translate._;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

/**
 * Preference page for the document settings
 * 
 * @author Gerd Bartelt
 */
public class DocumentPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	
	/* TRANSLATORS: The placeholder indicates the bug-reporting address
    for this package.  Please add _another line_ saying
    "Report translation bugs to <...>\n" with the address for translation
    bugs (typically your translation team's web or email address).  */

	
	
	/**
	 * Constructor
	 */
	public DocumentPreferencePage() {
		super(GRID);

	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {

		//T: Preference page "Document" - Label "Format (net or gross) of the price in the item list"
		addField(new RadioGroupFieldEditor("DOCUMENT_USE_NET_GROSS", _("Price in the item list:"), 2, new String[][] { 
					{ _("Net"), "0" },
					{ _("Gross"), "1" } },
				getFieldEditorParent()));
		
		//T: Preference page "Document" - Label "Copy the content of the message field when creating a duplicate of the document."
		addField(new BooleanFieldEditor("DOCUMENT_COPY_MESSAGE_FROM_PARENT", _("Copy message field when creating a duplicate"), getFieldEditorParent()));
		//T: Preference page "Document" - Label "Copy the description in product selection dialog."
		addField(new BooleanFieldEditor("DOCUMENT_COPY_PRODUCT_DESCRIPTION_FROM_PRODUCTS_DIALOG", _("Copy description from product."), getFieldEditorParent()));

	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//T: Preference page "Document" - Title"
		setDescription(_("Document Settings"));
		
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("DOCUMENT_USE_NET_GROSS", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("DOCUMENT_COPY_MESSAGE_FROM_PARENT", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("DOCUMENT_COPY_PRODUCT_DESCRIPTION_FROM_PRODUCTS_DIALOG", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("DOCUMENT_USE_NET_GROSS", "1");
		node.putBoolean("DOCUMENT_COPY_MESSAGE_FROM_PARENT", false);
		node.putBoolean("DOCUMENT_COPY_PRODUCT_DESCRIPTION_FROM_PRODUCTS_DIALOG", false);

	}

}
