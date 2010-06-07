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

public class PriceValue {
	private Double value;

	public PriceValue(PriceValue value) {
		this.value = value.asDouble();
	}

	public PriceValue(Double value) {
		this.value = value;
	}

	public void set(Double d) {
		this.value = d;
	}

	public void add(Double d) {
		this.value += d;
	}

	public Double asDouble() {
		return value;
	}

	public Double asRoundedDouble() {
		return DataUtils.round(value);
	}

	public String asFormatedString() {
		return DataUtils.DoubleToFormatedPrice(value);
	}

	public void setValue(Double value) {
		this.value = value;
	}

}
