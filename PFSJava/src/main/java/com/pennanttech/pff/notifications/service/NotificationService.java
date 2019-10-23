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
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.dao.notifications.NotificationsDAO;
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
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.MailTemplateData;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.NotificationAttribute;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pennapps.notification.sms.SmsEngine;
import com.pennanttech.pff.core.util.DataMapUtil;
import com.pennanttech.pff.core.util.DataMapUtil.FieldPrefix;
import com.pennanttech.pff.external.DrawingPower;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class NotificationService {
	private static final Logger logger = Logger.getLogger(NotificationService.class);

	private Configuration freemarkerMailConfiguration;
	private MailTemplateDAO mailTemplateDAO;
	private NotificationsDAO notificationsDAO;
	private NotesDAO notesDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	private SecurityRoleDAO securityRoleDAO;
	private SecurityUserDAO securityUserDAO;
	private ReportingManagerDAO reportingManagerDAO;
	private WorkFlowDetailsDAO workFlowDetailsDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private DocumentManagerDAO documentManagerDAO;
	private BranchDAO branchDAO;
	private VehicleDealerDAO vehicleDealerDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private EmailEngine emailEngine;
	private SmsEngine smsEngine;

	public NotificationService() {
		super();
	}

	public void sendNotification(Notification mailKeyData, Object object) throws Exception {
		logger.debug(Literal.ENTERING);
		Map<String, Object> data = null;

		if (object instanceof QueryDetail) {
			QueryDetail queryDetail = (QueryDetail) object;
			data = getTemplateData(queryDetail);
		} else if (object instanceof PresentmentDetail) {
			PresentmentDetail presentmentDetail = (PresentmentDetail) object;
			data = getTemplateData(presentmentDetail);
		} else if (object instanceof FinanceDetail) {
			FinanceDetail financeDetail = (FinanceDetail) object;
			data = getTemplateData(financeDetail, null);
		} else if (object instanceof LimitHeader) {
			LimitHeader limitHeader = (LimitHeader) object;
			data = getTemplateData(limitHeader, mailKeyData);
		}

		Map<String, byte[]> attachements = mailKeyData.getAttachments();
		MailTemplate template = mailTemplateDAO.getMailTemplateByCode(mailKeyData.getTemplateCode(), "_AView");

		if (template != null && template.isActive()) {
			try {
				parseMail(template, data);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		if (template == null) {
			return;
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

	public void sendNotifications(String moduleCode, Object object) throws Exception {
		logger.debug(Literal.ENTERING);

		Notification notification = new Notification();
		List<DocumentDetails> documents = null;

		String keyReference = null;
		String role = null;
		notification.setModule(moduleCode);
		notification.setSubModule(moduleCode);
		try {
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
			} else if (object instanceof InvestmentFinHeader) {
				//
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

			sendNotifications(notification, object, null, documents);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void sendNotifications(Notification mailKeyData, Object object, String finType,
			List<DocumentDetails> documents) throws IOException, TemplateException {
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
		//For Customers marked as DND true are not allow to Trigger a Mail. 
		if (customerDetails != null && customerDetails.getCustomer() != null && customerDetails.getCustomer().isDnd()) {
			return;
		}

		for (Notifications mailNotification : notifications) {
			boolean sendNotification = false;
			boolean emailAlreadySent = false;
			boolean smsAlreadySent = false;
			long notificationId = mailNotification.getRuleId();

			if (resendNotifications) {
				sendNotification = true;
			} else {
				// checking for mail already sent or not
				String stageReq = SysParamUtil.getValueAsString("STAGE_REQ_FOR_MAIL_CHECK");

				if ("Y".equalsIgnoreCase(stageReq)) {
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
					mailKeyData.getEmails().addAll(emailAndMobiles.get("EMAILS"));
				}

				if (CollectionUtils.isNotEmpty(emailAndMobiles.get("MOBILES"))) {
					mailKeyData.getMobileNumbers().addAll(emailAndMobiles.get("MOBILES"));
				}

				template = getMailTemplate(mailNotification.getRuleTemplate(), data);
				if (template != null && template.isActive()) {
					try {
						parseMail(template, data);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
				} else {
					template = null;
				}

				if (template == null) {
					continue;
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

	/**
	 * Method for Parsing Mail Details and Send Notification To Users/Customer
	 * 
	 * @param mailTemplate
	 * @param templateData
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void parseMail(MailTemplate mailTemplate, Object templateData) throws Exception {
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
		MailTemplate template = null;
		try {
			template = mailTemplateDAO.getMailTemplateById(templateId, "_AView");
			if (template != null && template.isActive()) {

				template.getEmailIds().add(mailId);
				// Template Fields Bean Preparation
				MailTemplateData templateData = new MailTemplateData();
				if (vo instanceof SysNotificationDetails) {
					SysNotificationDetails notification = (SysNotificationDetails) vo;

					templateData.setFinReference(notification.getFinReference());
					templateData.setFinBranch(notification.getFinBranch());
					templateData.setFinCcy(notification.getFinCcy());
					templateData.setCustShrtName(notification.getCustShrtName());
					templateData.setCustCIF(notification.getCustCIF());
					templateData.setFinCurODAmt(notification.getFinCurODAmtInStr());
					templateData.setFinCurODDays(notification.getFinCurODDays());
					templateData.setFinPurpose(notification.getFinPurpose());

					// user Details
					SecurityUser secUser = getSecurityUserById(notification.getLastMntBy());
					templateData.setUsrName(secUser.getUsrFName());

				}

				parseMail(template, templateData.getDeclaredFieldValues());
				if (template.isEmailTemplate() && CollectionUtils.isNotEmpty(template.getEmailIds())) {

					Notification emailMessage = new Notification();
					emailMessage.setKeyReference(templateData.getFinReference());
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

					emailEngine.sendEmail(emailMessage);
				}
				if (template.isSmsTemplate()) {
					// sendSMS(mailTemplate); //not implemented yet
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);

	}

	private Map<String, Object> getTemplateData(QueryDetail detail) {
		MailTemplateData data = new MailTemplateData();
		data.setFinReference(detail.getFinReference());
		data.setCustShrtName(detail.getUserDetails().getUserName());
		return data.getDeclaredFieldValues();
	}

	/**
	 * Preparing the template data for Presentemet bounce email processing
	 * 
	 * @param presentmentDetail
	 * @return
	 */
	private Map<String, Object> getTemplateData(PresentmentDetail presentmentDetail) {

		MailTemplateData data = new MailTemplateData();

		FinanceDetail financeDetail = presentmentDetail.getFinanceDetail();
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		List<CustomerEMail> customerEmailList = financeDetail.getCustomerDetails().getCustomerEMailList();
		List<CustomerPhoneNumber> custMobiles = financeDetail.getCustomerDetails().getCustomerPhoneNumList();
		int format = CurrencyUtil.getFormat(main.getFinCcy());

		data.setCustShrtName(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
		data.setFinReference(main.getFinReference());
		data.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(), format));

		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		// Customer Email
		for (CustomerEMail customerEMail : customerEmailList) {
			if (priority != customerEMail.getCustEMailPriority()) {
				continue;
			}
			data.setCustEmailId(customerEMail.getCustEMail());
			break;
		}

		// Customer Contact Number
		for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
			if (priority != customerPhoneNumber.getPhoneTypePriority()) {
				continue;
			}
			data.setCustMobileNumber(customerPhoneNumber.getPhoneNumber());
			break;
		}

		data.setCustId(main.getCustID());
		data.setCustCIF(main.getLovDescCustCIF());
		data.setFinType(main.getFinType());
		data.setNextRepayDate(DateUtil.format(main.getNextRepayDate(), DateFormat.LONG_DATE));
		data.setPriority(main.getPriority());

		data.setValueDate(DateUtility.formatToLongDate(presentmentDetail.getSchDate()));
		data.setAmount(PennantApplicationUtil.amountFormate(presentmentDetail.getPresentmentAmt(), format));
		data.setBounceReason(presentmentDetail.getBounceReason());

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

	/**
	 * Method for Data Preparion
	 * 
	 * @param data
	 * @param main
	 * @return
	 */
	private Map<String, Object> getTemplData(FinanceDetail financeDetail, FinReceiptHeader receiptHeader) {
		MailTemplateData data = new MailTemplateData();
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		List<CustomerAddres> custAddressList = financeDetail.getCustomerDetails().getAddressList();
		List<CustomerEMail> customerEmailList = financeDetail.getCustomerDetails().getCustomerEMailList();
		List<CustomerPhoneNumber> custMobiles = financeDetail.getCustomerDetails().getCustomerPhoneNumList();
		List<FinServiceInstruction> servInstructions = financeDetail.getFinScheduleData().getFinServiceInstructions();
		int format = CurrencyUtil.getFormat(main.getFinCcy());
		// Finance Data Preparation For Notifications
		data.setCustCIF(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		data.setCustShrtName(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
		data.setFinReference(main.getFinReference());
		data.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(), format));
		data.setDownPayment(PennantApplicationUtil.amountFormate(main.getDownPayment(), format));
		data.setFeeAmount(PennantApplicationUtil.amountFormate(main.getFeeChargeAmt(), format));
		data.setInsAmount(PennantApplicationUtil.amountFormate(main.getInsuranceAmt(), format));
		data.setFinCcy(main.getFinCcy());
		data.setFinStartDate(DateUtility.formatToLongDate(main.getFinStartDate()));
		data.setMaturityDate(DateUtility.formatToLongDate(main.getMaturityDate()));
		data.setNumberOfTerms(String.valueOf(main.getNumberOfTerms()));
		data.setGraceTerms(String.valueOf(main.getGraceTerms()));
		data.setFinCurrAssetValue(PennantApplicationUtil.amountFormate(main.getFinCurrAssetValue(), format));
		data.setRepaymentFrequency(main.getRepayFrq());
		data.setGraceBaseRate(main.getGraceBaseRate());
		data.setGraceSpecialRate(main.getGraceSpecialRate());
		data.setRepayBaseRate(main.getRepayBaseRate());
		data.setRepaySpecialRate(main.getRepaySpecialRate());
		data.setRepayMargin(PennantApplicationUtil.amountFormate(main.getRepayMargin(), format));
		data.setFinBranch(main.getFinBranch());
		data.setFinCcy(main.getFinCcy());
		data.setFinDivision(main.getLovDescFinDivision());
		data.setAccountsOfficerDesc(main.getLovDescAccountsOfficer());
		data.setDsaCode(main.getDsaCode());
		data.setDsaDesc(main.getDsaCodeDesc());
		data.setdMACodeDesc(main.getDmaCodeDesc());
		data.setTotalProfit(PennantApplicationUtil.amountFormate(main.getTotalProfit(), format));
		data.setFirstRepay(PennantApplicationUtil.amountFormate(main.getFirstRepay(), format));
		data.setLastRepay(PennantApplicationUtil.amountFormate(main.getLastRepay(), format));
		data.setUserName(main.getUserDetails().getUserName());
		data.setUserBranch(main.getUserDetails().getBranchName());
		data.setUserDepartment(main.getUserDetails().getDepartmentName());

		// Finance Branch Details
		Branch branch = branchDAO.getBranchById(main.getFinBranch(), "_AView");
		if (branch != null) {
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
		}

		// User Branch Details
		if (!StringUtils.equals(main.getFinBranch(), main.getUserDetails().getBranchCode())) {
			branch = branchDAO.getBranchById(main.getUserDetails().getBranchCode(), "_AView");
		}
		if (branch != null) {
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
		}

		// Customer Address
		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);
		for (CustomerAddres customerAddress : custAddressList) {
			if (priority != customerAddress.getCustAddrPriority()) {
				continue;
			}
			data.setCustAddrLine1(customerAddress.getCustAddrLine1());
			data.setCustAddrLine2(customerAddress.getCustAddrLine2());
			data.setCustAddrHNo(customerAddress.getCustAddrHNbr());
			data.setCustAddrFlatNo(customerAddress.getCustFlatNbr());
			data.setCustAddrStreet(customerAddress.getCustAddrStreet());
			data.setCustAddrCountry(customerAddress.getCustAddrCountry());
			data.setCustAddrProvince(customerAddress.getCustAddrProvince());
			data.setCustAddrDistrict(customerAddress.getCustDistrict());
			data.setCustAddrCity(customerAddress.getCustAddrCity());
			data.setCustAddrPincode(customerAddress.getCustPOBox());

			break;
		}
		// Customer Email
		for (CustomerEMail customerEMail : customerEmailList) {
			if (priority != customerEMail.getCustEMailPriority()) {
				continue;
			}
			data.setCustEmailId(customerEMail.getCustEMail());
			break;
		}
		// Customer Contact Number
		for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
			if (priority != customerPhoneNumber.getPhoneTypePriority()) {
				continue;
			}
			data.setCustMobileNumber(customerPhoneNumber.getPhoneNumber());
			break;
		}

		if (main.getEffectiveRateOfReturn() != null) {
			data.setEffectiveRate(PennantApplicationUtil.formatRate(main.getEffectiveRateOfReturn().doubleValue(), 2));
		} else {
			data.setEffectiveRate("");
		}
		data.setCustId(main.getCustID());
		data.setCustCIF(main.getLovDescCustCIF());
		data.setFinType(main.getFinType());
		data.setNextRepayDate(DateUtil.format(main.getNextRepayDate(), DateFormat.LONG_DATE));
		data.setPriority(main.getPriority());

		// Role Code For Alert Notification
		List<SecurityRole> securityRoles = securityRoleDAO.getSecurityRole(main.getRoleCode());
		data.setRoleCode(securityRoles.size() > 0 ? securityRoles.get(0).getRoleDesc() : "");

		// user Details
		SecurityUser secUser = getSecurityUserById(main.getLastMntBy());
		String secUsrFullName = PennantApplicationUtil.getFullName(secUser.getUsrFName(), secUser.getUsrMName(),
				secUser.getUsrLName());
		data.setUsrName(secUsrFullName);
		data.setNextUsrName("");
		data.setPrevUsrName(secUsrFullName);

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, main.getFinSourceID())) {
			WorkFlowDetails workFlowDetails = workFlowDetailsDAO.getWorkFlowDetailsByID(main.getWorkflowId());
			data.setWorkflowType(workFlowDetails == null ? "" : workFlowDetails.getWorkFlowType());
		}

		data.setNextUsrRoleCode(main.getNextRoleCode());
		List<SecurityRole> securityNextRoles = securityRoleDAO.getSecurityRole(main.getNextRoleCode());
		String nextRoleCode = "";
		for (SecurityRole securityRole : securityNextRoles) {
			if (StringUtils.isNotEmpty(nextRoleCode)) {
				nextRoleCode = nextRoleCode + " / " + securityRole.getRoleDesc();
			} else {
				nextRoleCode = securityRole.getRoleDesc();
			}
		}
		data.setNextUsrRole(nextRoleCode);
		data.setPrevUsrRole(main.getLastMntBy());
		data.setUsrRole(main.getRoleCode());
		data.setFinCommitmentRef(main.getFinCommitmentRef());
		data.setRcdMaintainSts(main.getRcdMaintainSts());

		Notes note = new Notes();
		note.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		note.setReference(main.getFinReference());
		List<Notes> list = notesDAO.getNotesListByRole(note, false, new String[] { main.getRoleCode() });
		StringBuilder recommendations = new StringBuilder();
		for (Notes notes : list) {
			recommendations.append(notes.getRemarks());
		}
		data.setRecommendations(recommendations.toString());
		data.setRecordStatus(main.getRecordStatus());
		data.setReceiptPurpose(main.getReceiptPurpose());

		if (receiptHeader != null) {
			data.setReceiptAmount(PennantApplicationUtil.amountFormate(receiptHeader.getReceiptAmount(), format));
			data.setBounceDate(DateUtility.formatToLongDate(receiptHeader.getBounceDate()));
			Date bounceDateValue = receiptHeader.getBounceDate();
			if (bounceDateValue != null) {
				data.setBounceReason(receiptHeader.getManualAdvise().getBounceCodeDesc());
			}
			data.setCancellationReason(receiptHeader.getCancelReason());
			Date valueDate = receiptHeader.getReceiptDate();
			BigDecimal modeAmount = BigDecimal.ZERO;
			if (receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()) {
				for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
					FinReceiptDetail receiptDetail = receiptHeader.getReceiptDetails().get(i);
					if (!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
							&& !StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
							&& !StringUtils.equals(receiptDetail.getPaymentType(),
									RepayConstants.RECEIPTMODE_PAYABLE)) {
						valueDate = receiptDetail.getReceivedDate();
						modeAmount = receiptDetail.getAmount();

					}
				}
			}
			data.setValueDate(DateUtility.formatToLongDate(valueDate));
			data.setAmount(PennantApplicationUtil.amountFormate(modeAmount, format));
		} else if (servInstructions != null && !servInstructions.isEmpty()) {

			FinServiceInstruction instruction = servInstructions.get(0);
			if (!StringUtils.equals(instruction.getFinEvent(), FinanceConstants.FINSER_EVENT_ORG)) {
				data.setValueDate(DateUtility.formatToLongDate(instruction.getFromDate()));
				data.setAmount(PennantApplicationUtil.amountFormate(instruction.getAmount(), format));
			}

		}
		return data.getDeclaredFieldValues();
	}

	/**
	 * Method for Data Preparion
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
		data.setTotAmountBD(PennantApplicationUtil.formatAmount(facility.getAmountBD(), 3, false));
		data.setTotAmountUSD(PennantApplicationUtil.formatAmount(facility.getAmountUSD(), 2, false));
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
		data.setAuditedDate(DateUtility.formatToLongDate(finCreditReviewDetails.getAuditedDate()));
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
	 * Method for Data Preparing
	 * 
	 * @param data
	 * @param InvestmentFinHeader
	 * @return
	 */
	public MailTemplateData getTemplateData(InvestmentFinHeader investmentFinHeader) {
		MailTemplateData data = new MailTemplateData();
		int format = CurrencyUtil.getFormat(investmentFinHeader.getFinCcy());

		// Facility Data Preparation For Notifications
		data.setInvestmentRef(investmentFinHeader.getInvestmentRef());
		data.setTotPrincipalAmt(PennantApplicationUtil.amountFormate(investmentFinHeader.getTotPrincipalAmt(), format));
		data.setFinCcy(investmentFinHeader.getFinCcy());
		data.setStartDate(DateUtility.formatToLongDate(investmentFinHeader.getStartDate()));
		data.setMaturityDate(DateUtility.formatToLongDate(investmentFinHeader.getMaturityDate()));
		data.setPrincipalInvested(
				PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalInvested(), format));
		data.setPrincipalMaturity(
				PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalMaturity(), format));
		data.setPrincipalDueToInvest(
				PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalDueToInvest(), format));
		data.setAvgPftRate(investmentFinHeader.getAvgPftRate().toString());
		// Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(investmentFinHeader.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(investmentFinHeader.getLastMntBy()));
		data.setNextUsrName("");
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(investmentFinHeader.getLastMntBy()));
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(investmentFinHeader.getWorkflowId()));
		data.setNextUsrRoleCode(investmentFinHeader.getNextRoleCode());

		List<SecurityRole> securityUsrRoles = PennantApplicationUtil
				.getRoleCodeDesc(investmentFinHeader.getNextRoleCode());
		data.setNextUsrRole(securityUsrRoles.get(0).getRoleDesc());
		data.setPrevUsrRole(investmentFinHeader.getLastMntBy());
		data.setUsrRole(investmentFinHeader.getRoleCode());

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
		data.setCustShrtName(provision.getLovDescCustShrtName());
		data.setCustCIF(provision.getLovDescCustCIF());
		data.setFinBranch(provision.getFinBranch());
		int format = CurrencyUtil.getFormat(provision.getFinCcy());
		data.setPrincipalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue(), format));
		data.setProfitDue(PennantApplicationUtil.amountFormate(provision.getProfitDue(), format));
		data.setTotalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue().add(provision.getProfitDue()),
				format));
		data.setDueFromDate(DateUtility.formatToLongDate(provision.getDueFromDate()));
		data.setNonFormulaProv(PennantApplicationUtil.amountFormate(provision.getNonFormulaProv(), format));
		data.setProvisionedAmt(PennantApplicationUtil.amountFormate(provision.getProvisionedAmt(), format));
		data.setProvisionedAmtCal(PennantApplicationUtil.amountFormate(provision.getProvisionAmtCal(), format));

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

	/**
	 * Method for Data Preparion
	 * 
	 * @param data
	 * @param InvestmentFinHeader
	 * @return
	 */
	public MailTemplateData getTemplateData(FinanceSuspHead financeSuspHead) {
		MailTemplateData data = new MailTemplateData();
		// Manual Suspense Data Preparation For Notifications
		data.setFinReference(financeSuspHead.getFinReference());
		data.setCustShrtName(financeSuspHead.getLovDescCustShrtName());
		data.setCustCIF(financeSuspHead.getLovDescCustCIFName());
		data.setFinBranch(financeSuspHead.getFinBranch());
		data.setManualSusp(financeSuspHead.isManualSusp() ? "Yes" : "No");
		data.setFinSuspDate(DateUtility.formatToLongDate(financeSuspHead.getFinSuspDate()));
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

	// New Methods
	private HashMap<String, Object> getTemplateData(FinanceDetail aFinanceDetail, FinReceiptHeader receiptHeader) {
		FinanceMain main = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		// Role Code For Alert Notification
		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, main.getFinSourceID())) {
			if (StringUtils.isNotEmpty(main.getRoleCode())) {
				main.setNextRoleCodeDesc(PennantApplicationUtil.getSecRoleCodeDesc(main.getRoleCode()));

				// user Details
				main.setSecUsrFullName(PennantApplicationUtil.getUserDesc(main.getLastMntBy()));
				main.setWorkFlowType(PennantApplicationUtil.getWorkFlowType(main.getWorkflowId()));
			}

		}
		HashMap<String, Object> declaredFieldValues = main.getDeclaredFieldValues();
		declaredFieldValues.put("fm_recordStatus", main.getRecordStatus());
		declaredFieldValues.putAll(customer.getDeclaredFieldValues());
		try {
			declaredFieldValues.putAll(getTemplData(aFinanceDetail, receiptHeader));
		} catch (Exception e) {

		}
		LMSServiceLog lmsServiceLog = aFinanceDetail.getLmsServiceLog();
		if (lmsServiceLog != null && lmsServiceLog.getEvent() != null) {
			declaredFieldValues.putAll(lmsServiceLog.getDeclaredFieldValues());
			declaredFieldValues.put("rc_" + "effectiveDate",
					DateUtility.formatToLongDate(lmsServiceLog.getEffectiveDate()));
		}

		if (receiptHeader != null) {
			declaredFieldValues.put("recordStatus", receiptHeader.getRecordStatus());
			declaredFieldValues.put("rh_receiptPurpose", receiptHeader.getReceiptPurpose());
			declaredFieldValues.put("rh_receiptAmount", receiptHeader.getReceiptAmount());
			declaredFieldValues.put("rh_receiptDate", DateUtility.formatToLongDate(receiptHeader.getReceiptDate()));
			declaredFieldValues.put("rh_balAmount", receiptHeader.getBalAmount());
		}
		if (aFinanceDetail.getPromotion() != null) {
			declaredFieldValues.put("rh_actualInterestRate", aFinanceDetail.getPromotion().getActualInterestRate());
		}
		// put call email template datamap added
		FinOption finOption = aFinanceDetail.getFinOption();
		declaredFieldValues.putAll(DataMapUtil.getDataMap(aFinanceDetail));
		if (finOption != null) {
			declaredFieldValues.put(FieldPrefix.Putcall.getPrefix().concat("code"), finOption.getOptionType());
			declaredFieldValues.put(FieldPrefix.Putcall.getPrefix().concat("description"), finOption.getOptionType());
			declaredFieldValues.put(FieldPrefix.Putcall.getPrefix().concat("nextFrequencyDate"),
					DateUtility.formatToLongDate(finOption.getNextOptionDate()));
			declaredFieldValues.put(FieldPrefix.Putcall.getPrefix().concat("nextOptionDate"),
					DateUtility.formatToLongDate(finOption.getNextOptionDate()));
			declaredFieldValues.put(FieldPrefix.Putcall.getPrefix().concat("currentOptionDate"),
					DateUtility.formatToLongDate(finOption.getCurrentOptionDate()));
		}
		
		//Adding the customer drawing power
		DrawingPower drawingPower = SpringBeanUtil.getBean(DrawingPower.class);
		if (drawingPower != null) {
			BigDecimal drawingPowerVal = drawingPower.getDrawingPower(aFinanceDetail.getFinReference());
			declaredFieldValues.put("drawingPower", drawingPowerVal == null ? BigDecimal.ZERO
					: PennantApplicationUtil.amountFormate(drawingPowerVal, 2));
			declaredFieldValues.put("currentDate", DateUtility.formatToLongDate(SysParamUtil.getAppDate()));
		}		

		return declaredFieldValues;
	}

	private void sendEmailNotification(Notification emailMessage) {
		try {
			emailEngine.sendEmail(emailMessage);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void sendSmsNotification(Notification smsNotification) {

		for (String mobilenumber : smsNotification.getMobileNumbers()) {
			smsNotification.setMobileNumber(mobilenumber);
			try {
				smsEngine.sendSms(smsNotification);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	private MailTemplate getMailTemplate(String rule, Map<String, Object> fieldsAndValues) {
		MailTemplate template = null;
		try {

			int templateId = (Integer) this.ruleExecutionUtil.executeRule(rule, fieldsAndValues, null,
					RuleReturnType.INTEGER);
			if (templateId == 0) {
				logger.warn(String.format("Template not found for the notification rule %s", rule));
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
		String ruleResString = (String) this.ruleExecutionUtil.executeRule(attachmentRule, fieldsAndValues, null,
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
					emails.add(customerEMail.getCustEMail());
					templateData.setCustEmailId(customerEMail.getCustEMail());

				}
			}

			if (CollectionUtils.isNotEmpty(custMobiles)) {
				for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
					mobileNumbers.add(customerPhoneNumber.getPhoneNumber());
					templateData.setCustMobileNumber(customerPhoneNumber.getPhoneNumber());
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

			String ruleResString = (String) this.ruleExecutionUtil.executeRule(notification.getRuleReciepent(),
					fieldsAndValues, null, RuleReturnType.STRING);
			if (StringUtils.isEmpty(ruleResString)) {
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
		if (template.isEmailTemplate()) {
			String[] documentCodes = getAttachmentCode(attachmentRule, fieldsAndValues);

			for (String documnetCode : documentCodes) {
				for (DocumentDetails document : documents) {
					if (documnetCode.equals(document.getDocCategory())) {
						byte[] docImg = document.getDocImage();
						if (docImg == null && document.getDocRefId() != Long.MIN_VALUE) {
							DocumentManager docManager = documentManagerDAO.getById(document.getDocRefId());
							if (docManager != null) {
								docImg = docManager.getDocImage();
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

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
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

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
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

}