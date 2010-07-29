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

package com.sebulli.fakturama.calculate;

/**
 * This Class represents one entry in the VatSummarySet.
 * It contains a net and vat value and the vat name.
 * 
 * @author Gerd Bartelt
 */
public class VatSummaryItem implements Comparable<Object> {

	// Absolute Net and Vat value
	// This can be the sum of more than one item
	private PriceValue net;
	private PriceValue vat;
	// Vat Name and Percent Value. These values identify the VatSummaryItem
	private String vatName;
	private Double vatPercent;

	/**
	 * Constructor
	 * Creates a VatSummaryItem from a net and vat value and the vat name.
	 * 
	 * @param vatName Vat name
	 * @param vatPercent Vat value in percent
	 * @param net Absolute Net value
	 * @param vat Absolute Vat value
	 */
	public VatSummaryItem(String vatName, Double vatPercent, Double net, Double vat) {
		this.vatName = vatName;
		this.vatPercent = vatPercent;
		this.net = new PriceValue(net);
		this.vat = new PriceValue(vat);
	}

	/**
	 * Add the net and vat value from an other VatSummaryItem.
	 * 
	 * @param other The other VatSummaryItem
	 */
	public void add(VatSummaryItem other) {
		this.net.add(other.net.asDouble());
		this.vat.add(other.vat.asDouble());
	}

	/**
	 * Get the absolute net value
	 * 
	 * @return Net value as PriceValue
	 */
	public Double getNet() {
		return net.asDouble();
	}

	/** 
	 * Get the absolute vat value
	 * 
	 * @return Vat value as PriceValue
	 */
	public Double getVat() {
		return vat.asDouble();
	}

	/**
	 * Get the name of the vat
	 * 
	 * @return Vat name as string
	 */
	public String getVatName() {
		return vatName;
	}

	/**
	 * Percent value of this VatSummaryItem
	 * 
	 * @return Vat in percent
	 */
	public Double getVatPercent() {
		return vatPercent;
	}

	/**
	 * Compares this VatSummaryItem with an other
	 * Compares vat percent value and vat name.
	 * 
	 * @param o The other VatSummaryItem
	 * @return result of the comparison
	 */
	@Override
	public int compareTo(Object o) {
		VatSummaryItem other = (VatSummaryItem) o;
		
		// First compare the vat value in percent
		if (this.vatPercent < other.vatPercent)
			return -1;
		if (this.vatPercent > other.vatPercent)
			return 1;

		// Then the vat name
		return this.vatName.compareToIgnoreCase(other.vatName);
	}
}