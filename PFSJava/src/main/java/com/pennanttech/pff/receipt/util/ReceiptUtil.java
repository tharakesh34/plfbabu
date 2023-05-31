package com.pennanttech.pff.receipt.util;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
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

	public static boolean isTransactionRefMandatory(FinServiceInstruction fsi, String productCategory) {
		String receiptMode = fsi.getPaymentMode();

		if (isAutoReceipt(receiptMode, productCategory)) {
			return false;
		}

		if (isTerminationEvent(fsi)) {
			return false;
		}

		if (ReceiptMode.isOfflineMode(receiptMode)) {
			return false;
		}

		if (fsi.isKnockOffReceipt()) {
			return false;
		}

		if (fsi.isLoanCancellation()) {
			return false;
		}

		return true;
	}

	public static boolean isTerminationEvent(FinServiceInstruction fsi) {
		return RequestSource.EOD.equals(fsi.getRequestSource())
				&& (ReceiptPurpose.EARLYSETTLE.equals(fsi.getReceiptPurpose())
						|| ReceiptPurpose.SCHDRPY.equals(fsi.getReceiptPurpose()));
	}

	public static BigDecimal getAllocatedAmount(List<ReceiptAllocationDetail> allocations) {
		BigDecimal paidAmount = allocations.stream().map(ReceiptAllocationDetail::getPaidAmount).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal waivedAmount = allocations.stream().map(ReceiptAllocationDetail::getWaivedAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal paidGST = allocations.stream().map(ReceiptAllocationDetail::getPaidGST).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal waivedGST = allocations.stream().map(ReceiptAllocationDetail::getWaivedGST).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal tdsPaid = allocations.stream().map(ReceiptAllocationDetail::getTdsPaid).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal tdsWaived = allocations.stream().map(ReceiptAllocationDetail::getTdsWaived).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		return paidAmount.add(waivedAmount).add(paidGST).add(waivedGST).subtract(tdsPaid.add(tdsWaived));
	}
}
