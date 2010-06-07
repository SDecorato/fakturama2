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

public class ItemEditingSupport extends EditingSupport {
	private CellEditor editor;
	private int column;
	private DocumentEditor documentEditor;

	public ItemEditingSupport(DocumentEditor documentEditor, ColumnViewer viewer, int column) {
		super(viewer);

		this.documentEditor = documentEditor;
		// Create the correct editor based on the column index
		switch (column) {
		case 5:
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), Data.INSTANCE.getVATs().getStrings("name"));
			break;
		default:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
		}
		this.column = column;
	}

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

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
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

	@Override
	protected void setValue(Object element, Object value) {
		DataSetItem item = (DataSetItem) element;

		switch (this.column) {
		case 1:
			item.setStringValueByKey("quantity", String.valueOf(value));
			int productId = item.getIntValueByKey("productid");
			if (productId > 0) {
				DataSetProduct product = Data.INSTANCE.getProducts().getDatasetById(productId);
				double price = product.getPriceByQuantity(DataUtils.StringToDouble(String.valueOf(value)));
				item.setDoubleValueByKey("price", price);
			}
			break;
		case 2:
			item.setStringValueByKey("itemnr", String.valueOf(value));
			break;
		case 3:
			item.setStringValueByKey("name", String.valueOf(value));
			break;
		case 4:
			item.setStringValueByKey("description", String.valueOf(value));
			break;
		case 5:
			Integer i = (Integer) value;
			String s;
			if (i >= 0) {
				s = ((ComboBoxCellEditor) this.editor).getItems()[i];
				i = Data.INSTANCE.getVATs().getDataSetByStringValue("name", s);
			} else {
				s = ((CCombo) ((ComboBoxCellEditor) this.editor).getControl()).getText();
				i = Data.INSTANCE.getVATs().getDataSetByDoubleValue("value", DataUtils.StringToDouble(s + "%"));
			}
			if (i < 0)
				i = Integer.parseInt(Data.INSTANCE.getProperty("standardvat"));
			item.setVat(i);
			break;
		case 6:
			if (documentEditor.getUseGross())
				item.setDoubleValueByKey("price", new Price(DataUtils.StringToDouble((String) value), item.getDoubleValueByKey("vatvalue"), item
						.getBooleanValueByKey("novat"), true).getUnitNet().asDouble());
			else
				item.setStringValueByKey("price", String.valueOf(value));

			break;
		case 7:
			Double d = DataUtils.StringToDoubleDiscount(String.valueOf(value));
			item.setDoubleValueByKey("discount", d);
			break;
		default:
			break;
		}
		documentEditor.calculate();
		getViewer().update(element, null);
	}

}