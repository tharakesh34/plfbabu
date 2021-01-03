package com.pennant.util.Constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.util.PennantRegularExpressions;

public class PTMobileNumberValidator implements Constraint {
	private String fieldParm;
	private boolean mandatory = false;
	private String regex;

	public PTMobileNumberValidator(String fieldParm, boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
	}

	public PTMobileNumberValidator(String fieldParm, boolean mandatory, String regex) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setRegex(regex);
	}

	public PTMobileNumberValidator(String fieldParm, boolean mandatory, String regex, int length) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setRegex(regex);
	}

	@Override
	public void validate(Component comp, Object value) {
		String errorMessage = getErrorMessage(value);
		if (StringUtils.isNotBlank(errorMessage)) {
			throw new WrongValueException(comp, errorMessage);
		}
	}

	private String getErrorMessage(Object value) {
		String compValue = null;
		boolean validRegex = false;

		if (value != null) {
			compValue = value.toString();
		}

		if (StringUtils.isBlank(compValue)) {
			if (isMandatory()) {
				return Labels.getLabel("FIELD_IS_MAND", new String[] { fieldParm });
			} else {
				return null;
			}
		} else {
			if (StringUtils.isBlank(regex)) {
				regex = PennantRegularExpressions.REGEX_FAX;
			}
			
			String expression = PennantRegularExpressions.getRegexMapper(regex);
			
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(compValue);
			validRegex = matcher.matches();

			if (!validRegex) {
				return Labels.getLabel("FIELD_MOBILE", new String[] { fieldParm, String.valueOf(pattern) });
			}

		}
		return null;
	}

	String getFieldParm() {
		return fieldParm;
	}

	boolean isMandatory() {
		return mandatory;
	}

	void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}

	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
}
