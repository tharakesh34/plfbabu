package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.AppException;

public class GSTCalculator {
	private static RuleDAO ruleDAO;
	private static BranchDAO branchDAO;
	private static ProvinceDAO provinceDAO;
	private static FinanceMainDAO financeMainDAO;
	private static FinanceTaxDetailDAO financeTaxDetailDAO;
	private static RuleExecutionUtil ruleExecutionUtil;
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	private static String TAX_ROUNDING_MODE;
	private static int TAX_ROUNDING_TARGET;

	public GSTCalculator(RuleDAO ruleDAO, BranchDAO branchDAO, ProvinceDAO provinceDAO, FinanceMainDAO financeMainDAO,
			FinanceTaxDetailDAO financeTaxDetailDAO, RuleExecutionUtil ruleExecutionUtil) {
		GSTCalculator.ruleDAO = ruleDAO;
		GSTCalculator.branchDAO = branchDAO;
		GSTCalculator.provinceDAO = provinceDAO;
		GSTCalculator.financeMainDAO = financeMainDAO;
		GSTCalculator.financeTaxDetailDAO = financeTaxDetailDAO;
		GSTCalculator.ruleExecutionUtil = ruleExecutionUtil;
	}

	/**
	 * This method will calculate the total GST on the specified taxableAmount by executing the GST rules configured.
	 * 
	 * @param finReference
	 *            The finRefernce to prepare the data map required to execute the GST rules.
	 * @param taxableAmount
	 *            The amount in which the GST will be calculated.
	 * @param taxComponent
	 *            The taxable component either whether the GST is include in <code>taxableAmount</code> or exclude.
	 * @return The total calculated GST.
	 */
	public static BigDecimal getTotalGST(String finReference, BigDecimal taxableAmount, String taxComponent) {
		Map<String, BigDecimal> gstPercentages = getTaxPercentages(finReference);
		return getGSTTaxSplit(taxableAmount, gstPercentages, taxComponent).gettGST();
	}

	/**
	 * This method will calculate the total GST on the specified taxableAmount by executing the GST rules configured.
	 * 
	 * @param taxableAmount
	 *            The amount in which the GST will be calculated.
	 * @param gstPercentages
	 *            The GST percentages
	 * @param taxComponent
	 *            The taxable component either whether the GST is include in <code>taxableAmount</code> or exclude.
	 * @return The total calculated GST.
	 */
	public static BigDecimal getTotalGST(BigDecimal taxableAmount, Map<String, BigDecimal> taxPercentages,
			String taxComponent) {
		return getGSTTaxSplit(taxableAmount, taxPercentages, taxComponent).gettGST();
	}
	
	public static TaxAmountSplit getGSTTaxSplit(BigDecimal taxableAmount, Map<String, BigDecimal> taxPercentages,
			String taxComponent) {

		return getGSTTaxSplit(taxableAmount, BigDecimal.ZERO, taxPercentages, taxComponent);
	}

	public static TaxAmountSplit getGSTTaxSplit(BigDecimal taxableAmount, BigDecimal waivedAmount,
			Map<String, BigDecimal> taxPercentages, String taxComponent) {
		TaxAmountSplit taxAmountSplit = null;
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			taxAmountSplit = getExclusiveGST(taxableAmount, taxPercentages);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			taxAmountSplit = getInclusiveGST(taxableAmount, waivedAmount, taxPercentages);
		} else {
			taxAmountSplit = new TaxAmountSplit();
		}

		return taxAmountSplit;
	}

	public static TaxAmountSplit getExclusiveGST(BigDecimal taxableAmount, Map<String, BigDecimal> taxPercentages) {
		TaxAmountSplit taxSplit = new TaxAmountSplit();
		taxSplit.setcGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_CGST)));
		taxSplit.setsGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_SGST)));
		taxSplit.setuGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_UGST)));
		taxSplit.setiGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_IGST)));
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST()).add(taxSplit.getiGST()));
		taxSplit.setNetAmount(taxSplit.getAmount().add(taxSplit.gettGST()));
		return taxSplit;
	}

	public static TaxAmountSplit getInclusiveGST(BigDecimal taxableAmount, Map<String, BigDecimal> taxPercentages) {
		return getInclusiveGST(taxableAmount, BigDecimal.ZERO, taxPercentages);
	}
	
	public static TaxAmountSplit getInclusiveGST(BigDecimal taxableAmount, BigDecimal waivedAmount,
			Map<String, BigDecimal> taxPercentages) {
		TaxAmountSplit taxSplit = new TaxAmountSplit();

		BigDecimal netAmount = getInclusiveAmount(taxableAmount.subtract(waivedAmount), taxPercentages.get(RuleConstants.CODE_TOTAL_GST));
		taxSplit.setcGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_CGST)));
		taxSplit.setsGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_SGST)));
		taxSplit.setuGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_UGST)));
		taxSplit.setiGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_IGST)));
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST()).add(taxSplit.getiGST()));
		taxSplit.setNetAmount(netAmount.subtract(taxSplit.gettGST()));
		return taxSplit;
	}


	/**
	 * This method will return the GST percentages by executing the GST rules configured.
	 * 
	 * @param finReference
	 * @return The GST percentages MAP
	 */
	public static Map<String, BigDecimal> getTaxPercentages(long custId, String finCCY, String userBranch, String finBranch) {
		Map<String, BigDecimal> taxPercentages = new HashMap<>();

		taxPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);
		
		Map<String, Object> dataMap = null;
		String custBranch = null;
		String custProvince = null;
		String custCountry = null;
		if (custId > 0) {
			dataMap = financeMainDAO.getGSTDataMap(custId);
			custBranch = (String) dataMap.get("CustBranch");
			custProvince = (String) dataMap.get("CustProvince");
			custCountry = (String) dataMap.get("CustCountry");
		} else {
			custBranch = userBranch;
		}

		FinanceTaxDetail financeTaxDetail = null;
		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custCountry, financeTaxDetail);
		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");

		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;
		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			ruleCode = rule.getRuleCode();

			taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCCY);
			totalGST = totalGST.add(taxPerc);
			taxPercentages.put(ruleCode, taxPerc);
		}

		taxPercentages.put("TOTALGST", totalGST);
		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return taxPercentages;
	}

	/**
	 * This method will return the GST percentages by executing the GST rules configured.
	 * 
	 * @param finReference
	 * @return The GST percentages MAP
	 */
	public static Map<String, BigDecimal> getTaxPercentages(String finReference) {
		Map<String, BigDecimal> gstPercentages = new HashMap<>();

		gstPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		Map<String, Object> dataMap = financeMainDAO.getGSTDataMap(finReference);
		String finBranch = (String) dataMap.get("FinBranch");
		String custBranch = (String) dataMap.get("CustBranch");
		String custProvince = (String) dataMap.get("CustProvince");
		String custCountry = (String) dataMap.get("CustCountry");
		String finCCY = (String) dataMap.get("FinCCY");

		FinanceTaxDetail financeTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finReference, "_View");
		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custCountry, financeTaxDetail);
		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");

		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;
		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			ruleCode = rule.getRuleCode();

			taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCCY);
			totalGST = totalGST.add(taxPerc);
			gstPercentages.put(ruleCode, taxPerc);
		}

		gstPercentages.put("TOTALGST", totalGST);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return gstPercentages;
	}

	private static Map<String, Object> getGSTDataMap(String finBranch, String custBranch, String custState,
			String custCountry, FinanceTaxDetail taxDetail) {

		HashMap<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;
		
		if (StringUtils.isBlank(custBranch)) {
			return gstExecutionMap;
		}


		if (finBranch == null) {
			finBranch = custBranch;
		}

		Branch branch = branchDAO.getBranchById(finBranch, "");
		Province fromState = provinceDAO.getProvinceById(branch.getBranchCountry(), branch.getBranchProvince(), "");

		if (fromState != null) {
			gstExecutionMap.put("fromState", fromState.getCPProvince());
			gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
			gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
		}

		String toStateCode = "";
		String toCountryCode = "";

		if (taxDetail != null && StringUtils.isNotBlank(taxDetail.getApplicableFor())
				&& !PennantConstants.List_Select.equals(taxDetail.getApplicableFor())
				&& StringUtils.isNotBlank(taxDetail.getProvince())
				&& StringUtils.isNotBlank(taxDetail.getCountry())) {
			toStateCode = taxDetail.getProvince();
			toCountryCode = taxDetail.getCountry();
			gstExempted = taxDetail.isTaxExempted();
		} else {
			toStateCode = custState;
			toCountryCode = custCountry;
		}

		if (StringUtils.isBlank(toCountryCode) || StringUtils.isBlank(toStateCode)) { // if toCountry is not available 
			gstExecutionMap.put("toState", "");
			gstExecutionMap.put("toUnionTerritory", 2);
			gstExecutionMap.put("toStateGstExempted", "");
		} else {
			Province toState = provinceDAO.getProvinceById(toCountryCode, toStateCode, "");

			if (toState == null) {
				gstExecutionMap.put("toState", "");
				gstExecutionMap.put("toUnionTerritory", 2);
				gstExecutionMap.put("toStateGstExempted", "");
			} else {
				gstExecutionMap.put("toState", toState.getCPProvince());
				gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
				gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
			}
		}

		gstExecutionMap.put("gstExempted", gstExempted);

	

		return gstExecutionMap;
	}

	private static BigDecimal getRuleResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = ruleExecutionUtil.executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			throw new AppException(String.format("Unable to execute the %s Rule.", sqlRule));
		}

		return result;
	}

	private static BigDecimal getExclusiveTax(BigDecimal amount, BigDecimal taxPerc) {
		BigDecimal taxAmount = BigDecimal.ZERO;

		if (taxPerc.compareTo(BigDecimal.ZERO) != 0) {
			taxAmount = (amount.multiply(taxPerc)).divide(HUNDRED, 9, RoundingMode.HALF_DOWN);
			taxAmount = CalculationUtil.roundAmount(taxAmount, getTaxRoundingMode(), getTaxRoundingTarget());
		}

		return taxAmount;
	}

	private static BigDecimal getInclusiveAmount(BigDecimal amount, BigDecimal taxPerc) {
		BigDecimal percentage = (taxPerc.add(HUNDRED)).divide(HUNDRED, 9, RoundingMode.HALF_DOWN);
		BigDecimal actualAmt = amount.divide(percentage, 9, RoundingMode.HALF_DOWN);
		actualAmt = CalculationUtil.roundAmount(actualAmt, getTaxRoundingMode(), getTaxRoundingTarget());
		return actualAmt;
	}

	private static String getTaxRoundingMode() {
		if (TAX_ROUNDING_MODE == null) {
			TAX_ROUNDING_MODE = SysParamUtil.getValueAsString(CalculationConstants.TAX_ROUNDINGMODE);
		}

		return TAX_ROUNDING_MODE;
	}

	private static int getTaxRoundingTarget() {
		if (TAX_ROUNDING_TARGET == 0) {
			TAX_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);
		}

		return TAX_ROUNDING_TARGET;
	}

}
