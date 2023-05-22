package com.pennant.pff.letter;

import java.util.Arrays;
import java.util.List;

public enum LetterType {
	NOC("NOC Letter"),

	CLOSURE("Closure Letter"),

	CANCELLATION("Cancellation Letter");

	private String description;

	private LetterType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static LetterType getType(String letterType) {
		List<LetterType> list = Arrays.asList(LetterType.values());

		for (LetterType it : list) {
			if (it.name().equals(letterType)) {
				return it;
			}
		}

		return null;
	}
}
