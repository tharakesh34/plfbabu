package com.pennant.pff.mandate;

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

	DDM("DAS", "DAS"),

	UDC("DAS", "DAS");

	private String code;
	private String description;

	private InstrumentType(String code, String description) {
		this.code = code;
		this.description = code;
	}

	public String code() {
		return code;
	}

	public String description() {
		return description;
	}

	public static boolean isECS(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.ECS;
	}

	public static boolean isDD(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.DDM;
	}

	public static boolean isNACH(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.NACH;
	}

	public static boolean isPDC(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.PDC;
	}

	public static boolean isEMandate(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.EMANDATE;
	}

	public static boolean isDAS(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.DAS;
	}

	public static boolean isSI(String instrumentType) {
		InstrumentType st = InstrumentType.valueOf(instrumentType);

		return st == null ? false : st == InstrumentType.SI;
	}

	public static boolean isValid(String instrumentType) {
		InstrumentType item = InstrumentType.valueOf(instrumentType);

		return item != null && item != MANUAL;
	}

}
