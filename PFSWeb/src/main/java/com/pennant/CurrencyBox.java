package com.pennant;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTDecimalValidator;

public class CurrencyBox extends Hbox {
	private static final long serialVersionUID = -4246285143621221275L;
	private static final Logger logger = LogManager.getLogger(CurrencyBox.class);

	private Space space;
	private Space spaceBtwComp;
	private Textbox textbox;
	private Decimalbox decimalbox;

	private static final int tb_Width = 180;
	private static final int db_Width = 200;

	private int scale;
	private int maxLength = 24;

	private String H = "H";
	private String T = "T";
	private String M = "M";
	private String B = "B";

	private BigDecimal HUNDERED = new BigDecimal(100);
	private BigDecimal THOUSAND = new BigDecimal(1000);
	private BigDecimal MILLIONORLAKHS = new BigDecimal(1000000);
	private BigDecimal BILLIONSORCRORES = new BigDecimal(1000000000);

	private String pattren = "[0-9BMTH\\.]*";

	private String errormesage1 = " Invalid format.Allowed only numbers and 'B' 'M' 'T' 'H' in sequence. Ex: 1M, 2B";
	private String errormesage2 = " Allowed Decimal points are : ";
	private String errormesage3 = " A character can't be follwed by another character";
	private String errormesage4 = " Value exceeded the maximum range.";
	private String errormesage5 = " Value should not be negative.";
	private boolean allowNagativeValues = false;

	public CurrencyBox() {
		super();
		setConstantsBasedOnImplementation();
		scale = 3;
		space = new Space();
		space.setWidth("2px");
		this.appendChild(space);
		textbox = new Textbox();
		textbox.setWidth(tb_Width + "px");
		textbox.setStyle("text-transform: uppercase;text-align:right;");
		textbox.setMaxlength(maxLength);
		textbox.addForward("onChange", this, "onValueChange");
		this.appendChild(textbox);
		spaceBtwComp = new Space();
		spaceBtwComp.setWidth("10px");
		this.appendChild(spaceBtwComp);
		decimalbox = new Decimalbox();
		decimalbox.setWidth(db_Width + "px");
		decimalbox.setReadonly(true);
		decimalbox.setStyle("border:none; background-color:white ;font-weight:bold; text-align:left;");
		decimalbox.setTabindex(-1);
		this.appendChild(decimalbox);
	}

	public void onValueChange(Event event) throws Exception {
		logger.debug("Entering");
		this.textbox.setConstraint("");
		this.textbox.setErrorMessage("");
		try {
			if (StringUtils.isNotEmpty(textbox.getValue())) {
				String val = textbox.getValue();
				val = val.replace(",", "").toUpperCase();

				if (val.matches(pattren)) {
					if (val.contains(B) || val.contains(M) || val.contains(T) || val.contains(H)) {
						calculateNumber(val);
					} else {
						decimalbox.setValue(new BigDecimal(val).setScale(scale));
					}
				} else {
					textbox.getValue();
					if (!allowNagativeValues) {
						if (textbox.getValue().contains("-")) {
							throw new WrongValueException(this.textbox, errormesage5);
						} else {
							throw new WrongValueException(this.textbox, errormesage1);
						}
					}
				}

				if (String.valueOf(PennantApplicationUtil.unFormateAmount(decimalbox.getValue(), scale))
						.length() > 18) {
					decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
					throw new WrongValueException(this.textbox, errormesage4);
				}

				String value = textbox.getValue();
				if (allowNagativeValues && value.compareTo("0") < 0) {
					value = value.replace(",", "").toUpperCase();
					BigDecimal negValue = new BigDecimal(value);
					decimalbox.setValue(negValue);
					decimalbox.getValue();
				} else {
					decimalbox.getValue();
				}

			} else {
				decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
			}
		} catch (WrongValueException e) {
			logger.error("Exception :", e);
			clrearError();
			throw new WrongValueException(textbox, e.getMessage());
		} catch (ArithmeticException e) {
			logger.error("Exception :", e);
			clrearError();
			throw new WrongValueException(this.textbox, errormesage2 + scale);
		} catch (NumberFormatException e) {
			logger.error("Exception :", e);
			clrearError();
			throw new WrongValueException(textbox, errormesage1);
		} catch (Exception e) {
			logger.error("Exception :", e);
			clrearError();
		} finally {
			Events.postEvent("onFulfill", this, null);
		}
		logger.debug("Leaving");
	}

	public void setFormat(String format) {
		decimalbox.setFormat(format);
	}

	public String getFormat() {
		return decimalbox.getFormat();
	}

	public Component getErrorComp() {
		return textbox;
	}

	public void setScale(int scale) {
		this.scale = scale;
		decimalbox.setScale(scale);
	}

	public void setRoundingMode(int ms) {
		decimalbox.setRenderdefer(ms);
	}

	public Double doubleValue() {
		return decimalbox.doubleValue();
	}

	public boolean isReadonly() {
		return textbox.isReadonly();
	}

	public void setDisabled(boolean disabled) {
		textbox.setDisabled(disabled);
		textbox.setVisible(!disabled);
		spaceBtwComp.setVisible(!disabled);
		if (disabled) {
			decimalbox.setStyle("border:normal;");
		} else {
			decimalbox.setStyle("border:none; background-color:white ;font-weight:bold; text-align:left;");
		}
	}

	public void setConstraint(String constraint) {
		decimalbox.setConstraint(constraint);
	}

	public void setConstraint(Constraint constraint) {
		decimalbox.setConstraint(constraint);
	}

	public void setErrorMessage(String errmsg) {
		textbox.setErrorMessage(errmsg);
		decimalbox.setErrorMessage(errmsg);
	}

	public void clearErrorMessage() {
		textbox.clearErrorMessage();
	}

	public void setMandatory(boolean mandatory) {
		if (mandatory) {
			this.space.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space.setSclass("");
		}
	}

	public boolean isMandatory() {
		if (StringUtils.trimToEmpty(this.space.getSclass()).equals(PennantConstants.mandateSclass)) {
			return true;
		}
		return false;
	}

	public void setValue(BigDecimal bigDecimal) {
		decimalbox.setValue(bigDecimal);
		textbox.setValue(CurrencyUtil.formatAmount(bigDecimal, this.scale));
	}

	public void setValue(String bigDecimal) {
		decimalbox.setValue(bigDecimal);
		textbox.setValue(bigDecimal);
	}

	public void setTextBoxWidth(int width) {
		textbox.setWidth(width + "px");
		decimalbox.setWidth(width + "px");
	}

	public Textbox getCcyTextBox() {
		return this.textbox;
	}

	public boolean isDisabled() {
		return this.textbox.isDisabled();
	}

	public void setReadonly(boolean readOnly) {
		this.textbox.setReadonly(readOnly);
		if (readOnly) {
			setTabindex(-1);
		}
		if (readOnly) {
			setMandatory(false);
		}
	}

	public void setTabindex(int tabIndex) {
		textbox.setTabindex(tabIndex);
	}

	public BigDecimal getValidateValue() throws WrongValueException {
		BigDecimal bigDecimal = null;
		this.textbox.setErrorMessage("");

		// To be Removed on Future , in case of setting field Parameter directly
		// to Currency box
		String fieldParam = "Value";
		if (this.decimalbox.getConstraint() != null) {
			if (this.decimalbox.getConstraint() instanceof PTDecimalValidator) {
				fieldParam = ((PTDecimalValidator) this.decimalbox.getConstraint()).getFieldParm();
			}
		}
		this.decimalbox.setConstraint("");
		this.decimalbox.setErrorMessage("");
		try {
			if (StringUtils.isNotEmpty(textbox.getValue())) {
				String valuWithComma = textbox.getValue();
				valuWithComma = valuWithComma.replace(",", "").toUpperCase();

				if (valuWithComma.matches(pattren)) {
					if (valuWithComma.contains(B) || valuWithComma.contains(M) || valuWithComma.contains(T)
							|| valuWithComma.contains(H)) {
						calculateNumber(valuWithComma);
					} else {
						decimalbox.setValue(new BigDecimal(valuWithComma).setScale(scale));
					}
				} else {

					textbox.getValue();
					if (!allowNagativeValues) {
						if (textbox.getValue().contains("-")) {
							throw new WrongValueException(this.textbox, errormesage5);
						} else {
							throw new WrongValueException(this.textbox, errormesage1);
						}
					}
				}

				if (String.valueOf(PennantApplicationUtil.unFormateAmount(decimalbox.getValue(), scale))
						.length() > 18) {
					decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
					throw new WrongValueException(this.textbox, errormesage4);
				}
				String value = textbox.getValue();
				if (allowNagativeValues && value.compareTo("0") < 0) {
					value = value.replace(",", "").toUpperCase();
					BigDecimal negValue = new BigDecimal(value);
					decimalbox.setValue(negValue);
					bigDecimal = decimalbox.getValue();
				} else {
					bigDecimal = decimalbox.getValue();
				}

			} else {
				bigDecimal = decimalbox.getValue() == null ? BigDecimal.ZERO : decimalbox.getValue();
			}

			if (isMandatory() && decimalbox.getValue().compareTo(BigDecimal.ZERO) <= 0) {
				throw new WrongValueException(this.textbox,
						Labels.getLabel("AMOUNT_NOT_NEGATIVE", new String[] { fieldParam }));
			}
		} catch (WrongValueException e) {
			logger.error("Exception :", e);
			clrearError();
			throw new WrongValueException(textbox, e.getMessage());
		} catch (ArithmeticException e) {
			logger.error("Exception :", e);
			clrearError();
			throw new WrongValueException(this.textbox, errormesage2 + scale);
		} catch (NumberFormatException e) {
			logger.error("Exception :", e);
			clrearError();
			throw new WrongValueException(textbox, errormesage1);
		} catch (Exception e) {
			logger.error("Exception :", e);
			clrearError();
		}
		return bigDecimal;
	}

	public BigDecimal getActualValue() throws WrongValueException {
		BigDecimal bigDecimal = null;
		try {
			bigDecimal = decimalbox.getValue() == null ? BigDecimal.ZERO : decimalbox.getValue();
			if (StringUtils.isNotEmpty(textbox.getValue())) {

				String valuWithComma = textbox.getValue();
				valuWithComma = valuWithComma.replace(",", "").toUpperCase();
				if (!valuWithComma.matches(pattren)) {
					if (textbox.getValue().contains("-")) {
						throw new WrongValueException(this.textbox, errormesage5);
					} else {
						throw new WrongValueException(this.textbox, errormesage1);
					}
				} else if (String.valueOf(PennantApplicationUtil.unFormateAmount(decimalbox.getValue(), scale))
						.length() > 18) {
					decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
					throw new WrongValueException(this.textbox, errormesage4);
				} else if (bigDecimal.compareTo(BigDecimal.ZERO) == 0
						&& new BigDecimal(textbox.getValue()).compareTo(BigDecimal.ZERO) > 0) {
					throw new WrongValueException(this.textbox, errormesage4);
				}
			}
		} catch (WrongValueException e) {
			logger.error("Exception :", e);
			if (StringUtils.isNotEmpty(textbox.getValue())) {
				getValidateValue();
			}
			throw new WrongValueException(textbox, e.getMessage());
		} catch (ArithmeticException e) {
			logger.error("Exception :", e);
			throw new WrongValueException(this.textbox, errormesage2 + scale);
		} catch (NumberFormatException e) {
			logger.error("Exception :", e);
			throw new WrongValueException(textbox, errormesage1);
		}
		return bigDecimal;
	}

	private void setConstantsBasedOnImplementation() {
		if (ImplementationConstants.INDIAN_IMPLEMENTATION && !allowNagativeValues) {
			MILLIONORLAKHS = new BigDecimal(100000);// lakhs
			BILLIONSORCRORES = new BigDecimal(10000000);// crores
			M = "L";
			B = "C";
			errormesage1 = " Invalid format.Allowed only numbers and 'C' 'L' 'T' 'H' in sequence. Ex: 1C, 1L";
			pattren = "[0-9CLTH\\.]*";
		}
	}

	private void clrearError() {
		decimalbox.setConstraint("");
		decimalbox.setErrorMessage("");
		decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
	}

	private void calculateNumber(String valueToConvert) throws WrongValueException {
		if (isValidString(valueToConvert)) {
			BigDecimal total = BigDecimal.ZERO;
			boolean conversionExists = false;
			// calculate Billions
			if (StringUtils.containsIgnoreCase(valueToConvert, B)) {
				String temp = valueToConvert.substring(0, valueToConvert.indexOf(B));
				total = total.add(new BigDecimal(temp).multiply(BILLIONSORCRORES));
				conversionExists = true;
			}
			// calculate Millions
			if (StringUtils.containsIgnoreCase(valueToConvert, M)) {
				String temp = valueToConvert.substring(valueToConvert.indexOf(B) + 1, valueToConvert.indexOf(M));
				total = total.add(new BigDecimal(temp).multiply(MILLIONORLAKHS));
				conversionExists = true;
			}
			// calculate Thousands
			if (StringUtils.containsIgnoreCase(valueToConvert, T)) {
				String temp = "";
				if (StringUtils.containsIgnoreCase(valueToConvert, M)) {
					temp = valueToConvert.substring(valueToConvert.indexOf(M) + 1, valueToConvert.indexOf(T));
				} else if (StringUtils.containsIgnoreCase(valueToConvert, B)) {
					temp = valueToConvert.substring(valueToConvert.indexOf(B) + 1, valueToConvert.indexOf(T));
				} else {
					temp = valueToConvert.substring(0, valueToConvert.indexOf(T));
				}
				total = total.add(new BigDecimal(temp).multiply(THOUSAND));
				conversionExists = true;
			}
			// calculate hundreds
			if (StringUtils.containsIgnoreCase(valueToConvert, H)) {
				String temp = "";
				if (StringUtils.containsIgnoreCase(valueToConvert, T)) {
					temp = valueToConvert.substring(valueToConvert.indexOf(T) + 1, valueToConvert.indexOf(H));
				} else if (StringUtils.containsIgnoreCase(valueToConvert, M)) {
					temp = valueToConvert.substring(valueToConvert.indexOf(M) + 1, valueToConvert.indexOf(H));
				} else if (StringUtils.containsIgnoreCase(valueToConvert, B)) {
					temp = valueToConvert.substring(valueToConvert.indexOf(B) + 1, valueToConvert.indexOf(H));
				} else {
					temp = valueToConvert.substring(0, valueToConvert.indexOf(H));
				}
				total = total.add(new BigDecimal(temp).multiply(HUNDERED));
				conversionExists = true;
			}

			/*
			 * //calculate Decimals String[] decimalVal = valueToConvert.replace(".", "#").split("#"); if
			 * (decimalVal.length == 2) { total = total.add(new BigDecimal("0." + decimalVal[1])); }
			 */

			try {
				// Set total value to the decimal box
				if (conversionExists) {
					decimalbox.setValue(total.setScale(scale));
				} else {
					decimalbox.setValue(new BigDecimal(valueToConvert).setScale(scale));
				}
			} catch (ArithmeticException e) {
				logger.error("Exception :", e);
				decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
				throw new WrongValueException(this.textbox, errormesage2 + scale);
			} catch (NumberFormatException e) {
				logger.error("Exception :", e);
				decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
				throw new WrongValueException(this.textbox, errormesage1);
			}
		} else {
			decimalbox.setValue(BigDecimal.ZERO.setScale(scale));
		}
		logger.debug("Leaving");
	}

	private boolean isValidString(String value) throws WrongValueException {
		int indexofb = 0;
		int indexofm = 0;
		int indexoft = 0;
		int indexofh = 0;

		if (StringUtils.containsIgnoreCase(value, B)) {
			indexofb = value.indexOf(B);
		}
		if (StringUtils.containsIgnoreCase(value, M)) {
			indexofm = value.indexOf(M);
		}
		if (StringUtils.containsIgnoreCase(value, T)) {
			indexoft = value.indexOf(T);
		}
		if (StringUtils.containsIgnoreCase(value, H)) {
			indexofh = value.indexOf(H);
		}

		if (indexofb > 0) {
			if (indexofm > 0 && indexofm < indexofb) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
			if (indexoft > 0 && indexoft < indexofb) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
			if (indexofh > 0 && indexofh < indexofb) {
				throw new WrongValueException(this.textbox, errormesage1);
			}

		}
		if (indexofm > 0) {
			if (indexoft > 0 && indexoft < indexofm) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
			if (indexofh > 0 && indexofh < indexofm) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
		}
		if (indexoft > 0) {
			if (indexofh > 0 && indexofh < indexoft) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
		}

		if (indexofb != 0) {
			if (indexofm != 0 && indexofm - indexofb == 1) {
				throw new WrongValueException(this.textbox, errormesage3);
			}
			if (indexoft != 0 && indexoft - indexofb == 1) {
				throw new WrongValueException(this.textbox, errormesage3);
			}
			if (indexofh != 0 && indexofh - indexofb == 1) {
				throw new WrongValueException(this.textbox, errormesage3);
			}
		}
		if (indexofm != 0) {
			if (indexoft != 0 && indexoft - indexofm == 1) {
				throw new WrongValueException(this.textbox, errormesage3);
			}
			if (indexofh != 0 && indexofh - indexofm == 1) {
				throw new WrongValueException(this.textbox, errormesage3);
			}
		}
		if (indexoft != 0) {
			if (indexofh != 0 && indexofh - indexoft == 1) {
				throw new WrongValueException(this.textbox, errormesage3);
			}
		}

		return true;
	}

	public void setProperties(boolean mandatory, int format) {
		setMandatory(mandatory);
		setFormat(PennantApplicationUtil.getAmountFormate(format));
		setScale(format);
	}

	public void setBalUnvisible(boolean isUnVisible) {
		if (isUnVisible) {
			this.decimalbox.setVisible(false);
			this.setStyle("text-align:right;width:100%;");
			this.setPack("end");
		} else {
			this.decimalbox.setVisible(true);
		}
	}

	public void setBalUnvisible(boolean isUnVisible, boolean setEnd) {
		setBalUnvisible(isUnVisible);

		if (isUnVisible && setEnd) {
			this.setPack("end");
		}
	}

	public void setAllowNagativeValues(boolean allowNagativeValues) {
		this.allowNagativeValues = allowNagativeValues;
	}

	public boolean isAllowNagativeValues() {
		return allowNagativeValues;
	}

	public void setRemoveSpace(boolean removeSpace) {
		space.setVisible(!removeSpace);
	}

}
