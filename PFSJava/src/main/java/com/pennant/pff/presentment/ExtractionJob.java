package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.dao.impl.PresentmentItemProcessor;
import com.pennant.pff.presentment.dao.impl.PresentmentItemReader;
import com.pennant.pff.presentment.dao.impl.PresentmentItemWriter;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ApprovalTasklet;
import com.pennant.pff.presentment.tasklet.ClearQueueTasklet;
import com.pennant.pff.presentment.tasklet.GroupingTasklet;
import com.pennant.pff.presentment.tasklet.PreparationTasklet;
import com.pennant.pff.presentment.tasklet.PresentmentDueConfigTasklet;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

@Configuration
public class ExtractionJob extends BatchConfiguration {

	public ExtractionJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "BATCH_");
	}

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private DueExtractionConfigService dueExtractionConfigService;

	@Autowired
	private DueExtractionConfigDAO dueExtractionConfigDAO;

	@Bean
	public Job peExtractionJob() throws Exception {
		return this.jobBuilderFactory.get("peExtractionJob")

				.incrementer(jobParametersIncrementer())

				// .start(dueConfigStep())

				// .next(preparationStep()).on("FAILED").fail()

				.start(groupingStep()).on("FAILED").fail()

				.next(extractionStep()).on("FAILED").fail()

				.next(approvalStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();
	}

	@Bean
	public PresentmentDueConfigTasklet dueConfigTasklet() {
		return new PresentmentDueConfigTasklet(dueExtractionConfigService, dueExtractionConfigDAO);
	}

	@Bean
	public PreparationTasklet preparationTasklet() {
		return new PreparationTasklet(presentmentEngine);
	}

	@Bean
	public GroupingTasklet groupingTasklet() {
		return new GroupingTasklet(presentmentEngine);
	}

	@Bean
	public ApprovalTasklet approvalTasklet() {
		return new ApprovalTasklet(presentmentEngine);
	}

	@Bean
	public ClearQueueTasklet clearQueueTasklet() {
		return new ClearQueueTasklet(presentmentEngine);
	}

	@Bean
	public TaskletStep dueConfigStep() {
		return this.stepBuilderFactory.get("CONFIGURATION").tasklet(dueConfigTasklet()).build();
	}

	@Bean
	public TaskletStep preparationStep() {
		return this.stepBuilderFactory.get("PREPARATION").tasklet(preparationTasklet()).build();
	}

	@Bean
	public TaskletStep groupingStep() {
		return this.stepBuilderFactory.get("GROUPING").tasklet(groupingTasklet()).build();
	}

	@Bean
	public TaskletStep approvalStep() {
		return this.stepBuilderFactory.get("APPROVAL").tasklet(approvalTasklet()).build();
	}

	@Bean
	public TaskletStep extractionStep() throws Exception {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return stepBuilderFactory.get("EXTRACTION").<PresentmentDetail, PresentmentDetail>chunk(10).reader(itemReader())
				.processor(new PresentmentItemProcessor(this.presentmentEngine))
				.writer(new PresentmentItemWriter(this.transactionManager, this.presentmentEngine))
				.taskExecutor(taskExecutor()).transactionAttribute(attribute).build();
	}

	@Bean
	public JdbcPagingItemReader<PresentmentDetail> itemReader() throws Exception {
		JdbcPagingItemReader<PresentmentDetail> itemReader = new PresentmentItemReader(this.dataSource).build();
		itemReader.afterPropertiesSet();
		return itemReader;
	}

	@Bean
	public TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	public SimpleAsyncTaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("PRESENTMENT_EXTRACTION_JOB");
		taskExecutor.setConcurrencyLimit(100);
		return taskExecutor;
	}

}
