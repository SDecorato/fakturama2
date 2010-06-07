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

public abstract class Editor extends EditorPart {

	protected StdComposite stdComposite = null;
	protected String tableViewID = "";
	protected String editorID = "";

	protected void makeLargeLabel(Label label) {
		FontData[] fD = label.getFont().getFontData();
		fD[0].setHeight(24);
		Font font = new Font(null, fD[0]);
		label.setFont(font);
		font.dispose();

	}


	protected class StdComposite {
		private Text txtStd;
		private String propertyKey = null;
		private final UniDataSet uds;
		private String thisDataset = null;
		private DataSetArray<?> dataSetArray;

		public void setStdText() {
			if (txtStd != null) {
				int stdID = 0;
				try {
					stdID = Integer.parseInt(Data.INSTANCE.getProperty(propertyKey));
				} catch (NumberFormatException e) {
					stdID = 0;
				}
				if (uds.getIntValueByKey("id") == stdID)
					txtStd.setText(thisDataset);
				else
					txtStd.setText(((UniDataSet) dataSetArray.getDatasetById(stdID)).getStringValueByKey("name"));
			}

		}

		public StdComposite(Composite parent, final UniDataSet uds, DataSetArray<?> dataSetArray, final String propertyKey, final String thisDataset) {
			Composite stdComposite = new Composite(parent, SWT.NONE);
			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(stdComposite);
			GridDataFactory.fillDefaults().applyTo(stdComposite);
			this.propertyKey = propertyKey;
			txtStd = new Text(stdComposite, SWT.BORDER);
			txtStd.setEnabled(false);
			this.uds = uds;
			this.thisDataset = thisDataset;
			this.dataSetArray = dataSetArray;
			setStdText();

			GridDataFactory.swtDefaults().hint(150, -1).align(SWT.BEGINNING, SWT.CENTER).applyTo(txtStd);

			Button stdButton = new Button(stdComposite, SWT.BORDER);
			stdButton.setText("zum Standard machen");
			stdButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Data.INSTANCE.setProperty(propertyKey, uds.getStringValueByKey("id"));
					txtStd.setText(thisDataset);
					refreshView();
				}
			});

			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(stdButton);

		}

	}

	@Override
	public void setFocus() {

		if (stdComposite != null)
			stdComposite.setStdText();
	}

	protected String getNextNr() {
		String prefStrFormat = "NUMBERRANGE_" + editorID.toUpperCase() + "_FORMAT";
		String prefStrNr = "NUMBERRANGE_" + editorID.toUpperCase() + "_NR";
		String format;
		String nrExp = "";
		String nextNr;
		int nr;
		format = Activator.getDefault().getPreferenceStore().getString(prefStrFormat);
		nr = Activator.getDefault().getPreferenceStore().getInt(prefStrNr);

		Pattern p = Pattern.compile("\\{\\d*nr\\}");
		Matcher m = p.matcher(format);

		if (m.find()) {
			nrExp = format.substring(m.start(), m.end());
			nrExp = "%0" + nrExp.substring(1, nrExp.length() - 3) + "d";
			format = m.replaceFirst(nrExp);
		}

		nextNr = String.format(format, nr);
		return nextNr;
	}

	protected boolean setNextNr(String s) {
		String prefStrFormat = "NUMBERRANGE_" + editorID.toUpperCase() + "_FORMAT";
		String prefStrNr = "NUMBERRANGE_" + editorID.toUpperCase() + "_NR";
		String format;
		int nr;
		boolean ok = false;
		Integer nextnr;
		format = Activator.getDefault().getPreferenceStore().getString(prefStrFormat);
		nextnr = Activator.getDefault().getPreferenceStore().getInt(prefStrNr) + 1;

		Pattern p = Pattern.compile("\\{\\d*nr\\}");
		Matcher m = p.matcher(format);

		if (m.find()) {
			s = s.substring(m.start(), s.length() - format.length() + m.end());
			try {
				nr = Integer.parseInt(s) + 1;
				if (nr == nextnr) {
					Activator.getDefault().getPreferenceStore().setValue(prefStrNr, nr);
					ok = true;
				}

			} catch (NumberFormatException e) {
				Logger.logError(e, "Document nr invalid");
			}
		}
		return ok;
	}

	protected void refreshView() {
		ViewDataSetTable view = (ViewDataSetTable) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(tableViewID);
		if (view != null)
			view.refresh();

	}

	protected void checkDirty() {
		firePropertyChange(PROP_DIRTY);
	}

	protected void superviceControl(Text text, int limit) {
		text.setTextLimit(limit);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				checkDirty();

			}
		});
	}

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
