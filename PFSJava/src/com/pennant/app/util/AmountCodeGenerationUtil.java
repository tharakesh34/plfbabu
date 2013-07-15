/*package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AmountCodeDetail;
import com.pennant.backend.model.rulefactory.DataSet;

public class AmountCodeGenerationUtil {
	
	private static AmountCodeDetail amountCodeDetail;

	public AmountCodeGenerationUtil() {
	}
	
	*//**
	 * Method for Preparation of AmountCode Details Based On Schedule List
	 * @param financeMain
	 * @param financeScheduleDetails
	 * @return
	 *//*
	public static AmountCodeDetail getAmountCodeDetails(FinanceMain financeMain, 
			List<FinanceScheduleDetail> financeScheduleDetails){
		
		amountCodeDetail = new AmountCodeDetail();
		amountCodeDetail.setLastRepayPftDate(financeMain.getLastRepayPftDate());
		amountCodeDetail.setNextRepayPftDate(financeMain.getNextRepayPftDate());
		amountCodeDetail.setLastRepayRvwDate(financeMain.getLastRepayRvwDate());
		amountCodeDetail.setNextRepayRvwDate(financeMain.getNextRepayRvwDate());
		BigDecimal totalPftAmount = new BigDecimal(0);
		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			amountCodeDetail = doPrepareAmountCodes(amountCodeDetail, financeScheduleDetails.get(i));
			totalPftAmount = totalPftAmount.add(financeScheduleDetails.get(i).getProfitSchd());
		}
		amountCodeDetail.setTotalProfit(totalPftAmount);
		return amountCodeDetail;
	}
	
	*//**
	 * Method for Prepare Amount Codes for each Schedule based on Review Periods
	 * (Current Period, Till End of Previous period, Till End, Review Period, Review Period Till Date)
	 * @param amountCodeDetail
	 * @param aScheduleDetail
	 * @return
	 *//*
	private static AmountCodeDetail doPrepareAmountCodes(AmountCodeDetail amountCodeDetail, 
			FinanceScheduleDetail aScheduleDetail){

		//Current Period
		if(aScheduleDetail.getSchDate().after(amountCodeDetail.getLastRepayPftDate()) && 
				(aScheduleDetail.getSchDate().before(amountCodeDetail.getNextRepayPftDate()) || 
						aScheduleDetail.getSchDate().equals(amountCodeDetail.getNextRepayPftDate()))){

			amountCodeDetail.setCPNoOfDays(amountCodeDetail.getCPNoOfDays()+ aScheduleDetail.getNoOfDays());
			amountCodeDetail.setCPProfitCalc(amountCodeDetail.getCPProfitCalc().add(aScheduleDetail.getProfitCalc()));
			amountCodeDetail.setCPProfitSchd(amountCodeDetail.getCPProfitSchd().add(aScheduleDetail.getProfitSchd()));
			amountCodeDetail.setCPPrincipalSchd(amountCodeDetail.getCPPrincipalSchd().add(aScheduleDetail.getPrincipalSchd()));
			amountCodeDetail.setCPRepayAmount(amountCodeDetail.getCPRepayAmount().add(aScheduleDetail.getRepayAmount()));
			amountCodeDetail.setCPDisbAmount(amountCodeDetail.getCPDisbAmount().add(aScheduleDetail.getDisbAmount()));
			amountCodeDetail.setCPDownPaymentAmount(amountCodeDetail.getCPDownPaymentAmount().add(aScheduleDetail.getDownPaymentAmount()));
			amountCodeDetail.setCPCpzAmount(amountCodeDetail.getCPCpzAmount().add(aScheduleDetail.getCpzAmount()));
			amountCodeDetail.setCPDefRepaySchd(amountCodeDetail.getCPDefRepaySchd().add(aScheduleDetail.getDefRepaySchd()));
			amountCodeDetail.setCPDefProfitSchd(amountCodeDetail.getCPDefProfitSchd().add(aScheduleDetail.getDefProfitSchd()));
			amountCodeDetail.setCPDefPrincipalSchd(amountCodeDetail.getCPDefPrincipalSchd().add(aScheduleDetail.getDefPrincipalSchd()));
			amountCodeDetail.setCPSchdPftPaid(amountCodeDetail.getCPSchdPftPaid().add(aScheduleDetail.getSchdPftPaid()));
			amountCodeDetail.setCPSchdPriPaid(amountCodeDetail.getCPSchdPriPaid().add(aScheduleDetail.getSchdPriPaid()));
		}

		//Till End of Previous period
		if((aScheduleDetail.getSchDate().before(amountCodeDetail.getLastRepayPftDate()) ||
				aScheduleDetail.getSchDate().equals(amountCodeDetail.getLastRepayPftDate()))){

			amountCodeDetail.setTPPNoOfDays(amountCodeDetail.getTPPNoOfDays()+ aScheduleDetail.getNoOfDays());
			amountCodeDetail.setTPPProfitCalc(amountCodeDetail.getTPPProfitCalc().add(aScheduleDetail.getProfitCalc()));
			amountCodeDetail.setTPPProfitSchd(amountCodeDetail.getTPPProfitSchd().add(aScheduleDetail.getProfitSchd()));
			amountCodeDetail.setTPPPrincipalSchd(amountCodeDetail.getTPPPrincipalSchd().add(aScheduleDetail.getPrincipalSchd()));
			amountCodeDetail.setTPPRepayAmount(amountCodeDetail.getTPPRepayAmount().add(aScheduleDetail.getRepayAmount()));
			amountCodeDetail.setTPPDisbAmount(amountCodeDetail.getTPPDisbAmount().add(aScheduleDetail.getDisbAmount()));
			amountCodeDetail.setTPPDownPaymentAmount(amountCodeDetail.getTPPDownPaymentAmount().add(aScheduleDetail.getDownPaymentAmount()));
			amountCodeDetail.setTPPCpzAmount(amountCodeDetail.getTPPCpzAmount().add(aScheduleDetail.getCpzAmount()));
			amountCodeDetail.setTPPDefRepaySchd(amountCodeDetail.getTPPDefRepaySchd().add(aScheduleDetail.getDefRepaySchd()));
			amountCodeDetail.setTPPDefProfitSchd(amountCodeDetail.getTPPDefProfitSchd().add(aScheduleDetail.getDefProfitSchd()));
			amountCodeDetail.setTPPDefPrincipalSchd(amountCodeDetail.getTPPDefPrincipalSchd().add(aScheduleDetail.getDefPrincipalSchd()));
			amountCodeDetail.setTPPSchdPftPaid(amountCodeDetail.getTPPSchdPftPaid().add(aScheduleDetail.getSchdPftPaid()));
			amountCodeDetail.setTPPSchdPriPaid(amountCodeDetail.getTPPSchdPriPaid().add(aScheduleDetail.getSchdPriPaid()));
		}

		//Till End
		amountCodeDetail.setTENoOfDays(amountCodeDetail.getTENoOfDays()+ aScheduleDetail.getNoOfDays());
		amountCodeDetail.setTEProfitCalc(amountCodeDetail.getTEProfitCalc().add(aScheduleDetail.getProfitCalc()));
		amountCodeDetail.setTEProfitSchd(amountCodeDetail.getTEProfitSchd().add(aScheduleDetail.getProfitSchd()));
		amountCodeDetail.setTEPrincipalSchd(amountCodeDetail.getTEPrincipalSchd().add(aScheduleDetail.getPrincipalSchd()));
		amountCodeDetail.setTERepayAmount(amountCodeDetail.getTERepayAmount().add(aScheduleDetail.getRepayAmount()));
		amountCodeDetail.setTEDisbAmount(amountCodeDetail.getTEDisbAmount().add(aScheduleDetail.getDisbAmount()));
		amountCodeDetail.setTEDownPaymentAmount(amountCodeDetail.getTEDownPaymentAmount().add(aScheduleDetail.getDownPaymentAmount()));
		amountCodeDetail.setTECpzAmount(amountCodeDetail.getTECpzAmount().add(aScheduleDetail.getCpzAmount()));
		amountCodeDetail.setTEDefRepaySchd(amountCodeDetail.getTEDefRepaySchd().add(aScheduleDetail.getDefRepaySchd()));
		amountCodeDetail.setTEDefProfitSchd(amountCodeDetail.getTEDefProfitSchd().add(aScheduleDetail.getDefProfitSchd()));
		amountCodeDetail.setTEDefPrincipalSchd(amountCodeDetail.getTEDefPrincipalSchd().add(aScheduleDetail.getDefPrincipalSchd()));
		amountCodeDetail.setTESchdPftPaid(amountCodeDetail.getTESchdPftPaid().add(aScheduleDetail.getSchdPftPaid()));
		amountCodeDetail.setTESchdPriPaid(amountCodeDetail.getTESchdPriPaid().add(aScheduleDetail.getSchdPriPaid()));

		//Review Period
		if(aScheduleDetail.getSchDate().after(amountCodeDetail.getLastRepayRvwDate()) && 
				(aScheduleDetail.getSchDate().before(amountCodeDetail.getNextRepayRvwDate()) || 
						aScheduleDetail.getSchDate().equals(amountCodeDetail.getNextRepayRvwDate()))){

			amountCodeDetail.setRPProfitCalc(amountCodeDetail.getRPProfitCalc().add(aScheduleDetail.getProfitCalc()));
			amountCodeDetail.setRPProfitSchd(amountCodeDetail.getRPProfitSchd().add(aScheduleDetail.getProfitSchd()));
			amountCodeDetail.setRPSchdPftPaid(amountCodeDetail.getRPSchdPftPaid().add(aScheduleDetail.getSchdPftPaid()));
		}
		//Review Period Till Date
		if(aScheduleDetail.getSchDate().after(amountCodeDetail.getLastRepayRvwDate()) && 
				(aScheduleDetail.getSchDate().before(amountCodeDetail.getNextRepayPftDate()) || 
						aScheduleDetail.getSchDate().equals(amountCodeDetail.getNextRepayPftDate()))){

			amountCodeDetail.setRPPProfitCalc(amountCodeDetail.getRPPProfitCalc().add(aScheduleDetail.getProfitCalc()));
			amountCodeDetail.setRPPProfitSchd(amountCodeDetail.getRPPProfitSchd().add(aScheduleDetail.getProfitSchd()));
			amountCodeDetail.setRPPSchdPftPaid(amountCodeDetail.getRPPSchdPftPaid().add(aScheduleDetail.getSchdPftPaid()));
		}

		return amountCodeDetail;
	}
	
	public static DataSet createDataSet(FinanceMain  financeMain, String eventCode){
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(financeMain.getFinReference());
		if(eventCode.equals("")){
			if(financeMain.getFinStartDate().after(new Date()) ){
				dataSet.setFinEvent("ADDDBSF");
			}else {
				dataSet.setFinEvent("ADDDBSP");
			}
		}else{
			dataSet.setFinEvent(eventCode);
		}
		dataSet.setFinBranch(financeMain.getFinBranch());
		dataSet.setFinCcy(financeMain.getFinCcy());
		dataSet.setPostDate((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"));
		dataSet.setValueDate(financeMain.getFinStartDate());
		dataSet.setSchdDate(financeMain.getFinStartDate());		
		dataSet.setFinType(financeMain.getFinType());		
		dataSet.setCustId(financeMain.getCustID());
		dataSet.setDisburseAccount(financeMain.getDisbAccountId());
		dataSet.setRepayAccount(financeMain.getRepayAccountId());
		dataSet.setFinAmount(financeMain.getFinAmount());
		dataSet.setNewRecord(financeMain.isNewRecord());
		dataSet.setDownPayment(financeMain.getDownPayment()==null?new BigDecimal(0):financeMain.getDownPayment());
		return dataSet;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getters & setters +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public static AmountCodeDetail getAmountCodeDetail() {
		return amountCodeDetail;
	}
	public static void setAmountCodeDetail(AmountCodeDetail amountCodeDetail) {
		AmountCodeGenerationUtil.amountCodeDetail = amountCodeDetail;
	}
}
*/