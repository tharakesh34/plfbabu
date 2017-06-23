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
 * FileName    		:  ManualAdviseDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinTaxUploadDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of CRUD operations.
 */
public class FinTaxUploadDetailDAOImpl extends BasisNextidDaoImpl<FinTaxUploadHeader> implements FinTaxUploadDetailDAO {
	private static Logger				logger	= Logger.getLogger(ManualAdviseDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinTaxUploadDetailDAOImpl() {
		super();
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<FinTaxUploadDetail> getFinTaxDetailUploadById(String reference) {
		return null;
	}

	@Override
	public void update(FinTaxUploadHeader finTaxUploadHeader, String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(FinTaxUploadHeader finTaxUploadHeader, String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(FinTaxUploadHeader finTaxUploadHeader, String type) {

		logger.debug("Entering");
		if (finTaxUploadHeader.getId() <= 0) {
			finTaxUploadHeader.setId(getNextId("SeqFeePostings"));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into finTaxUploadHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BatchReference, FileName, NumberofRecords, BatchCreatedDate,BatchApprovedDate,");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				"  Values(:BatchReference, :FileName, :NumberofRecords, :BatchCreatedDate, :BatchApprovedDate,");
		insertSql.append(
				"  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxUploadHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void saveFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type) {

		logger.debug("Entering");
		
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into finTaxUploadDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (BatchReference, TaxCode, AggrementNo, ApplicableFor,Applicant,TaxExempted,AddrLine1,AddrLine2,AddrLine3,AddrLine4,");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				"  Values(:BatchReference, :TaxCode, :AggrementNo, :ApplicableFor,:Applicant,:TaxExempted,:AddrLine1,:AddrLine2,:AddrLine3,:AddrLine4,");
		insertSql.append(
				"  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxUploadDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void updateFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type) {
		// TODO Auto-generated method stub

	}

}
