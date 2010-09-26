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
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DataSetProduct;

/**
 * Item editing support for the item table of the document editor
 * 
 * @author Gerd Bartelt
 */
public class ItemEditingSupport extends EditingSupport {

	// The cell editor
	private CellEditor editor;

	// The current columns
	private int column;
	
	private Object activeObject;
	
	// The parent document editor that contains the item table
	private DocumentEditor documentEditor;

	/**
	 * Contructor
	 * Create support to edit the table entries.
	 * 
	 * @param documentEditor The parent document editor that contains the item table
	 * @param viewer The column viewer
	 * @param column The column
	 */
	public ItemEditingSupport(DocumentEditor documentEditor, ColumnViewer viewer, int column) {
		super(viewer);

		// Set the local variables
		this.documentEditor = documentEditor;
		this.column = column;
		
		// Create the correct editor based on the column index
		// Column nr.5 uses a combo box cell editor.
		// The other columns a text cell editor.
		switch (column) {
		case 5:
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
		case 6:
		case 7:
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
		documentEditor.setItemEditing(this);
		
		DataSetItem item = (DataSetItem) element;
		switch (this.column) {
		case 1:
			return item.getFormatedStringValueByKey("quantity");
		case 2:
			return item.getStringValueByKey("itemnr");
		case 3:
			return item.getStringValueByKey("name");
		case 4:
			return item.getStringValueByKey("description");
		case 5:
			return item.getIntValueByKey("vatid");
		case 6:
			if (documentEditor.getUseGross())
				return new Price(item).getUnitGross().asFormatedString();
			else
				return new Price(item).getUnitNet().asFormatedString();
		case 7:
			return item.getFormatedStringValueByKey("discount");
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
		DataSetItem item = (DataSetItem) element;

		documentEditor.setItemEditing(null);
		
		switch (this.column) {
		case 1:
			// Set the quantity
			item.setStringValueByKey("quantity", String.valueOf(value));
			int productId = item.getIntValueByKey("productid");
			
			// If the item is coupled with a product, get the graduated price
			if (productId >= 0) {
				DataSetProduct product = Data.INSTANCE.getProducts().getDatasetById(productId);
				double price = product.getPriceByQuantity(DataUtils.StringToDouble(String.valueOf(value)));
				item.setDoubleValueByKey("price", price);
			}
			break;
		case 2:
			// Set the item number
			item.setStringValueByKey("itemnr", String.valueOf(value));
			break;
		case 3:
			// Set the name
			item.setStringValueByKey("name", String.valueOf(value));
			break;
		case 4:
			// Set the description
			item.setStringValueByKey("description", String.valueOf(value));
			break;
		case 5:
			// Set the VAT
			
			// Get the selected item from the combo box
			Integer i = (Integer) value;
			String s;

			// Get the VAT by the selected name
			if (i >= 0) {
				s = ((ComboBoxCellEditor) this.editor).getItems()[i];
				i = Data.INSTANCE.getVATs().getDataSetIDByStringValue("name", s);
			} 
			// Get the VAT by the Value in percent
			else {
				s = ((CCombo) ((ComboBoxCellEditor) this.editor).getControl()).getText();
				i = Data.INSTANCE.getVATs().getDataSetByDoubleValue("value", DataUtils.StringToDouble(s + "%"));
			}
			
			// If no VAT is found, use the standard VAT
			if (i < 0)
				i = Integer.parseInt(Data.INSTANCE.getProperty("standardvat"));

			// Set the vat and store the vat value before and after the modification.
			Double oldVat = 1.0 + item.getDoubleValueByKeyFromOtherTable("vatid.VATS:value");
			item.setVat(i);
			Double newVat = 1.0 + item.getDoubleValueByKeyFromOtherTable("vatid.VATS:value");
			
			// Modify the net value that the gross value stays constant.
			if (documentEditor.getUseGross())
				item.setDoubleValueByKey("price", oldVat / newVat * item.getDoubleValueByKey("price"));
			
			break;
		case 6:
			// Set the price as gross or net value.
			// If the editor displays gross values, calculate the net value,
			// because only net values are stored.
			if (documentEditor.getUseGross())
				item.setDoubleValueByKey("price", new Price(DataUtils.StringToDouble((String) value), item.getDoubleValueByKey("vatvalue"), item
						.getBooleanValueByKey("novat"), true).getUnitNet().asDouble());
			else
				item.setStringValueByKey("price", String.valueOf(value));
			break;
		case 7:
			// Set the discount value
			Double d = DataUtils.StringToDoubleDiscount(String.valueOf(value));
			item.setDoubleValueByKey("discount", d);
			break;
		default:
			break;
		}
		
		// Recalculate the total sum of the document
		documentEditor.calculate();
		
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