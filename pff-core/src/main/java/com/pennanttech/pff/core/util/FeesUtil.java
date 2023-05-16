package com.pennanttech.pff.core.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RuleConstants;

public class FeesUtil {

	private FeesUtil() {
		super();
	}

	public static Map<String, BigDecimal> getFeeRuleMap(FinFeeDetail fd) {
		Map<String, BigDecimal> dataMap = new HashMap<>();

		Taxes cgstTax = new Taxes();
		Taxes sgstTax = new Taxes();
		Taxes igstTax = new Taxes();
		Taxes ugstTax = new Taxes();
		Taxes cessTax = new Taxes();

		TaxHeader taxHeader = fd.getTaxHeader();
		if (taxHeader == null) {
			taxHeader = new TaxHeader();
		}

		for (Taxes tax : taxHeader.getTaxDetails()) {
			String taxType = tax.getTaxType();

			switch (taxType) {
			case RuleConstants.CODE_CGST:
				cgstTax = tax;
				break;
			case RuleConstants.CODE_SGST:
				sgstTax = tax;
				break;
			case RuleConstants.CODE_IGST:
				igstTax = tax;
				break;
			case RuleConstants.CODE_UGST:
				ugstTax = tax;
				break;
			case RuleConstants.CODE_CESS:
				cessTax = tax;
				break;
			default:
				break;
			}

		}

		String feeTypeCode = fd.getFeeTypeCode();

		setValue(dataMap, feeTypeCode, "_C", fd.getActualAmount());
		setValue(dataMap, feeTypeCode, "_P", fd.getPaidAmount());
		setValue(dataMap, feeTypeCode, "_TDS_P", fd.getPaidTDS());
		setValue(dataMap, feeTypeCode, "_N", fd.getNetAmount());
		setValue(dataMap, feeTypeCode, "_TDS_N", fd.getNetTDS());

		setValue(dataMap, "EX_".concat(feeTypeCode), "_P", BigDecimal.ZERO);
		setValue(dataMap, "EA_".concat(feeTypeCode), "_P", BigDecimal.ZERO);
		setValue(dataMap, "PA_".concat(feeTypeCode), "_P", BigDecimal.ZERO);
		setValue(dataMap, "PB_".concat(feeTypeCode), "_P", BigDecimal.ZERO);

		BigDecimal totWaivedTax = BigDecimal.ZERO;
		BigDecimal waivedAmount = fd.getWaivedAmount();

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fd.getTaxComponent())) {
			totWaivedTax = totWaivedTax.add(cgstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(sgstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(igstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(ugstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(cessTax.getWaivedTax());
		}

		setValue(dataMap, feeTypeCode, "_W", waivedAmount.subtract(totWaivedTax));

		// Calculated Amount
		setValue(dataMap, feeTypeCode, "_CGST_C", cgstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_SGST_C", sgstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_IGST_C", igstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_UGST_C", ugstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_CESS_C", cessTax.getActualTax());

		// Paid Amount
		setValue(dataMap, feeTypeCode, "_CGST_P", cgstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_SGST_P", sgstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_IGST_P", igstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_UGST_P", ugstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_CESS_P", cessTax.getPaidTax());

		// Net Amount
		setValue(dataMap, feeTypeCode, "_CGST_N", cgstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_SGST_N", sgstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_IGST_N", igstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_UGST_N", ugstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_CESS_N", cessTax.getNetTax());

		// Waiver GST Amounts (GST Waiver Changes)
		setValue(dataMap, feeTypeCode, "_CGST_W", cgstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_SGST_W", sgstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_IGST_W", igstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_UGST_W", ugstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_CESS_W", cessTax.getWaivedTax());

		String feeScheduleMethod = fd.getFeeScheduleMethod();

		BigDecimal remainingFeeOriginal = BigDecimal.ZERO;
		BigDecimal remainingCGST = BigDecimal.ZERO;
		BigDecimal remainingSGST = BigDecimal.ZERO;
		BigDecimal remainingIGST = BigDecimal.ZERO;
		BigDecimal remainingUSGT = BigDecimal.ZERO;
		BigDecimal remainingCESS = BigDecimal.ZERO;

		if (CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)) {
			remainingFeeOriginal = fd.getRemainingFeeOriginal();
			remainingCGST = cgstTax.getRemFeeTax();
			remainingSGST = sgstTax.getRemFeeTax();
			remainingIGST = igstTax.getRemFeeTax();
			remainingUSGT = ugstTax.getRemFeeTax();
			remainingCESS = cessTax.getRemFeeTax();
		}

		setValue(dataMap, feeTypeCode, "_SCH", remainingFeeOriginal);
		setValue(dataMap, feeTypeCode, "_CGST_SCH", remainingCGST);
		setValue(dataMap, feeTypeCode, "_SGST_SCH", remainingSGST);
		setValue(dataMap, feeTypeCode, "_IGST_SCH", remainingIGST);
		setValue(dataMap, feeTypeCode, "_UGST_SCH", remainingUSGT);
		setValue(dataMap, feeTypeCode, "_CESS_SCH", remainingCESS);

		remainingFeeOriginal = BigDecimal.ZERO;
		remainingCGST = BigDecimal.ZERO;
		remainingSGST = BigDecimal.ZERO;
		remainingIGST = BigDecimal.ZERO;
		remainingUSGT = BigDecimal.ZERO;
		remainingCESS = BigDecimal.ZERO;

		if (RuleConstants.DFT_FEE_FINANCE.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(feeScheduleMethod)) {
			remainingFeeOriginal = fd.getRemainingFeeOriginal();
			remainingCGST = cgstTax.getRemFeeTax();
			remainingSGST = sgstTax.getRemFeeTax();
			remainingIGST = igstTax.getRemFeeTax();
			remainingUSGT = ugstTax.getRemFeeTax();
			remainingCESS = cessTax.getRemFeeTax();
		}

		setValue(dataMap, feeTypeCode, "_AF", remainingFeeOriginal);
		setValue(dataMap, feeTypeCode, "_CGST_AF", remainingCGST);
		setValue(dataMap, feeTypeCode, "_SGST_AF", remainingSGST);
		setValue(dataMap, feeTypeCode, "_IGST_AF", remainingIGST);
		setValue(dataMap, feeTypeCode, "_UGST_AF", remainingUSGT);
		setValue(dataMap, feeTypeCode, "_CESS_AF", remainingCESS);

		// TDS
		setValue(dataMap, feeTypeCode, "_TDS_N", fd.getNetTDS());
		setValue(dataMap, feeTypeCode, "_TDS_P", fd.getPaidTDS());

		return dataMap;
	}

	public static void setValue(Map<String, BigDecimal> dataMap, String prefix, String suffix, BigDecimal amount) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		dataMap.put(prefix.concat(suffix), amount);
	}
}
