package com.pennanttech.bajaj.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dbengine.DataEngineDBProcess;

public class DisbursementService extends Thread {
	private static final Logger logger = Logger.getLogger(DisbursementService.class);

	private String finType;
	private List<FinAdvancePayments> disbusments;
	private String database;
	private long userId;

	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;

	public DisbursementService(String finType, List<FinAdvancePayments> disbusments, String database, long userId) {
		this.finType = finType;
		this.disbusments = disbusments;
		this.database = database;
		this.userId = userId;
		setDataSourece();
	}

	private void setDataSourece() {
		this.dataSource = (DataSource) SpringUtil.getBean("pfsDatasource");
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public void process() {
		logger.debug("Entering");
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
					process(paymentTypes, configName, partnerbankCode);
				} else if ("CHEQUE".equals(disbursment.getPaymentType()) || "DD".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_CHEQUE_DD_EXPORT";
					process(paymentTypes, configName, partnerbankCode);

				} else if ("IMPS".equals(disbursment.getPaymentType())) {
					configName = "DISB_IMPS_EXPORT";
					process(paymentTypes, configName, partnerbankCode);
				} else {
					continue;
				}
			} else {
				process(paymentTypes, configName, partnerbankCode);
			}
		}
		logger.debug("Leaving");
	}

	private void process(Map<String, StringBuilder> paymentTypes, String configName, String partnerbankCode) {
		if (paymentTypes.get("IMPS") != null && "DISB_IMPS_EXPORT".equals(configName)) {
			processImpsDisbursements(configName, paymentTypes.get("IMPS"));
			paymentTypes.remove("IMPS");
		} else if (paymentTypes.get("NEFT") != null) {
			processOthreDisbursements(configName, paymentTypes.get("NEFT"), "NEFT", partnerbankCode);
			paymentTypes.remove("NEFT");
		} else if (paymentTypes.get("RTGS") != null) {
			processOthreDisbursements(configName, paymentTypes.get("RTGS"), "RTGS", partnerbankCode);
			paymentTypes.remove("RTGS");
		} else if (paymentTypes.get("DD") != null) {
			processOthreDisbursements(configName, paymentTypes.get("DD"), "DD", partnerbankCode);
			paymentTypes.remove("DD");
		} else if (paymentTypes.get("CHEQUE") != null) {
			processOthreDisbursements(configName, paymentTypes.get("CHEQUE"), "CHEQUE", partnerbankCode);
			paymentTypes.remove("CHEQUE");
		}
	}

	private synchronized void processOthreDisbursements(String configName, StringBuilder paymentIds, String paymentType, String partnerbankCode) {
		DataEngineExport export = new DataEngineExport(dataSource, userId, database);

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("PAYMENTID", paymentIds.toString());

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("PRODUCT_CODE", StringUtils.trimToEmpty(finType));
		parameterMap.put("PAYMENT_TYPE", paymentType);
		parameterMap.put("PARTNER_BANK_CODE", partnerbankCode);

		try {
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);
			export.exportData(configName);
		} catch (Exception e) {

		} finally {
		}
	}

	private synchronized void processImpsDisbursements(String configName, StringBuilder paymentIds) {
		DataEngineDBProcess proce = new DataEngineDBProcess(dataSource, userId, database);
		try {
			proce.processData(configName, paymentIds.toString());
		} catch (Exception e) {

		} finally {
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

			return jdbcTemplate.queryForObject(sql.toString(), parameter, String.class);

		} catch (Exception e) {
		} finally {
			sql = null;
			parameter = null;
		}
		return null;
	}

	@Override
	public void run() {
		process();
	}

}
