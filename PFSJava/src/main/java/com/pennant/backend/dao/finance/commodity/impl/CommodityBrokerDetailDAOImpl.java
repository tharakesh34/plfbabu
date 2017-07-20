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
 * FileName    		:  CommodityBrokerDetailDAOImpl.java                                                   * 	  
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.commodity.CommodityBrokerDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.commodity.BrokerCommodityDetail;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CommodityBrokerDetail model</b> class.<br>
 * 
 */

public class CommodityBrokerDetailDAOImpl extends BasisCodeDAO<CommodityBrokerDetail> implements CommodityBrokerDetailDAO {

	private static Logger logger = Logger.getLogger(CommodityBrokerDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CommodityBrokerDetailDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record  Commodity Broker Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommodityBrokerDetail
	 */
	@Override
	public CommodityBrokerDetail getCommodityBrokerDetailById(final String id, String type) {
		logger.debug("Entering");
		CommodityBrokerDetail commodityBrokerDetail = new CommodityBrokerDetail();
		
		commodityBrokerDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select BrokerCode, BrokerCustID, BrokerFrom, BrokerAddrHNbr, BrokerAddrFlatNbr,");
		selectSql.append(" BrokerAddrStreet, BrokerAddrLane1, BrokerAddrLane2, BrokerAddrPOBox, BrokerAddrCountry, BrokerAddrProvince,");
		selectSql.append(" BrokerAddrCity, BrokerAddrZIP, BrokerAddrPhone, BrokerAddrFax, BrokerEmail, AgreementRef, feeOnUnsold, AccountNumber, CommissionRate,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescBrokerShortName,lovDescBrokerAddrCountryName,lovDescBrokerAddrProvinceName,lovDescBrokerAddrCityName,lovDescBrokerCIF ");
		}
		selectSql.append(" From FCMTBrokerDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BrokerCode =:BrokerCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityBrokerDetail);
		RowMapper<CommodityBrokerDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommodityBrokerDetail.class);
		
		try{
			commodityBrokerDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			commodityBrokerDetail = null;
		}
		logger.debug("Leaving");
		return commodityBrokerDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FCMTBrokerDetail or FCMTBrokerDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Commodity Broker Detail by key BrokerCode
	 * 
	 * @param Commodity Broker Detail (commodityBrokerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CommodityBrokerDetail commodityBrokerDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FCMTBrokerDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BrokerCode =:BrokerCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityBrokerDetail);
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
	 * This method insert new Records into FCMTBrokerDetail or FCMTBrokerDetail_Temp.
	 *
	 * save Commodity Broker Detail 
	 * 
	 * @param Commodity Broker Detail (commodityBrokerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(CommodityBrokerDetail commodityBrokerDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FCMTBrokerDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BrokerCode, BrokerCustID, BrokerFrom, BrokerAddrHNbr, BrokerAddrFlatNbr, BrokerAddrStreet, BrokerAddrLane1," );
		insertSql.append(" BrokerAddrLane2, BrokerAddrPOBox, BrokerAddrCountry, BrokerAddrProvince, BrokerAddrCity, BrokerAddrZIP, ");
		insertSql.append("  BrokerAddrPhone, BrokerAddrFax, BrokerEmail, AgreementRef, feeOnUnsold, AccountNumber, CommissionRate,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BrokerCode, :BrokerCustID, :BrokerFrom, :BrokerAddrHNbr, :BrokerAddrFlatNbr, :BrokerAddrStreet," );
		insertSql.append(" :BrokerAddrLane1, :BrokerAddrLane2, :BrokerAddrPOBox, :BrokerAddrCountry, :BrokerAddrProvince, :BrokerAddrCity, :BrokerAddrZIP," );
		insertSql.append(" :BrokerAddrPhone, :BrokerAddrFax, :BrokerEmail, :AgreementRef, :feeOnUnsold, :AccountNumber, :CommissionRate,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityBrokerDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return commodityBrokerDetail.getId();
	}
	
	/**
	 * This method updates the Record FCMTBrokerDetail or FCMTBrokerDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Commodity Broker Detail by key BrokerCode and Version
	 * 
	 * @param Commodity Broker Detail (commodityBrokerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(CommodityBrokerDetail commodityBrokerDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FCMTBrokerDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set BrokerCustID = :BrokerCustID, BrokerFrom = :BrokerFrom,");
		updateSql.append(" BrokerAddrHNbr = :BrokerAddrHNbr, BrokerAddrFlatNbr = :BrokerAddrFlatNbr, BrokerAddrStreet = :BrokerAddrStreet,");
		updateSql.append(" BrokerAddrLane1 = :BrokerAddrLane1, BrokerAddrLane2 = :BrokerAddrLane2, BrokerAddrPOBox = :BrokerAddrPOBox,");
		updateSql.append(" BrokerAddrCountry = :BrokerAddrCountry, BrokerAddrProvince = :BrokerAddrProvince, BrokerAddrCity = :BrokerAddrCity,");
		updateSql.append(" BrokerAddrZIP = :BrokerAddrZIP, BrokerAddrPhone = :BrokerAddrPhone, BrokerAddrFax = :BrokerAddrFax,");
		updateSql.append(" BrokerEmail = :BrokerEmail, AgreementRef = :AgreementRef, feeOnUnsold = :feeOnUnsold, AccountNumber = :AccountNumber, CommissionRate=:CommissionRate, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BrokerCode =:BrokerCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityBrokerDetail);
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
	@Override
	public List<BrokerCommodityDetail> getBrokerCommodityDetails(String id,String type) {
		logger.debug("Entering");
		BrokerCommodityDetail brokerCommodityDetail = new BrokerCommodityDetail();
		brokerCommodityDetail.setBrokerCode(id);
		
		StringBuilder selectSql = new StringBuilder("Select BrokerCode, CommodityCode,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From BrokerCommodityDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BrokerCode = :BrokerCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(brokerCommodityDetail);
		RowMapper<BrokerCommodityDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BrokerCommodityDetail.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 *  Method for saving Broker Commodity Details
	 * @throws Exception 
	 */
	@Override
	public void saveCommodities(CommodityBrokerDetail commodityBrokerDetail,String type){
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into BrokerCommodityDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BrokerCode, CommodityCode, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BrokerCode, :CommodityCode, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(commodityBrokerDetail.getBrokerCommodityList().toArray());
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
	}
	
	

	/**o
	 * This method Deletes the Record from the BrokerCommodityDetail or BrokerCommodityDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Commodity Broker Detail by key BrokerCode
	 * 
	 * @param Commodity  Detail (BrokerCommodityDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteCommodities(CommodityBrokerDetail commodityBrokerDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder deleteSql = new StringBuilder("Delete From BrokerCommodityDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BrokerCode =:BrokerCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityBrokerDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
}