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

import org.eclipse.jface.action.Action;

import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.editors.Editor;

/**
 * Parent class for all newXX actions. 
 * 
 * Stores the information about the category or the parent editor.
 *  
 * @author Gerd Bartelt
 */
public class NewEditorAction extends Action {
	
	// category String
	protected String category = "";
	
	// Parent UniDataSet
	protected UniDataSet parent = null;
	
	// Parent Editor
	protected Editor parentEditor = null;

	/**
	 * Default constructor
	 * 
	 * @param text Name of the action
	 */
	public NewEditorAction(String text) {
		super(text);
	}

	/**
	 * Constructor with additional parameter "category"
	 * 
	 * @param text Name of the action
	 * @param category Category of the new action
	 */
	public NewEditorAction(String text, String category) {
		super(text);
		if (category != null)
			this.category = category;
	}


	/**
	 * Constructor with a 2nd additional parameter "parentEditor"
	 * 
	 * @param text Name of the action
	 * @param category Category of the new action
	 * @param parentEditor Parent editor, which is duplicated
	 */
	public NewEditorAction(String text, String category, Editor parentEditor) {
		super(text);
		if (category != null)
			this.category = category;
		this.parentEditor = parentEditor;

	}

	/**
	 * Setter for the property "category"
	 * 
	 * @param category The category of the new action.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

}
