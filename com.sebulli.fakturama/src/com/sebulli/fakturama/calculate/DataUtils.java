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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;

import com.sebulli.fakturama.data.UniData;
import com.sebulli.fakturama.logger.Logger;

/**
 * This class provides static functions to convert
 * and format data like double values, dates or strings.
 * 
 * @author gerd
 */
public class DataUtils {

	/**
	 * Test, if a value is rounded to cent values.
	 * e.g. 
	 * 39,43000 € is a rounded value
	 * 39,43200 € is not.
	 * 
	 * @param d Double value to test
	 * @return true, if the value is rounded to cent values.
	 */
	public static boolean isRounded(Double d) {
		return DoublesAreEqual(d, round(d));
	}

	/**
	 * Test, if two double values are equal.
	 * Because of rounding errors during calculation, 
	 * two values with a difference of only 0.0001 are
	 * interpreted as "equal"
	 * 
	 * @param d1 First value
	 * @param d2 Second value
	 * @return True, if the values are equal.
	 */
	public static boolean DoublesAreEqual(Double d1, Double d2) {
		return (Math.abs(d1 - d2) < 0.0001);
	}

	/**
	 * Test, if 2 values are equal. One value is a double and one is string.
	 * 
	 * @param s1 First value as String
	 * @param d2 Second value as double
	 * @return True, if the values are equal.
	 */
	public static boolean DoublesAreEqual(String s1, Double d2) {
		return DoublesAreEqual(StringToDouble(s1), d2);
	}

	/**
	 * Test, if 2 values are equal. Both values are doubles as formated string.
	 * 
	 * @param s1 First value as String
	 * @param s2 Second value as String
	 * @return True, if the values are equal.
	 */
	public static boolean DoublesAreEqual(String s1, String s2) {
		return DoublesAreEqual(StringToDouble(s1), StringToDouble(s2));
	}

	/**
	 * Convert a String to a double value
	 * If there is a "%" Sign, the values are scales by 0.01
	 * If there is a "," - it is converted to a "."
	 * Only numbers are converted
	 * 
	 * @param s String to convert
	 * @return converted value
	 */
	public static Double StringToDouble(String s) {
		Double d = 0.0;
		
		// Test, if it is a percent value
		boolean isPercent = s.contains("%");
		
		// replace the localizes decimal separators
		s = s.replaceAll(",", ".");
		
		// use only numbers
		Pattern p = Pattern.compile("[+-]?\\d*\\.?\\d*");
		Matcher m = p.matcher(s);

		if (m.find()) {
			// extract the number
			s = s.substring(m.start(), m.end());
			try {
				// try to convert it to a double value
				d = Double.parseDouble(s);
				
				// scale it by 0.01, if it was a percent value
				if (isPercent)
					d = d / 100;
				
			} catch (NumberFormatException e) {
			}
		}
		return d;
	}

	/**
	 * Round a value to full cent values.
	 * Add an offset of 0.01 cent. This is, because there
	 * may be double values like 0.004999999999999 which should be rounded to 0.01
	 * 
	 * @param d value to round.
	 * @return Rounded value
	 */
	public static Double round(Double d) {
		return (Math.round((d + 0.0001) * 100.0)) / 100.0; 
	}

	private static String DoubleToFormatedValue(Double d, boolean twoDecimals) {
		Double d2;
		if (d >= 0)
			d2 = Math.floor(d * 100.0 + 0.0001) / 100.0;
		else
			d2 = Math.ceil(d * 100.0 - 0.0001) / 100.0;

		DecimalFormat price = new DecimalFormat("0.00");
		String s = price.format(d2);
		if (Math.abs(d - d2) > 0.0002)
			return s + "..";
		else {
			if (!twoDecimals) {
				if (s.endsWith("00"))
					return s.substring(0, s.length() - 3);
				if (s.endsWith("0"))
					return s.substring(0, s.length() - 1);
			}
			return s;
		}

	}

	public static String DoubleToFormatedPrice(Double d) {
		return DoubleToFormatedValue(d, true) + " €";
	}

	public static String DoubleToFormatedPercent(Double d) {
		return DoubleToFormatedValue(d * 100, false) + " %";
	}

	public static String DoubleToFormatedQuantity(Double d) {
		return DoubleToFormatedValue(d, false);
	}

	public static String DoubleToFormatedPriceRound(Double d) {
		return DoubleToFormatedValue(round(d), true) + " €";
	}

	public static String CalculateGrossFromNet(String net, Double vat, UniData netvalue) {
		netvalue.setValue(net);
		return CalculateGrossFromNet(netvalue.getValueAsDouble(), vat);
	}

	public static String CalculateGrossFromNet(Double net, Double vat) {
		Double gross = net * (1 + vat);
		return DoubleToFormatedPrice(gross);
	}

	public static void CalculateGrossFromNet(Text net, Text gross, Double vat, UniData netvalue) {
		String s = "";

		if (net != null) {
			s = CalculateGrossFromNet(net.getText(), vat, netvalue);
		} else {
			s = CalculateGrossFromNet(netvalue.getValueAsDouble(), vat);
		}

		if (gross != null)
			if (!gross.isFocusControl())
				gross.setText(s);
	}

	public static String CalculateNetFromGross(String gross, Double vat, UniData netvalue) {
		return CalculateNetFromGross(StringToDouble(gross), vat, netvalue);
	}

	public static String CalculateNetFromGross(Double gross, Double vat, UniData netvalue) {
		netvalue.setValue(gross / (1 + vat));
		return DoubleToFormatedPrice(netvalue.getValueAsDouble());
	}

	public static void CalculateNetFromGross(Text gross, Text net, Double vat, UniData netvalue) {
		String s = "";

		if (gross != null) {
			s = CalculateNetFromGross(gross.getText(), vat, netvalue);
		} else {
			s = DoubleToFormatedPrice(netvalue.getValueAsDouble());
		}

		if (net != null)
			if (!net.isFocusControl())
				net.setText(s);

	}

	public static String getDateTimeAsString(DateTime dtDate) {
		return String.format("%04d-%02d-%02d", dtDate.getYear(), dtDate.getMonth() + 1, dtDate.getDay());
	}

	public static String getDateTimeAsString(GregorianCalendar calendar) {
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH);
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		return String.format("%04d-%02d-%02d", y, m + 1, d);
	}

	public static String getDateTimeAsLocalString(GregorianCalendar calendar) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(calendar.getTime());
	}

	public static String DateAsLocalString(String s) {

		GregorianCalendar calendar = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			calendar.setTime(formatter.parse(s));
		} catch (ParseException e) {
			Logger.logError(e, "Error parsing Date");
		}
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(calendar.getTime());
	}

	public static String DateAndTimeAsLocalString(String s) {

		GregorianCalendar calendar = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			calendar.setTime(formatter.parse(s));
		} catch (ParseException e) {
			Logger.logError(e, "Error parsing Date and Time");
		}
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
		return df.format(calendar.getTime());
	}

	public static String DateAsUSString(String s) {
		GregorianCalendar calendar = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			calendar.setTime(formatter.parse(s));
		} catch (ParseException e) {
			Logger.logError(e, "Error parsing Date");
		}
		return getDateTimeAsString(calendar);
	}

	public static String AddToDate(String date, int days) {
		GregorianCalendar calendar = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			calendar.setTime(formatter.parse(date));
		} catch (ParseException e) {
			try {
				DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				calendar.setTime(formatter.parse(date));
			} catch (ParseException e2) {

				Logger.logError(e2, "Error parsing Date");
			}
		}
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return getDateTimeAsString(calendar);
	}

	public static double similarity(String sA, String sB) {
		int i;
		int ii;
		int min;
		int codesA = sA.length() - 1;
		int codesB = sB.length() - 1;
		int codeA[] = new int[codesA];
		int codeB[] = new int[codesB];

		for (i = 0; i < codesA; i++)
			codeA[i] = ((sA.charAt(i)) << 8) | (((sA.charAt(i) - sA.charAt(i + 1) & 0x00FF)));

		for (i = 0; i < codesB; i++)
			codeB[i] = ((sB.charAt(i)) << 8) | (((sB.charAt(i) - sB.charAt(i + 1) & 0x00FF)));

		int founds = 0;
		for (i = 0; i < codesA; i++)
			for (ii = 0; ii < codesB; ii++)
				if ((codeA[i] == codeB[ii]) && (codeA[i] != 0)) {
					founds++;
					ii = codesB;
				}

		if (sA.charAt(0) == sB.charAt(0))
			founds++;

		if (sA.charAt(codesA) == sB.charAt(codesB))
			founds++;

		if (codesA < codesB)
			min = codesA;
		else
			min = codesB;
		min += 2;
		return ((double) founds / (double) min);

	}

	public static double StringToDoubleDiscount(String s) {
		s = s + "%";
		double d = StringToDouble(s);
		if (d > 0)
			d = -d;
		if (d < -0.999)
			d = 0.0;
		return d;
	}

}
