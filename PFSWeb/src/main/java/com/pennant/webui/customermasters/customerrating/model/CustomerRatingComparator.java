package com.pennant.webui.customermasters.customerrating.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerRating;

public class CustomerRatingComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public CustomerRatingComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerRating data = (CustomerRating) o1;
		CustomerRating data2 = (CustomerRating) o2;
		return String.valueOf(data.getCustID()).compareTo(String.valueOf(data2.getCustID()));

	}
}
