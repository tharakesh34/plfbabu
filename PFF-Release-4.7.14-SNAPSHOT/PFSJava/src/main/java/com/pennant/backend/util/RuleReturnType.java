package com.pennant.backend.util;

public enum RuleReturnType {
		STRING("S"), 
		DECIMAL("D"), 
		INTEGER("I"), 
		BOOLEAN("B"), 
		OBJECT("O"),
		CALCSTRING("C");

		private String value;

		private RuleReturnType(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}
	}