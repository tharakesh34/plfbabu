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
 * FileName    		:  RelationshipOfficerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.applicationmaster.impl;

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
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>RelationshipOfficer model</b> class.<br>
 * 
 */
public class RelationshipOfficerDAOImpl extends BasisCodeDAO<RelationshipOfficer> implements RelationshipOfficerDAO {

	private static Logger logger = Logger.getLogger(RelationshipOfficerDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new RelationshipOfficer 
	 * @return RelationshipOfficer
	 */
	@Override
	public RelationshipOfficer getRelationshipOfficer() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("RelationshipOfficer");
		RelationshipOfficer relationshipOfficer= new RelationshipOfficer();
		if (workFlowDetails!=null){
			relationshipOfficer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return relationshipOfficer;
	}

	/**
	 * This method get the module from method getRelationshipOfficer() and set the new record flag as true and return RelationshipOfficer()   
	 * @return RelationshipOfficer
	 */
	@Override
	public RelationshipOfficer getNewRelationshipOfficer() {
		logger.debug("Entering");
		RelationshipOfficer relationshipOfficer = getRelationshipOfficer();
		relationshipOfficer.setNewRecord(true);
		logger.debug("Leaving");
		return relationshipOfficer;
	}

	/**
	 * Fetch the Record  Relationship Officers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return RelationshipOfficer
	 */
	@Override
	public RelationshipOfficer getRelationshipOfficerById(final String id, String type) {
		logger.debug("Entering");
		RelationshipOfficer relationshipOfficer = getRelationshipOfficer();
		relationshipOfficer.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT ROfficerCode, ROfficerDesc, ROfficerDeptCode, ROfficerIsActive,");
		if(type.contains("View")){
			selectSql.append("lovDescROfficerDeptCodeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  RelationshipOfficers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ROfficerCode =:rOfficerCode");
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		RowMapper<RelationshipOfficer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RelationshipOfficer.class);
		
		try{
			relationshipOfficer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			relationshipOfficer = null;
		}
		logger.debug("Leaving");
		return relationshipOfficer;
	}
	
	/**
	 * This method initialize the Record.
	 * @param RelationshipOfficer (relationshipOfficer)
 	 * @return RelationshipOfficer
	 */
	@Override
	public void initialize(RelationshipOfficer relationshipOfficer) {
		super.initialize(relationshipOfficer);
	}
	
	/**
	 * This method refresh the Record.
	 * @param RelationshipOfficer (relationshipOfficer)
 	 * @return void
	 */
	@Override
	public void refresh(RelationshipOfficer relationshipOfficer) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RelationshipOfficers or RelationshipOfficers_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Relationship Officers by key ROfficerCode
	 * 
	 * @param Relationship Officers (relationshipOfficer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(RelationshipOfficer relationshipOfficer,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From RelationshipOfficers");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ROfficerCode =:ROfficerCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		          
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", relationshipOfficer.getROfficerCode(), 
					relationshipOfficer.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", relationshipOfficer.getROfficerCode(), 
					relationshipOfficer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RelationshipOfficers or RelationshipOfficers_Temp.
	 *
	 * save Relationship Officers 
	 * 
	 * @param Relationship Officers (relationshipOfficer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(RelationshipOfficer relationshipOfficer,String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into RelationshipOfficers");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ROfficerCode, ROfficerDesc, ROfficerDeptCode, ROfficerIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:ROfficerCode, :ROfficerDesc, :ROfficerDeptCode, :ROfficerIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return relationshipOfficer.getId();
	}
	
	/**
	 * This method updates the Record RelationshipOfficers or RelationshipOfficers_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Relationship Officers by key ROfficerCode and Version
	 * 
	 * @param Relationship Officers (relationshipOfficer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(RelationshipOfficer relationshipOfficer,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update RelationshipOfficers");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ROfficerCode = :ROfficerCode, ROfficerDesc = :ROfficerDesc," );
		updateSql.append(" ROfficerDeptCode = :ROfficerDeptCode, ROfficerIsActive = :ROfficerIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where ROfficerCode =:ROfficerCode ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41003", relationshipOfficer.getROfficerCode(), 
					relationshipOfficer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String rOfficerCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = rOfficerCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_ROfficerCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}