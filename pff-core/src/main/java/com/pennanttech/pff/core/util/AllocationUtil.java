package com.pennanttech.pff.core.util;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;

public class AllocationUtil {

	private AllocationUtil() {
		super();
	}

	public static BigDecimal getPaidByCustomer(FinReceiptHeader rch) {
		ReceiptAllocationDetail pd = rch.getTotalPastDues();
		ReceiptAllocationDetail adv = rch.getTotalRcvAdvises();
		ReceiptAllocationDetail fee = rch.getTotalFees();

		BigDecimal paidAmt = pd.getTotalDue().add(adv.getTotalDue()).add(fee.getTotalDue());

		BigDecimal waivedAmt = pd.getWaivedAmount().add(adv.getWaivedAmount()).add(fee.getWaivedAmount());

		return paidAmt.subtract(waivedAmt);
	}
}
