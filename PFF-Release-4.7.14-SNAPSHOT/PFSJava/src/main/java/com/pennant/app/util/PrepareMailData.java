package com.pennant.app.util;

import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.pennant.app.model.MailData;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.notifications.NotificationsService;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class PrepareMailData implements Serializable {
	private static final long serialVersionUID = -4293213317229057447L;
	private static final Logger logger = Logger.getLogger(PrepareMailData.class);

	private DataSource dataSource;
	private MailUtility mailUtility;
	private ReportUtil reportUtil;
	private NotificationsService notificationsService;

	public void processData(String mailName,Date appDate) throws SQLException {
		logger.debug("Entering");
		File attachmentPath = null;
		Connection connection = null;
		try {
			List<MailData> mailDataList = getNotificationsService().getMailData(mailName);
			
			// MAIL_BODY
			for (MailData mailData : mailDataList) {
				MailTemplate mailTemplate = new MailTemplate();
				mailTemplate.setLovDescMailId(new String[] { mailData.getMailTo() });
				mailTemplate.setEmailSubject(mailData.getMailSubject());

				// Trigger
				int triggervalue = getNotificationsService().triggerMail(mailData.getMailTrigger());
				if (triggervalue != 0) {

					// data Setting
					Map<String, Object> datafields = null;
					if (mailData.getMailData() != null) {
						datafields = getNotificationsService().mergeFields(mailData.getMailData());
					}

					if (datafields != null && datafields.size() > 0) {
						mailTemplate.setLovDescFormattedContent(mergedata(datafields, mailData.getMailBody()));
					} else if (mailData.getMailBody() != null) {
						String mailText = FileUtils.readFileToString(new File(PathUtil.getPath(PathUtil.MAIL_BODY) + mailData.getMailBody()));
						mailTemplate.setLovDescFormattedContent(mailText);
					}

					// Mail Attachment
					if (mailData.getMailAttachmentName() != null) {
						mailTemplate.setLovDescAttachmentName(mailData.getMailAttachmentName());
						
						connection = getDataSource().getConnection();
						boolean attachment =  getReportUtil().generateExcelReport(PathUtil.MAIL_ATTACHMENT_REPORT, mailData.getMailAttachmentName(), "",
								false, "", connection,appDate);
						if (attachment) {
							attachmentPath = new File(PathUtil.getPath(PathUtil.MAIL_ATTACHMENT_REPORT) + mailData.getMailAttachmentName());
							byte[] data = FileUtils.readFileToByteArray(attachmentPath);
							mailTemplate.setLovDescEmailAttachment(data);
						}
					}

					getMailUtility().sendMail(mailTemplate);
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

	public MailUtility getMailUtility() {
		return mailUtility;
	}

	public void setMailUtility(MailUtility mailUtility) {
		this.mailUtility = mailUtility;
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
