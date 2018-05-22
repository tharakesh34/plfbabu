package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum TVStatus {

	SELECT(0, Labels.getLabel("Combo.Select")), POSITIVE(1, "Positive"), NEGATIVE(2, "Negative");

	private final Integer key;
	private final String value;

	private TVStatus(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public static TVStatus getType(Integer key) {
		for (TVStatus type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}


	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (TVStatus status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}
}
