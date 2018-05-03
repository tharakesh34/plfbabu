package com.pennant.backend.endofday.tasklet.ahb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.CollateralService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class CollateralDeMarkPostings implements Tasklet {
	private Logger logger = Logger.getLogger(CollateralDeMarkPostings.class);


	private DataSource dataSource;
	private CollateralService	collateralService;

	public CollateralDeMarkPostings() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context) throws Exception {
		int processed = 0;

		Date valueDate = DateUtility.getAppValueDate();

		logger.debug("START: Collateral Demarking Postings for Value Date: " + valueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setBoolean(1, false);
			sqlStatement.setString(2, EodConstants.FIN_CLOSESTS);
			sqlStatement.setString(3, EodConstants.COLLT_MARKSTS);
			sqlStatement.setString(4, EodConstants.COLLT_DEMARKSTS);
			sqlStatement.setDate(5, DateUtility.getDBDate(valueDate.toString()));

			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setBoolean(1, false);
			sqlStatement.setString(2, EodConstants.FIN_CLOSESTS);
			sqlStatement.setString(3, EodConstants.COLLT_MARKSTS);
			sqlStatement.setString(4, EodConstants.COLLT_DEMARKSTS);
			sqlStatement.setDate(5, DateUtility.getDBDate(valueDate.toString()));

			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				try {
					getCollateralService().doCollateralDemarking(resultSet);
				} catch (InterfaceException e) {
					logger.error("Exception: ", e);
					continue;
				}

				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: Collateral Demarking for Value Date: " + valueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for get total record count query
	 * 
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder();
		selQuery.append(" SELECT COUNT(T1.FinReference) FROM FinCollaterals T1 ");
		selQuery.append(" INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selQuery.append(" INNER JOIN CollateralMarkLog T3 ON T1.FinReference = T3.FinReference");
		selQuery.append(" INNER JOIN FinPftDetails T4 ON T1.FinReference = T4.FinReference");
		selQuery.append(" WHERE T2.FinIsActive = ? AND T2.ClosingStatus = ? AND T3.Status = ? ");
		selQuery.append(" AND T3.Status <> ? AND T4.FullPaidDate = ? ");

		return selQuery.toString();
	}

	/**
	 * Method for prepare SQL query to fetch Collateral details for Matured finances
	 * 
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder();
		selQuery.append(" SELECT T1.FinReference, T1.Reference, T1.Value, T1.Remarks FROM FinCollaterals T1 ");
		selQuery.append(" INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selQuery.append(" INNER JOIN CollateralMarkLog T3 ON T1.FinReference = T3.FinReference");
		selQuery.append(" INNER JOIN FinPftDetails T4 ON T1.FinReference = T4.FinReference");
		selQuery.append(" WHERE T2.FinIsActive = ? AND T2.ClosingStatus = ? AND T3.Status = ? ");
		selQuery.append(" AND T3.Status <> ? AND T4.FullPaidDate = ? ");

		return selQuery.toString();
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public CollateralService getCollateralService() {
		return collateralService;
	}

	public void setCollateralService(CollateralService collateralService) {
		this.collateralService = collateralService;
	}
}
