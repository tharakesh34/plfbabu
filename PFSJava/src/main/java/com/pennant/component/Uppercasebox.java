/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennant.component;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Textbox;

/**
 * An upper case text box.
 */
public class Uppercasebox extends Textbox {
	private static final long serialVersionUID = -4246285143621221275L;

	/**
	 * Creates a new upper case text box.
	 */
	public Uppercasebox() {
		super();
	}

	/**
	 * Returns the value.
	 * 
	 * @exception WrongValueException
	 *                - If user entered a wrong value.
	 */
	@Override
	public String getValue() {
		return null == super.getValue() ? super.getValue() : super.getValue().toUpperCase();
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            The value; If null, it is considered as empty.
	 * @exception WrongValueException
	 *                - If value is wrong.
	 */
	@Override
	public void setValue(String value) {
		if (value != null) {
			super.setValue(value.toUpperCase());
		} else {
			super.setValue("");
		}
	}

	/**
	 * Sets the CSS style.
	 * 
	 * @param style
	 *            The CSS style.
	 */
	@Override
	public void setStyle(String style) {
		String defaultStyle = "text-transform: uppercase;";

		if (!defaultStyle.equals(style)) {
			style = "text-transform: uppercase;" + style;
		}

		super.setStyle(style);
	}
}
