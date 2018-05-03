package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum FINeighbourHoodFeedBack {
	SELECT(0, Labels.getLabel("Combo.Select")), POSITIVE(1, "Positive"), NEGATIVE(2, "Negative"), REFER(3, "Refer");
	private final Integer key;
	private final String value;

	private FINeighbourHoodFeedBack(Integer key, String value) {
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
		for (FINeighbourHoodFeedBack type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}
		return list;
	}

}
