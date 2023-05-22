package com.pennant.pff.letter;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.ValueLabel;

public class LetterUtil {
	private LetterUtil() {
		super();
	}

	private static List<ValueLabel> letterTypes;
	private static List<ValueLabel> letterModes;

	public static List<ValueLabel> getLetterTypes() {
		if (letterTypes != null) {
			return letterTypes;
		}

		letterTypes = new ArrayList<>(3);

		for (LetterType item : LetterType.values()) {
			letterTypes.add(new ValueLabel(item.name(), item.getDescription()));
		}

		return letterTypes;
	}

	public static List<ValueLabel> getLetterModes() {
		if (letterModes != null) {
			return letterModes;
		}

		letterModes = new ArrayList<>(2);

		for (LetterMode item : LetterMode.values()) {
			letterModes.add(new ValueLabel(item.name(), item.getDescription()));
		}

		return letterModes;
	}
}
