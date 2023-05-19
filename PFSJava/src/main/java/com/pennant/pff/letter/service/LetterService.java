package com.pennant.pff.letter.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.letter.Letter;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.pff.letter.LetterMode;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
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

	public Letter generate(long letterID) {
		GenerateLetter gl = autoLetterGenerationDAO.getLetter(letterID);

		LetterType letterType = LetterType.getType(gl.getLetterType());

		if (letterType == null) {
			return null;
		}

		long templateId = gl.getAgreementTemplate();

		AgreementDefinition ad = agreementDefinitionDAO.getTemplate(templateId);

		int saveFormat = SaveFormat.PDF;

		if (PennantConstants.DOC_TYPE_WORD.equals(ad.getAggtype())) {
			saveFormat = SaveFormat.DOC;
		}

		Letter letter = new Letter();
		letter.setSaveFormat(saveFormat);
		letter.setLetterName(ad.getAggName());
		letter.setLetterDesc(ad.getAggDesc());
		letter.setLetterType(gl.getLetterType());
		letter.setLetterMode(gl.getModeofTransfer());

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
			engine.setTemplate(ad.getAggName());
			engine.loadTemplate();
			engine.mergeFields(letter);

			engine.getDocument().save(os, saveFormat);
			letter.setContent(os.toByteArray());

			return letter;
		} catch (Exception e) {
			throw new AppException("", e);
		}
	}

	public void sendEmail(Letter letter) {
		LetterMode letterMode = LetterMode.getMode(letter.getLetterMode());

		if (letterMode == null || letterMode != LetterMode.EMAIL || letter.getEmail() == null) {
			return;
		}

		MailTemplate template = mailTemplateDAO.getMailTemplateById(letter.getEmailTemplate(), "_AView");

		try {
			notificationService.parseMail(template, letter);
		} catch (Exception e) {
		}

		Notification emailMessage = new Notification();
		emailMessage.setKeyReference(letter.getFinReference());
		emailMessage.setModule(letter.getLetterType());
		emailMessage.setSubModule(letter.getLetterType());
		emailMessage.setSubject(template.getEmailSubject());
		emailMessage.setContent(template.getEmailMessage().getBytes(Charset.forName("UTF-8")));

		if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(template.getEmailFormat())) {
			emailMessage.setContentType(EmailBodyType.HTML.getKey());
		} else {
			emailMessage.setContentType(EmailBodyType.PLAIN.getKey());
		}

		for (String emailId : template.getEmailIds()) {
			MessageAddress address = new MessageAddress();
			address.setEmailId(emailId);
			address.setRecipientType(RecipientType.TO.getKey());
			emailMessage.getAddressesList().add(address);
		}

		try {
			emailEngine.sendEmail(emailMessage);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("Unable to save the email notification", e);
		}
	}

	public void storeLetter(Letter letter) {
		LetterMode letterMode = LetterMode.getMode(letter.getLetterMode());

		if (letterMode == null || letterMode != LetterMode.COURIER) {
			return;
		}

	}

	private void setData(Letter letter) {
		FinanceDetail fd = new FinanceDetail();

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

}
