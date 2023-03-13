package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.Map;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;

public class ManualAdviceUtil {

	private ManualAdviceUtil() {
		super();
	}

	public static void calculateBalanceAmt(ManualAdvise advice) {
		long finID = advice.getFinID();

		BigDecimal advAmount = advice.getAdviseAmount();
		BigDecimal bal = BigDecimal.ZERO;

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(advice.getTaxComponent())
				|| PennantConstants.List_Select.equals(advice.getTaxComponent()) || advice.getTaxComponent() == null) {
			advice.setBalanceAmt(advAmount.subtract(advice.getPaidAmount()).subtract(advice.getWaivedAmount()));
			return;
		}

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finID);
		TaxAmountSplit taxAmountSplit = GSTCalculator.getExclusiveGST(advAmount, taxPercentages);

		BigDecimal totPaidGSTAmount = CalculationUtil.getTotalPaidGST(advice);
		BigDecimal totWaivedGSTAmount = CalculationUtil.getTotalWaivedGST(advice);

		BigDecimal balGst = taxAmountSplit.gettGST().subtract(totPaidGSTAmount).subtract(totWaivedGSTAmount);

		bal = advAmount.subtract(advice.getPaidAmount()).subtract(advice.getWaivedAmount()).add(balGst);

		advice.setBalanceAmt(bal);
	}
}