package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.rmtmasters.GSTRateDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;

public class GSTCalculator {
	private static Logger logger = LogManager.getLogger(GSTCalculator.class);

	private static RuleDAO ruleDAO;
	private static BranchDAO branchDAO;
	private static ProvinceDAO provinceDAO;
	private static FinanceMainDAO financeMainDAO;
	private static FinanceTaxDetailDAO financeTaxDetailDAO;
	private static RuleExecutionUtil ruleExecutionUtil;
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	private static String TAX_ROUNDING_MODE;
	private static int TAX_ROUNDING_TARGET;

	private static BigDecimal TDS_PERCENTAGE = BigDecimal.ZERO;
	private static String TAX_ROUND_MODE = null;
	private static String TDS_ROUND_MODE = null;
	private static int TDS_ROUNDING_TARGET = 0;
	private static BigDecimal TDS_MULTIPLIER = BigDecimal.ZERO;
	private static GSTRateDAO gstRateDAO;

	public GSTCalculator(RuleDAO ruleDAO, BranchDAO branchDAO, ProvinceDAO provinceDAO, FinanceMainDAO financeMainDAO,
			FinanceTaxDetailDAO financeTaxDetailDAO, RuleExecutionUtil ruleExecutionUtil, GSTRateDAO gstRateDAO) {
		initilize(ruleDAO, branchDAO, provinceDAO, financeMainDAO, financeTaxDetailDAO, ruleExecutionUtil, gstRateDAO);
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
		taxSplit.setcGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_CGST))); //CGST Amount
		taxSplit.setsGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_SGST))); //SGST Amount
		taxSplit.setuGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_UGST))); //UGST Amount
		taxSplit.setiGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_IGST))); //IGST Amount
		taxSplit.setCess(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_CESS)));//CESS Amount
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST())
				.add(taxSplit.getiGST().add(taxSplit.getCess())));
		taxSplit.setNetAmount(taxableAmount.add(taxSplit.gettGST()));
		return taxSplit;
	}

	public static TaxAmountSplit getInclusiveGST(BigDecimal taxableAmount, Map<String, BigDecimal> taxPercentages) {
		return getInclusiveGST(taxableAmount, BigDecimal.ZERO, taxPercentages);
	}

	public static TaxAmountSplit getInclusiveGST(BigDecimal taxableAmount, BigDecimal waivedAmount,
			Map<String, BigDecimal> taxPercentages) {

		TaxAmountSplit taxSplit = new TaxAmountSplit();

		BigDecimal netAmount = getInclusiveAmount(taxableAmount.subtract(waivedAmount),
				taxPercentages.get(RuleConstants.CODE_TOTAL_GST));//Fee factor

		taxSplit.setcGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_CGST))); //CGST Amount
		taxSplit.setsGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_SGST))); //SGST Amount
		taxSplit.setuGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_UGST))); //UGST Amount
		taxSplit.setiGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_IGST))); //IGST Amount
		taxSplit.setCess(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_CESS))); //CESS Amount
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST())
				.add(taxSplit.getiGST().add(taxSplit.getCess())));

		taxSplit.setNetAmount(taxableAmount); //FeeFactor + GST Factor

		return taxSplit;
	}

	/**
	 * This method will return the GST percentages by executing the GST rules configured.
	 * 
	 * @param finReference
	 * @return The GST percentages MAP
	 */
	public static Map<String, BigDecimal> getTaxPercentages(long custId, String finCCY, String userBranch,
			String finBranch, FinanceTaxDetail financeTaxDetail) {
		Map<String, BigDecimal> taxPercentages = new HashMap<>();

		taxPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);

		Map<String, Object> dataMap = null;
		String custBranch = null;
		String custProvince = null;
		String custCountry = null;
		if (custId > 0) {
			dataMap = financeMainDAO.getGSTDataMap(custId);
			if (dataMap.get("CustBranch") != null) {
				custBranch = dataMap.get("CustBranch").toString();
			}

			if (dataMap.get("CustProvince") != null) {
				custProvince = dataMap.get("CustProvince").toString();
			}

			if (dataMap.get("CustCountry") != null) {
				custCountry = dataMap.get("CustCountry").toString();
			}
		} else {
			custBranch = userBranch;
		}

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custCountry, financeTaxDetail);
		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;
		if (SysParamUtil.isAllowed(SMTParameterConstants.CALCULATE_GST_ON_GSTRATE_MASTER)) {
			totalGST = BigDecimal.ZERO;
			if (dataMap.containsKey("fromState") && dataMap.containsKey("toState")) {
				String fromState = (String) dataMap.get("fromState");
				String toState = (String) dataMap.get("toState");
				if (StringUtils.isNotBlank(fromState) && StringUtils.isNotBlank(toState)) {
					List<GSTRate> gstRateDetailList = gstRateDAO.getGSTRateByStates(fromState, toState, "_AView");
					if (CollectionUtils.isNotEmpty(gstRateDetailList)) {
						for (GSTRate gstRate : gstRateDetailList) {
							BigDecimal taxPerc = gstRate.getPercentage();
							totalGST = totalGST.add(taxPerc);
							taxPercentages.put(gstRate.getTaxType(), gstRate.getPercentage());
						}
					}
				}

			}
		} else {
			List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");

			for (Rule rule : rules) {
				BigDecimal taxPerc = BigDecimal.ZERO;
				ruleCode = rule.getRuleCode();

				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCCY);
				totalGST = totalGST.add(taxPerc);
				taxPercentages.put(ruleCode, taxPerc);
			}

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
		gstPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_GST_PERCENTAGE, BigDecimal.ZERO);

		Map<String, Object> dataMap = getGSTDataMap(finReference);

		String finCCY = (Object) dataMap.get("FinCCY") == null ? "" : String.valueOf((Object) dataMap.get("FinCCY"));

		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;

		if (SysParamUtil.isAllowed(SMTParameterConstants.CALCULATE_GST_ON_GSTRATE_MASTER)) {
			totalGST = BigDecimal.ZERO;

			if (dataMap.containsKey("fromState") && dataMap.containsKey("toState")) {
				String fromState = (String) dataMap.get("fromState");
				String toState = (String) dataMap.get("toState");

				if (StringUtils.isNotBlank(fromState) && StringUtils.isNotBlank(toState)) {
					List<GSTRate> gstRateDetailList = gstRateDAO.getGSTRateByStates(fromState, toState, "_AView");

					if (CollectionUtils.isNotEmpty(gstRateDetailList)) {
						for (GSTRate gstRate : gstRateDetailList) {
							BigDecimal taxPerc = gstRate.getPercentage();
							totalGST = totalGST.add(taxPerc);
							gstPercentages.put(RuleConstants.CODE_GST_PERCENTAGE, taxPerc);
							gstPercentages.put(gstRate.getTaxType(), gstRate.getPercentage());
						}
					}
				}
			}
		} else {
			List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");
			for (Rule rule : rules) {
				BigDecimal taxPerc = BigDecimal.ZERO;
				ruleCode = rule.getRuleCode();
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCCY);
				totalGST = totalGST.add(taxPerc);
				gstPercentages.put(ruleCode, taxPerc);
				
			}
		}
		gstPercentages.put(RuleConstants.CODE_GST_PERCENTAGE, totalGST);
		gstPercentages.put("TOTALGST", totalGST);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return gstPercentages;
	}

	public static Map<String, BigDecimal> getTaxPercentages(Map<String, Object> dataMap, String finCcy) {

		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");

		BigDecimal totalTaxPerc = BigDecimal.ZERO;
		Map<String, BigDecimal> taxPercMap = new HashMap<>();
		taxPercMap.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);

		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			if (StringUtils.equals(RuleConstants.CODE_CGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_IGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_IGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_SGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_SGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_UGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_UGST, taxPerc);
			}
		}
		taxPercMap.put("TOTALGST", totalTaxPerc);

		return taxPercMap;
	}

	public static Map<String, Object> getGSTDataMap(String finReference) {
		Map<String, Object> dataMap = financeMainDAO.getGSTDataMap(finReference);
		String finBranch = (Object) dataMap.get("FinBranch") == null ? ""
				: ((Object) dataMap.get("FinBranch")).toString();
		String custBranch = (Object) dataMap.get("CustBranch") == null ? ""
				: String.valueOf((Object) dataMap.get("CustBranch"));
		String custProvince = (Object) dataMap.get("CustProvince") == null ? ""
				: String.valueOf((Object) dataMap.get("CustProvince"));
		String custCountry = (Object) dataMap.get("CustCountry") == null ? ""
				: String.valueOf((Object) dataMap.get("CustCountry"));

		FinanceTaxDetail financeTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finReference, "_View");

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custCountry, financeTaxDetail);
		return dataMap;
	}

	public static Map<String, Object> getGSTDataMap(String finBranch, String custBranch, String custState,
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
		if (SysParamUtil.isAllowed(SMTParameterConstants.GST_DEFAULT_FROM_STATE)) {
			String defaultFinBranch = SysParamUtil.getValueAsString(SMTParameterConstants.GST_DEFAULT_STATE_CODE);
			branch = branchDAO.getBranchById(defaultFinBranch, "");
		}

		Province fromState = provinceDAO.getProvinceById(branch.getBranchCountry(), branch.getBranchProvince(), "");

		if (fromState != null) {
			gstExecutionMap.put("fromState", fromState.getCPProvince());
			gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
			gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
		}

		String toStateCode = "";
		String toCountryCode = "";
		boolean sezCustomer = false;

		if (taxDetail != null && StringUtils.isNotBlank(taxDetail.getApplicableFor())
				&& !PennantConstants.List_Select.equals(taxDetail.getApplicableFor())
				&& StringUtils.isNotBlank(taxDetail.getProvince()) && StringUtils.isNotBlank(taxDetail.getCountry())) {
			toStateCode = taxDetail.getProvince();
			toCountryCode = taxDetail.getCountry();
			gstExempted = taxDetail.isTaxExempted();
			if (StringUtils.isNotBlank(taxDetail.getSezCertificateNo())) {
				sezCustomer = true;
			}
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
		gstExecutionMap.put("sezCertificateNo", sezCustomer);

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

	public static BigDecimal getExclusiveTax(BigDecimal amount, BigDecimal taxPerc) {
		BigDecimal taxAmount = BigDecimal.ZERO;

		if (taxPerc == null) {
			logger.warn("Tax percentage cannot be blank.");
			taxPerc = BigDecimal.ZERO;
		}

		if (taxPerc.compareTo(BigDecimal.ZERO) != 0) {
			taxAmount = (amount.multiply(taxPerc)).divide(HUNDRED, 9, RoundingMode.HALF_DOWN);
			taxAmount = CalculationUtil.roundAmount(taxAmount, getTaxRoundingMode(), getTaxRoundingTarget());
		}

		return taxAmount;
	}

	public static BigDecimal getInclusiveAmount(BigDecimal amount, BigDecimal taxPerc) {
		if (taxPerc == null) {
			logger.warn("Tax percentage cannot be blank.");
			taxPerc = BigDecimal.ZERO;
		}

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

	private void initilize(RuleDAO ruleDAO, BranchDAO branchDAO, ProvinceDAO provinceDAO, FinanceMainDAO financeMainDAO,
			FinanceTaxDetailDAO financeTaxDetailDAO, RuleExecutionUtil ruleExecutionUtil, GSTRateDAO gstRateDAO) {
		GSTCalculator.ruleDAO = ruleDAO;
		GSTCalculator.branchDAO = branchDAO;
		GSTCalculator.provinceDAO = provinceDAO;
		GSTCalculator.financeMainDAO = financeMainDAO;
		GSTCalculator.financeTaxDetailDAO = financeTaxDetailDAO;
		GSTCalculator.ruleExecutionUtil = ruleExecutionUtil;
		GSTCalculator.gstRateDAO = gstRateDAO;
	}

	/**
	 * Advise Waiver Calculation
	 * 
	 * @param finReference
	 * @param movement
	 */
	public static void waiverAdviseCalc(String finReference, ManualAdviseMovements movement) {
		TaxAmountSplit taxSplit = null;
		Map<String, BigDecimal> taxPercentages = getTaxPercentages(finReference);
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(movement.getTaxComponent())) {
			taxSplit = getExclusiveGST(movement.getPaidAmount(), taxPercentages);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(movement.getTaxComponent())) {
			taxSplit = getInclusiveGST(movement.getPaidAmount(), taxPercentages);
		} else {
			return;
		}
		if (taxSplit != null) {
			movement.setWaivedCGST(taxSplit.getcGST());
			movement.setWaivedSGST(taxSplit.getsGST());
			movement.setWaivedIGST(taxSplit.getiGST());
			movement.setWaivedUGST(taxSplit.getuGST());
		}
	}

	public static BigDecimal calGstTaxAmount(BigDecimal actTaxAmount, BigDecimal gstPerc, BigDecimal totalGSTPerc) {
		BigDecimal gstAmount = BigDecimal.ZERO;

		if (gstPerc.compareTo(BigDecimal.ZERO) > 0) {
			gstAmount = (actTaxAmount.multiply(gstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			gstAmount = CalculationUtil.roundAmount(gstAmount, getTaxRoundingMode(), getTaxRoundingTarget());
		}

		return gstAmount;
	}

	public static BigDecimal getTDS(BigDecimal amount) {
		initilizeTDSAttributes();

		BigDecimal tds = amount.multiply(TDS_PERCENTAGE);
		tds = tds.divide(HUNDRED, 9, RoundingMode.HALF_UP);
		tds = CalculationUtil.roundAmount(tds, TDS_ROUND_MODE, TDS_ROUNDING_TARGET);
		return tds;
	}

	public static BigDecimal getNetTDS(BigDecimal amount) {
		initilizeTDSAttributes();

		BigDecimal netAmount = amount.multiply(TDS_MULTIPLIER);
		netAmount = CalculationUtil.roundAmount(netAmount, TAX_ROUND_MODE, TDS_ROUNDING_TARGET);

		return netAmount;
	}

	private static void initilizeTDSAttributes() {
		if (StringUtils.isEmpty(TAX_ROUND_MODE) || StringUtils.isEmpty(TDS_ROUND_MODE)) {
			TAX_ROUND_MODE = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
			TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);

			TDS_ROUND_MODE = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
			TDS_PERCENTAGE = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			TDS_MULTIPLIER = HUNDRED.divide(HUNDRED.subtract(TDS_PERCENTAGE), 20, RoundingMode.HALF_DOWN);
		}
	}

}
