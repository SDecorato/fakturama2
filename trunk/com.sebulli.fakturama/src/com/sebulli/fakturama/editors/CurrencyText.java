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

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.UniData;

public class CurrencyText {
	private UniData value;
	private Text text;

	public CurrencyText(final Editor editor, Composite parent, int style, UniData parvalue) {
		this.text = new Text(parent, style);
		value = parvalue;
		this.text.setText(DataUtils.DoubleToFormatedPrice(this.value.getValueAsDouble()));

		text.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				text.setText(DataUtils.DoubleToFormatedPrice(value.getValueAsDouble()));
				editor.checkDirty();
			}
		});

		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				value.setValue(DataUtils.StringToDouble(text.getText()));
				text.setText(DataUtils.DoubleToFormatedPrice(value.getValueAsDouble()));
				editor.checkDirty();
			}

		});

	}

	public Text getText() {
		return text;
	}

}
