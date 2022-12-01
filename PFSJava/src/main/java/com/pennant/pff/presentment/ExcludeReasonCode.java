package com.pennant.pff.presentment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pennant.backend.model.ValueLabel;

public enum ExcludeReasonCode {
	EMI_INCLUDE(0, ""),

	EMI_IN_ADVANCE(1, ""),

	EMI_HOLD(2, ""),

	MANDATE_HOLD(3, ""),

	MANDATE_NOT_APPROVED(4, ""),

	MANDATE_EXPIRED(5, ""),

	MANUAL_EXCLUDE(6, ""),

	MANDATE_REJECTED(7, ""),

	CHEQUE_PRESENT(8, ""),

	// CHEQUE_BOUNCE(9, ""),

	// CHEQUE_REALISE(10, ""),

	// CHEQUE_REALISED(11, ""),

	INT_ADV(12, ""),

	EMI_ADV(13, "");

	private int id;
	private String description;

	private ExcludeReasonCode(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public String code() {
		return this.name();
	}

	public int id() {
		return id;
	}

	public String description() {
		return description;
	}

	public static String reasonCode(int excludeReason) {
		List<ExcludeReasonCode> list = Arrays.asList(ExcludeReasonCode.values());

		for (ExcludeReasonCode ec : list) {
			if (ec.id() == excludeReason) {
				return ec.code();
			}
		}

		return null;
	}

	private static List<ValueLabel> excludeCodes;

	public static List<ValueLabel> getExcludeCodes() {
		if (excludeCodes != null) {
			return excludeCodes;
		}

		excludeCodes = new ArrayList<>(4);

		for (ExcludeReasonCode item : ExcludeReasonCode.values()) {
			excludeCodes.add(new ValueLabel(item.name(), item.code()));
		}

		return excludeCodes;
	}
}
