package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennanttech.pff.eod.EODUtil;

public class Notification implements Tasklet {
	private Logger logger = LogManager.getLogger(Notification.class);

	private DataSource dataSource;

	public Notification() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context) throws Exception {
		Date dateValueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.debug("START: Notification for Value Date:{} ", dateValueDate);

		try {

		} finally {

		}

		logger.debug("COMPLETE:Notification for Value Date:{}", dateValueDate);
		return RepeatStatus.FINISHED;
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
}
