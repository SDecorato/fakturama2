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

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.editors.DocumentEditor;
import com.sebulli.fakturama.editors.Editor;
import com.sebulli.fakturama.editors.UniDataSetEditorInput;
import com.sebulli.fakturama.logger.Logger;

/**
 * This action creates a new contact in an editor.
 *  
 * @author Gerd Bartelt
 */
public class NewDocumentAction extends NewEditorAction {
	private int iconSize = 16;

	/**
	 * Default Constructor with no parameters.
	 * If no parameters are set, an order document is created.
	 */
	public NewDocumentAction() {
		super("neues Dokument");
		category = DocumentType.ORDER.getString();
		setText(DocumentType.ORDER.getString());
		setSettings(ICommandIds.CMD_NEW_ORDER, "");
	}

	/**
	 * Constructor
	 * Creates an Action with default icon size of 16x16 pixel
	 * 
	 * @param documentType Type of document to create
	 */
	public NewDocumentAction(DocumentType documentType) {
		super("");
		this.iconSize = 16;
		setDocumentType(documentType);
	}

	/**
	 * Constructor
	 * Creates an Action with default icon size of 16x16 pixel
	 * 
	 * @param documentType Type of document to create
	 * @param editor Parent editor. The Editors content is saved and duplicated. 
	 * @param iconSize Size of icon (16, 32 or 48)
	 */
	public NewDocumentAction(DocumentType documentType, Editor editor, int iconSize) {
		super("", null, editor);
		this.iconSize = iconSize;
		setDocumentType(documentType);
	}
	
	/**
	 * Sets Command ID and icon name of this action
	 * 
	 * @param cmd Command ID
	 * @param image Icon name 
	 */
	private void setSettings(String cmd, String image) {
		setId(cmd);
		setActionDefinitionId(cmd);
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor(image));
	}
	
	/**
	 * Sets Document Type and generates icon name
	 * 
	 * @param documentType
	 */
	private void setDocumentType(DocumentType documentType) {
		category = documentType.getString();
		setText(documentType.getString());
		String iconSizeString = "_" + Integer.toString(iconSize);
		if (iconSize == 32)
			iconSizeString = "_new" + iconSizeString;
		setSettings(ICommandIds.CMD_NEW_ + documentType.getTypeAsString(), "/icons/" + Integer.toString(iconSize) + "/"
				+ documentType.getTypeAsString().toLowerCase() + iconSizeString + ".png");
	}

	/**
	 * Run the action
	 * If a parent editor is set: Save the content and duplicate it.
	 * 
	 * Open a new document editor.
	 */
	@Override
	public void run() {

		// cancel, if the data base is not opened.
		if (!Data.INSTANCE.getDataBaseOpened())
			return;

		// Does a parent editor exist ?
		if (parentEditor != null) {
			
			//if yes and if it was an Document Editor ...
			if (parentEditor instanceof DocumentEditor) {
				
				// Mark parent document, save it and use it as base
				// for a new document editor.
				((DocumentEditor) parentEditor).childDocumentGenerated();
				parentEditor.doSave(null);
				parent = ((DocumentEditor) parentEditor).getDocument();
			}
		}
		
		// Set the editors input
		UniDataSetEditorInput input = new UniDataSetEditorInput(category, parent);
		
		// Open the editor
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, DocumentEditor.ID);
		} catch (PartInitException e) {
			Logger.logError(e, "Error opening Editor: " + DocumentEditor.ID);
		}
	}
}