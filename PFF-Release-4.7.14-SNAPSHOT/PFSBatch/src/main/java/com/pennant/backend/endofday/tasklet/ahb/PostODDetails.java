package com.pennant.backend.endofday.tasklet.ahb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.ext.ExtTablesDAO;
import com.pennant.backend.util.BatchUtil;

public class PostODDetails implements Tasklet {

	private Logger logger = Logger.getLogger(PostODDetails.class);

	private DataSource 		dataSource;
	private ExtTablesDAO 	extTablesDAO;

	private int count = 0;

	public PostODDetails() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		logger.debug("Entering");

		Date appDate = DateUtility.getAppDate();

		logger.debug("START: Overdue Accounts for Value Date: " + appDate);

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		List<String> odAccList = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			// delete the records from the table Everyday
			getExtTablesDAO().deleteODAccDetails();

			sqlStatement = connection.prepareStatement(getCountQuery());

			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			resultSet = sqlStatement.executeQuery();
			odAccList = new ArrayList<String>();

			while (resultSet.next()) {

				// process primary accounts
				String repayAccNum = resultSet.getString("RepayAccountId");
				processODAccDetails(odAccList, repayAccNum);

				// Process secondary accounts
				String scndAccNum = resultSet.getString("AccountNumber");
				processODAccDetails(odAccList, scndAccNum);

				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(resultSet.getRow()));
			}
		} catch (SQLException e) {
			logger.error("Finrefernce :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
			odAccList = null;
		}
		logger.debug("COMPLETE: Overdue Accounts for Value Date: " + appDate);
		return RepeatStatus.FINISHED;
	}


	/**
	 * Method for process OD Account details and save 
	 * 
	 * @param finReference
	 * @param finOdSchDate
	 * @param repayAccNum
	 */
	private void processODAccDetails(List<String> odAccList, String accountNum) {
		logger.debug("Entering");

		if(!StringUtils.isBlank(accountNum)) {
			if(!odAccList.contains(accountNum)) {
				odAccList.add(accountNum);

				getExtTablesDAO().saveODAccDetails(count, accountNum);
				count++;
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for prepare SQL query to fetch allocated commodity inventories for cancellation
	 * 
	 */
	private String prepareSelectQuery() {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(" SELECT FM.RepayAccountId, SA.AccountNumber FROM FinODDetails FOD INNER JOIN  FinanceMain FM ");
		selectQuery.append(" ON FOD.FinReference=FM.FinReference LEFT JOIN SecondaryAccounts SA ON SA.FinReference = FOD.FinReference");
		selectQuery.append(" WHERE FinCurODAmt != 0 GROUP BY FM.RepayAccountId, SA.AccountNumber");
		return selectQuery.toString();
	}

	/**
	 * Method for get total record count query
	 * 
	 * @return
	 */
	private String getCountQuery() {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(" SELECT count(*) from (SELECT FM.RepayAccountId, SA.AccountNumber FROM FinODDetails FOD INNER JOIN  FinanceMain FM");
		selectQuery.append(" ON FOD.FinReference=FM.FinReference Left Join SecondaryAccounts SA ON SA.FinReference = FOD.FinReference");
		selectQuery.append(" WHERE FinCurODAmt != 0 GROUP BY FM.RepayAccountId, SA.AccountNumber) temp");
		return selectQuery.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ExtTablesDAO getExtTablesDAO() {
		return extTablesDAO;
	}

	public void setExtTablesDAO(ExtTablesDAO extTablesDAO) {
		this.extTablesDAO = extTablesDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
