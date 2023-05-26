package com.pennant.pff.receipt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum ClosureType {

	CLOSURE("Closure"),

	FORE_CLOSURE("Fore-Closure"),

	RE_POSSESSION("Repossession"),

	TOP_UP("Top Up"),

	CANCEL("Cancel");

	private String code;

	private ClosureType(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static boolean isValid(String closureType) {
		if (getType(closureType) == null) {
			return false;
		}

		return true;
	}

	public static boolean isCancel(String closureType) {
		return isEqual(CANCEL, getType(closureType));
	}

	public static boolean isClosure(String closureType) {
		return isEqual(CLOSURE, getType(closureType));
	}

	public static boolean isForeClosure(String closureType) {
		return isEqual(FORE_CLOSURE, getType(closureType));
	}

	private static boolean isEqual(ClosureType closureType, ClosureType type) {
		return type == null ? false : type == closureType;
	}

	public static ClosureType getType(String closureType) {
		List<ClosureType> list = Arrays.asList(ClosureType.values());

		for (ClosureType it : list) {
			if (it.code().equals(closureType)) {
				return it;
			}
		}

		return null;
	}

	private static List<ValueLabel> closureTypes;

	public static List<ValueLabel> getTypes() {
		if (closureTypes == null) {
			closureTypes = new ArrayList<>(6);

			closureTypes.add(new ValueLabel(CLOSURE.code(), Labels.getLabel("label_ClosureType_Closure")));
			closureTypes.add(new ValueLabel(FORE_CLOSURE.code(), Labels.getLabel("label_ClosureType_ForeClosure")));
			closureTypes.add(new ValueLabel(RE_POSSESSION.code(), Labels.getLabel("label_ClosureType_Repossession")));
			closureTypes.add(new ValueLabel(TOP_UP.code(), Labels.getLabel("label_ClosureType_TopUp")));
			closureTypes.add(new ValueLabel(CANCEL.code(), Labels.getLabel("label_ClosureType_Cancel")));
		}

		return closureTypes;
	}

	public static String allowedTypes(ClosureType closureType) {
		StringBuilder values = new StringBuilder();

		if (closureType == null) {
			values.append(ClosureType.CLOSURE.code()).append(", ");
			values.append(ClosureType.FORE_CLOSURE.code()).append(", ");
			values.append(ClosureType.TOP_UP.code()).append(", ");
			values.append(ClosureType.RE_POSSESSION.code()).append(", ");
			values.append(ClosureType.CANCEL.code());
			return values.toString();
		}

		switch (closureType) {
		case CLOSURE:
			values.append(ClosureType.FORE_CLOSURE.code()).append(", ");
			values.append(ClosureType.TOP_UP.code()).append(", ");
			values.append(ClosureType.RE_POSSESSION.code()).append(", ");
			values.append(ClosureType.CANCEL.code());
			return values.toString();
		case CANCEL, FORE_CLOSURE:
			values.append(ClosureType.CLOSURE.code()).append(", ");
			values.append(ClosureType.TOP_UP.code()).append(", ");
			values.append(ClosureType.RE_POSSESSION.code());
			return values.toString();
		default:
			values.append(ClosureType.CLOSURE.code()).append(", ");
			values.append(ClosureType.FORE_CLOSURE.code()).append(", ");
			values.append(ClosureType.TOP_UP.code()).append(", ");
			values.append(ClosureType.RE_POSSESSION.code()).append(", ");
			values.append(ClosureType.CANCEL.code());
			return values.toString();
		}
	}
}
