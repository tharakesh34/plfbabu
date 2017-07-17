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
 * FileName    		:  VesselDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-05-2015    														*
 *                                                                  						*
 * Modified Date    :  12-05-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-05-2015       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.VesselDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.VesselDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>VesselDetail model</b> class.<br>
 * 
 */

public class VesselDetailDAOImpl extends BasisCodeDAO<VesselDetail> implements VesselDetailDAO {

	private static Logger logger = Logger.getLogger(VesselDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public VesselDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Vessel Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VesselDetail
	 */
	@Override
	public VesselDetail getVesselDetailById(final String id, String type) {
		logger.debug("Entering");
		VesselDetail vesselDetail = new VesselDetail();
		
		vesselDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select VesselTypeID, VesselType, VesselSubType, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",VesselTypeName");
		}
		selectSql.append(" From VesselDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where VesselTypeID =:VesselTypeID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vesselDetail);
		RowMapper<VesselDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VesselDetail.class);
		
		try{
			vesselDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vesselDetail = null;
		}
		logger.debug("Leaving");
		return vesselDetail;
	}
	
	@Override
    public VesselDetail getVesselDetailByType(VesselDetail vesselDetail, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("Select VesselTypeID, VesselType, VesselSubType, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",VesselTypeName");
		}
		selectSql.append(" From VesselDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where VesselTypeID != :VesselTypeID AND VesselType = :VesselType AND VesselSubType = :VesselSubType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vesselDetail);
		RowMapper<VesselDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VesselDetail.class);
		
		try{
			vesselDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vesselDetail = null;
		}
		logger.debug("Leaving");
		return vesselDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the VesselDetails or VesselDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Vessel Details by key VesselTypeID
	 * 
	 * @param Vessel Details (vesselDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(VesselDetail vesselDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From VesselDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VesselTypeID =:VesselTypeID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vesselDetail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into VesselDetails or VesselDetails_Temp.
	 * it fetches the available Sequence form SeqVesselDetails by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Vessel Details 
	 * 
	 * @param Vessel Details (vesselDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void save(VesselDetail vesselDetail,String type) {
		logger.debug("Entering");
		StringBuilder insertSql =new StringBuilder("Insert Into VesselDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (VesselTypeID, VesselType, VesselSubType, Active");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:VesselTypeID, :VesselType, :VesselSubType, :Active");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vesselDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record VesselDetails or VesselDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Vessel Details by key VesselTypeID and Version
	 * 
	 * @param Vessel Details (vesselDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VesselDetail vesselDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update VesselDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set VesselType = :VesselType, VesselSubType = :VesselSubType, Active = :Active");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where VesselTypeID =:VesselTypeID");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vesselDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}