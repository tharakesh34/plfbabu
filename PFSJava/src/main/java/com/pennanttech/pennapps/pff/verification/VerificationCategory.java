package com.pennanttech.pennapps.pff.verification;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.ValueLabel;

public enum VerificationCategory {
	INTERNAL(1, "Internal"), EXTERNAL(2, "External"), ONEPAGER(3, "");

	private final Integer key;
	private final String value;

	private VerificationCategory(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static VerificationCategory getType(Integer key) {
		for (VerificationCategory category : values()) {
			if (category.getKey() == key) {
				return category;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (VerificationCategory category : values()) {
			list.add(new ValueLabel(String.valueOf(category.getKey()), category.getValue()));
		}
		return list;
	}
}
