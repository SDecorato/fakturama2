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
import org.eclipse.jface.action.IAction;
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
import com.sebulli.fakturama.views.TemporaryViews;

public abstract class ViewDataSetTable extends ViewPart {

	protected static final IAction newEntry = null;
	protected TableViewer tableViewer;
	protected TopicTreeViewer topicTreeViewer;
	protected TableColumnLayout tableColumnLayout;
	protected String editor = "";
	protected String searchColumns[];
	protected NewEditorAction addNewAction = null;
	protected ViewDataSetTableContentProvider contentProvider;
	protected MenuManager menuManager;
	protected UniDataSetTableColumn stdIconColumn = null;
	protected String stdPropertyKey = null;
	protected TableFilter tableFilter;
	protected Label filterLabel;

	public void createPartControl(Composite parent, boolean useDocumentAndContactFilter, boolean useAll) {

		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(top);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).applyTo(top);

		topicTreeViewer = new TopicTreeViewer(top, SWT.BORDER, useDocumentAndContactFilter, useAll);
		GridDataFactory.swtDefaults().hint(10, -1).applyTo(topicTreeViewer.getTree());

		Composite searchAndTableComposite = new Composite(top, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).applyTo(searchAndTableComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(searchAndTableComposite);

		Composite searchAndToolbarComposite = new Composite(searchAndTableComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(searchAndToolbarComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(searchAndToolbarComposite);

		Composite toolbarComposite = new Composite(searchAndToolbarComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(toolbarComposite);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BOTTOM).applyTo(toolbarComposite);

		filterLabel = new Label(searchAndToolbarComposite, SWT.BORDER);
		FontData[] fD = filterLabel.getFont().getFontData();
		fD[0].setHeight(20);
		Font font = new Font(null, fD[0]);
		filterLabel.setFont(font);
		font.dispose();
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).applyTo(filterLabel);

		Composite searchComposite = new Composite(searchAndToolbarComposite, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(searchComposite);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.END, SWT.BOTTOM).applyTo(searchComposite);

		ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BOTTOM).applyTo(toolBar);
		ToolBarManager tbm = new ToolBarManager(toolBar);

		if (addNewAction != null) {
			addNewAction.setImageDescriptor(Activator.getImageDescriptor("/icons/16/plus_16.png"));
			tbm.add(addNewAction);
		}
		tbm.add(new DeleteDataSetAction());
		tbm.update(true);

		Label searchLabel = new Label(searchComposite, SWT.NONE);
		searchLabel.setText("Suchen:");
		GridDataFactory.swtDefaults().applyTo(searchLabel);
		final Text searchText = new Text(searchComposite, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.BOTTOM).hint(150, -1).applyTo(searchText);
		searchText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(searchText.getText());
				tableViewer.refresh();
			}
		});

		Composite tableComposite = new Composite(searchAndTableComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);

		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);

		getSite().setSelectionProvider(tableViewer);
		hookDoubleClickCommand();
		// hookSelect();
		contentProvider = new ViewDataSetTableContentProvider(tableViewer);
		tableViewer.setContentProvider(contentProvider);
		// tableViewer.setLabelProvider(new ViewDataSetTableLabelProvider());

		topicTreeViewer.setTable(this);
		tableViewer.setSorter(new TableSorter());
		tableFilter = new TableFilter(searchColumns);
		tableViewer.addFilter(tableFilter);
	}

	public TopicTreeViewer getTopicTreeViewer() {
		return topicTreeViewer;
	}

	protected void createMenuManager() {
		menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		tableViewer.getTable().setMenu(menuManager.createContextMenu(tableViewer.getTable()));

		getSite().registerContextMenu("com.sebulli.fakturama.views.datasettable.popup", menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);

	}

	protected void createDefaultContextMenu() {
		createMenuManager();
		if (addNewAction != null)
			menuManager.add(addNewAction);
		menuManager.add(new DeleteDataSetAction());
	}

	private void hookDoubleClickCommand() {

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				ICommandService commandService = (ICommandService) getSite().getService(ICommandService.class);

				try {

					Command callEditor = commandService.getCommand("com.sebulli.fakturama.editors.callEditor");
					Map<String, String> params = new HashMap<String, String>();
					params.put("com.sebulli.fakturama.editors.callEditorParameter", editor);
					ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(callEditor, params);
					handlerService.executeCommand(parameterizedCommand, null);
					// handlerService.executeCommand(editor, ev);
				} catch (Exception e) {
					Logger.logError(e, "Editor not found: " + editor);
				}
			}
		});
	}

	public void refresh() {
		refreshStdId();

		if (tableViewer != null)
			tableViewer.refresh();

		if (topicTreeViewer != null) {
			topicTreeViewer.refresh();
		}
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();

	}

	public void setCategoryFilter(String filter) {
		if (filter.equals("$shownothing"))
			filterLabel.setText("");
		else
			filterLabel.setText(filter);
		
		filterLabel.pack(true);
		contentProvider.setTransactionFilter(-1);
		contentProvider.setContactFilter(-1);
		contentProvider.setCategoryFilter(filter);
		if (addNewAction != null) {
			addNewAction.setCategory(filter);
		}
		this.refresh();
	}

	public void setTransactionFilter(int filter) {
		filterLabel.setText("Dieser Vorgang");
		filterLabel.pack(true);

		contentProvider.setTransactionFilter(filter);
		contentProvider.setContactFilter(-1);
		contentProvider.setCategoryFilter("");
		if (addNewAction != null) {
			addNewAction.setCategory("");
		}
		this.refresh();
	}

	public void setContactFilter(int filter) {
		filterLabel.setText(Data.INSTANCE.getContacts().getDatasetById(filter).getName());
		filterLabel.pack(true);

		contentProvider.setContactFilter(filter);
		contentProvider.setTransactionFilter(-1);
		contentProvider.setCategoryFilter("");
		if (addNewAction != null) {
			addNewAction.setCategory("");
		}
		this.refresh();
	}

	public void refreshStdId() {

		if (stdPropertyKey == null)
			return;
		if (stdIconColumn == null)
			return;

		try {
			stdIconColumn.setStdEntry(Integer.parseInt(Data.INSTANCE.getProperty(stdPropertyKey)));
		} catch (NumberFormatException e) {
		}

	}

	@Override
	public void dispose() {
		TemporaryViews.INSTANCE.unMinimizeEditorPart();
		super.dispose();
	}

}
