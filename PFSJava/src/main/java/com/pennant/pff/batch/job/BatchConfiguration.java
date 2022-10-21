package com.pennant.pff.batch.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.transaction.PlatformTransactionManager;

public class BatchConfiguration extends DefaultBatchConfigurer {
	protected static final Log logger = LogFactory.getLog(BatchConfiguration.class.getClass());

	protected DataSource dataSource;
	protected PlatformTransactionManager transactionManager;
	private JobOperator jobOperator;

	private String tablePrefix;

	public BatchConfiguration(String tablePrefix) throws Exception {
		super();
		this.tablePrefix = tablePrefix;
	}

	public JobInstance getLastJobInstance(String jobName) {
		return getJobExplorer().getLastJobInstance(jobName);
	}

	public JobExecution getLastJobExecution(JobInstance jobInstance) {
		return getJobExplorer().getLastJobExecution(jobInstance);
	}

	public Set<JobExecution> findRunningJobExecutions(String jobName) {
		return getJobExplorer().findRunningJobExecutions(jobName);
	}

	public List<StepExecution> getStepExecutions(JobInstance jobInstance) throws Exception {
		List<StepExecution> list = new ArrayList<>();

		List<JobExecution> jobExecutions = getJobExplorer().getJobExecutions(jobInstance);

		jobExecutions.stream().forEach(je -> list.addAll(je.getStepExecutions()));

		Map<String, StepExecution> distinctSteps = new HashMap<>();

		list.stream().forEach(se -> distinctSteps.put(se.getStepName(), se));

		return new ArrayList<StepExecution>(distinctSteps.values());
	}

	@Override
	protected JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Override
	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSerializer(serializer());
		factory.setTablePrefix(tablePrefix);
		factory.setTransactionManager(transactionManager);
		factory.afterPropertiesSet();
		factory.setLobHandler(lobHandler());

		return factory.getObject();
	}

	@Override
	protected JobExplorer createJobExplorer() throws Exception {
		JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
		factory.setDataSource(dataSource);
		factory.afterPropertiesSet();
		factory.setTablePrefix(tablePrefix);
		factory.setSerializer(serializer());
		return factory.getObject();
	}

	@Bean
	protected void jobOperator() throws Exception {
		SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
		simpleJobOperator.setJobLauncher(getJobLauncher());
		simpleJobOperator.setJobExplorer(getJobExplorer());
		simpleJobOperator.setJobRepository(getJobRepository());
		simpleJobOperator.setJobRegistry(getjobRegistry());

		this.jobOperator = simpleJobOperator;
	}

	@Override
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.transactionManager = new JdbcTransactionManager(dataSource);
	}

	public JobOperator getJobOperator() {
		return jobOperator;
	}

	@Bean
	public JobParametersIncrementer jobParametersIncrementer() {
		return new JobParametersIncrementer() {

			@Override
			public JobParameters getNext(JobParameters parameters) {
				if (parameters == null || parameters.isEmpty()) {
					return new JobParametersBuilder().addLong("run.id", 1L).toJobParameters();
				}

				Long id = parameters.getLong("run.id", 1L) + 1;

				return new JobParametersBuilder().addLong("run.id", id).toJobParameters();
			}

		};
	}

	private CustomSerializer serializer() {
		return new CustomSerializer();
	}

	private DefaultLobHandler lobHandler() {
		return new DefaultLobHandler();
	}

	private MapJobRegistry getjobRegistry() {
		return new MapJobRegistry();
	}
}
