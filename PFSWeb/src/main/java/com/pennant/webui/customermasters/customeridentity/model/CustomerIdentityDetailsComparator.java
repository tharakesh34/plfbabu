package com.pennant.webui.customermasters.customeridentity.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerIdentity;

public class CustomerIdentityDetailsComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public CustomerIdentityDetailsComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerIdentity data = (CustomerIdentity) o1;
		CustomerIdentity data2 = (CustomerIdentity) o2;
		return String.valueOf(data.getIdCustID()).compareTo(String.valueOf(data2.getIdCustID()));
	}

}
