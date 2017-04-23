package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.core.TableType;

public class DateRollOverService extends ServiceHelper {

	private static final long	serialVersionUID	= -3371115026576113554L;

	private static Logger		logger				= Logger.getLogger(DateRollOverService.class);

	private static final String	sltDateRollover		= "SELECT FinReference, NextDepDate, DepreciationFrq,"
															+ "AllowGrcCpz, AllowGrcPftRvw, AllowRepayCpz, AllowRepayRvw, GrcPeriodEndDate,MaturityDate,"
															+ "NextGrcCpzDate, NextGrcPftDate, NextGrcPftRvwDate, NextRepayCpzDate, NextRepayDate,"
															+ "NextRepayPftDate,NextRepayRvwDate, FinRvwRateApplFor, SchCalOnRvw FROM FinanceMain WHERE FinIsActive = 1 and CustID = ?";

	public List<FinEODEvent> process(Connection connection, List<FinEODEvent> finEODEvents) throws Exception {

		for (FinEODEvent finEODEvent : finEODEvents) {
			StringBuilder sqlString = new StringBuilder("");
			finEODEvent.setEodValueDate(DateUtility.addDays(finEODEvent.getEodDate(), 1));

			Date valueDate = finEODEvent.getEodValueDate();
			FinanceMain finMain = finEODEvent.getFinanceMain();
			Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

			//Set Next Grace Capitalization Date
			if (finMain.getNextGrcCpzDate() != null && finMain.getNextGrcCpzDate().compareTo(valueDate) == 0) {
				sqlString = setNextGraceCpzDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Grace Profit Date
			if (finMain.getNextGrcPftDate() != null && finMain.getNextGrcPftDate().compareTo(valueDate) == 0) {
				sqlString = setNextGrcPftDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Grace Profit Review Date
			if (finMain.getNextGrcPftRvwDate() != null && finMain.getNextGrcPftRvwDate().compareTo(valueDate) == 0) {
				sqlString = setNextGrcPftRvwDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Repay Capitalization Date
			if (finMain.getNextRepayCpzDate() != null && finMain.getNextRepayCpzDate().compareTo(valueDate) == 0) {
				sqlString = setNextRepayCpzDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Repayment Date
			if (finMain.getNextRepayDate().compareTo(valueDate) == 0) {
				sqlString = setNextRepayDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Repayment Profit Date
			if (finMain.getNextRepayPftDate().compareTo(valueDate) == 0) {
				sqlString = setNextRepayPftDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Repayment Profit Review Date
			if (finMain.getNextRepayRvwDate() != null && finMain.getNextRepayRvwDate().compareTo(valueDate) == 0) {
				sqlString = setNextRepayRvwDate(datesMap, finEODEvent, sqlString);
			}

			//Set Next Depreciation Date
			if (finMain.getNextDepDate() != null && finMain.getNextDepDate().compareTo(valueDate) == 0) {
				if (!StringUtils.isEmpty(finMain.getDepreciationFrq())) {
					if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) < 0) {
						finMain.setNextDepDate(FrequencyUtil.getNextDate(finMain.getDepreciationFrq(), 1, valueDate,
								"A", false).getNextFrequencyDate());
					}

					if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) > 0) {
						finMain.setNextDepDate(finMain.getMaturityDate());
					}

					sqlString.append(" NextDepDate=:NextDepDate");
				}
			}

			if (!StringUtils.isBlank(sqlString.toString())) {

				if (sqlString.toString().trim().endsWith(",")) {
					sqlString.deleteCharAt(sqlString.lastIndexOf(","));
				}

				StringBuilder updString = new StringBuilder("Update  FinanceMain set  ");
				updString.append(sqlString);
				updString.append(" where FinReference = :FinReference ");

				NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
				jdbcTemplate.update(updString.toString(), new BeanPropertySqlParameterSource(finMain));
			}

			datesMap.clear();

		}

		return finEODEvents;

	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Capitalization Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextGraceCpzDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (!finMain.isAllowGrcCpz()) {
			return sqlString;
		}

		if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return sqlString;
		}

		int i = datesMap.get(finMain.getNextGrcCpzDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				finMain.setNextGrcCpzDate(curSchd.getSchDate());
				sqlString.append(" NextGrcCpzDate=:NextGrcCpzDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextGrcPftDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return sqlString;
		}

		int i = datesMap.get(finMain.getNextGrcPftDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				finMain.setNextGrcPftDate(finSchdDetails.get(i).getSchDate());

				sqlString.append(" NextGrcPftDate=:NextGrcPftDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Grace Profit Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextGrcPftRvwDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (!finMain.isAllowGrcPftRvw()) {
			return sqlString;
		}

		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return sqlString;
		}

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(finEODEvents.getEodValueDate()) != 0
					&& finMain.getMaturityDate().compareTo(finEODEvents.getEodValueDate()) != 0) {
				finEODEvents.setRateReview(true);
			}
		}

		int i = datesMap.get(finMain.getNextGrcPftRvwDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate() || curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) == 0) {
				finMain.setNextGrcPftRvwDate(curSchd.getSchDate());
				sqlString.append(" NextGrcPftRvwDate=:NextGrcPftRvwDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Capitalization Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextRepayCpzDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (!finMain.isAllowRepayCpz()) {
			return sqlString;
		}

		if (finMain.getNextRepayCpzDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return sqlString;
		}

		int i = datesMap.get(finMain.getNextRepayCpzDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayCpzDate()) <= 0) {
				continue;
			}

			if (curSchd.isCpzOnSchDate()) {
				finMain.setLastRepayCpzDate(finMain.getNextRepayCpzDate());
				finMain.setNextRepayCpzDate(curSchd.getSchDate());
				sqlString.append(" LastRepayCpzDate=:LastRepayCpzDate, NextRepayCpzDate=:NextRepayCpzDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repayment Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextRepayDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (finMain.getNextRepayDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return sqlString;
		}

		int i = datesMap.get(finMain.getNextRepayDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()
					|| curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
				finMain.setLastRepayDate(finMain.getNextRepayDate());
				finMain.setNextRepayDate(curSchd.getSchDate());
				sqlString.append(" LastRepayDate=:LastRepayDate, NextRepayDate=:NextRepayDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Profit Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextRepayPftDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (finMain.getNextRepayPftDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return sqlString;
		}

		int i = datesMap.get(finMain.getNextRepayPftDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayPftDate()) <= 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
				finMain.setLastRepayPftDate(finMain.getNextRepayPftDate());
				finMain.setNextRepayPftDate(curSchd.getSchDate());
				sqlString.append(" LastRepayPftDate=:LastRepayPftDate, NextRepayPftDate=:NextRepayPftDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	//--------------------------------------------------------------------------------------------------------------------------
	//Next Repay Review Date
	//--------------------------------------------------------------------------------------------------------------------------
	private StringBuilder setNextRepayRvwDate(Map<Date, Integer> datesMap, FinEODEvent finEODEvents,
			StringBuilder sqlString) {
		FinanceMain finMain = finEODEvents.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvents.getFinanceScheduleDetails();

		if (!finMain.isAllowRepayRvw()) {
			return sqlString;
		}

		if (finMain.getNextRepayRvwDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return sqlString;
		}

		if (!StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_NORVW)) {
			if (finMain.getFinStartDate().compareTo(finEODEvents.getEodValueDate()) != 0
					&& finMain.getMaturityDate().compareTo(finEODEvents.getEodValueDate()) != 0) {
				finEODEvents.setRateReview(true);
			}
		}

		int i = datesMap.get(finMain.getNextRepayRvwDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextRepayRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate()) {
				finMain.setLastRepayRvwDate(finMain.getNextRepayRvwDate());
				finMain.setNextRepayRvwDate(curSchd.getSchDate());
				sqlString.append(" LastRepayRvwDate=:LastRepayRvwDate, NextRepayRvwDate=:NextRepayRvwDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	public List<FinEODEvent> preoarefinEODEvents(Connection connection, long custId, Date date) throws Exception {

		List<FinEODEvent> finEODEvents = new ArrayList<FinEODEvent>();

		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {

			sqlStatement = connection.prepareStatement(sltDateRollover);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				FinEODEvent eodEvent = new FinEODEvent();

				Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

				finreference = resultSet.getString("FinReference");
				FinanceMain finMain = new FinanceMain();
				finMain.setFinReference(finreference);

				finMain.setAllowGrcCpz(resultSet.getBoolean("AllowGrcCpz"));
				finMain.setAllowGrcPftRvw(resultSet.getBoolean("AllowGrcPftRvw"));
				finMain.setAllowRepayCpz(resultSet.getBoolean("AllowRepayCpz"));
				finMain.setAllowRepayRvw(resultSet.getBoolean("AllowRepayRvw"));

				finMain.setGrcPeriodEndDate(resultSet.getDate("GrcPeriodEndDate"));
				finMain.setMaturityDate(resultSet.getDate("MaturityDate"));

				finMain.setNextGrcCpzDate(resultSet.getDate("NextGrcCpzDate"));
				finMain.setNextGrcPftDate(resultSet.getDate("NextGrcPftDate"));
				finMain.setNextGrcPftRvwDate(resultSet.getDate("NextGrcPftRvwDate"));

				finMain.setNextRepayCpzDate(resultSet.getDate("NextRepayCpzDate"));
				finMain.setNextRepayDate(resultSet.getDate("NextRepayDate"));
				finMain.setNextRepayPftDate(resultSet.getDate("NextRepayPftDate"));
				finMain.setNextRepayRvwDate(resultSet.getDate("NextRepayRvwDate"));

				finMain.setDepreciationFrq(resultSet.getString("DepreciationFrq"));
				finMain.setNextDepDate(resultSet.getDate("NextDepDate"));

				finMain.setRvwRateApplFor(resultSet.getString("RvwRateApplFor"));
				finMain.setSchCalOnRvw(resultSet.getString("SchCalOnRvw"));

				List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
						finreference, TableType.MAIN_TAB.getSuffix(), false);
				//Place schedule dates to Map
				for (int i = 0; i < finSchdDetails.size(); i++) {
					datesMap.put(finSchdDetails.get(i).getSchDate(), i);
				}

				eodEvent.setFinanceMain(finMain);
				eodEvent.setFinanceScheduleDetails(finSchdDetails);
				eodEvent.setDatesMap(datesMap);
				eodEvent.setEodDate(date);
				eodEvent.setEodValueDate(date);

				finEODEvents.add(eodEvent);

			}
		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference :" + finreference, e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		return finEODEvents;

	}

}
