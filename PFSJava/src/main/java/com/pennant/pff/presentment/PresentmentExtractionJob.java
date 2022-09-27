package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
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
import com.pennant.pff.presentment.tasklet.ClearQueueTasklet;
import com.pennant.pff.presentment.tasklet.GroupingTasklet;
import com.pennant.pff.presentment.tasklet.PreparationTasklet;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentExtractionJob {

	@Autowired
	private JobBuilderFactory peJobBuilderFactory;

	@Autowired
	private BatchJobParameterIncrementer peBatchJobParameterIncrementer;

	@Autowired
	private StepBuilderFactory peStepBuilderFactory;

	@Autowired
	private PreparationTasklet preparation;

	@Autowired
	private ClearQueueTasklet clearQueue;

	@Autowired
	private GroupingTasklet grouping;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private TransactionManager peTransactionManager;

	@Bean
	public Job peExtractionJob() {
		return this.peJobBuilderFactory.get("peExtractionJob").incrementer(peBatchJobParameterIncrementer)
				.start(preparation())

				.next(grouping()).on("FAILED").fail()

				.next(extraction()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();
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
	public TaskletStep extraction() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return peStepBuilderFactory.get("EXTRACTION").<PresentmentDetail, PresentmentDetail>chunk(10)
				.reader(new PresentmentItemReader(this.dataSource))
				.processor(new PresentmentItemProcessor(this.presentmentEngine))
				.writer(new PresentmentItemWriter(this.peTransactionManager, this.presentmentEngine))
				// .taskExecutor(taskExecutor())
				// .transactionAttribute(attribute)
				.build();
	}

	@Bean
	public TaskletStep clear() {
		return this.peStepBuilderFactory.get("CLEAR").tasklet(clearQueue).build();
	}

	public SimpleAsyncTaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("PRESENTMENT_EXTRACTION_JOB");
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;

	}

}
