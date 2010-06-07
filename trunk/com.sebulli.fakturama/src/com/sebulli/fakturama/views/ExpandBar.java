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

package com.sebulli.fakturama.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.logger.Logger;

public class ExpandBar extends Composite {
	private final Label image;
	private final Label text;
	private final Label arrow;
	private Composite composite = null;
	private boolean collapsed = false;
	private Composite top;

	// private ExpandBarManager expandBarManager;

	public ExpandBar(ExpandBarManager expandBarManager, Composite parent, int style) {
		super(parent, style);
		// this.expandBarManager = expandBarManager;
		top = new Composite(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(top);

		Composite headbar = new Composite(top, SWT.BORDER);
		GridLayoutFactory.swtDefaults().numColumns(3).margins(2, 2).applyTo(headbar);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(headbar);
		headbar.setBackground(new Color(null, 255, 255, 255));

		image = new Label(headbar, SWT.NONE);
		image.setText("icon");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(image);

		text = new Label(headbar, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).applyTo(text);

		arrow = new Label(headbar, SWT.NONE);
		setArrowImage();
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(arrow);
		arrow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				toggle();
			}
		});

		FillLayout layout = new FillLayout();
		setLayout(layout);
		composite = new Composite(top, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(composite);
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(composite);

		expandBarManager.addExpandBar(this);

	}

	private void setArrowImage() {
		if (collapsed) {
			try {
				arrow.setImage(Activator.getImageDescriptor("/icons/expandbar/dropdownarrow.png").createImage());
			} catch (Exception e) {
				Logger.logError(e, "Icon dropdownarrow.png not found");
			}
		} else {
			try {
				arrow.setImage(Activator.getImageDescriptor("/icons/expandbar/dropuparrow.png").createImage());
			} catch (Exception e) {
				Logger.logError(e, "Icon dropuparrow.png not found");
			}
		}

	}

	private void toggle() {
		collapse(!collapsed);
	}

	public void collapse(boolean collapseMe) {
		collapsed = collapseMe;

		if (collapseMe) {
			GridDataFactory.fillDefaults().hint(0, 0).grab(true, false).applyTo(composite);

		} else {
			GridDataFactory.fillDefaults().indent(5, 0).applyTo(composite);
			// TODO: in Preferences :
			// this.expandBarManager.collapseOthers(this);

		}
		setArrowImage();
		this.getParent().layout(true);
	}

	public void setImage(Image image) {
		this.image.setImage(image);
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	public void addAction(final Action action) {
		Composite actionComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).applyTo(actionComposite);
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(actionComposite);

		Label actionImage = new Label(actionComposite, SWT.NONE);
		try {
			actionImage.setImage(action.getImageDescriptor().createImage());
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(0, 0).applyTo(actionImage);

		Label actionLabel = new Label(actionComposite, SWT.NONE);
		actionLabel.setText(action.getText());
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(0, 0).applyTo(actionImage);

		actionComposite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				action.run();
			}
		});

		actionImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				action.run();
			}
		});

		actionLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				action.run();
			}
		});


	}

}
