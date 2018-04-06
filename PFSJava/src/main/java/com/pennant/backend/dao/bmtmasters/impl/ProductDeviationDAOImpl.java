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
 * FileName    		:  ProductDeviationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant~	                 0.1                                            * 
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

package com.pennant.backend.dao.bmtmasters.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rmtmasters.ProductDeviationDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.ProductDeviation;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ProductDeviation model</b> class.<br>
 * 
 */
public class ProductDeviationDAOImpl extends BasisNextidDaoImpl<ProductDeviation> implements ProductDeviationDAO {

	private static Logger				logger	= Logger.getLogger(ProductDeviationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ProductDeviationDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new Product Deviation
	 * 
	 * @return Product Deviation
	 */
	@Override
	public ProductDeviation getProductDeviation() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ProductDeviation");
		ProductDeviation productDeviation = new ProductDeviation();
		if (workFlowDetails != null) {
			productDeviation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return productDeviation;
	}

	/**
	 * This method get the module from method getProduct Deviation() and set the new record flag as true and return
	 * Product Deviation()
	 * 
	 * @return Product Deviation
	 */
	@Override
	public ProductDeviation getNewProductDeviation() {
		logger.debug("Entering");
		ProductDeviation productDeviation = getProductDeviation();
		productDeviation.setNewRecord(true);
		logger.debug("Leaving");
		return productDeviation;
	}

	/**
	 * Fetch the Record Product Deviation Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Product Deviation
	 */
	@Override
	public ProductDeviation getProductDeviationById(long id, String type) {
		logger.debug("Entering");

		ProductDeviation productDeviation = getProductDeviation();
		productDeviation.setProductDevID(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT ProductDevID, ProductCode, DeviationID,");
		if (type.contains("View")) {
			selectSql.append("DeviationCode,");
		}
		selectSql.append("Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append("TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  ProductDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductDevID = :ProductDevID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productDeviation);
		RowMapper<ProductDeviation> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProductDeviation.class);

		try {
			productDeviation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			productDeviation = null;
		}
		logger.debug("Leaving");
		return productDeviation;
	}

	/**
	 * This method updates the Record ProductDeviations or ProductDeviations_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Product Deviation Details by key FinType and Version
	 * 
	 * @param Product
	 *            Deviation (productDeviation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ProductDeviation productDeviation, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update ProductDeviations");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ProductCode = :ProductCode, DeviationID = :DeviationID,");
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ProductDevID = :ProductDevID");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productDeviation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		/*if (recordCount <= 0) {
			throw new ConcurrencyException();
		}*/
		logger.debug("Leaving");

	}

	@Override
	public void delete(ProductDeviation productDeviation, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From ProductDeviations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductDevID = :ProductDevID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productDeviation);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			/*
			 * if (recordCount <= 0) { throw new ConcurrencyException(); }
			 */
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * This method Deletes the Record from the ProductDeviations or ProductDeviations_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Product Deviation Details by key ProductCode
	 * 
	 * @param Product
	 *            Deviation Details (productDeviation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteByProduct(String productCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		ProductDeviation productDeviation = new ProductDeviation();
		productDeviation.setProductCode(productCode);
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From ProductDeviations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode = :ProductCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productDeviation);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			/*if (recordCount <= 0) {
				throw new ConcurrencyException();
			}*/
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * This method insert new Records into ProductDeviations or ProductDeviations_Temp.
	 *
	 * save Product Deviation Details
	 * 
	 * @param Product
	 *            Deviations Details (productDeviation)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ProductDeviation productDeviation, String type) {
		logger.debug("Entering");
		if (productDeviation.getProductDevID() == Long.MIN_VALUE) {
			productDeviation.setProductDevID(getNextidviewDAO().getNextId("SeqProductDeviations"));
			logger.debug("get NextID:" + productDeviation.getProductDevID());
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into ProductDeviations");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductDevID, ProductCode, DeviationID,");
		insertSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ProductDevID, :ProductCode, :DeviationID,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productDeviation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return productDeviation.getProductDevID();
	}

	@Override
	public List<ProductDeviation> getProductDeviationByProdCode(String prodCode, String type) {
		logger.debug("Entering");
		ProductDeviation productDeviation = new ProductDeviation();
		productDeviation.setProductCode(prodCode);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select ProductDevID, ProductCode, DeviationID,");
		if (type.contains("View")) {
			selectSql.append(" DeviationCode,");
		}
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From ProductDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductCode = :ProductCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productDeviation);
		RowMapper<ProductDeviation> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProductDeviation.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public boolean isExistsDeviationID(long deviationID, String type) {
		logger.debug("Entering");
		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("DeviationID", deviationID);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(*)  FROM  ProductDeviations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DeviationID = :DeviationID");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource,
					Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		logger.debug("Leaving");

		return count > 0 ? true : false;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
