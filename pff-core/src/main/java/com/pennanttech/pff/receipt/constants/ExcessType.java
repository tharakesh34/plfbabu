package com.pennanttech.pff.receipt.constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;
import com.pennant.pff.extension.ExcessExtension;

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

	public static final String PRESENTMENT = "PRESENT";
	public static final String BOUNCE = "B";
	public static final String RECADJ = "R";

	private static List<ValueLabel> transferList = null;
	private static List<ValueLabel> adjustToList = null;
	private static List<ValueLabel> knockOffFromList = null;
	private static List<String> allowedTypeList = null;
	private static List<String> excessList = null;

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
		return getTransferList().stream().anyMatch(excess -> excess.getValue().equals(excessType));
	}

	public static List<ValueLabel> getTransferList() {
		if (transferList == null) {
			transferList = new ArrayList<>(4);
			transferList.add(new ValueLabel(EXCESS, Labels.getLabel("label_ExcessAdjustTo_ExcessAmount")));
			transferList.add(new ValueLabel(EMIINADV, Labels.getLabel("label_ExcessAdjustTo_EMIInAdvance")));
			transferList.add(new ValueLabel(TEXCESS, Labels.getLabel("label_RecceiptDialog_ExcessType_TEXCESS")));
			transferList.add(new ValueLabel(SETTLEMENT, Labels.getLabel("label_ExcessAdjustTo_Settlement")));
		}
		return transferList;
	}

	public static List<String> getAllowedTypes() {
		if (allowedTypeList == null) {
			allowedTypeList = new ArrayList<>(1);
			allowedTypeList.add(ExcessType.EXCESS);
		}
		return allowedTypeList;
	}

	public static Set<String> defaultAdjustToList() {
		Set<String> set = new HashSet<>();

		set.add(ADVEMI);
		set.add(EMIINADV);
		set.add(SETTLEMENT);
		set.add(CASHCLT);
		set.add(DSF);
		set.add(TEXCESS);

		return set;
	}

	public static Set<String> defaultKnockOffFromList() {
		Set<String> set = new HashSet<>();

		set.add(EXCESS);
		set.add(EMIINADV);
		set.add(PAYABLE);
		set.add(CASHCLT);
		set.add(DSF);
		set.add(PRESENTMENT);

		return set;
	}

	public static boolean isAllowedForAdjustment(String excessType) {
		return ExcessExtension.ALLOWED_ADJUSTMENTS.contains(excessType);
	}

	public static boolean isAllowedKnockOffFrom(String excessType) {
		return ExcessExtension.ALLOWED_KNOCKOFF_FROM.contains(excessType);
	}

	public static List<ValueLabel> getAdjustmentList() {

		if (adjustToList != null) {
			return adjustToList;
		}

		adjustToList = new ArrayList<>();

		if (isAllowedForAdjustment(EXCESS)) {
			adjustToList.add(new ValueLabel(EXCESS, Labels.getLabel("label_ExcessAdjustTo_ExcessAmount")));
		}

		if (isAllowedForAdjustment(EMIINADV)) {
			adjustToList.add(new ValueLabel(EMIINADV, Labels.getLabel("label_ExcessAdjustTo_EMIInAdvance")));
		}

		if (isAllowedForAdjustment(SETTLEMENT)) {
			adjustToList.add(new ValueLabel(SETTLEMENT, Labels.getLabel("label_ExcessAdjustTo_Settlement")));
		}

		if (isAllowedForAdjustment(CASHCLT)) {
			adjustToList.add(new ValueLabel(CASHCLT, Labels.getLabel("label_RecceiptDialog_ExcessType_CASHCLT")));
		}

		if (isAllowedForAdjustment(DSF)) {
			adjustToList.add(new ValueLabel(DSF, Labels.getLabel("label_RecceiptDialog_ExcessType_DSF")));
		}

		if (isAllowedForAdjustment(TEXCESS)) {
			adjustToList.add(new ValueLabel(TEXCESS, Labels.getLabel("label_RecceiptDialog_ExcessType_TEXCESS")));
		}

		return adjustToList;
	}

	public static List<String> getExcessList() {
		if (excessList == null) {
			excessList = new ArrayList<>(3);
			excessList.add(ReceiptMode.EXCESS);
			excessList.add(ReceiptMode.EMIINADV);
			excessList.add(ReceiptMode.PAYABLE);

		}
		return excessList;
	}

	public static List<ValueLabel> getKnockOffFromList() {

		if (knockOffFromList != null) {
			return knockOffFromList;
		}

		knockOffFromList = new ArrayList<>(3);

		if (isAllowedKnockOffFrom(EXCESS)) {
			knockOffFromList.add(new ValueLabel(EXCESS, Labels.getLabel("label_Excess")));
		}

		if (isAllowedKnockOffFrom(EMIINADV)) {
			knockOffFromList.add(new ValueLabel(EMIINADV, Labels.getLabel("label_EMI_Advance")));
		}

		if (isAllowedKnockOffFrom(PAYABLE)) {
			knockOffFromList.add(new ValueLabel(PAYABLE, Labels.getLabel("label_Payable_Advice")));
		}

		if (isAllowedKnockOffFrom(CASHCLT)) {
			knockOffFromList.add(new ValueLabel(CASHCLT, Labels.getLabel("label_CASHCLT")));
		}

		if (isAllowedKnockOffFrom(DSF)) {
			knockOffFromList.add(new ValueLabel(DSF, Labels.getLabel("label_DSF")));
		}

		if (isAllowedKnockOffFrom(PRESENTMENT)) {
			knockOffFromList.add(new ValueLabel(PRESENTMENT, Labels.getLabel("label_PRESENTMENT")));
		}

		return knockOffFromList;
	}

}
