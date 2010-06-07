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

public class NewEditorAction extends Action {
	protected String category = "";
	protected UniDataSet parent = null;
	protected Editor parentEditor = null;

	public NewEditorAction(String text) {
		this(text, null);
	}

	public NewEditorAction(String text, String category) {
		super(text);
		if (category != null)
			this.category = category;
	}

	public NewEditorAction(String text, String category, UniDataSet parent) {
		super(text);
		if (category != null)
			this.category = category;
		this.parent = parent;
	}

	public NewEditorAction(String text, String category, Editor parentEditor) {
		super(text);
		if (category != null)
			this.category = category;
		this.parentEditor = parentEditor;

	}

	public void setCategory(String category) {
		this.category = category;
	}

}
