package com.pennanttech.bajaj.process;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants.Status;
import com.pennanttech.pff.core.App;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class DisbursemenIMPSRequestProcess extends DatabaseDataEngine {
	private static final Logger	logger	= Logger.getLogger(DisbursemenIMPSRequestProcess.class);

	private List<String>		disbursments;

	public DisbursemenIMPSRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
	}

	@Override
	protected void processData() {
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM DISBURSEMENT_REQUESTS WHERE ID IN (:ID) AND STATUS = :STATUS");
		parmMap = new MapSqlParameterSource();

		parmMap.addValue("ID", disbursments);
		parmMap.addValue("STATUS", "APPROVED");

		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
			MapSqlParameterSource	map			= null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				long id = rs.getLong("ID");
				logger.debug("Processing the disbursement " + id);

				processedCount++;
				try {
					map = mapData(rs);
					updateDisbursement(rs.getLong("DISBURSEMENT_ID"), rs.getString("CHANNEL"));
					updateDisbursementRequest(id, batchId);

					save(map, "INT_DSBIMPS_REQUEST", destinationJdbcTemplate);
					successCount++;

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					failedCount++;
					logger.debug("Disbursement request: " + map.toString());
					saveBatchLog(rs.getString("DISBURSEMENT_ID"), "F", e.getMessage());
				} finally {
					map = null;
				}

			}
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BCAGENTID", null);
		map.addValue("SENDERID", null);
		map.addValue("RECEIVERNAME", rs.getString("BENFICIARY_NAME"));

		map.addValue("RECEIVERMOBILENO", rs.getString("BENFICIARY_MOBILE"));
		map.addValue("RECEIVEREMAILID", rs.getString("CUSTOMER_EMAIL"));
		map.addValue("IFSCODE", rs.getString("IFSC_CODE"));
		map.addValue("BANK", rs.getString("BENFICIARY_BANK"));
		map.addValue("RECEVIERBANKSTATE", rs.getString("BENFICIARY_BRANCH_STATE"));
		map.addValue("RECEVIERBANKCITY", rs.getString("BENFICIARY_BRANCH_CITY"));
		map.addValue("RECEVIERBANKBRANCH", rs.getString("BENFICIARY_BRANCH"));
		map.addValue("RECEVIERACCOUNTNUMBER", rs.getString("BENFICIARY_ACCOUNT"));
		map.addValue("AMOUNT", rs.getString("DISBURSEMENT_AMOUNT"));
		map.addValue("REMARKS", StringUtils.substring(rs.getString("REMARKS"), 0, 9));
		map.addValue("CHANNELPARTNERREFNO", rs.getString("ID"));
		map.addValue("PICKUPFLAG", Status.N.name());
		
		String appId = null;
		String finReference = StringUtils.trimToNull(rs.getString("FINREFERENCE"));
		if (finReference != null) {
			appId = StringUtils.substring(finReference, finReference.length() - 8, finReference.length());
			appId = StringUtils.trim(appId);
			map.addValue("AGREEMENTID", Integer.parseInt(appId));
		} else {
			map.addValue("AGREEMENTID", BigDecimal.ZERO);

		}
		return map;
	}

	private int updateDisbursement(long paymentId, String channel) {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		try {
			//IF Status is P then we will update customer payments tables..
			if ("P".equals(channel)) {
				sql.append("UPDATE PAYMENTINSTRUCTIONS  SET STATUS  =  :STATUS WHERE  PAYMENTINSTRUCTIONID = :PAYMENTID");
			} else {
				sql.append("UPDATE FINADVANCEPAYMENTS  SET STATUS  =  :STATUS WHERE  PAYMENTID = :PAYMENTID");
			}

			paramMap.addValue("STATUS", "AC");
			paramMap.addValue("PAYMENTID", paymentId);

			return jdbcTemplate.update(sql.toString(), paramMap);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private int updateDisbursementRequest(long id, long batchId) {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		try {
			sql.append("UPDATE DISBURSEMENT_REQUESTS  SET STATUS  = :STATUS, BATCH_ID  = :BATCH_ID  WHERE ID = :ID");

			paramMap.addValue("STATUS", "AC");
			paramMap.addValue("BATCH_ID", batchId);
			paramMap.addValue("ID", id);

			return jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	public void setDisbursments(List<String> disbursments) {
		this.disbursments = disbursments;
	}
}
