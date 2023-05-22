package com.pennant.pff.knockoff;

import java.util.Arrays;
import java.util.List;

public enum KnockOffType {

	MANUAL("M", "Manual"),

	AUTO("A", "Auto"),

	CROSS_LOAN("C", "Cross Loan"),

	AUTO_CROSS_LOAN("AC", "Auto Cross Loan");

	private String code;
	private String desc;

	private KnockOffType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String code() {
		return code;
	}

	public String desc() {
		return desc;
	}

	public static String getDesc(String knockOffType) {
		KnockOffType value = getCodeType(knockOffType);

		if (value == null) {
			return "";
		}

		return value.desc();
	}

	public static KnockOffType getCodeType(String knockOffType) {
		List<KnockOffType> list = Arrays.asList(KnockOffType.values());

		for (KnockOffType it : list) {
			if (it.code().equals(knockOffType)) {
				return it;
			}
		}

		return null;
	}

	public static String getCode(String knockOffType) {
		KnockOffType value = getDescType(knockOffType);

		if (value == null) {
			return "";
		}

		return value.code();
	}

	public static KnockOffType getDescType(String knockOffType) {
		List<KnockOffType> list = Arrays.asList(KnockOffType.values());

		for (KnockOffType it : list) {
			if (it.desc().equals(knockOffType)) {
				return it;
			}
		}

		return null;
	}
}
