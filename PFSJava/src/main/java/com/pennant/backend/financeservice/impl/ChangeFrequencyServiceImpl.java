package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.rits.cloning.Cloner;

public class ChangeFrequencyServiceImpl extends GenericService<FinServiceInstruction> implements ChangeFrequencyService {
	private static Logger logger = Logger.getLogger(ChangeFrequencyServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;


	/**
	 * Method for process schedule details and do change frequency action
	 * 
	 * @param scheduleData
	 * @param finServiceInst
	 * @return FinScheduleData
	 */
	public FinScheduleData doChangeFrequency(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst) {
		logger.debug("Entering");
		
		FinScheduleData scheduleData = null;
		Cloner cloner = new Cloner();
		scheduleData = cloner.deepClone(finScheduleData);

		String frequency = finServiceInst.getRepayFrq();
		Date fromDate = finServiceInst.getFromDate();
		Date startRepayCaldate = finServiceInst.getRecalFromDate();
		Date reCalToDate = finServiceInst.getRecalToDate();
		int adjRepayTerms = finServiceInst.getAdjRpyTerms();
		
		// FinanceMain
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleList = scheduleData.getFinanceScheduleDetails();

		// Repayment Calculated Rate storing
		Date firstRepayDate = null;
		boolean chkFirstRpyDate = false;
		BigDecimal repayCalRate = financeMain.getRepayProfitRate();
		for (int i = 0; i < scheduleList.size(); i++) {
			if (scheduleList.get(i).getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) >= 0) {
				if (chkFirstRpyDate) {
					firstRepayDate = scheduleList.get(i).getSchDate();
					break;
				}
				repayCalRate = scheduleList.get(i).getCalculatedRate();
				chkFirstRpyDate = true;
			}
		}

		// Removing Schedule Details from Selected Recalculation From Date
		HashMap<Date, FinanceScheduleDetail> mapList = new HashMap<Date, FinanceScheduleDetail>();
		BigDecimal unModifiedPft = BigDecimal.ZERO;
		BigDecimal schPriDue = BigDecimal.ZERO;
		BigDecimal schPftDue = BigDecimal.ZERO;
		for (int i = 0; i < scheduleList.size(); i++) {
			if (scheduleList.get(i).getSchDate().compareTo(fromDate) <= 0) {
				mapList.put(scheduleList.get(i).getSchDate(), scheduleList.get(i));
				unModifiedPft = unModifiedPft.add(scheduleList.get(i).getProfitSchd());
			} else {
				schPftDue = schPftDue.add(scheduleList.get(i).getProfitSchd());
				schPriDue = schPriDue.add(scheduleList.get(i).getPrincipalSchd());
			}
		}
		scheduleData.setScheduleMap(mapList);
		mapList = null;
		
		//Check Date Status Specifier
		boolean calFromGrcPeriod = false;
		if(fromDate != null && fromDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0){
			calFromGrcPeriod = true;
		}
		
		//Setting Event From Date Value
		if (calFromGrcPeriod) {
			scheduleData.getFinanceMain().setGrcPeriodEndDate(finServiceInst.getGrcPeriodEndDate());
			if (finServiceInst.getNextGrcRepayDate() != null) {
				scheduleData.getFinanceMain().setEventFromDate(finServiceInst.getNextGrcRepayDate());
			} else {
				scheduleData.getFinanceMain().setEventFromDate(FrequencyUtil.getNextDate(
						frequency, 1, finServiceInst.getFromDate(), "A", false, 0).getNextFrequencyDate());
			}

			if (finServiceInst.getNextRepayDate() != null) {
				startRepayCaldate = finServiceInst.getNextRepayDate();
				reCalToDate = finServiceInst.getNextRepayDate();
			} else {
				startRepayCaldate = FrequencyUtil.getNextDate(frequency, 1, finServiceInst.getGrcPeriodEndDate(), "A",
						false, 0).getNextFrequencyDate();
			}

			if (scheduleData.getFinanceMain().getNumberOfTerms() != 0) {

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(frequency, adjRepayTerms, startRepayCaldate,
						"A", true, 0).getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					reCalToDate = calendar.getTime();
				}
				scheduleDateList = null;
			}

			scheduleData.getFinanceMain().setMaturityDate(reCalToDate);

			finServiceInst.setRecalFromDate(startRepayCaldate);
			finServiceInst.setRecalToDate(reCalToDate);

		} else {
			if (finServiceInst.getNextRepayDate() != null) {
				startRepayCaldate = finServiceInst.getNextRepayDate();
			} else {
				startRepayCaldate = FrequencyUtil.getNextDate(frequency, 1, finServiceInst.getFromDate(), "A", false, 0)
						.getNextFrequencyDate();
			}

			if (scheduleData.getFinanceMain().getNumberOfTerms() != 0) {

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(frequency, adjRepayTerms, startRepayCaldate,
						"A", true, 0).getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					reCalToDate = calendar.getTime();
				}
				scheduleDateList = null;
			}

			scheduleData.getFinanceMain().setEventFromDate(startRepayCaldate);
			scheduleData.getFinanceMain().setMaturityDate(reCalToDate);

			finServiceInst.setRecalFromDate(startRepayCaldate);
			finServiceInst.setRecalToDate(reCalToDate);
		}
		
		// call schedule calculator for new schedules
		FinScheduleData finSchdData = ScheduleGenerator.getScheduleDateList(scheduleData, frequency, fromDate,
				startRepayCaldate, reCalToDate);
		
		boolean isDisbDateFoundInSD = false;
		int disbIndex = 0;
		List<FinanceScheduleDetail> finSchdDetails = scheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();
		
		//Add Disbursement amount to existing record if found
		List<FinanceDisbursement> finDisbDetails = scheduleData.getDisbursementDetails();
		FinanceScheduleDetail curSchd = null;
		Date schdDate = financeMain.getFinStartDate();
		boolean disbMaturityCrossed = false;
		for (int k = 0; k < finDisbDetails.size(); k++) {
			
			FinanceDisbursement curDisb = finDisbDetails.get(k);
			Date curDisbDate = curDisb.getDisbDate();
			if(curDisbDate.compareTo(fromDate) <= 0 || curDisbDate.compareTo(financeMain.getFinStartDate()) == 0){
				continue;
			}
			
			// Existing Disbursement Date should be less than Newly calculated Maturity Term
			if(curDisbDate.compareTo(financeMain.getMaturityDate()) >= 0){
				disbMaturityCrossed = true;
				scheduleData.setErrorDetail(new ErrorDetails("30575", new String[]{
						DateUtility.formatToLongDate(curDisbDate)}));
				break;
			}
			
			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				schdDate = curSchd.getSchDate();

				//Schedule Date before event from date
				if (schdDate.before(curDisbDate)) {
					disbIndex = i;
					continue;

					//Schedule Date matches event from date
				} else if (schdDate.compareTo(curDisbDate) == 0) {
					isDisbDateFoundInSD = true;
					curSchd.setDisbAmount(curSchd.getDisbAmount().add(curDisb.getDisbAmount()));
					curSchd.setDisbOnSchDate(true);
					curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().add(curDisb.getFeeChargeAmt()));
					disbIndex = i;
					break;

					//Event from date not found
				} else {
					break;
				}
			}

			//If new disbursement date add a record in schedule
			if (!isDisbDateFoundInSD) {
				scheduleData = addSchdRcd(scheduleData, curDisbDate, disbIndex);
				FinanceScheduleDetail prvSchd = finSchdDetails.get(disbIndex);
				disbIndex = disbIndex + 1;
				curSchd = scheduleData.getFinanceScheduleDetails().get(disbIndex);

				curSchd.setDisbOnSchDate(true);
				curSchd.setDisbAmount(curDisb.getDisbAmount());
				curSchd.setFeeChargeAmt(curDisb.getFeeChargeAmt());
				curSchd.setClosingBalance(prvSchd.getClosingBalance().add(curDisb.getDisbAmount()).add(curDisb.getFeeChargeAmt()));
			}
		}
		
		// If Existing Disbursement crossed newly calculated maturity Date
		if(disbMaturityCrossed){
			return scheduleData;
		}

		// Set Deferred scheduled date and schedule method first time
		Date newFirstRpyDate = null;
		boolean suplRentUpdated = false;

		for (int i = 0; i < finSchdData.getFinanceScheduleDetails().size(); i++) {
			curSchd = finSchdData.getFinanceScheduleDetails().get(i);
			FinanceScheduleDetail prvSchd = null;
			if (i != 0) {
				prvSchd = finSchdData.getFinanceScheduleDetails().get(i - 1);
			}
			curSchd.setDefSchdDate(curSchd.getSchDate());
			
			// Profit Days Basis Setting
			if(StringUtils.isEmpty(curSchd.getPftDaysBasis())){
				curSchd.setPftDaysBasis(prvSchd.getPftDaysBasis());
			}

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				if (!finSchdData.getFinanceMain().isAllowGrcRepay()) {
					curSchd.setSchdMethod(CalculationConstants.SCHMTHD_NOPAY);
				} else {
					curSchd.setSchdMethod(financeMain.getGrcSchdMthd());
				}
			} else {
				curSchd.setSchdMethod(financeMain.getScheduleMethod());
			}

			// Supplementary & increased Cost Re-Setting
			if (curSchd.getSchDate().compareTo(fromDate) >= 0) {

				if (!suplRentUpdated) {
					if (prvSchd.getSchDate().compareTo(financeMain.getFinStartDate()) == 0) {
						financeMain.setCurSuplRent(financeMain.getSupplementRent());
						financeMain.setCurIncrCost(financeMain.getIncreasedCost());
					} else {
						financeMain.setCurSuplRent(prvSchd.getSuplRent());
						financeMain.setCurIncrCost(prvSchd.getIncrCost());
					}
					suplRentUpdated = true;
				}
			}
			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) >= 0) {
				
				if(curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) > 0){
					if (chkFirstRpyDate && newFirstRpyDate == null) {
						newFirstRpyDate = curSchd.getSchDate();
					}
					chkFirstRpyDate = true;
				}

				// Actual rates Data setting for maintenance Schedule
				if (i != 0) {
					curSchd.setActRate(StringUtils.trimToNull(prvSchd.getBaseRate()) == null ? prvSchd.getActRate()
							: BigDecimal.ZERO);
					curSchd.setBaseRate(StringUtils.trimToNull(prvSchd.getBaseRate()));
					curSchd.setSplRate(StringUtils.trimToNull(prvSchd.getSplRate()));
					curSchd.setMrgRate(StringUtils.trimToNull(prvSchd.getBaseRate()) == null ? BigDecimal.ZERO:prvSchd.getMrgRate());

					// Advised Rates Setting
					if (StringUtils.equals(FinanceConstants.PRODUCT_STRUCTMUR, finSchdData.getFinanceType()
							.getFinCategory())) {
						curSchd.setAdvPftRate(StringUtils.trimToNull(prvSchd.getAdvBaseRate()) == null ? 
								prvSchd.getAdvPftRate():BigDecimal.ZERO);
						curSchd.setAdvBaseRate(StringUtils.trimToNull(prvSchd.getAdvBaseRate()));
						curSchd.setAdvMargin(StringUtils.trimToNull(prvSchd.getAdvBaseRate()) == null ? 
								BigDecimal.ZERO:prvSchd.getAdvMargin());
					}
				}
			}

			if (fromDate.compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
				if (i != 0 && curSchd.getSchDate().compareTo(fromDate) > 0) {
					curSchd.setCalculatedRate(finSchdData.getFinanceScheduleDetails().get(i - 1).getCalculatedRate());
				}
			} else {
				if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
					if (i != 0 && curSchd.getSchDate().compareTo(fromDate) > 0) {
						curSchd.setCalculatedRate(finSchdData.getFinanceScheduleDetails().get(i - 1)
								.getCalculatedRate());
					}
				} else {
					curSchd.setCalculatedRate(repayCalRate);
				}
			}
		}

		// For Grace Period Date Selection check Repay Instruction Details
		List<RepayInstruction> instructionList = finSchdData.getRepayInstructions();
		for (int i = 0; i < instructionList.size(); i++) {
			if (firstRepayDate != null && firstRepayDate.compareTo(instructionList.get(i).getRepayDate()) == 0) {
				instructionList.get(i).setRepayDate(newFirstRpyDate);
			}
		}

		// Setting Recalculation Type Method
		finSchdData.getFinanceMain().setRecalFromDate(finServiceInst.getRecalFromDate());
		finSchdData.getFinanceMain().setEventToDate(finServiceInst.getRecalToDate());
		finSchdData.getFinanceMain().setRecalToDate(finSchdData.getFinanceMain().getMaturityDate());
		finSchdData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		finSchdData.getFinanceMain().setPftIntact(finServiceInst.isPftIntact());

		if (finServiceInst.isPftIntact()) {
			finSchdData.getFinanceMain().setDesiredProfit(finSchdData.getFinanceMain().getTotalGrossPft());
			finSchdData.getFinanceMain().setAdjTerms(adjRepayTerms);
			finSchdData.getFinanceMain().setSchPftDue(schPftDue);
			finSchdData.getFinanceMain().setSchPriDue(schPriDue);
		}

		// Schedule Recalculation Depends on Frequency Change
		finSchdData = ScheduleCalculator.reCalSchd(finSchdData, financeMain.getScheduleMethod());

		// Plan EMI Holidays Resetting after Rescheduling
		if(scheduleData.getFinanceMain().isPlanEMIHAlw()){
			scheduleData.getFinanceMain().setEventFromDate(fromDate);
			scheduleData.getFinanceMain().setEventToDate(scheduleData.getFinanceMain().getMaturityDate());
			scheduleData.getFinanceMain().setRecalFromDate(fromDate);
			scheduleData.getFinanceMain().setRecalToDate(scheduleData.getFinanceMain().getMaturityDate());
			scheduleData.getFinanceMain().setRecalSchdMethod(scheduleData.getFinanceMain().getScheduleMethod());

			if(StringUtils.equals(scheduleData.getFinanceMain().getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)){
				scheduleData = ScheduleCalculator.getFrqEMIHoliday(scheduleData);
			}else{
				scheduleData = ScheduleCalculator.getAdhocEMIHoliday(scheduleData);
			}
		}


		logger.debug("Leaving");
		return finSchdData;
	}

	/**
	 * Method for Adding Schedule term when missing while on disbursement date checking
	 * @param finScheduleData
	 * @param newSchdDate
	 * @param prvIndex
	 * @return
	 */
	private FinScheduleData addSchdRcd(FinScheduleData finScheduleData, Date newSchdDate, int prvIndex) {
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(prvIndex);

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinReference(finScheduleData.getFinanceMain().getFinReference());
		sd.setBpiOrHoliday("");
		sd.setSchDate(newSchdDate);
		sd.setDefSchdDate(newSchdDate);

		sd.setBaseRate(prvSchd.getBaseRate());
		sd.setSplRate(prvSchd.getSplRate());
		sd.setMrgRate(prvSchd.getMrgRate());
		sd.setActRate(prvSchd.getActRate());
		sd.setCalculatedRate(prvSchd.getCalculatedRate());
		sd.setSchdMethod(prvSchd.getSchdMethod());
		sd.setPftDaysBasis(prvSchd.getPftDaysBasis());
		sd.setAdvBaseRate(prvSchd.getAdvBaseRate());
		sd.setAdvMargin(prvSchd.getAdvMargin());
		sd.setAdvPftRate(prvSchd.getAdvPftRate());
		sd.setSuplRent(prvSchd.getSuplRent());
		sd.setIncrCost(prvSchd.getIncrCost());

		finScheduleData.getFinanceScheduleDetails().add(sd);
		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

		finScheduleData.getFinanceMain().setNumberOfTerms(finScheduleData.getFinanceMain().getNumberOfTerms() + 1);

		return finScheduleData;
	}
	
	/**
	 * Method for Sorting schedule details
	 * @param financeScheduleDetail
	 * @return
	 */
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method for validate change frequency instructions
	 * 
	 * @param finServiceInstruction
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();

		FinanceMain financeMain = financeMainDAO.getFinanceDetailsForService(finReference, "", isWIF);
		
		// validate frqDay and frequency
		String frqday = String.valueOf(finServiceInstruction.getFrqDay());
		frqday = frqday.length() == 1?"0".concat(frqday):frqday;
		String newRepayFrq = StringUtils.substring(financeMain.getRepayFrq(), 0, financeMain.getRepayFrq().length()-2).concat(frqday);
		
		// validate newFrq
		if (StringUtils.isNotBlank(newRepayFrq)) {
			ErrorDetails errorDetail = FrequencyUtil.validateFrequency(newRepayFrq);
			if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = frqday;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91123", "", valueParm), lang));
			}
		}
		
		// validate from date
		Date fromDate = finServiceInstruction.getFromDate();
		
		// It should be valid schedule date
		boolean isFromDateExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, fromDate, isWIF);
		if(!isFromDateExists) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		// It shouldn't be greater than or equals to maturity date
		if (!isFromDateExists && fromDate.compareTo(financeMain.getMaturityDate()) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}

		// It shouldn't be past date when compare to appdate
		if(!isFromDateExists && fromDate.compareTo(DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
	
		// validate nextGrcRepayDate
		if(finServiceInstruction.getNextGrcRepayDate() != null) {
			if(!financeMain.isAllowGrcPeriod()) {
				String[] valueParm = new String[1];
				valueParm[0] = "NextGrcRepayDate";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91124", "", valueParm), lang));
			}
		}
		
		// validate grcPeriodEndDate
		// It should be valid schedule date
		Date grcEndDate = finServiceInstruction.getGrcPeriodEndDate();
		if(grcEndDate != null) {
			if(!financeMain.isAllowGrcPeriod()) {
				String[] valueParm = new String[1];
				valueParm[0] = "GrcPeriodEndDate";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91124", "", valueParm), lang));
			} else {
				isFromDateExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, grcEndDate, isWIF);
				if(!isFromDateExists) {
					String[] valueParm = new String[1];
					valueParm[0] = "GrcPeriodEndDate:"+DateUtility.formatToShortDate(grcEndDate);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
				}
				// It shouldn't be greater than application date
				if(grcEndDate.compareTo(DateUtility.getAppDate()) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "GrcPeriodEndDate:"+DateUtility.formatToShortDate(grcEndDate);
					valueParm[1] = "Application Date:"+DateUtility.formatToShortDate(DateUtility.getAppDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91125", "", valueParm), lang));
				}
			}
		}
		
		// validate nextRepayDate
		if(finServiceInstruction.getNextRepayDate().compareTo(DateUtility.getAppDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "NextRepayDate:"+DateUtility.formatToShortDate(finServiceInstruction.getNextRepayDate());
			valueParm[1] = "Application Date:"+DateUtility.formatToShortDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91125", "", valueParm), lang));
		}
		
		if(finServiceInstruction.getNextRepayDate().compareTo(DateUtility.getAppDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "NextRepayDate:"+DateUtility.formatToShortDate(finServiceInstruction.getNextRepayDate());
			valueParm[1] = "Application Date:"+DateUtility.formatToShortDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91125", "", valueParm), lang));
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
