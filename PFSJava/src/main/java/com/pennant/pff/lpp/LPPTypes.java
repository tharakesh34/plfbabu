package com.pennant.pff.lpp;

import java.util.Arrays;
import java.util.List;

public enum LPPTypes {

	FLAT("F"),

	FLAT_ON_PD_MTH("A"),

	PERC_ONE_TIME("P"),

	PERC_ON_PD_MTH("M"),

	PERC_ON_DUE_DAYS("D"),

	PERC_ON_EFF_DUE_DAYS("E");

	private String code;

	private LPPTypes(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static LPPTypes getTypes(String code) {
		List<LPPTypes> list = Arrays.asList(LPPTypes.values());

		for (LPPTypes it : list) {
			if (it.code().equals(code)) {
				return it;
			}
		}

		return null;
	}

	public static boolean isValidLppType(String code) {
		LPPTypes type = LPPTypes.getTypes(code);

		if (type == null) {
			return false;
		}

		switch (type) {
		case FLAT:
		case FLAT_ON_PD_MTH:
		case PERC_ONE_TIME:
		case PERC_ON_PD_MTH:
		case PERC_ON_DUE_DAYS:
		case PERC_ON_EFF_DUE_DAYS:
			return true;
		default:
			return false;
		}
	}
}
