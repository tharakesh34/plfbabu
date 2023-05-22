package com.pennant.webui.customermasters.customerphonenumber.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

public class CustomerPhoneComparater implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = -6494243803772761322L;

	public CustomerPhoneComparater() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		// Auto-generated method stub
		CustomerPhoneNumber data1 = (CustomerPhoneNumber) o1;
		CustomerPhoneNumber data2 = (CustomerPhoneNumber) o2;
		return String.valueOf(data1.getPhoneCustID()).compareTo(String.valueOf(data2.getPhoneCustID()));
	}

}
