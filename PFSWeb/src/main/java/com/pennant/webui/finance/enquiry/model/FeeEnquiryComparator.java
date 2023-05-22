package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.finance.FinFeeDetail;

public class FeeEnquiryComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public FeeEnquiryComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		FinFeeDetail data = (FinFeeDetail) o1;
		FinFeeDetail data2 = (FinFeeDetail) o2;
		return String.valueOf(data.getFinEvent()).compareTo(String.valueOf(data2.getFinEvent()));

	}
}
