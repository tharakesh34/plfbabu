package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pff.external.DataExportProcess;

public class CustomerDataExport implements Tasklet {
	private Logger logger = LogManager.getLogger(CustomerDataExport.class);

	@Autowired(required = false)
	private DataExportProcess dataExtractProcess;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		if (dataExtractProcess != null) {
			logger.info("Customer data exporting started");
			dataExtractProcess.export("CUSROMER");
			logger.info("Customer data exporting completed");
		}
		return RepeatStatus.FINISHED;
	}
}
