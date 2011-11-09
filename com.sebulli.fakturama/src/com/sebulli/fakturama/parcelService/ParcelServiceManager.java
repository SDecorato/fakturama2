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

public class ParcelServiceManager {
	
	private ArrayList<Properties> propertiesList;
	private int active = -1;
	
	public ParcelServiceManager (String templatePath) {
		
		propertiesList = new ArrayList<Properties>();
		
		File dir = new File(templatePath);
		String[] children = dir.list();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				TemplateFilename templateFilename = new TemplateFilename(templatePath, children[i]);
				if (templateFilename.getExtension().equalsIgnoreCase(".txt")) {
					Properties properties = new Properties();
					BufferedInputStream stream;
					try {
						stream = new BufferedInputStream(new FileInputStream(templateFilename.getPathAndFilename()));
						properties.load(stream);
						stream.close();
						if (properties.containsKey("name") && properties.containsKey("url")) {
							propertiesList.add(properties);
							active = propertiesList.size()-1;
						}
					} catch (IOException e) {
					}
					
				}
			}
		}
	}

	public int size() {
		return propertiesList.size();
	}
	
	public String getName(int i) {
		return propertiesList.get(i).getProperty("name");
	}

	public String getName() {
		return propertiesList.get(active).getProperty("name");
	}

	public String getUrl(int i) {
		return propertiesList.get(i).getProperty("url");
	}

	public String getUrl() {
		return propertiesList.get(active).getProperty("url");
	}
	
	public void setActive (int i) {
		if ( (i< propertiesList.size()) && (i>=0))
			active = i;
	}
	
	public Properties getProperties () {
		return propertiesList.get(active);
	}
	
}
