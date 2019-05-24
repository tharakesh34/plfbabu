package com.pennant.util.Constraint;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class PTEmailValidator implements Constraint {
	private String fieldParm;
	private boolean mandatory = false;
	private int maxLength = 100;

	public PTEmailValidator(String fieldParm, boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
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

			if (compValue.length() > maxLength) {
				return Labels.getLabel("FIELD_ALLOWED_MAXLENGTH",
						new String[] { fieldParm, String.valueOf(maxLength) });
			}

			validRegex = EmailValidator.getInstance(true).isValid(compValue);

			if (!validRegex) {
				return Labels.getLabel("FIELD_MAIL", new String[] { fieldParm });
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
}
