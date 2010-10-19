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

package com.sebulli.fakturama.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to enter a comment send to the user via the webshop
 * 
 * @author Gerd Bartelt
 */
public class OrderStatusDialog extends Dialog {

	// Controls of the dialog
	private Label labelComment;
	private Text txtComment;
	private Button bNotification;

	// The comment
	String comment = "";

	//True, if the customer should be notified
	boolean notify = false;

	String dialogTitle;

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 *            Reference to the parents shell
	 * @param dialogTitle
	 *            The dialog title
	 */
	public OrderStatusDialog(Shell parentShell, String dialogTitle) {
		super(parentShell);
		this.dialogTitle = dialogTitle;
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param the
	 *            parents composite.
	 */
	protected Control createDialogArea(Composite parent) {

		// The top composite
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.swtDefaults().applyTo(composite);
		GridDataFactory.swtDefaults().applyTo(composite);

		// The label
		labelComment = new Label(composite, SWT.NONE);
		labelComment.setText("Kommentar an den Kunden:");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 10).applyTo(labelComment);

		// The text field for the  comment
		txtComment = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().hint(450, 120).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(txtComment);

		// The notification check box
		bNotification = new Button(composite, SWT.CHECK | SWT.LEFT);
		bNotification.setSelection(true);
		bNotification.setText("Per Mail benachrichten.");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(bNotification);

		// Hide the text field, when "No notification" is selected
		bNotification.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				txtComment.setVisible(bNotification.getSelection());
			}
		});

		return composite;
	}

	/**
	 * Configures the given shell in preparation for opening this window in it.
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(dialogTitle);
	}

	/**
	 * Returns the comment that was entered
	 * 
	 * @return The comment, or an empty string
	 */
	public String getComment() {
		if (notify)
			return comment;
		else
			return "";
	}

	/**
	 * Returns, if the customer should be notified
	 * 
	 * @return TRUE, if the customer should be notified
	 */
	public boolean getNotify() {
		return notify;
	}

	/**
	 * Close the dialog and copy the content of the SWT controls to local
	 * variables.
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	@Override
	public boolean close() {
		comment = txtComment.getText();
		notify = bNotification.getSelection();
		return super.close();

	}

}
