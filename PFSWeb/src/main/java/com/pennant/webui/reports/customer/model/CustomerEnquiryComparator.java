package com.pennant.webui.reports.customer.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.finance.FinanceEnquiry;

public class CustomerEnquiryComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public CustomerEnquiryComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		FinanceEnquiry finEnq = (FinanceEnquiry) o1;
		FinanceEnquiry finEnq2 = (FinanceEnquiry) o2;
		int compFinType = finEnq.getFinType().compareTo(finEnq2.getFinType());
		if (compFinType == 0) {
			return finEnq.getFinCcy().compareTo(finEnq2.getFinCcy());
		}
		return compFinType;
	}

}