/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  DateRollOver.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.endofday.tasklet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.util.PennantConstants;

public class DateRollOver implements Tasklet {
	private Logger				logger			= Logger.getLogger(DateRollOver.class);

	private DataSource			dataSource;

	// Date Fields used in update
	private Date				dateValueDate	= null;

	private ExecutionContext	jobExecutionContext;
	private ExecutionContext	stepExecutionContext;

	public DateRollOver() {
		//
	}

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getValueDate();

		logger.debug("START: Date Rollover for Value Date: " + DateUtility.addDays(dateValueDate, -1));

		jobExecutionContext = context.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();

		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Date valueDate = DateUtility.addDays(dateValueDate, -1);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareSelectQuery(valueDate));
			resultSet = sqlStatement.executeQuery();

			String finReference = null;
			Date graceEndDate = null;
			Date maturityDate = null;
			Date dateNextGrcPft = null;
			Date dateNextGrcPftRvw = null;
			Date dateNextGrcCpz = null;
			Date dateNextRepay = null;
			Date dateNextRepayPft = null;
			Date dateNextRepayRvw = null;
			Date dateNextRepayCpz = null;
			Date dateLastRepay = null;
			Date dateLastRepayPft = null;
			Date dateLastRepayRvw = null;
			Date dateLastRepayCpz = null;
			Date dateNextDepDate = null;

			while (resultSet.next()) {

				// Reset Dates to incoming Date
				finReference = resultSet.getString("FinReference");
				graceEndDate = DateUtility.getDBDate(resultSet.getString("GrcPeriodEndDate"));
				maturityDate = DateUtility.getDBDate(resultSet.getString("MaturityDate"));
				dateNextRepay = DateUtility.getDBDate(resultSet.getString("NextRepayDate"));
				dateNextRepayPft = DateUtility.getDBDate(resultSet.getString("NextRepayPftDate"));
				dateNextRepayRvw = DateUtility.getDBDate(resultSet.getString("NextRepayRvwDate"));
				dateNextRepayCpz = DateUtility.getDBDate(resultSet.getString("NextRepayCpzDate"));
				dateLastRepay = DateUtility.getDBDate(resultSet.getString("LastRepayDate"));
				dateLastRepayPft = DateUtility.getDBDate(resultSet.getString("LastRepayPftDate"));
				dateLastRepayRvw = DateUtility.getDBDate(resultSet.getString("LastRepayRvwDate"));
				dateLastRepayCpz = DateUtility.getDBDate(resultSet.getString("LastRepayCpzDate"));
				dateNextDepDate = DateUtility.getDBDate(resultSet.getString("NextDepDate"));

				// Update FinanceMain
				StringBuilder updateSql = new StringBuilder("UPDATE FinanceMain SET");

				// If grace is allowed
				if (resultSet.getBoolean("AllowGrcPeriod") && DateUtility.compare(graceEndDate, valueDate) > 0) {

					if (resultSet.getString("NextGrcPftDate") != null
							&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextGrcPftDate")),
									valueDate) == 0)) {

						dateNextGrcPft = DateUtility.getDBDate(resultSet.getString("NextGrcPftDate"));
						dateLastRepayPft = dateNextGrcPft;
						dateNextGrcPft = getNextSchDate(connection, finReference, dateLastRepayPft, "PftOnSchDate");
						if (dateNextGrcPft == null) {
							dateNextGrcPft = graceEndDate;
						}
						updateSql.append(" NextGrcPftDate = '");
						updateSql.append(dateNextGrcPft);
						updateSql.append("',");
					}

					if (resultSet.getBoolean("AllowGrcPftRvw")) {
						dateNextGrcPftRvw = DateUtility.getDBDate(resultSet.getString("NextGrcPftRvwDate"));

						if (resultSet.getString("NextGrcPftRvwDate") != null
								&& (DateUtility.compare(
										DateUtility.getDBDate(resultSet.getString("NextGrcPftRvwDate")), valueDate) == 0)) {

							dateLastRepayRvw = dateNextGrcPftRvw;
							dateNextGrcPftRvw = getNextSchDate(connection, finReference, dateLastRepayPft,
									"RvwOnSchDate");
							if (dateNextGrcPftRvw == null) {
								dateNextGrcPftRvw = graceEndDate;
							}
							updateSql.append(" NextGrcPftRvwDate = '");
							updateSql.append(dateNextGrcPftRvw);
							updateSql.append("',");
						}
					}

					if (resultSet.getBoolean("AllowGrcCpz")) {
						dateNextGrcCpz = DateUtility.getDBDate(resultSet.getString("NextGrcCpzDate"));
						if (resultSet.getString("NextGrcCpzDate") != null
								&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextGrcCpzDate")),
										valueDate) == 0)) {

							dateLastRepayCpz = dateNextGrcCpz;
							dateNextGrcCpz = getNextSchDate(connection, finReference, dateLastRepayCpz, "CpzOnSchDate");
							if (dateNextGrcCpz == null) {
								dateNextGrcCpz = graceEndDate;
							}
							updateSql.append(" NextGrcCpzDate = '");
							updateSql.append(dateNextGrcCpz);
							updateSql.append("',");
						}
					}
				}

				// REPAYMENT PERIOD
				if (DateUtility.compare(maturityDate, valueDate) > 0) {

					if (resultSet.getString("NextGrcPftDate") != null
							&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextGrcPftDate")),
									valueDate) == 0)) {

						dateLastRepay = dateLastRepayPft;
						dateNextRepay = getNextSchDate(connection, finReference, dateLastRepay, "RepayOnSchDate");
						if (dateNextRepay == null) {
							dateNextRepay = maturityDate;
						}
						updateSql.append(" NextRepayDate = '");
						updateSql.append(dateNextRepay);
						updateSql.append("',");

					} else if (resultSet.getString("NextRepayDate") != null
							&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextRepayDate")),
									valueDate) == 0)) {

						dateLastRepay = dateNextRepay;
						if (resultSet.getBoolean("FinRepayPftOnFrq")) {
							dateNextRepay = getNextRpyPftSchDate(connection, finReference, dateLastRepay,
									"RepayOnSchDate");
						} else {
							dateNextRepay = getNextSchDate(connection, finReference, dateLastRepay, "RepayOnSchDate");
						}
						if (dateNextRepay == null) {
							dateNextRepay = maturityDate;
						}
						updateSql.append(" NextRepayDate = '");
						updateSql.append(dateNextRepay);
						updateSql.append("',");
					}

					if (resultSet.getString("NextRepayPftDate") != null
							&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextRepayPftDate")),
									valueDate) == 0)) {

						dateLastRepayPft = dateNextRepayPft;
						dateNextRepayPft = getNextSchDate(connection, finReference, dateLastRepayPft, "PftOnSchDate");
						if (dateNextRepayPft == null) {
							dateNextRepayPft = maturityDate;
						}
						updateSql.append(" NextRepayPftDate = '");
						updateSql.append(dateNextRepayPft);
						updateSql.append("',");
					}

					if (resultSet.getString("NextRepayRvwDate") != null
							&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextRepayRvwDate")),
									valueDate) == 0)) {

						dateLastRepayRvw = dateNextRepayRvw;
						dateNextRepayRvw = getNextSchDate(connection, finReference, dateLastRepayRvw, "RvwOnSchDate");
						if (dateNextRepayRvw == null) {
							dateNextRepayRvw = maturityDate;
						}
						updateSql.append(" NextRepayRvwDate = '");
						updateSql.append(dateNextRepayRvw);
						updateSql.append("',");
					}

					if (resultSet.getString("NextRepayCpzDate") != null
							&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextRepayCpzDate")),
									valueDate) == 0)) {

						dateLastRepayCpz = dateNextRepayCpz;
						dateNextRepayCpz = getNextSchDate(connection, finReference, dateLastRepayCpz, "CpzOnSchDate");
						if (dateNextRepayCpz == null) {
							dateNextRepayCpz = maturityDate;
						}
						updateSql.append(" NextRepayCpzDate = '");
						updateSql.append(dateNextRepayCpz);
						updateSql.append("',");
					}
				}

				//Depreciation Date
				if (StringUtils.isNotBlank(resultSet.getString("DepreciationFrq"))
						&& (DateUtility.compare(DateUtility.getDBDate(resultSet.getString("NextDepDate")), valueDate) == 0)) {

					dateNextDepDate = FrequencyUtil.getNextDate(resultSet.getString("DepreciationFrq"), 1, valueDate,
							"A", false).getNextFrequencyDate();

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
				updateSql.append(finReference);
				updateSql.append("'");
				//Update Next Dates
				PreparedStatement statement = null;

				try {

					statement = connection.prepareStatement(updateSql.toString());
					statement.executeUpdate();

					jobExecutionContext.putInt(context.getStepContext().getStepExecution().getStepName()
							+ "_FIELD_COUNT", resultSet.getRow());

				} catch (DataAccessException e) {
					logger.error("Exception: ", e);
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}

		} catch (SQLException e) {
			logger.error("Exception: ", e);
			throw new SQLException(e.getMessage()) {
			};
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: Date RollOver for Value Date: " + DateUtility.addDays(dateValueDate, -1));
		return RepeatStatus.FINISHED;

	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery(Date valueDate) {

		StringBuilder selQuery = new StringBuilder(
				" SELECT FinReference, CustID, FinBranch, GrcPftFrq, NextGrcPftDate, AllowGrcPeriod, ");
		selQuery.append(" FinRepayPftOnFrq, DepreciationFrq, NextDepDate, AllowGrcPftRvw, GrcPftRvwFrq, NextGrcPftRvwDate, ");
		selQuery.append(" AllowGrcCpz, GrcCpzFrq, AllowGrcRepay, NextGrcCpzDate, ");
		selQuery.append(" RepayFrq, NextRepayDate, NextRepayDate, NextRepayPftDate, ");
		selQuery.append(" AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, RepayCpzFrq, NextRepayCpzDate, ");
		selQuery.append(" FinType, FinCcy, GrcPeriodEndDate, LastRepayDate,");
		selQuery.append(" MaturityDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate ");
		selQuery.append(" FROM FinanceMain WHERE (NextGrcPftDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextGrcPftRvwDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextGrcCpzDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextRepayDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextRepayPftDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextRepayRvwDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextDepDate = '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" OR NextRepayCpzDate = '");
		selQuery.append(valueDate);
		selQuery.append("')");
		selQuery.append(" AND MaturityDate >= '");
		selQuery.append(valueDate);
		selQuery.append("'");
		selQuery.append(" AND (FinIsActive = 1 AND COALESCE(ClosingStatus, ' ') <> 'C') ");
		return selQuery.toString();

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
				" SELECT SchDate FROM (SELECT SchDate, row_number() over (order by SchDate) row_num ");
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
