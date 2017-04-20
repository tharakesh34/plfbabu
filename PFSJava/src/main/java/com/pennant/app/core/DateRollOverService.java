package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.core.TableType;

public class DateRollOverService extends ServiceHelper {

	private static final long	serialVersionUID	= -3371115026576113554L;

	private static Logger		logger				= Logger.getLogger(DateRollOverService.class);

	private static final String	sltDateRollover		= "SELECT FinReference, NextDepDate, DepreciationFrq,"
															+ "AllowGrcCpz, AllowGrcPftRvw, AllowRepayCpz, AllowRepayRvw, GrcPeriodEndDate,"
															+ "NextGrcCpzDate, NextGrcPftDate, NextGrcPftRvwDate, NextRepayCpzDate, NextRepayDate,"
															+ "NextRepayPftDate,NextRepayRvwDate FROM FinanceMain WHERE FinIsActive = 1 and CustID = ?";

	public void process(Connection connection, long custId, Date valueDate) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		StringBuilder sqlString = new StringBuilder(" ");

		try {
			sqlStatement = connection.prepareStatement(sltDateRollover);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

				finreference = resultSet.getString("FinReference");
				FinanceMain finMain = new FinanceMain();
				finMain.setFinReference(finreference);

				finMain.setAllowGrcCpz(resultSet.getBoolean("AllowGrcCpz"));
				finMain.setAllowGrcPftRvw(resultSet.getBoolean("AllowGrcPftRvw"));
				finMain.setAllowRepayCpz(resultSet.getBoolean("AllowRepayCpz"));
				finMain.setAllowRepayRvw(resultSet.getBoolean("AllowRepayRvw"));
				
				finMain.setGrcPeriodEndDate(resultSet.getDate("GrcPeriodEndDate"));
	
				finMain.setNextGrcCpzDate(resultSet.getDate("NextGrcCpzDate"));
				finMain.setNextGrcPftDate(resultSet.getDate("NextGrcPftDate"));
				finMain.setNextGrcPftRvwDate(resultSet.getDate("NextGrcPftRvwDate"));

				finMain.setNextRepayCpzDate(resultSet.getDate("NextRepayCpzDate"));
				finMain.setNextRepayDate(resultSet.getDate("NextRepayDate"));
				finMain.setNextRepayPftDate(resultSet.getDate("NextRepayPftDate"));
				finMain.setNextRepayRvwDate(resultSet.getDate("NextRepayRvwDate"));

				finMain.setDepreciationFrq(resultSet.getString("DepreciationFrq"));
				finMain.setNextDepDate(resultSet.getDate("NextDepDate"));

				List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
						finreference, TableType.MAIN_TAB.getSuffix(), false);

				//Place schedule dates to Map
				for (int i = 0; i < finSchdDetails.size(); i++) {
					datesMap.put(finSchdDetails.get(i).getSchDate(), i);
				}

				//Set Next Grace Capitalization Date
				if (finMain.getNextGrcCpzDate().compareTo(valueDate) == 0) {
					sqlString = setNextGraceCpzDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Grace Profit Date
				if (finMain.getNextGrcPftDate().compareTo(valueDate) == 0) {
					sqlString = setNextGrcPftDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Grace Profit Review Date
				if (finMain.getNextGrcPftRvwDate().compareTo(valueDate) == 0) {
					sqlString = setNextGrcPftRvwDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Repay Capitalization Date
				if (finMain.getNextRepayCpzDate().compareTo(valueDate) == 0) {
					sqlString = setNextRepayCpzDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Repayment Date
				if (finMain.getNextRepayDate().compareTo(valueDate) == 0) {
					sqlString = setNextRepayDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Repayment Profit Date
				if (finMain.getNextRepayPftDate().compareTo(valueDate) == 0) {
					sqlString = setNextRepayPftDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Repayment Profit Review Date
				if (finMain.getNextRepayRvwDate().compareTo(valueDate) == 0) {
					sqlString = setNextRepayRvwDate(datesMap, finSchdDetails, finMain, sqlString);
				}

				//Set Next Depreciation Date
				if (finMain.getNextDepDate()!=null && finMain.getNextDepDate().compareTo(valueDate) == 0) {
					if (!StringUtils.isEmpty(finMain.getDepreciationFrq())) {
						if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) < 0) {
							finMain.setNextDepDate(FrequencyUtil.getNextDate(finMain.getDepreciationFrq(), 1,
									valueDate, "A", false).getNextFrequencyDate());
						}

						if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) > 0) {
							finMain.setNextDepDate(finMain.getMaturityDate());
						}

						sqlString.append(" NextDepDate=:NextDepDate");
					}
				}

				if (!StringUtils.isEmpty(sqlString.toString())) {

					if (sqlString.toString().trim().endsWith(",")) {
						sqlString.deleteCharAt(sqlString.lastIndexOf(","));
					}

					StringBuilder updString = new StringBuilder("Update  FinanceMain set  ");
					updString.append(sqlString);
					sqlString.append(" where FinReference = :FinReference ");

					NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
					jdbcTemplate.update(updString.toString(), new BeanPropertySqlParameterSource(finMain));
				}

				datesMap.clear();

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

	}

	private StringBuilder setNextGraceCpzDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

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
				finMain.setNextGrcCpzDate(finSchdDetails.get(i).getSchDate());
				sqlString.append(" NextGrcCpzDate=:NextGrcCpzDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	private StringBuilder setNextGrcPftDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

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

	private StringBuilder setNextGrcPftRvwDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

		if (!finMain.isAllowGrcPftRvw()) {
			return sqlString;
		}

		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return sqlString;
		}

		int i = datesMap.get(finMain.getNextGrcPftRvwDate());
		FinanceScheduleDetail curSchd = null;

		for (int j = i; j < finSchdDetails.size(); j++) {
			curSchd = finSchdDetails.get(i);
			if (curSchd.getSchDate().compareTo(finMain.getNextGrcPftRvwDate()) <= 0) {
				continue;
			}

			if (curSchd.isRvwOnSchDate()) {
				finMain.setNextGrcPftRvwDate(finSchdDetails.get(i).getSchDate());
				sqlString.append(" NextGrcPftRvwDate=:NextGrcPftRvwDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	private StringBuilder setNextRepayCpzDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

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
				finMain.setNextRepayCpzDate(finSchdDetails.get(i).getSchDate());
				sqlString.append(" LastRepayCpzDate=:LastRepayCpzDate, NextRepayCpzDate=:NextRepayCpzDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	private StringBuilder setNextRepayDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

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

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				finMain.setLastRepayDate(finMain.getNextRepayDate());
				finMain.setNextRepayDate(finSchdDetails.get(i).getSchDate());
				sqlString.append(" LastRepayDate=:LastRepayDate, NextRepayDate=:NextRepayDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	private StringBuilder setNextRepayPftDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

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

			if (curSchd.isPftOnSchDate()) {
				finMain.setLastRepayPftDate(finMain.getNextRepayPftDate());
				finMain.setNextRepayPftDate(finSchdDetails.get(i).getSchDate());
				sqlString.append(" LastRepayPftDate=:LastRepayPftDate, NextRepayPftDate=:NextRepayPftDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

	private StringBuilder setNextRepayRvwDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain, StringBuilder sqlString) {

		if (!finMain.isAllowRepayRvw()) {
			return sqlString;
		}

		if (finMain.getNextRepayRvwDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return sqlString;
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
				finMain.setNextRepayRvwDate(finSchdDetails.get(i).getSchDate());
				sqlString.append(" LastRepayRvwDate=:LastRepayRvwDate, NextRepayRvwDate=:NextRepayRvwDate,");
				return sqlString;
			}
		}

		return sqlString;
	}

}
