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
 * FileName    		:  JVPostingDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.others.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>JVPosting model</b> class.<br>
 * 
 */
public class JVPostingDAOImpl extends SequenceDao<JVPosting> implements JVPostingDAO {
	private static Logger logger = Logger.getLogger(JVPostingDAOImpl.class);

	
	public JVPostingDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new JVPosting
	 * 
	 * @return JVPosting
	 */

	@Override
	public JVPosting getJVPosting() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("JVPosting");
		JVPosting jVPosting = new JVPosting();
		if (workFlowDetails != null) {
			jVPosting.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return jVPosting;
	}

	/**
	 * This method get the module from method getJVPosting() and set the new record flag as true and return JVPosting()
	 * 
	 * @return JVPosting
	 */

	@Override
	public JVPosting getNewJVPosting() {
		logger.debug("Entering");
		JVPosting jVPosting = getJVPosting();
		jVPosting.setNewRecord(true);
		logger.debug("Leaving");
		return jVPosting;
	}

	/**
	 * Fetch the Record JV Posting Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPosting
	 */
	@Override
	public JVPosting getJVPostingById(final long id, String type) {
		logger.debug("Entering");
		JVPosting jVPosting = getJVPosting();

		jVPosting.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select BatchReference, Batch, PostingDate, Filename, Branch, DebitCount, CreditsCount, TotDebitsByBatchCcy, TotCreditsByBatchCcy, BatchPurpose, Currency, ExchangeRateType, ValidationStatus, BatchPostingStatus,PostAgainst,Reference,postingDivision");
		selectSql
		        .append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CurrencyDesc,divisionCodeDesc");
		}
		selectSql.append(" From JVPostings");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		RowMapper<JVPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPosting.class);

		try {
			jVPosting = this.jdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jVPosting = null;
		}
		logger.debug("Leaving");
		return jVPosting;
	}

	@Override
	public JVPosting getJVPostingByFileName(String batchName) {
		logger.debug("Entering");
		JVPosting jVPosting = getJVPosting();

		StringBuilder selectSql = new StringBuilder(
		        "Select *  From JVPostings_View");
		selectSql.append(" Where Batch ='"+batchName+"' AND PostingDate='"+DateUtility.format(DateUtility.getSysDate(), PennantConstants.DBDateTimeFormat)+"'");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		RowMapper<JVPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPosting.class);

		logger.debug("Leaving");
		try {
			jVPosting = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
		        typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jVPosting = null;
		}
		logger.debug("Leaving");
		return jVPosting;
	}

	

	/**
	 * This method Deletes the Record from the JVPostings or JVPostings_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete JV Posting Details by key BatchReference
	 * 
	 * @param JV
	 *            Posting Details (jVPosting)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(JVPosting jVPosting, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From JVPostings");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BatchReference =:BatchReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into JVPostings or JVPostings_Temp.
	 * 
	 * save JV Posting Details
	 * 
	 * @param JV
	 *            Posting Details (jVPosting)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(JVPosting jVPosting, String type) {
		logger.debug("Entering");
		if (jVPosting.isNewRecord() && jVPosting.getBatchReference() == 0) {
			jVPosting.setBatchReference(getNextId("SeqJVpostings"));
		}
		logger.debug("get NextID:" + jVPosting.getId());

		StringBuilder insertSql = new StringBuilder("Insert Into JVPostings");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
		        .append(" (BatchReference,PostingDate, Batch, Filename,Branch, DebitCount, CreditsCount, TotDebitsByBatchCcy, TotCreditsByBatchCcy, BatchPurpose, Currency, ExchangeRateType, ValidationStatus , BatchPostingStatus,ExpReference,Reference,PostAgainst,PostingDivision");
		insertSql
		        .append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:BatchReference,:PostingDate, :Batch,  :Filename,:Branch, :DebitCount, :CreditsCount, :TotDebitsByBatchCcy, :TotCreditsByBatchCcy, :BatchPurpose, :Currency, :ExchangeRateType, :ValidationStatus, :BatchPostingStatus,:ExpReference,:Reference,:PostAgainst,:PostingDivision");
		insertSql
		        .append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return jVPosting.getBatchReference();
	}

	/**
	 * This method updates the Record JVPostings or JVPostings_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update JV Posting Details by key BatchReference and Version
	 * 
	 * @param JV
	 *            Posting Details (jVPosting)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(JVPosting jVPosting, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostings");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set PostingDate=:PostingDate, Batch = :Batch, Filename = :Filename,Branch=:Branch, DebitCount = :DebitCount, CreditsCount = :CreditsCount, TotDebitsByBatchCcy = :TotDebitsByBatchCcy, TotCreditsByBatchCcy = :TotCreditsByBatchCcy, BatchPurpose = :BatchPurpose, Currency = :Currency, ExchangeRateType = :ExchangeRateType, "
		        		+ "ValidationStatus = :ValidationStatus, BatchPostingStatus = :BatchPostingStatus ,ExpReference =:ExpReference,Reference =:Reference,PostAgainst =:PostAgainst,PostingDivision =:PostingDivision");
		updateSql
		        .append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BatchReference =:BatchReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateHeaderDetails(JVPosting jVPosting, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostings");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set  PostingDate=:PostingDate, Batch = :Batch, Filename = :Filename,Branch=:Branch, DebitCount = :DebitCount, CreditsCount = :CreditsCount, TotDebitsByBatchCcy = :TotDebitsByBatchCcy, TotCreditsByBatchCcy = :TotCreditsByBatchCcy, BatchPurpose = :BatchPurpose, Currency = :Currency, ExchangeRateType = :ExchangeRateType, ValidationStatus = :ValidationStatus, BatchPostingStatus = :BatchPostingStatus");
		updateSql
		        .append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BatchReference =:BatchReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateValidationStatus(JVPosting jVPosting, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostings");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ValidationStatus = :ValidationStatus");
		updateSql.append(" Where BatchReference =:BatchReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateBatchPostingStatus(JVPosting jVPosting, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostings");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set BatchPostingStatus = :BatchPostingStatus");
		updateSql.append(" Where BatchReference =:BatchReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}


	/**
	 * Fetch the Max Seq Number From SeqJVPostings
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPosting
	 */
	public long getMaxSeqNum(JVPosting jvPosting) {
		logger.debug("Entering");
		long count = 0;
		StringBuilder selectSql = new StringBuilder("Select COALESCE(MAX(SeqNo),0)");
		selectSql.append(" From SeqJVPostings");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jvPosting);
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		jvPosting = null;
		logger.debug("Leaving");
		return count;
	}

	@Override
	public long getBatchRerbyExpRef(String expReference) {
		logger.debug("Entering");
		long ref = 0;
		JVPosting jvPosting= new JVPosting();
		jvPosting.setExpReference(expReference);
		
		StringBuilder selectSql = new StringBuilder("Select batchreference from JVPostings_view where ExpReference=:ExpReference");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jvPosting);
		try {
			ref = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			ref = 0;
		}
		jvPosting = null;
		logger.debug("Leaving");
		return ref;
		}

}