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
 * FileName    		:  JountAccountDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>JountAccountDetail model</b> class.<br>
 * 
 */

public class JountAccountDetailDAOImpl extends SequenceDao<JointAccountDetail> implements JountAccountDetailDAO {
	private static Logger logger = LogManager.getLogger(JountAccountDetailDAOImpl.class);

	public JountAccountDetailDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new JountAccountDetail
	 * 
	 * @return JountAccountDetail
	 */

	@Override
	public JointAccountDetail getJountAccountDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("JountAccountDetail");
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		if (workFlowDetails != null) {
			jountAccountDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}

	/**
	 * This method get the module from method getJountAccountDetail() and set the new record flag as true and return
	 * JountAccountDetail()
	 * 
	 * @return JountAccountDetail
	 */

	@Override
	public JointAccountDetail getNewJountAccountDetail() {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = getJountAccountDetail();
		jountAccountDetail.setNewRecord(true);
		logger.debug("Leaving");
		return jountAccountDetail;
	}

	/**
	 * Fetch the Record Jount Account Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JountAccountDetail
	 */
	@Override
	public JointAccountDetail getJountAccountDetailById(final long id, String type) {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = new JointAccountDetail();

		jountAccountDetail.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId,CatOfcoApplicant, AuthoritySignatory, Sequence, IncludeIncome");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",LovDescCIFName, custID, lovCustDob ");
		}
		selectSql.append(" From FinJointAccountDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where JointAccountId =:JointAccountId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(JointAccountDetail.class);

		try {
			jountAccountDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jountAccountDetail = null;
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}

	/**
	 * This method Deletes the Record from the FinJointAccountDetails or FinJointAccountDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Jount Account Details by key JointAccountId
	 * 
	 * @param Jount
	 *            Account Details (jountAccountDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(JointAccountDetail jountAccountDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinJointAccountDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where JointAccountId =:JointAccountId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
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
	 * This method insert new Records into FinJointAccountDetails or FinJointAccountDetails_Temp. it fetches the
	 * available Sequence form SeqFinJointAccountDetails by using getNextidviewDAO().getNextId() method.
	 *
	 * save Jount Account Details
	 * 
	 * @param Jount
	 *            Account Details (jountAccountDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(JointAccountDetail jountAccountDetail, String type) {
		logger.debug("Entering");
		if (jountAccountDetail.getId() == Long.MIN_VALUE) {
			jountAccountDetail.setId(getNextValue("SeqFinJointAccountDetails"));
			logger.debug("get NextID:" + jountAccountDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinJointAccountDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId,CatOfcoApplicant,AuthoritySignatory, Sequence, IncludeIncome");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:JointAccountId, :FinReference, :CustCIF, :IncludeRepay, :RepayAccountId, :CatOfcoApplicant, :AuthoritySignatory, :Sequence, :IncludeIncome");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return jountAccountDetail.getId();
	}

	/**
	 * This method updates the Record FinJointAccountDetails or FinJointAccountDetails_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Jount Account Details by key JointAccountId and Version
	 * 
	 * @param Jount
	 *            Account Details (jountAccountDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(JointAccountDetail jountAccountDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinJointAccountDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set FinReference = :FinReference, CustCIF = :CustCIF, IncludeRepay = :IncludeRepay, RepayAccountId = :RepayAccountId, CatOfcoApplicant = :CatOfcoApplicant");
		updateSql.append(
				",AuthoritySignatory = :AuthoritySignatory, Sequence = :Sequence, IncludeIncome = :IncludeIncome");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where JointAccountId =:JointAccountId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public JointAccountDetail getJountAccountDetailByRefId(String finReference, long jointAccountId, String type) {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = new JointAccountDetail();

		jountAccountDetail.setFinReference(finReference);
		jountAccountDetail.setJointAccountId(jointAccountId);

		StringBuilder selectSql = new StringBuilder(
				"Select JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId, CatOfcoApplicant, AuthoritySignatory, Sequence, IncludeIncome");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",LovDescCIFName, custID, lovCustDob ");
		}
		selectSql.append(" From FinJointAccountDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and JointAccountId = :JointAccountId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(JointAccountDetail.class);

		try {
			jountAccountDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jountAccountDetail = null;
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}

	@Override
	public void deleteByFinRef(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		jountAccountDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinJointAccountDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference = :FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
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
	public List<JointAccountDetail> getJountAccountDetailByFinnRef(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" JointAccountId, FinReference, CustCIF");
		sql.append(" from FinJointAccountDetails");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
		}, (rs, rowNum) -> {
			JointAccountDetail ad = new JointAccountDetail();

			ad.setJointAccountId(rs.getLong("JointAccountId"));
			ad.setFinReference(rs.getString("FinReference"));
			ad.setCustCIF(rs.getString("CustCIF"));

			return ad;
		});
	}

	@Override
	public List<JointAccountDetail> getJountAccountDetailByFinRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId, CatOfcoApplicant");
		sql.append(", AuthoritySignatory, Sequence, IncludeIncome, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCIFName, CustID, LovCustDob");
		}

		sql.append(" from FinJointAccountDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, new RowMapper<JointAccountDetail>() {
				@Override
				public JointAccountDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					JointAccountDetail jad = new JointAccountDetail();

					jad.setJointAccountId(rs.getLong("JointAccountId"));
					jad.setFinReference(rs.getString("FinReference"));
					jad.setCustCIF(rs.getString("CustCIF"));
					jad.setIncludeRepay(rs.getBoolean("IncludeRepay"));
					jad.setRepayAccountId(rs.getString("RepayAccountId"));
					jad.setCatOfcoApplicant(rs.getString("CatOfcoApplicant"));
					jad.setAuthoritySignatory(rs.getBoolean("AuthoritySignatory"));
					jad.setSequence(rs.getInt("Sequence"));
					jad.setIncludeIncome(rs.getBoolean("IncludeIncome"));
					jad.setVersion(rs.getInt("Version"));
					jad.setLastMntBy(rs.getLong("LastMntBy"));
					jad.setLastMntOn(rs.getTimestamp("LastMntOn"));
					jad.setRecordStatus(rs.getString("RecordStatus"));
					jad.setRoleCode(rs.getString("RoleCode"));
					jad.setNextRoleCode(rs.getString("NextRoleCode"));
					jad.setTaskId(rs.getString("TaskId"));
					jad.setNextTaskId(rs.getString("NextTaskId"));
					jad.setRecordType(rs.getString("RecordType"));
					jad.setWorkflowId(rs.getLong("WorkflowId"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						jad.setLovDescCIFName(rs.getString("LovDescCIFName"));
						jad.setCustID(rs.getLong("CustID"));
						jad.setLovCustDob(rs.getTimestamp("LovCustDob"));
					}

					return jad;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jointAccountDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;

		StringBuilder query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,");
		query.append(
				" T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure, ");
		query.append(" T1.FinCcy finCcy, T7.ccyEditField ccyEditField, T4.custStsDescription status,");
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain T1");
		query.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference");
		query.append(" INNER JOIN Customers T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts");
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN RMTCurrencies T7 ON T7.CcyCode = T1.FinCcy ");
		query.append(" WHERE T3.CustCIF=:custCIF AND T1.FinIsActive = 1");
		query.append(" ORDER BY T1.FINSTARTDATE ASC");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(jointAccountDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}

	@Override
	public List<FinanceExposure> getPrimaryExposureList(List<String> listCIF) {
		logger.debug("Entering");
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CUSTCIF", listCIF);
		paramSource.addValue("ACTIVE", true);

		RowMapper<FinanceExposure> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceExposure.class);
		;
		StringBuilder query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,");
		query.append(
				" T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure, ");
		query.append(" T1.FinCcy finCcy, T7.ccyEditField ccyEditField, T4.custStsDescription status,");
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain T1");
		query.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference");
		query.append(" INNER JOIN Customers T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts");
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN RMTCurrencies T7 ON T7.CcyCode = T1.FinCcy ");
		query.append(" WHERE T3.CustCIF IN (:CUSTCIF) AND T1.FinIsActive = :ACTIVE");
		query.append(" ORDER BY T1.FINSTARTDATE ASC");
		logger.debug("selectSql: " + query.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(query.toString(), paramSource, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			typeRowMapper = null;
			paramSource = null;
			query = null;
		}
		return null;
	}

	@Override
	public List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;

		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,");
		query.append(
				" T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure, ");
		query.append(" T1.FinCcy finCcy, T8.ccyEditField ccyEditField, T4.custStsDescription status,");
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain T1");
		query.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference");
		query.append(" INNER JOIN Customers T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts");
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN FinJointAccountDetails_View T7  ON T7.FinReference=T1.FinReference");
		query.append(" INNER JOIN RMTCurrencies T8 ON T8.CcyCode = T1.FinCcy ");
		query.append(" WHERE T7.CustCIF=:custCIF  AND T1.FinIsActive = 1");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(jointAccountDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return new ArrayList<>();
	}

	@Override
	public List<FinanceExposure> getSecondaryExposureList(List<String> listCIF) {
		logger.debug("Entering");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CUSTCIF", listCIF);
		paramSource.addValue("ACTIVE", true);
		RowMapper<FinanceExposure> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceExposure.class);
		;

		StringBuilder query = null;
		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,");
		query.append(
				" T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure, ");
		query.append(" T1.FinCcy finCcy, T8.ccyEditField ccyEditField, T4.custStsDescription status,");
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain T1");
		query.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference");
		query.append(" INNER JOIN Customers T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts");
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN FinJointAccountDetails_View T7  ON T7.FinReference=T1.FinReference");
		query.append(" INNER JOIN RMTCurrencies T8 ON T8.CcyCode = T1.FinCcy ");
		query.append(" WHERE T7.CustCIF IN (:CUSTCIF) AND T1.FinIsActive = :ACTIVE");

		logger.debug("selectSql: " + query.toString());

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(query.toString(), paramSource, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			paramSource = null;
			typeRowMapper = null;
			query = null;
		}
		return new ArrayList<>();
	}

	@Override
	public List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;

		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T6.FinTypeDesc, T1.FinReference,");
		query.append(
				" T1.FinStartDate, T1.MaturityDate, (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T1.FeeChargeAmt - T1.DownPayment - T1.FinRepaymentAmount) CurrentExpoSure, ");
		query.append(" T1.FinCcy finCcy, T8.ccyEditField ccyEditField, T4.custStsDescription status,");
		query.append(" T5.custStsDescription WorstStatus, T3.CustCIF ");
		query.append(" FROM  FinanceMain T1");
		query.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference");
		query.append(" INNER JOIN Customers T3 ON T3.CustId = T1.CustID ");
		query.append(" LEFT JOIN BMTCustStatusCodes T4 ON T4.CustStsCode=T3.CustSts");
		query.append(" LEFT JOIN BMTCustStatusCodes T5 ON T5.CustStsCode=T1.FinStatus ");
		query.append(" INNER JOIN RMTFinanceTypes T6 ON T6.FinType = T1.FinType ");
		query.append(" INNER JOIN FinGuarantorsDetails_View T7  ON T7.FinReference=T1.FinReference");
		query.append(" INNER JOIN RMTCurrencies T8 ON T8.CcyCode = T1.FinCcy ");
		query.append(" WHERE T7.GuarantorCIF=:custCIF  AND T1.FinIsActive = 1");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(jointAccountDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}

	@Override
	public FinanceExposure getOverDueDetails(FinanceExposure exposer) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;

		query = new StringBuilder();
		query.append(" SELECT SUM(FinCurODAmt) OverdueAmt, MAX(FinCurODDays) PastdueDays");
		query.append(" FROM  FinanceMain FM");
		query.append(" INNER JOIN FinODDetails OD ON OD.FinReference = FM.FinReference");
		query.append(" WHERE FM.FinReference=:FinReference ");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(exposer);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		logger.debug("Leaving");
		FinanceExposure exposure = null;
		try {
			exposure = this.jdbcTemplate.queryForObject(String.valueOf(query), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			exposure = null;
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return exposure;
	}

	public JointAccountDetail getJountAccountDetailByRef(String finReference, String custCIF, String type) {
		logger.debug("Entering");
		JointAccountDetail jountAccountDetail = new JointAccountDetail();

		jountAccountDetail.setFinReference(finReference);
		jountAccountDetail.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder(
				"Select JointAccountId, FinReference, CustCIF, IncludeRepay, RepayAccountId, CatOfcoApplicant,AuthoritySignatory, Sequence, IncludeIncome");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",LovDescCIFName, custID, lovCustDob  ");
		}
		selectSql.append(" From FinJointAccountDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and CustCIF = :CustCIF ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jountAccountDetail);
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(JointAccountDetail.class);

		try {
			jountAccountDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jountAccountDetail = null;
		}
		logger.debug("Leaving");
		return jountAccountDetail;
	}

	@Override
	public List<JointAccountDetail> getCustIdsByFinnRef(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("CustCtgCode", "RETAIL");
		source.addValue("IncludeIncome", true);

		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" C.CustID, C.CustCIF");
		selectSql.append(" From FinJointAccountDetails_View F");
		selectSql.append(" Inner Join Customers C on C.CustCIF =  F.CustCIF");
		selectSql.append(" Left Join FinCreditReviewDetails FCRD on FCRD.CustomerId = C.CustId");
		selectSql.append(
				" Where F.FinReference =:FinReference and C.CustCtgCode !=:CustCtgCode and IncludeIncome =:IncludeIncome");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<JointAccountDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(JointAccountDetail.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getCustCtgCount(String finRef) {
		logger.trace(Literal.ENTERING);

		MapSqlParameterSource sqlScource = new MapSqlParameterSource();
		sqlScource.addValue("FinReference", finRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select custctgcode ,count(*) count ");
		selectSql.append(" from FinJointAccountDetails_View F ");
		selectSql.append(
				" Inner Join Customers C on C.CustCIF =  F.CustCIF where finreference=:FinReference group by custctgcode  ");
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			map = this.jdbcTemplate.query(selectSql.toString(), sqlScource,
					new ResultSetExtractor<Map<String, Integer>>() {
						Map<String, Integer> map = new HashMap<String, Integer>();

						@Override
						public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
							while (rs.next()) {
								map.put(rs.getString(1), rs.getInt(2));
							}
							return map;
						}

					});
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return map;
	}

}