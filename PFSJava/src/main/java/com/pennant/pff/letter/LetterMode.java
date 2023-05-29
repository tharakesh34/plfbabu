package com.pennant.pff.letter;

import java.util.Arrays;
import java.util.List;

public enum LetterMode {
	COURIER("Courier"),

	EMAIL("Email"),

	OTC("OTC");

	private String description;

	private LetterMode(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static LetterMode getMode(String letterMode) {
		List<LetterMode> list = Arrays.asList(LetterMode.values());

		for (LetterMode it : list) {
			if (it.name().equals(letterMode)) {
				return it;
			}
		}

		return null;
	}
}
