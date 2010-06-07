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

package com.sebulli.fakturama.exportsales;

import com.sebulli.fakturama.logger.Logger;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.Locale;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.table.BorderLine;
import com.sun.star.table.TableBorder;
import com.sun.star.table.XCell;
import com.sun.star.uno.UnoRuntime;

public class CellFormatter {

	private static void setCellProperty(XCell cell, String property, Object value) {
		XPropertySet xPropertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, cell);

		try {
			xPropertySet.setPropertyValue(property, value);
		} catch (UnknownPropertyException e) {
			Logger.logError(e, "Error 'UnknownProperty' setting cell property " + property + " to " + value.toString());
		} catch (PropertyVetoException e) {
			Logger.logError(e, "Error 'PropertyVeto' setting cell property " + property + " to " + value.toString());
		} catch (IllegalArgumentException e) {
			Logger.logError(e, "Error 'IllegalArgument' setting cell property " + property + " to " + value.toString());
		} catch (WrappedTargetException e) {
			Logger.logError(e, "Error 'WrappedTarget' setting cell property " + property + " to " + value.toString());
		}

	}

	public static void setBorder(XSpreadsheet spreadsheet, int row, int column, int color, boolean top, boolean right, boolean bottom, boolean left) {
		XCell cell = getCell(spreadsheet, row, column);
		BorderLine noBorderLine = new BorderLine();
		noBorderLine.Color = 0x000000;
		noBorderLine.InnerLineWidth = 0;
		noBorderLine.OuterLineWidth = 0;
		noBorderLine.LineDistance = 0;

		BorderLine singleBorderLine = new BorderLine();
		singleBorderLine.Color = color;
		singleBorderLine.InnerLineWidth = 30;
		singleBorderLine.OuterLineWidth = 0;
		singleBorderLine.LineDistance = 0;

		TableBorder tableBorder = new TableBorder();

		if (top)
			tableBorder.TopLine = singleBorderLine;
		else
			tableBorder.TopLine = noBorderLine;
		tableBorder.IsTopLineValid = true;

		if (bottom)
			tableBorder.BottomLine = singleBorderLine;
		else
			tableBorder.BottomLine = noBorderLine;
		tableBorder.IsBottomLineValid = true;

		if (left)
			tableBorder.LeftLine = singleBorderLine;
		else
			tableBorder.LeftLine = noBorderLine;
		tableBorder.IsLeftLineValid = true;

		if (right)
			tableBorder.RightLine = singleBorderLine;
		else
			tableBorder.RightLine = noBorderLine;
		tableBorder.IsRightLineValid = true;

		tableBorder.HorizontalLine = singleBorderLine;
		tableBorder.IsHorizontalLineValid = true;
		tableBorder.VerticalLine = noBorderLine;
		tableBorder.IsVerticalLineValid = true;

		setCellProperty(cell, "TableBorder", tableBorder);

	}

	public static void setColor(XSpreadsheet spreadsheet, int row, int column, int color) {
		XCell cell = getCell(spreadsheet, row, column);
		setCellProperty(cell, "CharColor", new Integer(color));
	}

	public static void setBackgroundColor(XSpreadsheet spreadsheet, int row, int column, int color) {
		XCell cell = getCell(spreadsheet, row, column);
		setCellProperty(cell, "CellBackColor", new Integer(color));
	}

	public static void setBold(XSpreadsheet spreadsheet, int row, int column) {
		XCell cell = getCell(spreadsheet, row, column);
		setCellProperty(cell, "CharWeight", new Float(com.sun.star.awt.FontWeight.BOLD));
	}

	public static void setItalic(XSpreadsheet spreadsheet, int row, int column) {
		XCell cell = getCell(spreadsheet, row, column);
		setCellProperty(cell, "CharPosture", com.sun.star.awt.FontSlant.ITALIC);
	}

	public static void setLocalCurrency(XSpreadsheetDocument xSpreadsheetDocument, XSpreadsheet spreadsheet, int row, int column) {
		XCell cell = getCell(spreadsheet, row, column);

		// Query the number formats supplier of the spreadsheet document
		com.sun.star.util.XNumberFormatsSupplier xNumberFormatsSupplier = (com.sun.star.util.XNumberFormatsSupplier) UnoRuntime.queryInterface(
				com.sun.star.util.XNumberFormatsSupplier.class, xSpreadsheetDocument);

		// Get the number formats from the supplier
		com.sun.star.util.XNumberFormats xNumberFormats = xNumberFormatsSupplier.getNumberFormats();

		// Query the XNumberFormatTypes interface
		com.sun.star.util.XNumberFormatTypes xNumberFormatTypes = (com.sun.star.util.XNumberFormatTypes) UnoRuntime.queryInterface(
				com.sun.star.util.XNumberFormatTypes.class, xNumberFormats);

		int nCurrKey = xNumberFormatTypes.getStandardFormat(com.sun.star.util.NumberFormat.CURRENCY, new Locale());
		setCellProperty(cell, "NumberFormat", new Integer(nCurrKey));
	}

	public static XCell getCell(XSpreadsheet spreadsheet, int row, int column) {
		XCell cell = null;
		XSheetCellCursor cellCursor = spreadsheet.createCursor();
		try {
			cell = cellCursor.getCellByPosition(column, row);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		return cell;
	}

	public static String getCellName(int row, int column) {
		char cM = ' ';
		char cL;
		int columnM = column / 26;
		int columnL = column % 26;

		if (column > (25 * 26)) {
			Logger.logError("Columns out of range");
			return "ZZ1";
		}
		if (columnM > 0)
			cM = (char) ('A' + columnM - 1);
		cL = (char) ('A' + columnL);
		String s = "";
		s = cL + Integer.toString(row + 1);

		if (columnM > 0)
			s = cM + s;
		return s;

	}

}
