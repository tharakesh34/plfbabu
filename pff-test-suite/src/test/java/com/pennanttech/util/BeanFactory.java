package com.pennanttech.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

import jxl.Cell;

public class BeanFactory {
	// BASE RATE "UNITTEST" IN RMTBaseRates MUST BE AVAILABLE with 11% latest
	// SPECIAL RATE "UNITTEST" IN RMTSplRates MUST BE AVAILABLE with 1.5% latest
	// So Net Rate will be 11% - 1.5% + Margin = 10%
	// TDS_PERCENTAGE may be 20

	// INSERT INTO RMTBASERATECODES (BRTYPE, BRTYPEDESC, VERSION, LASTMNTBY, LASTMNTON, RECORDSTATUS, NEXTROLECODE,
	// WORKFLOWID, BRTYPEISACTIVE) VALUES ('UNITTEST', 'UNIT TEST CASE PURPOSE ONLY', '1', '1007', TO_DATE('2010-01-01
	// 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Approved', 'MSTGRP1_MAKER', '0', '1')
	// INSERT INTO RMTBASERATES (BRTYPE, CURRENCY, BREFFDATE, BRRATE, DELEXISTINGRATES, LASTMDFDATE, VERSION, LASTMNTBY,
	// LASTMNTON, RECORDSTATUS, WORKFLOWID, BRTYPEISACTIVE) VALUES ('MIBOR', 'INR', TO_DATE('2015-04-09 00:00:00',
	// 'YYYY-MM-DD HH24:MI:SS'), '11', '0', TO_DATE('2015-04-09 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), '1', '1000',
	// TO_DATE('2017-02-18 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Approved', '0', '1')
	// INSERT INTO RMTSPLRATECODES (SRTYPE, SRTYPEDESC, SRISACTIVE, VERSION, LASTMNTBY, LASTMNTON, RECORDTYPE,
	// WORKFLOWID) VALUES ('UNITTEST', 'UNIT TEST CASE PURPOSE ONLY', '1', '1', '1007', TO_DATE('2010-01-01 00:00:00',
	// 'YYYY-MM-DD HH24:MI:SS'), '0', '1')
	// INSERT INTO RMTSPLRATES (SRTYPE, SREFFDATE, SRRATE, DELEXISTINGRATES, LASTMDFDATE, VERSION, LASTMNTBY, LASTMNTON,
	// RECORDTYPE, WORKFLOWID) VALUES ('UNITTEST', TO_DATE('2010-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), '1.5', '0',
	// TO_DATE('2010-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), '1', '1007', TO_DATE('2010-01-01 00:00:00', 'YYYY-MM-DD
	// HH24:MI:SS'), '0', '1')

	public static final String BASE_RATE = "UNITTEST";
	public static final String SPECIAL_RATE = "UNITTEST";
	public static BigDecimal MARGIN_RATE = new BigDecimal(0.5);
	public static final String MNTH_FRQ = "M0025";
	public static final String QTLY_FRQ = "Q0125";

	public static final String BASE_RATE_HIGH = "MIBORHIGH";

	public static final Date date_bpi = DateUtil.getDate("25/01/2017");
	public static final Date date_bpi_1month = DateUtil.getDate("25/02/2017");
	public static final Date date_bpi_1quarter = DateUtil.getDate("25/04/2017");
	public static final Date date_bpi_1year = DateUtil.getDate("25/02/2018");
	public static final Date date_bpi_1Year1quarter = DateUtil.getDate("25/04/2018");

	public static FinScheduleData getSchedule(Cell[] cells, String prodCtg, String testType) {
		FinScheduleData schedule = new FinScheduleData();

		if (prodCtg.equals(FinanceConstants.PRODUCT_CONVENTIONAL) && testType.equals("GENSCHD")) {
			schedule = getConvSchd(cells, testType);
		} else if (prodCtg.equals(FinanceConstants.PRODUCT_CONVENTIONAL) && testType.equals("SRVSCHD")) {
			schedule = getConvSrvSchd(cells);
		} else if (prodCtg.equals(FinanceConstants.PRODUCT_CONVENTIONAL) && testType.equals("HIGHSCHD")) {
			schedule = getConvSchd(cells, testType);
		}

		return schedule;
	}

	private static FinScheduleData getConvSchd(Cell[] cells, String testType) {
		FinScheduleData schedule = new FinScheduleData();
		schedule.setFinanceMain(new FinanceMain());
		schedule.setFinanceType(new FinanceType());
		schedule.getDisbursementDetails().add(new FinanceDisbursement());
		schedule.getFeeRules().add(new FeeRule());

		FinanceMain fm = schedule.getFinanceMain();

		String cellStrValue = Dataset.getString(cells, 37);

		schedule.setFinanceType(new FinanceTypeDataFactory().getFianceType(cellStrValue));

		// _______________________________________________________________________________________________
		// Basic Details
		// _______________________________________________________________________________________________
		cellStrValue = Dataset.getString(cells, 0);

		fm.setFinReference(cellStrValue);
		fm.setFinCcy("INR");
		fm.setFinStartDate(DateUtil.getDate("10/01/2017"));
		fm.setFinAmount(new BigDecimal(110000000));
		fm.setDownPayment(new BigDecimal(10000000));
		fm.setDownPayBank(new BigDecimal(10000000));
		fm.setDownPaySupl(new BigDecimal(0));
		fm.setDefferments(0);
		fm.setFinIsActive(true);
		fm.setCurDisbursementAmt(new BigDecimal(110000000));
		fm.setCalculateRepay(true);
		fm.setEqualRepay(true);
		fm.setProductCategory(FinanceConstants.PRODUCT_CONVENTIONAL);
		fm.setNewRecord(true);

		FinanceDisbursement fd = schedule.getDisbursementDetails().get(0);
		fd.setDisbDate(fm.getFinStartDate());
		fd.setDisbAmount(fm.getFinAmount());
		fd.setDisbReqDate(fd.getDisbDate());
		fd.setDisbIsActive(true);

		cellStrValue = Dataset.getString(cells, 2);
		if (cellStrValue.equals("AD")) {
			fm.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		} else {
			fm.setProfitDaysBasis(CalculationConstants.IDB_30U360);
		}

		// Planned Deferments
		cellStrValue = Dataset.getString(cells, 4);
		if (cellStrValue.equals("DY")) {
			fm.setPlanDeferCount(3);
		} else {
			fm.setPlanDeferCount(0);
		}

		// Planned EMI Holidays
		cellStrValue = Dataset.getString(cells, 5);
		if (cellStrValue.equals("EF")) {
			fm.setPlanEMIHAlw(true);
			fm.setPlanEMIHMethod(FinanceConstants.EMIH_FRQ);
			fm.setPlanEMIHMaxPerYear(2);
			fm.setPlanEMIHMax(6);
			fm.setPlanEMIHLockPeriod(6);
			fm.setPlanEMICpz(true);

			schedule.getPlanEMIHmonths().add(6);
			schedule.getPlanEMIHmonths().add(10);
			schedule.getPlanEMIHmonths().add(11);
		} else if (cellStrValue.equals("EA")) {
			fm.setPlanEMIHAlw(true);
			fm.setPlanEMIHMethod(FinanceConstants.EMIH_ADHOC);
			fm.setPlanEMIHMaxPerYear(2);
			fm.setPlanEMIHMax(6);
			fm.setPlanEMIHLockPeriod(6);
			fm.setPlanEMICpz(true);

			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/08/2017"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/06/2018"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/08/2018"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/10/2018"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/06/2019"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/10/2019"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/06/2020"));
			schedule.getPlanEMIHDates().add(DateUtil.getDate("25/10/2020"));
		} else {
			fm.setPlanEMIHAlw(false);
		}

		// BPI Methods
		fm.setAlwBPI(true);
		fm.setBpiPftDaysBasis(fm.getProfitDaysBasis());
		cellStrValue = Dataset.getString(cells, 11);
		if (cellStrValue.equals("NB")) {
			fm.setAlwBPI(false);
			fm.setBpiTreatment(FinanceConstants.BPI_NO);
		} else if (cellStrValue.equals("DD")) {
			fm.setBpiTreatment(FinanceConstants.BPI_DISBURSMENT);
		} else if (cellStrValue.equals("FI")) {
			fm.setBpiTreatment(FinanceConstants.BPI_SCHEDULE);
		} else if (cellStrValue.equals("CI")) {
			fm.setBpiTreatment(FinanceConstants.BPI_CAPITALIZE);
		} else {
			fm.setBpiTreatment(FinanceConstants.BPI_SCHD_FIRSTEMI);
		}

		// TDS Applicable
		cellStrValue = Dataset.getString(cells, 15);
		if (cellStrValue.equals("TY")) {
			fm.setTDSApplicable(true);
		} else {
			fm.setTDSApplicable(false);
		}

		// Fee and Charges
		fm.setFeeChargeAmt(new BigDecimal(1000000));
		FinFeeDetail feeDtl = new FinFeeDetail();
		feeDtl.setCalculatedAmount(fm.getFeeChargeAmt());
		feeDtl.setActualAmount(fm.getFeeChargeAmt());
		feeDtl.setRemainingFee(fm.getFeeChargeAmt());
		feeDtl.setFeeTypeCode("PROCFEE");
		feeDtl.setFinEvent("ADDDBS");
		feeDtl.setOriginationFee(true);

		cellStrValue = Dataset.getString(cells, 10);
		if (cellStrValue.equals("DD")) {
			feeDtl.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_DISBURSE);
			feeDtl.setTerms(0);
		} else if (cellStrValue.equals("PS")) {
			feeDtl.setTerms(0);
			feeDtl.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_SALE_PRICE);
		} else if (cellStrValue.equals("FI")) {
			feeDtl.setFeeScheduleMethod(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT);
			feeDtl.setTerms(1);
		} else if (cellStrValue.equals("ET")) {
			feeDtl.setFeeScheduleMethod(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR);
			feeDtl.setTerms(48);
		} else {
			feeDtl.setFeeScheduleMethod(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS);
			feeDtl.setTerms(5);
		}

		schedule.getFinFeeDetailList().add(feeDtl);
		schedule.getDisbursementDetails().set(0, fd);

		// Dropline
		fm.setDroplineFrq(null);
		fm.setFirstDroplineDate(null);
		fm.setPftServicingODLimit(false);

		// Step Finance
		cellStrValue = Dataset.getString(cells, 6);
		if (cellStrValue.equals("SN")) {
			fm.setStepFinance(false);
		} else {
			fm.setStepFinance(true);
			// Dummy Policy Name
			if (cellStrValue.equals("SE")) {
				fm.setStepType(FinanceConstants.STEPTYPE_EMI);
				fm.setStepPolicy("STEPEMI");

				schedule = procEMISteps(schedule, testType);
			} else if (cellStrValue.equals("SP")) {
				fm.setStepType(FinanceConstants.STEPTYPE_PRIBAL);
				fm.setStepPolicy("STEPPRI");
				schedule = procPercSteps(schedule, testType);
			}

			fm.setNoOfSteps(4);
		}

		// _______________________________________________________________________________________________
		// GRACE Details
		// _______________________________________________________________________________________________

		fm = setGraceDetails(fm, cells, testType);

		// _______________________________________________________________________________________________
		// REPAYMENT Details
		// _______________________________________________________________________________________________

		fm.setRepayPftFrq(MNTH_FRQ);
		fm.setAllowRepayRvw(true);
		fm.setRepayRvwFrq(QTLY_FRQ);
		fm.setRepayFrq(QTLY_FRQ);
		fm.setMaturityDate(DateUtil.getDate("25/01/2021"));

		// RATE BASIS: FIXED OR REFERENCE RATE
		cellStrValue = Dataset.getString(cells, 16);
		if (cellStrValue.equals("FIX")) {
			if (testType.equals("GENSCHD")) {
				fm.setRepayProfitRate(new BigDecimal(10));
			} else {
				fm.setRepayProfitRate(new BigDecimal(100));
			}

		} else {
			fm.setRepayProfitRate(new BigDecimal(0));

			if (testType.equals("GENSCHD")) {
				fm.setRepayBaseRate(BASE_RATE);
				fm.setRepaySpecialRate(SPECIAL_RATE);
				fm.setRepayMargin(MARGIN_RATE);
			} else {
				fm.setRepayBaseRate(BASE_RATE_HIGH);
				fm.setRepaySpecialRate(null);
				fm.setRepayMargin(BigDecimal.ZERO);
			}
		}

		// Schedule Method
		cellStrValue = Dataset.getString(cells, 12);
		if (cellStrValue.equals("NP")) {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_NOPAY);
		} else if (cellStrValue.equals("IP")) {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_PFT);
		} else if (cellStrValue.equals("EP")) {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_PRI);
		} else if (cellStrValue.equals("PI")) {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_PRI_PFT);
		} else {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_EQUAL);
		}

		// Next Dates
		if (fm.isAllowGrcPeriod()) {
			fm.setNumberOfTerms(36);
			fm.setNextRepayPftDate(date_bpi_1year);
			fm.setNextRepayRvwDate(date_bpi_1Year1quarter);
			fm.setNextRepayDate(date_bpi_1Year1quarter);
		} else {
			fm.setNumberOfTerms(48);
			fm.setNextRepayPftDate(date_bpi_1month);
			fm.setNextRepayRvwDate(date_bpi_1quarter);
			fm.setNextRepayDate(date_bpi_1quarter);
		}

		// Capitalize
		cellStrValue = Dataset.getString(cells, 14);
		if (cellStrValue.equals("CY")) {
			fm.setAllowRepayCpz(true);
			fm.setRepayCpzFrq(MNTH_FRQ);
			if (fm.isAllowGrcPeriod()) {
				fm.setNextRepayCpzDate(date_bpi_1year);
			} else {
				fm.setNextRepayCpzDate(date_bpi_1month);
			}
		} else {
			fm.setAllowRepayCpz(false);
		}

		// Pay profit on frequency
		cellStrValue = Dataset.getString(cells, 13);
		if (cellStrValue.equals("IY")) {
			fm.setFinRepayPftOnFrq(true);
		} else {
			fm.setFinRepayPftOnFrq(false);
		}

		fm.setEventFromDate(fm.getFinStartDate());
		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalFromDate(fm.getFinStartDate());
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		return schedule;
	}

	private static FinanceMain setGraceDetails(FinanceMain fm, Cell[] cells, String testType) {
		fm.setGrcPeriodEndDate(fm.getFinStartDate());
		String cellStrValue = Dataset.getString(cells, 7);

		if (cellStrValue.equals("GN")) {
			fm.setAllowGrcPeriod(false);
			return fm;
		}

		fm.setAllowGrcPeriod(true);
		fm.setGrcPeriodEndDate(DateUtil.getDate("25/01/2018"));

		cellStrValue = Dataset.getString(cells, 16);
		if (cellStrValue.equals("FIX")) {
			if (testType.equals("GENSCHD")) {
				fm.setGrcPftRate(new BigDecimal(10));
			} else {
				fm.setGrcPftRate(new BigDecimal(100));
			}

		} else {
			fm.setGrcPftRate(new BigDecimal(0));

			if (testType.equals("GENSCHD")) {
				fm.setGraceBaseRate(BASE_RATE);
				fm.setGraceSpecialRate(SPECIAL_RATE);
				fm.setGrcMargin(MARGIN_RATE);
			} else {
				fm.setGraceBaseRate(BASE_RATE_HIGH);
				fm.setGraceSpecialRate(null);
				fm.setGrcMargin(BigDecimal.ZERO);
			}
		}

		fm.setGrcProfitDaysBasis(fm.getProfitDaysBasis());
		fm.setGrcPftFrq(MNTH_FRQ);
		fm.setNextGrcPftDate(date_bpi_1month);

		fm.setAllowGrcPftRvw(true);
		fm.setGrcPftRvwFrq(QTLY_FRQ);
		fm.setNextGrcPftRvwDate(date_bpi_1quarter);

		// Grace Capitalize
		cellStrValue = Dataset.getString(cells, 9);
		if (cellStrValue.equals("F")) {
			fm.setAllowGrcCpz(true);
			fm.setGrcCpzFrq(MNTH_FRQ);
			fm.setNextGrcCpzDate(date_bpi_1month);
			fm.setCpzAtGraceEnd(false);
		} else if (cellStrValue.equals("G")) {
			fm.setCpzAtGraceEnd(true);
			fm.setNextGrcCpzDate(fm.getGrcPeriodEndDate());
		} else if (cellStrValue.equals("B")) {
			fm.setAllowGrcCpz(true);
			fm.setGrcCpzFrq(MNTH_FRQ);
			fm.setNextGrcCpzDate(date_bpi_1month);
			fm.setCpzAtGraceEnd(true);
		} else {
			fm.setAllowGrcCpz(false);
			fm.setCpzAtGraceEnd(false);
		}

		// Grace Schedule Method
		fm.setAllowGrcRepay(true);
		cellStrValue = Dataset.getString(cells, 8);
		if (cellStrValue.equals("GE")) {
			fm.setGrcSchdMthd(CalculationConstants.SCHMTHD_GRCENDPAY);
		} else if (cellStrValue.equals("IP")) {
			fm.setGrcSchdMthd(CalculationConstants.SCHMTHD_PFT);
		} else {
			fm.setGrcSchdMthd(CalculationConstants.SCHMTHD_NOPAY);
		}

		return fm;
	}

	private static FinScheduleData procPercSteps(FinScheduleData schedule, String testType) {
		BigDecimal rate = new BigDecimal(-0.5);
		BigDecimal rateIncrease = new BigDecimal(0.5);
		BigDecimal stepPercent = new BigDecimal(0);
		BigDecimal stepPercentIncrease = new BigDecimal(10);

		if (!testType.equals("GENSCHD")) {
			rate = new BigDecimal(90);
			rateIncrease = new BigDecimal(10);
		}

		for (int i = 0; i < 4; i++) {
			FinanceStepPolicyDetail spd = new FinanceStepPolicyDetail();

			spd.setStepNo(i + 1);
			spd.setTenorSplitPerc(new BigDecimal(25.00));
			spd.setInstallments(3);

			rate = rate.add(rateIncrease);
			spd.setRateMargin(rate);

			stepPercent = stepPercent.add(stepPercentIncrease);
			spd.setEmiSplitPerc(stepPercent);

			schedule.getStepPolicyDetails().add(spd);
		}

		return schedule;
	}

	private static FinScheduleData procEMISteps(FinScheduleData schedule, String testType) {
		BigDecimal rate = new BigDecimal(0.0);
		BigDecimal rateIncrease = new BigDecimal(0.5);
		BigDecimal step1 = new BigDecimal(75);
		BigDecimal step2 = new BigDecimal(95);
		BigDecimal step3 = new BigDecimal(105);
		BigDecimal step4 = new BigDecimal(125);

		if (!testType.equals("GENSCHD")) {
			rate = new BigDecimal(90);
			rateIncrease = new BigDecimal(10);
		}

		for (int i = 0; i < 4; i++) {
			FinanceStepPolicyDetail spd = new FinanceStepPolicyDetail();

			spd.setStepNo(i + 1);
			spd.setTenorSplitPerc(new BigDecimal(25.00));
			spd.setInstallments(3);

			rate = rate.add(rateIncrease);
			spd.setRateMargin(rate);

			if (i == 0) {
				spd.setEmiSplitPerc(step1);
			} else if (i == 1) {
				spd.setEmiSplitPerc(step2);
			} else if (i == 2) {
				spd.setEmiSplitPerc(step3);
			} else {
				spd.setEmiSplitPerc(step4);
			}

			schedule.getStepPolicyDetails().add(spd);
		}

		return schedule;
	}

	private static FinScheduleData getConvSrvSchd(Cell[] cells) {
		FinScheduleData schedule = new FinScheduleData();
		schedule.setFinanceMain(new FinanceMain());
		schedule.setFinanceType(new FinanceType());
		schedule.getDisbursementDetails().add(new FinanceDisbursement());
		schedule.getFeeRules().add(new FeeRule());

		FinanceMain fm = schedule.getFinanceMain();

		String cellStrValue = Dataset.getString(cells, 37);

		schedule.setFinanceType(new FinanceTypeDataFactory().getFianceType(cellStrValue));

		Boolean isGraceRequired = false;
		cellStrValue = Dataset.getString(cells, 3);

		if (cellStrValue.startsWith("GRC")) {
			isGraceRequired = true;
		} else {
			isGraceRequired = false;
		}

		// _______________________________________________________________________________________________
		// Basic Details
		// _______________________________________________________________________________________________
		fm.setFinCcy("INR");
		fm.setFinStartDate(DateUtil.getDate("10/01/2017"));
		fm.setFinAmount(new BigDecimal(110000000));
		fm.setDownPayment(new BigDecimal(10000000));
		fm.setDownPayBank(new BigDecimal(10000000));
		fm.setDownPaySupl(new BigDecimal(0));
		fm.setFinIsActive(true);
		fm.setCurDisbursementAmt(new BigDecimal(110000000));
		fm.setCalculateRepay(true);
		fm.setEqualRepay(true);
		fm.setProductCategory(FinanceConstants.PRODUCT_CONVENTIONAL);
		fm.setCalRoundingMode(RoundingMode.HALF_DOWN.name());
		fm.setRoundingTarget(0);

		FinanceDisbursement fd = schedule.getDisbursementDetails().get(0);
		fd.setDisbDate(fm.getFinStartDate());
		fd.setDisbAmount(fm.getFinAmount());
		fd.setDisbReqDate(fd.getDisbDate());
		fd.setDisbIsActive(true);

		fm.setProfitDaysBasis(CalculationConstants.IDB_ACT_365FIXED);
		fm.setPlanEMIHAlw(false);
		fm.setAlwBPI(false);
		schedule.getDisbursementDetails().set(0, fd);
		fm.setStepFinance(false);

		fm.setGrcPeriodEndDate(fm.getFinStartDate());
		fm.setAllowGrcPeriod(false);

		// _______________________________________________________________________________________________
		// REPAYMENT Details
		// _______________________________________________________________________________________________

		fm.setRepayPftFrq(MNTH_FRQ);
		fm.setAllowRepayRvw(true);
		fm.setRepayRvwFrq(MNTH_FRQ);
		fm.setRepayFrq(MNTH_FRQ);
		fm.setMaturityDate(DateUtil.getDate("25/01/2019"));
		fm.setRepayBaseRate(BASE_RATE);
		fm.setRepaySpecialRate(SPECIAL_RATE);
		fm.setRepayMargin(MARGIN_RATE);

		// fm.setRepayProfitRate(new BigDecimal(10));

		// Schedule Method
		cellStrValue = Dataset.getString(cells, 2);
		if (cellStrValue.equals("IP")) {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_PFT);
		} else if (cellStrValue.equals("PI")) {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_PRI_PFT);
		} else {
			fm.setScheduleMethod(CalculationConstants.SCHMTHD_EQUAL);
		}

		// Next Dates
		if (isGraceRequired) {
			fm.setNumberOfTerms(18);
			fm.setNextRepayPftDate(DateUtil.getDate("25/08/2017"));
		} else {
			fm.setNumberOfTerms(24);
			fm.setNextRepayPftDate(date_bpi_1month);
		}

		fm.setNextRepayRvwDate(fm.getNextRepayPftDate());
		fm.setNextRepayDate(fm.getNextRepayPftDate());
		fm.setFinRepayPftOnFrq(true);
		fm.setEventFromDate(fm.getFinStartDate());
		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalFromDate(fm.getFinStartDate());
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		// _______________________________________________________________________________________________
		// GRACE Details
		// _______________________________________________________________________________________________
		if (isGraceRequired) {
			fm = setSrvGraceDetails(fm, cells);
		}

		return schedule;
	}

	private static FinanceMain setSrvGraceDetails(FinanceMain fm, Cell[] cells) {
		fm.setAllowGrcPeriod(true);
		fm.setGrcPeriodEndDate(DateUtil.getDate("25/07/2017"));
		fm.setGrcPftRate(new BigDecimal(10));
		fm.setGrcRateBasis(CalculationConstants.RATE_BASIS_R);

		fm.setGrcProfitDaysBasis(fm.getProfitDaysBasis());
		fm.setGrcPftFrq(fm.getRepayPftFrq());
		fm.setNextGrcPftDate(date_bpi_1month);

		fm.setAllowGrcPftRvw(true);
		fm.setGrcPftRvwFrq(fm.getGrcPftFrq());
		fm.setNextGrcPftRvwDate(fm.getNextGrcPftDate());

		// Grace Capitalize
		fm.setAllowGrcCpz(true);
		fm.setGrcCpzFrq(QTLY_FRQ);
		fm.setNextGrcCpzDate(date_bpi_1quarter);
		fm.setCpzAtGraceEnd(true);

		// Grace Schedule Method
		fm.setAllowGrcRepay(true);
		fm.setGrcSchdMthd(CalculationConstants.SCHMTHD_GRCENDPAY);

		return fm;
	}

}
