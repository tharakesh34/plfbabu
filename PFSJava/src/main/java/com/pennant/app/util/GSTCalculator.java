package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.customermasters.GSTDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.rmtmasters.GSTRateDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
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
import com.pennanttech.pff.core.TableType;
import com.pennattech.pff.cd.dao.ManufacturerDAO;
import com.pennattech.pff.cd.dao.MerchantDetailsDAO;

public class GSTCalculator {
	private static Logger logger = LogManager.getLogger(GSTCalculator.class);

	private static RuleDAO ruleDAO;
	private static BranchDAO branchDAO;
	private static ProvinceDAO provinceDAO;
	private static FinanceMainDAO financeMainDAO;
	private static FinanceTaxDetailDAO financeTaxDetailDAO;
	private static GSTRateDAO gstRateDAO;
	private static MerchantDetailsDAO merchantDetailsDAO;
	private static ManufacturerDAO manufacturerDAO;
	private static GSTDetailDAO gstDetailDAO;

	private static final BigDecimal HUNDRED = new BigDecimal(100);
	private static String TAX_ROUNDING_MODE;
	private static int TAX_ROUNDING_TARGET;

	private static String CALCULATE_GST_ON_GSTRATE_MASTER_PARAM = null;
	private static boolean CALCULATE_GST_ON_GSTRATE_MASTER = false;

	private static String GST_DEFAULT_FROM_STATE_PARAM = null;
	private static boolean GST_DEFAULT_FROM_STATE = false;
	private static String GST_DEFAULT_STATE_CODE = null;

	/**
	 * This method will calculate the total GST on the specified taxableAmount by executing the GST rules configured.
	 * 
	 * @param finID         The finID to prepare the data map required to execute the GST rules.
	 * @param taxableAmount The amount in which the GST will be calculated.
	 * @param taxComponent  The taxable component either whether the GST is include in <code>taxableAmount</code> or
	 *                      exclude.
	 * @return The total calculated GST.
	 */
	public static BigDecimal getTotalGST(long finID, BigDecimal taxableAmount, String taxComponent) {
		Map<String, BigDecimal> gstPercentages = getTaxPercentages(finID);
		return getGSTTaxSplit(taxableAmount, gstPercentages, taxComponent).gettGST();
	}

	/**
	 * This method will calculate the total GST on the specified taxableAmount by executing the GST rules configured.
	 * 
	 * @param finReference  The finReference to prepare the data map required to execute the GST rules.
	 * @param taxableAmount The amount in which the GST will be calculated.
	 * @param taxComponent  The taxable component either whether the GST is include in <code>taxableAmount</code> or
	 *                      exclude.
	 * @return The total calculated GST.
	 */
	public static BigDecimal getTotalGST(String finReference, BigDecimal taxableAmount, String taxComponent) {
		Long finID = financeMainDAO.getFinID(finReference);

		return getTotalGST(finID, taxableAmount, taxComponent);
	}

	/**
	 * This method will calculate the total GST on the specified taxableAmount by executing the GST rules configured.
	 * 
	 * @param taxableAmount  The amount in which the GST will be calculated.
	 * @param gstPercentages The GST percentages
	 * @param taxComponent   The taxable component either whether the GST is include in <code>taxableAmount</code> or
	 *                       exclude.
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
		taxSplit.setcGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_CGST))); // CGST Amount
		taxSplit.setsGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_SGST))); // SGST Amount
		taxSplit.setuGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_UGST))); // UGST Amount
		taxSplit.setiGST(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_IGST))); // IGST Amount
		taxSplit.setCess(getExclusiveTax(taxableAmount, taxPercentages.get(RuleConstants.CODE_CESS)));// CESS Amount
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
				taxPercentages.get(RuleConstants.CODE_TOTAL_GST));// Fee factor

		taxSplit.setcGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_CGST))); // CGST Amount
		taxSplit.setsGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_SGST))); // SGST Amount
		taxSplit.setuGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_UGST))); // UGST Amount
		taxSplit.setiGST(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_IGST))); // IGST Amount
		taxSplit.setCess(getExclusiveTax(netAmount, taxPercentages.get(RuleConstants.CODE_CESS))); // CESS Amount
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST())
				.add(taxSplit.getiGST().add(taxSplit.getCess())));

		taxSplit.setNetAmount(taxableAmount); // FeeFactor + GST Factor

		return taxSplit;
	}

	// TODO:GANESH need to check FinanceTaxDetail
	public static Map<String, BigDecimal> getTaxPercentages(long custId, String finCCY, String userBranch,
			String finBranch) {
		return getTaxPercentages(custId, finCCY, userBranch, finBranch, null);

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
		taxPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		Map<String, Object> dataMap = null;
		String custBranch = null;
		String custProvince = null;
		String custCountry = null;
		String custResdSts = null;

		if (custId > 0) {
			dataMap = financeMainDAO.getCustGSTDataMap(custId, TableType.MAIN_TAB);

			if (MapUtils.isEmpty(dataMap)) {
				dataMap = financeMainDAO.getCustGSTDataMap(custId, TableType.TEMP_TAB);
			}

			if (MapUtils.isEmpty(dataMap)) {
				dataMap = financeMainDAO.getCustGSTDataMap(custId, TableType.VIEW);
			}

			if (dataMap.get("CustBranch") != null) {
				custBranch = dataMap.get("CustBranch").toString();
			}

			if (dataMap.get("CustProvince") != null) {
				custProvince = dataMap.get("CustProvince").toString();
			}

			if (dataMap.get("CustCountry") != null) {
				custCountry = dataMap.get("CustCountry").toString();
			}

			if (dataMap.get("ResidentialStatus") != null) {
				custResdSts = dataMap.get("ResidentialStatus").toString();
			}
		} else {
			custBranch = userBranch;
		}

		List<GSTDetail> gstDetails = gstDetailDAO.getGSTDetailById(custId, "_View");

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custResdSts, custCountry, financeTaxDetail,
				gstDetails);

		// setting the customer residential status
		dataMap.put("custResidentialSts", custResdSts);
		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;
		if (isGSTCalculationOnMaster()) {
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

		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return taxPercentages;
	}

	public static Map<String, BigDecimal> getTaxPercentages(CustomerDetails cd, String finCCY, String userBranch,
			String finBranch, FinanceTaxDetail financeTaxDetail) {
		Map<String, BigDecimal> taxPercentages = new HashMap<>();

		taxPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		Map<String, Object> dataMap = null;
		String custBranch = null;
		String custProvince = null;
		String custCountry = null;
		String custResdSts = null;

		Customer customer = cd.getCustomer();

		if (cd.getCustID() > 0) {
			dataMap = financeMainDAO.getCustGSTDataMap(cd.getCustID(), TableType.MAIN_TAB);

			if (MapUtils.isEmpty(dataMap)) {
				dataMap = financeMainDAO.getCustGSTDataMap(cd.getCustID(), TableType.TEMP_TAB);
			}

			if (MapUtils.isEmpty(dataMap)) {
				dataMap = financeMainDAO.getCustGSTDataMap(cd.getCustID(), TableType.VIEW);
			}

			if (dataMap.get("CustBranch") != null) {
				custBranch = dataMap.get("CustBranch").toString();
			}

			if (dataMap.get("CustProvince") != null) {
				custProvince = dataMap.get("CustProvince").toString();
			}

			if (dataMap.get("CustCountry") != null) {
				custCountry = dataMap.get("CustCountry").toString();
			}

			if (dataMap.get("ResidentialStatus") != null) {
				custResdSts = dataMap.get("ResidentialStatus").toString();
			}
		} else {
			dataMap = new HashMap<String, Object>();
			dataMap.put("CustBranch", customer.getCustDftBranch());
			dataMap.put("ResidentialStatus", customer.getResidentialStatus());
			dataMap.put("CustResidentialSts", customer.getCustResidentialSts());
			for (CustomerAddres ca : cd.getAddressList()) {
				if (PennantConstants.KYC_PRIORITY_VERY_HIGH.equals(String.valueOf(ca.getCustAddrPriority()))) {
					dataMap.put("CustProvince", ca.getCustAddrProvince());
					dataMap.put("CustCountry", ca.getCustAddrCountry());
					custProvince = dataMap.get("CustProvince").toString();
					custCountry = dataMap.get("CustCountry").toString();
				}
			}
			custBranch = dataMap.get("CustBranch").toString();
			custResdSts = dataMap.get("ResidentialStatus").toString();
		}

		List<GSTDetail> gstDetails = gstDetailDAO.getGSTDetailById(customer.getCustID(), "_View");

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custResdSts, custCountry, financeTaxDetail,
				gstDetails);

		// setting the customer residential status
		dataMap.put("custResidentialSts", custResdSts);
		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;
		if (isGSTCalculationOnMaster()) {
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

		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return taxPercentages;
	}

	public static Map<String, BigDecimal> getDealerTaxPercentages(Long manufacturerDealerId, String finCCY,
			String userBranch, String finBranch, FinanceTaxDetail financeTaxDetail) {

		Map<String, BigDecimal> taxPercentages = new HashMap<>();

		taxPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		Map<String, Object> dataMap = null;
		String custBranch = null;
		String custProvince = null;
		String custCountry = null;
		String custResdSts = null;

		if (manufacturerDealerId != null && manufacturerDealerId > 0) {
			dataMap = financeMainDAO.getGSTDataMapForDealer(manufacturerDealerId);

			dataMap.put("CustBranch", "MUM");// FIXME
			if (dataMap.get("CustBranch") != null) {
				custBranch = dataMap.get("CustBranch").toString();
			}

			if (dataMap.get("CustProvince") != null) {
				custProvince = dataMap.get("CustProvince").toString();
			}

			if (dataMap.get("CustCountry") != null) {
				custCountry = dataMap.get("CustCountry").toString();
			}

			// FIXME
			if (dataMap.get("ResidentialStatus") == null) {
				dataMap.put("ResidentialStatus", PennantConstants.RESIDENT);
			}

			custResdSts = dataMap.get("ResidentialStatus").toString();
		} else {
			custBranch = userBranch;
		}

		dataMap = getDealerGSTDataMap(finBranch, custBranch, custProvince, custResdSts, custCountry, financeTaxDetail);
		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;
		if (isGSTCalculationOnMaster()) {
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

		taxPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return taxPercentages;
	}

	private static boolean isGSTCalculationOnMaster() {
		if (CALCULATE_GST_ON_GSTRATE_MASTER_PARAM == null) {
			CALCULATE_GST_ON_GSTRATE_MASTER_PARAM = SMTParameterConstants.CALCULATE_GST_ON_GSTRATE_MASTER;
			CALCULATE_GST_ON_GSTRATE_MASTER = SysParamUtil.isAllowed(CALCULATE_GST_ON_GSTRATE_MASTER_PARAM);
		}
		return CALCULATE_GST_ON_GSTRATE_MASTER;
	}

	public static Map<String, BigDecimal> getTaxPercentages(FinanceMain fm) {
		Map<String, BigDecimal> taxPercentages = fm.getTaxPercentages();

		if (MapUtils.isNotEmpty(taxPercentages)) {
			return taxPercentages;
		}

		taxPercentages = getTaxPercentages(fm.getFinID());
		fm.getTaxPercentages().putAll(taxPercentages);

		return taxPercentages;
	}

	/**
	 * This method will return the GST percentages by executing the GST rules configured.
	 * 
	 * @param finID
	 * @return The GST percentages MAP
	 */
	public static Map<String, BigDecimal> getTaxPercentages(long finID) {
		Map<String, BigDecimal> gstPercentages = new HashMap<>();

		gstPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		Map<String, Object> dataMap = getGSTDataMap(finID);

		String finCCY = (Object) dataMap.get("FinCCY") == null ? "" : String.valueOf((Object) dataMap.get("FinCCY"));

		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;

		if (isGSTCalculationOnMaster()) {
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
							gstPercentages.put(RuleConstants.CODE_TOTAL_GST, taxPerc);
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
		taxPercMap.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_TOTAL_AMOUNT_INCLUDINGGST, BigDecimal.ZERO);

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
			} else if (StringUtils.equals(RuleConstants.CODE_CESS, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CESS, taxPerc);
			}
		}
		taxPercMap.put(RuleConstants.CODE_TOTAL_GST, totalTaxPerc);

		return taxPercMap;
	}

	public static Map<String, Object> getGSTDataMap(long finID, FinanceTaxDetail taxDetail) {
		if (taxDetail == null) {
			taxDetail = financeTaxDetailDAO.getFinanceTaxDetailForLMSEvent(finID);
		}

		Map<String, Object> dataMap = financeMainDAO.getGSTDataMap(finID, TableType.MAIN_TAB);

		String finBranch = (String) dataMap.computeIfAbsent("FinBranch", ft -> "");
		String custBranch = (String) dataMap.computeIfAbsent("CustBranch", ft -> "");
		String custProvince = (String) dataMap.computeIfAbsent("CustProvince", ft -> "");
		String custCountry = (String) dataMap.computeIfAbsent("CustCountry", ft -> "");
		String custResdSts = (Object) dataMap.get("ResidentialStatus") == null ? ""
				: String.valueOf((Object) dataMap.get("ResidentialStatus"));

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custResdSts, custCountry, taxDetail, null);

		dataMap.put("custResidentialSts", custResdSts);
		return dataMap;
	}

	public static Map<String, Object> getGSTDataMap(long finID) {
		Map<String, Object> dataMap = financeMainDAO.getGSTDataMap(finID, TableType.MAIN_TAB);

		FinanceTaxDetail financeTaxDetail = null;
		if (MapUtils.isEmpty(dataMap)) {
			dataMap = financeMainDAO.getGSTDataMap(finID, TableType.TEMP_TAB);

			financeTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_View");
		} else {
			financeTaxDetail = financeTaxDetailDAO.getFinanceTaxDetailForLMSEvent(finID);
		}

		String finBranch = (String) dataMap.computeIfAbsent("FinBranch", ft -> "");
		String custBranch = (String) dataMap.computeIfAbsent("CustBranch", ft -> "");
		String custProvince = (String) dataMap.computeIfAbsent("CustProvince", ft -> "");
		String custCountry = (String) dataMap.computeIfAbsent("CustCountry", ft -> "");
		String custResdSts = (Object) dataMap.get("ResidentialStatus") == null ? ""
				: String.valueOf((Object) dataMap.get("ResidentialStatus"));

		Long custId = financeMainDAO.getCustomerIdByFinID(finID);

		List<GSTDetail> gstDetails = new ArrayList<>();
		if (custId != null) {
			gstDetails = gstDetailDAO.getGSTDetailById(custId, "_View");
		}

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custResdSts, custCountry, financeTaxDetail,
				gstDetails);
		// setting the customer residential status
		dataMap.put("custResidentialSts", custResdSts);
		return dataMap;
	}

	public static Map<String, Object> getGSTDataMap(String finBranch, String custBranch, String custState,
			String custResdType, String custCountry, FinanceTaxDetail taxDetail, List<GSTDetail> gstDetails) {

		Map<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;

		if (StringUtils.isBlank(custBranch)) {
			return gstExecutionMap;
		}
		// getting empty string for some cases
		if (StringUtils.isEmpty(finBranch)) {
			finBranch = custBranch;
		}

		Branch branch = deriveGSTBranch(finBranch);

		if (branch != null) {
			Province fromState = provinceDAO.getProvinceById(branch.getBranchCountry(), branch.getBranchProvince(), "");

			if (fromState != null) {
				gstExecutionMap.put("fromState", fromState.getCPProvince());
				gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
			}
		}

		String toStateCode = "";
		String toCountryCode = "";
		boolean sezCustomer = false;
		boolean gstNumber = true;

		if (taxDetail != null && StringUtils.isNotBlank(taxDetail.getApplicableFor())
				&& !PennantConstants.List_Select.equals(taxDetail.getApplicableFor())
				&& StringUtils.isNotBlank(taxDetail.getProvince()) && StringUtils.isNotBlank(taxDetail.getCountry())) {
			toStateCode = taxDetail.getProvince();
			toCountryCode = taxDetail.getCountry();
			gstExempted = taxDetail.isTaxExempted();
			if (StringUtils.isNotBlank(taxDetail.getSezCertificateNo())) {
				sezCustomer = true;
			}
			if (StringUtils.isNotBlank(taxDetail.getTaxNumber())) {
				gstNumber = false;
			}
		} else {
			if (CollectionUtils.isNotEmpty(gstDetails)) {
				for (GSTDetail gstDetail : gstDetails) {
					if (gstDetail.isDefaultGST()) {
						toStateCode = gstDetail.getStateCode();
						toCountryCode = gstDetail.getCountryCode();
						break;
					} else {
						toStateCode = custState;
						toCountryCode = custCountry;
					}
				}
			} else {
				toStateCode = custState;
				toCountryCode = custCountry;
			}
		}

		if (StringUtils.isBlank(toCountryCode) || StringUtils.isBlank(toStateCode)) {
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
		gstExecutionMap.put("residentialStatus", custResdType);
		gstExecutionMap.put("customerUnRegistered", gstNumber);

		return gstExecutionMap;
	}

	public static Map<String, Object> getDealerGSTDataMap(long dealerId, String userBranch, String finBranch) {
		Map<String, Object> dataMap = new HashMap<>();
		String custBranch = null;
		String custProvince = null;
		String custCountry = null;
		String custResdSts = null;

		if (dealerId > 0) {
			dataMap = financeMainDAO.getGSTDataMapForDealer(dealerId);

			if (dataMap.get("CustProvince") != null) {
				custProvince = dataMap.get("CustProvince").toString();
			}

			if (dataMap.get("CustCountry") != null) {
				custCountry = dataMap.get("CustCountry").toString();
			}

			dataMap.put("ResidentialStatus", PennantConstants.RESIDENT);// Default Value
		} else {
			custBranch = userBranch;
		}

		if (StringUtils.isNotBlank(finBranch)) {
			dataMap = getGSTDataMap(finBranch, custBranch, custProvince, custResdSts, custCountry, null, null);
		}

		return dataMap;
	}

	public static Map<String, BigDecimal> getManufacMerchTaxPercentages(long finID, String finBranch,
			ExtendedFieldRender aExtendedFieldRender, String type) {
		Map<String, BigDecimal> gstPercentages = new HashMap<>();

		gstPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		Map<String, Object> dataMap = getGSTDataMap(finID, finBranch, aExtendedFieldRender, type);

		String finCCY = (Object) dataMap.get("FinCCY") == null ? "" : String.valueOf((Object) dataMap.get("FinCCY"));

		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;

		if (isGSTCalculationOnMaster()) {
			if (dataMap.containsKey("fromState") && dataMap.containsKey("toState")) {
				String fromState = (String) dataMap.get("fromState");
				String toState = (String) dataMap.get("toState");

				if (StringUtils.isNotBlank(fromState) && StringUtils.isNotBlank(toState)) {
					List<GSTRate> gstRateDetailList = gstRateDAO.getGSTRateByStates(fromState, toState, "_AView");

					if (CollectionUtils.isNotEmpty(gstRateDetailList)) {
						for (GSTRate gstRate : gstRateDetailList) {
							BigDecimal taxPerc = gstRate.getPercentage();
							totalGST = totalGST.add(taxPerc);
							gstPercentages.put(RuleConstants.CODE_TOTAL_GST, taxPerc);
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
		gstPercentages.put("TOTALGST", totalGST);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return gstPercentages;
	}

	public static Map<String, Object> getGSTDataMap(long finID, String finBranch,
			ExtendedFieldRender aExtendedFieldRender, String type) {

		Map<String, Object> dataMap = null;
		if (type.equals("DBD")) {
			long mId = Long.valueOf(aExtendedFieldRender.getMapValues().get("MID").toString());
			dataMap = merchantDetailsDAO.getGSTDataMapForMerch(mId);
		} else if (type.equals("MBD")) {
			long oEMID = Long.valueOf(aExtendedFieldRender.getMapValues().get("OEMID").toString());
			dataMap = manufacturerDAO.getGSTDataMapForManufac(oEMID);
		}

		String custBranch = (Object) dataMap.get("CustBranch") == null ? ""
				: String.valueOf((Object) dataMap.get("CustBranch"));
		String custProvince = (Object) dataMap.get("CustProvince") == null ? ""
				: String.valueOf((Object) dataMap.get("CustProvince"));
		String custCountry = (Object) dataMap.get("CustCountry") == null ? ""
				: String.valueOf((Object) dataMap.get("CustCountry"));

		FinanceTaxDetail financeTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_View");

		dataMap = getGSTDataMap(finBranch, custBranch, custProvince, null, custCountry, financeTaxDetail, null);
		return dataMap;
	}

	private static Branch deriveGSTBranch(String finBranch) {
		if (GST_DEFAULT_FROM_STATE_PARAM == null) {
			GST_DEFAULT_FROM_STATE_PARAM = SMTParameterConstants.GST_DEFAULT_FROM_STATE;
			GST_DEFAULT_FROM_STATE = SysParamUtil.isAllowed(GST_DEFAULT_FROM_STATE_PARAM);
			if (GST_DEFAULT_FROM_STATE) {
				if (GST_DEFAULT_STATE_CODE == null) {
					GST_DEFAULT_STATE_CODE = SysParamUtil
							.getValueAsString(SMTParameterConstants.GST_DEFAULT_STATE_CODE);
				}
			}
		}

		if (GST_DEFAULT_FROM_STATE) {
			finBranch = GST_DEFAULT_STATE_CODE;
		}

		return branchDAO.getBranchById(finBranch, "");
	}

	private static BigDecimal getRuleResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = RuleExecutionUtil.executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
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

	public static BigDecimal calGstTaxAmount(BigDecimal actTaxAmount, BigDecimal gstPerc, BigDecimal totalGSTPerc) {
		BigDecimal gstAmount = BigDecimal.ZERO;

		if (gstPerc.compareTo(BigDecimal.ZERO) > 0) {
			gstAmount = (actTaxAmount.multiply(gstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			gstAmount = CalculationUtil.roundAmount(gstAmount, getTaxRoundingMode(), getTaxRoundingTarget());
		}

		return gstAmount;
	}

	public static Map<String, Object> getDealerGSTDataMap(String finBranch, String custBranch, String custState,
			String custResdType, String custCountry, FinanceTaxDetail td) {

		Map<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;

		if (StringUtils.isBlank(custBranch)) {
			return gstExecutionMap;
		}

		if (finBranch == null) {
			finBranch = custBranch;
		}

		Branch branch = deriveGSTBranch(finBranch);

		Province toState = provinceDAO.getProvinceById(branch.getBranchCountry(), branch.getBranchProvince(), "");

		if (toState != null) {
			gstExecutionMap.put("toState", toState.getCPProvince());
			gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
			gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
		}

		String fromStateCode = "";
		String fromCountryCode = "";
		boolean sezCustomer = false;
		boolean gstNumber = true;

		if (td != null && StringUtils.isNotBlank(td.getApplicableFor())
				&& !PennantConstants.List_Select.equals(td.getApplicableFor())
				&& StringUtils.isNotBlank(td.getProvince()) && StringUtils.isNotBlank(td.getCountry())) {
			fromStateCode = td.getProvince();
			fromCountryCode = td.getCountry();
			gstExempted = td.isTaxExempted();

			if (StringUtils.isNotBlank(td.getSezCertificateNo())) {
				sezCustomer = true;
			}
			if (StringUtils.isNotBlank(td.getTaxNumber())) {
				gstNumber = false;
			}
		} else {
			fromStateCode = custState;
			fromCountryCode = custCountry;
		}

		if (StringUtils.isBlank(fromCountryCode) || StringUtils.isBlank(fromStateCode)) { // if toCountry is not
																							// available
			gstExecutionMap.put("toState", "");
			gstExecutionMap.put("toUnionTerritory", 2);
			gstExecutionMap.put("toStateGstExempted", "");
		} else {
			Province fromState = provinceDAO.getProvinceById(fromCountryCode, fromStateCode, "");

			if (fromState == null) {
				gstExecutionMap.put("fromState", "");
				gstExecutionMap.put("fromUnionTerritory", 2);
				gstExecutionMap.put("fromStateGstExempted", "");
			} else {
				gstExecutionMap.put("fromState", fromState.getCPProvince());
				gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
			}
		}

		gstExecutionMap.put("gstExempted", gstExempted);
		gstExecutionMap.put("sezCertificateNo", sezCustomer);
		gstExecutionMap.put("residentialStatus", custResdType);
		gstExecutionMap.put("customerUnRegistered", gstNumber);

		return gstExecutionMap;
	}

	public static void calculateActualGST(FeeWaiverDetail detail, TaxAmountSplit taxSplit,
			Map<String, BigDecimal> gstPercentages) {
		if (taxSplit == null) {
			return;
		}

		if (detail == null) {
			return;
		}

		TaxAmountSplit tas = null;

		String taxComponent = detail.getTaxComponent();
		BigDecimal taxableAmt = detail.getAdviseAmount().add(detail.getWaivedAmount());

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			tas = getExclusiveGST(taxableAmt, gstPercentages);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			tas = getInclusiveGST(taxableAmt, gstPercentages);
		}

		BigDecimal diffGST = BigDecimal.ZERO;

		BigDecimal payableGST = taxSplit.gettGST().add(detail.getWaiverGST());

		if (payableGST.compareTo(tas.gettGST()) > 0) {
			diffGST = payableGST.subtract(tas.gettGST());
			taxSplit.settGST(taxSplit.gettGST().subtract(diffGST));
			taxSplit.setNetAmount(taxSplit.getNetAmount().subtract(diffGST));
		}

		if (diffGST.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		BigDecimal waivedTaxableAmt = detail.getWaivedAmount().add(detail.getWaiverGST());
		TaxAmountSplit paidGSTSplit = getInclusiveGST(waivedTaxableAmt, gstPercentages);

		BigDecimal prvCGst = paidGSTSplit.getcGST();
		BigDecimal prvSGst = paidGSTSplit.getsGST();
		BigDecimal prvIGst = paidGSTSplit.getiGST();
		BigDecimal prvUGst = paidGSTSplit.getuGST();
		BigDecimal prvCess = paidGSTSplit.getCess();

		BigDecimal diffCGST = taxSplit.getcGST().add(prvCGst).subtract(tas.getcGST());
		BigDecimal diffSGST = taxSplit.getsGST().add(prvSGst).subtract(tas.getsGST());
		BigDecimal diffIGST = taxSplit.getiGST().add(prvIGst).subtract(tas.getiGST());
		BigDecimal diffUGST = taxSplit.getuGST().add(prvUGst).subtract(tas.getuGST());
		BigDecimal diffCESS = taxSplit.getCess().add(prvCess).subtract(tas.getCess());

		taxSplit.setcGST(taxSplit.getcGST().subtract(diffCGST));
		taxSplit.setsGST(taxSplit.getsGST().subtract(diffSGST));
		taxSplit.setiGST(taxSplit.getiGST().subtract(diffIGST));
		taxSplit.setuGST(taxSplit.getuGST().subtract(diffUGST));
		taxSplit.setCess(taxSplit.getCess().subtract(diffCESS));
	}

	public static TaxAmountSplit calculateGST(FinanceDetail fd, String taxType, BigDecimal paidAmount) {
		return calculateGST(fd, taxType, paidAmount, BigDecimal.ZERO);
	}

	public static TaxAmountSplit calculateGST(FinanceDetail fd, String taxType, BigDecimal paidAmount,
			BigDecimal waivedAmount) {

		TaxAmountSplit taxSplit = new TaxAmountSplit();
		taxSplit.setNetAmount(paidAmount);
		if (StringUtils.isBlank(taxType)) {
			return taxSplit;
		}

		Map<String, BigDecimal> gstPercentages = fd.getGstPercentages();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (gstPercentages.isEmpty()) {
			Map<String, Object> dataMap = getGSTDataMap(fm.getFinID());
			gstPercentages = getTaxPercentages(dataMap, fm.getFinReference());
			fd.setGstPercentages(gstPercentages);
		}

		return calculateGST(gstPercentages, taxType, paidAmount, waivedAmount);
	}

	public static TaxAmountSplit calculateGST(long finID, String finCcy, String taxType, BigDecimal paidAmount) {
		return calculateGST(finID, finCcy, taxType, paidAmount, BigDecimal.ZERO);
	}

	public static TaxAmountSplit calculateGST(long finID, String finCcy, String taxType, BigDecimal paidAmount,
			BigDecimal waivedAmount) {

		if (StringUtils.isBlank(taxType)) {
			return new TaxAmountSplit();
		}

		Map<String, Object> dataMap = getGSTDataMap(finID);
		Map<String, BigDecimal> gstPercentages = getTaxPercentages(dataMap, finCcy);

		return calculateGST(gstPercentages, taxType, paidAmount, waivedAmount);
	}

	public static TaxAmountSplit calculateGST(Map<String, BigDecimal> gstPercentages, String taxType,
			BigDecimal paidAmount) {
		return calculateGST(gstPercentages, taxType, paidAmount, BigDecimal.ZERO);
	}

	private static TaxAmountSplit calculateGST(Map<String, BigDecimal> gstPercentages, String taxType,
			BigDecimal paidAmount, BigDecimal waivedAmount) {

		if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return new TaxAmountSplit();
		}

		if (gstPercentages.get(RuleConstants.CODE_TOTAL_GST) == BigDecimal.ZERO) {
			return new TaxAmountSplit();
		}

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			return getInclusiveGST(gstPercentages, paidAmount, waivedAmount);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			return getExclusiveGST(gstPercentages, paidAmount, waivedAmount);
		}

		return new TaxAmountSplit();
	}

	private static TaxAmountSplit getExclusiveGST(Map<String, BigDecimal> taxPercMap, BigDecimal paidAmount,
			BigDecimal waivedAmount) {
		BigDecimal taxableAmount = paidAmount.subtract(waivedAmount);

		TaxAmountSplit taxSplit = new TaxAmountSplit();

		taxSplit.setAmount(paidAmount);
		taxSplit.setWaivedAmount(waivedAmount);
		taxSplit.setcGST(getExclusiveTax(taxableAmount, taxPercMap.get(RuleConstants.CODE_CGST)));
		taxSplit.setsGST(getExclusiveTax(taxableAmount, taxPercMap.get(RuleConstants.CODE_SGST)));
		taxSplit.setuGST(getExclusiveTax(taxableAmount, taxPercMap.get(RuleConstants.CODE_UGST)));
		taxSplit.setiGST(getExclusiveTax(taxableAmount, taxPercMap.get(RuleConstants.CODE_IGST)));
		taxSplit.setCess(getExclusiveTax(taxableAmount, taxPercMap.get(RuleConstants.CODE_CESS)));
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST()).add(taxSplit.getiGST())
				.add(taxSplit.getCess()));
		taxSplit.setNetAmount(taxSplit.getAmount().add(taxSplit.gettGST()));

		return taxSplit;
	}

	private static TaxAmountSplit getInclusiveGST(Map<String, BigDecimal> taxPercMap, BigDecimal paidAmount,
			BigDecimal waivedAmount) {
		BigDecimal taxableAmount = paidAmount.subtract(waivedAmount);
		BigDecimal netAmount = getInclusiveAmount(taxableAmount, taxPercMap.get(RuleConstants.CODE_TOTAL_GST));

		TaxAmountSplit taxSplit = new TaxAmountSplit();

		taxSplit.setAmount(paidAmount);
		taxSplit.setWaivedAmount(waivedAmount);
		taxSplit.setcGST(getExclusiveTax(netAmount, taxPercMap.get(RuleConstants.CODE_CGST)));
		taxSplit.setsGST(getExclusiveTax(netAmount, taxPercMap.get(RuleConstants.CODE_SGST)));
		taxSplit.setuGST(getExclusiveTax(netAmount, taxPercMap.get(RuleConstants.CODE_UGST)));
		taxSplit.setiGST(getExclusiveTax(netAmount, taxPercMap.get(RuleConstants.CODE_IGST)));
		taxSplit.setCess(getExclusiveTax(netAmount, taxPercMap.get(RuleConstants.CODE_CESS)));
		taxSplit.settGST(taxSplit.getcGST().add(taxSplit.getsGST()).add(taxSplit.getuGST()).add(taxSplit.getiGST())
				.add(taxSplit.getCess()));
		taxSplit.setNetAmount(netAmount.add(taxSplit.gettGST()));

		if (netAmount.add(taxSplit.gettGST()).compareTo(taxableAmount) != 0) {
			BigDecimal diff = taxableAmount.subtract(netAmount.add(taxSplit.gettGST()));
			taxSplit.setNetAmount(taxSplit.getNetAmount().add(diff));
		}

		return taxSplit;
	}

	/**
	 * This method will return the GST percentages by executing the GST rules configured.
	 * 
	 * @param finReference
	 * @return The GST percentages MAP
	 */
	public static Map<String, BigDecimal> getTaxPercentages(FinServiceInstruction fsi) {
		Map<String, BigDecimal> gstPercentages = new HashMap<>();

		gstPercentages.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_CESS, BigDecimal.ZERO);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, BigDecimal.ZERO);

		long finID = fsi.getFinID();
		String finReference = fsi.getFinReference();

		Map<String, Object> dataMap = getGSTDataMap(finID);

		Object finCcy = (Object) dataMap.get("FinCCY");
		String finCCY = finCcy == null ? "" : String.valueOf(finCcy);

		String fromState = null;
		String toState = null;
		if (finReference == null) {
			fromState = fsi.getFromState();

			if (SysParamUtil.isAllowed(SMTParameterConstants.GST_DEFAULT_FROM_STATE)) {
				String defaultFinBranch = SysParamUtil.getValueAsString(SMTParameterConstants.GST_DEFAULT_STATE_CODE);
				Branch branch = branchDAO.getBranchById(defaultFinBranch, "");
				Province province = provinceDAO.getProvinceById(branch.getBranchCountry(), branch.getBranchProvince(),
						"");
				fromState = province.getCPProvince();
			}

			dataMap.put("fromState", fromState);
			dataMap.put("toState", fsi.getToState());
		}

		if (dataMap.containsKey("fromState")) {
			Object fromStateVal = dataMap.get("fromState");
			if (fromStateVal != null && StringUtils.isBlank(fromStateVal.toString())) {
				dataMap.put("fromState", "");
			}
		} else {
			dataMap.put("fromState", "");
		}

		if (dataMap.containsKey("toUnionTerritory")) {
			Object toUnionTerritory = dataMap.get("toUnionTerritory");
			if (toUnionTerritory != null && StringUtils.isBlank(toUnionTerritory.toString())) {
				dataMap.put("toUnionTerritory", false);
			}
		} else {
			dataMap.put("toUnionTerritory", false);
		}

		if (dataMap.containsKey("toState")) {
			Object toStateVal = dataMap.get("toState");
			if (toStateVal != null && StringUtils.isBlank(toStateVal.toString())) {
				dataMap.put("toState", "");
			}
		} else {
			dataMap.put("toState", "");
		}

		if (dataMap.containsKey("fromUnionTerritory")) {
			Object fromUnionTerritory = dataMap.get("fromUnionTerritory");
			if (fromUnionTerritory != null && StringUtils.isBlank(fromUnionTerritory.toString())) {
				dataMap.put("fromUnionTerritory", false);
			}
		} else {
			dataMap.put("fromUnionTerritory", false);
		}

		if (dataMap.containsKey("toState")) {
			Object toStateVal = dataMap.get("toState");
			if (toStateVal != null && StringUtils.isBlank(toStateVal.toString())) {
				dataMap.put("toState", "");
			}
		} else {
			dataMap.put("toState", "");
		}

		toState = (String) dataMap.get("toState");

		dataMap = getGSTDataMap(fsi.getFromBranch(), fsi.getToBranch(), toState, "", "IN", null, null);

		String ruleCode;
		BigDecimal totalGST = BigDecimal.ZERO;

		if (SysParamUtil.isAllowed(SMTParameterConstants.CALCULATE_GST_ON_GSTRATE_MASTER)) {
			totalGST = BigDecimal.ZERO;

			if (dataMap.containsKey("fromState") && dataMap.containsKey("toState")) {
				fromState = (String) dataMap.get("fromState");
				toState = (String) dataMap.get("toState");

				if (StringUtils.isNotBlank(fromState) && StringUtils.isNotBlank(toState)) {
					List<GSTRate> gstRateDetailList = gstRateDAO.getGSTRateByStates(fromState, toState, "_AView");

					if (CollectionUtils.isNotEmpty(gstRateDetailList)) {
						for (GSTRate gstRate : gstRateDetailList) {
							BigDecimal taxPerc = gstRate.getPercentage();
							totalGST = totalGST.add(taxPerc);
							gstPercentages.put(RuleConstants.CODE_TOTAL_GST, taxPerc);
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

		gstPercentages.put("TOTALGST", totalGST);
		gstPercentages.put(RuleConstants.CODE_TOTAL_GST, totalGST);

		return gstPercentages;
	}

	public static TaxHeader prepareTaxHeader(FinanceMain fm, String taxType, BigDecimal dueAmount) {
		Map<String, BigDecimal> taxes = getTaxPercentages(fm);

		TaxHeader taxHeader = new TaxHeader();
		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RCD_ADD);
		taxHeader.setVersion(taxHeader.getVersion() + 1);
		taxHeader.setTaxDetails(new ArrayList<>());

		Taxes cgstTax = getTaxDetail(RuleConstants.CODE_CGST, taxes.get(RuleConstants.CODE_CGST));
		taxHeader.getTaxDetails().add(cgstTax);

		Taxes sgstTax = getTaxDetail(RuleConstants.CODE_SGST, taxes.get(RuleConstants.CODE_SGST));
		taxHeader.getTaxDetails().add(sgstTax);

		Taxes igstTax = getTaxDetail(RuleConstants.CODE_IGST, taxes.get(RuleConstants.CODE_IGST));
		taxHeader.getTaxDetails().add(igstTax);

		Taxes ugstTax = getTaxDetail(RuleConstants.CODE_UGST, taxes.get(RuleConstants.CODE_UGST));
		taxHeader.getTaxDetails().add(ugstTax);

		Taxes cessTax = getTaxDetail(RuleConstants.CODE_CESS, taxes.get(RuleConstants.CODE_CESS));
		taxHeader.getTaxDetails().add(cessTax);

		TaxAmountSplit taxSplit = null;
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			taxSplit = GSTCalculator.getExclusiveGST(dueAmount, taxes);
		} else {
			taxSplit = GSTCalculator.getInclusiveGST(dueAmount, taxes);
		}

		cgstTax.setPaidTax(taxSplit.getcGST());
		sgstTax.setPaidTax(taxSplit.getsGST());
		igstTax.setPaidTax(taxSplit.getiGST());
		ugstTax.setPaidTax(taxSplit.getuGST());
		cessTax.setPaidTax(taxSplit.getCess());

		return taxHeader;
	}

	private static Taxes getTaxDetail(String taxType, BigDecimal taxPerc) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	public static BigDecimal getPaidTax(String taxType, List<Taxes> taxes) {
		for (Taxes tax : taxes) {
			if (tax.getTaxType().equals(taxType)) {
				return tax.getPaidTax();
			}
		}
		return BigDecimal.ZERO;
	}

	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		GSTCalculator.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setBranchDAO(BranchDAO branchDAO) {
		GSTCalculator.branchDAO = branchDAO;
	}

	@Autowired
	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		GSTCalculator.provinceDAO = provinceDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		GSTCalculator.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		GSTCalculator.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	@Autowired
	public void setGstRateDAO(GSTRateDAO gstRateDAO) {
		GSTCalculator.gstRateDAO = gstRateDAO;
	}

	@Autowired
	public void setMerchantDetailsDAO(MerchantDetailsDAO merchantDetailsDAO) {
		GSTCalculator.merchantDetailsDAO = merchantDetailsDAO;
	}

	@Autowired
	public void setManufacturerDAO(ManufacturerDAO manufacturerDAO) {
		GSTCalculator.manufacturerDAO = manufacturerDAO;
	}

	@Autowired
	public void setGstDetailDAO(GSTDetailDAO gstDetailDAO) {
		GSTCalculator.gstDetailDAO = gstDetailDAO;
	}

}
