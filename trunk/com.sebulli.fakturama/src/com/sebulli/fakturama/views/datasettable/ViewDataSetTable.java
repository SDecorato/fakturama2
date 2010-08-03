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

package com.sebulli.fakturama.views.datasettable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.actions.DeleteDataSetAction;
import com.sebulli.fakturama.actions.NewEditorAction;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.logger.Logger;

/**
 * This is the abstract parent class for all views that show a table with
 * UniDataSets and a tree viewer
 * 
 * @author Gerd Bartelt
 *
 */
public abstract class ViewDataSetTable extends ViewPart {

	// The table with the UniDataSets
	protected TableViewer tableViewer;
	protected TableColumnLayout tableColumnLayout;
	protected ViewDataSetTableContentProvider contentProvider;
	protected UniDataSetTableColumn stdIconColumn = null;
	
	// The columns that are used for the text search
	protected String searchColumns[];

	// Filter the table 
	protected TableFilter tableFilter;
	protected Label filterLabel;
	
	// The topic tree viewer displays the categories of the UniDataSets
	protected TopicTreeViewer topicTreeViewer;

	// Name of the editor to edit the UniDataSets
	protected String editor = "";
	
	// Action to create new dataset in the editor
	protected NewEditorAction addNewAction = null;

	// Menu manager of the context menu
	protected MenuManager menuManager;
	
	// The standard UniDataSet
	protected String stdPropertyKey = null;


	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent, boolean useDocumentAndContactFilter, boolean useAll) {
		
		// Create the top composite
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(top);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).applyTo(top);

		// Create the tree viewer
		topicTreeViewer = new TopicTreeViewer(top, SWT.BORDER, useDocumentAndContactFilter, useAll);
		GridDataFactory.swtDefaults().hint(10, -1).applyTo(topicTreeViewer.getTree());

		// Create the composite that contains the search field and the table
		Composite searchAndTableComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).applyTo(searchAndTableComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(searchAndTableComposite);

		// Create the composite that contains the search field and the toolbar
		Composite searchAndToolbarComposite = new Composite(searchAndTableComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(searchAndToolbarComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(searchAndToolbarComposite);

		// The toolbar 
		ToolBar toolBar = new ToolBar(searchAndToolbarComposite, SWT.FLAT);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(toolBar);
		ToolBarManager tbm = new ToolBarManager(toolBar);

		if (addNewAction != null) {
			addNewAction.setImageDescriptor(Activator.getImageDescriptor("/icons/16/plus_16.png"));
			tbm.add(addNewAction);
		}
		tbm.add(new DeleteDataSetAction());
		tbm.update(true);

		// The filter label
		filterLabel = new Label(searchAndToolbarComposite, SWT.NONE);
		FontData[] fD = filterLabel.getFont().getFontData();
		fD[0].setHeight(20);
		Font font = new Font(null, fD[0]);
		filterLabel.setFont(font);
		font.dispose();
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).applyTo(filterLabel);

		// The search composite
		Composite searchComposite = new Composite(searchAndToolbarComposite, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(searchComposite);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.END, SWT.CENTER).applyTo(searchComposite);

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

		// The table composite
		Composite tableComposite = new Composite(searchAndTableComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);

		// Set the table layout
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		// Create a table viewer
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);

		// Set selection provider
		getSite().setSelectionProvider(tableViewer);
		
		// Listen to double clicks
		hookDoubleClickCommand();

		// Set the content provider
		contentProvider = new ViewDataSetTableContentProvider(tableViewer);
		tableViewer.setContentProvider(contentProvider);

		// Set the table
		topicTreeViewer.setTable(this);
		
		// Set sorter and filter
		tableViewer.setSorter(new TableSorter());
		tableFilter = new TableFilter(searchColumns);
		tableViewer.addFilter(tableFilter);

	}

	/**
	 * Returns the topic tree viewer
	 * 
	 * @return The topic tree viewer
	 */
	public TopicTreeViewer getTopicTreeViewer() {
		return topicTreeViewer;
	}

	/**
	 * Create the menu manager for the context menu
	 */
	protected void createMenuManager() {
		menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		tableViewer.getTable().setMenu(menuManager.createContextMenu(tableViewer.getTable()));

		getSite().registerContextMenu("com.sebulli.fakturama.views.datasettable.popup", menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);

	}

	/**
	 * Create the default context menu with one addNew and one Delete action
	 */
	protected void createDefaultContextMenu() {
		createMenuManager();
		if (addNewAction != null)
			menuManager.add(addNewAction);
		menuManager.add(new DeleteDataSetAction());
	}

	/**
	 * On double click: open the corresponding editor
	 */
	private void hookDoubleClickCommand() {

		// Add a double click listener
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				ICommandService commandService = (ICommandService) getSite().getService(ICommandService.class);

				try {
					
					// Call the corresponding editor. The editor is set
					// in the variable "editor", which is used as a parameter
					// when calling the editor command.
					Command callEditor = commandService.getCommand("com.sebulli.fakturama.editors.callEditor");
					Map<String, String> params = new HashMap<String, String>();
					params.put("com.sebulli.fakturama.editors.callEditorParameter", editor);
					ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(callEditor, params);
					handlerService.executeCommand(parameterizedCommand, null);

				} catch (Exception e) {
					Logger.logError(e, "Editor not found: " + editor);
				}
			}
		});
	}

	/**
	 * Refresh the table and the tree viewer
	 */
	public void refresh() {
		
		// Refresh the standard entry
		refreshStdId();

		// Refresh the table
		if (tableViewer != null)
			tableViewer.refresh();

		// Refresh the tree viewer
		if (topicTreeViewer != null) {
			topicTreeViewer.refresh();
		}
	}

	/**
	 * Asks this part to take focus within the workbench. 
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();

	}

	/**
	 * Set the category filter
	 * @param filter The new filter string
	 */
	public void setCategoryFilter(String filter) {

		// Set the label with the filter string
		if (filter.equals("$shownothing"))
			filterLabel.setText("");
		else
			filterLabel.setText(filter);
		filterLabel.pack(true);
		
		// Reset transaction and contact filter, set category filter
		contentProvider.setTransactionFilter(-1);
		contentProvider.setContactFilter(-1);
		contentProvider.setCategoryFilter(filter);

		// Set category to the addNew action. So a new data set is created
		// with the selected category
		if (addNewAction != null) {
			addNewAction.setCategory(filter);
		}
		
		//Refresh
		this.refresh();
	}

	/**
	 * Set the transaction filter
	 * @param filter The new filter string
	 */
	public void setTransactionFilter(int filter) {

		// Set the label with the filter string
		filterLabel.setText("Dieser Vorgang");
		filterLabel.pack(true);

		// Reset category and contact filter, set transaction filter
		contentProvider.setTransactionFilter(filter);
		contentProvider.setContactFilter(-1);
		contentProvider.setCategoryFilter("");

		// Reset the addNew action. 
		if (addNewAction != null) {
			addNewAction.setCategory("");
		}
		this.refresh();
	}

	/**
	 * Set the contact filter
	 * @param filter The new filter string
	 */
	public void setContactFilter(int filter) {

		// Set the label with the filter string
		filterLabel.setText(Data.INSTANCE.getContacts().getDatasetById(filter).getName());
		filterLabel.pack(true);

		// Reset transaction and category filter, set contact filter
		contentProvider.setContactFilter(filter);
		contentProvider.setTransactionFilter(-1);
		contentProvider.setCategoryFilter("");
		
		// Reset the addNew action. 
		if (addNewAction != null) {
			addNewAction.setCategory("");
		}

		this.refresh();
	}

	/**
	 * Refresh the standard ID. Sets the new standard ID to the standard
	 * icon column of the table
	 */
	public void refreshStdId() {

		if (stdPropertyKey == null)
			return;
		if (stdIconColumn == null)
			return;

		try {
			// Set the the new standard ID to the standard icon column
			stdIconColumn.setStdEntry(Integer.parseInt(Data.INSTANCE.getProperty(stdPropertyKey)));
		} catch (NumberFormatException e) {
		}

	}
}
