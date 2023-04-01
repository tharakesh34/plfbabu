package com.pennanttech.pff.eod;

import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;

import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.zkoss.zul.Timer;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.SecLoginlogDAO;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.dao.messages.OfflineUserMessagesBackupDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.notification.email.OutGoingMailServer;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.OutgoingMailProperties;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.batch.backend.service.BatchProcessStatusService;
import com.pennanttech.pff.batch.model.BatchProcessStatus;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EODServiceImpl implements EODService {
	private static final Logger logger = LogManager.getLogger(EODServiceImpl.class);

	private EODConfigDAO eODConfigDAO;
	private BatchProcessStatusService bpsService;
	private SecLoginlogDAO secLoginlogDAO;
	protected EventManager eventManager;
	private OfflineUserMessagesBackupDAO offlineUserMessagesBackupDAO;

	protected Timer timer;
	String[] args = new String[1];

	@Override
	public void startEOD() {
		logger.debug(Literal.ENTERING);

		offlineUserMessagesBackupDAO.deleteByFromUsrId("EOD_ALERT");

		BatchProcessStatus bps = new BatchProcessStatus();

		if (!eODConfigDAO.isAutoEODEnabled()) {
			logger.info("Auto EOD job not enabled.");
			return;
		}

		bps.setName("PLF_EOD");
		bps = bpsService.getBatchStatus(bps);

		Date sysDate = DateUtil.getSysDate();

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_MULITIPLE_EODS_ON_SAME_DAY)
				&& bps.getEndTime() != null) {
			int days = DateUtil.getDaysBetween(sysDate, bps.getEndTime());
			if (days == 0) {
				int timeBetween = Integer.valueOf(DateUtil.timeBetween(sysDate, bps.getEndTime(), "HH"));
				if (timeBetween < 20) {
					logger.debug("EOD is already processed for this System Date {$}.", sysDate);
					return;
				}
			}
		} else {
			bps = new BatchProcessStatus();
			bps.setName("PLF_EOD");
			bps.setStartTime(sysDate);
			bps.setStatus("I");
			bps.setValueDate(SysParamUtil.getAppValueDate());
			bpsService.saveBatchStatus(bps);
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_EOD_START_ON_SAME_DAY)) {
			if (DateUtil.getDaysBetween(SysParamUtil.getNextBusinessdate(), sysDate) != 0) {
				logger.debug("System Date and Next BUsiness Date are not equal");
				return;
			}
		}

		String[] args = new String[1];
		args[0] = DateUtil.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));

		PFSBatchAdmin.getInstance();

		PFSBatchAdmin.setArgs(args);

		PFSBatchAdmin.setRunType("START");

		PennantConstants.EOD_DELAY_REQ = true;

		try {
			Thread thread = new Thread(new EODJob());
			thread.start();
			Thread.sleep(1000);
		} catch (Exception e) {
			timer.stop();
			MessageUtil.showError(e);
		}
	}

	@Override
	public void stopEOD() {

	}

	public class EODJob implements Runnable {

		public EODJob() {
			super();
		}

		@Override
		public void run() {
			PFSBatchAdmin.startJob();
		}

	}

	@Override
	public String getCronExpression() {
		logger.debug(Literal.ENTERING);

		String cronExpression = eODConfigDAO.getFrequency();

		if (StringUtils.isEmpty(cronExpression)) {
			return null;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate process not valid.", cronExpression));
		}

		logger.debug(Literal.LEAVING);
		return cronExpression;
	}

	@Override
	public String getReminderCronExp() {
		logger.debug(Literal.ENTERING);

		String cronExpression = eODConfigDAO.getReminderFrequency();

		if (StringUtils.isEmpty(cronExpression)) {
			return null;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate process not valid.", cronExpression));
		}

		logger.debug(Literal.LEAVING);
		return cronExpression;
	}

	@Override
	public String getDelayCronExp() {
		logger.debug(Literal.ENTERING);

		String cronExpression = eODConfigDAO.getDelayFrequency();

		if (StringUtils.isEmpty(cronExpression)) {
			return null;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate process not valid.", cronExpression));
		}

		logger.debug(Literal.LEAVING);
		return cronExpression;
	}

	@Override
	public boolean isAutoRequired() {
		logger.debug(Literal.ENTERING);
		return eODConfigDAO.isAutoRequired();
	}

	@Override
	public void sendReminderNotification() {
		logger.debug(Literal.ENTERING);

		EODConfig eodConfig = eODConfigDAO.getEODConfig().get(0);

		Date eodDate = SysParamUtil.getAppValueDate();

		String subject = "Reminder : EOD for value date %s will be start in %s hrs";

		Date notifTime = cronToDate(eodConfig.getEODStartJobFrequency());
		String format = DateUtil.timeBetween(notifTime, DateUtil.getSysDate());
		subject = String.format(subject, eodDate, format);

		if (!eodConfig.isSendEmailRequired()) {
			logger.info("Sending mail is not enabled.");
			return;
		}

		if (eodConfig.getToEmailAddress() == null || eodConfig.getToEmailAddress() == "") {
			logger.info("To Email Address is Mandatory.");
			return;
		}

		if (eodConfig.isEmailNotifReqrd()) {
			sendEmail(eodConfig, subject);
		}

		if (eodConfig.isPublishNotifReqrd()) {
			sendPushMessage(eodConfig, subject);
		}

		logger.debug(Literal.LEAVING);
	}

	private void sendPushMessage(EODConfig eodConfig, String subject) {
		Date sysDate = DateUtil.getSysDate();
		sysDate = DateUtil.getDatePart(sysDate);

		String[] activeUsers = secLoginlogDAO.getLoginUsers(sysDate);
		subject = subject.replace("Reminder : ", "");
		subject = subject.replace("EOD Slowness Alert : ", "");

		eventManager.publish(subject, Notify.USER, "EOD_ALERT", activeUsers);
	}

	@Override
	public void sendDelayNotification() {
		logger.debug(Literal.ENTERING);

		if (PennantConstants.EOD_DELAY_REQ) {
			EODConfig eodConfig = eODConfigDAO.getEODConfig().get(0);

			if (!eodConfig.isSendEmailRequired()) {
				logger.info("Sending mail is not enabled.");
				return;
			}

			if (!eodConfig.isDelayNotifyReq()) {
				logger.info("Delay Notification is not required.");
				return;
			}

			if (eodConfig.getToEmailAddress() == null || eodConfig.getToEmailAddress() == "") {
				logger.info("To Email Address is Mandatory.");
				return;
			}

			Date eodDate = SysParamUtil.getAppValueDate();
			String subject = "EOD Slowness Alert : EOD for value date %s is taking more time than expected";
			subject = String.format(subject, eodDate);

			sendEmail(eodConfig, subject);
		}

		logger.debug(Literal.LEAVING);
	}

	private void sendEmail(EODConfig eodConfig, String subject) {
		logger.debug(Literal.ENTERING);

		OutGoingMailServer instance = getOutGoingMailServer(eodConfig);

		MimeBodyPart body = new MimeBodyPart();

		String[] ccMailAddress = eodConfig.getCCEmailAddress().split(",");
		String[] toMailAddress = eodConfig.getToEmailAddress().split(",");

		try {
			EODNotificationStatus eod = setEODStatus(subject, eodConfig);
			Configuration config = setConfiguration();

			String result = FreeMarkerTemplateUtils.processTemplateIntoString(config.getTemplate("eod_alert.html"),
					eod);

			body.setText(result, StandardCharsets.UTF_8.name(), EmailBodyType.HTML.getValue());

			instance.setSender(eodConfig.getFromEmailAddress(), eodConfig.getFromName());
			instance.send(toMailAddress, ccMailAddress, subject, body, null);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

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

	private Date cronToDate(String cronExp) {
		final CronSequenceGenerator generator = new CronSequenceGenerator(cronExp);
		final Date executionDate = generator.next(DateUtil.addDays(DateUtil.getSysDate(), -1));

		return executionDate;
	}

	private EODNotificationStatus setEODStatus(String subject, EODConfig eodConfig) {
		EODNotificationStatus eod = new EODNotificationStatus();

		try {
			Date eodDate = cronToDate(eodConfig.getEODStartJobFrequency());
			Date delayTime = cronToDate(eodConfig.getDelayFrequency());

			subject = subject.replace("Reminder : ", "");
			subject = subject.replace("EOD Slowness Alert : ", "");

			eod.setSubject(subject);
			eod.setClient(License.getClientInfo().get("Client").replace("Client: ", ""));
			eod.setVersion(EODServiceImpl.class.getPackage().getImplementationVersion());
			eod.setEnvironment(License.getClientInfo().get("Environment").replace("Environment: ", ""));

			eod.setStartTime(DateUtil.format(eodDate, DateFormat.LONG_TIME));
			eod.setUserEstimatedTime(DateUtil.format(delayTime, DateFormat.LONG_TIME));
			eod.setTimeTaken(DateUtil.timeBetween(delayTime, eodDate));

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return eod;
	}

	private Configuration setConfiguration() {
		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

		try {
			config.setClassForTemplateLoading(EODServiceImpl.class, "/");
			config.setDefaultEncoding("UTF-8");
			config.setLocale(Locale.getDefault());
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			String path = EODServiceImpl.class.getResource("/eod_alert.html").getFile();
			path = URLDecoder.decode(path, "UTF-8");

			FileTemplateLoader templateLoader = new FileTemplateLoader(new File(path).getParentFile(), true);
			config.setTemplateLoader(templateLoader);

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return config;
	}

	public void setEODConfigDAO(EODConfigDAO eODConfigDAO) {
		this.eODConfigDAO = eODConfigDAO;
	}

	public BatchProcessStatusService getBpsService() {
		return bpsService;
	}

	public void setBpsService(BatchProcessStatusService bpsService) {
		this.bpsService = bpsService;
	}

	public void setSecLoginlogDAO(SecLoginlogDAO secLoginlogDAO) {
		this.secLoginlogDAO = secLoginlogDAO;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setOfflineUserMessagesBackupDAO(OfflineUserMessagesBackupDAO offlineUserMessagesBackupDAO) {
		this.offlineUserMessagesBackupDAO = offlineUserMessagesBackupDAO;
	}

	public class EODNotificationStatus implements Serializable {
		private static final long serialVersionUID = 8845475181314388995L;

		String subject;
		String timeTaken;
		String startTime;
		String userEstimatedTime;
		String client;
		String version;
		String environment;

		public String getTimeTaken() {
			return timeTaken;
		}

		public void setTimeTaken(String timeTaken) {
			this.timeTaken = timeTaken;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getUserEstimatedTime() {
			return userEstimatedTime;
		}

		public void setUserEstimatedTime(String userEstimatedTime) {
			this.userEstimatedTime = userEstimatedTime;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
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
