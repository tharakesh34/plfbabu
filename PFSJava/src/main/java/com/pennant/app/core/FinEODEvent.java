package com.pennant.app.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class FinEODEvent implements Serializable {

	private static final long			serialVersionUID		= 1183720618731771888L;
	private FinanceMain					financeMain				= new FinanceMain();
	private FinanceType					finType					= new FinanceType();
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
	private List<Provision>				provisions				= new ArrayList<Provision>(1);
	private Date						eventFromDate;
	private Date						eventToDate;
	private Date						recalFromDate;
	private Date						recalToDate;
	private String						recalType;
	private String						recalSchdMethod;
	private String						rateOnChgDate;

	private boolean						rateReviewExist			= false;

	private int							idxPD					= -1;
	private int							idxGrcCpz				= -1;
	private int							idxGrcPft				= -1;
	private int							idxGrcPftRvw			= -1;
	private int							idxRpyCpz				= -1;
	private int							idxRpyPft				= -1;
	private int							idxRpyPftRvw			= -1;
	private int							idxRpy					= -1;
	private int							idxDisb					= -1;
	private int							idxDue					= -1;
	private int							idxPresentment			= -1;

	private boolean						updFinMain				= false;
	private boolean						updFinSchdForRateRvw	= false;
	private boolean						updRepayInstruct		= false;
	private boolean						updLBDPostings			= false;
	private boolean						updMonthEndPostings		= false;
	private List<String>				finMainUpdateFields		= new ArrayList<String>(1);
	
	private String						aMZMethod;
	List<ProjectedAmortization> 		projectedAMZList 		= new ArrayList<ProjectedAmortization>(1);
	List<ProjectedAccrual> 				ProjectedAccrualList 	= new ArrayList<ProjectedAccrual>(1);


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

	public String getAMZMethod() {
		return aMZMethod;
	}
	
	public void setAMZMethod(String aMZMethod) {
		this.aMZMethod = aMZMethod;
	}

	public List<ProjectedAmortization> getProjectedAMZList() {
		return projectedAMZList;
	}

	public void setProjectedAMZList(List<ProjectedAmortization> projectedAMZList) {
		this.projectedAMZList = projectedAMZList;
	}
	public List<ProjectedAccrual> getProjectedAccrualList() {
		return ProjectedAccrualList;
	}

	public void setProjectedAccrualList(List<ProjectedAccrual> projectedAccrualList) {
		ProjectedAccrualList = projectedAccrualList;
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
		this.finSchFrqInsurances.clear();
		this.presentmentDetails.clear();
		this.returnDataSet.clear();
		this.provisions.clear();
		this.projectedAMZList.clear();
		this.ProjectedAccrualList.clear();
	}
}
