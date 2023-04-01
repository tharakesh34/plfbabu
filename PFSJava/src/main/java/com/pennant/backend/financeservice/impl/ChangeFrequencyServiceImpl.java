package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SanctionBasedSchedule;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class ChangeFrequencyServiceImpl extends GenericService<FinServiceInstruction>
		implements ChangeFrequencyService {
	private static Logger logger = LogManager.getLogger(ChangeFrequencyServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;

	public FinScheduleData doChangeFrequency(FinScheduleData schdData, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		boolean isApplySanctionBasedSchedule = SanctionBasedSchedule.isApplySanctionBasedSchedule(schdData);

		FinanceMain fm = schdData.getFinanceMain();

		BigDecimal oldTotalPft = fm.getTotalGrossPft();
		String frequency = fsi.getRepayFrq();
		Date fromDate = fsi.getFromDate();

		String recalType = fsi.getRecalType();
		List<FinanceScheduleDetail> scheduleList = schdData.getFinanceScheduleDetails();

		// Dates Modifications as per New Frequency Date Selection
		Date prvSchdate = fm.getFinStartDate();
		FinanceScheduleDetail prvSchd = null;
		Date eventFromdate = null;
		Date appDate = SysParamUtil.getAppDate();
		int day = Integer.parseInt(frequency.substring(3));

		boolean additionalBPIAdded = false;
		FinanceScheduleDetail additionalBPISchd = null;

		for (int i = 1; i < scheduleList.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleList.get(i);

			if (curSchd.getSchDate().compareTo(fromDate) <= 0) {
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0
						|| StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())) {
					prvSchdate = curSchd.getSchDate();
					prvSchd = curSchd;
				}
				continue;
			}

			// Not Review Date
			if (!curSchd.isRepayOnSchDate() && !fm.isFinRepayPftOnFrq() && !curSchd.isPftOnSchDate()) {
				if (curSchd.isDisbOnSchDate()) {
					curSchd.setDisbOnSchDate(false);
					curSchd.setDisbAmount(BigDecimal.ZERO);
					if (prvSchd != null && prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0) {
						prvSchd.setDisbAmount(BigDecimal.ZERO);
						scheduleList.remove(i);
						i--;
					}
				}

				continue;
			}

			// Only allowed if payment amount is greater than Zero
			if (!isApplySanctionBasedSchedule && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0
					&& StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
				if (curSchd.isDisbOnSchDate()) {
					curSchd.setDisbOnSchDate(false);
					curSchd.setDisbAmount(BigDecimal.ZERO);
					if (prvSchd != null && prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0) {
						prvSchd.setDisbAmount(BigDecimal.ZERO);
						scheduleList.remove(i);
						i--;
					}
				}
				continue;
			}

			// if there is any part payment done after selected event from date
			if (!curSchd.isFrqDate() && curSchd.getPrincipalSchd().compareTo(curSchd.getRepayAmount()) == 0
					&& curSchd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) {
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
			if (DateUtil.getDaysBetween(newDate.getTime(), prvSchdate) <= 15) {
				if (!(DateUtil.compare(prvSchdate, fm.getGrcPeriodEndDate()) == 0
						&& DateUtil.compare(oldDate, appDate) > 0)) {
					newDate.add(Calendar.MONTH, 1);
				}
			}

			Date curSchDate = newDate.getTime();

			while (prvSchdate.compareTo(curSchDate) >= 0) {
				newDate.add(Calendar.MONTH, 1);
				curSchDate = newDate.getTime();
			}

			curSchd.setSchDate(curSchDate);
			curSchd.setDefSchdDate(curSchd.getSchDate());
			curSchd.setDisbOnSchDate(false);
			curSchd.setDisbAmount(BigDecimal.ZERO);
			curSchd.setFeeChargeAmt(BigDecimal.ZERO);

			if (additionalBPISchd != null) {
				additionalBPIAdded = true;
			}

			int lastDateInMonth = DateUtil.getDay(DateUtil.getMonthEnd(prvSchdate));

			if (!additionalBPIAdded && CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recalType)
					&& DateUtil.getDaysBetween(newDate.getTime(), prvSchdate) > lastDateInMonth) {
				additionalBPISchd = prepareAddtionalBPISchd(schdData, curSchd, day);
			}

			String planEMIHM = fm.getPlanEMIHMethod();
			String bpiOrHoliday = curSchd.getBpiOrHoliday();
			if (FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday)
					&& FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHM)) {
				schdData.getPlanEMIHDates().removeIf(e -> (e.compareTo(oldDate) == 0));
				schdData.getPlanEMIHDates().add(curSchd.getSchDate());
			}

			if (DateUtil.compare(oldDate, fm.getGrcPeriodEndDate()) == 0) {
				fm.setGrcPeriodEndDate(curSchd.getSchDate());
			}

			if (prvSchd != null && prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0) {
				prvSchd.setDisbAmount(BigDecimal.ZERO);
				scheduleList.remove(i - 1);
				i--;
			}

			if (eventFromdate == null) {
				if (additionalBPISchd != null) {
					eventFromdate = additionalBPISchd.getSchDate();
				} else {
					eventFromdate = curSchd.getSchDate();
				}

				fsi.setFromDate(eventFromdate);
			}

			// For Grace Period Date Selection check Repay Instruction Details
			List<RepayInstruction> instructionList = schdData.getRepayInstructions();
			for (int ri = 0; ri < instructionList.size(); ri++) {
				if (oldDate != null && oldDate.compareTo(instructionList.get(ri).getRepayDate()) == 0) {
					instructionList.get(ri).setRepayDate(curSchd.getSchDate());
				}
			}

			if (!additionalBPIAdded && additionalBPISchd != null) {
				prvSchdate = additionalBPISchd.getSchDate();
				prvSchd = additionalBPISchd;
				schdData.getFinanceScheduleDetails().add(additionalBPISchd);
				schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));
			} else {
				prvSchdate = curSchd.getSchDate();
				prvSchd = curSchd;
			}

		}

		boolean isDisbDateFoundInSD = false;
		int disbIndex = 0;
		List<FinanceScheduleDetail> finSchdDetails = schdData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		// Add Disbursement amount to existing record if found
		List<FinanceDisbursement> finDisbDetails = schdData.getDisbursementDetails();
		Date schdDate = fm.getFinStartDate();
		boolean disbMaturityCrossed = false;

		for (FinanceDisbursement curDisb : finDisbDetails) {
			if (FinanceConstants.DISB_STATUS_CANCEL.equals(curDisb.getDisbStatus())) {
				continue;
			}

			Date curDisbDate = curDisb.getDisbDate();
			isDisbDateFoundInSD = false;
			if (curDisbDate.compareTo(fromDate) <= 0 || curDisbDate.compareTo(fm.getFinStartDate()) == 0) {
				continue;
			}

			// Existing Disbursement Date should be less than Newly calculated Maturity Term
			if (curDisbDate.compareTo(fm.getMaturityDate()) >= 0) {
				disbMaturityCrossed = true;
				String[] valueParm = new String[1];
				valueParm[0] = DateUtil.formatToLongDate(curDisbDate);
				schdData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30575", "", valueParm), "EN"));
				break;
			}

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(i);
				schdDate = curSchd.getSchDate();

				// Schedule Date before event from date
				if (schdDate.before(curDisbDate)) {
					disbIndex = i;
					continue;

					// Schedule Date matches event from date
				} else if (schdDate.compareTo(curDisbDate) == 0) {
					isDisbDateFoundInSD = true;
					curSchd.setDisbAmount(curSchd.getDisbAmount().add(curDisb.getDisbAmount()));
					curSchd.setDisbOnSchDate(true);
					curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().add(curDisb.getFeeChargeAmt()));
					disbIndex = i;
					break;

					// Event from date not found
				} else {
					break;
				}
			}

			// If new disbursement date add a record in schedule
			if (!isDisbDateFoundInSD) {
				schdData = addSchdRcd(schdData, curDisbDate, disbIndex);
				prvSchd = finSchdDetails.get(disbIndex);
				disbIndex = disbIndex + 1;
				FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(disbIndex);

				curSchd.setDisbOnSchDate(true);
				curSchd.setDisbAmount(curDisb.getDisbAmount());
				curSchd.setFeeChargeAmt(curDisb.getFeeChargeAmt());
				curSchd.setClosingBalance(
						prvSchd.getClosingBalance().add(curDisb.getDisbAmount()).add(curDisb.getFeeChargeAmt()));
			}

		}

		// If Existing Disbursement crossed newly calculated maturity Date
		if (disbMaturityCrossed) {
			return schdData;
		}

		// Setting Recalculation Type Method
		fm.setRecalFromDate(eventFromdate);
		fm.setEventFromDate(eventFromdate);
		fm.setEventToDate(fsi.getRecalToDate());
		fm.setRecalToDate(schdData.getFinanceMain().getMaturityDate());
		fm.setRecalType(fsi.getRecalType());
		fm.setPftIntact(fsi.isPftIntact());
		fm.setProcMethod(FinServiceEvent.CHGFRQ);

		// Schedule Recalculation Depends on Frequency Change
		schdData = ScheduleCalculator.reCalSchd(schdData, fm.getScheduleMethod());

		// Plan EMI Holidays Resetting after Rescheduling
		if (fm.isPlanEMIHAlw()) {
			fm.setEventFromDate(eventFromdate);
			fm.setEventToDate(fm.getCalMaturity());
			fm.setRecalFromDate(fm.getCalMaturity());
			fm.setRecalToDate(fm.getCalMaturity());
			fm.setRecalSchdMethod(fm.getScheduleMethod());

			if (StringUtils.equals(fm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				schdData = ScheduleCalculator.getFrqEMIHoliday(schdData);
			} else {
				schdData = ScheduleCalculator.getAdhocEMIHoliday(schdData);
			}
		}

		// Setting maturity Date
		sdSize = schdData.getFinanceScheduleDetails().size();
		if (sdSize > 0) {
			fm.setMaturityDate(schdData.getFinanceScheduleDetails().get(sdSize - 1).getSchDate());
		}

		BigDecimal newTotalPft = fm.getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);

		schdData.setPftChg(pftDiff);
		fm.setScheduleRegenerated(true);

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	private FinanceScheduleDetail prepareAddtionalBPISchd(FinScheduleData schdData, FinanceScheduleDetail curSchd,
			int day) {
		FinanceMain fm = schdData.getFinanceMain();
		Date adtlBPIDate = DateUtil.addMonths(curSchd.getSchDate(), -1);

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinID(fm.getFinID());
		sd.setFinReference(fm.getFinReference());
		sd.setSchDate(adtlBPIDate);
		sd.setDefSchdDate(adtlBPIDate);
		sd.setBpiOrHoliday(FinanceConstants.FLAG_ADDTNL_BPI);
		sd.setBalanceForPftCal(BigDecimal.ZERO);
		sd.setNoOfDays(0);
		sd.setDayFactor(BigDecimal.ZERO);
		sd.setProfitCalc(BigDecimal.ZERO);
		sd.setProfitSchd(BigDecimal.ZERO);
		sd.setPrincipalSchd(BigDecimal.ZERO);
		sd.setRepayAmount(BigDecimal.ZERO);
		sd.setProfitBalance(BigDecimal.ZERO);
		sd.setDisbAmount(BigDecimal.ZERO);
		sd.setDownPaymentAmount(BigDecimal.ZERO);
		sd.setCpzAmount(BigDecimal.ZERO);
		sd.setCpzBalance(BigDecimal.ZERO);
		sd.setClosingBalance(BigDecimal.ZERO);
		sd.setProfitFraction(BigDecimal.ZERO);
		sd.setBaseRate(curSchd.getBaseRate());
		sd.setSplRate(curSchd.getSplRate());
		sd.setMrgRate(curSchd.getMrgRate());
		sd.setActRate(curSchd.getActRate());
		sd.setCalculatedRate(curSchd.getCalculatedRate());

		if (StringUtils.isNotEmpty(fm.getBpiPftDaysBasis())) {
			sd.setPftDaysBasis(fm.getBpiPftDaysBasis());
		} else {
			sd.setPftDaysBasis(curSchd.getPftDaysBasis());
		}

		sd.setTDSApplicable(fm.isTDSApplicable());

		if (day > DateUtil.getDay(adtlBPIDate)) {
			int monthEndDate = DateUtil.getDay(DateUtil.getMonthEnd(adtlBPIDate));
			if (day > monthEndDate) {
				adtlBPIDate = DateUtil.getDate(DateUtil.getYear(adtlBPIDate), DateUtil.getMonth(adtlBPIDate) - 1,
						monthEndDate);
			} else {
				adtlBPIDate = DateUtil.getDate(DateUtil.getYear(adtlBPIDate), DateUtil.getMonth(adtlBPIDate) - 1, day);
			}

			sd.setSchDate(adtlBPIDate);
			sd.setDefSchdDate(adtlBPIDate);
		}

		if (DateUtil.compare(adtlBPIDate, fm.getGrcPeriodEndDate()) > 0) {
			sd.setSchdMethod(fm.getScheduleMethod());
		} else {
			sd.setSchdMethod(fm.getGrcSchdMthd());
		}

		if (FinanceConstants.BPI_CAPITALIZE.equals(fm.getBpiTreatment())) {
			sd.setPftOnSchDate(false);
			sd.setCpzOnSchDate(true);
		} else {
			sd.setPftOnSchDate(true);
			sd.setCpzOnSchDate(false);
		}

		sd.setRvwOnSchDate(false);
		sd.setRepayOnSchDate(false);

		return sd;
	}

	/**
	 * Method for Adding Schedule term when missing while on disbursement date checking
	 * 
	 * @param schdData
	 * @param newSchdDate
	 * @param prvIndex
	 * @return
	 */
	private FinScheduleData addSchdRcd(FinScheduleData schdData, Date newSchdDate, int prvIndex) {
		FinanceScheduleDetail prvSchd = schdData.getFinanceScheduleDetails().get(prvIndex);

		FinanceScheduleDetail schdule = new FinanceScheduleDetail();
		FinanceMain fm = schdData.getFinanceMain();

		schdule.setFinID(fm.getFinID());
		schdule.setFinReference(fm.getFinReference());
		schdule.setBpiOrHoliday("");
		schdule.setSchDate(newSchdDate);
		schdule.setDefSchdDate(newSchdDate);

		schdule.setBaseRate(prvSchd.getBaseRate());
		schdule.setSplRate(prvSchd.getSplRate());
		schdule.setMrgRate(prvSchd.getMrgRate());
		schdule.setActRate(prvSchd.getActRate());
		schdule.setCalculatedRate(prvSchd.getCalculatedRate());
		schdule.setSchdMethod(prvSchd.getSchdMethod());
		schdule.setPftDaysBasis(prvSchd.getPftDaysBasis());

		schdData.getFinanceScheduleDetails().add(schdule);
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

		fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);

		return schdData;
	}

	/**
	 * Method for Sorting schedule details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method for validate change frequency instructions
	 * 
	 * @param fsi
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction fsi) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = fsi.isWif();
		long finID = fsi.getFinID();

		FinanceMain financeMain = financeMainDAO.getFinanceDetailsForService(finID, "", isWIF);

		// validate frqDay and frequency
		String frqday = String.valueOf(fsi.getFrqDay());
		frqday = frqday.length() == 1 ? "0".concat(frqday) : frqday;
		String newRepayFrq = StringUtils.substring(financeMain.getRepayFrq(), 0, financeMain.getRepayFrq().length() - 2)
				.concat(frqday);

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
		Date fromDate = fsi.getFromDate();
		if (fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}

		// It shouldn't be past date when compare to appdate
		Date appDate = SysParamUtil.getAppDate();
		if (DateUtil.compare(fsi.getFromDate(), appDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtil.formatToLongDate(appDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
			return auditDetail;
		}

		// validate from date with finStart date and maturity date
		if (fromDate.compareTo(financeMain.getFinStartDate()) < 0
				|| fromDate.compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "finance start date:" + DateUtil.formatToShortDate(financeMain.getFinStartDate());
			valueParm[2] = "maturity date:" + DateUtil.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);

		for (FinanceScheduleDetail schDetail : schedules) {
			if (DateUtil.compare(fromDate, schDetail.getSchDate()) == 0) {
				isValidFromDate = true;
				if (!FinanceConstants.FLAG_BPI.equals(schDetail.getBpiOrHoliday())
						&& checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
					return auditDetail;
				}
			}
		}

		if (!isValidFromDate) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate:" + DateUtil.formatToShortDate(fsi.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
		}

		logger.debug(Literal.LEAVING);
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
		if (!((curSchd.isRepayOnSchDate()
				|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate()
						&& !curSchd.isSchPftPaid())
						|| (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
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
