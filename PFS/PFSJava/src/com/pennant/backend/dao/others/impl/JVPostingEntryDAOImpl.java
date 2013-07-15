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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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
	 * @return JVPostingEntry
	 */

	@Override
	public JVPostingEntry getJVPostingEntry() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("JVPostingEntry");
		JVPostingEntry jVPostingEntry= new JVPostingEntry();
		if (workFlowDetails!=null){
			jVPostingEntry.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return jVPostingEntry;
	}


	/**
	 * This method get the module from method getJVPostingEntry() and set the new record flag as true and return JVPostingEntry()   
	 * @return JVPostingEntry
	 */


	@Override
	public JVPostingEntry getNewJVPostingEntry() {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getJVPostingEntry();
		jVPostingEntry.setNewRecord(true);
		logger.debug("Leaving");
		return jVPostingEntry;
	}

	/**
	 * Fetch the Record  JV Posting Entry details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return JVPostingEntry
	 */
	@Override
	public JVPostingEntry getJVPostingEntryById(final String id,	String txnReference, String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getJVPostingEntry();
		jVPostingEntry.setId(id);
		jVPostingEntry.setTxnReference(txnReference);
	
		StringBuilder selectSql = new StringBuilder("Select BatchReference, Account, AccountName, TxnCCy, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac");
		selectSql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",TxnCCyName,TxnCodeName");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JVPostingEntry.class);
		
		try{
			jVPostingEntry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			jVPostingEntry = null;
		}
		logger.debug("Leaving");
		return jVPostingEntry;
	}

	/**
	 * Fetch the Record  JV Posting Entry details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return JVPostingEntry
	 */
	@Override
	public List<JVPostingEntry> getJVPostingEntryListById(final String id, String type) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry = getJVPostingEntry();
		jVPostingEntry.setId(id);
		StringBuilder selectSql = new StringBuilder("Select BatchReference, Account, AccountName, TxnCCy, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac");
		selectSql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(", TxnCCyName, TxnCCyEditField, AccCCyName, AccCCyEditField");
		}
		selectSql.append(" From JVPostingEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		RowMapper<JVPostingEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JVPostingEntry.class);
		
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	/**
	 * This method initialise the Record.
	 * @param JVPostingEntry (jVPostingEntry)
 	 * @return JVPostingEntry
	 */
	@Override
	public void initialize(JVPostingEntry jVPostingEntry) {
		super.initialize(jVPostingEntry);
	}
	/**
	 * This method refresh the Record.
	 * @param JVPostingEntry (jVPostingEntry)
 	 * @return void
	 */
	@Override
	public void refresh(JVPostingEntry jVPostingEntry) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the JVPostingEntry or JVPostingEntry_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete JV Posting Entry by key BatchReference
	 * 
	 * @param JV Posting Entry (jVPostingEntry)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(JVPostingEntry jVPostingEntry,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From JVPostingEntry");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",jVPostingEntry.getId() ,jVPostingEntry.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",jVPostingEntry.getId() ,jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into JVPostingEntry or JVPostingEntry_Temp.
	 *
	 * save JV Posting Entry 
	 * 
	 * @param JV Posting Entry (jVPostingEntry)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(JVPostingEntry jVPostingEntry,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into JVPostingEntry");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BatchReference, Account, AccountName, TxnCCy, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1, NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac");
		insertSql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BatchReference, :Account, :AccountName, :TxnCCy, :AccCCy, :TxnCode, :PostingDate, :ValueDate, :TxnAmount, :TxnReference, :NarrLine1, :NarrLine2, :NarrLine3, :NarrLine4, :ExchRate_Batch, :ExchRate_Ac, :TxnAmount_Batch, :TxnAmount_Ac");
		insertSql.append(", :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return jVPostingEntry.getId();
	}
	
	/**
	 * This method updates the Record JVPostingEntry or JVPostingEntry_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update JV Posting Entry by key BatchReference and Version
	 * 
	 * @param JV Posting Entry (jVPostingEntry)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(JVPostingEntry jVPostingEntry,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update JVPostingEntry");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set BatchReference = :BatchReference, Account = :Account, AccountName = :AccountName, TxnCCy = :TxnCCy, AccCCy=:AccCCy, TxnCode = :TxnCode, PostingDate = :PostingDate, ValueDate = :ValueDate, TxnAmount = :TxnAmount, TxnReference = :TxnReference, NarrLine1 = :NarrLine1, NarrLine2 = :NarrLine2, NarrLine3 = :NarrLine3, NarrLine4 = :NarrLine4, ExchRate_Batch = :ExchRate_Batch, ExchRate_Ac = :ExchRate_Ac, TxnAmount_Batch = :TxnAmount_Batch, TxnAmount_Ac = :TxnAmount_Ac");
		updateSql.append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BatchReference =:BatchReference and TxnReference = :TxnReference");
		
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",jVPostingEntry.getId() ,jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String batchReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = batchReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_BatchReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


	@SuppressWarnings("serial")
    @Override
    public void deleteByBatchRef(String batchReference, String tableType) {
		logger.debug("Entering");
		JVPostingEntry jVPostingEntry=new JVPostingEntry();
		jVPostingEntry.setBatchReference(batchReference);
		StringBuilder deleteSql = new StringBuilder("Delete From JVPostingEntry");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where BatchReference =:BatchReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPostingEntry);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
	
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",jVPostingEntry.getId() ,jVPostingEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	    
    }

	
}