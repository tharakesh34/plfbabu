package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinMainReportData implements Serializable {

	private static final long serialVersionUID = -1716545536082000070L;

	// Basic Details
	private String finReference;
	private String finType;
	private String finStatus;
	private String finCcy;
	private String profitDaysBasis;
	private String custCIF;
	private String finBranch;
	private String finStartDate;
	private String finContractDate;
	private String finAmount;
	private String finRepaymentAmount;
	private String downPayBank;
	private String downPaySupl;
	private String defferments;
	private String planDeferCount;
	private String finPurpose;
	private String finCommitRef;
	private String overdueDays;
	private String finSuspense;

	// Grace period Details
	private String allowGrace;
	private String grcPeriodEndDate;
	private String grcRateBasis;
	private String grcPftRate;
	private String graceBaseRate;
	private String graceSpecialRate;
	private String grcMargin;
	private String gracePftFrq;
	private String nextGrcPftDate;
	private String grcPftRvwFrq;
	private String nextGrcPftRvwDate;
	private String grcCpzFrq;
	private String nextGrcCpzDate;
	private String allowGrcRepay;
	private String grcSchdMethod;
	private String graceTerms;

	// Repay Period Details
	private String numberOfTerms;
	private String reqRepayAmount;
	private String repayRateBasis;
	private String repayProfitRate;
	private String repayBaseRate;
	private String repaySpecialRate;
	private String repayMargin;
	private String scheduleMethod;
	private String repayPftFrq;
	private String nextRepayPftDate;
	private String repayRevFrq;
	private String nextRepayRvwDate;
	private String repayCpzFrq;
	private String nextRepayCpzDate;
	private String repayFrq;
	private String nextRepayDate;
	private String maturityDate;
	private String repayPftOnPftFrq;

	// Over Due Penalty Rate Details
	private String applyOverdue = "False";
	private String incGrcDays = "False";
	private String penaltyCalOn = "";
	private String grcDays = "";
	private String penaltyType = "";
	private String chargeAmt = "";
	private String alwWaiver = "";
	private String maxWaiver = "";

	// Finance Summary Details
	private String totalDisb = "";
	private String totalDownPayment = "";

	private String totalPriSchd = "";
	private String totalPftSchd = "";
	private String totalOriginal = "";

	private String outStandPrincipal = "";
	private String outStandProfit = "";
	private String totalOutStanding = "";

	private String schdPriPaid = "";
	private String schdPftPaid = "";
	private String totalPaid = "";

	private String unPaidPrincipal = "";
	private String unPaidProfit = "";
	private String totalUnPaid = "";

	private String overDuePrincipal = "";
	private String overDueProfit = "";
	private String totalOverDue = "";

	private String earnedPrincipal = "";
	private String earnedProfit = "";
	private String totalEarned = "";

	private String unEarnedPrincipal = "";
	private String unEarnedProfit = "";
	private String totalUnEarned = "";

	private String payOffPrincipal = "";
	private String payOffProfit = "";
	private String totalPayOff = "";

	// Totals
	private String totalFees = "";
	private String totalWaivers = "";
	private String finODTotPenaltyAmt = "";
	private String finODTotPenaltyPaid = "";
	private String totalPaidFees = "";
	private String finODTotWaived = "";
	private String finODTotPenaltyBal = "";

	// Finance Installment Details
	private String overDueInstlments = "";
	private String overDueInstlementPft = "";
	private String finProfitrate = "";
	private String paidInstlments = "";
	private String paidInstlementPft = "";
	private String unPaidInstlments = "";
	private String unPaidInstlementPft = "";

	// Flags
	private String applyDownPay = "False";
	private String applyDifferment = "False";
	private String applyRepayAccntId = "False";
	private String custShrtName;

	public FinMainReportData() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public String getFinStatus() {
		return finStatus;
	}

	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getFinContractDate() {
		return finContractDate;
	}

	public void setFinContractDate(String finContractDate) {
		this.finContractDate = finContractDate;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(String finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
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

	public String getDefferments() {
		return defferments;
	}

	public void setDefferments(String defferments) {
		this.defferments = defferments;
	}

	public String getPlanDeferCount() {
		return planDeferCount;
	}

	public void setPlanDeferCount(String planDeferCount) {
		this.planDeferCount = planDeferCount;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public String getFinCommitRef() {
		return finCommitRef;
	}

	public void setFinCommitRef(String finCommitRef) {
		this.finCommitRef = finCommitRef;
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

	public String getGrcRateBasis() {
		return grcRateBasis;
	}

	public void setGrcRateBasis(String grcRateBasis) {
		this.grcRateBasis = grcRateBasis;
	}

	public String getGrcPftRate() {
		return grcPftRate;
	}

	public void setGrcPftRate(String grcPftRate) {
		this.grcPftRate = grcPftRate;
	}

	public String getGraceBaseRate() {
		return graceBaseRate;
	}

	public void setGraceBaseRate(String graceBaseRate) {
		this.graceBaseRate = graceBaseRate;
	}

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
	}

	public String getGrcMargin() {
		return grcMargin;
	}

	public void setGrcMargin(String grcMargin) {
		this.grcMargin = grcMargin;
	}

	public String getGracePftFrq() {
		return gracePftFrq;
	}

	public void setGracePftFrq(String gracePftFrq) {
		this.gracePftFrq = gracePftFrq;
	}

	public String getNextGrcPftDate() {
		return nextGrcPftDate;
	}

	public void setNextGrcPftDate(String nextGrcPftDate) {
		this.nextGrcPftDate = nextGrcPftDate;
	}

	public String getGrcPftRvwFrq() {
		return grcPftRvwFrq;
	}

	public void setGrcPftRvwFrq(String grcPftRvwFrq) {
		this.grcPftRvwFrq = grcPftRvwFrq;
	}

	public String getNextGrcPftRvwDate() {
		return nextGrcPftRvwDate;
	}

	public void setNextGrcPftRvwDate(String nextGrcPftRvwDate) {
		this.nextGrcPftRvwDate = nextGrcPftRvwDate;
	}

	public String getGrcCpzFrq() {
		return grcCpzFrq;
	}

	public void setGrcCpzFrq(String grcCpzFrq) {
		this.grcCpzFrq = grcCpzFrq;
	}

	public String getNextGrcCpzDate() {
		return nextGrcCpzDate;
	}

	public void setNextGrcCpzDate(String nextGrcCpzDate) {
		this.nextGrcCpzDate = nextGrcCpzDate;
	}

	public String getAllowGrcRepay() {
		return allowGrcRepay;
	}

	public void setAllowGrcRepay(String allowGrcRepay) {
		this.allowGrcRepay = allowGrcRepay;
	}

	public String getGrcSchdMethod() {
		return grcSchdMethod;
	}

	public void setGrcSchdMethod(String grcSchdMethod) {
		this.grcSchdMethod = grcSchdMethod;
	}

	public String getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(String numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getReqRepayAmount() {
		return reqRepayAmount;
	}

	public void setReqRepayAmount(String reqRepayAmount) {
		this.reqRepayAmount = reqRepayAmount;
	}

	public String getRepayRateBasis() {
		return repayRateBasis;
	}

	public void setRepayRateBasis(String repayRateBasis) {
		this.repayRateBasis = repayRateBasis;
	}

	public String getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(String repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public String getRepayBaseRate() {
		return repayBaseRate;
	}

	public void setRepayBaseRate(String repayBaseRate) {
		this.repayBaseRate = repayBaseRate;
	}

	public String getRepaySpecialRate() {
		return repaySpecialRate;
	}

	public void setRepaySpecialRate(String repaySpecialRate) {
		this.repaySpecialRate = repaySpecialRate;
	}

	public String getRepayMargin() {
		return repayMargin;
	}

	public void setRepayMargin(String repayMargin) {
		this.repayMargin = repayMargin;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public String getRepayPftFrq() {
		return repayPftFrq;
	}

	public void setRepayPftFrq(String repayPftFrq) {
		this.repayPftFrq = repayPftFrq;
	}

	public String getNextRepayPftDate() {
		return nextRepayPftDate;
	}

	public void setNextRepayPftDate(String nextRepayPftDate) {
		this.nextRepayPftDate = nextRepayPftDate;
	}

	public String getRepayRevFrq() {
		return repayRevFrq;
	}

	public void setRepayRevFrq(String repayRevFrq) {
		this.repayRevFrq = repayRevFrq;
	}

	public String getNextRepayRvwDate() {
		return nextRepayRvwDate;
	}

	public void setNextRepayRvwDate(String nextRepayRvwDate) {
		this.nextRepayRvwDate = nextRepayRvwDate;
	}

	public String getRepayCpzFrq() {
		return repayCpzFrq;
	}

	public void setRepayCpzFrq(String repayCpzFrq) {
		this.repayCpzFrq = repayCpzFrq;
	}

	public String getNextRepayCpzDate() {
		return nextRepayCpzDate;
	}

	public void setNextRepayCpzDate(String nextRepayCpzDate) {
		this.nextRepayCpzDate = nextRepayCpzDate;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public String getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(String nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getRepayPftOnPftFrq() {
		return repayPftOnPftFrq;
	}

	public void setRepayPftOnPftFrq(String repayPftOnPftFrq) {
		this.repayPftOnPftFrq = repayPftOnPftFrq;
	}

	public String getApplyOverdue() {
		return applyOverdue;
	}

	public void setApplyOverdue(String applyOverdue) {
		this.applyOverdue = applyOverdue;
	}

	public String getIncGrcDays() {
		return incGrcDays;
	}

	public void setIncGrcDays(String incGrcDays) {
		this.incGrcDays = incGrcDays;
	}

	public String getPenaltyCalOn() {
		return penaltyCalOn;
	}

	public void setPenaltyCalOn(String penaltyCalOn) {
		this.penaltyCalOn = penaltyCalOn;
	}

	public String getGrcDays() {
		return grcDays;
	}

	public void setGrcDays(String grcDays) {
		this.grcDays = grcDays;
	}

	public String getPenaltyType() {
		return penaltyType;
	}

	public void setPenaltyType(String penaltyType) {
		this.penaltyType = penaltyType;
	}

	public String getChargeAmt() {
		return chargeAmt;
	}

	public void setChargeAmt(String chargeAmt) {
		this.chargeAmt = chargeAmt;
	}

	public String getAlwWaiver() {
		return alwWaiver;
	}

	public void setAlwWaiver(String alwWaiver) {
		this.alwWaiver = alwWaiver;
	}

	public String getMaxWaiver() {
		return maxWaiver;
	}

	public void setMaxWaiver(String maxWaiver) {
		this.maxWaiver = maxWaiver;
	}

	public String getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(String overdueDays) {
		this.overdueDays = overdueDays;
	}

	public String getFinSuspense() {
		return finSuspense;
	}

	public void setFinSuspense(String finSuspense) {
		this.finSuspense = finSuspense;
	}

	public String getTotalDisb() {
		return totalDisb;
	}

	public void setTotalDisb(String totalDisb) {
		this.totalDisb = totalDisb;
	}

	public String getTotalDownPayment() {
		return totalDownPayment;
	}

	public void setTotalDownPayment(String totalDownPayment) {
		this.totalDownPayment = totalDownPayment;
	}

	public String getTotalPriSchd() {
		return totalPriSchd;
	}

	public void setTotalPriSchd(String totalPriSchd) {
		this.totalPriSchd = totalPriSchd;
	}

	public String getTotalPftSchd() {
		return totalPftSchd;
	}

	public void setTotalPftSchd(String totalPftSchd) {
		this.totalPftSchd = totalPftSchd;
	}

	public String getTotalOriginal() {
		return totalOriginal;
	}

	public void setTotalOriginal(String totalOriginal) {
		this.totalOriginal = totalOriginal;
	}

	public String getOutStandPrincipal() {
		return outStandPrincipal;
	}

	public void setOutStandPrincipal(String outStandPrincipal) {
		this.outStandPrincipal = outStandPrincipal;
	}

	public String getOutStandProfit() {
		return outStandProfit;
	}

	public void setOutStandProfit(String outStandProfit) {
		this.outStandProfit = outStandProfit;
	}

	public String getTotalOutStanding() {
		return totalOutStanding;
	}

	public void setTotalOutStanding(String totalOutStanding) {
		this.totalOutStanding = totalOutStanding;
	}

	public String getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(String schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public String getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(String schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public String getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(String totalPaid) {
		this.totalPaid = totalPaid;
	}

	public String getUnPaidPrincipal() {
		return unPaidPrincipal;
	}

	public void setUnPaidPrincipal(String unPaidPrincipal) {
		this.unPaidPrincipal = unPaidPrincipal;
	}

	public String getUnPaidProfit() {
		return unPaidProfit;
	}

	public void setUnPaidProfit(String unPaidProfit) {
		this.unPaidProfit = unPaidProfit;
	}

	public String getTotalUnPaid() {
		return totalUnPaid;
	}

	public void setTotalUnPaid(String totalUnPaid) {
		this.totalUnPaid = totalUnPaid;
	}

	public String getOverDuePrincipal() {
		return overDuePrincipal;
	}

	public void setOverDuePrincipal(String overDuePrincipal) {
		this.overDuePrincipal = overDuePrincipal;
	}

	public String getOverDueProfit() {
		return overDueProfit;
	}

	public void setOverDueProfit(String overDueProfit) {
		this.overDueProfit = overDueProfit;
	}

	public String getTotalOverDue() {
		return totalOverDue;
	}

	public void setTotalOverDue(String totalOverDue) {
		this.totalOverDue = totalOverDue;
	}

	public String getEarnedPrincipal() {
		return earnedPrincipal;
	}

	public void setEarnedPrincipal(String earnedPrincipal) {
		this.earnedPrincipal = earnedPrincipal;
	}

	public String getEarnedProfit() {
		return earnedProfit;
	}

	public void setEarnedProfit(String earnedProfit) {
		this.earnedProfit = earnedProfit;
	}

	public String getTotalEarned() {
		return totalEarned;
	}

	public void setTotalEarned(String totalEarned) {
		this.totalEarned = totalEarned;
	}

	public String getUnEarnedPrincipal() {
		return unEarnedPrincipal;
	}

	public void setUnEarnedPrincipal(String unEarnedPrincipal) {
		this.unEarnedPrincipal = unEarnedPrincipal;
	}

	public String getUnEarnedProfit() {
		return unEarnedProfit;
	}

	public void setUnEarnedProfit(String unEarnedProfit) {
		this.unEarnedProfit = unEarnedProfit;
	}

	public String getTotalUnEarned() {
		return totalUnEarned;
	}

	public void setTotalUnEarned(String totalUnEarned) {
		this.totalUnEarned = totalUnEarned;
	}

	public String getPayOffPrincipal() {
		return payOffPrincipal;
	}

	public void setPayOffPrincipal(String payOffPrincipal) {
		this.payOffPrincipal = payOffPrincipal;
	}

	public String getPayOffProfit() {
		return payOffProfit;
	}

	public void setPayOffProfit(String payOffProfit) {
		this.payOffProfit = payOffProfit;
	}

	public String getTotalPayOff() {
		return totalPayOff;
	}

	public void setTotalPayOff(String totalPayOff) {
		this.totalPayOff = totalPayOff;
	}

	public String getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(String totalFees) {
		this.totalFees = totalFees;
	}

	public String getTotalWaivers() {
		return totalWaivers;
	}

	public void setTotalWaivers(String totalWaivers) {
		this.totalWaivers = totalWaivers;
	}

	public String getFinODTotPenaltyAmt() {
		return finODTotPenaltyAmt;
	}

	public void setFinODTotPenaltyAmt(String finODTotPenaltyAmt) {
		this.finODTotPenaltyAmt = finODTotPenaltyAmt;
	}

	public String getFinODTotPenaltyPaid() {
		return finODTotPenaltyPaid;
	}

	public void setFinODTotPenaltyPaid(String finODTotPenaltyPaid) {
		this.finODTotPenaltyPaid = finODTotPenaltyPaid;
	}

	public String getFinODTotWaived() {
		return finODTotWaived;
	}

	public void setFinODTotWaived(String finODTotWaived) {
		this.finODTotWaived = finODTotWaived;
	}

	public String getFinODTotPenaltyBal() {
		return finODTotPenaltyBal;
	}

	public void setFinODTotPenaltyBal(String finODTotPenaltyBal) {
		this.finODTotPenaltyBal = finODTotPenaltyBal;
	}

	public String getOverDueInstlments() {
		return overDueInstlments;
	}

	public void setOverDueInstlments(String overDueInstlments) {
		this.overDueInstlments = overDueInstlments;
	}

	public String getOverDueInstlementPft() {
		return overDueInstlementPft;
	}

	public void setOverDueInstlementPft(String overDueInstlementPft) {
		this.overDueInstlementPft = overDueInstlementPft;
	}

	public String getFinProfitrate() {
		return finProfitrate;
	}

	public void setFinProfitrate(String finProfitrate) {
		this.finProfitrate = finProfitrate;
	}

	public String getPaidInstlments() {
		return paidInstlments;
	}

	public void setPaidInstlments(String paidInstlments) {
		this.paidInstlments = paidInstlments;
	}

	public String getPaidInstlementPft() {
		return paidInstlementPft;
	}

	public void setPaidInstlementPft(String paidInstlementPft) {
		this.paidInstlementPft = paidInstlementPft;
	}

	public String getUnPaidInstlments() {
		return unPaidInstlments;
	}

	public void setUnPaidInstlments(String unPaidInstlments) {
		this.unPaidInstlments = unPaidInstlments;
	}

	public String getUnPaidInstlementPft() {
		return unPaidInstlementPft;
	}

	public void setUnPaidInstlementPft(String unPaidInstlementPft) {
		this.unPaidInstlementPft = unPaidInstlementPft;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	// Method for Preparing Finance Main Object for Report purpose
	public FinMainReportData getFinMainReportData(FinScheduleData finSchData, FinanceSummary finSummary) {
		FinMainReportData reportData = new FinMainReportData();
		FinanceMain financeMain = finSchData.getFinanceMain();
		FinanceSummary financeSummary = finSchData.getFinanceSummary();
		int ccyFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		// Finance Basic details
		reportData.setFinReference(financeMain.getFinReference());
		reportData.setFinType(financeMain.getFinType() + "-" + financeMain.getLovDescFinTypeName());

		if (financeMain.isFinIsActive()) {
			reportData.setFinStatus("Active");
		} else {
			if (FinanceConstants.CLOSE_STATUS_MATURED.equals(financeMain.getClosingStatus())) {
				reportData.setFinStatus("Matured");
			} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(financeMain.getClosingStatus())) {
				reportData.setFinStatus("Cancelled");
			} else if (financeMain.isWriteoffLoan()) {
				reportData.setFinStatus("Written-Off");
			}
		}

		reportData.setFinCcy(financeMain.getFinCcy());
		reportData.setProfitDaysBasis(financeMain.getProfitDaysBasis());
		// ###Release PFFV1.0.6 - Changed the customer short name to Full Name
		reportData.setCustCIF(financeMain.getLovDescCustCIF());
		reportData.setCustShrtName(financeMain.getLovDescCustShrtName());
		reportData.setFinBranch(financeMain.getFinBranch() + "-" + financeMain.getLovDescFinBranchName());
		reportData.setFinStartDate(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		reportData.setFinContractDate(DateUtil.formatToLongDate(financeMain.getFinContractDate()));
		reportData.setFinAmount(PennantApplicationUtil.amountFormate(financeMain.getFinAmount(), ccyFormatter));
		reportData.setFinRepaymentAmount(PennantApplicationUtil
				.amountFormate(financeMain.getFinAmount().subtract(financeMain.getFinRepaymentAmount()), ccyFormatter));
		reportData.setDownPayBank(PennantApplicationUtil.amountFormate(financeMain.getDownPayBank(), ccyFormatter));
		if (financeMain.getDownPayBank().compareTo(BigDecimal.ZERO) > 0) {
			reportData.setApplyDownPay("TRUE");
		}
		reportData.setDownPaySupl(PennantApplicationUtil.amountFormate(financeMain.getDownPaySupl(), ccyFormatter));
		reportData.setDefferments(String.valueOf(financeMain.getDefferments()));
		if (financeMain.getDefferments() != 0) {
			reportData.setApplyDifferment("TRUE");
		}
		reportData.setPlanDeferCount(String.valueOf(financeMain.getPlanDeferCount()));
		reportData.setFinPurpose(
				financeMain.getFinPurpose() + "-" + StringUtils.trimToEmpty(financeMain.getLovDescFinPurposeName()));
		reportData.setFinCommitRef(StringUtils.trimToEmpty(financeMain.getFinCommitmentRef()));
		reportData.setOverdueDays(financeMain.getFinStatus() + "/" + financeSummary.getFinCurODDays());

		if (finSchData.isFinPftSuspended()) {
			reportData.setFinSuspense("True(" + DateUtil.formatToLongDate(finSchData.getFinSuspDate()) + ")");
		} else {
			reportData.setFinSuspense("False");
		}

		// Grace Period Details
		reportData.setAllowGrace(financeMain.isAllowGrcPeriod() ? "TRUE" : "FALSE");
		if (financeMain.isAllowGrcPeriod()) {
			reportData.setGraceTerms(String.valueOf(financeMain.getGraceTerms()));
			reportData.setGrcPeriodEndDate(DateUtil.formatToLongDate(financeMain.getGrcPeriodEndDate()));
			reportData.setGrcRateBasis(
					"#".equals(StringUtils.trimToEmpty(financeMain.getGrcRateBasis())) ? financeMain.getGrcRateBasis()
							: "");
			reportData.setGrcPftRate(
					PennantApplicationUtil.formatRate(financeMain.getGrcPftRate().doubleValue(), 2) + " %");
			reportData.setGraceBaseRate(StringUtils.trimToEmpty(financeMain.getGraceBaseRate()));
			reportData.setGraceSpecialRate(StringUtils.trimToEmpty(financeMain.getGraceSpecialRate()));
			reportData.setGrcMargin(PennantApplicationUtil.formatRate(financeMain.getGrcMargin().doubleValue(), 2));
			reportData.setGracePftFrq(
					FrequencyUtil.getFrequencyDetail(FrequencyUtil.getFrequencyCode(financeMain.getGrcPftFrq()))
							.getFrequencyDescription());
			reportData.setNextGrcPftDate(DateUtil.formatToLongDate(financeMain.getNextGrcPftDate()));
			reportData.setGrcPftRvwFrq(
					FrequencyUtil.getFrequencyDetail(financeMain.getGrcPftRvwFrq()).getFrequencyDescription());
			reportData.setNextGrcPftRvwDate(DateUtil.formatToLongDate(financeMain.getNextGrcPftRvwDate()));
			reportData.setGrcCpzFrq(
					FrequencyUtil.getFrequencyDetail(financeMain.getGrcCpzFrq()).getFrequencyDescription());
			reportData.setNextGrcCpzDate(DateUtil.formatToLongDate(financeMain.getNextGrcCpzDate()));
			reportData.setAllowGrcRepay(financeMain.isAllowGrcRepay() ? "True" : "False");
			reportData.setGrcSchdMethod(
					"#".equals(StringUtils.trimToEmpty(financeMain.getGrcSchdMthd())) ? financeMain.getGrcSchdMthd()
							: "");
		}

		// Repay Details
		if (financeMain.getNOInst() > 0) {
			reportData.setNumberOfTerms(String.valueOf(financeMain.getNOInst()));
		} else {
			reportData.setNumberOfTerms(String.valueOf(financeMain.getCalTerms()));
		}
		reportData.setReqRepayAmount(
				PennantApplicationUtil.amountFormate(financeMain.getFinRepaymentAmount(), ccyFormatter));
		reportData.setRepayRateBasis("#".equals(financeMain.getRepayRateBasis()) ? ""
				: getlabelDesc(financeMain.getRepayRateBasis(), PennantStaticListUtil.getInterestRateType(false)));
		reportData.setRepayProfitRate(financeMain.getRepayProfitRate() != null
				? PennantApplicationUtil.formatRate(financeMain.getRepayProfitRate().doubleValue(), 2) + " %"
				: "");
		reportData.setRepayBaseRate(StringUtils.trimToEmpty(financeMain.getRepayBaseRate()));
		if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(financeMain.getRepayBaseRate()))) {
			reportData.setApplyRepayAccntId("TRUE");
		}
		reportData.setRepaySpecialRate(StringUtils.trimToEmpty(financeMain.getRepaySpecialRate()));
		reportData.setRepayMargin(PennantApplicationUtil.formatRate(financeMain.getRepayMargin().doubleValue(), 2));
		reportData.setScheduleMethod("#".equals(StringUtils.trimToEmpty(financeMain.getScheduleMethod())) ? ""
				: StringUtils.trimToEmpty(financeMain.getScheduleMethod()));
		reportData.setRepayPftFrq(
				FrequencyUtil.getFrequencyDetail(financeMain.getRepayPftFrq()).getFrequencyDescription());
		reportData.setNextRepayPftDate(DateUtil.formatToLongDate(financeMain.getNextRepayPftDate()));
		reportData.setRepayRevFrq(
				FrequencyUtil.getFrequencyDetail(financeMain.getRepayRvwFrq()).getFrequencyDescription());
		reportData.setNextRepayRvwDate(DateUtil.formatToLongDate(financeMain.getNextRepayRvwDate()));
		reportData.setRepayCpzFrq(
				FrequencyUtil.getFrequencyDetail(financeMain.getRepayCpzFrq()).getFrequencyDescription());
		reportData.setNextRepayCpzDate(DateUtil.formatToLongDate(financeMain.getNextRepayCpzDate()));
		reportData.setRepayFrq(FrequencyUtil.getFrequencyDetail(financeMain.getRepayFrq()).getFrequencyDescription());
		reportData.setNextRepayDate(DateUtil.formatToLongDate(financeMain.getNextRepayDate()));
		reportData.setMaturityDate(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
		reportData.setRepayPftOnPftFrq(financeMain.isFinRepayPftOnFrq() ? "True" : "False");

		// OverDue Charges Details
		if (finSchData.getFinODPenaltyRate() != null) {
			reportData.setApplyOverdue(finSchData.getFinODPenaltyRate().isApplyODPenalty() ? "True" : "False");
			reportData.setIncGrcDays(finSchData.getFinODPenaltyRate().isODIncGrcDays() ? "True" : "False");
			reportData.setPenaltyCalOn(finSchData.getFinODPenaltyRate().getODChargeCalOn());
			reportData.setGrcDays(String.valueOf(finSchData.getFinODPenaltyRate().getODGraceDays()));
			reportData.setPenaltyType(finSchData.getFinODPenaltyRate().getODChargeType());
			reportData.setChargeAmt(String.valueOf(finSchData.getFinODPenaltyRate().getODChargeAmtOrPerc()));
			reportData.setAlwWaiver(finSchData.getFinODPenaltyRate().isODAllowWaiver() ? "True" : "False");
			reportData.setMaxWaiver(String.valueOf(finSchData.getFinODPenaltyRate().getODMaxWaiverPerc()) + " %");
		}

		// Profit Details

		reportData.setTotalDisb(
				PennantApplicationUtil.amountFormate(financeSummary.getTotalDisbursement(), ccyFormatter));
		reportData.setTotalDownPayment(
				PennantApplicationUtil.amountFormate(financeSummary.getTotalDownPayment(), ccyFormatter));

		// Totals
		reportData.setTotalFees(PennantApplicationUtil.amountFormate(financeSummary.getTotalFees(), ccyFormatter));
		reportData.setTotalWaivers(
				PennantApplicationUtil.amountFormate(financeSummary.getTotalWaiverFee(), ccyFormatter));
		reportData.setFinODTotPenaltyAmt(
				PennantApplicationUtil.amountFormate(financeSummary.getFinODTotPenaltyAmt(), ccyFormatter));
		reportData.setFinODTotPenaltyPaid(
				PennantApplicationUtil.amountFormate(financeSummary.getFinODTotPenaltyPaid(), ccyFormatter));
		reportData
				.setTotalPaidFees(PennantApplicationUtil.amountFormate(financeSummary.getTotalPaidFee(), ccyFormatter));
		reportData.setFinODTotWaived(
				PennantApplicationUtil.amountFormate(financeSummary.getFinODTotWaived(), ccyFormatter));
		reportData.setFinODTotPenaltyBal(
				PennantApplicationUtil.amountFormate(financeSummary.getFinODTotPenaltyBal(), ccyFormatter));

		// Profit Schedule Details
		reportData.setTotalPriSchd(PennantApplicationUtil.amountFormate(finSummary.getTotalPriSchd(), ccyFormatter));
		reportData.setTotalPftSchd(PennantApplicationUtil.amountFormate(finSummary.getTotalPftSchd(), ccyFormatter));
		reportData.setTotalOriginal(PennantApplicationUtil.amountFormate(finSummary.getTotalOriginal(), ccyFormatter));

		reportData.setOutStandPrincipal(
				PennantApplicationUtil.amountFormate(finSummary.getOutStandPrincipal(), ccyFormatter));
		reportData
				.setOutStandProfit(PennantApplicationUtil.amountFormate(finSummary.getOutStandProfit(), ccyFormatter));
		reportData.setTotalOutStanding(
				PennantApplicationUtil.amountFormate(finSummary.getTotalOutStanding(), ccyFormatter));

		reportData.setSchdPriPaid(PennantApplicationUtil.amountFormate(finSummary.getSchdPriPaid(), ccyFormatter));
		reportData.setSchdPftPaid(PennantApplicationUtil.amountFormate(finSummary.getSchdPftPaid(), ccyFormatter));
		reportData.setTotalPaid(PennantApplicationUtil.amountFormate(finSummary.getTotalPaid(), ccyFormatter));

		reportData.setUnPaidPrincipal(
				PennantApplicationUtil.amountFormate(finSummary.getUnPaidPrincipal(), ccyFormatter));
		reportData.setUnPaidProfit(PennantApplicationUtil.amountFormate(finSummary.getUnPaidProfit(), ccyFormatter));
		reportData.setTotalUnPaid(PennantApplicationUtil.amountFormate(finSummary.getTotalUnPaid(), ccyFormatter));

		reportData.setOverDuePrincipal(
				PennantApplicationUtil.amountFormate(finSummary.getOverDuePrincipal(), ccyFormatter));
		reportData.setOverDueProfit(PennantApplicationUtil.amountFormate(finSummary.getOverDueProfit(), ccyFormatter));
		reportData.setTotalOverDue(PennantApplicationUtil.amountFormate(finSummary.getTotalOverDue(), ccyFormatter));

		reportData.setEarnedPrincipal(
				PennantApplicationUtil.amountFormate(finSummary.getEarnedPrincipal(), ccyFormatter));
		reportData.setEarnedProfit(PennantApplicationUtil.amountFormate(finSummary.getEarnedProfit(), ccyFormatter));
		reportData.setTotalEarned(PennantApplicationUtil.amountFormate(finSummary.getTotalEarned(), ccyFormatter));

		reportData.setUnEarnedPrincipal(
				PennantApplicationUtil.amountFormate(finSummary.getUnEarnedPrincipal(), ccyFormatter));
		reportData
				.setUnEarnedProfit(PennantApplicationUtil.amountFormate(finSummary.getUnEarnedProfit(), ccyFormatter));
		reportData.setTotalUnEarned(PennantApplicationUtil.amountFormate(finSummary.getTotalUnEarned(), ccyFormatter));

		reportData.setPayOffPrincipal(
				PennantApplicationUtil.amountFormate(finSummary.getPayOffPrincipal(), ccyFormatter));
		reportData.setPayOffProfit(PennantApplicationUtil.amountFormate(finSummary.getPayOffProfit(), ccyFormatter));
		reportData.setTotalPayOff(PennantApplicationUtil.amountFormate(finSummary.getTotalPayOff(), ccyFormatter));

		// Finance Installment Details
		reportData.setOverDueInstlments(String.valueOf(finSummary.getOverDueInstlments()));
		reportData.setOverDueInstlementPft(
				PennantApplicationUtil.amountFormate(finSummary.getOverDueInstlementPft(), ccyFormatter));
		reportData.setFinProfitrate(PennantApplicationUtil.formatRate(finSummary.getFinRate().doubleValue(), 2) + " %");
		reportData.setPaidInstlments(String.valueOf(finSummary.getPaidInstlments()));
		reportData.setPaidInstlementPft(PennantApplicationUtil.amountFormate(finSummary.getTotalPaid(), ccyFormatter));

		if (financeMain.getNOInst() > 0) {
			reportData.setUnPaidInstlments(String.valueOf(financeMain.getNOInst() - finSummary.getPaidInstlments()));
		} else {
			reportData.setUnPaidInstlments(String.valueOf(financeMain.getCalTerms() - finSummary.getPaidInstlments()));
		}
		reportData.setUnPaidInstlementPft(PennantApplicationUtil
				.amountFormate(finSummary.getTotalUnPaid().add(financeSummary.getFinODTotPenaltyBal()), ccyFormatter));

		return reportData;
	}

	private String getlabelDesc(String value, List<ValueLabel> list) {
		if (value != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getValue().equalsIgnoreCase(value)) {
					return list.get(i).getLabel();
				}
			}
		}
		return "";
	}

	public String getApplyDownPay() {
		return applyDownPay;
	}

	public void setApplyDownPay(String applyDownPay) {
		this.applyDownPay = applyDownPay;
	}

	public String getApplyDifferment() {
		return applyDifferment;
	}

	public void setApplyDifferment(String applyDifferment) {
		this.applyDifferment = applyDifferment;
	}

	public String getApplyRepayAccntId() {
		return applyRepayAccntId;
	}

	public void setApplyRepayAccntId(String applyRepayAccntId) {
		this.applyRepayAccntId = applyRepayAccntId;
	}

	public String getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(String graceTerms) {
		this.graceTerms = graceTerms;
	}

	public String getTotalPaidFees() {
		return totalPaidFees;
	}

	public void setTotalPaidFees(String totalPaidFees) {
		this.totalPaidFees = totalPaidFees;
	}

}
