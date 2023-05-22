package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pff.receipt.constants.Allocation;

public class ManualAdviceUtil {

	private ManualAdviceUtil() {
		super();
	}

	public static void calculateBalanceAmt(ManualAdvise advice) {
		long finID = advice.getFinID();

		BigDecimal advAmount = advice.getAdviseAmount();

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

		advice.setBalanceAmt(advAmount.subtract(advice.getPaidAmount()).subtract(advice.getWaivedAmount()).add(balGst));
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

	public static List<ManualAdviseMovements> getMovements(List<ManualAdviseMovements> list) {
		Map<String, ManualAdviseMovements> map = new HashMap<>();

		for (ManualAdviseMovements mam : list) {
			String feeTypeCode = mam.getFeeTypeCode();

			ManualAdviseMovements tempMAM = new ManualAdviseMovements();
			tempMAM.setFeeTypeCode(feeTypeCode);
			map.putIfAbsent(feeTypeCode, tempMAM);

			preparePaidAmounts(mam, map.get(feeTypeCode));
		}

		return map.values().stream().collect(Collectors.toList());
	}

	private static void preparePaidAmounts(ManualAdviseMovements newMAM, ManualAdviseMovements oldMAM) {
		oldMAM.setPaidAmount(oldMAM.getPaidAmount().add(newMAM.getPaidAmount()));
		oldMAM.setWaivedAmount(oldMAM.getWaivedAmount().add(newMAM.getWaivedAmount()));

		oldMAM.setPaidCGST(oldMAM.getPaidCGST().add(newMAM.getPaidCGST()));
		oldMAM.setPaidSGST(oldMAM.getPaidSGST().add(newMAM.getPaidSGST()));
		oldMAM.setPaidUGST(oldMAM.getPaidUGST().add(newMAM.getPaidUGST()));
		oldMAM.setPaidIGST(oldMAM.getPaidIGST().add(newMAM.getPaidIGST()));
		oldMAM.setPaidCESS(oldMAM.getPaidCESS().add(newMAM.getPaidCESS()));

		oldMAM.setWaivedCGST(oldMAM.getWaivedCGST().add(newMAM.getWaivedCGST()));
		oldMAM.setWaivedSGST(oldMAM.getWaivedSGST().add(newMAM.getWaivedSGST()));
		oldMAM.setWaivedUGST(oldMAM.getWaivedUGST().add(newMAM.getWaivedUGST()));
		oldMAM.setWaivedIGST(oldMAM.getWaivedIGST().add(newMAM.getWaivedIGST()));
		oldMAM.setWaivedCESS(oldMAM.getWaivedCESS().add(newMAM.getWaivedCESS()));

		oldMAM.setTdsPaid(oldMAM.getTdsPaid().add(newMAM.getTdsPaid()));
	}

	public static Map<String, BigDecimal> prepareMovementMap(List<ManualAdviseMovements> movements,
			String bounceComponent) {

		Map<String, BigDecimal> movementMap = new HashMap<>();

		addAmountToMap(movementMap, "bounceChargePaid", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_IGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_SGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_UGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CESS_P", BigDecimal.ZERO);

		addAmountToMap(movementMap, "bounceChargeWaived", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_IGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_SGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_UGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CESS_W", BigDecimal.ZERO);

		for (ManualAdviseMovements movement : movements) {
			TaxHeader taxHeader = movement.getTaxHeader();

			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();

			List<Taxes> taxDetails = taxHeader.getTaxDetails();

			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						cgstTax = taxes;
						break;
					case RuleConstants.CODE_SGST:
						sgstTax = taxes;
						break;
					case RuleConstants.CODE_IGST:
						igstTax = taxes;
						break;
					case RuleConstants.CODE_UGST:
						ugstTax = taxes;
						break;
					case RuleConstants.CODE_CESS:
						cessTax = taxes;
						break;

					default:
						break;
					}
				}
			}

			BigDecimal cgstPaid = cgstTax.getPaidTax();
			BigDecimal sgstPaid = sgstTax.getPaidTax();
			BigDecimal igstPaid = igstTax.getPaidTax();
			BigDecimal ugstPaid = ugstTax.getPaidTax();
			BigDecimal cessPaid = cessTax.getPaidTax();

			BigDecimal cgstWaived = cgstTax.getWaivedTax();
			BigDecimal sgstWaived = sgstTax.getWaivedTax();
			BigDecimal igstWaived = igstTax.getWaivedTax();
			BigDecimal ugstWaived = ugstTax.getWaivedTax();
			BigDecimal cessWaived = cessTax.getWaivedTax();

			BigDecimal paidAmt = movement.getPaidAmount();
			BigDecimal waivedAmt = movement.getWaivedAmount();
			BigDecimal tdsPaid = movement.getTdsPaid();

			BigDecimal totalPaidGST = CalculationUtil.getTotalPaidGST(movement);
			BigDecimal totalWaivedGST = CalculationUtil.getTotalWaivedGST(movement);

			String feeTypeCode = movement.getFeeTypeCode();
			String taxComponent = movement.getTaxComponent();

			if (StringUtils.isEmpty(feeTypeCode) || Allocation.BOUNCE.equals(feeTypeCode)) {
				if (taxComponent == null) {
					taxComponent = bounceComponent;
				}
				if (bounceComponent == null) {
					continue;
				}

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
					addAmountToMap(movementMap, "bounceChargePaid", paidAmt);
					addAmountToMap(movementMap, "bounceChargeWaived", waivedAmt);
				} else {
					addAmountToMap(movementMap, "bounceChargePaid", paidAmt.add(totalPaidGST));
					addAmountToMap(movementMap, "bounceChargeWaived", waivedAmt.add(totalWaivedGST));
				}

				addAmountToMap(movementMap, "bounceCharge" + "_CGST_P", cgstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_SGST_P", sgstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_IGST_P", igstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_UGST_P", ugstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_CESS_P", cessPaid);

				addAmountToMap(movementMap, "bounceCharge" + "_CGST_W", cgstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_SGST_W", sgstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_IGST_W", igstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_UGST_W", ugstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_CESS_W", cessWaived);

				addAmountToMap(movementMap, "bounceCharge" + "_TDS_P", tdsPaid);

			} else {
				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
					addAmountToMap(movementMap, feeTypeCode + "_P", paidAmt);
					addAmountToMap(movementMap, feeTypeCode + "_W", waivedAmt);
				} else {
					addAmountToMap(movementMap, feeTypeCode + "_P", paidAmt.add(totalPaidGST));
					addAmountToMap(movementMap, feeTypeCode + "_W", waivedAmt.add(totalWaivedGST));
				}
			}

			addAmountToMap(movementMap, feeTypeCode + "_CGST_P", cgstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_SGST_P", sgstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_IGST_P", igstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_UGST_P", ugstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_CESS_P", cessPaid);

			addAmountToMap(movementMap, feeTypeCode + "_CGST_W", cgstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_SGST_W", sgstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_IGST_W", igstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_UGST_W", ugstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_CESS_W", cessWaived);

			addAmountToMap(movementMap, feeTypeCode + "_TDS_P", tdsPaid);
		}

		return movementMap;
	}

	private static void addAmountToMap(Map<String, BigDecimal> movementMap, String feeCode, BigDecimal amount) {
		BigDecimal amt = movementMap.computeIfAbsent(feeCode, code -> BigDecimal.ZERO);

		movementMap.put(feeCode, amt.add(amount));
	}
}