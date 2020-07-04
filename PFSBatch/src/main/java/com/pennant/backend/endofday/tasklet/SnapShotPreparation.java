package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.SnapshotService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class SnapShotPreparation implements Tasklet {
	private Logger logger = LogManager.getLogger(SnapShotPreparation.class);

	private SnapshotService snapshotService;

	public SnapShotPreparation() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = SysParamUtil.getAppValueDate();
		valueDate = DateUtil.addDays(valueDate, -1);

		logger.info("START Snap Shot Preparation On {}", valueDate);

		int count = snapshotService.doSnapshotPreparation(valueDate);

		StepUtil.SNAPSHOT_PREPARATION.setTotalRecords(count);
		StepUtil.SNAPSHOT_PREPARATION.setProcessedRecords(count);
		BatchUtil.setExecutionStatus(context, StepUtil.SNAPSHOT_PREPARATION);

		logger.info("COMPLETE Prepare Customer Groups On {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setSnapshotService(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}

}
