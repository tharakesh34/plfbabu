package com.pennant.backend.endofday.tasklet.ahb;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PrepareMailData;

public class SendMail implements Tasklet {
	private Logger logger = Logger.getLogger(SendMail.class);

	private DataSource dataSource;
	PrepareMailData prepareMailData;

	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		Date appDate = DateUtility.getAppDate();

		logger.debug("START: Send Mail for Value Date: " + appDate);
		
		getPrepareMailData().processData("PDC", appDate);
						
		return RepeatStatus.FINISHED;
	}
	
	
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public PrepareMailData getPrepareMailData() {
		return prepareMailData;
	}
	public void setPrepareMailData(PrepareMailData prepareMailData) {
		this.prepareMailData = prepareMailData;
	}
	
	
}
