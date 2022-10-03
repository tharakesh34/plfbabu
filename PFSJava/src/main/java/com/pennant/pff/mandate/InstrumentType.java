package com.pennant.pff.mandate;

import java.util.Arrays;
import java.util.List;

public enum InstrumentType {

	NACH("NACH", "National Automated Clearing House"),

	ECS("ECS", "Electronic Clearing Service"),

	ENACH("E-NACH", "E-NACH"),

	EMANDATE("E-MANDATE", "E-Mandate"),

	PDC("PDC", "Post Dated Cheque"),

	SPDC("SPDC", "Security Post Dated Cheque"),

	SI("SI", "Standing Instruction"),

	SII("SII", "Standing Instruction Internal"),

	DAS("DAS", "Deduct Against Salary"),

	MANUAL("MANUAL", "Manual Payment"),

	DD("DD", "Direct Debit"),

	UDC("UDC", "Undated Cheque"),

	CHEQUE("CHEQUE", "Cheque"),

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

	public static boolean isNACH(String instrumentType) {
		return isEqual(NACH, getType(instrumentType));
	}

	public static boolean isECS(String instrumentType) {
		return isEqual(ECS, getType(instrumentType));
	}

	public static boolean isENACH(String instrumentType) {
		return isEqual(ENACH, getType(instrumentType));
	}

	public static boolean isEMandate(String instrumentType) {
		return isEqual(EMANDATE, getType(instrumentType));
	}

	public static boolean isPDC(String instrumentType) {
		return isEqual(PDC, getType(instrumentType));
	}

	public static boolean isSPDC(String instrumentType) {
		return isEqual(SPDC, getType(instrumentType));
	}

	public static boolean isSI(String instrumentType) {
		return isEqual(SI, getType(instrumentType));
	}

	public static boolean isSII(String instrumentType) {
		return isEqual(SII, getType(instrumentType));
	}

	public static boolean isDAS(String instrumentType) {
		return isEqual(DAS, getType(instrumentType));
	}

	public static boolean isManual(String instrumentType) {
		return isEqual(MANUAL, getType(instrumentType));
	}

	public static boolean isDD(String instrumentType) {
		return isEqual(DD, getType(instrumentType));
	}

	public static boolean isUDC(String instrumentType) {
		return isEqual(UDC, getType(instrumentType));
	}

	public static boolean isCheque(String instrumentType) {
		return isEqual(CHEQUE, getType(instrumentType));
	}

	public static boolean isDda(String instrumentType) {
		return isEqual(DDA, getType(instrumentType));
	}

	private static boolean isEqual(InstrumentType instrumentType, InstrumentType type) {
		return type == null ? false : type == instrumentType;
	}

	public static InstrumentType getType(String instrumentType) {
		List<InstrumentType> list = Arrays.asList(InstrumentType.values());

		for (InstrumentType it : list) {
			if (it.name().equals(instrumentType)) {
				return it;
			}
		}

		return null;
	}

	public static boolean mandateRequired(String instrumentType) {
		InstrumentType object = getType(instrumentType);

		if (object == null || object == MANUAL) {
			return false;
		}

		switch (object) {
		case NACH:
		case ECS:
		case EMANDATE:
		case SI:
		case DAS:
			return true;

		default:
			return false;
		}
	}
}
