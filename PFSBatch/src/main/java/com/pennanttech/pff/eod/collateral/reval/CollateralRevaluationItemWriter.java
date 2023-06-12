package com.pennanttech.pff.eod.collateral.reval;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;
import com.pennanttech.pff.notifications.service.NotificationService;

public class CollateralRevaluationItemWriter extends BasicDao<CollateralRevaluation>
		implements ItemWriter<CollateralRevaluation> {
	private static Logger logger = LogManager.getLogger(CollateralRevaluationItemWriter.class);

	private SecurityUserDAO securityUserDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDetailsService customerDetailsService;
	private NotificationService notificationService;

	public void write(List<? extends CollateralRevaluation> items) throws Exception {
		updateCollateralValues(items);
		updateCollateralSetup(items);
		updateCollateralLTVBreaches(items);

		for (CollateralRevaluation collateralRevaluation : items) {
			try {

				if (collateralRevaluation.getCurrentBankLTV().compareTo(collateralRevaluation.getThresholdLTV()) >= 0) {
					sendAlert(collateralRevaluation);
					collateralRevaluation.setSendAlert(true);
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	private void updateCollateralLTVBreaches(List<? extends CollateralRevaluation> items) {
		StringBuilder sql = new StringBuilder();
		sql.append("update COLLATERAL_LTV_BREACHES set");
		sql.append(" UnitPrice = :UnitPrice");
		sql.append(", NoOfUnits = :NoOfUnits");
		sql.append(", CurrentCollateralValue = :CurrentCollateralValue");
		sql.append(", CurrentBankLTV = :CurrentBankLTV");
		sql.append(", CurrentBankValuation = :CurrentBankValuation");
		sql.append(", SendAlert =:SendAlert");
		sql.append(" where Id =:Id");

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(items.toArray()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void updateCollateralValues(List<? extends CollateralRevaluation> items) {
		for (CollateralRevaluation collateralDetail : items) {
			updateUnitPriceData(collateralDetail.getCollHSNData(), collateralDetail.getTableName());
		}
	}

	private void updateUnitPriceData(List<? extends CollateralRevaluation> hsnData, String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(" update ");
		sql.append(tableName);
		sql.append(" set UnitPrice = :unitPrice where Reference = :collateralRef And HSNCode = :hsnCode");

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(hsnData.toArray()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void updateCollateralSetup(List<? extends CollateralRevaluation> items) {
		StringBuilder sql = new StringBuilder();
		sql.append("update CollateralSetup set");
		sql.append(" CollateralValue =:CurrentCollateralValue");
		sql.append(", BankLTV = :CurrentBankLTV");
		sql.append(", BankValuation = :CurrentBankValuation");
		sql.append(" where CollateralRef =:CollateralRef");

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(items.toArray()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void sendAlert(CollateralRevaluation collateral) {
		for (CollateralRevaluation hsnData : collateral.getCollHSNData()) {
			FinanceDetail financeDetail = new FinanceDetail();
			CustomerDetails customerDetails = new CustomerDetails();

			FinanceMain financeMain = financeMainDAO.getFinanceMainByRef(collateral.getFinReference(), "_aview", false);
			financeMain.setUserDetails(new LoggedInUser());

			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.setCollateralRevaluation(collateral);
			financeDetail.setCustomerDetails(customerDetails);

			customerDetails.setCustID(financeMain.getCustID());
			customerDetailsService.setCustomerBasicDetails(customerDetails);

			if (customerDetails.getCustomer() == null) {
				return;
			}
			// For Customers marked as DND true are not allow to Trigger a Mail.
			if (customerDetails.getCustomer().isDnd()) {
				return;
			}

			if (hsnData.getCustomerTemplateCode() != null) {
				Notification notification = new Notification();
				notification.setKeyReference(collateral.getFinReference());
				notification.setModule("LOAN");
				notification.setSubModule("Collateral");
				notification.setTemplateCode(hsnData.getUserTemplateCode());

				List<String> emails = new ArrayList<>();

				for (CustomerEMail customerEmail : customerDetails.getCustomerEMailList()) {
					if (customerEmail.getCustEMailPriority() == Integer
							.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						if (StringUtils.isNotEmpty(customerEmail.getCustEMail())) {
							emails.add(customerEmail.getCustEMail());
						}
					}
				}

				notification.setEmails(emails);
				sendNotification(financeDetail, notification);

			} else if (hsnData.getUserTemplateCode() != null && hsnData.getAlertToRoles() != null) {
				Notification notification = new Notification();
				notification.setKeyReference(collateral.getFinReference());
				notification.setModule("LOAN");
				notification.setSubModule("Collateral");
				notification.setTemplateCode(hsnData.getUserTemplateCode());

				List<SecurityUser> secUsers = securityUserDAO.getSecUsersByRoles(hsnData.getAlertToRoles().split(","));

				List<String> emails = new ArrayList<>();
				for (SecurityUser securityUser : secUsers) {
					if (StringUtils.isNotEmpty(securityUser.getUsrEmail())) {
						emails.add(securityUser.getUsrEmail());
					}
				}

				notification.setEmails(emails);

				sendNotification(financeDetail, notification);

			}
		}
	}

	@Override
	public void write(Chunk<? extends CollateralRevaluation> chunk) throws Exception {
		// TODO Auto-generated method stub

	}

	private long sendNotification(FinanceDetail financeDetail, Notification notification) {
		if (CollectionUtils.isEmpty(notification.getEmails())) {
			return 0;
		}

		notificationService.sendNotification(notification, financeDetail);
		return notification.getId();
	}

	@Autowired
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
