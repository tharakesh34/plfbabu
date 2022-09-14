package com.pennant.pff.accounting;

import java.util.Arrays;
import java.util.List;

public enum PostAgainst {
	LOAN("L"),

	CUSTOMER("C"),

	COLLATERAL("CLT"),

	LIMIT("LMT"),

	ENTITY("E");

	private String code;

	private PostAgainst(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static boolean isLoan(String type) {
		return isEqual(LOAN, object(type));
	}

	public static boolean isCustomer(String type) {
		return isEqual(CUSTOMER, object(type));
	}

	public static boolean isCollateral(String type) {
		return isEqual(COLLATERAL, object(type));
	}

	public static boolean isLimit(String type) {
		return isEqual(LIMIT, object(type));
	}

	public static boolean isEntity(String type) {
		return isEqual(ENTITY, object(type));
	}

	private static boolean isEqual(PostAgainst postAgaint, PostAgainst type) {
		return type == null ? false : type == postAgaint;
	}

	public static PostAgainst object(String code) {
		List<PostAgainst> list = Arrays.asList(PostAgainst.values());

		for (PostAgainst pa : list) {
			if (pa.code.equals(code)) {
				return pa;
			}
		}

		return null;
	}
}
