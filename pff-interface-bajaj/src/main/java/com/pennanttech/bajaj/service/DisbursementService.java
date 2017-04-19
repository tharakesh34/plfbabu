package com.pennanttech.bajaj.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dbengine.DataEngineDBProcess;

public class DisbursementService extends Thread {
	private static final Logger logger = Logger.getLogger(DisbursementService.class);

	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;
	private String database;
	private long userId;
	private List<FinAdvancePayments> disbusments;

	public DisbursementService(List<FinAdvancePayments> disbusments, String database, long userId) {
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
		Map<String, StringBuilder> config = new HashMap<>();

		for (FinAdvancePayments disbursment : disbusments) {
			String configName = getConfigName(disbursment);

			if (configName == null) {
				if ("NEFT".equals(disbursment.getPaymentType()) || "RTGS".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_NEFT_RTGS_EXPORT";
				} else if ("CHEQUE".equals(disbursment.getPaymentType()) || "DD".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_CHEQUE_DD_EXPORT";
				} else if ("IMPS".equals(disbursment.getPaymentType())) {
					configName = "DISB_IMPS_EXPORT";
				} else {
					continue;
				}
			}

			if (!config.containsKey(configName)) {
				config.put(configName, new StringBuilder());
			}

			StringBuilder builder = config.get(configName);

			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(String.valueOf(disbursment.getPaymentId()));

		}

		for (Entry<String, StringBuilder> disbursment : config.entrySet()) {

			if ("DISB_IMPS_EXPORT".equals(disbursment.getKey())) {
				DataEngineDBProcess proce = new DataEngineDBProcess(dataSource, userId, database);
				proce.processData(disbursment.getKey());
				continue;
			}

			DataEngineExport export = new DataEngineExport(dataSource, userId, database);
			Map<String, Object> filterMap = new HashMap<>();
			filterMap.put("PAYMENTID", disbursment.getValue().toString());
			
			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("PRODUCT_CODE", "TL"); // FIXME

			try {
				export.setFilterMap(filterMap);
				export.setParameterMap(parameterMap);
				
				
				export.exportData(disbursment.getKey());
			} catch (Exception e) {
				logger.error("Exception: ", e);
				e.printStackTrace();
			}
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
