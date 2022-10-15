package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.pff.batch.job.BatchJobParameterIncrementer;
import com.pennant.pff.presentment.dao.impl.PresentmentItemProcessor;
import com.pennant.pff.presentment.dao.impl.PresentmentItemReader;
import com.pennant.pff.presentment.dao.impl.PresentmentItemWriter;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ApprovalTasklet;
import com.pennant.pff.presentment.tasklet.ClearQueueTasklet;
import com.pennant.pff.presentment.tasklet.GroupingTasklet;
import com.pennant.pff.presentment.tasklet.PreparationTasklet;
import com.pennant.pff.presentment.tasklet.PresentmentDueConfigTasklet;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentExtractionJob {

	@Autowired
	private JobBuilderFactory peJobBuilderFactory;

	@Autowired
	private BatchJobParameterIncrementer peBatchJobParameterIncrementer;

	@Autowired
	private StepBuilderFactory peStepBuilderFactory;

	@Autowired
	private PresentmentDueConfigTasklet dueConfig;

	@Autowired
	private PreparationTasklet preparation;

	@Autowired
	private ClearQueueTasklet clearQueue;

	@Autowired
	private GroupingTasklet grouping;

	@Autowired
	private ApprovalTasklet approval;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private TransactionManager peTransactionManager;

	@Bean
	public Job peExtractionJob() throws Exception {
		return this.peJobBuilderFactory.get("peExtractionJob").incrementer(peBatchJobParameterIncrementer)
				.start(dueConfig())

				.next(preparation()).on("FAILED").fail()

				.next(grouping()).on("FAILED").fail()

				.next(extraction()).on("FAILED").fail()

				.next(approval()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();
	}

	@Bean
	private TaskletStep dueConfig() {
		return this.peStepBuilderFactory.get("CONFIGURATION").tasklet(dueConfig).build();
	}

	@Bean
	private TaskletStep preparation() {
		return this.peStepBuilderFactory.get("PREPARATION").tasklet(preparation).build();
	}

	@Bean
	private TaskletStep grouping() {
		return this.peStepBuilderFactory.get("GROUPING").tasklet(grouping).build();
	}

	@Bean
	private TaskletStep approval() {
		return this.peStepBuilderFactory.get("APPROVAL").tasklet(approval).build();
	}

	@Bean
	public TaskletStep extraction() throws Exception {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return peStepBuilderFactory.get("EXTRACTION").<PresentmentDetail, PresentmentDetail>chunk(1)
				.reader(itemReader()).processor(new PresentmentItemProcessor(this.presentmentEngine))
				.writer(new PresentmentItemWriter(this.peTransactionManager, this.presentmentEngine))
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
		return this.peStepBuilderFactory.get("CLEAR").tasklet(clearQueue).build();
	}

	public SimpleAsyncTaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("PRESENTMENT_EXTRACTION_JOB");
		taskExecutor.setConcurrencyLimit(100);
		return taskExecutor;

	}

}
