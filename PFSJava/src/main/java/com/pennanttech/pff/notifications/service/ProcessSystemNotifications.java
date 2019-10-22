package com.pennanttech.pff.notifications.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.Notifications.SystemNotificationExecutionDetails;
import com.pennant.backend.model.Notifications.SystemNotifications;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.util.NotificationConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.sms.SmsEngine;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class ProcessSystemNotifications extends BasicDao<SystemNotifications> {
	private static final Logger logger = Logger.getLogger(ProcessSystemNotifications.class);
	Map<String, Configuration> TEMPLATES = new HashMap<String, Configuration>();
	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private SmsEngine smsEngine;

	@Autowired
	private MailTemplateDAO mailTemplateDAO;

	public void processNotifications() {
		logger.info(Literal.ENTERING);

		try {
			List<SystemNotificationExecutionDetails> notifications = getSystemNotificationExecDetails();

			for (SystemNotificationExecutionDetails detail : notifications) {
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
			Configuration freemarkerMailConfiguration = new Configuration();
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
		try {
			parseSMS(detail, notification);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		notification.setModule(NotificationConstants.SYSTEM_NOTIFICATION);
		notification.setSubModule(detail.getNotificationCode());

		smsEngine.sendSms(notification);

	}

	private void parseSMS(SystemNotificationExecutionDetails detail, Notification notification) throws Exception {
		String result = "";
		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate("smsTemplate", detail.getSubject());
		Configuration configuration = new Configuration();
		configuration.setTemplateLoader(loader);
		Template template = configuration.getTemplate("smsTemplate");

		try {
			result = FreeMarkerTemplateUtils.processTemplateIntoString(template, detail.getNotificationData());
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
		configuration = new Configuration();
		loader.putTemplate("mailSubject", detail.getSubject());
		configuration.setTemplateLoader(loader);
		Template templateSubject = configuration.getTemplate("mailSubject");

		String data = new String(detail.getNotificationData(), "UTF-8");
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

		String path = App.getResourcePath("Config", detail.getContentFileName());
		File ftlFile = new File(path);
		StringTemplateLoader contentloader = new StringTemplateLoader();
		byte[] contentData = FileUtils.readFileToByteArray(ftlFile);
		contentloader.putTemplate(detail.getContentFileName(), new String(contentData));

		Configuration config = new Configuration();
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
		logger.debug(Literal.ENTERING);

		SystemNotificationExecutionDetails systemNotifications = new SystemNotificationExecutionDetails();

		StringBuilder sql = new StringBuilder();
		sql.append(" select snl.id, Executionid, Notificationid, Email, Mobilenumber, Notificationdata, Attributes,");
		sql.append(" Notificationtype, Contentlocation, Contentfilename, Subject, code as Notificationcode, ");
		sql.append(" TemplateCode, keyreference from sys_notification_exec_log snl");
		sql.append(" inner join sys_notifications sn on sn.id = snl.notificationid");
		sql.append(" where snl.processingflag = :ProcessingFlag");
		systemNotifications.setProcessingFlag(false);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemNotifications);
		RowMapper<SystemNotificationExecutionDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SystemNotificationExecutionDetails.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

}
