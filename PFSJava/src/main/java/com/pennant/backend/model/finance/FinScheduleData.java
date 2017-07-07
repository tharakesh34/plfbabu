/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinScheduleData.java                                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-03-2011    														*
 *                                                                  						*
 * Modified Date    :  22-03-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-03-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;

@XmlType(propOrder = { "finReference", "financeMain", "repayInstructions", "rateInstruction", "finFeeDetailList",
		"feeDues", "foreClosureFees", "insuranceList", "stepPolicyDetails", "financeScheduleDetails",
		"finODPenaltyRate", "apiPlanEMIHmonths", "apiPlanEMIHDates", "finODDetails", "financeSummary",
		"vasRecordingList", "excessAmount", "advPaymentAmount", "outstandingPri", "overduePft", "tdPftAccured",
		"returnStatus" })
@XmlRootElement(name = "financeSchedule")
@XmlAccessorType(XmlAccessType.NONE)
public class FinScheduleData {
	@XmlElement
	private String finReference = null;	
	private boolean schduleGenerated = false;

	private FinanceType financeType;

	@XmlElement(name="financeDetail")
	private FinanceMain financeMain = null;
	private HashMap<Date, FinanceScheduleDetail> scheduleMap;

	@XmlElement(name="step")
	private List<FinanceStepPolicyDetail> stepPolicyDetails = new ArrayList<FinanceStepPolicyDetail>(1);
	
	@XmlElementWrapper(name="schedules")
	@XmlElement(name="schedule")
	private List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<FinanceScheduleDetail>(1);

	private List<OverdraftScheduleDetail> overdraftScheduleDetails = new ArrayList<OverdraftScheduleDetail>(1);
	private List<FinanceDisbursement> disbursementDetails = new ArrayList<FinanceDisbursement>(1);

	@XmlElement(name="repayInstruction")
	private List<RepayInstruction> repayInstructions = new ArrayList<RepayInstruction>(1);
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>(1);
	@XmlElement(name="summary")
	private FinanceSummary financeSummary;
	private List<FeeRule> feeRules = new ArrayList<FeeRule>(1);
	@XmlElementWrapper(name="fees")
	@XmlElement(name="fee")
	private List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>(1);
	private List<FinanceRepayments> repayDetails = new ArrayList<FinanceRepayments>(1);	
	
	private List<OverdueChargeRecovery> penaltyDetails = new ArrayList<OverdueChargeRecovery>(1);	
	private BigDecimal accrueValue  = BigDecimal.ZERO;
	
	@XmlElement(name="overdue")
	private FinODPenaltyRate finODPenaltyRate;
	private boolean finPftSuspended;
	private Date finSuspDate;
	private String feeEvent;

	@XmlElement(name="insurance")
	private List<Insurance> insuranceList = new ArrayList<Insurance>();//DDP : TO BE REMOVED TODO
	private List<FinInsurances> finInsuranceList = new ArrayList<>();
	@XmlElement(name="rateInstruction")
	private List<RateInstruction> rateInstruction = new ArrayList<RateInstruction>();
	private List<FinServiceInstruction> finServiceInstructions = new ArrayList<FinServiceInstruction>();
	private boolean odLimitReset;
	
	private List<Integer> planEMIHmonths = new ArrayList<Integer>();
	
	private List<Date> planEMIHDates = new ArrayList<Date>(); 
	
	private List<BaseRate> baseRates = new ArrayList<BaseRate>();
	private List<SplRate> splRates = new ArrayList<SplRate>();
	@XmlElement(name="planEMIHDates")
	private List<FinPlanEmiHoliday> apiPlanEMIHDates = new ArrayList<FinPlanEmiHoliday>(); 
	@XmlElement(name="planEMIHmonths")
	private List<FinPlanEmiHoliday> apiPlanEMIHmonths = new ArrayList<FinPlanEmiHoliday>(); 
	// Vas Recording Details
	@XmlElementWrapper(name="vas")
	@XmlElement(name="vasRecording")
	private List<VASRecording> vasRecordingList = new ArrayList<VASRecording>(1);
	
	// API specific fields
	@XmlElement(name="overdueCharges")
	private List<FinODDetails> finODDetails = new ArrayList<FinODDetails>(); 
	@XmlElementWrapper(name="foreClosureFees")
	@XmlElement(name="foreClosureFee")
	List<FinFeeDetail> foreClosureFees;
	
	@XmlElementWrapper(name="feeDues")
	@XmlElement(name="feeDue")
	List<FinFeeDetail> feeDues = new ArrayList<FinFeeDetail>(1);
	@XmlElement
	private BigDecimal excessAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal advPaymentAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal outstandingPri = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal overduePft = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal tdPftAccured = BigDecimal.ZERO;
	
	@XmlElement
	private WSReturnStatus returnStatus;
	
	private List<FinReceiptDetail> finReceiptDetails = new ArrayList<FinReceiptDetail>();
	private List<FinFeeReceipt> finFeeReceipts = new ArrayList<FinFeeReceipt>();

	public FinScheduleData() {
		super();
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
		if (disbursementDetails != null && disbursementDetails.size() > 1) {
			Collections.sort(disbursementDetails, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					return DateUtility.compare(detail1.getDisbDate(), detail2.getDisbDate());
				}
			});
		}
		this.disbursementDetails = disbursementDetails;
	}

	public List<RepayInstruction> getRepayInstructions() {
		return repayInstructions;
	}
	public void setRepayInstructions(List<RepayInstruction> repayInstructions) {
		
		if (repayInstructions != null && repayInstructions.size() > 0) {
			Collections.sort(repayInstructions, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					return DateUtility.compare(detail1.getRepayDate(), detail2.getRepayDate());
				}
			});
		}
		this.repayInstructions = repayInstructions;
	}

	public List<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(ArrayList<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public void setErrorDetail(ErrorDetails errorDetail) {

		if(errorDetail!=null){
			if (errorDetails == null) {
				errorDetails = new ArrayList<ErrorDetails>();
			}
			this.errorDetails.add(errorDetail);
		}
	}

	public HashMap<Date, FinanceScheduleDetail> getScheduleMap() {
		return scheduleMap;
	}
	public void setScheduleMap(HashMap<Date, FinanceScheduleDetail> scheduleMap) {
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

	public void setFinanceMain(FinanceMain financeMain, FinanceType financeType) {

		if (this.financeMain == null) {
			WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceMain");
			this.financeMain = new FinanceMain();
			if (workFlowDetails != null) {
				financeMain.setWorkflowId(workFlowDetails.getWorkFlowId());
			}
		} else {
			this.financeMain = financeMain;
		}

		// Basic Details
		this.financeMain.setFinType(financeType.getFinType());
		this.financeMain.setLovDescFinTypeName(financeType.getFinTypeDesc());
		this.financeMain.setPromotionCode(financeType.getPromotionCode());
		this.financeMain.setFinCcy(financeType.getFinCcy());
		this.financeMain.setProfitDaysBasis(financeType.getFinDaysCalType());
		this.financeMain.setScheduleMethod(financeType.getFinSchdMthd());
		this.financeMain.setFinStartDate(DateUtility.getAppDate());
		this.financeMain.setDepreciationFrq(financeType.getFinDepreciationFrq());
		this.financeMain.setTDSApplicable(financeType.isTDSApplicable());
		this.financeMain.setProductCategory(financeType.getProductCategory());
		this.financeMain.setFinCategory(financeType.getFinCategory());
		//Step Policy Details 
		if(financeType.isSteppingMandatory()){
			this.financeMain.setStepFinance(financeType.isStepFinance());
			this.financeMain.setStepPolicy(StringUtils.trimToEmpty(financeType.getDftStepPolicy()).equals(PennantConstants.List_Select) ? "" : financeType.getDftStepPolicy());
			this.financeMain.setLovDescStepPolicyName(StringUtils.trimToEmpty(financeType.getLovDescDftStepPolicyName()));
			this.financeMain.setStepType(financeType.getDftStepPolicyType());
		}else{
			this.financeMain.setStepFinance(false);
		}
		this.financeMain.setManualSchedule(financeType.isManualSchedule());
		//Grace period details

		//Default Grace Period Group box UnVisible by setting Allow grace period to FALSE
		if(financeType.getFinCategory().equals(FinanceConstants.PRODUCT_ISTISNA)){
			this.financeMain.setAllowGrcPeriod(financeType.isFInIsAlwGrace());
		}else{
			this.financeMain.setAllowGrcPeriod(false);
		}
		
		if(this.financeMain.isAllowGrcPeriod()){

			this.financeMain.setGraceBaseRate(financeType.getFinGrcBaseRate());
			this.financeMain.setGraceSpecialRate(financeType.getFinGrcSplRate());
			this.financeMain.setGrcPftRate(financeType.getFinGrcIntRate());
			this.financeMain.setGrcPftFrq(financeType.getFinGrcDftIntFrq());
			this.financeMain.setGrcProfitDaysBasis(financeType.getFinDaysCalType());
			if (StringUtils.isNotEmpty(financeType.getFinGrcDftIntFrq()) &&
					FrequencyUtil.validateFrequency(financeType.getFinGrcDftIntFrq()) == null) {
				this.financeMain.setNextGrcPftDate(FrequencyUtil.getNextDate(financeType.getFinGrcDftIntFrq(), 1, 
						this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
			this.financeMain.setAllowGrcPftRvw(financeType.isFinGrcIsRvwAlw());
			this.financeMain.setGrcPftRvwFrq(financeType.getFinGrcRvwFrq());
			if (StringUtils.isNotEmpty(financeType.getFinGrcRvwFrq()) &&
					FrequencyUtil.validateFrequency(financeType.getFinGrcRvwFrq()) == null) {
				this.financeMain.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(financeType.getFinGrcRvwFrq(), 1,
						this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
			this.financeMain.setAllowGrcCpz(financeType.isFinGrcIsIntCpz());
			this.financeMain.setGrcCpzFrq(financeType.getFinGrcCpzFrq());
			if (StringUtils.isNotEmpty(financeType.getFinGrcCpzFrq()) &&
					FrequencyUtil.validateFrequency(financeType.getFinGrcCpzFrq()) == null) {
				this.financeMain.setNextGrcCpzDate(FrequencyUtil.getNextDate(financeType.getFinGrcCpzFrq(), 1,
						this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
			this.financeMain.setCpzAtGraceEnd(financeType.isFinIsIntCpzAtGrcEnd());
			this.financeMain.setGrcRateBasis(financeType.getFinGrcRateType().substring(0, 1));
			this.financeMain.setAllowGrcRepay(financeType.isFinIsAlwGrcRepay());
			this.financeMain.setGrcSchdMthd(financeType.getFinGrcSchdMthd());
			this.financeMain.setGrcMargin(financeType.getFinGrcMargin());

			this.financeMain.setGrcAdvBaseRate(financeType.getGrcAdvBaseRate());
			this.financeMain.setGrcAdvMargin(financeType.getGrcAdvMargin());
			this.financeMain.setGrcAdvPftRate(financeType.getGrcAdvPftRate());
		}

		//RepaymentDetails
		this.financeMain.setNumberOfTerms(financeType.getFinDftTerms());
		this.financeMain.setRepayBaseRate(financeType.getFinBaseRate());
		this.financeMain.setRepaySpecialRate(financeType.getFinSplRate());
		this.financeMain.setRepayMargin(financeType.getFinMargin());
		this.financeMain.setRepayProfitRate(financeType.getFinIntRate());
		this.financeMain.setRepayFrq(financeType.getFinRpyFrq());
		if (StringUtils.isNotEmpty(financeType.getFinRpyFrq()) &&
				FrequencyUtil.validateFrequency(financeType.getFinRpyFrq()) == null) {
			this.financeMain.setNextRepayDate(FrequencyUtil.getNextDate(financeType.getFinRpyFrq(), 1,
					this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		this.financeMain.setRepayPftFrq(financeType.getFinDftIntFrq());
		if (StringUtils.isNotEmpty(financeType.getFinDftIntFrq()) &&
				FrequencyUtil.validateFrequency(financeType.getFinDftIntFrq()) == null) {
			this.financeMain.setNextRepayPftDate(FrequencyUtil.getNextDate(financeType.getFinDftIntFrq(), 1,
					this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		this.financeMain.setAllowRepayRvw(financeType.isFinIsRvwAlw());
		this.financeMain.setRepayRvwFrq(financeType.getFinRvwFrq());
		this.financeMain.setSchCalOnRvw(financeType.getFinSchCalCodeOnRvw());
		this.financeMain.setPastduePftCalMthd(financeType.getPastduePftCalMthd());
		this.financeMain.setPastduePftMargin(financeType.getPastduePftMargin());
		this.financeMain.setDroppingMethod(financeType.getDroppingMethod());
		this.financeMain.setRateChgAnyDay(financeType.isRateChgAnyDay());
		
		if (StringUtils.isNotEmpty(financeType.getFinRvwFrq()) &&
				FrequencyUtil.validateFrequency(financeType.getFinRvwFrq()) == null) {
			this.financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(financeType.getFinRvwFrq(), 1,
					this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		this.financeMain.setAllowRepayCpz(financeType.isFinIsIntCpz());
		this.financeMain.setRepayCpzFrq(financeType.getFinCpzFrq());
		if (StringUtils.isNotEmpty(financeType.getFinCpzFrq()) &&
				FrequencyUtil.validateFrequency(financeType.getFinCpzFrq()) == null) {
			this.financeMain.setNextRepayCpzDate(FrequencyUtil.getNextDate(financeType.getFinCpzFrq(), 1, 
					this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		this.financeMain.setRepayRateBasis(financeType.getFinRateType().substring(0, 1));
		this.financeMain.setEqualRepay(financeType.isEqualRepayment());
		this.financeMain.setNewRecord(true);
		this.financeMain.setRecordType("");

		this.financeMain.setLovDescIsSchdGenerated(false);
		this.financeMain.setDefferments(financeType.getFinMaxDifferment());
		this.financeMain.setPlanDeferCount(financeType.getPlanDeferCount());
		this.financeMain.setRvwRateApplFor(financeType.getFinRvwRateApplFor());
		this.financeMain.setFinRepayMethod(financeType.getFinRepayMethod());
		this.financeMain.setRpyAdvBaseRate(financeType.getRpyAdvBaseRate());
		this.financeMain.setRpyAdvMargin(financeType.getRpyAdvMargin());
		this.financeMain.setRpyAdvPftRate(financeType.getRpyAdvPftRate());
		this.financeMain.setRolloverFrq(StringUtils.trimToEmpty(financeType.getRollOverFrq()));
		if (StringUtils.isNotEmpty(financeType.getRollOverFrq()) &&
				FrequencyUtil.validateFrequency(financeType.getRollOverFrq()) == null) {
			this.financeMain.setNextRolloverDate(FrequencyUtil.getNextDate(financeType.getRollOverFrq(), financeMain.getNumberOfTerms(),
					this.financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		
		this.financeMain.setAlwBPI(financeType.isAlwBPI());
		this.financeMain.setBpiTreatment(financeType.getBpiTreatment());
		this.financeMain.setPlanEMIHAlw(financeType.isPlanEMIHAlw());
		this.financeMain.setPlanEMIHMethod(financeType.getPlanEMIHMethod());
		this.financeMain.setPlanEMIHLockPeriod(financeType.getPlanEMIHLockPeriod());
		this.financeMain.setPlanEMIHMaxPerYear(financeType.getPlanEMIHMaxPerYear());
		this.financeMain.setPlanEMIHMax(financeType.getPlanEMIHMax());
		this.financeMain.setPlanEMICpz(financeType.isPlanEMICpz());
		this.financeMain.setCalRoundingMode(financeType.getRoundingMode());
		this.financeMain.setRoundingTarget(financeType.getRoundingTarget());
		this.financeMain.setAlwMultiDisb(financeType.isFinIsAlwMD());
		this.financeMain.setUnPlanEMIHLockPeriod(financeType.getUnPlanEMIHLockPeriod());
		this.financeMain.setMaxUnplannedEmi(financeType.getMaxUnplannedEmi());
		this.financeMain.setMaxReAgeHolidays(financeType.getMaxReAgeHolidays());
		this.financeMain.setUnPlanEMICpz(financeType.isUnPlanEMICpz());
		this.financeMain.setReAgeCpz(financeType.isReAgeCpz());

		//overdue Penalty Details
		if(this.finODPenaltyRate == null){
			this.finODPenaltyRate = new FinODPenaltyRate();
		}
		this.finODPenaltyRate.setApplyODPenalty(financeType.isApplyODPenalty());
		this.finODPenaltyRate.setODIncGrcDays(financeType.isODIncGrcDays());
		this.finODPenaltyRate.setODChargeCalOn(financeType.getODChargeCalOn());
		this.finODPenaltyRate.setODGraceDays(financeType.getODGraceDays());
		this.finODPenaltyRate.setODChargeType(financeType.getODChargeType());
		this.finODPenaltyRate.setODChargeAmtOrPerc(financeType.getODChargeAmtOrPerc());
		this.finODPenaltyRate.setODAllowWaiver(financeType.isODAllowWaiver());
		this.finODPenaltyRate.setODMaxWaiverPerc(financeType.getODMaxWaiverPerc());
	}

	/**
	 * Method for Setting Finance Step Policies Using Step Policy Detail List
	 * @param stepPolicyList
	 */
	public void resetStepPolicyDetails(List<StepPolicyDetail> stepPolicyList) {
		stepPolicyDetails = new ArrayList<FinanceStepPolicyDetail>();
		if(stepPolicyList != null && !stepPolicyList.isEmpty()){
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

	public void setErrorDetails(List<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
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
	//	List<FinFeeDetail> finFeeDetailTemp = new ArrayList<FinFeeDetail>();
		//for (FinFeeDetail finFeeDetail : finFeeDetailList) {
		//	if(finFeeDetail.isRcdVisible()){
				//finFeeDetailTemp.add(finFeeDetail);
		//	}
		//}
		return finFeeDetailList;
	}
	public List<FinFeeDetail> getFinFeeDetailActualList() {
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

	public List<Insurance> getInsuranceList() {
		return insuranceList;
	}

	public void setInsuranceList(List<Insurance> insuranceList) {
		this.insuranceList = insuranceList;
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

		if(finServiceInstruction!=null){
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

	public List<FinInsurances> getFinInsuranceList() {
		return finInsuranceList;
	}
	public void setFinInsuranceList(List<FinInsurances> finInsuranceList) {
		this.finInsuranceList = finInsuranceList;
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
	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	public BigDecimal getAdvPaymentAmount() {
		return advPaymentAmount;
	}

	public void setAdvPaymentAmount(BigDecimal advPaymentAmount) {
		this.advPaymentAmount = advPaymentAmount;
	}

	public BigDecimal getOutstandingPri() {
		return outstandingPri;
	}

	public void setOutstandingPri(BigDecimal outstandingPri) {
		this.outstandingPri = outstandingPri;
	}

	public BigDecimal getOverduePft() {
		return overduePft;
	}

	public void setOverduePft(BigDecimal overduePft) {
		this.overduePft = overduePft;
	}

	public BigDecimal getTdPftAccured() {
		return tdPftAccured;
	}

	public void setTdPftAccured(BigDecimal tdPftAccured) {
		this.tdPftAccured = tdPftAccured;
	}

	public List<FinReceiptDetail> getFinReceiptDetails() {
		return finReceiptDetails;
	}

	public void setFinReceiptDetails(List<FinReceiptDetail> finReceiptDetails) {
		this.finReceiptDetails = finReceiptDetails;
	}

	public List<FinFeeReceipt> getFinFeeReceipts() {
		return finFeeReceipts;
	}

	public void setFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts) {
		this.finFeeReceipts = finFeeReceipts;
	}
}
