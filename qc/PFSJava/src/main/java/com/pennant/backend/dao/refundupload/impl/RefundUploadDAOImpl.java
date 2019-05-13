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
 * FileName    		:  RefundUploadDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2018    														*
 *                                                                  						*
 * Modified Date    :  08-10-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2018       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.refundupload.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.refundupload.RefundUploadDAO;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class RefundUploadDAOImpl extends SequenceDao<RefundUpload> implements RefundUploadDAO {
	private static Logger logger = Logger.getLogger(RefundUploadDAOImpl.class);

	public RefundUploadDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<RefundUpload> getRefundUploadsByUploadId(long uploadId, String type) {
		logger.debug("Entering");
		RefundUpload refundupload = new RefundUpload();
		refundupload.setUploadId(uploadId);

		StringBuilder selectSql = new StringBuilder(
				"Select RefundId, UploadId, FinReference, PayableAmount, Type, FeeType, PaymentDate, PaymentType, PartnerBank, Remarks, IFSC, MICR, AccountNumber,");
		selectSql.append(
				"AccountHolderName, PhoneNumber, IssuingBank, FavourName, PayableLocation, PrintingLocation, ValueDate, Status, RejectReason,");
		if (type.contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM RefundUploads");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UploadId = :UploadId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(refundupload);
		RowMapper<RefundUpload> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RefundUpload.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *            Types (financeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(RefundUpload refundupload, String type) {
		logger.debug("Entering ");

		if (refundupload.getRefundId() == Long.MIN_VALUE) {
			refundupload.setRefundId(getNextValue("SeqRefundUploads"));
			logger.debug("get NextID:" + refundupload.getRefundId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into RefundUploads");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (RefundId, UploadId, FinReference, PayableAmount, Type, FeeType, PaymentDate, PaymentType, PartnerBank, Remarks, IFSC, MICR, AccountNumber,");
		insertSql.append(
				" AccountHolderName, PhoneNumber, IssuingBank, FavourName, PayableLocation, PrintingLocation, ValueDate, Status, RejectReason,");
		insertSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:RefundId, :UploadId, :FinReference, :PayableAmount, :Type, :FeeType, :PaymentDate, :PaymentType, :PartnerBank, :Remarks, :IFSC, :MICR, :AccountNumber,");
		insertSql.append(
				" :AccountHolderName, :PhoneNumber, :IssuingBank, :FavourName, :PayableLocation, :PrintingLocation, :ValueDate, :Status, :RejectReason,");
		insertSql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(refundupload);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");

		return refundupload.getFinReference();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param Finance
	 *            Types (financeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(RefundUpload refundupload, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update RefundUploads");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference = :FinReference, PayableAmount = :PayableAmount, Type = :Type, ");
		updateSql.append(" FeeType = :FeeType, PaymentDate = :PaymentDate, PaymentType = :PaymentType,");
		updateSql.append(
				" PartnerBank = :PartnerBank, Remarks = :Remarks, IFSC = :IFSC, MICR = :MICR, AccountNumber = :AccountNumber,");
		updateSql.append(
				" AccountHolderName = :AccountHolderName, PhoneNumber = :PhoneNumber, IssuingBank = :IssuingBank, FavourName = :FavourName,");
		updateSql.append(" PayableLocation = :PayableLocation, PrintingLocation = :PrintingLocation,");
		updateSql.append(" ValueDate = :ValueDate, Status = :Status, RejectReason = :RejectReason, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId,  NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where UploadId = :UploadId And RefundId = :RefundId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(refundupload);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving ");
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceType
	 *            (financeType)
	 * @return FinanceType
	 */

	@Override
	public void deleteByUploadId(long uploadId, String type) {
		logger.debug("Entering");

		RefundUpload refundupload = new RefundUpload();
		refundupload.setUploadId(uploadId);
		StringBuilder deleteSql = new StringBuilder("Delete From RefundUploads");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where UploadId = :UploadId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(refundupload);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
	}

	@Override
	public boolean getRefundUploadsByFinReference(String finReference, long uploadId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" Select Count(FinReference) from RefundUploads");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference And Status = :Status");
		if (uploadId > 0) {
			sql.append(" And UploadId != :UploadId");
		}
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Status", UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS);

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
}