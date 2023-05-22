package com.pennant.pff.letter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum CourierStatus {

	D("Delivered"),

	T("In Transit"),

	R("Returned"),

	L("Lost");

	String code;

	private CourierStatus(String code) {
		this.code = code;

	}

	public String getCode() {
		return code;
	}

	public static CourierStatus getCourier(String courier) {
		List<CourierStatus> list = Arrays.asList(CourierStatus.values());

		for (CourierStatus it : list) {
			if (it.name().equals(courier)) {
				return it;
			}
		}

		return null;
	}

	private static List<ValueLabel> courierStatus;

	public static List<ValueLabel> getTypes() {
		if (courierStatus == null) {
			courierStatus = new ArrayList<>(4);

			courierStatus.add(new ValueLabel(D.getCode(), Labels.getLabel("label_CourierStatus_Delivered")));
			courierStatus.add(new ValueLabel(T.getCode(), Labels.getLabel("label_CourierStatus_InTransit")));
			courierStatus.add(new ValueLabel(L.getCode(), Labels.getLabel("label_CourierStatus_Returned")));
			courierStatus.add(new ValueLabel(R.getCode(), Labels.getLabel("label_CourierStatus_Lost")));
		}

		return courierStatus;
	}

}
