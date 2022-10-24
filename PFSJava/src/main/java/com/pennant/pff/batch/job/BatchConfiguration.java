package com.pennant.pff.batch.job;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.transaction.PlatformTransactionManager;

public class BatchConfiguration {
	protected static final Log logger = LogFactory.getLog(BatchConfiguration.class.getClass());

	protected DataSource dataSource;
	protected PlatformTransactionManager transactionManager;
	protected String tablePrefix;
	protected JobRepository jobRepository;
	protected JobLauncher jobLauncher;
	protected JobExplorer jobExplorer;
	protected JobOperator jobOperator;
	protected CustomSerializer serializer;
	protected MapJobRegistry jobRegistry;
	protected DefaultLobHandler defaultLobHandler;
	protected StepBuilderFactory stepBuilderFactory;
	protected JobBuilderFactory jobBuilderFactory;
	protected JobParametersIncrementer jobParametersIncrementer;

	public BatchConfiguration() throws Exception {
		super();
	}

	public BatchConfiguration(DataSource dataSource, String tablePrefix) throws Exception {
		this.dataSource = dataSource;
		this.tablePrefix = tablePrefix;
		this.transactionManager = new DataSourceTransactionManager(this.dataSource);
	}

	@PostConstruct
	public void initilize() throws Exception {
		this.serializer = serializer();
		this.defaultLobHandler = defaultLobHandler();
		this.jobRegistry = jobRegistry();
		this.jobRepository = createJobRepository();
		this.jobLauncher = createJobLauncher();
		this.jobExplorer = createJobExplorer();
		this.jobOperator = jobOperator();
		this.jobBuilderFactory = new JobBuilderFactory(jobRepository);
		this.stepBuilderFactory = new StepBuilderFactory(jobRepository, transactionManager);
		this.jobParametersIncrementer = jobParametersIncrementer();
	}

	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	public CustomSerializer serializer() {
		return new CustomSerializer();
	}

	public DefaultLobHandler defaultLobHandler() {
		return new DefaultLobHandler();
	}

	public MapJobRegistry jobRegistry() {
		return new MapJobRegistry();
	}

	protected JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSerializer(serializer);
		factory.setTablePrefix(tablePrefix);
		factory.setTransactionManager(transactionManager);
		factory.setValidateTransactionState(false);
		factory.afterPropertiesSet();
		factory.setLobHandler(defaultLobHandler);

		return factory.getObject();
	}

	protected JobExplorer createJobExplorer() throws Exception {
		JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
		factory.setDataSource(dataSource);
		factory.afterPropertiesSet();
		factory.setTablePrefix(tablePrefix);
		factory.setSerializer(serializer);
		return factory.getObject();
	}

	public JobOperator jobOperator() throws Exception {
		SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
		simpleJobOperator.setJobLauncher(jobLauncher);
		simpleJobOperator.setJobExplorer(jobExplorer);
		simpleJobOperator.setJobRepository(jobRepository);
		simpleJobOperator.setJobRegistry(jobRegistry);

		return simpleJobOperator;
	}

	public JobParametersIncrementer jobParametersIncrementer() {
		return new JobParametersIncrementer() {

			@Override
			public JobParameters getNext(JobParameters parameters) {
				if (parameters == null || parameters.isEmpty()) {
					return new JobParametersBuilder().addLong("run.id", Long.valueOf("1")).toJobParameters();
				}

				Long id = parameters.getLong("run.id", Long.valueOf("1")) + 1;

				return new JobParametersBuilder().addLong("run.id", id).toJobParameters();
			}

		};
	}

}
