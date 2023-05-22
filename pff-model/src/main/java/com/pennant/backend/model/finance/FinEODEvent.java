package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pff.overdraft.model.OverdraftDTO;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class FinEODEvent implements Serializable {
	private static final long serialVersionUID = 1183720618731771888L;

	private FinanceMain financeMain = new FinanceMain();
	private OverdraftDTO overDraftFM = new OverdraftDTO();
	private FinanceType finType = new FinanceType();
	private List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<>(1);
	private List<RepayInstruction> RepayInstructions = new ArrayList<>(1);
	private FinanceProfitDetail finProfitDetail = new FinanceProfitDetail();
	private List<FinODDetails> finODDetails = new ArrayList<FinODDetails>(1);
	// this is used to store the date which is in database, this should not be modified
	private List<FinODDetails> finODDetailsLBD = new ArrayList<FinODDetails>(1);
	private List<OverdueChargeRecovery> odcRecoveries = new ArrayList<>(1);
	private FinODPenaltyRate penaltyrate;
	private List<FinanceDisbursement> financeDisbursements = new ArrayList<>(1);
	private List<FinFeeScheduleDetail> finFeeScheduleDetails = new ArrayList<>(1);
	private List<PresentmentDetail> presentmentDetails = new ArrayList<>(1);
	private List<ReturnDataSet> returnDataSet = new ArrayList<>(1);
	private List<Provision> provisions = new ArrayList<>(1);
	private List<FinExcessAmount> finExcessAmounts = new ArrayList<>(1);
	private List<ManualAdvise> cancelManualAdvises = new ArrayList<>();
	private List<ManualAdvise> postingManualAdvises = new ArrayList<>();
	private Date eventFromDate;
	private Date eventToDate;
	private Date recalFromDate;
	private Date recalToDate;
	private String recalType;
	private String recalSchdMethod;
	private String rateOnChgDate;
	private BigDecimal accruedAmount = BigDecimal.ZERO;

	private boolean rateReviewExist = false;

	private int idxPD = -1;
	private int idxGrcCpz = -1;
	private int idxGrcPft = -1;
	private int idxGrcPftRvw = -1;
	private int idxRpyCpz = -1;
	private int idxRpyPft = -1;
	private int idxRpyPftRvw = -1;
	private int idxRpy = -1;
	private int idxDisb = -1;
	private int idxDue = -1;
	private int idxPresentment = -1;

	private boolean updFinMain = false;
	private boolean updFinSchdForRateRvw = false;
	private boolean updRepayInstruct = false;
	private boolean updLBDPostings = false;
	private boolean updMonthEndPostings = false;
	private List<String> finMainUpdateFields = new ArrayList<>(1);

	// IND AS : Income / Expense Amortization
	private List<ProjectedAccrual> projectedAccrualList = new ArrayList<>(1);
	private List<ProjectedAmortization> incomeAMZList = new ArrayList<>(1);
	private Date appDate;

	// Auto Increment Grace End
	private boolean updFinSchdForChangeGrcEnd = false;
	private List<RepayInstruction> orgRepayInsts = new ArrayList<>(1);
	private List<FinanceScheduleDetail> orgFinSchdDetails = new ArrayList<>(1);
	private List<FinServiceInstruction> finServiceInstructions = new ArrayList<>();
	private SubventionDetail subventionDetail = new SubventionDetail();
	private List<FinanceStepPolicyDetail> stepPolicyDetails = new ArrayList<>(1);
	private boolean npaStage;
	private List<FinOverDueCharges> finODCAmounts = new ArrayList<>(1);

	public FinEODEvent copyEntity() {
		FinEODEvent entity = new FinEODEvent();
		entity.setFinanceMain(this.financeMain == null ? null : this.financeMain.copyEntity());
		// entity.setOverDraftFM(this.overDraftFM == null ? null : this.overDraftFM.copyEntity());
		entity.setFinType(this.finType == null ? null : this.finType.copyEntity());
		this.financeScheduleDetails.stream()
				.forEach(e -> entity.getFinanceScheduleDetails().add(e == null ? null : e.copyEntity()));
		this.RepayInstructions.stream()
				.forEach(e -> entity.getRepayInstructions().add(e == null ? null : e.copyEntity()));
		entity.setFinProfitDetail(this.finProfitDetail == null ? null : this.finProfitDetail.copyEntity());
		this.finODDetails.stream().forEach(e -> entity.getFinODDetails().add(e == null ? null : e.copyEntity()));
		this.finODDetailsLBD.stream().forEach(e -> entity.getFinODDetailsLBD().add(e == null ? null : e.copyEntity()));
		this.odcRecoveries.stream().forEach(e -> entity.getOdcRecoveries().add(e == null ? null : e.copyEntity()));
		entity.setPenaltyrate(this.penaltyrate == null ? null : this.penaltyrate.copyEntity());
		this.financeDisbursements.stream()
				.forEach(e -> entity.getFinanceDisbursements().add(e == null ? null : e.copyEntity()));
		this.finFeeScheduleDetails.stream()
				.forEach(e -> entity.getFinFeeScheduleDetails().add(e == null ? null : e.copyEntity()));
		this.returnDataSet.stream().forEach(e -> entity.getReturnDataSet().add(e == null ? null : e.copyEntity()));
		this.provisions.stream().forEach(e -> entity.getProvisions().add(e == null ? null : e.copyEntity()));
		this.finExcessAmounts.stream()
				.forEach(e -> entity.getFinExcessAmounts().add(e == null ? null : e.copyEntity()));
		this.cancelManualAdvises.stream().forEach(e -> entity.getCancelManualAdvises().add(e.copyEntity()));
		this.postingManualAdvises.stream().forEach(e -> entity.getPostingManualAdvises().add(e.copyEntity()));
		entity.setEventFromDate(this.eventFromDate);
		entity.setEventToDate(this.eventToDate);
		entity.setRecalFromDate(this.recalFromDate);
		entity.setRecalToDate(this.recalToDate);
		entity.setRecalType(this.recalType);
		entity.setRecalSchdMethod(this.recalSchdMethod);
		entity.setRateOnChgDate(this.rateOnChgDate);
		entity.setAccruedAmount(this.accruedAmount);
		entity.setRateReviewExist(this.rateReviewExist);
		entity.setIdxPD(this.idxPD);
		entity.setIdxGrcCpz(this.idxGrcCpz);
		entity.setIdxGrcPft(this.idxGrcPft);
		entity.setIdxGrcPftRvw(this.idxGrcPftRvw);
		entity.setIdxRpyCpz(this.idxRpyCpz);
		entity.setIdxRpyPft(this.idxRpyPft);
		entity.setIdxRpyPftRvw(this.idxRpyPftRvw);
		entity.setIdxRpy(this.idxRpy);
		entity.setIdxDisb(this.idxDisb);
		entity.setIdxDue(this.idxDue);
		entity.setIdxPresentment(this.idxPresentment);
		entity.setUpdFinMain(this.updFinMain);
		entity.setUpdFinSchdForRateRvw(this.updFinSchdForRateRvw);
		entity.setUpdRepayInstruct(this.updRepayInstruct);
		entity.setUpdLBDPostings(this.updLBDPostings);
		entity.setUpdMonthEndPostings(this.updMonthEndPostings);
		this.finMainUpdateFields.stream().forEach(e -> entity.getFinMainUpdateFields().add(e));
		this.projectedAccrualList.stream()
				.forEach(e -> entity.getProjectedAccrualList().add(e == null ? null : e.copyEntity()));
		this.incomeAMZList.stream().forEach(e -> entity.getIncomeAMZList().add(e == null ? null : e.copyEntity()));
		entity.setAppDate(this.appDate);
		entity.setUpdFinSchdForChangeGrcEnd(this.updFinSchdForChangeGrcEnd);
		this.orgRepayInsts.stream().forEach(e -> entity.getOrgRepayInsts().add(e == null ? null : e.copyEntity()));
		this.orgFinSchdDetails.stream()
				.forEach(e -> entity.getOrgFinSchdDetails().add(e == null ? null : e.copyEntity()));
		this.finServiceInstructions.stream()
				.forEach(e -> entity.getFinServiceInstructions().add(e == null ? null : e.copyEntity()));
		entity.setSubventionDetail(this.subventionDetail == null ? null : this.subventionDetail.copyEntity());
		this.stepPolicyDetails.stream().forEach(e -> entity.getStepPolicyDetails().add(e.copyEntity()));
		return entity;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public OverdraftDTO getOverDraftFM() {
		return overDraftFM;
	}

	public void setOverDraftFM(OverdraftDTO overDraftFM) {
		this.overDraftFM = overDraftFM;
	}

	public FinanceType getFinType() {
		return finType;
	}

	public void setFinType(FinanceType finType) {
		this.finType = finType;
	}

	public List<FinanceScheduleDetail> getFinanceScheduleDetails() {
		return financeScheduleDetails;
	}

	public void setFinanceScheduleDetails(List<FinanceScheduleDetail> financeScheduleDetails) {
		this.financeScheduleDetails = financeScheduleDetails;
	}

	public List<RepayInstruction> getRepayInstructions() {
		return RepayInstructions;
	}

	public void setRepayInstructions(List<RepayInstruction> repayInstructions) {
		RepayInstructions = repayInstructions;
	}

	public FinanceProfitDetail getFinProfitDetail() {
		return finProfitDetail;
	}

	public void setFinProfitDetail(FinanceProfitDetail finProfitDetail) {
		this.finProfitDetail = finProfitDetail;
	}

	public List<FinODDetails> getFinODDetails() {
		return finODDetails;
	}

	public void setFinODDetails(List<FinODDetails> finODDetails) {
		this.finODDetails = finODDetails;
	}

	public List<OverdueChargeRecovery> getOdcRecoveries() {
		return odcRecoveries;
	}

	public void setOdcRecoveries(List<OverdueChargeRecovery> odcRecoveries) {
		this.odcRecoveries = odcRecoveries;
	}

	public boolean isRateReviewExist() {
		return rateReviewExist;
	}

	public void setRateReviewExist(boolean rateReviewExist) {
		this.rateReviewExist = rateReviewExist;
	}

	public Date getEventFromDate() {
		return eventFromDate;
	}

	public void setEventFromDate(Date eventFromDate) {
		this.eventFromDate = eventFromDate;
	}

	public Date getEventToDate() {
		return eventToDate;
	}

	public void setEventToDate(Date eventToDate) {
		this.eventToDate = eventToDate;
	}

	public Date getRecalFromDate() {
		return recalFromDate;
	}

	public void setRecalFromDate(Date recalFromDate) {
		this.recalFromDate = recalFromDate;
	}

	public Date getRecalToDate() {
		return recalToDate;
	}

	public void setRecalToDate(Date recalToDate) {
		this.recalToDate = recalToDate;
	}

	public String getRecalType() {
		return recalType;
	}

	public void setRecalType(String recalType) {
		this.recalType = recalType;
	}

	public String getRecalSchdMethod() {
		return recalSchdMethod;
	}

	public void setRecalSchdMethod(String recalSchdMethod) {
		this.recalSchdMethod = recalSchdMethod;
	}

	public String getRateOnChgDate() {
		return rateOnChgDate;
	}

	public void setRateOnChgDate(String rateOnChgDate) {
		this.rateOnChgDate = rateOnChgDate;
	}

	public boolean isUpdFinMain() {
		return updFinMain;
	}

	public void setUpdFinMain(boolean updFinMain) {
		this.updFinMain = updFinMain;
	}

	public boolean isupdFinSchdForRateRvw() {
		return updFinSchdForRateRvw;
	}

	public void setupdFinSchdForRateRvw(boolean updFinSchdForRateRvw) {
		this.updFinSchdForRateRvw = updFinSchdForRateRvw;
	}

	public boolean isUpdRepayInstruct() {
		return updRepayInstruct;
	}

	public void setUpdRepayInstruct(boolean updRepayInstruct) {
		this.updRepayInstruct = updRepayInstruct;
	}

	public FinODPenaltyRate getPenaltyrate() {
		return penaltyrate;
	}

	public void setPenaltyrate(FinODPenaltyRate penaltyrate) {
		this.penaltyrate = penaltyrate;
	}

	public List<FinanceDisbursement> getFinanceDisbursements() {
		return financeDisbursements;
	}

	public void setFinanceDisbursements(List<FinanceDisbursement> financeDisbursements) {
		this.financeDisbursements = financeDisbursements;
	}

	public List<FinFeeScheduleDetail> getFinFeeScheduleDetails() {
		return finFeeScheduleDetails;
	}

	public void setFinFeeScheduleDetails(List<FinFeeScheduleDetail> finFeeScheduleDetails) {
		this.finFeeScheduleDetails = finFeeScheduleDetails;
	}

	public List<PresentmentDetail> getPresentmentDetails() {
		return presentmentDetails;
	}

	public void setPresentmentDetails(List<PresentmentDetail> presentmentDetails) {
		this.presentmentDetails = presentmentDetails;
	}

	public List<ReturnDataSet> getReturnDataSet() {
		return returnDataSet;
	}

	public void setReturnDataSet(List<ReturnDataSet> returnDataSet) {
		this.returnDataSet = returnDataSet;
	}

	public List<String> getFinMainUpdateFields() {
		return finMainUpdateFields;
	}

	public void setFinMainUpdateFields(List<String> finMainUpdateFields) {
		this.finMainUpdateFields = finMainUpdateFields;
	}

	public void addToFinMianUpdate(String fieldName) {
		finMainUpdateFields.add(fieldName);
	}

	public int getIdxPD() {
		return idxPD;
	}

	public void setIdxPD(int idxPD) {
		this.idxPD = idxPD;
	}

	public int getIdxGrcCpz() {
		return idxGrcCpz;
	}

	public void setIdxGrcCpz(int idxGrcCpz) {
		this.idxGrcCpz = idxGrcCpz;
	}

	public int getIdxGrcPft() {
		return idxGrcPft;
	}

	public void setIdxGrcPft(int idxGrcPft) {
		this.idxGrcPft = idxGrcPft;
	}

	public int getIdxGrcPftRvw() {
		return idxGrcPftRvw;
	}

	public void setIdxGrcPftRvw(int idxGrcPftRvw) {
		this.idxGrcPftRvw = idxGrcPftRvw;
	}

	public int getIdxRpyCpz() {
		return idxRpyCpz;
	}

	public void setIdxRpyCpz(int idxRpyCpz) {
		this.idxRpyCpz = idxRpyCpz;
	}

	public int getIdxRpyPft() {
		return idxRpyPft;
	}

	public void setIdxRpyPft(int idxRpyPft) {
		this.idxRpyPft = idxRpyPft;
	}

	public int getIdxRpyPftRvw() {
		return idxRpyPftRvw;
	}

	public void setIdxRpyPftRvw(int idxRpyPftRvw) {
		this.idxRpyPftRvw = idxRpyPftRvw;
	}

	public int getIdxRpy() {
		return idxRpy;
	}

	public void setIdxRpy(int idxRpy) {
		this.idxRpy = idxRpy;
	}

	public boolean isUpdFinSchdForRateRvw() {
		return updFinSchdForRateRvw;
	}

	public void setUpdFinSchdForRateRvw(boolean updFinSchdForRateRvw) {
		this.updFinSchdForRateRvw = updFinSchdForRateRvw;
	}

	public int getIdxDisb() {
		return idxDisb;
	}

	public void setIdxDisb(int idxDisb) {
		this.idxDisb = idxDisb;
	}

	public int getIdxDue() {
		return idxDue;
	}

	public void setIdxDue(int idxDue) {
		this.idxDue = idxDue;
	}

	public int getIdxPresentment() {
		return idxPresentment;
	}

	public void setIdxPresentment(int idxPresentment) {
		this.idxPresentment = idxPresentment;
	}

	public boolean isUpdLBDPostings() {
		return updLBDPostings;
	}

	public void setUpdLBDPostings(boolean updLBDPostings) {
		this.updLBDPostings = updLBDPostings;
	}

	public boolean isUpdMonthEndPostings() {
		return updMonthEndPostings;
	}

	public void setUpdMonthEndPostings(boolean updMonthEndPostings) {
		this.updMonthEndPostings = updMonthEndPostings;
	}

	public List<Provision> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<Provision> provisions) {
		this.provisions = provisions;
	}

	public List<FinExcessAmount> getFinExcessAmounts() {
		return finExcessAmounts;
	}

	public void setFinExcessAmounts(List<FinExcessAmount> finExcessAmounts) {
		this.finExcessAmounts = finExcessAmounts;
	}

	public List<ManualAdvise> getCancelManualAdvises() {
		return cancelManualAdvises;
	}

	public void setCancelManualAdvises(List<ManualAdvise> cancelManualAdvises) {
		this.cancelManualAdvises = cancelManualAdvises;
	}

	public List<ManualAdvise> getPostingManualAdvises() {
		return postingManualAdvises;
	}

	public void setPostingManualAdvises(List<ManualAdvise> postingManualAdvises) {
		this.postingManualAdvises = postingManualAdvises;
	}

	public List<ProjectedAccrual> getProjectedAccrualList() {
		return projectedAccrualList;
	}

	public void setProjectedAccrualList(List<ProjectedAccrual> projectedAccrualList) {
		this.projectedAccrualList = projectedAccrualList;
	}

	public List<ProjectedAmortization> getIncomeAMZList() {
		return incomeAMZList;
	}

	public void setIncomeAMZList(List<ProjectedAmortization> incomeAMZList) {
		this.incomeAMZList = incomeAMZList;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public void destroy() {
		this.financeMain = null;
		this.finType = null;
		this.financeScheduleDetails.clear();
		this.RepayInstructions.clear();
		this.finProfitDetail = null;
		this.finODDetails.clear();
		this.odcRecoveries.clear();
		this.penaltyrate = null;
		this.financeDisbursements.clear();
		this.finFeeScheduleDetails.clear();
		this.presentmentDetails.clear();
		this.returnDataSet.clear();
		this.provisions.clear();
		this.incomeAMZList.clear();
		this.projectedAccrualList.clear();
	}

	public List<FinODDetails> getFinODDetailsLBD() {
		return finODDetailsLBD;
	}

	public void setFinODDetailsLBD(List<FinODDetails> finODDetailsLBD) {
		this.finODDetailsLBD = finODDetailsLBD;
	}

	public BigDecimal getAccruedAmount() {
		return accruedAmount;
	}

	public void setAccruedAmount(BigDecimal accruedAmount) {
		this.accruedAmount = accruedAmount;
	}

	public boolean isUpdFinSchdForChangeGrcEnd() {
		return updFinSchdForChangeGrcEnd;
	}

	public void setUpdFinSchdForChangeGrcEnd(boolean updFinSchdForChangeGrcEnd) {
		this.updFinSchdForChangeGrcEnd = updFinSchdForChangeGrcEnd;
	}

	public List<RepayInstruction> getOrgRepayInsts() {
		return orgRepayInsts;
	}

	public void setOrgRepayInsts(List<RepayInstruction> orgRepayInsts) {
		this.orgRepayInsts = orgRepayInsts;
	}

	public List<FinanceScheduleDetail> getOrgFinSchdDetails() {
		return orgFinSchdDetails;
	}

	public void setOrgFinSchdDetails(List<FinanceScheduleDetail> orgFinSchdDetails) {
		this.orgFinSchdDetails = orgFinSchdDetails;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return finServiceInstructions;
	}

	public void setFinServiceInstructions(List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public SubventionDetail getSubventionDetail() {
		return subventionDetail;
	}

	public void setSubventionDetail(SubventionDetail subventionDetail) {
		this.subventionDetail = subventionDetail;
	}

	public List<FinanceStepPolicyDetail> getStepPolicyDetails() {
		return stepPolicyDetails;
	}

	public void setStepPolicyDetails(List<FinanceStepPolicyDetail> stepPolicyDetails) {
		this.stepPolicyDetails = stepPolicyDetails;
	}

	public boolean isNpaStage() {
		return npaStage;
	}

	public void setNpaStage(boolean npaStage) {
		this.npaStage = npaStage;
	}

	public List<FinOverDueCharges> getFinODCAmounts() {
		return finODCAmounts;
	}

	public void setFinODCAmounts(List<FinOverDueCharges> finODCAmounts) {
		this.finODCAmounts = finODCAmounts;
	}
}
