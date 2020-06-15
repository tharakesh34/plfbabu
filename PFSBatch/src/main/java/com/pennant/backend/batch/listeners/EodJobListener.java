package com.pennant.backend.batch.listeners;

import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pff.batch.backend.dao.BatchProcessStatusDAO;
import com.pennanttech.pff.batch.model.BatchProcessStatus;
import com.pennanttech.pff.eod.step.StepUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EodJobListener implements JobExecutionListener {
	private static final Logger logger = LogManager.getLogger(EodJobListener.class);

	private EODConfigDAO eODConfigDAO;
	private BatchProcessStatusDAO bpsDAO;

	public EodJobListener() {
		super();
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		logger.debug(Literal.ENTERING);

		EODConfig eodConfig = eODConfigDAO.getEODConfig().get(0);
		updateExecutionStatus(jobExecution);

		if (!eodConfig.isSendEmailRequired()) {
			logger.info("Sending mail is not enabled.");
			return;
		}

		if (eodConfig.getToEmailAddress() == null || eodConfig.getToEmailAddress() == "") {
			logger.info("To Email Address is Mandatory.");
			return;
		}

		String[] ccMailAddress = eodConfig.getCCEmailAddress().split(",");
		String[] toMailAddress = eodConfig.getToEmailAddress().split(",");

		JavaMailSenderImpl mailSender = generateMailSender(eodConfig);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(eodConfig.getFromEmailAddress());
			helper.setSentDate(DateUtil.getSysDate());
			helper.setTo(toMailAddress);
			helper.setCc(ccMailAddress);

			Date eodDate = SysParamUtil.getAppValueDate();

			for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
				if ("datesUpdate".equals(stepExecution.getStepName())) {
					if (BatchStatus.COMPLETED.name().equals(stepExecution.getExitStatus().getExitCode())) {
						eodDate = DateUtil.addDays(eodDate, -1);
						break;
					}
				}
			}

			String subject = null;
			String eodStatus = null;

			String srtValueDate = DateUtil.format(eodDate, DateFormat.LONG_DATE);

			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
				subject = "PLF EOD completed successfully for the value date " + srtValueDate;
				eodStatus = BatchStatus.COMPLETED.name();

				if (eodConfig.isAutoEodRequired()) {
					eodConfig.setEnableAutoEod(true);
				}
				if (eodConfig.isEODAutoDisable()) {
					eodConfig.setEnableAutoEod(false);
				}

				eODConfigDAO.updateEnableEOD(eodConfig);
			} else {
				subject = "PLF EOD Failed for the value date " + srtValueDate;
				eodStatus = BatchStatus.FAILED.name();

				setAttachement(jobExecution, helper);

				eodConfig.setEnableAutoEod(false);
				eODConfigDAO.updateEnableEOD(eodConfig);
			}

			helper.setSubject(subject);

			EODStatus eod = setEODStatus(subject, eodStatus, jobExecution);
			Configuration config = setConfiguration();

			String result = FreeMarkerTemplateUtils
					.processTemplateIntoString(config.getTemplate("eod_notification.html"), eod);

			helper.setText("", result);
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateExecutionStatus(JobExecution jobExecution) {
		BatchProcessStatus batchProcessStatus = new BatchProcessStatus();
		batchProcessStatus.setEndTime(jobExecution.getEndTime());
		batchProcessStatus.setName("PLF_EOD");

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			batchProcessStatus.setStatus("S");
		} else {
			batchProcessStatus.setStatus("F");
		}

		bpsDAO.updateBatchStatus(batchProcessStatus);
	}

	private void setAttachement(JobExecution jobExecution, MimeMessageHelper helper) throws MessagingException {
		StepExecution currentStepExecution = null;

		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			currentStepExecution = stepExecution;
		}

		if (currentStepExecution != null) {
			byte[] content = currentStepExecution.getExitStatus().getExitDescription().getBytes();
			ByteArrayDataSource is = new ByteArrayDataSource(content, "txt/plain");
			helper.addAttachment(currentStepExecution.getStepName() + ".log", is);
		}
	}

	private Configuration setConfiguration() {
		Configuration config = new Configuration();

		try {
			config.setClassForTemplateLoading(EodJobListener.class, "/");
			config.setDefaultEncoding("UTF-8");
			config.setLocale(Locale.getDefault());
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			String path = EodJobListener.class.getResource("/eod_notification.html").getFile();
			path = URLDecoder.decode(path, "UTF-8");

			FileTemplateLoader templateLoader = new FileTemplateLoader(new File(path).getParentFile(), true);

			config.setTemplateLoader(templateLoader);

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return config;
	}

	private EODStatus setEODStatus(String subject, String eodStatus, JobExecution jobExecution) {
		EODStatus eod = new EODStatus();

		Date startTime = jobExecution.getStartTime();
		Date endTime = jobExecution.getEndTime();

		try {
			eod.setSubject(subject);
			eod.setStatus(eodStatus);
			eod.setLastBusinessDate(DateUtil.format(SysParamUtil.getLastBusinessdate(), DateFormat.LONG_DATE));
			eod.setNextBusinessDate(DateUtil.format(SysParamUtil.getNextBusinessdate(), DateFormat.LONG_DATE));
			eod.setValueDate(DateUtil.format(SysParamUtil.getAppValueDate(), DateFormat.LONG_DATE));
			eod.setTotalCustomers(String.valueOf(StepUtil.PREPARE_CUSTOMER_QUEUE.getTotalRecords()));
			eod.setProcessedCustomers(String.valueOf(StepUtil.PREPARE_CUSTOMER_QUEUE.getProcessedRecords()));
			eod.setStartTime(DateUtil.format(startTime, DateFormat.LONG_TIME));
			eod.setEndTime(DateUtil.format(endTime, DateFormat.LONG_TIME));
			eod.setCompletedTime(DateUtility.timeBetween(endTime, startTime));
			long totalLoans = 0;
			for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
				if (stepExecution.getStepName().startsWith("microEOD")) {
					DataEngineStatus status = (DataEngineStatus) stepExecution.getExecutionContext()
							.get(stepExecution.getStepName());
					totalLoans = totalLoans + status.getTotalRecords();
				}
			}

			eod.setTotalLoans(String.valueOf(totalLoans));
			eod.setClient(License.getClientInfo().get("Client").replace("Client: ", ""));
			eod.setVersion(EodJobListener.class.getPackage().getImplementationVersion());
			eod.setEnvironment(License.getClientInfo().get("Environment").replace("Environment: ", ""));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return eod;
	}

	private JavaMailSenderImpl generateMailSender(EODConfig eodConfig) {
		logger.debug(Literal.ENTERING);

		JavaMailSenderImpl eodMailNotification = new JavaMailSenderImpl();
		eodMailNotification.setHost(eodConfig.getSMTPHost());
		eodMailNotification.setPort(Integer.valueOf(eodConfig.getSMTPPort()));
		eodMailNotification.setUsername(eodConfig.getSMTPUserName());

		if (eodConfig.isSMTPAutenticationRequired()) {
			String password = EncryptionUtil.decrypt("ENC(" + eodConfig.getSMTPPwd() + ")");
			eodMailNotification.setPassword(password);
		}

		logger.debug(Literal.LEAVING);
		return eodMailNotification;
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		logger.debug(Literal.ENTERING);

		ExecutionContext executionContext = arg0.getExecutionContext();
		if (executionContext.get("APP_VALUEDATE") == null) {
			executionContext.put("APP_VALUEDATE", SysParamUtil.getAppValueDate());
			executionContext.put("APP_DATE", SysParamUtil.getAppDate());
		}

		logger.debug(Literal.LEAVING);
	}

	public void seteODConfigDAO(EODConfigDAO eODConfigDAO) {
		this.eODConfigDAO = eODConfigDAO;
	}

	public void setBpsDAO(BatchProcessStatusDAO bpsDAO) {
		this.bpsDAO = bpsDAO;
	}

	public class EODStatus implements Serializable {
		private static final long serialVersionUID = 8845475181314388995L;

		String subject;
		String status;
		String startTime;
		String endTime;
		String completedTime;
		String lastBusinessDate;
		String nextBusinessDate;
		String valueDate;
		String totalCustomers;
		String processedCustomers;
		String totalLoans;
		String client;
		String version;
		String environment;

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(String completedTime) {
			this.completedTime = completedTime;
		}

		public String getLastBusinessDate() {
			return lastBusinessDate;
		}

		public void setLastBusinessDate(String lastBusinessDate) {
			this.lastBusinessDate = lastBusinessDate;
		}

		public String getNextBusinessDate() {
			return nextBusinessDate;
		}

		public void setNextBusinessDate(String nextBusinessDate) {
			this.nextBusinessDate = nextBusinessDate;
		}

		public String getValueDate() {
			return valueDate;
		}

		public void setValueDate(String valueDate) {
			this.valueDate = valueDate;
		}

		public String getTotalCustomers() {
			return totalCustomers;
		}

		public void setTotalCustomers(String totalCustomers) {
			this.totalCustomers = totalCustomers;
		}

		public String getProcessedCustomers() {
			return processedCustomers;
		}

		public void setProcessedCustomers(String processedCustomers) {
			this.processedCustomers = processedCustomers;
		}

		public String getTotalLoans() {
			return totalLoans;
		}

		public void setTotalLoans(String totalLoans) {
			this.totalLoans = totalLoans;
		}

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getEnvironment() {
			return environment;
		}

		public void setEnvironment(String environment) {
			this.environment = environment;
		}

	}
}
