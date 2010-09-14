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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.custom.CCombo;

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

	// The current columns
	private int column;
	
	private Object activeObject;
	
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
		// Column nr.3 uses a combo box cell editor.
		// The other columns a text cell editor.
		switch (column) {
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
			return item.getStringValueByKey("category");
		case 3:
			return item.getIntValueByKey("vatid");
		case 4:
			if (expenditureEditor.getUseGross())
				return new Price(item).getUnitGross().asFormatedString();
			else
				return new Price(item).getUnitNet().asFormatedString();
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
			// Set the name
			item.setStringValueByKey("category", String.valueOf(value));
			break;
		case 3:
			// Set the VAT
			
			// Get the selected item from the combo box
			Integer i = (Integer) value;
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
			// Set the price as gross or net value.
			// If the editor displays gross values, calculate the net value,
			// because only net values are stored.
			if (expenditureEditor.getUseGross())
				item.setDoubleValueByKey("price", new Price(DataUtils.StringToDouble((String) value), item.getDoubleValueByKeyFromOtherTable("vatid.VATS:value"),false, true).getUnitNet().asDouble());
			else
				item.setStringValueByKey("price", String.valueOf(value));
			break;
		default:
			break;
		}
		
		// Update the data
		getViewer().update(element, null);
	}
	
	/**
	 * Cancel editing of this cell
	 */
	public void cancelAndSave() {
		this.setValue(activeObject, this.editor.getValue());
		
	}

}