package com.pennant.pff.letter.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.finance.FinanceEnquiryService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.letter.LetterMode;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.net.FTPUtil;
import com.pennanttech.pennapps.core.net.Protocol;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

public class LetterService {
	private static final Logger logger = LogManager.getLogger(LetterService.class);

	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;
	private AutoLetterGenerationDAO autoLetterGenerationDAO;
	private AgreementDefinitionDAO agreementDefinitionDAO;
	private EmailEngine emailEngine;
	private MailTemplateDAO mailTemplateDAO;
	private NotificationService notificationService;
	private FinanceEnquiryService financeEnquiryService;
	private FinFeeDetailDAO finFeeDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;

	public void logForAutoLetter(FinanceMain fm, Date appDate) {
		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingDAO.getLetterMapping(fm.getFinType());

		for (LoanTypeLetterMapping ltlp : letterMapping) {

			if (!ltlp.isAutoGeneration()) {
				continue;
			}

			LetterType letterType = LetterType.getType(ltlp.getLetterType());

			if ((letterType == LetterType.CANCELLATION
					&& FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus()))
					|| (letterType == LetterType.NOC || letterType == LetterType.CLOSURE)
							&& FinanceUtil.isClosedNow(fm)) {

				GenerateLetter gl = new GenerateLetter();
				gl.setFinID(fm.getFinID());
				gl.setRequestType("A");
				gl.setLetterType(ltlp.getLetterType());
				gl.setCreatedDate(appDate);
				gl.setCreatedBy(ltlp.getCreatedBy());
				gl.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				gl.setAgreementTemplate(ltlp.getAgreementCodeId());
				gl.setModeofTransfer(ltlp.getLetterMode());

				autoLetterGenerationDAO.save(gl);
			}
		}
	}

	public LoanLetter generate(long letterID, Date appDate) {
		LoanLetter loanLetter = new LoanLetter();

		GenerateLetter gl = autoLetterGenerationDAO.getLetter(letterID);
		if (autoLetterGenerationDAO.getCountBlockedItems(gl.getFinID()) > 0) {
			loanLetter.setBlocked(true);
		}

		LetterType letterType = LetterType.getType(gl.getLetterType());

		if (letterType == null || loanLetter.isBlocked()) {
			return loanLetter;
		}

		long templateId = gl.getAgreementTemplate();

		Long emailtemplateId = gl.getEmailTemplate();

		AgreementDefinition ad = agreementDefinitionDAO.getTemplate(templateId);

		loanLetter.setId(letterID);
		loanLetter.setFinID(gl.getFinID());
		loanLetter.setSaveFormat(SaveFormat.PDF);
		loanLetter.setLetterDesc(ad.getAggDesc());
		loanLetter.setLetterType(gl.getLetterType());
		loanLetter.setLetterMode(gl.getModeofTransfer());
		loanLetter.setCreatedDate(gl.getCreatedDate());
		loanLetter.setAppDate(appDate);
		loanLetter.setEmailTemplate(emailtemplateId);

		LetterMode letterMode = LetterMode.getMode(loanLetter.getLetterMode());

		if (letterMode == LetterMode.EMAIL) {
			loanLetter.setMailTemplate(mailTemplateDAO.getMailTemplateById(loanLetter.getEmailTemplate(), "_AView"));
		}

		setData(loanLetter);

		String finBranch = loanLetter.getFinBranch();
		String finType = loanLetter.getFinType();
		loanLetter.setServiceBranch(autoLetterGenerationDAO.getServiceBranch(finType, finBranch));
		loanLetter.setEventProperties(autoLetterGenerationDAO.getEventProperties("CSD_STORAGE"));

		setLetterName(loanLetter);

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
			engine.mergeFields(loanLetter);

			engine.getDocument().save(os, loanLetter.getSaveFormat());
			loanLetter.setContent(os.toByteArray());

			return loanLetter;
		} catch (Exception e) {
			throw new AppException("LetterService", e);
		}
	}

	public void sendEmail(LoanLetter letter) {
		MailTemplate mailTemplate = letter.getMailTemplate();
		if (mailTemplate == null) {
			return;
		}

		try {
			notificationService.parseMail(mailTemplate, letter);
		} catch (Exception e) {
			throw new AppException("LetterService", e);
		}

		Notification emailMessage = new Notification();
		emailMessage.setKeyReference(letter.getFinReference());
		emailMessage.setModule(letter.getLetterType());
		emailMessage.setSubModule(letter.getLetterType());
		emailMessage.setSubject(mailTemplate.getEmailSubject());
		emailMessage.setContent(mailTemplate.getEmailMessage().getBytes(StandardCharsets.UTF_8));

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

			letter.setTrackingID(emailMessage.getId());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("Unable to save the email notification", e);
		}
	}

	public void storeLetter(LoanLetter letter) {
		LetterMode letterMode = LetterMode.getMode(letter.getLetterMode());

		if (letterMode == null) {
			return;
		}

		ServiceBranch serviceBranch = letter.getServiceBranch();

		if (serviceBranch == null) {
			throw new AppException("Customer Service Branch not found found with " + letter.getFinType() + " and "
					+ letter.getFinBranch());
		}

		EventProperties ep = letter.getEventProperties();

		if (ep == null) {
			throw new AppException(
					"There is no FTP/SFTP/S-3 Storage details not configured with Data-Engine Name CSD_STORAGE");
		}

		String csdCode = serviceBranch.getCode();
		String parentFolder = serviceBranch.getFolderPath();

		Date appDate = letter.getAppDate();

		String fileName = letter.getLetterName();
		String remotePath = parentFolder.concat(File.separator).concat(csdCode).concat(File.separator)
				.concat(DateUtil.format(appDate, "ddMMyyyy"));
		byte[] fileContent = letter.getContent();

		String host = ep.getHostName();
		String port = ep.getPort();
		String username = ep.getAccessKey();
		String password = ep.getSecretKey();

		remotePath = ep.getBucketName().concat(File.separator).concat(remotePath);

		try {
			FTPUtil.writeBytesToFTP(Protocol.SFTP, host, port, username, password, remotePath, fileName, fileContent);
		} catch (Exception e) {
			throw new AppException("LetterService", e);
		}
	}

	public void update(LoanLetter letter) {
		GenerateLetter gl = new GenerateLetter();

		long letterID = letter.getId();
		gl.setId(letterID);
		gl.setFeeID(letter.getFeeID());
		gl.setGeneratedDate(letter.getAppDate());
		gl.setGeneratedOn(new Timestamp(System.currentTimeMillis()));
		gl.setAdviseID(letter.getAdviseID());
		gl.setTrackingID(letter.getTrackingID());
		gl.setStatus(letter.getStatus());
		gl.setRemarks(letter.getRemarks());

		autoLetterGenerationDAO.update(gl);

		autoLetterGenerationDAO.moveFormStage(letterID);

		autoLetterGenerationDAO.deleteFromStage(letterID);

	}

	private void setLetterName(LoanLetter letter) {
		Date appDate = letter.getAppDate();

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

	private void setData(LoanLetter letter) {
		FinanceDetail fd = financeEnquiryService.getLoanBasicDetails(letter.getFinID());

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		letter.setFinReference(fm.getFinReference());
		letter.setCustFullName(fm.getLoanName());
		letter.setFinStartDate(DateUtil.format(fm.getFinStartDate(), DateFormat.LONG_DATE));
		letter.setStrAppDate(DateUtil.format(letter.getAppDate(), DateFormat.LONG_DATE));

		letter.setFinBranch(fm.getFinBranch());
		letter.setFinType(fm.getFinType());
		letter.setFinTypeDesc(fm.getLovDescFinTypeName());

		List<CustomerEMail> customerEMailList = fd.getCustomerDetails().getCustomerEMailList();

		if (!customerEMailList.isEmpty()) {
			CustomerEMail customerEMail = customerEMailList.get(0);
			letter.setEmail(customerEMail.getCustEMail());
		}

	}

	public void createAdvise(LoanLetter letter) {
		logger.debug(Literal.ENTERING);

		Long feetID = letter.getFeeID();

		if (feetID == null) {
			return;
		}

		FinFeeDetail finFeeDetail = finFeeDetailDAO.getFinFeeDetail(feetID);

		BigDecimal remainingFee = finFeeDetail.getRemainingFee();

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(AdviseType.RECEIVABLE.id());
		manualAdvise.setFinReference(letter.getFinReference());
		manualAdvise.setFeeTypeID(finFeeDetail.getFeeTypeID());
		manualAdvise.setAdviseAmount(remainingFee);
		manualAdvise.setRemarks("Advise For " + letter.getLetterType() + " Letter Generation Charges");
		manualAdvise.setValueDate(letter.getAppDate());
		manualAdvise.setPostDate(letter.getAppDate());
		manualAdvise.setBalanceAmt(remainingFee);

		manualAdvise.setVersion(0);
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setWorkflowId(0);

		manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setLoanTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
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
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

}
