package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.dao.impl.SuccessResponseJobQueueDAOImpl;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ClearQueueTasklet;
import com.pennant.pff.presentment.tasklet.SuccessResponsePartitioner;
import com.pennant.pff.presentment.tasklet.SuccessResponseTasklet;
import com.pennanttech.pennapps.core.AppException;

@Configuration
@EnableBatchProcessing(modular = true)
public class SuccessResponseJob extends BatchConfiguration {

	public SuccessResponseJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "PRMT_", "PRMT_SUCCESS_RESPONSE");
	}

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private PresentmentEngine presentmentEngine;

	private DataSourceTransactionManager transactionManager;

	@Autowired
	private BatchJobQueueDAO bjqDAO;

	public Job job;

	@Scheduled(cron = "*/5 * * * * *")
	public void perform() throws Exception {

		int totalRecords = presentmentDAO.getRecordsByWaiting("S");

		long batchID = 0;
		if (totalRecords > 0) {
			batchID = presentmentDAO.createBatch("RESPONSE_SUCCESS");
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setBatchId(batchID);

			totalRecords = bjqDAO.prepareQueue(jobQueue);
		}

		if (totalRecords == 0) {
			return;
		}

		JobParameters jobParameters = new JobParametersBuilder().addLong("BATCH_ID", batchID).toJobParameters();

		try {
			start(jobParameters);
		} catch (Exception e) {
			presentmentDAO.clearQueue(batchID);
			throw new AppException("Presentment succes response job failed", e);
		}
	}

	@Bean
	public Job peSuccessJob() throws Exception {
		this.job = this.jobBuilderFactory.get("peSuccessJob")

				.incrementer(jobParametersIncrementer())

				.start(masterStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();

		return this.job;
	}

	@Bean
	public Step masterStep() throws Exception {
		SuccessResponsePartitioner extractionPartition = new SuccessResponsePartitioner(bjqDAO);
		return stepBuilderFactory.get("SUCCESS_RESPONSE_MASTER").partitioner(masterTasklet())
				.partitioner("extractionStep", extractionPartition).listener(extractionPartition).build();
	}

	@Bean
	public TaskletStep masterTasklet() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("SUCCESS_RESPONS")

				.tasklet(new SuccessResponseTasklet(bjqDAO, presentmentEngine, transactionManager))

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("SUCCESS_RESPONS_"))

				.allowStartIfComplete(true)

				.build();
	}

	@Bean
	public TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	@Bean
	public ClearQueueTasklet clearQueueTasklet() {
		return new ClearQueueTasklet(presentmentDAO, bjqDAO);
	}

	@Bean
	public BatchJobQueueDAO bjqDAO() {
		return new SuccessResponseJobQueueDAOImpl(dataSource);
	}

	private SimpleAsyncTaskExecutor taskExecutor(String threadNamePrefix) {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor(threadNamePrefix);
		taskExecutor.setConcurrencyLimit(1);
		return taskExecutor;
	}

	@Override
	public Job getJob() {
		return this.job;
	}

}
