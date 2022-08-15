package com.pennanttech.pennapps.pff.verification;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum LegaVerificationType {
	LV(1, Labels.getLabel("label_LV")), TSR(2, Labels.getLabel("label_TSR"));

	private final String value;
	private final Integer key;

	private LegaVerificationType(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static LegaVerificationType getType(Integer key) {
		for (LegaVerificationType type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (LegaVerificationType type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}
		return list;
	}

	public static List<ValueLabel> getLegaVerificationType() {
		List<ValueLabel> list = new ArrayList<>();
		for (LegaVerificationType type : values()) {
			String labelCode = "label_RCU_" + type.name();
			list.add(new ValueLabel(String.valueOf(type.getKey()), Labels.getLabel(labelCode)));
		}
		return list;
	}
}
