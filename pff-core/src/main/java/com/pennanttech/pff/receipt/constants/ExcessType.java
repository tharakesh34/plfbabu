package com.pennanttech.pff.receipt.constants;

public class ExcessType {

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private ExcessType() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static final String EXCESS = "E";
	public static final String EMIINADV = "A";
	public static final String PAYABLE = "P";
	public static final String ADVINT = "ADVINT";
	public static final String ADVEMI = "ADVEMI";
	public static final String CASHCLT = "CASHCLT";
	public static final String DSF = "DSF";
	public static final String TEXCESS = "T";
	public static final String SETTLEMENT = "S";

	public static boolean isWriteOffReceiptAllowed(String excessType) {
		return EXCESS.equals(excessType) || EMIINADV.equals(excessType) || DSF.equals(excessType)
				|| CASHCLT.equals(excessType);
	}

	public static String getReceiptMode(String excessType) {
		String receiptMode = null;

		if (excessType == null) {
			return receiptMode;
		}

		switch (excessType) {
		case ExcessType.EXCESS:
			receiptMode = ReceiptMode.EXCESS;
			break;
		case ExcessType.EMIINADV:
			receiptMode = ReceiptMode.EMIINADV;
			break;
		case ExcessType.PAYABLE:
			receiptMode = ReceiptMode.PAYABLE;
			break;
		case ExcessType.ADVINT:
			receiptMode = ReceiptMode.ADVINT;
			break;
		case ExcessType.ADVEMI:
			receiptMode = ReceiptMode.ADVEMI;
			break;
		case ExcessType.CASHCLT:
			receiptMode = ReceiptMode.CASHCLT;
			break;
		case ExcessType.DSF:
			receiptMode = ReceiptMode.DSF;
			break;
		case ExcessType.TEXCESS:
			receiptMode = ReceiptMode.TEXCESS;
			break;
		case ExcessType.SETTLEMENT:
			receiptMode = ReceiptMode.SETTLEMENT;
			break;
		default:
			receiptMode = null;
		}

		return receiptMode;
	}

	public static boolean isTransferAllowed(String excessType) {
		return EXCESS.equals(excessType) || EMIINADV.equals(excessType) || TEXCESS.equals(excessType)
				|| SETTLEMENT.equals(excessType);
	}

}
