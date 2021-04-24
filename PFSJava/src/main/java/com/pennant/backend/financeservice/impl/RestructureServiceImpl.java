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

	/**
	 * Method for perform re-schedule action based on given instructions.
	 * 
	 * @param scheduleData
	 * @param finServiceInstruction
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doRestructure(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction) {
		return recalScheduleData(finScheduleData, finServiceInstruction, null);
	}

	private FinScheduleData recalScheduleData(FinScheduleData finScheduleData,
			FinServiceInstruction finServiceInstruction, Date maturityDate) {
		logger.debug(Literal.ENTERING);

		// Check Date Status Specifier
		boolean calFromGrcPeriod = false;

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		FinScheduleData scheduleData = null;
		Cloner cloner = new Cloner();
		scheduleData = cloner.deepClone(finScheduleData);

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
		HashMap<Date, FinanceScheduleDetail> mapList = new HashMap<Date, FinanceScheduleDetail>();
		BigDecimal schPriDue = BigDecimal.ZERO;
		BigDecimal schPftDue = BigDecimal.ZERO;
		BigDecimal unModifiedPft = BigDecimal.ZERO;
		Date rateAppliedFromDate = null;
		for (int i = 0; i < scheduleList.size(); i++) {
			if (scheduleList.get(i).getSchDate().compareTo(fromDate) <= 0) {
				rateAppliedFromDate = scheduleList.get(i).getSchDate();
				mapList.put(scheduleList.get(i).getSchDate(), scheduleList.get(i));
				unModifiedPft = unModifiedPft.add(scheduleList.get(i).getProfitSchd());
			} else {

				schPftDue = schPftDue.add(scheduleList.get(i).getProfitSchd());
				schPriDue = schPriDue.add(scheduleList.get(i).getPrincipalSchd());
			}
		}
		scheduleData.setScheduleMap(mapList);
		mapList = null;

		Date startRepayCalDate = null;
		Date recalToDate = null;

		// Setting Event From Date Value
		if (calFromGrcPeriod) {
			financeMain.setGrcPeriodEndDate(finServiceInstruction.getGrcPeriodEndDate());
			if (finServiceInstruction.getNextGrcRepayDate() != null) {
				financeMain.setEventFromDate(finServiceInstruction.getNextGrcRepayDate());
			} else {

				Date eventFromDate = FrequencyUtil.getNextDate(finServiceInstruction.getRepayFrq(), 1,
						finServiceInstruction.getFromDate(), "A", false, 0).getNextFrequencyDate();
				financeMain.setEventFromDate(eventFromDate);
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
			lastDateLimit = DateUtil.addYears(financeMain.getFinStartDate(), maxFinYears);

			if (DateUtil.compare(recalToDate, lastDateLimit) > 0) {
				finScheduleData.setErrorDetail(new ErrorDetail("SCH30",
						"ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS. NOT ALLOWED TO ADD MORE TERMS.",
						new String[] { " " }));
				return finScheduleData;
			}

			financeMain.setEventFromDate(startRepayCalDate);
			financeMain.setMaturityDate(recalToDate);

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
		boolean suplRentUpdated = false;
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

			// Profit Days Basis Setting
			if (StringUtils.isEmpty(curSchd.getPftDaysBasis())) {
				curSchd.setPftDaysBasis(prvSchd.getPftDaysBasis());
			}

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				if (!financeMain.isAllowGrcRepay()) {
					curSchd.setSchdMethod(CalculationConstants.SCHMTHD_NOPAY);
				} else {
					curSchd.setSchdMethod(financeMain.getGrcSchdMthd());
				}
			} else {
				curSchd.setSchdMethod(financeMain.getScheduleMethod());
			}

			// Supplement Rent & Increased Cost Re-Setting
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
				if (chkFirstRpyDate && newFirstRpyDate == null) {
					newFirstRpyDate = curSchd.getSchDate();
				}
				chkFirstRpyDate = true;
			}

			if (fromDate.compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
				if (i != 0 && curSchd.getSchDate().compareTo(fromDate) > 0) {
					curSchd.setCalculatedRate(scheduleData.getFinanceScheduleDetails().get(i - 1).getCalculatedRate());
				}
			} else {
				if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
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
			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) >= 0) {
				if (curSchd.getSchDate().compareTo(rateAppliedFromDate) >= 0) {
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
					curSchd.setMrgRate(StringUtils.trimToNull(finServiceInstruction.getBaseRate()) == null
							? BigDecimal.ZERO : finServiceInstruction.getMargin());
					curSchd.setActRate(finServiceInstruction.getActualRate());

					// Advised Rates Setting
					if (i != 0 && StringUtils.equals(FinanceConstants.PRODUCT_STRUCTMUR,
							scheduleData.getFinanceType().getFinCategory())) {
						if (prvSchd != null) {
							curSchd.setAdvPftRate(StringUtils.trimToNull(prvSchd.getAdvBaseRate()) == null
									? prvSchd.getAdvPftRate() : BigDecimal.ZERO);
							curSchd.setAdvBaseRate(StringUtils.trimToNull(prvSchd.getAdvBaseRate()));
							curSchd.setAdvMargin(StringUtils.trimToNull(prvSchd.getAdvBaseRate()) == null
									? BigDecimal.ZERO : prvSchd.getAdvMargin());
						}
					}
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
		financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
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

	/**
	 * Method for Adding Schedule term when missing while on disbursement date checking
	 * 
	 * @param finScheduleData
	 * @param newSchdDate
	 * @param prvIndex
	 * @return
	 */
	private FinScheduleData addSchdRcd(FinScheduleData finScheduleData, Date newSchdDate, int prvIndex) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return finScheduleData;
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
		logger.debug(Literal.ENTERING);

		// Finance Service Instruction Preparation
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinServiceInstruction serviceInstruction = new FinServiceInstruction();
		serviceInstruction.setFromDate(finMain.getEventFromDate());
		serviceInstruction.setRepayFrq(finMain.getRepayFrq());
		serviceInstruction.setRepayRvwFrq(finMain.getRepayRvwFrq());

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
				if (DateUtil.getDaysBetween(brforeDate, startCalFrom) <= 15) {
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

		logger.debug(Literal.LEAVING);
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
	 * @param finServiceInstruction
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();

		Date fromDate = finServiceInstruction.getFromDate();
		if (fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// It shouldn't be past date when compare to appdate
		if (DateUtil.compare(finServiceInstruction.getFromDate(), SysParamUtil.getAppDate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtil.formatToLongDate(SysParamUtil.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
			return auditDetail;
		}

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
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
				valueParm[0] = "FromDate:" + DateUtil.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				return auditDetail;
			}
		}
		if (finServiceInstruction.getNextRepayDate() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "NextRepayDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// validate Next payment date with finStart date and maturity date
		if (finServiceInstruction.getNextRepayDate().compareTo(fromDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Next RepayDate";
			valueParm[1] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			return auditDetail;
		}

		// FromDate should be Unpaid Date
		FinanceScheduleDetail finScheduleDetail = financeScheduleDetailDAO.getFinanceScheduleDetailById(finReference,
				fromDate, "", isWIF);
		BigDecimal paidAmount = finScheduleDetail.getSchdPriPaid()
				.add(finScheduleDetail.getSchdFeePaid().add(finScheduleDetail.getSchdPftPaid()));

		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91116", "", valueParm), lang));
		}

		// validate repay frequency code
		ErrorDetail errorDetail = FrequencyUtil.validateFrequency(finServiceInstruction.getRepayFrq());
		if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = finServiceInstruction.getRepayFrq();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90207", "", valueParm), lang));
		}

		// terms
		if (finServiceInstruction.getTerms() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Terms";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
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

	@Override
	public List<RepayInstruction> getRepayInstructions(String id, String type, boolean isWIF) {
		return repayInstructionDAO.getRepayInstructions(id, type, isWIF);
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
	public List<AuditDetail> doApproveRestructureDetail(FinanceDetail financeDetail, String type, String transType) {
		logger.debug(Literal.ENTERING);

		FinScheduleData fsd = financeDetail.getFinScheduleData();
		FinanceMain financeMain = fsd.getFinanceMain();
		RestructureDetail restructureDetail = fsd.getRestructureDetail();
		FinanceProfitDetail fpd = fsd.getFinPftDeatil();
		if (fpd == null) {
			fpd = financeProfitDetailDAO.getFinProfitDetailsById(financeMain.getFinReference());
		}
		List<FinanceScheduleDetail> fsdList = fsd.getFinanceScheduleDetails();

		// String recordType = restructureDetail.getRecordType();

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			transType = PennantConstants.TRAN_DEL;
			restructureDAO.delete(restructureDetail.getId(), TableType.MAIN_TAB.getSuffix());
		} else {
			restructureDetail.setRoleCode("");
			restructureDetail.setNextRoleCode("");
			restructureDetail.setTaskId("");
			restructureDetail.setNextTaskId("");
			restructureDetail.setWorkflowId(0);
			restructureDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			restructureDetail.setLastMntBy(financeMain.getLastMntBy());
			restructureDetail.setLastMntOn(financeMain.getLastMntOn());
			restructureDetail.setVersion(financeMain.getVersion());
			restructureDetail.setNewTenure(fpd.getNOInst());
			restructureDetail.setNewBalTenure(fpd.getFutureInst());
			restructureDetail.setNewMaturity(financeMain.getMaturityDate());
			restructureDetail.setNewInterest(fpd.getTotalPftSchd());
			restructureDetail.setNewCpzInterest(fpd.getTotalPftCpz());

			FinanceScheduleDetail lastSchd = fsdList.get(fsdList.size() - 2);
			restructureDetail.setNewFinalEmi(lastSchd.getRepayAmount());
			restructureDetail.setRepayProfitRate(lastSchd.getCalculatedRate());

			// TODO:GANESH:Restructute Need to check with satish.k
			String finStatus = StringUtils.trimToEmpty(financeMain.getFinStatus());
			if (StringUtils.equals(financeMain.getFinStatus(), "S")) {
				finStatus = "0";
			} else if (finStatus.startsWith("DPD ")) {
				finStatus = finStatus.replace("DPD ", "");
			} else if (finStatus.startsWith("M")) {
				finStatus = finStatus.replace("M", "");
			}
			restructureDetail.setNewBucket(Integer.parseInt(finStatus));

			restructureDetail.setNewDpd(fpd.getCurODDays());
			restructureDetail.setNewEmiOs((fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()))
					.subtract(fpd.getTdSchdPri().add(fpd.getTdSchdPft())));
			restructureDetail.setNewMaxUnplannedEmi(financeMain.getMaxUnplannedEmi());
			restructureDetail.setNewAvailedUnplanEmi(financeMain.getAvailedUnPlanEmi());
			restructureDetail.setNewPOsAmount(fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
			restructureDetail.setNewEmiOverdue(fpd.getTdSchdPri().subtract(fpd.getTotalPriPaid()));
			BigDecimal otherCharge = getReceivableAmt(financeMain.getFinReference(), false);
			BigDecimal bounceCharge = getReceivableAmt(financeMain.getFinReference(), true);
			restructureDetail.setBounceCharge(bounceCharge);
			restructureDetail.setOtherCharge(otherCharge);
			BigDecimal penaltyAmount = getTotalPenaltyBal(financeMain.getFinReference(), null);
			restructureDetail.setNewPenaltyAmount(penaltyAmount);
			restructureDetail.setRestructureCharge(BigDecimal.ZERO);
			restructureDetail.setNewExtOdDays(fpd.getExtODDays());
			restructureDetail.setNewEmiOverdue(fpd.getODProfit().add(fpd.getODPrincipal()));

			if (StringUtils.isEmpty(type)) {
				restructureDetail.setRecordType("");
				restructureDAO.save(restructureDetail, TableType.MAIN_TAB.getSuffix());
			} else {
				restructureDetail.setRecordType("");
				restructureDAO.update(restructureDetail, TableType.MAIN_TAB.getSuffix());
			}
		}
		financeMainDAO.updateRestructure(financeMain.getFinReference(), true);
		financeMain.setRestructure(true);

		restructureDAO.delete(restructureDetail.getId(), TableType.TEMP_TAB.getSuffix());

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new RestructureDetail(),
				new RestructureDetail().getExcludeFields());
		int i = 0;
		auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1],
				restructureDetail.getBefImage(), restructureDetail));
		auditDetails.add(new AuditDetail(transType, ++i, fields[0], fields[1], restructureDetail.getBefImage(),
				restructureDetail));

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
	public void processRestructureAccounting(AEEvent aeEvent, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		if (dataMap == null) {
			dataMap = new HashMap<>();
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinanceScheduleDetail> currSchedules = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		List<FinanceScheduleDetail> prvsSchedules = financeScheduleDetailDAO
				.getFinScheduleDetails(financeMain.getFinReference(), "", false);
		RestructureDetail rsd = financeDetail.getFinScheduleData().getRestructureDetail();

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
		/*
		 * if (DateUtil.compare(resEndDate, SysParamUtil.getAppDate()) > 0) { resEndDate = SysParamUtil.getAppDate(); }
		 */

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
			if (DateUtil.compare(prvsSchd.getSchDate(), SysParamUtil.getAppDate()) > 0) {
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

		FinanceProfitDetail finPftDetails = financeProfitDetailDAO
				.getFinProfitDetailsById(financeMain.getFinReference());
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
	public void computeLPPandUpdateOD(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinODDetails> fodList = finODDetailsDAO.getFinODDByFinRef(financeMain.getFinReference(), null);
		if (CollectionUtils.isEmpty(fodList)) {
			logger.debug(Literal.LEAVING);
			return;
		}
		List<FinanceScheduleDetail> fsdList = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		RestructureDetail rsd = financeDetail.getFinScheduleData().getRestructureDetail();
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
					List<FinanceRepayments> rpdList = financeRepaymentsDAO.getByFinRefAndSchdDate(fod.getFinReference(),
							fod.getFinODSchdDate());
					// TODO:Ganesh Need to check with satish
					latePayMarkingService.resetMaxODAmount(rpdList, fod, fsd);
					latePayMarkingService.latePayMarking(financeMain, fod, fsdList, rpdList, fsd,
							SysParamUtil.getAppDate(), false);
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
	public BigDecimal getReceivableAmt(String finReference, boolean isBounce) {
		return manualAdviseDAO.getReceivableAmt(finReference, isBounce);
	}

	@Override
	public BigDecimal getTotalPenaltyBal(String finReference, List<Date> presentmentDates) {
		return finODDetailsDAO.getTotalPenaltyBal(finReference, presentmentDates);
	}

	@Override
	public RestructureDetail getRestructureDetailByRef(String finReference, String type) {
		return restructureDAO.getRestructureDetailByFinReference(finReference, type);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String fineference) {
		return financeProfitDetailDAO.getFinProfitDetailsById(fineference);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
