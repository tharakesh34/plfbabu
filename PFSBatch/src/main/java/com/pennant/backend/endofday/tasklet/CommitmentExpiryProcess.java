package com.pennant.backend.endofday.tasklet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;

public class CommitmentExpiryProcess implements Tasklet {
	private Logger logger = LogManager.getLogger(CommitmentExpiryProcess.class);

	private DataSource dataSource;

	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;

	public CommitmentExpiryProcess() {
		//
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.debug("START: Commitment Expiry Details for Value Date: " + DateUtil.addDays(dateValueDate, -1));

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;

		try {

			// Update of Commitment Details as per Expiry Date
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareUpdateQuery());
			sqlStatement.setString(1, DateUtil.addDays(dateValueDate, -1).toString());
			sqlStatement.executeUpdate();
			sqlStatement.close();

			// Update Non-Performing Status of Commitment
			try {
				sqlStatement = connection.prepareStatement(resetCmtNonPerformStatus());
				sqlStatement.executeUpdate();
				sqlStatement.close();

				sqlStatement = connection.prepareStatement(UpdateCmtNonPerformStatusQuery());
				sqlStatement.executeUpdate();
				sqlStatement.close();
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: Commitment Expiry Details for Value Date: " + DateUtil.addDays(dateValueDate, -1));
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Update Query To update
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareUpdateQuery() {

		StringBuilder updateQuery = new StringBuilder(" Update Commitments SET CmtAvailable = 0 ");
		updateQuery.append(" Where CmtExpDate = ?");
		return updateQuery.toString();

	}

	/**
	 * Method for preparation of Update Query To update
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String UpdateCmtNonPerformStatusQuery() {

		StringBuilder updateQuery = new StringBuilder(" Update Commitments SET NonPerforming = 1 ");
		updateQuery.append(" WHERE CmtReference IN( SELECT DISTINCT FinCommitmentRef from FinanceMain ");
		updateQuery.append(
				" where FinReference IN(SELECT FinReference from FinSuspHead where FinIsInSusp = 1) AND FinCommitmentRef != '')");
		return updateQuery.toString();

	}

	/**
	 * Method for preparation of Update Query To update
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String resetCmtNonPerformStatus() {
		return " Update Commitments SET NonPerforming = 0 ";
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
