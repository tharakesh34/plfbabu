package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.ChangeScheduleMethodService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.rits.cloning.Cloner;

public class ChangeScheduleMethodServiceImpl implements ChangeScheduleMethodService {

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private static Logger				logger	= Logger.getLogger(ChangeScheduleMethodServiceImpl.class);

	@Override
	/**
	 * Method for perform schedule method change action based on given instructions.
	 * 
	 * @param scheduleData
	 * @param finServiceInstruction
	 * @return FinScheduleData
	 */
	public FinScheduleData doChangeScheduleMethod(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction) {
		return recalScheduleData(finScheduleData, finServiceInstruction);
	}

	/**
	 * Method for perform re-schedule action based on given instructions.
	 * 
	 * @param scheduleData
	 * @param finServiceInstruction
	 * @return FinScheduleData
	 */
	private FinScheduleData recalScheduleData(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		// Check Date Status Specifier
		FinScheduleData scheduleData = null;
		Cloner cloner = new Cloner();
		scheduleData = cloner.deepClone(finScheduleData);

		FinanceMain financeMain = scheduleData.getFinanceMain();
		Date fromDate = finServiceInstruction.getFromDate();

		// Set Deferred scheduled date and schedule method first time
		FinanceScheduleDetail curSchd = null;
		scheduleData.setFinanceScheduleDetails(sortSchdDetails(scheduleData.getFinanceScheduleDetails()));
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (i == 0) {
				continue;
			}

			// Supplement Rent & Increased Cost Re-Setting
			if (curSchd.getSchDate().compareTo(fromDate) < 0) {
				continue;
			}
			curSchd.setSchdMethod(finServiceInstruction.getSchdMethod());
		}

		// Setting Recalculation Type Method
		financeMain.setEventToDate(financeMain.getMaturityDate());
		financeMain.setRecalFromDate(fromDate);
		financeMain.setRecalToDate(financeMain.getMaturityDate());
		financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		financeMain.setPftIntact(finServiceInstruction.isPftIntact());

		// Setting Desired Values for the Profit Intact option
		if (finServiceInstruction.isPftIntact()) {
			financeMain.setDesiredProfit(financeMain.getTotalGrossPft());
		}

		String oldScheduleMethod = financeMain.getScheduleMethod();
		financeMain.setScheduleMethod(finServiceInstruction.getSchdMethod());
		financeMain.setRecalSchdMethod(finServiceInstruction.getSchdMethod());
		scheduleData = ScheduleCalculator.reCalSchd(scheduleData, financeMain.getRecalSchdMethod());

		// Plan EMI Holidays Resetting after Rescheduling
		if (scheduleData.getFinanceMain().isPlanEMIHAlw()) {
			scheduleData.getFinanceMain().setEventFromDate(fromDate);
			scheduleData.getFinanceMain().setEventToDate(financeMain.getMaturityDate());
			scheduleData.getFinanceMain().setRecalFromDate(fromDate);
			scheduleData.getFinanceMain().setRecalToDate(financeMain.getMaturityDate());
			scheduleData.getFinanceMain().setRecalSchdMethod(finServiceInstruction.getSchdMethod());

			scheduleData.getFinanceMain().setEqualRepay(true);
			scheduleData.getFinanceMain().setCalculateRepay(true);

			if (StringUtils.equals(scheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData = ScheduleCalculator.getFrqEMIHoliday(scheduleData);
			} else {
				scheduleData = ScheduleCalculator.getAdhocEMIHoliday(scheduleData);
			}
		}

		scheduleData.getFinanceMain().setScheduleMethod(oldScheduleMethod);
		scheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return scheduleData;

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
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";
		String finReference = finServiceInstruction.getFinReference();
		Date fromDate = finServiceInstruction.getFromDate();
		//Repayment Schedule Method (If not blanks validation already happens in defaulting)
				if (	!StringUtils.equals(finServiceInstruction.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)
						&& !StringUtils.equals(finServiceInstruction.getSchdMethod(), CalculationConstants.SCHMTHD_PFT)
						&& !StringUtils.equals(finServiceInstruction.getSchdMethod(), CalculationConstants.SCHMTHD_PRI_PFT)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Schedule method";
					valueParm[1] = CalculationConstants.SCHMTHD_EQUAL+", "+ CalculationConstants.SCHMTHD_PFT +", "+CalculationConstants.SCHMTHD_PRI_PFT;
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90337",valueParm)));
					return auditDetail;
				}
		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		// It shouldn't be past date when compare to appdate
		if (DateUtility.compare(finServiceInstruction.getFromDate(), DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtility.formatToLongDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
			return auditDetail;
		}

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
		if (schedules != null) {
			for (FinanceScheduleDetail schDetail : schedules) {
				if (DateUtility.compare(fromDate, schDetail.getSchDate()) == 0) {
					isValidFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
						return auditDetail;
					}
				}
			}

			if (!isValidFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "FromDate:" + DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				return auditDetail;
			}
		}
		// FromDate should be Unpaid Date
		FinanceScheduleDetail finScheduleDetail = financeScheduleDetailDAO.getFinanceScheduleDetailById(finReference,
				fromDate, "", isWIF);
		BigDecimal paidAmount = finScheduleDetail.getSchdPriPaid()
				.add(finScheduleDetail.getSchdFeePaid().add(finScheduleDetail.getSchdPftPaid()));

		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:" + DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91116", "", valueParm), lang));
		}
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

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
