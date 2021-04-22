package com.pennant.backend.endofday.tasklet.ahb;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.PrepareMailData;
import com.pennanttech.pff.eod.EODUtil;

public class SendMail implements Tasklet {
	private Logger logger = LogManager.getLogger(SendMail.class);

	private DataSource dataSource;
	PrepareMailData prepareMailData;

	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date appDate = EODUtil.getDate("APP_DATE", context);

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
