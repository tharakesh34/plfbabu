package com.pennanttech.pff.notifications.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.SysNotificationDetails;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.MailTemplateData;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.drawingpower.DrawingPowerService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.NotificationAttribute;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pennapps.notification.sms.SmsEngine;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.DataMapUtil;
import com.pennanttech.pff.core.util.DataMapUtil.FieldPrefix;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.sms.PresentmentBounceService;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class NotificationService extends GenericService<Notification> {
	private static final Logger logger = LogManager.getLogger(NotificationService.class);

	private Configuration freemarkerMailConfiguration;
	private MailTemplateDAO mailTemplateDAO;
	private NotificationsDAO notificationsDAO;
	private NotesDAO notesDAO;
	private SecurityRoleDAO securityRoleDAO;
	private SecurityUserDAO securityUserDAO;
	private ReportingManagerDAO reportingManagerDAO;
	private WorkFlowDetailsDAO workFlowDetailsDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private BranchDAO branchDAO;
	private VehicleDealerDAO vehicleDealerDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private EmailEngine emailEngine;
	private SmsEngine smsEngine;
	private DrawingPowerService drawingPowerService;
	private DocumentTypeDAO documentTypeDAO;

	private PresentmentBounceService presentmentBounceService;
	private final String ERROR_MESSAGE = "Unable to parse the email template, please check the configured email template";

	public NotificationService() {
		super();
	}

	public void sendNotification(Notification mailKeyData, Object object) throws AppException {
		logger.debug(Literal.ENTERING);
		Map<String, Object> data = null;

		String code = mailKeyData.getTemplateCode();
		MailTemplate template = mailTemplateDAO.getMailTemplateByCode(code, "_AView");

		if (template == null || !template.isActive()) {
			return;
		}

		if (object instanceof QueryDetail) {
			QueryDetail queryDetail = (QueryDetail) object;
			data = getTemplateData(queryDetail);
		} else if (object instanceof PresentmentDetail) {
			if (presentmentBounceService != null && presentmentBounceService.getTemplateCode(code) != null) {
				mailKeyData.setTemplateCode(presentmentBounceService.getTemplateCode(code));
			}
			data = getTemplateData((PresentmentDetail) object);
		} else if (object instanceof FinanceDetail) {
			FinanceDetail financeDetail = (FinanceDetail) object;
			data = getTemplateData(financeDetail, null);
		} else if (object instanceof LimitHeader) {
			LimitHeader limitHeader = (LimitHeader) object;
			data = getTemplateData(limitHeader, mailKeyData);
		}

		Map<String, byte[]> attachements = mailKeyData.getAttachments();

		try {
			parseMail(template, data);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("999", ERROR_MESSAGE);
		}

		if (MapUtils.isNotEmpty(attachements)) {
			template.setAttchments(attachements);
		}

		Notification message = prepareNotification(mailKeyData, 0, template);

		if (template.isEmailTemplate()) {
			sendEmailNotification(message);
			mailKeyData.setId(message.getId());
		}

		if (template.isSmsTemplate()) {
			sendSmsNotification(message);
			mailKeyData.setId(message.getId());
		}

		logger.debug(Literal.LEAVING);
	}

	public void sendNotifications(String moduleCode, Object object) throws AppException {
		logger.debug(Literal.ENTERING);

		Notification notification = new Notification();
		List<DocumentDetails> documents = null;

		String keyReference = null;
		String role = null;
		notification.setModule(moduleCode);
		notification.setSubModule(moduleCode);

		FinanceDetail financeDetail;
		FinanceMain financeMain;
		if (object instanceof FinanceDetail) {
			financeDetail = (FinanceDetail) object;
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			documents = financeDetail.getDocumentDetailsList();
			keyReference = financeMain.getFinReference();
			role = financeMain.getRoleCode();
		} else if (object instanceof Facility) {
			Facility facility = (Facility) object;
			documents = facility.getDocumentDetailsList();
			keyReference = facility.getCAFReference();
			role = facility.getRoleCode();
		} else if (object instanceof FinCreditReviewDetails) {
			FinCreditReviewDetails finCreditReviewDetails = (FinCreditReviewDetails) object;
			keyReference = finCreditReviewDetails.getLovDescCustCIF();
		} else if (object instanceof RepayData) {
			RepayData repayData = (RepayData) object;
			financeMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			documents = repayData.getFinanceDetail().getDocumentDetailsList();
			keyReference = repayData.getFinReference();
			role = financeMain.getRoleCode();
		} else if (object instanceof FinanceWriteoffHeader) {
			FinanceWriteoffHeader writeoffHeader = (FinanceWriteoffHeader) object;
			financeMain = writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();
			keyReference = writeoffHeader.getFinReference();
			role = financeMain.getRoleCode();
		} else if (object instanceof Provision) {
			Provision provision = (Provision) object;
			keyReference = provision.getFinReference();
			role = provision.getRoleCode();
		} else if (object instanceof FinanceSuspHead) {
			FinanceSuspHead suspHead = (FinanceSuspHead) object;
			keyReference = suspHead.getFinReference();
			role = suspHead.getRoleCode();
		} else if (object instanceof QueryDetail) {
			QueryDetail queryDetail = (QueryDetail) object;
			documents = queryDetail.getDocumentDetailsList();
			keyReference = queryDetail.getFinReference();
			role = queryDetail.getRoleCode();
		} else if (object instanceof VehicleDealer) {
			VehicleDealer vehicleDealer = (VehicleDealer) object;
			keyReference = String.valueOf(vehicleDealer.getCode());
			role = vehicleDealer.getRoleCode();
		}

		notification.setKeyReference(keyReference);
		notification.setStage(role);

		try {
			sendNotifications(notification, object, null, documents);
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	public void sendNotifications(Notification mailKeyData, Object object, String finType,
			List<DocumentDetails> documents) throws AppException {
		boolean resendNotifications = false;
		String finReference = mailKeyData.getKeyReference();
		String finEvent = mailKeyData.getSubModule();
		String role = mailKeyData.getStage();
		String module = mailKeyData.getModule();
		List<String> templates = mailKeyData.getTemplates();

		List<Long> notificationIds;
		List<Notifications> notifications = null;

		if (!CollectionUtils.isEmpty(templates)) {
			if ("LOAN".equals(module) || "LOAN_ORG".equals(module)) {
				resendNotifications = financeReferenceDetailDAO.resendNotification(finType, finEvent, role, templates);
			}
			notificationIds = getNotifications(finType, finEvent, role, templates);
			if (CollectionUtils.isNotEmpty(notificationIds)) {
				notifications = notificationsDAO.getNotificationsByRuleIdList(notificationIds, "");
			}
		} else {
			notifications = notificationsDAO.getNotificationsByModule(module, "");
		}

		if (CollectionUtils.isEmpty(notifications)) {
			return;
		}

		Map<String, Object> data = new HashMap<>();
		Map<String, List<String>> emailAndMobiles = null;
		FinanceDetail financeDetail = null;
		FinanceMain financeMain = null;
		CustomerDetails customerDetails = null;
		Commitment commitment = null;
		FinReceiptData finReceiptData = null;

		if (object instanceof FinanceDetail) {
			financeDetail = (FinanceDetail) object;
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			data = getTemplateData(financeDetail, null);
			if ("LOAN_ORG".equals(module)) {
				module = "LOAN";
				mailKeyData.setModule(module);
			}
			customerDetails = financeDetail.getCustomerDetails();
		} else if (object instanceof Commitment) {
			commitment = (Commitment) object;
			customerDetails = commitment.getCustomerDetails();
		} else if (object instanceof FinReceiptData) {

			finReceiptData = (FinReceiptData) object;
			financeDetail = finReceiptData.getFinanceDetail();
			data = getTemplateData(financeDetail, finReceiptData.getReceiptHeader());

			customerDetails = financeDetail.getCustomerDetails();
		} else if (object instanceof VehicleDealer) {
			VehicleDealer vehicleDealer = (VehicleDealer) object;
			data = vehicleDealer.getDeclaredFieldValues();
			data.put("vd_recordStatus", vehicleDealer.getRecordStatus());
		}
		// For Customers marked as DND true are not allow to Trigger a Mail.
		if (customerDetails != null && customerDetails.getCustomer() != null && customerDetails.getCustomer().isDnd()) {
			return;
		}

		boolean stageReq = "Y".equalsIgnoreCase(SysParamUtil.getValueAsString("STAGE_REQ_FOR_MAIL_CHECK"));

		for (Notifications mailNotification : notifications) {
			boolean sendNotification = false;
			boolean emailAlreadySent = false;
			boolean smsAlreadySent = false;
			long notificationId = mailNotification.getRuleId();

			if (resendNotifications) {
				sendNotification = true;
			} else {
				if (stageReq) {
					emailAlreadySent = emailEngine.isMailExist(finReference, module, finEvent, notificationId, role);
					smsAlreadySent = smsEngine.isSmsExist(finReference, module, finEvent, notificationId, role);

				} else {
					emailAlreadySent = emailEngine.isMailExist(finReference, module, finEvent, notificationId);
					smsAlreadySent = smsEngine.isSmsExist(finReference, module, finEvent, notificationId);
				}

				if (!emailAlreadySent || !smsAlreadySent) {
					sendNotification = true;
				}
				sendNotification = true;
			}
			MailTemplate template = null;
			if (sendNotification) {
				emailAndMobiles = getEmailsAndMobile(customerDetails, mailNotification, data, financeMain);

				if (CollectionUtils.isNotEmpty(emailAndMobiles.get("EMAILS"))) {
					if (CollectionUtils.isNotEmpty(mailKeyData.getEmails())) {
						mailKeyData.getEmails().clear();
					}
					mailKeyData.getEmails().addAll(emailAndMobiles.get("EMAILS"));
				}

				if (CollectionUtils.isNotEmpty(emailAndMobiles.get("MOBILES"))) {
					if (CollectionUtils.isNotEmpty(mailKeyData.getMobileNumbers())) {
						mailKeyData.getMobileNumbers().clear();
					}
					mailKeyData.getMobileNumbers().addAll(emailAndMobiles.get("MOBILES"));
				}

				prepareAdditionalData(documents, data);
				template = getMailTemplate(mailNotification.getRuleTemplate(), data);
				if (template != null && template.isActive()) {
					try {
						parseMail(template, data);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						throw new AppException("999", ERROR_MESSAGE);
					}
				} else {
					template = null;
				}

				if (template == null || !template.isActive()) {
					continue;
				}

				if (CollectionUtils.isNotEmpty(mailKeyData.getAttachmentList())) {
					mailKeyData.getAttachmentList().clear();
				}

				if (template != null && template.isEmailTemplate() && CollectionUtils.isNotEmpty(documents)) {
					setAttachements(template, mailNotification.getRuleAttachment(), data, documents);
				}

				Notification emailMessage = prepareNotification(mailKeyData, notificationId, template);
				if (template.isEmailTemplate() && sendNotification) {
					sendEmailNotification(emailMessage);
				}

				if (template.isSmsTemplate() && sendNotification) {
					sendSmsNotification(emailMessage);
				}
			}
		}
	}

	// Prepare additional data required for rule .
	private void prepareAdditionalData(List<DocumentDetails> documents, Map<String, Object> data) {
		boolean isCovDocFound = false;
		if (CollectionUtils.isNotEmpty(documents)) {
			for (DocumentDetails documentDetail : documents) {
				if (documentDetail != null && documentDetail.getDocCategory() != null) {
					String docCategoryCode = documentTypeDAO.getDocCategoryByDocType(documentDetail.getDocCategory());
					if (StringUtils.equals(docCategoryCode, DocumentCategories.COVENANT.getKey())) {
						isCovDocFound = true;
						break;
					}
				}
			}
		}
		if (isCovDocFound) {
			data.put("CovenantDoc", PennantConstants.YES);
		} else {
			data.put("CovenantDoc", PennantConstants.NO);
		}
	}

	private Notification prepareNotification(Notification mailKeyData, long notificationId, MailTemplate template) {
		Notification emailMessage = new Notification();
		BeanUtils.copyProperties(mailKeyData, emailMessage);

		emailMessage.setNotificationId(notificationId);
		emailMessage.setSubject(template.getEmailSubject());
		emailMessage.setContent(template.getEmailMessage().getBytes(Charset.forName("UTF-8")));

		if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(template.getEmailFormat())) {
			emailMessage.setContentType(EmailBodyType.HTML.getKey());
		} else {
			emailMessage.setContentType(EmailBodyType.PLAIN.getKey());
		}

		for (String mailId : emailMessage.getEmails()) {
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

		if (template.getSmsMessage() != null && !template.getSmsMessage().isEmpty()) {
			emailMessage.setMessage(template.getSmsMessage());
		} else if (template.getSmsContent() != null && !template.getSmsContent().isEmpty()) {
			emailMessage.setMessage(template.getSmsContent());
		}

		NotificationAttribute attribute = new NotificationAttribute();
		attribute.setAttribute("Notification_Desc");
		attribute.setValue(template.getTemplateDesc());
		emailMessage.getAttributes().add(attribute);

		attribute = new NotificationAttribute();
		attribute.setAttribute("Notification_Code");
		attribute.setValue(template.getTemplateCode());
		emailMessage.getAttributes().add(attribute);

		attribute = new NotificationAttribute();
		attribute.setAttribute("Template_id");
		attribute.setValue(String.valueOf(template.getTemplateId()));
		emailMessage.getAttributes().add(attribute);

		attribute = new NotificationAttribute();
		attribute.setAttribute("Template_For");
		attribute.setValue(template.getTemplateFor());
		emailMessage.getAttributes().add(attribute);

		attribute = new NotificationAttribute();
		attribute.setAttribute("Stage");
		attribute.setValue(emailMessage.getStage());
		emailMessage.getAttributes().add(attribute);

		emailMessage.setNotificationData(template.getNotificationData());

		return emailMessage;
	}

	public void parseMail(MailTemplate mailTemplate, Object templateData) throws Exception {
		logger.debug("Entering");

		String subject = "";
		String result = "";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vo", templateData);

		Map<String, Object> tempMap = (Map<String, Object>) model.get("vo");
		Map<String, Object> notificationDataMap = new HashMap<String, Object>();
		StringTemplateLoader loader = new StringTemplateLoader();
		Template template = null;
		Template templateSubject = null;
		if (mailTemplate.isEmailTemplate()) {
			loader.putTemplate("mailTemplate",
					new String(mailTemplate.getEmailContent(), NotificationConstants.DEFAULT_CHARSET));
			freemarkerMailConfiguration.setTemplateLoader(loader);
			template = freemarkerMailConfiguration.getTemplate("mailTemplate");

			try {
				Pattern pattern = Pattern.compile("\\{(.*?)\\}");
				Matcher matchPattern = pattern.matcher(template.toString());
				while (matchPattern.find()) {
					String[] array = matchPattern.group(1).split("\\.");
					String key = array[1];
					notificationDataMap.put(matchPattern.group(1), tempMap.get(key));
				}
			} catch (Exception e) {
				throw new Exception("Error While Preparing NotificationData", e);
			}
			try {
				result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			} catch (IOException e) {
				throw new AppException("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				logger.error("Template {}", template);
				throw new AppException("Problem initializing freemarker or rendering template ", e);
			}

			StringTemplateLoader subloader = new StringTemplateLoader();
			subloader.putTemplate("mailSubject", mailTemplate.getEmailSubject());
			freemarkerMailConfiguration.setTemplateLoader(subloader);
			templateSubject = freemarkerMailConfiguration.getTemplate("mailSubject");

			try {
				Pattern subPattern = Pattern.compile("\\{(.*?)\\}");
				Matcher subMatchPattern = subPattern.matcher(templateSubject.toString());
				while (subMatchPattern.find()) {
					String[] array = subMatchPattern.group(1).split("\\.");
					String key = array[1];
					notificationDataMap.put(subMatchPattern.group(1), tempMap.get(key));
				}
			} catch (Exception e) {
				throw new AppException("Error While Preparing NotificationData", e);

			}

			try {
				subject = FreeMarkerTemplateUtils.processTemplateIntoString(templateSubject, model);
			} catch (IOException e) {
				throw new AppException("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				throw new AppException("Problem initializing freemarker or rendering template ", e);
			}

			mailTemplate.setEmailMessage(result);
			mailTemplate.setEmailSubject(subject);
		}

		if (mailTemplate.isSmsTemplate()) {
			loader = new StringTemplateLoader();
			loader.putTemplate("smsTemplate", mailTemplate.getSmsContent());
			freemarkerMailConfiguration.setTemplateLoader(loader);
			template = freemarkerMailConfiguration.getTemplate("smsTemplate");

			try {
				Pattern smsPattern = Pattern.compile("\\{(.*?)\\}");
				Matcher smsMatchPattern = smsPattern.matcher(template.toString());
				while (smsMatchPattern.find()) {
					String[] array = smsMatchPattern.group(1).split("\\.");
					String key = array[1];
					notificationDataMap.put(smsMatchPattern.group(1), tempMap.get(key));
				}
			} catch (Exception e) {
				throw new AppException("Error While Preparing NotificationData", e);
			}

			try {
				result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
				mailTemplate.setSmsMessage(result);
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
				throw new AppException("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				logger.debug(Literal.EXCEPTION, e);
				throw new AppException("Problem initializing freemarker or rendering template ", e);
			}
		}

		notificationDataMap.put("contentCode", mailTemplate.getTemplateCode());
		String json = new ObjectMapper().writeValueAsString(notificationDataMap);
		mailTemplate.setNotificationData(json);

		logger.debug("Leaving");
	}

	public void sendMailtoCustomer(long templateId, String mailId, Object vo) throws TemplateException, Exception {
		logger.debug(Literal.ENTERING);
		MailTemplate template = mailTemplateDAO.getMailTemplateById(templateId, "_AView");

		if (template == null || !template.isActive()) {
			return;
		}

		template.getEmailIds().add(mailId);

		MailTemplateData mailData = new MailTemplateData();

		if (vo instanceof SysNotificationDetails) {
			SysNotificationDetails notification = (SysNotificationDetails) vo;
			mailData.setFinReference(notification.getFinReference());
			mailData.setFinBranch(notification.getFinBranch());
			mailData.setFinCcy(notification.getFinCcy());
			mailData.setCustShrtName(notification.getCustShrtName());
			mailData.setCustCIF(notification.getCustCIF());
			mailData.setFinCurODAmt(notification.getFinCurODAmtInStr());
			mailData.setFinCurODDays(notification.getFinCurODDays());
			mailData.setFinPurpose(notification.getFinPurpose());
			mailData.setUsrName(getSecurityUserById(notification.getLastMntBy()).getUsrFName());
		}

		Map<String, Object> data = mailData.getDeclaredFieldValues();
		try {
			parseMail(template, data);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("999", ERROR_MESSAGE);
		}

		if (!template.isEmailTemplate() || CollectionUtils.isEmpty(template.getEmailIds())) {
			return;
		}

		Notification emailMessage = new Notification();
		emailMessage.setKeyReference(mailData.getFinReference());
		emailMessage.setModule("SYS_NOTIFICATION");
		emailMessage.setSubModule("SYS_NOTIFICATION");
		emailMessage.setNotificationId(templateId);
		emailMessage.setStage("");
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

		logger.debug(Literal.LEAVING);
	}

	private Map<String, Object> getTemplateData(QueryDetail detail) {
		MailTemplateData data = new MailTemplateData();
		data.setFinReference(detail.getFinReference());
		data.setCustShrtName(detail.getUserDetails().getUserName());
		data.setQryDesc(detail.getQryNotes());
		return data.getDeclaredFieldValues();
	}

	private Map<String, Object> getTemplateData(PresentmentDetail pd) {
		MailTemplateData data = new MailTemplateData();

		FinanceDetail fd = pd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		CustomerDetails cd = fd.getCustomerDetails();
		List<CustomerEMail> emails = cd.getCustomerEMailList();
		List<CustomerPhoneNumber> mobiles = cd.getCustomerPhoneNumList();

		int format = CurrencyUtil.getFormat(fm.getFinCcy());
		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		data.setCustShrtName(cd.getCustomer().getCustShrtName());
		data.setCustSalutation(StringUtils.trimToEmpty(cd.getCustomer().getLovDescCustSalutationCodeName()));
		data.setFinReference(fm.getFinReference());
		data.setFinAmount(PennantApplicationUtil.amountFormate(fm.getFinAmount(), format));
		if (presentmentBounceService != null) {
			data.setLimitAmount(PennantApplicationUtil
					.amountFormate(presentmentBounceService.getLimitAmount(cd.getCustID()), format));
		}

		for (CustomerEMail email : emails) {
			if (priority != email.getCustEMailPriority()) {
				continue;
			}
			data.setCustEmailId(email.getCustEMail());
			break;
		}

		// Customer Contact Number
		for (CustomerPhoneNumber mobile : mobiles) {
			if (priority != mobile.getPhoneTypePriority()) {
				continue;
			}
			data.setCustMobileNumber(mobile.getPhoneNumber());
			break;
		}

		data.setCustId(fm.getCustID());
		data.setCustCIF(fm.getLovDescCustCIF());
		data.setFinType(fm.getFinType());
		data.setFinTypeDesc(fm.getLovDescFinTypeName());
		data.setNextRepayDate(DateUtil.format(fm.getNextRepayDate(), DateFormat.LONG_DATE));
		data.setPriority(fm.getPriority());

		data.setValueDate(DateUtil.formatToLongDate(pd.getSchDate()));
		data.setAmount(PennantApplicationUtil.amountFormate(pd.getPresentmentAmt(), format));
		data.setBounceReason(pd.getBounceCode());

		return data.getDeclaredFieldValues();
	}

	/**
	 * Preparing the template data for LimitHeader email and sms processing
	 * 
	 * @param limitHeader
	 * @return
	 */

	private Map<String, Object> getTemplateData(LimitHeader limitHeader, Notification notification) {

		MailTemplateData data = new MailTemplateData();
		data.setCustShrtName(limitHeader.getCustShrtName());
		data.setCustCIF(limitHeader.getCustCIF());
		// Customer Mail
		for (String notifMail : notification.getEmails()) {
			data.setCustEmailId(notifMail);
		}
		// Customer Contact Number
		for (String mobile : notification.getMobileNumbers()) {
			data.setCustMobileNumber(mobile);
		}

		return data.getDeclaredFieldValues();
	}

	private Map<String, Object> getTemplData(FinanceDetail fd, FinReceiptHeader rch) {
		MailTemplateData data = new MailTemplateData();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		CustomerDetails custDtls = fd.getCustomerDetails();
		List<CustomerAddres> addresses = custDtls.getAddressList();
		List<CustomerEMail> emails = custDtls.getCustomerEMailList();
		List<CustomerPhoneNumber> mobiles = custDtls.getCustomerPhoneNumList();
		List<FinServiceInstruction> servInstructions = schdData.getFinServiceInstructions();
		int format = CurrencyUtil.getFormat(fm.getFinCcy());

		data.setCustCIF(custDtls.getCustomer().getCustCIF());
		data.setCustShrtName(custDtls.getCustomer().getCustShrtName());
		data.setFinReference(fm.getFinReference());
		data.setFinAmount(PennantApplicationUtil.amountFormate(fm.getFinAmount(), format));
		data.setDownPayment(PennantApplicationUtil.amountFormate(fm.getDownPayment(), format));
		data.setFeeAmount(PennantApplicationUtil.amountFormate(fm.getFeeChargeAmt(), format));
		data.setFinCcy(fm.getFinCcy());
		data.setFinStartDate(DateUtil.formatToLongDate(fm.getFinStartDate()));
		data.setMaturityDate(DateUtil.formatToLongDate(fm.getMaturityDate()));
		data.setNumberOfTerms(String.valueOf(fm.getNumberOfTerms()));
		data.setGraceTerms(String.valueOf(fm.getGraceTerms()));
		data.setTotalTenor(String.valueOf(fm.getNumberOfTerms() + fm.getGraceTerms()));
		data.setFinCurrAssetValue(PennantApplicationUtil.amountFormate(fm.getFinCurrAssetValue(), format));
		data.setRepaymentFrequency(fm.getRepayFrq());
		data.setGraceBaseRate(fm.getGraceBaseRate());
		data.setGraceSpecialRate(fm.getGraceSpecialRate());
		data.setRepayBaseRate(fm.getRepayBaseRate());
		data.setRepaySpecialRate(fm.getRepaySpecialRate());
		data.setRepayMargin(PennantApplicationUtil.amountFormate(fm.getRepayMargin(), format));
		data.setFinBranch(fm.getFinBranch());
		data.setFinCcy(fm.getFinCcy());
		data.setFinDivision(fm.getLovDescFinDivision());
		data.setAccountsOfficerDesc(fm.getLovDescAccountsOfficer());
		data.setDsaCode(fm.getDsaCode());
		data.setDsaDesc(fm.getDsaCodeDesc());
		data.setdMACodeDesc(fm.getDmaCodeDesc());
		data.setTotalProfit(PennantApplicationUtil.amountFormate(fm.getTotalProfit(), format));
		data.setFirstRepay(PennantApplicationUtil.amountFormate(fm.getFirstRepay(), format));
		data.setLastRepay(PennantApplicationUtil.amountFormate(fm.getLastRepay(), format));
		data.setUserName(fm.getUserDetails().getUserName());
		data.setUserBranch(fm.getUserDetails().getBranchName());
		data.setUserDepartment(fm.getUserDetails().getDepartmentName());
		data.setRepaymentDate(StringUtils.substring(fm.getRepayFrq(), -2));

		Branch branch = branchDAO.getBranchById(fm.getFinBranch(), "_AView");
		if (branch == null) {
			branch = new Branch();
		}

		data.setFinBranchAddrLine1(StringUtils.trimToEmpty(branch.getBranchAddrLine1()));
		data.setFinBranchAddrLine2(StringUtils.trimToEmpty(branch.getBranchAddrLine2()));
		data.setFinBranchAddrHNbr(StringUtils.trimToEmpty(branch.getBranchAddrHNbr()));
		data.setFinBranchAddrFlatNo(StringUtils.trimToEmpty(branch.getBranchFlatNbr()));
		data.setFinBranchAddrStreet(StringUtils.trimToEmpty(branch.getBranchAddrStreet()));
		data.setFinBranchAddrCountry(StringUtils.trimToEmpty(branch.getBranchCountry()));
		data.setFinBranchAddrCity(StringUtils.trimToEmpty(branch.getBranchCity()));
		data.setFinBranchAddrProvince(StringUtils.trimToEmpty(branch.getBranchProvince()));
		data.setFinBranchAddrDistrict("");
		data.setFinBranchAddrPincode(StringUtils.trimToEmpty(branch.getPinCode()));
		data.setFinBranchPhone(StringUtils.trimToEmpty(branch.getBranchTel()));

		// User Branch Details
		if (!StringUtils.equals(fm.getFinBranch(), fm.getUserDetails().getBranchCode())) {
			branch = branchDAO.getBranchById(fm.getUserDetails().getBranchCode(), "_AView");
		}

		if (branch == null) {
			branch = new Branch();
		}

		data.setUserBranchAddrLine1(StringUtils.trimToEmpty(branch.getBranchAddrLine1()));
		data.setUserBranchAddrLine2(StringUtils.trimToEmpty(branch.getBranchAddrLine2()));
		data.setUserBranchAddrHNbr(StringUtils.trimToEmpty(branch.getBranchAddrHNbr()));
		data.setUserBranchAddrFlatNo(StringUtils.trimToEmpty(branch.getBranchFlatNbr()));
		data.setUserBranchAddrStreet(StringUtils.trimToEmpty(branch.getBranchAddrStreet()));
		data.setUserBranchAddrCountry(StringUtils.trimToEmpty(branch.getBranchCountry()));
		data.setUserBranchAddrCity(StringUtils.trimToEmpty(branch.getBranchCity()));
		data.setUserBranchAddrProvince(StringUtils.trimToEmpty(branch.getBranchProvince()));
		data.setUserBranchAddrDistrict("");
		data.setUserBranchAddrPincode(StringUtils.trimToEmpty(branch.getPinCode()));
		data.setUserBranchPhone(StringUtils.trimToEmpty(branch.getBranchTel()));

		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);
		for (CustomerAddres ca : addresses) {
			if (priority != ca.getCustAddrPriority()) {
				continue;
			}

			data.setCustAddrLine1(ca.getCustAddrLine1());
			data.setCustAddrLine2(ca.getCustAddrLine2());
			data.setCustAddrHNo(ca.getCustAddrHNbr());
			data.setCustAddrFlatNo(ca.getCustFlatNbr());
			data.setCustAddrStreet(StringUtils.trimToEmpty(ca.getCustAddrStreet()));
			data.setCustAddrCountry(ca.getCustAddrCountry());
			data.setCustAddrProvince(ca.getCustAddrProvince());
			data.setCustAddrDistrict(ca.getCustDistrict());
			data.setCustAddrCity(ca.getCustAddrCity());
			data.setCustAddrPincode(ca.getCustPOBox());

			break;
		}

		for (CustomerEMail email : emails) {
			if (priority != email.getCustEMailPriority()) {
				continue;
			}

			data.setCustEmailId(email.getCustEMail());
			break;
		}

		for (CustomerPhoneNumber phone : mobiles) {
			if (priority != phone.getPhoneTypePriority()) {
				continue;
			}

			data.setCustMobileNumber(phone.getPhoneNumber());
			break;
		}

		data.setEffectiveRate("");
		data.setRepayRate("");
		data.setCustId(fm.getCustID());
		data.setCustCIF(fm.getLovDescCustCIF());
		data.setFinType(fm.getFinType());
		data.setFinTypeDesc(fm.getLovDescFinTypeName());
		data.setNextRepayDate(DateUtil.format(fm.getNextRepayDate(), DateFormat.LONG_DATE));
		data.setPriority(fm.getPriority());

		if (fm.getEffectiveRateOfReturn() != null) {
			data.setEffectiveRate(PennantApplicationUtil.formatRate(fm.getEffectiveRateOfReturn().doubleValue(), 2));
		}

		try {
			RateDetail details = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(), fm.getRepaySpecialRate(),
					fm.getRepayMargin(), fm.getRpyMinRate(), fm.getRpyMaxRate());
			data.setRepayRate(PennantApplicationUtil.formatRate(details.getNetRefRateLoan().doubleValue(), 2));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (fm.getRepayProfitRate() != null) {
			data.setRepayRate(PennantApplicationUtil.formatRate(fm.getRepayProfitRate().doubleValue(), 2));
		}

		if (StringUtils.isNotEmpty(fm.getLovDescFinTypeName())
				&& StringUtils.contains(fm.getLovDescFinTypeName(), "-")) {
			String splitLoanType = StringUtils.substring(fm.getLovDescFinTypeName(), 3);
			data.setFinTypeDesc(splitLoanType);
		}

		List<SecurityRole> securityRoles = securityRoleDAO.getSecurityRole(fm.getRoleCode());
		data.setRoleCode(securityRoles.size() > 0 ? securityRoles.get(0).getRoleDesc() : "");

		// user Details
		SecurityUser secUser = getSecurityUserById(fm.getLastMntBy());
		String secUsrFullName = PennantApplicationUtil.getFullName(secUser.getUsrFName(), secUser.getUsrMName(),
				secUser.getUsrLName());
		data.setUsrName(secUsrFullName);
		data.setNextUsrName("");
		data.setPrevUsrName(secUsrFullName);

		if (!PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID())) {
			WorkFlowDetails workFlowDetails = workFlowDetailsDAO.getWorkFlowDetailsByID(fm.getWorkflowId());
			data.setWorkflowType(workFlowDetails == null ? "" : workFlowDetails.getWorkFlowType());
		}

		data.setNextUsrRoleCode(fm.getNextRoleCode());
		List<SecurityRole> securityNextRoles = securityRoleDAO.getSecurityRole(fm.getNextRoleCode());

		String nextRoleCode = "";
		for (SecurityRole securityRole : securityNextRoles) {
			if (StringUtils.isNotEmpty(nextRoleCode)) {
				nextRoleCode = nextRoleCode + " / " + securityRole.getRoleDesc();
			} else {
				nextRoleCode = securityRole.getRoleDesc();
			}
		}

		data.setNextUsrRole(nextRoleCode);
		data.setPrevUsrRole(fm.getLastMntBy());
		data.setUsrRole(fm.getRoleCode());
		data.setFinCommitmentRef(fm.getFinCommitmentRef());
		data.setRcdMaintainSts(fm.getRcdMaintainSts());

		Notes note = new Notes();
		note.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		note.setReference(fm.getFinReference());
		List<Notes> list = notesDAO.getNotesListByRole(note, false, new String[] { fm.getRoleCode() });
		StringBuilder recommendations = new StringBuilder();
		list.forEach(l1 -> recommendations.append(l1.getRemarks()));

		data.setRecommendations(recommendations.toString());
		data.setRecordStatus(fm.getRecordStatus());
		data.setReceiptPurpose(fm.getReceiptPurpose());

		data.setReceiptAmount(PennantApplicationUtil.amountFormate(rch.getReceiptAmount(), format));
		data.setBounceDate(DateUtil.formatToLongDate(rch.getBounceDate()));
		Date bounceDateValue = rch.getBounceDate();
		if (bounceDateValue != null) {
			data.setBounceReason(rch.getManualAdvise().getBounceCodeDesc());
		}
		data.setCancellationReason(rch.getCancelReason());
		Date valueDate = rch.getReceiptDate();
		BigDecimal modeAmount = BigDecimal.ZERO;
		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
				if (!StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {
					valueDate = receiptDetail.getReceivedDate();
					modeAmount = receiptDetail.getAmount();

				}
			}
		}
		data.setValueDate(DateUtil.formatToLongDate(valueDate));
		data.setAmount(PennantApplicationUtil.amountFormate(modeAmount, format));

		if (CollectionUtils.isNotEmpty(servInstructions)
				&& (data.getValueDate() == null || StringUtils.isEmpty(data.getAmount()))) {
			FinServiceInstruction instruction = servInstructions.get(0);
			if (!FinServiceEvent.ORG.equals(instruction.getFinEvent())) {
				data.setValueDate(DateUtil.formatToLongDate(instruction.getFromDate()));
				data.setAmount(PennantApplicationUtil.amountFormate(instruction.getAmount(), format));
			}
		}

		return data.getDeclaredFieldValues();
	}

	/**
	 * Method for Data Preparation
	 * 
	 * @param data
	 * @param facility
	 * @return
	 */

	public MailTemplateData getTemplateData(Facility facility) {
		MailTemplateData data = new MailTemplateData();
		// Facility Data Preparation For Notifications
		data.setCustShrtName(facility.getCustShrtName());
		data.setCustId(facility.getCustID());
		// Role Code For Alert Notification
		data.setRoleCode(facility.getNextRoleCode());
		data.setCafReference(facility.getCAFReference());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(facility.getLastMntBy()));
		data.setNextUsrName("");
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(facility.getLastMntBy()));
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(facility.getWorkflowId()));
		data.setNextUsrRoleCode(facility.getNextRoleCode());
		List<SecurityRole> securityNextRoles = PennantApplicationUtil.getRoleCodeDesc(facility.getNextRoleCode());
		String nextRoleCode = "";
		for (SecurityRole securityRole : securityNextRoles) {
			if (StringUtils.isNotEmpty(nextRoleCode)) {
				nextRoleCode = nextRoleCode + "/" + securityRole.getRoleDesc();
			} else {
				nextRoleCode = securityRole.getRoleDesc();
			}
		}
		data.setNextUsrRole(nextRoleCode);
		data.setPrevUsrRole(facility.getLastMntBy());
		data.setUsrRole(facility.getRoleCode());
		if (StringUtils.trimToEmpty(facility.getFacilityType()).equals(FacilityConstants.FACILITY_COMMERCIAL)) {
			data.setFacilityType("Commercial");
		} else if (StringUtils.trimToEmpty(facility.getFacilityType()).equals(FacilityConstants.FACILITY_CORPORATE)) {
			data.setFacilityType("Corporate");
		}
		data.setCountryOfDomicileName(facility.getCountryOfDomicileName());
		data.setCountryOfRiskName(facility.getCountryOfRiskName());
		data.setCountryManagerName(facility.getCountryManagerName());
		data.setCustomerGroupName(facility.getCustomerGroupName());
		data.setNatureOfBusinessName(facility.getNatureOfBusinessName());
		return data;
	}

	/**
	 * Method for Data Preparion
	 * 
	 * @param data
	 * @param FinCreditReviewDetails
	 * @return
	 */
	public MailTemplateData getTemplateData(FinCreditReviewDetails finCreditReviewDetails) {
		MailTemplateData data = new MailTemplateData();
		// Facility Data Preparation For Notifications
		data.setCustShrtName(finCreditReviewDetails.getLovDescCustShrtName());
		data.setCustCIF(finCreditReviewDetails.getLovDescCustCIF());
		data.setAuditors(finCreditReviewDetails.getAuditors());
		data.setLocation(finCreditReviewDetails.getLocation());
		data.setAuditType(finCreditReviewDetails.getAuditType());
		data.setAuditedDate(DateUtil.formatToLongDate(finCreditReviewDetails.getAuditedDate()));
		data.setAuditYear(finCreditReviewDetails.getAuditYear());
		data.setAuditPeriod(finCreditReviewDetails.getAuditPeriod());
		// Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(finCreditReviewDetails.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(finCreditReviewDetails.getLastMntBy()));
		data.setNextUsrName("");
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(finCreditReviewDetails.getLastMntBy()));
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(finCreditReviewDetails.getWorkflowId()));
		data.setNextUsrRoleCode(finCreditReviewDetails.getNextRoleCode());

		List<SecurityRole> securityUsrRoles = PennantApplicationUtil
				.getRoleCodeDesc(finCreditReviewDetails.getNextRoleCode());
		data.setNextUsrRole(securityUsrRoles.get(0).getRoleDesc());
		data.setPrevUsrRole(finCreditReviewDetails.getLastMntBy());
		data.setUsrRole(finCreditReviewDetails.getRoleCode());
		return data;
	}

	/**
	 * Method for Data Preparion
	 * 
	 * @param data
	 * @param Provision
	 * @return
	 */
	public MailTemplateData getTemplateData(Provision provision) {
		MailTemplateData data = new MailTemplateData();

		// Provision Data Preparation For Notifications
		data.setFinReference(provision.getFinReference());
		data.setCustShrtName(provision.getCustShrtName());
		data.setCustCIF(provision.getCustCIF());
		data.setFinBranch(provision.getFinBranch());
		int format = CurrencyUtil.getFormat(provision.getFinCcy());
		// data.setPrincipalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue(), format));
		// data.setProfitDue(PennantApplicationUtil.amountFormate(provision.getProfitDue(), format));
		// data.setTotalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue().add(provision.getProfitDue()),
		// format));
		data.setDueFromDate(DateUtil.formatToLongDate(provision.getDueFromDate()));
		// data.setNonFormulaProv(PennantApplicationUtil.amountFormate(provision.getNonFormulaProv(), format));
		data.setProvisionedAmt(PennantApplicationUtil.amountFormate(provision.getProvisionedAmt(), format));
		// data.setProvisionedAmtCal(PennantApplicationUtil.amountFormate(provision.getProvisionAmtCal(), format));

		// Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(provision.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(provision.getLastMntBy()));
		data.setNextUsrName("");
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(provision.getLastMntBy()));
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(provision.getWorkflowId()));
		data.setNextUsrRoleCode(provision.getNextRoleCode());

		List<SecurityRole> securityUsrRoles = PennantApplicationUtil.getRoleCodeDesc(provision.getNextRoleCode());
		data.setNextUsrRole(securityUsrRoles.get(0).getRoleDesc());
		data.setPrevUsrRole(provision.getLastMntBy());
		data.setUsrRole(provision.getRoleCode());

		return data;
	}

	public MailTemplateData getTemplateData(FinanceSuspHead financeSuspHead) {
		MailTemplateData data = new MailTemplateData();
		// Manual Suspense Data Preparation For Notifications
		data.setFinReference(financeSuspHead.getFinReference());
		data.setCustShrtName(financeSuspHead.getLovDescCustShrtName());
		data.setCustCIF(financeSuspHead.getLovDescCustCIFName());
		data.setFinBranch(financeSuspHead.getFinBranch());
		data.setManualSusp(financeSuspHead.isManualSusp() ? "Yes" : "No");
		data.setFinSuspDate(DateUtil.formatToLongDate(financeSuspHead.getFinSuspDate()));
		data.setFinSuspAmt(PennantApplicationUtil.amountFormate(financeSuspHead.getFinSuspAmt(),
				CurrencyUtil.getFormat(financeSuspHead.getFinCcy())));
		data.setFinCurSuspAmt(PennantApplicationUtil.amountFormate(financeSuspHead.getFinCurSuspAmt(),
				CurrencyUtil.getFormat(financeSuspHead.getFinCcy())));

		// Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(financeSuspHead.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(financeSuspHead.getLastMntBy()));
		data.setNextUsrName("");
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(financeSuspHead.getLastMntBy()));
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(financeSuspHead.getWorkflowId()));
		data.setNextUsrRoleCode(financeSuspHead.getNextRoleCode());

		List<SecurityRole> securityUsrRoles = PennantApplicationUtil.getRoleCodeDesc(financeSuspHead.getNextRoleCode());
		data.setNextUsrRole(securityUsrRoles.get(0).getRoleDesc());
		data.setPrevUsrRole(financeSuspHead.getLastMntBy());
		data.setUsrRole(financeSuspHead.getRoleCode());

		return data;
	}

	private Map<String, Object> getTemplateData(FinanceDetail fd, FinReceiptHeader rch) {
		if (rch == null) {
			rch = new FinReceiptHeader();
		}

		Promotion promotion = fd.getPromotion();
		if (promotion == null) {
			promotion = new Promotion();
		}

		FinOption finOption = fd.getFinOption();
		if (finOption == null) {
			finOption = new FinOption();
		}

		LMSServiceLog lmsServiceLog = fd.getLmsServiceLog();
		if (lmsServiceLog == null) {
			lmsServiceLog = new LMSServiceLog();
		}

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		Customer customer = fd.getCustomerDetails().getCustomer();
		int format = CurrencyUtil.getFormat(fm.getFinCcy());
		String roleCode = fm.getRoleCode();

		if (!PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID())) {
			if (StringUtils.isNotEmpty(roleCode)) {
				fm.setNextRoleCodeDesc(PennantApplicationUtil.getSecRoleCodeDesc(roleCode));
				fm.setSecUsrFullName(PennantApplicationUtil.getUserDesc(fm.getLastMntBy()));
				if (fm.getWorkflowId() > 0) {
					fm.setWorkFlowType(PennantApplicationUtil.getWorkFlowType(fm.getWorkflowId()));
				}
			}
		}

		Map<String, Object> dataMap = fm.getDeclaredFieldValues();

		dataMap.put("fm_recordStatus", fm.getRecordStatus());
		dataMap.putAll(customer.getDeclaredFieldValues());

		try {
			dataMap.putAll(getTemplData(fd, rch));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		String finReference = fm.getFinReference();
		String drwingPwr = PennantApplicationUtil.amountFormate(drawingPowerService.getDrawingPower(finReference), 2);
		BigDecimal emiOnFinAssetValue = ScheduleCalculator.getEMIOnFinAssetValue(fd.getFinScheduleData());
		String prefix = FieldPrefix.Putcall.getPrefix();

		dataMap.putAll(lmsServiceLog.getDeclaredFieldValues());
		dataMap.putAll(DataMapUtil.getDataMap(fd));
		dataMap.put("rc_" + "effectiveDate", DateUtil.formatToLongDate(lmsServiceLog.getEffectiveDate()));
		dataMap.put("recordStatus", rch.getRecordStatus());
		dataMap.put("rh_receiptPurpose", rch.getReceiptPurpose());
		dataMap.put("rh_receiptAmount", PennantApplicationUtil.amountFormate(rch.getReceiptAmount(), format));
		dataMap.put("rh_receiptDate", DateUtil.formatToLongDate(rch.getReceiptDate()));
		dataMap.put("rh_balAmount", PennantApplicationUtil.amountFormate(rch.getBalAmount(), format));
		dataMap.put("rh_actualInterestRate", promotion.getActualInterestRate());
		dataMap.put(prefix.concat("code"), finOption.getOptionType());
		dataMap.put(prefix.concat("description"), finOption.getOptionType());
		dataMap.put(prefix.concat("nextFrequencyDate"), DateUtil.formatToLongDate(finOption.getNextOptionDate()));
		dataMap.put(prefix.concat("nextOptionDate"), DateUtil.formatToLongDate(finOption.getNextOptionDate()));
		dataMap.put(prefix.concat("currentOptionDate"), DateUtil.formatToLongDate(finOption.getCurrentOptionDate()));
		dataMap.put(prefix.concat("appDate"), DateUtil.formatToLongDate(SysParamUtil.getAppDate()));
		dataMap.put("drawingPower", drwingPwr);
		dataMap.put("currentDate", DateUtil.formatToLongDate(SysParamUtil.getAppDate()));
		dataMap.put("emiOnTotalLoanAmt", PennantApplicationUtil.amountFormate(emiOnFinAssetValue, 2));
		dataMap.put("ct_custSalutationCode", StringUtils.trimToEmpty(customer.getLovDescCustSalutationCodeName()));
		dataMap.put("di_paymentType", "");

		for (FinAdvancePayments fap : fd.getAdvancePaymentsList()) {
			String paymentType = StringUtils.trimToEmpty(fap.getPaymentType());
			dataMap.put("di_paymentType", paymentType);
			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)) {
				break;
			}
		}

		return dataMap;
	}

	private void sendEmailNotification(Notification emailMessage) {
		try {
			emailEngine.sendEmail(emailMessage);
		} catch (Exception e) {
			throw new AppException("Unable to save the email notification", e);
		}
	}

	private void sendSmsNotification(Notification smsNotification) {

		for (String mobilenumber : smsNotification.getMobileNumbers()) {
			smsNotification.setMobileNumber(mobilenumber);
			try {
				smsEngine.sendSms(smsNotification);
			} catch (Exception e) {
				throw new AppException("Unable to save the sms notification", e);
			}
		}
	}

	private MailTemplate getMailTemplate(String rule, Map<String, Object> fieldsAndValues) {
		MailTemplate template = null;
		try {

			int templateId = (Integer) RuleExecutionUtil.executeRule(rule, fieldsAndValues, null,
					RuleReturnType.INTEGER);
			if (templateId == 0) {
				logger.warn(Message.NO_TEMPLATE_FOUND);
				return null;
			}

			template = mailTemplateDAO.getMailTemplateById(templateId, "_AView");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return template;
	}

	private String[] getAttachmentCode(String attachmentRule, Map<String, Object> fieldsAndValues) {
		// Getting the Attached Documents
		String ruleResString = (String) RuleExecutionUtil.executeRule(attachmentRule, fieldsAndValues, null,
				RuleReturnType.STRING);
		return StringUtils.trimToEmpty(ruleResString).split(",");
	}

	private Map<String, List<String>> getEmailsAndMobile(CustomerDetails customerDetails, Notifications notification,
			Map<String, Object> fieldsAndValues, FinanceMain financeMain) {
		Map<String, List<String>> map = new HashMap<>();

		MailTemplateData templateData = new MailTemplateData();
		List<CustomerEMail> custEmails = null;
		List<CustomerPhoneNumber> custMobiles = null;
		if (customerDetails != null) {
			custEmails = customerDetails.getCustomerEMailList();
			custMobiles = customerDetails.getCustomerPhoneNumList();
		}

		String templateType = notification.getTemplateType();

		List<String> emails = new ArrayList<>();
		List<String> mobileNumbers = new ArrayList<>();

		// Customer notification
		if (NotificationConstants.TEMPLATE_FOR_CN.equals(templateType)) {
			if (CollectionUtils.isNotEmpty(custEmails)) {
				for (CustomerEMail customerEMail : custEmails) {
					if (customerEMail.getCustEMailPriority() == NumberUtils
							.toInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						emails.add(customerEMail.getCustEMail());
						templateData.setCustEmailId(customerEMail.getCustEMail());
						break;
					}
				}
			}

			if (CollectionUtils.isNotEmpty(custMobiles)) {
				for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
					if (customerPhoneNumber.getPhoneTypePriority() == NumberUtils
							.toInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						mobileNumbers.add(customerPhoneNumber.getPhoneNumber());
						templateData.setCustMobileNumber(customerPhoneNumber.getPhoneNumber());
						break;
					}
				}
			}
		} else if (NotificationConstants.TEMPLATE_FOR_SP.equals(templateType)) {
			long vehicleDealerid = customerDetails.getCustomer().getCustRO1();
			VehicleDealer vehicleDealer = vehicleDealerDAO.getVehicleDealerById(vehicleDealerid, "_AView");

			if (vehicleDealer != null) {
				emails.add(StringUtils.trimToEmpty(vehicleDealer.getEmail()));
				mobileNumbers.add(StringUtils.trimToEmpty(vehicleDealer.getDealerTelephone()));
				fieldsAndValues.putAll(vehicleDealer.getDeclaredFieldValues());
			}
		} else if (NotificationConstants.TEMPLATE_FOR_DSAN.equals(templateType)) {
			VehicleDealer vehicleDealer = null;
			if (StringUtils.isNotBlank(financeMain.getDsaCode()) && StringUtils.isNumeric(financeMain.getDsaCode())) {
				vehicleDealer = vehicleDealerDAO.getVehicleDealerById(Long.valueOf(financeMain.getDsaCode()), "_AView");
			}

			if (vehicleDealer != null) {
				emails.add(StringUtils.trimToEmpty(vehicleDealer.getEmail()));
				mobileNumbers.add(StringUtils.trimToEmpty(vehicleDealer.getDealerTelephone()));
				fieldsAndValues.putAll(vehicleDealer.getDeclaredFieldValues());
			}
		} else if (NotificationConstants.TEMPLATE_FOR_AE.equals(templateType)
				|| NotificationConstants.TEMPLATE_FOR_TAT.equals(templateType)
				|| NotificationConstants.TEMPLATE_FOR_QP.equals(templateType)
				|| NotificationConstants.TEMPLATE_FOR_GE.equals(templateType)) {

			String ruleResString = (String) RuleExecutionUtil.executeRule(notification.getRuleReciepent(),
					fieldsAndValues, null, RuleReturnType.STRING);
			if (StringUtils.isNotEmpty(ruleResString)) {
				List<String> emailList = securityUserOperationsDAO.getUsrMailsByRoleIds(ruleResString);

				if (CollectionUtils.isNotEmpty(emailList)) {
					emails.addAll(emailList);
				}
			}
		} else if (NotificationConstants.TEMPLATE_FOR_PVRN.equals(templateType)) {
			emails.add(StringUtils.trimToEmpty(fieldsAndValues.get("vd_email").toString()));
			mobileNumbers.add(StringUtils.trimToEmpty(fieldsAndValues.get("vd_dealerTelephone").toString()));
		}

		map.put("EMAILS", emails);
		map.put("MOBILES", mobileNumbers);

		return map;
	}

	private void setAttachements(MailTemplate template, String attachmentRule, Map<String, Object> fieldsAndValues,
			List<DocumentDetails> documents) {
		if (template.isEmailTemplate() && StringUtils.isNotEmpty(attachmentRule)) {
			String[] documentCodes = getAttachmentCode(attachmentRule, fieldsAndValues);

			for (String documnetCode : documentCodes) {
				for (DocumentDetails document : documents) {
					if (documnetCode.equals(document.getDocCategory())) {
						byte[] docImg = document.getDocImage();
						if (docImg == null && document.getDocRefId() != null) {
							byte[] docManager = getDocumentImage(document.getDocRefId());
							if (docManager != null) {
								docImg = docManager;
							}
						}
						if (docImg != null) {
							template.getAttchments().put(document.getDocName(), docImg);
						}
					}
				}
			}
		}
	}

	public String[] getAttchmentRuleResult(String attachmentRule, FinanceDetail financeDetail) {
		String[] documentCodes = getAttachmentCode(attachmentRule, getTemplateData(financeDetail, null));
		return documentCodes;
	}

	public SecurityUser getSecurityUserById(long id) {
		logger.debug(Literal.ENTERING);

		SecurityUser securityUser = securityUserDAO.getSecurityUserById(id, "_View");

		securityUser.setSecurityUserDivBranchList(securityUserDAO.getSecUserDivBrList(id, "_View"));

		securityUser.setReportingManagersList(reportingManagerDAO.getReportingManagers(id, "_View"));

		return securityUser;
	}

	public List<Long> getNotifications(String financeType, String finEvent, String roleCode, List<String> lovCodeList) {
		List<Long> list = new ArrayList<>();
		Map<Long, String> templateMap = financeReferenceDetailDAO.getTemplateIdList(financeType, finEvent, roleCode,
				lovCodeList);

		if (templateMap == null) {
			return list;
		}

		for (Long notificationId : templateMap.keySet()) {
			list.add(notificationId);
		}

		return list;
	}

	public void setFreemarkerMailConfiguration(Configuration configuration) {
		this.freemarkerMailConfiguration = configuration;
	}

	public void setMailTemplateDAO(MailTemplateDAO mailTemplateDAO) {
		this.mailTemplateDAO = mailTemplateDAO;
	}

	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

	public void setNotificationsDAO(NotificationsDAO notificationsDAO) {
		this.notificationsDAO = notificationsDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public void setSecurityRoleDAO(SecurityRoleDAO securityRoleDAO) {
		this.securityRoleDAO = securityRoleDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public void setReportingManagerDAO(ReportingManagerDAO reportingManagerDAO) {
		this.reportingManagerDAO = reportingManagerDAO;
	}

	public void setWorkFlowDetailsDAO(WorkFlowDetailsDAO workFlowDetailsDAO) {
		this.workFlowDetailsDAO = workFlowDetailsDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public void setEmailEngine(EmailEngine emailEngine) {
		this.emailEngine = emailEngine;
	}

	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public void setSmsEngine(SmsEngine smsEngine) {
		this.smsEngine = smsEngine;
	}

	public DrawingPowerService getDrawingPowerService() {
		return drawingPowerService;
	}

	public void setDrawingPowerService(DrawingPowerService drawingPowerService) {
		this.drawingPowerService = drawingPowerService;
	}

	public PresentmentBounceService getPresentmentBounceService() {
		return presentmentBounceService;
	}

	@Autowired(required = false)
	@Qualifier("presentmentBounceService")
	public void setPresentmentBounceService(PresentmentBounceService presentmentBounceService) {
		this.presentmentBounceService = presentmentBounceService;
	}

	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

}