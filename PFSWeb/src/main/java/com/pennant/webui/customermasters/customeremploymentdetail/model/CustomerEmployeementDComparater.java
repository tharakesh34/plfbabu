package com.pennant.webui.customermasters.customeremploymentdetail.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;

public class CustomerEmployeementDComparater implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 2285565639591177581L;

	public CustomerEmployeementDComparater() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		// Auto-generated method stub
		CustomerEmploymentDetail data1 = (CustomerEmploymentDetail) o1;
		CustomerEmploymentDetail data2 = (CustomerEmploymentDetail) o2;
		return String.valueOf(data1.getCustID()).compareTo(String.valueOf(data2.getCustID()));
	}

}
