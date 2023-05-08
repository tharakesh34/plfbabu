package com.pennant.webui.customermasters.customeremail.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerEMail;

public class CustomerEmailComparater implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = -5089682583362960445L;

	public CustomerEmailComparater() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerEMail data1 = (CustomerEMail) o1;
		CustomerEMail data2 = (CustomerEMail) o2;
		return String.valueOf(data1.getCustID()).compareTo(String.valueOf(data2.getCustID()));

	}

}
