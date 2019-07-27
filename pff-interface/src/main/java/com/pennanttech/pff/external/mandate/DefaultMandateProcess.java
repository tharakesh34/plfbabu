/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AbstractMandateProcess.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-07-2017    														*
 *                                                                  						*
 * Modified Date    :  28-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-07-2017       Pennant	                 0.1                                            * 
 * 28-05-2018       Srikanth.m	             0.2          Add additional fields             * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.pff.external.mandate;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.TransactionStatus;
import org.zkoss.util.media.Media;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.model.mandate.MandateData;

public class DefaultMandateProcess extends AbstractInterface implements MandateProcesses {
	protected final Logger logger = Logger.getLogger(getClass());

	public DefaultMandateProcess() {
		super();
	}

	@Override
	public void sendReqest(MandateData mandateData) {

		long processId = mandateData.getProcess_Id();
		Date fromDate = mandateData.getFromDate();
		Date toDate = mandateData.getToDate();
		long userId = mandateData.getUserId();
		String userName = mandateData.getUserName();
		String selectedBranchs = mandateData.getSelectedBranchs();
		String entity = mandateData.getEntity();

		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		// filterMap.put("ID", mandates);
		// filterMap.put("MandateId", Arrays.asList(mandateIds));
		filterMap.put("PROCESS_ID", processId);
		filterMap.put("FROMDATE", fromDate);
		filterMap.put("TODATE", toDate);

		if (StringUtils.isNotBlank(selectedBranchs)) {
			filterMap.put("BRANCHCODE", Arrays.asList(selectedBranchs.split(",")));
		}

		parameterMap.put("USER_NAME", userName);
		parameterMap.put("ENTITY_CODE", entity);

		addCustomParameter(parameterMap);

		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		genetare(dataEngine, userName, filterMap, parameterMap);
	}

	/**
	 * @param userId
	 * @param userName
	 * @param filterMap
	 * @param parameterMap
	 * @throws Exception
	 */
	protected DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Map<String, Object> filterMap,
			Map<String, Object> parameterMap) {
		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		try {
			return dataEngine.exportData("MANDATES_EXPORT");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("MANDATES_EXPORT", e);
		}
	}

	@Override
	public void processResponseFile(long userId, File file, Media media, DataEngineStatus status) throws Exception {
		logger.debug(Literal.ENTERING);

		String configName = status.getName();

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Mandate response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				receiveResponse(status.getId(), status);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void receiveResponse(long respBatchId, DataEngineStatus status) throws Exception {
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<Mandate> mandates = null;
		RowMapper<Mandate> rowMapper = null;

		long approved = 0;
		long rejected = 0;
		long notMatched = 0;

		sql = new StringBuilder();
		sql.append(" SELECT MANDATEID, FINREFERENCE, CUSTCIF, MICR_CODE MICR, IFSC_CODE IFSC, ACCT_NUMBER AccNumber,");
		sql.append(" case when OPENFLAG = 'Y' THEN 'New Open ECS' ELSE 'No Open ECS' END lovValue,");
		sql.append(" MANDATE_TYPE MandateType, MANDATE_REG_NO mandateRef, STATUS, REMARKS reason");
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
								logger.warn(Literal.EXCEPTION, e);
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
			updateRemarks(respBatchId, approved, rejected, notMatched, status);
		}
	}

	protected Mandate getMandateById(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append(
				" SELECT ID RequestID, MandateID, FINREFERENCE, CUSTCIF,  MICR_CODE MICR, IFSC_CODE IFSC, ACCT_NUMBER AccNumber, OPENFLAG lovValue, MANDATE_TYPE MandateType, STATUS ");
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

	protected void updateMandateResponse(Mandate respmandate) {
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

	protected void logMandate(long respBatchId, Mandate respMandate) {
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

	protected void updateMandates(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		sql.append("Update Mandates");
		sql.append(" Set MANDATEREF = :MANDATEREF, STATUS = :STATUS, REASON = :REASON");
		sql.append("  Where MANDATEID = :MANDATEID");
		if (respmandate.getFinReference() == null) {
			sql.append(" AND ORGREFERENCE is NULL");
		} else {
			sql.append(" AND ORGREFERENCE = :FINREFERENCE");
		}
		sql.append(" AND STATUS = :AC");

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

	protected void logMandateHistory(Mandate respmandate, long requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate,:fileID)");

		paramMap.addValue("mandateID", respmandate.getMandateID());

		if ("Y".equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
		} else {
			paramMap.addValue("STATUS", "APPROVED");
		}

		paramMap.addValue("REASON", respmandate.getReason());
		paramMap.addValue("changeDate", SysParamUtil.getAppDate());
		paramMap.addValue("fileID", requestId);

		this.namedJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}

	protected void updateMandateRequest(Mandate respmandate, long id) {
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

	protected void updateRemarks(long respBatchId, long approved, long rejected, long notMatched,
			DataEngineStatus status) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		StringBuilder remarks = new StringBuilder(status.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(approved);
		remarks.append(", Rejected: ");
		remarks.append(rejected);
		remarks.append(", Not Matched: ");
		remarks.append(notMatched);

		status.setRemarks(remarks.toString());

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

	protected void processSecondaryMandate(Mandate respMandate) {

		boolean secondaryMandate = checkSecondaryMandate(respMandate.getMandateID());
		if (secondaryMandate) {
			makeSecondaryMandateInActive(respMandate.getMandateID());
			loanMandateSwapping(respMandate.getFinReference(), respMandate.getMandateID(),
					respMandate.getMandateType());

		}

	}

	protected void processSwappedMandate(Mandate respMandate) {

		boolean swappedMandate = checkSwappedMandate(respMandate.getMandateID());
		if (swappedMandate) {
			loanMandateSwapping(respMandate.getFinReference(), respMandate.getMandateID(),
					respMandate.getMandateType());

		}
	}

	private boolean checkSecondaryMandate(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT Count(*) FROM MANDATES");
		selectSql.append(" WHERE PRIMARYMANDATEID = :PRIMARYMANDATEID AND ACTIVE = :ACTIVE");
		paramMap.addValue("PRIMARYMANDATEID", mandateID);
		paramMap.addValue("ACTIVE", 1);

		try {
			if (namedJdbcTemplate.queryForObject(selectSql.toString(), paramMap, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	private boolean checkSwappedMandate(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT SWAPISACTIVE  FROM MANDATES");
		selectSql.append(" WHERE MANDATEID = :MANDATEID");
		paramMap.addValue("MANDATEID", mandateID);

		try {
			return namedJdbcTemplate.queryForObject(selectSql.toString(), paramMap, Boolean.class);
		} catch (Exception e) {
			throw e;
		}
	}

	private void loanMandateSwapping(String finReference, long mandateId, String repayMethod) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(" Set MandateID =:MandateID ");
		sql.append(" ,FinRepayMethod =:FinRepayMethod");
		sql.append(" Where FinReference =:FinReference");

		source.addValue("MandateID", mandateId);
		source.addValue("FinReference", finReference);
		source.addValue("FinRepayMethod", repayMethod);

		try {
			namedJdbcTemplate.update(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug("updateSql: " + source.toString());

	}

	private void makeSecondaryMandateInActive(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MANDATES SET ACTIVE = :ACTIVE WHERE  PRIMARYMANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", mandateID);
		paramMap.addValue("ACTIVE", 0);

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
	}

	protected void addCustomParameter(Map<String, Object> parameterMap) {

	}

	public boolean registerMandate(Mandate mandate) throws Exception {
		return false;
	}

	public void updateMandateStatus() throws Exception {

	}

	public void processMandateResponse() throws Exception {

	}

}
