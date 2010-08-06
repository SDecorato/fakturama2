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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.logger.Logger;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;

/**
 * Parent class for all editors
 * 
 * @author Gerd Bartelt
 */
public abstract class Editor extends EditorPart {

	protected StdComposite stdComposite = null;
	protected String tableViewID = "";
	protected String editorID = "";

	/**
	 * Set the font size of a label to 24pt
	 * 
	 * @param label The label that is modified
	 */
	protected void makeLargeLabel(Label label) {
		resizeLabel (label, 24);
	}

	/**
	 * Set the font size of a label to 9pt
	 * 
	 * @param label The label that is modified
	 */
	protected void makeSmallLabel(Label label) {
		resizeLabel (label, 9);
	}

	/**
	 * Set the font size of a label to x px
	 * 
	 * @param label The label that is modified
	 * @size Size of the label in px
	 */
	protected void resizeLabel(Label label, int size) {
		FontData[] fD = label.getFont().getFontData();
		fD[0].setHeight(size);
		Font font = new Font(null, fD[0]);
		label.setFont(font);
		font.dispose();
	}

	
	/**
	 * Class to create the widgets to show and set the standard
	 * entry.
	 * 
	 */
	protected class StdComposite {

		// Text widgets that displays the standard widget
		private Text txtStd;
		
		// The property key that defines the standard
		private String propertyKey = null;
		
		// The unidataset of this editor 
		private final UniDataSet uds;
		
		// The label for "This dataset"
		private String thisDataset = null;
		
		// The data set array with this and the other unidatasets
		private DataSetArray<?> dataSetArray;
	
		/**
		 * Constructor
		 * Creates the widgets to set this entry as standard entry.
		 * 
		 * @param parent The parent widget
		 * @param uds The editor's unidataset
		 * @param dataSetArray This and the other unidatasets
		 * @param propertyKey The property key that defines the standard
		 * @param thisDataset Text for "This dataset" 
		 * @param hSpan Horizontal span
		 */
		public StdComposite(Composite parent, final UniDataSet uds, DataSetArray<?> dataSetArray, final String propertyKey, final String thisDataset, int hSpan) {
			
			// Set the local variables
			this.propertyKey = propertyKey;
			this.uds = uds;
			this.thisDataset = thisDataset;
			this.dataSetArray = dataSetArray;
			
			// Create a container for the text widget and the button
			Composite stdComposite = new Composite(parent, SWT.NONE);
			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(stdComposite);
			GridDataFactory.fillDefaults().span(hSpan, 1).applyTo(stdComposite);
			
			// Create the text widget that displays the standard entry
			txtStd = new Text(stdComposite, SWT.BORDER);
			txtStd.setEnabled(false);
			GridDataFactory.swtDefaults().hint(150, -1).align(SWT.BEGINNING, SWT.CENTER).applyTo(txtStd);
			setStdText();

			// Create the button to make this entry to the standard
			Button stdButton = new Button(stdComposite, SWT.BORDER);
			stdButton.setText("zum Standard machen");
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(stdButton);
			stdButton.addSelectionListener(new SelectionAdapter() {
				
				/**
				 * Make this entry to the standard
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Data.INSTANCE.setProperty(propertyKey, uds.getStringValueByKey("id"));
					txtStd.setText(thisDataset);
					refreshView();
				}
			});

		}
		
		
		/**
		 * Test, if this is the standard entry and set the text of
		 * the text widget.
		 */
		public void setStdText() {
			if (txtStd != null) {
				int stdID = 0;

				// Get the ID of the standard unidataset
				try {
					stdID = Integer.parseInt(Data.INSTANCE.getProperty(propertyKey));
				} catch (NumberFormatException e) {
					stdID = 0;
				}
				
				// If the editor's unidataset is the standard entry
				if (uds.getIntValueByKey("id") == stdID)
					// Mark it as "standard" ..
					txtStd.setText(thisDataset);
				else
					// .. or display the one that is the standard entry.
					txtStd.setText(((UniDataSet) dataSetArray.getDatasetById(stdID)).getStringValueByKey("name"));
			}

		}


	}

	/**
	 * Asks this part to take focus within the workbench
	 * Set the focus to the standard text
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

		if (stdComposite != null)
			stdComposite.setStdText();
	}

	/**
	 * Get the next document number
	 * 
	 * @return The next document number
	 */
	protected String getNextNr() {
		
		// Create the string of the preference store for format and number
		String prefStrFormat = "NUMBERRANGE_" + editorID.toUpperCase() + "_FORMAT";
		String prefStrNr = "NUMBERRANGE_" + editorID.toUpperCase() + "_NR";
		String format;
		String nrExp = "";
		String nextNr;
		int nr;
		
		// Get the last (it's the next free) document number from the preferences
		format = Activator.getDefault().getPreferenceStore().getString(prefStrFormat);
		nr = Activator.getDefault().getPreferenceStore().getInt(prefStrNr);

		// Find the placeholder for a decimal number with n digits
		// with the format "{Xnr}", "X" is the number of digits.
		Pattern p = Pattern.compile("\\{\\d*nr\\}");
		Matcher m = p.matcher(format);

		// replace "{Xnr}" with "%0Xd"
		if (m.find()) {
			nrExp = format.substring(m.start(), m.end());
			nrExp = "%0" + nrExp.substring(1, nrExp.length() - 3) + "d";
			format = m.replaceFirst(nrExp);
		}

		// Replace the "%0Xd" with the decimal number
		nextNr = String.format(format, nr);
		
		// Return the strin with the next free document number
		return nextNr;
	}

	/**
	 * Set the next free document number in the preference store.
	 * But check, if the documents number is the next free one. 
	 * 
	 * @param s The documents number as string.
	 * @return TRUE, if the document number is correctly set to the
	 * 			next free number.
	 */
	protected boolean setNextNr(String s) {
		
		// Create the string of the preference store for format and number
		String prefStrFormat = "NUMBERRANGE_" + editorID.toUpperCase() + "_FORMAT";
		String prefStrNr = "NUMBERRANGE_" + editorID.toUpperCase() + "_NR";
		String format;
		int nr;
		boolean ok = false;
		Integer nextnr;

		// Get the next document number from the preferences, increased be one.
		format = Activator.getDefault().getPreferenceStore().getString(prefStrFormat);
		nextnr = Activator.getDefault().getPreferenceStore().getInt(prefStrNr) + 1;

		// Find the placeholder for a decimal number with n digits
		// with the format "{Xnr}", "X" is the number of digits.
		Pattern p = Pattern.compile("\\{\\d*nr\\}");
		Matcher m = p.matcher(format);

		// Get the next number
		if (m.find()) {

			// Extract the number string
			s = s.substring(m.start(), s.length() - format.length() + m.end());
			
			try {
				// Convert it to an integer and increase it by one.
				nr = Integer.parseInt(s) + 1;

				// Update the value of the last document number, but only,
				// If the number of this document is the next free number
				if (nr == nextnr) {
					Activator.getDefault().getPreferenceStore().setValue(prefStrNr, nr);
					ok = true;
				}
			} catch (NumberFormatException e) {
				Logger.logError(e, "Document number invalid");
			}
		}
		
		// The result of the validation
		return ok;
	}

	/**
	 * Refresh the view that corresponds to this editor
	 * 
	 */
	protected void refreshView() {
		
		// Find the view
		ViewDataSetTable view = (ViewDataSetTable) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(tableViewID);
		
		// Refresh it
		if (view != null)
			view.refresh();

	}

	/**
	 * Request a new validation, if the document is dirty.
	 */
	protected void checkDirty() {
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Supervice this text widget.
	 * Set the text limit and request a new "isDirty" validation,
	 * if the content of the text widget is modified.
	 */
	protected void superviceControl(Text text, int limit) {
		text.setTextLimit(limit);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				checkDirty();

			}
		});
	}

	/**
	 * Supervice this dateTime widget.
	 * Set the text limit and request a new "isDirty" validation,
	 * if the content of the text dateTime is modified.
	 */
	protected void superviceControl(DateTime dateTime) {
		dateTime.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkDirty();
			}

		});
	}

	/**
	 * Supervice this combo widget.
	 * Set the text limit and request a new "isDirty" validation,
	 * if the content of the text combo is modified.
	 */
	protected void superviceControl(Combo combo) {
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkDirty();
			}

		});
	}

}