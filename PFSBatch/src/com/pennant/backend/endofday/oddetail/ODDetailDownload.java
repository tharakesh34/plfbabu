package com.pennant.backend.endofday.oddetail;

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

public class ODDetailDownload implements Tasklet {
	private Logger logger = Logger.getLogger(ODDetailDownload.class);
	
	private DataSource dataSource;
	
	private Date dateValueDate = null;
	private Date dateAppDate = null;
	private ExecutionContext stepExecutionContext;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {	
		
		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());

		logger.debug("START: Overdue Details for Report as Value Date: "+  DateUtility.addDays(dateValueDate,-1));		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;
		
		try {
			
			//Daily Download of Overdue Details
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareUpdateQuery());
			sqlStatement.setString(1, dateAppDate.toString());
			sqlStatement.executeUpdate();
		
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}finally {
			sqlStatement.close();
		}

		logger.debug("COMPLETE: Overdue Details for Report as Value Date: "+  DateUtility.addDays(dateValueDate,-1));
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for preparation of Update Query To update 
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareUpdateQuery() {
		
		StringBuilder updateQuery = new StringBuilder(" Insert INTO FinODDetails_DailyDownload Select * from RPT_FInODDetails_DailyDownload_View ");
		updateQuery.append(" Where CurrentDate = ?");
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
