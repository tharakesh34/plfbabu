package com.pennant.pff.accounting;

import java.util.Arrays;
import java.util.List;

public enum TransactionType {
	NONE("N"),

	DEBIT("D"),

	CREDIT("C"),

	BOTH("B");

	private String code;

	private TransactionType(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static boolean isNone(String code) {
		return isEqual(NONE, object(code));
	}

	public static boolean isDebit(String code) {
		return isEqual(DEBIT, object(code));
	}

	public static boolean isCredit(String code) {
		return isEqual(CREDIT, object(code));
	}

	public static boolean isBoth(String code) {
		return isEqual(BOTH, object(code));
	}

	private static boolean isEqual(TransactionType transactionType, TransactionType type) {
		return type == null ? false : type == transactionType;
	}

	public static TransactionType object(String code) {
		List<TransactionType> list = Arrays.asList(TransactionType.values());

		for (TransactionType tt : list) {
			if (tt.code.equals(code)) {
				return tt;
			}
		}

		return null;
	}
}
