package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.dao.impl.ExtractionJobQueueDAOImpl;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ApprovalPartitioner;
import com.pennant.pff.presentment.tasklet.ApprovalQueueTasklet;
import com.pennant.pff.presentment.tasklet.ApprovalTasklet;
import com.pennant.pff.presentment.tasklet.ClearQueueTasklet;
import com.pennant.pff.presentment.tasklet.CreateBatchesTasklet;
import com.pennant.pff.presentment.tasklet.ExtractionPartitioner;
import com.pennant.pff.presentment.tasklet.ExtractionQueueTasklet;
import com.pennant.pff.presentment.tasklet.ExtractionTasklet;
import com.pennant.pff.presentment.tasklet.GroupingTasklet;

@Configuration
@EnableBatchProcessing(modular = true)
public class ExtractionJob extends BatchConfiguration {

	public ExtractionJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "PRMT_", "PRESENTMENT_EXTRACTION");
	}

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private DataSourceTransactionManager transactionManager;

	@Autowired
	private BatchJobQueueDAO extractionBatchJobQueueDAO;

	@Autowired
	private BatchJobQueueDAO approvalBatchJobQueueDAO;

	@Autowired
	private EventPropertiesService eventPropertiesService;

	public Job job;

	@Bean
	public Job peExtractionJob() throws Exception {
		this.job = this.jobBuilderFactory.get("peExtractionJob")

				.incrementer(jobParametersIncrementer())

				.start(groupingStep()).on("FAILED").fail()

				.next(extractionQueueStep()).on("FAILED").fail()

				.next(extractionMasterStep()).on("FAILED").fail()

				.next(approvalQueueStep()).on("FAILED").fail()

				.next(approvalMasterStep()).on("FAILED").fail()

				.next(createBatchesStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();

		return this.job;
	}

	@Bean
	public TaskletStep groupingStep() {
		return this.stepBuilderFactory.get("GROUPING").tasklet(new GroupingTasklet(presentmentEngine)).build();
	}

	@Bean
	public TaskletStep extractionQueueStep() {
		return this.stepBuilderFactory.get("EXTRACTION_QUIENG")
				.tasklet(new ExtractionQueueTasklet(extractionBatchJobQueueDAO)).build();
	}

	@Bean
	public Step extractionMasterStep() throws Exception {
		ExtractionPartitioner extractionPartition = new ExtractionPartitioner(extractionBatchJobQueueDAO);
		return stepBuilderFactory.get("EXTRACTION_MASTER").partitioner(extractionStep())
				.partitioner("extractionStep", extractionPartition).listener(extractionPartition).build();
	}

	@Bean
	public TaskletStep extractionStep() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("EXTRACTION")

				.tasklet(new ExtractionTasklet(extractionBatchJobQueueDAO, presentmentEngine, transactionManager))

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("PRESENTMENT_EXTRACTION_"))

				.allowStartIfComplete(true)

				.build();
	}

	@Bean
	public TaskletStep approvalQueueStep() {
		return this.stepBuilderFactory.get("APPROVAL_QUIENG")
				.tasklet(new ApprovalQueueTasklet(approvalBatchJobQueueDAO)).build();
	}

	@Bean
	public Step approvalMasterStep() throws Exception {
		ApprovalPartitioner approvalPartition = new ApprovalPartitioner(approvalBatchJobQueueDAO);
		return stepBuilderFactory.get("APPROVAL_MASTER").partitioner(approvalStep())
				.partitioner("approvalStep", approvalPartition).listener(approvalPartition).build();
	}

	@Bean
	public TaskletStep approvalStep() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		ApprovalTasklet tasklet = new ApprovalTasklet(approvalBatchJobQueueDAO, presentmentEngine, transactionManager);
		tasklet.setEventPropertiesService(eventPropertiesService);
		return this.stepBuilderFactory

				.get("APPROVAL")

				.tasklet(tasklet)

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("PRESENTMENT_APPROVAL_"))

				.allowStartIfComplete(true)

				.build();
	}

	@Bean
	public TaskletStep createBatchesStep() {
		return this.stepBuilderFactory.get("BATCH_CREATION").tasklet(new CreateBatchesTasklet(presentmentEngine))
				.build();
	}

	@Bean
	public TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	@Bean
	public ClearQueueTasklet clearQueueTasklet() {
		return new ClearQueueTasklet(presentmentDAO, extractionBatchJobQueueDAO, approvalBatchJobQueueDAO);
	}

	@Bean
	public BatchJobQueueDAO extractionJobQueueDAO() {
		return new ExtractionJobQueueDAOImpl(dataSource);
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
