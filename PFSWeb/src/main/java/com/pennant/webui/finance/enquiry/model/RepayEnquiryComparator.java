package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.Repayments.FinanceRepayments;

public class RepayEnquiryComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public RepayEnquiryComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		FinanceRepayments data = (FinanceRepayments) o1;
		FinanceRepayments data2 = (FinanceRepayments) o2;
		return String.valueOf(data.getFinSchdDate()).compareTo(String.valueOf(data2.getFinSchdDate()));

	}
}
