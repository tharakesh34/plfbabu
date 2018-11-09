package com.pennant.backend.endofday.tasklet;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennanttech.pennapps.core.resource.Literal;

public class CollectionDataDownload  implements Tasklet {
	private Logger					logger	= Logger.getLogger(CollectionDataDownload.class);
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);
		
		
		
		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}
}
