package com.pennant.backend.service.finance.lmsservicelog.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;

public class LMSServiceLogAlerts {
	private static Logger logger = LogManager.getLogger(LMSServiceLogAlerts.class);

	private FinanceMainDAO financeMainDAO;
	private CustomerDetailsService customerDetailsService;
	private NotificationService notificationService;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;

	public void sendAlerts() {
		logger.debug(Literal.ENTERING);

		List<LMSServiceLog> lmsServiceLogs = finServiceInstrutionDAO.getLMSServiceLogList(PennantConstants.NO);
		for (LMSServiceLog lmsServiceLog : lmsServiceLogs) {
			FinanceDetail financeDetail = new FinanceDetail();
			CustomerDetails customerDetails = new CustomerDetails();
			FinanceMain financeMain = financeMainDAO.getFinanceMainById(lmsServiceLog.getFinReference(), "_aview",
					false);
			financeMain.setUserDetails(new LoggedInUser());
			financeDetail.setFinReference(lmsServiceLog.getFinReference());
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			customerDetails.setCustID(financeMain.getCustID());
			customerDetailsService.setCustomerBasicDetails(customerDetails);
			financeDetail.setCustomerDetails(customerDetails);
			//For Customers marked as DND true are not allow to Trigger a Mail. 
			if (customerDetails.getCustomer().isDnd()) {
				finServiceInstrutionDAO.updateNotificationFlag("D", lmsServiceLog.getId());
				continue;
			} else {
				sendAlert(lmsServiceLog, financeDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void sendAlert(LMSServiceLog lmsServiceLog, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		financeDetail.setLmsServiceLog(lmsServiceLog);

		List<String> emails = new ArrayList<>();

		for (CustomerEMail customerEmail : customerDetails.getCustomerEMailList()) {
			if (customerEmail.getCustEMailPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
				if (StringUtils.isNotEmpty(customerEmail.getCustEMail())) {
					emails.add(customerEmail.getCustEMail());
				}
			}
		}

		Notification lmsServiceNotifyCust = new Notification();
		lmsServiceNotifyCust.setTemplateCode(NotificationConstants.ADD_RATE_CHANGE_NOTIFICATION);
		lmsServiceNotifyCust.setKeyReference(financeDetail.getFinReference());
		lmsServiceNotifyCust.setModule("LOAN");
		lmsServiceNotifyCust.setSubModule(FinServiceEvent.RATECHG);
		financeDetail.setModuleDefiner(FinServiceEvent.RATECHG);
		lmsServiceNotifyCust.setEmails(emails);

		long lmsServiceNotifyId = sendNotification(financeDetail, lmsServiceNotifyCust, customerDetails);

		if (lmsServiceNotifyId > 0) {
			finServiceInstrutionDAO.updateNotificationFlag(PennantConstants.YES, lmsServiceLog.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	private long sendNotification(FinanceDetail financeDetail, Notification notification,
			CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(notification.getEmails())) {
			logger.debug("Customer Emails are not available for the customer: " + customerDetails.getCustID());
			return 0;
		}
		try {
			notificationService.sendNotification(notification, financeDetail);
			return notification.getId();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return 0;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public FinServiceInstrutionDAO getFinServiceInstrutionDAO() {
		return finServiceInstrutionDAO;
	}

	public void setFinServiceInstrutionDAO(FinServiceInstrutionDAO finServiceInstrutionDAO) {
		this.finServiceInstrutionDAO = finServiceInstrutionDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}
}
