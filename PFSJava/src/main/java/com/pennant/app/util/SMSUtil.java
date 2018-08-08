package com.pennant.app.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.RuleReturnType;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SMSUtil {
	private static final Logger				logger	= Logger.getLogger(SMSUtil.class);
	private Configuration					freemarkerSMSConfiguration;
	private NotificationsService			notificationsService;
	private RuleExecutionUtil				ruleExecutionUtil;
	private MailTemplateService				smsTemplateService;
	private SecurityUserOperationsService	securityUserOperationsService;

	public Configuration getFreemarkerSMSConfiguration() {
		return freemarkerSMSConfiguration;
	}

	public void setFreemarkerSMSConfiguration(Configuration freemarkerSMSConfiguration) {
		this.freemarkerSMSConfiguration = freemarkerSMSConfiguration;
	}

	public NotificationsService getNotificationsService() {
		return notificationsService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public MailTemplateService getSmsTemplateService() {
		return smsTemplateService;
	}

	public void setSmsTemplateService(MailTemplateService smsTemplateService) {
		this.smsTemplateService = smsTemplateService;
	}

	public SecurityUserOperationsService getSecurityUserOperationsService() {
		return securityUserOperationsService;
	}

	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public MailTemplate getSMSTemplate(Notifications notification, HashMap<String, Object> fieldsAndValues,
			Map<String, List<String>> mobileNoMap) throws IOException, TemplateException {
		logger.debug("Entering");
		List<MailTemplate> smsContentList = new ArrayList<MailTemplate>();


		MailTemplate smsTemplate = null;
		// Getting Mail Template
		Integer templateId = (Integer) this.ruleExecutionUtil.executeRule(notification.getRuleTemplate(),
				fieldsAndValues, null, RuleReturnType.INTEGER);
		if (templateId > 0) {
			smsTemplate = getSmsTemplateService().getApprovedMailTemplateById(templateId);
			if (smsTemplate != null && smsTemplate.isActive() && smsTemplate.isSmsTemplate()) {
				List<String> mobileList = null;

				String templateType = notification.getTemplateType();

				// If No mail Id exists No need to continue
				if (mobileNoMap == null) {
					return null;
				}
				// Other Type of Template, we need to Fetch from Map
				// passing as parameter using Template Type in
				// Notification
				if (!mobileNoMap.containsKey(templateType)) {
					return null;
				}
				mobileList = mobileNoMap.get(templateType);

				if (mobileList == null || mobileList.isEmpty()) {
					return null;
				}
				smsTemplate.setLovDescMobileNumbers(mobileList);

				if (mobileList != null) {
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("vo", fieldsAndValues);
					String smsContent = getSMSTemplateData("smsTemplate", smsTemplate, smsTemplate.getSmsContent(),
							model);
					smsTemplate.setLovDescSMSContent(smsContent);
				}
				smsContentList.add(smsTemplate);
			}
		}

		logger.debug("Leaving");
		return smsTemplate;
	}

	private String getSMSTemplateData(String templateName, MailTemplate smsTemplate, String templateSource,
			Map<String, Object> model) throws IOException, TemplateException {
		StringTemplateLoader loader = new StringTemplateLoader();
		loader.putTemplate(templateName, templateSource);
		getFreemarkerSMSConfiguration().setTemplateLoader(loader);
		Template template = getFreemarkerSMSConfiguration().getTemplate(templateName);
		String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		return result;
	}

}
