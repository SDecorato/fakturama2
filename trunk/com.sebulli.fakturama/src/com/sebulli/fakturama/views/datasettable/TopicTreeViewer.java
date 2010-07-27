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

/**
 * This is the topic tree viewer that displays an tree of the
 * categories of all the data sets. 
 * 
 * @author Gerd Bartelt
*/
public class TopicTreeViewer extends TreeViewer {

	private TopicTreeViewer me = this;

	protected TreeParent root;
	private TreeParent all;

	// Display a transaction item, only if it is a tree of documents
	private TreeParent transactionItem;
	// Display a contact item, only if it is a tree of documents
	private TreeParent contactItem;

	// The input
	private DataSetArray<?> inputElement;

	// The selected item
	private TreeObject selectedItem;
	
	// True, if there is a entry "show all"
	final boolean useAll;

	// The corresponding table
	private ViewDataSetTable viewDataSetTable;

	/**
	 * Constructor
	 * Creates a 
	 * 
	 * @param parent
	 * @param style
	 * @param useDocumentAndContactFilter
	 * @param useAll
	 */
	public TopicTreeViewer(Composite parent, int style, boolean useDocumentAndContactFilter, boolean useAll) {
		super(parent, style);
		this.useAll = useAll;
		
		// Create a new root element
		root = new TreeParent("");
		// select nothing
		selectedItem = null;
		
		// Add a "show all" entry
		if (useAll) {
			all = new TreeParent("alle");
			root.addChild(all);
		}
		
		// Add a transaction and contact entry
		if (useDocumentAndContactFilter) {
			transactionItem = new TreeParent("---", "document_10.png");
			contactItem = new TreeParent("---", "contact_10.png");
		}
		
		// If an element of the tree is selected, update the filter
		this.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				// Update the category, transaction and contact filter
				String categoryFilter = "";
				int transactionFilter = -1;
				int contactFilter = -1;
				ISelection selection = event.getSelection();
				
				// Get the selection
				if (selection != null && selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					if (obj != null) {

						// Get the selected object
						TreeObject treeObject = (TreeObject) obj;
						selectedItem = treeObject;

						// Update the category, transaction and contact filter
						categoryFilter = treeObject.getFullPathName();
						transactionFilter = treeObject.getTransactionId();
						contactFilter = treeObject.getContactId();
					}
				}
				
				if (contactFilter >= 0)

					// Set the contact filter
					viewDataSetTable.setContactFilter(contactFilter);
				
				else if (transactionFilter >= 0)
				
					// Set the transaction filter
					viewDataSetTable.setTransactionFilter(transactionFilter);
				
				else {
					if (!me.useAll && categoryFilter.isEmpty())
						// Show nothing
						viewDataSetTable.setCategoryFilter("$shownothing");
					else
						// Set the category filter
						viewDataSetTable.setCategoryFilter(categoryFilter);
				}
			}
		});
	}

	/**
	 * Clear the tree
	 */
	private void clear() {
		if (all != null)
			all.clear();
		
		root.clear();
		
		// Add the transaction item
		if (transactionItem != null)
			root.addChild(transactionItem);
		
		// Add the contact item
		if (contactItem != null)
			root.addChild(contactItem);
		
		// Add the "show all" item
		if (all != null)
			root.addChild(all);
		
		// Reset the marker for "category has changed"
		if (inputElement != null)
			inputElement.resetCategoryChanged();
	}

	/**
	 * This class represents a tree object of the Topic Tree
	 *
	 * @author Gerd Bartelt
	 */
	class TreeObject {
		private String name;
		private String command;
		private TreeParent parent;
		private String icon;
		private int transactionId = -1;
		private int contactId = -1;

		/**
		 * Constructor
		 * Create a tree object by name
		 * 
		 * @param name Name of the tree object
		 */
		public TreeObject(String name) {
			this.command = null;
			this.name = name;
			this.icon = null;
		}

		/**
		 * Constructor
		 * Create a tree object by name and icon
		 * 
		 * @param name Name of the tree object
		 * @param icon Icon of the tree object
		 */
		public TreeObject(String name, String icon) {
			this.command = null;
			this.name = name;
			this.icon = icon;
		}

		/**
		 * Constructor
		 * Create a tree object by name and icon
		 * 
		 * @param name Name of the tree object
		 * @param command of the tree object
		 * @param icon Icon of the tree object
		 */
		public TreeObject(String name, String command, String icon) {
			this.name = name;
			this.command = command;
			this.icon = icon;
		}

		/**
		 * Returns the name of the tree object
		 * @return The name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the icon of the tree object
		 * @return The icon
		 */
		public String getIcon() {
			return icon;
		}

		/**
		 * Returns the command of the tree object
		 * @return The command
		 */
		public String getCommand() {
			return command;
		}

		/**
		 * Returns the ID of the transaction
		 * @return Transaction ID
		 */
		public int getTransactionId() {
			return this.transactionId;
		}

		/**
		 * Returns the ID of the contact
		 * @return Contact ID
		 */
		public int getContactId() {
			return this.contactId;
		}

		/**
		 * Sets the Transaction ID
		 * @param transactionId ID to set
		 */
		public void setTransactionId(int transactionId) {
			this.transactionId = transactionId;
		}

		/**
		 * Sets the contact ID
		 * @param contactId ID to set
		 */
		public void setContactId(int contactId) {
			this.contactId = contactId;
		}

		/**
		 * Sets the name of the tree object
		 * @param name The name of the tree object
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Sets the parent object
		 * @param parent
		 */
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		/**
		 * Returns the parent object
		 * @return The parent object
		 */
		public TreeParent getParent() {
			return parent;
		}

		/**
		 * Returns a string representation of the object.
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return getName();
		}

		/**
		 * Returns the full path of the tree element.
		 * This is the name of the element plus the name of all
		 * parent elements, separated by a slash "/"
		 * 
		 * @return The full path of a tree element
		 */
		public String getFullPathName() {
			
			// Get the name of the element
			String fullPathName = getName();
			
			// Root and all have an empty path
			if ((this == root) || (this == all))
				return "";
			
			TreeObject p = this;
			p = p.getParent();
			
			//Add the name of all parent elements
			while ((p != null) && (p != all) && (p != root)) {
				fullPathName = p.getName() + "/" + fullPathName;
				p = p.getParent();
			}
			
			// The full path name
			return fullPathName;
		}
	}

	/**
	 * This class represents a tree object, that can be the parent of
	 * an other tree object
	 * 
	 * @author Gerd Bartelt
	 */
	class TreeParent extends TreeObject {
		
		// List with all children
		private ArrayList<TreeObject> children;

		/**
		 * Constructor
		 * Create a parent element by a name
		 * 
		 * @param name the name of the new object
		 */
		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}

		/**
		 * Constructor
		 * Create a parent element by a name and an icon
		 *
		 * @param name The name of the new object
		 * @icon The name of the icon
		 */
		public TreeParent(String name, String icon) {
			super(name, icon);
			children = new ArrayList<TreeObject>();
		}

		/**
		 * Add a child to the tree object
		 * 
		 * @param child The child to add
		 */
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		/**
		 * Remove a child from the tree object
		 * 
		 * @param child to remove
		 */
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		/**
		 * Returns all children
		 *  
		 * @return Array with all children
		 */
		public TreeObject[] getChildren() {
			return children.toArray(new TreeObject[children.size()]);
		}

		/**
		 * Returns whether the parent has children or not
		 * 
		 * @return True, if there are childchen
		 */
		public boolean hasChildren() {
			return children.size() > 0;
		}

		/**
		 * Remove the object's icon
		 */
		public void removeIcon() {
			((TreeObject) this).icon = null;
		}

		/**
		 * Remove all children
		 */
		public void clear() {
			children.clear();
		}

	}

	/**
	 * Content provider for the tree view
	 * 
	 * @author Gerd Bartelt
	 */
	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		/**
		 * Notifies this content provider that the given viewer's input has
		 * been switched to a different element.
		 */
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

		}
		
		/**
		 * Disposes of this content provider.
		 */
		@Override
		public void dispose() {
		}

		/**
		 * Returns the elements to display in the viewer when its
		 * input is set to the given element.
		 */
		@Override
		public Object[] getElements(Object parent) {
			int entryCnt = 0;

			// Get the elements
			if (parent == root) {
				
				// Rebuild the elements, if some strings have changed
				if (inputElement.getCategoryStringsChanged()) {
					
					// Clear the tree
					clear();
					if (inputElement instanceof DataSetArray<?>) {
						
						// Get all category strings
						Object[] entries = inputElement.getCategoryStrings();
						for (Object entry : entries) {
							addEntry(entry.toString());
						}
					}
				}
			}

			// Count the category strings
			if (inputElement instanceof DataSetArray<?>) {
				Object[] entries = inputElement.getCategoryStrings();
				entryCnt = entries.length;
			}

			// Hide the Tree viewer, if there is no tree element
			if (entryCnt != 0) {
				me.getTree().setVisible(true);
				GridDataFactory.fillDefaults().hint(150, -1).grab(false, true).applyTo(me.getTree());
				me.getTree().getParent().layout(true);
			} else {
				me.getTree().setVisible(false);
				GridDataFactory.fillDefaults().hint(1, -1).grab(false, true).applyTo(me.getTree());
				me.getTree().getParent().layout(true);
			}

			// Return the children elements
			return getChildren(parent);
		}

		/**
		 * Returns the parent element
		 */
		@Override
		public Object getParent(Object child) {
			if (child instanceof TreeObject) { return ((TreeObject) child).getParent(); }
			return null;
		}

		/**
		 * Returns the children elements
		 */
		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) { return ((TreeParent) parent).getChildren(); }
			return new Object[0];
		}

		/**
		 * Returns, if the element has children
		 */
		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}
	}

	/**
	 * The label provider for the topic tree
	 * 
	 * @author Gerd Bartelt
	 */
	class ViewLabelProvider extends LabelProvider {

		/**
		 * The LabelProvider implementation of this ILabelProvider method
		 * returns the element's toString string
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object obj) {
			return obj.toString();
		}

		/**
		 * The LabelProvider implementation of this ILabelProvider
		 * method returns null
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object obj) {

			// Get the icon string of the element
			String icon = ((TreeObject) obj).getIcon();
			LocalResourceManager resources = new LocalResourceManager(JFaceResources.getResources());
			if (icon != null) {
				try {
					
					// Load the icon by the icon name
					return resources.createImage(Activator.getImageDescriptor("/icons/10/" + icon));

				} catch (AssertionFailedException e) {
					Logger.logError(e, "Icon not found: " + icon);
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
				}

			}
			
			// Return a "dot" icon for parent elements
			if (obj instanceof TreeParent) { return resources.createImage(Activator.getImageDescriptor("/icons/10/dot_10.png")); }

			// Return no icon
			return PlatformUI.getWorkbench().getSharedImages().getImage(null);
		}
	}

	/**
	 * Add a new entry to the tree
	 * 
	 * @param entry The new entry to add
	 * @return
	 */
	private TreeParent addEntry(String entry) {

		// Split the input into string parts, all separated  by "/"
		// Every slash separates the elements and will generate a new
		// tree branch.
		String[] entryParts = entry.split("/");
		
		TreeParent me;

		// Start with the root or all element
		if (all != null)
			me = all;
		else
			me = root;

		TreeParent newMe;
		boolean found;
		
		// Use all string parts to build the tree elements
		for (String entryPart : entryParts) {
			found = false;

			// Get all children
			if (me.hasChildren()) {
				for (TreeObject treeObject : me.children) {
					
					// Check, whether there is already an entry with the
					// same name. If there is one, do not create a new.
					if (treeObject.getName().equalsIgnoreCase(entryPart)) {
						me = (TreeParent) treeObject;
						found = true;
						break;
					}
				}
			}

			// Create only a new entry, if there is not already an old one with
			// the same name
			if (!found) {
				me.addChild(newMe = new TreeParent(entryPart));
				me = newMe;
			}
		}
		return me;
	}

	/**
	 * Set the transaction Filter
	 * 
	 * @param name Name of the transaction
	 * @param transactionId ID of the transaction
	 */
	public void setTransaction(String name, int transactionId) {
		
		if (transactionItem == null)
			return;
		
		// Set the filter ID
		transactionItem.setTransactionId(transactionId);

		// Set the name
		transactionItem.setName("Vorgang");
		refresh();
	}

	/**
	 * Returns the name of the selected item
	 * 
	 * @return The name of the item
	 */
	public String getSelectedItemName() {
		if (selectedItem != null)
			return selectedItem.getFullPathName();
		else
			return "";
	}

	/**
	 * Select an item by its name
	 * 
	 * @param name of the item to select
	 */
	public void selectItemByName(String name) {
		boolean found = false;
		boolean allScanned = true;
		
		//Split the name into parts, separated by a slash "/"
		String[] nameParts = name.split("/");

		boolean childfound = false;
		TreeItem newParent = null;
		TreeItem[] children;
		children = me.getTree().getItems();

		// Scan all parts of the input string
		for (String namePart : nameParts) {
			found = true;
			
			// Reached the end of the tree
			if (children.length == 0)
				allScanned = false;
			
			// Search all tree items for one with the same name
			for (TreeItem item : children) {
				if (item.getText().equals(namePart)) {
					childfound = true;
					newParent = item;
				}
			}
			
			// No child was found
			if (!childfound)
				found = false;
			
			// Get the next children
			if (newParent != null)
				children = newParent.getItems();
		}

		// Select the item, if it was found
		if (found && allScanned) {
			me.getTree().setSelection(newParent);
			me.setSelection(me.getSelection(), true);
		}
		
		// Reset the filter to the new entry
		viewDataSetTable.setCategoryFilter(name);
	}

	/**
	 * Set the contact Filter
	 * 
	 * @param name Name of the contact
	 * @param contactId ID of the contact
	 */
	public void setContact(String name, int contactId) {
		if (contactItem == null)
			return;
		contactItem.setContactId(contactId);
		contactItem.setName(name);
		refresh();
	}

	/**
	 * Sets the input of the tree
	 * @param input
	 */
	public void setInput(DataSetArray<?> input) {
		this.inputElement = input;
		this.inputElement.resetCategoryChanged();
		this.setContentProvider(new ViewContentProvider());
		this.setLabelProvider(new ViewLabelProvider());
		this.setInput(root);
		
		// Expand the tree only to level 2
		this.expandToLevel(2);
	}

	/**
	 * Set the table that corresponds to this tree viewer
	 * 
	 * @param viewDataSetTable The table of the view
	 */
	public void setTable(ViewDataSetTable viewDataSetTable) {
		this.viewDataSetTable = viewDataSetTable;
	}

}