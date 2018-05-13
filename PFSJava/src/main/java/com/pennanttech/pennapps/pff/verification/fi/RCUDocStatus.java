package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum RCUDocStatus {

	SELECT(0, Labels.getLabel("Combo.Select")), INITIATED(1, "Initiated"), REFERRED(2, "Referred"), HOLD(3,
			"Hold"), COMPLETED(4, "Completed");

	private final Integer key;
	private final String value;

	private RCUDocStatus(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (RCUDocStatus status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}
	
}
