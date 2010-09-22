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

package com.sebulli.fakturama.editors;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.calculate.Price;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetExpenditureItem;

/**
 * Item editing support for the item table of the document editor
 * 
 * @author Gerd Bartelt
 */
public class ExpenditureItemEditingSupport extends EditingSupport {

	// The cell editor
	private CellEditor editor;
	private String[] categoryListEntries;

	// The current columns
	private int column;
	
	private Object activeObject;
	boolean textCorrected = false;
	
	// The parent expenditure editor that contains the item table
	private ExpenditureEditor expenditureEditor;

	/**
	 * Contructor
	 * Create support to edit the table entries.
	 * 
	 * @param documentEditor The parent document editor that contains the item table
	 * @param viewer The column viewer
	 * @param column The column
	 */
	public ExpenditureItemEditingSupport(ExpenditureEditor expenditureEditor, ColumnViewer viewer, int column) {
		super(viewer);

		// Set the local variables
		this.expenditureEditor = expenditureEditor;
		this.column = column;
		
		// Create the correct editor based on the column index
		// Column nr 2 and nr.3 use a combo box cell editor.
		// The other columns a text cell editor.
		switch (column) {
		case 2:
			categoryListEntries = Data.INSTANCE.getListEntries().getStringsInCategory("value", "billing_accounts");
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), categoryListEntries );
			final CCombo combo = (CCombo)editor.getControl();
			
			combo.addVerifyListener(new VerifyListener() {

				@Override
				public void verifyText(VerifyEvent e) {
					
					// Do it only, if the new text is not empty.
					// This must be done to prevent an event loop.
					if (!e.text.isEmpty() && !textCorrected) {

						// The complete text is the old one of the combo and
						// the new sequence from the event.
						String text = combo.getText()+e.text;

						// Delete or backslash will end the suggestion mode
						if ((e.keyCode == 8) || (e.keyCode == 127))
							textCorrected = true;
						else {
							
							// Get the suggestion ..
							String suggestion = getSuggestion(text);
							if (!suggestion.isEmpty()) {
								
								// .. and use it.
								combo.setText("");
								e.text = suggestion;
							}
						}
					}
				}});
			break;
		case 3:
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), Data.INSTANCE.getVATs().getStrings("name"));
			break;
		default:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
		}
		

	}

	/**
	 * Specifies the columns with cells that are editable.
	 * 
	 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
	 */
	@Override
	protected boolean canEdit(Object element) {

		switch (this.column) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return true;
		}
		return false;
	}

	/**
	 * The editor to be shown
	 * 
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	/**
	 * Get the value to set to the editor
	 * 
	 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
	 */
	@Override
	protected Object getValue(Object element) {

		activeObject = element;
		expenditureEditor.setItemEditing(this);
		
		DataSetExpenditureItem item = (DataSetExpenditureItem) element;
		switch (this.column) {
		case 1:
			return item.getFormatedStringValueByKey("name");
		case 2:
			
			// Get the index of that entry, that is equal to the category
			for (int i = 0; i < categoryListEntries.length; i++) {
				if (categoryListEntries[i].equals(item.getStringValueByKey("category")))
					return i;
			}
			
			// No entry found
			return -1;
			
		case 3:
			return item.getIntValueByKey("vatid");
		case 4:
			return new Price(item).getUnitNet().asFormatedString();
		case 5:
			return new Price(item).getUnitGross().asFormatedString();
		}
		return "";
	}

	/**
	 * Sets the new value on the given element.
	 * 
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		DataSetExpenditureItem item = (DataSetExpenditureItem) element;

		expenditureEditor.setItemEditing(null);
		
		switch (this.column) {
		case 1:
			// Set the name
			item.setStringValueByKey("name", String.valueOf(value));
			break;
			
		case 2:
			// Get the selected item from the combo box
			Integer i = (Integer) value;
			
			// If there is an entry of the combo list selected
			if ( i>=0 && i<categoryListEntries.length)
				item.setStringValueByKey("category", categoryListEntries[i]);
			
			// If there is an entry with the same name as one of the combo list
			else {
				// get the text of the combo box
				String text = ((CCombo)editor.getControl()).getText();
				
				boolean found = false;
				
				// Search for the entry with the same value of the category
				for (int ii = 0; ii < categoryListEntries.length && !found; ii++) {
					String listEntry = categoryListEntries[ii];
					if (listEntry.equals(text)) {
						item.setStringValueByKey("category", listEntry);
						found = true;
					}
				}
				
				// No entry found
				//TODO: add the entry to the list of billing accounts
				if (!found)
					item.setStringValueByKey("category", "??");
				
			}
			break;
		case 3:
			// Set the VAT
			
			// Get the selected item from the combo box
			i = (Integer) value;
			String s;

			// Get the VAT by the selected name
			if (i >= 0) {
				s = ((ComboBoxCellEditor) this.editor).getItems()[i];
				i = Data.INSTANCE.getVATs().getDataSetByStringValue("name", s);
			} 
			// Get the VAT by the Value in percent
			else {
				s = ((CCombo) ((ComboBoxCellEditor) this.editor).getControl()).getText();
				i = Data.INSTANCE.getVATs().getDataSetByDoubleValue("value", DataUtils.StringToDouble(s + "%"));
			}
			
			// If no VAT is found, use the standard VAT
			if (i < 0)
				i = Integer.parseInt(Data.INSTANCE.getProperty("standardvat"));
			item.setIntValueByKey("vatid", i);
			break;
		case 4:
			// Net price
			item.setStringValueByKey("price", String.valueOf(value));
			break;
		case 5:
			// Gross price
			item.setDoubleValueByKey("price", new Price(DataUtils.StringToDouble((String) value), item.getDoubleValueByKeyFromOtherTable("vatid.VATS:value"),false, true).getUnitNet().asDouble());
			break;
		default:
			break;
		}
		
		// Update the data
		getViewer().update(element, null);
	}
	
	/**
	 * Search for the "base" string in the list and get those part of the string
	 * that was found in the list. If there are more than one entry that starts
	 * with the same sequence, return the sequence, that is equal in all strings of 
	 * the list.
	 * 
	 * @param base String to search for
	 * @return Result string
	 */
	private String getSuggestion (String base) {

		// Do not work with empty strings
		if (base.isEmpty())
			return "";

		// Get list to search for
		String[] suggestions = Data.INSTANCE.getListEntries().getStringsInCategory("value", "billing_accounts");
		
		// Temporary list with all strings that start with the base string
		ArrayList<String> resultStrings = new ArrayList<String>();

		// Get all strings that start with the base string
		// and copy them to the temporary list
		for (int i = 0; i < suggestions.length; i++) {
			if (suggestions[i].toLowerCase().startsWith(base.toLowerCase()))
				resultStrings.add(suggestions[i]);
		}

		// No string matches: return with an empty string
		if (resultStrings.isEmpty())
			return "";
		
		// There was at least one string found in the list.
		// Start with this entry.
		String tempResult = resultStrings.get(0);
		String result = "";

		// Get that part of the all the strings, that is equal
		for (String resultString : resultStrings) {
			
			// To compare two strings character by character, the minimum
			// length of both must be used for the loop
			int length = tempResult.length();
			if (resultString.length() < length)
				length = resultString.length();
			
			// Compare both strings, and get the part, that is equal
			for (int i = 0;i < length;i++) {
				if (tempResult.substring(0, i+1).
						equalsIgnoreCase(resultString.substring(0, i+1)))
					result = tempResult.substring(0, i+1);

			}
			
			// Use the result to compare it with the next entry
			tempResult = result;
		}
		
		// Return the result
		return result;
	}
	
	/**
	 * Cancel editing of this cell
	 */
	public void cancelAndSave() {
		this.setValue(activeObject, this.editor.getValue());
		
	}

}