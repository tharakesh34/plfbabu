package com.pennant.backend.service.finance.covenant.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;

public class CovenantAlerts extends BasicDao<Covenant> {
	private static Logger logger = LogManager.getLogger(CovenantAlerts.class);

	private SecurityUserDAO securityUserDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDetailsService customerDetailsService;
	private NotificationService notificationService;
	private CovenantsDAO covenantsDAO;

	List<Covenant> covenants = new ArrayList<>();
	private Date appDate;

	public void sendAlerts() {

		loadAppDate();

		covenants = covenantsDAO.getCovenantsAlertList();
		for (Covenant covenant : covenants) {
			FinanceDetail financeDetail = new FinanceDetail();
			CustomerDetails customerDetails = new CustomerDetails();
			FinanceMain financeMain = financeMainDAO.getFinanceMainByRef(covenant.getKeyReference(), "_aview", false);
			financeMain.setUserDetails(new LoggedInUser());
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.setCovenant(covenant);
			financeDetail.setCustomerDetails(customerDetails);
			customerDetails.setCustID(financeMain.getCustID());
			customerDetailsService.setCustomerBasicDetails(customerDetails);
			// For Customers marked as DND true are not allow to Trigger a Mail.
			if (customerDetails.getCustomer().isDnd()) {
				continue;
			} else {
				sendAlert(covenant, financeDetail);
			}
		}
	}

	private void sendAlert(Covenant covenant, FinanceDetail financeDetail) {
		Date frequencyaDate = covenant.getNextFrequencyDate();
		int alertDays = covenant.getAlertDays();

		frequencyaDate = DateUtil.addDays(frequencyaDate, alertDays * -1);

		frequencyaDate = DateUtil.getDatePart(frequencyaDate);

		if (appDate.compareTo(frequencyaDate) < 0) {
			return;
		}

		Date lastAlertSentOn = covenant.getAlertsentOn();

		if (lastAlertSentOn != null && DateUtil.getDaysBetween(appDate, lastAlertSentOn) <= alertDays) {
			return;
		}

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		if (covenant.getCustomerTemplateCode() != null) {
			Notification notification = new Notification();
			notification.setKeyReference(covenant.getKeyReference());
			notification.setModule("LOAN");
			notification.setSubModule(FinServiceEvent.COVENANTS);
			notification.setTemplateCode(covenant.getCustomerTemplateCode());

			List<String> emails = new ArrayList<>();

			for (CustomerEMail customerEmail : customerDetails.getCustomerEMailList()) {
				if (customerEmail.getCustEMailPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					if (StringUtils.isNotEmpty(customerEmail.getCustEMail())) {
						emails.add(customerEmail.getCustEMail());
					}
				}
			}

			notification.setEmails(emails);
			long notificationId = sendNotification(financeDetail, notification);
			if (notificationId > 0) {
				updateAlertStatus(notificationId, covenant);
			}

		} else if (covenant.getUserTemplateCode() != null && covenant.getAlertToRoles() != null) {
			Notification notification = new Notification();
			notification.setKeyReference(covenant.getKeyReference());
			notification.setModule("LOAN");
			notification.setSubModule(FinServiceEvent.COVENANTS);
			notification.setTemplateCode(covenant.getUserTemplateCode());

			List<SecurityUser> secUsers = securityUserDAO.getSecUsersByRoles(covenant.getAlertToRoles().split(","));

			List<String> emails = new ArrayList<>();
			for (SecurityUser securityUser : secUsers) {
				if (StringUtils.isNotEmpty(securityUser.getUsrEmail())) {
					emails.add(securityUser.getUsrEmail());
				}
			}

			notification.setEmails(emails);

			long notificationId = sendNotification(financeDetail, notification);
			if (notificationId > 0) {
				updateAlertStatus(notificationId, covenant);
			}
		}
	}

	private void updateAlertStatus(long notificationId, Covenant covenant) {
		StringBuilder sql = new StringBuilder();

		sql.append("insert into covenant_alerts(CovenantId, FrequencyDate, NotificationId, AlertSentOn)");
		sql.append(" values(:CovenantId, :FrequencyDate, :NotificationId, :AlertSentOn)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("CovenantId", covenant.getId());
		source.addValue("FrequencyDate", covenant.getNextFrequencyDate());
		source.addValue("NotificationId", notificationId);
		source.addValue("AlertSentOn", appDate);

		jdbcTemplate.update(sql.toString(), source);
	}

	private long sendNotification(FinanceDetail financeDetail, Notification notification) {
		if (CollectionUtils.isEmpty(notification.getEmails())) {
			return 0;
		}

		notificationService.sendNotification(notification, financeDetail);
		return notification.getId();
	}

	private void loadAppDate() {
		appDate = DateUtil.getDatePart(SysParamUtil.getAppDate());
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
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

	public CovenantsDAO getCovenantsDAO() {
		return covenantsDAO;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

}
