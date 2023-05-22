package com.pennanttech.pff.notifications.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.Notifications.SystemNotificationExecutionDetails;
import com.pennant.backend.model.Notifications.SystemNotifications;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.NotificationConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.AttachmentType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pennapps.notification.sms.SmsEngine;
import com.pennanttech.pennapps.pff.finance.FinScheduleReportGenerator;
import com.pennanttech.pff.core.TableType;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class ProcessSystemNotifications extends BasicDao<SystemNotifications> {
	private static final Logger logger = LogManager.getLogger(ProcessSystemNotifications.class);
	Map<String, Configuration> TEMPLATES = new HashMap<String, Configuration>();
	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private SmsEngine smsEngine;

	@Autowired
	private MailTemplateDAO mailTemplateDAO;

	@Autowired
	private SOAReportGenerationService soaReportGenerationService;

	@Autowired
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	private FinanceDetailService financeDetailService;
	private CustomerDAO customerDAO;

	public void processNotifications() {
		logger.info(Literal.ENTERING);

		try {
			List<SystemNotificationExecutionDetails> notifications = getSystemNotificationExecDetails();

			for (SystemNotificationExecutionDetails detail : notifications) {
				try {
					if (detail.getTemplateCode() != null) {
						if ("EMAIL".equals(detail.getNotificationType())) {
							prepareEmail(detail);
						} else if ("SMS".equals(detail.getNotificationType())) {
							prepareSMSMsg(detail);
						}
					} else {
						if ("EMAIL".equals(detail.getNotificationType())) {
							prepareEmailMessage(detail);
						} else if ("SMS".equals(detail.getNotificationType())) {
							prepareSMSMessage(detail);
						}
					}
					updateProcessFlag(detail);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.info(Literal.LEAVING);

	}

	private void prepareEmail(SystemNotificationExecutionDetails detail) {
		logger.debug("Entering");

		Notification notification = new Notification();
		String header = "";
		MessageAddress address = new MessageAddress();
		address.setEmailId(detail.getEmail());
		address.setRecipientType(RecipientType.TO.getKey());
		notification.getAddressesList().add(address);
		notification.setModule(NotificationConstants.SYSTEM_NOTIFICATION);
		notification.setSubModule(detail.getNotificationCode());
		notification.setKeyReference(detail.getKeyReference());
		notification.setNotificationId(detail.getNotificationId());

		try {
			header = getTemplateContent(detail, "EML");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (StringUtils.isNotBlank(header)) {
			String[] data = header.split("~@#");
			notification.setContent(data[0].getBytes());
			notification.setSubject(data[1]);
			emailEngine.sendEmail(notification);
		}
		logger.debug("LEAVING");
	}

	public String getTemplateContent(SystemNotificationExecutionDetails detail, String type) throws Exception {
		logger.debug("Entering");
		MailTemplate template = null;
		template = mailTemplateDAO.getMailTemplateByCode(detail.getTemplateCode(), "");
		if (template == null) {
			logger.error("No Template exists with templatecode " + detail.getTemplateCode());
		}
		String content = null;
		String subject = null;

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(new String(detail.getNotificationData(), "UTF-8"))));
		try {
			if (type.equalsIgnoreCase("SMS")) {
				content = parseData(new String(template.getSmsContent()), document);
			} else if (type.equalsIgnoreCase("EML")) {
				subject = parseData(template.getEmailSubject(), document);
				content = parseData(new String(template.getEmailContent(), "UTF-16"), document);
				content = content + "~@#" + subject;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		logger.debug("Entering");
		return content;
	}

	private String parseData(String content, Document document) throws Exception {
		logger.debug("Entering");
		String result = "";
		try {
			Configuration freemarkerMailConfiguration = new Configuration(
					Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			StringTemplateLoader loader = new StringTemplateLoader();
			loader.putTemplate("template", content);
			freemarkerMailConfiguration.setTemplateLoader(loader);
			freemarker.template.Template freeMarkerTemplate = freemarkerMailConfiguration.getTemplate("template");
			result = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerTemplate, document);
		} catch (IOException e) {
			throw new Exception("Unable to read or process freemarker configuration or template", e);
		} catch (TemplateException e) {
			throw new Exception("Problem initializing freemarker or rendering template ", e);
		}

		logger.debug("Entering");
		return result;
	}

	private void updateProcessFlag(SystemNotificationExecutionDetails detail) {
		logger.debug(Literal.ENTERING);
		detail.setProcessingFlag(true);
		StringBuilder sql = new StringBuilder();
		sql.append(" Update Sys_Notification_Exec_Log");
		sql.append(" set Processingflag = :ProcessingFlag");
		sql.append(" WHERE Id = :Id");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		logger.debug(Literal.LEAVING);
		jdbcTemplate.update(sql.toString(), beanParameters);

	}

	private void prepareEmailMessage(SystemNotificationExecutionDetails detail) {

		Notification notification = new Notification();
		notification.getEmails().add(detail.getEmail());
		notification.setNotificationId(detail.getNotificationId());
		try {
			parseMail(detail, notification);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Attachments addition
		setAttachments(detail, notification);

		MessageAddress address = new MessageAddress();
		address.setEmailId(detail.getEmail());
		// notification.setContentType(EmailBodyType.PLAIN.getKey());
		address.setRecipientType(RecipientType.TO.getKey());
		notification.getAddressesList().add(address);

		notification.setModule(NotificationConstants.SYSTEM_NOTIFICATION);
		notification.setSubModule(detail.getNotificationCode());
		notification.setKeyReference(detail.getKeyReference());

		emailEngine.sendEmail(notification);

	}

	private void setAttachments(SystemNotificationExecutionDetails detail, Notification notification) {
		logger.debug(Literal.ENTERING);

		MessageAttachment attachment = new MessageAttachment();

		if (StringUtils.containsIgnoreCase(detail.getAttachmentFileNames(), "SOA")
				&& StringUtils.isNotBlank(detail.getAttributes())) {

			String map[] = detail.getAttributes().split(",");
			Date startDate = null;
			for (int i = 0; i < map.length; i++) {
				if (map[i].contains("FINSTARTDATE")) {
					startDate = DateUtil.getDate(map[i].substring(map[i].indexOf("=") + 1), "dd-MM-yyyy");
					break;
				}
			}

			Date appDate = DateUtil.addDays(SysParamUtil.getAppDate(), -1);
			StatementOfAccount account = null;
			try {
				account = soaReportGenerationService.getStatmentofAccountDetails(detail.getKeyReference(), startDate,
						appDate, false);
			} catch (IllegalAccessException e1) {
				logger.error(Literal.EXCEPTION, e1);
			} catch (InvocationTargetException e1) {
				logger.error(Literal.EXCEPTION, e1);
			}
			if (account != null) {
				List<Object> list = new ArrayList<Object>();
				list.add(account.getSoaSummaryReports());
				list.add(account.getTransactionReports());
				list.add(account.getApplicantDetails());
				list.add(account.getOtherFinanceDetails());
				list.add(account.getInterestRateDetails());

				byte[] buf = null;
				try {
					buf = ReportsUtil.generatePDF("FINENQ_StatementOfAccount", account, list, App.CODE);

					attachment.setAttachment(buf);
					attachment.setAttachmentType(AttachmentType.PDF.getKey());
					attachment.setFileName(detail.getAttachmentFileNames());
					notification.getAttachmentList().add(attachment);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
				}
			}
		} else if (StringUtils.containsIgnoreCase(detail.getAttachmentFileNames(), "RepaymentSchedule")
				&& StringUtils.isNotBlank(detail.getAttributes())) {

			String map[] = detail.getAttributes().split(",");
			String finReference = null;
			long logKey = 0;

			for (int i = 0; i < map.length; i++) {
				if (map[i].contains("FINREFERENCE")) {
					finReference = map[i].substring(map[i].indexOf("=") + 1);
					if (finReference.endsWith("}")) {
						finReference = finReference.substring(0, finReference.indexOf("}"));
					}
				}
				if (map[i].contains("LOGKEY")) {
					logKey = Long.valueOf(map[i].substring(map[i].indexOf("=") + 1));
				}
			}

			Long finID = financeDetailService.getFinID(finReference, TableType.MAIN_TAB);

			FinScheduleData finScheduleData = financeDetailService.getFinSchDataById(finID, "_AView", true);

			if (finScheduleData == null) {
				return;
			}

			List<Object> list = new ArrayList<Object>();

			List<FinanceScheduleDetail> schdDetails = financeScheduleDetailDAO.getFinScheduleDetails(finID, "_Log",
					false, logKey);
			schdDetails = ScheduleCalculator.sortSchdDetails(schdDetails);
			finScheduleData.setFinanceScheduleDetails(schdDetails);

			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

			if (CollectionUtils.isNotEmpty(finScheduleData.getRepayDetails())) {
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				// Penalty Details
				for (OverdueChargeRecovery penaltyDetail : finScheduleData.getPenaltyDetails()) {
					if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
								.get(penaltyDetail.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					} else {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}

			FinanceMain financeMain = finScheduleData.getFinanceMain();
			Customer customer = customerDAO.getCustomerByID(financeMain.getCustID());
			financeMain.setLovDescCustCIF(customer.getCustCIF() + " - " + customer.getCustShrtName());

			FinScheduleReportGenerator reportGenerator = new FinScheduleReportGenerator();

			List<FinanceGraphReportData> schdGraphList = reportGenerator.getScheduleGraphData(finScheduleData);
			list.add(schdGraphList);

			List<FinanceScheduleReportData> schdList = reportGenerator.getPrintScheduleData(finScheduleData,
					rpyDetailsMap, penaltyDetailsMap, true, false);
			list.add(schdList);

			try {
				byte[] buf = ReportsUtil.generatePDF("FINENQ_ScheduleDetail", financeMain, list, App.CODE);
				attachment.setAttachment(buf);
				attachment.setAttachmentType(AttachmentType.PDF.getKey());
				attachment.setFileName(detail.getAttachmentFileNames());
				notification.getAttachmentList().add(attachment);
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void prepareSMSMsg(SystemNotificationExecutionDetails detail) {
		logger.debug(Literal.ENTERING);
		Notification notification = new Notification();
		notification.getMobileNumbers().add(detail.getMobileNumber());
		notification.setMobileNumber(detail.getMobileNumber());
		notification.setNotificationId(detail.getNotificationId());
		String header = "";
		try {
			header = getTemplateContent(detail, "SMS");
			notification.setMessage(header);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		notification.setKeyReference(detail.getKeyReference());
		notification.setModule(NotificationConstants.SYSTEM_NOTIFICATION);
		notification.setSubModule(detail.getNotificationCode());
		notification.setNotificationData(settingNotificationData(detail));

		smsEngine.sendSms(notification);
		logger.debug(Literal.LEAVING);

	}

	private String settingNotificationData(SystemNotificationExecutionDetails detail) {
		String str = null;
		String json = "";

		try {
			if (detail.getNotificationData() != null) {
				str = new String(detail.getNotificationData(), "UTF-8");
				str = str.replace("</SYS_NOTIFICATION>",
						"<CONTENTCODE>" + detail.getTemplateCode() + "</CONTENTCODE></SYS_NOTIFICATION>");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (str != null && str != "") {
			try {
				XmlMapper xmlMapper = new XmlMapper();
				JsonNode node = xmlMapper.readTree(str.getBytes());
				ObjectMapper jsonMapper = new ObjectMapper();
				json = jsonMapper.writeValueAsString(node);
			} catch (JsonProcessingException e) {
				logger.error(Literal.EXCEPTION, e);
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}

		return json;

	}

	private void prepareSMSMessage(SystemNotificationExecutionDetails detail) {
		Notification notification = new Notification();
		notification.getMobileNumbers().add(detail.getMobileNumber());
		notification.setNotificationId(detail.getNotificationId());
		notification.setMobileNumber(detail.getMobileNumber());
		try {
			parseSMS(detail, notification);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		notification.setKeyReference(detail.getKeyReference());
		notification.setModule(NotificationConstants.SYSTEM_NOTIFICATION);
		notification.setSubModule(detail.getNotificationCode());
		notification.setNotificationData(settingNotificationData(detail));

		smsEngine.sendSms(notification);

	}

	private void parseSMS(SystemNotificationExecutionDetails detail, Notification notification) throws Exception {
		String result = "";
		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate("smsTemplate", detail.getSubject());
		Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setTemplateLoader(loader);
		Template template = configuration.getTemplate("smsTemplate");

		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(new String(detail.getNotificationData(), "UTF-8"))));

			result = FreeMarkerTemplateUtils.processTemplateIntoString(template, document);
			notification.setMessage(result);
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to read or process freemarker configuration or template", e);
		} catch (TemplateException e) {
			logger.debug(Literal.EXCEPTION, e);
			throw new Exception("Problem initializing freemarker or rendering template ", e);
		}

	}

	private void parseMail(SystemNotificationExecutionDetails detail, Notification notification) throws Exception {
		logger.debug(Literal.ENTERING);

		String subject = "";
		String content = "";
		Configuration configuration = null;

		StringTemplateLoader loader = new StringTemplateLoader();
		configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		loader.putTemplate("mailSubject", detail.getSubject());
		configuration.setTemplateLoader(loader);
		Template templateSubject = configuration.getTemplate("mailSubject");

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(new String(detail.getNotificationData(), "UTF-8"))));

		try {
			subject = FreeMarkerTemplateUtils.processTemplateIntoString(templateSubject, document);
		} catch (IOException e) {
			throw new Exception("Unable to read or process freemarker configuration or template", e);
		} catch (TemplateException e) {
			throw new Exception("Problem initializing freemarker or rendering template ", e);
		}

		notification.setSubject(subject);

		String path = "";
		if (StringUtils.isNotBlank(detail.getContentLocation())) {
			path = App.getResourcePath("config", detail.getContentLocation(), detail.getContentFileName());
		} else {
			path = App.getResourcePath("config", detail.getContentFileName());
		}

		File ftlFile = new File(path);
		StringTemplateLoader contentloader = new StringTemplateLoader();
		byte[] contentData = FileUtils.readFileToByteArray(ftlFile);
		contentloader.putTemplate(detail.getContentFileName(), new String(contentData));

		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		config.setClassForTemplateLoading(ProcessSystemNotifications.class, detail.getContentFileName());
		config.setTemplateLoader(contentloader);
		config.setDefaultEncoding("UTF-8");
		config.setLocale(Locale.getDefault());
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		TEMPLATES.put(detail.getContentFileName(), config);

		try {
			content = FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(detail.getContentFileName()),
					document);
		} catch (IOException e) {
			throw new Exception("Unable to read or process freemarker configuration or template", e);
		} catch (TemplateException e) {
			throw new Exception("Problem initializing freemarker or rendering template ", e);
		}

		notification.setContent(content.getBytes(Charset.forName("UTF-8")));
		logger.debug(Literal.LEAVING);
	}

	private Template getTemplate(String templateName) throws Exception {
		Configuration config = null;
		config = TEMPLATES.get(templateName);

		if (config == null) {
			throw new Exception("Template not found for the name " + templateName);
		}

		return config.getTemplate(templateName);
	}

	private List<SystemNotificationExecutionDetails> getSystemNotificationExecDetails() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" snl.Id, Executionid, NotificationId, Email, Mobilenumber, NotificationData, Attributes");
		sql.append(", NotificationType, ContentLocation, ContentFileName, Subject, Code Notificationcode");
		sql.append(", sn.AttachmentFileNames, TemplateCode, KeyReference");
		sql.append(" From sys_notification_exec_log snl");
		sql.append(" Inner Join sys_notifications sn on sn.id = snl.notificationid");
		sql.append(" Where snl.processingflag = ?");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setBoolean(index, false);
		}, (rs, roeNum) -> {
			SystemNotificationExecutionDetails sysNoti = new SystemNotificationExecutionDetails();

			sysNoti.setId(rs.getLong("Id"));
			sysNoti.setExecutionId(rs.getLong("Executionid"));
			sysNoti.setNotificationId(rs.getLong("NotificationId"));
			sysNoti.setEmail(rs.getString("Email"));
			sysNoti.setMobileNumber(rs.getString("Mobilenumber"));
			sysNoti.setNotificationData(rs.getBytes("NotificationData"));
			sysNoti.setAttributes(rs.getString("Attributes"));
			sysNoti.setNotificationType(rs.getString("NotificationType"));
			sysNoti.setContentLocation(rs.getString("ContentLocation"));
			sysNoti.setContentFileName(rs.getString("ContentFileName"));
			sysNoti.setSubject(rs.getString("Subject"));
			sysNoti.setNotificationCode(rs.getString("Notificationcode"));
			sysNoti.setAttachmentFileNames(rs.getString("AttachmentFileNames"));
			sysNoti.setTemplateCode(rs.getString("TemplateCode"));
			sysNoti.setKeyReference(rs.getString("KeyReference"));

			sysNoti.setProcessingFlag(false);

			return sysNoti;
		});
	}

	public void setSoaReportGenerationService(SOAReportGenerationService soaReportGenerationService) {
		this.soaReportGenerationService = soaReportGenerationService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}
