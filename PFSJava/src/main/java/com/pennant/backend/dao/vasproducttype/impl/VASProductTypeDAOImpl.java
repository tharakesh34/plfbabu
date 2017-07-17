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
 * FileName    		:  VASProductTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-01-2017    														*
 *                                                                  						*
 * Modified Date    :  09-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.vasproducttype.impl;

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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.vasproducttype.VASProductTypeDAO;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>VASProductType model</b> class.<br>
 * 
 */

public class VASProductTypeDAOImpl extends BasisCodeDAO<VASProductType> implements VASProductTypeDAO {

	private static Logger				logger	= Logger.getLogger(VASProductTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new VASProductType
	 * 
	 * @return VASProductType
	 */

	@Override
	public VASProductType getVASProductType() {
		logger.debug("Entering");
		VASProductType productType = new VASProductType();
		logger.debug("Leaving");
		return productType;
	}

	/**
	 * This method get the module from method getVASProductType() and set the new record flag as true and return
	 * VASProductType()
	 * 
	 * @return VASProductType
	 */
	@Override
	public VASProductType getNewVASProductType() {
		logger.debug("Entering");
		VASProductType vASProductType = getVASProductType();
		vASProductType.setNewRecord(true);
		logger.debug("Leaving");
		return vASProductType;
	}

	/**
	 * Fetch the Record VASProductType details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASProductType
	 */
	@Override
	public VASProductType getVASProductTypeById(final String id, String type) {
		logger.debug("Entering");
		VASProductType vASProductType = getVASProductType();
		vASProductType.setId(id);
		StringBuilder selectSql = new StringBuilder("Select ProductType, ProductTypeDesc, ProductCtg, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",ProductCtgDesc");
		}
		selectSql.append(" From VasProductType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductType =:ProductType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductType);
		RowMapper<VASProductType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASProductType.class);

		try {
			vASProductType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vASProductType = null;
		}
		logger.debug("Leaving");
		return vASProductType;
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
	 * This method Deletes the Record from the VasProductType or VasProductType_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete VASProductType by key ProductType
	 * 
	 * @param VASProductType
	 *            (vASProductType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(VASProductType vASProductType, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From VasProductType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductType =:ProductType");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductType);
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
	 * This method insert new Records into VasProductType or VasProductType_Temp.
	 *
	 * save VASProductType
	 * 
	 * @param VASProductType
	 *            (vASProductType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(VASProductType vASProductType, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into VasProductType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductType, ProductTypeDesc, ProductCtg, Active");
		insertSql
				.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ProductType, :ProductTypeDesc, :ProductCtg, :Active");
		insertSql
				.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vASProductType.getId();
	}

	/**
	 * This method updates the Record VasProductType or VasProductType_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update VASProductType by key ProductType and Version
	 * 
	 * @param VASProductType
	 *            (vASProductType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VASProductType vASProductType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update VasProductType");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
				.append(" Set ProductTypeDesc = :ProductTypeDesc, ProductCtg = :ProductCtg, Active = :Active");
		updateSql
				.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ProductType =:ProductType");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	
	@Override
	public int getVASProductTypeByActive(String productType, String type) {

		logger.debug("Entering");
		VASConfiguration vASConfiguration = new VASConfiguration();
		vASConfiguration.setProductType(productType);
		int count;
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From VasStructure");
		selectSql.append(" Where ProductType =:ProductType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Integer.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
		logger.debug("Leaving");
		return count;
	}

}