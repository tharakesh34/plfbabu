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
 * FileName    		:  JVPostingEntryDAOImpl.java                                                   * 	  
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

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.others.JVPostingEntryDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>JVPostingEntry model</b> class.<br>
 * 
 */
public class JVPostingEntryDAOImpl extends BasisCodeDAO<JVPostingEntry> implements JVPostingEntryDAO {
	private static Logger logger = Logger.getLogger(JVPostingEntryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public JVPostingEntryDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new JVPostingEntry
	 * 
	 * @return JVPostingEntry
	 */

	/**
	 * This method get the module from method getJVPostingEntry() and set the new record flag as true and return
	 * JVPostingEntry()
	 * 
	 * @return JVPostingEntry
	 */

	@Override
	public JVPostingEntry getNewJVPostingEntry() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("JVPostingEntry");
		JVPostingEntry jVPostingEntry = new JVPostingEntry();
		if (workFlowDetails != null) {
			jVPostingEntry.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		jVPostingEntry.setNewRecord(true);
		logger.debug("Leaving");
		return jVPostingEntry;
	}

	/**
	 * Fetch the Record JV Posting Entry details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPostingEntry
	 */
	@Override
	public JVPostingEntry getJVPostingEntryById(final long id,
	        long txnReference, long acEntryRef, String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getNewJVPostingEntry();
		jVPostingEntry.setId(id);
		jVPostingEntry.setTxnReference(txnReference);
		jVPostingEntry.setAcEntryRef(acEntryRef);
		

		StringBuilder selectSql = new StringBuilder(
		        "Select  FileName,BatchReference, AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount, LinkedTranId");
		selectSql
		        .append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",TxnDesc");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference AND AcEntryRef=:AcEntryRef");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		try {
			jVPostingEntry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jVPostingEntry = null;
		}
		logger.debug("Leaving");
		return jVPostingEntry;
	}

	/**
	 * Fetch the Record JV Posting Entry details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPostingEntry
	 */
	@Override
	public List<JVPostingEntry> getJVPostingEntryListById(final long id,
	        String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getNewJVPostingEntry();
		jVPostingEntry.setId(id);
		StringBuilder selectSql = new StringBuilder(
		        "Select  FileName, BatchReference,AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus , PostingStatus, ExternalAccount,LinkedTranId");
		selectSql
		        .append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",TxnDesc,derivedTxnRef");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference AND ExternalAccount=1 order by TxnReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	/**
	 * Fetch the Record JV Posting Entry details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return JVPostingEntry
	 */
	@Override
	public List<JVPostingEntry> getFailureJVPostingEntryListById(final long id,
	        String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getNewJVPostingEntry();
		jVPostingEntry.setId(id);
		jVPostingEntry.setPostingStatus(PennantConstants.POSTSTS_SUCCESS);
		StringBuilder selectSql = new StringBuilder(
		        "Select  FileName, BatchReference,AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus , PostingStatus, ExternalAccount, LinkedTranId");
		selectSql
		        .append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",TxnDesc");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference AND PostingStatus !=:PostingStatus ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
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
	 * This method Deletes the Record from the JVPostingEntry or JVPostingEntry_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete JV Posting Entry by key BatchReference
	 * 
	 * @param JV
	 *            Posting Entry (jVPostingEntry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From JVPostingEntry");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql
		        .append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference AND AcEntryRef=:AcEntryRef");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into JVPostingEntry or JVPostingEntry_Temp.
	 * 
	 * save JV Posting Entry
	 * 
	 * @param JV
	 *            Posting Entry (jVPostingEntry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");

		if (jVPostingEntry.getTxnReference() == 0) {
			// Getting max Sequence Number Form Entry Sequence Table for Current Day and Current Batch File
			JVPostingEntry aJVPostingEntry = getNewJVPostingEntry();
			aJVPostingEntry.setDaySeqDate(DateUtility.getDBDate(DateUtility.format(DateUtility.getSysDate(),PennantConstants.DBDateFormat)));
			aJVPostingEntry.setDaySeqNo(0);
			jVPostingEntry.setTxnReference(getMaxSeqNumForCurrentDay(aJVPostingEntry) + 1);

			// Updating Latest Sequence No in Entry table for Current Day and Current Batch File 
			aJVPostingEntry.setDaySeqNo((int) jVPostingEntry.getTxnReference());
			upDateSeqNoForCurrentDayBatch(aJVPostingEntry);
			if(!jVPostingEntry.getTxnEntry().equals(AccountConstants.TRANTYPE_CREDIT)){
				jVPostingEntry.setDerivedTxnRef(jVPostingEntry.getTxnReference()-1);
			}
		}
		StringBuilder insertSql = new StringBuilder("Insert Into JVPostingEntry");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
		        .append(" (FileName, BatchReference, AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount, LinkedTranId");
		insertSql
		        .append(",Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DerivedTxnRef)");
		insertSql
		        .append(" Values(:FileName, :BatchReference, :AcEntryRef, :HostSeqNo, :Account, :AcType, :AccountName, :TxnCCy, :TxnEntry, :AccCCy, :TxnCode, :PostingDate, :ValueDate, :TxnAmount, :TxnReference, :NarrLine1, :NarrLine2, :NarrLine3, :NarrLine4, :ExchRate_Batch, :ExchRate_Ac, :TxnAmount_Batch, :TxnAmount_Ac, :ModifiedFlag, :DeletedFlag, :ValidationStatus, :PostingStatus, :ExternalAccount, :LinkedTranId");
		insertSql
		        .append(",:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId ,:DerivedTxnRef)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return jVPostingEntry.getId();
	}

	/**
	 * This method insert new Records into JVPostingEntry or JVPostingEntry_Temp.
	 * 
	 * save JV Posting Entry
	 * 
	 * @param JV
	 *            Posting Entry (jVPostingEntry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void saveJVPostingEntryList(List<JVPostingEntry> aJVPostingEntryList, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into JVPostingEntry");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
		        .append(" (FileName, BatchReference, AcEntryRef, AcType, HostSeqNo, Account, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount, LinkedTranId");
		insertSql
		        .append(",Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:FileName, :BatchReference, :AcEntryRef, :AcType, :HostSeqNo, :Account,:AccountName, :TxnCCy, :TxnEntry, :AccCCy, :TxnCode, :PostingDate, :ValueDate, :TxnAmount, :TxnReference, :NarrLine1, :NarrLine2, :NarrLine3, :NarrLine4, :ExchRate_Batch, :ExchRate_Ac, :TxnAmount_Batch, :TxnAmount_Ac, :ModifiedFlag, :DeletedFlag, :ValidationStatus, :PostingStatus, :ExternalAccount,:LinkedTranId");
		insertSql
		        .append(",:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(aJVPostingEntryList.toArray());
		int[] cont = this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(),
		        beanParameters);
		logger.debug("Leaving Updated Count ==" + cont.length);
	}

	public long getNextTxnReference(JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");
		long nextTxnReference = 0;
		StringBuilder selectSql = new StringBuilder("select COALESCE(MAX(TxnReference),0) TxnReference  ");
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		try {
			nextTxnReference = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, Long.class);
			nextTxnReference = nextTxnReference + 1;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			nextTxnReference = 0;
			nextTxnReference = nextTxnReference + 1;
		}
		jVPostingEntry = null;
		logger.debug("Leaving NextTxnReference = " + nextTxnReference);
		return nextTxnReference;
	}

	/**
	 * This method updates the Record JVPostingEntry or JVPostingEntry_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update JV Posting Entry by key BatchReference and Version
	 * 
	 * @param JV
	 *            Posting Entry (jVPostingEntry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set FileName=:FileName, HostSeqNo = :HostSeqNo, Account = :Account, AcType = :AcType, AccountName = :AccountName, TxnCCy = :TxnCCy, AccCCy=:AccCCy, TxnCode = :TxnCode, TxnEntry = :TxnEntry, PostingDate = :PostingDate, ValueDate = :ValueDate, TxnAmount = :TxnAmount, NarrLine1 = :NarrLine1, NarrLine2 = :NarrLine2, NarrLine3 = :NarrLine3, NarrLine4 = :NarrLine4, ExchRate_Batch = :ExchRate_Batch, ExchRate_Ac = :ExchRate_Ac, TxnAmount_Batch = :TxnAmount_Batch, TxnAmount_Ac = :TxnAmount_Ac, ModifiedFlag = :ModifiedFlag, DeletedFlag = :DeletedFlag, ValidationStatus = :ValidationStatus, PostingStatus = :PostingStatus, ExternalAccount=:ExternalAccount, LinkedTranId=:LinkedTranId");
		updateSql
		        .append(", Version=:Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference  AND AcEntryRef = :AcEntryRef");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateDeleteFlag(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DeletedFlag = :DeletedFlag ");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference  AND AcEntryRef = :AcEntryRef ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateDeletedDetails(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set FileName=:FileName, HostSeqNo = :HostSeqNo, Account = :Account,  AcType = :AcType, AccountName = :AccountName, TxnCCy = :TxnCCy, AccCCy=:AccCCy, TxnCode = :TxnCode, TxnEntry = :TxnEntry, PostingDate = :PostingDate, ValueDate = :ValueDate, TxnAmount = :TxnAmount, NarrLine1 = :NarrLine1, NarrLine2 = :NarrLine2, NarrLine3 = :NarrLine3, NarrLine4 = :NarrLine4, ExchRate_Batch = :ExchRate_Batch, ExchRate_Ac = :ExchRate_Ac, TxnAmount_Batch = :TxnAmount_Batch, TxnAmount_Ac = :TxnAmount_Ac, ModifiedFlag = :ModifiedFlag, DeletedFlag = :DeletedFlag, ValidationStatus = :ValidationStatus, PostingStatus = :PostingStatus, ExternalAccount=:ExternalAccount, LinkedTranId=:LinkedTranId ");
		updateSql
		        .append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, WorkflowId = :WorkflowId");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference AND AcEntryRef = :AcEntryRef");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateValidationStatus(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set  AcType=:AcType, AccCCy=:AccCCy, AccountName=:AccountName, ValidationStatus = :ValidationStatus, PostingStatus = :PostingStatus, LinkedTranId=:LinkedTranId");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND Account = :Account ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateListValidationStatus(List<JVPostingEntry> aJVPostingEntryList, String type,
	        boolean isAccountWise) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set  AcType=:AcType, AccCCy=:AccCCy, AccountName=:AccountName,ValidationStatus = :ValidationStatus, PostingStatus = :PostingStatus,LinkedTranId=:LinkedTranId");
		if (isAccountWise) {
			updateSql
			        .append(" Where BatchReference =:BatchReference AND Account = :Account ");
		} else {
			updateSql.append(" Where BatchReference =:BatchReference ");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(aJVPostingEntryList.toArray());
		int[] cont = this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(),
		        beanParameters);
		logger.debug("Leaving Updated Count ==" + cont.length);
	}

	@Override
	public void updateWorkFlowDetails(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set LastMntBy = :LastMntBy, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, WorkflowId = :WorkflowId, RecordStatus= :RecordStatus");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND Account = :Account AND DeletedFlag = 0");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateDelteEntryDetails(JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set LastMntBy = :LastMntBy, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, WorkflowId = :WorkflowId, RecordStatus= :RecordStatus");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = 1");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updatePostingStatus(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  PostingStatus = :PostingStatus");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference AND AcEntryRef = :AcEntryRef ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}


	@Override
	public void deleteByID(JVPostingEntry jVPostingEntry, String tableType) {
		logger.debug("Entering");
		JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		aJVPostingEntry.setBatchReference(jVPostingEntry.getBatchReference());
		aJVPostingEntry.setTxnReference(jVPostingEntry.getTxnReference());
		StringBuilder deleteSql = new StringBuilder("Delete From JVPostingEntry");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference AND AcEntryRef = :AcEntryRef ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	@Override
	public JVPostingEntry getJVPostingEntryById(long batchRef,
	        long txnReference, String account, String txnEntry, BigDecimal txnAmount, String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getNewJVPostingEntry();
		jVPostingEntry.setBatchReference(batchRef);
		jVPostingEntry.setTxnReference(txnReference);
		jVPostingEntry.setTxnEntry(txnEntry);
		jVPostingEntry.setAccount(account);
		jVPostingEntry.setTxnAmount(txnAmount);

		StringBuilder selectSql = new StringBuilder(
		        "Select FileName, BatchReference, AcEntryRef, HostSeqNo, Account,  AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount,LinkedTranId ");
		selectSql
		        .append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",TxnCCyName,TxnCCyEditField,TxnDesc,AccCCyName,AccCCyEditField,AcCcyNumber,TxnCcyNumber");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference AND TxnEntry = :TxnEntry AND Account = :Account AND TxnAmount = :TxnAmount ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		try {
			jVPostingEntry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jVPostingEntry = null;
		}
		logger.debug("Leaving");
		return jVPostingEntry;
	}

	@Override
	public List<JVPostingEntry> getDistinctJVPostingEntryListById(JVPostingEntry jVPostingEntry,
	        String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("select DISTINCT Account, AcType, ExternalAccount,AccCcy ");
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = 0 ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	@Override
	public List<JVPostingEntry> getDistinctJVPostingEntryValidationStatusById(
	        JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("select distinct ValidationStatus");
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = 0 ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	@Override
	public List<JVPostingEntry> getDistinctJVPostingEntryPostingStatusById(
	        JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("select distinct PostingStatus");
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = 0 ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	@Override
	public List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef,
	        String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getNewJVPostingEntry();
		jVPostingEntry.setBatchReference(batchRef);
		StringBuilder selectSql = new StringBuilder(
		        "Select FileName, BatchReference, HostSeqNo, Account,  AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus , ExternalAccount, LinkedTranId");
		selectSql
		        .append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",TxnDesc");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = 1 ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	@Override
	public void updateListPostingStatus(List<JVPostingEntry> aJVPostingEntryList, String type,
	        boolean isTxnRefWise) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  PostingStatus = :PostingStatus");
		if (isTxnRefWise) {
			updateSql
			        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference AND AcEntryRef=:AcEntryRef ");
		} else {
			updateSql.append(" Where BatchReference =:BatchReference ");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(aJVPostingEntryList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Entering");
	}

	@Override
	public int getMaxSeqNumForCurrentDay(JVPostingEntry jVPostingEntry) {
		logger.debug("Entering");
		int count = 0;
		StringBuilder selectSql = new StringBuilder(
		        "Select COALESCE(MAX(SeqNo),0) DaySeqNo From SeqJVPostingEntry Where SeqDate = :DaySeqDate");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
        	logger.warn("Exception: ", e);
        	count = 0;
        }
		if (count == 0) {
			selectSql = new StringBuilder(
			        "Insert into SeqJVPostingEntry (SeqDate, SeqNo)");
			selectSql.append(" Values (:DaySeqDate, :DaySeqNo)");
			logger.debug("inserttSql: " + selectSql.toString());
			SqlParameterSource beanParameters1 = new BeanPropertySqlParameterSource(jVPostingEntry);
			this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters1);

			selectSql = new StringBuilder(
			        "Select COALESCE(MAX(SeqNo),0) DaySeqNo From SeqJVPostingEntry Where SeqDate = :DaySeqDate");
			logger.debug("selectSql: " + selectSql.toString());
			SqlParameterSource beanParameters2 = new BeanPropertySqlParameterSource(jVPostingEntry);
			try {
				count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
						beanParameters2, Integer.class);
			} catch (EmptyResultDataAccessException e) {
				logger.warn("Exception: ", e);
				count = 0;
			}
		}
		
		logger.debug("Leaving");
		return count;
	}

	@Override
	public void upDateSeqNoForCurrentDayBatch(JVPostingEntry jVPostingEntry) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update SeqJVPostingEntry");
		updateSql.append(" Set  SeqNo = :DaySeqNo");
		updateSql.append(" Where SeqDate = :DaySeqDate");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
    public void deleteIAEntries(long batchReference) {
		logger.debug("Entering");
		JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		aJVPostingEntry.setBatchReference(batchReference);
		StringBuilder deleteSql = new StringBuilder("Delete From JVPostingEntry_Temp");
		deleteSql
		        .append(" Where BatchReference =:BatchReference AND ExternalAccount = 0 ");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aJVPostingEntry);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");	    
    }

	@Override
	public JVPostingEntry getJVPostingEntrybyDerivedTxnRef(long derivedTxnRef,long batchReference) {

		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getNewJVPostingEntry();
		jVPostingEntry.setDerivedTxnRef(derivedTxnRef);
		jVPostingEntry.setBatchReference(batchReference);
		
		StringBuilder selectSql = new StringBuilder(
		        "select T1.Account,T1.TxnCode,T2.TranDesc as DebitTxnDesc from JVPostingEntry_view T1,BMTTransactionCode T2 ");
		selectSql
		        .append(" where T1.TxnCode=T2.TranCode and T1.DerivedTxnRef =:DerivedTxnRef and T1.BatchReference =:BatchReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		try {
			jVPostingEntry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			jVPostingEntry = null;
		}
		logger.debug("Leaving");
		return jVPostingEntry;
	
	}

}