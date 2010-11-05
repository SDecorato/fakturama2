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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

/**
 * Preference page for the sales export functionality
 * 
 * @author Gerd Bartelt
 */
public class ExportSalesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public ExportSalesPreferencePage() {
		super(GRID);

	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {

		//T: Preference page "Export Sales" - Label "Use Pay Date instead of Invoice Date"
		addField(new BooleanFieldEditor("EXPORTSALES_PAIDDATE", _("Use pay date instead of invoice date"), getFieldEditorParent()));

		//T: Preference page "Export Sales" - Label "Display column sum of expenditures"
		addField(new BooleanFieldEditor("EXPORTSALES_SHOW_EXPENDITURE_SUM_COLUMN", _("Display column sum of expenditures"), getFieldEditorParent()));

		//T: Preference page "Export Sales" - Label "Display columns with 0% tax"
		addField(new BooleanFieldEditor("EXPORTSALES_SHOW_ZERO_VAT_COLUMN", _("Display columns with 0% tax"), getFieldEditorParent()));
	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//T: Preference page "Export Sales" - Title"
		setDescription(_("Export Sales settings"));
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("EXPORTSALES_PAIDDATE", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("EXPORTSALES_SHOW_EXPENDITURE_SUM_COLUMN", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("EXPORTSALES_SHOW_ZERO_VAT_COLUMN", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.putBoolean("EXPORTSALES_PAIDDATE", true);
		node.putBoolean("EXPORTSALES_SHOW_EXPENDITURE_SUM_COLUMN", false);
		node.putBoolean("EXPORTSALES_SHOW_ZERO_VAT_COLUMN", false);

	}

}