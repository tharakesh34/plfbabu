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
 * FileName    		:  ProductDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.bmtmasters.ProductDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Product model</b> class.<br>
 * 
 */
public class ProductDAOImpl extends BasisCodeDAO<Product> implements ProductDAO {

	private static Logger logger	= Logger.getLogger(ProductDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ProductDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record Product Detail details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return Product
	 */
	@Override
	public Product getProductByID(final String id, String code, String type) {
		logger.debug("Entering");
		Product product = new Product();
		product.setId(id);
		product.setProductCode(code);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select ProductCode, ProductDesc, ProductCategory,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTProduct");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductCode =:ProductCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);
		RowMapper<Product> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Product.class);

		try {
			product = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			product = null;
		}
		logger.debug("Leaving");
		return product;
	}
	
	/**
	 * Fetch the Record Product Detail details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return Product
	 */
	@Override
	public String getProductCtgByProduct(final String code) {
		logger.debug("Entering");
		Product product = new Product();
		product.setProductCode(code);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select ProductCategory From BMTProduct ");
		selectSql.append(" Where ProductCode =:ProductCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);

		String productCtg = null;
		try {
			productCtg = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			productCtg = null;
		}
		logger.debug("Leaving");
		return productCtg;
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
	 * This method Deletes the Record from the BMTProduct or BMTProduct_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Product Detail by key ProductCode
	 * 
	 * @param Product
	 *         Detail (product)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Product product, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder  deleteSql = 	new StringBuilder ();
		deleteSql.append(" Delete From BMTProduct");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode =:ProductCode");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);

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
	 * This method insert new Records into BMTProduct or BMTProduct_Temp.
	 * 
	 * save Product Detail
	 * 
	 * @param Product
	 *         Detail (product)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Product product, String type) {
		logger.debug("Entering");

		StringBuilder   insertSql =new StringBuilder();
		insertSql.append("Insert Into BMTProduct");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductCode, ProductDesc, ProductCategory,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ProductCode, :ProductDesc, :ProductCategory, :Version , :LastMntBy, :LastMntOn,");
		insertSql.append(" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return product.getId();
	}

	/**
	 * This method updates the Record BMTProduct or BMTProduct_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Product Detail by key ProductCode and Version
	 * 
	 * @param Product
	 *         Detail (product)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Product product, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTProduct");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ProductDesc = :ProductDesc, ProductCategory = :ProductCategory,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ProductCode =:ProductCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}