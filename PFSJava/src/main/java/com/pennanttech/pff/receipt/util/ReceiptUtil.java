package com.pennanttech.pff.receipt.util;

import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class ReceiptUtil {
	private ReceiptUtil() {
		super();
	}

	public static boolean isAutoReceipt(String receiptMode, String productCategory) {
		if (ReceiptMode.RESTRUCT.equals(receiptMode)) {
			return true;
		}

		return FinanceConstants.PRODUCT_CD.equals(productCategory) && (ReceiptMode.PAYABLE.equals(receiptMode));
	}

	public static String getAccountingEvent(int methodCtg) {
		switch (methodCtg) {
		case 0:
			return AccountingEvent.REPAY;
		case 1:
			return AccountingEvent.EARLYPAY;
		case 2:
			return AccountingEvent.EARLYSTL;
		case 4:
			return AccountingEvent.RESTRUCTURE;
		default:
			return "";
		}
	}

	public static int getReceiptPurpose(String receiptPurpose) {
		int receiptPurposeCtg = -1;

		if (receiptPurpose == null) {
			return receiptPurposeCtg;
		}

		switch (receiptPurpose) {
		case FinServiceEvent.SCHDRPY:
			receiptPurposeCtg = ReceiptPurpose.SCHDRPY.index();
			break;
		case FinServiceEvent.EARLYRPY:
			receiptPurposeCtg = ReceiptPurpose.EARLYRPY.index();
			break;
		case FinServiceEvent.EARLYSETTLE:
			receiptPurposeCtg = ReceiptPurpose.EARLYSETTLE.index();
			break;
		case FinServiceEvent.EARLYSTLENQ:
			receiptPurposeCtg = ReceiptPurpose.EARLYSTLENQ.index();
			break;
		case FinServiceEvent.RESTRUCTURE:
			receiptPurposeCtg = ReceiptPurpose.RESTRUCTURE.index();
			break;
		default:
			receiptPurposeCtg = ReceiptPurpose.NON.index();
			break;
		}

		return receiptPurposeCtg;
	}

}
