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
 * FileName    		:  VehicleModelDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.VehicleModelDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>VehicleModel model</b> class.<br>
 */
public class VehicleModelDAOImpl extends BasisNextidDaoImpl<VehicleModel>
		implements VehicleModelDAO {

	private static Logger logger = Logger.getLogger(VehicleModelDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new VehicleModel
	 * 
	 * @return VehicleModel
	 */
	@Override
	public VehicleModel getVehicleModel() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("VehicleModel");
		VehicleModel vehicleModel= new VehicleModel();
		if (workFlowDetails!=null){
			vehicleModel.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return vehicleModel;
	}

	/**
	 * This method get the module from method getVehicleModel() and set the new
	 * record flag as true and return VehicleModel()
	 * 
	 * @return VehicleModel
	 */
	@Override
	public VehicleModel getNewVehicleModel() {
		logger.debug("Entering");
		VehicleModel vehicleModel = getVehicleModel();
		vehicleModel.setNewRecord(true);
		logger.debug("Leaving");
		return vehicleModel;
	}

	/**
	 * Fetch the Record  Vehicle Model Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleModel
	 */
	@Override
	public VehicleModel getVehicleModelById(final long id, String type) {
		logger.debug("Entering");
		VehicleModel vehicleModel = new VehicleModel();
		vehicleModel.setId(id);
		
		StringBuilder   selectSql = new StringBuilder  ("SELECT VehicleManufacturerId," );
		selectSql.append(" VehicleModelId,VehicleModelDesc, ");

		if(type.contains("View")){
			selectSql.append("lovDescVehicleManufacturerName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  AMTVehicleModel");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where VehicleModelId =:VehicleModelId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleModel);
		RowMapper<VehicleModel> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VehicleModel.class);

		try{
			vehicleModel = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			vehicleModel = null;
		}
		logger.debug("Leaving");
		return vehicleModel;
	}

	/**
	 * This method initialize the Record.
	 * @param VehicleModel (vehicleModel)
	 * @return VehicleModel
	 */
	@Override
	public void initialize(VehicleModel vehicleModel) {
		super.initialize(vehicleModel);
	}
	
	/**
	 * This method refresh the Record.
	 * @param VehicleModel (vehicleModel)
	 * @return void
	 */
	@Override
	public void refresh(VehicleModel vehicleModel) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTVehicleModel or
	 * AMTVehicleModel_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Vehicle Model Detail by key
	 * VehicleModelId
	 * 
	 * @param Vehicle
	 *            Model Detail (vehicleModel)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(VehicleModel vehicleModel,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From AMTVehicleModel");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VehicleModelId =:VehicleModelId");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleModel);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",vehicleModel.getVehicleModelId() ,
						vehicleModel.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",vehicleModel.getVehicleModelId() ,
					vehicleModel.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTVehicleModel or
	 * AMTVehicleModel_Temp. it fetches the available Sequence form
	 * SeqAMTVehicleModel by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Vehicle Model Detail
	 * 
	 * @param Vehicle
	 *            Model Detail (vehicleModel)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleModel vehicleModel,String type) {
		logger.debug("Entering");
		if (vehicleModel.getId()==Long.MIN_VALUE){
			vehicleModel.setId(getNextidviewDAO().getNextId("SeqAMTVehicleModel"));
			logger.debug("get NextID:"+vehicleModel.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into AMTVehicleModel");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(VehicleManufacturerId,VehicleModelId, VehicleModelDesc, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:VehicleManufacturerId,:VehicleModelId, :VehicleModelDesc,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleModel);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vehicleModel.getId();
	}

	/**
	 * This method updates the Record AMTVehicleModel or AMTVehicleModel_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Vehicle Model Detail by key VehicleModelId and Version
	 * 
	 * @param Vehicle Model Detail (vehicleModel)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(VehicleModel vehicleModel,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update AMTVehicleModel");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set VehicleManufacturerId =:VehicleManufacturerId,");
		updateSql.append(" VehicleModelId = :VehicleModelId, VehicleModelDesc = :VehicleModelDesc,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where VehicleModelId =:VehicleModelId");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("AND Version= :Version-1");
		}
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleModel);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",vehicleModel.getVehicleModelId() ,
					vehicleModel.getUserDetails().getUsrLanguage());
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
	private ErrorDetails getError(String errorId, long vehicleModelId, String userLanguage) {

		String[][] parms = new String[2][2];
		parms[1][0] = String.valueOf(vehicleModelId);
		parms[0][0] = PennantJavaUtil.getLabel("label_VehicleModelId") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),userLanguage);
	}
}