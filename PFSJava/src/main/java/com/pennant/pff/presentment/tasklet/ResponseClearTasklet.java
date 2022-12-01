package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;

public class ResponseClearTasklet implements Tasklet {

	private PresentmentDAO presentmentDAO;
	private BatchJobQueueDAO ebjqDAO;

	public ResponseClearTasklet(PresentmentDAO presentmentDAO, BatchJobQueueDAO ebjqDAO) {
		super();
		this.presentmentDAO = presentmentDAO;
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");
		String batchType = jobParameters.getString("BATCH_TYPE");

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		presentmentDAO.logRespDetail(batchId, batchType);
		presentmentDAO.clearRespDetail(batchId, batchType);
		ebjqDAO.clearQueue();

		return RepeatStatus.FINISHED;
	}

}
