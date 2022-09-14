package com.pennant.pff.accounting;

import java.util.Arrays;
import java.util.List;

public enum HostAccountStatus {
	CLOSE("C"),

	OPEN("O");

	private String code;

	private HostAccountStatus(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public static boolean isClose(String code) {
		return isEqual(CLOSE, object(code));
	}

	public static boolean isOpen(String code) {
		return isEqual(OPEN, object(code));
	}

	private static boolean isEqual(HostAccountStatus hostAccountStatus, HostAccountStatus status) {
		return status == null ? false : status == hostAccountStatus;
	}

	public static HostAccountStatus object(String code) {
		List<HostAccountStatus> list = Arrays.asList(HostAccountStatus.values());

		for (HostAccountStatus status : list) {
			if (status.code.equals(code)) {
				return status;
			}
		}

		return null;
	}
}
