package com.pennant.webui.customermasters.directordetail.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.DirectorDetail;

public class CustomerDirectorComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 6780367280268538892L;

	public CustomerDirectorComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		DirectorDetail data = (DirectorDetail) o1;
		DirectorDetail data2 = (DirectorDetail) o2;
		return String.valueOf(data.getCustID()).compareTo(String.valueOf(data2.getCustID()));

	}
}
