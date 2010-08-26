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

package com.sebulli.fakturama.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.views.datasettable.TableFilter;

/**
 * Abstract class for all dialogs to select an UniDataSet entry from a table
 * 
 * @author Gerd Bartelt
 */
public abstract class SelectDataSetDialog extends Dialog {
	protected TableViewer tableViewer;
	protected TableColumnLayout tableColumnLayout;
	protected String editor = "";
	protected UniDataSet selectedDataSet = null;
	protected String title = "";

	// Filter the table 
	protected TableFilter tableFilter;
	// The columns that are used for the text search
	protected String searchColumns[];

	/**
	 * Constructor
	 * 
	 * @param parentShell The parent shell
	 */
	protected SelectDataSetDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Constructor 
	 * Creates a new dialog and uses the shell of the active workbench window
	 * 
	 * @param title Title of the new dialog
	 */
	public SelectDataSetDialog(String title) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.title = title;
	}

	/**
	 * Create this part of the dialog are that is common in all the different types
	 * of SelectDataSetDialogs
	 *  
	 *  @param parent The parent composite
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		// Create the top composite dialog area
		Composite top = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(top);

		// Set the title
		this.getShell().setText(title);
		
		// The search composite
		Composite searchComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(searchComposite);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(searchComposite);

		// Search label an search field
		Label searchLabel = new Label(searchComposite, SWT.NONE);
		searchLabel.setText("Suchen:");
		GridDataFactory.swtDefaults().applyTo(searchLabel);
		final Text searchText = new Text(searchComposite, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).hint(150, -1).applyTo(searchText);
		searchText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(searchText.getText());
				tableViewer.refresh();
			}
		});
		
		// Define the SWT layout
		Composite tableComposite = new Composite(top, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);

		// Define the layout of the table
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		// Create the jface table viewer
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		
		// Add a selection change listener
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			/**
			 * Selection Changed method
			 * 
			 * @param event
			 */
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = tableViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					selectedDataSet = (UniDataSet) obj;
				}
			}

		});

		// Add a double click listener.
		// Close the dialog
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			/**
			 * Double click method
			 * 
			 * @param event
			 */
			public void doubleClick(DoubleClickEvent event) {
				close();
			}
		});

		tableFilter = new TableFilter(searchColumns);
		tableViewer.addFilter(tableFilter);

		
		return top;
	}

	/**
	 * Set the initial size of the dialogs in pixel
	 * 
	 * @return Size as Point object
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 550);
	}

	/**
	 * Configures the shell
	 * 
	 * @param newShell The new shell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}

	/**
	 * Get the selected element
	 * 
	 * @return The selected element or null, if none is selected
	 */
	public UniDataSet getSelection() {
		return selectedDataSet;
	}
}