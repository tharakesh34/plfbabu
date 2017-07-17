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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Mandate model</b> class.<br>
 * 
 */
public class MandateDAOImpl extends BasisNextidDaoImpl<Mandate> implements MandateDAO {
	private static Logger				logger	= Logger.getLogger(MandateDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

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
		logger.debug("Entering");
		Mandate mandate = getMandate();
		mandate.setId(id);
		StringBuilder selectSql = new StringBuilder("Select MandateID, CustID, MandateRef, MandateType, BankBranchID,");
		selectSql.append(" AccNumber, AccHolderName, JointAccHolderName, AccType, OpenMandate,StartDate, ");
		selectSql.append(" ExpiryDate ,MaxLimit, Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status,");
		selectSql.append(" ApprovalID, InputDate, Active, Reason, MandateCcy,OrgReference , Version ,LastMntBy,");
		selectSql.append(" LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType,");
		selectSql.append(" WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CustCIF,custShrtName,BankCode,BranchCode,BranchDesc,BankName,City,MICR,IFSC,PcCityName,");
			selectSql.append("useExisting");
		}
		selectSql.append(" From Mandates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MandateID =:MandateID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);

		try {
			mandate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandate = null;
		}
		logger.debug("Leaving");
		return mandate;
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
		logger.debug("Entering");
		Mandate mandate = getMandate();

		mandate.setId(id);
		mandate.setStatus(status);

		StringBuilder selectSql = new StringBuilder("Select MandateID, CustID, MandateRef, MandateType, BankBranchID,");
		selectSql.append(" AccNumber, AccHolderName, JointAccHolderName, AccType, OpenMandate,StartDate, ");
		selectSql.append(" ExpiryDate ,MaxLimit, Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status,");
		selectSql.append(" ApprovalID, InputDate, Active, Reason, MandateCcy,OrgReference , Version ,LastMntBy,");
		selectSql.append(" LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType,");
		selectSql.append(" WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CustCIF,custShrtName,BankCode,BranchCode,BranchDesc,BankName,City,MICR,IFSC,PcCityName,");
			selectSql.append("useExisting");
		}
		selectSql.append(" From Mandates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MandateID =:MandateID and Status=:Status");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);

		try {
			mandate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandate = null;
		}
		logger.debug("Leaving");
		return mandate;
	}
	
	
	@Override
	public Mandate getMandateByOrgReference(final String orgReference, String status, String type) {
		logger.debug("Entering");
		Mandate mandate = getMandate();
		
		mandate.setOrgReference(orgReference);
		mandate.setStatus(status);
		
		StringBuilder selectSql = new StringBuilder("Select MandateID, CustID, MandateRef, MandateType, BankBranchID,");
		selectSql.append(" AccNumber, AccHolderName, JointAccHolderName, AccType, OpenMandate,StartDate, ");
		selectSql.append(" ExpiryDate ,MaxLimit, Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status,");
		selectSql.append(" ApprovalID, InputDate, Active, Reason, MandateCcy,OrgReference , Version ,LastMntBy,");
		selectSql.append(" LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType,");
		selectSql.append(" WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CustCIF,custShrtName,BankCode,BranchCode,BranchDesc,BankName,City,MICR,IFSC,PcCityName,");
			selectSql.append("useExisting");
		}
		selectSql.append(" From Mandates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where OrgReference =:OrgReference and Status=:Status");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
		
		try {
			mandate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandate = null;
		}
		logger.debug("Leaving");
		return mandate;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
			mandate.setId(getNextidviewDAO().getNextId("SeqMandates"));
			logger.debug("get NextID:" + mandate.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into Mandates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (MandateID, CustID, MandateRef, MandateType, BankBranchID, AccNumber, AccHolderName,");
		insertSql.append(" JointAccHolderName, AccType, OpenMandate, StartDate, ExpiryDate, MaxLimit,");
		insertSql.append(" Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status, ApprovalID,");
		insertSql.append(" InputDate, Active, Reason, MandateCcy , Version , LastMntBy, LastMntOn,");
		insertSql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,  RecordType,");
		insertSql.append(" WorkflowId,OrgReference)");
		insertSql.append(" Values(:MandateID, :CustID, :MandateRef, :MandateType, :BankBranchID, :AccNumber, ");
		insertSql.append(" :AccHolderName, :JointAccHolderName, :AccType, :OpenMandate, :StartDate, :ExpiryDate,");
		insertSql.append(" :MaxLimit, :Periodicity, :PhoneCountryCode, :PhoneAreaCode, :PhoneNumber, :Status,");
		insertSql.append(" :ApprovalID, :InputDate, :Active, :Reason, :MandateCcy, :Version,:LastMntBy, :LastMntOn,");
		insertSql.append(" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		insertSql.append(" :WorkflowId, :OrgReference)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
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
		StringBuilder updateSql = new StringBuilder("Update Mandates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, MandateRef = :MandateRef,");
		updateSql.append(" MandateType = :MandateType, BankBranchID = :BankBranchID, AccNumber = :AccNumber,");
		updateSql.append(" AccHolderName = :AccHolderName, JointAccHolderName = :JointAccHolderName,");
		updateSql.append(" AccType = :AccType, OpenMandate = :OpenMandate, StartDate = :StartDate,");
		updateSql.append(" ExpiryDate = :ExpiryDate , MaxLimit = :MaxLimit, Periodicity = :Periodicity,");
		updateSql.append(" PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode,");
		updateSql.append(" PhoneNumber = :PhoneNumber, Status = :Status, ApprovalID = :ApprovalID,");
		updateSql.append("  Active = :Active, Reason = :Reason, MandateCcy =:MandateCcy,");
		updateSql.append(" OrgReference = :OrgReference, Version = :Version , LastMntBy = :LastMntBy, ");
		updateSql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId,NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where MandateID =:MandateID");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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

		StringBuilder updateSql = new StringBuilder("Update Mandates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, MandateRef = :MandateRef,");
		updateSql.append(" MandateType = :MandateType, BankBranchID = :BankBranchID, AccNumber = :AccNumber,");
		updateSql.append(" AccHolderName = :AccHolderName, JointAccHolderName = :JointAccHolderName,");
		updateSql.append(" AccType = :AccType, OpenMandate = :OpenMandate, StartDate = :StartDate,");
		updateSql.append(" ExpiryDate = :ExpiryDate , MaxLimit = :MaxLimit, Periodicity = :Periodicity,");
		updateSql.append(" PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode,");
		updateSql.append(" PhoneNumber = :PhoneNumber, ApprovalID = :ApprovalID,");
		updateSql.append(" Active = :Active, Reason = :Reason, MandateCcy =:MandateCcy,");
		updateSql.append(" OrgReference = :OrgReference, Version = :Version , LastMntBy = :LastMntBy, ");
		updateSql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId,NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where MandateID =:MandateID");
		updateSql.append("  AND Status = :Status");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");

	}

	@Override
	public void updateActive(long mandateID, boolean active) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MandateID", mandateID);
		source.addValue("Active", active);

		StringBuilder updateSql = new StringBuilder("Update Mandates");
		updateSql.append(" Set  Active = :Active");
		updateSql.append(" Where MandateID = :MandateID");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
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
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setMandateID(mandateID);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, FinType, FinStatus, FinStartDate,");
		selectSql.append(" FinAmount, DownPayment, FeeChargeAmt, InsuranceAmt, NumberOfTerms, LovDescFinTypeName ");
		selectSql.append(" from FinanceEnquiry_View ");
		selectSql.append(" Where MandateID = :MandateID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceEnquiry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		logger.debug("Entering");
		Mandate mandate = new Mandate();
		mandate.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("Select MandateID, CustID, MandateRef, MandateType, BankBranchID,");
		selectSql.append(" AccNumber, AccHolderName, JointAccHolderName, AccType, OpenMandate,StartDate, ");
		selectSql.append(" ExpiryDate ,MaxLimit, Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, Status,");
		selectSql.append(" ApprovalID, InputDate, Active, Reason, MandateCcy,OrgReference , Version ,LastMntBy,");
		selectSql.append(" LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType,");
		selectSql.append(" WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CustCIF,custShrtName,BankCode,BranchCode,BranchDesc,BankName,City,MICR,IFSC,");
			selectSql.append("useExisting");
		}
		selectSql.append(" From Mandates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);

		List<Mandate> mandates = new ArrayList<>();
		try {
			mandates = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return mandates;
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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<Mandate> getMnadateByCustID(long custID, long mandateID) {
		Mandate mandate = new Mandate();
		mandate.setCustID(custID);
		mandate.setOpenMandate(true);
		mandate.setActive(true);
		mandate.setMandateID(mandateID);
		
		StringBuilder selectSql = new StringBuilder("Select  MandateType");
		selectSql.append(" From Mandates");
		selectSql.append(" Where CustID =:CustID and OpenMandate =:OpenMandate and Active =:Active and MandateID !=:MandateID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);
		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,	typeRowMapper);
	}
}