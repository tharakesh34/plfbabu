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

import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;

public class FinScheduleData {

	private String finReference = null;	
	private boolean schduleGenerated = false;
	private FinanceType financeType;
	private FinanceMain financeMain = null;
	private HashMap<Date, FinanceScheduleDetail> scheduleMap;
	private List<FinanceStepPolicyDetail> stepPolicyDetails = new ArrayList<FinanceStepPolicyDetail>();
	private List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<FinanceScheduleDetail>();
	private List<FinanceDisbursement> disbursementDetails = new ArrayList<FinanceDisbursement>();
	private List<RepayInstruction> repayInstructions = new ArrayList<RepayInstruction>();
	private List<DefermentHeader> defermentHeaders = new ArrayList<DefermentHeader>();
	private List<DefermentDetail> defermentDetails = new ArrayList<DefermentDetail>();
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
	private FinanceSummary financeSummary;
	private List<FeeRule> feeRules = new ArrayList<FeeRule>();	
	private List<FinanceRepayments> repayDetails = new ArrayList<FinanceRepayments>();	
	private List<OverdueChargeRecovery> penaltyDetails = new ArrayList<OverdueChargeRecovery>();	
	private BigDecimal accrueValue;
	private FinODPenaltyRate finODPenaltyRate;
	private boolean finPftSuspended; 
	private Date finSuspDate; 

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
		if (disbursementDetails != null && disbursementDetails.size() > 0) {
			Collections.sort(disbursementDetails, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					if (detail1.getDisbDate().after(detail2.getDisbDate())) {
						return 1;
					}
					return 0;
				}
			});
		}
		this.disbursementDetails = disbursementDetails;
	}

	public List<RepayInstruction> getRepayInstructions() {
		return repayInstructions;
	}
	public void setRepayInstructions(List<RepayInstruction> repayInstructions) {
		this.repayInstructions = repayInstructions;
	}
	
	public List<DefermentHeader> getDefermentHeaders() {
		return defermentHeaders;
	}
	public void setDefermentHeaders(List<DefermentHeader> defermentHeaders) {
		this.defermentHeaders = defermentHeaders;
	}

	public List<DefermentDetail> getDefermentDetails() {
		return defermentDetails;
	}
	public void setDefermentDetails(List<DefermentDetail> defermentDetails) {
		this.defermentDetails = defermentDetails;
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
		this.financeMain.setFinCcy(financeType.getFinCcy());
		//this.financeMain.setDisbCcy(financeType.getFinCcy());
		this.financeMain.setLovDescFinCcyName(financeType.getLovDescFinCcyName());
		//this.financeMain.setLovDescDisbCcyName(financeType.getLovDescFinCcyName());
		//this.financeMain.setCcyConversionRate(BigDecimal.ONE);
		this.financeMain.setProfitDaysBasis(financeType.getFinDaysCalType());
		this.financeMain.setLovDescProfitDaysBasisName(financeType.getLovDescFinDaysCalTypeName());
		this.financeMain.setScheduleMethod(financeType.getFinSchdMthd());
		this.financeMain.setLovDescScheduleMethodName(financeType.getLovDescFinSchdMthdName());
		this.financeMain.setLovDescFinFormatter(financeType.getLovDescFinFormetter());
		//this.financeMain.setLovDescDisbCcyFormatter(financeType.getLovDescFinFormetter());
		this.financeMain.setFinStartDate((Date)SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		this.financeMain.setDepreciationFrq(financeType.getFinDepreciationFrq());

		//Grace period details

		if(financeType.isFInIsAlwGrace()){
			
			//Default Grace Period Group box UnVisible by setting Allow grace period to FALSE
			this.financeMain.setAllowGrcPeriod(false);
			
			this.financeMain.setGraceBaseRate(financeType.getFinGrcBaseRate());
			this.financeMain.setLovDescGraceBaseRateName(financeType.getLovDescFinGrcBaseRateName());
			this.financeMain.setGraceSpecialRate(financeType.getFinGrcSplRate());
			this.financeMain.setLovDescGraceSpecialRateName(financeType.getLovDescFinGrcSplRateName());
			this.financeMain.setGrcPftRate(financeType.getFinGrcIntRate());
			this.financeMain.setGrcPftFrq(financeType.getFinGrcDftIntFrq());
			if (FrequencyUtil.validateFrequency(financeType.getFinGrcDftIntFrq()) == null) {
				this.financeMain.setNextGrcPftDate(FrequencyUtil.getNextDate(financeType.getFinGrcDftIntFrq(), 1, 
						this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
			}
			this.financeMain.setAllowGrcPftRvw(financeType.isFinGrcIsRvwAlw());
			this.financeMain.setGrcPftRvwFrq(financeType.getFinGrcRvwFrq());
			if (FrequencyUtil.validateFrequency(financeType.getFinGrcRvwFrq()) == null) {
				this.financeMain.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(financeType.getFinGrcRvwFrq(), 1,
						this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
			}
			this.financeMain.setAllowGrcCpz(financeType.isFinGrcIsIntCpz());
			this.financeMain.setGrcCpzFrq(financeType.getFinGrcCpzFrq());
			if (FrequencyUtil.validateFrequency(financeType.getFinGrcCpzFrq()) == null) {
				this.financeMain.setNextGrcCpzDate(FrequencyUtil.getNextDate(financeType.getFinGrcCpzFrq(), 1,
						this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
			}
			this.financeMain.setCpzAtGraceEnd(financeType.isFinIsIntCpzAtGrcEnd());
			this.financeMain.setGrcRateBasis(financeType.getFinGrcRateType().substring(0, 1));
			this.financeMain.setAllowGrcRepay(financeType.isFinIsAlwGrcRepay());
			this.financeMain.setGrcSchdMthd(financeType.getFinGrcSchdMthd());
			this.financeMain.setGrcMargin(financeType.getFinGrcMargin());
			this.financeMain.setFinGrcRvwRateApplFor(financeType.getFinGrcRvwRateApplFor());
		}

		//RepaymentDetails
		this.financeMain.setNumberOfTerms(financeType.getFinDftTerms());
		this.financeMain.setRepayBaseRate(financeType.getFinBaseRate());
		this.financeMain.setLovDescRepayBaseRateName(financeType.getLovDescFinBaseRateName());
		this.financeMain.setRepaySpecialRate(financeType.getFinSplRate());
		this.financeMain.setLovDescRepaySpecialRateName(financeType.getLovDescFinSplRateName());
		this.financeMain.setRepayMargin(financeType.getFinMargin());
		this.financeMain.setRepayProfitRate(financeType.getFinIntRate());
		this.financeMain.setRepayFrq(financeType.getFinRpyFrq());
		if (FrequencyUtil.validateFrequency(financeType.getFinRpyFrq()) == null) {
			this.financeMain.setNextRepayDate(FrequencyUtil.getNextDate(financeType.getFinRpyFrq(), 1,
					this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
		}
		this.financeMain.setRepayPftFrq(financeType.getFinDftIntFrq());
		if (FrequencyUtil.validateFrequency(financeType.getFinDftIntFrq()) == null) {
			this.financeMain.setNextRepayPftDate(FrequencyUtil.getNextDate(financeType.getFinDftIntFrq(), 1,
					this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
		}
		this.financeMain.setAllowRepayRvw(financeType.isFinIsRvwAlw());
		this.financeMain.setRepayRvwFrq(financeType.getFinRvwFrq());
		if (FrequencyUtil.validateFrequency(financeType.getFinRvwFrq()) == null) {
			this.financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(financeType.getFinRvwFrq(), 1,
					this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
		}
		this.financeMain.setAllowRepayCpz(financeType.isFinIsIntCpz());
		this.financeMain.setRepayCpzFrq(financeType.getFinCpzFrq());
		if (FrequencyUtil.validateFrequency(financeType.getFinCpzFrq()) == null) {
			this.financeMain.setNextRepayCpzDate(FrequencyUtil.getNextDate(financeType.getFinCpzFrq(), 1, 
					this.financeMain.getFinStartDate(), "A", false).getNextFrequencyDate());
		}
		this.financeMain.setDownPayment(financeType.getFinMinDownPayAmount());
		this.financeMain.setRepayRateBasis(financeType.getFinRateType().substring(0, 1));
		this.financeMain.setRecalType(financeType.getFinSchCalCodeOnRvw());
		this.financeMain.setEqualRepay(financeType.isFinFrEqrepayment());
		this.financeMain.setNewRecord(true);
		this.financeMain.setRecordType("");
		this.financeMain.setLovDescFinAlwDeferment(financeType.isFinIsAlwDifferment());
		this.financeMain.setLovDescFinMaxAmt(financeType.getFinMaxAmount());
		this.financeMain.setLovDescFinMinAmt(financeType.getFinMinAmount());
		this.financeMain.setLovDescMinDwnPayPercent(financeType.getFinMinDownPayAmount());
		this.financeMain.setLovDescDwnPayReq(financeType.isFinIsDwPayRequired());

		this.financeMain.setLovDescAssetCodeName(financeType.getLovDescAssetCodeName());
		this.financeMain.setLovDescIsSchdGenerated(false);
		this.financeMain.setDefferments(financeType.getFinMaxDifferment());
		this.financeMain.setFrqDefferments(financeType.getFinMaxFrqDifferment());
		this.financeMain.setGrcAlwIndRate(financeType.isFinGrcAlwIndRate());
		this.financeMain.setAlwIndRate(financeType.isFinAlwIndRate());
		this.financeMain.setGrcIndBaseRate(financeType.getFinGrcIndBaseRate());
		this.financeMain.setLovDescGrcIndBaseRateName(financeType.getLovDescFinGrcIndBaseRateName());
		this.financeMain.setIndBaseRate(financeType.getFinIndBaseRate());
		this.financeMain.setLovDescIndBaseRateName(financeType.getLovDescFinIndBaseRateName());
		this.financeMain.setFinRvwRateApplFor(financeType.getFinRvwRateApplFor());
		this.financeMain.setFinRepayPftOnFrq(financeType.isFinRepayPftOnFrq());
		this.financeMain.setFinRepayMethod(financeType.getFInRepayMethod());
		
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

	public HashMap<Date, ArrayList<DefermentDetail>> getDefermentMap() {
		HashMap<Date, ArrayList<DefermentDetail>> defMap=new HashMap<Date, ArrayList<DefermentDetail>>();
		for (int i = 0; i < this.defermentDetails.size(); i++) {
			
			if(defMap.containsKey(this.defermentDetails.get(i).getDeferedRpyDate())){
				ArrayList<DefermentDetail> penaltyDetailList = defMap.get(this.defermentDetails.get(i).getDeferedRpyDate());
				penaltyDetailList.add(this.defermentDetails.get(i));
				defMap.put(this.defermentDetails.get(i).getDeferedRpyDate(), penaltyDetailList);
			}else{
				ArrayList<DefermentDetail> penaltyDetailList = new ArrayList<DefermentDetail>();
				penaltyDetailList.add(this.defermentDetails.get(i));
				defMap.put(this.defermentDetails.get(i).getDeferedRpyDate(), penaltyDetailList);
			}
		}
		return defMap;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinScheduleData) {
			FinScheduleData finScheduleData = (FinScheduleData) obj;
			return equals(finScheduleData);
		}
		return false;
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

}
