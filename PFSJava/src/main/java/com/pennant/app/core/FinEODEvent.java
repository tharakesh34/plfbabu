package com.pennant.app.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pff.core.model.AbstractEntity;

public class FinEODEvent extends AbstractEntity {

	/**
	 * 
	 */
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
	private Provision					provision				= new Provision();
	private boolean						rateReview				= false;
	private boolean						odFiance				= false;
	private Date						eventFromDate;
	private Date						eventToDate;
	private Date						recalFromDate;
	private Date						recalToDate;
	private String						recalType;
	private String						recalSchdMethod;
	private String						rateOnChgDate;
	private boolean						updFinMain				= false;
	private boolean						updFinSchedule			= false;
	private boolean						updFinPft				= false;
	private boolean						updRepayInstruct		= false;
	private boolean						updProvision			= false;

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
	
	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public boolean isRateReview() {
		return rateReview;
	}

	public void setRateReview(boolean rateReview) {
		this.rateReview = rateReview;
	}

	public boolean isOdFiance() {
		return odFiance;
	}

	public void setOdFiance(boolean odFiance) {
		this.odFiance = odFiance;
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

	public boolean isUpdFinSchedule() {
		return updFinSchedule;
	}

	public void setUpdFinSchedule(boolean updFinSchedule) {
		this.updFinSchedule = updFinSchedule;
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

	public boolean isUpdProvision() {
		return updProvision;
	}

	public void setUpdProvision(boolean updProvision) {
		this.updProvision = updProvision;
	}

	public FinODPenaltyRate getPenaltyrate() {
		return penaltyrate;
	}

	public void setPenaltyrate(FinODPenaltyRate penaltyrate) {
		this.penaltyrate = penaltyrate;
	}

}
