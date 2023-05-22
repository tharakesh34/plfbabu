package com.pennanttech.test.schedule;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.util.Dataset;
import com.pennanttech.util.PrintFactory;

import jxl.Cell;

public class SrvReducingRateTest {
	FinScheduleData schedule;
	Cell[] data;
	long t1;

	public SrvReducingRateTest(FinScheduleData schedule, Cell[] data, long t1) {
		super();

		this.schedule = schedule;
		this.data = data;
		this.t1 = 0;
	}

	@Test
	public void testSchedule()
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		long t2 = DateUtil.getSysDate().getTime();

		String name = Dataset.getString(data, 0);
		PrintFactory.toConsole(name);

		// Get the expected results
		long expIntInGrace = Dataset.getLong(data, 26);
		long expCpzIntInGrace = Dataset.getLong(data, 27);
		long expTotalIntPaid = Dataset.getLong(data, 28);
		long expTotalCpz = Dataset.getLong(data, 29);
		long expLastInst = Dataset.getLong(data, 33);
		long expPrvCloseBal = Dataset.getLong(data, 31);
		int expRecords = Dataset.getInt(data, 34);
		long expFirstFee = Dataset.getInt(data, 35);
		long expFeeCharge = Dataset.getInt(data, 36);

		long actIntInGrace = 0;
		long actCpzIntInGrace = 0;
		long actTotalIntPaid = 0;
		long actTotalCpz = 0;
		long actLastInst = 0;
		long actPrvCloseBal = 0;
		int actRecords = 0;
		long actFirstFee = 0;
		long actFeeCharge = 0;

		// Calculate the schedule
		schedule = execute(schedule);
		FinanceMain fm = schedule.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = schedule.getFinanceScheduleDetails();

		actIntInGrace = fm.getTotalGrossGrcPft().longValue();
		actCpzIntInGrace = fm.getTotalGraceCpz().longValue();
		actTotalIntPaid = fm.getTotalProfit().longValue();
		actTotalCpz = fm.getTotalCpz().longValue();

		int size = schdDetails.size();
		actLastInst = schdDetails.get(size - 1).getRepayAmount().longValue();
		actPrvCloseBal = schdDetails.get(size - 2).getClosingBalance().longValue();
		actRecords = size;

		// Get the actual results

		PrintFactory.toConsole(expIntInGrace, actIntInGrace);
		PrintFactory.toConsole(expCpzIntInGrace, actCpzIntInGrace);
		PrintFactory.toConsole(expTotalIntPaid, actTotalIntPaid);
		PrintFactory.toConsole(expTotalCpz, actTotalCpz);
		PrintFactory.toConsole(expPrvCloseBal, actPrvCloseBal);
		PrintFactory.toConsole(expLastInst, actLastInst);
		PrintFactory.toConsole(expRecords, actRecords);

		PrintFactory.scheduleToExcel(name, schedule);

		Assert.assertEquals(actIntInGrace, expIntInGrace, (name + " Grace Interst: "));
		Assert.assertEquals(actCpzIntInGrace, expCpzIntInGrace, (name + " Grace Cpz Interst: "));
		Assert.assertEquals(actTotalIntPaid, expTotalIntPaid, (name + " Total Interst Schd: "));
		Assert.assertEquals(actTotalCpz, expTotalCpz, (name + " Grace Interst Cpz: "));
		Assert.assertEquals(actPrvCloseBal, expPrvCloseBal, (name + " Prv Closing Bal: "));
		Assert.assertEquals(actLastInst, expLastInst, (name + " Last Installment: "));
		Assert.assertEquals(actRecords, expRecords, (name + " Total Record:"));
		Assert.assertEquals(expFirstFee, actFirstFee, (name + " First Fee Amount:"));
		Assert.assertEquals(expFeeCharge, actFeeCharge, (name + " Total Fee Amount:"));

		long t3 = DateUtil.getSysDate().getTime();
		System.out.println("Time in long " + String.valueOf(t3 - t2));
		t1 = t1 + t3 - t2;
		System.out.println("total Time in long " + String.valueOf(t1));
	}

	public FinScheduleData execute(FinScheduleData model)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		FinScheduleData schedule = (FinScheduleData) BeanUtils.cloneBean(model);

		// _______________________________________________________________________________________________
		// Setting to be moved to actual test class
		// _______________________________________________________________________________________________
		schedule.getFinanceMain().setRepayRateBasis(CalculationConstants.RATE_BASIS_R);
		schedule = ScheduleGenerator.getNewSchd(schedule);
		schedule = ScheduleCalculator.getCalSchd(schedule, BigDecimal.ZERO);

		// Execute Services
		schedule = executeServiceCases(schedule);

		return schedule;
	}

	public FinScheduleData executeServiceCases(FinScheduleData schedule) {
		FinanceMain fm = schedule.getFinanceMain();
		String schdMethod = fm.getScheduleMethod();

		// First Service Case
		String srvTestCase = Dataset.getString(data, 3);
		String recalType = Dataset.getString(data, 4);
		if (srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		// Second Service Case
		srvTestCase = Dataset.getString(data, 5);
		recalType = Dataset.getString(data, 6);

		if (srvTestCase == null || srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		// Third Service Case
		srvTestCase = Dataset.getString(data, 7);
		recalType = Dataset.getString(data, 8);

		if (srvTestCase == null || srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		// Fourth Service Case
		srvTestCase = Dataset.getString(data, 9);
		recalType = Dataset.getString(data, 10);

		if (srvTestCase == null || srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		// Fifth Service Case
		srvTestCase = Dataset.getString(data, 11);
		recalType = Dataset.getString(data, 12);

		if (srvTestCase == null || srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		// Sixth Service Case
		srvTestCase = Dataset.getString(data, 13);
		recalType = Dataset.getString(data, 14);

		if (srvTestCase == null || srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		// Seventh Service Case
		srvTestCase = Dataset.getString(data, 15);
		recalType = Dataset.getString(data, 16);

		if (srvTestCase == null || srvTestCase.equals("")) {
			return schedule;
		} else {
			if (srvTestCase.equals("ADDDBSP")) {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				}
			}

			schedule = executeService(schedule, srvTestCase, recalType, schdMethod);
		}

		return schedule;
	}

	public static FinScheduleData executeService(FinScheduleData schedule, String srvTestCase, String recalType,
			String schdMethod) {
		FinanceMain fm = schedule.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = schedule.getFinanceScheduleDetails();

		if (srvTestCase.equals("ADDDBSP")) {
			BigDecimal amount = new BigDecimal(10000000);
			BigDecimal fee = new BigDecimal(0);

			int index = schedule.getDisbursementDetails().size() - 1;
			Date prvDisbDate = schedule.getDisbursementDetails().get(index).getDisbDate();

			fm.setRecalType(recalType);
			fm.setRecalSchdMethod(schdMethod);
			fm.setEventFromDate(DateUtil.addMonths(prvDisbDate, 1));

			int sdSize = fsdList.size();
			int iAfter = 0;

			for (int i = 0; i < sdSize; i++) {
				Date schdDate = fsdList.get(i).getSchDate();
				if (schdDate.compareTo(fm.getEventFromDate()) > 0) {
					iAfter = iAfter + 1;
				}

				if (iAfter == 2) {
					fm.setRecalFromDate(schdDate);

					if (recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
						fm.setRecalFromDate(fsdList.get(1).getSchDate());
						fm.setAdjTerms(3);
					} else if (recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
						fm.setAdjTerms(3);
					} else if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)) {
						// Do nothing
					} else if (recalType.equals(CalculationConstants.RPYCHG_TILLDATE)) {
						fm.setRecalFromDate(fsdList.get(i + 2).getSchDate());
						fm.setRecalToDate(fsdList.get(i + 5).getSchDate());
					} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
						fm.setRecalFromDate(fsdList.get(i + 7).getSchDate());
					}

					break;
				}
			}

			schedule = ScheduleCalculator.addDisbursement(schedule, amount, fee, false);
		}

		if (srvTestCase.equals("RATCHG")) {
			int iRecord = 0;
			BigDecimal MARGIN_RATE1 = new BigDecimal(1.5);
			BigDecimal MARGIN_RATE2 = new BigDecimal(2.5);
			BigDecimal MARGIN_RATE3 = new BigDecimal(3.5);

			Date evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
			Date evtToDate = evtFromDate;
			Date recalFromDate = evtFromDate;

			fm.setRecalType(recalType);

			if (recalType.equals(CalculationConstants.RPYCHG_CURPRD)) {
				iRecord = 3;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtToDate = schedule.getFinanceScheduleDetails().get(iRecord + 3).getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE1);
			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				iRecord = 9;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtToDate = schedule.getFinanceScheduleDetails().get(iRecord + 3).getSchDate();

				recalFromDate = schedule.getFinanceScheduleDetails().get(iRecord + 4).getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRecalFromDate(recalFromDate);
				fm.setRepayMargin(MARGIN_RATE2);
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)) {
				iRecord = 15;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtToDate = schedule.getFinanceScheduleDetails().get(iRecord + 3).getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE3);
				// CURPRDD is for period goes till END
			} else if (recalType.equals("CURPRDD")) {
				iRecord = 3;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtToDate = schedule.getFinanceScheduleDetails().get(schedule.getFinanceScheduleDetails().size() - 1)
						.getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE1);
				fm.setRecalType(CalculationConstants.RPYCHG_CURPRD);
			}

			schedule = ScheduleCalculator.changeRate(schedule, fm.getRepayBaseRate(), fm.getRepaySpecialRate(),
					fm.getRepayMargin(), BigDecimal.ZERO, true);
		}

		if (srvTestCase.equals("RATCHGD")) {
			int iRecord = 0;
			BigDecimal MARGIN_RATE1 = new BigDecimal(1.5);
			BigDecimal MARGIN_RATE2 = new BigDecimal(2.5);
			BigDecimal MARGIN_RATE3 = new BigDecimal(3.5);

			Date evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
			Date evtToDate = evtFromDate;
			Date recalFromDate = evtFromDate;

			fm.setRecalType(recalType);

			if (recalType.equals(CalculationConstants.RPYCHG_CURPRD)) {
				iRecord = 3;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtFromDate = DateUtil.addDays(evtFromDate, -10);
				evtToDate = schedule.getFinanceScheduleDetails().get(iRecord + 3).getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE1);
			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				iRecord = 9;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtToDate = schedule.getFinanceScheduleDetails().get(iRecord + 3).getSchDate();
				evtToDate = DateUtil.addDays(evtToDate, 10);

				recalFromDate = schedule.getFinanceScheduleDetails().get(iRecord + 4).getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRecalFromDate(recalFromDate);
				fm.setRepayMargin(MARGIN_RATE2);
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)) {
				iRecord = 15;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtFromDate = DateUtil.addDays(evtFromDate, -10);
				evtToDate = schedule.getFinanceScheduleDetails().get(iRecord + 3).getSchDate();
				evtToDate = DateUtil.addDays(evtToDate, 10);

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE3);
			}

			schedule = ScheduleCalculator.changeRate(schedule, fm.getRepayBaseRate(), fm.getRepaySpecialRate(),
					fm.getRepayMargin(), BigDecimal.ZERO, true);
		}

		// RATE CHANGE HAPPENS IN BETWEEN THE SAME INSTALLMENT
		if (srvTestCase.equals("RATCHGI")) {
			int iRecord = 0;
			BigDecimal MARGIN_RATE1 = new BigDecimal(1.5);
			BigDecimal MARGIN_RATE2 = new BigDecimal(2.5);
			BigDecimal MARGIN_RATE3 = new BigDecimal(3.5);

			Date evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
			Date evtToDate = evtFromDate;
			Date recalFromDate = evtFromDate;

			fm.setRecalType(recalType);

			if (recalType.equals(CalculationConstants.RPYCHG_CURPRD)) {
				iRecord = 3;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtFromDate = DateUtil.addDays(evtFromDate, -15);
				evtToDate = DateUtil.addDays(evtFromDate, 10);
				;

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE1);
			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				iRecord = 9;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtFromDate = DateUtil.addDays(evtFromDate, -15);
				evtToDate = DateUtil.addDays(evtFromDate, 10);
				;

				recalFromDate = schedule.getFinanceScheduleDetails().get(iRecord + 2).getSchDate();

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRecalFromDate(recalFromDate);
				fm.setRepayMargin(MARGIN_RATE2);
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)) {
				iRecord = 15;
				evtFromDate = schedule.getFinanceScheduleDetails().get(iRecord).getSchDate();
				evtFromDate = DateUtil.addDays(evtFromDate, -15);
				evtToDate = DateUtil.addDays(evtFromDate, 10);
				;

				fm.setEventFromDate(evtFromDate);
				fm.setEventToDate(evtToDate);
				fm.setRepayMargin(MARGIN_RATE3);
			}

			schedule = ScheduleCalculator.changeRate(schedule, fm.getRepayBaseRate(), fm.getRepaySpecialRate(),
					fm.getRepayMargin(), BigDecimal.ZERO, true);
		}

		if (srvTestCase.equals("CHGPAY")) {
			BigDecimal newPayment = BigDecimal.ZERO;

			fm.setRecalType(recalType);

			if (recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
				newPayment = BigDecimal.ZERO;
				schdMethod = CalculationConstants.SCHMTHD_NOPAY;
				fm.setEventFromDate(fsdList.get(2).getSchDate());
				fm.setEventToDate(fsdList.get(4).getSchDate());

				fm.setRecalFromDate(fsdList.get(7).getSchDate());
				fm.setAdjTerms(2);
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)) {

				newPayment = new BigDecimal(10000000);
				schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				fm.setEventFromDate(fsdList.get(3).getSchDate());
				fm.setEventToDate(fsdList.get(3).getSchDate());

			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				newPayment = new BigDecimal(7500000);

				fm.setEventFromDate(fsdList.get(5).getSchDate());
				fm.setEventToDate(fsdList.get(7).getSchDate());
				fm.setRecalFromDate(fsdList.get(16).getSchDate());
			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				newPayment = new BigDecimal(5000000);

				fm.setEventFromDate(fsdList.get(6).getSchDate());
				fm.setEventToDate(fsdList.get(7).getSchDate());
				fm.setRecalFromDate(fsdList.get(10).getSchDate());
				fm.setRecalToDate(fsdList.get(18).getSchDate());
			}

			schedule = ScheduleCalculator.changeRepay(schedule, newPayment, schdMethod);
		}

		if (srvTestCase.equals("ADDTERM")) {
			schedule = ScheduleCalculator.addTerm(schedule, 3);
		}

		if (srvTestCase.equals("RECALSCHD")) {
			fm.setRecalType(recalType);

			if (recalType.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				fm.setRecalFromDate(fsdList.get(10).getSchDate());
				fm.setRecalToDate(fsdList.get(15).getSchDate());
			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				fm.setRecalFromDate(fsdList.get(10).getSchDate());
			} else if (recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
				schdMethod = CalculationConstants.SCHMTHD_NOPAY;
				fm.setRecalFromDate(fsdList.get(10).getSchDate());
				fm.setAdjTerms(2);
			} else if (recalType.equals("TILLDATEL")) {
				schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				int i = fsdList.size() - 1;
				fm.setRecalFromDate(fsdList.get(i - 1).getSchDate());
				fm.setRecalToDate(fsdList.get(i).getSchDate());
			} else if (recalType.equals("TILLMDTL")) {
				schdMethod = CalculationConstants.SCHMTHD_EQUAL;
				int i = fsdList.size() - 1;
				fm.setRecalFromDate(fsdList.get(i - 1).getSchDate());
			}

			schedule = ScheduleCalculator.reCalSchd(schedule, schdMethod);
		}

		if (srvTestCase.equals("DLTTERM")) {
			fm.setRecalType(recalType);
			fm.setException(true);

			if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)) {
				fm.setEventFromDate(fsdList.get(23).getSchDate());
			} else if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				fm.setEventFromDate(fsdList.get(22).getSchDate());
				fm.setRecalFromDate(fsdList.get(8).getSchDate());
			}

			schedule = ScheduleCalculator.deleteTerm(schedule);
			fm.setException(false);
		}

		if (srvTestCase.equals("GRCCHG_E")) {
			fm.setRecalType("");
			fm.setEventFromDate(fm.getGrcPeriodEndDate());
			fm.setGrcPeriodEndDate(DateUtil.addMonths(fm.getGrcPeriodEndDate(), 2));
			fm.setNextRepayDate(DateUtil.addMonths(fm.getGrcPeriodEndDate(), 1));
			fm.setNextRepayPftDate(fm.getNextRepayDate());
			fm.setNextRepayRvwDate(fm.getNextRepayDate());
			fm.setMaturityDate(DateUtil.addMonths(fm.getMaturityDate(), 2));
			schedule = ScheduleCalculator.changeGraceEnd(schedule);
		}

		if (srvTestCase.equals("GRCCHG_R")) {
			fm.setRecalType("");
			fm.setEventFromDate(fm.getGrcPeriodEndDate());
			fm.setGrcPeriodEndDate(DateUtil.addMonths(fm.getGrcPeriodEndDate(), -2));
			fm.setNextRepayDate(DateUtil.addMonths(fm.getGrcPeriodEndDate(), 1));
			fm.setNextRepayPftDate(fm.getNextRepayDate());
			fm.setNextRepayRvwDate(fm.getNextRepayDate());
			fm.setMaturityDate(DateUtil.addMonths(fm.getMaturityDate(), -2));
			schedule = ScheduleCalculator.changeGraceEnd(schedule);
		}

		if (srvTestCase.equals("GRCCHG_ED")) {
			fm.setRecalType("");
			fm.setEventFromDate(fm.getGrcPeriodEndDate());

			Date dateCal = DateUtil.addMonths(fm.getGrcPeriodEndDate(), 2);

			fm.setGrcPeriodEndDate(DateUtil.addDays(dateCal, -10));
			fm.setNextRepayDate(DateUtil.addMonths(dateCal, 1));
			fm.setNextRepayPftDate(fm.getNextRepayDate());
			fm.setNextRepayRvwDate(fm.getNextRepayDate());
			fm.setMaturityDate(DateUtil.addMonths(fm.getMaturityDate(), 2));
			schedule = ScheduleCalculator.changeGraceEnd(schedule);
		}

		if (srvTestCase.equals("GRCCHG_RD")) {
			fm.setRecalType("");
			fm.setEventFromDate(fm.getGrcPeriodEndDate());

			Date dateCal = DateUtil.addMonths(fm.getGrcPeriodEndDate(), -2);

			fm.setGrcPeriodEndDate(DateUtil.addDays(dateCal, -10));
			fm.setNextRepayDate(DateUtil.addMonths(dateCal, 1));
			fm.setNextRepayPftDate(fm.getNextRepayDate());
			fm.setNextRepayRvwDate(fm.getNextRepayDate());
			fm.setMaturityDate(DateUtil.addMonths(fm.getMaturityDate(), -2));
			schedule = ScheduleCalculator.changeGraceEnd(schedule);
		}

		if (srvTestCase.equals("REAGE")) {
			fm.setRecalType("");

			fm.setEventFromDate(schedule.getFinanceScheduleDetails().get(2).getSchDate());
			fm.setEventToDate(schedule.getFinanceScheduleDetails().get(4).getSchDate());

			schedule.getFinanceScheduleDetails().get(3).setSchdPftPaid(new BigDecimal(787629));
			schedule.getFinanceScheduleDetails().get(3).setSchdPriPaid(new BigDecimal(1528516));
			schedule.getFinanceScheduleDetails().get(4).setSchdPftPaid(new BigDecimal(730621));
			schedule.getFinanceScheduleDetails().get(4).setSchdPriPaid(new BigDecimal(2743596));

			schedule = ScheduleCalculator.reAging(schedule);
		}

		if (srvTestCase.equals("REAGE_C")) {
			fm.setReAgeCpz(true);
			fm.setRecalType("");
			fm.setEventFromDate(schedule.getFinanceScheduleDetails().get(2).getSchDate());
			fm.setEventToDate(schedule.getFinanceScheduleDetails().get(4).getSchDate());

			schedule.getFinanceScheduleDetails().get(3).setSchdPftPaid(new BigDecimal(787629));
			schedule.getFinanceScheduleDetails().get(3).setSchdPriPaid(new BigDecimal(1528516));
			schedule.getFinanceScheduleDetails().get(4).setSchdPftPaid(new BigDecimal(730621));
			schedule.getFinanceScheduleDetails().get(4).setSchdPriPaid(new BigDecimal(2743596));

			schedule = ScheduleCalculator.reAging(schedule);
		}

		return schedule;
	}
}
