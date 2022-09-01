package com.pennant.pff.mandate;

public enum InstrumentType {

	MANUAL,

	CASA,

	ECS,

	DDM,

	NACH,

	PDC,

	UDC,

	EMANDATE,

	DAS,

	SI;

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

	public static boolean isEMNDT(String instrumentType) {
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
