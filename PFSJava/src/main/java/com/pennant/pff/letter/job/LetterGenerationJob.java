package com.pennant.pff.letter.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.letter.LetterGenerationClearTasklet;
import com.pennant.pff.letter.LetterGenerationTasklet;
import com.pennant.pff.letter.dao.impl.LetterGenerationJobQueueDAOImpl;
import com.pennant.pff.letter.partitioner.LetterGeneratePartitioner;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class LetterGenerationJob extends BatchConfiguration {

	public LetterGenerationJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "BATCH", "LETTER_GENERATION_JOB");

		initilizeVariables();
	}

	private BatchJobQueueDAO bjqDAO;

	public void start(long batchID) {
		JobParametersBuilder builder = new JobParametersBuilder();

		builder.addLong("BATCH_ID", batchID);
		JobParameters jobParameters = builder.toJobParameters();

		bjqDAO.prepareQueue(null);

		try {
			start(jobParameters);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e.getMessage());
			throw new AppException("LETTER_GENERATION_JOB", e);
		}
	}

	@Bean
	public Job letterGenerationJob() throws Exception {
		super.job = this.jobBuilderFactory.get("letterGenerationJob")

				.incrementer(jobParametersIncrementer())

				.start(masterStep()).on("FAILED").fail()

				.next(clear()).on("*").end("COMPLETED").on("FAILED").fail()

				.end()

				.build();

		return super.job;
	}

	private Step masterStep() throws Exception {
		LetterGeneratePartitioner partitioner = new LetterGeneratePartitioner(bjqDAO);
		return stepBuilderFactory.get("LETTER_GENERATION_MASTER")

				.partitioner(masterTasklet())

				.partitioner("LETTER_GENERATION", partitioner)

				.listener(partitioner)

				.build();
	}

	private TaskletStep masterTasklet() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("LETTER_GENERATION")

				.tasklet(letterGenerationTasklet())

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("LETTER_GENERATION_"))

				.build();
	}

	private TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	private void initilizeVariables() {
		this.bjqDAO = new LetterGenerationJobQueueDAOImpl(dataSource);
	}

	private Tasklet letterGenerationTasklet() {
		return new LetterGenerationTasklet(this.bjqDAO);
	}

	private Tasklet clearQueueTasklet() {
		return new LetterGenerationClearTasklet(this.bjqDAO);
	}

}
