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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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

	public void process(List<Disbursement> disbusments) {
		Map<String, List<Long>> config = new HashMap<>();

		for (Disbursement disbursment : disbusments) {
			Disbursement item = getDisbursment(disbursment);

			if (item == null) {
				item = new Disbursement();
				if ("NEFT".equals(disbursment.getPaymentType()) || "RTGS".equals(disbursment.getPaymentType())) {
					item.setConfigName("DISB_OTHER_NEFT_RTGS_EXPORT");
				} else if ("CHEQUE".equals(disbursment.getPaymentType()) || "DD".equals(disbursment.getPaymentType())) {
					item.setConfigName("DISB_OTHER_CHEQUE_DD_EXPORT");
				}
			}

			if (!config.containsKey(item.getConfigName())) {
				config.put(item.getConfigName(), new ArrayList<Long>());
			}
			config.get(item.getConfigName()).add(item.getDisbusmentId());

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

	private Disbursement getDisbursment(Disbursement disbursment) {
		MapSqlParameterSource parameter = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder();
			sql.append(" SELECT * ");
			sql.append(" FROM DISBURSMENT_UPLOAD_MAPPING");
			sql.append(" WHERE BANKCODE = :BANKCODE AND PAYMENTTYPE = :PAYMENTTYPE");

			parameter = new MapSqlParameterSource();
			parameter.addValue("BANKCODE", disbursment.getBankCode());
			parameter.addValue("PAYMENTTYPE", disbursment.getPaymentType());

			return jdbcTemplate.queryForObject(sql.toString(), parameter,
					ParameterizedBeanPropertyRowMapper.newInstance(Disbursement.class));

		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			sql = null;
			parameter = null;
		}
		return null;
	}

}
