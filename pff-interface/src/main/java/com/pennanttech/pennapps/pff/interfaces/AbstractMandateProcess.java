package com.pennanttech.pennapps.pff.interfaces;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.zkoss.util.media.Media;

import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.MandateProcess;

public abstract class AbstractMandateProcess extends AbstractInterface implements MandateProcess {
	private final Logger	logger	= Logger.getLogger(getClass());

	@Override
	public void sendReqest(Object... object) throws Exception {
		
		@SuppressWarnings("unchecked")
		List<Long> mandateIdList = (List<Long>) object[0];

		Date fromDate = (Date) object[1];
		Date toDate = (Date) object[2];
		long userId = (Long) object[3];
		String userName = (String) object[4];
		String selectedBranchs = (String) object[5];
		String entity = (String) object[6];
		
		Long[] mandateIds = new Long[mandateIdList.size()];
				
		int i = 0;
		for (Long mandateId : mandateIdList) {
			mandateIds[i++] = mandateId;
		}

		List<Long> mandates = prepareRequest(mandateIds);

		if (mandates == null || mandates.isEmpty()) {
			return;
		}

		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		filterMap.put("ID", mandates);
		filterMap.put("FROMDATE", fromDate);
		filterMap.put("TODATE", toDate);

		if (StringUtils.isNotBlank(selectedBranchs)) {
			filterMap.put("BRANCHCODE", Arrays.asList(selectedBranchs.split(",")));
		}

		parameterMap.put("USER_NAME", userName);
		parameterMap.put("ENTITY_CODE", entity);
				
		addCustomParameter(parameterMap);
		
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate());

		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(getValueDate());
		dataEngine.exportData("MANDATES_EXPORT");
	}
	
	@Override
	public void processResponseFile(long userId, File file, Media media) throws Exception {
		logger.debug(Literal.ENTERING);

			
		String configName = MANDATES_IMPORT.getName();

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		MANDATES_IMPORT.reset();
		MANDATES_IMPORT.setFileName(name);
		MANDATES_IMPORT.setRemarks("initiated Mandate response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), MANDATES_IMPORT);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(getValueDate());
		dataEngine.importData(configName);
		
		do {
			if ("S".equals(MANDATES_IMPORT.getStatus()) || "F".equals(MANDATES_IMPORT.getStatus())) {
				receiveResponse(MANDATES_IMPORT.getId());
				break;
			}
		} while ("S".equals(MANDATES_IMPORT.getStatus()) || "F".equals(MANDATES_IMPORT.getStatus()));
		
		
		logger.debug(Literal.LEAVING);
	
	}
	
	@Override
	public void receiveResponse(long respBatchId) throws Exception {
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<Mandate> mandates = null;
		RowMapper<Mandate> rowMapper = null;

		long approved = 0;
		long rejected = 0;
		long notMatched = 0;

		sql = new StringBuilder();
		sql.append(" SELECT MANDATEID, FINREFERENCE, CUSTCIF,  MICR_CODE MICR, ACCT_NUMBER AccNumber,");
		sql.append(" case when OPENFLAG = 'Y' THEN 'New Open ECS' ELSE 'No Open ECS' END lovValue,");
		sql.append(" MANDATE_TYPE, MANDATE_REG_NO mandateRef, STATUS, REMARKS reason");
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
					respMandate.setStatus("F");
					updateMandateResponse(respMandate);
					logMandate(respBatchId, respMandate);
				} else {
					validateMandate(respMandate, mandate, remarks);

					if (remarks.length() > 0) {
						respMandate.setReason(remarks.toString());
						respMandate.setStatus("F");
						updateMandateResponse(respMandate);
						matched = false;
					}

					if (matched) {
						TransactionStatus txnStatus = null;
						try {
							txnStatus = transManager.getTransaction(transDef);
							updateMandates(respMandate);
							
							try {
								if ("N".equals(respMandate.getStatus())) {
									processSecondaryMandate(mandate);
									processSwappedMandate(mandate);
								}
							} catch (EmptyResultDataAccessException e) {
								logger.warn("Exception: ", e);
							}

							logMandateHistory(respMandate, mandate.getRequestID());
							updateMandateRequest(respMandate, respBatchId);
							updateMandateResponse(respMandate);
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

	private List<Long> prepareRequest(Long[] mandateIds) throws Exception {
		logger.debug(Literal.ENTERING);
		final Map<String, Integer> bankCodeSeq = getCountByProcessed();

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT");
		sql.append(" MANDATEID,");
		sql.append(" BANKCODE BANK_CODE,");
		sql.append(" BANKNAME BANK_NAME,");
		sql.append(" BRANCHDESC BRANCH_NAME,");
		sql.append(" CUSTCIF,");
		sql.append(" CUSTSHRTNAME CUSTOMER_NAME,");
		sql.append(" FINTYPE,");
		sql.append(" FINREFERENCE,");
		sql.append(" CUST_EMI,");
		sql.append(" EMI,");
		sql.append(" OPENMANDATE OPENFLAG,");
		sql.append(" ACCNUMBER ACCT_NUMBER,");
		sql.append(" ACCTYPE ACCT_TYPE,");
		sql.append(" ACCHOLDERNAME ACCT_HOLDER_NAME,");
		sql.append(" MICR MICR_CODE,");
		sql.append(" FIRSTDUEDATE EFFECTIVE_DATE,");
		sql.append(" EMIENDDATE EMI_ENDDATE,");
		sql.append(" EXPIRYDATE OPEN_ENDDATE,");
		sql.append(" MAXLIMIT UPPER_LIMIT,");
		sql.append(" CCYMINORCCYUNITS,");
		sql.append(" DEBITAMOUNT DEBIT_AMOUNT,");
		sql.append(" STARTDATE START_DATE,");
		sql.append(" EXPIRYDATE END_DATE,");
		sql.append(" APPLICATIONNO APPLICATION_NUMBER,");
		sql.append(" MANDATETYPE MANDATE_TYPE,");
		sql.append(" STATUS");
		sql.append(" FROM INT_MANDATE_REQUEST_VIEW");
		sql.append(" WHERE MANDATEID IN (:MANDATEID)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MANDATEID", Arrays.asList(mandateIds));

		final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Map<String, Object> rowMap = rowMapper.mapRow(rs, rowNum);
					String bankCode = null;
					
					if (rowMap.get("BANK_CODE") != null) {
						bankCode =  rowMap.get("BANK_CODE").toString();
					}
					
					rowMap.put("BATCH_ID", 0);
					rowMap.put("BANK_SEQ", getSequence(bankCode, bankCodeSeq));
					rowMap.put("EXTRACTION_DATE", getAppDate());

					String appId = null;
					String finReference = StringUtils.trimToNull(rs.getString("FINREFERENCE"));
				
					if (finReference != null) {
						appId = StringUtils.substring(finReference, finReference.length() - 7, finReference.length());
						appId = StringUtils.trim(appId);
						rowMap.put("APPLICATION_NUMBER", Integer.parseInt(appId));
					} else {
						rowMap.put("APPLICATION_NUMBER", null);

					}

					BigDecimal UPPER_LIMIT = (BigDecimal) rowMap.get("UPPER_LIMIT");
					BigDecimal CUST_EMI = (BigDecimal) rowMap.get("CUST_EMI");

					if (UPPER_LIMIT == null) {
						UPPER_LIMIT = BigDecimal.ZERO;
					}

					if (CUST_EMI == null) {
						CUST_EMI = BigDecimal.ZERO;
					}

					if (StringUtils.trimToNull((String) rowMap.get("FINREFERENCE")) == null) {

						if (CUST_EMI.compareTo(UPPER_LIMIT) > 0) {
							rowMap.put("EMI", UPPER_LIMIT);
							rowMap.put("DEBIT_AMOUNT", UPPER_LIMIT);
						} else {
							rowMap.put("EMI", CUST_EMI);
							rowMap.put("DEBIT_AMOUNT", CUST_EMI);
						}
						
						Date startDate = (Date) rowMap.get("START_DATE");
						Date firstDueDate = (Date) rowMap.get("FIRSTDUEDATE");
						Date endDate = DateUtil.addMonths(startDate, 240);
						
						rowMap.put("EFFECTIVE_DATE", startDate);
						rowMap.put("EMI_ENDDATE", endDate);
						
						if(firstDueDate == null) {
							rowMap.put("EFFECTIVE_DATE", startDate);
						}
					}

					rowMap.remove("CCYMINORCCYUNITS");
					rowMap.remove("CUST_EMI");
					rowMap.remove("FIRSTDUEDATE");
					
					long id = insertData(rowMap);
					logMandateHistory((BigDecimal) rowMap.get("mandateid"), id);
					rowMap = null;
					return id;
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} 

		logger.debug(Literal.ENTERING);
		return null;
	}

	private long insertData(Map<String, Object> rowMap) {
		String sql = QueryUtil.getInsertQuery(rowMap.keySet(), "MANDATE_REQUESTS");
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			namedJdbcTemplate.update(sql, getMapSqlParameterSource(rowMap), keyHolder, new String[] { "id" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();
	}

	private String getSequence(String bankCode, Map<String, Integer> bankCodeSeq) {
		int seq = 0;
		if (bankCode == null || bankCodeSeq.get(bankCode) == null) {
			bankCodeSeq.put(bankCode, 0);
		} else {
			seq = bankCodeSeq.get(bankCode);
		}

		seq = seq + 1;
		bankCodeSeq.put(bankCode, seq);

		return StringUtils.trimToEmpty(bankCode) + "-" + seq;
	}

	private Map<String, Integer> getCountByProcessed() {
		final Map<String, Integer> bankCodeMap = new HashMap<>();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" Select BANK_CODE, count(*) From MANDATE_REQUESTS");
		sql.append(" Where EXTRACTION_DATE =:EXTRACTION_DATE");
		sql.append(" GROUP BY BANK_CODE");

		paramMap.addValue("EXTRACTION_DATE", getValueDate());

		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
					while (rs.next()) {
						bankCodeMap.put(rs.getString(1), rs.getInt(2));
					}
					return bankCodeMap;
				}

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}
		return bankCodeMap;
	}
	
	private void logMandateHistory(BigDecimal mandateId, long requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate,:fileID)");

		paramMap.addValue("mandateID", mandateId);

		paramMap.addValue("STATUS", "AC");
		paramMap.addValue("REASON", null);
		paramMap.addValue("changeDate", getAppDate());
		paramMap.addValue("fileID", requestId);

		this.namedJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}
	
	
	
	protected Mandate getMandateById(final long id) {
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
		} 
		
		logger.debug(Literal.LEAVING);
		return null;
	}
	
		
	private void updateMandateResponse(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("update MANDATE_RESPONSE");
		sql.append(" set REMARKS = :REMARKS , STATUS = :STATUS");
		sql.append(" where MANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());
		paramMap.addValue("REMARKS", respmandate.getReason());
		paramMap.addValue("STATUS", respmandate.getStatus());

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
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

		MANDATES_IMPORT.getDataEngineLogList().add(log);

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
	
	protected void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {
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

		if (!StringUtils.equals(mandate.getAccNumber(), respMandate.getAccNumber())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Account No.");
		}
		
		if (!StringUtils.equals(mandate.getMICR(), respMandate.getMICR())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("MICR Code");
		}
	}
	
	private void updateMandates(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		sql.append("Update Mandates");
		sql.append(" Set MANDATEREF = :MANDATEREF, STATUS = :STATUS, REASON = :REASON");
		sql.append("  Where MANDATEID = :MANDATEID AND ORGREFERENCE = :FINREFERENCE AND STATUS = :AC");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());

		if ("Y".equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
			paramMap.addValue("AC", "AC");
			paramMap.addValue("MANDATEREF", null);
			paramMap.addValue("FINREFERENCE", respmandate.getFinReference());
		} else {
			paramMap.addValue("STATUS", "APPROVED");
			paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
			paramMap.addValue("AC", "AC");
			paramMap.addValue("FINREFERENCE", respmandate.getFinReference());
			

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
	
	private void updateRemarks(long respBatchId, long approved, long rejected, long notMatched) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		StringBuilder remarks = new StringBuilder(MANDATES_IMPORT.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(approved);
		remarks.append(", Rejected: ");
		remarks.append(rejected);
		remarks.append(", Not Matched: ");
		remarks.append(notMatched);

		MANDATES_IMPORT.setRemarks(remarks.toString());

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
	
	protected  void processSecondaryMandate(Mandate respMandate){
		
	}
	
	protected  void processSwappedMandate(Mandate respMandate){
		
	}
	
	protected void addCustomParameter(Map<String, Object> parameterMap){
		
	}

}
