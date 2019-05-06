package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.Province;
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

	public GSTCalculator(RuleDAO ruleDAO, BranchDAO branchDAO, ProvinceDAO provinceDAO, FinanceMainDAO financeMainDAO,
			FinanceTaxDetailDAO financeTaxDetailDAO, RuleExecutionUtil ruleExecutionUtil) {
		GSTCalculator.ruleDAO = ruleDAO;
		GSTCalculator.branchDAO = branchDAO;
		GSTCalculator.provinceDAO = provinceDAO;
		GSTCalculator.financeMainDAO = financeMainDAO;
		GSTCalculator.financeTaxDetailDAO = financeTaxDetailDAO;
		GSTCalculator.ruleExecutionUtil = ruleExecutionUtil;
	}

	public static Map<String, BigDecimal> getTaxPercentages(String finReference) {
		Map<String, BigDecimal> gstPercentageMap = new HashMap<>();

		gstPercentageMap.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		gstPercentageMap.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		gstPercentageMap.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		gstPercentageMap.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		gstPercentageMap.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

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
			gstPercentageMap.put(ruleCode, taxPerc);
		}

		gstPercentageMap.put("TOTALGST", totalGST);
		gstPercentageMap.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return gstPercentageMap;
	}

	private static Map<String, Object> getGSTDataMap(String finBranch, String custBranch, String custState,
			String custCountry, FinanceTaxDetail taxDetail) {

		HashMap<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;

		if (StringUtils.isNotBlank(custBranch)) {
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

		}

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

}
