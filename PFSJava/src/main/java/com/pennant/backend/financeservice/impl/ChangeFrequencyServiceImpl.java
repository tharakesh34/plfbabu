package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
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
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
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
		
		// Finance Main Details
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleList = scheduleData.getFinanceScheduleDetails();
		
		// Dates Modifications as per New Frequency Date Selection
		Date prvSchdate = financeMain.getFinStartDate();
		FinanceScheduleDetail prvSchd = null;;
		Date eventFromdate = null;
		int day = Integer.parseInt(frequency.substring(3));
		for (int i = 1; i < scheduleList.size(); i++) {
			
			FinanceScheduleDetail curSchd = scheduleList.get(i);
			if(curSchd.getSchDate().compareTo(fromDate) <= 0){
				prvSchdate = curSchd.getSchDate();
				prvSchd = curSchd;
				continue;
			}
			
			if(!curSchd.isRepayOnSchDate()){
				curSchd.setDisbOnSchDate(false);
				curSchd.setDisbAmount(BigDecimal.ZERO);
				curSchd.setFeeChargeAmt(BigDecimal.ZERO);
				prvSchd = curSchd;
				continue;
			}
			
			Calendar newDate = Calendar.getInstance();
			newDate.setTime(curSchd.getSchDate());
			int maxdays = newDate.getActualMaximum(Calendar.DAY_OF_MONTH);

			if (day > maxdays) {
				newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), maxdays);
			} else {
				newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), day);
			}
			
			// Check days Difference between earlier date and current newly calculated date
			if(DateUtility.getDaysBetween(newDate.getTime(), prvSchdate) <= 15){
				newDate.add(Calendar.MONTH,1);
			}
			curSchd.setSchDate(DateUtility.getDBDate(DateUtility.formatUtilDate(newDate.getTime(),PennantConstants.DBDateFormat)));
			curSchd.setDefSchdDate(curSchd.getSchDate());
			curSchd.setDisbOnSchDate(false);
			curSchd.setDisbAmount(BigDecimal.ZERO);
			curSchd.setFeeChargeAmt(BigDecimal.ZERO);
			
			if(prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0){
				scheduleList.remove(i-1);
				i--;
			}
			
			if(eventFromdate == null){
				eventFromdate = curSchd.getSchDate();
				finServiceInst.setFromDate(eventFromdate);
			}
			
			prvSchdate = curSchd.getSchDate();
			prvSchd = curSchd;
		}
		
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
			isDisbDateFoundInSD = false;
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
				prvSchd = finSchdDetails.get(disbIndex);
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

		// Setting Recalculation Type Method
		scheduleData.getFinanceMain().setRecalFromDate(eventFromdate);
		scheduleData.getFinanceMain().setEventFromDate(eventFromdate);
		scheduleData.getFinanceMain().setEventToDate(finServiceInst.getRecalToDate());
		scheduleData.getFinanceMain().setRecalToDate(scheduleData.getFinanceMain().getMaturityDate());
		scheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		scheduleData.getFinanceMain().setPftIntact(finServiceInst.isPftIntact());

		// Schedule Recalculation Depends on Frequency Change
		scheduleData = ScheduleCalculator.reCalSchd(scheduleData, financeMain.getScheduleMethod());

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
		return scheduleData;
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
