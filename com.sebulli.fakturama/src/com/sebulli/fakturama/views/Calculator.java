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

package com.sebulli.fakturama.views;

import java.text.DecimalFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.sebulli.fakturama.Activator;
import com.sebulli.fakturama.logger.Logger;

public class Calculator extends ViewPart {

	public static final String ID = "com.sebulli.fakturama.views.calculator";
	/*
	 * Initialize variables needed for this class.
	 */
	private Text displayText;
	// The three calculator registers.
	private String displayString = "0.";
	private String operatorString = new String();
	// A variable to store the pending calculation
	private char calcChar = ' ';

	// Error strings
	private final String ERROR_STRING = "E:";
	private final String NAN_STRING = "NaN";
	private final String INFINITY_STRING = "Infinity";
	// A flag to check if display should be cleared on the next keystroke
	private boolean clearDisplay = true;

	// An ID constant for the copy to clipboard key
	// private static final int CLIPBOARD_ID = IDialogConstants.NO_TO_ALL_ID +
	// 1;

	@Override
	public void createPartControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout calculatorGridLayout = new GridLayout();
		calculatorGridLayout.marginRight = 10;
		calculatorGridLayout.marginLeft = 10;
		calculatorGridLayout.marginBottom = 15;
		calculatorGridLayout.marginTop = 5;
		calculatorGridLayout.marginWidth = 0;
		calculatorGridLayout.marginHeight = 0;
		calculatorGridLayout.numColumns = 4;
		calculatorGridLayout.verticalSpacing = 5;
		calculatorGridLayout.makeColumnsEqualWidth = true;
		calculatorGridLayout.horizontalSpacing = 2;
		container.setLayout(calculatorGridLayout);

		// The display. Note that it has a limit of 30 characters,
		// much greater than the length of a double-precision number.
		displayText = new Text(container, SWT.RIGHT | SWT.BORDER);
		displayText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		displayText.setEditable(false);
		displayText.setDoubleClickEnabled(false);
		displayText.setTextLimit(10);
		displayText.setText(displayString);
		displayText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		FontData[] fD = displayText.getFont().getFontData();
		fD[0].setHeight(32);
		Font font = new Font(null, fD[0]);
		displayText.setFont(font);
		font.dispose();

		Color foreground = new Color(null, 55, 55, 55);
		Color background = new Color(null, 0xf0, 0xf0, 0xf0);
		displayText.setForeground(foreground);
		displayText.setBackground(background);
		foreground.dispose();
		background.dispose();

		final Label clearButton = new Label(container, SWT.NONE);
		clearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('C');
			}
		});
		try {
			clearButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_C.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(clearButton);

		final Label ceButton = new Label(container, SWT.NONE);
		ceButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('E');
			}
		});
		try {
			ceButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_CE.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(ceButton);

		final Label backButton = new Label(container, SWT.NONE);
		backButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('B');
			}
		});
		try {
			backButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_BACK.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(backButton);

		final Label multiplyButton = new Label(container, SWT.NONE);
		multiplyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateCalc('*');
			}
		});
		try {
			multiplyButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_X.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(multiplyButton);

		final Label inverseButton = new Label(container, SWT.NONE);
		inverseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('I');
			}
		});
		try {
			inverseButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_INV.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(inverseButton);

		final Label percentButton = new Label(container, SWT.NONE);
		percentButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('%');
			}
		});
		try {
			percentButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_PROZENT.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(percentButton);

		final Label divideButton = new Label(container, SWT.NONE);
		divideButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateCalc('/');
			}
		});
		try {
			divideButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_DIV.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(divideButton);

		final Label SubtractButton = new Label(container, SWT.NONE);
		SubtractButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateCalc('-');
			}
		});
		try {
			SubtractButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_MINUS.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(SubtractButton);

		final Label num7Button = new Label(container, SWT.NONE);
		num7Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('7');
			}
		});
		try {
			num7Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_7.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num7Button);

		final Label num8Button = new Label(container, SWT.NONE);
		num8Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('8');
			}
		});
		try {
			num8Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_8.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num8Button);

		final Label num9Button = new Label(container, SWT.NONE);
		num9Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('9');
			}
		});
		try {
			num9Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_9.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num9Button);

		final Label AdditionButton = new Label(container, SWT.NONE);
		AdditionButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateCalc('+');
			}
		});
		try {
			AdditionButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_PLUS.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).span(1, 2).applyTo(AdditionButton);

		final Label num4Button = new Label(container, SWT.NONE);
		num4Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('4');
			}
		});
		try {
			num4Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_4.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num4Button);

		final Label num5Button = new Label(container, SWT.NONE);
		num5Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('5');
			}
		});
		try {
			num5Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_5.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num5Button);

		final Label num6Button = new Label(container, SWT.NONE);
		num6Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('6');
			}
		});
		try {
			num6Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_6.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num6Button);

		final Label num1Button = new Label(container, SWT.NONE);
		num1Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('1');
			}
		});
		try {
			num1Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_1.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num1Button);

		final Label num2Button = new Label(container, SWT.NONE);
		num2Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('2');
			}
		});
		try {
			num2Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_2.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num2Button);

		final Label num3Button = new Label(container, SWT.NONE);
		num3Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('3');
			}
		});
		try {
			num3Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_3.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num3Button);

		final Label equalsButton = new Label(container, SWT.NONE);
		equalsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateCalc('=');
			}
		});
		try {
			equalsButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_ISTGLEICH.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).span(1, 2).applyTo(equalsButton);

		final Label num0Button = new Label(container, SWT.NONE);
		num0Button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('0');
			}
		});
		try {
			num0Button.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_0.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(num0Button);

		final Label decimalButton = new Label(container, SWT.NONE);
		decimalButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('.');
			}
		});
		try {
			decimalButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_PUNKT.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(decimalButton);

		final Label signButton = new Label(container, SWT.NONE);
		signButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateDisplay('-');
			}
		});
		try {
			signButton.setImage((Activator.getImageDescriptor("/icons/calculator/calculator_PLUSMINUS.png").createImage()));
		} catch (Exception e) {
			Logger.logError(e, "Icon not found");
		}
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(signButton);

		displayText.setFocus();
		displayText.setSelection(0);
		displayText.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case 'C':
				case 'E':
				case 'B':
				case 'I':
				case '%':
				case '.':
					updateDisplay(e.character);
					break;
				case '*':
				case '/':
				case '-':
				case '+':
				case '=':
					updateCalc(e.character);
					break;
				case ',':
					updateDisplay('.');
					break;
				case '\n':
					updateCalc('=');
					break;
				case '\r':
					updateCalc('=');
					break;
				}
				switch (e.keyCode) {
				case 8:
					updateDisplay('B');
					break;
				case 27:
					updateDisplay('C');
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

	}

	private void setDisplayString(String displayString, boolean isResult) {
		String LCDString = displayString;
		if (!LCDString.contains("."))
			LCDString += ".";
		if (LCDString.startsWith("."))
			LCDString = "0" + LCDString;
		if (LCDString.startsWith("-."))
			LCDString = LCDString.replaceFirst("-\\.", "-0.");
		if (LCDString.length() >= 10)
			LCDString = LCDString.substring(0, 10);

		if (isResult)
			while (LCDString.endsWith("0"))
				LCDString = LCDString.substring(0, LCDString.length() - 1);
		displayText.setText(LCDString);
	}

	@Override
	public void setFocus() {
		displayText.setFocus();
	}

	/*
	 * This method updates the display text based on user input.
	 */
	private void updateDisplay(final char keyPressed) {
		char keyVal = keyPressed;
		String tempString = new String();
		boolean doClear = false;

		if (!clearDisplay) {
			tempString = displayString;
		}

		switch (keyVal) {
		case 'B': // Backspace
			if (tempString.length() < 2) {
				tempString = "";
			} else {
				tempString = tempString.substring(0, tempString.length() - 1);
			}
			break;

		case 'C': // Clear
			tempString = "0.";
			operatorString = "";
			calcChar = ' ';
			doClear = true;
			break;

		case 'E': // Clear Entry
			tempString = "0.";
			doClear = true;
			break;

		case 'I': // Inverse
			tempString = doCalc(displayString, "", keyVal);
			doClear = true;
			break;

		case '%': // Percent
			tempString = doCalc(displayString, operatorString, keyVal);
			doClear = true;
			break;

		case '-': // Change Sign
			if (tempString.startsWith("-")) {
				tempString = tempString.substring(1, tempString.length());
			} else {
				tempString = keyVal + tempString;
			}
			break;

		case '.': // Can't have two decimal points.
			if (tempString.indexOf(".") == -1 && tempString.length() < 9) {
				tempString = tempString + keyVal;
			}
			break;

		case '0': // Don't want 00 to be entered.
			if (!tempString.equals("0") && tempString.length() < 9) {
				tempString = tempString + keyVal;
			}
			break;

		default: // Default case is for the digits 1 through 9.
			if (tempString.length() < 9) {
				tempString = tempString + keyVal;
			}
			break;
		}

		clearDisplay = doClear;
		if (!displayString.equals(tempString)) {
			displayString = tempString;
			setDisplayString(displayString, keyVal == '%' || keyVal == 'I');
		}
	}

	/*
	 * This method converts the operator and display strings to double values
	 * and performs the calculation.
	 */
	private String doCalc(final String valAString, final String valBString, final char opChar) {
		String resultString = ERROR_STRING + NAN_STRING;
		Double valA = 0.0;
		Double valB = 0.0;
		Double valAnswer = 0.0;

		// Make sure register strings are numbers
		if (valAString.length() > 0) {
			try {
				valA = Double.parseDouble(valAString);
			} catch (NumberFormatException e) {
				return resultString;
			}
		} else {
			return resultString;
		}

		if (opChar != '%' && opChar != 'I') {
			if (valBString.length() > 0) {
				try {
					valB = Double.parseDouble(valBString);
				} catch (NumberFormatException e) {
					return resultString;
				}
			} else {
				return resultString;
			}
		}

		if (opChar == '%') {
			if (valBString.length() > 0) {
				try {
					valB = Double.parseDouble(valBString);
				} catch (NumberFormatException e) {
					return resultString;
				}
			} else {
				valB = 1.0;
			}
		}

		switch (opChar) {
		case '%': // Percent
			valAnswer = valB * (valA / 100);
			break;

		case 'I': // Inverse
			valB = 1.0;
			valAnswer = valB / valA;
			break;

		case '+': // Addition
			valAnswer = valA + valB;
			break;

		case '-': // Subtraction
			valAnswer = valA - valB;
			break;

		case '/': // Division
			valAnswer = valA / valB;
			break;

		case '*': // Multiplication
			valAnswer = valA * valB;
			break;

		default: // Do nothing - this should never happen
			break;

		}
		// Convert answer to string and format it before return.

		DecimalFormat format = new DecimalFormat("0.000000000000");
		// resultString = valAnswer.toString();
		resultString = format.format(valAnswer);
		resultString = resultString.replace(',', '.');
		resultString = trimString(resultString);
		return resultString;
	}

	/*
	 * This method updates the operator and display strings, and the pending
	 * calculation flag.
	 */
	private void updateCalc(char keyPressed) {
		char keyVal = keyPressed;
		String tempString = displayString;

		/*
		 * If there is no display value, the keystroke is deemed invalid and
		 * nothing is done.
		 */
		if (tempString.length() == 0) { return; }

		/*
		 * If there is no operator value, only calculation key presses are
		 * considered valid. Check that the display value is valid and if so,
		 * move the display value to the operator. No calculation is done.
		 */
		if (operatorString.length() == 0) {
			if (keyVal != '=') {
				tempString = trimString(tempString);
				if (tempString.startsWith(ERROR_STRING)) {
					clearDisplay = true;
					operatorString = "";
					calcChar = ' ';
				} else {
					operatorString = tempString;
					calcChar = keyVal;
					clearDisplay = true;
				}
			}
			return;
		}

		// There is an operator and a display value, so do the calculation.
		displayString = doCalc(operatorString, tempString, calcChar);

		/*
		 * If '=' was pressed or result was invalid, reset pending calculation
		 * flag and operator value. Otherwise, set new calculation flag so
		 * calculations can be chained.
		 */
		if (keyVal == '=' || displayString.startsWith(ERROR_STRING)) {
			calcChar = ' ';
			operatorString = "";
		} else {
			calcChar = keyVal;
			operatorString = displayString;
		}

		// Set the clear display flag and show the result.
		clearDisplay = true;
		setDisplayString(displayString, true);
	}

	/*
	 * This method formats a string.
	 */
	private String trimString(final String newString) {
		String tempString = newString;

		// Value is not a number
		if (tempString.equals("NaN")) {
			tempString = ERROR_STRING + NAN_STRING;
			return tempString;
		}
		// Value is infinity
		if (tempString.equals("Infinity") || tempString.equals("-Infinity")) {
			tempString = ERROR_STRING + INFINITY_STRING;
			return tempString;
		}
		// Value is -0
		if (tempString.equals(-0.0)) {
			tempString = "0";
			return tempString;
		}
		// Trim unnecessary trailing .0
		if (tempString.endsWith(".0")) {
			tempString = tempString.substring(0, tempString.length() - 2);
		}
		/*
		 * // String is too long to display if (tempString.length() > 8) {
		 * //tempString = ERROR_STRING + LONG_STRING; tempString =
		 * tempString.substring(0, 9); }
		 */
		return tempString;
	}

}
