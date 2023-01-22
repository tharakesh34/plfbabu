package com.pennanttech.pff.receipt.constants;

public class Allocation {

	public static final String PRI = "PRI";
	public static final String PFT = "PFT";
	public static final String NPFT = "NPFT";
	public static final String TDS = "TDS";
	public static final String LPFT = "LPFT";
	public static final String ODC = "ODC";
	public static final String FEE = "FEE";
	public static final String INS = "INS";
	public static final String MANADV = "MANADV";
	public static final String BOUNCE = "BOUNCE";
	public static final String PP = "PP";
	public static final String EMI = "EMI";
	public static final String FUT_PRI = "FUTPRI";
	public static final String FUT_PFT = "FUTPFT";
	public static final String FUT_NPFT = "FUTNPFT";
	public static final String FUT_TDS = "FUTTDS";
	public static final String KOEMI = "KOFFEMI";
	public static final String KOLPI = "KOFFLPI";
	public static final String ADHOC = "ADHOC";

	private Allocation() {
		super();
	}

	public static String getCode(String code) {
		switch (code) {
		case Allocation.PFT:
			return "I";
		case Allocation.PRI:
			return "P";
		case Allocation.LPFT:
			return "L";
		case Allocation.FEE:
			return "F";
		case Allocation.ODC:
			return "O";
		case Allocation.FUT_PFT:
			return "FI";
		case Allocation.FUT_PRI:
			return "FP";
		case Allocation.EMI:
			return "EM";
		case Allocation.MANADV:
			return "M";
		case Allocation.BOUNCE:
			return "B";
		default:
			return code;
		}
	}
}
