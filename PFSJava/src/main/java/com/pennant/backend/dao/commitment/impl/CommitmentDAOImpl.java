/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : CommitmentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified
 * Date : 25-03-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.commitment.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentSummary;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>Commitment model</b> class.<br>
 * 
 */

public class CommitmentDAOImpl extends BasicDao<Commitment> implements CommitmentDAO {
	private static Logger logger = LogManager.getLogger(CommitmentDAOImpl.class);

	public CommitmentDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new Commitment
	 * 
	 * @return Commitment
	 */

	@Override
	public Commitment getCommitment() {
		logger.debug("Entering");

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Commitment");
		Commitment commitment = new Commitment();
		if (workFlowDetails != null) {
			commitment.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		logger.debug("Leaving");
		return commitment;
	}

	/**
	 * This method get the module from method getCommitment() and set the new record flag as true and return
	 * Commitment()
	 * 
	 * @return Commitment
	 */

	@Override
	public Commitment getNewCommitment() {
		logger.debug("Entering");

		Commitment commitment = getCommitment();
		commitment.setNewRecord(true);

		logger.debug("Leaving");
		return commitment;
	}

	/**
	 * Fetch the Record Commitment Detail details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Commitment
	 */
	@Override
	public Commitment getCommitmentById(final String id, String type) {
		logger.debug("Entering");
		Commitment commitment = new Commitment();
		commitment.setId(id);

		StringBuilder selectSql = new StringBuilder("Select CmtReference, custID, CmtBranch, OpenAccount, CmtAccount");
		selectSql.append(", CmtCcy, CmtPftRateMin, CmtPftRateMax, CmtAmount, CmtUtilizedAmount");
		selectSql.append(", CmtAvailable, CmtPromisedDate, CmtStartDate, CmtExpDate");
		selectSql.append(", CmtTitle, CmtNotes, Revolving, SharedCmt, MultiBranch");
		selectSql.append(", CmtCharges,ChargesAccount,CmtActive,CmtStopRateRange,NonPerforming,FacilityRef");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(
				", CmtRvwDate, CmtAvailableMonths, CollateralRequired, CmtEndDate, limitLineId,bankingArrangement,limitCondition,externalRef,externalRef1,tenor");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(", custShrtName, BranchDesc, custCIF, facilityRefDesc");
		}
		selectSql.append(" From Commitments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CmtReference = :CmtReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<Commitment> typeRowMapper = BeanPropertyRowMapper.newInstance(Commitment.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Commitment Detail details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Commitment
	 */
	@Override
	public Commitment getCommitmentByRef(final String id, String type) {
		logger.debug("Entering");

		Commitment commitment = new Commitment();
		commitment.setId(id);

		StringBuilder selectSql = new StringBuilder("Select CmtReference ");
		selectSql.append(" From Commitments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CmtReference = :CmtReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<Commitment> typeRowMapper = BeanPropertyRowMapper.newInstance(Commitment.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Commitment getCommitmentByFacilityRef(final String id, String type) {
		logger.debug("Entering");

		Commitment commitment = new Commitment();
		commitment.setId(id);

		StringBuilder selectSql = new StringBuilder("Select CmtReference, custID, CmtBranch, OpenAccount, CmtAccount");
		selectSql.append(", CmtCcy, CmtPftRateMin, CmtPftRateMax, CmtAmount, CmtUtilizedAmount");
		selectSql.append(", CmtAvailable, CmtPromisedDate, CmtStartDate, CmtExpDate");
		selectSql.append(", CmtTitle, CmtNotes, Revolving, SharedCmt, MultiBranch");
		selectSql.append(", CmtCharges,ChargesAccount,CmtActive,CmtStopRateRange,NonPerforming,FacilityRef");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(
				", CmtRvwDate, CmtAvailableMonths, CollateralRequired, CmtEndDate, limitLineId,bankingArrangement,limitCondition,externalRef,externalRef1,tenor");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",custShrtName, branchDesc, custCIF, facilityRefDesc");
		}
		selectSql.append(" From Commitments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID And FacilityRef = :FacilityRef");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<Commitment> typeRowMapper = BeanPropertyRowMapper.newInstance(Commitment.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Commitment Detail details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Commitment
	 */
	@Override
	public List<AvailCommitment> getCommitmentListByCustId(final long custId, String type) {
		logger.debug("Entering");

		Commitment commitment = new Commitment();
		commitment.setCustID(custId);

		StringBuilder selectSql = new StringBuilder(
				"Select CmtReference, CmtAccount , CmtCcy, CmtAmount, CmtUtilizedAmount,");
		selectSql.append(" CmtAvailable, CmtExpDate , CmtTitle, Revolving, CmtNotes, FacilityRef, ");
		selectSql.append(
				" CmtRvwDate, CmtAvailableMonths, CollateralRequired, CmtEndDate, limitLineId,bankingArrangement,limitCondition,externalRef,externalRef1,tenor");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(", facilityRefDesc");
		}
		selectSql.append(" From Commitments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID ORDER BY CmtExpDate DESC");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<AvailCommitment> typeRowMapper = BeanPropertyRowMapper.newInstance(AvailCommitment.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the Commitments or Commitments_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Commitment Detail by key CmtReference
	 * 
	 * @param Commitment Detail (commitment)
	 * @param type       (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Commitment commitment, String type) {
		logger.debug("Entering");

		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Commitments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CmtReference = :CmtReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
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
	 * Method for Deleting Commitment based on Commitment Reference
	 * 
	 * @param cmtReference
	 * @param type
	 */
	@Override
	public void deleteByRef(String cmtReference, String type) {
		logger.debug("Entering");

		Commitment commitment = new Commitment();
		commitment.setCmtReference(cmtReference);

		StringBuilder deleteSql = new StringBuilder("Delete From Commitments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CmtReference = :CmtReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into Commitments or Commitments_Temp.
	 * 
	 * save Commitment Detail
	 * 
	 * @param Commitment Detail (commitment)
	 * @param type       (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(Commitment commitment, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into Commitments");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (CmtReference, custID, CmtBranch, OpenAccount, CmtAccount, CmtCcy, CmtPftRateMin, CmtPftRateMax, CmtAmount, CmtUtilizedAmount, CmtAvailable, CmtPromisedDate, CmtStartDate, CmtExpDate, CmtTitle, CmtNotes, Revolving, SharedCmt, MultiBranch");
		insertSql.append(", CmtCharges,ChargesAccount,CmtActive,CmtStopRateRange,NonPerforming,FacilityRef");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		insertSql.append(
				", CmtRvwDate, CmtAvailableMonths, CollateralRequired, CmtEndDate, limitLineId,bankingArrangement,limitCondition,externalRef,externalRef1,tenor)");
		insertSql.append(
				" Values(:CmtReference, :custID, :CmtBranch, :OpenAccount, :CmtAccount, :CmtCcy, :CmtPftRateMin, :CmtPftRateMax, :CmtAmount, :CmtUtilizedAmount, :CmtAvailable, :CmtPromisedDate, :CmtStartDate, :CmtExpDate, :CmtTitle, :CmtNotes, :Revolving, :SharedCmt, :MultiBranch");
		insertSql.append(", :CmtCharges,:ChargesAccount,:CmtActive,:CmtStopRateRange,:NonPerforming,:FacilityRef");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId");
		insertSql.append(
				", :CmtRvwDate, :CmtAvailableMonths, :CollateralRequired, :CmtEndDate, :limitLineId,:bankingArrangement,:limitCondition,:externalRef,:externalRef1,:tenor)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return commitment.getId();
	}

	/**
	 * This method updates the Record Commitments or Commitments_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Commitment Detail by key CmtReference and Version
	 * 
	 * @param Commitment Detail (commitment)
	 * @param type       (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Commitment commitment, String type) {
		logger.debug("Entering");

		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder("Update Commitments");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set custID = :custID, CmtBranch = :CmtBranch, OpenAccount = :OpenAccount, CmtAccount = :CmtAccount, CmtCcy = :CmtCcy, CmtPftRateMin = :CmtPftRateMin");
		updateSql.append(
				", CmtPftRateMax = :CmtPftRateMax, CmtAmount = :CmtAmount, CmtUtilizedAmount = :CmtUtilizedAmount, CmtAvailable = :CmtAvailable, CmtPromisedDate = :CmtPromisedDate, CmtStartDate = :CmtStartDate");
		updateSql.append(
				", CmtExpDate = :CmtExpDate, CmtTitle = :CmtTitle, CmtNotes = :CmtNotes, Revolving = :Revolving, SharedCmt = :SharedCmt, MultiBranch = :MultiBranch");
		updateSql.append(
				", CmtCharges=:CmtCharges,ChargesAccount=:ChargesAccount,CmtActive=:CmtActive,CmtStopRateRange=:CmtStopRateRange,NonPerforming=:NonPerforming,FacilityRef=:FacilityRef");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(
				", CmtRvwDate = :CmtRvwDate, CmtAvailableMonths = :CmtAvailableMonths, CollateralRequired = :CollateralRequired, CmtEndDate = :CmtEndDate, limitLineId = :limitLineId,bankingArrangement =:bankingArrangement,limitCondition =:limitCondition,externalRef =:externalRef,externalRef1 =:externalRef1,tenor=:tenor");

		updateSql.append(" Where CmtReference = :CmtReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version = :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	/**
	 * This method get RoleIds count from UserRoles_View
	 * 
	 * @param RoleId (long)
	 * @return int
	 */
	public int getCmtAmountCount(long custID) {
		logger.debug("Entering ");

		Map<String, Long> namedParamters = Collections.singletonMap("custID", custID);
		StringBuilder selectSql = new StringBuilder(
				"SELECT COUNT(*) FROM Commitments where custID=:custID and CmtExpDate >= CURRENT_TIMESTAMP ");
		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), namedParamters, Integer.class);
	}

	/**
	 * This method get Commitment Amount Summary
	 * 
	 * @param RoleId (long)
	 */
	@Override
	public Map<String, Object> getAmountSummary(long custID) {
		logger.debug("Entering ");

		Map<String, Long> namedParamters = Collections.singletonMap("custID", custID);
		StringBuilder selectSql = new StringBuilder(" SELECT COUNT(*) ");
		selectSql.append(PennantConstants.CMT_TOTALCMT);
		selectSql.append(" , COALESCE(SUM(CmtAmount), 0) ");
		selectSql.append(PennantConstants.CMT_TOTALCMTAMT);
		selectSql.append(" , COALESCE(SUM(CmtUtilizedAmount), 0) ");
		selectSql.append(PennantConstants.CMT_TOTALUTZAMT);
		selectSql.append(" FROM Commitments where custID=:custID and CmtExpDate >= CURRENT_TIMESTAMP ");
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForMap(selectSql.toString(), namedParamters);
	}

	/**
	 * This method get Commitment Amount Summary
	 * 
	 * @param RoleId (long)
	 */
	@Override
	public List<CommitmentSummary> getCommitmentSummary(long custID) {
		logger.debug("Entering ");

		CommitmentSummary commitmentSummary = new CommitmentSummary();
		commitmentSummary.setCustID(custID);
		StringBuilder selectSql = new StringBuilder(" SELECT CmtCcy,CcyEditField, COUNT(*) ");
		selectSql.append(PennantConstants.CMT_TOTALCMT);
		selectSql.append(" , COALESCE(SUM(CmtAmount), 0) ");
		selectSql.append(PennantConstants.CMT_TOTALCMTAMT);
		selectSql.append(" , COALESCE(SUM(CmtUtilizedAmount), 0) ");
		selectSql.append(PennantConstants.CMT_TOTALUTZAMT);
		selectSql.append(
				" FROM Commitments_AView where custID=:custID and CmtExpDate >= CURRENT_TIMESTAMP group by CmtCcy,CcyEditField");
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving getCmtAmountCount()");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentSummary);
		RowMapper<CommitmentSummary> typeRowMapper = BeanPropertyRowMapper.newInstance(CommitmentSummary.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	public boolean updateCommitmentAmounts(String cmtReference, BigDecimal postingAmount, Date cmtExpDate) {
		logger.debug("Entering");

		boolean cmtExpired = false;
		Date dateValueDate = SysParamUtil.getAppValueDate();
		if (cmtExpDate.compareTo(dateValueDate) < 0) {
			cmtExpired = true;
		}

		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("CmtReference", cmtReference);
		namedParameters.put("CmtUtilizedAmount", postingAmount);
		namedParameters.put("CmtAvailable", postingAmount);

		StringBuilder updateSql = new StringBuilder(" Update Commitments");
		updateSql.append(" set CmtUtilizedAmount = CmtUtilizedAmount + :CmtUtilizedAmount, ");
		if (cmtExpired) {
			updateSql.append(" CmtAvailable = 0 ");
		} else {
			updateSql.append(" CmtAvailable = CmtAvailable - :CmtAvailable ");
		}
		updateSql.append(" where CmtReference = :CmtReference");

		logger.debug("updateSql:" + updateSql.toString());
		return this.jdbcTemplate.update(updateSql.toString(), namedParameters) >= 1;
	}

	/**
	 * 
	 */
	@Override
	public void updateNonPerformStatus(String commitmentRef) {
		logger.debug("Entering");

		Commitment commitment = new Commitment();
		commitment.setCmtReference(commitmentRef);
		commitment.setNonperformingStatus(true);

		StringBuilder updateSql = new StringBuilder();

		updateSql.append(" Update Commitments");
		updateSql.append(" set NonperformingStatus = :NonperformingStatus ");
		updateSql.append(" where CmtReference =:CmtReference");

		logger.debug("updateSql:" + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Get approved commitments record count by id.
	 * 
	 * @param id (commitment Reference)
	 * @return Integer
	 */
	@Override
	public int getCommitmentCountById(String id, String type) {
		logger.debug("Entering");

		Commitment commitment = new Commitment();
		commitment.setCmtReference(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From Commitments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CmtReference = :CmtReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}