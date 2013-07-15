package com.pennant;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

public class CurrencyBox extends Hbox {
	private static final long	    serialVersionUID	= -4246285143621221275L;
	private final static Logger	    logger	         = Logger.getLogger(CurrencyBox.class);
	private Space	                space;
	private Textbox	                textbox;
	private Decimalbox	            decimalbox;

	private static final int	    tb_Width	     = 120;
	private static final int	    db_Width	     = 120;

	private int	                    scale;

	private static final BigDecimal	HUNDERED	     = new BigDecimal(100);
	private static final BigDecimal	THOUSAND	     = new BigDecimal(1000);
	private static final BigDecimal	MILLION	         = new BigDecimal(1000000);
	private static final BigDecimal	BILLIONS	     = new BigDecimal(1000000000);

	private static final String	    H	             = "H";
	private static final String	    T	             = "T";
	private static final String	    M	             = "M";
	private static final String	    B	             = "B";

	private static final String	    sClass	         = "mandatory";

	private static final String	    errormesage1	 = "Invalid Fomrat.Allwoed Only Numbers and 'B' 'M' 'T' 'H' In Sequence";
	private static final String	    errormesage2	 = "Characters Are Not allowed After the Decimal point";
	private static final String	    errormesage3	 = "Only One Decimal point Allowed";
	private static final String	    errormesage4	 = "Allowed Decimal point are :";

	public CurrencyBox() {
		logger.debug("Entering ExtenedBox()");
		scale = 3;
		space = new Space();
		space.setWidth("2px");
		this.appendChild(space);
		textbox = new Textbox();
		textbox.setWidth(tb_Width + "px");
		textbox.setStyle("text-transform: uppercase;");
		textbox.addForward("onChange", this, "onValueChange");
		this.appendChild(textbox);
		decimalbox = new Decimalbox();
		decimalbox.setWidth(db_Width + "px");
		decimalbox.setReadonly(true);
		decimalbox.setStyle("border:none; background-color:white ;font-weight:bold;");
		this.appendChild(decimalbox);
		logger.debug("Leaving ExtenedBox()");
	}

	public void onValueChange(Event event) throws Exception {
		logger.debug("Entering onValueChange()");
		try {
			if (textbox.getValue() != null && textbox.getValue().length() != 0) {
				try {
					decimalbox.setValue(new BigDecimal(textbox.getValue()).setScale(scale));
				} catch (Exception e) {
					if (e instanceof ArithmeticException) {
						decimalbox.setValue(new BigDecimal(textbox.getValue()).setScale(scale, RoundingMode.HALF_DOWN));
						throw new WrongValueException(this.textbox, errormesage4 + scale);
					} else {
						calculateNumber(textbox.getValue().toUpperCase());
					}
				}
			}
		} catch (Exception e) {
			decimalbox.setValue(new BigDecimal(0));
			if (e instanceof WrongValueException) {
				throw e;
			}
		}finally{
			Events.postEvent("onFulfill", this, null);
		}
		logger.debug("Leaving onValueChange()");
	}

	private void calculateNumber(String valueToConvert) throws WrongValueException, Exception {
		logger.debug("Entering calculateNumber()");
		if (isValidString(valueToConvert)) {
			BigDecimal total = new BigDecimal(0);
			//calculate Billions
			if (StringUtils.containsIgnoreCase(valueToConvert, B)) {
				String temp = valueToConvert.substring(0, valueToConvert.indexOf(B));
				total = total.add(new BigDecimal(temp).multiply(BILLIONS));
			}
			//calculate Millions
			if (StringUtils.containsIgnoreCase(valueToConvert, M)) {
				String temp = valueToConvert.substring(valueToConvert.indexOf(B) + 1, valueToConvert.indexOf(M));
				total = total.add(new BigDecimal(temp).multiply(MILLION));

			}
			//calculate Thousands
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
			}
			//calculate hundreds
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
			}

			//calculate Decimals
			String[] decimalVal = valueToConvert.replace(".", "#").split("#");
			if (decimalVal.length == 2) {
				total = total.add(new BigDecimal("0." + decimalVal[1]));
			}
			try {
				//Set total value to the decimal box
				decimalbox.setValue(total.setScale(scale));
			} catch (ArithmeticException e) {
				decimalbox.setValue(new BigDecimal(0).setScale(scale));
				throw new WrongValueException(this.textbox, errormesage4 + scale);
			}
		} else {
			decimalbox.setValue(new BigDecimal(0).setScale(scale));
		}
		logger.debug("Leaving calculateNumber()");
	}

	private boolean isValidString(String value) throws Exception {
		logger.debug("Entering isValidString()");
		try {
			String temp = value.replace(B, "").replace(M, "").replace(T, "").replace(H, "");
			BigDecimal bigDecimal = new BigDecimal(temp);
			System.out.println(bigDecimal);

			String[] dotCheck = value.replace(".", "#").split("#");
			if (dotCheck.length != 0 && dotCheck.length != 1) {
				if (dotCheck.length == 2) {
					if (!dotCheck[1].matches("[0-9]*")) {
						throw new WrongValueException(this.textbox, errormesage2);
					}

				} else {
					throw new WrongValueException(this.textbox, errormesage3);
				}
			}

		} catch (Exception e) {
			if (e instanceof WrongValueException) {
				throw e;
			} else {
				throw new WrongValueException(this.textbox, errormesage1);
			}
		}
		int IDX_B = 0;
		int IDX_M = 0;
		int IDX_T = 0;
		int IDX_H = 0;

		if (StringUtils.containsIgnoreCase(value, B)) {
			IDX_B = value.indexOf(B);
		}
		if (StringUtils.containsIgnoreCase(value, M)) {
			IDX_M = value.indexOf(M);
		}
		if (StringUtils.containsIgnoreCase(value, T)) {
			IDX_T = value.indexOf(T);
		}
		if (StringUtils.containsIgnoreCase(value, H)) {
			IDX_H = value.indexOf(H);
		}

		if (IDX_B > 0) {
			if (IDX_M > 0 && IDX_M < IDX_B) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
			if (IDX_T > 0 && IDX_T < IDX_B) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
			if (IDX_H > 0 && IDX_H < IDX_B) {
				throw new WrongValueException(this.textbox, errormesage1);
			}

		}
		if (IDX_M > 0) {
			if (IDX_T > 0 && IDX_T < IDX_M) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
			if (IDX_H > 0 && IDX_H < IDX_M) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
		}
		if (IDX_T > 0) {
			if (IDX_H > 0 && IDX_H < IDX_T) {
				throw new WrongValueException(this.textbox, errormesage1);
			}
		}
		logger.debug("Leaving isValidString()");
		return true;
	}

	
	public void setFormat(String format) {
		decimalbox.setFormat(format);
	}

	public void setScale(int scale) {
		this.scale = scale;
		decimalbox.setScale(scale);
	}
	public void setMaxlength(int length){
		decimalbox.setMaxlength(length);
	}
	public void setRoundingMode(int ms){
		decimalbox.setRenderdefer(ms);
	}
	
	public Double doubleValue(){
		return decimalbox.doubleValue();
	}
	
	
	public boolean isReadonly(){
		return textbox.isReadonly();
	}

	public void setDisabled(boolean disabled){
		textbox.setDisabled(disabled);
		setMandatory(!disabled);
	}
	
	public void setConstraint(String constraint) {
		decimalbox.setConstraint(constraint);
	}
	public void setConstraint(Constraint constraint) {
		decimalbox.setConstraint(constraint);
	}

	public void setErrorMessage(String errmsg) {
		textbox.setErrorMessage(errmsg);
	}

	public void clearErrorMessage() {
		textbox.clearErrorMessage();
	}

	public void setMandatory(boolean mandatory) {
		if (mandatory) {
			this.space.setSclass(sClass);
		} else {
			this.space.setSclass("");
		}
	}

	public void setValue(BigDecimal bigDecimal) {
		decimalbox.setValue(bigDecimal);
	}
	public void setValue(String bigDecimal) {
		decimalbox.setValue(bigDecimal);
	}

	public BigDecimal getValue() {
		return decimalbox.getValue();
	}
	//	public Decimalbox getDecimalbox() {
	//		return decimalbox;
	//	}

}