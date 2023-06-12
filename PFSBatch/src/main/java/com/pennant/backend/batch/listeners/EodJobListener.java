package com.pennant.backend.batch.listeners;

import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.eventproperties.service.impl.EventPropertiesServiceImpl.EventType;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.script.ScriptEngine;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.notification.email.OutGoingMailServer;
import com.pennanttech.pennapps.notification.email.configuration.AttachmentType;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.OutgoingMailProperties;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pff.batch.backend.dao.BatchProcessStatusDAO;
import com.pennanttech.pff.batch.model.BatchProcessStatus;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EodJobListener implements JobExecutionListener {
	private static final Logger logger = LogManager.getLogger(EodJobListener.class);

	private EODConfigDAO eODConfigDAO;
	private BatchProcessStatusDAO bpsDAO;
	private EventPropertiesService eventPropertiesService;

	public EodJobListener() {
		super();
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		for (Entry<String, ScriptEngine> entry : RuleExecutionUtil.EOD_SCRIPT_ENGINE_MAP.entrySet()) {
			try {
				entry.getValue().setEod(false);
				entry.getValue().close();
			} catch (Exception e) {
				//
			}
		}
		Date eodDate = EODUtil.EVENT_PROPS.getAppDate();

		RuleExecutionUtil.EOD_SCRIPT_ENGINE_MAP.clear();
		EODUtil.EVENT_PROPS = new EventProperties();
		EODUtil.setEod(false);

		PennantConstants.EOD_DELAY_REQ = false;

		List<EODConfig> eodList = eODConfigDAO.getEODConfig();

		if (CollectionUtils.isEmpty(eodList)) {
			logger.info("EOD Configuration is not available.");
			return;
		}

		EODConfig eodConfig = eodList.get(0);
		updateExecutionStatus(jobExecution, eodDate);

		if (!eodConfig.isSendEmailRequired()) {
			logger.info("Sending mail is not enabled.");
			return;
		}

		if (eodConfig.getToEmailAddress() == null || eodConfig.getToEmailAddress() == "") {
			logger.info("To Email Address is Mandatory.");
			return;
		}

		try {
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

				eodConfig.setEnableAutoEod(false);
				eODConfigDAO.updateEnableEOD(eodConfig);
			}

			OutGoingMailServer instance = getOutGoingMailServer(eodConfig);

			String[] toMailAddress = eodConfig.getToEmailAddress().split(",");
			String[] ccMailAddress = eodConfig.getCCEmailAddress().split(",");

			MimeBodyPart body = new MimeBodyPart();

			EODStatus eod = setEODStatus(subject, eodStatus, jobExecution);
			Configuration config = setConfiguration();

			String result = FreeMarkerTemplateUtils
					.processTemplateIntoString(config.getTemplate("eod_notification.html"), eod);

			body.setText(result, StandardCharsets.UTF_8.name(), EmailBodyType.HTML.getValue());

			instance.setSender(eodConfig.getFromEmailAddress(), eodConfig.getFromName());
			instance.send(toMailAddress, ccMailAddress, subject, body, getAttachments(jobExecution));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		ThreadContext.clearAll();
		logger.debug(Literal.LEAVING);
	}

	private OutGoingMailServer getOutGoingMailServer(EODConfig eodConfig) {
		OutgoingMailProperties properties = new OutgoingMailProperties();

		properties.setUser(eodConfig.getSMTPUserName());

		if (eodConfig.isSMTPAutenticationRequired()) {
			String password = EncryptionUtil.decrypt("ENC(" + eodConfig.getSMTPPwd() + ")");
			properties.setPassword(password);
		}

		properties.setHost(eodConfig.getSMTPHost());
		properties.setPort(eodConfig.getSMTPPort());
		properties.setEncryptionType(eodConfig.getEncryptionType());
		properties.setFrom(eodConfig.getFromEmailAddress());
		properties.setPersonal(eodConfig.getFromName());
		properties.setReturnMail(eodConfig.getFromEmailAddress());
		properties.setAuth(eodConfig.isSMTPAutenticationRequired());

		return OutGoingMailServer.getInstance(properties);
	}

	private List<MimeBodyPart> getAttachments(JobExecution jobExecution) throws MessagingException {
		List<MimeBodyPart> attachements = new ArrayList<>();
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			return attachements;
		}

		StepExecution currentStepExecution = null;

		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			currentStepExecution = stepExecution;
		}

		if (currentStepExecution == null) {
			return attachements;
		}

		byte[] content = currentStepExecution.getExitStatus().getExitDescription().getBytes();
		MimeBodyPart bodyPart = new MimeBodyPart();
		MessageAttachment attachement = new MessageAttachment(currentStepExecution.getStepName() + ".log",
				AttachmentType.TEXT);
		attachement.setAttachment(content);

		AttachmentType attachmentType = AttachmentType.getAttachmentType(AttachmentType.TEXT.getKey());
		DataSource dataSource = new ByteArrayDataSource(attachement.getAttachment(), attachmentType.getMimeType());
		bodyPart.setFileName(currentStepExecution.getStepName() + ".log");
		bodyPart.setDataHandler(new DataHandler(dataSource));
		attachements.add(bodyPart);

		return attachements;
	}

	private void updateExecutionStatus(JobExecution jobExecution, Date eodDate) {
		BatchProcessStatus batchProcessStatus = new BatchProcessStatus();
		batchProcessStatus.setEndTime(jobExecution.getEndTime());
		batchProcessStatus.setStartTime(jobExecution.getEndTime());
		batchProcessStatus.setName("PLF_EOD");
		batchProcessStatus.setValueDate(eodDate);

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			batchProcessStatus.setStatus("S");
		} else {
			batchProcessStatus.setStatus("F");
		}

		bpsDAO.updateBatchStatus(batchProcessStatus);
	}

	private Configuration setConfiguration() {
		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

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
			eod.setCompletedTime(DateUtil.timeBetween(endTime, startTime));
			eod.setTotalLoans("0");

			for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
				DataEngineStatus status = null;
				if (stepExecution.getStepName().startsWith("microEOD")) {
					status = (DataEngineStatus) stepExecution.getExecutionContext().get(stepExecution.getStepName());
					if (status != null) {
						Object totalCustomers = status.getKeyAttributes().get(EodConstants.DATA_TOTALCUSTOMER);
						eod.setTotalLoans(String.valueOf(totalCustomers));
					}

					break;
				}
			}

			eod.setClient(License.getClientInfo().get("Client").replace("Client: ", ""));
			eod.setVersion(EodJobListener.class.getPackage().getImplementationVersion());
			eod.setEnvironment(License.getClientInfo().get("Environment").replace("Environment: ", ""));

			if (PFSBatchAdmin.startedBy == null) {
				eod.setStartedBy("AUTO");
			} else {
				eod.setStartedBy(PFSBatchAdmin.startedBy);
			}
			PFSBatchAdmin.startedBy = null;

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return eod;
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		ThreadContext.put("MODULE", "EOD");
		logger.debug("Preparing before EOD details...");

		RuleExecutionUtil.EOD_SCRIPT_ENGINE_MAP.clear();

		logger.debug("Loading EOD properties into EventProperties bean to reuse during EOD...");
		EODUtil.EVENT_PROPS = eventPropertiesService.getEventProperties(EventType.EOD);
		EODUtil.setEod(true);

		ExecutionContext executionContext = arg0.getExecutionContext();

		executionContext.put("APP_VALUEDATE", SysParamUtil.getAppValueDate());
		executionContext.put("APP_DATE", SysParamUtil.getAppDate());
		executionContext.put("APP_NEXT_BUS_DATE", SysParamUtil.getValueAsDate("APP_NEXT_BUS_DATE"));
		executionContext.put("APP_LAST_BUS_DATE", SysParamUtil.getValueAsDate("APP_LAST_BUS_DATE"));

		executionContext.put(EODUtil.EVENT_PROPERTIES, EODUtil.EVENT_PROPS);

		logger.debug("Prepared before EOD details..");
	}

	public void seteODConfigDAO(EODConfigDAO eODConfigDAO) {
		this.eODConfigDAO = eODConfigDAO;
	}

	public void setBpsDAO(BatchProcessStatusDAO bpsDAO) {
		this.bpsDAO = bpsDAO;
	}

	public void setEventPropertiesService(EventPropertiesService eventPropertiesService) {
		this.eventPropertiesService = eventPropertiesService;
	}

	public static class EODStatus implements Serializable {
		private static final long serialVersionUID = 8845475181314388995L;

		private String subject;
		private String status;
		private String startTime;
		private String endTime;
		private String completedTime;
		private String lastBusinessDate;
		private String nextBusinessDate;
		private String valueDate;
		private String totalCustomers;
		private String processedCustomers;
		private String totalLoans;
		private String client;
		private String version;
		private String environment;
		private String startedBy;

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

		public String getStartedBy() {
			return startedBy;
		}

		public void setStartedBy(String startedBy) {
			this.startedBy = startedBy;
		}

	}
}
