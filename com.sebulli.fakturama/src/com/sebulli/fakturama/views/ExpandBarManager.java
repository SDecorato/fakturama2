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

package com.sebulli.fakturama.views;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all the expand bars
 * 
 * @author Gerd Bartelt
 */
public class ExpandBarManager {

	// List with all expand bars
	List<ExpandBar> expandBars = new ArrayList<ExpandBar>();

	/**
	 * Constructor Clears the list.
	 */
	public ExpandBarManager() {
		expandBars.clear();
	}

	/**
	 * Add a new expand bar
	 * 
	 * @param expandBar
	 *            A new expand bar
	 */
	public void addExpandBar(ExpandBar expandBar) {
		expandBars.add(expandBar);
	}

	/**
	 * Collapse the other expand bars
	 * 
	 * @param expandBar
	 *            Do not collapse this expand bar
	 */
	public void collapseOthers(ExpandBar expandBar) {

		for (ExpandBar nextExpandBar : expandBars) {
			if (nextExpandBar != expandBar)
				nextExpandBar.collapse(true);
		}
	}

}
