package com.pennanttech.pff.web.util;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinanceMain;

public class ComponentUtil {
	private ComponentUtil() {
		//
	}

	public static long getFinID(ExtendedCombobox component) {
		FinanceMain fm = (FinanceMain) component.getObject();
		return fm.getFinID();
	}

}
