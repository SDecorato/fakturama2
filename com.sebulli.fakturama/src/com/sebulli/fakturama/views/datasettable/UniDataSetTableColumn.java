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
import com.sebulli.fakturama.data.DataSetItem;
import com.sebulli.fakturama.data.DocumentType;
import com.sebulli.fakturama.data.UniDataSet;

public class UniDataSetTableColumn {
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

	public UniDataSetTableColumn(TableColumnLayout tableColumnLayout, TableViewer tableViewer, int style, String header, int weight, int minimumWidth,
			boolean fixsize, final String dataKey) {
		this(tableColumnLayout, tableViewer, style, header, weight, minimumWidth, fixsize, dataKey, null);
	}

	public UniDataSetTableColumn(TableColumnLayout tableColumnLayout, final TableViewer tableViewer, int style, String header, int weight, int minimumWidth,
			boolean fixsize, final String dataKey, EditingSupport editingSupport) {

		// Für jede Spalte ein TableViewerColumn erzeugen
		TableViewerColumn viewerNameColumn = new TableViewerColumn(tableViewer, style);
		final TableColumn column = viewerNameColumn.getColumn();
		column.setText(header);

		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataSetArray<?> datasets = (DataSetArray<?>) tableViewer.getInput();
				((TableSorter) tableViewer.getSorter()).setDataKey(datasets.getTemplate(), dataKey);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		});

		if (fixsize)
			tableColumnLayout.setColumnData(viewerNameColumn.getColumn(), new ColumnPixelData(weight));
		else
			tableColumnLayout.setColumnData(viewerNameColumn.getColumn(), new ColumnWeightData(weight, minimumWidth, true));

		if (editingSupport != null)
			viewerNameColumn.setEditingSupport(editingSupport);

		// LabelProvider für jede Spalte setzen
		viewerNameColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				UniDataSet uds = (UniDataSet) cell.getElement();
				if (!dataKey.startsWith("$")) {
					cell.setText(getText(uds, dataKey));
				} else if (dataKey.equals("$stdId")) {
					if (stdId == uds.getIntValueByKey("id"))
						cell.setImage(CHECKED);
					else
						cell.setImage(null);

				} else if (dataKey.equals("$status")) {
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
							cell.setImage(null);
							break;
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
				} else if (dataKey.equals("$documenttype")) {
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
				} else if (dataKey.equals("$vatbyid")) {
					cell.setText(((UniDataSet) cell.getElement()).getFormatedStringValueByKeyFromOtherTable("vatid.VATS:value"));
				} else if (dataKey.equals("$ItemNetTotal")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getTotalNetRounded().asFormatedString());
				} else if (dataKey.equals("$ItemVatPercent")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getVatPercent());
				} else if (dataKey.equals("$ItemGrossTotal")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getTotalGrossRounded().asFormatedString());
				} else if (dataKey.equals("$ItemGrossPrice")) {
					cell.setText(new Price(((DataSetItem) cell.getElement())).getUnitGross().asFormatedString());
				}

			}
		});

	}

	public static String getText(UniDataSet uds, String dataKey) {
		if (!dataKey.startsWith("$")) {
			return uds.getFormatedStringValueByKey(dataKey);
		} else if (dataKey.equals("$status")) {
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
					return "???";
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

		} else if (dataKey.equals("$documenttype")) {
			DocumentType documentType = DocumentType.getType(uds.getIntValueByKey("category"));
			return documentType.getString();
		} else if (dataKey.equals("$vatbyid")) { return uds.getFormatedStringValueByKeyFromOtherTable("vatid.VATS:value"); }

		return "";
	}

	public static Double getDoubleValue(UniDataSet uds, String dataKey) {
		if (!dataKey.startsWith("$")) {
			return uds.getDoubleValueByKey(dataKey);
		} else if (dataKey.equals("$status")) {
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

		} else if (dataKey.equals("$documenttype")) {
			return uds.getDoubleValueByKey("category");
		} else if (dataKey.equals("$vatbyid")) { return DataUtils.StringToDouble(uds.getFormatedStringValueByKeyFromOtherTable("vatid.VATS:value")); }

		return 0.0;
	}

	public static boolean isNumeric(UniDataSet uds, String dataKey) {
		if (!dataKey.startsWith("$")) {
			return uds.getUniDataTypeByKey(dataKey).isNumeric();
		} else if (dataKey.equals("$status")) {
			return false;
		} else if (dataKey.equals("$documenttype")) {
			return false;
		} else if (dataKey.equals("$vatbyid")) { return true; }

		return false;
	}

	public static boolean isDate(UniDataSet uds, String dataKey) {
		if (!dataKey.startsWith("$")) { return uds.getUniDataTypeByKey(dataKey).isDate(); }
		return false;
	}

	public void setStdEntry(int stdId) {
		this.stdId = stdId;
	}

}
