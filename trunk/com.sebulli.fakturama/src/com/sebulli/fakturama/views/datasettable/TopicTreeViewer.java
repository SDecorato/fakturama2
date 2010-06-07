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

import java.util.ArrayList;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.logger.Logger;

public class TopicTreeViewer extends TreeViewer {

	protected TreeParent root;
	private TreeParent all;
	private TreeParent documentItem;
	private TreeParent contactItem;
	private ViewDataSetTable viewDataSetTable;
	private DataSetArray<?> inputElement;
	private TopicTreeViewer me = this;
	private TreeObject selectedItem;
	final boolean useAll;

	public TopicTreeViewer(Composite parent, int style, boolean useDocumentAndContactFilter, boolean useAll) {
		super(parent, style);
		this.useAll = useAll;
		root = new TreeParent("");
		selectedItem = null;
		if (useAll)
			all = new TreeParent("alle");
		if (useDocumentAndContactFilter) {
			documentItem = new TreeParent("---", "document_10.png");
			contactItem = new TreeParent("---", "contact_10.png");
		}
		if (useAll)
			root.addChild(all);
		this.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				String categoryFilter = "";
				int transactionFilter = -1;
				int contactFilter = -1;
				ISelection selection = event.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					if (obj != null) {
						TreeObject treeObject = (TreeObject) obj;
						categoryFilter = treeObject.getFullPathName();
						transactionFilter = treeObject.getTransactionId();
						contactFilter = treeObject.getContactId();
						selectedItem = treeObject;
					}
				}
				if (contactFilter >= 0)
					viewDataSetTable.setContactFilter(contactFilter);
				else if (transactionFilter >= 0)
					viewDataSetTable.setTransactionFilter(transactionFilter);
				else {
					if (!me.useAll && categoryFilter.isEmpty())
						viewDataSetTable.setCategoryFilter("$shownothing");
					else
						viewDataSetTable.setCategoryFilter(categoryFilter);
				}
			}
		});
	}

	private void clear() {
		if (all != null)
			all.clear();
		root.clear();
		if (documentItem != null)
			root.addChild(documentItem);
		if (contactItem != null)
			root.addChild(contactItem);
		if (all != null)
			root.addChild(all);
		if (inputElement != null)
			inputElement.resetCategoryChanged();
	}

	class TreeObject {
		private String name;
		private String command;
		private TreeParent parent;
		private String icon;
		private int transactionId = -1;
		private int contactId = -1;

		public TreeObject(String name) {
			this.command = null;
			this.name = name;
			this.icon = null;
		}

		public TreeObject(String name, String icon) {
			this.command = null;
			this.name = name;
			this.icon = icon;
		}

		public TreeObject(String name, String command, String icon) {
			this.name = name;
			this.command = command;
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public String getIcon() {
			return icon;
		}

		public String getCommand() {
			return command;
		}

		public int getTransactionId() {
			return this.transactionId;
		}

		public int getContactId() {
			return this.contactId;
		}

		public void setTransactionId(int transactionId) {
			this.transactionId = transactionId;
		}

		public void setContactId(int contactId) {
			this.contactId = contactId;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		@Override
		public String toString() {
			return getName();
		}

		public String getFullPathName() {
			String fullPathName = getName();
			if ((this == root) || (this == all))
				return "";
			TreeObject p = this;
			p = p.getParent();
			while ((p != null) && (p != all) && (p != root)) {
				fullPathName = p.getName() + "/" + fullPathName;
				p = p.getParent();
			}
			return fullPathName;
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}

		public TreeParent(String name, String icon) {
			super(name, icon);
			children = new ArrayList<TreeObject>();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return children.toArray(new TreeObject[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

		public void removeIcon() {
			((TreeObject) this).icon = null;
		}

		public void clear() {
			children.clear();
		}

	}

	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			int entryCnt = 0;

			if (parent == root) {
				if (inputElement.getCategoryStringsChanged()) {
					clear();
					if (inputElement instanceof DataSetArray<?>) {
						Object[] entries = inputElement.getCategoryStrings();
						for (Object entry : entries) {
							addEntry(entry.toString());
						}
					}
				}
			}

			if (inputElement instanceof DataSetArray<?>) {
				Object[] entries = inputElement.getCategoryStrings();
				entryCnt = entries.length;
			}

			if (entryCnt != 0) {
				me.getTree().setVisible(true);
				GridDataFactory.fillDefaults().hint(150, -1).grab(false, true).applyTo(me.getTree());
				me.getTree().getParent().layout(true);
			} else {
				me.getTree().setVisible(false);
				GridDataFactory.fillDefaults().hint(1, -1).grab(false, true).applyTo(me.getTree());
				me.getTree().getParent().layout(true);
			}

			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) { return ((TreeObject) child).getParent(); }
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) { return ((TreeParent) parent).getChildren(); }
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		@Override
		public String getText(Object obj) {
			return obj.toString();
		}

		@Override
		public Image getImage(Object obj) {
			String icon = ((TreeObject) obj).getIcon();
			LocalResourceManager resources = new LocalResourceManager(JFaceResources.getResources());
			if (icon != null) {
				try {
					return resources.createImage(Activator.getImageDescriptor("/icons/10/" + icon));
				} catch (AssertionFailedException e) {
					Logger.logError(e, "Icon not found: " + icon);
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
				}

			}
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent) { return resources.createImage(Activator.getImageDescriptor("/icons/10/dot_10.png")); }
			imageKey = null;// ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	private TreeParent addEntry(String entry) {
		String[] entryParts = entry.split("/");
		TreeParent me;
		if (all != null)
			me = all;
		else
			me = root;

		TreeParent newMe;
		boolean found;
		for (String entryPart : entryParts) {
			found = false;

			if (me.hasChildren()) {
				for (TreeObject treeObject : me.children) {
					if (treeObject.getName().equalsIgnoreCase(entryPart)) {
						me = (TreeParent) treeObject;
						found = true;
						break;
					}
				}
			}

			if (!found) {
				me.addChild(newMe = new TreeParent(entryPart));
				me = newMe;
			}
		}
		return me;
	}

	public void setTransaction(String name, int transactionId) {
		if (documentItem == null)
			return;
		documentItem.setTransactionId(transactionId);
		documentItem.setName("Vorgang");
		refresh();
	}

	public String getSelectedItemName() {
		if (selectedItem != null)
			return selectedItem.getFullPathName();
		else
			return "";
	}

	public void selectItemByName(String name) {
		boolean found = false;
		boolean allScanned = true;
		String[] nameParts = name.split("/");

		boolean childfound = false;
		TreeItem newParent = null;
		TreeItem[] children;
		children = me.getTree().getItems();

		for (String namePart : nameParts) {
			found = true;
			if (children.length == 0)
				allScanned = false;
			for (TreeItem item : children) {
				if (item.getText().equals(namePart)) {
					childfound = true;
					newParent = item;
				}
			}
			if (!childfound)
				found = false;
			if (newParent != null)
				children = newParent.getItems();
		}

		if (found && allScanned) {
			me.getTree().setSelection(newParent);
			me.setSelection(me.getSelection(), true);
		}
		viewDataSetTable.setCategoryFilter(name);
	}

	public void setContact(String name, int contactId) {
		if (contactItem == null)
			return;
		contactItem.setContactId(contactId);
		contactItem.setName(name);
		refresh();
	}

	public void setInput(DataSetArray<?> input) {
		this.inputElement = input;
		this.inputElement.resetCategoryChanged();
		this.setContentProvider(new ViewContentProvider());
		this.setLabelProvider(new ViewLabelProvider());
		this.setInput(root);
		this.expandToLevel(2);
	}

	public void setTable(ViewDataSetTable viewDataSetTable) {
		this.viewDataSetTable = viewDataSetTable;
	}

}
