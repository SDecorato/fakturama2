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

import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.logger.Logger;

public class Price {

	private Double unitPrice;
	private Double unitNet;
	private Double unitVat;
	private Double unitGross;

	private Double discount;

	private Double totalNet;
	private Double totalVat;
	private Double totalGross;

	private Double unitNetRounded;
	private Double unitVatRounded;
	private Double unitGrossRounded;

	private Double totalNetRounded;
	private Double totalVatRounded;
	private Double totalGrossRounded;

	private Double vatPercent;
	private Double quantity;

	public Price(DataSetItem item) {
		this(item.getDoubleValueByKey("quantity"), item.getDoubleValueByKey("price"), item.getDoubleValueByKey("vatvalue"), item
				.getDoubleValueByKey("discount"), item.getBooleanValueByKey("novat"), false);
	}

	public Price(Double net) {
		this(net, 0.0);
	}

	public Price(Double net, Double vatPercent) {
		this(1.0, net, vatPercent, 0.0, false, false);
	}

	public Price(Double price, Double vatPercent, boolean noVat, boolean asGross) {
		this(1.0, price, vatPercent, 0.0, noVat, asGross);
	}

	private void calculate(boolean asGross) {

		if (asGross) {
			this.unitGross = this.unitPrice;
			this.unitNet = this.unitPrice / (1 + vatPercent);
		} else {
			this.unitGross = this.unitPrice * (1 + vatPercent);
			this.unitNet = this.unitPrice;
		}
		this.unitVat = this.unitNet * vatPercent;

		Double discountFactor = (1 + this.discount);
		if ((discountFactor > 1.0) || (discountFactor <= 0.0)) {
			Logger.logError("Discount value out of range: " + String.valueOf(this.discount));
			discountFactor = 1.0;
		}

		this.totalNet = this.quantity * this.unitNet * discountFactor;
		this.totalVat = this.quantity * this.unitVat * discountFactor;
		this.totalGross = this.quantity * this.unitGross * discountFactor;

		if (!DataUtils.isRounded(this.totalGross) && DataUtils.isRounded(this.totalNet)) {
			this.unitNetRounded = DataUtils.round(unitNet);
			this.unitVatRounded = DataUtils.round(unitVat);
			this.unitGrossRounded = this.unitNetRounded + this.unitVatRounded;

			this.totalNetRounded = DataUtils.round(totalNet);
			this.totalVatRounded = DataUtils.round(totalVat);
			this.totalGrossRounded = this.totalNetRounded + this.totalVatRounded;
		} else {
			this.unitGrossRounded = DataUtils.round(unitGross);
			this.unitVatRounded = DataUtils.round(unitVat);
			this.unitNetRounded = this.unitGrossRounded - this.unitVatRounded;

			this.totalGrossRounded = DataUtils.round(totalGross);
			this.totalVatRounded = DataUtils.round(totalVat);
			this.totalNetRounded = this.totalGrossRounded - this.totalVatRounded;
		}
	}

	public Price(Double quantity, Double unitPrice, Double vatPercent, Double discount, boolean noVat, boolean asGross) {

		this.quantity = quantity;
		if (noVat)
			this.vatPercent = 0.0;
		else
			this.vatPercent = vatPercent;

		this.unitPrice = unitPrice;
		this.discount = discount;

		calculate(asGross);

	}

	public String getVatPercent() {
		return DataUtils.DoubleToFormatedPercent(vatPercent);
	}

	public PriceValue getUnitNet() {
		return new PriceValue(unitNet);
	}

	public PriceValue getUnitVat() {
		return new PriceValue(unitVat);
	}

	public PriceValue getUnitGross() {
		return new PriceValue(unitGross);
	}

	public PriceValue getTotalNet() {
		return new PriceValue(totalNet);
	}

	public PriceValue getTotalVat() {
		return new PriceValue(totalVat);
	}

	public PriceValue getTotalGross() {
		return new PriceValue(totalGross);
	}

	public PriceValue getUnitNetRounded() {
		return new PriceValue(unitNetRounded);
	}

	public PriceValue getUnitVatRounded() {
		return new PriceValue(unitVatRounded);
	}

	public PriceValue getUnitGrossRounded() {
		return new PriceValue(unitGrossRounded);
	}

	public PriceValue getTotalNetRounded() {
		return new PriceValue(totalNetRounded);
	}

	public PriceValue getTotalVatRounded() {
		return new PriceValue(totalVatRounded);
	}

	public PriceValue getTotalGrossRounded() {
		return new PriceValue(totalGrossRounded);
	}

}
