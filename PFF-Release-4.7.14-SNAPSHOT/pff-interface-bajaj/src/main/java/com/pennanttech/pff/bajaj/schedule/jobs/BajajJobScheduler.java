package com.pennanttech.pff.bajaj.schedule.jobs;

import java.text.ParseException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.scheduler.AbstractJobScheduler;
import com.pennanttech.pennapps.core.scheduler.Job;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.MandateProcess;
import com.pennanttech.pff.external.PresentmentProcess;

public class BajajJobScheduler extends AbstractJobScheduler {
	private static final Logger logger = Logger.getLogger(BajajJobScheduler.class);

	protected DataSource dataSource;
	private DataEngineConfig datEngine = null;
		
	@Autowired
	protected DataSource		finOneDataSource;
	
	@Autowired
	private MandateProcess mandateProcess;
	
	@Autowired
	private PresentmentProcess presementProcess;
	
	@Autowired
	private AbstractInterface abstractInterface;

	public BajajJobScheduler() {
		super();

		abstractInterface = (AbstractInterface)mandateProcess;
		abstractInterface = (AbstractInterface)presementProcess;

	}

	@Override
	public void registerJobs() throws ParseException {
		logger.debug(Literal.ENTERING);

		// autoDisbursementJob();

		impsDisbursementRespJob();
		posidexCustomerUpdateRespJob();
		masterExtractJob();
		customerCrudOpretion();
		
		
		

		logger.debug(Literal.LEAVING);
	}
	

	private void masterExtractJob() {
		logger.debug(Literal.ENTERING);

		List<Configuration> list = getConfigurations();

		for (Configuration config : list) {
			String name = config.getName();
			if (!name.startsWith("ME_") || config.getCronExpression() == null) {
				continue;
			}

			JobDataMap args = new JobDataMap();
			args.put("dataSource", dataSource);
			args.put("configuration", name);

			Job job = new Job();
			job.setJobDetail(
					JobBuilder.newJob(MasterExtractSchedulerJob.class).withIdentity(name, "MASTER_FILE_EXTRACT")
							.withDescription(config.getDescription()).setJobData(args).build());
			job.setTrigger(TriggerBuilder.newTrigger().withIdentity(name, "MASTER_FILE_EXTRACT")
					.withDescription(config.getDescription())
					.withSchedule(CronScheduleBuilder.cronSchedule(config.getCronExpression())).build());

			jobs.put("DE_" + name, job);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void autoDisbursementJob() {
		logger.debug(Literal.ENTERING);

		// Auto Disbrsement response file
		Configuration configuration = loadConfiguration("DISB_HDFC_IMPORT");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
				BajajInterfaceConstants.autoDisbFileLoaction = configuration.getUploadPath();
			} catch (Exception e) {
				logger.warn("Disbursement response file reading scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {

				Job job = new Job();
				job.setJobDetail(JobBuilder.newJob(DisbursementResponseJob.class)
						.withIdentity("AUTO_DISB_RES_JOB", "AUTO_DISB_RES_FILE")
						.withDescription("Auto Disbrsement response file job").build());

				job.setTrigger(
						TriggerBuilder.newTrigger().withIdentity("AUTO_DISB_RES_FILE_TRIGGER", "AUTO_DISB_RES_FILE")
								.withDescription("Auto disbursement response file trigger.")
								.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				jobs.put("AUTO_DISB_RES_FILE", job);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void impsDisbursementRespJob() {
		logger.debug(Literal.ENTERING);

		Configuration configuration = loadConfiguration("DISB_IMPS_RESPONSE");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
			} catch (Exception e) {
				logger.warn("Disbursement IMPS response scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {

				Job job = new Job();
				job.setJobDetail(JobBuilder.newJob(DisbrsementImpsResponseJob.class)
						.withIdentity("DISB_IMPS_RES_JOB", "DISB_IMPS_RESPONSE")
						.withDescription("Disbrsement imps response job").build());

				job.setTrigger(
						TriggerBuilder.newTrigger().withIdentity("DISB_IMPS_RES_JOB_TRIGGER", "DISB_IMPS_RESPONSE")
								.withDescription("Disbrsement imps response job trigger.")
								.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				jobs.put("DISB_IMPS_RES_JOB", job);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void posidexCustomerUpdateRespJob() {
		logger.debug(Literal.ENTERING);

		Configuration configuration = loadConfiguration("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
			} catch (Exception e) {
				logger.warn("Posidex customer update response scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {

				Job job = new Job();
				job.setJobDetail(JobBuilder.newJob(PosidexCustomerUpdateResponseJob.class)
						.withIdentity("POSIDEX_RES_JOB", "POSIDEX_CUSTOMER_RESPONSE")
						.withDescription("Posidex customer update response job trigger.").build());

				job.setTrigger(
						TriggerBuilder.newTrigger().withIdentity("POSIDEX_RES_JOB_TRIGGER", "POSIDEX_CUSTOMER_RESPONSE")
								.withDescription("Posidex customer update response job trigger.")
								.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				jobs.put("POSIDEX_RES_JOB", job);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void customerCrudOpretion() {
		logger.debug(Literal.ENTERING);

		Configuration configuration = loadConfiguration("CUSTOMER_CURD_OPERATIONS");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
			} catch (Exception e) {
				logger.warn("Customer Create Operation scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {

				Job job = new Job();
				job.setJobDetail(JobBuilder.newJob(CustomerCrudOperationJob.class)
						.withIdentity("CUSTOMER_CRUD_JOB", "CUSTOMER_CRUD_RESPONSE")
						.withDescription("Customer crud opretion job trigger.").build());

				job.setTrigger(
						TriggerBuilder.newTrigger().withIdentity("CUSTOMER_CRUD_JOB_TRIGGER", "CUSTOMER_CRUD_RESPONSE")
								.withDescription("Customer crud opretion job trigger.")
								.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				jobs.put("CUSTOMER_CRUD_JOB", job);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private Configuration loadConfiguration(String configName) {
		logger.debug(Literal.ENTERING);
		Configuration configuration = null;
		try {
			if (datEngine == null) {
				datEngine = new DataEngineConfig(dataSource);
			}
			configuration = datEngine.getConfigurationByName(configName);
		} catch (Exception e) {
			logger.warn("Data engine configuration details not avilable for " + configName);
		}
		logger.debug(Literal.LEAVING);
		return configuration;
	}

	private List<Configuration> getConfigurations() {
		logger.debug(Literal.ENTERING);
		List<Configuration> list = null;
		try {
			if (datEngine == null) {
				datEngine = new DataEngineConfig(dataSource);
			}

			list = datEngine.getConfigurationList();
		} catch (Exception e) {
			logger.warn("Data engine configuration details not avilable");
		}
		logger.debug(Literal.LEAVING);
		return list;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
