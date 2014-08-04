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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.others.JVPostingEntryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>JVPostingEntry model</b> class.<br>
 * 
 */

public class JVPostingEntryDAOImpl extends BasisCodeDAO<JVPostingEntry> implements JVPostingEntryDAO {

	private static Logger logger = Logger.getLogger(JVPostingEntryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
			selectSql.append(",TxnCCyName,TxnCCyEditField,TxnDesc,AccCCyName,AccCCyEditField,AcCcyNumber,TxnCcyNumber");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference AND AcEntryRef=:AcEntryRef");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(JVPostingEntry.class);

		try {
			jVPostingEntry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
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
			selectSql.append(",TxnCCyName,TxnCCyEditField,TxnDesc,AccCCyName,AccCCyEditField,AcCcyNumber,TxnCcyNumber");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference AND ExternalAccount=1");

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
		jVPostingEntry.setPostingStatus(PennantConstants.Posting_success);
		StringBuilder selectSql = new StringBuilder(
		        "Select  FileName, BatchReference,AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus , PostingStatus, ExternalAccount, LinkedTranId");
		selectSql
		        .append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",TxnCCyName,TxnCCyEditField,TxnDesc,AccCCyName,AccCCyEditField,AcCcyNumber,TxnCcyNumber");
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
	 * This method initialise the Record.
	 * 
	 * @param JVPostingEntry
	 *            (jVPostingEntry)
	 * @return JVPostingEntry
	 */
	@Override
	public void initialize(JVPostingEntry jVPostingEntry) {
		super.initialize(jVPostingEntry);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param JVPostingEntry
	 *            (jVPostingEntry)
	 * @return void
	 */
	@Override
	public void refresh(JVPostingEntry jVPostingEntry) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails = getError("41003",
				        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails()
				                .getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
			aJVPostingEntry.setDaySeqDate(DateUtility.getSystemDate());
			aJVPostingEntry.setDaySeqNo(0);
			jVPostingEntry.setTxnReference(getMaxSeqNumForCurrentDay(aJVPostingEntry) + 1);

			// Updating Latest Sequence No in Entry table for Current Day and Current Batch File 
			aJVPostingEntry.setDaySeqNo((int) jVPostingEntry.getTxnReference());
			upDateSeqNoForCurrentDayBatch(aJVPostingEntry);
		}
		StringBuilder insertSql = new StringBuilder("Insert Into JVPostingEntry");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
		        .append(" (FileName, BatchReference, AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy, TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac, ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount, LinkedTranId");
		insertSql
		        .append(",Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:FileName, :BatchReference, :AcEntryRef, :HostSeqNo, :Account, :AcType, :AccountName, :TxnCCy, :TxnEntry, :AccCCy, :TxnCode, :PostingDate, :ValueDate, :TxnAmount, :TxnReference, :NarrLine1, :NarrLine2, :NarrLine3, :NarrLine4, :ExchRate_Batch, :ExchRate_Ac, :TxnAmount_Batch, :TxnAmount_Ac, :ModifiedFlag, :DeletedFlag, :ValidationStatus, :PostingStatus, :ExternalAccount, :LinkedTranId");
		insertSql
		        .append(",:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

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
		StringBuilder selectSql = new StringBuilder("select MAX(TxnReference) AS TxnReference  ");
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		nextTxnReference = this.namedParameterJdbcTemplate.queryForLong(selectSql.toString(),
		        beanParameters);
		nextTxnReference = nextTxnReference + 1;
		try {
		} catch (EmptyResultDataAccessException e) {
			jVPostingEntry = null;
		}
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

	@SuppressWarnings("serial")
	@Override
	public void update(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set FileName=:FileName, BatchReference = :BatchReference,AcEntryRef = :AcEntryRef, HostSeqNo = :HostSeqNo, Account = :Account, AcType = :AcType, AccountName = :AccountName, TxnCCy = :TxnCCy, AccCCy=:AccCCy, TxnCode = :TxnCode, TxnEntry = :TxnEntry, PostingDate = :PostingDate, ValueDate = :ValueDate, TxnAmount = :TxnAmount, TxnReference = :TxnReference, NarrLine1 = :NarrLine1, NarrLine2 = :NarrLine2, NarrLine3 = :NarrLine3, NarrLine4 = :NarrLine4, ExchRate_Batch = :ExchRate_Batch, ExchRate_Ac = :ExchRate_Ac, TxnAmount_Batch = :TxnAmount_Batch, TxnAmount_Ac = :TxnAmount_Ac, ModifiedFlag = :ModifiedFlag, DeletedFlag = :DeletedFlag, ValidationStatus = :ValidationStatus, PostingStatus = :PostingStatus, ExternalAccount=:ExternalAccount, LinkedTranId=:LinkedTranId");
		updateSql
		        .append(", Version=:Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference  AND AcEntryRef = :AcEntryRef");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	// TODO
	@SuppressWarnings("serial")
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
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
	@Override
	public void updateDeletedDetails(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set FileName=:FileName, BatchReference = :BatchReference, AcEntryRef = :AcEntryRef, HostSeqNo = :HostSeqNo, Account = :Account,  AcType = :AcType, AccountName = :AccountName, TxnCCy = :TxnCCy, AccCCy=:AccCCy, TxnCode = :TxnCode, TxnEntry = :TxnEntry, PostingDate = :PostingDate, ValueDate = :ValueDate, TxnAmount = :TxnAmount, TxnReference = :TxnReference, NarrLine1 = :NarrLine1, NarrLine2 = :NarrLine2, NarrLine3 = :NarrLine3, NarrLine4 = :NarrLine4, ExchRate_Batch = :ExchRate_Batch, ExchRate_Ac = :ExchRate_Ac, TxnAmount_Batch = :TxnAmount_Batch, TxnAmount_Ac = :TxnAmount_Ac, ModifiedFlag = :ModifiedFlag, DeletedFlag = :DeletedFlag, ValidationStatus = :ValidationStatus, PostingStatus = :PostingStatus, ExternalAccount=:ExternalAccount, LinkedTranId=:LinkedTranId ");
		updateSql
		        .append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, WorkflowId = :WorkflowId");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND TxnReference = :TxnReference AND AcEntryRef = :AcEntryRef");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
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
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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

	@SuppressWarnings("serial")
	@Override
	public void updateWorkFlowDetails(JVPostingEntry jVPostingEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set BatchReference = :BatchReference, LastMntBy = :LastMntBy, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, WorkflowId = :WorkflowId, RecordStatus= :RecordStatus");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND Account = :Account AND DeletedFlag = '0'");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateDelteEntryDetails(JVPostingEntry jVPostingEntry, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set BatchReference = :BatchReference, LastMntBy = :LastMntBy, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, WorkflowId = :WorkflowId, RecordStatus= :RecordStatus");
		updateSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = '1'");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
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
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String batchReference,
	        String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = batchReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_BatchReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
		        errorId, parms[0], parms[1]), userLanguage);
	}

	@SuppressWarnings("serial")
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
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = '0' ");
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
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = '0' ");
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
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = '0' ");
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
			selectSql.append(",TxnCCyName,TxnCCyEditField,TxnDesc,AccCCyName,AccCCyEditField,AcCcyNumber,TxnCcyNumber");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
		        .append(" Where BatchReference =:BatchReference AND DeletedFlag = '1' ");
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
		        "Select MAX(SeqNo) AS DaySeqNo From SeqJVPostingEntry Where SeqDate = :DaySeqDate");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		count = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);
		if (count == 0) {
			selectSql = new StringBuilder(
			        "Insert into SeqJVPostingEntry (SeqDate, SeqNo)");
			selectSql.append(" Values (:DaySeqDate, :DaySeqNo)");
			logger.debug("inserttSql: " + selectSql.toString());
			SqlParameterSource beanParameters1 = new BeanPropertySqlParameterSource(jVPostingEntry);
			this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters1);

			selectSql = new StringBuilder(
			        "Select MAX(SeqNo) AS DaySeqNo From SeqJVPostingEntry Where SeqDate = :DaySeqDate");
			logger.debug("selectSql: " + selectSql.toString());
			SqlParameterSource beanParameters2 = new BeanPropertySqlParameterSource(jVPostingEntry);
			count = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(),
			        beanParameters2);
		}
		try {
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		logger.debug("Leaving");
		return count;
	}

	@SuppressWarnings("serial")
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
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
			        jVPostingEntry.getId() + "", jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
			logger.error(e);
		}
		logger.debug("Leaving");	    
    }

}