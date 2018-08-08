package com.pennant.app.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.SysNotificationDetails;
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
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.EmailMessage;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pennapps.notification.email.model.MessageAttribute;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MailUtil extends MailUtility {
	private static final long				serialVersionUID	= -2543427090845637670L;
	private static final Logger				logger				= Logger.getLogger(MailUtil.class);

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

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public MailUtil() {
		super();
	}

	public void sendMail(String moduleCode, Object object, Object controller) throws Exception {
		processMailSending(moduleCode, object, controller);
	}

	public void sendMail(List<Long> notificationIdList, FinanceDetail financeDetail,
			Map<String, List<String>> mailIdMap, byte[] bs) throws Exception {
		processMailSending(notificationIdList, financeDetail.getFinScheduleData().getFinanceMain(),
				financeDetail.getDocumentDetailsList(), mailIdMap, bs, null);
	}

	public boolean sendMail(List<Long> notificationIdList, FinanceMain financeMain) throws Exception {
		return processMailSending(notificationIdList, financeMain, null, null, null, null);
	}

	public boolean sendMail(List<Long> notificationIdList, FinanceDetail financeDetail,
			Map<String, List<String>> mailIdMap, byte[] bs, String fileName) throws Exception {
		return processMailSending(notificationIdList, financeDetail.getFinScheduleData().getFinanceMain(),
				financeDetail.getDocumentDetailsList(), mailIdMap, bs, fileName);
	}
	
	public void sendMail(Notifications notification, HashMap<String, Object> fieldsAndValues,
			List<DocumentDetails> docList,
			Map<String, List<String>> mailIdMap, byte[] bs) throws Exception {
		processMailSending(notification, fieldsAndValues, docList, mailIdMap, bs, null);
	}

	private boolean processMailSending(List<Long> notificationIdList, FinanceMain financeMain,
			List<DocumentDetails> docList, Map<String, List<String>> mailIdMap, byte[] bs, String filename)
			throws Exception {
		logger.debug("Entering");
		boolean mailsend = false;

		// Fetching List of Notification using Notification ID list
		List<Notifications> notificationsList = getNotificationsService().getApprovedNotificationsByRuleIdList(
				notificationIdList);

		if (notificationsList.isEmpty()) {
			logger.debug("No Notificatin Defined...");
			return false;
		}

		List<DocumentDetails> documentslist = null;
		try {

			// Preparation of Mail Template Data for Rule Execution To Create
			// Template with Structured Data
			MailTemplateData templateData = getTemplateData(financeMain);
			documentslist = docList;

			for (Notifications notifications : notificationsList) {
				HashMap<String, Object> fieldsAndValues = templateData.getDeclaredFieldValues();

				// Getting Mail Template
				Integer templateId = (Integer) getRuleExecutionUtil().executeRule(notifications.getRuleTemplate(),
						fieldsAndValues, null, RuleReturnType.INTEGER);
				if (templateId == 0) { //FIXME to be verified
					continue;
				}

				MailTemplate mailTemplate = getMailTemplateService().getApprovedMailTemplateById(templateId); //FIXME templateID should be long
				if (mailTemplate != null && mailTemplate.isActive()) {
					List<String> emailList = null;
					if (NotificationConstants.TEMPLATE_FOR_AE.equals(notifications.getTemplateType())
							|| NotificationConstants.TEMPLATE_FOR_TAT.equals(notifications.getTemplateType())
							|| NotificationConstants.TEMPLATE_FOR_QP.equals(notifications.getTemplateType())
							|| NotificationConstants.TEMPLATE_FOR_GE.equals(notifications.getTemplateType())) {

						// Getting UserRoles
						String ruleResString = (String) getRuleExecutionUtil().executeRule(
								notifications.getRuleReciepent(), fieldsAndValues, null, RuleReturnType.STRING);
						if (StringUtils.isEmpty(ruleResString)) { //FIXME to be verified
							continue;
						}

						// Prepare Mail ID Details
						emailList = getSecurityUserOperationsService().getUsrMailsByRoleIds(ruleResString);

					} else {

						// If No mail Id exists No need to continue
						if (mailIdMap == null) {
							continue;
						}

						// Other Type of Template, we need to Fetch from Map
						// passing as parameter using Template Type in
						// Notification
						if (!mailIdMap.containsKey(notifications.getTemplateType())) {
							continue;
						}
						emailList = mailIdMap.get(notifications.getTemplateType());
					}

					if (emailList == null || emailList.isEmpty()) {
						continue;
					}

					// Check Mail ID List
					String[] mailId = null;
					List<String> usrMailList = new ArrayList<String>();
					if (emailList != null && !emailList.isEmpty()) {
						for (String usrMail : emailList) {
							if (StringUtils.isNotBlank(usrMail)) {
								usrMailList.add(usrMail);
							}
						}
						mailId = new String[usrMailList.size()];
						mailId = (String[]) usrMailList.toArray(mailId);
						usrMailList = null;
					}
					if (mailId != null && StringUtils.isNotEmpty(StringUtils.join(mailId, ","))) {
						// Template Fields Bean Preparation
						mailTemplate.setLovDescMailId(mailId);
						parseMail(mailTemplate, templateData);
					}

					// Getting the Attached Documents
					String ruleResString = (String) getRuleExecutionUtil().executeRule(
							notifications.getRuleAttachment(), fieldsAndValues, null, RuleReturnType.STRING);
					Map<String,byte[]> attchment= new HashMap<>();
					if (StringUtils.isNotEmpty(ruleResString)) {
						String[] documentCtgList = (ruleResString).split(",");
						for (String docCtg : documentCtgList) {
							if (documentslist != null) {
								for (DocumentDetails documentDetails : documentslist) {
									if (docCtg.equals(documentDetails.getDocCategory())) {
										byte[] docImg = documentDetails.getDocImage();
										if (docImg == null && documentDetails.getDocRefId() != Long.MIN_VALUE) {
											DocumentManager docManager = documentManagerDAO
													.getById(documentDetails.getDocRefId());
											if (docManager != null) {
												docImg = docManager.getDocImage();
											}
										}
										attchment.put(documentDetails.getDocName(), documentDetails.getDocImage());
									}
								}
							}
						}
						mailTemplate.setAttchments(attchment);
					}

					if (mailTemplate.isEmailTemplate()
							&& StringUtils.isNotEmpty(StringUtils.join(mailTemplate.getLovDescMailId(), ","))) {
						sendMail(mailTemplate);
						mailsend = true;
					}
				}

			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			mailsend = false;
		}

		logger.debug("Leaving");
		return mailsend;
	}

	private void processMailSending(String moduleCode, Object object, Object controller) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Notifications> notifications = getNotificationsService().getApprovedNotificationsByModule(moduleCode);
		MailTemplate mailTemplate = null;
		String[] mailId = null;
		List<String> emailList = null;
		List<DocumentDetails> documents = null;
		MailTemplateData templateData = null;

		String keyReference = null;

		try {
			FinanceDetail financeDetail;
			FinanceMain financeMain;
			if (object instanceof FinanceDetail) {
				financeDetail = (FinanceDetail) object;
				financeMain = financeDetail.getFinScheduleData().getFinanceMain();

				templateData = getTemplateData(financeDetail.getFinScheduleData().getFinanceMain());
				documents = financeDetail.getDocumentDetailsList();

				keyReference = financeMain.getFinReference();
			} else if (object instanceof Facility) {
				Facility facility = (Facility) object;
				templateData = getTemplateData((Facility) object);
				documents = facility.getDocumentDetailsList();

				keyReference = facility.getCAFReference();
			} else if (object instanceof FinCreditReviewDetails) {
				FinCreditReviewDetails finCreditReviewDetails = (FinCreditReviewDetails) object;

				templateData = getTemplateData(finCreditReviewDetails);

				keyReference = finCreditReviewDetails.getLovDescCustCIF();
			} else if (object instanceof InvestmentFinHeader) {
				templateData = getTemplateData((InvestmentFinHeader) object);
			} else if (object instanceof RepayData) {
				RepayData repayData = (RepayData) object;
				financeMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

				templateData = getTemplateData(financeMain);
				documents = repayData.getFinanceDetail().getDocumentDetailsList();
				keyReference = repayData.getFinReference();
			} else if (object instanceof FinanceWriteoffHeader) {
				FinanceWriteoffHeader writeoffHeader = (FinanceWriteoffHeader) object;
				financeMain = writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();
				templateData = getTemplateData(financeMain);
				keyReference = writeoffHeader.getFinReference();
			} else if (object instanceof Provision) {
				Provision provision = (Provision) object;
				templateData = getTemplateData(provision);
				keyReference = provision.getFinReference();
			} else if (object instanceof FinanceSuspHead) {
				FinanceSuspHead suspHead = (FinanceSuspHead) object;
				templateData = getTemplateData(suspHead);
				keyReference = suspHead.getFinReference();
			}

			HashMap<String, Object> fieldsAndValues = templateData.getDeclaredFieldValues();
			for (Notifications notification : notifications) {
				// Getting Mail Template
				Integer templateId = (Integer) ruleExecutionUtil.executeRule(notification.getRuleTemplate(),
						fieldsAndValues, null, RuleReturnType.INTEGER);
				if (templateId == 0) {
					continue;
				}

				mailTemplate = getMailTemplateService().getApprovedMailTemplateById(templateId);
				if (mailTemplate != null && mailTemplate.isActive()) {
					//mailTemplate.setStage(financeMain.getRoleCode());
					// Getting UserRoles
					String ruleResString = (String) ruleExecutionUtil.executeRule(notification.getRuleReciepent(),
							fieldsAndValues, null, RuleReturnType.STRING);
					if (StringUtils.isEmpty(ruleResString)) {
						continue;
					}

					// Prepare Mail ID Details
					emailList = getSecurityUserOperationsService().getUsrMailsByRoleIds(ruleResString);

					// Check Mail List
					List<String> usrMailList = new ArrayList<>();
					if (CollectionUtils.isNotEmpty(emailList)) {
						for (String usrMail : emailList) {
							if (StringUtils.isNotBlank(usrMail)) {
								usrMailList.add(usrMail);
							}
						}
						mailId = new String[usrMailList.size()];
						mailId = (String[]) usrMailList.toArray(mailId);
					}
					if (mailId != null && StringUtils.isNotEmpty(StringUtils.join(mailId, ","))) {
						mailTemplate.setLovDescMailId(mailId);
						parseMail(mailTemplate, templateData);
					}

					// Getting the Attached Documents
					ruleResString = (String) ruleExecutionUtil.executeRule(notification.getRuleAttachment(),
							fieldsAndValues, null, RuleReturnType.STRING);
					Map<String,byte[]> attchments= new HashMap<>();
					if (StringUtils.isNotEmpty(ruleResString)) {
						if (CollectionUtils.isNotEmpty(documents)) {
							for (DocumentDetails documentDetails : documents) {
								if (ruleResString.equals(documentDetails.getDocCategory())) {
									attchments.put(documentDetails.getDocName(), documentDetails.getDocImage());
								}
							}
						}
						mailTemplate.setAttchments(attchments);
					}

					if (mailTemplate.isEmailTemplate()
							&& StringUtils.isNotEmpty(StringUtils.join(mailTemplate.getLovDescMailId(), ","))) {
						sendMail(mailTemplate);
						//mailStoring(mailTemplate);
					}
				}

			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}


	public MailTemplate getMailTemplate(Notifications notification, HashMap<String, Object> fieldsAndValues,
			List<DocumentDetails> docList) {
		logger.debug(Literal.ENTERING);
		MailTemplate mailTemplate = null;

		String templateRule = notification.getRuleTemplate();

		try {

			// Getting Mail Template
			int templateId = (Integer) this.ruleExecutionUtil.executeRule(templateRule, fieldsAndValues, null,
					RuleReturnType.INTEGER);

			if (templateId == 0) {
				logger.warn(String.format("Template not found for the notification rule %s", templateRule));
				return null;
			}

			mailTemplate = mailTemplateService.getApprovedMailTemplateById(templateId);
			if (mailTemplate != null && mailTemplate.isActive() && mailTemplate.isEmailTemplate()) {

				parseMail(mailTemplate, fieldsAndValues);

				// Getting the Attached Documents
				String ruleResString = (String) this.ruleExecutionUtil.executeRule(notification.getRuleAttachment(),
						fieldsAndValues, null, RuleReturnType.STRING);
				Map<String, byte[]> attchments = new HashMap<>();
				if (StringUtils.isNotEmpty(ruleResString)) {
					if (docList != null) {
						for (DocumentDetails documentDetails : docList) {
							byte[] docImg = documentDetails.getDocImage();
							if (docImg == null && documentDetails.getDocRefId() != Long.MIN_VALUE) {
								DocumentManager docManager = documentManagerDAO.getById(documentDetails.getDocRefId());
								if (docManager != null) {
									docImg = docManager.getDocImage();
								}
							}
							if (docImg != null) {
								attchments.put(documentDetails.getDocName(), docImg);
							}
						}
					}

					mailTemplate.setAttchments(attchments);
				}

				return mailTemplate;
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");

		return mailTemplate;

	}

	private MailTemplate processMailSending(Notifications notification, HashMap<String, Object> fieldsAndValues,
			List<DocumentDetails> docList, Map<String, List<String>> mailIdMap, byte[] bs, String filename)
					throws Exception {
		logger.debug(Literal.ENTERING);
		MailTemplate mailTemplate = null;
		
		try {
			
			// Getting Mail Template
			Long templateId = (Long) this.ruleExecutionUtil.executeRule(notification.getRuleTemplate(), fieldsAndValues,
					null, RuleReturnType.INTEGER);

			if (templateId == 0) {
				logger.warn(String.format("Template not found for the notification rule %s",
						notification.getRuleTemplate()));
				return null;
			}

			mailTemplate = getMailTemplateService().getApprovedMailTemplateById(templateId);
			if (mailTemplate != null && mailTemplate.isActive() && mailTemplate.isEmailTemplate()) {
				List<String> emailList = null;
				if (NotificationConstants.TEMPLATE_FOR_AE.equals(notification.getTemplateType())
						|| NotificationConstants.TEMPLATE_FOR_TAT.equals(notification.getTemplateType())
						|| NotificationConstants.TEMPLATE_FOR_QP.equals(notification.getTemplateType())
						|| NotificationConstants.TEMPLATE_FOR_GE.equals(notification.getTemplateType())) {
					// Getting UserRoles
					String ruleResString = (String) this.ruleExecutionUtil.executeRule(notification.getRuleReciepent(),
							fieldsAndValues, null, RuleReturnType.STRING);
					if (StringUtils.isEmpty(ruleResString)) {
						return null;
					}
					// Prepare Mail ID Details
					emailList = getSecurityUserOperationsService().getUsrMailsByRoleIds(ruleResString);
				} else {
					if (mailIdMap == null) {
						return null;
					}
					if (!mailIdMap.containsKey(notification.getTemplateType())) {
						return null;
					}
					emailList = mailIdMap.get(notification.getTemplateType());
				}

				if (emailList == null || emailList.isEmpty()) {
					return null;
				}

				// Check Mail ID List
				String[] mailId = null;
				List<String> usrMailList = new ArrayList<String>();
				if (emailList != null && !emailList.isEmpty()) {
					for (String usrMail : emailList) {
						if (StringUtils.isNotBlank(usrMail)) {
							usrMailList.add(usrMail);
						}
					}
					mailId = new String[usrMailList.size()];
					mailId = (String[]) usrMailList.toArray(mailId);
					usrMailList = null;
				}

				if (mailId != null && StringUtils.isNotEmpty(StringUtils.join(mailId, ","))) {
					// Template Fields Bean Preparation
					mailTemplate.setLovDescMailId(mailId);
					parseMail(mailTemplate, fieldsAndValues);
				}

				// Getting the Attached Documents
				String ruleResString = (String) this.ruleExecutionUtil.executeRule(notification.getRuleAttachment(),
						fieldsAndValues, null, RuleReturnType.STRING);
				Map<String, byte[]> attchments = new HashMap<>();
				if (StringUtils.isNotEmpty(ruleResString)) {
					if (docList != null) {
						for (DocumentDetails documentDetails : docList) {
							byte[] docImg = documentDetails.getDocImage();
							if (docImg == null && documentDetails.getDocRefId() != Long.MIN_VALUE) {
								DocumentManager docManager = documentManagerDAO.getById(documentDetails.getDocRefId());
								if (docManager != null) {
									docImg = docManager.getDocImage();
									}
							}
							attchments.put(documentDetails.getDocName(), docImg);
						}
					}

					mailTemplate.setAttchments(attchments);
				}
				
				if (mailTemplate.isEmailTemplate()
						&& StringUtils.isNotEmpty(StringUtils.join(mailTemplate.getLovDescMailId(), ","))) {
					sendMail(mailTemplate);
				}
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
		logger.debug("Leaving");
		
		return mailTemplate;
	}

	/*
	 * private boolean resendingMail(EmailMessage message) { logger.debug("Entering");
	 * 
	 * boolean resendmail = false;
	 * 
	 * if(message.getStatus() == "S"){ if(message.getModule() == null){
	 * 
	 * }
	 * 
	 * 
	 * }else{
	 * 
	 * }
	 * 
	 * logger.debug("Leaving"); return resendmail;
	 * 
	 * }
	 */

	/**
	 * Prepare and send mail
	 * */
	/*
	 * public void sendMail(long templateId, String templateFor, Object vo) { logger.debug("Entering"); MailTemplate
	 * mailTemplate = null; try { mailTemplate = getMailTemplateService().getApprovedMailTemplateById(templateId); if
	 * (mailTemplate != null && mailTemplate.isActive()) {
	 * 
	 * // Template Fields Bean Preparation MailTemplateData templateData = new MailTemplateData(); if (vo instanceof
	 * FinanceMain) { templateData = getPreparedMailData(templateData, (FinanceMain) vo); }
	 * 
	 * if (vo instanceof Facility) { templateData = getPrepareFacilityMailData(templateData, (Facility) vo); }
	 * 
	 * if (vo instanceof FinCreditReviewDetails) { templateData = getPrepareCreditReviewMailData(templateData,
	 * (FinCreditReviewDetails) vo); }
	 * 
	 * if (vo instanceof InvestmentFinHeader) { templateData = getPrepareTreasuryInvestmentMailData(templateData,
	 * (InvestmentFinHeader) vo); }
	 * 
	 * parseMail(mailTemplate, templateData); if (mailTemplate.isEmailTemplate() &&
	 * StringUtils.isNotEmpty(StringUtils.join(mailTemplate.getLovDescMailId(), ","))) { //sendMail(mailTemplate);
	 * mailStoring(mailTemplate); } if (mailTemplate.isSmsTemplate()) { //sendSMS(mailTemplate); //not implemented yet }
	 * } } catch (Exception e) { logger.debug("Exception: ", e); } logger.debug("Leaving"); }
	 */
	
	/**
	 * Method for Parsing Mail Details and Send Notification To Users/Customer
	 * 
	 * @param mailTemplate
	 * @param templateData
	 * @throws Exception
	 */
	private void parseMail(MailTemplate mailTemplate, Object templateData) throws Exception {
		logger.debug("Entering");
		
		String subject = "";
		String result = "";
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vo", templateData);
		
		/*
		 * String reportSrc = PathUtil.getPath(""); File file = new File(reportSrc +"/sample_body.html"); if
		 * (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(mailTemplate.getEmailFormat())) {
		 * mailTemplate.setEmailContent(FileUtils.readFileToByteArray(file)); }
		 */
		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate("mailTemplate", new String(mailTemplate.getEmailContent(),
				NotificationConstants.DEFAULT_CHARSET));
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
				logger.debug("Exception: ", e);
				throw new Exception("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				logger.debug("Exception: ", e);
				throw new Exception("Problem initializing freemarker or rendering template ", e);
			}
		}
		
		logger.debug("Leaving");
	}

	public void sendMailtoCustomer(long templateId, String mailID, Object vo) throws TemplateException, Exception {

		logger.debug("Entering");
		MailTemplate mailTemplate = null;
		try {
			mailTemplate = getMailTemplateService().getApprovedMailTemplateById(templateId);
			if (mailTemplate != null && mailTemplate.isActive()) {

				mailTemplate.setLovDescMailId(new String[] { mailID });
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

				parseMail(mailTemplate, templateData);
				if (mailTemplate.isEmailTemplate()
						&& StringUtils.isNotEmpty(StringUtils.join(mailTemplate.getLovDescMailId(), ","))) {
					sendMail(mailTemplate);
				}
				if (mailTemplate.isSmsTemplate()) {
					//sendSMS(mailTemplate);    //not implemented yet
				}
			}
		} catch (TemplateException e) {
			logger.debug("Exception: ", e);
			throw e;
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving");

	}

	/**
	 * Method for Parsing Mail Details and Send Notification To Users/Customer
	 * 
	 * @param mailTemplate
	 * @param templateData
	 * @throws Exception
	 */
	private void parseMail(MailTemplate mailTemplate, MailTemplateData templateData) throws Exception {
		logger.debug("Entering");

		String subject = "";
		String result = "";

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vo", templateData);

		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate("mailTemplate", new String(mailTemplate.getEmailContent(),
				NotificationConstants.DEFAULT_CHARSET));
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
				logger.debug("Exception: ", e);
				throw new Exception("Unable to read or process freemarker configuration or template", e);
			} catch (TemplateException e) {
				logger.debug("Exception: ", e);
				throw new Exception("Problem initializing freemarker or rendering template ", e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Data Preparion
	 * 
	 * @param data
	 * @param main
	 * @return
	 */
	public MailTemplateData getTemplateData(FinanceMain main) {
		MailTemplateData data = new MailTemplateData();
		int format=CurrencyUtil.getFormat(main.getFinCcy());
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
		return data;
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

		List<SecurityRole> securityUsrRoles = PennantApplicationUtil.getRoleCodeDesc(finCreditReviewDetails
				.getNextRoleCode());
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
		data.setTotPrincipalAmt(PennantApplicationUtil.amountFormate(investmentFinHeader.getTotPrincipalAmt(),
				format));
		data.setFinCcy(investmentFinHeader.getFinCcy());
		data.setStartDate(DateUtility.formatToLongDate(investmentFinHeader.getStartDate()));
		data.setMaturityDate(DateUtility.formatToLongDate(investmentFinHeader.getMaturityDate()));
		data.setPrincipalInvested(PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalInvested(),
				format));
		data.setPrincipalMaturity(PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalMaturity(),
				format));
		data.setPrincipalDueToInvest(PennantApplicationUtil.amountFormate(
				investmentFinHeader.getPrincipalDueToInvest(), format));
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

		List<SecurityRole> securityUsrRoles = PennantApplicationUtil.getRoleCodeDesc(investmentFinHeader
				.getNextRoleCode());
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
		data.setPrincipalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue(),
				format));
		data.setProfitDue(PennantApplicationUtil.amountFormate(provision.getProfitDue(),
				format));
		data.setTotalDue(PennantApplicationUtil.amountFormate(
				provision.getPrincipalDue().add(provision.getProfitDue()), format));
		data.setDueFromDate(DateUtility.formatToLongDate(provision.getDueFromDate()));
		data.setNonFormulaProv(PennantApplicationUtil.amountFormate(provision.getNonFormulaProv(),
				format));
		data.setProvisionedAmt(PennantApplicationUtil.amountFormate(provision.getProvisionedAmt(),
				format));
		data.setProvisionedAmtCal(PennantApplicationUtil.amountFormate(provision.getProvisionAmtCal(),
				format));

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

	public HashMap<String, Object> getPreparedMailData(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		FinanceMain main = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		// Role Code For Alert Notification
		main.setNextRoleCodeDesc(PennantApplicationUtil.getSecRoleCodeDesc(main.getRoleCode()));

		// user Details
		main.setSecUsrFullName(PennantApplicationUtil.getUserDesc(main.getLastMntBy()));
		main.setWorkFlowType(PennantApplicationUtil.getWorkFlowType(main.getWorkflowId()));
		main.setFinPurpose(main.getLovDescFinPurposeName());
		main.setFinBranch(main.getLovDescFinBranchName());

		logger.debug("Leaving");

		HashMap<String, Object> declaredFieldValues = main.getDeclaredFieldValues();
		declaredFieldValues.put("fm_recordStatus", main.getRecordStatus());
		declaredFieldValues.putAll(customer.getDeclaredFieldValues());

		return declaredFieldValues;
	}

	public void processNotifications(FinanceDetail financeDetail, String moduleDefiner)
			throws IOException, TemplateException {
		boolean resendNotifications = false;
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		String finType = financeMain.getFinType();
		String finEvent = StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner;
		String role = financeMain.getRoleCode();
		String module = "LOAN";

		List<String> templates = new ArrayList<String>();
		templates.add(NotificationConstants.TEMPLATE_FOR_AE);
		templates.add(NotificationConstants.TEMPLATE_FOR_CN);

		if (FinanceConstants.FINSER_EVENT_ORG.equals(finEvent)) {
			templates.add(NotificationConstants.TEMPLATE_FOR_SP);
		}

		List<Long> notificationIds;
		List<Notifications> notifications = null;

		resendNotifications = financeReferenceDetailService.resendNotification(finType, finEvent, role, templates);
		notificationIds = financeReferenceDetailService.getNotifications(finType, finEvent, role, templates);

		if (CollectionUtils.isNotEmpty(notificationIds)) {
			notifications = getNotificationsService().getApprovedNotificationsByRuleIdList(notificationIds);
		}

		if (CollectionUtils.isNotEmpty(notifications)) {
			for (Notifications notification : notifications) {
				boolean sendNotification = false;

				long notificationId = notification.getRuleId();

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
					template = loadEmailTemplate(financeDetail, notification);

					if (template == null) {
						continue;
					}

					if (template.isEmailTemplate()) {
						EmailMessage emailMessage = new EmailMessage();
						emailMessage.setKeyReference(finReference);
						emailMessage.setModule(module);
						emailMessage.setSubModule(finEvent);
						emailMessage.setNotificationId(notificationId);
						emailMessage.setStage(role);
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
						for (Entry<String, byte[]> item : attachments.entrySet()) {
							MessageAttachment attachment = new MessageAttachment();
							attachment.setAttachment(item.getValue());
							attachment.setFileName(item.getKey());
							emailMessage.getAttachmentList().add(attachment);
						}

						MessageAttribute attribute = new MessageAttribute();
						attribute.setAttribute("Template");
						attribute.setValue(template.getTemplateDesc());
						emailMessage.getAttributeList().add(attribute);

						attribute = new MessageAttribute();
						attribute.setAttribute("Template_id");
						attribute.setValue(String.valueOf(template.getTemplateId()));
						emailMessage.getAttributeList().add(attribute);

						attribute = new MessageAttribute();
						attribute.setAttribute("Template_For");
						attribute.setValue(template.getTemplateFor());
						emailMessage.getAttributeList().add(attribute);

						attribute = new MessageAttribute();
						attribute.setAttribute("Stage");
						attribute.setValue(role);
						emailMessage.getAttributeList().add(attribute);

						sendEmailNotification(emailMessage);
					}
					if (template.isSmsTemplate()) {
						sendSmsNotification(template, finReference);
					}
				}
			}
		}
	}

	private void sendEmailNotification(EmailMessage emailMessage) {
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

	public MailTemplate loadEmailTemplate(FinanceDetail aFinanceDetail, Notifications notification) {
		CustomerDetails customerDetails = aFinanceDetail.getCustomerDetails();
		List<CustomerEMail> custEmails = customerDetails.getCustomerEMailList();
		List<CustomerPhoneNumber> custMobiles = customerDetails.getCustomerPhoneNumList();
		List<DocumentDetails> docDetailsList = aFinanceDetail.getDocumentDetailsList();
		String templateType = notification.getTemplateType();

		HashMap<String, Object> fieldsAndValues = getPreparedMailData(aFinanceDetail);
		Map<String, List<String>> emails = new HashMap<String, List<String>>();
		Map<String, List<String>> mobileNumbers = new HashMap<String, List<String>>();

		// Customer Email Preparation
		if (NotificationConstants.TEMPLATE_FOR_CN.equals(templateType)) {
			if (CollectionUtils.isNotEmpty(custEmails)) {
				List<String> emailList = new ArrayList<>();
				emails.put(templateType, emailList);

				for (CustomerEMail customerEMail : custEmails) {
					emailList.add(customerEMail.getCustEMail());
				}
			}

			if (CollectionUtils.isNotEmpty(custMobiles)) {
				List<String> mobileNoList = new ArrayList<String>();
				mobileNumbers.put(templateType, mobileNoList);
				for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
					mobileNoList.add(customerPhoneNumber.getPhoneNumber());
				}
			}
		} else if (NotificationConstants.TEMPLATE_FOR_SP.equals(templateType)) {
			List<String> emailList = new ArrayList<>();
			List<String> mobileNoList = new ArrayList<String>();
			long vehicleDealerid = aFinanceDetail.getCustomerDetails().getCustomer().getCustRO1();
			VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(vehicleDealerid);

			if (vehicleDealer != null) {
				emails.put(templateType, emailList);
				mobileNumbers.put(templateType, mobileNoList);

				emailList.add(StringUtils.trimToEmpty(vehicleDealer.getEmail()));
				mobileNoList.add(StringUtils.trimToEmpty(vehicleDealer.getDealerTelephone()));
				fieldsAndValues.putAll(vehicleDealer.getDeclaredFieldValues());
			}
		} else if (NotificationConstants.TEMPLATE_FOR_AE.equals(templateType)
				|| NotificationConstants.TEMPLATE_FOR_TAT.equals(templateType)
				|| NotificationConstants.TEMPLATE_FOR_QP.equals(templateType)
				|| NotificationConstants.TEMPLATE_FOR_GE.equals(templateType)) {
			// Getting UserRoles
			String ruleResString = (String) this.ruleExecutionUtil.executeRule(notification.getRuleReciepent(),
					fieldsAndValues, null, RuleReturnType.STRING);
			if (StringUtils.isEmpty(ruleResString)) {
				List<String> emailList = securityUserOperationsService.getUsrMailsByRoleIds(ruleResString);

				if (CollectionUtils.isNotEmpty(emailList)) {
					emails.put(templateType, emailList);
				}
			}
		}

		MailTemplate template = getMailTemplate(notification, fieldsAndValues, docDetailsList);
		if (template != null && template.isActive() && template.isEmailTemplate()) {
		List<String> tempEmails = new ArrayList<>();
		for (Entry<String, List<String>> email : emails.entrySet()) {
			tempEmails.addAll(email.getValue());
		}

		template.setLovDescMailId(tempEmails.toArray(new String[tempEmails.size()]));
		}
		return template;
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