package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum RCUStatus {
	
	SELECT(0, Labels.getLabel("Combo.Select")), NONE(1, "None"), POSITIVE(2, "Positive"), NEGATIVE(3,
			"Negative"), NOT_COMPLETED(4, "RCU Not Completed");

	private final Integer key;
	private final String value;

	private RCUStatus(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public static RCUStatus getType(Integer key) {
		for (RCUStatus type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (RCUStatus status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}


}
