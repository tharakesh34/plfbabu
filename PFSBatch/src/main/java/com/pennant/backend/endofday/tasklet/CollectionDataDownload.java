package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.process.collection.CollectionDataDownloadProcess;

public class CollectionDataDownload implements Tasklet {
	private Logger logger = LogManager.getLogger(CollectionDataDownload.class);

	private CollectionDataDownloadProcess collectionDataDownloadProcess;
	private CollectionDataDownloadProcess customCollectionDataDownloadProcess;

	public CollectionDataDownload() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);
		BatchUtil.setExecutionStatus(chunkContext, StepUtil.COLLECTION_DOWNLOAD);
		collectionDataDownloadProcess.processDownload();

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setCollectionDataDownloadProcess(CollectionDataDownloadProcess collectionDataDownloadProcess) {
		this.collectionDataDownloadProcess = collectionDataDownloadProcess;
	}

	@Autowired(required = false)
	public void setCustomCollectionDataDownloadProcess(
			CollectionDataDownloadProcess customCollectionDataDownloadProcess) {
		this.customCollectionDataDownloadProcess = customCollectionDataDownloadProcess;
	}

	private CollectionDataDownloadProcess getCollectionDataDownloadProcess() {
		return customCollectionDataDownloadProcess == null ? collectionDataDownloadProcess
				: customCollectionDataDownloadProcess;
	}
}
