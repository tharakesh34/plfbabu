package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;

public class FinanceWriteoffHeader implements Serializable {
	
    private static final long serialVersionUID = -1477748770396649402L;
    
	//Finance Details
    private String finReference;
    private FinanceWriteoff financeWriteoff;
	private FinanceMain financeMain;
	private List<FinanceScheduleDetail> scheduleDetails = new ArrayList<FinanceScheduleDetail>();
	private List<FinanceDisbursement> disbursementDetails = new ArrayList<FinanceDisbursement>();
	private FinanceType financeType;
	private List<DefermentDetail> defermentDetails = new ArrayList<DefermentDetail>();
	private List<FeeRule> feeRules = new ArrayList<FeeRule>();
	private List<FinanceRepayments> financeRepayments = new ArrayList<FinanceRepayments>();
	private List<OverdueChargeRecovery> penaltyDetails = new ArrayList<OverdueChargeRecovery>();
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceMain getFinanceMain() {
    	return financeMain;
    }
	public String getFinReference() {
    	return finReference;
    }
	
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	public void setFinanceMain(FinanceMain financeMain) {
    	this.financeMain = financeMain;
    }
	
	public List<FinanceScheduleDetail> getScheduleDetails() {
    	return scheduleDetails;
    }
	public void setScheduleDetails(List<FinanceScheduleDetail> scheduleDetails) {
    	this.scheduleDetails = scheduleDetails;
    }
	
	public List<DefermentDetail> getDefermentDetails() {
    	return defermentDetails;
    }
	public void setDefermentDetails(List<DefermentDetail> defermentDetails) {
    	this.defermentDetails = defermentDetails;
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
	
	public List<FeeRule> getFeeRules() {
    	return feeRules;
    }
	public void setFeeRules(List<FeeRule> feeRules) {
    	this.feeRules = feeRules;
    }
	
	public List<FinanceRepayments> getFinanceRepayments() {
    	return financeRepayments;
    }
	public void setFinanceRepayments(List<FinanceRepayments> financeRepayments) {
    	this.financeRepayments = financeRepayments;
    }
	
	public List<OverdueChargeRecovery> getPenaltyDetails() {
    	return penaltyDetails;
    }
	public void setPenaltyDetails(List<OverdueChargeRecovery> penaltyDetails) {
    	this.penaltyDetails = penaltyDetails;
    }
	
	public FinanceWriteoff getFinanceWriteoff() {
	    return financeWriteoff;
    }
	public void setFinanceWriteoff(FinanceWriteoff financeWriteoff) {
	    this.financeWriteoff = financeWriteoff;
    }
	
	public List<FinanceDisbursement> getDisbursementDetails() {
    	return disbursementDetails;
    }
	public void setDisbursementDetails(List<FinanceDisbursement> disbursementDetails) {
    	this.disbursementDetails = disbursementDetails;
    }
	
	public FinanceType getFinanceType() {
    	return financeType;
    }
	public void setFinanceType(FinanceType financeType) {
    	this.financeType = financeType;
    }
	
	
	
}
