package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.dao.impl.SuccessResponseJobQueueDAOImpl;
import com.pennant.pff.presentment.istener.PresentmentJobListener;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ClearQueueTasklet;
import com.pennant.pff.presentment.tasklet.SuccessResponsePartitioner;
import com.pennant.pff.presentment.tasklet.SuccessResponseTasklet;
import com.pennant.pff.presentment.tasklet.UpdateResponseTasklet;
import com.pennanttech.pennapps.core.AppException;

@Configuration
public class SuccessResponseJob extends BatchConfiguration {

	public SuccessResponseJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "PRMNT_", "PRMNT_SUCCESS_RESPONSE");
	}

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private PresentmentJobListener jobListener;

	@Autowired
	private EventPropertiesService eventPropertiesService;

	private BatchJobQueueDAO bjqDAO;

	@Bean
	public PresentmentJobListener jobListener() {
		return jobListener = new PresentmentJobListener(presentmentDAO);
	}

	@Bean
	public BatchJobQueueDAO bjqDAO() {
		return bjqDAO = new SuccessResponseJobQueueDAOImpl(dataSource);
	}

	@Scheduled(cron = "0 */1 * ? * *")
	public void perform() throws Exception {

		int totalRecords = presentmentDAO.getRecordsByWaiting("S");

		if (totalRecords == 0) {
			return;
		}

		long batchID = 0;
		bjqDAO.clearQueue();

		batchID = presentmentDAO.createBatch("RESPONSE_SUCCESS");
		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchID);

		totalRecords = bjqDAO.prepareQueue(jobQueue);

		if (totalRecords == 0) {
			return;
		}

		JobParameters jobParameters = new JobParametersBuilder().addLong("BATCH_ID", batchID).toJobParameters();

		try {
			start(jobParameters);
		} catch (Exception e) {
			bjqDAO.clearQueue();
			throw new AppException("Presentment succes response job failed", e);
		}
	}

	@Bean
	public Job peSuccessJob() throws Exception {
		super.job = this.jobBuilderFactory.get("peSuccessJob")

				.listener(jobListener)

				.incrementer(jobParametersIncrementer())

				.start(masterStep()).on("FAILED").fail()

				.next(updateHeaderStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();

		return super.job;
	}

	@Bean
	public Step masterStep() throws Exception {
		SuccessResponsePartitioner partitioner = new SuccessResponsePartitioner(bjqDAO);
		return stepBuilderFactory.get("SUCCESS_RESPONSE_MASTER")

				.partitioner(masterTasklet())

				.partitioner("successResponseStep", partitioner)

				.listener(partitioner)

				.build();
	}

	@Bean
	public Step updateHeaderStep() throws Exception {
		return this.stepBuilderFactory.get("UPDATE_HEADER").tasklet(new UpdateResponseTasklet(presentmentDAO)).build();

	}

	@Bean
	public TaskletStep masterTasklet() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("SUCCESS_RESPONS")

				.tasklet(new SuccessResponseTasklet(bjqDAO, presentmentEngine, transactionManager,
						eventPropertiesService))

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
}
