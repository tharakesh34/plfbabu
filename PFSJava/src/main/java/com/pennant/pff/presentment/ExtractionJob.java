package com.pennant.pff.presentment;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.dao.impl.ExtractionJobQueueDAOImpl;
import com.pennant.pff.presentment.istener.ApprovalStepListener;
import com.pennant.pff.presentment.istener.ExtractionStepListener;
import com.pennant.pff.presentment.istener.PresentmentJobListener;
import com.pennant.pff.presentment.partitioner.ApprovalPartitioner;
import com.pennant.pff.presentment.partitioner.ExtractionPartitioner;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennant.pff.presentment.tasklet.ApprovalQueueTasklet;
import com.pennant.pff.presentment.tasklet.ApprovalTasklet;
import com.pennant.pff.presentment.tasklet.CreateBatchesTasklet;
import com.pennant.pff.presentment.tasklet.ExtractionClearTasklet;
import com.pennant.pff.presentment.tasklet.ExtractionQueueTasklet;
import com.pennant.pff.presentment.tasklet.ExtractionTasklet;
import com.pennant.pff.presentment.tasklet.GroupingByIncludeTasklet;
import com.pennant.pff.presentment.tasklet.GroupingTasklet;

@Configuration
public class ExtractionJob extends BatchConfiguration {

	public ExtractionJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "PRMNT_", "PRMNT_EXTRACTION");

		initilizeVariables();
	}

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private EventPropertiesService eventPropertiesService;

	private BatchJobQueueDAO ebjqDAO;

	@Bean
	public Job peExtractionJob() throws Exception {
		this.job = this.jobBuilderFactory.get("peExtractionJob")

				.listener(presentmentJobListener())

				.incrementer(jobParametersIncrementer())

				.start(groupingStep()).on("FAILED").fail()

				.next(extractionQueueStep()).on("FAILED").fail()

				.next(extractionMasterStep()).on("FAILED").fail()

				.next(groupingByIncludeStep()).on("FAILED").fail()

				.next(approvalQueueStep()).on("FAILED").fail()

				.next(approvalMasterStep()).on("FAILED").fail()

				.next(createBatchesStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();

		return this.job;
	}

	private PresentmentJobListener presentmentJobListener() {
		return new PresentmentJobListener(presentmentDAO);
	}

	private TaskletStep groupingStep() {
		return this.stepBuilderFactory.get("GROUPING").tasklet(new GroupingTasklet(dataSource, presentmentEngine))
				.build();
	}

	private TaskletStep extractionQueueStep() {
		return this.stepBuilderFactory.get("EXTRACTION_QUIENG").tasklet(new ExtractionQueueTasklet(ebjqDAO)).build();
	}

	private Step extractionMasterStep() throws Exception {
		ExtractionPartitioner partitioner = new ExtractionPartitioner(ebjqDAO);
		return stepBuilderFactory.get("EXTRACTION_MASTER")

				.partitioner(extractionStep())

				.listener(partitioner)

				.partitioner("extractionStep", partitioner)

				.listener(new ExtractionStepListener(presentmentDAO)).build();
	}

	private Step groupingByIncludeStep() {
		return this.stepBuilderFactory.get("GROUPING_INCLUDE").tasklet(new GroupingByIncludeTasklet(presentmentEngine))
				.build();
	}

	private TaskletStep extractionStep() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("EXTRACTION")

				.tasklet(new ExtractionTasklet(ebjqDAO, presentmentEngine, transactionManager, presentmentDAO))

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("PRESENTMENT_EXTRACTION_"))

				.allowStartIfComplete(true)

				.build();
	}

	private TaskletStep approvalQueueStep() {
		return this.stepBuilderFactory.get("APPROVAL_QUIENG").tasklet(new ApprovalQueueTasklet(ebjqDAO, presentmentDAO))
				.build();
	}

	private Step approvalMasterStep() throws Exception {
		ApprovalPartitioner partitioner = new ApprovalPartitioner(ebjqDAO);
		return stepBuilderFactory.get("APPROVAL_MASTER")

				.partitioner(approvalStep())

				.listener(partitioner)

				.partitioner("approvalStep", partitioner)

				.listener(new ApprovalStepListener(presentmentDAO)).build();
	}

	private TaskletStep approvalStep() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		ApprovalTasklet tasklet = new ApprovalTasklet(ebjqDAO, presentmentEngine, presentmentDAO, transactionManager);
		tasklet.setEventPropertiesService(eventPropertiesService);
		return this.stepBuilderFactory

				.get("APPROVAL")

				.tasklet(tasklet)

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("PRESENTMENT_APPROVAL_"))

				.allowStartIfComplete(true)

				.build();
	}

	private TaskletStep createBatchesStep() {
		return this.stepBuilderFactory.get("BATCH_CREATION").tasklet(new CreateBatchesTasklet(presentmentEngine))
				.build();
	}

	private TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	private ExtractionClearTasklet clearQueueTasklet() {
		return new ExtractionClearTasklet(presentmentDAO, ebjqDAO);
	}

	private void initilizeVariables() {
		this.ebjqDAO = new ExtractionJobQueueDAOImpl(dataSource);
	}

}
