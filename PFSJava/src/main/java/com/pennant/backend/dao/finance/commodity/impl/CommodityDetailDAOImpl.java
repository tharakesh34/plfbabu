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
 * FileName    		:  CommodityDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.commodity.impl;


import java.util.List;

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

import com.pennant.backend.dao.finance.commodity.CommodityDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.commodity.BrokerCommodityDetail;
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CommodityDetail model</b> class.<br>
 * 
 */

public class CommodityDetailDAOImpl extends BasisCodeDAO<CommodityDetail> implements CommodityDetailDAO {

	private static Logger logger = Logger.getLogger(CommodityDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CommodityDetailDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record  Commodity Details details by key field
	 * 
	 * @param   commodityDetail (CommodityDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommodityDetail
	 */
	@Override
	public CommodityDetail getCommodityDetailById(final String id,String commodityUnitCode, String type) {
		logger.debug("Entering");
		
		CommodityDetail commodityDetail = new CommodityDetail();
		commodityDetail.setId(id);
		commodityDetail.setCommodityUnitCode(commodityUnitCode);
		StringBuilder selectSql = new StringBuilder("Select CommodityCode, CommodityName, CommodityUnitCode");
		selectSql.append(", CommodityUnitName, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		selectSql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From FCMTCommodityDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CommodityCode =:CommodityCode and CommodityUnitCode=:CommodityUnitCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityDetail);
		RowMapper<CommodityDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommodityDetail.class);
		
		try{
			commodityDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			commodityDetail = null;
		}
		logger.debug("Leaving");
		return commodityDetail;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FCMTCommodityDetail or FCMTCommodityDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Commodity Details by key CommodityCode
	 * 
	 * @param  commodityDetail (CommodityDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CommodityDetail commodityDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FCMTCommodityDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CommodityCode =:CommodityCode  and CommodityUnitCode=:CommodityUnitCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityDetail);
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
	 * This method insert new Records into FCMTCommodityDetail or FCMTCommodityDetail_Temp.
	 *
	 * save Commodity Details 
	 * 
	 * @param  commodityDetail (CommodityDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(CommodityDetail commodityDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FCMTCommodityDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CommodityCode, CommodityName, CommodityUnitCode, CommodityUnitName");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		insertSql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CommodityCode, :CommodityName, :CommodityUnitCode, :CommodityUnitName");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		insertSql.append(", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return commodityDetail.getId();
	}
	
	/**
	 * This method updates the Record FCMTCommodityDetail or FCMTCommodityDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Commodity Details by key CommodityCode and Version
	 * 
	 * @param  commodityDetail (CommodityDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(CommodityDetail commodityDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FCMTCommodityDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CommodityName = :CommodityName, CommodityUnitName = :CommodityUnitName");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
	    updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
	   updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CommodityCode =:CommodityCode and CommodityUnitCode = :CommodityUnitCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record  Commodity Broker Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommodityBrokerDetail
	 */
	public boolean getBrokerCommodityDetails(String code) {
		logger.debug("Entering");
		BrokerCommodityDetail brokerCommodityDetail = new BrokerCommodityDetail();
		brokerCommodityDetail.setCommodityCode(code);
		
		StringBuilder selectSql = new StringBuilder("Select BrokerCode,commodityCode ");
			
		selectSql.append(" From BrokerCommodityDetail");
		selectSql.append(" Where CommodityCode = :CommodityCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(brokerCommodityDetail);
		RowMapper<BrokerCommodityDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BrokerCommodityDetail.class);
		
		logger.debug("Leaving");
		List<BrokerCommodityDetail> commList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		if(commList.size() > 0){
			return true;
		}else{
			return false;
		}
		
	}
}