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
 * FileName    		:  VASProductCategoryDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.vasproduct.impl;

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
import com.pennant.backend.dao.vasproduct.VASProductCategoryDAO;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>VASProductCategory model</b> class.<br>
 * 
 */
public class VASProductCategoryDAOImpl extends BasisCodeDAO<VASProductCategory> implements VASProductCategoryDAO {

	private static Logger				logger	= Logger.getLogger(VASProductCategoryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new VASProductCategory
	 * 
	 * @return VASProductCategory
	 */

	@Override
	public VASProductCategory getVASProductCategory() {
		logger.debug("Entering");
		VASProductCategory productCategory = new VASProductCategory();
		logger.debug("Leaving");
		return productCategory;
	}

	/**
	 * This method get the module from method getVASProductCategory() and set the new record flag as true and return
	 * VASProductCategory()
	 * 
	 * @return VASProductCategory
	 */

	@Override
	public VASProductCategory getNewVASProductCategory() {
		logger.debug("Entering");
		VASProductCategory vASProductCategory = getVASProductCategory();
		vASProductCategory.setNewRecord(true);
		logger.debug("Leaving");
		return vASProductCategory;
	}

	/**
	 * Fetch the Record VASProductCategory details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASProductCategory
	 */
	@Override
	public VASProductCategory getVASProductCategoryById(final String id, String type) {
		logger.debug("Entering");
		VASProductCategory vASProductCategory = getVASProductCategory();
		vASProductCategory.setId(id);
		StringBuilder selectSql = new StringBuilder("Select ProductCtg, ProductCtgDesc, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From VasProductCategory");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductCtg =:ProductCtg");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductCategory);
		RowMapper<VASProductCategory> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VASProductCategory.class);

		try {
			vASProductCategory = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vASProductCategory = null;
		}
		logger.debug("Leaving");
		return vASProductCategory;
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
	 * This method Deletes the Record from the VasProductCategory or VasProductCategory_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete VASProductCategory by key ProductCtg
	 * 
	 * @param VASProductCategory
	 *            (vASProductCategory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(VASProductCategory vASProductCategory, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From VasProductCategory");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCtg =:ProductCtg");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductCategory);
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
	 * This method insert new Records into VasProductCategory or VasProductCategory_Temp.
	 *
	 * save VASProductCategory
	 * 
	 * @param VASProductCategory
	 *            (vASProductCategory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(VASProductCategory vASProductCategory, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into VasProductCategory");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductCtg, ProductCtgDesc, Active");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ProductCtg, :ProductCtgDesc, :Active");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductCategory);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vASProductCategory.getId();
	}

	/**
	 * This method updates the Record VasProductCategory or VasProductCategory_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update VASProductCategory by key ProductCtg and Version
	 * 
	 * @param VASProductCategory
	 *            (vASProductCategory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(VASProductCategory vASProductCategory, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update VasProductCategory");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ProductCtgDesc = :ProductCtgDesc, Active = :Active");
		updateSql.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ProductCtg =:ProductCtg");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductCategory);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}


	@Override
	public int getVASProductCategoryByActive(String productCtg, String type) {

		logger.debug("Entering");
		VASProductType vASProductType = new VASProductType();
		vASProductType.setProductCtg(productCtg);
		int count;

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From VASProductType");
		selectSql.append(" Where ProductCtg =:ProductCtg ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductType);

		try {
			 count= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Integer.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
		logger.debug("Leaving");
		return count;

	}

}