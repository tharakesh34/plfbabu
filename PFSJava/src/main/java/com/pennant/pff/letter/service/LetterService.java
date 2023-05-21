package com.pennant.pff.letter.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.letter.Letter;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.finance.FinanceEnquiryService;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.pff.batch.dao.BatchJobDAO;
import com.pennant.pff.letter.LetterMode;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.letter.job.LetterGenerationJob;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.net.FTPUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pff.notifications.service.NotificationService;

public class LetterService {
	private static final Logger logger = LogManager.getLogger(LetterService.class);
	private AutoLetterGenerationDAO autoLetterGenerationDAO;
	private AgreementDefinitionDAO agreementDefinitionDAO;
	private EmailEngine emailEngine;
	private MailTemplateDAO mailTemplateDAO;
	private NotificationService notificationService;
	private FinanceEnquiryService financeEnquiryService;
	private BatchJobDAO batchJobDAO;

	private LetterGenerationJob letterGenerationJob;

	public void executeJob() {
		int count = autoLetterGenerationDAO.getPendingRecords();

		if (count == 0) {
			return;
		}

		long batchID = batchJobDAO.createBatch("LETTER_GENERATION", count);

		try {
			letterGenerationJob.start(batchID);
		} catch (Exception e) {
			batchJobDAO.deleteBatch(batchID);
		}

	}

	public Letter generate(long letterID, Date appDate) {
		GenerateLetter gl = autoLetterGenerationDAO.getLetter(letterID);

		LetterType letterType = LetterType.getType(gl.getLetterType());

		if (letterType == null) {
			return null;
		}

		long templateId = gl.getAgreementTemplate();

		Long emailtemplateId = gl.getEmailTemplate();

		AgreementDefinition ad = agreementDefinitionDAO.getTemplate(templateId);

		Letter letter = new Letter();
		letter.setFinID(gl.getFinID());
		letter.setSaveFormat(SaveFormat.PDF);
		letter.setLetterDesc(ad.getAggDesc());
		letter.setLetterType(gl.getLetterType());
		letter.setLetterMode(gl.getModeofTransfer());
		letter.setCreatedDate(gl.getCreatedDate());
		letter.setAppDate(appDate);
		letter.setEmailTemplate(emailtemplateId);

		LetterMode letterMode = LetterMode.getMode(letter.getLetterMode());

		if (letterMode == LetterMode.EMAIL) {
			letter.setMailTemplate(mailTemplateDAO.getMailTemplateById(letter.getEmailTemplate(), "_AView"));
		}

		setLetterName(letter);

		System.out.println();

		setData(letter);

		String templatePath = null;
		switch (LetterType.valueOf(gl.getLetterType())) {
		case NOC:
			templatePath = PathUtil.NOC_LETTER;
			break;
		case CANCELLATION:
			templatePath = PathUtil.CANCELLED_LETTER;
			break;
		case CLOSURE:
			templatePath = PathUtil.CLOUSER_LETTER;
			break;
		default:
			break;
		}

		templatePath = PathUtil.getPath(templatePath);

		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			TemplateEngine engine = new TemplateEngine(templatePath, templatePath);
			engine.setTemplate(ad.getAggName().concat(".docx"));
			engine.loadTemplate();
			engine.mergeFields(letter);

			engine.getDocument().save(os, letter.getSaveFormat());
			letter.setContent(os.toByteArray());

			return letter;
		} catch (Exception e) {
			throw new AppException("LetterService", e);
		}
	}

	public void sendEmail(Letter letter) {
		MailTemplate mailTemplate = letter.getMailTemplate();
		if (mailTemplate == null) {
			return;
		}

		try {
			notificationService.parseMail(mailTemplate, letter);
		} catch (Exception e) {
		}

		Notification emailMessage = new Notification();
		emailMessage.setKeyReference(letter.getFinReference());
		emailMessage.setModule(letter.getLetterType());
		emailMessage.setSubModule(letter.getLetterType());
		emailMessage.setSubject(mailTemplate.getEmailSubject());
		emailMessage.setContent(mailTemplate.getEmailMessage().getBytes(Charset.forName("UTF-8")));

		if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(mailTemplate.getEmailFormat())) {
			emailMessage.setContentType(EmailBodyType.HTML.getKey());
		} else {
			emailMessage.setContentType(EmailBodyType.PLAIN.getKey());
		}

		MessageAddress address = new MessageAddress();
		address.setEmailId(letter.getEmail());
		address.setRecipientType(RecipientType.TO.getKey());
		emailMessage.getAddressesList().add(address);

		try {
			emailEngine.sendEmail(emailMessage);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("Unable to save the email notification", e);
		}
	}

	public void storeLetter(Letter letter) {
		LetterMode letterMode = LetterMode.getMode(letter.getLetterMode());

		if (letterMode == null) {
			return;
		}

		String csdCode = "";
		csdCode = autoLetterGenerationDAO.getCSDCode(letter.getFinType(), letter.getFinBranch());
		letter.setCsdCode(csdCode);
		Date createdDate = letter.getCreatedDate();

		String fileName = letter.getFileName();
		String remotePath = csdCode.concat(File.separator).concat(DateUtil.format(createdDate, "ddMMyyyy"));
		byte[] fileContent = letter.getContent();

		EventProperties ep = autoLetterGenerationDAO.getEventProperties("CSD_STORAGE");

		String host = ep.getHostName();
		String port = ep.getPort();
		String username = ep.getAccessKey();
		String password = ep.getSecretKey();
		String privateKey = ep.getPrivateKey();

		try {
			FTPUtil.writeBytesToFTP(host, port, username, password, privateKey, remotePath, fileName, fileContent);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setLetterName(Letter letter) {
		Date appDate = letter.getAppDate();
		Date createdDate = letter.getCreatedDate();

		StringBuilder builder = new StringBuilder();
		builder.append(letter.getFinReference());

		LetterType letterType = LetterType.getType(letter.getLetterType());

		switch (letterType) {
		case NOC:
			builder.append("NOC");
			break;
		case CANCELLATION:
			builder.append("CAN");
			break;
		case CLOSURE:
			builder.append("CL");
			break;
		default:
			break;
		}

		letter.setSequence(autoLetterGenerationDAO.getNextSequence(letter.getFinID(), letterType));

		builder.append(DateUtil.format(appDate, "ddMMyyyy"));
		builder.append(letter.getSequence());
		builder.append(".");

		int saveFormat = letter.getSaveFormat();
		if (saveFormat == SaveFormat.PDF) {
			builder.append("pdf");
		} else if (saveFormat == SaveFormat.DOCX) {
			builder.append("docx");
		} else if (saveFormat == SaveFormat.DOC) {
			builder.append("doc");
		}

		letter.setLetterName(builder.toString());

	}

	private void setData(Letter letter) {
		FinanceDetail fd = financeEnquiryService.getLoanBasicDetails(letter.getFinID());

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		letter.setCustFullName(fm.getLoanName());
		letter.setFinStartDate(DateUtil.format(fm.getFinStartDate(), DateFormat.LONG_DATE));

		letter.setFinBranch(fm.getFinBranch());
		letter.setFinType(fm.getFinType());
		letter.setFinTypeDesc(fm.getLovDescFinTypeName());

		List<CustomerEMail> customerEMailList = fd.getCustomerDetails().getCustomerEMailList();

		if (!customerEMailList.isEmpty()) {
			CustomerEMail customerEMail = customerEMailList.get(0);
			letter.setEmail(customerEMail.getCustEMail());
		}

	}

	@Autowired
	public void setAutoLetterGenerationDAO(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
	}

	@Autowired
	public void setAgreementDefinitionDAO(AgreementDefinitionDAO agreementDefinitionDAO) {
		this.agreementDefinitionDAO = agreementDefinitionDAO;
	}

	@Autowired
	public void setEmailEngine(EmailEngine emailEngine) {
		this.emailEngine = emailEngine;
	}

	@Autowired
	public void setMailTemplateDAO(MailTemplateDAO mailTemplateDAO) {
		this.mailTemplateDAO = mailTemplateDAO;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setFinanceEnquiryService(FinanceEnquiryService financeEnquiryService) {
		this.financeEnquiryService = financeEnquiryService;
	}

	@Autowired
	public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
		this.batchJobDAO = batchJobDAO;
	}

	@Autowired
	public void setLetterGenerationJob(LetterGenerationJob letterGenerationJob) {
		this.letterGenerationJob = letterGenerationJob;
	}

}
