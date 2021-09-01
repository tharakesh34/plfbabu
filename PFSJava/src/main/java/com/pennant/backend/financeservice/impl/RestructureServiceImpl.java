package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.RestructureDAO;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class RestructureServiceImpl extends GenericService<FinServiceInstruction> implements RestructureService {
	private static final Logger logger = LogManager.getLogger(RestructureServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private RestructureDAO restructureDAO;
	private FinanceMainDAO financeMainDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private LatePayMarkingService latePayMarkingService;
	private FinanceRepaymentsDAO financeRepaymentsDAO;

	@Override
	public FinScheduleData doRestructure(FinScheduleData schdData, FinServiceInstruction finServiceInstruction) {
		return recalScheduleData(schdData, finServiceInstruction, null);
	}

	private FinScheduleData recalScheduleData(FinScheduleData schdData, FinServiceInstruction finServiceInstruction,
			Date maturityDate) {
		logger.debug(Literal.ENTERING);

		// Check Date Status Specifier
		boolean calFromGrcPeriod = false;

		BigDecimal oldTotalPft = schdData.getFinanceMain().getTotalGrossPft();
		FinScheduleData scheduleData = null;
		Cloner cloner = new Cloner();
		scheduleData = cloner.deepClone(schdData);

		FinanceMain fm = scheduleData.getFinanceMain();
		Date fromDate = finServiceInstruction.getFromDate();
		String frequency = finServiceInstruction.getRepayFrq();
		int terms = finServiceInstruction.getTerms();

		List<FinanceScheduleDetail> schedules = scheduleData.getFinanceScheduleDetails();
		if (fromDate != null && fm.isAllowGrcPeriod() && fromDate.compareTo(fm.getGrcPeriodEndDate()) < 0) {
			calFromGrcPeriod = true;
		}

		// Repayment Calculated Rate storing
		Date firstRepayDate = null;
		boolean chkFirstRpyDate = false;
		BigDecimal repayCalRate = fm.getRepayProfitRate();
		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getSchDate().compareTo(fm.getGrcPeriodEndDate()) >= 0) {
				if (chkFirstRpyDate) {
					firstRepayDate = schedules.get(i).getSchDate();
					break;
				}
				repayCalRate = schedules.get(i).getCalculatedRate();
				chkFirstRpyDate = true;
			}
		}

		// Removing Schedule Details from Selected Recalculation From Date
		Map<Date, FinanceScheduleDetail> mapList = new HashMap<>();
		BigDecimal schPriDue = BigDecimal.ZERO;
		BigDecimal schPftDue = BigDecimal.ZERO;
		BigDecimal unModifiedPft = BigDecimal.ZERO;
		Date rateAppliedFromDate = null;
		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getSchDate().compareTo(fromDate) <= 0) {
				rateAppliedFromDate = schedules.get(i).getSchDate();
				mapList.put(schedules.get(i).getSchDate(), schedules.get(i));
				unModifiedPft = unModifiedPft.add(schedules.get(i).getProfitSchd());
			} else {

				schPftDue = schPftDue.add(schedules.get(i).getProfitSchd());
				schPriDue = schPriDue.add(schedules.get(i).getPrincipalSchd());
			}
		}
		scheduleData.setScheduleMap(mapList);
		mapList = null;

		Date startRepayCalDate = null;
		Date recalToDate = null;

		// Setting Event From Date Value
		if (calFromGrcPeriod) {
			fm.setGrcPeriodEndDate(finServiceInstruction.getGrcPeriodEndDate());
			if (finServiceInstruction.getNextGrcRepayDate() != null) {
				fm.setEventFromDate(finServiceInstruction.getNextGrcRepayDate());
			} else {

				Date eventFromDate = FrequencyUtil.getNextDate(finServiceInstruction.getRepayFrq(), 1,
						finServiceInstruction.getFromDate(), "A", false, 0).getNextFrequencyDate();
				fm.setEventFromDate(eventFromDate);
			}

			if (finServiceInstruction.getNextRepayDate() != null) {
				startRepayCalDate = finServiceInstruction.getNextRepayDate();
				recalToDate = finServiceInstruction.getNextRepayDate();
			} else {
				startRepayCalDate = FrequencyUtil
						.getNextDate(frequency, 1, finServiceInstruction.getGrcPeriodEndDate(), "A", false, 0)
						.getNextFrequencyDate();

				if (DateUtil.getDaysBetween(fromDate, startRepayCalDate) <= 15) {
					startRepayCalDate = FrequencyUtil.getNextDate(frequency, 1, startRepayCalDate, "A", false, 0)
							.getNextFrequencyDate();
				}

			}

			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(frequency, terms, startRepayCalDate, "A", true, 0).getScheduleList();
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				recalToDate = calendar.getTime();
			}
			scheduleDateList = null;

			// Set the limits based on system values table
			int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
			Date lastDateLimit = new Date();
			lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

			if (DateUtil.compare(recalToDate, lastDateLimit) > 0) {
				// Through Error
				schdData.setErrorDetail(new ErrorDetail("SCH30",
						"ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS. NOT ALLOWED TO ADD MORE TERMS.",
						new String[] { " " }));
				return schdData;
			}

			fm.setMaturityDate(recalToDate);

			// Schedule Dates Generation Process calculation
			scheduleData = ScheduleGenerator.getScheduleDateList(scheduleData, finServiceInstruction, fromDate,
					startRepayCalDate, recalToDate);
		} else {
			if (finServiceInstruction.getNextRepayDate() != null) {
				startRepayCalDate = finServiceInstruction.getNextRepayDate();
			} else {
				startRepayCalDate = FrequencyUtil
						.getNextDate(frequency, 1, finServiceInstruction.getFromDate(), "A", false, 0)
						.getNextFrequencyDate();

				if (DateUtil.getDaysBetween(fromDate, startRepayCalDate) <= 15) {
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
					recalToDate = calendar.getTime();
				}
				scheduleDateList = null;
			}

			// Set the limits based on system values table
			int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
			Date lastDateLimit = new Date();
			lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

			if (DateUtil.compare(recalToDate, lastDateLimit) > 0) {
				schdData.setErrorDetail(new ErrorDetail("SCH30",
						"ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS. NOT ALLOWED TO ADD MORE TERMS.",
						new String[] { " " }));
				return schdData;
			}

			fm.setEventFromDate(startRepayCalDate);
			fm.setMaturityDate(recalToDate);

			// Schedule Dates Generation Process calculation
			scheduleData = ScheduleGenerator.getScheduleDateList(scheduleData, finServiceInstruction, fromDate,
					startRepayCalDate, recalToDate);
		}

		boolean isDisbDateFoundInSD = false;
		int disbIndex = 0;
		List<FinanceScheduleDetail> finSchdDetails = scheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		// Add Disbursement amount to existing record if found
		List<FinanceDisbursement> finDisbDetails = scheduleData.getDisbursementDetails();
		FinanceScheduleDetail curSchd = null;
		Date schdDate = fm.getFinStartDate();
		boolean disbMaturityCrossed = false;
		for (int k = 0; k < finDisbDetails.size(); k++) {

			FinanceDisbursement curDisb = finDisbDetails.get(k);
			if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
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
			return schdData;
		}

		// Set Deferred scheduled date and schedule method first time
		chkFirstRpyDate = false;
		Date newFirstRpyDate = null;
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (i == 0) {
				if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) == 0) {
					chkFirstRpyDate = true;
				}
				continue;
			}
			FinanceScheduleDetail prvSchd = scheduleData.getFinanceScheduleDetails().get(i - 1);

			// Profit Days Basis Setting
			if (StringUtils.isEmpty(curSchd.getPftDaysBasis())) {
				curSchd.setPftDaysBasis(prvSchd.getPftDaysBasis());
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
				if (!fm.isAllowGrcRepay()) {
					curSchd.setSchdMethod(CalculationConstants.SCHMTHD_NOPAY);
				} else {
					curSchd.setSchdMethod(fm.getGrcSchdMthd());
				}
			} else {
				curSchd.setSchdMethod(fm.getScheduleMethod());
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) >= 0) {
				if (chkFirstRpyDate && newFirstRpyDate == null) {
					newFirstRpyDate = curSchd.getSchDate();
				}
				chkFirstRpyDate = true;
			}

			if (fromDate.compareTo(fm.getGrcPeriodEndDate()) > 0) {
				if (i != 0 && curSchd.getSchDate().compareTo(fromDate) > 0) {
					curSchd.setCalculatedRate(scheduleData.getFinanceScheduleDetails().get(i - 1).getCalculatedRate());
				}
			} else {
				if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) < 0) {
					if (i != 0 && curSchd.getSchDate().compareTo(fromDate) > 0) {
						curSchd.setCalculatedRate(
								scheduleData.getFinanceScheduleDetails().get(i - 1).getCalculatedRate());
					}
				} else {
					curSchd.setCalculatedRate(repayCalRate);
				}
			}
		}

		// Rate Modification for All Modified Schedules
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			FinanceScheduleDetail prvSchd = null;
			if (i != 0) {
				prvSchd = scheduleData.getFinanceScheduleDetails().get(i - 1);
			}
			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) >= 0) {
				if (curSchd.getSchDate().compareTo(rateAppliedFromDate) >= 0) {
					if (StringUtils.isEmpty(finServiceInstruction.getBaseRate())) {
						curSchd.setCalculatedRate(finServiceInstruction.getActualRate() == null ? BigDecimal.ZERO
								: finServiceInstruction.getActualRate());
					} else {
						BigDecimal recalculateRate = RateUtil.rates(finServiceInstruction.getBaseRate(), fm.getFinCcy(),
								null, finServiceInstruction.getMargin(), curSchd.getSchDate(), fm.getRpyMinRate(),
								fm.getRpyMaxRate()).getNetRefRateLoan();

						curSchd.setCalculatedRate(recalculateRate);
					}
					curSchd.setBaseRate(StringUtils.trimToNull(finServiceInstruction.getBaseRate()));
					curSchd.setSplRate(StringUtils.trimToNull(finServiceInstruction.getSplRate()));
					curSchd.setMrgRate(
							StringUtils.trimToNull(finServiceInstruction.getBaseRate()) == null ? BigDecimal.ZERO
									: finServiceInstruction.getMargin());
					curSchd.setActRate(finServiceInstruction.getActualRate());
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
		fm.setEventToDate(recalToDate);
		fm.setRecalFromDate(startRepayCalDate);
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		fm.setPftIntact(finServiceInstruction.isPftIntact());

		// Setting Desired Values for the Profit Intact option
		if (finServiceInstruction.isPftIntact()) {
			fm.setDesiredProfit(fm.getTotalGrossPft());
			fm.setAdjTerms(terms);
			fm.setSchPftDue(schPftDue);
			fm.setSchPriDue(schPriDue);
		}

		// TODO: PV 19JAN17 schdMethod to be added
		fm.setRecalSchdMethod(fm.getScheduleMethod());
		scheduleData = ScheduleCalculator.reCalSchd(scheduleData, fm.getScheduleMethod());

		// Plan EMI Holidays Resetting after Rescheduling
		if (scheduleData.getFinanceMain().isPlanEMIHAlw()) {
			scheduleData.getFinanceMain().setEventFromDate(startRepayCalDate);
			scheduleData.getFinanceMain().setEventToDate(scheduleData.getFinanceMain().getMaturityDate());
			scheduleData.getFinanceMain().setRecalFromDate(startRepayCalDate);
			scheduleData.getFinanceMain().setRecalToDate(scheduleData.getFinanceMain().getMaturityDate());
			scheduleData.getFinanceMain().setRecalSchdMethod(scheduleData.getFinanceMain().getScheduleMethod());
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

		logger.debug(Literal.LEAVING);
		return scheduleData;
	}

	private FinScheduleData addSchdRcd(FinScheduleData schdData, Date newSchdDate, int prvIndex) {
		logger.debug(Literal.ENTERING);

		FinanceScheduleDetail prvSchd = schdData.getFinanceScheduleDetails().get(prvIndex);

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinReference(schdData.getFinanceMain().getFinReference());
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
		schdData.getFinanceMain().setNumberOfTerms(schdData.getFinanceMain().getNumberOfTerms() + 1);

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	@Override
	public FinScheduleData doResetOverdraftSchd(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		// Finance Service Instruction Preparation
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction serviceInstruction = new FinServiceInstruction();
		serviceInstruction.setFromDate(fm.getEventFromDate());
		serviceInstruction.setRepayFrq(fm.getRepayFrq());
		serviceInstruction.setRepayRvwFrq(fm.getRepayRvwFrq());

		Date startCalFrom = fm.getFinStartDate();
		FinanceScheduleDetail prvSchd = null;
		for (int i = 0; i < schdData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);
			if (DateUtil.compare(curSchd.getSchDate(), fm.getEventFromDate()) >= 0) {
				Date brforeDate = null;
				if (prvSchd == null) {
					brforeDate = fm.getFinStartDate();
					startCalFrom = FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, fm.getFinStartDate(), "A", false)
							.getNextFrequencyDate();
				} else {
					brforeDate = prvSchd.getSchDate();
					startCalFrom = FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, prvSchd.getSchDate(), "A", false)
							.getNextFrequencyDate();
				}
				if (DateUtil.getDaysBetween(brforeDate, startCalFrom) <= 15) {
					startCalFrom = FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, startCalFrom, "A", false)
							.getNextFrequencyDate();
				}
				if (DateUtil.compare(startCalFrom, fm.getMaturityDate()) >= 0) {
					startCalFrom = fm.getMaturityDate();
				}
				break;
			}
			prvSchd = curSchd;
		}

		int terms = FrequencyUtil.getTerms(fm.getRepayFrq(), startCalFrom, fm.getMaturityDate(), true, true).getTerms();
		serviceInstruction.setTerms(terms);
		serviceInstruction.setNextRepayDate(startCalFrom);
		serviceInstruction.setPftIntact(false);
		serviceInstruction.setBaseRate(fm.getRepayBaseRate());
		serviceInstruction.setSplRate(fm.getRepaySpecialRate());
		serviceInstruction.setMargin(fm.getRepayMargin());
		serviceInstruction.setActualRate(fm.getRepayProfitRate());

		// Schedule Recalculation
		schdData = recalScheduleData(schdData, serviceInstruction, fm.getMaturityDate());

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> schedules) {

		if (schedules != null && schedules.size() > 0) {
			Collections.sort(schedules, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return schedules;
	}

	@Override
	public AuditDetail doValidations(FinServiceInstruction finServInst) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = finServInst.isWif();
		long finID = finServInst.getFinID();

		Date fromDate = finServInst.getFromDate();
		if (fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// It shouldn't be past date when compare to appdate
		if (DateUtil.compare(finServInst.getFromDate(), SysParamUtil.getAppDate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtil.formatToLongDate(SysParamUtil.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
			return auditDetail;
		}

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);
		if (schedules != null) {
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
				valueParm[0] = "FromDate:" + DateUtil.formatToShortDate(finServInst.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				return auditDetail;
			}
		}
		if (finServInst.getNextRepayDate() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "NextRepayDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// validate Next payment date with finStart date and maturity date
		if (finServInst.getNextRepayDate().compareTo(fromDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Next RepayDate";
			valueParm[1] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			return auditDetail;
		}

		// FromDate should be Unpaid Date
		FinanceScheduleDetail finScheduleDetail = financeScheduleDetailDAO.getFinanceScheduleDetailById(finID, fromDate,
				"", isWIF);
		BigDecimal paidAmount = finScheduleDetail.getSchdPriPaid()
				.add(finScheduleDetail.getSchdFeePaid().add(finScheduleDetail.getSchdPftPaid()));

		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91116", "", valueParm), lang));
		}

		// validate repay frequency code
		ErrorDetail errorDetail = FrequencyUtil.validateFrequency(finServInst.getRepayFrq());
		if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = finServInst.getRepayFrq();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90207", "", valueParm), lang));
		}

		// terms
		if (finServInst.getTerms() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Terms";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

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

	@Override
	public List<RepayInstruction> getRepayInstructions(long finID, String type, boolean isWIF) {
		return repayInstructionDAO.getRepayInstructions(finID, type, isWIF);
	}

	@Override
	public AuditDetail deleteRestructureDetail(FinanceDetail financeDetail, String type, String transType) {
		logger.debug(Literal.ENTERING);
		RestructureDetail restructureDetail = financeDetail.getFinScheduleData().getRestructureDetail();
		restructureDAO.delete(restructureDetail.getId(), type);
		String[] fields = PennantJavaUtil.getFieldDetails(restructureDetail, restructureDetail.getExcludeFields());
		AuditDetail auditDetail = new AuditDetail(transType, 1, fields[0], fields[1], restructureDetail.getBefImage(),
				restructureDetail);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<AuditDetail> doApproveRestructureDetail(FinanceDetail fd, String type, String transType) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		RestructureDetail rd = schdData.getRestructureDetail();
		FinanceProfitDetail fpd = schdData.getFinPftDeatil();

		long finID = fm.getFinID();

		if (fpd == null) {
			fpd = financeProfitDetailDAO.getFinProfitDetailsById(finID);
		}

		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();

		// String recordType = restructureDetail.getRecordType();

		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			transType = PennantConstants.TRAN_DEL;
			restructureDAO.delete(rd.getId(), TableType.MAIN_TAB.getSuffix());
		} else {
			rd.setRoleCode("");
			rd.setNextRoleCode("");
			rd.setTaskId("");
			rd.setNextTaskId("");
			rd.setWorkflowId(0);
			rd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			rd.setLastMntBy(fm.getLastMntBy());
			rd.setLastMntOn(fm.getLastMntOn());
			rd.setVersion(fm.getVersion());
			rd.setNewTenure(fpd.getNOInst());
			rd.setNewBalTenure(fpd.getFutureInst());
			rd.setNewMaturity(fm.getMaturityDate());
			rd.setNewInterest(fpd.getTotalPftSchd());
			rd.setNewCpzInterest(fpd.getTotalPftCpz());

			FinanceScheduleDetail lastSchd = fsdList.get(fsdList.size() - 2);
			rd.setNewFinalEmi(lastSchd.getRepayAmount());
			rd.setRepayProfitRate(lastSchd.getCalculatedRate());

			// TODO:GANESH:Restructute Need to check with satish.k
			String finStatus = StringUtils.trimToEmpty(fm.getFinStatus());
			if (StringUtils.equals(fm.getFinStatus(), "S")) {
				finStatus = "0";
			} else if (finStatus.startsWith("DPD ")) {
				finStatus = finStatus.replace("DPD ", "");
			} else if (finStatus.startsWith("M")) {
				finStatus = finStatus.replace("M", "");
			}
			rd.setNewBucket(Integer.parseInt(finStatus));

			rd.setNewDpd(fpd.getCurODDays());
			rd.setNewEmiOs((fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()))
					.subtract(fpd.getTdSchdPri().add(fpd.getTdSchdPft())));
			rd.setNewMaxUnplannedEmi(fm.getMaxUnplannedEmi());
			rd.setNewAvailedUnplanEmi(fm.getAvailedUnPlanEmi());
			rd.setNewPOsAmount(fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
			rd.setNewEmiOverdue(fpd.getTdSchdPri().subtract(fpd.getTotalPriPaid()));
			BigDecimal otherCharge = getReceivableAmt(finID, false);
			BigDecimal bounceCharge = getReceivableAmt(finID, true);
			rd.setBounceCharge(bounceCharge);
			rd.setOtherCharge(otherCharge);
			BigDecimal penaltyAmount = getTotalPenaltyBal(finID, null);
			rd.setNewPenaltyAmount(penaltyAmount);
			rd.setRestructureCharge(BigDecimal.ZERO);
			rd.setNewExtOdDays(fpd.getExtODDays());
			rd.setNewEmiOverdue(fpd.getODProfit().add(fpd.getODPrincipal()));

			if (StringUtils.isEmpty(type)) {
				rd.setRecordType("");
				restructureDAO.save(rd, TableType.MAIN_TAB.getSuffix());
			} else {
				rd.setRecordType("");
				restructureDAO.update(rd, TableType.MAIN_TAB.getSuffix());
			}
		}
		financeMainDAO.updateRestructure(finID, true);
		fm.setRestructure(true);

		restructureDAO.delete(rd.getId(), TableType.TEMP_TAB.getSuffix());

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new RestructureDetail(),
				new RestructureDetail().getExcludeFields());
		int i = 0;
		auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1], rd.getBefImage(), rd));
		auditDetails.add(new AuditDetail(transType, ++i, fields[0], fields[1], rd.getBefImage(), rd));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public AuditDetail saveOrUpdateRestructureDetail(FinanceDetail financeDetail, String type, String transType) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		RestructureDetail restructureDetail = financeDetail.getFinScheduleData().getRestructureDetail();

		restructureDetail.setRecordType(finMain.getRecordType());
		restructureDetail.setWorkflowId(finMain.getWorkflowId());
		restructureDetail.setVersion(finMain.getVersion());
		restructureDetail.setRoleCode(finMain.getRoleCode());
		restructureDetail.setNextRoleCode(finMain.getNextRoleCode());
		restructureDetail.setTaskId(finMain.getTaskId());
		restructureDetail.setNextTaskId(finMain.getNextTaskId());
		restructureDetail.setRecordStatus(finMain.getRecordStatus());
		restructureDetail.setLastMntBy(finMain.getLastMntBy());
		restructureDetail.setLastMntOn(finMain.getLastMntOn());

		if (restructureDetail.isNewRecord()) {
			restructureDAO.save(restructureDetail, type);
		} else {
			restructureDAO.update(restructureDetail, type);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(restructureDetail, restructureDetail.getExcludeFields());
		AuditDetail auditDetail = new AuditDetail(transType, 1, fields[0], fields[1], restructureDetail.getBefImage(),
				restructureDetail);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public AuditDetail validationRestructureDetail(FinanceDetail financeDetail, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		RestructureDetail restructureDetail = financeDetail.getFinScheduleData().getRestructureDetail();
		String auditTranType;

		if (finMain.isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
		}
		AuditDetail auditDetail = new AuditDetail();
		// It shouldn't be past date when compare to appdate
		if (!(PennantConstants.RCD_STATUS_SAVED.contains(financeDetail.getUserAction())
				|| PennantConstants.RCD_STATUS_CANCELLED.contains(financeDetail.getUserAction())
				|| PennantConstants.RCD_STATUS_REJECTED.contains(financeDetail.getUserAction())
				|| PennantConstants.RCD_STATUS_DECLINED.contains(financeDetail.getUserAction())
				|| "Resubmit".equalsIgnoreCase(financeDetail.getUserAction()))) {
			if (DateUtil.compare(restructureDetail.getAppDate(), SysParamUtil.getAppDate()) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Restructure App Date: " + DateUtil.formatToLongDate(restructureDetail.getAppDate());
				valueParm[1] = "Application Date: " + DateUtil.formatToLongDate(SysParamUtil.getAppDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm)));
				return auditDetail;
			}
		}
		String[] fields = PennantJavaUtil.getFieldDetails(financeDetail.getFinScheduleData().getRestructureDetail(),
				financeDetail.getFinScheduleData().getRestructureDetail().getExcludeFields());
		auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1],
				financeDetail.getFinScheduleData().getRestructureDetail().getBefImage(),
				financeDetail.getFinScheduleData().getRestructureDetail());

		return auditDetail;
	}

	@Override
	public void processRestructureAccounting(AEEvent aeEvent, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		if (dataMap == null) {
			dataMap = new HashMap<>();
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> currSchedules = schdData.getFinanceScheduleDetails();
		List<FinanceScheduleDetail> prvsSchedules = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), "",
				false);
		RestructureDetail rsd = schdData.getRestructureDetail();

		BigDecimal prvInst = BigDecimal.ZERO;
		BigDecimal prvPrip = BigDecimal.ZERO;
		BigDecimal prvCpz = BigDecimal.ZERO;
		BigDecimal netCpz = BigDecimal.ZERO;
		BigDecimal curInst = BigDecimal.ZERO;
		BigDecimal curPrip = BigDecimal.ZERO;
		BigDecimal curCpz = BigDecimal.ZERO;

		Date resStartDate = rsd.getRestructureDate();
		Date resEndDate = null;
		if (rsd.getPriHldEndDate() != null) {
			resEndDate = rsd.getPriHldEndDate();
		} else {
			resEndDate = rsd.getEmiHldEndDate();
		}

		Date appDate = SysParamUtil.getAppDate();
		if (DateUtil.compare(resEndDate, appDate) > 0) {
			resEndDate = appDate;
		}

		// Previous
		for (FinanceScheduleDetail prvsSchd : prvsSchedules) {
			if (DateUtil.compare(prvsSchd.getSchDate(), resEndDate) > 0) {
				break;
			}
			if (DateUtil.compare(prvsSchd.getSchDate(), resStartDate) >= 0) {
				prvInst = prvInst.add(prvsSchd.getProfitSchd());
				prvPrip = prvPrip.add(prvsSchd.getPrincipalSchd());
				prvCpz = prvCpz.add(prvsSchd.getCpzAmount());
			}
		}

		// Current
		for (FinanceScheduleDetail cursSchd : currSchedules) {
			if (DateUtil.compare(cursSchd.getSchDate(), resEndDate) > 0) {
				break;
			}
			if (DateUtil.compare(cursSchd.getSchDate(), resStartDate) >= 0) {
				curInst = curInst.add(cursSchd.getProfitSchd());
				curPrip = curPrip.add(cursSchd.getPrincipalSchd());
				curCpz = curCpz.add(cursSchd.getCpzAmount());
			}
		}

		// accrue
		BigDecimal totProfitCalc = BigDecimal.ZERO;
		for (FinanceScheduleDetail prvsSchd : prvsSchedules) {
			if (DateUtil.compare(prvsSchd.getSchDate(), appDate) > 0) {
				break;
			}
			totProfitCalc = totProfitCalc.add(prvsSchd.getProfitCalc());
		}

		dataMap.put("ae_PrvInst", prvInst);
		dataMap.put("ae_PrvPrip", prvPrip);
		dataMap.put("ae_CurInst", curInst);
		dataMap.put("ae_CurPrip", curPrip);

		netCpz = curCpz.subtract(prvCpz);
		dataMap.put("ae_NetCpz", netCpz);
		// TODO:need to remove
		dataMap.put("ae_PrvCpz", prvCpz);
		dataMap.put("ae_CurCpz", prvCpz);

		FinanceProfitDetail finPftDetails = financeProfitDetailDAO.getFinProfitDetailsById(fm.getFinID());
		BigDecimal mnthIncome = BigDecimal.ZERO;
		if (finPftDetails != null && finPftDetails.getAmzTillLBD() != null) {
			mnthIncome = finPftDetails.getAmzTillLBD().subtract(totProfitCalc);
		}
		if (mnthIncome.compareTo(BigDecimal.ZERO) > 0) {
			dataMap.put("ae_mthIncome", mnthIncome);
		} else {
			dataMap.put("ae_mthIncome", BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void computeLPPandUpdateOD(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();

		List<FinODDetails> fodList = finODDetailsDAO.getFinODDByFinRef(finID, null);
		if (CollectionUtils.isEmpty(fodList)) {
			logger.debug(Literal.LEAVING);
			return;
		}
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
		RestructureDetail rsd = schdData.getRestructureDetail();
		List<FinODDetails> updatedODList = new ArrayList<>();

		for (int iFod = 0; iFod < fodList.size(); iFod++) {
			FinODDetails fod = fodList.get(iFod);

			if (fod.getFinODSchdDate().compareTo(rsd.getRestructureDate()) < 0) {
				fodList.remove(iFod);
				iFod = iFod - 1;
				continue;
			}

			for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);

				if (fsd.getSchDate().compareTo(SysParamUtil.getAppDate()) > 0) {
					break;
				}

				if (fsd.getSchDate().compareTo(fod.getFinODSchdDate()) == 0) {
					if (fsd.getProfitSchd().compareTo(BigDecimal.ZERO) == 0
							&& fsd.getPrincipalSchd().compareTo(BigDecimal.ZERO) == 0) {
						// fod.setFinODFor("D");
						// TODO:Ganesh Need to check with satish in case of EMI holiday
						// continue;
					}
					List<FinanceRepayments> rpdList = financeRepaymentsDAO.getByFinRefAndSchdDate(fod.getFinID(),
							fod.getFinODSchdDate());
					// TODO:Ganesh Need to check with satish
					latePayMarkingService.resetMaxODAmount(rpdList, fod, fsd);
					latePayMarkingService.latePayMarking(fm, fod, fsdList, rpdList, fsd, SysParamUtil.getAppDate(),
							false);
					updatedODList.add(fod);
					break;
				}
			}
		}
		if (CollectionUtils.isNotEmpty(updatedODList)) {
			finODDetailsDAO.updateTotals(updatedODList);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public BigDecimal getReceivableAmt(long finID, boolean isBounce) {
		return manualAdviseDAO.getReceivableAmt(finID, isBounce);
	}

	@Override
	public BigDecimal getTotalPenaltyBal(long finID, List<Date> presentmentDates) {
		return finODDetailsDAO.getTotalPenaltyBal(finID, presentmentDates);
	}

	@Override
	public RestructureDetail getRestructureDetailByRef(long finID, String type) {
		return restructureDAO.getRestructureDetailByFinReference(finID, type);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(long finID) {
		return financeProfitDetailDAO.getFinProfitDetailsById(finID);
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setRestructureDAO(RestructureDAO restructureDAO) {
		this.restructureDAO = restructureDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}
}
