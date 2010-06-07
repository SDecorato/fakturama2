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

import java.util.ArrayList;
import java.util.Iterator;

import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DataSetShipping;

public class DocumentSummary {
	private Price shipping;
	private Price discount;
	private PriceValue itemsNet;
	private PriceValue itemsGross;
	private PriceValue totalNet;
	private PriceValue totalVat;
	private PriceValue totalGross;
	private Double shippingNet = 0.0;

	public DocumentSummary() {
		shipping = new Price(0.0, 0.0);
		itemsNet = new PriceValue(0.0);
		totalVat = new PriceValue(0.0);
		totalGross = new PriceValue(0.0);
	}

	public void calculate(VatSummarySet vatSummaryItems, DataSetArray<DataSetItem> items, double shippingValue, double shippingVatPercent,
			String shippingVatDescription, int shippingAutoVat, Double itemsDiscount, boolean noVat, String noVatDescription) {
		Double vatPercent;
		String vatDescription;
		VatSummarySet documentVatSummaryItems = new VatSummarySet();
		this.itemsNet = new PriceValue(0.0);
		this.totalVat = new PriceValue(0.0);
		this.totalGross = new PriceValue(0.0);

		ArrayList<DataSetItem> itemDataset = items.getActiveDatasets();
		for (DataSetItem item : itemDataset) {
			vatDescription = item.getStringValueByKey("vatdescription");
			vatPercent = item.getDoubleValueByKey("vatvalue");
			Price price = new Price(item);
			this.itemsNet.add(price.getTotalNetRounded().asDouble());
			Double itemVat = price.getTotalVatRounded().asDouble();
			if (noVat) {
				vatDescription = noVatDescription;
				vatPercent = 0.0;
				itemVat = 0.0;
			}
			this.totalVat.add(itemVat);
			VatSummaryItem vatSummaryItem = new VatSummaryItem(vatDescription, vatPercent, price.getTotalNetRounded().asDouble(), itemVat);

			documentVatSummaryItems.add(vatSummaryItem);
			if (vatSummaryItems != null)
				vatSummaryItems.add(vatSummaryItem);
		}

		this.totalNet = new PriceValue(this.itemsNet);
		this.itemsGross = new PriceValue(this.itemsNet);
		this.itemsGross.add(this.totalVat.asDouble());

		Double itemsNet = this.itemsNet.asDouble();
		Double itemsGross = this.itemsGross.asDouble();

		// calculate Discount
		if (!DataUtils.DoublesAreEqual(itemsDiscount, 0.0)) {
			Double discountVatPercent;

			Double discountNet = itemsNet * itemsDiscount;

			if (itemsNet != 0.0)
				discountVatPercent = (itemsGross / itemsNet) - 1;
			else
				discountVatPercent = 0.0;

			if (noVat) {
				discountVatPercent = 0.0;
			}

			discount = new Price(discountNet, discountVatPercent);

			Double discountVatValue = 0.0;
			String discountVatDescription = "";
			for (Iterator<VatSummaryItem> iterator = documentVatSummaryItems.iterator(); iterator.hasNext();) {
				VatSummaryItem vatSummaryItem = iterator.next();
				discountVatDescription = vatSummaryItem.getVatName();
				discountVatPercent = vatSummaryItem.getVatPercent();

				if (noVat) {
					discountVatDescription = noVatDescription;
					discountVatPercent = 0.0;
				}

				Double discountNetPart = 0.0;
				if (itemsNet != 0.0)
					discountNetPart = discountNet * (vatSummaryItem.getNet() / itemsNet);

				Price discountPart = new Price(discountNetPart, discountVatPercent);
				discountVatValue += discountPart.getUnitVatRounded().asDouble();
				documentVatSummaryItems.add(new VatSummaryItem(discountVatDescription, discountVatPercent, discountPart.getUnitNetRounded().asDouble(),
						discountPart.getUnitVatRounded().asDouble()));
			}

			this.totalVat.add(discountVatValue);
			this.totalNet.add(discount.getUnitNetRounded().asDouble());
			this.totalGross.set(this.totalNet.asDouble() + this.totalVat.asDouble());
		}

		// calculate Shipping
		if (shippingAutoVat != DataSetShipping.SHIPPINGVATAUTO) {

			// Double itemsNet = this.itemsNet.asDouble();
			// Double itemsGross = this.itemsGross.asDouble();

			if (shippingAutoVat == DataSetShipping.SHIPPINGVATGROSS) {
				if (itemsGross != 0.0)
					shippingNet = shippingValue * itemsNet / itemsGross;
				else
					shippingNet = shippingValue;

			}
			if (shippingAutoVat == DataSetShipping.SHIPPINGVATNET)
				shippingNet = shippingValue;

			if (itemsNet != 0.0)
				shippingVatPercent = (itemsGross / itemsNet) - 1;
			else
				shippingVatPercent = 0;

			shipping = new Price(shippingNet, shippingVatPercent);

			Double shippingVatValue = 0.0;

			for (Iterator<VatSummaryItem> iterator = documentVatSummaryItems.iterator(); iterator.hasNext();) {
				VatSummaryItem vatSummaryItem = iterator.next();
				shippingVatDescription = vatSummaryItem.getVatName();
				shippingVatPercent = vatSummaryItem.getVatPercent();
				if (noVat) {
					shippingVatDescription = noVatDescription;
					shippingVatPercent = 0.0;
				}

				Double shippingNetPart = 0.0;
				if (itemsNet != 0.0)
					shippingNetPart = shippingNet * (vatSummaryItem.getNet() / itemsNet);

				Price shippingPart = new Price(shippingNetPart, shippingVatPercent);
				shippingVatValue += shippingPart.getUnitVatRounded().asDouble();
				documentVatSummaryItems.add(new VatSummaryItem(shippingVatDescription, shippingVatPercent, shippingPart.getUnitNetRounded().asDouble(),
						shippingPart.getUnitVatRounded().asDouble()));
			}

			this.totalVat.add(shippingVatValue);
			this.totalNet.add(shipping.getUnitNetRounded().asDouble());
			this.totalGross.set(this.totalNet.asDouble() + this.totalVat.asDouble());

		} else {
			shippingNet = shippingValue;
			if (noVat) {
				shippingVatDescription = noVatDescription;
				shippingVatPercent = 0.0;
			}
			shipping = new Price(shippingNet, shippingVatPercent);

			this.totalVat.add(shipping.getUnitVatRounded().asDouble());
			if (vatSummaryItems != null)
				vatSummaryItems.add(new VatSummaryItem(shippingVatDescription, shippingVatPercent, shipping.getTotalNetRounded().asDouble(), shipping
						.getTotalVatRounded().asDouble()));

			this.totalGross.set(this.itemsNet.asDouble() + this.totalVat.asDouble() + shipping.getUnitNetRounded().asDouble());
		}
	}

	public Price getShipping() {
		return this.shipping;
	}

	public PriceValue getItemsNet() {
		return this.itemsNet;
	}

	public PriceValue getItemsGross() {
		return this.itemsGross;
	}

	public PriceValue getTotalNet() {
		return this.totalNet;
	}

	public PriceValue getTotalVat() {
		return this.totalVat;
	}

	public PriceValue getTotalGross() {
		return this.totalGross;
	}

}
