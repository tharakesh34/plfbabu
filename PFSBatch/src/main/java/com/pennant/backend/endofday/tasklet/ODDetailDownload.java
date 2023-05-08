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

public class ODDetailDownload implements Tasklet {
	private Logger logger = LogManager.getLogger(ODDetailDownload.class);

	private DataSource dataSource;

	private Date dateValueDate = null;
	private Date dateAppDate = null;
	private ExecutionContext stepExecutionContext;

	public ODDetailDownload() {
	    super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = EODUtil.getDate("APP_VALUEDATE", context);
		dateAppDate = EODUtil.getDate("APP_DATE", context);

		logger.debug("START: Overdue Details for Report as Value Date: " + DateUtil.addDays(dateValueDate, -1));

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;

		try {

			// Daily Download of Overdue Details
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareUpdateQuery());
			sqlStatement.setString(1, dateAppDate.toString());
			sqlStatement.executeUpdate();

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: Overdue Details for Report as Value Date: " + DateUtil.addDays(dateValueDate, -1));
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Update Query To update
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareUpdateQuery() {

		StringBuilder updateQuery = new StringBuilder(" Insert into FinODDetails_SnapShot ");
		updateQuery.append(
				" SELECT ?, FinReference, FinODSchdDate,FinODFor,FinBranch,FinType,CustID,FinODTillDate,FinCurODAmt, ");
		updateQuery.append(
				" FinCurODPri,FinCurODPft,FinMaxODAmt,FinMaxODPri,FinMaxODPft,GraceDays,IncGraceDays,FinCurODDays, ");
		updateQuery.append(
				" TotPenaltyAmt,TotWaived,TotPenaltyPaid,TotPenaltyBal,FinLMdfDate,TotPftAmt,TotPftPaid,TotPftBal ");
		updateQuery.append(" FROM FinODDetails ");
		updateQuery.append(" where FinCurODAmt !=0 ");
		return updateQuery.toString();
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
