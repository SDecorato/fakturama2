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

package com.sebulli.fakturama;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

import com.sebulli.fakturama.views.Calculator;
import com.sebulli.fakturama.views.ErrorView;
import com.sebulli.fakturama.views.NavigationView;
import com.sebulli.fakturama.views.datasettable.ViewContactTable;
import com.sebulli.fakturama.views.datasettable.ViewDocumentTable;
import com.sebulli.fakturama.views.datasettable.ViewPaymentTable;
import com.sebulli.fakturama.views.datasettable.ViewProductTable;
import com.sebulli.fakturama.views.datasettable.ViewShippingTable;
import com.sebulli.fakturama.views.datasettable.ViewTextTable;
import com.sebulli.fakturama.views.datasettable.ViewVatTable;

/**
 * This is the default (and the only) perspective in the Fakturama project.
 * 
 * @author Gerd Bartelt
 */
public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "com.sebulli.fakturama.perspective";

	 /** bottom folder's id. */
    public static final String ID_BOTTOM = "com.sebulli.fakturama.perspective.bottomFolder";
 
	
	/**
	 * Creates the initial layout of the perspective.
	 * The Navigation view and the error view on the left side.
	 * The Table views under the editor area.
	 * The calculator on the right side of the editor.
	 * 
	 * @param layout Page layout
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		layout.addStandaloneView(NavigationView.ID, false, IPageLayout.LEFT, 0.2f, editorArea);
		layout.getViewLayout(NavigationView.ID).setCloseable(false);
		layout.addPlaceholder(ErrorView.ID, IPageLayout.BOTTOM, 0.7f, NavigationView.ID);

		
		
		layout.createPlaceholderFolder(ID_BOTTOM, IPageLayout.BOTTOM, 0.6f, editorArea);
		
//		IPlaceholderFolderLayout folder = layout.createPlaceholderFolder(ID_BOTTOM, IPageLayout.BOTTOM, 0.6f, editorArea);
/*		folder.addPlaceholder(ViewContactTable.ID);
		folder.addPlaceholder(ViewProductTable.ID);
		folder.addPlaceholder(ViewVatTable.ID);
		folder.addPlaceholder(ViewDocumentTable.ID);
		folder.addPlaceholder(ViewShippingTable.ID);
		folder.addPlaceholder(ViewPaymentTable.ID);
		folder.addPlaceholder(ViewTextTable.ID);*/

/*		layout.addPlaceholder(ViewContactTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addPlaceholder(ViewProductTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addPlaceholder(ViewVatTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addPlaceholder(ViewDocumentTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addPlaceholder(ViewShippingTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addPlaceholder(ViewPaymentTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addPlaceholder(ViewTextTable.ID, IPageLayout.BOTTOM, 0.6f, editorArea);*/

/*		layout.addStandaloneView(ViewContactTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addStandaloneView(ViewProductTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addStandaloneView(ViewVatTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addStandaloneView(ViewDocumentTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addStandaloneView(ViewShippingTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addStandaloneView(ViewPaymentTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);
		layout.addStandaloneView(ViewTextTable.ID, true, IPageLayout.BOTTOM, 0.6f, editorArea);*/
		
		layout.addPlaceholder(Calculator.ID, IPageLayout.RIGHT, 0.7f, editorArea);
	}
}
