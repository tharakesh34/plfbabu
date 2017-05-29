package com.pennant.backend.endofday.tasklet.ahb;

import java.math.BigDecimal;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.service.ddapayments.impl.DDARepresentmentService;
import com.pennant.backend.util.FinanceConstants;

public class DDARepresentmentPostings implements Tasklet {
	private Logger logger = Logger.getLogger(DDARepresentmentPostings.class);

	private Date dateValueDate = null;

	private ExecutionContext 		jobExecutionContext;
	private ExecutionContext 		stepExecutionContext;

	private DataSource 				dataSource;
	private DDARepresentmentService ddaRepresentmentService;


	public DDARepresentmentPostings() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context) throws Exception {
		dateValueDate= DateUtility.getAppValueDate();

		logger.debug("START: DDA Representment Postings for Value Date: "+ dateValueDate);

		jobExecutionContext = context.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	

		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setString(1, FinanceConstants.REPAYMTH_AUTODDA);
			sqlStatement.setDate(2, new java.sql.Date(dateValueDate.getTime()));
			sqlStatement.setBigDecimal(3, BigDecimal.ZERO);

			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				DDAPayments ddaPayments = new DDAPayments();

				ddaPayments.setDirectDebitRefNo(resultSet.getString("DDAReferenceNo"));
				ddaPayments.setCustCIF(resultSet.getString("CustCIF"));
				ddaPayments.setFinReference(resultSet.getString("FinReference"));
				ddaPayments.setFinRepaymentAmount(resultSet.getBigDecimal("FinTotSchdPaid"));
				ddaPayments.setSchDate(resultSet.getDate("FinSchdDate"));

				// process DDARepresentment data
				getDdaRepresentmentService().representment(ddaPayments);
			}

			jobExecutionContext.putInt(context.getStepContext().getStepExecution().getStepName()+ "_FIELD_COUNT", 
					resultSet.getRow());

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

		logger.debug("COMPLETE: DDA Representment Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}


	/**
	 * Method for prepare SQL query to fetch DDA cancellation details for Matured finances
	 * 
	 */
	private String prepareSelectQuery() {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  R.FinReference, R.FinTotSchdPaid, F.DDAReferenceNo, C.CustCIF,");
		selectSql.append(" R.FinSchdDate from FinRepayDetails R INNER JOIN FinODDetails P ");
		selectSql.append(" ON R.FinReference = P.FinReference AND R.FinSchdDate = P.FinODSchdDate"); 
		selectSql.append(" AND R.FinRpyFor = P.FinODFor INNER JOIN FinanceMain F ");
		selectSql.append(" ON R.FinReference = F.FinReference  INNER JOIN Customers C ");
		selectSql.append(" ON C.CustCIF = F.CustID ");
		selectSql.append(" WHERE F.FinRepayMethod = ? AND FinValueDate = ? AND FinCurODAmt = ?");
		return selectSql.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DDARepresentmentService getDdaRepresentmentService() {
		return ddaRepresentmentService;
	}

	public void setDdaRepresentmentService(DDARepresentmentService ddaRepresentmentService) {
		this.ddaRepresentmentService = ddaRepresentmentService;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
