package com.pennanttech.pff.receipt.constants;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.util.DisbursementConstants;

public class ReceiptMode {

	private static Set<String> receiptModes = new HashSet<>();
	private static Set<String> subReceiptModes = new HashSet<>();

	public static final String CASH = "CASH";
	public static final String CHEQUE = "CHEQUE";
	public static final String DD = "DD";
	public static final String NEFT = "NEFT";
	public static final String RTGS = "RTGS";
	public static final String IMPS = "IMPS";
	public static final String PAYTM = "PAYTM";
	public static final String EXPERIA = "EXPERIA";
	public static final String PORTAL = "PORTAL";
	public static final String PAYU = "PAYU";
	public static final String ESCROW = "ESCROW";
	public static final String REPLEDGE = "REPLEDGE";
	public static final String ONLINE = "ONLINE";
	public static final String BILLDESK = "BILLDESK";
	public static final String MOBILE = "MOBILE";
	public static final String RESTRUCT = "RESTRUCT";
	public static final String DIGITAL = "DIGITAL";
	public static final String BANKDEPOSIT = "BANKDEPT";

	public static final String BBPS = "BBPS";
	public static final String IFT = "IFT";
	public static final String RTRNGDS = "RTRNGDS";
	public static final String NACH = "NACH";
	public static final String PAYMENTGATEWAY = "PAYMENTGATEWAY";
	public static final String UPI = "UPI";
	public static final String ZERORECEIPT = "FULLWAIVER";

	public static final String EXCESS = "EXCESS";
	public static final String EMIINADV = "EMIINADV";
	public static final String ADVINT = "ADVINT";
	public static final String ADVEMI = "ADVEMI";
	public static final String PAYABLE = "PAYABLE";
	public static final String PRESENTMENT = "PRESENT";
	public static final String CASHCLT = "CASHCLT";
	public static final String DSF = "DSF";
	public static final String TEXCESS = "T";
	public static final String SETTLEMENT = "S";

	private ReceiptMode() {
		super();
	}

	public static String getReceiptMode(String receiptMode) {
		switch (receiptMode) {
		case EXPERIA, IMPS, NEFT, RTGS, BILLDESK, PAYU, PAYTM, PORTAL, ESCROW, DIGITAL, RTRNGDS:
			return ONLINE;
		case RESTRUCT:
			return RESTRUCT;
		case MOBILE:
			return MOBILE;
		default:
			return receiptMode;
		}
	}

	public static String getSubReceiptMode(String receiptMode) {
		return ONLINE.equals(getReceiptMode(receiptMode)) ? receiptMode : "";
	}

	public static String getReceiptChannel(String receiptMode) {
		switch (receiptMode) {
		case PORTAL, BILLDESK:
			return DisbursementConstants.RECEIPT_CHANNEL_POR;
		case CASH, CHEQUE, DD:
			return DisbursementConstants.PAYMENT_TYPE_OTC;
		default:
			return "";
		}

	}

	public static boolean isValidReceiptMode(String receiptMode) {
		if (isOfflineMode(receiptMode)) {
			return true;
		}

		switch (receiptMode) {
		case ONLINE, MOBILE:
			return true;
		default:
			return false;
		}
	}

	public static boolean isOfflineMode(String receiptMode) {
		switch (receiptMode) {
		case CHEQUE, DD, CASH:
			return true;
		default:
			return false;
		}
	}

	public static boolean isValidReceiptChannel(String receiptMode, String channel) {
		if (isOfflineMode(receiptMode)) {
			switch (channel) {
			case DisbursementConstants.PAYMENT_TYPE_OTC, DisbursementConstants.PAYMENT_TYPE_MOB:
				return true;
			default:
				return false;
			}
		}

		return true;
	}

	public static boolean isValidSubReceiptMode(String receiptMode) {
		return getSubReceiptModes().contains(receiptMode);
	}

	public static String getValidReceiptModes() {
		return getReceiptModes().toString();
	}

	public static String getValidSubReceiptModes() {
		return getSubReceiptModes().toString();
	}

	public static boolean isFundingAccountReq(String receiptMode) {
		switch (receiptMode) {
		case CHEQUE, DD, ONLINE, RESTRUCT:
			return true;
		default:
			return false;
		}
	}

	private static Set<String> getReceiptModes() {
		if (receiptModes.isEmpty()) {
			receiptModes.add(CASH);
			receiptModes.add(CHEQUE);
			receiptModes.add(DD);
			receiptModes.add(ONLINE);
			receiptModes.add(MOBILE);
		}

		return receiptModes;

	}

	private static Set<String> getSubReceiptModes() {
		if (subReceiptModes.isEmpty()) {
			subReceiptModes.add(IMPS);
			subReceiptModes.add(NEFT);
			subReceiptModes.add(RTGS);
			subReceiptModes.add(EXPERIA);
			subReceiptModes.add(BILLDESK);
			subReceiptModes.add(ESCROW);
			subReceiptModes.add(PAYU);
			subReceiptModes.add(PAYTM);
			subReceiptModes.add(PORTAL);
			subReceiptModes.add(DIGITAL);
			subReceiptModes.add(RTRNGDS);
			subReceiptModes.add(NACH);
			subReceiptModes.add(PAYMENTGATEWAY);
			subReceiptModes.add(UPI);
			subReceiptModes.add(BBPS);
			subReceiptModes.add(IFT);
		}

		return subReceiptModes;
	}

	public static boolean isReceiptFromBank(String paymentType) {
		return StringUtils.isNotEmpty(StringUtils.trimToEmpty(paymentType)) && !"#".equals(paymentType)
				&& !ReceiptMode.EMIINADV.equals(paymentType) && !ReceiptMode.EXCESS.equals(paymentType)
				&& !ReceiptMode.TEXCESS.equals(paymentType) && !ReceiptMode.PAYABLE.equals(paymentType)
				&& !ReceiptMode.ADVINT.equals(paymentType) && !ReceiptMode.ADVEMI.equals(paymentType)
				&& !ReceiptMode.CASHCLT.equals(paymentType) && !ReceiptMode.DSF.equals(paymentType)
				&& !ReceiptMode.SETTLEMENT.equals(paymentType);

	}

}
