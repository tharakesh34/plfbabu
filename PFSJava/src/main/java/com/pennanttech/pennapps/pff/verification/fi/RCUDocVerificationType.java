package com.pennanttech.pennapps.pff.verification.fi;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum RCUDocVerificationType {

	SELECT(0, Labels.getLabel("Combo.Select")), SAMPLED(1, "Sampled"), EYEBALLED(2, "Eyeballed"), SCREENED(3,
			"Screened");

	private final Integer key;
	private final String value;

	private RCUDocVerificationType(Integer key, String value) {
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
		for (RCUDocVerificationType status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}
	
}
