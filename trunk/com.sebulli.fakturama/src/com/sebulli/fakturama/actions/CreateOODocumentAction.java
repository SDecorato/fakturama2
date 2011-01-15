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

package com.sebulli.fakturama.actions;

import static com.sebulli.fakturama.Translate._;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.Workspace;
import com.sebulli.fakturama.data.DataBaseConnectionState;
import com.sebulli.fakturama.editors.DocumentEditor;
import com.sebulli.fakturama.editors.Editor;
import com.sebulli.fakturama.openoffice.OOManager;
import com.sebulli.fakturama.openoffice.OOTemplateFilename;

/**
 * This action starts the OpenOffice exporter. If there is more than one
 * template, a menu appears and the user can select the template.
 * 
 * @author Gerd Bartelt
 */
public class CreateOODocumentAction extends Action {

	private ArrayList<OOTemplateFilename> templates = new ArrayList<OOTemplateFilename>();
	private OOTemplateFilename template;
	private DocumentEditor documentEditor;

	/**
	 * default constructor
	 */
	public CreateOODocumentAction() {
		this(_("Print as OO document"), _("Print/Export this document as an OpenOffice Writer document"));
	}

	/**
	 * constructor
	 * 
	 * @param text
	 *            Action text
	 * @param toolTipText
	 *            Tool tip text
	 */
	public CreateOODocumentAction(String text, String toolTipText) {
		super(text);
		setToolTipText(toolTipText);
		setId(ICommandIds.CMD_CREATE_OODOCUMENT);
		setActionDefinitionId(ICommandIds.CMD_CREATE_OODOCUMENT);
		setImageDescriptor(com.sebulli.fakturama.Activator.getImageDescriptor("/icons/32/oowriter_32.png"));
	}

	/**
	 * Scans the template path for all templates. If a template exists, add it
	 * to the list of available templates
	 * 
	 * @param templatePath
	 *            path which is scanned
	 */
	private void scanPathForTemplates(String templatePath) {
		File dir = new File(templatePath);
		String[] children = dir.list();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				OOTemplateFilename oOTemplateFilename = new OOTemplateFilename(templatePath, children[i]);
				if (oOTemplateFilename.getExtension().equalsIgnoreCase(".ott"))
					templates.add(oOTemplateFilename);
			}
		}
	}

	/**
	 * Run the action Search for all available templates. If there is more than
	 * one, display a menu to select one template. The content of the editor is
	 * saved before exporting it.
	 */
	@Override
	public void run() {

		// cancel, if the data base is not connected.
		if (!DataBaseConnectionState.INSTANCE.isConnected())
			return;

		Editor editor = (Editor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null)
			if (editor instanceof DocumentEditor) {

				// Search in the folder "Templates" and also in the folder with the localized  name
				documentEditor = (DocumentEditor) editor;

				// Exit, if there is a document with the same number
				if (documentEditor.thereIsOneWithSameNumber())
					return;

				String workspace = Activator.getDefault().getPreferenceStore().getString("GENERAL_WORKSPACE");
				String templatePath1 = workspace + "/Templates/" + documentEditor.getDocumentType().getTypeAsString() + "/";
				String templatePath2 = workspace + "/" + Workspace.INSTANCE.getTemplateFolderName() + "/" + documentEditor.getDocumentType().getString() + "/";

				// Clear the list before adding new entries
				templates.clear();

				// If the name of the localized folder is equal to "Templates", don't search 2 times.
				scanPathForTemplates(templatePath1);
				if (!templatePath1.equals(templatePath2))
					scanPathForTemplates(templatePath2);

				// If more than 1 template is found, show a pup up menu
				if (templates.size() > 1) {
					Menu menu = new Menu(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP);
					for (int i = 0; i < templates.size(); i++) {
						template = templates.get(i);
						MenuItem item = new MenuItem(menu, SWT.PUSH);
						item.setText(template.getName());
						item.setData(template.getPathAndFilename());
						item.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event e) {
								// save the document and open the exporter
								documentEditor.doSave(null);
								OOManager.INSTANCE.openOODocument(documentEditor.getDocument(), (String) e.widget.getData());
							}
						});
					}

					// Set the location of the pup up menu near to the upper left corner,
					// but with an gap, so it should be under the tool bar icon of this action.
					int x = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getLocation().x;
					int y = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getLocation().y;
					menu.setLocation(x + 80, y + 80);
					menu.setVisible(true);

				}
				else if (templates.size() == 1) {
					// Save the document and open the exporter
					documentEditor.doSave(null);
					OOManager.INSTANCE.openOODocument(documentEditor.getDocument(), templates.get(0).getPathAndFilename());
				}
			}
	}
}
