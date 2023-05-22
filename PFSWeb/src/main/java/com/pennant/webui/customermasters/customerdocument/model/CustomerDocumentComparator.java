package com.pennant.webui.customermasters.customerdocument.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerDocument;

public class CustomerDocumentComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public CustomerDocumentComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		CustomerDocument data = (CustomerDocument) o1;
		CustomerDocument data2 = (CustomerDocument) o2;
		return String.valueOf(data.getCustID()).compareTo(String.valueOf(data2.getCustID()));
	}

}
