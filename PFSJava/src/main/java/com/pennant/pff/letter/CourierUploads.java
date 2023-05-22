package com.pennant.pff.letter;

import java.util.Arrays;
import java.util.List;

public enum CourierUploads {

	D("Delivered"),

	T("In Transit"),

	R("Returned"),

	L("Lost");

	String code;

	private CourierUploads(String code) {
		this.code = code;

	}

	public String getCode() {
		return code;
	}

	public static CourierUploads getCourier(String courier) {
		List<CourierUploads> list = Arrays.asList(CourierUploads.values());

		for (CourierUploads it : list) {
			if (it.name().equals(courier)) {
				return it;
			}
		}

		return null;
	}

}
