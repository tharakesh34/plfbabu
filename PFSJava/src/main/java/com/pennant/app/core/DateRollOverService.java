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

	private static final String	sltDateRollover		= "SELECT FinReference, NextDepDate, DepreciationFrq"
															+ "AllowGrcCpz, AllowGrcPftRvw, AllowRepayCpz, AllowRepayRvw,"
															+ "NextGrcCpzDate, NextGrcPftDate, NextGrcPftRvwDate, NextRepayCpzDate, NextRepayDate,"
															+ "NextRepayPftDate,NextRepayRvwDate FROM FinanceMain WHERE FinIsActive = 1 and CustID = ?";

	private static final String	updDateRollover		= "Update  FinanceMain set LastRepayCpzDate=:LastRepayCpzDate, LastRepayDate=:LastRepayDate,"
															+ "LastRepayPftDate=:LastRepayPftDate,LastRepayRvwDate=:LastRepayRvwDate,NextDepDate=:NextDepDate,"
															+ "NextGrcCpzDate=:NextGrcCpzDate,NextGrcPftDate=:NextGrcPftDate,NextGrcPftRvwDate=:NextGrcPftRvwDate,"
															+ "NextRepayCpzDate=:NextRepayCpzDate,NextRepayDate=:NextRepayDate,NextRepayPftDate=:NextRepayPftDate,"
															+ "NextRepayRvwDate=:NextRepayRvwDate where FinReference = :FinReference ";

	public void process(Connection connection, long custId, Date valueDate) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";
		try {
			sqlStatement = connection.prepareStatement(sltDateRollover);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

				finreference = resultSet.getString("FinReference");
				FinanceMain finMain = new FinanceMain();
				finMain.setFinReference(finreference);

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

				boolean isUpdateReq = false;
				boolean isNextDateFound = false;

				//Set Next Grace Capitalization Date
				if (finMain.getNextGrcCpzDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextGraceCpzDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Grace Profit Date
				if (finMain.getNextGrcPftDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextGrcPftDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Grace Profit Review Date
				if (finMain.getNextGrcPftRvwDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextGrcPftRvwDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Repay Capitalization Date
				if (finMain.getNextRepayCpzDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextRepayCpzDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Repayment Date
				if (finMain.getNextRepayDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextRepayDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Repayment Profit Date
				if (finMain.getNextRepayPftDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextRepayPftDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Repayment Profit Review Date
				if (finMain.getNextRepayRvwDate().compareTo(valueDate) == 0) {
					isNextDateFound = setNextRepayRvwDate(datesMap, finSchdDetails, finMain);
					if (isNextDateFound) {
						isUpdateReq = true;
					}
				}

				//Set Next Depreciation Date
				if (finMain.getNextDepDate().compareTo(valueDate) == 0) {
					if (!StringUtils.isEmpty(finMain.getDepreciationFrq())) {
						if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) < 0) {
							finMain.setNextDepDate(FrequencyUtil.getNextDate(finMain.getDepreciationFrq(), 1,
									valueDate, "A", false).getNextFrequencyDate());
						}

						if (finMain.getNextDepDate().compareTo(finMain.getMaturityDate()) > 0) {
							finMain.setNextDepDate(finMain.getMaturityDate());
						}
					}
				}

				if (isUpdateReq) {
					NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
					jdbcTemplate.update(updDateRollover, new BeanPropertySqlParameterSource(finMain));
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

	private boolean setNextGraceCpzDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (!finMain.isAllowGrcCpz()) {
			return false;
		}

		if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

	private boolean setNextGrcPftDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (finMain.getNextGrcPftDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

	private boolean setNextGrcPftRvwDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (!finMain.isAllowGrcPftRvw()) {
			return false;
		}

		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getGrcPeriodEndDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

	private boolean setNextRepayCpzDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (!finMain.isAllowRepayCpz()) {
			return false;
		}

		if (finMain.getNextRepayCpzDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

	private boolean setNextRepayDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (finMain.getNextRepayDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

	private boolean setNextRepayPftDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (finMain.getNextRepayPftDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

	private boolean setNextRepayRvwDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> finSchdDetails,
			FinanceMain finMain) {

		if (!finMain.isAllowRepayRvw()) {
			return false;
		}
		
		if (finMain.getNextRepayRvwDate().compareTo(finMain.getMaturityDate()) >= 0) {
			return false;
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
				return true;
			}
		}

		return false;
	}

}
