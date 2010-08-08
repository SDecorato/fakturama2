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
 * Class PriceValue
 * Stores a value as double and provides methods to get the value 
 * formated or rounded 
 * 
 * @author Gerd Bartelt
 */
public class PriceValue {
	private Double value;

	/**
	 * Constructor
	 * Creates a PriceValue from an other PriceValue
	 * 
	 * @param value Other PriceValue
	 */
	public PriceValue(PriceValue value) {
		this.value = value.asDouble();
	}

	/**
	 * Creates a PriceValue from a double value
	 * 
	 * @param value Value as double
	 */
	public PriceValue(Double value) {
		this.value = value;
	}

	/**
	 * Sets the PriceValue to an double
	 * 
	 * @param d New double value
	 */
	public void set(Double d) {
		this.value = d;
	}

	/**
	 * Adds a double to the PriceValue
	 * 
	 * @param d Double to add
	 */
	public void add(Double d) {
		this.value += d;
	}

	/**
	 * Get the PriceValue as Double
	 * 
	 * @return Value as Double
	 */
	public Double asDouble() {
		return value;
	}

	/**
	 * Get the PriceValue as rounded Double
	 * 
	 * @return Roundes value as Double 
	 */
	public Double asRoundedDouble() {
		return DataUtils.round(value);
	}

	/**
	 * Get the PriceValue as formated String
	 * 
	 * @return PriceValue as formated currency string
	 */
	public String asFormatedString() {
		return DataUtils.DoubleToFormatedPrice(value);
	}

	/**
	 * Get the PriceValue as formated and rounded String
	 * 
	 * @return PriceValue as formated and rounded currency string
	 */
	public String asFormatedRoundedString() {
		return DataUtils.DoubleToFormatedPriceRound(value);
	}


}
