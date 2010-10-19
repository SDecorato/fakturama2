/*
 * 
 * Fakturama - Free Invoicing Software Copyright (C) 2010 Gerd Bartelt
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sebulli.fakturama.editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.data.Data;
import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.views.datasettable.ViewDataSetTable;

/**
 * Parent class for all editors
 * 
 * @author Gerd Bartelt
 */
public abstract class Editor extends EditorPart implements ISaveablePart2 {

	protected StdComposite stdComposite = null;
	protected String tableViewID = "";
	protected String editorID = "";
	protected static final int NO_ERROR = 0;
	protected static final int ERROR_NOT_NEXT_ID = 1;

	/**
	 * Set the font size of a label to 24pt
	 * 
	 * @param label
	 *            The label that is modified
	 */
	protected void makeLargeLabel(Label label) {
		resizeLabel(label, 24);
	}

	/**
	 * Set the font size of a label to 9pt
	 * 
	 * @param label
	 *            The label that is modified
	 */
	protected void makeSmallLabel(Label label) {
		resizeLabel(label, 9);
	}

	/**
	 * Set the font size of a label to x px
	 * 
	 * @param label
	 *            The label that is modified
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
	 * Class to create the widgets to show and set the standard entry.
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
		 * Constructor Creates the widgets to set this entry as standard entry.
		 * 
		 * @param parent
		 *            The parent widget
		 * @param uds
		 *            The editor's unidataset
		 * @param dataSetArray
		 *            This and the other unidatasets
		 * @param propertyKey
		 *            The property key that defines the standard
		 * @param thisDataset
		 *            Text for "This dataset"
		 * @param hSpan
		 *            Horizontal span
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
		 * Test, if this is the standard entry and set the text of the text
		 * widget.
		 */
		public void setStdText() {
			if (txtStd != null) {
				int stdID = 0;

				// Get the ID of the standard unidataset
				try {
					stdID = Integer.parseInt(Data.INSTANCE.getProperty(propertyKey));
				}
				catch (NumberFormatException e) {
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
	 * Asks this part to take focus within the workbench Set the focus to the
	 * standard text
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
	 * Set the next free document number in the preference store. But check, if
	 * the documents number is the next free one.
	 * 
	 * @param s
	 *            The documents number as string.
	 * @return Errorcode, if the document number is correctly set to the next
	 *         free number.
	 */
	protected int setNextNr(String value, String key, DataSetArray<?> allDataSets) {

		// Create the string of the preference store for format and number
		String prefStrFormat = "NUMBERRANGE_" + editorID.toUpperCase() + "_FORMAT";
		String prefStrNr = "NUMBERRANGE_" + editorID.toUpperCase() + "_NR";
		String format;
		String s = "";
		int nr;
		int result = ERROR_NOT_NEXT_ID;
		Integer nextnr;

		// Get the next document number from the preferences, increased be one.
		format = Activator.getDefault().getPreferenceStore().getString(prefStrFormat);
		nextnr = Activator.getDefault().getPreferenceStore().getInt(prefStrNr) + 1;

		// Exit, if format is empty
		if (format.trim().isEmpty())
			return NO_ERROR;

		// Find the placeholder for a decimal number with n digits
		// with the format "{Xnr}", "X" is the number of digits.
		Pattern p = Pattern.compile("\\{\\d*nr\\}");
		Matcher m = p.matcher(format);

		// Get the next number
		if (m.find()) {

			// Extract the number string
			s = value.substring(m.start(), value.length() - format.length() + m.end());

			try {
				// Convert it to an integer and increase it by one.
				nr = Integer.parseInt(s) + 1;

				// Update the value of the last document number, but only,
				// If the number of this document is the next free number
				if (nr == nextnr) {
					Activator.getDefault().getPreferenceStore().setValue(prefStrNr, nr);
					result = NO_ERROR;
				}
			}
			catch (NumberFormatException e) {
				//Logger.logError(e, "Document number invalid");
			}
		}

		// The result of the validation
		return result;
	}

	/**
	 * Refresh the view that corresponds to this editor
	 * 
	 */
	protected void refreshView() {

		// Refresh the view that corresponds to this editor
		refreshView(tableViewID);

	}

	/**
	 * Refresh a view that corresponds to this editor
	 * 
	 * @prama ID of the view to refresh
	 */
	protected void refreshView(String viewId) {

		// Find the view
		ViewDataSetTable view = (ViewDataSetTable) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewId);

		// Refresh it
		if (view != null)
			view.refresh();

	}

	/**
	 * Request a new validation, if the document is dirty.
	 */
	public void checkDirty() {
		firePropertyChange(EditorPart.PROP_DIRTY);
	}

	/**
	 * Supervice this text widget. Set the text limit and request a new
	 * "isDirty" validation, if the content of the text widget is modified.
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
	 * Supervice this dateTime widget. Set the text limit and request a new
	 * "isDirty" validation, if the content of the text dateTime is modified.
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
	 * Supervice this combo widget. Set the text limit and request a new
	 * "isDirty" validation, if the content of the text combo is modified.
	 */
	protected void superviceControl(Combo combo) {

		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				checkDirty();

			}
		});

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

	/**
	 * Jump to the next control, if in a multi-line text control the tab key is
	 * pressed. Normally the tab won't jump to the next control, if the current
	 * one is a text control. It will insert a tabulator.
	 * 
	 * @param text
	 *            This (multi-line) text control
	 * @param nextControl
	 *            The next control
	 */
	protected void setTabOrder(Text text, final Control nextControl) {
		text.addKeyListener(new KeyAdapter() {

			/**
			 * Capture the tab key and set the focus to the next control
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == '\t') {
					e.doit = false;
					nextControl.setFocus();
				}
			}

		});

	}

	/**
	 * Test before close, if the document ID is correct
	 * 
	 * @see org.eclipse.ui.ISaveablePart2#promptToSaveOnClose()
	 */
	@Override
	public int promptToSaveOnClose() {

		MessageDialog dialog = new MessageDialog(getEditorSite().getShell(), "Änderungen speichern", null, "Änderungen speichern ?", MessageDialog.QUESTION,
				new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

		final int dialogResult = dialog.open();

		if (dialogResult == 0) {
			return 0;
			// Check, if the number is unique
			/*if (thereIsOneWithSameNumber())
				return ISaveablePart2.CANCEL;
			else
				return ISaveablePart2.YES;*/
		}
		else if (dialogResult == 1) {
			return ISaveablePart2.NO;
		}
		else {
			return ISaveablePart2.CANCEL;
		}
	}

	/**
	 * Returns, if save is allowed
	 * 
	 * @return TRUE, if save is allowed
	 */
	protected boolean saveAllowed() {
		return true;
	}

}
