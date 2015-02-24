package com.pennant.app.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.documentdetails.DocumentDetails;
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
import com.pennant.backend.service.administration.SecurityUserRolesService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MailUtil implements Serializable {

	private static final long serialVersionUID = -2543427090845637670L;
	private final static Logger logger = Logger.getLogger(MailUtil.class);
	
	private Configuration freemarkerMailConfiguration;
	private MailTemplateService mailTemplateService;
	private NotificationsService notificationsService;
	private CustomerEMailService customerEMailService;
	private AgreementDefinitionService agreementDefinitionService;
	private SecurityUserRolesService securityUserRolesService;
	private String smtpHost;
	private String smtpPort;
	private boolean auth;
	private boolean debug;
	private String userName;
	private String password;
	
	
	private NotesService notesService;
	
	public MailUtil(){
		super();
	}
	
	public MailUtil(String smtpHost,String smtpPort, boolean auth, boolean debug, String userName, String password){
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.userName = userName;
		this.password = password;
		this.auth = auth;
		this.debug = debug;
	}
	
	public void sendMail(String moduleCode, Object object, Object controller) throws Exception {
		logger.debug("Entering");
		List<Notifications> notificationsList = getNotificationsService().getApprovedNotificationsByModule(moduleCode);
		RuleExecutionUtil ruleExecutionUtil = new RuleExecutionUtil();
		Object ruleResString;
		MailTemplate mailTemplate = null;
		String[] mailId = null;
		List<String> emailList = null;
		FinanceDetail financeDetail= null;
		RepayData aRepayData= null;
		List<DocumentDetails> documentslist = null;
		MailTemplateData templateData = new MailTemplateData();
		try{
			if(object instanceof FinanceDetail){
				financeDetail = (FinanceDetail) object;
				templateData = getPreparedMailData(templateData, financeDetail.getFinScheduleData().getFinanceMain());
				documentslist = financeDetail.getDocumentDetailsList();
			}
			if(object instanceof Facility){
				templateData = getPrepareFacilityMailData(templateData, (Facility)object);
				documentslist = ((Facility)object).getDocumentDetailsList();
			}
			if(object instanceof FinCreditReviewDetails){
				templateData = getPrepareCreditReviewMailData(templateData, (FinCreditReviewDetails)object);
			}
			if(object instanceof InvestmentFinHeader){
				templateData = getPrepareTreasuryInvestmentMailData(templateData, (InvestmentFinHeader)object);
			}
			if(object instanceof RepayData){
				aRepayData = (RepayData) object;
				templateData = getPreparedMailData(templateData, aRepayData.getFinanceMain());
				documentslist = aRepayData.getDocumentDetailList();
			}
			if(object instanceof FinanceWriteoffHeader){
				templateData = getPreparedMailData(templateData, ((FinanceWriteoffHeader)object).getFinanceMain());
			}
			if(object instanceof Provision){
				templateData = getPrepareProvisionMailData(templateData, (Provision)object);
			}
			if(object instanceof FinanceSuspHead){
				templateData = getPrepareManualSuspenseMailData(templateData, (FinanceSuspHead)object);
			}
			for (Notifications notifications : notificationsList) {
				// Getting Mail Template 
				ruleResString = ruleExecutionUtil.executeRule(notifications.getRuleTemplate(), templateData, null, null);
				if(ruleResString == null){
					continue;
				}
				mailTemplate = getMailTemplateService().getApprovedMailTemplateById(Long.parseLong(ruleResString.toString()), PennantConstants.TEMPLATE_FOR_AE);
				if(mailTemplate != null && mailTemplate.isActive()) {


					//Getting UserRoles
					ruleResString = ruleExecutionUtil.executeRule(notifications.getRuleReciepent(), templateData, null, null);
					if(ruleResString == null){
						continue;
					}
					//Prepare Mail ID Details
					if(!ruleResString.toString().equals("")){
						emailList = getSecurityUserRolesService().getUsrMailsByRoleIds(ruleResString.toString());
					}

					//Check Mail List
					List<String> usrMailList = new ArrayList<String>();
					if(emailList != null && !emailList.isEmpty()){
						for (String usrMail : emailList) {
							if(!StringUtils.trimToEmpty(usrMail).equals("")){
								usrMailList.add(usrMail);
							}
						}
						mailId = new String[usrMailList.size()];
						mailId = (String[]) usrMailList.toArray(mailId);
						usrMailList = null;
					}
					if(mailId != null && !StringUtils.join(mailId,",").equals("")){
						//Template Fields Bean Preparation
						mailTemplate.setLovDescMailId(mailId);
						parseMail(mailTemplate, templateData);
					}	

					//Getting the Attached Documents 
					ruleResString = ruleExecutionUtil.executeRule(notifications.getRuleAttachment(), templateData, null, null);
					if(ruleResString != null){
						//agreementDef = getAgreementDefinitionService().getApprovedAgreementDefinitionById(Long.parseLong(ruleResString.toString()));
						if(documentslist != null){
							for (DocumentDetails documentDetails : documentslist) {
								if(ruleResString.equals(documentDetails.getDocCategory())){
									mailTemplate.setLovDescAttachmentName(documentDetails.getDocName());
									mailTemplate.setLovDescEmailAttachment(documentDetails.getDocImage());
								}
							}
						}
					}

					if(mailTemplate.isEmailTemplate() && !StringUtils.join(mailTemplate.getLovDescMailId(),",").equals("")) {
						sendingMail(mailTemplate);
					}
				}

			}
		}catch(Exception e ){
			logger.error(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Prepare and send mail
	 * */
	public void sendMail(long templateId, String templateFor, Object vo) {
		logger.debug("Entering");
		MailTemplate mailTemplate = null;
		try {			
			mailTemplate = getMailTemplateService().getApprovedMailTemplateById(templateId, templateFor);
			if(mailTemplate != null && mailTemplate.isActive()) {
				
				//Template Fields Bean Preparation
				MailTemplateData templateData = new MailTemplateData();
				if(vo instanceof FinanceMain){
					templateData = getPreparedMailData(templateData, (FinanceMain)vo);
				}
				
				if(vo instanceof Facility){
					templateData = getPrepareFacilityMailData(templateData, (Facility)vo);
				}
				
				if(vo instanceof FinCreditReviewDetails){
					templateData = getPrepareCreditReviewMailData(templateData, (FinCreditReviewDetails)vo);
				}
				
				if(vo instanceof InvestmentFinHeader){
					templateData = getPrepareTreasuryInvestmentMailData(templateData, (InvestmentFinHeader)vo);
				}
				
				if(vo instanceof TakafulProvider){
					templateData = getPrepareTakafulProviderMailData(templateData, (TakafulProvider)vo);
					mailTemplate.setLovDescMailId(new String[]{"siva.m@pennanttech.com"});
				}
				
				parseMail(mailTemplate, templateData);
				if(mailTemplate.isEmailTemplate() && !StringUtils.join(mailTemplate.getLovDescMailId(),",").equals("")) {
					sendingMail(mailTemplate);
				}
				if(mailTemplate.isSmsTemplate()) {
					sendSMS(mailTemplate);
				}
			}
		}catch (Exception e) {
			logger.debug("Exception :: "+e);			
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Parsing Mail Details and Send Notification To Users/Customer
	 * @param mailTemplate
	 * @param templateData
	 * @throws Exception
	 */
	private void parseMail(MailTemplate mailTemplate, Object templateData) throws Exception {
		logger.debug("Entering");

		String	subject="";
		String result = "";

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vo", templateData);

		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate("mailTemplate", new String(mailTemplate.getEmailContent(), PennantConstants.DEFAULT_CHARSET));
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

		if(mailTemplate.isSmsTemplate()) {

			loader = new StringTemplateLoader();
			loader.putTemplate("smsTemplate", mailTemplate.getSmsContent());
			getFreemarkerMailConfiguration().setTemplateLoader(loader);
			template = getFreemarkerMailConfiguration().getTemplate("smsTemplate");		

			try {
				result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			} catch (IOException e) {
				logger.debug("Exception :: "+e);
				throw new Exception("Unable to read or process freemarker configuration or template",e);
			} catch (TemplateException e) {
				logger.debug("Exception :: "+e);
				throw new Exception("Problem initializing freemarker or rendering template ", e);
			}
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Method to send Mail
	 * @param mailTemplate
	 * @throws Exception
	 */

	private void sendingMail(MailTemplate mailTemplate)  throws Exception {
		logger.debug("Entering");
		try {
			MimeMessage message = new MimeMessage(getSession());
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(new InternetAddress(this.userName));
			helper.setSubject(mailTemplate.getEmailSubject());
			helper.setText(mailTemplate.getLovDescFormattedContent(), true);
			helper.setTo(mailTemplate.getLovDescMailId());
			if(mailTemplate.getLovDescEmailAttachment() != null){
				helper.addAttachment(mailTemplate.getLovDescAttachmentName(), new ByteArrayResource(mailTemplate.getLovDescEmailAttachment()));
			}
			
			logger.debug(mailTemplate.getLovDescMailId()+" Count of Mails Sending to :: "+ mailTemplate.getLovDescMailId().length);
			Transport.send(message);
			
		} catch (Exception e) {
			logger.debug("Exception :: "+e);
		}
		logger.debug("Leaving");
	}
	
	private Session getSession() {
		logger.debug("Entering");
		Authenticator authenticator = null;
		if(this.auth){
			authenticator = new Authenticator();
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", this.smtpHost);
		props.put("mail.smtp.port", this.smtpPort);
		props.put("mail.smtp.auth", this.auth);
		props.put("mail.smtp.debug", this.debug);
		logger.debug("Leaving");
 		return Session.getInstance(props, authenticator);
	}

	private class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;

		public Authenticator() {
			authentication = new PasswordAuthentication(userName, password);
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
	
	public void sendSMS(MailTemplate mailTemplate) throws Exception {/*
		Properties props = new Properties();

		props.put("mail.host", SystemParameterDetails.getSystemParameterValue("DFT_SMTP_SMS_IP").toString());
		props.put("mail.smtp.host", SystemParameterDetails.getSystemParameterValue("DFT_SMTP_SMS_IP").toString());
		props.put("mail.smtp.port", SystemParameterDetails.getSystemParameterValue("DFT_SMTP_SMS_PORT").toString());
		props.put("mail.transport.protocol", SystemParameterDetails.getSystemParameterValue("DFT_SMS_PROTOCOL").toString());
		Message message = null;
		try {
			Session session = Session.getDefaultInstance(props);
			session.setDebug(true);
			InternetAddress[] addressTo = new InternetAddress[1];
			addressTo[0] = new InternetAddress(mailTemplate.getLovDescMobileNo());
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SystemParameterDetails.getSystemParameterValue("SMS_FROM_ADDRESS").toString()));
			message.setRecipients(Message.RecipientType.TO, addressTo);
			message.setSubject(mailTemplate.getLovDescFormattedSMSContent());		
			message.setContent(mailTemplate.getLovDescFormattedSMSContent(), "text/plain");
			Transport.send(message);
		} catch (Exception e) {
			logger.debug("Exception :: "+e);
		} finally {
			message = null;
		}
	*/}
	
	
	/**
	 * Method for Data Preparion
	 * @param data
	 * @param main
	 * @return
	 */
	public MailTemplateData getPreparedMailData(MailTemplateData data, FinanceMain main) {
		
		//Finance Data Preparation For Notifications
		data.setFinReference(main.getFinReference());
		data.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(),main.getLovDescFinFormatter()));
		data.setDownPayment(PennantApplicationUtil.amountFormate(main.getDownPayment(),main.getLovDescFinFormatter()));
		data.setFeeAmount(PennantApplicationUtil.amountFormate(main.getFeeChargeAmt(),main.getLovDescFinFormatter()));
		data.setFinCcy(main.getFinCcy());
		data.setFinStartDate(DateUtility.formatDate(main.getFinStartDate(), PennantConstants.dateFormate));
		data.setMaturityDate(DateUtility.formatDate(main.getMaturityDate(), PennantConstants.dateFormate));
		data.setNumberOfTerms(String.valueOf(main.getNumberOfTerms()));
		data.setGraceTerms(String.valueOf(main.getGraceTerms()));
		if(main.getEffectiveRateOfReturn()!= null){
			data.setEffectiveRate(PennantApplicationUtil.formatRate(main.getEffectiveRateOfReturn().doubleValue(),2));
		}else{
			data.setEffectiveRate("");
		}
		data.setCustShrtName(main.getLovDescCustShrtName());
		data.setCustId(main.getCustID());

		//Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(main.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(main.getLastMntBy())); 
		data.setNextUsrName(""); // TODO Need to set the data
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(main.getLastMntBy())); 
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(main.getWorkflowId())); 
		data.setNextUsrRoleCode(main.getNextRoleCode());
		List<SecurityRole> securityNextRoles = PennantApplicationUtil.getRoleCodeDesc(main.getNextRoleCode());
		String nextRoleCode= "";
		for (SecurityRole securityRole : securityNextRoles) {
			if(!nextRoleCode.equals("")){
				nextRoleCode = nextRoleCode + " / " +securityRole.getRoleDesc();
			}else{
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
		List<Notes> list = getNotesService().getNotesListByRole(note, false, new String[]{main.getRoleCode()});
		StringBuilder recommendations=new StringBuilder("");
		for (Notes notes : list) {
			recommendations.append(notes.getRemarks());
        }
		data.setRecommendations(recommendations.toString());
	    return data;
    }
	/**
	 * Method for Data Preparion
	 * @param data
	 * @param facility
	 * @return
	 */
	public MailTemplateData getPrepareFacilityMailData(MailTemplateData data, Facility facility) {
		
		//Facility Data Preparation For Notifications
		data.setCustShrtName(facility.getCustShrtName());
		data.setCustId(facility.getCustID());
		data.setTotAmountBD(PennantApplicationUtil.formatAmount(facility.getAmountBD(),3,false));
		data.setTotAmountUSD(PennantApplicationUtil.formatAmount(facility.getAmountUSD(),2,false));
		//Role Code For Alert Notification
		data.setRoleCode(facility.getNextRoleCode());
		data.setCafReference(facility.getCAFReference());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(facility.getLastMntBy())); 
		data.setNextUsrName(""); // TODO Need to set the data
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(facility.getLastMntBy())); 
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(facility.getWorkflowId())); 
		data.setNextUsrRoleCode(facility.getNextRoleCode());
		List<SecurityRole> securityNextRoles = PennantApplicationUtil.getRoleCodeDesc(facility.getNextRoleCode());
		String nextRoleCode= "";
		for (SecurityRole securityRole : securityNextRoles) {
			if(!nextRoleCode.equals("")){
				nextRoleCode = nextRoleCode + "/" +securityRole.getRoleDesc();
			}else{
				nextRoleCode = securityRole.getRoleDesc();
			}
		}
		data.setNextUsrRole(nextRoleCode);
		data.setPrevUsrRole(facility.getLastMntBy());
		data.setUsrRole(facility.getRoleCode());
		if (StringUtils.trimToEmpty(facility.getFacilityType()).equals(PennantConstants.FACILITY_COMMERCIAL)) {
			data.setFacilityType("Commercial");
		} else if (StringUtils.trimToEmpty(facility.getFacilityType()).equals(PennantConstants.FACILITY_CORPORATE)) {
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
	 * @param data
	 * @param FinCreditReviewDetails
	 * @return
	 */
	public MailTemplateData getPrepareCreditReviewMailData(MailTemplateData data, FinCreditReviewDetails finCreditReviewDetails) {
		
		//Facility Data Preparation For Notifications
		data.setCustShrtName(finCreditReviewDetails.getLovDescCustShrtName());
		data.setCustCIF(finCreditReviewDetails.getLovDescCustCIF());
		data.setAuditors(finCreditReviewDetails.getAuditors());
		data.setLocation(finCreditReviewDetails.getLocation());
		data.setAuditType(finCreditReviewDetails.getAuditType());
		data.setAuditedDate(DateUtility.formatDate(finCreditReviewDetails.getAuditedDate(), PennantConstants.dateFormate));
		data.setAuditYear(finCreditReviewDetails.getAuditYear());
		data.setAuditPeriod(finCreditReviewDetails.getAuditPeriod());
		//Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(finCreditReviewDetails.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(finCreditReviewDetails.getLastMntBy())); 
		data.setNextUsrName(""); // TODO Need to set the data
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(finCreditReviewDetails.getLastMntBy())); 
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(finCreditReviewDetails.getWorkflowId())); 
		data.setNextUsrRoleCode(finCreditReviewDetails.getNextRoleCode());
		
		List<SecurityRole> securityUsrRoles = PennantApplicationUtil.getRoleCodeDesc(finCreditReviewDetails.getNextRoleCode());
		data.setNextUsrRole(securityUsrRoles.get(0).getRoleDesc());
		data.setPrevUsrRole(finCreditReviewDetails.getLastMntBy());
		data.setUsrRole(finCreditReviewDetails.getRoleCode());
		
		return data;
	}
	
	/**
	 * Method for Data Preparion
	 * @param data
	 * @param InvestmentFinHeader
	 * @return
	 */
	public MailTemplateData getPrepareTreasuryInvestmentMailData(MailTemplateData data, InvestmentFinHeader investmentFinHeader) {
		
		//Facility Data Preparation For Notifications
		data.setInvestmentRef(investmentFinHeader.getInvestmentRef());
		data.setTotPrincipalAmt(PennantApplicationUtil.amountFormate(investmentFinHeader.getTotPrincipalAmt(),investmentFinHeader.getLovDescFinFormatter()));
		data.setFinCcy(investmentFinHeader.getFinCcy());
		data.setStartDate(DateUtility.formatDate(investmentFinHeader.getStartDate(), PennantConstants.dateFormate));
		data.setMaturityDate(DateUtility.formatDate(investmentFinHeader.getMaturityDate(), PennantConstants.dateFormate));
		data.setPrincipalInvested(PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalInvested(),investmentFinHeader.getLovDescFinFormatter()));
		data.setPrincipalMaturity(PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalMaturity(),investmentFinHeader.getLovDescFinFormatter()));
		data.setPrincipalDueToInvest(PennantApplicationUtil.amountFormate(investmentFinHeader.getPrincipalDueToInvest(),investmentFinHeader.getLovDescFinFormatter()));
		data.setAvgPftRate(investmentFinHeader.getAvgPftRate().toString());
		//Role Code For Alert Notification
		List<SecurityRole> securityRoles = PennantApplicationUtil.getRoleCodeDesc(investmentFinHeader.getRoleCode());
		data.setRoleCode(securityRoles.get(0).getRoleDesc());
		// user Details
		data.setUsrName(PennantApplicationUtil.getUserDesc(investmentFinHeader.getLastMntBy())); 
		data.setNextUsrName(""); // TODO Need to set the data
		data.setPrevUsrName(PennantApplicationUtil.getUserDesc(investmentFinHeader.getLastMntBy())); 
		data.setWorkflowType(PennantApplicationUtil.getWorkFlowType(investmentFinHeader.getWorkflowId())); 
		data.setNextUsrRoleCode(investmentFinHeader.getNextRoleCode());
		
		List<SecurityRole> securityUsrRoles = PennantApplicationUtil.getRoleCodeDesc(investmentFinHeader.getNextRoleCode());
		data.setNextUsrRole(securityUsrRoles.get(0).getRoleDesc());
		data.setPrevUsrRole(investmentFinHeader.getLastMntBy());
		data.setUsrRole(investmentFinHeader.getRoleCode());
		
		return data;
	}
	
	/**
	 * Method for Data Preparion
	 * @param data
	 * @param Provision
	 * @return
	 */
	public MailTemplateData getPrepareProvisionMailData(MailTemplateData data, Provision provision) {
		
		//Provision Data Preparation For Notifications
		data.setFinReference(provision.getFinReference());
		data.setCustShrtName(provision.getLovDescCustShrtName());
		data.setCustCIF(provision.getLovDescCustCIF());
		data.setFinBranch(provision.getFinBranch());
		data.setPrincipalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue(),provision.getLovDescFinFormatter()));
		data.setProfitDue(PennantApplicationUtil.amountFormate(provision.getProfitDue(),provision.getLovDescFinFormatter()));
		data.setTotalDue(PennantApplicationUtil.amountFormate(provision.getPrincipalDue().add(provision.getProfitDue()),provision.getLovDescFinFormatter()));
		data.setDueFromDate(DateUtility.formatDate(provision.getDueFromDate(), PennantConstants.dateFormate));
		data.setNonFormulaProv(PennantApplicationUtil.amountFormate(provision.getNonFormulaProv(),provision.getLovDescFinFormatter()));
		data.setProvisionedAmt(PennantApplicationUtil.amountFormate(provision.getProvisionedAmt(),provision.getLovDescFinFormatter()));
		data.setProvisionedAmtCal(PennantApplicationUtil.amountFormate(provision.getProvisionAmtCal(),provision.getLovDescFinFormatter()));

		//Role Code For Alert Notification
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
	 * @param data
	 * @param InvestmentFinHeader
	 * @return
	 */
	public MailTemplateData getPrepareManualSuspenseMailData(MailTemplateData data, FinanceSuspHead financeSuspHead) {
		
		//Manual Suspense Data Preparation For Notifications
		data.setFinReference(financeSuspHead.getFinReference());
		data.setCustShrtName(financeSuspHead.getLovDescCustShrtName());
		data.setCustCIF(financeSuspHead.getLovDescCustCIFName());
		data.setFinBranch(financeSuspHead.getFinBranch());
		data.setManualSusp(financeSuspHead.isManualSusp() ? "Yes" : "No");
		data.setFinSuspDate(DateUtility.formatDate(financeSuspHead.getFinSuspDate(), PennantConstants.dateFormate));
		data.setFinSuspAmt(PennantApplicationUtil.amountFormate(financeSuspHead.getFinSuspAmt(),financeSuspHead.getLovDescFinFormatter()));
		data.setFinCurSuspAmt(PennantApplicationUtil.amountFormate(financeSuspHead.getFinCurSuspAmt(),financeSuspHead.getLovDescFinFormatter()));
		
		//Role Code For Alert Notification
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
	
	/**
	 *  Method for Data Preparation
	 * @param data
	 * @param takafulProvider
	 * @return
	 */
	public MailTemplateData getPrepareTakafulProviderMailData(MailTemplateData data, TakafulProvider takafulProvider) {
		
		//Takaful Data Preparation For Notifications
		data.setTakafulCode(takafulProvider.getTakafulCode());
		data.setTakafulName(takafulProvider.getTakafulName());
		data.setTakafulType(takafulProvider.getTakafulType());
		data.setEmailId(takafulProvider.getEmailId());
		data.setFinReference("");
		
		return data;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public boolean isAuth() {
		return auth;
	}
	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
	    this.customerEMailService = customerEMailService;
    }
	public CustomerEMailService getCustomerEMailService() {
	    return customerEMailService;
    }

	public void setSecurityUserRolesService(SecurityUserRolesService securityUserRolesService) {
	    this.securityUserRolesService = securityUserRolesService;
    }
	public SecurityUserRolesService getSecurityUserRolesService() {
	    return securityUserRolesService;
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
	
}