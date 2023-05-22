package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.service.finance.EligibilityRule;

public class EligibilityCheckComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public EligibilityCheckComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		EligibilityRule data = (EligibilityRule) o1;
		EligibilityRule data2 = (EligibilityRule) o2;
		return String.valueOf(data.getFinType()).compareTo(String.valueOf(data2.getFinType()));

	}
}
