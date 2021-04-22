package com.pennanttech.pff.notifications.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.impl.FinanceMainDAOImpl;
import com.pennant.backend.model.Notifications.SystemNotificationExecution;
import com.pennant.backend.model.Notifications.SystemNotificationExecutionDetails;
import com.pennant.backend.model.Notifications.SystemNotifications;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.util.XmlBuilder;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.notification.process.NotificationProcess;

public class NotificationProcessImpl extends BasicDao<SystemNotifications> implements NotificationProcess {
	private static final Logger logger = LogManager.getLogger(NotificationProcessImpl.class);

	protected DataSourceTransactionManager transManager;
	protected DefaultTransactionDefinition transDef;
	private long totalCount;
	private long successCount;
	private long failedCount;

	private FinanceMainDAOImpl financeMainDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private CustomerDetailsService customerDetailsService;
	private transient PaymentHeaderService paymentHeaderService;
	protected FinFeeDetailService finFeeDetailService;

	@Override
	public void invokePaymentsNotifications(PaymentTransaction paymentTransaction) {
		logger.info(Literal.ENTERING);
		successCount = 0;
		totalCount = 0;
		failedCount = 0;

		List<SystemNotifications> notifications = getConfiguredSystemNotifications("DISB_ADVICE_ALERT");
		SystemNotificationExecution execution = null;

		for (SystemNotifications systemNotification : notifications) {
			Map<String, String> attributes = new HashMap<String, String>();

			int count = 0;
			int executionID = 0;

			try {
				logger.debug("Notification process started for notification id " + systemNotification.getId());

				// Preparing the execution object.
				execution = new SystemNotificationExecution();
				execution.setNotificationId(systemNotification.getId());
				execution.setTotalCount(count);
				execution.setCreateTime(new Timestamp(System.currentTimeMillis()));
				execution.setStartTime(new Timestamp(System.currentTimeMillis()));
				executionID = logExecutionHeader(execution);
				execution.setId(executionID);

				// Process started.
				String email = null;
				FinanceDetail financeDetail = new FinanceDetail();
				CustomerDetails customerDetails = new CustomerDetails();
				String finReference = paymentTransaction.getFinReference();

				BigDecimal deductions = BigDecimal.ZERO;
				BigDecimal sanctionAmount = BigDecimal.ZERO;
				BigDecimal disbursedAmt = BigDecimal.ZERO;
				int amtFormatter = 2;
				String utrNumber = "";

				FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_AView", false);
				financeMain.setUserDetails(new LoggedInUser());
				financeDetail.setFinReference(finReference);
				customerDetails.setCustID(financeMain.getCustID());
				customerDetailsService.setCustomerBasicDetails(customerDetails);
				financeDetail.setCustomerDetails(customerDetails);
				amtFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

				List<CustomerEMail> customerEmailList = customerDetails.getCustomerEMailList();
				if (CollectionUtils.isNotEmpty(customerEmailList)) {
					for (CustomerEMail customerEMail : customerEmailList) {
						if (Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH) != customerEMail
								.getCustEMailPriority()) {
							continue;
						}
						email = customerEMail.getCustEMail();
						break;
					}
				}
				if ("DISB".equals(paymentTransaction.getTranModule())) {
					FinAdvancePayments finAdvancePayments = new FinAdvancePayments();

					finAdvancePayments.setPaymentId(paymentTransaction.getPaymentId());
					finAdvancePayments = this.finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, "");
					sanctionAmount = (BigDecimal) financeMain.getFinAssetValue();

					if (finAdvancePayments != null) {
						paymentTransaction.setFinAdvancePayments(finAdvancePayments);
						utrNumber = finAdvancePayments.getTransactionRef();

						attributes.put("BENEFICIARYNAME", finAdvancePayments.getBeneficiaryName());
						attributes.put("BANKACCNUM", finAdvancePayments.getBeneficiaryAccNo());
						disbursedAmt = finAdvancePayments.getAmtToBeReleased();
					}

					List<FinFeeDetail> finFeeDetailList = getFinFeeDetailService().getFinFeeDetailById(finReference,
							false, "_AView");

					if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
						for (FinFeeDetail finFee : finFeeDetailList) {
							if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(finFee.getFeeScheduleMethod())) {
								if (finAdvancePayments.getDisbSeq() == 1) {
									if (finFee.isOriginationFee()) {
										deductions = deductions
												.add(finFee.getActualAmount().subtract(finFee.getWaivedAmount()));
									}
								} else {
									if (!finFee.isOriginationFee()) {
										deductions = deductions
												.add(finFee.getActualAmount().subtract(finFee.getWaivedAmount()));
									}
								}
							}
						}
					}
				} else if ("PYMT".equals(paymentTransaction.getTranModule())) {
					PaymentInstruction paymentInstruction = paymentHeaderService
							.getPaymentInstruction(paymentTransaction.getPaymentId());
					if (paymentInstruction != null) {
						utrNumber = paymentInstruction.getTransactionRef();
						attributes.put("BENEFICIARYNAME", paymentInstruction.getAcctHolderName());
						attributes.put("BANKACCNUM", paymentInstruction.getAccountNo());
						disbursedAmt = paymentInstruction.getPaymentAmount();
					}
				}

				attributes.put("EMAIL", email);
				attributes.put("FINREFERENCE", finReference);
				attributes.put("SANCTIONAMOUNT", PennantApplicationUtil.amountFormate(sanctionAmount, amtFormatter));
				attributes.put("DEDUCTION", PennantApplicationUtil.amountFormate(deductions, amtFormatter));
				attributes.put("NETAMTCREDITED", PennantApplicationUtil.amountFormate(disbursedAmt, amtFormatter));
				attributes.put("AMOUNTDISBURSED",
						PennantApplicationUtil.amountFormate(disbursedAmt.add(deductions), amtFormatter));

				if (StringUtils.trimToNull(utrNumber) == null) {
					attributes.put("UTRNO", paymentTransaction.getTranReference());
				} else {
					attributes.put("UTRNO", utrNumber);
				}

				executeCriteriaQuery(systemNotification, executionID, attributes);

				execution.setStatus("S");

				logger.debug("Notification process Ended for notification id " + systemNotification.getId());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				execution.setFailedCount(failedCount);
				execution.setStatus("F");
			} finally {
				try {
					if (execution.getTotalCount() == 0) {
						execution.setTotalCount(totalCount);
					}
					execution.setSucessCount(successCount);
					execution.setEndTime(new Timestamp(System.currentTimeMillis()));
					updateExecutionHeader(execution);
				} catch (Exception exe) {
					logger.error(Literal.EXCEPTION, exe);
				}
			}
		}
		logger.info(Literal.LEAVING);
	}

	private void updateExecutionHeader(SystemNotificationExecution execution) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("UPDATE SYS_NOTIFICATION_EXECUTION");
		sql.append(" set TotalCount = :TotalCount, SuccessCount = :SucessCount, FailedCount = :FailedCount,");
		sql.append(" Status = :Status, EndTime = :EndTime");
		sql.append(" WHERE id = :id");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(execution);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	private int logExecutionHeader(SystemNotificationExecution execution) {

		StringBuilder query = new StringBuilder();
		query.append(" insert into sys_notification_execution ");
		query.append(" (NotificationId, InstanceId, Createtime, Starttime, Totalcount, Successcount, ");
		query.append(" Failedcount, Status, Endtime)");
		query.append(" values(:NotificationId, :InstanceId, :CreateTime, :StartTime, :TotalCount, ");
		query.append(":SucessCount, :FailedCount, :Status, :EndTime)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(execution);

		try {
			jdbcTemplate.update(query.toString(), paramSource, keyHolder, new String[] { "id" });
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return keyHolder.getKey().intValue();
	}

	private void executeCriteriaQuery(SystemNotifications systemNotifications, long id,
			Map<String, String> attributes) {
		totalCount++;

		try {
			XmlBuilder builder = new XmlBuilder(NotificationConstants.SYSTEM_NOTIFICATION, null);
			String value;

			String mobileNum = null;
			String email = attributes.get("EMAIL");

			for (Entry<String, String> data : attributes.entrySet()) {
				if (data.getValue() != null) {
					value = data.getValue().toString();
				} else {
					value = "";
				}
				builder.setOMElement(builder, data.getKey(), value);
			}

			System.out.println((builder.getEnvelope().toString()));
			logExecutionDetails(builder.getEnvelope().toString().getBytes("UTF-8"), id, email, mobileNum,
					(int) systemNotifications.getId(), attributes);
			successCount++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			failedCount++;
		}

	}

	private void logExecutionDetails(byte[] notificationData, long id, String email, String mobileNum,
			int notificationId, Map<String, String> dataMap) throws Exception {

		SystemNotificationExecutionDetails details = new SystemNotificationExecutionDetails();

		details.setNotificationData(notificationData);
		details.setExecutionId(id);
		details.setEmail(email);
		details.setMobileNumber(mobileNum);
		details.setNotificationId(notificationId);
		details.setProcessingFlag(BooleanUtils.toBoolean(0));
		details.setKeyReference(dataMap.get("FINREFERENCE"));
		details.setAttributes(dataMap.toString());

		StringBuilder query = new StringBuilder();
		query.append(" INSERT INTO SYS_NOTIFICATION_EXEC_LOG ");
		query.append(" (EXECUTIONID, NOTIFICATIONID, PROCESSINGFLAG, KEYREFERENCE, EMAIL, MOBILENUMBER, ");
		query.append(" NOTIFICATIONDATA, Attributes )");
		query.append(" VALUES(:ExecutionId, :NotificationId, :ProcessingFlag, :KeyReference, :Email, :MobileNumber,");
		query.append(":NotificationData, :Attributes)");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(details);

		try {
			jdbcTemplate.update(query.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	private List<SystemNotifications> getConfiguredSystemNotifications(String code) {
		logger.debug(Literal.ENTERING);

		SystemNotifications systemNotifications = new SystemNotifications();
		systemNotifications.setActive(true);
		systemNotifications.setCode(code);

		StringBuilder sql = new StringBuilder();
		sql.append("Select * from Sys_Notifications where code= :code and active = :active");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemNotifications);
		RowMapper<SystemNotifications> typeRowMapper = BeanPropertyRowMapper.newInstance(SystemNotifications.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinanceMainDAOImpl getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAOImpl financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public PaymentHeaderService getPaymentHeaderService() {
		return paymentHeaderService;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}
}
