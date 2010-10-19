package com.sebulli.fakturama.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public enum DataSetListNames {
	NAMES;

	Properties listNames = new Properties();
	Properties lokalizedListNames = new Properties();

	/**
	 * Constructor Fill the list with name pairs
	 */
	DataSetListNames() {
		setNamePair("country_codes", "Länderkennung");
		setNamePair("billing_accounts", "Buchungskonten");
	}

	/**
	 * Create 2 Lists, one with the localizes and one with the non-lokalized
	 * name.
	 * 
	 * @param name
	 * @param lokalizedName
	 */
	private void setNamePair(String name, String lokalizedName) {
		lokalizedListNames.setProperty(name, lokalizedName);
		listNames.setProperty(lokalizedName, name);
	}

	/**
	 * Return the localized list name by the name
	 * 
	 * @param name
	 * @return
	 */
	public String getLocalizedName(String name) {
		return lokalizedListNames.getProperty(name, name);
	}

	/**
	 * Return the list name by the localized name
	 * 
	 * @param name
	 * @return
	 */
	public String getName(String name) {
		return listNames.getProperty(name, name);
	}

	/**
	 * Return the list with the localized names as set
	 * 
	 * @return
	 */
	public Set<Map.Entry<String, String>> getLocalizedNames() {

		// Convert properties to set
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> listNames = new HashMap<String, String>((Map) lokalizedListNames);
		Set<Map.Entry<String, String>> propertySet = listNames.entrySet();

		return propertySet;
	}

	/**
	 * Returns, whether the a list with this name exists
	 * 
	 * @param name
	 *            of the List
	 * @return TRUE, if there is list with this name
	 */
	public boolean exists(String name) {
		return listNames.containsValue(name);
	}

}
