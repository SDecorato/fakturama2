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

package com.sebulli.fakturama.views.datasettable;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.calculate.DataUtils;
import com.sebulli.fakturama.calculate.Price;
import com.sebulli.fakturama.data.DataSetArray;
import com.sebulli.fakturama.data.DataSetExpenditureItem;
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DataSetProduct;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.data.UniDataSet;

/**
 * This class represents a column of a table that contains UniDataSets
 * 
 * @author Gerd Bartelt
 */
public class UniDataSetTableColumn {
	
	// All used images are loaded by default and when the table row is
	// displayed. This makes the loading of the table faster.
	private static final Image CHECKED = Activator.getImageDescriptor("icons/16/checked_16.png").createImage();
	private static final Image OFFER = Activator.getImageDescriptor("icons/16/offer_16.png").createImage();
	private static final Image CONFIRMATION = Activator.getImageDescriptor("icons/16/confirmation_16.png").createImage();
	private static final Image ORDER = Activator.getImageDescriptor("icons/16/order_16.png").createImage();
	private static final Image INVOICE = Activator.getImageDescriptor("icons/16/invoice_16.png").createImage();
	private static final Image DELIVERY = Activator.getImageDescriptor("icons/16/delivery_16.png").createImage();
	private static final Image CREDIT = Activator.getImageDescriptor("icons/16/credit_16.png").createImage();
	private static final Image DUNNING = Activator.getImageDescriptor("icons/16/dunning_16.png").createImage();
	private static final Image LETTER = Activator.getImageDescriptor("icons/16/letter_16.png").createImage();
	private static final Image UNPAYED = Activator.getImageDescriptor("icons/16/error_16.png").createImage();
	private static final Image ORDER_PENDING = Activator.getImageDescriptor("icons/16/order_pending_16.png").createImage();
	private static final Image ORDER_PROCESSING = Activator.getImageDescriptor("icons/16/order_processing_16.png").createImage();
	private static final Image ORDER_SHIPPED = Activator.getImageDescriptor("icons/16/order_shipped_16.png").createImage();
	private int stdId = 0;

	/**
	 * Constructor
	 * Creates a UniDatSet table column with no editing support
	 * 
	 * @param tableColumnLayout The layout of the table column
	 * @param tableViewer The table viewer
	 * @param style SWT style of the column
	 * @param header The header of the column
	 * @param weight Width of the column
	 * @param minimumWidth The minimum width
	 * @param fixsize Set the width to a fix value
	 * @param dataKey Key that represents the column's data
	 */
	public UniDataSetTableColumn(TableColumnLayout tableColumnLayout, TableViewer tableViewer, int style, String header, int weight, int minimumWidth,
			boolean fixsize, final String dataKey) {
		this(tableColumnLayout, tableViewer, style, header, weight, minimumWidth, fixsize, dataKey, null);
	}

	/**
	 * Constructor
	 * Creates a UniDatSet table column with  editing support
	 * 
	 * @param tableColumnLayout The layout of the table column
	 * @param tableViewer The table viewer
	 * @param style SWT style of the column
	 * @param header The header of the column
	 * @param weight Width of the column
	 * @param minimumWidth The minimum width
	 * @param fixsize Set the width to a fix value
	 * @param dataKey Key that represents the column's data
	 * @param editingSupport The editing support of the cell
	 */
	public UniDataSetTableColumn(TableColumnLayout tableColumnLayout, final TableViewer tableViewer, int style, String header, int weight, int minimumWidth,
			boolean fixsize, final String dataKey, EditingSupport editingSupport) {

		// Create a TableViewerColum for the column
		TableViewerColumn viewerNameColumn = new TableViewerColumn(tableViewer, style);

		// Create a column and set the header text
		final TableColumn column = viewerNameColumn.getColumn();
		column.setText(header);

		// Add a selection listener
		column.addSelectionListener(new SelectionAdapter() {

			// The table column was selected
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// Get the data key of the column
				DataSetArray<?> datasets = (DataSetArray<?>) tableViewer.getInput();
				((TableSorter) tableViewer.getSorter()).setDataKey(datasets.getTemplate(), dataKey);

				// Get the sort order (direction)
				int dir = tableViewer.getTable().getSortDirection();
				
				// Toggle the direction
				if (tableViewer.getTable().getSortColumn() == column) {
					if (dir == SWT.UP)
						dir = SWT.DOWN;
					else
						dir = SWT.UP;
				} else {
					dir = SWT.DOWN;
				}

				// Set the new sort order (direction)
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		});

		// Set the column width as fix or variable width
		if (fixsize)
			tableColumnLayout.setColumnData(viewerNameColumn.getColumn(), new ColumnPixelData(weight));
		else
			tableColumnLayout.setColumnData(viewerNameColumn.getColumn(), new ColumnWeightData(weight, minimumWidth, true));

		// Add the editing support
		if (editingSupport != null)
			viewerNameColumn.setEditingSupport(editingSupport);

		// Add the label provider
		viewerNameColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				UniDataSet uds = (UniDataSet) cell.getElement();
				
				// Fill the cell with a UniData value, if the dataKey
				// does not start with a "$"
				if (!dataKey.startsWith("$")) {
					cell.setText(getText(uds, dataKey));
				}
				
				// Fill the cell with the icon for standard ID
				else if (dataKey.equals("$stdId")) {
					if (stdId == uds.getIntValueByKey("id"))
						cell.setImage(CHECKED);
					else
						cell.setImage(null);

				}
				
				// Fill the cell with the icon for status
				// eg. "payed/unpayed" for invoices
				else if (dataKey.equals("$status")) {
					switch (DocumentType.getType(uds.getIntValueByKey("category"))) {
					case INVOICE:
					case CREDIT:
						if (uds.getBooleanValueByKey("payed")) {
							cell.setImage(CHECKED);
						} else {
							cell.setImage(UNPAYED);
						}
						break;
					case DUNNING:
						if (uds.getBooleanValueByKey("payed")) {
							cell.setImage(CHECKED);
						} else {
							cell.setImage(UNPAYED);
						}
						break;
					case ORDER:
						switch (uds.getIntValueByKey("progress")) {
						case 0:
						case 10:
							cell.setImage(ORDER_PENDING);
							break;
						case 50:
							cell.setImage(ORDER_PROCESSING);
							break;
						case 90:
							cell.setImage(ORDER_SHIPPED);
							break;
						case 100:
							cell.setImage(CHECKED);
							break;
						}
						break;
					default:
						cell.setImage(null);
					}
					cell.setText(getText(uds, dataKey));
				}
				
				// Fill the cell with the icon of the document type
				else if (dataKey.equals("$documenttype")) {
					DocumentType documentType = DocumentType.getType(((UniDataSet) cell.getElement()).getIntValueByKey("category"));
					switch (documentType) {
					case LETTER:
						cell.setImage(LETTER);
						break;
					case OFFER:
						cell.setImage(OFFER);
						break;
					case CONFIRMATION:
						cell.setImage(CONFIRMATION);
						break;
					case ORDER:
						cell.setImage(ORDER);
						break;
					case INVOICE:
						cell.setImage(INVOICE);
						break;
					case DELIVERY:
						cell.setImage(DELIVERY);
						break;
					case CREDIT:
						cell.setImage(CREDIT);
						break;
					case DUNNING:
						cell.setImage(DUNNING);
						break;
					}
				}
				
				// Fill the cell with the VAT value
				else if (dataKey.equals("$vatbyid")) {
					cell.setText(((UniDataSet) cell.getElement()).getFormatedStringValueByKeyFromOtherTable("vatid.VATS:value"));
				}
				
				// Fill the cell with the total net value of the item
				else if (dataKey.equals("$ItemNetTotal")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getTotalNetRounded().asFormatedString());
				}
				
				// Fill the cell with the VAT (percent) value of the item
				else if (dataKey.equals("$ItemVatPercent")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getVatPercent());
				}
				
				// Fill the cell with the VAT (percent) value of the item
				else if (dataKey.equals("$ExpenditureItemVatPercent")) {
					cell.setText(new Price(((DataSetExpenditureItem) cell.getElement())).getVatPercent());
				}

				// Fill the cell with the total gross value of the item
				else if (dataKey.equals("$ItemGrossTotal")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getTotalGrossRounded().asFormatedString());
				}
				
				// Fill the cell with the gross price of the item
				else if (dataKey.equals("$ItemGrossPrice")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getUnitGross().asFormatedString());
				}

				// Fill the cell with the gross price of the item
				else if (dataKey.equals("$ExpenditureItemGrossPrice")) {
					cell.setText(new Price(((DataSetExpenditureItem) cell.getElement())).getUnitGross().asFormatedString());
				}

				// Fill the cell with the net price of the product (quantity = 1)
				else if (dataKey.equals("$Price1Net")) {
					DataSetProduct product = (DataSetProduct) cell.getElement();
					cell.setText(new Price(product.getDoubleValueByKey("price1"), product.getDoubleValueByKeyFromOtherTable("vatid.VATS:value") )
							.getUnitNet().asFormatedString());
				}

				// Fill the cell with the gross price of the product (quantity = 1)
				else if (dataKey.equals("$Price1Gross")) {
					DataSetProduct product = (DataSetProduct) cell.getElement();
					cell.setText(new Price(product.getDoubleValueByKey("price1"), product.getDoubleValueByKeyFromOtherTable("vatid.VATS:value") )
							.getUnitGross().asFormatedString());
				}

			}
		});

	}

	/**
	 * Get the value to fill the cell as text. 
	 * 
	 * @param uds The UniDataSet that contains the text
	 * @param dataKey The data key to access the UniDataSet
	 * @return The value as text string
	 */
	public static String getText(UniDataSet uds, String dataKey) {
		// Fill the cell direct with a UniData value, if the dataKey
		// does not start with a "$"
		if (!dataKey.startsWith("$")) {
			return uds.getFormatedStringValueByKey(dataKey);
		}
		
		// Fill the cell with the status
		// eg. "payed/unpayed" for invoices
		else if (dataKey.equals("$status")) {
			switch (DocumentType.getType(uds.getIntValueByKey("category"))) {
			case INVOICE:
			case CREDIT:
				if (uds.getBooleanValueByKey("payed")) {
					return "bezahlt";
				} else {
					return "nicht bezahlt";
				}
			case DUNNING:
				if (uds.getBooleanValueByKey("payed")) {
					return "bezahlt";
				} else {
					return uds.getStringValueByKey("dunninglevel") + ".Mahnung";
				}
			case ORDER:
				switch (uds.getIntValueByKey("progress")) {
				case 0:
				case 10:
					return "offen";
				case 50:
					return "in Bearbeitung";
				case 90:
					return "versendet";
				case 100:
					return "abgeschlossen";
				}
				break;
			default:
				return "";
			}

		}
		
		// Fill the cell with the document type
		else if (dataKey.equals("$documenttype")) {
			DocumentType documentType = DocumentType.getType(uds.getIntValueByKey("category"));
			return documentType.getString();
		}
		
		// Fill the cell with the VAT value
		else if (dataKey.equals("$vatbyid")) { return uds.getFormatedStringValueByKeyFromOtherTable("vatid.VATS:value"); }

		return "";
	}

	/**
	 * Get the value to fill the cell as number. 
	 * 
	 * @param uds The UniDataSet that contains the text
	 * @param dataKey The data key to access the UniDataSet
	 * @return The value as double
	 */
	public static Double getDoubleValue(UniDataSet uds, String dataKey) {

		// Get the UniData value, if the dataKey
		// does not start with a "$"
		if (!dataKey.startsWith("$")) {
			return uds.getDoubleValueByKey(dataKey);
			
		}
		
		// Get the value of the status
		// eg. "payed/unpayed" for invoices
		else if (dataKey.equals("$status")) {
			switch (DocumentType.getType(uds.getIntValueByKey("category"))) {
			case INVOICE:
			case CREDIT:
				return uds.getDoubleValueByKey("payed");
			case DUNNING:
				if (uds.getBooleanValueByKey("payed")) {
					return 0.0;
				} else {
					return uds.getDoubleValueByKey("dunninglevel");
				}
			case ORDER:
				return uds.getDoubleValueByKey("progress");
			default:
				return 0.0;
			}

		}
		
		// Get the number of the document type
		else if (dataKey.equals("$documenttype")) {
			return uds.getDoubleValueByKey("category");
		}
		
		// Get the VAT
		else if (dataKey.equals("$vatbyid")) { return DataUtils.StringToDouble(uds.getFormatedStringValueByKeyFromOtherTable("vatid.VATS:value")); }

		return 0.0;
	}

	/**
	 * Returns TRUE, if the dataKey describes a numeric value
	 * 
	 * @param uds The UnidataSet to test
	 * @param dataKey The data key
	 * @return TRUE, if the dataKey describes a numeric value
	 */
	public static boolean isNumeric(UniDataSet uds, String dataKey) {
		
		// If it does not start with a "$", test, if the UniDataSet contains
		// a numeric value.
		if (!dataKey.startsWith("$")) {
			return uds.getUniDataTypeByKey(dataKey).isNumeric();
		}
		
		// Status is not numeric
		else if (dataKey.equals("$status")) {
			return false;
		}
		
		// Document type is not numeric
		else if (dataKey.equals("$documenttype")) {
			return false;
		}
		
		// VAT is numeric
		else if (dataKey.equals("$vatbyid")) { return true; }

		// Price is numeric
		else if (dataKey.equals("$Price1Net")) { return true; }
		else if (dataKey.equals("$Price1Gross")) { return true; }


		return false;
	}

	/**
	 * Returns TRUE, if the dataKey describes a date value
	 * 
	 * @param uds The UnidataSet to test
	 * @param dataKey The data key
	 * @return TRUE, if the dataKey describes a date value
	 */
	public static boolean isDate(UniDataSet uds, String dataKey) {
		if (!dataKey.startsWith("$")) { return uds.getUniDataTypeByKey(dataKey).isDate(); }
		return false;
	}

	/**
	 * Set the standard entry
	 * 
	 * @param stdId ID of the standard entry
	 */
	public void setStdEntry(int stdId) {
		this.stdId = stdId;
	}

}
