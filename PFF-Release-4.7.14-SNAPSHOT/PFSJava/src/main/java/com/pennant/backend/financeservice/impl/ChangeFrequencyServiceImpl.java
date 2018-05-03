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
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
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
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0 ||
						StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())) {
					prvSchdate = curSchd.getSchDate();
					prvSchd = curSchd;
					continue;
				}
			}
			
			//Not Review Date
			if (!curSchd.isRepayOnSchDate() && !financeMain.isFinRepayPftOnFrq() && !curSchd.isPftOnSchDate()) {
				if(curSchd.isDisbOnSchDate()){
					curSchd.setDisbOnSchDate(false);
					curSchd.setDisbAmount(BigDecimal.ZERO);
					if(prvSchd != null && prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0){
						prvSchd.setDisbAmount(BigDecimal.ZERO);
						scheduleList.remove(i);
						i--;
					}
				}
					
				continue;
			}
			
			// Only allowed if payment amount is greater than Zero
			if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0 && StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
				if(curSchd.isDisbOnSchDate()){
					curSchd.setDisbOnSchDate(false);
					curSchd.setDisbAmount(BigDecimal.ZERO);
					if(prvSchd != null && prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0){
						prvSchd.setDisbAmount(BigDecimal.ZERO);
						scheduleList.remove(i);
						i--;
					}
				}
				continue;
			}
			
			Calendar newDate = Calendar.getInstance();
			newDate.setTime(curSchd.getSchDate());
			Date oldDate = curSchd.getSchDate();
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
			
			if(scheduleData.getFinanceMain().isPlanEMIHAlw() && StringUtils.equals(scheduleData.getFinanceMain().getPlanEMIHMethod(), 
					FinanceConstants.PLANEMIHMETHOD_ADHOC)){
				for (Date planEMIHDate : scheduleData.getPlanEMIHDates()) {
					if(planEMIHDate.compareTo(oldDate) == 0){
						scheduleData.getPlanEMIHDates().remove(oldDate);
						scheduleData.getPlanEMIHDates().add(curSchd.getSchDate());
					}
				}
			}
			
			if(DateUtility.compare(oldDate, financeMain.getGrcPeriodEndDate()) == 0){
				financeMain.setGrcPeriodEndDate(curSchd.getSchDate());
			}
			
			if(prvSchd != null && prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0){
				prvSchd.setDisbAmount(BigDecimal.ZERO);
				scheduleList.remove(i-1);
				i--;
			}
			
			if(eventFromdate == null){
				eventFromdate = curSchd.getSchDate();
				finServiceInst.setFromDate(eventFromdate);
			}
			
			// For Grace Period Date Selection check Repay Instruction Details
			List<RepayInstruction> instructionList = scheduleData.getRepayInstructions();
			for (int ri = 0; ri < instructionList.size(); ri++) {
				if (oldDate != null && oldDate.compareTo(instructionList.get(ri).getRepayDate()) == 0) {
					instructionList.get(ri).setRepayDate(curSchd.getSchDate());
				}
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
			if(StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())){
				continue;
			}
			Date curDisbDate = curDisb.getDisbDate();
			isDisbDateFoundInSD = false;
			if(curDisbDate.compareTo(fromDate) <= 0 || curDisbDate.compareTo(financeMain.getFinStartDate()) == 0){
				continue;
			}
			
			// Existing Disbursement Date should be less than Newly calculated Maturity Term
			if(curDisbDate.compareTo(financeMain.getMaturityDate()) >= 0){
				disbMaturityCrossed = true;
				String[] valueParm = new String[1];
				valueParm[0] = DateUtility.formatToLongDate(curDisbDate);
				finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30575", "", valueParm), "EN"));
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
			scheduleData.getFinanceMain().setEventFromDate(eventFromdate);
			scheduleData.getFinanceMain().setEventToDate(scheduleData.getFinanceMain().getCalMaturity());
			scheduleData.getFinanceMain().setRecalFromDate(eventFromdate);
			scheduleData.getFinanceMain().setRecalToDate(scheduleData.getFinanceMain().getCalMaturity());
			scheduleData.getFinanceMain().setRecalSchdMethod(scheduleData.getFinanceMain().getScheduleMethod());

			if(StringUtils.equals(scheduleData.getFinanceMain().getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)){
				scheduleData = ScheduleCalculator.getFrqEMIHoliday(scheduleData);
			}else{
				scheduleData = ScheduleCalculator.getAdhocEMIHoliday(scheduleData);
			}
		}

		// Setting maturity Date
		sdSize = scheduleData.getFinanceScheduleDetails().size();
		if(sdSize > 0){
			scheduleData.getFinanceMain().setMaturityDate(scheduleData.getFinanceScheduleDetails().get(sdSize-1).getSchDate());
		}

		scheduleData.getFinanceMain().setScheduleRegenerated(true);
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
			ErrorDetail errorDetail = FrequencyUtil.validateFrequency(newRepayFrq);
			if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = frqday;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91123", "", valueParm), lang));
			}
		}

		// validate from date
		Date fromDate = finServiceInstruction.getFromDate();
		if(fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}

		// It shouldn't be past date when compare to appdate
		if(DateUtility.compare(finServiceInstruction.getFromDate(), DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:"+DateUtility.formatToLongDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
			return auditDetail;
		}
		
		// validate from date with finStart date and maturity date
		if(fromDate.compareTo(financeMain.getFinStartDate()) < 0 || fromDate.compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "finance start date:"+DateUtility.formatToShortDate(financeMain.getFinStartDate());
			valueParm[2] = "maturity date:"+DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
		if(schedules != null) {
			for(FinanceScheduleDetail schDetail: schedules) {
				if(DateUtility.compare(fromDate, schDetail.getSchDate()) == 0) {
					isValidFromDate = true;
					if(checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
						return auditDetail;
					}
				}
			}

			if(!isValidFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "FromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
			}
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
	/**
	 * Method for validate current schedule date is valid schedule or not
	 * 
	 * @param auditDetail
	 * @param curSchd
	 * @param label
	 * @return
	 */
	private AuditDetail checkIsValidRepayDate(AuditDetail auditDetail, FinanceScheduleDetail curSchd, String label) {
		if (!((curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) 
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate() 
				&& !curSchd.isSchPftPaid()) || (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
				&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid())))) {
			String[] valueParm = new String[1];
			valueParm[0] = label;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return auditDetail;
		}
		return null;
	}
	
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
