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
 * FileName    		:  VASRecordingDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.configuration.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>VASRecording model</b> class.<br>
 * 
 */

public class VASRecordingDAOImpl extends BasicDao<VASRecording> implements VASRecordingDAO {
	private static Logger logger = Logger.getLogger(VASRecordingDAOImpl.class);

	public VASRecordingDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new VASRecording
	 * 
	 * @return VASRecording
	 */

	@Override
	public VASRecording getVASRecording() {
		logger.debug("Entering");
		VASRecording recording = new VASRecording();
		logger.debug("Leaving");
		return recording;
	}

	/**
	 * This method get the module from method getVASRecording() and set the new record flag as true and return
	 * VASRecording()
	 * 
	 * @return VASRecording
	 */
	@Override
	public VASRecording getNewVASRecording() {
		logger.debug("Entering");
		VASRecording vASRecording = getVASRecording();
		vASRecording.setNewRecord(true);
		logger.debug("Leaving");
		return vASRecording;
	}

	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecordingByReference(String vasReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where VasReference = ?");

		VASRecordingRowMapper rowMapper = new VASRecordingRowMapper(type);

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { vasReference }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public VASRecording getVASRecording(String vasReference, String vasStatus, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where VasReference = ? and VasStatus = ?");

		VASRecordingRowMapper rowMapper = new VASRecordingRowMapper(type);

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { vasReference, vasStatus },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode");
		sql.append(", EntityCode, TermInsuranceLien, ProviderName, PolicyNumber, MedicalApplicable");
		sql.append(", MedicalStatus, ValueDate, AccrualTillDate, RecurringDate, DsaId, DmaId, FulfilOfficerId");
		sql.append(", ReferralId,	Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, VasStatus, FinanceProcess, PaidAmt");
		sql.append(", WaivedAmt, Status, Remarks, Reason, CancelAmt, ServiceReqNumber, CancelAfterFLP");
		sql.append(", OldVasReference, ManualAdviseId, PaymentInsId, ReceivableAdviseId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ProductDesc, DsaIdDesc, DmaIDDesc, FulfilOfficerIdDesc");
			sql.append(", ReferralIdDesc, ProductType, ProductTypeDesc, ProductCtg");
			sql.append(", ProductCtgDesc, ManufacturerDesc, finType, flpDays, FeeAccounting");
		}

		sql.append(" from VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public List<VASRecording> getVASRecordingsByLinkRef(String primaryLinkRef, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PrimaryLinkRef = ?");

		VASRecordingRowMapper rowMapper = new VASRecordingRowMapper(type);

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, primaryLinkRef);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	/**
	 * This method Deletes the Record from the VASRecording or VASRecording_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete VASRecording by key ProductCode
	 * 
	 * @param VASRecording
	 *            (vASRecording)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(VASRecording vASRecording, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From VASRecording");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VasReference =:VasReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASRecording);
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
	 * This method Deletes the Record from the VASRecording or VASRecording_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete VASRecording by key ProductCode
	 * 
	 * @param VASRecording
	 *            (vASRecording)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteByPrimaryLinkRef(String primaryLinkRef, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PrimaryLinkRef", primaryLinkRef);

		StringBuilder deleteSql = new StringBuilder("Delete From VASRecording");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PrimaryLinkRef =:PrimaryLinkRef");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into VASRecording or VASRecording_Temp.
	 *
	 * save VASRecording
	 * 
	 * @param VASRecording
	 *            (vASRecording)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(VASRecording vASRecording, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into VASRecording");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee, FeePaymentMode, ValueDate, AccrualTillDate, RecurringDate, EntityCode,");
		insertSql.append(" TermInsuranceLien, ProviderName, PolicyNumber, MedicalApplicable, MedicalStatus,");
		insertSql.append(
				"  DsaId, DmaId, FulfilOfficerId, ReferralId, Status, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId,VasStatus,FinanceProcess, PaidAmt, WaivedAmt,");
		insertSql.append(
				"  Remarks  , Reason  , CancelAmt , ServiceReqNumber  , CancelAfterFLP, OldVasReference, ManualAdviseId, ReceivableAdviseId )");
		insertSql.append(
				"  Values(:ProductCode, :PostingAgainst, :PrimaryLinkRef, :VasReference, :Fee, :RenewalFee, :FeePaymentMode, :ValueDate, :AccrualTillDate, :RecurringDate, :EntityCode,");
		insertSql.append(" :TermInsuranceLien, :ProviderName, :PolicyNumber, :MedicalApplicable, :MedicalStatus,");
		insertSql.append("   :DsaId, :DmaId, :FulfilOfficerId, :ReferralId, :Status,");
		insertSql.append(
				"  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:VasStatus,:FinanceProcess, :PaidAmt, :WaivedAmt");
		insertSql.append(
				", :Remarks ,  :Reason ,  :CancelAmt , :ServiceReqNumber , :CancelAfterFLP, :OldVasReference, :ManualAdviseId, :ReceivableAdviseId )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASRecording);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vASRecording.getVasReference();
	}

	/**
	 * This method updates the Record VASRecording or VASRecording_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update VASRecording by key ProductCode and Version
	 * 
	 * @param VASRecording
	 *            (vASRecording)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VASRecording vASRecording, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update VASRecording");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set ProductCode = :ProductCode, PostingAgainst = :PostingAgainst, PrimaryLinkRef = :PrimaryLinkRef, Fee = :Fee, RenewalFee = :RenewalFee, FeePaymentMode = :FeePaymentMode, ");
		updateSql.append(
				" TermInsuranceLien = :TermInsuranceLien, ProviderName = :ProviderName, PolicyNumber = :PolicyNumber, MedicalApplicable = :MedicalApplicable, MedicalStatus = :MedicalStatus,");
		updateSql.append(
				" ValueDate = :ValueDate, AccrualTillDate = :AccrualTillDate, EntityCode = :EntityCode, RecurringDate = :RecurringDate, DsaId = :DsaId, DmaId = :DmaId, FulfilOfficerId = :FulfilOfficerId, ReferralId = :ReferralId,");
		updateSql.append(
				" Version= :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(
				"  Remarks = :Remarks , Reason = :Reason , CancelAmt =:CancelAmt , ServiceReqNumber = :ServiceReqNumber , CancelAfterFLP = :CancelAfterFLP , OldVasReference = :OldVasReference, ManualAdviseId = :ManualAdviseId, ReceivableAdviseId = :ReceivableAdviseId, ");
		updateSql.append(
				" RecordType = :RecordType, WorkflowId = :WorkflowId,VasStatus = :VasStatus,FinanceProcess =:FinanceProcess, PaidAmt =:PaidAmt, WaivedAmt =:WaivedAmt");
		updateSql.append(" Where VasReference = :VasReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASRecording);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/*
	 * Check whether reference is exists or not
	 */
	@Override
	public boolean isVasReferenceExists(String reference, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select Count(VasReference) from VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasReference = :VasReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("VasReference", reference);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	public boolean updateVasReference(long oldReference, long newReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" UPDATE  SeqVasReference  SET Seqno = :newReference Where Seqno = :oldReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("newReference", newReference);
		source.addValue("oldReference", oldReference);

		try {
			if (this.jdbcTemplate.update(sql.toString(), source) == 1) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return false;
	}

	@Override
	public VasCustomer getVasCustomerCif(String primaryLinkRef, String postingAgainst) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		if (VASConsatnts.VASAGAINST_FINANCE.equals(postingAgainst)) {
			sql.append(
					" Select CU.CustID CustomerId, CU.CustCIF, CU.CustShrtName from FinanceMain FM Inner Join Customers CU ON FM.CustID = CU.CustID");
			sql.append(" Where FM.FinReference = :PrimaryLinkRef");
		} else if (VASConsatnts.VASAGAINST_COLLATERAL.equals(postingAgainst)) {
			sql.append(
					" Select CU.CustID CustomerId, CU.CustCIF, CU.CustShrtName from CollateralSetup CO Inner Join Customers CU ON CO.DepositorId = CU.CustID");
			sql.append(" Where CO.CollateralRef = :PrimaryLinkRef");
		} else if (VASConsatnts.VASAGAINST_CUSTOMER.equals(postingAgainst)) {
			sql.append(" Select CustID CustomerId, CustCIF, CustShrtName from Customers ");
			sql.append(" Where CustCIF = :PrimaryLinkRef");
		}

		logger.debug("Sql: " + sql.toString());

		VasCustomer vasCustomer = null;
		try {
			if (StringUtils.isNotEmpty(sql.toString())) {

				source = new MapSqlParameterSource();
				source.addValue("PrimaryLinkRef", primaryLinkRef);

				RowMapper<VasCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper
						.newInstance(VasCustomer.class);

				vasCustomer = this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
			}
		} catch (Exception e) {
			logger.error(e);
			vasCustomer = null;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return vasCustomer;
	}

	@Override
	public void updateVasStatus(String status, String vasReference) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update VASRecording");
		sql.append(" Set Status = :Status  Where VasReference = :VasReference");
		logger.debug("sql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Status", status);
		source.addValue("VasReference", vasReference);

		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateVasStatus(String reference, long paymentInsId) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update VASRecording");
		sql.append(" Set PaymentInsId = :PaymentInsId  Where VasReference = :VasReference");
		logger.debug("sql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentInsId", paymentInsId);
		source.addValue("VasReference", reference);

		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASRecording
	 */
	@Override
	public List<VASRecording> getVASRecordingsStatusByReference(String primaryLinkRef, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		List<VASRecording> vasRecordingList = new ArrayList<>();

		sql = new StringBuilder("Select ProductCode, PostingAgainst, PrimaryLinkRef, VasReference, Fee, RenewalFee,");
		sql.append(" FeePaymentMode, VR.EntityCode, TermInsuranceLien, ProviderName, ");
		sql.append(" PolicyNumber, MedicalApplicable, MedicalStatus, ");
		sql.append(" ValueDate, AccrualTillDate, RecurringDate, DsaId, DmaId, FulfilOfficerId, ReferralId,");
		sql.append(" VasStatus, FinanceProcess, PaidAmt, WaivedAmt, VR.Status, OldVasReference,");
		sql.append(" ManualAdviseId, PaymentInsId, ReceivableAdviseId, IP.Status AS InsStatus");
		sql.append(" From VASRecording VR");
		sql.append(" Inner Join InsurancePaymentInstructions IP");
		sql.append(" ON VR.paymentinsid = IP.ID");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PrimaryLinkRef =:PrimaryLinkRef");

		logger.debug("selectSql: " + sql.toString());

		RowMapper<VASRecording> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASRecording.class);

		source = new MapSqlParameterSource();
		source.addValue("PrimaryLinkRef", primaryLinkRef);
		vasRecordingList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return vasRecordingList;
	}

	private class VASRecordingRowMapper implements RowMapper<VASRecording> {
		private String type;

		public VASRecordingRowMapper(String type) {
			this.type = type;
		}

		@Override
		public VASRecording mapRow(ResultSet rs, int rowNum) throws SQLException {
			VASRecording vas = new VASRecording();

			vas.setProductCode(rs.getString("ProductCode"));
			vas.setPostingAgainst(rs.getString("PostingAgainst"));
			vas.setPrimaryLinkRef(rs.getString("PrimaryLinkRef"));
			vas.setVasReference(rs.getString("VasReference"));
			vas.setFee(rs.getBigDecimal("Fee"));
			vas.setRenewalFee(rs.getBigDecimal("RenewalFee"));
			vas.setFeePaymentMode(rs.getString("FeePaymentMode"));
			vas.setEntityCode(rs.getString("EntityCode"));
			vas.setTermInsuranceLien(rs.getBoolean("TermInsuranceLien"));
			vas.setProviderName(rs.getString("ProviderName"));
			vas.setPolicyNumber(rs.getString("PolicyNumber"));
			vas.setMedicalApplicable(rs.getBoolean("MedicalApplicable"));
			vas.setMedicalStatus(rs.getString("MedicalStatus"));
			vas.setValueDate(rs.getTimestamp("ValueDate"));
			vas.setAccrualTillDate(rs.getTimestamp("AccrualTillDate"));
			vas.setRecurringDate(rs.getTimestamp("RecurringDate"));
			vas.setDsaId(rs.getString("DsaId"));
			vas.setDmaId(rs.getString("DmaId"));
			vas.setFulfilOfficerId(rs.getString("FulfilOfficerId"));
			vas.setReferralId(rs.getString("ReferralId"));
			vas.setVersion(rs.getInt("Version"));
			vas.setLastMntBy(rs.getLong("LastMntBy"));
			vas.setLastMntOn(rs.getTimestamp("LastMntOn"));
			vas.setRecordStatus(rs.getString("RecordStatus"));
			vas.setRoleCode(rs.getString("RoleCode"));
			vas.setNextRoleCode(rs.getString("NextRoleCode"));
			vas.setTaskId(rs.getString("TaskId"));
			vas.setNextTaskId(rs.getString("NextTaskId"));
			vas.setRecordType(rs.getString("RecordType"));
			vas.setWorkflowId(rs.getLong("WorkflowId"));
			vas.setVasStatus(rs.getString("VasStatus"));
			vas.setFinanceProcess(rs.getBoolean("FinanceProcess"));
			vas.setPaidAmt(rs.getBigDecimal("PaidAmt"));
			vas.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			vas.setStatus(rs.getString("Status"));
			vas.setRemarks(rs.getString("Remarks"));
			vas.setReason(rs.getString("Reason"));
			vas.setCancelAmt(rs.getBigDecimal("CancelAmt"));
			vas.setServiceReqNumber(rs.getString("ServiceReqNumber"));
			vas.setCancelAfterFLP(rs.getBoolean("CancelAfterFLP"));
			vas.setOldVasReference(rs.getString("OldVasReference"));
			vas.setManualAdviseId(rs.getLong("ManualAdviseId"));
			vas.setPaymentInsId(rs.getLong("PaymentInsId"));
			vas.setReceivableAdviseId(rs.getLong("ReceivableAdviseId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				vas.setProductDesc(rs.getString("ProductDesc"));
				vas.setDsaIdDesc(rs.getString("DsaIdDesc"));
				vas.setDmaIdDesc(rs.getString("DmaIdDesc"));
				vas.setFulfilOfficerIdDesc(rs.getString("FulfilOfficerIdDesc"));
				vas.setReferralIdDesc(rs.getString("ReferralIdDesc"));
				vas.setProductType(rs.getString("ProductType"));
				vas.setProductTypeDesc(rs.getString("ProductTypeDesc"));
				vas.setProductCtg(rs.getString("ProductCtg"));
				vas.setProductCtgDesc(rs.getString("ProductCtgDesc"));
				vas.setManufacturerDesc(rs.getString("ManufacturerDesc"));
				vas.setFinType(rs.getString("finType"));
				vas.setFlpDays(rs.getInt("FlpDays"));
				vas.setFeeAccounting(rs.getInt("FeeAccounting"));
			}

			return vas;
		}
	}

	@Override
	public Long getCustomerId(String vasReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select distinct CustId");
		sql.append(" from (Select fm.CustId, VasReference from VASRecording_Temp vr ");
		sql.append(" inner join");
		sql.append(" FinanceMain_Temp fm on fm.FinReference = vr.PrimaryLinkRef and PostingAgainst = 'Finance'");
		sql.append(" union all");
		sql.append(" Select fm.CustId, VasReference");
		sql.append(" from VASRecording vr");
		sql.append(" inner join");
		sql.append(" FinanceMain fm on fm.FinReference = vr.PrimaryLinkRef and PostingAgainst ='Finance'");
		sql.append(" union all");
		sql.append(" Select cu.custId, VasReference");
		sql.append(" from VASRecording_Temp vr");
		sql.append(" inner join");
		sql.append(" Customers_Temp cu on cu.CustCIF = vr.PrimaryLinkRef and PostingAgainst = 'Customer'");
		sql.append(" union all");
		sql.append(" Select cu.custId, VasReference");
		sql.append(" from VASRecording vr");
		sql.append(" inner join");
		sql.append(" Customers cu on cu.CustCIF = vr.PrimaryLinkRef and PostingAgainst = 'Customer')T");
		sql.append(" Where T.VasReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		logger.trace(Literal.LEAVING);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { vasReference },
					new RowMapper<Long>() {

						@Override
						public Long mapRow(ResultSet rs, int rowNum) throws SQLException {

							return rs.getLong("CustId");
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e.getCause());
		}
		return null;
	}

	@Override
	public void updatePaidAmt(String vasReference, String primaryLinkRef, BigDecimal paidAmt, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update VASRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PaidAmt =:PaidAmt ");
		sql.append(" Where VasReference = :VasReference AND PrimaryLinkRef =:PrimaryLinkRef");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaidAmt", paidAmt);
		source.addValue("VasReference", vasReference);
		source.addValue("PrimaryLinkRef", primaryLinkRef);

		int recordCount = this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<VASRecording> getLoanReportVasRecordingByRef(String finReference) {
		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		List<VASRecording> vasRecordingList = new ArrayList<>();
		sql = new StringBuilder("Select  vr.fee,vs.modeofPayment,vr.productcode ");
		sql.append("from vasrecording vr JOIN VasStructure vs ON vr.productcode = vs.productcode ");
		sql.append("Where PrimaryLinkRef=:PrimaryLinkRef");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<VASRecording> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASRecording.class);

		source = new MapSqlParameterSource();
		source.addValue("PrimaryLinkRef", finReference);
		vasRecordingList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return vasRecordingList;
	}

	public Long getPaymentInsId(String vasReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select PaymentInsId");
		sql.append(" from VasRecording");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		logger.trace(Literal.LEAVING);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { vasReference },
					new RowMapper<Long>() {

						@Override
						public Long mapRow(ResultSet rs, int rowNum) throws SQLException {

							return rs.getLong("PaymentInsId");
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e.getCause());
		}
		return null;
	}

	public String getVasInsStatus(long paymentInsId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select status");
		sql.append(" from InsurancePaymentInstructions ");
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());
		logger.trace(Literal.LEAVING);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentInsId },
					new RowMapper<String>() {

						@Override
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {

							return rs.getString("Status");
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e.getCause());
		}
		return null;
	}

	public void updateVasInsStatus(long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update InsurancePaymentInstructions");
		sql.append(" Set Status = (case when Status = " + "'" + DisbursementConstants.STATUS_APPROVED + "'");
		sql.append(" then " + "'" + DisbursementConstants.STATUS_CANCEL + "'");
		sql.append(" when Status = " + "'" + DisbursementConstants.STATUS_PAID + "'");
		sql.append(" then " + "'" + DisbursementConstants.STATUS_REVERSED + "'" + " else  status end )");

		sql.append(" Where Id = :Id");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public String getProductCodeByReference(String primaryLinkRef, String vasReference) {
		logger.debug(Literal.ENTERING);

		String productDesc = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductDesc");
		sql.append(" From VASRECORDING_VIEW ");
		sql.append(" Where PrimaryLinkRef = ? and vasReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			productDesc = jdbcOperations.queryForObject(sql.toString(), new Object[] { primaryLinkRef, vasReference },
					new RowMapper<String>() {
						@Override
						public String mapRow(ResultSet rs, int arg1) throws SQLException {
							return rs.getString("ProductDesc");
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return productDesc;
	}

}