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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.app.util.ErrorUtil;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.model.others.JVPosting;

/**
 * DAO methods implementation for the <b>JVPosting model</b> class.<br>
 * 
 */

public class JVPostingDAOImpl extends BasisCodeDAO<JVPosting> implements JVPostingDAO {

	private static Logger logger = Logger.getLogger(JVPostingDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new JVPosting 
	 * @return JVPosting
	 */

	@Override
	public JVPosting getJVPosting() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("JVPosting");
		JVPosting jVPosting= new JVPosting();
		if (workFlowDetails!=null){
			jVPosting.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return jVPosting;
	}


	/**
	 * This method get the module from method getJVPosting() and set the new record flag as true and return JVPosting()   
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
	 * Fetch the Record  JV Posting Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return JVPosting
	 */
	@Override
	public JVPosting getJVPostingById(final String id, String type) {
		logger.debug("Entering");
		JVPosting jVPosting = getJVPosting();
		
		jVPosting.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select BatchReference, Batch,   DebitCount, CreditsCount, TotDebitsByBatchCcy, TotCreditsByBatchCcy, BatchPurpose");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
		
		}
		selectSql.append(" From JVPostings");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		RowMapper<JVPosting> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(JVPosting.class);
		
		try{
			jVPosting = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			jVPosting = null;
		}
		logger.debug("Leaving");
		return jVPosting;
	}
	
	/**
	 * This method initialise the Record.
	 * @param JVPosting (jVPosting)
 	 * @return JVPosting
	 */
	@Override
	public void initialize(JVPosting jVPosting) {
		super.initialize(jVPosting);
	}
	/**
	 * This method refresh the Record.
	 * @param JVPosting (jVPosting)
 	 * @return void
	 */
	@Override
	public void refresh(JVPosting jVPosting) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the JVPostings or JVPostings_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete JV Posting Details by key BatchReference
	 * 
	 * @param JV Posting Details (jVPosting)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(JVPosting jVPosting,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From JVPostings");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BatchReference =:BatchReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",jVPosting.getId() ,jVPosting.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",jVPosting.getId() ,jVPosting.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into JVPostings or JVPostings_Temp.
	 *
	 * save JV Posting Details 
	 * 
	 * @param JV Posting Details (jVPosting)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(JVPosting jVPosting,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into JVPostings");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BatchReference, Batch,  DebitCount, CreditsCount, TotDebitsByBatchCcy, TotCreditsByBatchCcy, BatchPurpose");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BatchReference, :Batch,   :DebitCount, :CreditsCount, :TotDebitsByBatchCcy, :TotCreditsByBatchCcy, :BatchPurpose");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return jVPosting.getId();
	}
	
	/**
	 * This method updates the Record JVPostings or JVPostings_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update JV Posting Details by key BatchReference and Version
	 * 
	 * @param JV Posting Details (jVPosting)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(JVPosting jVPosting,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update JVPostings");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set BatchReference = :BatchReference, Batch = :Batch,  DebitCount = :DebitCount, CreditsCount = :CreditsCount, TotDebitsByBatchCcy = :TotDebitsByBatchCcy, TotCreditsByBatchCcy = :TotCreditsByBatchCcy, BatchPurpose = :BatchPurpose");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BatchReference =:BatchReference");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(jVPosting);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",jVPosting.getId() ,jVPosting.getUserDetails().getUsrLanguage());
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

	
}