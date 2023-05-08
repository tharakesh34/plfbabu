package com.pennant.webui.customermasters.customerincome.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerIncome;

public class CustomerIncomeComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public CustomerIncomeComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerIncome data = (CustomerIncome) o1;
		CustomerIncome data2 = (CustomerIncome) o2;
		return String.valueOf(data.getCustId()).compareTo(String.valueOf(data2.getCustId()));

	}
}
