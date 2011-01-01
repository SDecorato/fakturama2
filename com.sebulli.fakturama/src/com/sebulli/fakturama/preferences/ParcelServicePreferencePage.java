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
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sebulli.fakturama.Activator;

/**
 * Preference page for the parcel service settings
 * 
 * @author Gerd Bartelt
 */
public class ParcelServicePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public ParcelServicePreferencePage() {
		super(GRID);

	}

	/**
	 * Creates the page's field editors.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {

		
		//T: Preference page "Parcel service" - Label "Service provider"
		addField(new ComboFieldEditor("PARCEL_SERVICE_PROVIDER", _("Parcel Service") + ":", new String[][] { 
				{ "DHL (efiliale.de)", "DHL" }, 
				{ "Hermes (hermespaketshop.de)", "HERMES" },
				{ _("Use settings below"), "SETTINGS" },
				}, getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_URL", _("Parcel service URL"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_NAME", _("Sender name"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_FIRST_NAME", _("Sender first name"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_LAST_NAME", _("Sender last name"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_ADDITIONAL_ADDRESS", _("Sender additional address"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_STREET", _("Sender street"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_NO", _("Sender street No"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_ZIP", _("Sender ZIP code"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_CITY", _("Sender city"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_SENDER_COUNTRY", _("Sender country"), getFieldEditorParent()));

		
		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_NAME", _("Consignee name"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_FIRST_NAME", _("Consignee first name"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_LAST_NAME", _("Consignee last name"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_ADDITIONAL_ADDRESS", _("Consignee additional address"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_STREET", _("Consignee street"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_NO", _("Consignee street No"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_ZIP", _("Consignee ZIP code"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_CITY", _("Consignee city"), getFieldEditorParent()));

		//T: Preference page "Parcel service" - Label 
		addField(new StringFieldEditor("PARCEL_SERVICE_CONSIGNEE_COUNTRY", _("Consignee country"), getFieldEditorParent()));
	}

	/**
	 * Initializes this preference page for the given workbench.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//T: Preference page "Parcel Service Import" - Title
		setDescription(_("Parcel Service"));
	}

	/**
	 * Write or read the preference settings to or from the data base
	 * 
	 * @param write
	 *            TRUE: Write to the data base
	 */
	public static void syncWithPreferencesFromDatabase(boolean write) {
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_PROVIDER", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_URL", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_FIRST_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_LAST_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_ADDITIONAL_ADDRESS", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_STREET", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_NO", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_ZIP", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_CITY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_SENDER_COUNTRY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_FIRST_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_LAST_NAME", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_ADDITIONAL_ADDRESS", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_STREET", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_NO", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_ZIP", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_CITY", write);
		PreferencesInDatabase.syncWithPreferencesFromDatabase("PARCEL_SERVICE_CONSIGNEE_COUNTRY", write);
	}

	/**
	 * Set the default values for this preference page
	 * 
	 * @param node
	 *            The preference node
	 */
	public static void setInitValues(IEclipsePreferences node) {
		node.put("PARCEL_SERVICE_URL", "http://www.yourparcelservice.com");
		node.put("PARCEL_SERVICE_PROVIDER", "DHL");
		node.put("PARCEL_SERVICE_SENDER_NAME", "");
		node.put("PARCEL_SERVICE_SENDER_FIRST_NAME", "");
		node.put("PARCEL_SERVICE_SENDER_LAST_NAME", "");
		node.put("PARCEL_SERVICE_SENDER_ADDITIONAL_ADDRESS", "");
		node.put("PARCEL_SERVICE_SENDER_STREET", "");
		node.put("PARCEL_SERVICE_SENDER_NO", "");
		node.put("PARCEL_SERVICE_SENDER_ZIP", "");
		node.put("PARCEL_SERVICE_SENDER_CITY", "");
		node.put("PARCEL_SERVICE_SENDER_COUNTRY", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_NAME", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_FIRST_NAME", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_LAST_NAME", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_ADDITIONAL_ADDRESS", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_STREET", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_NO", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_ZIP", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_CITY", "");
		node.put("PARCEL_SERVICE_CONSIGNEE_COUNTRY", "");
	}
	
}
