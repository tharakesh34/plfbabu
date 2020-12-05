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
 * FileName    		:  MandateDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.mandate.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>Mandate model</b> class.<br>
 * 
 */
public class MandateDAOImpl extends SequenceDao<Mandate> implements MandateDAO {
	private static Logger logger = Logger.getLogger(MandateDAOImpl.class);

	public MandateDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new Mandate
	 * 
	 * @return Mandate
	 */

	@Override
	public Mandate getMandate() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Mandate");
		Mandate mandate = new Mandate();
		if (workFlowDetails != null) {
			mandate.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return mandate;
	}

	/**
	 * This method get the module from method getMandate() and set the new record flag as true and return Mandate()
	 * 
	 * @return Mandate
	 */

	@Override
	public Mandate getNewMandate() {
		logger.debug("Entering");
		Mandate mandate = getMandate();
		mandate.setNewRecord(true);
		logger.debug("Leaving");
		return mandate;
	}

	/**
	 * Fetch the Record Mandate details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Mandate
	 */
	@Override
	public Mandate getMandateById(final long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where MandateID = ?");

		logger.trace(Literal.SQL + sql.toString());

		MandateRowMapper rowMapper = new MandateRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Fetch the Record Mandate details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param status
	 *            (String) ""/_Temp/_View
	 * @return Mandate
	 */
	@Override
	public Mandate getMandateByStatus(final long id, String status, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where MandateID = ?  and Status = ?");

		logger.trace(Literal.SQL + sql.toString());

		MandateRowMapper rowMapper = new MandateRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id, status }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public Mandate getMandateByOrgReference(final String orgReference, String status, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where OrgReference = ?  and Status = ?");

		logger.trace(Literal.SQL + sql.toString());

		MandateRowMapper rowMapper = new MandateRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { orgReference, status }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * This method Deletes the Record from the Mandates or Mandates_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Mandate by key MandateID
	 * 
	 * @param Mandate
	 *            (mandate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Mandate mandate, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Mandates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where MandateID =:MandateID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
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
	 * This method insert new Records into Mandates or Mandates_Temp. it fetches the available Sequence form SeqMandates
	 * by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Mandate
	 * 
	 * @param Mandate
	 *            (mandate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(Mandate mandate, String type) {
		logger.debug("Entering");
		if (mandate.getId() == Long.MIN_VALUE) {
			mandate.setId(getNextValue("SeqMandates"));
			logger.debug("get NextID:" + mandate.getId());
		}

		StringBuilder sql = new StringBuilder("Insert Into Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (MandateID, CustID, MandateRef, MandateType, BankBranchID, AccNumber, AccHolderName,");
		sql.append(" JointAccHolderName, AccType, OpenMandate, StartDate, ExpiryDate, MaxLimit,");
		sql.append(" Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status, ApprovalID,");
		sql.append(
				" InputDate, Active, Reason, MandateCcy, DocumentName, DocumentRef, ExternalRef, Version, LastMntBy,");
		sql.append(" LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,  RecordType,");
		sql.append(
				" WorkflowId,OrgReference, BarCodeNumber, SwapIsActive, PrimaryMandateId, EntityCode, PartnerBankId, DefaultMandate");
		sql.append(", EMandateSource, EMandateReferenceNo)");
		sql.append(" Values(:MandateID, :CustID, :MandateRef, :MandateType, :BankBranchID, :AccNumber, ");
		sql.append(" :AccHolderName, :JointAccHolderName, :AccType, :OpenMandate, :StartDate, :ExpiryDate,");
		sql.append(" :MaxLimit, :Periodicity, :PhoneCountryCode, :PhoneAreaCode, :PhoneNumber, :Status,");
		sql.append(
				" :ApprovalID, :InputDate, :Active, :Reason, :MandateCcy, :DocumentName, :DocumentRef, :ExternalRef, :Version,:LastMntBy, :LastMntOn,");
		sql.append(" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		sql.append(
				" :WorkflowId, :OrgReference, :BarCodeNumber, :SwapIsActive, :PrimaryMandateId, :EntityCode, :PartnerBankId, :DefaultMandate");
		sql.append(", :EMandateSource, :EMandateReferenceNo)");

		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return mandate.getId();
	}

	/**
	 * This method updates the Record Mandates or Mandates_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update Mandate by key MandateID and Version
	 * 
	 * @param Mandate
	 *            (mandate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(Mandate mandate, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder("Update Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustID = :CustID, MandateRef = :MandateRef");
		sql.append(", MandateType = :MandateType, BankBranchID = :BankBranchID, AccNumber = :AccNumber");
		sql.append(", AccHolderName = :AccHolderName, JointAccHolderName = :JointAccHolderName");
		sql.append(", AccType = :AccType, OpenMandate = :OpenMandate, StartDate = :StartDate");
		sql.append(", ExpiryDate = :ExpiryDate , MaxLimit = :MaxLimit, Periodicity = :Periodicity");
		sql.append(", PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode");
		sql.append(", PhoneNumber = :PhoneNumber, Status = :Status, ApprovalID = :ApprovalID");
		sql.append(", Active = :Active, Reason = :Reason, MandateCcy =:MandateCcy");
		sql.append(", DocumentName = :DocumentName, DocumentRef = :DocumentRef, ExternalRef = :ExternalRef");
		sql.append(", OrgReference = :OrgReference, Version = :Version , LastMntBy = :LastMntBy");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId,NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(", InputDate = :InputDate, BarCodeNumber = :BarCodeNumber");
		sql.append(", SwapIsActive = :SwapIsActive, PrimaryMandateId = :PrimaryMandateId");
		sql.append(", EntityCode = :EntityCode , PartnerBankId =:PartnerBankId");
		sql.append(", DefaultMandate = :DefaultMandate, EMandateSource = :EMandateSource");
		sql.append(", EMandateReferenceNo = :EMandateReferenceNo");
		sql.append(" Where MandateID =:MandateID");
		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record Mandates or Mandates_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update Mandate by key MandateID and Version
	 * 
	 * @param Mandate
	 *            (mandate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void updateFinMandate(Mandate mandate, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Update Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustID = :CustID, MandateRef = :MandateRef");
		sql.append(", MandateType = :MandateType, BankBranchID = :BankBranchID, AccNumber = :AccNumber");
		sql.append(", AccHolderName = :AccHolderName, JointAccHolderName = :JointAccHolderName");
		sql.append(", AccType = :AccType, OpenMandate = :OpenMandate, StartDate = :StartDate");
		sql.append(", ExpiryDate = :ExpiryDate , MaxLimit = :MaxLimit, Periodicity = :Periodicity");
		sql.append(", PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode");
		sql.append(", PhoneNumber = :PhoneNumber, ApprovalID = :ApprovalID");
		sql.append(", Active = :Active, Reason = :Reason, MandateCcy =:MandateCcy");
		sql.append(", DocumentName = :DocumentName, DocumentRef = :DocumentRef, ExternalRef = :ExternalRef");
		sql.append(", OrgReference = :OrgReference, Version = :Version , LastMntBy = :LastMntBy");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId,NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId, BarCodeNumber = :BarCodeNumber");
		sql.append(", SwapIsActive = :SwapIsActive, EntityCode = :EntityCode");
		sql.append(", PartnerBankId = :PartnerBankId, DefaultMandate = :DefaultMandate");
		sql.append(", EMandateSource = :EMandateSource, EMandateReferenceNo = :EMandateReferenceNo");
		sql.append("  Where MandateID = :MandateID");
		sql.append("  AND Status = :Status");

		logger.debug("updateSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateStatus(long mandateID, String mandateStatusAwaitcon, String mandateRef, String approvalId,
			String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MandateID", mandateID);
		source.addValue("Status", mandateStatusAwaitcon);
		source.addValue("MandateRef", mandateRef);
		source.addValue("ApprovalID", approvalId);

		StringBuilder updateSql = new StringBuilder("Update Mandates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  Status = :Status");
		updateSql.append(", MandateRef = :MandateRef");
		updateSql.append(", ApprovalID = :ApprovalID");
		updateSql.append(" Where MandateID = :MandateID");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");

	}

	@Override
	public void updateActive(long mandateID, String status, boolean active) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MandateID", mandateID);
		source.addValue("Active", active);
		source.addValue("Status", status);

		StringBuilder updateSql = new StringBuilder("Update Mandates");
		updateSql.append(" Set  Active = :Active, Status= :Status");
		updateSql.append(" Where MandateID = :MandateID");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");

	}

	/**
	 * Fetch the Customer Finance Details
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<FinanceEnquiry> getMandateFinanceDetailById(long mandateID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinType, FinStatus, FinStartDate, FinAmount, DownPayment, FeeChargeAmt");
		sql.append(", InsuranceAmt, NumberOfTerms, LovDescFinTypeName, MaxInstAmount, FinRepaymentAmount");
		sql.append(", FinCurrAssetValue");
		sql.append(" from MandateEnquiry_view");
		sql.append(" Where MandateID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, mandateID);
				}
			}, new RowMapper<FinanceEnquiry>() {
				@Override
				public FinanceEnquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceEnquiry mndts = new FinanceEnquiry();

					mndts.setFinReference(rs.getString("FinReference"));
					mndts.setFinType(rs.getString("FinType"));
					mndts.setFinStatus(rs.getString("FinStatus"));
					mndts.setFinStartDate(rs.getTimestamp("FinStartDate"));
					mndts.setFinAmount(rs.getBigDecimal("FinAmount"));
					mndts.setDownPayment(rs.getBigDecimal("DownPayment"));
					mndts.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					mndts.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
					mndts.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					mndts.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
					mndts.setMaxInstAmount(rs.getBigDecimal("MaxInstAmount"));
					mndts.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
					mndts.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));

					return mndts;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	/**
	 * Fetch the Mandate Details
	 * 
	 * @param CustID
	 * 
	 * @return
	 */
	@Override
	public List<Mandate> getApprovedMandatesByCustomerId(long custID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		MandateRowMapper rowMapper = new MandateRowMapper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custID);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void updateOrgReferecne(long mandateID, String orgReference, String type) {
		logger.debug("Entering");
		Mandate mandate = new Mandate();
		mandate.setMandateID(mandateID);
		mandate.setOrgReference(orgReference);
		StringBuilder updateSql = new StringBuilder("Update Mandates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set OrgReference = :OrgReference");
		updateSql.append(" Where MandateID =:MandateID and OrgReference is null");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public int getBranch(long bankBranchID, String type) {
		Mandate mandate = new Mandate();
		mandate.setBankBranchID(bankBranchID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Mandates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankBranchID =:BankBranchID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<Mandate> getMnadateByCustID(long custID, long mandateID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MandateType");
		sql.append(" from Mandates");
		sql.append(" Where CustID = ? and OpenMandate = ? and Active = ? and MandateID != ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custID);
					ps.setBoolean(index++, true);
					ps.setBoolean(index++, true);
					ps.setLong(index++, mandateID);
				}
			}, new RowMapper<Mandate>() {
				@Override
				public Mandate mapRow(ResultSet rs, int rowNum) throws SQLException {
					Mandate mndts = new Mandate();

					mndts.setMandateType(rs.getString("MandateType"));

					return mndts;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public int getSecondaryMandateCount(long mandateID) {

		logger.debug("Entering");

		Mandate mandate = new Mandate();
		mandate.setPrimaryMandateId(mandateID);
		mandate.setActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From Mandates");
		selectSql.append(" Where PrimaryMandateId =:PrimaryMandateId AND Active =:Active");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

	}

	@Override
	public void updateStatusAfterRegistration(long mandateID, String statusInprocess) {
		logger.info(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder updateSql = new StringBuilder("UPDATE MANDATES SET STATUS = :STATUS");
		updateSql.append(" WHERE MANDATEID = :MANDATEID");
		paramMap.addValue("MANDATEID", mandateID);
		paramMap.addValue("STATUS", statusInprocess);

		logger.info("updateSql: " + updateSql.toString());
		jdbcTemplate.update(updateSql.toString(), paramMap);
		logger.info(Literal.LEAVING);

	}

	@Override
	public boolean checkMandateStatus(long mandateID) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from MANDATE_REGISTRATION ");
		sql.append("  WHERE MANDATE_ID = :MANDATEID AND MACHINE_FLAG = :MACHINE_FLAG AND ACTIVE_FLAG = :ACTIVE_FLAG");
		logger.debug("Sql: " + sql.toString());

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MANDATEID", mandateID);
		paramMap.addValue("MACHINE_FLAG", "Y");
		paramMap.addValue("ACTIVE_FLAG", 1);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		return false;
	}

	@Override
	public boolean checkMandates(String orgReference, long mandateId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" SELECT COUNT(*) FROM  MANDATES ");
		sql.append("  where ORGREFERENCE = :ORGREFERENCE  AND STATUS IN (:STATUS) AND ACTIVE = :ACTIVE");
		logger.debug("Sql: " + sql.toString());

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("ORGREFERENCE", orgReference);
		paramMap.addValue("STATUS", Arrays.asList("AC", "INPROCESS", "NEW"));
		paramMap.addValue("MANDATEID", mandateId);
		paramMap.addValue("ACTIVE", 1);

		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		return false;
	}

	@Override
	public int getBarCodeCount(String barCode, long mandateID, String type) {
		Mandate mandate = new Mandate();
		mandate.setMandateID(mandateID);
		mandate.setBarCodeNumber(barCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Mandates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BarCodeNumber = :BarCodeNumber And MandateID !=:MandateID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public BigDecimal getMaxRepayAmount(String finReference, String type) {

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT MAX(REPAYAMOUNT)  FROM  FINSCHEDULEDETAILS");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FINREFERENCE = :FINREFERENCE");
		selectSql.append(" AND INSTNUMBER <> 0 AND PARTIALPAIDAMT = 0");
		source.addValue("FINREFERENCE", finReference);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);

	}

	@Override
	public boolean entityExistMandate(String entityCode, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  WHERE EntityCode = :EntityCode");
		logger.debug("Sql: " + sql.toString());

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("EntityCode", entityCode);

		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		return false;
	}

	@Override
	public Mandate getMandateStatusById(String finReference, long mandateID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Status, ExpiryDate");
		sql.append(" from Mandates");
		sql.append(" Where MandateID = ? and OrgReference = ? and ACTIVE = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { mandateID, finReference, 1 },
					new RowMapper<Mandate>() {
						@Override
						public Mandate mapRow(ResultSet rs, int rowNum) throws SQLException {
							Mandate mndts = new Mandate();

							mndts.setStatus(rs.getString("Status"));
							mndts.setExpiryDate(rs.getTimestamp("ExpiryDate"));

							return mndts;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public int getMandateCount(long custID, long mandateID) {
		logger.debug("Entering");

		Mandate mandate = getMandate();
		mandate.setCustID(custID);
		mandate.setDefaultMandate(true);
		mandate.setMandateID(mandateID);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From Mandates");
		selectSql.append(" Where CustID = :CustID and defaultMandate = :defaultMandate and mandateID != :mandateID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int validateEmandateSource(String eMandateSource) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from Mandate_Sources");
		sql.append("  WHERE Code = :Code");
		logger.debug("Sql: " + sql.toString());

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("Code", eMandateSource);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return 0;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MandateID, CustID, MandateRef, MandateType, BankBranchID, AccNumber, AccHolderName");
		sql.append(", JointAccHolderName, AccType, OpenMandate, StartDate, ExpiryDate, MaxLimit, Periodicity");
		sql.append(", PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status, ApprovalID, InputDate");
		sql.append(", Active, Reason, MandateCcy, OrgReference, DocumentName, DocumentRef, ExternalRef");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, BarCodeNumber, SwapIsActive, PrimaryMandateId");
		sql.append(", EntityCode, PartnerBankId, DefaultMandate, EMandateSource, EMandateReferenceNo");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", finType, CustCIF, CustShrtName, BankCode, BranchCode");
			sql.append(", BranchDesc, BankName, City, MICR, IFSC, PccityName, UseExisting, EntityDesc");
			sql.append(", PartnerBankCode, PartnerBankName");
		}

		sql.append(" from Mandates");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class MandateRowMapper implements RowMapper<Mandate> {
		private String type;

		private MandateRowMapper(String type) {
			this.type = type;
		}

		@Override
		public Mandate mapRow(ResultSet rs, int rowNum) throws SQLException {
			Mandate mndts = new Mandate();

			mndts.setMandateID(rs.getLong("MandateID"));
			mndts.setCustID(rs.getLong("CustID"));
			mndts.setMandateRef(rs.getString("MandateRef"));
			mndts.setMandateType(rs.getString("MandateType"));
			mndts.setBankBranchID(rs.getLong("BankBranchID"));
			mndts.setAccNumber(rs.getString("AccNumber"));
			mndts.setAccHolderName(rs.getString("AccHolderName"));
			mndts.setJointAccHolderName(rs.getString("JointAccHolderName"));
			mndts.setAccType(rs.getString("AccType"));
			mndts.setOpenMandate(rs.getBoolean("OpenMandate"));
			mndts.setStartDate(rs.getTimestamp("StartDate"));
			mndts.setExpiryDate(rs.getTimestamp("ExpiryDate"));
			mndts.setMaxLimit(rs.getBigDecimal("MaxLimit"));
			mndts.setPeriodicity(rs.getString("Periodicity"));
			mndts.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			mndts.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			mndts.setPhoneNumber(rs.getString("PhoneNumber"));
			mndts.setStatus(rs.getString("Status"));
			mndts.setApprovalID(rs.getString("ApprovalID"));
			mndts.setInputDate(rs.getTimestamp("InputDate"));
			mndts.setActive(rs.getBoolean("Active"));
			mndts.setReason(rs.getString("Reason"));
			mndts.setMandateCcy(rs.getString("MandateCcy"));
			mndts.setOrgReference(rs.getString("OrgReference"));
			mndts.setDocumentName(rs.getString("DocumentName"));
			mndts.setDocumentRef(rs.getLong("DocumentRef"));
			mndts.setExternalRef(rs.getString("ExternalRef"));
			mndts.setVersion(rs.getInt("Version"));
			mndts.setLastMntBy(rs.getLong("LastMntBy"));
			mndts.setLastMntOn(rs.getTimestamp("LastMntOn"));
			mndts.setRecordStatus(rs.getString("RecordStatus"));
			mndts.setRoleCode(rs.getString("RoleCode"));
			mndts.setNextRoleCode(rs.getString("NextRoleCode"));
			mndts.setTaskId(rs.getString("TaskId"));
			mndts.setNextTaskId(rs.getString("NextTaskId"));
			mndts.setRecordType(rs.getString("RecordType"));
			mndts.setWorkflowId(rs.getLong("WorkflowId"));
			mndts.setBarCodeNumber(rs.getString("BarCodeNumber"));
			mndts.setSwapIsActive(rs.getBoolean("SwapIsActive"));
			mndts.setPrimaryMandateId(rs.getLong("PrimaryMandateId"));
			mndts.setEntityCode(rs.getString("EntityCode"));
			mndts.setPartnerBankId(rs.getLong("PartnerBankId"));
			mndts.setDefaultMandate(rs.getBoolean("DefaultMandate"));
			mndts.seteMandateSource(rs.getString("EMandateSource"));
			mndts.seteMandateReferenceNo(rs.getString("EMandateReferenceNo"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				mndts.setFinType(rs.getString("finType"));
				mndts.setCustCIF(rs.getString("CustCIF"));
				mndts.setCustShrtName(rs.getString("CustShrtName"));
				mndts.setBankCode(rs.getString("BankCode"));
				mndts.setBranchCode(rs.getString("BranchCode"));
				mndts.setBranchDesc(rs.getString("BranchDesc"));
				mndts.setBankName(rs.getString("BankName"));
				mndts.setCity(rs.getString("City"));
				mndts.setMICR(rs.getString("MICR"));
				mndts.setIFSC(rs.getString("IFSC"));
				mndts.setPccityName(rs.getString("PccityName"));
				mndts.setUseExisting(rs.getBoolean("UseExisting"));
				mndts.setEntityDesc(rs.getString("EntityDesc"));
				mndts.setPartnerBankCode(rs.getString("PartnerBankCode"));
				mndts.setPartnerBankName(rs.getString("PartnerBankName"));
			}

			return mndts;
		}

	}

	@Override
	public int updateMandateStatus(Mandate mandate) {
		logger.info(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("UPDATE MANDATES Set Status = :Status, MandateRef = :MandateRef");
		sql.append(", OrgReference = :OrgReference, Reason = :Reason");
		sql.append(" Where MandateID = :MandateID");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		try {
			return this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.info(Literal.LEAVING);
		return 0;
	}

	@Override
	public int getMandateByMandateRef(String mandateRef) {
		logger.info(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM MANDATES");
		sql.append(" Where  MandateRef = :MandateRef");
		logger.trace(Literal.SQL + sql.toString());
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MandateRef", mandateRef);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return 0;
		}
	}
}