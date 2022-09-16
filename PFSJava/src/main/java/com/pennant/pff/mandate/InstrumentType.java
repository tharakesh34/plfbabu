package com.pennant.pff.mandate;

import java.util.Arrays;
import java.util.List;

public enum InstrumentType {

	NACH("NACH", "National Automated Clearing House"),

	ECS("ECS", "Electronic Clearing Service"),

	ENACH("E-NACH", "E-NACH"),

	EMANDATE("E-MANDATE", "E-Mandate"),

	PDC("PDC", "Post Dated Cheque"),

	SI("SI", "SI"),

	SII("SII", "SI-Internal"),

	DAS("DAS", "DAS"),

	MANUAL("MANUAL", "Manual Payment"),

	DD("DD", "DD"),

	UDC("UDC", "UDC"),

	CHEQUE("CHEQUE", "CHEQUE"),

	DDA("DDA", "DDA");

	private String code;
	private String description;

	private InstrumentType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String code() {
		return code;
	}

	public String description() {
		return description;
	}

	public static boolean isECS(String instrumentType) {
		return isEqual(ECS, object(instrumentType));
	}

	public static boolean isDD(String instrumentType) {
		return isEqual(DD, object(instrumentType));
	}

	public static boolean isNACH(String instrumentType) {
		return isEqual(NACH, object(instrumentType));
	}

	public static boolean isPDC(String instrumentType) {
		return isEqual(PDC, object(instrumentType));
	}

	public static boolean isUDC(String instrumentType) {
		return isEqual(UDC, object(instrumentType));
	}

	public static boolean isEMandate(String instrumentType) {
		return isEqual(EMANDATE, object(instrumentType));
	}

	public static boolean isDAS(String instrumentType) {
		return isEqual(DAS, object(instrumentType));
	}

	public static boolean isSI(String instrumentType) {
		return isEqual(SI, object(instrumentType));
	}

	public static boolean isValid(String instrumentType) {
		return isEqual(MANUAL, object(instrumentType));
	}

	private static boolean isEqual(InstrumentType instrumentType, InstrumentType type) {
		return type == null ? false : type == instrumentType;
	}

	public static InstrumentType object(String instrumentType) {
		List<InstrumentType> list = Arrays.asList(InstrumentType.values());

		for (InstrumentType it : list) {
			if (it.code().equals(instrumentType)) {
				return it;
			}
		}

		return null;
	}
}
