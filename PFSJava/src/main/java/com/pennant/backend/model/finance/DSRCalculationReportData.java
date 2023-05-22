package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class DSRCalculationReportData implements Serializable {
	private static final long serialVersionUID = 8106498127614408765L;

	// customer details
	private String custName;
	private String custCRCPR;
	private String appDate;
	private String empStatus;
	private String jointCust;

	// finance details
	private String finReference;
	private String finType;
	private String finStartDate;
	private String maturityDate;
	private String finAmount;
	private String downPay;
	private String downPayBank;
	private String downPaySupl;
	private String firstRepay;
	private String lastRepay;
	private String effectiveRate;
	private String custDSR;
	private String lastInstallmentAmnt;
	private String totalFinAmount;
	private String totalGrcPft;
	private String totalRepayPft;
	private String totalProfit;
	private String numberOfTerms;
	private String graceTerms;
	private String totRepayAmount;
	private String netIncome;

	// grace period details
	private String allowGrace;
	private String grcPeriodEndDate;
	private String grcPftRate;
	private String grcPeriod;
	private String nextGrcPftDate;
	private String tenure;
	private String repayPftRate;
	private String repayFrq;
	private String nextrepayDate;
	private String incomeType;
	private String incomeAmount;

	List<Fee> feeList = new ArrayList<Fee>();

	public DSRCalculationReportData() {
	    super();
	}

	public class Fee {
		private String feeDesc;
		private String feeAmount;

		public Fee() {
		    super();
		}

		public void setFeeDesc(String feeDesc) {
			this.feeDesc = feeDesc;
		}

		public String getFeeDesc() {
			return feeDesc;
		}

		public void setFeeAmount(String feeAmount) {
			this.feeAmount = feeAmount;
		}

		public String getFeeAmount() {
			return feeAmount;
		}
	}

	ArrayList<Income> customerIncomeList = new ArrayList<Income>();
	ArrayList<Income> customerExpenseList = new ArrayList<Income>();

	public class Income {

		int formatter = 0;

		public Income(int ccyFormat) {
			formatter = ccyFormat;
		}

		private String category;
		private String incomeType;
		private String incAmount = PennantApplicationUtil.amountFormate(BigDecimal.ZERO, formatter);
		private String jointIncAmount = PennantApplicationUtil.amountFormate(BigDecimal.ZERO, formatter);

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getIncomeType() {
			return incomeType;
		}

		public void setIncomeType(String incomeType) {
			this.incomeType = incomeType;
		}

		public String getIncAmount() {
			return incAmount;
		}

		public void setIncAmount(String incAmount) {
			this.incAmount = incAmount;
		}

		public String getJointIncAmount() {
			return jointIncAmount;
		}

		public void setJointIncAmount(String jointIncAmount) {
			this.jointIncAmount = jointIncAmount;
		}

	}

	ArrayList<Eligibility> eligibilityList = new ArrayList<Eligibility>();

	public ArrayList<Eligibility> getEligibilityList() {
		return eligibilityList;
	}

	public void setEligibilityList(ArrayList<Eligibility> eligibilityList) {
		this.eligibilityList = eligibilityList;
	}

	public class Eligibility {

		private String ruleCode = "";
		private String description = "";
		private String eligibilityLimit = "";
		private String overrideEL = "";
		private String override = "";

		public Eligibility() {
		    super();
		}

		public String getRuleCode() {
			return ruleCode;
		}

		public void setRuleCode(String ruleCode) {
			this.ruleCode = ruleCode;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getEligibilityLimit() {
			return eligibilityLimit;
		}

		public void setEligibilityLimit(String eligibilityLimit) {
			this.eligibilityLimit = eligibilityLimit;
		}

		public String getOverrideEL() {
			return overrideEL;
		}

		public void setOverrideEL(String overrideEL) {
			this.overrideEL = overrideEL;
		}

		public String getOverride() {
			return override;
		}

		public void setOverride(String override) {
			this.override = override;
		}
	}

	ArrayList<Score> scoreList = new ArrayList<Score>();

	public ArrayList<Score> getScoreList() {
		return scoreList;
	}

	public void setScoreList(ArrayList<Score> scoreList) {
		this.scoreList = scoreList;
	}

	public class Score {

		private String scoringGroup = "";
		private String minScore = "";
		private String overrideScore = "";
		private String scoringMetric = "";
		private String description = "";
		private String maxScore = "";
		private String score = "";

		public Score() {
		    super();
		}

		public String getScoringGroup() {
			return scoringGroup;
		}

		public void setScoringGroup(String scoringGroup) {
			this.scoringGroup = scoringGroup;
		}

		public String getMinScore() {
			return minScore;
		}

		public void setMinScore(String minScore) {
			this.minScore = minScore;
		}

		public String getOverrideScore() {
			return overrideScore;
		}

		public void setOverrideScore(String overrideScore) {
			this.overrideScore = overrideScore;
		}

		public String getScoringMetric() {
			return scoringMetric;
		}

		public void setScoringMetric(String scoringMetric) {
			this.scoringMetric = scoringMetric;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getMaxScore() {
			return maxScore;
		}

		public void setMaxScore(String maxScore) {
			this.maxScore = maxScore;
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getDownPayBank() {
		return downPayBank;
	}

	public void setDownPayBank(String downPayBank) {
		this.downPayBank = downPayBank;
	}

	public String getDownPaySupl() {
		return downPaySupl;
	}

	public void setDownPaySupl(String downPaySupl) {
		this.downPaySupl = downPaySupl;
	}

	public String getFirstRepay() {
		return firstRepay;
	}

	public void setFirstRepay(String firstRepay) {
		this.firstRepay = firstRepay;
	}

	public String getEffectiveRate() {
		return effectiveRate;
	}

	public void setEffectiveRate(String effectiveRate) {
		this.effectiveRate = effectiveRate;
	}

	public String getCustDSR() {
		return custDSR;
	}

	public void setCustDSR(String custDSR) {
		this.custDSR = custDSR;
	}

	public String getLastInstallmentAmnt() {
		return lastInstallmentAmnt;
	}

	public void setLastInstallmentAmnt(String lastInstallmentAmnt) {
		this.lastInstallmentAmnt = lastInstallmentAmnt;
	}

	public String getTotalFinAmount() {
		return totalFinAmount;
	}

	public void setTotalFinAmount(String totalFinAmount) {
		this.totalFinAmount = totalFinAmount;
	}

	public String getTotalGrcPft() {
		return totalGrcPft;
	}

	public void setTotalGrcPft(String totalGrcPft) {
		this.totalGrcPft = totalGrcPft;
	}

	public String getTotalRepayPft() {
		return totalRepayPft;
	}

	public void setTotalRepayPft(String totalRepayPft) {
		this.totalRepayPft = totalRepayPft;
	}

	public void setTotalProfit(String totalProfit) {
		this.totalProfit = totalProfit;
	}

	public String getTotalProfit() {
		return totalProfit;
	}

	public String getAllowGrace() {
		return allowGrace;
	}

	public void setAllowGrace(String allowGrace) {
		this.allowGrace = allowGrace;
	}

	public String getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(String grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public String getGrcPftRate() {
		return grcPftRate;
	}

	public void setGrcPftRate(String grcPftRate) {
		this.grcPftRate = grcPftRate;
	}

	public String getGrcPeriod() {
		return grcPeriod;
	}

	public void setGrcPeriod(String grcPeriod) {
		this.grcPeriod = grcPeriod;
	}

	public String getNextGrcPftDate() {
		return nextGrcPftDate;
	}

	public void setNextGrcPftDate(String nextGrcPftDate) {
		this.nextGrcPftDate = nextGrcPftDate;
	}

	public String getTenure() {
		return tenure;
	}

	public void setTenure(String tenure) {
		this.tenure = tenure;
	}

	public String getRepayPftRate() {
		return repayPftRate;
	}

	public void setRepayPftRate(String repayPftRate) {
		this.repayPftRate = repayPftRate;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public String getNextrepayDate() {
		return nextrepayDate;
	}

	public void setNextrepayDate(String nextrepayDate) {
		this.nextrepayDate = nextrepayDate;
	}

	public String getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	public String getIncomeAmount() {
		return incomeAmount;
	}

	public void setIncomeAmount(String incomeAmount) {
		this.incomeAmount = incomeAmount;
	}

	public void setLastRepay(String lastRepay) {
		this.lastRepay = lastRepay;
	}

	public String getLastRepay() {
		return lastRepay;
	}

	public void setJointCust(String jointCust) {
		this.jointCust = jointCust;
	}

	public String getJointCust() {
		return jointCust;
	}

	public void setNumberOfTerms(String numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getNumberOfTerms() {
		return numberOfTerms;
	}

	public String getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(String graceTerms) {
		this.graceTerms = graceTerms;
	}

	public void setDownPay(String downPay) {
		this.downPay = downPay;
	}

	public String getDownPay() {
		return downPay;
	}

	public void setTotRepayAmount(String totRepayAmount) {
		this.totRepayAmount = totRepayAmount;
	}

	public String getTotRepayAmount() {
		return totRepayAmount;
	}

	public void setNetIncome(String netIncome) {
		this.netIncome = netIncome;
	}

	public String getNetIncome() {
		return netIncome;
	}

	public List<Fee> getFeeList() {
		return feeList;
	}

	public void setFeeList(List<Fee> feeList) {
		this.feeList = feeList;
	}

	public List<Income> getCustomerIncomeList() {
		return customerIncomeList;
	}

	public void setCustomerIncomeList(ArrayList<Income> customerIncomeList) {
		this.customerIncomeList = customerIncomeList;
	}

	public List<Income> getCustomerExpenseList() {
		return customerExpenseList;
	}

	public void setCustomerExpenseList(ArrayList<Income> customerExpenseList) {
		this.customerExpenseList = customerExpenseList;
	}

	/**
	 * Method for getting DSRCalculationReportData data
	 */
	public DSRCalculationReportData getDSRCalculationReportData(FinanceDetail detail) {

		DSRCalculationReportData reportData = new DSRCalculationReportData();
		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		// Customer Details
		reportData.setCustName(detail.getCustomer().getCustShrtName());
		reportData.setCustCRCPR(detail.getCustomer().getCustCRCPR());
		reportData.setAppDate(SysParamUtil.getAppDate(DateFormat.LONG_DATE));
		reportData.setEmpStatus(detail.getCustomer().getLovDescCustEmpStsName());
		reportData.setJointCust(detail.getCustomer().isJointCust() ? "True" : "False");

		// Finance Details
		reportData.setFinType(financeMain.getLovDescFinTypeName());
		reportData.setFinReference(financeMain.getFinReference());
		reportData.setFinStartDate(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		reportData.setMaturityDate(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
		reportData.setFinAmount(PennantApplicationUtil.amountFormate(financeMain.getFinAmount(), format));
		reportData.setDownPay(PennantApplicationUtil
				.amountFormate(financeMain.getDownPayBank().add(financeMain.getDownPaySupl()), format));
		reportData.setDownPayBank(PennantApplicationUtil.amountFormate(financeMain.getDownPayBank(), format));
		reportData.setDownPaySupl(PennantApplicationUtil.amountFormate(financeMain.getDownPaySupl(), format));
		reportData.setFirstRepay(PennantApplicationUtil.amountFormate(financeMain.getFirstRepay(), format));
		reportData.setLastRepay(PennantApplicationUtil.amountFormate(financeMain.getLastRepay(), format));
		reportData.setEffectiveRate(
				PennantApplicationUtil.formatRate(financeMain.getEffectiveRateOfReturn().doubleValue(), 2) + " %");

		BigDecimal dsr = BigDecimal.ZERO;
		if (detail.getCustomerEligibilityCheck() != null) {
			dsr = detail.getCustomerEligibilityCheck().getDSCR();
		}
		reportData.setCustDSR(PennantApplicationUtil.formatRate(dsr.doubleValue(), 2) + " %");

		reportData
				.setTotalFinAmount(PennantApplicationUtil.amountFormate(
						financeMain.getFinAmount()
								.add(financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO
										: financeMain.getFeeChargeAmt())
								.subtract(financeMain.getDownPayment()),
						format));
		reportData.setTotalGrcPft(PennantApplicationUtil.amountFormate(financeMain.getTotalGracePft(), format));
		reportData.setTotalRepayPft(PennantApplicationUtil
				.amountFormate(financeMain.getTotalProfit().subtract(financeMain.getTotalGracePft()), format));
		reportData.setTotalProfit(PennantApplicationUtil.amountFormate(financeMain.getTotalProfit(), format));
		reportData.setTotRepayAmount(PennantApplicationUtil.amountFormate(
				financeMain.getTotalProfit()
						.add(financeMain.getFinAmount()
								.add(financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO
										: financeMain.getFeeChargeAmt())
								.subtract(financeMain.getDownPayment())),
				format));
		reportData.setNumberOfTerms((financeMain.getNumberOfTerms() + financeMain.getGraceTerms()) + " Payments");

		reportData.setAllowGrace(financeMain.isAllowGrcPeriod() ? "True" : "False");
		reportData.setGrcPeriodEndDate(DateUtil.formatToLongDate(financeMain.getGrcPeriodEndDate()));
		reportData
				.setGrcPftRate(PennantApplicationUtil.formatRate(financeMain.getGrcPftRate().doubleValue(), 2) + " %");
		reportData.setGrcPeriod(FrequencyUtil.getFrequencyDetail(financeMain.getGrcPftFrq()).getFrequencyDescription());
		reportData.setNextGrcPftDate(DateUtil.formatToLongDate(financeMain.getNextGrcPftDate()));

		int months = DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());
		if (months > 0) {
			reportData.setTenure((financeMain.getNumberOfTerms()) + " Payments");
		}
		reportData.setGraceTerms(financeMain.getGraceTerms() + " Payments");

		if (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_R)) {

			BigDecimal totalFinAmt = financeMain.getFinAmount()
					.add(financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeMain.getFeeChargeAmt())
					.subtract(financeMain.getDownPayment());
			BigDecimal totalPft = financeMain.getTotalProfit();
			BigDecimal totalDays = new BigDecimal(
					DateUtil.getDaysBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate()));

			BigDecimal flatRate = ((totalPft.multiply(new BigDecimal(360))).divide(totalFinAmt.multiply(totalDays), 9,
					RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
			reportData.setRepayPftRate(PennantApplicationUtil.formatRate(flatRate.doubleValue(), 2) + " %");
		} else {
			reportData.setRepayPftRate(
					PennantApplicationUtil.formatRate(financeMain.getRepayProfitRate().doubleValue(), 2) + " %");
		}

		reportData.setRepayFrq(FrequencyUtil.getFrequencyDetail(financeMain.getRepayFrq()).getFrequencyDescription());
		reportData.setNextrepayDate(DateUtil.formatToLongDate(financeMain.getNextRepayDate()));

		// Prepare Fee Details
		List<FeeRule> feeRules = detail.getFinScheduleData().getFeeRules();
		List<Fee> fees = new ArrayList<DSRCalculationReportData.Fee>();
		if (feeRules != null && !feeRules.isEmpty()) {
			for (FeeRule feeRule : feeRules) {
				Fee fee = new Fee();
				fee.setFeeDesc(feeRule.getFeeCodeDesc());
				fee.setFeeAmount(PennantApplicationUtil.amountFormate(
						feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount()),
						format));

				fees.add(fee);
			}
		}
		reportData.setFeeList(fees);

		// Prepare Fee Details
		BigDecimal totalInc = BigDecimal.ZERO;
		List<CustomerIncome> incomes = detail.getCustomer().getCustomerIncomeList();
		Map<String, Income> incMap = new HashMap<String, Income>();
		Map<String, Income> expMap = new HashMap<String, Income>();

		if (incomes != null && !incomes.isEmpty()) {
			for (CustomerIncome income : incomes) {

				Income inc = new Income(format);
				if (incMap.containsKey("I_" + income.getCategory() + "_" + income.getIncomeType())) {
					inc = incMap.get("I_" + income.getCategory() + "_" + income.getIncomeType());
					if (!income.isJointCust()) {
						inc.setIncAmount(PennantApplicationUtil.amountFormate(income.getIncome(), format));
					} else {
						inc.setJointIncAmount(PennantApplicationUtil.amountFormate(income.getIncome(), format));
					}
				} else if (expMap.containsKey("E_" + income.getCategory() + "_" + income.getIncomeType())) {
					inc = expMap.get("E_" + income.getCategory() + "_" + income.getIncomeType());
					if (!income.isJointCust()) {
						inc.setIncAmount(PennantApplicationUtil.amountFormate(income.getIncome(), format));
					} else {
						inc.setJointIncAmount(PennantApplicationUtil.amountFormate(income.getIncome(), format));
					}
				} else {
					inc.setCategory(income.getCategoryDesc().trim());
					inc.setIncomeType(income.getIncomeTypeDesc());
					if (!income.isJointCust()) {
						inc.setIncAmount(PennantApplicationUtil.amountFormate(income.getIncome(), format));
					} else {
						inc.setJointIncAmount(PennantApplicationUtil.amountFormate(income.getIncome(), format));
					}

					if (income.getIncomeExpense().equals(PennantConstants.INCOME)) {
						incMap.put("I_" + income.getCategory() + "_" + income.getIncomeType(), inc);
					} else {
						expMap.put("E_" + income.getCategory() + "_" + income.getIncomeType(), inc);
					}
				}

				if (income.getIncomeExpense().equals(PennantConstants.INCOME)) {
					totalInc = totalInc.add(income.getIncome());
				}
			}
		}

		ArrayList<Income> list = new ArrayList<DSRCalculationReportData.Income>(incMap.values());
		reportData.setCustomerIncomeList(sortIncomeDetails(list));

		list = new ArrayList<DSRCalculationReportData.Income>(expMap.values());
		reportData.setCustomerExpenseList(sortIncomeDetails(list));
		reportData.setNetIncome(PennantApplicationUtil.amountFormate(totalInc, format));

		incMap = null;
		expMap = null;
		list = null;

		// Eligibility Details Preparation
		if (detail.getElgRuleList() != null && !detail.getElgRuleList().isEmpty()) {
			for (FinanceEligibilityDetail eligibility : detail.getElgRuleList()) {
				Eligibility elg = new Eligibility();
				elg.setRuleCode(eligibility.getLovDescElgRuleCode());
				elg.setDescription(eligibility.getLovDescElgRuleCodeDesc());

				if (RuleConstants.RETURNTYPE_DECIMAL.equals(eligibility.getRuleResultType())) {

					if ("E".equals(eligibility.getRuleResult())) {
						elg.setEligibilityLimit(Labels.getLabel("common.InSuffData"));
					} else if (PennantStaticListUtil.getConstElgRules().contains(eligibility.getLovDescElgRuleCode())) {
						String result = eligibility.getRuleResult();
						if (RuleConstants.ELGRULE_DSRCAL.equals(eligibility.getLovDescElgRuleCode())
								|| RuleConstants.ELGRULE_PDDSRCAL.equals(eligibility.getLovDescElgRuleCode())) {
							result = result.concat("%");
						}

						elg.setEligibilityLimit(result);
					} else {
						elg.setEligibilityLimit(PennantApplicationUtil
								.amountFormate(new BigDecimal(eligibility.getRuleResult()), format));
						elg.setOverrideEL(
								PennantApplicationUtil.amountFormate(eligibility.getOverrideResult(), format));
					}
				} else {
					if (eligibility.isEligible()) {
						elg.setEligibilityLimit(Labels.getLabel("common.Eligible"));
					} else {
						elg.setEligibilityLimit(Labels.getLabel("common.Ineligible"));
					}
				}

				elg.setOverride(eligibility.isUserOverride() ? "True" : "");
				eligibilityList.add(elg);
			}
			reportData.setEligibilityList(eligibilityList);
		}

		// Scoring Details Preparation
		if (detail.getFinScoreHeaderList() != null && !detail.getFinScoreHeaderList().isEmpty()) {
			for (FinanceScoreHeader header : detail.getFinScoreHeaderList()) {

				if (detail.getScoreDetailListMap() != null
						&& detail.getScoreDetailListMap().containsKey(header.getHeaderId())) {

					List<FinanceScoreDetail> metrics = detail.getScoreDetailListMap().get(header.getHeaderId());

					BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
					BigDecimal totalGrpExecScore = BigDecimal.ZERO;

					for (FinanceScoreDetail financeScoreDetail : metrics) {
						Score score = new Score();
						score.setScoringGroup(header.getGroupCodeDesc());
						score.setMinScore(String.valueOf(header.getMinScore()));
						score.setOverrideScore(String.valueOf(header.getOverrideScore()));

						score.setScoringMetric(financeScoreDetail.getRuleCode());
						score.setDescription(financeScoreDetail.getRuleCodeDesc());
						score.setMaxScore(String.valueOf(financeScoreDetail.getMaxScore()));
						score.setScore(String.valueOf(financeScoreDetail.getExecScore()));
						totalGrpMaxScore = totalGrpMaxScore.add(financeScoreDetail.getMaxScore());
						totalGrpExecScore = totalGrpExecScore.add(financeScoreDetail.getExecScore());

						scoreList.add(score);
					}

					Score grpTotScore = new Score();
					grpTotScore.setScoringGroup(header.getGroupCodeDesc());
					grpTotScore.setMinScore("");
					grpTotScore.setOverrideScore("");

					grpTotScore.setScoringMetric(Labels.getLabel("listcell_GrpTotalScr"));
					grpTotScore.setDescription("");
					grpTotScore.setMaxScore(String.valueOf(totalGrpMaxScore));
					grpTotScore.setScore(String.valueOf(totalGrpExecScore));
					scoreList.add(grpTotScore);
				}
			}
			reportData.setScoreList(scoreList);
		}

		return reportData;
	}

	public ArrayList<Income> sortIncomeDetails(ArrayList<Income> incomeDetails) {

		if (incomeDetails != null && incomeDetails.size() > 0) {
			Collections.sort(incomeDetails, new Comparator<Income>() {
				@Override
				public int compare(Income detail1, Income detail2) {
					if (detail1.getCategory().compareTo(detail2.getCategory()) > 0) {
						return 1;
					} else if (detail1.getCategory().compareTo(detail2.getCategory()) == 0) {
						if (detail1.getIncomeType().compareTo(detail2.getIncomeType()) > 0) {
							return 1;
						}
					}
					return 0;
				}
			});
		}

		return incomeDetails;
	}

}
