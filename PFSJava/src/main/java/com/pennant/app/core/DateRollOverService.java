package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.util.PennantConstants;

public class DateRollOverService extends ServiceHelper {

	private static final long	serialVersionUID	= -3371115026576113554L;

	private static Logger		logger				= Logger.getLogger(DateRollOverService.class);

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery(Date valueDate) {

		StringBuilder query = new StringBuilder("SELECT FinReference, CustID, FinBranch, GrcPftFrq, NextGrcPftDate");
		query.append("  ,AllowGrcPeriod, ");
		query.append(" FinRepayPftOnFrq, DepreciationFrq, NextDepDate, AllowGrcPftRvw, GrcPftRvwFrq, NextGrcPftRvwDate, ");
		query.append(" AllowGrcCpz, GrcCpzFrq, AllowGrcRepay, NextGrcCpzDate, ");
		query.append(" RepayFrq, NextRepayDate, NextRepayDate, NextRepayPftDate, ");
		query.append(" AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, RepayCpzFrq, NextRepayCpzDate, ");
		query.append(" FinType, FinCcy, GrcPeriodEndDate, LastRepayDate,");
		query.append(" MaturityDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate ");
		query.append(" FROM FinanceMain WHERE (NextGrcPftDate = ?  OR NextGrcPftRvwDate = ? ");
		query.append(" OR NextGrcCpzDate = ? OR NextRepayDate = ? OR NextRepayPftDate = ? ");
		query.append(" OR NextRepayRvwDate = ? OR NextDepDate = ?  OR NextRepayCpzDate = ? ) ");
		query.append(" AND MaturityDate >= ? ");
		query.append(" AND (FinIsActive = 1 AND COALESCE(ClosingStatus, ' ') <> 'C') and CustID = ?");
		return query.toString();

	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void process(Connection connection, long custId, Date date) throws Exception {

		Date valueDate = DateUtility.addDays(date, -1);

		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";
		try {
			sqlStatement = connection.prepareStatement(prepareSelectQuery(valueDate));
			sqlStatement.setDate(1, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(2, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(3, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(4, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(5, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(6, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(7, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(8, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setDate(9, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setLong(10, custId);
			resultSet = sqlStatement.executeQuery();
		

			while (resultSet.next()) {

				finreference = resultSet.getString("FinReference");
				boolean allowGrcPeriod = resultSet.getBoolean("AllowGrcPeriod");
				boolean allowGrcPftRvw = resultSet.getBoolean("AllowGrcPftRvw");
				boolean allowGrcCpz = resultSet.getBoolean("AllowGrcCpz");
				boolean finRepayPftOnFrq = resultSet.getBoolean("FinRepayPftOnFrq");

				// Reset Dates to incoming Date
				String finRef = resultSet.getString("FinReference");
				Date graceEndDate = resultSet.getDate("GrcPeriodEndDate");
				Date maturityDate = resultSet.getDate("MaturityDate");
				Date dateNextRepay = resultSet.getDate("NextRepayDate");
				Date dateNextRepayPft = resultSet.getDate("NextRepayPftDate");
				Date dateNextRepayRvw = resultSet.getDate("NextRepayRvwDate");
				Date dateNextRepayCpz = resultSet.getDate("NextRepayCpzDate");
				Date dateLastRepay = resultSet.getDate("LastRepayDate");
				Date dateLastRepayPft = resultSet.getDate("LastRepayPftDate");
				Date dateLastRepayRvw = resultSet.getDate("LastRepayRvwDate");
				Date dateLastRepayCpz = resultSet.getDate("LastRepayCpzDate");
				Date dateNextDepDate = resultSet.getDate("NextDepDate");
				Date dateNextGrcPftDate = resultSet.getDate("NextGrcPftDate");
				Date dateNextGrcPftRvwDate = resultSet.getDate("NextGrcPftRvwDate");
				Date dateNextGrcCpzDate = resultSet.getDate("NextGrcCpzDate");
				Date NextRepayDate = resultSet.getDate("NextRepayDate");
				Date NextRepayPftDate = resultSet.getDate("NextRepayPftDate");
				Date NextRepayRvwDate = resultSet.getDate("NextRepayRvwDate");
				Date NextRepayCpzDate = resultSet.getDate("NextRepayCpzDate");
				Date NextDepDate = resultSet.getDate("NextDepDate");

				String depreciationFrq = resultSet.getString("DepreciationFrq");

				// Update FinanceMain
				StringBuilder updateSql = new StringBuilder("UPDATE FinanceMain SET");

				// If grace is allowed
				if (allowGrcPeriod && DateUtility.compare(graceEndDate, valueDate) > 0) {

					if (dateNextGrcPftDate != null && (DateUtility.compare(dateNextGrcPftDate, valueDate) == 0)) {

						dateLastRepayPft = dateNextGrcPftDate;
						dateNextGrcPftDate = getNextSchDate(connection, finRef, dateLastRepayPft, "PftOnSchDate");
						if (dateNextGrcPftDate == null) {
							dateNextGrcPftDate = graceEndDate;
						}
						updateSql.append(" NextGrcPftDate = '");
						updateSql.append(dateNextGrcPftDate);
						updateSql.append("',");
					}

					if (allowGrcPftRvw) {

						if (dateNextGrcPftRvwDate != null
								&& (DateUtility.compare(dateNextGrcPftRvwDate, valueDate) == 0)) {

							dateLastRepayRvw = dateNextGrcPftRvwDate;
							dateNextGrcPftRvwDate = getNextSchDate(connection, finRef, dateLastRepayPft, "RvwOnSchDate");
							if (dateNextGrcPftRvwDate == null) {
								dateNextGrcPftRvwDate = graceEndDate;
							}
							updateSql.append(" NextGrcPftRvwDate = '");
							updateSql.append(dateNextGrcPftRvwDate);
							updateSql.append("',");
						}
					}

					if (allowGrcCpz) {

						if (dateNextGrcCpzDate != null && (DateUtility.compare(dateNextGrcCpzDate, valueDate) == 0)) {

							dateLastRepayCpz = dateNextGrcCpzDate;
							dateNextGrcCpzDate = getNextSchDate(connection, finRef, dateLastRepayCpz, "CpzOnSchDate");
							if (dateNextGrcCpzDate == null) {
								dateNextGrcCpzDate = graceEndDate;
							}
							updateSql.append(" NextGrcCpzDate = '");
							updateSql.append(dateNextGrcCpzDate);
							updateSql.append("',");
						}
					}
				}

				// REPAYMENT PERIOD
				if (DateUtility.compare(maturityDate, valueDate) > 0) {

					if (dateNextGrcPftDate != null && (DateUtility.compare(dateNextGrcPftDate, valueDate) == 0)) {

						dateLastRepay = dateLastRepayPft;
						dateNextRepay = getNextSchDate(connection, finRef, dateLastRepay, "RepayOnSchDate");
						if (dateNextRepay == null) {
							dateNextRepay = maturityDate;
						}
						updateSql.append(" NextRepayDate = '");
						updateSql.append(dateNextRepay);
						updateSql.append("',");

					} else if (NextRepayDate != null && (DateUtility.compare(NextRepayDate, valueDate) == 0)) {

						dateLastRepay = dateNextRepay;
						if (finRepayPftOnFrq) {
							dateNextRepay = getNextRpyPftSchDate(connection, finRef, dateLastRepay, "RepayOnSchDate");
						} else {
							dateNextRepay = getNextSchDate(connection, finRef, dateLastRepay, "RepayOnSchDate");
						}
						if (dateNextRepay == null) {
							dateNextRepay = maturityDate;
						}
						updateSql.append(" NextRepayDate = '");
						updateSql.append(dateNextRepay);
						updateSql.append("',");
					}

					if (NextRepayPftDate != null && (DateUtility.compare(NextRepayPftDate, valueDate) == 0)) {

						dateLastRepayPft = dateNextRepayPft;
						dateNextRepayPft = getNextSchDate(connection, finRef, dateLastRepayPft, "PftOnSchDate");
						if (dateNextRepayPft == null) {
							dateNextRepayPft = maturityDate;
						}
						updateSql.append(" NextRepayPftDate = '");
						updateSql.append(dateNextRepayPft);
						updateSql.append("',");
					}

					if (NextRepayRvwDate != null && (DateUtility.compare(NextRepayRvwDate, valueDate) == 0)) {

						dateLastRepayRvw = dateNextRepayRvw;
						dateNextRepayRvw = getNextSchDate(connection, finRef, dateLastRepayRvw, "RvwOnSchDate");
						if (dateNextRepayRvw == null) {
							dateNextRepayRvw = maturityDate;
						}
						updateSql.append(" NextRepayRvwDate = '");
						updateSql.append(dateNextRepayRvw);
						updateSql.append("',");
					}

					if (NextRepayCpzDate != null && (DateUtility.compare(NextRepayCpzDate, valueDate) == 0)) {

						dateLastRepayCpz = dateNextRepayCpz;
						dateNextRepayCpz = getNextSchDate(connection, finRef, dateLastRepayCpz, "CpzOnSchDate");
						if (dateNextRepayCpz == null) {
							dateNextRepayCpz = maturityDate;
						}
						updateSql.append(" NextRepayCpzDate = '");
						updateSql.append(dateNextRepayCpz);
						updateSql.append("',");
					}
				}

				//Depreciation Date
				if (StringUtils.isNotBlank(depreciationFrq) && (DateUtility.compare(NextDepDate, valueDate) == 0)) {

					dateNextDepDate = FrequencyUtil.getNextDate(depreciationFrq, 1, valueDate, "A", false)
							.getNextFrequencyDate();

					dateNextDepDate = DateUtility.getDBDate(DateUtility.formatUtilDate(dateNextDepDate,
							PennantConstants.DBDateFormat));
					updateSql.append(" NextDepDate = '");
					updateSql.append(dateNextDepDate);
					updateSql.append("',");
				}

				updateSql.append(" LastRepayDate = '");
				updateSql.append(dateLastRepay);
				updateSql.append("',");
				updateSql.append(" LastRepayPftDate = '");
				updateSql.append(dateLastRepayPft);
				updateSql.append("',");
				updateSql.append(" LastRepayRvwDate = '");
				updateSql.append(dateLastRepayRvw);
				updateSql.append("',");
				updateSql.append(" LastRepayCpzDate = '");
				updateSql.append(dateLastRepayCpz);
				updateSql.append("'");
				updateSql.append(" WHERE FinReference= '");
				updateSql.append(finRef);
				updateSql.append("'");
				//Update Next Dates
				PreparedStatement statement = connection.prepareStatement(updateSql.toString());
				statement.executeUpdate();

			}
		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference :" + finreference,  e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	/**
	 * Method to get next schedule date from FinanceScheduleDetails.
	 * 
	 * @param nextDate
	 * @param finReference
	 * @param con
	 * @return
	 */
	private Date getNextSchDate(Connection con, String finReference, Date dateParam, String columnName)
			throws SQLException {

		Date nextDate = null;
		StringBuilder selDateQuery = new StringBuilder(
				" SELECT SchDate FROM (SELECT SchDate, row_number() over (order by SchDate) row_num ");
		selDateQuery.append(" from FinScheduleDetails WHERE FinReference = '");
		selDateQuery.append(finReference);
		selDateQuery.append("' ");
		selDateQuery.append(" AND ");
		selDateQuery.append(columnName.trim());
		selDateQuery.append(" = 1 AND SchDate > '");
		selDateQuery.append(dateParam);
		selDateQuery.append("' )T WHERE row_num <= 1 ");
		PreparedStatement dateSqlStmt = con.prepareStatement(selDateQuery.toString());
		ResultSet dateResultSet = dateSqlStmt.executeQuery();
		while (dateResultSet.next()) {
			nextDate = DateUtility.getDBDate(dateResultSet.getString("SchDate"));
			break;
		}

		dateSqlStmt.close();
		dateResultSet.close();
		return nextDate;

	}

	/**
	 * Method for Setting Repay Date on Repayment Period for Different Profit and Repayment Frequencies.
	 * 
	 * @param con
	 * @param finReference
	 * @param dateParam
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	private Date getNextRpyPftSchDate(Connection con, String finReference, Date dateParam, String columnName)
			throws SQLException {

		Date nextDate = null;
		StringBuilder selDateQuery = new StringBuilder(
				"SELECT SchDate FROM (SELECT SchDate, row_number() over (order by SchDate) row_num ");
		selDateQuery.append(" FROM FinScheduleDetails WHERE FinReference = '");
		selDateQuery.append(finReference);
		selDateQuery.append("' ");
		selDateQuery.append(" AND (");
		selDateQuery.append(columnName.trim());
		selDateQuery.append(" = 1 OR (PftOnSchDate = 1 AND RepayAmount > 0)) ");
		selDateQuery.append("  AND SchDate > '");
		selDateQuery.append(dateParam);
		selDateQuery.append("' )T where row_num <= 1 ");
		PreparedStatement dateSqlStmt = con.prepareStatement(selDateQuery.toString());
		ResultSet dateResultSet = dateSqlStmt.executeQuery();
		while (dateResultSet.next()) {
			nextDate = DateUtility.getDBDate(dateResultSet.getString("SchDate"));
			break;
		}

		dateSqlStmt.close();
		dateResultSet.close();
		return nextDate;

	}

}
