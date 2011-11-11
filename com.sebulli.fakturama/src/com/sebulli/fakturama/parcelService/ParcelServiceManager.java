/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.parcelService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.sebulli.fakturama.TemplateFilename;

/**
 * Loads the parcel service configurations files into properties lists.  
 * 
 * @author Gerd Bartelt
 *
 */
public class ParcelServiceManager {
	
	// All properties lists of all parcel services
	private ArrayList<Properties> propertiesList;
	
	// The active parcel service
	private int active = -1;
	
	/**
	 * Loads all the parcel service lists from a specified path
	 * 
	 * @param templatePath
	 * 		The path where all the files are listed
	 */
	public ParcelServiceManager (String templatePath) {
		
		// Clear all, and create a new array list
		propertiesList = new ArrayList<Properties>();
		
		// Get the directory and find all files
		File dir = new File(templatePath);
		String[] children = dir.list();

		// Get all files
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				
				// Get filename of file or directory
				TemplateFilename templateFilename = new TemplateFilename(templatePath, children[i]);
				
				// It's used as a parcel service file, if it ends with a *.txt
				if (templateFilename.getExtension().equalsIgnoreCase(".txt")) {

					// Load the file into a properties object
					Properties properties = new Properties();
					BufferedInputStream stream;
					try {
						stream = new BufferedInputStream(new FileInputStream(templateFilename.getPathAndFilename()));
						properties.load(stream);
						stream.close();

						// Use it only, if there is at least a name and a url key
						if (properties.containsKey("name") && properties.containsKey("url")) {
							propertiesList.add(properties);
							
							// Select this as active
							active = propertiesList.size()-1;
						}
					} catch (IOException e) {
					}
					
				}
			}
		}
	}

	/**
	 * Getter for the number of properties entries
	 * 
	 * @return
	 * 	The number of properties entries
	 */
	public int size() {
		return propertiesList.size();
	}
	
	/**
	 * Get the name of the parcel service
	 * 
	 * @param i
	 * 		The number of the properties object
	 * @return
	 * 		The name of the parcel service
	 */
	public String getName(int i) {
		return propertiesList.get(i).getProperty("name");
	}

	/**
	 * Get the name of the active parcel service
	 * 
	 * @return
	 * 		The name of the active parcel service
	 */
	public String getName() {
		return propertiesList.get(active).getProperty("name");
	}

	/**
	 * Get the URL of the parcel service
	 * 
	 * @param i
	 * 		The number of the properties object
	 * @return
	 * 		The URL of the parcel service
	 */
	public String getUrl(int i) {
		return propertiesList.get(i).getProperty("url");
	}

	/**
	 * Get the URL of the active parcel service
	 * 
	 * @return
	 * 		The URL of the active parcel service
	 */
	public String getUrl() {
		return propertiesList.get(active).getProperty("url");
	}
	
	/**
	 * Set the active properties object
	 * 
	 * @param i
	 * 		Number of the properties object
	 */
	public void setActive (int i) {
		if ( (i< propertiesList.size()) && (i>=0))
			active = i;
	}
	
	/**
	 * Get the active properties object
	 * 
	 * @return
	 * 		The active properties object
	 */
	public Properties getProperties () {
		return propertiesList.get(active);
	}
	
}
