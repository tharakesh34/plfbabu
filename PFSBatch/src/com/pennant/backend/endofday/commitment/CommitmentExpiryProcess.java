package com.pennant.backend.endofday.commitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.PennantConstants;

public class CommitmentExpiryProcess implements Tasklet {
	private Logger logger = Logger.getLogger(CommitmentExpiryProcess.class);
	
	private DataSource dataSource;
	
	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {	
		
		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());

		logger.debug("START: Commitment Expiry Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;
		
		try {
			
			//Update of Commitment Details as per Expiry Date
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareUpdateQuery());
			sqlStatement.setString(1, dateValueDate.toString());
			sqlStatement.executeUpdate();
			
			//Update Non-Performing Status of Commitment
			try {
				sqlStatement = connection.prepareStatement(resetCmtNonPerformStatus());
				sqlStatement.executeUpdate();
				
				sqlStatement = connection.prepareStatement(UpdateCmtNonPerformStatusQuery());
				sqlStatement.executeUpdate();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}finally {
			sqlStatement.close();
		}

		logger.debug("COMPLETE: Commitment Expiry Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		
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
		
		StringBuilder updateQuery = new StringBuilder(" Update Commitments SET NonPerforming = 1 " );
		updateQuery.append(" WHERE CmtReference IN( SELECT DISTINCT FinCommitmentRef from FinanceMain " );
		updateQuery.append(" where FinReference IN(SELECT FinReference from FinSuspHead where FinIsInSusp = 1) AND FinCommitmentRef != '')");
		return updateQuery.toString();
		
	}
	
	/**
	 * Method for preparation of Update Query To update 
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String resetCmtNonPerformStatus() {
		StringBuilder updateQuery = new StringBuilder(" Update Commitments SET NonPerforming = 0 " );
		return updateQuery.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	
}
