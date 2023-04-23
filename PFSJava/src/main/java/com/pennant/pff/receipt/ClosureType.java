package com.pennant.pff.receipt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum ClosureType {

	CLOSURE("CLOSURE"),

	FORE_CLOSURE("FORE-CLOSURE"),

	RE_POSSESSION("REPOSSESSION"),

	TOP_UP("TOPUP"),

	CANCEL("CANCEL"),

	SETTLEMENT("SETTLEMENT");

	private String code;

	private ClosureType(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static boolean isCancel(String closureType) {
		return isEqual(CANCEL, getType(closureType));
	}

	private static boolean isEqual(ClosureType closureType, ClosureType type) {
		return type == null ? false : type == closureType;
	}

	public static ClosureType getType(String closureType) {
		List<ClosureType> list = Arrays.asList(ClosureType.values());

		for (ClosureType it : list) {
			if (it.name().equals(closureType)) {
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
			closureTypes.add(new ValueLabel(SETTLEMENT.code(), Labels.getLabel("label_ClosureType_Settlement")));
		}

		return closureTypes;
	}
}
