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

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dbengine.DataEngineDBProcess;

public class DisbursementService {
	private static final Logger			logger	= Logger.getLogger(DisbursementService.class);

	private DataSource					dataSource;
	private String						database;
	private long						userId;

	private static DisbursementService	instance;
	private NamedParameterJdbcTemplate	jdbcTemplate;

	private DisbursementService() {

	}

	private DisbursementService(DataSource dataSource, String database, long userId) {
		this.dataSource = dataSource;
		this.database = database;
	}

	public void process(List<FinAdvancePayments> disbusments) {
		Map<String, List<Long>> config = new HashMap<>();

		for (FinAdvancePayments disbursment : disbusments) {
			String configName = getConfigName(disbursment);

			if (configName == null) {
				if ("NEFT".equals(disbursment.getPaymentType()) || "RTGS".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_NEFT_RTGS_EXPORT";
				} else if ("CHEQUE".equals(disbursment.getPaymentType()) || "DD".equals(disbursment.getPaymentType())) {
					configName = "DISB_OTHER_CHEQUE_DD_EXPORT";
				}
			}

			if (!config.containsKey(configName)) {
				config.put(configName, new ArrayList<Long>());
			}
			config.get(configName).add(disbursment.getPaymentId());

		}

		for (Entry<String, List<Long>> disbursment : config.entrySet()) {

			if ("DISB_IMPS_EXPORT".equals(disbursment.getKey())) {
				DataEngineDBProcess proce = new DataEngineDBProcess(dataSource, userId, database);
				proce.processData(disbursment.getKey());
				continue;
			}

			DataEngineExport export = new DataEngineExport(dataSource, userId, database);
			Map<String, Object> filterMap = new HashMap<>();

			filterMap.put("PAYMENTID", disbursment.getValue().toArray(new String[disbursment.getValue().size()]));

			try {
				export.setFilterMap(filterMap);
				export.exportData(disbursment.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public DisbursementService getInstance(DataSource dataSource, String database, long userId) {
		if (instance == null) {

			synchronized (DisbursementService.class) {
				if (instance == null) {
					instance = new DisbursementService(dataSource, database, userId);
				}
			}
		}

		return instance;
	}

	private String getConfigName(FinAdvancePayments disbursment) {
		MapSqlParameterSource parameter = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder();
			sql.append(" SELECT ConfigName ");
			sql.append(" FROM DISBURSMENT_UPLOAD_MAPPING");
			sql.append(" WHERE BANKCODE = :BANKCODE AND PAYMENTTYPE = :PAYMENTTYPE");

			parameter = new MapSqlParameterSource();
			parameter.addValue("BANKCODE", disbursment.getBankCode());
			parameter.addValue("PAYMENTTYPE", disbursment.getPaymentType());

			return jdbcTemplate.queryForObject(sql.toString(), parameter, String.class);

		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			sql = null;
			parameter = null;
		}
		return null;
	}

}
