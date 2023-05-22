package com.pennant.webui.rmtmasters.financetype.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.rmtmasters.FinanceType;

public class FinanceTypeComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = -8606975433219761922L;

	public FinanceTypeComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		FinanceType data = (FinanceType) o1;
		FinanceType data2 = (FinanceType) o2;
		return String.valueOf(data.getFinCategory()).compareTo(String.valueOf(data2.getFinCategory()));

	}
}
