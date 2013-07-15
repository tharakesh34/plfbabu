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
 * FileName    		:  VehicleManufacturerDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.VehicleManufacturerDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>VehicleManufacturer model</b> class.<br>
 */
public class VehicleManufacturerDAOImpl extends
		BasisNextidDaoImpl<VehicleManufacturer> implements VehicleManufacturerDAO {

	private static Logger logger = Logger.getLogger(VehicleManufacturerDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new VehicleManufacturer
	 * 
	 * @return VehicleManufacturer
	 */
	@Override
	public VehicleManufacturer getVehicleManufacturer() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("VehicleManufacturer");
		VehicleManufacturer vehicleManufacturer= new VehicleManufacturer();
		if (workFlowDetails!=null){
			vehicleManufacturer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return vehicleManufacturer;
	}


	/**
	 * This method get the module from method getVehicleManufacturer() and set
	 * the new record flag as true and return VehicleManufacturer()
	 * 
	 * @return VehicleManufacturer
	 */
	@Override
	public VehicleManufacturer getNewVehicleManufacturer() {
		logger.debug("Entering");
		VehicleManufacturer vehicleManufacturer = getVehicleManufacturer();
		vehicleManufacturer.setNewRecord(true);
		logger.debug("Leaving");
		return vehicleManufacturer;
	}

	/**
	 * Fetch the Record  Vehicle Manufacturer Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleManufacturer
	 */
	@Override
	public VehicleManufacturer getVehicleManufacturerById(final long id, String type) {
		logger.debug("Entering");
		VehicleManufacturer vehicleManufacturer = getVehicleManufacturer();
		vehicleManufacturer.setId(id);
		
		StringBuilder   selectSql = new StringBuilder  ("SELECT ManufacturerId,  ManufacturerName, ");
		selectSql.append("Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append("TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  AMTVehicleManufacturer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ManufacturerId = :ManufacturerId ");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleManufacturer);
		RowMapper<VehicleManufacturer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VehicleManufacturer.class);
		
		try{
			vehicleManufacturer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			vehicleManufacturer = null;
		}
		logger.debug("Leaving");
		return vehicleManufacturer;
	}
	
	/**
	 * This method initialize the Record.
	 * @param VehicleManufacturer (vehicleManufacturer)
 	 * @return VehicleManufacturer
	 */
	@Override
	public void initialize(VehicleManufacturer vehicleManufacturer) {
		super.initialize(vehicleManufacturer);
	}
	
	/**
	 * This method refresh the Record.
	 * @param VehicleManufacturer (vehicleManufacturer)
 	 * @return void
	 */
	@Override
	public void refresh(VehicleManufacturer vehicleManufacturer) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the AMTVehicleManufacturer or
	 * AMTVehicleManufacturer_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Vehicle Manufacturer Detail
	 * by key ManufacturerId
	 * 
	 * @param Vehicle
	 *            Manufacturer Detail (vehicleManufacturer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(VehicleManufacturer vehicleManufacturer,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From AMTVehicleManufacturer");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ManufacturerId =:ManufacturerId");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleManufacturer);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",vehicleManufacturer.getManufacturerId(),
						vehicleManufacturer.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",vehicleManufacturer.getManufacturerId() ,
					vehicleManufacturer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into AMTVehicleManufacturer or
	 * AMTVehicleManufacturer_Temp. it fetches the available Sequence form
	 * SeqAMTVehicleManufacturer by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Vehicle Manufacturer Detail
	 * 
	 * @param Vehicle
	 *            Manufacturer Detail (vehicleManufacturer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleManufacturer vehicleManufacturer,String type) {
		logger.debug("Entering");
		if (vehicleManufacturer.getId()==Long.MIN_VALUE){
			vehicleManufacturer.setId(getNextidviewDAO().getNextId("SeqAMTVehicleManufacturer"));
			logger.debug("get NextID:"+vehicleManufacturer.getId());
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into AMTVehicleManufacturer");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ManufacturerId, ManufacturerName, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ManufacturerId, :ManufacturerName,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleManufacturer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vehicleManufacturer.getId();
	}
	
	/**
	 * This method updates the Record AMTVehicleManufacturer or
	 * AMTVehicleManufacturer_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Vehicle Manufacturer Detail
	 * by key ManufacturerId and Version
	 * 
	 * @param Vehicle
	 *            Manufacturer Detail (vehicleManufacturer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(VehicleManufacturer vehicleManufacturer,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update AMTVehicleManufacturer");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ManufacturerId = :ManufacturerId, ManufacturerName = :ManufacturerName,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where ManufacturerId =:ManufacturerId");
		logger.debug("updateSql: "+ updateSql.toString());

		if (!type.endsWith("_TEMP")) {
			updateSql.append("AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleManufacturer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",vehicleManufacturer.getManufacturerId() ,
					vehicleManufacturer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails getError(String errorId, 
			long manufacturerId, String userLanguage) {
		
		String[][] parms = new String[2][2];
		
		parms[1][0] = String.valueOf(manufacturerId);
		parms[0][0] = PennantJavaUtil.getLabel("label_ManufacturerId") + ":" + parms[1][0];
		
		return ErrorUtil.getErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD, errorId,
				parms[0], parms[1]),userLanguage);
	}


	
	
}