/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ProductDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-08-2011 * * Modified Date
 * : 12-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.bmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.bmtmasters.ProductDAO;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>Product model</b> class.<br>
 * 
 */
public class ProductDAOImpl extends BasicDao<Product> implements ProductDAO {
	private static Logger logger = LogManager.getLogger(ProductDAOImpl.class);

	public ProductDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Product Detail details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
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
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, AllowDeviation");
		selectSql.append(" From BMTProduct");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductCode =:ProductCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);
		RowMapper<Product> typeRowMapper = BeanPropertyRowMapper.newInstance(Product.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Product Detail details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Product
	 */
	@Override
	public Product getProductByProduct(final String code) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCategory, AllowDeviation, ProductCode, ProductDesc");
		sql.append(" from BMTProduct");
		sql.append(" Where ProductCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<Product>() {
				@Override
				public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
					Product pc = new Product();

					pc.setProductCategory(rs.getString("ProductCategory"));
					pc.setAllowDeviation(rs.getBoolean("AllowDeviation"));
					pc.setProductCode(rs.getString("ProductCode"));
					pc.setProductDesc(rs.getString("ProductDesc"));

					return pc;
				}
			}, code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public String getProductCtgByProduct(final String code) {
		logger.debug("Entering");
		Product product = new Product();
		product.setProductCode(code);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select ProductCategory From BMTProduct ");
		selectSql.append(" Where ProductCode =:ProductCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the BMTProduct or BMTProduct_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Product Detail by key ProductCode
	 * 
	 * @param Product Detail (product)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Product product, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From BMTProduct");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode =:ProductCode");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	 * @param Product Detail (product)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Product product, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into BMTProduct");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductCode, ProductDesc, ProductCategory,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append("  RecordType, WorkflowId, AllowDeviation)");
		insertSql.append(" Values(:ProductCode, :ProductDesc, :ProductCategory, :Version , :LastMntBy, :LastMntOn,");
		insertSql.append(
				" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :AllowDeviation)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return product.getId();
	}

	/**
	 * This method updates the Record BMTProduct or BMTProduct_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Product Detail by key ProductCode and Version
	 * 
	 * @param Product Detail (product)
	 * @param type    (String) ""/_Temp/_View
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
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(
				" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, AllowDeviation = :AllowDeviation");
		updateSql.append(" Where ProductCode =:ProductCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(product);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}