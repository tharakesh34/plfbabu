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

	private static boolean isEqual(ExcessHead excessHead, ExcessHead type) {
		return type == null ? false : type == excessHead;
	}

	public static boolean isExcess(String amountType) {
		return isEqual(EXCESS, getHead(amountType));
	}

	public static boolean isEmiInAdv(String amountType) {
		return isEqual(EMIINADV, getHead(amountType));
	}

	public static boolean isAdvEmi(String amountType) {
		return isEqual(ADVEMI, getHead(amountType));
	}

	public static boolean isAdvInt(String amountType) {
		return isEqual(ADVINT, getHead(amountType));
	}

	public static boolean isCashclt(String amountType) {
		return isEqual(CASHCLT, getHead(amountType));
	}

	public static boolean isDsf(String amountType) {
		return isEqual(DSF, getHead(amountType));
	}

	public static boolean isTExcess(String amountType) {
		return isEqual(TEXCESS, getHead(amountType));
	}

	public static boolean isSettlement(String amountType) {
		return isEqual(SETTLEMENT, getHead(amountType));
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
