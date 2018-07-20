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
 * FileName    		:  DocumentDetailsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2012    														*
 *                                                                  						*
 * Modified Date    :  21-06-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.insurancedetails.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>documentDetails model</b> class.<br>
 */
public class FinInsurancesDAOImpl extends SequenceDao<FinInsurances> implements FinInsurancesDAO {
	private static Logger logger = Logger.getLogger(FinInsurancesDAOImpl.class);

	public FinInsurancesDAOImpl() {
		super();
	}

	@Override
	public FinInsurances getFinInsuranceByID(FinInsurances finInsurance, String type, boolean isWIF) {

		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT InsId,Reference,Module, InsuranceType, InsReference, ");
		selectSql.append(
				" InsuranceReq, Provider,PaymentMethod,CalType,InsuranceRate,WaiverReason,InsuranceFrq,Amount,CalRule,CalPerc,CalOn,InsuranceStatus,PolicyCode,");
		if (type.contains("View")) {
			selectSql.append("PolicyDesc,InsuranceTypeDesc,ProviderName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (!isWIF) {
			selectSql.append(" FROM FinInsurances");
		} else {
			selectSql.append(" FROM WIFFinInsurances");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference = :Reference AND InsId = :InsId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		RowMapper<FinInsurances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinInsurances.class);

		try {
			finInsurance = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finInsurance = null;
		}
		logger.debug("Leaving");
		return finInsurance;

	}

	@Override
	public List<FinInsurances> getFinInsuranceListByRef(String finReference, String type, boolean isWIF) {

		logger.debug("Entering");
		FinInsurances finInsurance = new FinInsurances();
		finInsurance.setReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT InsId,Reference,Module, InsuranceType, InsReference, ");
		selectSql.append(
				" InsuranceReq, Provider,PaymentMethod,CalType,InsuranceRate,WaiverReason,InsuranceFrq,Amount,CalRule,CalPerc,CalOn,InsuranceStatus,PolicyCode,");
		if (type.contains("View")) {
			selectSql.append("PolicyDesc, InsuranceTypeDesc,ProviderName,");
		}
		selectSql.append("Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (!isWIF) {
			selectSql.append(" FROM FinInsurances");
		} else {
			selectSql.append(" FROM WIFFinInsurances");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference = :Reference");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		RowMapper<FinInsurances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinInsurances.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public void update(FinInsurances finInsurance, String type, boolean isWIF) {

		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update ");
		if (!isWIF) {
			updateSql.append("FinInsurances");
		} else {
			updateSql.append("WIFFinInsurances");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Reference = :Reference, Module=:Module, InsuranceType = :InsuranceType, ");
		updateSql.append(" InsuranceReq = :InsuranceReq, Provider = :Provider,PaymentMethod = :PaymentMethod,");
		updateSql.append(" CalType = :CalType,InsuranceRate =:InsuranceRate,InsReference =:InsReference,");
		updateSql.append(" WaiverReason=:WaiverReason,InsuranceFrq=:InsuranceFrq,Amount=:Amount,");
		updateSql.append(
				" CalRule=:CalRule,CalPerc=:CalPerc,CalOn=:CalOn,InsuranceStatus=:InsuranceStatus, PolicyCode=:PolicyCode,");
		updateSql.append(" Version=:Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where InsId=:InsId ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");

	}

	@Override
	public long save(FinInsurances finInsurance, String type, boolean isWIF) {
		logger.debug("Entering ");
		if (finInsurance.getInsId() == Long.MIN_VALUE) {
			finInsurance.setId(getNextValue("SeqFinInsurance"));
			logger.debug("get NextID:" + finInsurance.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (!isWIF) {
			insertSql.append("FinInsurances");
		} else {
			insertSql.append("WIFFinInsurances");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (InsId,Reference,Module, InsuranceType, Provider,insuranceReq,");
		insertSql.append(
				" PaymentMethod, CalType,InsuranceRate,InsReference,WaiverReason,InsuranceFrq,Amount,CalRule,CalPerc,CalOn,InsuranceStatus,PolicyCode,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:InsId,:Reference, :Module, :InsuranceType, :Provider,:insuranceReq,");
		insertSql.append(
				" :PaymentMethod, :CalType, :InsuranceRate, :InsReference, :WaiverReason,:InsuranceFrq,:Amount,:CalRule,:CalPerc,:CalOn,:InsuranceStatus,:PolicyCode,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finInsurance.getId();
	}

	@Override
	public void delete(FinInsurances finInsurance, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (!isWIF) {
			deleteSql.append("FinInsurances");
		} else {
			deleteSql.append("WIFFinInsurances");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where Reference =:Reference And InsReference =:InsReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void deleteFinInsurancesList(String finReference, boolean isWIF, String tableType) {

		FinInsurances finInsurance = new FinInsurances();
		finInsurance.setReference(finReference);

		StringBuilder deleteSql = new StringBuilder();
		if (isWIF) {
			deleteSql.append(" DELETE FROM WIFFinInsurances");
		} else {
			deleteSql.append(" DELETE FROM FinInsurances");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE Reference =:Reference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void deleteByFinReference(String finReference, String type) {
		logger.debug("Entering");
		FinInsurances finInsurance = new FinInsurances();
		finInsurance.setReference(finReference);
		StringBuilder deleteSql = new StringBuilder("Delete From FinInsurances");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference =:Reference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void saveFreqBatch(List<FinSchFrqInsurance> frqList, boolean isWIF, String tableType) {

		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		if (isWIF) {
			insertSql.append(" INSERT INTO WIFFinSchFrqInsurance");
		} else {
			insertSql.append(" INSERT INTO FinSchFrqInsurance");
		}
		insertSql.append(StringUtils.trimToEmpty(tableType));

		insertSql.append(" (InsId, InsSchDate, InsuranceRate , InsuranceFrq ,Amount,InsurancePaid,");
		insertSql.append(
				" Version , LastMntBy , LastMntOn ,RecordStatus ,RoleCode ,NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId) ");

		insertSql.append(" VALUES (:InsId, :InsSchDate, :InsuranceRate , :InsuranceFrq ,:Amount,");
		insertSql.append(
				" :Version ,:LastMntBy ,:LastMntOn ,:RecordStatus ,:RoleCode ,:NextRoleCode,:TaskId,:NextTaskId, :RecordType,:WorkflowId ) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(frqList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void deleteFreqBatch(long insId, boolean isWIF, String tableType) {
		logger.debug("Entering");

		FinSchFrqInsurance finSchFrqInsurance = new FinSchFrqInsurance();
		finSchFrqInsurance.setInsId(insId);

		StringBuilder deleteSql = new StringBuilder();
		if (isWIF) {
			deleteSql.append(" DELETE FROM WIFFinSchFrqInsurance");
		} else {
			deleteSql.append(" DELETE FROM FinSchFrqInsurance");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE InsId =:InsId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchFrqInsurance);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinSchFrqInsurance> getFinSchFrqInsuranceFinRef(String finReference, boolean isWIF, String tableType) {

		logger.debug("Entering");

		FinSchFrqInsurance finSchFrqInsurance = new FinSchFrqInsurance();
		finSchFrqInsurance.setReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT InsuranceType,Reference,Module,Insreference,InsId, InsSchDate , InsuranceRate , InsuranceFrq ,ClosingBalance, ");
		selectSql.append(
				" Amount ,InsurancePaid, Version , LastMntBy , LastMntOn ,RecordStatus ,RoleCode ,NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId ");
		if (isWIF) {
			selectSql.append(" FROM WIFFinSchFrqInsurance");
		} else {
			selectSql.append(" FROM FinSchFrqInsurance");
		}

		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE Reference=:Reference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchFrqInsurance);
		RowMapper<FinSchFrqInsurance> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinSchFrqInsurance.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public List<FinInsurances> getInsurancesList(String insuranceType, String tableType) {

		logger.debug("Entering");
		FinInsurances finInsurance = new FinInsurances();
		finInsurance.setInsuranceType(insuranceType);

		StringBuilder selectSql = new StringBuilder("SELECT InsId,Reference,Module, InsuranceType, InsReference, ");
		selectSql.append(
				" InsuranceReq, Provider,PaymentMethod,CalType,InsuranceRate,WaiverReason,InsuranceFrq,Amount,CalRule,CalPerc,CalOn,InsuranceStatus,");
		if (tableType.contains("View")) {
			selectSql.append(" InsuranceTypeDesc,ProviderName,");
		}
		selectSql.append("Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM FinInsurances");

		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" Where InsuranceType = :InsuranceType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finInsurance);
		RowMapper<FinInsurances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinInsurances.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void updateInsSchdPaids(List<FinSchFrqInsurance> updateInsList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinSchFrqInsurance");
		updateSql.append(" Set InsurancePaid = InsurancePaid + :InsurancePaid ");
		updateSql.append(" Where InsId =:InsId AND InsSchDate=:InsSchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateInsList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinSchFrqInsurance> getInsScheduleBySchDate(String finReference, Date schDate) {
		logger.debug("Entering");
		FinSchFrqInsurance insSchd = new FinSchFrqInsurance();
		insSchd.setReference(finReference);
		insSchd.setInsSchDate(schDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT InsId, InsSchDate, InsurancePaid ");
		selectSql.append(" FROM FinSchFrqInsurance ");
		selectSql.append(" WHERE  Reference = :Reference AND InsSchDate=:InsSchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insSchd);
		RowMapper<FinSchFrqInsurance> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinSchFrqInsurance.class);
		List<FinSchFrqInsurance> insList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return insList;
	}

	@Override
	public List<FinSchFrqInsurance> getInsSchdToPost(String finReference, Date schDate) {
		logger.debug("Entering");
		FinSchFrqInsurance insSchd = new FinSchFrqInsurance();
		insSchd.setReference(finReference);
		insSchd.setInsSchDate(schDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FIN.REFERENCE, INSD.INSSCHDATE, FIN.INSURANCETYPE, ");
		selectSql.append(" INSD.AMOUNT, INSD.INSURANCEPAID  FROM FINSCHFRQINSURANCE INSD");
		selectSql.append(" INNER JOIN FININSURANCES FIN ON INSD.INSID=FIN.INSID");
		selectSql.append(" WHERE FIN.REFERENCE = :Reference AND INSD.INSSCHDATE=:InsSchDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(insSchd);
		RowMapper<FinSchFrqInsurance> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinSchFrqInsurance.class);
		List<FinSchFrqInsurance> insList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return insList;
	}

	@Override
	public void updateInsPaids(List<FinSchFrqInsurance> updateInsList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinSchFrqInsurance");
		updateSql.append(" Set InsurancePaid = :InsurancePaid ");
		updateSql.append(" Where InsId =:InsId AND InsSchDate=:InsSchDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateInsList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}