package com.pennant.app.util;

import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.model.MailData;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.NotificationConstants;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.EmailMessage;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class PrepareMailData implements Serializable {
	private static final long serialVersionUID = -4293213317229057447L;
	private static final Logger logger = Logger.getLogger(PrepareMailData.class);
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private DataSource dataSource;
	private ReportUtil reportUtil;
	private NotificationsService notificationsService;
	@Autowired
	private EmailEngine emailEngine;

	public void processData(String mailName,Date appDate) throws SQLException {
		logger.debug("Entering");
		File attachmentPath = null;
		Connection connection = null;
		try {
			List<MailData> mailDataList = getNotificationsService().getMailData(mailName);
			
			// MAIL_BODY
			for (MailData mailData : mailDataList) {
				MailTemplate template = new MailTemplate();
				template.setLovDescMailId(new String[] { mailData.getMailTo() });
				template.setEmailSubject(mailData.getMailSubject());

				// Trigger
				int triggervalue = getNotificationsService().triggerMail(mailData.getMailTrigger());
				if (triggervalue != 0) {

					// data Setting
					Map<String, Object> datafields = null;
					if (mailData.getMailData() != null) {
						datafields = getNotificationsService().mergeFields(mailData.getMailData());
					}

					if (datafields != null && datafields.size() > 0) {
						template.setLovDescFormattedContent(mergedata(datafields, mailData.getMailBody()));
					} else if (mailData.getMailBody() != null) {
						String mailText = FileUtils.readFileToString(new File(PathUtil.getPath(PathUtil.MAIL_BODY) + mailData.getMailBody()));
						template.setLovDescFormattedContent(mailText);
					}

					// Mail Attachment
					if (mailData.getMailAttachmentName() != null) {
						Map<String,byte[]> attchments= new HashMap<>();

						
						connection = getDataSource().getConnection();
						boolean attachment =  getReportUtil().generateExcelReport(PathUtil.MAIL_ATTACHMENT_REPORT, mailData.getMailAttachmentName(), "",
								false, "", connection,appDate);
						byte[] data = null;
						if (attachment) {
							attachmentPath = new File(PathUtil.getPath(PathUtil.MAIL_ATTACHMENT_REPORT) + mailData.getMailAttachmentName());
							 data = FileUtils.readFileToByteArray(attachmentPath);
						}
						attchments.put(mailData.getMailAttachmentName(), data);
						template.setAttchments(attchments);
					}

					EmailMessage emailMessage = new EmailMessage();
					emailMessage.setKeyReference("");
					emailMessage.setModule(mailName);
					emailMessage.setSubModule(mailName);
					emailMessage.setNotificationId(template.getId());
					emailMessage.setStage("");
					emailMessage.setSubject(template.getEmailSubject());
					emailMessage.setContent(template.getLovDescFormattedContent().getBytes(UTF_8));

					if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(template.getEmailFormat())) {
						emailMessage.setContentType(EmailBodyType.HTML.getKey());
					} else {
						emailMessage.setContentType(EmailBodyType.PLAIN.getKey());
					}

					for (String mailId : template.getLovDescMailId()) {
						MessageAddress address = new MessageAddress();
						address.setEmailId(mailId);
						address.setRecipientType(RecipientType.TO.getKey());
						emailMessage.getAddressesList().add(address);
					}

					Map<String, byte[]> attachments = template.getAttchments();
					if (MapUtils.isNotEmpty(attachments)) {
						for (Entry<String, byte[]> document : attachments.entrySet()) {
							MessageAttachment attachment = new MessageAttachment();
							attachment.setAttachment(document.getValue());
							attachment.setFileName(document.getKey());
							emailMessage.getAttachmentList().add(attachment);
						}
					}

					emailEngine.sendEmail(emailMessage);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}finally{
			if (connection!=null) {
				connection.close();
			}
		}
	}

	private String mergedata(Map<String, Object> datafields, String template) {
		Configuration configuration = new Configuration();

		configuration.setClassForTemplateLoading(PrepareMailData.class, "/");

		FileTemplateLoader templateLoader;
		try {
			templateLoader = new FileTemplateLoader(new File(PathUtil.getPath(PathUtil.MAIL_BODY)), true);

			configuration.setTemplateLoader(templateLoader);

			Template helloTemp = configuration.getTemplate(template);
			StringWriter writer = new StringWriter();
			Map<String, Object> helloMap = new HashMap<String, Object>();
			for (Map.Entry<String, Object> entry : datafields.entrySet()) {
				helloMap.put(entry.getKey(), entry.getValue());
			}

			helloTemp.process(helloMap, writer);

			return String.valueOf(writer);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;

	}

	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public NotificationsService getNotificationsService() {
		return notificationsService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ReportUtil getReportUtil() {
		return reportUtil;
	}

	public void setReportUtil(ReportUtil reportUtil) {
		this.reportUtil = reportUtil;
	}

}
