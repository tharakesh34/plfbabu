/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinScheduleData.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-03-2011 * * Modified Date
 * : 22-03-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-03-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;

@XmlType(propOrder = { "finReference", "financeMain", "repayInstructions", "rateInstruction", "finFeeDetailList",
		"feeDues", "foreClosureFees", "stepPolicyDetails", "financeScheduleDetails", "finODPenaltyRate",
		"apiPlanEMIHmonths", "apiPlanEMIHDates", "finODDetails", "financeSummary", "vasRecordingList", "outstandingPri",
		"subventionDetail", "receiptAllocations", "returnStatus" })
@XmlRootElement(name = "financeSchedule")
@XmlAccessorType(XmlAccessType.NONE)
public class FinScheduleData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
	@XmlElement
	private String finReference = null;
	private boolean schduleGenerated = false;

	private FinanceType financeType;

	@XmlElement(name = "financeDetail")
	private FinanceMain financeMain = null;
	private Map<Date, FinanceScheduleDetail> scheduleMap;

	@XmlElement(name = "step")
	private List<FinanceStepPolicyDetail> stepPolicyDetails = new ArrayList<>(1);

	@XmlElementWrapper(name = "schedules")
	@XmlElement(name = "schedule")
	private List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<>(1);

	private List<OverdraftScheduleDetail> overdraftScheduleDetails = new ArrayList<>(1);
	private List<FinanceDisbursement> disbursementDetails = new ArrayList<>(1);

	@XmlElement(name = "repayInstruction")
	private List<RepayInstruction> repayInstructions = new ArrayList<>(1);
	private List<ErrorDetail> errorDetails = new ArrayList<>(1);
	@XmlElement(name = "summary")
	private FinanceSummary financeSummary;
	private List<FeeRule> feeRules = new ArrayList<>(1);
	@XmlElementWrapper(name = "fees")
	@XmlElement(name = "fee")
	private List<FinFeeDetail> finFeeDetailList = new ArrayList<>(1);
	private List<FinanceRepayments> repayDetails = new ArrayList<>(1);

	private List<OverdueChargeRecovery> penaltyDetails = new ArrayList<>(1);
	private BigDecimal accrueValue = BigDecimal.ZERO;

	@XmlElement(name = "overdue")
	private FinODPenaltyRate finODPenaltyRate;
	private boolean finPftSuspended;
	private Date finSuspDate;
	private String feeEvent;

	@XmlElement(name = "rateInstruction")
	private List<RateInstruction> rateInstruction = new ArrayList<>();
	private List<FinServiceInstruction> finServiceInstructions = new ArrayList<>();
	private boolean odLimitReset;

	private List<Integer> planEMIHmonths = new ArrayList<>();

	private List<Date> planEMIHDates = new ArrayList<>();

	private List<BaseRate> baseRates = new ArrayList<>();
	private List<SplRate> splRates = new ArrayList<>();
	@XmlElement(name = "planEMIHDates")
	private List<FinPlanEmiHoliday> apiPlanEMIHDates = new ArrayList<>();
	@XmlElement(name = "planEMIHmonths")
	private List<FinPlanEmiHoliday> apiPlanEMIHmonths = new ArrayList<>();
	// Vas Recording Details
	@XmlElementWrapper(name = "vas")
	@XmlElement(name = "vasRecording")
	private List<VASRecording> vasRecordingList = new ArrayList<>(1);

	// API specific fields
	@XmlElement(name = "overdueCharges")
	private List<FinODDetails> finODDetails = new ArrayList<>();
	@XmlElementWrapper(name = "foreClosureFees")
	@XmlElement(name = "foreClosureFee")
	List<FinFeeDetail> foreClosureFees;

	@XmlElementWrapper(name = "feeDues")
	@XmlElement(name = "feeDue")
	List<FinFeeDetail> feeDues = new ArrayList<>(1);
	@XmlElement
	private BigDecimal outstandingPri = BigDecimal.ZERO;
	private BigDecimal pftChg = BigDecimal.ZERO; // Using for Every service instruction Profit change

	@XmlElement
	private WSReturnStatus returnStatus;

	// SubventionDetail
	@XmlElement
	private SubventionDetail subventionDetail;

	// Promotion / Scheme
	private Promotion promotion;

	private boolean quickDisb;

	private List<FinReceiptHeader> imdReceipts = new ArrayList<>();
	private List<FinFeeReceipt> finFeeReceipts = new ArrayList<>();
	private List<FinIRRDetails> iRRDetails = new ArrayList<>();

	private List<ExposureLinking> exposureLinkings = new ArrayList<>();
	private List<SanctionCondition> sanctionConditions = new ArrayList<>();

	@XmlElement
	private String externalReference;
	@XmlElement
	private boolean isUpfrontAuto;

	// AddFlexiDisbursement
	private boolean isFlexiDisb;

	private FinanceProfitDetail finPftDeatil;

	@XmlElementWrapper(name = "receiptAllocations")
	@XmlElement(name = "allocation")
	private List<ReceiptAllocationDetail> receiptAllocationList;

	// GST Tax Map
	private Map<String, Object> gstExecutionMap = new HashMap<>();

	private List<LowerTaxDeduction> lowerTaxDeductionDetails = new ArrayList<>();
	private List<IRRScheduleDetail> irrSDList = new ArrayList<>(1);
	private FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();

	@XmlElement(name = "hostReference")
	private String oldFinReference;

	private RestructureDetail restructureDetail;
	private boolean equalInst;
	private String moduleDefiner = "";

	public FinScheduleData() {
		super();
	}

	public FinScheduleData copyEntity() {
		FinScheduleData entity = new FinScheduleData();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setSchduleGenerated(this.schduleGenerated);
		entity.setFinanceType(this.financeType == null ? null : this.financeType.copyEntity());
		entity.setFinanceMain(this.financeMain == null ? null : this.financeMain.copyEntity());
		if (scheduleMap != null) {
			entity.setScheduleMap(new HashMap<Date, FinanceScheduleDetail>());
			this.scheduleMap.entrySet().stream().forEach(e -> entity.getScheduleMap().put(e.getKey(),
					e.getValue() == null ? null : e.getValue().copyEntity()));
		}
		this.stepPolicyDetails.stream()
				.forEach(e -> entity.getStepPolicyDetails().add(e == null ? null : e.copyEntity()));
		this.financeScheduleDetails.stream()
				.forEach(e -> entity.getFinanceScheduleDetails().add(e == null ? null : e.copyEntity()));
		this.overdraftScheduleDetails.stream()
				.forEach(e -> entity.getOverdraftScheduleDetails().add(e == null ? null : e.copyEntity()));
		this.disbursementDetails.stream()
				.forEach(e -> entity.getDisbursementDetails().add(e == null ? null : e.copyEntity()));
		this.repayInstructions.stream()
				.forEach(e -> entity.getRepayInstructions().add(e == null ? null : e.copyEntity()));
		this.errorDetails.stream().forEach(e -> entity.getErrorDetails().add(e));
		entity.setFinanceSummary(this.financeSummary == null ? null : this.financeSummary.copyEntity());
		this.feeRules.stream().forEach(e -> entity.getFeeRules().add(e == null ? null : e.copyEntity()));
		this.finFeeDetailList.stream()
				.forEach(e -> entity.getFinFeeDetailList().add(e == null ? null : e.copyEntity()));
		this.repayDetails.stream().forEach(e -> entity.getRepayDetails().add(e == null ? null : e.copyEntity()));
		this.penaltyDetails.stream().forEach(e -> entity.getPenaltyDetails().add(e == null ? null : e.copyEntity()));
		entity.setAccrueValue(this.accrueValue);
		entity.setFinODPenaltyRate(this.finODPenaltyRate == null ? null : this.finODPenaltyRate.copyEntity());
		entity.setFinPftSuspended(this.finPftSuspended);
		entity.setFinSuspDate(this.finSuspDate);
		entity.setFeeEvent(this.feeEvent);
		this.rateInstruction.stream().forEach(e -> entity.getRateInstruction().add(e == null ? null : e.copyEntity()));
		this.finServiceInstructions.stream()
				.forEach(e -> entity.getFinServiceInstructions().add(e == null ? null : e.copyEntity()));
		entity.setOdLimitReset(this.odLimitReset);
		this.planEMIHmonths.stream().forEach(e -> entity.getPlanEMIHmonths().add(e));
		this.planEMIHDates.stream().forEach(e -> entity.getPlanEMIHDates().add(e));
		this.baseRates.stream().forEach(e -> entity.getBaseRates().add(e == null ? null : e.copyEntity()));
		this.splRates.stream().forEach(e -> entity.getSplRates().add(e == null ? null : e.copyEntity()));
		this.apiPlanEMIHDates.stream()
				.forEach(e -> entity.getApiPlanEMIHDates().add(e == null ? null : e.copyEntity()));
		this.apiPlanEMIHmonths.stream()
				.forEach(e -> entity.getApiPlanEMIHmonths().add(e == null ? null : e.copyEntity()));
		this.vasRecordingList.stream()
				.forEach(e -> entity.getVasRecordingList().add(e == null ? null : e.copyEntity()));
		this.finODDetails.stream().forEach(e -> entity.getFinODDetails().add(e == null ? null : e.copyEntity()));
		if (foreClosureFees != null) {
			entity.setForeClosureFees(new ArrayList<FinFeeDetail>());
			this.foreClosureFees.stream()
					.forEach(e -> entity.getForeClosureFees().add(e == null ? null : e.copyEntity()));
		}
		this.feeDues.stream().forEach(e -> entity.getFeeDues().add(e == null ? null : e.copyEntity()));
		entity.setOutstandingPri(this.outstandingPri);
		entity.setPftChg(this.pftChg);
		entity.setReturnStatus(this.returnStatus == null ? null : this.returnStatus.copyEntity());
		entity.setSubventionDetail(this.subventionDetail == null ? null : this.subventionDetail.copyEntity());
		entity.setPromotion(this.promotion == null ? null : this.promotion.copyEntity());
		entity.setQuickDisb(this.quickDisb);
		this.imdReceipts.stream().forEach(e -> entity.getImdReceipts().add(e == null ? null : e.copyEntity()));
		this.finFeeReceipts.stream().forEach(e -> entity.getFinFeeReceipts().add(e == null ? null : e.copyEntity()));
		this.iRRDetails.stream().forEach(e -> entity.getiRRDetails().add(e == null ? null : e.copyEntity()));
		this.exposureLinkings.stream()
				.forEach(e -> entity.getExposureLinkings().add(e == null ? null : e.copyEntity()));
		this.sanctionConditions.stream()
				.forEach(e -> entity.getSanctionConditions().add(e == null ? null : e.copyEntity()));
		entity.setExternalReference(this.externalReference);
		entity.setUpfrontAuto(this.isUpfrontAuto);
		entity.setFlexiDisb(this.isFlexiDisb);
		entity.setFinPftDeatil(this.finPftDeatil == null ? null : this.finPftDeatil.copyEntity());
		if (receiptAllocationList != null) {
			entity.setReceiptAllocationList(new ArrayList<ReceiptAllocationDetail>());
			this.receiptAllocationList.stream()
					.forEach(e -> entity.getReceiptAllocationList().add(e == null ? null : e.copyEntity()));
		}
		// FIXME
		/*
		 * this.gstExecutionMap.entrySet().stream().forEach(e -> entity.getGstExecutionMap().put(e.getKey(),
		 * e.getValue() == null ? null : e.getValue().copyEntity()));
		 */
		this.lowerTaxDeductionDetails.stream()
				.forEach(e -> entity.getLowerTaxDeductionDetails().add(e == null ? null : e.copyEntity()));
		this.irrSDList.stream().forEach(e -> entity.getIrrSDList().add(e == null ? null : e.copyEntity()));
		entity.setFinMaintainInstruction(
				this.finMaintainInstruction == null ? null : this.finMaintainInstruction.copyEntity());
		entity.setOldFinReference(this.oldFinReference);
		entity.setRestructureDetail(this.restructureDetail == null ? null : this.restructureDetail.copyEntity());
		entity.setEqualInst(this.equalInst);
		entity.setModuleDefiner(this.moduleDefiner);
		return entity;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public List<FinanceStepPolicyDetail> getStepPolicyDetails() {
		return stepPolicyDetails;
	}

	public void setStepPolicyDetails(List<FinanceStepPolicyDetail> stepPolicyDetails) {
		this.stepPolicyDetails = stepPolicyDetails;
	}

	public void setStepPolicyDetails(List<FinanceStepPolicyDetail> stepPolicyDetails, boolean sort) {
		// Steps needs to sort based on specifier and step no for schedule calculation if step calculated on Amount.
		if (sort && CollectionUtils.isNotEmpty(stepPolicyDetails)) {
			stepPolicyDetails = stepPolicyDetails.stream()
					.sorted(Comparator.comparing(FinanceStepPolicyDetail::getStepSpecifier)
							.thenComparingInt(FinanceStepPolicyDetail::getStepNo))
					.collect(Collectors.toList());
		}
		this.stepPolicyDetails = stepPolicyDetails;
	}

	public List<FinanceScheduleDetail> getFinanceScheduleDetails() {
		return financeScheduleDetails;
	}

	public void setFinanceScheduleDetails(List<FinanceScheduleDetail> financeScheduleDetails) {
		this.financeScheduleDetails = financeScheduleDetails;
	}

	public List<FinanceDisbursement> getDisbursementDetails() {
		return disbursementDetails;
	}

	public void setDisbursementDetails(List<FinanceDisbursement> disbursementDetails) {
		if (disbursementDetails != null && !disbursementDetails.isEmpty()) {
			Collections.sort(disbursementDetails, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					return DateUtils.truncatedCompareTo(detail1.getDisbDate(), detail2.getDisbDate(), Calendar.DATE);
				}
			});
		}
		this.disbursementDetails = disbursementDetails;
	}

	public List<RepayInstruction> getRepayInstructions() {
		return repayInstructions;
	}

	public void setRepayInstructions(List<RepayInstruction> repayInstructions) {

		if (repayInstructions != null && !repayInstructions.isEmpty()) {
			Collections.sort(repayInstructions, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					return DateUtils.truncatedCompareTo(detail1.getRepayDate(), detail2.getRepayDate(), Calendar.DATE);
				}
			});
		}
		this.repayInstructions = repayInstructions;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public void setErrorDetail(ErrorDetail errorDetail) {

		if (errorDetail != null) {
			if (errorDetails == null) {
				errorDetails = new ArrayList<>();
			}
			this.errorDetails.add(errorDetail);
		}
	}

	public Map<Date, FinanceScheduleDetail> getScheduleMap() {
		return scheduleMap;
	}

	public void setScheduleMap(Map<Date, FinanceScheduleDetail> scheduleMap) {
		this.scheduleMap = scheduleMap;
	}

	public void setScheduleMap(FinanceScheduleDetail scheduleDetail) {

		if (scheduleMap == null) {
			scheduleMap = new HashMap<Date, FinanceScheduleDetail>();
		} else {
			if (scheduleMap.containsKey(scheduleDetail.getSchDate())) {
				scheduleMap.remove(scheduleDetail.getSchDate());
			}
		}

		scheduleMap.put(scheduleDetail.getSchDate(), scheduleDetail);
	}

	/**
	 * Method for Setting Finance Step Policies Using Step Policy Detail List
	 * 
	 * @param stepPolicyList
	 */
	public void resetStepPolicyDetails(List<StepPolicyDetail> stepPolicyList) {
		stepPolicyDetails = new ArrayList<FinanceStepPolicyDetail>();
		if (stepPolicyList != null && !stepPolicyList.isEmpty()) {
			for (StepPolicyDetail stepPolicyDetail : stepPolicyList) {

				FinanceStepPolicyDetail detail = new FinanceStepPolicyDetail();
				detail.setStepNo(stepPolicyDetail.getStepNumber());
				detail.setTenorSplitPerc(stepPolicyDetail.getTenorSplitPerc());
				detail.setRateMargin(stepPolicyDetail.getRateMargin());
				detail.setEmiSplitPerc(stepPolicyDetail.getEmiSplitPerc());

				stepPolicyDetails.add(detail);
			}
		}
		setStepPolicyDetails(stepPolicyDetails);
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public FinanceType getFinanceType() {
		return financeType;
	}

	public FinanceSummary getFinanceSummary() {
		return financeSummary;
	}

	public void setFinanceSummary(FinanceSummary financeSummary) {
		this.financeSummary = financeSummary;
	}

	public boolean isSchduleGenerated() {
		return schduleGenerated;
	}

	public void setSchduleGenerated(boolean schduleGenerated) {
		this.schduleGenerated = schduleGenerated;
	}

	public void setFeeRules(List<FeeRule> feeRules) {
		this.feeRules = feeRules;
	}

	public List<FeeRule> getFeeRules() {
		return feeRules;
	}

	public List<FinFeeDetail> getFinFeeDetailList() {
		return finFeeDetailList;
	}

	public void setFinFeeDetailList(List<FinFeeDetail> finFeeDetailList) {
		this.finFeeDetailList = finFeeDetailList;
	}

	public List<FinanceRepayments> getRepayDetails() {
		return repayDetails;
	}

	public void setRepayDetails(List<FinanceRepayments> repayDetails) {
		this.repayDetails = repayDetails;
	}

	public void setPenaltyDetails(List<OverdueChargeRecovery> penaltyDetails) {
		this.penaltyDetails = penaltyDetails;
	}

	public List<OverdueChargeRecovery> getPenaltyDetails() {
		return penaltyDetails;
	}

	public void setAccrueValue(BigDecimal accrueValue) {
		this.accrueValue = accrueValue;
	}

	public BigDecimal getAccrueValue() {
		return accrueValue;
	}

	public FinODPenaltyRate getFinODPenaltyRate() {
		return finODPenaltyRate;
	}

	public void setFinODPenaltyRate(FinODPenaltyRate finODPenaltyRate) {
		this.finODPenaltyRate = finODPenaltyRate;
	}

	public void setFinPftSuspended(boolean finPftSuspended) {
		this.finPftSuspended = finPftSuspended;
	}

	public boolean isFinPftSuspended() {
		return finPftSuspended;
	}

	public void setFinSuspDate(Date finSuspDate) {
		this.finSuspDate = finSuspDate;
	}

	public Date getFinSuspDate() {
		return finSuspDate;
	}

	public String getFeeEvent() {
		return feeEvent;
	}

	public void setFeeEvent(String feeEvent) {
		this.feeEvent = feeEvent;
	}

	public List<RateInstruction> getRateInstruction() {
		return rateInstruction;
	}

	public void setRateInstruction(List<RateInstruction> rateInstruction) {
		this.rateInstruction = rateInstruction;
	}

	public boolean isOdLimitReset() {
		return odLimitReset;
	}

	public void setOdLimitReset(boolean odLimitReset) {
		this.odLimitReset = odLimitReset;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return finServiceInstructions;
	}

	public void setFinServiceInstructions(List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public void setFinServiceInstruction(FinServiceInstruction finServiceInstruction) {

		if (finServiceInstruction != null) {
			if (finServiceInstructions == null) {
				finServiceInstructions = new ArrayList<FinServiceInstruction>();
			}
			this.finServiceInstructions.add(finServiceInstruction);
		}
	}

	public List<OverdraftScheduleDetail> getOverdraftScheduleDetails() {
		return overdraftScheduleDetails;
	}

	public void setOverdraftScheduleDetails(List<OverdraftScheduleDetail> overdraftScheduleDetail) {
		this.overdraftScheduleDetails = overdraftScheduleDetail;
	}

	public List<Integer> getPlanEMIHmonths() {
		return planEMIHmonths;
	}

	public void setPlanEMIHmonths(List<Integer> planEMIHmonths) {
		this.planEMIHmonths = planEMIHmonths;
	}

	public List<Date> getPlanEMIHDates() {
		return planEMIHDates;
	}

	public void setPlanEMIHDates(List<Date> planEMIHDates) {
		this.planEMIHDates = planEMIHDates;
	}

	public List<FinODDetails> getFinODDetails() {
		return finODDetails;
	}

	public void setFinODDetails(List<FinODDetails> finODDetails) {
		this.finODDetails = finODDetails;
	}

	public List<BaseRate> getBaseRates() {
		return baseRates;
	}

	public void setBaseRates(List<BaseRate> baseRates) {
		this.baseRates = baseRates;
	}

	public List<ReceiptAllocationDetail> getReceiptAllocationList() {
		return receiptAllocationList;
	}

	public void setReceiptAllocationList(List<ReceiptAllocationDetail> receiptAllocationList) {
		this.receiptAllocationList = receiptAllocationList;
	}

	public List<SplRate> getSplRates() {
		return splRates;
	}

	public void setSplRates(List<SplRate> splRates) {
		this.splRates = splRates;
	}

	public List<FinPlanEmiHoliday> getApiPlanEMIHDates() {
		return apiPlanEMIHDates;
	}

	public void setApiPlanEMIHDates(List<FinPlanEmiHoliday> apiPlanEMIHDates) {
		this.apiPlanEMIHDates = apiPlanEMIHDates;
	}

	public List<FinPlanEmiHoliday> getApiPlanEMIHmonths() {
		return apiPlanEMIHmonths;
	}

	public void setApiplanEMIHmonths(List<FinPlanEmiHoliday> apiPlanEMIHmonths) {
		this.apiPlanEMIHmonths = apiPlanEMIHmonths;
	}

	public List<VASRecording> getVasRecordingList() {
		return vasRecordingList;
	}

	public void setVasRecordingList(List<VASRecording> vasRecordingList) {
		this.vasRecordingList = vasRecordingList;
	}

	public List<FinFeeDetail> getForeClosureFees() {
		return foreClosureFees;
	}

	public void setForeClosureFees(List<FinFeeDetail> foreClosureFees) {
		this.foreClosureFees = foreClosureFees;
	}

	public List<FinFeeDetail> getFeeDues() {
		return feeDues;
	}

	public void setFeeDues(List<FinFeeDetail> feeDues) {
		this.feeDues = feeDues;
	}

	public BigDecimal getOutstandingPri() {
		return outstandingPri;
	}

	public void setOutstandingPri(BigDecimal outstandingPri) {
		this.outstandingPri = outstandingPri;
	}

	public List<FinReceiptHeader> getImdReceipts() {
		return imdReceipts;
	}

	public void setImdReceipts(List<FinReceiptHeader> imdReceipts) {
		this.imdReceipts = imdReceipts;
	}

	public List<FinFeeReceipt> getFinFeeReceipts() {
		return finFeeReceipts;
	}

	public void setFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts) {
		this.finFeeReceipts = finFeeReceipts;
	}

	public SubventionDetail getSubventionDetail() {
		return subventionDetail;
	}

	public void setSubventionDetail(SubventionDetail subventionDetail) {
		this.subventionDetail = subventionDetail;
	}

	public List<FinIRRDetails> getiRRDetails() {
		return iRRDetails;
	}

	public void setiRRDetails(List<FinIRRDetails> iRRDetails) {
		this.iRRDetails = iRRDetails;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public boolean isUpfrontAuto() {
		return isUpfrontAuto;
	}

	public void setUpfrontAuto(boolean isUpfrontAuto) {
		this.isUpfrontAuto = isUpfrontAuto;
	}

	public BigDecimal getPftChg() {
		return pftChg;
	}

	public void setPftChg(BigDecimal pftChg) {
		this.pftChg = pftChg;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public boolean isFlexiDisb() {
		return isFlexiDisb;
	}

	public void setFlexiDisb(boolean isFlexiDisb) {
		this.isFlexiDisb = isFlexiDisb;
	}

	public List<SanctionCondition> getSanctionConditions() {
		return sanctionConditions;
	}

	public void setSanctionConditions(List<SanctionCondition> sanctionConditions) {
		this.sanctionConditions = sanctionConditions;
	}

	public List<ExposureLinking> getExposureLinkings() {
		return exposureLinkings;
	}

	public void setExposureLinkings(List<ExposureLinking> exposureLinkings) {
		this.exposureLinkings = exposureLinkings;
	}

	public FinanceProfitDetail getFinPftDeatil() {
		return finPftDeatil;
	}

	public void setFinPftDeatil(FinanceProfitDetail finPftDeatil) {
		this.finPftDeatil = finPftDeatil;
	}

	public Map<String, Object> getGstExecutionMap() {
		return gstExecutionMap;
	}

	public void setGstExecutionMap(Map<String, Object> gstExecutionMap) {
		this.gstExecutionMap = gstExecutionMap;
	}

	public FinServiceInstruction getFinServiceInstruction() {
		FinServiceInstruction finServiceInstruction = null;
		if (finServiceInstructions != null && !finServiceInstructions.isEmpty()) {
			finServiceInstruction = finServiceInstructions.get(0);
		}
		return finServiceInstruction;
	}

	public String getOldFinReference() {
		return oldFinReference;
	}

	public void setOldFinReference(String oldFinReference) {
		this.oldFinReference = oldFinReference;
	}

	public List<LowerTaxDeduction> getLowerTaxDeductionDetails() {
		return lowerTaxDeductionDetails;
	}

	public void setLowerTaxDeductionDetails(List<LowerTaxDeduction> lowerTaxDeductionDetails) {
		this.lowerTaxDeductionDetails = lowerTaxDeductionDetails;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public List<IRRScheduleDetail> getIrrSDList() {
		return irrSDList;
	}

	public void setIrrSDList(List<IRRScheduleDetail> irrSDList) {
		this.irrSDList = irrSDList;
	}

	public RestructureDetail getRestructureDetail() {
		return restructureDetail;
	}

	public void setRestructureDetail(RestructureDetail restructureDetail) {
		this.restructureDetail = restructureDetail;
	}

	public FinMaintainInstruction getFinMaintainInstruction() {
		return finMaintainInstruction;
	}

	public void setFinMaintainInstruction(FinMaintainInstruction finMaintainInstruction) {
		this.finMaintainInstruction = finMaintainInstruction;
	}

	public boolean isEqualInst() {
		return equalInst;
	}

	public void setEqualInst(boolean equalInst) {
		this.equalInst = equalInst;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

}
