package com.pennanttech.bajaj.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.TransactionStatus;

import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.MandateResponseService;
import com.pennanttech.pff.core.util.DateUtil;

public class MandateResponseServiceImpl extends BajajService implements MandateResponseService {
	private final Logger	logger	= Logger.getLogger(getClass());

	public MandateResponseServiceImpl() {
		super();
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<Mandate> mandates = null;
		RowMapper<Mandate> rowMapper = null;
		long respBatchId = (long) params[0];

		long approved = 0;
		long rejected = 0;
		long notMatched = 0;

		sql = new StringBuilder();
		sql.append(" SELECT MANDATEID, FINREFERENCE, CUSTCIF,  MICR_CODE MICR, ACCT_NUMBER AccNumber, case when OPENFLAG = 'Y' THEN 'New Open ECS' ELSE 'No Open ECS' END lovValue, MANDATE_TYPE, MANDATE_REG_NO mandateRef, STATUS, REMARKS reason");
		sql.append(" FROM MANDATE_RESPONSE");
		sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("RESP_BATCH_ID", respBatchId);

		rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
		mandates = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

		if (mandates == null || mandates.isEmpty()) {
			return;
		}
		try {
			for (Mandate respMandate : mandates) {
				boolean matched = true;
				boolean reject = false;
				Mandate mandate = getMandateById(respMandate.getMandateID());
				StringBuilder remarks = new StringBuilder();

				if (mandate == null) {
					respMandate.setReason("Mandate request not exist or already processed.");
					updateMandateResponse(respMandate);
					logMandate(respBatchId, respMandate);
				} else {
					validateMandate(respMandate, mandate, remarks);

					if (remarks.length() > 0) {
						respMandate.setReason(remarks.toString() + " not matched with request");
						respMandate.setStatus("Y");
						matched = false;
					}

					if (matched) {
						TransactionStatus txnStatus = null;
						try {
							txnStatus = transManager.getTransaction(transDef);
							updateMandates(respMandate);
							logMandateHistory(respMandate, mandate.getRequestID());
							updateMandateRequest(respMandate, respBatchId);
							transManager.commit(txnStatus);
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							respMandate.setReason(e.getMessage());
							logMandate(respBatchId, respMandate);
							transManager.rollback(txnStatus);
						} finally {
							txnStatus.flush();
							txnStatus = null;
						}

						if ("Y".equals(respMandate.getStatus())) {
							rejected++;
							reject = true;
						} else {
							approved++;
						}
					} else {
						notMatched++;
					}

					if (!matched || reject) {
						logMandate(respBatchId, respMandate);
					}
				}

			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			updateRemarks(respBatchId, approved, rejected, notMatched);
		}
	}

	private void updateRemarks(long respBatchId, long approved, long rejected, long notMatched) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		StringBuilder remarks = new StringBuilder(BajajInterfaceConstants.MANDATE_INMPORT_STATUS.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(approved);
		remarks.append(", Rejected: ");
		remarks.append(rejected);
		remarks.append(", Not Matched: ");
		remarks.append(notMatched);

		BajajInterfaceConstants.MANDATE_INMPORT_STATUS.setRemarks(remarks.toString());

		StringBuffer query = new StringBuffer();
		query.append(" UPDATE DATA_ENGINE_STATUS set EndTime = :EndTime, Remarks = :Remarks ");
		query.append(" WHERE Id = :Id");

		parameterSource.addValue("EndTime", DateUtil.getSysDate());
		parameterSource.addValue("Remarks", remarks.toString());
		parameterSource.addValue("Id", respBatchId);

		try {
			this.namedJdbcTemplate.update(query.toString(), parameterSource);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void logMandate(long respBatchId, Mandate respMandate) {
		SqlParameterSource beanParameters = null;
		DataEngineLog log = new DataEngineLog();

		log.setId(respBatchId);
		log.setKeyId(String.valueOf(respMandate.getMandateID()));
		log.setReason(respMandate.getReason());

		if (respMandate.getStatus() != null && respMandate.getStatus().length() == 1) {
			log.setStatus(respMandate.getStatus());
		} else {
			log.setStatus("Y");
		}

		BajajInterfaceConstants.MANDATE_INMPORT_STATUS.getDataEngineLogList().add(log);

		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO DATA_ENGINE_LOG");
		query.append(" (Id, KeyId, Status, Reason)");
		query.append(" VALUES(:Id, :KeyId, :Status, :Reason)");

		try {
			beanParameters = new BeanPropertySqlParameterSource(log);
			this.namedJdbcTemplate.update(query.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}

	private void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {
		if (!StringUtils.equals(mandate.getCustCIF(), respMandate.getCustCIF())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Customer Code");
		}

		if (!StringUtils.equals(mandate.getFinReference(), respMandate.getFinReference())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Fin Reference");
		}

		if (!StringUtils.equals(mandate.getMICR(), respMandate.getMICR())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("MICR Code");
		}

		if (!StringUtils.equals(mandate.getAccNumber(), respMandate.getAccNumber())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Account No.");
		}

		if (!StringUtils.equals(mandate.getMandateType(), respMandate.getMandateType())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Mandate Type");
		}

		if (!StringUtils.equals(mandate.getLovValue(), respMandate.getLovValue())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Open Mandate");
		}

	}

	public Mandate getMandateById(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT ID RequestID, MandateID, FINREFERENCE, CUSTCIF,  MICR_CODE MICR, ACCT_NUMBER AccNumber, OPENFLAG lovValue, MANDATE_TYPE, STATUS ");
		sql.append(" From MANDATE_REQUESTS");
		sql.append(" Where MandateID =:MandateID and RESP_BATCH_ID IS NULL");
		source = new MapSqlParameterSource();
		source.addValue("MandateID", id);

		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
		try {
			return this.namedJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
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
			paramMap.addValue("MANDATEREF", null);
		} else {
			paramMap.addValue("STATUS", "APPROVED");
			paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
		}
		
		paramMap.addValue("REASON", respmandate.getReason());

		this.namedJdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}
	
	private void logMandateHistory(Mandate respmandate, long requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		
		StringBuilder sql =new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate,:fileID)");
		
		paramMap.addValue("mandateID", respmandate.getMandateID());
		
		if ("Y".equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
		} else {
			paramMap.addValue("STATUS", "APPROVED");
		}
		paramMap.addValue("REASON", respmandate.getReason());
		paramMap.addValue("changeDate", getAppDate());
		paramMap.addValue("fileID", requestId);
		
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

	private void updateMandateResponse(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("update MANDATE_RESPONSE");
		sql.append(" set REMARKS = :REMARKS");
		sql.append(" where MANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());
		paramMap.addValue("REMARKS", respmandate.getReason());

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}
}
