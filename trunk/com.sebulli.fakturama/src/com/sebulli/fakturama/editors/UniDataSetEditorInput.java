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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.sebulli.fakturama.data.UniDataSet;
import com.sebulli.fakturama.logger.Logger;

public class UniDataSetEditorInput implements IEditorInput {

	private final UniDataSet uds;
	private String category = "";
	private boolean duplicate = false;

	public UniDataSetEditorInput(UniDataSet uds) {
		this.uds = uds;
		this.duplicate = false;
	}

	public UniDataSetEditorInput(String category, UniDataSet uds) {
		this.uds = uds;
		this.category = category;
		if ((category != null) && (uds != null))
			this.duplicate = true;
	}

	public UniDataSetEditorInput(String category) {
		this.uds = null;
		this.category = category;
		this.duplicate = false;
	}

	public UniDataSet getUniDataSet() {
		return uds;
	}

	public String getCategory() {
		return category;
	}

	public boolean getDuplicate() {
		return duplicate;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		if (uds == null)
			return "neu";
		return uds.getStringValueByKey("name");
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return this.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) { return true; }
		if (uds == null)
			return false;

		if (obj instanceof UniDataSetEditorInput) { return (uds.equals(((UniDataSetEditorInput) obj).getUniDataSet())
				&& category.equals(((UniDataSetEditorInput) obj).getCategory()) && duplicate == ((UniDataSetEditorInput) obj).getDuplicate()); }
		return false;
	}

	@Override
	public int hashCode() {
		if (uds == null) {
			Logger.logInfo("hashCode 0");
			return 0;
		}
		return uds.hashCode();
	}
}