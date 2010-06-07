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
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.editors.DocumentEditor;
import com.sebulli.fakturama.editors.Editor;
import com.sebulli.fakturama.editors.UniDataSetEditorInput;
import com.sebulli.fakturama.logger.Logger;

public class NewDocumentAction extends NewEditorAction {
	private int iconSize = 16;

	public NewDocumentAction() {
		super("neues Dokument", null);
		category = DocumentType.ORDER.getString();
		setText(DocumentType.ORDER.getString());
		setSettings(ICommandIds.CMD_NEW_ORDER, "");
	}

	private void setSettings(String cmd, String image) {
		setId(cmd);
		setActionDefinitionId(cmd);
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor(image));
	}

	public NewDocumentAction(DocumentType documentType) {
		this(documentType, (UniDataSet) null, 16);
	}

	private void setDocumentType(DocumentType documentType) {
		category = documentType.getString();
		setText(documentType.getString());
		String iconSizeString = "_" + Integer.toString(iconSize);
		if (iconSize == 32)
			iconSizeString = "_new" + iconSizeString;
		setSettings(ICommandIds.CMD_NEW_ + documentType.getTypeAsString(), "/icons/" + Integer.toString(iconSize) + "/"
				+ documentType.getTypeAsString().toLowerCase() + iconSizeString + ".png");
	}

	public NewDocumentAction(DocumentType documentType, Editor editor, int iconSize) {
		super("", null, editor);
		this.iconSize = iconSize;
		setDocumentType(documentType);
	}

	public NewDocumentAction(DocumentType documentType, UniDataSet parent, int iconSize) {
		super("", null, parent);
		this.iconSize = iconSize;
		setDocumentType(documentType);
	}

	@Override
	public void run() {
		if (!Data.INSTANCE.getDataBaseOpened())
			return;

		if (parentEditor != null) {
			if (parentEditor instanceof DocumentEditor) {
				((DocumentEditor) parentEditor).childDocumentGenerated();
			}
			parentEditor.doSave(null);
			parent = ((DocumentEditor) parentEditor).getDocument();
		}

		UniDataSetEditorInput input = new UniDataSetEditorInput(category, parent);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, DocumentEditor.ID);

		} catch (PartInitException e) {
			Logger.logError(e, "Error opening Editor: " + DocumentEditor.ID);
		}
	}
}