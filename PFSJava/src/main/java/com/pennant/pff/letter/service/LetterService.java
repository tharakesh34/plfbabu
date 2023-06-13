package com.pennant.pff.letter.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
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
import com.pennant.pff.receipt.ClosureType;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.net.FTPUtil;
import com.pennanttech.pennapps.core.net.Protocol;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.AttachmentType;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.core.util.LoanCancelationUtil;
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
	private BranchDAO branchDAO;

	public void logForAutoLetter(FinanceMain fm, Date appDate) {
		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingDAO.getLetterMapping(fm.getFinType());

		for (LoanTypeLetterMapping ltlp : letterMapping) {

			if (!ltlp.isAutoGeneration()) {
				continue;
			}

			String closureType = fm.getClosureType();

			LetterType letterType = LetterType.getType(ltlp.getLetterType());

			if ((letterType == LetterType.NOC || letterType == LetterType.CLOSURE)
					&& !(ClosureType.isClosure(closureType) || ClosureType.isForeClosure(closureType))) {
				continue;
			}

			if ((letterType == LetterType.CANCELLATION
					&& FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())
					&& !LoanCancelationUtil.LOAN_CANCEL_REBOOK.equals(fm.getCancelType()))
					|| ((letterType == LetterType.NOC || letterType == LetterType.CLOSURE)
							&& FinanceUtil.isClosedNow(fm))) {

				GenerateLetter gl = new GenerateLetter();
				gl.setFinID(fm.getFinID());
				gl.setRequestType("A");
				gl.setLetterType(ltlp.getLetterType());
				gl.setCreatedDate(appDate);
				gl.setCreatedBy(ltlp.getCreatedBy());
				gl.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				gl.setAgreementTemplate(ltlp.getAgreementCodeId());
				gl.setEmailTemplate(ltlp.getEmailTemplateId());
				gl.setModeofTransfer(ltlp.getLetterMode());

				autoLetterGenerationDAO.save(gl);
			}
		}
	}

	public LoanLetter generate(long letterID, Date appDate) {
		LoanLetter loanLetter = new LoanLetter();

		GenerateLetter gl = autoLetterGenerationDAO.getLetter(letterID);

		String requestType = gl.getRequestType();

		loanLetter.setId(letterID);
		loanLetter.setFinID(gl.getFinID());
		loanLetter.setSaveFormat(SaveFormat.PDF);
		loanLetter.setLetterType(gl.getLetterType());
		loanLetter.setLetterMode(gl.getModeofTransfer());
		loanLetter.setModeofTransfer(gl.getModeofTransfer());
		loanLetter.setCreatedDate(gl.getCreatedDate());
		loanLetter.setBusinessDate(appDate);
		loanLetter.setRequestType(requestType);
		loanLetter.setFeeID(gl.getFeeID());

		if (autoLetterGenerationDAO.getCountBlockedItems(gl.getFinID()) > 0
				&& !(LetterMode.OTC.name().equals(requestType) || "M".equals(requestType))) {
			loanLetter.setBlocked(true);
			return loanLetter;
		}

		LetterType letterType = LetterType.getType(gl.getLetterType());

		if (letterType == null || loanLetter.isBlocked()) {
			return loanLetter;
		}

		long templateId = gl.getAgreementTemplate();

		Long emailtemplateId = gl.getEmailTemplate();

		AgreementDefinition ad = agreementDefinitionDAO.getTemplate(templateId);

		loanLetter.setLetterDesc(ad.getAggDesc());

		loanLetter.setEmailTemplate(emailtemplateId);

		LetterMode letterMode = LetterMode.getMode(loanLetter.getLetterMode());

		if (letterMode == LetterMode.EMAIL) {
			loanLetter.setMailTemplate(mailTemplateDAO.getMailTemplateById(emailtemplateId, "_AView"));
		}

		setData(loanLetter);

		setServiceBranchData(loanLetter);

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
			engine.setTemplate(ad.getAggReportName().concat(".docx"));
			engine.loadTemplate();
			engine.mergeFields(loanLetter);

			engine.getDocument().save(os, loanLetter.getSaveFormat());
			loanLetter.setContent(os.toByteArray());

			return loanLetter;
		} catch (Exception e) {
			throw new AppException("LetterService", e);
		}
	}

	private void setServiceBranchData(LoanLetter loanLetter) {
		String finBranch = loanLetter.getFinBranch();
		String finType = loanLetter.getFinType();

		ServiceBranch serviceBranch = autoLetterGenerationDAO.getServiceBranch(finType, finBranch);

		if (serviceBranch == null) {
			throw new AppException("Customer Service Branch not found with Loan Type [" + finType + "] and Fin Barnch ["
					+ finBranch + "]");
		}

		loanLetter.setServiceBranch(serviceBranch);
		loanLetter.setCsbCode(serviceBranch.getCode());
		loanLetter.setCsbDescription(serviceBranch.getDescription());
		loanLetter.setCsbHouseNo(serviceBranch.getOfcOrHouseNum());
		loanLetter.setCsbFlatNo(serviceBranch.getFlatNum());
		loanLetter.setCsbStreet(serviceBranch.getStreet());
		loanLetter.setCsbAddrL1(serviceBranch.getAddrLine1());
		loanLetter.setCsbAddrL2(serviceBranch.getAddrLine2());
		loanLetter.setCsbPoBox(serviceBranch.getPoBox());
		loanLetter.setCsbCounty(serviceBranch.getCountry());
		loanLetter.setCsbState(serviceBranch.getCpProvince());
		loanLetter.setCsbCity(serviceBranch.getCity());
		loanLetter.setCsbPinCode(serviceBranch.getPinCode());
		loanLetter.setCsbFolderPath(serviceBranch.getFolderPath());
	}

	public void sendEmail(LoanLetter letter) {
		MailTemplate mailTemplate = letter.getMailTemplate();
		if (mailTemplate == null) {
			return;
		}

		if (letter.getEmailID() == null) {
			letter.setLetterMode(LetterMode.COURIER.name());
			letter.setModeofTransfer(LetterMode.COURIER.name());
			letter.setRemarks(
					"Since the Email-ID not available, Letter Mode is marked as COURIER, and store the document.");
			return;
		}

		try {
			notificationService.parseMail(mailTemplate, letter.getDeclaredFieldValues());
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
		address.setEmailId(letter.getEmailID());
		address.setRecipientType(RecipientType.TO.getKey());
		emailMessage.getAddressesList().add(address);

		List<MessageAttachment> attachments = new ArrayList<>();

		MessageAttachment attachement = new MessageAttachment(letter.getFileName(), AttachmentType.TEXT);
		attachement.setAttachment(letter.getContent());
		attachments.add(attachement);

		emailMessage.setAttachmentList(attachments);

		try {
			emailEngine.sendEmail(emailMessage);

			letter.setEmailNotificationID(emailMessage.getId());
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

		if (letterMode == LetterMode.EMAIL && letter.getEmailID() != null) {
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

		String parentFolder = serviceBranch.getFolderPath();
		String csdCode = serviceBranch.getCode();
		Date appDate = letter.getBusinessDate();

		String letterLocation = csdCode.concat(File.separator).concat(DateUtil.format(appDate, "ddMMyyyy"));

		if ("A".equals(letter.getRequestType())) {
			letterLocation = letterLocation.concat(File.separator).concat("Closure");
		} else if ("M".equals(letter.getRequestType())) {
			letterLocation = letterLocation.concat(File.separator).concat("Request");
		} else if ("D".equals(letter.getRequestType())) {
			letterLocation = letterLocation.concat(File.separator).concat("Delink");
		}

		String fileName = letter.getFileName();
		String remotePath = parentFolder.concat(File.separator).concat(letterLocation);
		byte[] fileContent = letter.getContent();

		String host = ep.getHostName();
		String port = ep.getPort();
		String username = ep.getAccessKey();
		String password = ep.getSecretKey();

		remotePath = ep.getBucketName().concat(File.separator).concat(remotePath);

		letter.setLetterLocation(letterLocation);

		try {
			FTPUtil.writeBytesToFTP(Protocol.SFTP, host, port, username, password, remotePath, fileName, fileContent);
		} catch (Exception e) {
			throw new AppException("LetterService", e);
		}
	}

	public void update(LoanLetter letter) {
		long letterID = letter.getId();

		letter.setGeneratedDate(letter.getBusinessDate());
		letter.setGeneratedOn(new Timestamp(System.currentTimeMillis()));

		if (PFSBatchAdmin.loggedInUser != null) {
			letter.setApprovedBy(PFSBatchAdmin.loggedInUser.getUserId());
			letter.setGeneratedBy(PFSBatchAdmin.loggedInUser.getUserId());
		}

		letter.setApprovedOn(new Timestamp(System.currentTimeMillis()));

		autoLetterGenerationDAO.update(letter);

		autoLetterGenerationDAO.moveFormStage(letterID);

		autoLetterGenerationDAO.deleteFromStage(letterID);

	}

	private void setLetterName(LoanLetter letter) {
		Date appDate = letter.getBusinessDate();

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

		int seqNo = autoLetterGenerationDAO.getNextSequence(letter.getFinID(), letterType);
		String letterSeqNo = StringUtils.leftPad(String.valueOf(seqNo), 3, "0");

		letter.setSequence(seqNo);
		letter.setLetterSeqNo(letterSeqNo);

		builder.append(DateUtil.format(appDate, "ddMMyyyy"));
		builder.append(letterSeqNo);

		letter.setLetterName(builder.toString());

		builder.append(".");

		int saveFormat = letter.getSaveFormat();
		if (saveFormat == SaveFormat.PDF) {
			builder.append("pdf");
		} else if (saveFormat == SaveFormat.DOCX) {
			builder.append("docx");
		} else if (saveFormat == SaveFormat.DOC) {
			builder.append("doc");
		}

		letter.setFileName(builder.toString());

	}

	private void setCustomerData(LoanLetter letter, CustomerDetails customerDetails) {
		Customer customer = customerDetails.getCustomer();

		letter.setCustCif(StringUtils.trimToEmpty(customer.getCustCIF()));
		letter.setCustCoreBank(StringUtils.trimToEmpty(customer.getCustCoreBank()));
		letter.setSalutation(StringUtils.trimToEmpty(customer.getCustGenderCode()));
		letter.setCustShrtName(StringUtils.trimToEmpty(customer.getCustShrtName()));

		List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();

		if (!customerEMailList.isEmpty() && LetterMode.EMAIL.name().equals(letter.getLetterMode())) {
			CustomerEMail customerEMail = customerEMailList.get(0);
			letter.setEmailID(customerEMail.getCustEMail());
		}

		List<CustomerAddres> customerAddresList = customerDetails.getAddressList();

		if (!customerAddresList.isEmpty()) {
			CustomerAddres ca = customerAddresList.get(0);

			letter.setCustCountry(StringUtils.trimToEmpty(ca.getCustAddrCountry()));
			letter.setCustFlatNo(StringUtils.trimToEmpty(ca.getCustFlatNbr()));
			letter.setCustLandMark(StringUtils.trimToEmpty(ca.getCustAddrLine1()));
			letter.setCustCareOf(StringUtils.trimToEmpty(ca.getCustAddrLine3()));
			letter.setCustDistrict(StringUtils.trimToEmpty(ca.getCustDistrict()));
			letter.setCustSubDistrict(StringUtils.trimToEmpty(ca.getCustAddrLine4()));
			letter.setCustHouseBullingNo(StringUtils.trimToEmpty(ca.getCustAddrHNbr()));
			letter.setCustLocalty(StringUtils.trimToEmpty(ca.getCustAddrLine2()));
			letter.setCustPoBox(StringUtils.trimToEmpty(ca.getCustPOBox()));
			letter.setCustState(StringUtils.trimToEmpty(ca.getCustAddrProvince()));
			letter.setCustPinCode(StringUtils.trimToEmpty(ca.getCustAddrZIP()));
			letter.setCustStreet(StringUtils.trimToEmpty(ca.getCustAddrStreet()));
			letter.setCustCity(StringUtils.trimToEmpty(ca.getCustAddrCity()));
		}

	}

	private void setData(LoanLetter letter) {
		FinanceDetail fd = financeEnquiryService.getLoanBasicDetails(letter.getFinID());

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		letter.setFinReference(fm.getFinReference());
		letter.setCustFullName(fm.getLoanName());
		letter.setFinStartDate(DateUtil.format(fm.getFinStartDate(), DateFormat.LONG_DATE));
		letter.setAppDate(DateUtil.format(letter.getBusinessDate(), DateFormat.LONG_DATE));
		letter.setClosureDate(DateUtil.format(fm.getClosedDate(), DateFormat.LONG_DATE));

		letter.setFinBranch(fm.getFinBranch());
		letter.setFinType(fm.getFinType());
		letter.setFinTypeDesc(fm.getLovDescFinTypeName());

		CustomerDetails customerDetails = fd.getCustomerDetails();

		setCustomerData(letter, customerDetails);

		Branch branch = branchDAO.getBranchById(letter.getFinBranch(), "_AView");
		if (branch != null) {
			letter.setFbCode(StringUtils.trimToEmpty(branch.getBranchCode()));
			letter.setFbDescription(StringUtils.trimToEmpty(branch.getBranchDesc()));
			letter.setFbCounty(StringUtils.trimToEmpty(branch.getBranchCountry()));
			letter.setFbCity(StringUtils.trimToEmpty(branch.getBranchCity()));
			letter.setFbFax(StringUtils.trimToEmpty(branch.getBranchFax()));
			letter.setFbHouseNo(StringUtils.trimToEmpty(branch.getBranchAddrHNbr()));
			letter.setFbPoBox(StringUtils.trimToEmpty(branch.getBranchPOBox()));
			letter.setFbStreet(StringUtils.trimToEmpty(branch.getBranchAddrStreet()));
			letter.setFbTelePhone(StringUtils.trimToEmpty(branch.getBranchTel()));
			letter.setFbFlatNo(StringUtils.trimToEmpty(branch.getBranchFlatNbr()));
			letter.setFbState(StringUtils.trimToEmpty(branch.getLovDescBranchProvinceName()));

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

		if (remainingFee.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(AdviseType.RECEIVABLE.id());
		manualAdvise.setFinID(letter.getFinID());
		manualAdvise.setFinReference(letter.getFinReference());
		manualAdvise.setFeeTypeID(finFeeDetail.getFeeTypeID());
		manualAdvise.setAdviseAmount(remainingFee);
		manualAdvise.setRemarks("Advise For " + letter.getLetterType() + " Letter Generation Charges");
		manualAdvise.setValueDate(letter.getBusinessDate());
		manualAdvise.setPostDate(letter.getBusinessDate());
		manualAdvise.setBalanceAmt(remainingFee);

		manualAdvise.setVersion(1);
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setLastMntBy(letter.getApprovedBy());
		manualAdvise.setWorkflowId(0);

		manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);

		finFeeDetail.setPaidAmount(remainingFee);
		finFeeDetail.setPaidAmountOriginal(remainingFee.add(finFeeDetail.getRemainingFeeGST()));
		finFeeDetail.setRemainingFeeOriginal(BigDecimal.ZERO);
		finFeeDetail.setRemainingFee(BigDecimal.ZERO);
		finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);

		finFeeDetailDAO.update(finFeeDetail, false, "");

		letter.setAdviseID(manualAdvise.getAdviseID());

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

	@Autowired
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

}
