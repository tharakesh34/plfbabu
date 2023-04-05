package com.pennant.pff.excess;

import java.util.Arrays;
import java.util.List;

public enum ExcessHead {

	EXCESS("E"),

	EMIINADV("A"),

	ADVINT("ADVINT"),

	ADVEMI("ADVEMI"),

	CASHCLT("CASHCLT"),

	DSF("DSF"),

	TEXCESS("T"),

	SETTLEMENT("S");

	private String code;

	private ExcessHead(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static ExcessHead getHead(String code) {
		List<ExcessHead> list = Arrays.asList(ExcessHead.values());

		for (ExcessHead it : list) {
			if (it.code().equals(code)) {
				return it;
			}
		}

		return null;
	}

	public static boolean isValidExcessTransferHead(String code) {
		ExcessHead excessHead = ExcessHead.getHead(code);

		if (excessHead == null) {
			return false;
		}

		switch (excessHead) {
		case EXCESS:
		case EMIINADV:
		case TEXCESS:
		case SETTLEMENT:
			return true;
		default:
			return false;
		}
	}
}
