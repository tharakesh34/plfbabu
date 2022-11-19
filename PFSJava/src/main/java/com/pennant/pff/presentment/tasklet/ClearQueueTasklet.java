package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;

public class ClearQueueTasklet implements Tasklet {

	private PresentmentDAO presentmentDAO;
	private BatchJobQueueDAO ebjqDAO;
	private BatchJobQueueDAO abjqDAO;

	public ClearQueueTasklet(PresentmentDAO presentmentDAO, BatchJobQueueDAO ebjqDAO, BatchJobQueueDAO abjqDAO) {
		super();
		this.presentmentDAO = presentmentDAO;
		this.ebjqDAO = ebjqDAO;
		this.abjqDAO = abjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		this.presentmentDAO.clearQueue(batchId);
		this.ebjqDAO.deleteQueue(jobQueue);
		this.abjqDAO.deleteQueue(jobQueue);

		return RepeatStatus.FINISHED;
	}

}
