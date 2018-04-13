package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum FILivingStandard {

	SELECT(0, Labels.getLabel("Combo.Select")), POSH(1, "Posh"), SLUM(2, "Slum"), UPPERCLASS(3, "Upper Class"),
	MIDDLECLASS(4, "Middle Class"),CDA(5, "Community Dominated Area");
	private final Integer key;
	private final String value;

	private FILivingStandard(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (FILivingStandard type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}
		return list;
	}

}
