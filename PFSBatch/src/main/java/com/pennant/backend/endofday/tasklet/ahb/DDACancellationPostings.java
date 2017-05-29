package com.pennant.backend.endofday.tasklet.ahb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.dda.DDAProcessService;
import com.pennant.backend.util.BatchUtil;

public class DDACancellationPostings implements Tasklet {

	private Logger logger = Logger.getLogger(DDACancellationPostings.class);


	private DDAControllerService	ddaControllerService;
	private DDAProcessService	    ddaProcessService;
	

	private DataSource 				dataSource;
	

	public DDACancellationPostings() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context)
			throws Exception {
		Date	appDate = DateUtility.getAppDate();

		logger.debug("START: DDA Cancellation Postings for Value Date: "
				+ appDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finReference = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setString(1, "REGISTRATION");
			sqlStatement.setInt(2, 1);
			sqlStatement.setBoolean(3, false);
			sqlStatement.setString(4, "M");
			sqlStatement.setDate(5,
					DateUtility.getDBDate(appDate.toString()));

			resultSet = sqlStatement.executeQuery();
			resultSet.next();
			BatchUtil.setExecution(context, "TOTAL",
					String.valueOf(resultSet.getInt(1)));
			resultSet.close();
			sqlStatement.close();
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setString(1, "REGISTRATION");
			sqlStatement.setInt(2, 1);
			sqlStatement.setBoolean(3, false);
			sqlStatement.setString(4, "M");
			sqlStatement.setDate(5,
					DateUtility.getDBDate(appDate.toString()));
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {

				finReference = resultSet.getString("FinReference");
				if (finReference != null) {
					try {
						getDdaControllerService().cancelDDARegistration(finReference);
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}

			}

		} catch (SQLException e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: DDA Cancellation Postings for Value Date: "
				+ appDate);
		return RepeatStatus.FINISHED;
	}
	

	/**
	 * Method for prepare SQL query to fetch DDA cancellation details for Matured finances
	 * 
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder("SELECT T1.FinReference FROM FinanceMain T1");
		selQuery.append(" INNER JOIN DDAReferenceLog T2 ON T1.FinReference = T2.FinRefence ");
		selQuery.append(" INNER JOIN FinPftDetails T3 ON T1.FinReference = T3.FinReference");
		selQuery.append(" WHERE T1.DdaReferenceNo = T2.DdaReference AND T2.Purpose = ? AND T2.Active = ?");
		selQuery.append(" AND T1.FinIsActive = ? AND T1.ClosingStatus = ? AND T3.FullPaidDate = ? ");
		
		return selQuery.toString();

	}
	
	/**
	 * Method for get total record count query
	 * 
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder("SELECT COUNT(T1.FinReference) FROM FinanceMain T1");
		selQuery.append(" INNER JOIN DDAReferenceLog T2 ON T1.FinReference = T2.FinRefence ");
		selQuery.append(" INNER JOIN FinPftDetails T3 ON T1.FinReference = T3.FinReference");
		selQuery.append(" WHERE T1.DdaReferenceNo = T2.DdaReference AND T2.Purpose = ? AND T2.Active = ?");
		selQuery.append(" AND T1.FinIsActive = ? AND T1.ClosingStatus = ? AND T3.FullPaidDate = ? ");

		return selQuery.toString();
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}
	
	public DDAProcessService getDdaProcessService() {
		return ddaProcessService;
	}

	public void setDdaProcessService(DDAProcessService ddaProcessService) {
		this.ddaProcessService = ddaProcessService;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
