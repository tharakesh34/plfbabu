package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum VettingStatus {

	SELECT(0, Labels.getLabel("Combo.Select")),
	POSITIVE(1, Labels.getLabel("label_VETTING_POSITIVE")),
	NEGATIVE(2, Labels.getLabel("label_VETTING_NEGATIVE"));

	private final Integer key;
	private final String value;

	private VettingStatus(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static VettingStatus getType(Integer key) {
		for (VettingStatus type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (VettingStatus status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}
}