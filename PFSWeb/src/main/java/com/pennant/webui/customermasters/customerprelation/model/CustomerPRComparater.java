package com.pennant.webui.customermasters.customerprelation.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.customermasters.CustomerPRelation;

public class CustomerPRComparater implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 1811599533773017341L;

	public CustomerPRComparater() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		// Auto-generated method stub
		CustomerPRelation data1 = (CustomerPRelation) o1;
		CustomerPRelation data2 = (CustomerPRelation) o2;
		// return data1.getPRRelationCode().compareTo(data2.getPRRelationCode());
		if (data1.getPRCustID() > data2.getPRCustID()) {
			return 1;
		} else if (data1.getPRCustID() < data2.getPRCustID()) {
			return -1;
		} else {
			return 0;
		}
	}

}
