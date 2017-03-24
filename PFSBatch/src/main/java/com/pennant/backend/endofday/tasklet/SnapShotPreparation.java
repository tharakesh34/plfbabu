package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.core.SnapshotService;
import com.pennant.app.util.DateUtility;

public class SnapShotPreparation implements Tasklet {
	private Logger logger = Logger.getLogger(SnapShotPreparation.class);
	
	private DataSource dataSource;
	private SnapshotService snapshotService;
	
	private Date dateAppDate = null;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {	
		
		dateAppDate = DateUtility.getAppDate();
		
		logger.debug("START: SnapShot Details for Value Date: "+  dateAppDate);		

		try {
			
			getSnapshotService().doSnapshotPreparation(dateAppDate);
			
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}

		return RepeatStatus.FINISHED;
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

	public SnapshotService getSnapshotService() {
		return snapshotService;
	}

	public void setSnapshotService(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}
	
}
