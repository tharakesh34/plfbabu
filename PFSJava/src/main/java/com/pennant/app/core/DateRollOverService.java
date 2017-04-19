package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.core.TableType;

public class DateRollOverService extends ServiceHelper {

	private static final long	serialVersionUID	= -3371115026576113554L;

	private static Logger		logger				= Logger.getLogger(DateRollOverService.class);

	private static final String	DR_SELECT			= "SELECT FinReference, LastRepayCpzDate, LastRepayDate, LastRepayPftDate, LastRepayRvwDate, NextDepDate,"
															+ "NextGrcCpzDate, NextGrcPftDate, NextGrcPftRvwDate, NextRepayCpzDate, NextRepayDate,"
															+ "NextRepayPftDate,NextRepayRvwDate,DepreciationFrq FROM FinanceMain WHERE FinIsActive = 1 and CustID = ?";

	private static final String	DR_UPDATE			= "Update  FinanceMain set LastRepayCpzDate=:LastRepayCpzDate, LastRepayDate=:LastRepayDate,"
															+ "LastRepayPftDate=:LastRepayPftDate,LastRepayRvwDate=:LastRepayRvwDate,NextDepDate=:NextDepDate,"
															+ "NextGrcCpzDate=:NextGrcCpzDate,NextGrcPftDate=:NextGrcPftDate,NextGrcPftRvwDate=:NextGrcPftRvwDate,"
															+ "NextRepayCpzDate=:NextRepayCpzDate,NextRepayDate=:NextRepayDate,NextRepayPftDate=:NextRepayPftDate,"
															+ "NextRepayRvwDate=:NextRepayRvwDate where FinReference = :FinReference ";

	public void process(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";
		try {
			sqlStatement = connection.prepareStatement(DR_SELECT);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

				finreference = resultSet.getString("FinReference");
				FinanceMain finMain = new FinanceMain();
				finMain.setFinReference(finreference);

				finMain.setLastRepayCpzDate(resultSet.getDate("LastRepayCpzDate"));
				finMain.setLastRepayDate(resultSet.getDate("LastRepayDate"));
				finMain.setLastRepayPftDate(resultSet.getDate("LastRepayPftDate"));
				finMain.setLastRepayRvwDate(resultSet.getDate("LastRepayRvwDate"));

				finMain.setNextGrcCpzDate(resultSet.getDate("NextGrcCpzDate"));
				finMain.setNextGrcPftDate(resultSet.getDate("NextGrcPftDate"));
				finMain.setNextGrcPftRvwDate(resultSet.getDate("NextGrcPftRvwDate"));

				finMain.setNextRepayCpzDate(resultSet.getDate("NextRepayCpzDate"));
				finMain.setNextRepayDate(resultSet.getDate("NextRepayDate"));
				finMain.setNextRepayPftDate(resultSet.getDate("NextRepayPftDate"));
				finMain.setNextRepayRvwDate(resultSet.getDate("NextRepayRvwDate"));

				finMain.setDepreciationFrq(resultSet.getString("DepreciationFrq"));
				finMain.setNextDepDate(resultSet.getDate("NextDepDate"));

				List<FinanceScheduleDetail> list = getFinanceScheduleDetailDAO().getFinScheduleDetails(finreference,
						TableType.MAIN_TAB.getSuffix(), false);

				for (int i = 0; i < list.size(); i++) {
					datesMap.put(list.get(i).getSchDate(), i);
				}

				boolean anyDateChanged = false;

				for (int i = 0; i < list.size(); i++) {

					if (anyDateChanged) {

					}

					anyDateChanged = true;

				}

				if (anyDateChanged) {
					NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
					jdbcTemplate.update(DR_UPDATE, new BeanPropertySqlParameterSource(finMain));
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

	private void setNextRepayCpzDate(Map<Date, Integer> datesMap, List<FinanceScheduleDetail> list, FinanceMain main) {
		int i = datesMap.get(main.getNextRepayCpzDate());
		for (int j = i; j < list.size(); j++) {

		}

	}
}
