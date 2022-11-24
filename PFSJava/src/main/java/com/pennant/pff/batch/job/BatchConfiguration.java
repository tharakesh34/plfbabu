package com.pennant.pff.batch.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.transaction.PlatformTransactionManager;

import com.pennant.pff.batch.job.model.BatchJob;
import com.pennant.pff.batch.job.model.StepDetail;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;

public abstract class BatchConfiguration implements BatchConfigurer {
	protected static final Log logger = LogFactory.getLog(BatchConfiguration.class.getClass());

	protected DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private String tablePrefix;
	private String threadNamePrefix;
	private CustomSerializer serializer;
	private DefaultLobHandler lobHandler;
	private JobRepository jobRepository;
	private JobLauncher jobLauncher;
	private JobExplorer jobExplorer;
	private MapJobRegistry jobRegistry;
	private JobOperator jobOperator;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	public BatchConfiguration(DataSource dataSource, String tablePrefix, String threadNamePrefix) throws Exception {
		this.dataSource = dataSource;
		this.tablePrefix = tablePrefix;
		this.threadNamePrefix = threadNamePrefix;
	}

	public abstract Job getJob();

	@Override
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSerializer(serializer);
		factory.setTablePrefix(tablePrefix);
		factory.setTransactionManager(getTransactionManager());
		factory.setValidateTransactionState(false);
		factory.setLobHandler(lobHandler);
		factory.afterPropertiesSet();

		return this.jobRepository = factory.getObject();

	}

	@Override
	public PlatformTransactionManager getTransactionManager() throws Exception {
		if (transactionManager == null) {
			transactionManager = new DataSourceTransactionManager(this.dataSource);
		}

		return transactionManager;
	}

	@Override
	@Bean
	public JobLauncher getJobLauncher() throws Exception {
		SimpleJobLauncher simpleLobLauncher = new SimpleJobLauncher();
		simpleLobLauncher.setJobRepository(jobRepository);
		simpleLobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor(threadNamePrefix));
		simpleLobLauncher.afterPropertiesSet();

		return this.jobLauncher = simpleLobLauncher;
	}

	@Override
	@Bean
	public JobExplorer getJobExplorer() throws Exception {
		JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTablePrefix(tablePrefix);
		factory.setSerializer(serializer);
		factory.afterPropertiesSet();

		return this.jobExplorer = factory.getObject();

	}

	@Bean
	public CustomSerializer serializer() {
		return this.serializer = new CustomSerializer();
	}

	@Bean
	public DefaultLobHandler lobHandler() {
		return this.lobHandler = new DefaultLobHandler();
	}

	@Bean
	public MapJobRegistry jobRegistry() {
		return this.jobRegistry = new MapJobRegistry();
	}

	@Bean
	public JobOperator jobOperator() throws Exception {
		SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
		simpleJobOperator.setJobLauncher(jobLauncher);
		simpleJobOperator.setJobExplorer(jobExplorer);
		simpleJobOperator.setJobRepository(jobRepository);
		simpleJobOperator.setJobRegistry(jobRegistry);
		simpleJobOperator.afterPropertiesSet();

		return simpleJobOperator;
	}

	@Bean
	public JobParametersIncrementer jobParametersIncrementer() {
		return new JobParametersIncrementer() {

			@Override
			public JobParameters getNext(JobParameters parameters) {
				if (parameters == null || parameters.isEmpty()) {
					return new JobParametersBuilder().addLong("run.id", 1L).toJobParameters();
				}

				Long id = parameters.getLong("run.id", Long.valueOf("1")) + 1;

				return new JobParametersBuilder().addLong("run.id", id).toJobParameters();
			}

		};
	}

	protected SimpleAsyncTaskExecutor taskExecutor(String threadNamePrefix) {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor(threadNamePrefix);
		taskExecutor.setConcurrencyLimit(1);
		return taskExecutor;
	}

	public void start(JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		logger.info(Literal.ENTERING);

		jobLauncher.run(getJob(), jobParameters);

		logger.info(Literal.LEAVING);
	}

	public void restart(long executionId) throws Exception {
		try {
			jobRegistry.getJob(getJob().getName());
		} catch (NoSuchJobException e) {
			jobRegistry.register(new ReferenceJobFactory(getJob()));
		}

		try {
			jobOperator.restart(executionId);
		} catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException
				| JobRestartException | JobParametersInvalidException e) {
			logger.error(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public JobInstance getLastJobInstance(String jobName) {
		return jobExplorer.getLastJobInstance(jobName);
	}

	public JobExecution getLastJobExecution(JobInstance jobInstance) {
		return jobExplorer.getLastJobExecution(jobInstance);
	}

	public Set<JobExecution> findRunningJobExecutions(String jobName) {
		return jobExplorer.findRunningJobExecutions(jobName);
	}

	public List<StepExecution> getStepExecutions(JobInstance jobInstance) throws Exception {
		Map<String, StepExecution> stepExecutions = new HashMap<>();

		// Get the job executions for the instance, recent at the top.
		for (JobExecution jobExecution : jobExplorer.getJobExecutions(jobInstance)) {
			for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
				if (!stepExecutions.containsKey(stepExecution.getStepName())) {
					stepExecutions.put(stepExecution.getStepName(), stepExecution);
				}
			}
		}

		return new ArrayList<StepExecution>(stepExecutions.values());
	}

	public void setRunningJobExecutionDetails(BatchJob eodJob) throws Exception {
		setJobExecutionDetails(eodJob, getLastJobInstance(eodJob.getJobName()));
	}

	public void setJobExecutionDetails(BatchJob eodJob) throws Exception {
		JobInstance lastJobInstance = getLastJobInstance(eodJob.getJobName());

		if (lastJobInstance != null) {
			setJobExecutionDetails(eodJob, lastJobInstance);
		}
	}

	private void setJobExecutionDetails(BatchJob eodJob, JobInstance jobInstance) throws Exception {
		JobExecution jobExecution = getLastJobExecution(jobInstance);

		BatchStatus batchStatus = jobExecution.getStatus();
		ExitStatus exitStatus = jobExecution.getExitStatus();

		eodJob.setJobName(jobInstance.getJobName());
		eodJob.setJobInstanceId(jobExecution.getJobId());
		eodJob.setJobExecutionId(jobExecution.getId());
		eodJob.setStartTime(jobExecution.getStartTime());
		eodJob.setEndTime(jobExecution.getEndTime());

		eodJob.setStatus(batchStatus.getBatchStatus().name());

		eodJob.setExitCode(exitStatus.getExitCode());
		eodJob.setExitDescription(exitStatus.getExitDescription());

		List<StepExecution> stepExecutions = getStepExecutions(jobInstance);

		for (StepExecution stepExecution : stepExecutions) {
			StepDetail sd = new StepDetail();
			sd.setReference(stepExecution.getStepName());
			sd.setStartTime(stepExecution.getStartTime());
			sd.setEndTime(stepExecution.getEndTime());
			sd.setExitCode(stepExecution.getExitStatus().getExitCode());
			sd.setExitDescription(stepExecution.getExitStatus().getExitDescription());

			ExecutionContext executionContext = stepExecution.getExecutionContext();
			DataEngineStatus des = (DataEngineStatus) executionContext.get("STATUS");

			if (des != null) {
				sd.setTotalRecords(des.getTotalRecords());
				sd.setProcessedRecords(des.getProcessedRecords());
				sd.setSuccessRecords(des.getSuccessRecords());
				sd.setFailedRecords(des.getFailedRecords());
			} else {
				sd.setTotalRecords(getTotal(executionContext));
				sd.setProcessedRecords(getProcessed(executionContext));
				sd.setSuccessRecords(getSuccess(executionContext));
				sd.setFailedRecords(getFailed(executionContext));
			}

			eodJob.getSteps().add(sd);
		}
	}

	private int getTotal(ExecutionContext context) {
		return context.getInt("TOTAL", 0);
	}

	private int getProcessed(ExecutionContext context) {
		return context.getInt("PROCESSED", 0);
	}

	private int getSuccess(ExecutionContext context) {
		return context.getInt("SUCCESS", 0);
	}

	private int getFailed(ExecutionContext context) {
		return context.getInt("FAILED", 0);
	}

}
