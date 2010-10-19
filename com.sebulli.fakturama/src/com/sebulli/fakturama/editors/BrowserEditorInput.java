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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Browser editor input
 * 
 * @author Gerd Bartelt
 */
public class BrowserEditorInput implements IEditorInput {

	String url;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            Url of the web browser
	 */
	public BrowserEditorInput(String url) {
		this.url = url;
	}

	/**
	 * Returns whether the editor input exists
	 * 
	 * @return null
	 */
	@Override
	public boolean exists() {
		return false;
	}

	/**
	 * The editors image descriptor
	 * 
	 * @return null: there is no image
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * Returns the name of this editor input for display purposes
	 * 
	 * @return the url "fakturama.sebulli.com"
	 */
	@Override
	public String getName() {
		return "fakturama.sebulli.com";
	}

	/**
	 * Returns an object that can be used to save the state of this editor
	 * input.
	 * 
	 * @return null
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Returns the tool tip text for this editor input
	 * 
	 * @return The editors name
	 */
	@Override
	public String getToolTipText() {
		return this.getName();
	}

	/**
	 * Returns an object which is an instance of the given class associated with
	 * this object.
	 * 
	 * @return null: there is no such object
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 *            the object to compare
	 * @return True, if it it equal to this object
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) { return true; }
		if (url == null)
			return false;

		// Compate the URLs
		if (obj instanceof BrowserEditorInput) { return url.equals(((BrowserEditorInput) obj).getUrl()); }
		return false;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return Hash code of the URL
	 */
	@Override
	public int hashCode() {
		return url.hashCode();
	}

	/**
	 * Returns the URL
	 * 
	 * @return URL as string
	 */
	public String getUrl() {
		return this.url;
	}
}