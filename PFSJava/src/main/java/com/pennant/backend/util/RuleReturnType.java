package com.pennant.backend.util;

public enum RuleReturnType {
	STRING("S"), DECIMAL("D"), RATE("R"), INTEGER("I"), BOOLEAN("B"), OBJECT("O"), CALCSTRING("C");

	private String value;

	private RuleReturnType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}