package com.pennant.backend.service.finance.covenant.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.putcall.FinOptionDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.notifications.service.NotificationService;

public class PutCallAlerts extends BasicDao<Covenant> {
	private static Logger logger = Logger.getLogger(PutCallAlerts.class);

	private SecurityUserDAO securityUserDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDetailsService customerDetailsService;
	private NotificationService notificationService;
	private FinOptionDAO finOptionDAO;

	private Date appDate;

	public void sendAlerts() {

		loadAppDate();

		List<FinOption> finOptions = finOptionDAO.getPutCallAlertList();

		for (FinOption finOption : finOptions) {
			FinanceDetail financeDetail = new FinanceDetail();
			CustomerDetails customerDetails = new CustomerDetails();
			FinanceMain financeMain = financeMainDAO.getFinanceMainById(finOption.getFinReference(), "_aview", false);
			financeMain.setUserDetails(new LoggedInUser());
			customerDetails.setCustID(financeMain.getCustID());
			customerDetailsService.setCustomerBasicDetails(customerDetails);
			financeDetail.setCustomerDetails(customerDetails);
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			//For Customers marked as DND true are not allow to Trigger a Mail. 
			if (customerDetails.getCustomer().isDnd()) {
				continue;
			} else {
				sendAlert(finOption, financeDetail);
			}
		}
	}

	private void sendAlert(FinOption finOption, FinanceDetail financeDetail) {
		Date currentOptionDate = finOption.getCurrentOptionDate();

		int alertDays = finOption.getAlertDays();
		int noticePeriodDays = finOption.getNoticePeriodDays();
		int totalDays = noticePeriodDays + alertDays;

		currentOptionDate = DateUtil.getDatePart(currentOptionDate);
		Date userFrequencyDate = DateUtil.addDays(currentOptionDate, -totalDays);

		BigDecimal totalPriBal = finOption.getTotalPriBal();
		BigDecimal penaltyPaid = finOption.getPenaltyPaid();

		if (totalPriBal == null) {
			totalPriBal = BigDecimal.ZERO;
		}

		if (penaltyPaid == null) {
			penaltyPaid = BigDecimal.ZERO;
		}

		finOption.setTotalAmt(totalPriBal.add(penaltyPaid));

		Date custUserFrequencyDate = DateUtil.addDays(currentOptionDate, -noticePeriodDays);

		if (appDate.compareTo(userFrequencyDate) < 0 || appDate.compareTo(custUserFrequencyDate) < 0) {
			return;
		}

		Date lastAlertSentOn = finOption.getAlertsentOn();

		if (lastAlertSentOn != null && DateUtility.getDaysBetween(appDate, lastAlertSentOn) <= alertDays) {
			return;
		}

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		financeDetail.setFinOption(finOption);

		if (finOption.getCustomerTemplateCode() != null) {

			List<String> emails = new ArrayList<>();
			for (CustomerEMail customerEmail : customerDetails.getCustomerEMailList()) {
				if (customerEmail.getCustEMailPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					if (StringUtils.isNotEmpty(customerEmail.getCustEMail())) {
						emails.add(customerEmail.getCustEMail());
					}
				}
			}

			// Notification for PutCall option to Customer - Start
			Notification putCallNotifyCust = new Notification();
			putCallNotifyCust.setKeyReference(finOption.getFinReference());
			putCallNotifyCust.setModule("LOAN");
			putCallNotifyCust.setSubModule(FinanceConstants.FINSER_EVENT_PUTCALL);
			putCallNotifyCust.setTemplateCode(finOption.getCustomerTemplateCode());
			putCallNotifyCust.setEmails(emails);
			long putCallNotifyId = sendNotification(financeDetail, putCallNotifyCust);

			if (putCallNotifyId > 0) {
				updateAlertStatus(putCallNotifyId, finOption);
			}
			// Notification for PutCall option to Customer - End

			// Notification for Interest Review to Customer - Start
			Notification intReviewNotifyCust = new Notification();
			intReviewNotifyCust.setKeyReference(finOption.getFinReference());
			intReviewNotifyCust.setModule("LOAN");
			intReviewNotifyCust.setSubModule(FinanceConstants.FINSER_EVENT_PUTCALL);
			intReviewNotifyCust.setTemplateCode("INTEREST_REVIEW_CUST");
			intReviewNotifyCust.setEmails(emails);
			long intReviewNotifyId = sendNotification(financeDetail, intReviewNotifyCust);

			if (intReviewNotifyId > 0) {
				updateAlertStatus(intReviewNotifyId, finOption);
			}
			// Notification for Interest Review to Customer - End

			// Notification for Asset Review to Customer - Start
			Notification assetReviewNotifyCust = new Notification();
			assetReviewNotifyCust.setKeyReference(finOption.getFinReference());
			assetReviewNotifyCust.setModule("LOAN");
			assetReviewNotifyCust.setSubModule(FinanceConstants.FINSER_EVENT_PUTCALL);
			assetReviewNotifyCust.setTemplateCode("ASSET_REVIEW_CUST");
			assetReviewNotifyCust.setEmails(emails);
			long assetReviewNotifyId = sendNotification(financeDetail, assetReviewNotifyCust);

			if (assetReviewNotifyId > 0) {
				updateAlertStatus(assetReviewNotifyId, finOption);
			}
			// Notification for Asset Review to Customer - End

		} else if (finOption.getUserTemplateCode() != null && finOption.getAlertToRoles() != null) {
			List<SecurityUser> secUsers = securityUserDAO.getSecUsersByRoles(finOption.getAlertToRoles().split(","));
			List<String> emails = new ArrayList<>();
			for (SecurityUser securityUser : secUsers) {
				if (StringUtils.isNotEmpty(securityUser.getUsrEmail())) {
					emails.add(securityUser.getUsrEmail());
				}
			}

			// Notification for PutCall option to User - Start
			Notification putCallNotifyUser = new Notification();
			putCallNotifyUser.setKeyReference(finOption.getFinReference());
			putCallNotifyUser.setModule("LOAN");
			putCallNotifyUser.setSubModule(FinanceConstants.FINSER_EVENT_PUTCALL);
			putCallNotifyUser.setTemplateCode(finOption.getUserTemplateCode());
			putCallNotifyUser.setEmails(emails);
			long putCallNotifyId = sendNotification(financeDetail, putCallNotifyUser);

			if (putCallNotifyId > 0) {
				updateAlertStatus(putCallNotifyId, finOption);
			}
			// Notification for PutCall option to User - End

			// Notification for Interest Review to User - Start
			Notification intReviewNotifyUser = new Notification();
			intReviewNotifyUser.setKeyReference(finOption.getFinReference());
			intReviewNotifyUser.setModule("LOAN");
			intReviewNotifyUser.setSubModule(FinanceConstants.FINSER_EVENT_PUTCALL);
			intReviewNotifyUser.setTemplateCode("INTEREST_REVIEW_USER");
			intReviewNotifyUser.setEmails(emails);
			long intReviewNotifyId = sendNotification(financeDetail, intReviewNotifyUser);

			if (intReviewNotifyId > 0) {
				updateAlertStatus(intReviewNotifyId, finOption);
			}
			// Notification for Interest Review to User - End

			// Notification for Asset Review to User - Start
			Notification assetReviewNotifyUser = new Notification();
			assetReviewNotifyUser.setKeyReference(finOption.getFinReference());
			assetReviewNotifyUser.setModule("LOAN");
			assetReviewNotifyUser.setSubModule(FinanceConstants.FINSER_EVENT_PUTCALL);
			assetReviewNotifyUser.setTemplateCode("ASSET_REVIEW_USER");
			assetReviewNotifyUser.setEmails(emails);
			long assetReviewNotifyId = sendNotification(financeDetail, assetReviewNotifyUser);

			if (assetReviewNotifyId > 0) {
				updateAlertStatus(assetReviewNotifyId, finOption);
			}
			// Notification for Asset Review to User - End
		}
	}

	private void updateAlertStatus(long notificationId, FinOption finOption) {
		StringBuilder sql = new StringBuilder();

		sql.append(
				"insert into fin_option_alerts(FinOptiontId, CurrentOptionDate, AlertFor, Notificationid,alertSentOn)");
		sql.append(" values(:FinOptiontId, :CurrentOptionDate, :AlertFor, :NotificationId, :AlertSentOn)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("FinOptiontId", finOption.getId());
		source.addValue("CurrentOptionDate", finOption.getCurrentOptionDate());
		source.addValue("NotificationId", notificationId);

		if (StringUtils.equalsIgnoreCase(finOption.getAlertType(), "User")) {
			source.addValue("AlertFor", "U");
		} else if (StringUtils.equalsIgnoreCase(finOption.getAlertType(), "Customer")) {
			source.addValue("AlertFor", "C");
		} else if (StringUtils.equalsIgnoreCase(finOption.getAlertType(), "Both")) {
			source.addValue("AlertFor", "B");
		}

		source.addValue("AlertSentOn", appDate);

		jdbcTemplate.update(sql.toString(), source);
	}

	private long sendNotification(FinanceDetail financeDetail, Notification notification) {
		if (CollectionUtils.isEmpty(notification.getEmails())) {
			return 0;
		}

		try {
			notificationService.sendNotification(notification, financeDetail);
			return notification.getId();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return 0;
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

	public void setFinOptionDAO(FinOptionDAO finOptionDAO) {
		this.finOptionDAO = finOptionDAO;
	}
}
