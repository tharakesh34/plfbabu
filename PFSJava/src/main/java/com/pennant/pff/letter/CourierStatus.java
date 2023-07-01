package com.pennant.pff.letter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		if (courier == null) {
			return null;
		}

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

			courierStatus.add(new ValueLabel(D.getCode(), D.getCode()));
			courierStatus.add(new ValueLabel(T.getCode(), T.getCode()));
			courierStatus.add(new ValueLabel(R.getCode(), R.getCode()));
			courierStatus.add(new ValueLabel(L.getCode(), L.getCode()));
		}

		return courierStatus;
	}

}
