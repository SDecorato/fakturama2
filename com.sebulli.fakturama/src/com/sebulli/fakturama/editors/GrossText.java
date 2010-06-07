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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.data.UniData;

public class GrossText {

	private UniData netValue;
	private Double vatValue;
	private Text netText;
	private Text grossText;

	public GrossText(final Editor editor, Composite parent, int style, UniData net, Double vat) {
		this.grossText = new Text(parent, style);
		this.netValue = net;
		this.vatValue = vat;
		grossText.setText(DataUtils.CalculateGrossFromNet(netValue.getValueAsDouble(), vat));

		grossText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				grossText.setText(DataUtils.CalculateGrossFromNet(netValue.getValueAsDouble(), vatValue));
				editor.checkDirty();
			}
		});

		grossText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (grossText.isFocusControl()) {
					DataUtils.CalculateNetFromGross(grossText, netText, vatValue, netValue);
					editor.checkDirty();
				}
			}
		});

	}

	public void setVisible(boolean visible) {
		grossText.setVisible(visible);
	}

	public Text getGrossText() {
		return this.grossText;
	}

	public void setNetText(Text netT) {
		this.netText = netT;
	}

	public void setVatValue(Double vatValue) {
		this.vatValue = vatValue;
		grossText.setText(DataUtils.CalculateGrossFromNet(netValue.getValueAsDouble(), vatValue));
	}

	public Text getNetText() {
		return netText;
	}

}
