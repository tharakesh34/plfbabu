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
		this.job = super.jobBuilder("peExtractionJob")

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
		return taskletStep("GROUPING", new GroupingTasklet(dataSource, presentmentEngine));
	}

	private TaskletStep extractionQueueStep() {
		return taskletStep("EXTRACTION_QUIENG", new ExtractionQueueTasklet(ebjqDAO));
	}

	private Step extractionMasterStep() {
		ExtractionPartitioner partitioner = new ExtractionPartitioner(ebjqDAO);
		return stepBuilder("EXTRACTION_MASTER")

				.partitioner(extractionStep())

				.listener(partitioner)

				.partitioner("extractionStep", partitioner)

				.listener(new ExtractionStepListener(presentmentDAO)).build();
	}

	private Step groupingByIncludeStep() {
		return this.taskletStep("GROUPING_INCLUDE", new GroupingByIncludeTasklet(presentmentEngine));
	}

	private TaskletStep extractionStep() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return taskletStep("EXTRACTION",
				new ExtractionTasklet(ebjqDAO, presentmentEngine, transactionManager, presentmentDAO), attribute);

	}

	private TaskletStep approvalQueueStep() {
		return taskletStep("APPROVAL_QUIENG", new ApprovalQueueTasklet(ebjqDAO, presentmentDAO));
	}

	private Step approvalMasterStep() {
		ApprovalPartitioner partitioner = new ApprovalPartitioner(ebjqDAO);
		return stepBuilder("APPROVAL_MASTER")

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

		return taskletStep("APPROVAL", tasklet, attribute);
	}

	private TaskletStep createBatchesStep() {
		return this.taskletStep("BATCH_CREATION", new CreateBatchesTasklet(presentmentEngine));
	}

	private TaskletStep clear() {
		return this.taskletStep("CLEAR", clearQueueTasklet());
	}

	private ExtractionClearTasklet clearQueueTasklet() {
		return new ExtractionClearTasklet(presentmentDAO, ebjqDAO);
	}

	private void initilizeVariables() {
		this.ebjqDAO = new ExtractionJobQueueDAOImpl(dataSource);
	}

}
