package com.pennant.app.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class FinEODEvent implements Serializable {

	private static final long			serialVersionUID		= 1183720618731771888L;
	private FinanceMain					financeMain				= new FinanceMain();
	private FinanceType					finType					= new FinanceType();
	private Map<Date, Integer>			datesMap;
	private List<FinanceScheduleDetail>	financeScheduleDetails	= new ArrayList<FinanceScheduleDetail>(1);
	private List<RepayInstruction>		RepayInstructions		= new ArrayList<RepayInstruction>(1);
	private FinanceProfitDetail			finProfitDetail			= new FinanceProfitDetail();
	private List<FinODDetails>			finODDetails			= new ArrayList<FinODDetails>(1);
	private List<OverdueChargeRecovery>	odcRecoveries			= new ArrayList<OverdueChargeRecovery>(1);
	private FinODPenaltyRate			penaltyrate;
	private List<FinanceDisbursement>	financeDisbursements	= new ArrayList<FinanceDisbursement>(1);
	private List<FinFeeScheduleDetail>	finFeeScheduleDetails	= new ArrayList<FinFeeScheduleDetail>(1);
	private List<FinSchFrqInsurance>	finSchFrqInsurances		= new ArrayList<FinSchFrqInsurance>(1);
	private List<PresentmentDetail>		presentmentDetails		= new ArrayList<PresentmentDetail>(1);
	private List<ReturnDataSet>			returnDataSet			= new ArrayList<ReturnDataSet>(1);
	private Date						eventFromDate;
	private Date						eventToDate;
	private Date						recalFromDate;
	private Date						recalToDate;
	private String						recalType;
	private String						recalSchdMethod;
	private String						rateOnChgDate;
	private boolean						rateReviewExist			= false;
	private boolean						pastDueExist			= false;
	private boolean						disbExist				= false;
	private boolean						instDueExist			= false;
	private boolean						feeDueExist				= false;
	private boolean						insuranceDueExist		= false;
	private boolean						checkPresentment		= false;

	private boolean						updFinMain				= false;
	private boolean						updFinSchdForRateRvw	= false;
	private boolean						updFinPft				= false;
	private boolean						updRepayInstruct		= false;
	private List<String>				finMainUpdateFields		= new ArrayList<String>(1);

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceType getFinType() {
		return finType;
	}

	public void setFinType(FinanceType finType) {
		this.finType = finType;
	}

	public Map<Date, Integer> getDatesMap() {
		return datesMap;
	}

	public void setDatesMap(Map<Date, Integer> datesMap) {
		this.datesMap = datesMap;
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

	public boolean isPastDueExist() {
		return pastDueExist;
	}

	public void setPastDueExist(boolean pastDueExist) {
		this.pastDueExist = pastDueExist;
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

	public boolean isUpdFinPft() {
		return updFinPft;
	}

	public void setUpdFinPft(boolean updFinPft) {
		this.updFinPft = updFinPft;
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

	public List<FinSchFrqInsurance> getFinSchFrqInsurances() {
		return finSchFrqInsurances;
	}

	public void setFinSchFrqInsurances(List<FinSchFrqInsurance> finSchFrqInsurances) {
		this.finSchFrqInsurances = finSchFrqInsurances;
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

	public boolean isDisbExist() {
		return disbExist;
	}

	public void setDisbExist(boolean disbExist) {
		this.disbExist = disbExist;
	}

	public boolean isInstDueExist() {
		return instDueExist;
	}

	public void setInstDueExist(boolean instDueExist) {
		this.instDueExist = instDueExist;
	}

	public boolean isFeeDueExist() {
		return feeDueExist;
	}

	public void setFeeDueExist(boolean feeDueExist) {
		this.feeDueExist = feeDueExist;
	}

	public boolean isInsuranceDueExist() {
		return insuranceDueExist;
	}

	public void setInsuranceDueExist(boolean insuranceDueExist) {
		this.insuranceDueExist = insuranceDueExist;
	}

	public boolean isCheckPresentment() {
		return checkPresentment;
	}

	public void setCheckPresentment(boolean checkPresentment) {
		this.checkPresentment = checkPresentment;
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

}
