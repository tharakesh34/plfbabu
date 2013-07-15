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
 * FileName    		:  OwnerShipTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.amtmasters.impl;


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
import com.pennant.backend.dao.amtmasters.OwnerShipTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.OwnerShipType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>OwnerShipType model</b> class.<br>
 * 
 */
public class OwnerShipTypeDAOImpl extends BasisNextidDaoImpl<OwnerShipType> implements OwnerShipTypeDAO {

	private static Logger logger = Logger.getLogger(OwnerShipTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new OwnerShipType 
	 * @return OwnerShipType
	 */

	@Override
	public OwnerShipType getOwnerShipType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("OwnerShipType");
		OwnerShipType ownerShipType= new OwnerShipType();
		if (workFlowDetails!=null){
			ownerShipType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return ownerShipType;
	}

	/**
	 * This method get the module from method getOwnerShipType() and 
	 * set the new record flag as true and return OwnerShipType()   
	 * @return OwnerShipType
	 */
	@Override
	public OwnerShipType getNewOwnerShipType() {
		logger.debug("Entering");
		OwnerShipType ownerShipType = getOwnerShipType();
		ownerShipType.setNewRecord(true);
		logger.debug("Leaving");
		return ownerShipType;
	}

	/**
	 * Fetch the Record  OwnerShip Type details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OwnerShipType
	 */
	@Override
	public OwnerShipType getOwnerShipTypeById(final long id, String type) {
		logger.debug("Entering");
		OwnerShipType ownerShipType = getOwnerShipType();
		ownerShipType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select OwnerShipTypeId, OwnerShipTypeName,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AMTOwnerShipType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where OwnerShipTypeId =:OwnerShipTypeId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ownerShipType);
		RowMapper<OwnerShipType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				OwnerShipType.class);

		try{
			ownerShipType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			ownerShipType = null;
		}
		logger.debug("Leaving");
		return ownerShipType;
	}

	/**
	 * This method initialize the Record.
	 * @param OwnerShipType (ownerShipType)
	 * @return OwnerShipType
	 */
	@Override
	public void initialize(OwnerShipType ownerShipType) {
		super.initialize(ownerShipType);
	}
	
	/**
	 * This method refresh the Record.
	 * @param OwnerShipType (ownerShipType)
	 * @return void
	 */
	@Override
	public void refresh(OwnerShipType ownerShipType) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTOwnerShipType or AMTOwnerShipType_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete OwnerShip Type by key OwnerShipTypeId
	 * 
	 * @param OwnerShip Type (ownerShipType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(OwnerShipType ownerShipType,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql =new StringBuilder();

		deleteSql.append("Delete From AMTOwnerShipType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where OwnerShipTypeId =:OwnerShipTypeId");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ownerShipType);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",ownerShipType.getOwnerShipTypeId(),
						ownerShipType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",ownerShipType.getOwnerShipTypeId(), 
					ownerShipType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTOwnerShipType or AMTOwnerShipType_Temp.
	 * it fetches the available Sequence form SeqAMTOwnerShipType by using 
	 * getNextidviewDAO().getNextId() method.  
	 *
	 * save OwnerShip Type 
	 * 
	 * @param OwnerShip Type (ownerShipType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(OwnerShipType ownerShipType,String type) {
		logger.debug("Entering");
		if (ownerShipType.getId()==Long.MIN_VALUE){
			ownerShipType.setId(getNextidviewDAO().getNextId("SeqAMTOwnerShipType"));
			logger.debug("get NextID:"+ownerShipType.getId());
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into AMTOwnerShipType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (OwnerShipTypeId, OwnerShipTypeName,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:OwnerShipTypeId, :OwnerShipTypeName,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ownerShipType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return ownerShipType.getId();
	}

	/**
	 * This method updates the Record AMTOwnerShipType or AMTOwnerShipType_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update OwnerShip Type by key OwnerShipTypeId and Version
	 * 
	 * @param OwnerShip Type (ownerShipType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(OwnerShipType ownerShipType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update AMTOwnerShipType");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set OwnerShipTypeId = :OwnerShipTypeId, OwnerShipTypeName = :OwnerShipTypeName,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, " );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where OwnerShipTypeId =:OwnerShipTypeId");

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(ownerShipType.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_OwnerShipTypeId")+":"+valueParm[0];

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ownerShipType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",ownerShipType.getOwnerShipTypeId(), 
					ownerShipType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
	private ErrorDetails  getError(String errorId, long ownerShipId, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] =String.valueOf(ownerShipId);
		parms[0][0] = PennantJavaUtil.getLabel("label_OwnerShipTypeId")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}


}