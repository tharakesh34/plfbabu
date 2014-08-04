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
 * FileName    		:  CommidityLoanHeaderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters.impl;


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
import com.pennant.backend.dao.lmtmasters.CommidityLoanHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CommidityLoanHeader model</b> class.<br>
 * 
 */

public class CommidityLoanHeaderDAOImpl extends BasisCodeDAO<CommidityLoanHeader> implements CommidityLoanHeaderDAO {

	private static Logger logger = Logger.getLogger(CommidityLoanHeaderDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CommidityLoanHeader 
	 * @return CommidityLoanHeader
	 */

	@Override
	public CommidityLoanHeader getCommidityLoanHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CommidityLoanHeader");
		CommidityLoanHeader CommidityLoanHeader= new CommidityLoanHeader();
		if (workFlowDetails!=null){
			CommidityLoanHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return CommidityLoanHeader;
	}


	/**
	 * This method get the module from method getCommidityLoanHeader() and set the new record flag as true and return CommidityLoanHeader()   
	 * @return CommidityLoanHeader
	 */


	@Override
	public CommidityLoanHeader getNewCommidityLoanHeader() {
		logger.debug("Entering");
		CommidityLoanHeader CommidityLoanHeader = getCommidityLoanHeader();
		CommidityLoanHeader.setNewRecord(true);
		logger.debug("Leaving");
		return CommidityLoanHeader;
	}

	/**
	 * Fetch the Record  Goods Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommidityLoanHeader
	 */
	@Override
	public CommidityLoanHeader getCommidityLoanHeaderById(final String id, String type) {
		logger.debug("Entering");
		CommidityLoanHeader CommidityLoanHeader = new CommidityLoanHeader();
		CommidityLoanHeader.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select LoanRefNumber, BrokerName, SplInstruction ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("  ");
		}
		selectSql.append(" From LMTCommidityLoanHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LoanRefNumber =:LoanRefNumber ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(CommidityLoanHeader);
		RowMapper<CommidityLoanHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommidityLoanHeader.class);
		
		try{
			CommidityLoanHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			CommidityLoanHeader = null;
		}
		logger.debug("Leaving");
		return CommidityLoanHeader;
	}
	
	/**
	 * This method initialise the Record.
	 * @param CommidityLoanHeader (CommidityLoanHeader)
 	 * @return CommidityLoanHeader
	 */
	@Override
	public void initialize(CommidityLoanHeader CommidityLoanHeader) {
		super.initialize(CommidityLoanHeader);
	}
	/**
	 * This method refresh the Record.
	 * @param CommidityLoanHeader (CommidityLoanHeader)
 	 * @return void
	 */
	@Override
	public void refresh(CommidityLoanHeader CommidityLoanHeader) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LMTCommidityLoanHeader or LMTCommidityLoanHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods Details (CommidityLoanHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public int delete(CommidityLoanHeader CommidityLoanHeader,String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From LMTCommidityLoanHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LoanRefNumber =:LoanRefNumber");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(CommidityLoanHeader);
		recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
		return recordCount;
	}
	
	/**
	 * This method insert new Records into LMTCommidityLoanHeader or LMTCommidityLoanHeader_Temp.
	 *
	 * save Goods Details 
	 * 
	 * @param Goods Details (CommidityLoanHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CommidityLoanHeader CommidityLoanHeader,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LMTCommidityLoanHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LoanRefNumber, BrokerName, SplInstruction ");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LoanRefNumber, :BrokerName, :SplInstruction ");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(CommidityLoanHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return CommidityLoanHeader.getId();
	}
	
	/**
	 * This method updates the Record LMTCommidityLoanHeader or LMTCommidityLoanHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (CommidityLoanHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(CommidityLoanHeader CommidityLoanHeader,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LMTCommidityLoanHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LoanRefNumber=:LoanRefNumber, BrokerName=:BrokerName, SplInstruction=:SplInstruction ");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where LoanRefNumber =:LoanRefNumber and BrokerName = :BrokerName");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(CommidityLoanHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",CommidityLoanHeader.getId() ,CommidityLoanHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String loanRefNumber, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = loanRefNumber;
		parms[0][0] = PennantJavaUtil.getLabel("label_LoanRefNumber")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}