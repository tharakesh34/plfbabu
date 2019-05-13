package com.pennant.backend.endofday.tasklet;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.process.collection.CollectionDataDownloadProcess;

public class CollectionDataDownload implements Tasklet {
	private Logger logger = Logger.getLogger(CollectionDataDownload.class);
	
	@Autowired(required=false)
	CollectionDataDownloadProcess process;
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);
		if(process!=null){
			int count = process.processDownload();
			logger.debug("Total Number of OD Records :"+count);
		}else{
			logger.debug("CollectionDataDownloadProcess Not Configured ");
		}
		
		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}
}
