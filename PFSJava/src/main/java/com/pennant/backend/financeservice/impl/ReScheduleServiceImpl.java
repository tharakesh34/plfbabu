package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennapps.core.util.ObjectUtil;

public class ReScheduleServiceImpl extends GenericService<FinServiceInstruction> implements ReScheduleService {
	private static Logger logger = LogManager.getLogger(ReScheduleServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	/**
	 * Method for perform re-schedule action based on given instructions.
	 * 
	 * @param scheduleData
	 * @param finServiceInstruction
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doReSchedule(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction) {
		return recalScheduleData(finScheduleData, finServiceInstruction, null);
	}

	/**
	 * Method for perform re-schedule action based on given instructions.
	 * 
	 * @param scheduleData
	 * @param finServiceInstruction
	 * @return FinScheduleData
	 */
	private FinScheduleData recalScheduleData(FinScheduleData finScheduleData,
			FinServiceInstruction finServiceInstruction, Date maturityDate) {
		logger.debug("Entering");

		// Check Date Status Specifier
		boolean calFromGrcPeriod = false;

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		FinScheduleData scheduleData = null;
		scheduleData = ObjectUtil.clone(finScheduleData);

		FinanceMain financeMain = scheduleData.getFinanceMain();
		Date fromDate = finServiceInstruction.getFromDate();
		String frequency = finServiceInstruction.getRepayFrq();
		int terms = finServiceInstruction.getTerms();

		List<FinanceScheduleDetail> scheduleList = scheduleData.getFinanceScheduleDetails();
		if (fromDate != null && financeMain.isAllowGrcPeriod()
				&& fromDate.compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
			calFromGrcPeriod = true;
		}

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
		Map<Date, FinanceScheduleDetail> mapList = new HashMap<>();
		BigDecimal schPriDue = BigDecimal.ZERO;
		BigDecimal schPftDue = BigDecimal.ZERO;
		BigDecimal unModifiedPft = BigDecimal.ZERO;
		// Set Original end balance of Event From Date schedule
		BigDecimal orgEndBalOnFromDate = BigDecimal.ZERO;

		Date rateAppliedFromDate = null;
		for (int i = 0; i < scheduleList.size(); i++) {
			if (scheduleList.get(i).getSchDate().compareTo(fromDate) <= 0) {
				rateAppliedFromDate = scheduleList.get(i).getSchDate();
				mapList.put(scheduleList.get(i).getSchDate(), scheduleList.get(i));
				unModifiedPft = unModifiedPft.add(scheduleList.get(i).getProfitSchd());
				if (scheduleList.get(i).getSchDate().compareTo(fromDate) == 0) {
					orgEndBalOnFromDate = scheduleList.get(i).getOrgEndBal();
				}
			} else {
				// if there is any part payment done after selected event from date
				if (!scheduleList.get(i).isFrqDate()
						&& scheduleList.get(i).getPrincipalSchd().compareTo(scheduleList.get(i).getRepayAmount()) == 0
						&& scheduleList.get(i).getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) {
					mapList.put(scheduleList.get(i).getSchDate(), scheduleList.get(i));
				}
				schPftDue = schPftDue.add(scheduleList.get(i).getProfitSchd());
				schPriDue = schPriDue.add(scheduleList.get(i).getPrincipalSchd());
			}
		}
		scheduleData.setScheduleMap(mapList);
		mapList = null;

		Date startRepayCalDate = null;
		Date recalToDate = null;
		String frqCode = frequency.substring(0, 1);
		boolean isMonthlyFrq = StringUtils.equals(frqCode, FrequencyCodeTypes.FRQ_MONTHLY) ? true : false;

		// Setting Event From Date Value
		if (calFromGrcPeriod) {
			Date grcEndDate = finServiceInstruction.getGrcPeriodEndDate();
			financeMain.setGrcPeriodEndDate(grcEndDate);

			if (DateUtil.compare(fromDate, grcEndDate) < 0) {
				financeMain.setEventFromDate(fromDate);
			} else {
				Date eventFromDate = FrequencyUtil.getNextDate(finServiceInstruction.getRepayFrq(), 1,
						finServiceInstruction.getFromDate(), "A", false, isMonthlyFrq ? 30 : 0).getNextFrequencyDate();
				eventFromDate = DateUtil.getDatePart(eventFromDate);
				fromDate = eventFromDate;
				financeMain.setEventFromDate(eventFromDate);
			}

			if (finServiceInstruction.getNextRepayDate() != null) {
				startRepayCalDate = finServiceInstruction.getNextRepayDate();
				recalToDate = finServiceInstruction.getNextRepayDate();
			} else {
				startRepayCalDate = FrequencyUtil
						.getNextDate(frequency, 1, finServiceInstruction.getGrcPeriodEndDate(), "A", false, 0)
						.getNextFrequencyDate();

				if (!StringUtils.equals(frqCode, FrequencyCodeTypes.FRQ_DAILY)
						&& DateUtil.getDaysBetween(fromDate, startRepayCalDate) <= 15) {
					startRepayCalDate = FrequencyUtil.getNextDate(frequency, 1, startRepayCalDate, "A", false, 0)
							.getNextFrequencyDate();
				}

				startRepayCalDate = DateUtil.getDatePart(startRepayCalDate);

			}

			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(frequency, terms, startRepayCalDate, "A", true, 0).getScheduleList();
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				recalToDate = calendar.getTime();
				recalToDate = DateUtil.getDatePart(recalToDate);
			}
			scheduleDateList = null;

			// Set the limits based on system values table
			int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
			Date lastDateLimit = new Date();
			lastDateLimit = DateUtil.addYears(financeMain.getFinStartDate(), maxFinYears);

			if (DateUtil.compare(recalToDate, lastDateLimit) > 0) {
				// Through Error
				finScheduleData.setErrorDetail(new ErrorDetail("SCH30",
						"ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS. NOT ALLOWED TO ADD MORE TERMS.",
						new String[] { " " }));
				return finScheduleData;
			}

			financeMain.setMaturityDate(recalToDate);

			// Schedule Dates Generation Process calculation
			scheduleData = ScheduleGenerator.getScheduleDateList(scheduleData, finServiceInstruction, fromDate,
					startRepayCalDate, recalToDate);
		} else {
			if (finServiceInstruction.getNextRepayDate() != null) {
				startRepayCalDate = DateUtil.getDatePart(finServiceInstruction.getNextRepayDate());
			} else {
				startRepayCalDate = FrequencyUtil
						.getNextDate(frequency, 1, finServiceInstruction.getFromDate(), "A", false, 0)
						.getNextFrequencyDate();

				if (!StringUtils.equals(frqCode, FrequencyCodeTypes.FRQ_DAILY)
						&& DateUtil.getDaysBetween(fromDate, startRepayCalDate) <= 15) {
					startRepayCalDate = FrequencyUtil.getNextDate(frequency, 1, startRepayCalDate, "A", false, 0)
							.getNextFrequencyDate();
				}
			}

			if (maturityDate != null) {
				recalToDate = maturityDate;
			} else {
				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(frequency, terms, startRepayCalDate, "A", true).getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					recalToDate = DateUtil.getDatePart(calendar.getTime());
				}
				scheduleDateList = null;
			}

			// Set the limits based on system values table
			int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
			Date lastDateLimit = new Date();
			lastDateLimit = DateUtil.addYears(financeMain.getFinStartDate(), maxFinYears);

			if (DateUtil.compare(recalToDate, lastDateLimit) > 0) {
				// Through Error
				finScheduleData.setErrorDetail(new ErrorDetail("SCH30",
						"ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS. NOT ALLOWED TO ADD MORE TERMS.",
						new String[] { " " }));
				return finScheduleData;
			}

			Date eventFromDate = startRepayCalDate;
			if (!StringUtils.equals(finServiceInstruction.getRepayPftFrq(), finServiceInstruction.getRepayFrq())) {
				eventFromDate = FrequencyUtil
						.getNextDate(finServiceInstruction.getRepayPftFrq(), 1, fromDate, "A", false, 0)
						.getNextFrequencyDate();
				if (DateUtil.compare(eventFromDate, startRepayCalDate) > 0) {
					eventFromDate = startRepayCalDate;
				}
				eventFromDate = DateUtil.getDatePart(eventFromDate);
			}

			if (financeMain.isAllowRepayRvw() && !StringUtils.equals(finServiceInstruction.getRepayRvwFrq(),
					finServiceInstruction.getRepayFrq())) {
				Date rvwDate = FrequencyUtil
						.getNextDate(finServiceInstruction.getRepayRvwFrq(), 1, fromDate, "A", false, 0)
						.getNextFrequencyDate();
				if (DateUtil.compare(rvwDate, eventFromDate) < 0) {
					eventFromDate = rvwDate;
				}
				eventFromDate = DateUtil.getDatePart(eventFromDate);
			}

			if (financeMain.isAllowRepayCpz() && !StringUtils.equals(finServiceInstruction.getRepayCpzFrq(),
					finServiceInstruction.getRepayFrq())) {
				Date cpzDate = FrequencyUtil
						.getNextDate(finServiceInstruction.getRepayCpzFrq(), 1, fromDate, "A", false, 0)
						.getNextFrequencyDate();
				if (DateUtil.compare(cpzDate, eventFromDate) < 0) {
					eventFromDate = cpzDate;
				}
				eventFromDate = DateUtil.getDatePart(eventFromDate);
			}

			financeMain.setEventFromDate(eventFromDate);
			financeMain.setMaturityDate(recalToDate);

			// Schedule Dates Generation Process calculation
			scheduleData = ScheduleGenerator.getScheduleDateList(scheduleData, finServiceInstruction, fromDate,
					startRepayCalDate, recalToDate);
			startRepayCalDate = eventFromDate;
		}

		boolean isDisbDateFoundInSD = false;
		int disbIndex = 0;
		List<FinanceScheduleDetail> finSchdDetails = scheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		// Add Disbursement amount to existing record if found
		List<FinanceDisbursement> finDisbDetails = scheduleData.getDisbursementDetails();
		FinanceScheduleDetail curSchd = null;
		Date schdDate = financeMain.getFinStartDate();
		boolean disbMaturityCrossed = false;
		for (int k = 0; k < finDisbDetails.size(); k++) {

			FinanceDisbursement curDisb = finDisbDetails.get(k);
			if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
				continue;
			}
			Date curDisbDate = curDisb.getDisbDate();
			isDisbDateFoundInSD = false;
			if (curDisbDate.compareTo(fromDate) <= 0 || curDisbDate.compareTo(financeMain.getFinStartDate()) == 0) {
				continue;
			}

			// Existing Disbursement Date should be less than Newly calculated Maturity Term
			if (curDisbDate.compareTo(financeMain.getMaturityDate()) >= 0) {
				disbMaturityCrossed = true;
				String[] valueParm = new String[1];
				valueParm[0] = DateUtil.formatToLongDate(curDisbDate);
				finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30575", "", valueParm), "EN"));
				break;
			}

			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
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
				scheduleData = addSchdRcd(scheduleData, curDisbDate, disbIndex);
				FinanceScheduleDetail prvSchd = finSchdDetails.get(disbIndex);
				disbIndex = disbIndex + 1;
				curSchd = scheduleData.getFinanceScheduleDetails().get(disbIndex);

				curSchd.setDisbOnSchDate(true);
				curSchd.setDisbAmount(curDisb.getDisbAmount());
				curSchd.setFeeChargeAmt(curDisb.getFeeChargeAmt());
				curSchd.setClosingBalance(
						prvSchd.getClosingBalance().add(curDisb.getDisbAmount()).add(curDisb.getFeeChargeAmt()));
			}
		}

		// If Existing Disbursement crossed newly calculated maturity Date
		if (disbMaturityCrossed) {
			return finScheduleData;
		}

		// Set Deferred scheduled date and schedule method first time
		chkFirstRpyDate = false;
		Date newFirstRpyDate = null;
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (i == 0) {
				if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
					chkFirstRpyDate = true;
				}
				continue;
			}
			FinanceScheduleDetail prvSchd = scheduleData.getFinanceScheduleDetails().get(i - 1);

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				if (!financeMain.isAllowGrcRepay()) {
					curSchd.setSchdMethod(CalculationConstants.SCHMTHD_NOPAY);
				} else {
					curSchd.setSchdMethod(financeMain.getGrcSchdMthd());
				}
				// Profit Days Basis Setting
				if (StringUtils.isEmpty(curSchd.getPftDaysBasis())) {
					curSchd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());
				}
				curSchd.setActRate(financeMain.getGrcPftRate());
				curSchd.setBaseRate(financeMain.getGraceBaseRate());
				curSchd.setSplRate(financeMain.getGraceSpecialRate());
				curSchd.setMrgRate(financeMain.getGrcMargin());
			} else {
				curSchd.setSchdMethod(financeMain.getScheduleMethod());
				// Profit Days Basis Setting
				if (StringUtils.isEmpty(curSchd.getPftDaysBasis())) {
					if (!StringUtils.equals(curSchd.getBpiOrHoliday(), "B")) {
						curSchd.setPftDaysBasis(financeMain.getProfitDaysBasis());
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), "B")) {
						curSchd.setPftDaysBasis(financeMain.getBpiPftDaysBasis());
					}
				}
			}

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) >= 0) {
				if (chkFirstRpyDate && newFirstRpyDate == null) {
					newFirstRpyDate = curSchd.getSchDate();
				}
				chkFirstRpyDate = true;
			}

			curSchd.setTDSApplicable(TDSCalculator.isTDSApplicable(financeMain));

			if (fromDate.compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
				if (i != 0 && curSchd.getSchDate().compareTo(fromDate) > 0) {
					curSchd.setCalculatedRate(scheduleData.getFinanceScheduleDetails().get(i - 1).getCalculatedRate());
				}
			} else {
				if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
					if (i != 0 && curSchd.getSchDate().compareTo(fromDate) >= 0) {
						curSchd.setCalculatedRate(
								scheduleData.getFinanceScheduleDetails().get(i - 1).getCalculatedRate());
					}
				} else {
					curSchd.setCalculatedRate(repayCalRate);
				}
			}
		}

		// DE#1550(24-10-2020) - While doing the ReScheduling, RPS is not plotting properly in case of Grace.
		/*
		 * Date recalLockTill = finScheduleData.getFinanceMain().getRecalFromDate(); if (recalLockTill == null) {
		 * recalLockTill = finScheduleData.getFinanceMain().getMaturityDate(); }
		 */

		Date recalLockTill = finServiceInstruction.getFromDate();
		boolean alwRecalLock = SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK);

		// Rate Modification for All Modified Schedules
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			FinanceScheduleDetail prvSchd = null;
			if (i != 0) {
				prvSchd = scheduleData.getFinanceScheduleDetails().get(i - 1);
			}
			if (curSchd.getSchDate().compareTo(rateAppliedFromDate) >= 0) {

				if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
					if (StringUtils.isEmpty(finServiceInstruction.getGraceBaseRate())) {
						curSchd.setCalculatedRate(finServiceInstruction.getGrcPftRate() == null ? BigDecimal.ZERO
								: finServiceInstruction.getGrcPftRate());
					} else {
						BigDecimal recalculateRate = RateUtil.rates(finServiceInstruction.getGraceBaseRate(),
								financeMain.getFinCcy(), null, finServiceInstruction.getGrcMargin(),
								curSchd.getSchDate(), financeMain.getGrcMinRate(), financeMain.getGrcMaxRate())
								.getNetRefRateLoan();

						curSchd.setCalculatedRate(recalculateRate);
					}
					curSchd.setBaseRate(StringUtils.trimToNull(finServiceInstruction.getGraceBaseRate()));
					curSchd.setSplRate(StringUtils.trimToNull(finServiceInstruction.getGraceSpecialRate()));
					curSchd.setMrgRate(
							StringUtils.trimToNull(finServiceInstruction.getGraceBaseRate()) == null ? BigDecimal.ZERO
									: finServiceInstruction.getGrcMargin() == null ? BigDecimal.ZERO
											: finServiceInstruction.getGrcMargin());
					curSchd.setActRate(finServiceInstruction.getGrcPftRate() == null ? BigDecimal.ZERO
							: finServiceInstruction.getGrcPftRate());

				} else {
					if (StringUtils.isEmpty(finServiceInstruction.getBaseRate())) {
						curSchd.setCalculatedRate(finServiceInstruction.getActualRate() == null ? BigDecimal.ZERO
								: finServiceInstruction.getActualRate());
					} else {
						BigDecimal recalculateRate = RateUtil.rates(finServiceInstruction.getBaseRate(),
								financeMain.getFinCcy(), null, finServiceInstruction.getMargin(), curSchd.getSchDate(),
								financeMain.getRpyMinRate(), financeMain.getRpyMaxRate()).getNetRefRateLoan();

						curSchd.setCalculatedRate(recalculateRate);
					}
					curSchd.setBaseRate(StringUtils.trimToNull(finServiceInstruction.getBaseRate()));
					curSchd.setSplRate(StringUtils.trimToNull(finServiceInstruction.getSplRate()));
					curSchd.setMrgRate(
							StringUtils.trimToNull(finServiceInstruction.getBaseRate()) == null ? BigDecimal.ZERO
									: finServiceInstruction.getMargin() == null ? BigDecimal.ZERO
											: finServiceInstruction.getMargin());
					curSchd.setActRate(finServiceInstruction.getActualRate() == null ? BigDecimal.ZERO
							: finServiceInstruction.getActualRate());
				}
			}

			// Schedule Recalculation Locking Period Applicability
			if (alwRecalLock) {
				if (DateUtil.compare(curSchd.getSchDate(), recalLockTill) < 0
						&& (i != scheduleData.getFinanceScheduleDetails().size() - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		// For Grace Period Date Selection check Repay Instruction Details
		List<RepayInstruction> instructionList = scheduleData.getRepayInstructions();
		for (int i = 0; i < instructionList.size(); i++) {
			if (firstRepayDate != null && firstRepayDate.compareTo(instructionList.get(i).getRepayDate()) == 0) {
				instructionList.get(i).setRepayDate(newFirstRpyDate);
			}
		}

		// Setting Recalculation Type Method
		financeMain.setEventToDate(recalToDate);
		financeMain.setRecalFromDate(startRepayCalDate);
		financeMain.setRecalToDate(financeMain.getMaturityDate());

		if (financeMain.isSanBsdSchdle()) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		} else {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			if (financeMain.isStepFinance() && (StringUtils.equals(financeMain.getScheduleMethod(),
					CalculationConstants.SCHMTHD_PRI)
					|| StringUtils.equals(financeMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT))) {

				// financeMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
				financeMain.setProcMethod(FinServiceEvent.RESCHD);

				for (FinanceScheduleDetail finSch : scheduleData.getFinanceScheduleDetails()) {
					if (finSch.getSchDate().compareTo(financeMain.getEventFromDate()) > 0) {
						if (!finSch.isRepayOnSchDate()) {
							continue;
						}
						financeMain.setRecalFromDate(finSch.getSchDate());
						break;
					}
				}
				financeMain.setResetOrgBal(false);

				int idxStart = 0;
				int riStart = 0;
				int riEnd = 0;
				boolean grcEnd = false;

				List<FinanceStepPolicyDetail> spdList = scheduleData.getStepPolicyDetails();
				scheduleList = scheduleData.getFinanceScheduleDetails();
				boolean lastStep = false;

				for (int i = 0; i < spdList.size(); i++) {

					FinanceStepPolicyDetail spd = spdList.get(i);
					if (grcEnd && PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
						continue;
					}

					riStart = idxStart;
					if (riEnd == 0) {
						riEnd = riStart + spd.getInstallments();
					} else {
						riEnd = riStart + spd.getInstallments() - 1;
					}

					BigDecimal steppedEMI = BigDecimal.ZERO;
					if (i == spdList.size() - 1) {
						lastStep = true;

						steppedEMI = orgEndBalOnFromDate.divide(new BigDecimal(spd.getInstallments()), 9,
								RoundingMode.HALF_DOWN);
						steppedEMI = steppedEMI.setScale(0, RoundingMode.HALF_DOWN);
						steppedEMI = CalculationUtil.roundAmount(steppedEMI, financeMain.getCalRoundingMode(),
								financeMain.getRoundingTarget());

					}

					int instCount = 0;
					for (int iFsd = idxStart; iFsd < scheduleList.size(); iFsd++) {
						FinanceScheduleDetail fsd = scheduleList.get(iFsd);

						// Part payment installment is also considering for installment count so added isFrqDate
						// condition.
						String specifier = fsd.getSpecifier();
						if (fsd.isRepayOnSchDate() && fsd.isFrqDate()
								&& !(StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI))) {
							instCount = instCount + 1;

							if (lastStep) {
								fsd.setOrgPri(steppedEMI);
							} else {
								fsd.setOrgPri(spd.getSteppedEMI());
							}

						} else if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())
								&& !(FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday()))
								&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
										|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
								&& fsd.isFrqDate()) {
							instCount = instCount + 1;

							if (lastStep) {
								fsd.setOrgPri(steppedEMI);
							} else {
								fsd.setOrgPri(spd.getSteppedEMI());
							}
						}

						// Set Original End Balance
						fsd.setOrgEndBal(orgEndBalOnFromDate.subtract(fsd.getOrgPri()));
						orgEndBalOnFromDate = orgEndBalOnFromDate.subtract(fsd.getOrgPri());

						if (spd.getInstallments() == instCount) {
							if (CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier)
									&& CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier)) {
								grcEnd = true;
							}
							idxStart = iFsd + 1;

							for (int j = idxStart; j < scheduleList.size(); j++) {

								FinanceScheduleDetail sd = scheduleList.get(j);

								if (sd.isRepayOnSchDate() && sd.isFrqDate() && !sd.isDisbOnSchDate()
										&& !(StringUtils.equals(sd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI))) {
									break;
								} else if (iFsd != 0
										&& PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())
										&& !(FinanceConstants.FLAG_BPI.equals(sd.getBpiOrHoliday()))
										&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(sd.getSpecifier())
												|| CalculationConstants.SCH_SPECIFIER_GRACE_END
														.equals(sd.getSpecifier()))
										&& !sd.isDisbOnSchDate() && sd.isFrqDate()) {

									break;
								}

								// Set Original End Balance
								sd.setOrgEndBal(orgEndBalOnFromDate);

								idxStart++;
							}

							break;
						}
					}
				}
			}
		}
		financeMain.setPftIntact(finServiceInstruction.isPftIntact());

		// Setting Desired Values for the Profit Intact option
		if (finServiceInstruction.isPftIntact()) {
			financeMain.setDesiredProfit(financeMain.getTotalGrossPft());
			financeMain.setAdjTerms(terms);
			financeMain.setSchPftDue(schPftDue);
			financeMain.setSchPriDue(schPriDue);
		}

		// TODO: PV 19JAN17 schdMethod to be added
		financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
		scheduleData = ScheduleCalculator.reCalSchd(scheduleData, financeMain.getScheduleMethod());

		// Plan EMI Holidays Resetting after Rescheduling
		if (scheduleData.getFinanceMain().isPlanEMIHAlw()) {
			if (!(financeMain.isStepFinance()
					&& PennantConstants.STEPPING_CALC_AMT.equals(financeMain.getCalcOfSteps()))) {
				scheduleData.getFinanceMain().setEventFromDate(startRepayCalDate);
				scheduleData.getFinanceMain().setEventToDate(scheduleData.getFinanceMain().getMaturityDate());
				scheduleData.getFinanceMain().setRecalFromDate(startRepayCalDate);
				scheduleData.getFinanceMain().setRecalToDate(scheduleData.getFinanceMain().getMaturityDate());
				scheduleData.getFinanceMain().setRecalSchdMethod(scheduleData.getFinanceMain().getScheduleMethod());
			}
			scheduleData.getFinanceMain().setEqualRepay(true);
			scheduleData.getFinanceMain().setCalculateRepay(true);

			if (StringUtils.equals(scheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData = ScheduleCalculator.getFrqEMIHoliday(scheduleData);
			} else {
				scheduleData = ScheduleCalculator.getAdhocEMIHoliday(scheduleData);
			}
		}

		BigDecimal newTotalPft = scheduleData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		scheduleData.setPftChg(pftDiff);
		scheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return scheduleData;
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

		FinanceMain fm = schdData.getFinanceMain();

		FinanceScheduleDetail sd = new FinanceScheduleDetail();

		sd.setFinID(fm.getFinID());
		sd.setFinReference(fm.getFinReference());
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

		schdData.getFinanceScheduleDetails().add(sd);
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

		fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);

		return schdData;
	}

	/**
	 * Method for Resetting Data and Recalculate Schedule when Overdraft Maintained
	 * 
	 * @param finScheduleData
	 * @param newSchdDate
	 * @param prvIndex
	 * @return
	 */
	@Override
	public FinScheduleData doResetOverdraftSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		// Finance Service Instruction Preparation
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinServiceInstruction serviceInstruction = new FinServiceInstruction();
		serviceInstruction.setFromDate(finMain.getEventFromDate());
		serviceInstruction.setRepayFrq(finMain.getRepayFrq());
		serviceInstruction.setRepayRvwFrq(finMain.getRepayRvwFrq());
		serviceInstruction.setRepayPftFrq(finMain.getRepayPftFrq());

		Date startCalFrom = finMain.getFinStartDate();
		FinanceScheduleDetail prvSchd = null;
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (DateUtil.compare(curSchd.getSchDate(), finMain.getEventFromDate()) >= 0) {
				Date brforeDate = null;
				if (prvSchd == null) {
					brforeDate = finMain.getFinStartDate();
					startCalFrom = FrequencyUtil
							.getNextDate(finMain.getRepayFrq(), 1, finMain.getFinStartDate(), "A", false)
							.getNextFrequencyDate();
				} else {
					brforeDate = prvSchd.getSchDate();
					startCalFrom = FrequencyUtil.getNextDate(finMain.getRepayFrq(), 1, prvSchd.getSchDate(), "A", false)
							.getNextFrequencyDate();
				}

				String frqCode = finMain.getRepayFrq().substring(0, 1);
				if (!StringUtils.equals(frqCode, FrequencyCodeTypes.FRQ_DAILY)
						&& DateUtil.getDaysBetween(brforeDate, startCalFrom) <= 15) {
					startCalFrom = FrequencyUtil.getNextDate(finMain.getRepayFrq(), 1, startCalFrom, "A", false)
							.getNextFrequencyDate();
				}
				if (DateUtil.compare(startCalFrom, finMain.getMaturityDate()) >= 0) {
					startCalFrom = finMain.getMaturityDate();
				}
				break;
			}
			prvSchd = curSchd;
		}

		int terms = FrequencyUtil.getTerms(finMain.getRepayFrq(), startCalFrom, finMain.getMaturityDate(), true, true)
				.getTerms();
		serviceInstruction.setTerms(terms);
		serviceInstruction.setNextRepayDate(startCalFrom);
		serviceInstruction.setPftIntact(false);
		serviceInstruction.setBaseRate(finMain.getRepayBaseRate());
		serviceInstruction.setSplRate(finMain.getRepaySpecialRate());
		serviceInstruction.setMargin(finMain.getRepayMargin());
		serviceInstruction.setActualRate(finMain.getRepayProfitRate());

		// Schedule Recalculation
		finScheduleData = recalScheduleData(finScheduleData, serviceInstruction, finMain.getMaturityDate());

		logger.debug("Leaving");
		return finScheduleData;
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
	 * Validate Re-Schedule request object
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

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);
		for (FinanceScheduleDetail schDetail : schedules) {
			if (DateUtil.compare(fromDate, schDetail.getSchDate()) == 0) {
				isValidFromDate = true;
				if (checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
					return auditDetail;
				}
			}
		}

		if (!isValidFromDate) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate:" + DateUtil.formatToShortDate(fsi.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
			return auditDetail;
		}
		if (fsi.getNextRepayDate() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "NextRepayDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// validate Next payment date with finStart date and maturity date
		if (fsi.getNextRepayDate().compareTo(fromDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Next RepayDate";
			valueParm[1] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			return auditDetail;
		}

		// FromDate should be Unpaid Date
		FinanceScheduleDetail schedule = financeScheduleDetailDAO.getFinanceScheduleDetailById(finID, fromDate, "",
				isWIF);
		BigDecimal paidAmount = schedule.getSchdPriPaid().add(schedule.getSchdFeePaid().add(schedule.getSchdPftPaid()));

		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91116", "", valueParm), lang));
		}

		// validate repay frequency code
		ErrorDetail errorDetail = FrequencyUtil.validateFrequency(fsi.getRepayFrq());
		if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = fsi.getRepayFrq();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90207", "", valueParm), lang));
		}

		// terms
		if (fsi.getTerms() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Terms";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
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
}
