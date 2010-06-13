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

/**
 * Price class
 * 
 * Calculate gross values from net values.
 * Rounding of all values.
 * 
 * @author Gerd Bartelt
 */
public class Price {

	private Double quantity;
	private Double vatPercent;
	private Double discount;

	// unit values
	private Double unitPrice;
	private Double unitNet;
	private Double unitVat;
	private Double unitGross;

	// total values
	private Double totalNet;
	private Double totalVat;
	private Double totalGross;

	// unit values rounded
	private Double unitNetRounded;
	private Double unitVatRounded;
	private Double unitGrossRounded;

	// total values rounded
	private Double totalNetRounded;
	private Double totalVatRounded;
	private Double totalGrossRounded;

	/**
	 * Constructor
	 * Create a price value from an item
	 * 
	 * @param item Item as UniDataSet
	 */
	public Price(DataSetItem item) {
		this(item.getDoubleValueByKey("quantity"), item.getDoubleValueByKey("price"), item.getDoubleValueByKey("vatvalue"), item
				.getDoubleValueByKey("discount"), item.getBooleanValueByKey("novat"), false);
	}

	/**
	 * Constructor
	 * Create a price value from a net value
	 * 
	 * @param net Net value
	 */
	public Price(Double net) {
		this(net, 0.0);
	}

	/**
	 * Constructor
	 * Create a price value from a net value and a vat value
	 * 
	 * @param net Net value
	 * @param vatPercent VAT
	 */
	public Price(Double net, Double vatPercent) {
		this(1.0, net, vatPercent, 0.0, false, false);
	}

	/**
	 * Constructor
	 * Create a price value from a  value where value can be a net or a gross value
	 * 
	 * @param price Value (can be net or gross)
	 * @param vatPercent VAT value
	 * @param noVat true, if VAT should be 0.0
	 * @param asGross true, if price is a gross value
	 */
	public Price(Double price, Double vatPercent, boolean noVat, boolean asGross) {
		this(1.0, price, vatPercent, 0.0, noVat, asGross);
	}

	/**
	 * Constructor
	 * Create a price value from a price value with quantity, vat and discount
	 * 
	 * @param quantity Quantity 
	 * @param unitPrice Unit price (can be net or gross)
	 * @param vatPercent VAT value 
	 * @param discount Discount value
	 * @param noVat True, if VAT is 0.0
	 * @param asGross True, if price is a gross value
	 */
	public Price(Double quantity, Double unitPrice, Double vatPercent, Double discount, boolean noVat, boolean asGross) {

		// if noVat is set, the vat value is set to 0.0
		if (noVat)
			this.vatPercent = 0.0;
		else
			this.vatPercent = vatPercent;

		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.discount = discount;

		// do the calculation
		calculate(asGross);

	}

	/**
	 * Calculate the price and round all values
	 * 
	 * @param asGross true, if price is a gross value
	 */
	private void calculate(boolean asGross) {

		// Calculate net from gross
		if (asGross) {
			this.unitGross = this.unitPrice;
			this.unitNet = this.unitPrice / (1 + vatPercent);
		}
		// or gross from net
		else {
			this.unitGross = this.unitPrice * (1 + vatPercent);
			this.unitNet = this.unitPrice;
		}
		
		// Calculate the absolute VAT value from net value and VAT in percent
		this.unitVat = this.unitNet * vatPercent;

		// Calculate the discount factor.
		// Discount factor is a value between 0.0 and 1.0.
		// If the discount is -30% (-0.3), the discount factor is 0.7
		// Only discount values in the range -100% to -0% are allowed
		Double discountFactor = (1 + this.discount);
		if ((discountFactor > 1.0) || (discountFactor <= 0.0)) {
			Logger.logError("Discount value out of range: " + String.valueOf(this.discount));
			discountFactor = 1.0;
		}
		
		// Calculate the total values and use the quantity
		this.totalNet = this.quantity * this.unitNet * discountFactor;
		this.totalVat = this.quantity * this.unitVat * discountFactor;
		this.totalGross = this.quantity * this.unitGross * discountFactor;

		// Normally, the vat and gross value is rounded,
		// and the net value is the difference.
		// But only if the Net value is still a rounded value and the gross is not,
		// then the rounded gross value is calculated from rounded net and vat. 
		if ( ! DataUtils.isRounded(this.totalGross) && DataUtils.isRounded(this.totalNet)) {
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

	/**
	 * Get the VAT value in percent
	 * 
	 * @return VAT as formated string
	 */
	public String getVatPercent() {
		return DataUtils.DoubleToFormatedPercent(vatPercent);
	}

	/**
	 * Get the net value of one unit.
	 * 
	 * @return Net value as PriceValue
	 */
	public PriceValue getUnitNet() {
		return new PriceValue(unitNet);
	}

	/**
	 * Get the VAT value of one unit
	 * 
	 * @return VAT value as PriceValue
	 */
	public PriceValue getUnitVat() {
		return new PriceValue(unitVat);
	}

	/**
	 * Get the gross value of one unit.
	 * 
	 * @return Gross value as PriceValue
	 */
	public PriceValue getUnitGross() {
		return new PriceValue(unitGross);
	}

	/**
	 * Get the total net value.
	 * 
	 * @return Net value as PriceValue
	 */
	public PriceValue getTotalNet() {
		return new PriceValue(totalNet);
	}

	/**
	 * Get the total vat value.
	 * 
	 * @return Vat value as PriceValue
	 */
	public PriceValue getTotalVat() {
		return new PriceValue(totalVat);
	}

	/**
	 * Get the total gross value.
	 * 
	 * @return Gross value as PriceValue
	 */
	public PriceValue getTotalGross() {
		return new PriceValue(totalGross);
	}

	/**
	 * Get the net value of one unit as rounded value.
	 * 
	 * @return Net value as PriceValue
	 */
	public PriceValue getUnitNetRounded() {
		return new PriceValue(unitNetRounded);
	}

	/**
	 * Get the VAT value of one unit as rounded value.
	 * 
	 * @return VAT value as PriceValue
	 */
	public PriceValue getUnitVatRounded() {
		return new PriceValue(unitVatRounded);
	}

	/**
	 * Get the gross value of one unit as rounded value.
	 * 
	 * @return Gross value as PriceValue
	 */
	public PriceValue getUnitGrossRounded() {
		return new PriceValue(unitGrossRounded);
	}

	/**
	 * Get the total net as rounded value.
	 * 
	 * @return Net value as PriceValue
	 */
	public PriceValue getTotalNetRounded() {
		return new PriceValue(totalNetRounded);
	}

	/**
	 * Get the total vat as rounded value.
	 * 
	 * @return Vat value as PriceValue
	 */
	public PriceValue getTotalVatRounded() {
		return new PriceValue(totalVatRounded);
	}

	/**
	 * Get the total gross as rounded value.
	 * 
	 * @return Gross value as PriceValue
	 */
	public PriceValue getTotalGrossRounded() {
		return new PriceValue(totalGrossRounded);
	}

}
