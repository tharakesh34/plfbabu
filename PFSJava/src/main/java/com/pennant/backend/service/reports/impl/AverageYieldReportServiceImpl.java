package com.pennant.backend.service.reports.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.systemmasters.AverageYieldReport;
import com.pennant.backend.service.reports.AverageYieldReportService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AverageYieldReportServiceImpl implements AverageYieldReportService {

	private static final Logger logger = LogManager.getLogger(AverageYieldReportServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceTypeDAO financeTypeDAO;

	@Override
	public List<AverageYieldReport> getAverageYieldLoanReportList(Date startDate, Date endDate) {
		logger.debug(Literal.ENTERING);

		List<FinanceMain> financeMainList = financeMainDAO.getFinanceMainActiveList(startDate, endDate, null);
		List<AverageYieldReport> averageYieldReportList = new ArrayList<>();

		if (CollectionUtils.isEmpty(financeMainList)) {
			return new ArrayList<>();
		}

		BigDecimal noOfDays = new BigDecimal(DateUtil.getDaysBetween(startDate, endDate));

		for (FinanceMain financeMain : financeMainList) {// Schedule Details
			List<FinanceScheduleDetail> scheduleDetails = this.financeScheduleDetailDAO
					.getFinSchdDetailsBtwDates(financeMain.getFinReference(), startDate, endDate);

			scheduleDetails = sortSchdDetails(scheduleDetails);

			if (CollectionUtils.isEmpty(scheduleDetails)) {
				continue;
			}

			AverageYieldReport averageYieldReport = new AverageYieldReport();
			int size = scheduleDetails.size();
			FinanceScheduleDetail prvSchd;
			boolean endDateCompleted = false;
			BigDecimal totalInterest = BigDecimal.ZERO;
			BigDecimal totalClosingBal = BigDecimal.ZERO;
			Date fromDate = null;
			Date toDate = null;

			for (int i = 1; i < size; i++) {

				FinanceScheduleDetail curSchd = scheduleDetails.get(i);
				prvSchd = scheduleDetails.get(i - 1);
				if (DateUtil.compare(curSchd.getSchDate(), startDate) >= 0) {

					if ((DateUtil.compare(curSchd.getSchDate(), endDate) > 0 && endDateCompleted)) {
						break;
					}

					boolean startDateisFrqDate = false;
					if (DateUtil.compare(prvSchd.getSchDate(), startDate) < 0) {
						fromDate = startDate;
					} else {
						fromDate = prvSchd.getSchDate();
						startDateisFrqDate = true;
					}

					boolean endDateisFrqDate = false;
					if (DateUtil.compare(curSchd.getSchDate(), endDate) > 0) {
						toDate = endDate;
						endDateCompleted = true;
					} else {
						toDate = curSchd.getSchDate();
						toDate = DateUtil.addDays(toDate, -1);
						endDateisFrqDate = true;
						if (DateUtil.compare(curSchd.getSchDate(), endDate) == 0) {
							endDateCompleted = true;
						}
					}
					if (DateUtil.compare(toDate, startDate) >= 0) {

						if (startDateisFrqDate && endDateisFrqDate) {
							totalInterest = totalInterest.add(PennantApplicationUtil
									.formateAmount(curSchd.getProfitCalc(), PennantConstants.defaultCCYDecPos));
							totalClosingBal = totalClosingBal.add(curSchd.getBalanceForPftCal());
						} else {
							BigDecimal calInt = CalculationUtil.calInterest(fromDate, toDate,
									curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(),
									prvSchd.getCalculatedRate());

							BigDecimal calIntRounded = BigDecimal.ZERO;
							if (calInt.compareTo(BigDecimal.ZERO) > 0) {
								calIntRounded = CalculationUtil.roundAmount(calInt, financeMain.getCalRoundingMode(),
										financeMain.getRoundingTarget());
							}
							BigDecimal diffDays = new BigDecimal(DateUtil.getDaysBetween(fromDate, toDate));
							totalInterest = totalInterest.add(calIntRounded);
							totalClosingBal = totalClosingBal.add(curSchd.getBalanceForPftCal().multiply(diffDays));
						}
					}
				}
			}
			if (totalInterest.compareTo(BigDecimal.ZERO) == 0) {
				logger.info("no interest");
			} else {
				averageYieldReport.setCustShrtName(financeMain.getCustShrtName());
				averageYieldReport.setFinReference(financeMain.getFinReference());
				averageYieldReport.setFinType(financeMain.getFinType());
				averageYieldReport.setAvgFunding(totalClosingBal.divide(noOfDays, 0, RoundingMode.HALF_DOWN));
				averageYieldReport.setIntEarned(totalInterest);
				BigDecimal yieldPercent = totalInterest
						.divide(averageYieldReport.getAvgFunding(), 9, RoundingMode.HALF_DOWN)
						.multiply(BigDecimal.valueOf(365));
				averageYieldReport.setYieldPercent(yieldPercent.divide(noOfDays, 2, RoundingMode.HALF_DOWN));
				averageYieldReportList.add(averageYieldReport);
			}
		}
		return averageYieldReportList;
	}

	@Override
	public List<AverageYieldReport> getAverageYieldProductReportList(Date startDate, Date endDate) {
		logger.debug(Literal.ENTERING);

		List<String> finTypeList = financeTypeDAO.getFinanceTypeList();
		List<AverageYieldReport> averageYieldReportList = new ArrayList<>();
		if (CollectionUtils.isEmpty(finTypeList)) {
			return new ArrayList<>();
		}

		for (String finType : finTypeList) {
			List<FinanceMain> financeMainList = financeMainDAO.getFinanceMainActiveList(startDate, endDate, finType);

			if (CollectionUtils.isEmpty(financeMainList)) {
				continue;
			}

			AverageYieldReport averageYieldReport = new AverageYieldReport();
			BigDecimal totalInterest = BigDecimal.ZERO;
			BigDecimal totalClosingBal = BigDecimal.ZERO;
			BigDecimal noOfDays = new BigDecimal(DateUtil.getDaysBetween(startDate, endDate));

			for (FinanceMain financeMain : financeMainList) {// Schedule Details
				List<FinanceScheduleDetail> scheduleDetails = this.financeScheduleDetailDAO
						.getFinSchdDetailsBtwDates(financeMain.getFinReference(), startDate, endDate);

				scheduleDetails = sortSchdDetails(scheduleDetails);

				if (CollectionUtils.isEmpty(scheduleDetails)) {
					continue;
				}

				int size = scheduleDetails.size();
				FinanceScheduleDetail prvSchd;
				boolean endDateCompleted = false;
				Date fromDate = null;
				Date toDate = null;

				for (int i = 1; i < size; i++) {

					FinanceScheduleDetail curSchd = scheduleDetails.get(i);
					prvSchd = scheduleDetails.get(i - 1);
					if (DateUtil.compare(curSchd.getSchDate(), startDate) >= 0) {
						if ((DateUtil.compare(curSchd.getSchDate(), endDate) > 0 && endDateCompleted)) {
							break;
						}

						boolean startDateisFrqDate = false;
						if (DateUtil.compare(prvSchd.getSchDate(), startDate) < 0) {
							fromDate = startDate;
						} else {
							fromDate = prvSchd.getSchDate();
							startDateisFrqDate = true;
						}

						boolean endDateisFrqDate = false;
						if (DateUtil.compare(curSchd.getSchDate(), endDate) > 0) {
							toDate = endDate;
							endDateCompleted = true;
						} else {
							toDate = curSchd.getSchDate();
							toDate = DateUtil.addDays(toDate, -1);
							endDateisFrqDate = true;
							if (DateUtil.compare(curSchd.getSchDate(), endDate) == 0) {
								endDateCompleted = true;
							}
						}
						if (DateUtil.compare(toDate, startDate) >= 0) {

							if (startDateisFrqDate && endDateisFrqDate) {
								totalInterest = totalInterest.add(PennantApplicationUtil
										.formateAmount(curSchd.getProfitCalc(), PennantConstants.defaultCCYDecPos));
								totalClosingBal = totalClosingBal.add(curSchd.getBalanceForPftCal());
							} else {
								BigDecimal calInt = CalculationUtil.calInterest(fromDate, toDate,
										curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(),
										prvSchd.getCalculatedRate());

								BigDecimal calIntRounded = BigDecimal.ZERO;
								if (calInt.compareTo(BigDecimal.ZERO) > 0) {
									calIntRounded = CalculationUtil.roundAmount(calInt,
											financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
								}
								totalInterest = totalInterest.add(calIntRounded);
								BigDecimal diffDays = new BigDecimal(DateUtil.getDaysBetween(fromDate, toDate));
								totalClosingBal = totalClosingBal.add(curSchd.getBalanceForPftCal().multiply(diffDays));
							}
						}
					}
				}
			}
			if (totalInterest.compareTo(BigDecimal.ZERO) == 0) {
				logger.info("no interest");
			} else {
				averageYieldReport.setFinType(finType);
				averageYieldReport.setAvgFunding(totalClosingBal.divide(noOfDays, 0, RoundingMode.HALF_DOWN));
				averageYieldReport.setIntEarned(totalInterest);
				BigDecimal yieldPercent = totalInterest
						.divide(averageYieldReport.getAvgFunding(), 9, RoundingMode.HALF_DOWN)
						.multiply(BigDecimal.valueOf(365));
				averageYieldReport.setYieldPercent(yieldPercent.divide(noOfDays, 2, RoundingMode.HALF_DOWN));

				averageYieldReportList.add(averageYieldReport);
			}
		}
		return averageYieldReportList;
	}

	public static List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

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

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

}
