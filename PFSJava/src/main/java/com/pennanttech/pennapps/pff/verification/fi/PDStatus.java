package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum PDStatus {

	SELECT(0, Labels.getLabel("Combo.Select")),
	POSITIVE(1, Labels.getLabel("label_POSITIVE")),
	NEGATIVE(2, Labels.getLabel("label_NEGATIVE")),
	REFERTOCREDIT(3, Labels.getLabel("label_REFERTOCREDIT"));

	private final Integer key;
	private final String value;

	private PDStatus(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static PDStatus getType(Integer key) {
		for (PDStatus type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (PDStatus status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}
}