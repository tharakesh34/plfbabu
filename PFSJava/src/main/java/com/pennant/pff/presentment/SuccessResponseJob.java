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
import com.pennant.pff.presentment.partitioner.ResponsePartitioner;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ResponseClearTasklet;
import com.pennant.pff.presentment.tasklet.SuccessResponseTasklet;
import com.pennant.pff.presentment.tasklet.UpdateResponseTasklet;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

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
	private EventPropertiesService eventPropertiesService;

	private BatchJobQueueDAO bjqDAO;

	@Bean
	public BatchJobQueueDAO bjqDAO() {
		if (this.bjqDAO == null) {
			this.bjqDAO = new SuccessResponseJobQueueDAOImpl(dataSource);
		}

		return bjqDAO;

	}

	@Scheduled(cron = "0 */5 * ? * *")
	public void successResponseJob() throws Exception {
		logger.info("Presentment Success Response Job invoked at {}", DateUtil.getSysDate(DateFormat.LONG_DATE_TIME));

		if (bjqDAO.getQueueCount() > 0) {
			logger.info("Previous Job still in progress");
			return;
		}

		int totalRecords = presentmentDAO.getRecordsByWaiting("S");

		if (totalRecords == 0) {
			logger.info("There is no pending records to process");
			return;
		}

		long batchID = 0;
		bjqDAO.clearQueue();

		batchID = presentmentDAO.createBatch("RESPONSE_SUCCESS");
		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchID);

		totalRecords = bjqDAO.prepareQueue(jobQueue);

		if (totalRecords == 0) {
			logger.info("There is no pending records to process");
			return;
		}

		JobParameters jobParameters = new JobParametersBuilder()

				.addLong("BATCH_ID", batchID)

				.addString("RESPONSE_TYPE", "S").toJobParameters();

		logger.info("Presentment Success Response Job startred with BATCH_ID {}", batchID);

		try {
			start(jobParameters);
		} catch (Exception e) {
			bjqDAO.clearQueue();
			throw new AppException("Presentment Succes Response Job failed", e);
		}
	}

	@Bean
	public Job peSuccessJob() throws Exception {
		super.job = this.jobBuilderFactory.get("peSuccessJob")

				.listener(new PresentmentJobListener(presentmentDAO))

				.incrementer(jobParametersIncrementer())

				.start(masterStep()).on("FAILED").fail()

				.next(updateHeaderStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();

		return super.job;
	}

	public Step masterStep() throws Exception {
		ResponsePartitioner partitioner = new ResponsePartitioner(bjqDAO);
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

	public TaskletStep masterTasklet() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("SUCCESS_RESPONSE")

				.tasklet(new SuccessResponseTasklet(bjqDAO, presentmentEngine, transactionManager,
						eventPropertiesService))

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("SUCCESS_RESPONSE_"))

				.allowStartIfComplete(true)

				.build();
	}

	public TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	public ResponseClearTasklet clearQueueTasklet() {
		return new ResponseClearTasklet(presentmentDAO, bjqDAO);
	}
}
