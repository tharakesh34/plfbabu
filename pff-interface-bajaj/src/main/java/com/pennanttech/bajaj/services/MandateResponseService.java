package com.pennanttech.bajaj.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.MandateResponse;

public class MandateResponseService extends BajajService implements MandateResponse {
	private final Logger logger = Logger.getLogger(getClass());

	public MandateResponseService() {
		super();
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<Mandate> mandates = null;
		RowMapper<Mandate> rowMapper = null;
		long respBatchId = (long) params[0];

		try {
			sql = new StringBuilder();
			sql.append(" SELECT MANDATEID, FINREFERENCE, CUSTCIF,  MICR_CODE, ACCT_NUMBER, OPENFLAG, MANDATE_TYPE, MANDATE_REG_NO mandateRef, STATUS, REMARKS reason");
			sql.append(" FROM MANDATE_RESPONSE");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");

			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", respBatchId);

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
			mandates = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

			for (Mandate respMandate : mandates) {
				Mandate mandate = getMandateById(respMandate.getMandateID());

				StringBuilder remarks = new StringBuilder();

				if (!StringUtils.equalsIgnoreCase(mandate.getCustCIF(), respMandate.getCustCIF())) {
					if (remarks.length() > 0) {
						remarks.append("\n");
					}
					remarks.append("Customer Code ");
				}

				if (!StringUtils.equalsIgnoreCase(mandate.getFinReference(), respMandate.getFinReference())) {
					if (remarks.length() > 0) {
						remarks.append("\n");
					}
					remarks.append("Fin Reference ");
				}

				if (!StringUtils.equalsIgnoreCase(mandate.getMICR(), respMandate.getMICR())) {
					if (remarks.length() > 0) {
						remarks.append("\n");
					}
					remarks.append("MICR Code ");
				}

				if (!StringUtils.equalsIgnoreCase(mandate.getAccNumber(), respMandate.getAccNumber())) {
					if (remarks.length() > 0) {
						remarks.append("\n");
					}
					remarks.append("Account No. ");
				}

				if (!StringUtils.equalsIgnoreCase(mandate.getMandateType(), respMandate.getMandateType())) {
					if (remarks.length() > 0) {
						remarks.append("\n");
					}
					remarks.append("Mandate Type ");
				}

				if (!(mandate.isOpenMandate() == respMandate.isOpenMandate())) {
					if (remarks.length() > 0) {
						remarks.append("\n");
					}
					remarks.append("Open Mandate ");
				}

				if (remarks.length() > 0) {
					respMandate.setReason(remarks.toString() + " not matched with request");
					respMandate.setStatus("Y");
				}

				updateMandates(respMandate);
				updateMandateRequest(respMandate, respBatchId);

			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public Mandate getMandateById(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT FINREFERENCE, CUSTCIF,  MICR_CODE, ACCT_NUMBER, OPENFLAG, MANDATE_TYPE, STATUS ");
		sql.append(" From MANDATE_REQUESTS");
		sql.append(" Where MandateID =:MandateID");
		source = new MapSqlParameterSource();
		source.addValue("MandateID", id);

		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
		try {
			return this.namedJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
		} finally {
			source = null;
		}
		logger.debug(Literal.LEAVING);
		return null;

	}

	public void updateMandates(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		sql.append("Update Mandates");
		sql.append(" Set MANDATEREF = :MANDATEREF, STATUS = :STATUS, REASON = :REASON");

		sql.append("  Where MANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());

		if ("Y".equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
		} else {
			paramMap.addValue("STATUS", "APPROVED");
		}

		paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
		paramMap.addValue("REASON", respmandate.getReason());

		this.namedJdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}

	private void updateMandateRequest(Mandate respmandate, long id) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("Update Mandate_Requests");
		sql.append(" Set STATUS = :STATUS, REJECT_REASON = :REASON, RESP_BATCH_ID = :RESP_BATCH_ID");
		sql.append("  Where MANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());
		paramMap.addValue("STATUS", respmandate.getStatus());
		paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
		paramMap.addValue("REASON", respmandate.getReason());
		paramMap.addValue("RESP_BATCH_ID", id);

		this.namedJdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}
}
