
/*package com.pennant.app.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceDetailService;

public class FinanceRateReviewUtil {

	private static BaseRateDAO	            baseRateDAO;
	private static SplRateDAO	            splRateDAO;
	private static FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private static FinanceDetailService	    financeDetailService;
	private static String	                baseRate;
	private static String	                splRate;
	private static BigDecimal	            actlRate	= new BigDecimal(0);
	private static BigDecimal	            marginRate	= new BigDecimal(0);

	public static void recalRateReview() {
		System.out.println("Rate Review Process Started");
		List<FinScheduleData> finScheduleDatas = new ArrayList<FinScheduleData>();
		Date date = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		//list of base rates which fall under the application date
		final ArrayList<BaseRate> baseRateslist = new ArrayList<BaseRate>(getBaseRateDAO().getBSRListByMdfDate(date, ""));
		//list of special rates which fall under the application date
		final ArrayList<SplRate> splRateslist = new ArrayList<SplRate>(getSplRateDAO().getSRListByMdfDate(date, ""));
		ArrayList<String> listFinRefeences = null;
		//base rate change	
		//get the distinct fin references with changed base rates and special rates
		listFinRefeences = new ArrayList<String>(getFinanceScheduleDetailDAO().getRateRvwSchd(getBaseRateCode(baseRateslist), getSplRateCode(splRateslist), ""));
		if (listFinRefeences != null && listFinRefeences.size() > 0) {
			for (String finref : listFinRefeences) {
				//get the schedule from the database 
				FinScheduleData finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(finref, "");
				// TODO ===========delete after testing ====start
				try {
					testshedule(finScheduleData, "Before_" + finScheduleData.getFinanceMain().getFinReference());
				} catch (IOException e) {
					e.printStackTrace();
				}
				//=================delete after testing===== end

				//set event from date and to date
				finScheduleData = rvwStartEndDateFinder(finScheduleData, date);
				finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_CURPRD);
				// call service for base rate change
				finScheduleData = ScheduleCalculator.changeRate(finScheduleData, baseRate, splRate, marginRate, actlRate, true);

				// TODO ===========delete after testing ====start
				try {
					testshedule(finScheduleData, "After_" + finScheduleData.getFinanceMain().getFinReference());
				} catch (IOException e) {
					e.printStackTrace();
				}
				//=================delete after testing ====end

				//add to the update list
				finScheduleDatas.add(finScheduleData);

			}

		}
		// METHOD TO UPDATE
		for (FinScheduleData finSchDt : finScheduleDatas) {
			getFinanceScheduleDetailDAO().updateList(finSchDt.getFinanceScheduleDetails(), "");
		}

		System.out.println("Rate Review Process Completed");

	}

	*//**	Identifies the rate review start date and end date of the schedule  *//*

	private static FinScheduleData rvwStartEndDateFinder(FinScheduleData scheduleData, Date ChangeDate) {
		Date start = null;
		Date end = null;
		baseRate = "";
		splRate = "";
		//go throw schedule
		for (FinanceScheduleDetail finSchd : scheduleData.getFinanceScheduleDetails()) {
			//process the schedule which is equal or after the given change date
			if (finSchd.getSchDate().compareTo(ChangeDate) >= 0) {
				//if review flag found then
				if (finSchd.isRvwOnSchDate()) {
					//if start date is null set scheduled date as start date
					if (start == null) {
						start = finSchd.getSchDate();
						baseRate = finSchd.getBaseRate();
						splRate = finSchd.getSplRate();
						actlRate = finSchd.getActRate();
						marginRate = finSchd.getMrgRate();

						continue;
					} else {
						//set schedule date as end date
						end = finSchd.getSchDate();
						break;
					}

				}

			}
		}
		if (end == null) {
			end = scheduleData.getFinanceMain().getMaturityDate();
		}

		scheduleData.getFinanceMain().setEventFromDate(start);
		scheduleData.getFinanceMain().setEventToDate(end);

		return scheduleData;

	}

	private static String getBaseRateCode(ArrayList<BaseRate> baseRateslist) {
		String rateCode = "''";
		for (int i = 0; i < baseRateslist.size(); i++) {
			if (i == 0) {
				rateCode = "'" + baseRateslist.get(i).getBRType() + "'";
			} else {
				rateCode = rateCode + ",'" + baseRateslist.get(i).getBRType() + "'";
			}

		}
		return rateCode;

	}

	private static String getSplRateCode(ArrayList<SplRate> splRateslist) {
		String rateCode = "''";
		for (int i = 0; i < splRateslist.size(); i++) {
			if (i == 0) {
				rateCode = "'" + splRateslist.get(i).getSRType() + "'";
			} else {
				rateCode = rateCode + ",'" + splRateslist.get(i).getSRType() + "'";
			}
		}
		return rateCode;
	}

	public void setSplRateDAO(SplRateDAO splRateDAO) {
		FinanceRateReviewUtil.splRateDAO = splRateDAO;
	}

	private static SplRateDAO getSplRateDAO() {
		return splRateDAO;
	}

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		FinanceRateReviewUtil.baseRateDAO = baseRateDAO;
	}

	private static BaseRateDAO getBaseRateDAO() {
		return baseRateDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		FinanceRateReviewUtil.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	private static FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		FinanceRateReviewUtil.financeDetailService = financeDetailService;
	}

	private static FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	//TODO to be deleted
	private static void testshedule(FinScheduleData aFinScheduleData, String name) throws IOException {
		File file = new File("D:/" + name + ".xls");
		System.out.println(file.getAbsolutePath());
		FileWriter txt = new FileWriter(file);
		PrintWriter out = new PrintWriter(txt);

		out.print("SNo \tDate \t CPZ \t PFT \t RVW \t RPY \t DEFF \t DEFPAY \t OPBAL \t Rate \t NODAYS \t ProfitCalc \t Profit schd \t Principal schd "
		        + "\t Def.Pri \t Def.Pft \t DefPriShcd \t DefPftSchd \t RepayAmount \t ProfitBalance \t DisAmount \t DWPAY \t CpzAmount \t CLOSBAL" + "\t rate  ");

		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail sd = aFinScheduleData.getFinanceScheduleDetails().get(i);
			out.write("\n " + i + " \t " + sd.getSchDate() + " \t " + yesrno(sd.isCpzOnSchDate()) + " \t " + yesrno(sd.isPftOnSchDate()) + " \t " + yesrno(sd.isRvwOnSchDate())
			        + " \t " + yesrno(sd.isRepayOnSchDate()) + " \t " + yesrno(sd.isDefered()) + " \t " + yesrno(sd.isDeferedPay()) + " \t " + amount(sd.getBalanceForPftCal())
			        + " \t " + amount(sd.getActRate()) + " \t " + sd.getNoOfDays() + " \t " + amount(sd.getProfitCalc()) + " \t " + amount(sd.getProfitSchd()) + " \t "
			        + amount(sd.getPrincipalSchd()) + " \t " + amount(sd.getDefPrincipal()) + " \t " + amount(sd.getDefProfit()) + " \t " + amount(sd.getDefPrincipalSchd())
			        + " \t " + amount(sd.getDefProfitSchd()) + " \t " + amount(sd.getRepayAmount()) + " \t " + amount(sd.getProfitBalance()) + " \t " + amount(sd.getDisbAmount())
			        + " \t " + amount(sd.getDownPaymentAmount()) + " \t " + amount(sd.getCpzAmount()) + " \t " + amount(sd.getClosingBalance()) + " \t " + amount(sd.getActRate()));
		}
		;
		out.close();
	}

	private static String amount(BigDecimal decimal) {
		String amt = "00";
		if (decimal.equals(new BigDecimal(0))) {
			amt = "00";
			return amt;
		} else {
			if (decimal.intValue() < 10 && decimal.intValue() > 0) {
				amt = "0" + decimal.toString();
				return amt;
			}
			decimal.setScale(5, RoundingMode.DOWN);
			amt = String.valueOf(decimal.floatValue());
			return amt;
		}

	}

	private static String yesrno(boolean bool) {

		String yes = " ";
		if (bool) {
			yes = "Y";
		}
		return yes;

	}

}
*/