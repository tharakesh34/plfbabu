package com.pennanttech.pff.core.util;

import java.math.BigDecimal;
import java.util.Map;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.util.RuleConstants;

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

	public static Map<String, BigDecimal> getFeeRuleMap(ReceiptAllocationDetail rad) {
		FinFeeDetail fd = new FinFeeDetail();

		fd.setFeeTypeCode(rad.getFeeTypeCode());

		fd.setPaidAmount(rad.getPaidAmount());
		fd.setPaidTDS(rad.getTdsPaid());

		Taxes cgstTax = new Taxes();
		cgstTax.setTaxType(RuleConstants.CODE_CGST);
		cgstTax.setPaidTax(rad.getPaidCGST());

		Taxes sgstTax = new Taxes();
		sgstTax.setTaxType(RuleConstants.CODE_SGST);
		sgstTax.setPaidTax(rad.getPaidSGST());

		Taxes igstTax = new Taxes();
		igstTax.setTaxType(RuleConstants.CODE_IGST);
		igstTax.setPaidTax(rad.getPaidIGST());

		Taxes ugstTax = new Taxes();
		ugstTax.setTaxType(RuleConstants.CODE_UGST);
		ugstTax.setPaidTax(rad.getPaidUGST());

		Taxes cessTax = new Taxes();
		cessTax.setTaxType(RuleConstants.CODE_CESS);
		cessTax.setPaidTax(rad.getPaidCESS());

		TaxHeader taxHeader = fd.getTaxHeader();

		taxHeader.getTaxDetails().add(cgstTax);
		taxHeader.getTaxDetails().add(sgstTax);
		taxHeader.getTaxDetails().add(igstTax);
		taxHeader.getTaxDetails().add(ugstTax);
		taxHeader.getTaxDetails().add(cessTax);

		return FeesUtil.getFeeRuleMap(fd);
	}
}
