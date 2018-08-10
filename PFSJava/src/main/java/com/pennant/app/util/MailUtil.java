package com.pennant.app.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.SysNotificationDetails;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.MailTemplateData;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.NotificationAttribute;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MailUtil {
	private static final Logger logger = Logger.getLogger(MailUtil.class);

	private Configuration freemarkerMailConfiguration;
	private MailTemplateService mailTemplateService;
	private NotificationsService notificationsService;
	private CustomerEMailService customerEMailService;
	private AgreementDefinitionService agreementDefinitionService;
	private SecurityUserOperationsService securityUserOperationsService;
	private RuleExecutionUtil ruleExecutionUtil;
	private SecurityRoleService securityRoleService;
	private SecurityUserService securityUserService;
	private WorkFlowDetailsService workFlowDetailsService;
	private NotesService notesService;
	private DocumentManagerDAO documentManagerDAO;

	@Autowired
	private VehicleDealerService vehicleDealerService;
	@Autowired
	private FinanceReferenceDetailService financeReferenceDetailService;
	@Autowired
	private EmailEngine emailEngine;

	public MailUtil() {
		super();
	}

	public void sendNotification(Notification notification, Object object) throws Exception {
		logger.debug(Literal.ENTERING);
		Map<String, Object> data = null;

		QueryDetail queryDetail;
		if (object instanceof QueryDetail) {
			queryDetail = (QueryDetail) object;
			data = getTemplateData(queryDetail);
		}
		
		Map<String, byte[]> attachements = notification.getAttachments();
		
		MailTemplate template = getMailTemplateService().getMailTemplateByCode(notification.getTemplateCode());
		
		if (template != null && template.isActive()) {
			try {
				parseMail(template, data);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				template = null;
			}
		}

		if (template == null) {
			return;
		}
		
		if (MapUtils.isNotEmpty(attachements)) {
			template.setAttchments(attachements);
		}
		
		Notification message = prepareNotification(notification, 0, template);

		if (template.isEmailTemplate()) {
			sendEmailNotification(message);
		} else if (template.isSmsTemplate()) {
			sendSmsNotification(template, message.getKeyReference());
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
			}

			notification.setKeyReference(keyReference);
			notification.setStage(role);

			sendNotifications(notification, object, null, documents);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void sendNotifications(Notification notification, Object object, String finType,
			List<DocumentDetails> documents) throws IOException, TemplateException {
		boolean resendNotifications = false;
		String finReference = notification.getKeyReference();
		String finEvent = notification.getSubModule();
		String role = notification.getStage();
		String module = notification.getModule();
		List<String> templates = notification.getTemplates();

		List<Long> notificationIds;
		List<Notifications> notifications = null;

		if (!CollectionUtils.isEmpty(templates)) {
			if ("LOAN".equals(module) || "LOAN_ORG".equals(module)) {
				resendNotifications = financeReferenceDetailService.resendNotification(finType, finEvent, role,
						templates);
			}
			notificationIds = financeReferenceDetailService.getNotifications(finType, finEvent, role, templates);
			if (CollectionUtils.isNotEmpty(notificationIds)) {
				notifications = getNotificationsService().getApprovedNotificationsByRuleIdList(notificationIds);
			}
		} else {
			notifications = notificationsService.getApprovedNotificationsByModule(module);
		}

		if (CollectionUtils.isEmpty(notifications)) {
			return;
		}

		Map<String, Object> data = null;
		Map<String, List<String>> emailAndMobiles = null;
		FinanceDetail financeDetail = null;
		FinanceMain financeMain = null;
		CustomerDetails customerDetails = null;
		Commitment commitment = null;
		VASRecording vasRecording;
		QueryDetail queryDetail = null;

		if (object instanceof FinanceDetail) {
			financeDetail = (FinanceDetail) object;
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			if ("LOAN_ORG".equals(module)) {
				data = getTemplateData(financeDetail);
				module = "LOAN";
			} else {
				data = getTemplateData(financeMain);
			}
			customerDetails = financeDetail.getCustomerDetails();
		} else if (object instanceof Commitment) {
			commitment = (Commitment) object;
			customerDetails = commitment.getCustomerDetails();
		} else if (object instanceof VASRecording) {
			// FIXME
		} else if (object instanceof QueryDetail) {
			queryDetail = (QueryDetail) object;
		}

		for (Notifications item : notifications) {
			boolean sendNotification = false;

			long notificationId = item.getRuleId();

			if (resendNotifications) {
				sendNotification = true;
			} else {
				// checking for mail already sent or not
				String stageReq = SysParamUtil.getValueAsString("STAGE_REQ_FOR_MAIL_CHECK");
				boolean mailExists = false;
				if ("Y".equalsIgnoreCase(stageReq)) {
					mailExists = emailEngine.isMailExist(finReference, module, finEvent, notificationId, role);
				} else {
					mailExists = emailEngine.isMailExist(finReference, module, finEvent, notificationId);
				}

				if (!mailExists) {
					sendNotification = true;
				}
			}

			MailTemplate template = null;
			if (sendNotification) {
				emailAndMobiles = getEmailsAndMobile(customerDetails, item, data);

				template = getMailTemplate(item.getRuleTemplate(), data);
				if (template != null && template.isActive()) {
					try {
						parseMail(template, data);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						template = null;
					}
				}

				if (template == null) {
					continue;
				}

				if (template != null && template.isEmailTemplate()) {
					List<String> emails = emailAndMobiles.get("EMAILS");
					template.setLovDescMailId(emails.toArray(new String[emails.size()]));
					setAttachements(template, item.getRuleAttachment(), data, documents);
				}

				Notification emailMessage = prepareNotification(notification, notificationId, template);

				if (template.isEmailTemplate()) {
					sendEmailNotification(emailMessage);
				}

				if (template.isSmsTemplate()) {
					sendSmsNotification(template, finReference);
				}
			}
		}
	}

	private Notification prepareNotification(Notification notification, long notificationId, MailTemplate template) {
		Notification emailMessage = new Notification();
		BeanUtils.copyProperties(emailMessage, notification);

		emailMessage.setNotificationId(notificationId);
		emailMessage.setSubject(template.getEmailSubject());
		emailMessage.setContent(template.getLovDescFormattedContent().getBytes(Charset.forName("UTF-8")));

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

		NotificationAttribute attribute = new NotificationAttribute();
		attribute.setAttribute("Template");
		attribute.setValue(template.getTemplateDesc());
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

		return emailMessage;
	}

	/**
	 * Method for Parsing Mail Details and Send Notification To Users/Customer
	 * 
	 * @param mailTemplate
	 * @param templateData
	 * @throws Exception
	 */
	public void parseMail(MailTemplate mailTemplate, Object templateData) throws Exception {
		logger.debug("Entering");

		String subject = "";
		String result = "";

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vo", templateData);

		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate("mailTemplate",
				new String(mailTemplate.getEmailContent(), NotificationConstants.DEFAULT_CHARSET));
		getFreemarkerMailConfiguration().setTemplateLoader(loader);
		Template template = getFreemarkerMailConfiguration().getTemplate("mailTemplate");

		try {
			result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (IOException e) {
			throw new Exception("Unable to read or process freemarker configuration or template", e);
		} catch (TemplateException e) {
			throw new Exception("Problem initializing freemarker or rendering template ", e);
		}
		StringTemplateLoader subloader = new StringTemplateLoader();
		subloader.putTemplate("mailSubject", mailTemplate.getEmailSubject());
		getFreemarkerMailConfiguration().setTemplateLoader(subloader);
		Template templateSubject = getFreemarkerMailConfiguration().getTemplate("mailSubject");

		try {
			subject = FreeMarkerTemplateUtils.processTemplateIntoString(templateSubject, model);
		} catch (IOException e) {
			throw new Exception("Unable to read or process freemarker configuration or template", e);
		} catch (TemplateException e) {
			throw new Exception("Problem initializing freemarker or rendering template ", e);
		}

		mailTemplate.setLovDescFormattedContent(result);
		mailTemplate.setEmailSubject(subject);

		if (mailTemplate.isSmsTemplate()) {
			loader = new StringTemplateLoader();
			loader.putTemplate("smsTemplate", mailTemplate.getSmsContent());
			getFreemarkerMailConfiguration().setTemplateLoader(loader);
			template = getFreemarkerMailConfiguration().getTemplate("smsTemplate");

			try {
				result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
				throw new Exception("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				logger.debug(Literal.EXCEPTION, e);
				throw new Exception("Problem initializing freemarker or rendering template ", e);
			}
		}

		logger.debug("Leaving");
	}

	public void sendMailtoCustomer(long templateId, String mailID, Object vo) throws TemplateException, Exception {
		logger.debug(Literal.ENTERING);
		MailTemplate template = null;
		try {
			template = getMailTemplateService().getApprovedMailTemplateById(templateId);
			if (template != null && template.isActive()) {

				template.setLovDescMailId(new String[] { mailID });
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
					SecurityUser secUser = getSecurityUserService().getSecurityUserById(notification.getLastMntBy());
					templateData.setUsrName(secUser.getUsrFName());

				}

				parseMail(template, templateData.getDeclaredFieldValues());
				if (template.isEmailTemplate()
						&& StringUtils.isNotEmpty(StringUtils.join(template.getLovDescMailId(), ","))) {

					Notification emailMessage = new Notification();
					emailMessage.setKeyReference(templateData.getFinReference());
					emailMessage.setModule("SYS_NOTIFICATION");
					emailMessage.setSubModule("SYS_NOTIFICATION");
					emailMessage.setNotificationId(templateId);
					emailMessage.setStage("");
					emailMessage.setSubject(template.getEmailSubject());
					emailMessage.setContent(template.getLovDescFormattedContent().getBytes(Charset.forName("UTF-8")));

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

					emailEngine.sendEmail(emailMessage);
				}
				if (template.isSmsTemplate()) {
					//sendSMS(mailTemplate);    //not implemented yet
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);

	}

	public Map<String, Object> getTemplateData(QueryDetail detail) {
		MailTemplateData data = new MailTemplateData();
		data.setFinReference(detail.getFinReference());
		data.setCustShrtName(detail.getUserDetails().getUserName());
		return data.getDeclaredFieldValues();

	}

	/**
	 * Method for Data Preparion
	 * 
	 * @param data
	 * @param main
	 * @return
	 */
	public Map<String, Object> getTemplateData(FinanceMain main) {
		MailTemplateData data = new MailTemplateData();
		int format = CurrencyUtil.getFormat(main.getFinCcy());
		// Finance Data Preparation For Notifications
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
		if (main.getEffectiveRateOfReturn() != null) {
			data.setEffectiveRate(PennantApplicationUtil.formatRate(main.getEffectiveRateOfReturn().doubleValue(), 2));
		} else {
			data.setEffectiveRate("");
		}
		data.setCustShrtName(main.getLovDescCustShrtName());
		data.setCustId(main.getCustID());
		data.setCustCIF(main.getLovDescCustCIF());
		data.setFinType(main.getFinType());
		data.setPriority(main.getPriority());

		// Role Code For Alert Notification
		List<SecurityRole> securityRoles = getSecurityRoleService().getSecRoleCodeDesc(main.getRoleCode());
		data.setRoleCode(securityRoles.size() > 0 ? securityRoles.get(0).getRoleDesc() : "");

		// user Details
		SecurityUser secUser = getSecurityUserService().getSecurityUserById(main.getLastMntBy());
		String secUsrFullName = PennantApplicationUtil.getFullName(secUser.getUsrFName(), secUser.getUsrMName(),
				secUser.getUsrLName());
		data.setUsrName(secUsrFullName);
		data.setNextUsrName("");
		data.setPrevUsrName(secUsrFullName);
		WorkFlowDetails workFlowDetails = getWorkFlowDetailsService().getWorkFlowDetailsByID(main.getWorkflowId());
		data.setWorkflowType(workFlowDetails == null ? "" : workFlowDetails.getWorkFlowType());
		data.setNextUsrRoleCode(main.getNextRoleCode());
		List<SecurityRole> securityNextRoles = getSecurityRoleService().getSecRoleCodeDesc(main.getNextRoleCode());
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
		data.setFinPurpose(main.getLovDescFinPurposeName());
		data.setFinCommitmentRef(main.getFinCommitmentRef());
		data.setFinBranch(main.getLovDescFinBranchName());
		data.setRcdMaintainSts(main.getRcdMaintainSts());

		Notes note = new Notes();
		note.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		note.setReference(main.getFinReference());
		List<Notes> list = getNotesService().getNotesListByRole(note, false, new String[] { main.getRoleCode() });
		StringBuilder recommendations = new StringBuilder();
		for (Notes notes : list) {
			recommendations.append(notes.getRemarks());
		}
		data.setRecommendations(recommendations.toString());
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
	 * Method for Data Preparion
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

	public Map<String, Object> getTemplateData(FinanceDetail aFinanceDetail) {
		FinanceMain main = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		// Role Code For Alert Notification
		main.setNextRoleCodeDesc(PennantApplicationUtil.getSecRoleCodeDesc(main.getRoleCode()));

		// user Details
		main.setSecUsrFullName(PennantApplicationUtil.getUserDesc(main.getLastMntBy()));
		main.setWorkFlowType(PennantApplicationUtil.getWorkFlowType(main.getWorkflowId()));
		main.setFinPurpose(main.getLovDescFinPurposeName());
		main.setFinBranch(main.getLovDescFinBranchName());

		HashMap<String, Object> declaredFieldValues = main.getDeclaredFieldValues();
		declaredFieldValues.put("fm_recordStatus", main.getRecordStatus());
		declaredFieldValues.putAll(customer.getDeclaredFieldValues());

		return declaredFieldValues;
	}

	private void sendEmailNotification(Notification emailMessage) {
		try {
			mailTemplateService.sendMail(emailMessage);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void sendSmsNotification(MailTemplate smsTemplate, String finReference) {
		/*
		 * try { if (isExtSMSService()) { // send mail to external service // send SMS to external service if
		 * (smsTemplate != null) { List<MailTemplate> list = new ArrayList<>(); list.add(smsTemplate);
		 * 
		 * getShortMessageService().sendMessage(list, finReference); }
		 * 
		 * } else { //getMailUtil().sendMail(notification, fieldsAndValues, docDetailsList, mailIDMap, null);
		 * 
		 * }
		 * 
		 * } catch (Exception e) { logger.error(Literal.EXCEPTION, e); }
		 */}

	private MailTemplate getMailTemplate(String rule, Map<String, Object> fieldsAndValues) {
		MailTemplate template = null;
		try {

			int templateId = (Integer) this.ruleExecutionUtil.executeRule(rule, fieldsAndValues, null,
					RuleReturnType.INTEGER);

			if (templateId == 0) {
				logger.warn(String.format("Template not found for the notification rule %s", rule));
				return null;
			}

			template = mailTemplateService.getApprovedMailTemplateById(templateId);
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

	public Map<String, List<String>> getEmailsAndMobile(CustomerDetails customerDetails, Notifications notification,
			Map<String, Object> fieldsAndValues) {
		Map<String, List<String>> map = new HashMap<>();
		List<CustomerEMail> custEmails = customerDetails.getCustomerEMailList();
		List<CustomerPhoneNumber> custMobiles = customerDetails.getCustomerPhoneNumList();
		String templateType = notification.getTemplateType();

		List<String> emails = new ArrayList<>();
		List<String> mobileNumbers = new ArrayList<>();

		// Customer notification
		if (NotificationConstants.TEMPLATE_FOR_CN.equals(templateType)) {
			if (CollectionUtils.isNotEmpty(custEmails)) {
				for (CustomerEMail customerEMail : custEmails) {
					emails.add(customerEMail.getCustEMail());
				}
			}

			if (CollectionUtils.isNotEmpty(custMobiles)) {
				for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
					mobileNumbers.add(customerPhoneNumber.getPhoneNumber());
				}
			}
		} else if (NotificationConstants.TEMPLATE_FOR_SP.equals(templateType)) {
			long vehicleDealerid = customerDetails.getCustomer().getCustRO1();
			VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(vehicleDealerid);

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
				List<String> emailList = securityUserOperationsService.getUsrMailsByRoleIds(ruleResString);

				if (CollectionUtils.isNotEmpty(emailList)) {
					emails.addAll(emailList);
				}
			}
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

						template.getAttchments().put(document.getDocName(), docImg);
					}
				}
			}
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFreemarkerMailConfiguration(Configuration configuration) {
		this.freemarkerMailConfiguration = configuration;
	}

	public Configuration getFreemarkerMailConfiguration() {
		return freemarkerMailConfiguration;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public MailTemplateService getMailTemplateService() {
		return mailTemplateService;
	}

	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}

	public CustomerEMailService getCustomerEMailService() {
		return customerEMailService;
	}

	public SecurityUserOperationsService getSecurityUserOperationsService() {
		return securityUserOperationsService;
	}

	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public NotificationsService getNotificationsService() {
		return notificationsService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return agreementDefinitionService;
	}

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public NotesService getNotesService() {
		return notesService;
	}

	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public SecurityRoleService getSecurityRoleService() {
		return securityRoleService;
	}

	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}

	public SecurityUserService getSecurityUserService() {
		return securityUserService;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	public WorkFlowDetailsService getWorkFlowDetailsService() {
		return workFlowDetailsService;
	}

	public void setWorkFlowDetailsService(WorkFlowDetailsService workFlowDetailsService) {
		this.workFlowDetailsService = workFlowDetailsService;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setEmailEngine(EmailEngine emailEngine) {
		this.emailEngine = emailEngine;
	}

}