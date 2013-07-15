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
 * FileName    		:  VehicleDealerDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>VehicleDealer model</b> class.<br>
 */
public class VehicleDealerDAOImpl extends BasisNextidDaoImpl<VehicleDealer> implements VehicleDealerDAO {

	private static Logger logger = Logger.getLogger(VehicleDealerDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new VehicleDealer 
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getVehicleDealer() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("VehicleDealer");
		VehicleDealer vehicleDealer= new VehicleDealer();
		if (workFlowDetails!=null){
			vehicleDealer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return vehicleDealer;
	}

	/**
	 * This method get the module from method getVehicleDealer() and set the new
	 * record flag as true and return VehicleDealer()
	 * 
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getNewVehicleDealer() {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = getVehicleDealer();
		vehicleDealer.setNewRecord(true);
		logger.debug("Leaving");
		return vehicleDealer;
	}

	/**
	 * Fetch the Record  Vehicle Dealer details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getVehicleDealerById(final long id, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = getVehicleDealer();
		vehicleDealer.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT DealerId, DealerName, ");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerId =:DealerId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VehicleDealer.class);

		try{
			vehicleDealer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			vehicleDealer = null;
		}
		logger.debug("Leaving");
		return vehicleDealer;
	}

	/**
	 * This method initialize the Record.
	 * @param VehicleDealer (vehicleDealer)
	 * @return VehicleDealer
	 */
	@Override
	public void initialize(VehicleDealer vehicleDealer) {
		super.initialize(vehicleDealer);
	}
	
	/**
	 * This method refresh the Record.
	 * @param VehicleDealer (vehicleDealer)
	 * @return void
	 */
	@Override
	public void refresh(VehicleDealer vehicleDealer) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTVehicleDealer or AMTVehicleDealer_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Vehicle Dealer by key DealerId
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(VehicleDealer vehicleDealer,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From AMTVehicleDealer");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DealerId =:DealerId");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(
					deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",
						vehicleDealer.getDealerId(), vehicleDealer
								.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", vehicleDealer.getDealerId(),
					vehicleDealer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTVehicleDealer or
	 * AMTVehicleDealer_Temp. it fetches the available Sequence form
	 * SeqAMTVehicleDealer by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Vehicle Dealer
	 * 
	 * @param Vehicle
	 *            Dealer (vehicleDealer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleDealer vehicleDealer,String type) {
		logger.debug("Entering");
		if (vehicleDealer.getId()==Long.MIN_VALUE){
			vehicleDealer.setId(getNextidviewDAO().getNextId("SeqAMTVehicleDealer"));
			logger.debug("get NextID:"+vehicleDealer.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into AMTVehicleDealer");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(DealerId, DealerName, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DealerId, :DealerName,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vehicleDealer.getId();
	}

	/**
	 * This method updates the Record AMTVehicleDealer or AMTVehicleDealer_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Vehicle Dealer by key DealerId and Version
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(VehicleDealer vehicleDealer,String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update AMTVehicleDealer");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DealerId = :DealerId, DealerName = :DealerName,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, " );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where DealerId =:DealerId");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("AND Version= :Version-1");
		}
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",vehicleDealer.getDealerId() ,
					vehicleDealer.getUserDetails().getUsrLanguage());
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
	private ErrorDetails getError(String errorId, long dealerId, String userLanguage) {

		String[][] parms = new String[2][1];
		parms[1][0] = String.valueOf(dealerId);
		parms[0][0] = PennantJavaUtil.getLabel("label_DealerId") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),userLanguage);
	}
}