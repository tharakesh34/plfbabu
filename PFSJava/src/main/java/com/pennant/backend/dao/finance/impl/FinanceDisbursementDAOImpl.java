/**
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceDisbursement model</b> class.<br>
 * 
 */

public class FinanceDisbursementDAOImpl extends BasicDao<FinanceDisbursement> implements FinanceDisbursementDAO {
	private static Logger logger = LogManager.getLogger(FinanceDisbursementDAOImpl.class);

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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());
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
			sql.append(
					" ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate,  instructionUID , QuickDisb,InstCalReq,LinkedDisbId");
			sql.append(" From FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");

		FinanceDisbursementRowMapper rowMapper = new FinanceDisbursementRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
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
			insertSql.append(
					" ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate, instructionUID,InstCalReq,LinkedDisbId,");
		}
		insertSql.append(" DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAccountId, :DisbAmount,:DisbReqDate, :FeeChargeAmt,:InsuranceAmt,");
		if (!isWIF) {
			insertSql.append(
					" :DisbStatus, :DisbType, :DisbClaim, :DisbExpType, :ContractorId, :DisbRetPerc, :DisbRetAmount, ");
			insertSql.append(" :AutoDisb, :NetAdvDue, :NetRetDue, :DisbRetPaid, :RetPaidDate, ");
			insertSql.append(
					" :ConsultFeeFrq, :ConsultFeeStartDate, :ConsultFeeEndDate, :instructionUID,:InstCalReq,:LinkedDisbId,");
		}
		insertSql.append(" :DisbIsActive, :DisbRemarks, :Version , :LastMntBy, ");
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
			insertSql.append(
					" ConsultFeeFrq, ConsultFeeStartDate, ConsultFeeEndDate,LinkedTranId, instructionUID,InstCalReq,LinkedDisbId, ");
			if (type.contains("Log")) {
				insertSql.append(" LogKey , ");
			}
		}
		insertSql.append(" DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAccountId, :DisbAmount,:DisbReqDate, :FeeChargeAmt,:InsuranceAmt,");
		if (!isWIF) {
			insertSql.append(
					" :DisbStatus, :QuickDisb ,:DisbType, :DisbClaim, :DisbExpType, :ContractorId, :DisbRetPerc, :DisbRetAmount, ");
			insertSql.append(" :AutoDisb, :NetAdvDue, :NetRetDue, :DisbRetPaid, :RetPaidDate, ");
			insertSql.append(
					" :ConsultFeeFrq, :ConsultFeeStartDate, :ConsultFeeEndDate,:LinkedTranId, :instructionUID,:InstCalReq,:LinkedDisbId, ");
			if (type.contains("Log")) {
				insertSql.append(" :LogKey , ");
			}
		}
		insertSql.append("  :DisbIsActive, :DisbRemarks, :Version , :LastMntBy, ");
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
					" ConsultFeeFrq=:ConsultFeeFrq, ConsultFeeStartDate=:ConsultFeeStartDate, ConsultFeeEndDate=:ConsultFeeEndDate,  instructionUID=:instructionUID,InstCalReq=:InstCalReq,LinkedDisbId=:LinkedDisbId, ");
		}
		updateSql.append(" DisbReqDate = :DisbReqDate, DisbIsActive = :DisbIsActive,");
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
	public int updateBatchDisb(List<FinanceDisbursement> fdList, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinDisbursementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LinkedTranId = ? ");
		sql.append(" Where FinReference = ?  And DisbDate = ? And DisbSeq = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceDisbursement fd = fdList.get(i);
				int index = 1;
				ps.setLong(index++, fd.getLinkedTranId());
				ps.setString(index++, fd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(fd.getDisbDate()));
				ps.setInt(index++, fd.getDisbSeq());
			}

			@Override
			public int getBatchSize() {
				return fdList.size();
			}
		}).length;
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
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append("  Where FinReference = ?");

		FinanceDisbursementRowMapper rowMapper = new FinanceDisbursementRowMapper(type, isWIF);

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, id);
		}, rowMapper);
	}

	private StringBuilder getSqlQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, DisbDate, DisbSeq, DisbDesc, FeeChargeAmt, InsuranceAmt, DisbAccountId");
		sql.append(", DisbAmount, DisbReqDate, DisbIsActive, DisbRemarks");

		if (!isWIF) {
			sql.append(", DisbStatus, DisbType");
			sql.append(", AutoDisb, instructionUID, QuickDisb,InstCalReq,LinkedDisbId ");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		return sql;
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinReference = ? and LogKey = ?");

		FinanceDisbursementRowMapper rowMapper = new FinanceDisbursementRowMapper(type, isWIF);

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, id);
					ps.setLong(index++, logKey);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public List<FinanceDisbursement> getDisbursementToday(String finRefernce, Date disbDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, DisbDate, DisbSeq, FeeChargeAmt, InsuranceAmt, DisbAmount, DisbDate");
		sql.append(" From FinDisbursementDetails");
		sql.append(" Where FinReference = ? and DisbDate = ?");
		sql.append(" and (DisbStatus is null or DisbStatus != ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> {
				int index = 1;
				ps.setString(index++, finRefernce);
				ps.setDate(index++, JdbcUtil.getDate(disbDate));
				ps.setString(index++, FinanceConstants.DISB_STATUS_CANCEL);

			}, (rs, rowNum) -> {
				FinanceDisbursement fd = new FinanceDisbursement();

				fd.setFinReference(rs.getString("FinReference"));
				fd.setDisbDate(rs.getTimestamp("DisbDate"));
				fd.setDisbSeq(rs.getInt("DisbSeq"));
				fd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
				fd.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
				fd.setDisbAmount(rs.getBigDecimal("DisbAmount"));

				return fd;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not found in FinDisbursementDetails for the specified FinReference {} and DisbDate {}",
					finRefernce, disbDate);
		}

		return new ArrayList<>();
	}

	@Override
	public List<FinanceDisbursement> getDMFinanceDisbursementDetails(String id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, DisbDate, DisbSeq, DisbDesc, FeeChargeAmt, InsuranceAmt, DisbAmount");
		sql.append(", DisbReqDate, DisbIsActive, DisbRemarks, DisbStatus, AutoDisb");
		sql.append(", LastMntBy, LastMntOn, QuickDisb");
		sql.append(" from FinDisbursementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");
		sql.append(" Order by DisbDate");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, id);
				}
			}, new RowMapper<FinanceDisbursement>() {
				@Override
				public FinanceDisbursement mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceDisbursement finDisb = new FinanceDisbursement();

					finDisb.setFinReference(rs.getString("FinReference"));
					finDisb.setDisbDate(rs.getTimestamp("DisbDate"));
					finDisb.setDisbSeq(rs.getInt("DisbSeq"));
					finDisb.setDisbDesc(rs.getString("DisbDesc"));
					finDisb.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					finDisb.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
					finDisb.setDisbAmount(rs.getBigDecimal("DisbAmount"));
					finDisb.setDisbReqDate(rs.getTimestamp("DisbReqDate"));
					finDisb.setDisbIsActive(rs.getBoolean("DisbIsActive"));
					finDisb.setDisbRemarks(rs.getString("DisbRemarks"));
					finDisb.setDisbStatus(rs.getString("DisbStatus"));
					finDisb.setAutoDisb(rs.getBoolean("AutoDisb"));
					finDisb.setLastMntBy(rs.getLong("LastMntBy"));
					finDisb.setLastMntOn(rs.getTimestamp("LastMntOn"));
					finDisb.setQuickDisb(rs.getBoolean("QuickDisb"));

					return finDisb;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<Integer> getFinanceDisbSeqs(String finReferecne, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select DisbSeq");
		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finReferecne),
				(rs, rowNum) -> rs.getInt(1));
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

		RowMapper<FinanceDisbursement> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceDisbursement.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	private class FinanceDisbursementRowMapper implements RowMapper<FinanceDisbursement> {
		private String type;
		private boolean wIf;

		private FinanceDisbursementRowMapper(String type, boolean wIf) {
			this.type = type;
			this.wIf = wIf;
		}

		@Override
		public FinanceDisbursement mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceDisbursement finDisb = new FinanceDisbursement();

			finDisb.setFinReference(rs.getString("FinReference"));
			finDisb.setDisbDate(rs.getTimestamp("DisbDate"));
			finDisb.setDisbSeq(rs.getInt("DisbSeq"));
			finDisb.setDisbDesc(rs.getString("DisbDesc"));
			finDisb.setDisbAccountId(rs.getString("DisbAccountId"));
			finDisb.setDisbAmount(rs.getBigDecimal("DisbAmount"));
			finDisb.setDisbReqDate(rs.getTimestamp("DisbReqDate"));
			finDisb.setDisbIsActive(rs.getBoolean("DisbIsActive"));
			finDisb.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			finDisb.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
			finDisb.setDisbRemarks(rs.getString("DisbRemarks"));
			finDisb.setVersion(rs.getInt("Version"));
			finDisb.setLastMntBy(rs.getLong("LastMntBy"));
			finDisb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			finDisb.setRecordStatus(rs.getString("RecordStatus"));
			finDisb.setRoleCode(rs.getString("RoleCode"));
			finDisb.setNextRoleCode(rs.getString("NextRoleCode"));
			finDisb.setTaskId(rs.getString("TaskId"));
			finDisb.setNextTaskId(rs.getString("NextTaskId"));
			finDisb.setRecordType(rs.getString("RecordType"));
			finDisb.setWorkflowId(rs.getLong("WorkflowId"));

			if (!wIf) {
				finDisb.setDisbStatus(rs.getString("DisbStatus"));
				finDisb.setDisbType(rs.getString("DisbType"));
				finDisb.setAutoDisb(rs.getBoolean("AutoDisb"));
				finDisb.setInstructionUID(rs.getLong("instructionUID"));
				finDisb.setQuickDisb(rs.getBoolean("QuickDisb"));
				finDisb.setInstCalReq(rs.getBoolean("InstCalReq"));
				finDisb.setLinkedDisbId(rs.getLong("LinkedDisbId"));
			}

			return finDisb;
		}
	}

	@Override
	public int getFinDsbursmntInstrctnIds(long instructionUid) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select count(INSTRUCTIONUID) from FinDisbursementDetails  where INSTRUCTIONUID = :INSTRUCTIONUID");
		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("INSTRUCTIONUID", instructionUid);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}
}
