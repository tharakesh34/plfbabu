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
 * FileName    		:  FinanceDisbursementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceDisbursement model</b> class.<br>
 * 
 */

public class FinanceDisbursementDAOImpl extends BasicDao<FinanceDisbursement> implements FinanceDisbursementDAO {
	private static Logger logger = Logger.getLogger(FinanceDisbursementDAOImpl.class);

	public FinanceDisbursementDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Finance Disbursement Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceDisbursement
	 */
	@Override
	public FinanceDisbursement getFinanceDisbursementById(final String id, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		financeDisbursement.setId(id);

		StringBuilder sql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, DisbDesc, ");
		sql.append(" DisbAccountId, DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive , FeeChargeAmt,InsuranceAmt,");
		sql.append(" DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			if (!isWIF) {
				sql.append(" , lovDescDisbExpType ");
			}
		}
		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" ,DisbStatus, DisbType, DisbClaim, DisbExpType, ContractorId, DisbRetPerc, DisbRetAmount, ");
			sql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");
			sql.append(" ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate,  instructionUID , QuickDisb");
			sql.append(" From FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);

		try {
			financeDisbursement = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeDisbursement = null;
		}
		logger.debug("Leaving");
		return financeDisbursement;
	}

	/**
	 * This method Deletes the Record from the FinDisbursementDetails or FinDisbursementDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Disbursement Details by key FinReference
	 * 
	 * @param Finance
	 *            Disbursement Details (financeDisbursement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByFinReference(String id, String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		financeDisbursement.setId(id);

		StringBuilder deleteSql = new StringBuilder("Delete From ");

		if (isWIF) {
			deleteSql.append(" WIFFinDisbursementDetails");
		} else {
			deleteSql.append(" FinDisbursementDetails");
		}

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		if (logKey != 0) {
			deleteSql.append(" AND LogKey =:LogKey");
		}

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the FinDisbursementDetails or FinDisbursementDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Disbursement Details by key FinReference
	 * 
	 * @param Finance
	 *            Disbursement Details (financeDisbursement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceDisbursement financeDisbursement, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ");

		if (isWIF) {
			deleteSql.append(" WIFFinDisbursementDetails");
		} else {
			deleteSql.append(" FinDisbursementDetails");
		}

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and DisbDate = :DisbDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
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

	/**
	 * This method insert new Records into FinDisbursementDetails or FinDisbursementDetails_Temp.
	 * 
	 * save Finance Disbursement Details
	 * 
	 * @param Finance
	 *            Disbursement Details (financeDisbursement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceDisbursement financeDisbursement, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFFinDisbursementDetails");
		} else {
			insertSql.append(" FinDisbursementDetails");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference, DisbDate, DisbSeq, DisbDesc, DisbAccountId, DisbAmount, DisbReqDate, FeeChargeAmt,InsuranceAmt,");
		if (!isWIF) {
			insertSql.append(
					" DisbStatus, DisbType, DisbClaim, DisbExpType, ContractorId, DisbRetPerc, DisbRetAmount, ");
			insertSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");
			insertSql.append(" ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate, instructionUID,");
		}
		insertSql.append(" DisbDisbursed, DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAccountId, :DisbAmount,:DisbReqDate, :FeeChargeAmt,:InsuranceAmt,");
		if (!isWIF) {
			insertSql.append(
					" :DisbStatus, :DisbType, :DisbClaim, :DisbExpType, :ContractorId, :DisbRetPerc, :DisbRetAmount, ");
			insertSql.append(" :AutoDisb, :NetAdvDue, :NetRetDue, :DisbRetPaid, :RetPaidDate, ");
			insertSql.append(" :ConsultFeeFrq, :ConsultFeeStartDate, :ConsultFeeEndDate, :instructionUID,");
		}
		insertSql.append(" :DisbDisbursed, :DisbIsActive, :DisbRemarks, :Version , :LastMntBy, ");
		insertSql.append(" :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		insertSql.append(" :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeDisbursement.getId();
	}

	/**
	 * This method inserts List of Records into FinDisbursementDetails or FinDisbursementDetails_Temp.
	 * 
	 * save Finance Disbursement Details
	 * 
	 * @param Finance
	 *            Disbursement Details (financeDisbursement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void saveList(List<FinanceDisbursement> financeDisbursement, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFFinDisbursementDetails");
		} else {
			insertSql.append(" FinDisbursementDetails");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (FinReference, DisbDate, DisbSeq, DisbDesc, DisbAccountId, DisbAmount, DisbReqDate, FeeChargeAmt,InsuranceAmt,");
		if (!isWIF) {
			insertSql.append(
					" DisbStatus, QuickDisb, DisbType, DisbClaim, DisbExpType, ContractorId, DisbRetPerc, DisbRetAmount, ");
			insertSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");
			insertSql.append(" ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate,LinkedTranId, instructionUID, ");
			if (type.contains("Log")) {
				insertSql.append(" LogKey , ");
			}
		}
		insertSql.append(" DisbDisbursed, DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAccountId, :DisbAmount,:DisbReqDate, :FeeChargeAmt,:InsuranceAmt,");
		if (!isWIF) {
			insertSql.append(
					" :DisbStatus, :QuickDisb ,:DisbType, :DisbClaim, :DisbExpType, :ContractorId, :DisbRetPerc, :DisbRetAmount, ");
			insertSql.append(" :AutoDisb, :NetAdvDue, :NetRetDue, :DisbRetPaid, :RetPaidDate, ");
			insertSql.append(
					" :ConsultFeeFrq, :ConsultFeeStartDate, :ConsultFeeEndDate,:LinkedTranId, :instructionUID, ");
			if (type.contains("Log")) {
				insertSql.append(" :LogKey , ");
			}
		}
		insertSql.append("  :DisbDisbursed, :DisbIsActive, :DisbRemarks, :Version , :LastMntBy, ");
		insertSql.append(" :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		insertSql.append(" :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeDisbursement.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record FinDisbursementDetails or FinDisbursementDetails_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Finance Disbursement Details by key FinReference and Version
	 * 
	 * @param Finance
	 *            Disbursement Details (financeDisbursement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceDisbursement financeDisbursement, String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");
		if (isWIF) {
			updateSql.append(" WIFFinDisbursementDetails");
		} else {
			updateSql.append(" FinDisbursementDetails");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set DisbDesc = :DisbDesc,DisbAccountId = :DisbAccountId, DisbAmount = :DisbAmount, FeeChargeAmt=:FeeChargeAmt,InsuranceAmt=:InsuranceAmt, ");
		if (!isWIF) {
			updateSql.append(
					" DisbStatus=:DisbStatus, DisbType=:DisbType, DisbClaim=:DisbClaim, DisbExpType=:DisbExpType, ");
			updateSql.append(" ContractorId=:ContractorId, DisbRetPerc=:DisbRetPerc, DisbRetAmount=:DisbRetAmount, ");
			updateSql.append(
					" AutoDisb=:AutoDisb, NetAdvDue=:NetAdvDue, NetRetDue=:NetRetDue, DisbRetPaid=:DisbRetPaid, RetPaidDate=:RetPaidDate, ");
			updateSql.append(
					" ConsultFeeFrq=:ConsultFeeFrq, ConsultFeeStartDate=:ConsultFeeStartDate, ConsultFeeEndDate=:ConsultFeeEndDate,  instructionUID=:instructionUID, ");
		}
		updateSql.append(" DisbReqDate = :DisbReqDate, DisbDisbursed = :DisbDisbursed, DisbIsActive = :DisbIsActive,");
		updateSql.append(
				" DisbRemarks = :DisbRemarks, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference and DisbDate = :DisbDate AND DisbSeq = :DisbSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateBatchDisb(FinanceDisbursement financeDisbursement, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinDisbursementDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DisbDisbursed = :DisbDisbursed , LinkedTranId=:LinkedTranId");
		updateSql.append(" Where FinReference =:FinReference and DisbDate = :DisbDate AND DisbSeq = :DisbSeq");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method updates the LinkedTranId
	 * 
	 * @param Finance
	 *            Disbursement Details (financeDisbursement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void updateLinkedTranId(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");
		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		financeDisbursement.setFinReference(finReference);
		financeDisbursement.setLinkedTranId(linkedTranId);

		StringBuilder updateSql = new StringBuilder("Update FinDisbursementDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set LinkedTranId = :LinkedTranId");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Fetch the List of Finance Disbursement Detail Records by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public List<FinanceDisbursement> getFinanceDisbursementDetails(final String id, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		wIFFinanceDisbursement.setId(id);

		StringBuilder sql = new StringBuilder();
		sql.append("select FinReference, DisbDate, DisbSeq, DisbDesc, FeeChargeAmt, InsuranceAmt");
		sql.append(", DisbAccountId, DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive, DisbRemarks");
		if (!isWIF) {
			sql.append(", DisbStatus, DisbType, DisbClaim, DisbExpType, ContractorId, DisbRetPerc, DisbRetAmount");
			sql.append(", AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate");
			sql.append(", ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate, instructionUID , QuickDisb ");
		}

		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			if (!isWIF) {
				sql.append(", lovDescDisbExpType");
			}
		}

		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the List of Finance Disbursement Detail Records by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public List<FinanceDisbursement> getFinanceDisbursementDetails(final String id, String type, boolean isWIF,
			long logKey) {
		logger.debug("Entering");

		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		wIFFinanceDisbursement.setId(id);
		wIFFinanceDisbursement.setLogKey(logKey);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, DisbDate, DisbSeq, DisbDesc,FeeChargeAmt,InsuranceAmt, ");
		selectSql.append(" DisbAccountId, DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive, DisbRemarks,");
		if (!isWIF) {
			selectSql.append(
					" DisbStatus, DisbType, DisbClaim, DisbExpType, ContractorId, DisbRetPerc, DisbRetAmount, ");
			selectSql.append(
					" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate,ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate,  instructionUID, QuickDisb");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			if (!isWIF) {
				selectSql.append(" , lovDescDisbExpType ");
			}
		}

		if (isWIF) {
			selectSql.append(" From WIFFinDisbursementDetails");
		} else {
			selectSql.append(" From FinDisbursementDetails");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND LogKey =:LogKey");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinanceDisbursement> getDisbursementToday(String finRefernce, Date disbDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finRefernce);
		source.addValue("DisbDate", disbDate);
		source.addValue("DisbStatus", FinanceConstants.DISB_STATUS_CANCEL);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, DisbDate, DisbSeq, FeeChargeAmt, InsuranceAmt, ");
		selectSql.append("  DisbAmount, DisbDate ");
		selectSql.append(" From FinDisbursementDetails");
		selectSql.append(" Where FinReference =:FinReference AND DisbDate = :DisbDate");
		selectSql.append(" AND (DisbStatus IS NULL OR DisbStatus != :DisbStatus )");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);

		List<FinanceDisbursement> todayDisbs = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return todayDisbs;
	}

	@Override
	public List<FinanceDisbursement> getDMFinanceDisbursementDetails(String id, String type) {

		//Copied from getFinanceDisbursementDetails and removed unwanted fields
		logger.debug("Entering");

		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		wIFFinanceDisbursement.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, DisbDate, DisbSeq, DisbDesc,FeeChargeAmt,InsuranceAmt, ");
		selectSql.append(" DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive, DisbRemarks,");
		selectSql.append(" DisbStatus, AutoDisb, LastMntBy, LastMntOn ,QuickDisb ");
		selectSql.append(" From FinDisbursementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference Order by DisbDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<Integer> getFinanceDisbSeqs(String finReferecne, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DisbSeq ");
		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReferecne);

		try {
			return this.jdbcTemplate.queryForList(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<FinanceDisbursement> getDeductDisbFeeDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinEvent", Arrays.asList(
				new String[] { AccountEventConstants.ACCEVENT_ADDDBSN, AccountEventConstants.ACCEVENT_ADDDBSP }));

		StringBuilder sql = new StringBuilder();
		sql.append(" Select F.FinReference, D.DisbSeq");
		sql.append(", SUM(F.ACTUALAMOUNT - F.WAIVEDAMOUNT - F.PAIDAMOUNT) DeductFeeDisb");
		sql.append(" from FINFEEDETAIL_TEMP F");
		sql.append(" INNER JOIN FINDISBURSEMENTDETAILS_TEMP D ON D.InstructionUID =  F.InstructionUID");
		sql.append(" where F.FinReference = :FinReference and F.FinEvent in (:FinEvent)");
		sql.append(" GROUP BY F.FinReference, D.DisbSeq");
		sql.append(" UNION ALL");
		sql.append(" Select F.FinReference, D.DisbSeq");
		sql.append(", SUM(F.ACTUALAMOUNT - F.WAIVEDAMOUNT - F.PAIDAMOUNT) DeductFeeDisb");
		sql.append(" from FINFEEDETAIL F");
		sql.append(" INNER JOIN FINDISBURSEMENTDETAILS D ON D.InstructionUID =  F.InstructionUID");
		sql.append(" where F.FinReference = :FinReference and F.FinEvent in (:FinEvent)");
		sql.append(" GROUP BY F.FinReference, D.DisbSeq");

		logger.debug("selectSql: " + sql.toString());

		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

}