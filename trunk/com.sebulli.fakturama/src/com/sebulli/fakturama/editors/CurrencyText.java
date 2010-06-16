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

/**
 * This class contains a SWT text widget and some
 * methods to format the text as a currency value.
 * 
 * @author Gerd Bartelt
 */
public class CurrencyText {
	
	// UniData object, that is modified, if the text changes.
	private UniData value;
	// The SWT text widget
	private Text text;

	/**
	 * Constructor
	 * Create a new text widget and add listeners
	 * 
	 * @param editor The editor (is used to call the checkDirty method)
	 * @param parent The parent composite in which the widget is placed.
	 * @param style The text's style
	 * @param parvalue The corresponding UniData object
	 */
	public CurrencyText(final Editor editor, Composite parent, int style, UniData parvalue) {
		
		// Set the local reference to the UniData object
		value = parvalue;
		
		// Create the SWT text widget
		this.text = new Text(parent, style);
		this.text.setText(DataUtils.DoubleToFormatedPrice(this.value.getValueAsDouble()));

		// Add a selection listener
		text.addSelectionListener(new SelectionAdapter() {

			/**
			 * Sent when default selection occurs in the control
			 * The content of the text widget is formated,
			 * if the used has pressed the enter key.
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				text.setText(DataUtils.DoubleToFormatedPrice(value.getValueAsDouble()));
				editor.checkDirty();
			}
			
		});

		// Add a focus listener
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

			}

			/**
			 * Sent when a control loses focus
			 * 
			 * The content of the text widget is formated,
			 * if the focus is lost.
			 * 
			 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				value.setValue(DataUtils.StringToDouble(text.getText()));
				text.setText(DataUtils.DoubleToFormatedPrice(value.getValueAsDouble()));
				editor.checkDirty();
			}
			
		});
	}
	
	/**
	 * Get the text control
	 * 
	 * @return The SWT text control
	 */
	public Text getText() {
		return text;
	}

}
