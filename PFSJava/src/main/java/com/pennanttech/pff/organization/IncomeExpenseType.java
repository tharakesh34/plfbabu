package com.pennanttech.pff.organization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.ValueLabel;

public enum IncomeExpenseType {
	CORE_INCOME("CORE_INCOME", "CORE_INCOME"), NON_CORE_INCOME("NON_CORE_INCOME", "NON_CORE_INCOME"),
	EXPENSE("EXPENSE", "EXPENSE");

	private final String key;
	private final String value;

	private IncomeExpenseType(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static IncomeExpenseType getType(String key) {
		for (IncomeExpenseType type : values()) {
			if (StringUtils.equals(type.getKey(), key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (IncomeExpenseType type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}
		return list;
	}
}
