package com.pennanttech.pff.organization;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.ValueLabel;

public enum OrganizationType {
	SCHOOL(1, "School"), INDUSTRY(2, "Industry");

	private final Integer key;
	private final String value;

	private OrganizationType(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static OrganizationType getType(Integer key) {
		for (OrganizationType type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (OrganizationType type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}
		return list;
	}
}
