package com.pennanttech.pennapps.pff.document;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum DocumentCategories {

	CUSTOMER("CUSTOMER", Labels.getLabel("label_Customer_Documents")),
	FINANCE("FINANCE",Labels.getLabel("label_Finance_Documents")),
	COLLATERAL("COLLATERAL", Labels.getLabel("label_Collateral_Documents")),
	VERIFICATION_TV("VERIFICATION_TV", Labels.getLabel("label_Technical_Verification")),
	VERIFICATION_RCU("VERIFICATION_RCU", Labels.getLabel("label_RCU_Verification")),
	VERIFICATION_FI("VERIFICATION_FI", Labels.getLabel("label_FieldInvestigation_Documents")),
	VERIFICATION_LV("VERIFICATION_LV", Labels.getLabel("label_LegalVerification_Documents"));

	private final String key;
	private final String value;

	private DocumentCategories(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static DocumentCategories getType(String key) {
		for (DocumentCategories type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (DocumentCategories type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}

		return list;
	}

}
