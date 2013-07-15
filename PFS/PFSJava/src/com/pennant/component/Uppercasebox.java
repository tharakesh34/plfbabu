package com.pennant.component;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Textbox;

public class Uppercasebox extends Textbox {
	private static final long serialVersionUID = -4246285143621221275L;

	public Uppercasebox() {
		// super();
	}

	@Override
	public String getValue() throws WrongValueException {
		return null == super.getValue() ? super.getValue() : super.getValue()
				.toUpperCase();
	}

	@Override
	public void setValue(String value) throws WrongValueException {
		super.setValue(null == value ? value : value.toUpperCase());
	}

	@Override
	public void setStyle(String style) {
		if (!"text-transform: uppercase;".equals(style)) {
			style = "text-transform: uppercase;" + style;
		}
		super.setStyle(style);
	}
}
