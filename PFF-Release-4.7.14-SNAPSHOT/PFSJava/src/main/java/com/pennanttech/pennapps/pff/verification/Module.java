/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.pff.verification;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.ValueLabel;

public enum Module {

	LOAN(1, "Loan"), CUSTOMER(2, "Customer"), COLLATERAL(3, "Collateral");

	private final Integer key;
	private final String value;

	private Module(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		list.add(new ValueLabel());
		for (Module module : values()) {
			list.add(new ValueLabel(String.valueOf(module.getKey()), module.getValue()));
		}
		return list;
	}

}
