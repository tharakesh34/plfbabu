package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	public static long getAdviseId(List<ManualAdvise> adviseList) {
		List<Long> adviseId = new ArrayList<>();

		for (ManualAdvise mas : adviseList) {
			if (mas.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0) {
				adviseId.add(mas.getAdviseID());
			}
		}

		return Collections.max(adviseId);

	}

	public static BigDecimal getReservedAmount(List<ManualAdvise> adviseList) {
		BigDecimal reserveAmount = BigDecimal.ZERO;

		for (ManualAdvise ma : adviseList) {
			reserveAmount = reserveAmount.add(ma.getReservedAmt());
		}

		return reserveAmount;
	}

	public static BigDecimal getBalanceAmount(List<ManualAdvise> advisesList) {
		BigDecimal balanceAmount = BigDecimal.ZERO;

		for (ManualAdvise ma : advisesList) {
			balanceAmount = balanceAmount.add(ma.getBalanceAmt());
		}

		return balanceAmount;
	}

}