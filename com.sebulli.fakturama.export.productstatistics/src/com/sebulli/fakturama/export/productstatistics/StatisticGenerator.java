/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2011 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package com.sebulli.fakturama.export.productstatistics;

import java.util.GregorianCalendar;


/**
 * The statistic generator. 
 * This class collects all the sold products 
 * 
 * @author Gerd Bartelt
 */
public class StatisticGenerator {

	// The begin and end date to specify the export periode
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;

	// the date key to sort the documents
	private String documentDateKey;

	// Settings from the preference page
	boolean showExpenditureSumColumn;
	boolean showZeroVatColumn;
	boolean usePaidDate;

	/**
	 * Default constructor
	 */
	public StatisticGenerator() {
		this.startDate = null;
		this.endDate = null;
	}

	/**
	 * Constructor Sets the begin and end date
	 * 
	 * @param startDate
	 *            Begin date
	 * @param endDate
	 *            Begin date
	 */
	public StatisticGenerator(GregorianCalendar startDate, GregorianCalendar endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	// Do the export job.
	public boolean export() {

		// True = Export was successful
		return true;
	}

}
