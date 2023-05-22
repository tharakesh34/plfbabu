package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;

public class FinanceEnquiryPostingsComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public FinanceEnquiryPostingsComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		ReturnDataSet data = (ReturnDataSet) o1;
		ReturnDataSet data2 = (ReturnDataSet) o2;
		if (StringUtils.equals(PennantConstants.POSTDATE, data.getPostingGroupBy())) {
			return String.valueOf(data.getPostDate()).compareTo(String.valueOf(data2.getPostDate()));
		} else if (StringUtils.equals(PennantConstants.ACCNO, data.getPostingGroupBy())) {
			return String.valueOf(data.getAccount()).compareTo(String.valueOf(data2.getAccount()));
		} else {
			return String.valueOf(data.getFinEvent()).compareTo(String.valueOf(data2.getFinEvent()));
		}

	}
}
