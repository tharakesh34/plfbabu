package com.pennant.webui.reports.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class LoanEnquiryPostingsComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public LoanEnquiryPostingsComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		ReturnDataSet data = (ReturnDataSet) o1;
		ReturnDataSet data2 = (ReturnDataSet) o2;
		return String.valueOf(data.getFinEvent()).compareTo(String.valueOf(data2.getFinEvent()));

	}
}
