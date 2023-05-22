package com.pennant.webui.customermasters.customeraddres.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerAddres;

public class CustomerAddresComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public CustomerAddresComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerAddres data = (CustomerAddres) o1;
		CustomerAddres data2 = (CustomerAddres) o2;
		return String.valueOf(data.getCustID()).compareTo(String.valueOf(data2.getCustID()));
	}

}
