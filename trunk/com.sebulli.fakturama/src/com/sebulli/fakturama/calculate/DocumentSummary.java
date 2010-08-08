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

/**
 * Calculates the tax, gross and sum of one document.
 * This is the central calculation used by the document editors
 * and the export functions.
 * 
 * @author Gerd Bartelt
 */
public class DocumentSummary {
	
	// sum off items
	private PriceValue itemsNet;
	private PriceValue itemsGross;
	
	// total sum
	private PriceValue totalNet;
	private PriceValue totalVat;
	private PriceValue totalGross;

	// shipping value
	private Price shipping;
	private Double shippingNet;

	/**
	 * Default constructor. Resets all value to 0.
	 */
	public DocumentSummary() {
		itemsNet = new PriceValue(0.0);
		itemsGross = new PriceValue(0.0);
		totalNet = new PriceValue(0.0);
		totalVat = new PriceValue(0.0);
		totalGross = new PriceValue(0.0);
		shipping = new Price(0.0, 0.0);
		shippingNet = 0.0;
	}

	/**
	 * Calculates the tax, gross and sum of a document 
	 * 
	 * @param globalVatSummarySet The documents vat is added to this global VAT summary set. 
	 * @param items Document's items
	 * @param shippingValue Document's shipping
	 * @param shippingVatPercent Shipping's VAT - This is only used, if the shipping's VAT is not calculated based on the items. 
	 * @param shippingVatDescription Shipping's VAT name
	 * @param shippingAutoVat If TRUE, the shipping VAT is based on the item's VAT
	 * @param itemsDiscount Discount value
	 * @param noVat TRUE, if all VAT values are set to 0. 
	 * @param noVatDescription Name of the VAT, which is 0.
	 */
	public void calculate(VatSummarySet globalVatSummarySet, DataSetArray<DataSetItem> items, double shippingValue, double shippingVatPercent,
			String shippingVatDescription, int shippingAutoVat, Double itemsDiscount, boolean noVat, String noVatDescription) {
		
		Double vatPercent;
		String vatDescription;
		
		// This Vat summary contains only the VAT entries of this document,
		// whereas the the parameter vatSummaryItems is a global VAT summary
		// and contains entries from this document and from others.
		VatSummarySet documentVatSummaryItems = new VatSummarySet();
		
		// Set the sums to 0.0
		this.itemsNet = new PriceValue(0.0);
		this.totalVat = new PriceValue(0.0);
		this.totalGross = new PriceValue(0.0);

		// Use all non-deleted items
		ArrayList<DataSetItem> itemDataset = items.getActiveDatasets();
		for (DataSetItem item : itemDataset) {
			
			// Get the data from each item
			vatDescription = item.getStringValueByKey("vatdescription");
			vatPercent = item.getDoubleValueByKey("vatvalue");
			Price price = new Price(item);
			Double itemVat = price.getTotalVatRounded().asDouble();
			
			// Add the total net value of this item to the sum of net items
			this.itemsNet.add( price.getTotalNetRounded().asDouble() );
			
			// If noVat is set, the VAT is 0%
			if (noVat) {
				vatDescription = noVatDescription;
				vatPercent = 0.0;
				itemVat = 0.0;
			}
			
			// Add the VAT to the sum of VATs
			this.totalVat.add(itemVat);
			
			// Add the VAT summary item to the ... 
			VatSummaryItem vatSummaryItem = new VatSummaryItem(vatDescription, vatPercent, price.getTotalNetRounded().asDouble(), itemVat);
			
			// .. VAT summary of the document ..
			documentVatSummaryItems.add(vatSummaryItem);
			
			// .. and maybe to the global VAT summary
			if (globalVatSummarySet != null)
				globalVatSummarySet.add(vatSummaryItem);
		}

		// Gross value is the sum of net and VAT value
		this.totalNet = new PriceValue(this.itemsNet);
		this.itemsGross = new PriceValue(this.itemsNet);
		this.itemsGross.add(this.totalVat.asDouble());

		Double itemsNet = this.itemsNet.asDouble();
		Double itemsGross = this.itemsGross.asDouble();

		// Calculate discount
		if (!DataUtils.DoublesAreEqual(itemsDiscount, 0.0)) {

			// Discount value = discount percent * Net value
			Double discountNet = itemsDiscount * itemsNet;

			// Calculate the vat value in percent from the gross value of all items
			// and the net value of all items. So the discount's vat is the average 
			// value of the item's vat
			Double discountVatPercent;
			if (itemsNet != 0.0)
				discountVatPercent = (itemsGross / itemsNet) - 1;
			else
				// do not divide by zero
				discountVatPercent = 0.0;

			// If noVat is set, the VAT is 0%
			if (noVat) {
				discountVatPercent = 0.0;
			}


			// Reduce all the VAT entries in the VAT Summary Set by the discount 
			Double discountVatValue = 0.0;
			String discountVatDescription = "";
			for (Iterator<VatSummaryItem> iterator = documentVatSummaryItems.iterator(); iterator.hasNext();) {
				
				// Get the data from each entry
				VatSummaryItem vatSummaryItem = iterator.next();
				discountVatDescription = vatSummaryItem.getVatName();
				discountVatPercent = vatSummaryItem.getVatPercent();

				// If noVat is set, the VAT is 0%
				if (noVat) {
					discountVatDescription = noVatDescription;
					discountVatPercent = 0.0;
				}

				// Calculate the ratio of this vat summary item and all items.
				// The discountNetPart is proportional to this ratio.
				Double discountNetPart = 0.0;
				if (itemsNet != 0.0)
					discountNetPart = discountNet * (vatSummaryItem.getNet() / itemsNet);

				// Add discountNetPart to the sum "discountVatValue"  
				Price discountPart = new Price(discountNetPart, discountVatPercent);
				discountVatValue += discountPart.getUnitVatRounded().asDouble();
				
				VatSummaryItem discountVatSummaryItem = new VatSummaryItem(discountVatDescription, discountVatPercent,
																discountPart.getUnitNetRounded().asDouble(),
																discountPart.getUnitVatRounded().asDouble());

				// Adjust the vat summary item by the discount part
				documentVatSummaryItems.add(discountVatSummaryItem);

				// Add it to the global VAT summary
				if (globalVatSummarySet != null)
					globalVatSummarySet.add(discountVatSummaryItem);
				
			}

			// adjust the documents sum by the discount
			this.totalVat.add(discountVatValue);
			this.totalNet.add(new Price(discountNet, discountVatPercent).getUnitNetRounded().asDouble());
			this.totalGross.set(this.totalNet.asDouble() + this.totalVat.asDouble());
		}

		// calculate shipping
		// If shippingAutoVat is not fix, the shipping vat is 
		// an average value of the vats of the items.
		if (shippingAutoVat != DataSetShipping.SHIPPINGVATFIX) {

			// If the shipping is set as gross value, calculate the net value.
			// Use the average vat of all the items.
			if (shippingAutoVat == DataSetShipping.SHIPPINGVATGROSS) {
				if (itemsGross != 0.0)
					shippingNet = shippingValue * itemsNet / itemsGross;
				else
					shippingNet = shippingValue;

			}
			
			// If the shipping is set as net value, use the net value.
			if (shippingAutoVat == DataSetShipping.SHIPPINGVATNET)
				shippingNet = shippingValue;

			// Use the average vat of all the items.
			if (itemsNet != 0.0)
				shippingVatPercent = (itemsGross / itemsNet) - 1;
			else
				shippingVatPercent = 0;

			shipping = new Price(shippingNet, shippingVatPercent);

			// Increase the vat summary entries by the shipping ratio
			Double shippingVatValue = 0.0;
			for (Iterator<VatSummaryItem> iterator = documentVatSummaryItems.iterator(); iterator.hasNext();) {
				
				// Get the data from each entry
				VatSummaryItem vatSummaryItem = iterator.next();
				shippingVatDescription = vatSummaryItem.getVatName();
				shippingVatPercent = vatSummaryItem.getVatPercent();
				
				// If noVat is set, the VAT is 0%
				if (noVat) {
					shippingVatDescription = noVatDescription;
					shippingVatPercent = 0.0;
				}

				// Calculate the ratio of this vat summary item and all items.
				// The shippingNetPart is proportional to this ratio.
				Double shippingNetPart = 0.0;
				if (itemsNet != 0.0)
					shippingNetPart = shippingNet * (vatSummaryItem.getNet() / itemsNet);

				// Add shippingNetPart to the sum "shippingVatValue"  
				Price shippingPart = new Price(shippingNetPart, shippingVatPercent);
				shippingVatValue += shippingPart.getUnitVatRounded().asDouble();
				
				VatSummaryItem shippingVatSummaryItem = new VatSummaryItem(shippingVatDescription, shippingVatPercent,
															shippingPart.getUnitNetRounded().asDouble(),
															shippingPart.getUnitVatRounded().asDouble());
				
				// Adjust the vat summary item by the shipping part
				documentVatSummaryItems.add(shippingVatSummaryItem);
				
				// Add it to the global VAT summary
				if (globalVatSummarySet != null)
					globalVatSummarySet.add(shippingVatSummaryItem);

					

			}

			// Add the shipping to the documents sum.
			this.totalVat.add(shippingVatValue);
			this.totalNet.add(shipping.getUnitNetRounded().asDouble());
			this.totalGross.set(this.totalNet.asDouble() + this.totalVat.asDouble());
		} 
		
		// If shippingAutoVat is fix set, the shipping vat is 
		// a constant percent value.
		else {
			
			shippingNet = shippingValue;
			
			// If noVat is set, the VAT is 0%
			if (noVat) {
				shippingVatDescription = noVatDescription;
				shippingVatPercent = 0.0;
			}
			
			// use shippingVatPercent as fix percent value for the shipping
			shipping = new Price(shippingNet, shippingVatPercent);

			VatSummaryItem shippingVatSummaryItem  = new VatSummaryItem(shippingVatDescription, shippingVatPercent,
															shipping.getTotalNetRounded().asDouble(),
															shipping.getTotalVatRounded().asDouble());
			
			// add the shipping's vat to the global vat summary set
			if (globalVatSummarySet != null)
				globalVatSummarySet.add(shippingVatSummaryItem);

			// add the shipping to the documents sum
			this.totalVat.add(shipping.getUnitVatRounded().asDouble());
			this.totalGross.set(this.itemsNet.asDouble() + this.totalVat.asDouble() + shipping.getUnitNetRounded().asDouble());
		}
	}

	/**
	 * Getter for shipping value
	 * @return shipping as Price
	 */
	public Price getShipping() {
		return this.shipping;
	}

	/**
	 * Getter for sum of items (net)
	 * @return Sum as PriceValue
	 */
	public PriceValue getItemsNet() {
		return this.itemsNet;
	}

	/**
	 * Getter for sum of items (gross)
	 * @return Sum as PriceValue
	 */
	public PriceValue getItemsGross() {
		return this.itemsGross;
	}

	/**
	 * Getter for total document sum (net)
	 * @return Sum as PriceValue
	 */
	public PriceValue getTotalNet() {
		return this.totalNet;
	}

	/**
	 * Getter for total document sum (vat)
	 * @return Sum as PriceValue
	 */
	public PriceValue getTotalVat() {
		return this.totalVat;
	}

	/**
	 * Getter for total document sum (gross)
	 * @return Sum as PriceValue
	 */
	public PriceValue getTotalGross() {
		return this.totalGross;
	}

}
