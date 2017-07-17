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
 * FileName    		:  ProductAssetDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.rmtmasters.impl;



import java.util.ArrayList;
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
import com.pennant.backend.dao.rmtmasters.ProductAssetDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ProductAsset model</b> class.<br>
 * 
 */
public class ProductAssetDAOImpl extends BasisNextidDaoImpl<ProductAsset> implements ProductAssetDAO {

	private static Logger logger = Logger.getLogger(ProductAssetDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ProductAssetDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Product Asset 
	 * @return Product Asset
	 */
	@Override
	public ProductAsset getProductAsset() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ProductAsset");
		ProductAsset productAsset= new ProductAsset();
		if (workFlowDetails!=null){
			productAsset.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return productAsset;
	}

	/**
	 * This method get the module from method getProduct Asset() and set the new record flag as true and return Product Asset()   
	 * @return Product Asset
	 */
	@Override
	public ProductAsset getNewProductAsset() {
		logger.debug("Entering");
		ProductAsset productAsset = getProductAsset();
		productAsset.setNewRecord(true);
		logger.debug("Leaving");
		return productAsset;
	}

	/**
	 * Fetch the Record  Product  Asset Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Product Asset
	 */
	@Override
	public ProductAsset getProductAssetById(final long id, String type) {
		logger.debug("Entering");

		ProductAsset productAsset = new ProductAsset();
		productAsset.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT AssetID, ProductCode, AssetCode, AssetDesc, AssetIsActive,");
		if(type.contains("View")){
			selectSql.append(" ");
		}
		selectSql.append("Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append("TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  RMTProductAssets");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AssetID = :assetID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productAsset);
		RowMapper<ProductAsset> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProductAsset.class);

		try {
			productAsset = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			productAsset = null;
		}
		logger.debug("Leaving");
		return productAsset;
	}

	/**
	 * Fetch the Record  Product Asset details by product Code field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ProductAsset
	 */
	@Override
	public List<ProductAsset> getProductAssetByProdCode(final String prodCode, String type) {
		logger.debug("Entering");
		ProductAsset productAsset = new ProductAsset();
		productAsset.setProductCode(prodCode);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select AssetID, ProductCode, AssetCode, AssetDesc, AssetIsActive,");
		if(type.contains("View")){
			selectSql.append(" ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTProductAssets");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProductCode = :ProductCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productAsset);
		RowMapper<ProductAsset> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProductAsset.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
	}
	
	
	/**
	 * To fetch finance Purpose details by AssetId 
	 * @param id (list)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ProductAsset details list
	 */
	@Override
	public List<ProductAsset> getFinPurposeByAssetId(ArrayList<String> list, String type) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource=new MapSqlParameterSource();
		mapSqlParameterSource.addValue("paramValue", list);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select AssetCode,AssetDesc from RMTProductAssets");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where AssetID IN (:paramValue)");
		logger.debug("selectSql: " + selectSql.toString());
		List<ProductAsset> finPurposeList = null;
		RowMapper<ProductAsset> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProductAsset.class);
		finPurposeList = this.namedParameterJdbcTemplate.query(selectSql.toString(),mapSqlParameterSource,rowMapper);
		logger.debug("Leaving");
		return finPurposeList;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTProductAssets or RMTProductAssets_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Product Asset Details by key ProductCode
	 * 
	 * @param Product Asset Details (productAsset)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ProductAsset productAsset,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From RMTProductAssets");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AssetID = :AssetID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productAsset);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the RMTProductAssets or RMTProductAssets_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Product Asset Details by key ProductCode
	 * 
	 * @param Product Asset Details (productAsset)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteByProduct(ProductAsset productAsset,String type) {
		logger.debug("Entering");
		@SuppressWarnings("unused")
		int recordCount = 0;
		/*ProductAsset productAsset = new ProductAsset();
		productAsset.setProductCode(prodCode);*/
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From RMTProductAssets");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode = :ProductCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productAsset);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			/*if (recordCount <= 0) {
				throw new ConcurrencyException();
			}*/
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	/**
	 * This method insert new Records into RMTProductAssets or RMTProductAssets_Temp.
	 *
	 * save Product  Asset Details 
	 * 
	 * @param Product Assets Details (productAsset)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ProductAsset productAsset,String type) {
		logger.debug("Entering");
		if (productAsset.getId()==Long.MIN_VALUE){
			productAsset.setId(getNextidviewDAO().getNextId("SeqRMTProductAssets"));
			logger.debug("get NextID:"+productAsset.getId());
		}
		StringBuilder insertSql =new StringBuilder();

		insertSql.append("Insert Into RMTProductAssets");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AssetID, ProductCode, AssetCode, AssetDesc, AssetIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AssetID, :ProductCode, :AssetCode, :AssetDesc, :AssetIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productAsset);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return productAsset.getId();
	}

	/**
	 * This method updates the Record RMTProductAssets or RMTProductAssets_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Product  Asset Details by key FinType and Version
	 * 
	 * @param Product Asset (productAsset)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ProductAsset productAsset,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder();
		updateSql.append("Update RMTProductAssets");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ProductCode = :ProductCode, AssetCode = :AssetCode,");
		updateSql.append(" AssetDesc = :AssetDesc, AssetIsActive = :AssetIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AssetID = :AssetID");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productAsset);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}
