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

public class VatSummaryItem implements Comparable<Object> {

	private PriceValue net;
	private PriceValue vat;
	private String vatName;
	private Double vatPercent;

	public VatSummaryItem(String vatName, Double vatPercent, Double net, Double vat) {
		this.vatName = vatName;
		this.vatPercent = vatPercent;
		this.net = new PriceValue(net);
		this.vat = new PriceValue(vat);
	}

	public Double getNet() {
		return net.asDouble();
	}

	public void add(VatSummaryItem other) {
		this.net.add(other.net.asDouble());
		this.vat.add(other.vat.asDouble());
	}

	public Double getVat() {
		return vat.asDouble();
	}

	public String getVatName() {
		return vatName;
	}

	public Double getVatPercent() {
		return vatPercent;
	}

	@Override
	public int compareTo(Object o) {
		VatSummaryItem other = (VatSummaryItem) o;
		if (this.vatPercent < other.vatPercent)
			return -1;
		if (this.vatPercent > other.vatPercent)
			return 1;

		return this.vatName.compareToIgnoreCase(other.vatName);
	}
}
