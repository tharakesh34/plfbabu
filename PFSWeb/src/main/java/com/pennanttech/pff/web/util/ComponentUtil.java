package com.pennanttech.pff.web.util;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.model.finance.FinanceMain;

public class ComponentUtil {
	private ComponentUtil() {
		//
	}

	public static long getFinID(ExtendedCombobox component) {

		Object object = component.getObject();

		if (object instanceof FinanceMain) {
			return ((FinanceMain) component.getObject()).getFinID();
		}

		if (object instanceof FinChangeCustomer) {
			return ((FinChangeCustomer) component.getObject()).getFinID();
		}

		return 0;
	}

}
