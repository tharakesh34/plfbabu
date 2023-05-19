package com.pennant.pff.letter.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.pennant.pff.batch.job.BatchConfiguration;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.letter.AutoLetterGenerationTasklet;
import com.pennant.pff.letter.GLResponseClearTasklet;
import com.pennant.pff.letter.LetterGenerationJobQueueDAOImpl;
import com.pennant.pff.letter.LetterGenerationtJobListener;
import com.pennant.pff.letter.UpdateGLResponseTasklet;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.letter.partitioner.LetterGeneratePartitioner;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

@Configuration
public class LetterGenerationJob extends BatchConfiguration {

	public LetterGenerationJob(@Autowired DataSource dataSource) throws Exception {
		super(dataSource, "BATCH", "LETTER_GENERATION");

		initilizeVariables();
	}

	@Autowired
	private AutoLetterGenerationDAO letterGenerationDAO;

	private BatchJobQueueDAO bjqDAO;

	public void start(long batchID) {
		JobParametersBuilder builder = new JobParametersBuilder();

		builder.addLong("BATCH_ID", batchID);
		JobParameters jobParameters = builder.toJobParameters();

		try {
			start(jobParameters);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e.getMessage());
			throw new AppException("LGEN", e);
		}
	}

	@Bean
	public Job peSuccessJob() throws Exception {
		super.job = this.jobBuilderFactory.get("peSuccessJob")

				.listener(new LetterGenerationtJobListener(letterGenerationDAO))

				.incrementer(jobParametersIncrementer())

				.start(masterStep()).on("FAILED").fail()

				.next(updateHeaderStep()).on("FAILED").fail()

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

	private Step updateHeaderStep() throws Exception {
		return this.stepBuilderFactory.get("UPDATE_HEADER").tasklet(new UpdateGLResponseTasklet(letterGenerationDAO))
				.build();

	}

	private TaskletStep masterTasklet() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehaviorName("PROPAGATION_NEVER");

		return this.stepBuilderFactory

				.get("LETTER_GENERATION")

				.tasklet(new AutoLetterGenerationTasklet(bjqDAO, transactionManager))

				.transactionAttribute(attribute)

				.taskExecutor(taskExecutor("SUCCESS_RESPONSE_"))

				.build();
	}

	private TaskletStep clear() {
		return this.stepBuilderFactory.get("CLEAR").tasklet(clearQueueTasklet()).build();
	}

	private GLResponseClearTasklet clearQueueTasklet() {
		return new GLResponseClearTasklet(letterGenerationDAO, bjqDAO);
	}

	private void initilizeVariables() {
		this.bjqDAO = new LetterGenerationJobQueueDAOImpl(dataSource);
	}

}
