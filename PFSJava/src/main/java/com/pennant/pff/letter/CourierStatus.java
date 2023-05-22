package com.pennant.pff.letter;

import java.util.Arrays;
import java.util.List;

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

}
