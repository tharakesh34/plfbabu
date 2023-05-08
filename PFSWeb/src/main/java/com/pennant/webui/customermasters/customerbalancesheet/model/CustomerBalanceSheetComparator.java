package com.pennant.webui.customermasters.customerbalancesheet.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerBalanceSheet;

public class CustomerBalanceSheetComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 6780367280268538892L;

	public CustomerBalanceSheetComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerBalanceSheet data = (CustomerBalanceSheet) o1;
		CustomerBalanceSheet data2 = (CustomerBalanceSheet) o2;
		return String.valueOf(data.getCustId()).compareTo(String.valueOf(data2.getCustId()));
	}
}
