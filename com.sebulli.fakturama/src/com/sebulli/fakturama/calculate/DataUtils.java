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
 * @author Gerd Bartelt
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

	/**
	 * Convert a double to a formated string value.
	 * If the value has parts of a cent, add ".."
	 * 
	 * @param d Double value to convert
	 * @param twoDecimals TRUE, if the value is displayed in the format 0.00
	 * @return Converted value as String
	 */
	private static String DoubleToFormatedValue(Double d, boolean twoDecimals) {
		
		// Calculate the floor cent value.
		// for negative values, use the ceil
		Double floorValue;
		if (d >= 0)
			floorValue = Math.floor(d * 100.0 + 0.0001) / 100.0;
		else
			floorValue = Math.ceil(d * 100.0 - 0.0001) / 100.0;

		// Format as "0.00"
		DecimalFormat price = new DecimalFormat("0.00");
		String s = price.format(floorValue);
		
		// Are there parts of a cent ? Add ".."
		if (Math.abs(d - floorValue) > 0.0002)
			return s + "..";
		else {
			
			if (!twoDecimals) {

				// remove the last ".00" from e.g. "12.00"
				if (s.endsWith("00"))
					return s.substring(0, s.length() - 3);

				// remove the last "0" from e.g. "12.50"
				if (s.endsWith("0"))
					return s.substring(0, s.length() - 1);
			}

			return s;
		}
	}

	/**
	 * Convert a double to a formated price value.
	 * Same as conversion to a formated value. But use always 2 decimals and add
	 * the currency sign.
	 * 
	 * @param d Value to convert to a price string.
	 * @return Converted value as string
	 */
	public static String DoubleToFormatedPrice(Double d) {
		return DoubleToFormatedValue(d, true) + " €";
	}

	/**
	 * Convert a double to a formated percent value.
	 * Same as conversion to a formated value.
	 * But do not use 2 decimals and add the percent sign,
	 * and scale it by 100
	 * 
	 * @param d Value to convert to a percent string.
	 * @return Converted value as string
	 */
	public static String DoubleToFormatedPercent(Double d) {
		return DoubleToFormatedValue(d * 100, false) + " %";
	}

	/**
	 * Convert a double to a formated quantity value.
	 * Same as conversion to a formated value.
	 * But do not use 2 decimals.
	 * 
	 * @param d Value to convert to a quantity string.
	 * @return Converted value as string
	 */
	public static String DoubleToFormatedQuantity(Double d) {
		return DoubleToFormatedValue(d, false);
	}

	/**
	 * Convert a double to a formated price value.
	 * Same as conversion to a formated price value.
	 * But round the value to full cent values
	 * 
	 * @param d Value to convert to a price string.
	 * @return Converted value as string
	 */
	public static String DoubleToFormatedPriceRound(Double d) {
		return DoubleToFormatedValue(round(d), true) + " €";
	}

	/**
	 * Calculates the gross value based on a net value and the vat
	 * 
	 * @param net Net value as String
	 * @param vat Vat as double
	 * @param netvalue Net value as UniData. This is modified with the net value.
	 * @return Gross value as string
	 */
	public static String CalculateGrossFromNet(String net, Double vat, UniData netvalue) {
		netvalue.setValue(net);
		return CalculateGrossFromNet(netvalue.getValueAsDouble(), vat);
	}

	/**
	 * Calculates the gross value based on a net value and the vat
	 * 
	 * @param net Net value as double
	 * @param vat Vat as double
	 * @return Gross value as string
	 */
	public static String CalculateGrossFromNet(Double net, Double vat) {
		Double gross = net * (1 + vat);
		return DoubleToFormatedPrice(gross);
	}

	/**
	 * Calculates the gross value based on a net value and the vat.
	 * Uses the net value from a SWT text field and write the result
	 * into a gross SWT text field
	 * 
	 * @param net SWT text field. This value is used as net value.
	 * @param gross SWT text field. This filed is modified.
	 * @param vat Vat as double
	 * @param netvalue Net value as UniData. This is modified with the net value.
	 */
	public static void CalculateGrossFromNet(Text net, Text gross, Double vat, UniData netvalue) {
		String s = "";

		// If there is a net SWT text field specified, its value is used
		if (net != null) {
			s = CalculateGrossFromNet(net.getText(), vat, netvalue);
		// In the other case, the UniData netvalue is used
		} else {
			s = CalculateGrossFromNet(netvalue.getValueAsDouble(), vat);
		}

		// Fill the SWT text field "gross" with the result
		if (gross != null)
			if (!gross.isFocusControl())
				gross.setText(s);
	}

	/**
	 * Convert a gross value to a net value.
	 * 
	 * @param gross Gross value as String
	 * @param vat Vat as double
	 * @param netvalue Net value as UniData. This is modified with the new net value.
	 * @return Net value as string
	 */
	public static String CalculateNetFromGross(String gross, Double vat, UniData netvalue) {
		return CalculateNetFromGross(StringToDouble(gross), vat, netvalue);
	}

	/**
	 * Convert a gross value to a net value.
	 * 
	 * @param gross Gross value as Double
	 * @param vat Vat as double 
	 * @param netvalue Net value as UniData. This is modified with the new net value.
	 * @return Net value as string
	 */
	public static String CalculateNetFromGross(Double gross, Double vat, UniData netvalue) {
		netvalue.setValue(gross / (1 + vat));
		return DoubleToFormatedPrice(netvalue.getValueAsDouble());
	}

	/**
	 * Calculates the net value based on a gross value and the vat.
	 * Uses the gross value from a SWT text field and write the result
	 * into a net SWT text field
	 * 
	 * @param gross SWT text field. This value is used as gross value.
	 * @param net SWT text field. This filed is modified.
	 * @param vat Vat as double
	 * @param netvalue Net value as UniData. This is modified with the net value.
	 */
	public static void CalculateNetFromGross(Text gross, Text net, Double vat, UniData netvalue) {
		String s = "";

		// If there is a gross SWT text field specified, its value is used
		if (gross != null) {
			s = CalculateNetFromGross(gross.getText(), vat, netvalue);
		// In the other case: do not convert. Just format the netvalue.
		} else {
			s = DoubleToFormatedPrice(netvalue.getValueAsDouble());
		}

		// Fill the SWT text field "net" with the result
		if (net != null)
			if (!net.isFocusControl())
				net.setText(s);

	}

	/**
	 * Get the date from a SWT DateTime widget in the format:
	 * YYYY-MM-DD
	 * 
	 * @param dtDate SWT DateTime widget
	 * @return Date as formated String
	 */
	public static String getDateTimeAsString(DateTime dtDate) {
		return String.format("%04d-%02d-%02d", dtDate.getYear(), dtDate.getMonth() + 1, dtDate.getDay());
	}

	/**
	 * Get the date from a Calendar object in the format:
	 * YYYY-MM-DD
	 * 
	 * @param calendar Gregorian Calendar object
	 * @return Date as formated String
	 */
	public static String getDateTimeAsString(GregorianCalendar calendar) {
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH);
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		return String.format("%04d-%02d-%02d", y, m + 1, d);
	}

	/**
	 * Get the date from a Calendar object in the localized format.
	 * 
	 * @param calendar calendar Gregorian Calendar object
	 * @return Date as formated String
	 */
	public static String getDateTimeAsLocalString(GregorianCalendar calendar) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(calendar.getTime());
	}

	/**
	 * Convert a date string from the format YYYY-MM-DD to to localized format.
	 * 
	 * @param s Date String
	 * @return Date as formated String
	 */
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

	/**
	 * Convert a date and time string from the format
	 * YYYY-MM-DD HH:MM:SS to to localized format.
	 * 
	 * @param s Date and time String
	 * @return Date and time as formated String
	 */
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

	/**
	 * Convert a date string into the format ISO 8601 YYYY-MM-DD.
	 * 
	 * @param s Date String
	 * @return Date as formated String
	 */
	public static String DateAsISO8601String(String s) {
		GregorianCalendar calendar = new GregorianCalendar();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			calendar.setTime(formatter.parse(s));
		} catch (ParseException e) {
			Logger.logError(e, "Error parsing Date");
		}
		return getDateTimeAsString(calendar);
	}

	/**
	 * Convert date strings from the following format to a calendar
	 * 
	 * @param date Date as string
	 * @return GregorianCalendar
	 */
	public static GregorianCalendar getCalendarFromDateString (String date) {
		GregorianCalendar calendar = new GregorianCalendar();
		
		// try to parse the input date string
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			calendar.setTime(formatter.parse(date));
		} catch (ParseException e) {
			
			// use also localized formats
			try {
				DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				calendar.setTime(formatter.parse(date));
			} catch (ParseException e2) {

				// use also localized formats
				try {
					//TODO: support other date formats
					DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
					calendar.setTime(formatter.parse(date));
				} catch (ParseException e3) {
					Logger.logError(e3, "Error parsing Date");
				}
			}
		}
		return calendar;
	}
	
	

	
	/**
	 * Adds days to a date string.
	 * 
	 * @param date Days to add
	 * @param days Date as string 
	 * @return Calculated date
	 */
	public static String AddToDate(String date, int days) {
		GregorianCalendar calendar = getCalendarFromDateString (date);
		
		// Add the days
		calendar.add(Calendar.DAY_OF_MONTH, days);
		
		// And convert it back to a String value
		return getDateTimeAsString(calendar);
	}


	/**
	 * Calculates the similarity of two string.
	 * 
	 * The result is a value from 0.0 to 1.0
	 * Returns 1.0, if both strings are equal.
	 * 
	 * @param sA First String value
	 * @param sB Second String value 
	 * @return Similarity from 0.0 to 1.0
	 */
	public static double similarity(String sA, String sB) {
		int i;
		int ii;
		int min;
		int codesA = sA.length() - 1;
		int codesB = sB.length() - 1;
		int codeA[] = new int[codesA];
		int codeB[] = new int[codesB];

		// Scans first String. 
		// Generate a 16 Bit Code of two 8 Bit characters.
		for (i = 0; i < codesA; i++)
			codeA[i] = ((sA.charAt(i)) << 8) | (((sA.charAt(i) - sA.charAt(i + 1) & 0x00FF)));

		// Scans second String. 
		// Generate a 16 Bit Code of two 8 Bit characters.
		for (i = 0; i < codesB; i++)
			codeB[i] = ((sB.charAt(i)) << 8) | (((sB.charAt(i) - sB.charAt(i + 1) & 0x00FF)));

		// Count how much of the codes from the first strings are found
		// in the codes of the second string.
		int founds = 0;
		for (i = 0; i < codesA; i++)
			for (ii = 0; ii < codesB; ii++)
				if ((codeA[i] == codeB[ii]) && (codeA[i] != 0)) {
					founds++;
					ii = codesB;
				}

		// Normaly only 2 following characters are scanned. 
		// So don't forget to compare the first character of both strings
		if (sA.charAt(0) == sB.charAt(0))
			founds++;

		// And both last characters
		if (sA.charAt(codesA) == sB.charAt(codesB))
			founds++;

		// min is the length of the shortest string
		if (codesA < codesB)
			min = codesA;
		else
			min = codesB;
		
		// add an offset, so that two equal strings will result 1.0
		// codeX is length-1
		min += 2;
		
		// Calculate the ratio of the founds and the number of characters.
		return ((double) founds / (double) min);

	}

	/**
	 * Convert a discount string to a double value
	 * The input string is interpreted as a percent value.
	 * Positive values are converted to negative, because a discount is always negative.
	 * 
	 * "-3%" is converted to -0.03
	 * "-3"  is converted to -0.03
	 * "3"   is converted to -0.03
	 * 
	 * @param s String to convert
	 * @return Result as double from -0.999 to 0.0
	 */
	public static double StringToDoubleDiscount(String s) {
		
		// The input String is always a percent value
		s = s + "%";
		
		// convert it
		double d = StringToDouble(s);
		
		// Convert it to negative values
		if (d > 0)
			d = -d;
		
		// A discount of more than -99.9% is invalid.
		if (d < -0.999)
			d = 0.0;

		return d;
	}

}
