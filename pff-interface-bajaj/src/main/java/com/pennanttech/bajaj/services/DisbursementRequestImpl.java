package com.pennanttech.bajaj.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dbengine.DataEngineDBProcess;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.disbursement.DisbursementProcess;
import com.pennanttech.pff.core.services.disbursement.DisbursementRequest;
import com.pennanttech.pff.core.services.disbursement.DisbursementResponse;

public class DisbursementRequestImpl extends BajajServices implements DisbursementRequest, DisbursementResponse {
	private final Logger		logger	= Logger.getLogger(getClass());

	@Autowired
	private DisbursementProcess	disbursementProcess;

	@SuppressWarnings("unchecked")
	@Override
	public void sendReqest(Object... params) throws Exception {
		String finType = (String) params[0];
		List<FinAdvancePayments> disbusments = (List<FinAdvancePayments>) params[1];
		long userId = (long) params[2];

		DisbursementProcessThread process = new DisbursementProcessThread(finType, userId, disbusments);
		Thread thread = new Thread(process);
		try {
			DisbursementProcessThread.sleep(5000);
		} catch (InterruptedException e) {
		}

		thread.start();

	}

	private void processDisbursements(String finType, long userId, List<FinAdvancePayments> disbusments) {
		logger.debug(Literal.ENTERING);
		Map<String, StringBuilder> paymentTypes = new HashMap<>();
		String partnerbankCode = null;
		for (FinAdvancePayments disbursment : disbusments) {
			String paymentType = StringUtils.trimToEmpty(disbursment.getPaymentType());
			partnerbankCode = disbursment.getPartnerbankCode();
			if (!paymentTypes.containsKey(paymentType)) {
				paymentTypes.put(paymentType, new StringBuilder());
			}

			StringBuilder builder = paymentTypes.get(paymentType);

			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(String.valueOf(disbursment.getPaymentId()));
		}

		for (FinAdvancePayments disbursment : disbusments) {
			String configName = getConfigName(disbursment);
			if (configName == null) {
				if ("NEFT".equals(disbursment.getPaymentType()) || "RTGS".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_NEFT_RTGS_EXPORT";
					process(paymentTypes, configName, partnerbankCode, finType, userId);
				} else if ("CHEQUE".equals(disbursment.getPaymentType()) || "DD".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_CHEQUE_DD_EXPORT";
					process(paymentTypes, configName, partnerbankCode, finType, userId);

				} else if ("IMPS".equals(disbursment.getPaymentType())) {
					configName = "DISB_IMPS_EXPORT";
					process(paymentTypes, configName, partnerbankCode, finType, userId);
				} else {
					continue;
				}
			} else {
				process(paymentTypes, configName, partnerbankCode, finType, userId);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void process(Map<String, StringBuilder> paymentTypes, String configName, String partnerbankCode,
			String finType, long userId) {
		if (paymentTypes.get("IMPS") != null && "DISB_IMPS_EXPORT".equals(configName)) {
			processImpsDisbursements(configName, paymentTypes.get("IMPS"), userId);
			paymentTypes.remove("IMPS");
		} else if (paymentTypes.get("NEFT") != null) {
			processOthreDisbursements(configName, paymentTypes.get("NEFT"), "NEFT", partnerbankCode, finType, userId);
			paymentTypes.remove("NEFT");
		} else if (paymentTypes.get("RTGS") != null) {
			processOthreDisbursements(configName, paymentTypes.get("RTGS"), "RTGS", partnerbankCode, finType, userId);
			paymentTypes.remove("RTGS");
		} else if (paymentTypes.get("DD") != null) {
			processOthreDisbursements(configName, paymentTypes.get("DD"), "DD", partnerbankCode, finType, userId);
			paymentTypes.remove("DD");
		} else if (paymentTypes.get("CHEQUE") != null) {
			processOthreDisbursements(configName, paymentTypes.get("CHEQUE"), "CHEQUE", partnerbankCode, finType,
					userId);
			paymentTypes.remove("CHEQUE");
		}
	}

	private synchronized void processOthreDisbursements(String configName, StringBuilder paymentIds,
			String paymentType, String partnerbankCode, String finType, long userId) {
		DataEngineExport export = new DataEngineExport(dataSource, userId, App.DATABASE.name());

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("PAYMENTID", paymentIds.toString());

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("PRODUCT_CODE", StringUtils.trimToEmpty(finType));
		parameterMap.put("PAYMENT_TYPE", paymentType);
		parameterMap.put("PARTNER_BANK_CODE", partnerbankCode);

		try {
			export.setValueDate(getAppDate());
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);
			export.exportData(configName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(Literal.EXCEPTION, e);
		} finally {
			export = null;
			parameterMap = null;
		}
	}

	private synchronized void processImpsDisbursements(String configName, StringBuilder paymentIds, long userId) {
		DataEngineDBProcess proce = new DataEngineDBProcess(dataSource, userId, App.DATABASE.name());
		try {
			proce.setValueDate(getAppDate());
			proce.processData(configName, paymentIds.toString());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			proce = null;
		}

	}

	private String getConfigName(FinAdvancePayments disbursment) {
		MapSqlParameterSource parameter = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder();
			sql.append(" SELECT ConfigName ");
			sql.append(" FROM DISBURSMENT_UPLOAD_MAPPING DM");
			sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKCODE = DM.BANKCODE");
			sql.append(" WHERE PB.PARTNERBANKID = :PARTNERBANKID AND DM.PAYMENTTYPE = :PAYMENTTYPE");

			parameter = new MapSqlParameterSource();
			parameter.addValue("PARTNERBANKID", disbursment.getPartnerBankID());
			parameter.addValue("PAYMENTTYPE", disbursment.getPaymentType());

			return namedJdbcTemplate.queryForObject(sql.toString(), parameter, String.class);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			sql = null;
			parameter = null;
		}
		return null;
	}

	public class DisbursementProcessThread extends Thread {
		private final Logger				logger	= Logger.getLogger(DisbursementProcessThread.class);

		private String						finType;
		private long						userId;
		private List<FinAdvancePayments>	disbursements;

		public DisbursementProcessThread(String finType, long userId, List<FinAdvancePayments> disbursements) {
			this.finType = finType;
			this.userId = userId;
			this.disbursements = disbursements;
		}

		@Override
		public void run() {
			try {
				processDisbursements(finType, userId, disbursements);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		
		
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<FinAdvancePayments> disbursements = null;
		try {
			sql = new StringBuilder("Select * FROM FINADVANCEPAYMENTS");
			sql.append(" WHERE Status IN(:Status");

			paramMap = new MapSqlParameterSource();
			paramMap.addValue("Status", Arrays.asList(new String[] { "E", "R" }));

			disbursements = namedJdbcTemplate.queryForList(sql.toString(), paramMap, FinAdvancePayments.class);

			for (FinAdvancePayments disbursement : disbursements) {
				disbursementProcess.process(disbursement);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			sql = null;
			paramMap = null;
		}
	}
}
