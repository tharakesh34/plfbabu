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
 * * FileName : GuarantorDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>GuarantorDetail model</b> class.<br>
 * 
 */

public class GuarantorDetailDAOImpl extends SequenceDao<GuarantorDetail> implements GuarantorDetailDAO {
    private static Logger logger = Logger.getLogger(GuarantorDetailDAOImpl.class);

	public GuarantorDetailDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new GuarantorDetail
	 * 
	 * @return GuarantorDetail
	 */

	@Override
	public GuarantorDetail getGuarantorDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GuarantorDetail");
		GuarantorDetail guarantorDetail = new GuarantorDetail();
		if (workFlowDetails != null) {
			guarantorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return guarantorDetail;
	}

	/**
	 * This method get the module from method getGuarantorDetail() and set the
	 * new record flag as true and return GuarantorDetail()
	 * 
	 * @return GuarantorDetail
	 */

	@Override
	public GuarantorDetail getNewGuarantorDetail() {
		logger.debug("Entering");
		GuarantorDetail guarantorDetail = getGuarantorDetail();
		guarantorDetail.setNewRecord(true);
		logger.debug("Leaving");
		return guarantorDetail;
	}

	/**
	 * Fetch the Record Guarantor Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return GuarantorDetail
	 */
	@Override
	public GuarantorDetail getGuarantorDetailById(final long id, String type) {
		logger.debug("Entering");
		GuarantorDetail guarantorDetail = new GuarantorDetail();

		guarantorDetail.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select GuarantorId, FinReference, BankCustomer, GuarantorCIF, GuarantorIDType, GuarantorIDNumber, GuarantorCIFName, GuranteePercentage, MobileNo, EmailId, GuarantorProof, GuarantorProofName");
		selectSql.append(
				", AddrHNbr, FlatNbr, AddrStreet, AddrLine1, AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, GuarantorGenderCode");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",GuarantorIDTypeName, custID ");
		}
		selectSql.append(" From FinGuarantorsDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GuarantorId =:GuarantorId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
		RowMapper<GuarantorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(GuarantorDetail.class);

		try {
			guarantorDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			guarantorDetail = null;
		}
		logger.debug("Leaving");
		return guarantorDetail;
	}

	/**
	 * This method Deletes the Record from the FinGuarantorsDetails or
	 * FinGuarantorsDetails_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Guarantor Details by key
	 * GuarantorId
	 * 
	 * @param Guarantor
	 *            Details (guarantorDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(GuarantorDetail guarantorDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinGuarantorsDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where GuarantorId =:GuarantorId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
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
	 * This method insert new Records into FinGuarantorsDetails or
	 * FinGuarantorsDetails_Temp. it fetches the available Sequence form
	 * SeqFinGuarantorsDetails by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Guarantor Details
	 * 
	 * @param Guarantor
	 *            Details (guarantorDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(GuarantorDetail guarantorDetail, String type) {
		logger.debug("Entering");
		if (guarantorDetail.getId() == Long.MIN_VALUE) {
			guarantorDetail.setId(getNextValue("SeqFinGuarantorsDetails"));
			logger.debug("get NextID:" + guarantorDetail.getId());
		}
		if (guarantorDetail.getGuarantorProof() == null) {
			guarantorDetail.setGuarantorProof(new byte[] { Byte.MIN_VALUE });
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinGuarantorsDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (GuarantorId, FinReference, BankCustomer, GuarantorCIF, GuarantorIDType, GuarantorIDNumber, GuarantorCIFName, GuranteePercentage, MobileNo, EmailId, GuarantorProof, GuarantorProofName, Remarks ");
		insertSql.append(
				", AddrHNbr, FlatNbr, AddrStreet, AddrLine1, AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, GuarantorGenderCode");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:GuarantorId, :FinReference, :BankCustomer, :GuarantorCIF, :GuarantorIDType, :GuarantorIDNumber, :GuarantorCIFName, :GuranteePercentage, :MobileNo, :EmailId, :GuarantorProof, :GuarantorProofName, :Remarks ");
		insertSql.append(
				", :AddrHNbr, :FlatNbr, :AddrStreet, :AddrLine1, :AddrLine2, :POBox, :AddrCity, :AddrProvince, :AddrCountry, :AddrZIP, :GuarantorGenderCode");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return guarantorDetail.getId();
	}

	/**
	 * This method updates the Record FinGuarantorsDetails or
	 * FinGuarantorsDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Guarantor Details by key
	 * GuarantorId and Version
	 * 
	 * @param Guarantor
	 *            Details (guarantorDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(GuarantorDetail guarantorDetail, String type) {
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update FinGuarantorsDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference = :FinReference, ");
		updateSql.append(" BankCustomer = :BankCustomer, GuarantorCIF = :GuarantorCIF, ");
		updateSql.append(" GuarantorIDType = :GuarantorIDType, GuarantorIDNumber = :GuarantorIDNumber, ");
		updateSql.append(" GuarantorCIFName = :GuarantorCIFName, GuranteePercentage = :GuranteePercentage, ");
		updateSql.append(" MobileNo = :MobileNo, EmailId = :EmailId, GuarantorProofName = :GuarantorProofName,");
		if (guarantorDetail.getGuarantorProof() != null) {
			updateSql.append(" GuarantorProof = :GuarantorProof,");
		}
		updateSql.append(" AddrHNbr=:AddrHNbr, FlatNbr=:FlatNbr, AddrStreet=:AddrStreet, AddrLine1=:AddrLine1, ");
		updateSql.append(" AddrLine2=:AddrLine2, POBox=:POBox, AddrCity=:AddrCity, AddrProvince=:AddrProvince, ");
		updateSql.append(" AddrCountry=:AddrCountry, AddrZIP=:AddrZIP, GuarantorGenderCode = :GuarantorGenderCode, ");
		updateSql.append(" Remarks =:Remarks , Version = :Version , ");
		updateSql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where GuarantorId = :GuarantorId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public GuarantorDetail getGuarantorDetailByRefId(String finReference, long guarantorId, String type) {

		logger.debug("Entering");
		GuarantorDetail guarantorDetail = new GuarantorDetail();

		guarantorDetail.setFinReference(finReference);
		guarantorDetail.setGuarantorId(guarantorId);

		StringBuilder selectSql = new StringBuilder(
				"Select GuarantorId, FinReference, BankCustomer, GuarantorCIF, GuarantorIDType, GuarantorIDNumber, GuarantorCIFName, GuranteePercentage, MobileNo, EmailId, GuarantorProof, GuarantorProofName, Remarks");
		selectSql.append(
				", AddrHNbr, FlatNbr, AddrStreet, AddrLine1, AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, GuarantorGenderCode");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",GuarantorIDTypeName, custID, CustShrtName");
		}
		selectSql.append(" From FinGuarantorsDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference And GuarantorId = :GuarantorId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
		RowMapper<GuarantorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(GuarantorDetail.class);

		try {
			guarantorDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			guarantorDetail = null;
		}
		logger.debug("Leaving");
		return guarantorDetail;

	}

	@Override
	public void deleteByFinRef(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		GuarantorDetail guarantorDetail = new GuarantorDetail();
		guarantorDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinGuarantorsDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
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
	public List<GuarantorDetail> getGuarantorDetailByFinRef(String finReference, String type) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<GuarantorDetail> typeRowMapper = null;

		GuarantorDetail guarantorDetail = new GuarantorDetail();
		guarantorDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" GuarantorId, FinReference, BankCustomer, ");
		selectSql.append(" GuarantorCIF, GuarantorIDType, GuarantorIDNumber, ");
		selectSql.append(" GuarantorCIFName, GuranteePercentage, MobileNo, ");
		selectSql.append(" EmailId, GuarantorProofName, GuarantorIDTypeName, ");
		selectSql.append(" AddrHNbr, FlatNbr, AddrStreet, AddrLine1, AddrLine2, ");
		selectSql.append(" POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, ");
		selectSql.append(" Remarks, Version , LastMntBy, LastMntOn, RecordStatus, ");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, ");
		selectSql.append(" RecordType, WorkflowId,GuarantorIDTypeName,GuarantorProof, GuarantorGenderCode");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(", custID, CustShrtName ");
		}

		selectSql.append(" From FinGuarantorsDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		logger.debug("selectSql: " + selectSql.toString());

		beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GuarantorDetail.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
		}
		return null;
	}

	@Override
	public GuarantorDetail getGuarantorProof(GuarantorDetail guarantorDetail) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" GuarantorProof");
		selectSql.append(" From FinGuarantorsDetails_View");
		selectSql.append(" Where GuarantorId =:GuarantorId");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
		RowMapper<GuarantorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(GuarantorDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinanceExposure> getPrimaryExposureList(GuarantorDetail guarantorDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;

		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T1.FinReference, T1.FinStartDate, T1.MaturityDate,");
		query.append("  (T1.FinAmount + T3.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T3.FeeChargeAmt - T1.DownPayment - T3.FinRepaymentAmount) CurrentExpoSure,");
		query.append(" T1.FinCcy, T1.CustCIF,T2.ccyEditField ccyEditField,");
		query.append(
				" COALESCE((SELECT SUM(FinCurODAmt) from FinODDetails where FinReference=T1.FinReference), 0) OverdueAmt,");
		query.append(
				" COALESCE((SELECT MAX(FinCurODDays) from FinODDetails where FinReference=T1.FinReference), 0) PastdueDays");
		query.append(" FROM FinPftDetails T1 INNER JOIN RMTCurrencies T2 ON T2.CcyCode = T1.FinCcy ");
		query.append(" INNER JOIN FinanceMain T3 ON T1.FinReference = T3.FinReference ");
		query.append(" where T1.CustCIF=:GuarantorCIF ");
		query.append("  AND T1.FinIsActive = 1  ORDER BY T1.FINSTARTDATE ASC ");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
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
	public List<FinanceExposure> getSecondaryExposureList(GuarantorDetail guarantorDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;

		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T1.FinReference, T1.FinStartDate, T1.MaturityDate,");
		query.append(" (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment - T4.FinRepaymentAmount) CurrentExpoSure,");
		query.append(" T1.FinCcy, T1.CustCIF,T2.ccyEditField ccyEditField,");
		query.append(
				" COALESCE((SELECT SUM(FinCurODAmt) from FinODDetails where FinReference=T1.FinReference), 0) OverdueAmt,");
		query.append(
				" COALESCE((SELECT MAX(FinCurODDays) from FinODDetails where FinReference=T1.FinReference), 0) PastdueDays ");
		query.append(" FROM FinPftDetails T1 INNER JOIN RMTCurrencies T2 ON T2.CcyCode = T1.FinCcy ");
		query.append(" INNER JOIN FinJointAccountDetails_View T3 on T1.FinReference=T3.FinReference ");
		query.append(" INNER JOIN FinanceMain T4 on T1.FinReference=T4.FinReference ");
		query.append(" where T3.CustCIF=:GuarantorCIF ");
		query.append("  AND T1.FinIsActive = 1  ORDER BY T1.FINSTARTDATE ASC ");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
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
	public List<FinanceExposure> getGuarantorExposureList(GuarantorDetail guarantorDetail) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		RowMapper<FinanceExposure> typeRowMapper = null;
		StringBuilder query = null;

		query = new StringBuilder();
		query.append(" SELECT T1.FinType, T1.FinReference, T1.FinStartDate, T1.MaturityDate,");
		query.append(" (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment) FinanceAmt,");
		query.append(" (T1.FinAmount + T4.FeeChargeAmt - T1.DownPayment - T4.FinRepaymentAmount) CurrentExpoSure,");
		query.append(" T1.FinCcy, T1.CustCIF,T2.ccyEditField ccyEditField,");
		query.append(
				" COALESCE((SELECT SUM(FinCurODAmt) from FinODDetails where FinReference=T1.FinReference), 0) OverdueAmt,");
		query.append(
				" COALESCE((SELECT MAX(FinCurODDays) from FinODDetails where FinReference=T1.FinReference), 0) PastdueDays ");
		query.append(" FROM FinPftDetails T1 INNER JOIN RMTCurrencies T2 ON T2.CcyCode = T1.FinCcy ");
		query.append(" INNER JOIN FinGuarantorsDetails_View T3 on T1.FinReference=T3.FinReference ");
		query.append(" INNER JOIN FinanceMain T4 on T1.FinReference=T4.FinReference ");
		query.append(" where T3.GuarantorCIF=:GuarantorCIF ");
		query.append("  AND T1.FinIsActive = 1  ORDER BY T1.FINSTARTDATE ASC ");

		logger.debug("selectSql: " + query.toString());
		beanParameters = new BeanPropertySqlParameterSource(guarantorDetail);
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
		try {
			return this.jdbcTemplate.queryForObject(query.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			beanParameters = null;
			typeRowMapper = null;
			query = null;
		}
		return null;
	}

}