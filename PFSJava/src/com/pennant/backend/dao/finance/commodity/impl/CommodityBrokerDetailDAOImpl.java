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
import com.pennant.backend.dao.finance.commodity.CommodityBrokerDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CommodityBrokerDetail model</b> class.<br>
 * 
 */

public class CommodityBrokerDetailDAOImpl extends BasisCodeDAO<CommodityBrokerDetail> implements CommodityBrokerDetailDAO {

	private static Logger logger = Logger.getLogger(CommodityBrokerDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CommodityBrokerDetail 
	 * @return CommodityBrokerDetail
	 */

	@Override
	public CommodityBrokerDetail getCommodityBrokerDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CommodityBrokerDetail");
		CommodityBrokerDetail commodityBrokerDetail= new CommodityBrokerDetail();
		if (workFlowDetails!=null){
			commodityBrokerDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return commodityBrokerDetail;
	}

	/**
	 * This method get the module from method getCommodityBrokerDetail() and set the new record flag as true and return CommodityBrokerDetail()   
	 * @return CommodityBrokerDetail
	 */

	@Override
	public CommodityBrokerDetail getNewCommodityBrokerDetail() {
		logger.debug("Entering");
		CommodityBrokerDetail commodityBrokerDetail = getCommodityBrokerDetail();
		commodityBrokerDetail.setNewRecord(true);
		logger.debug("Leaving");
		return commodityBrokerDetail;
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
		
		StringBuilder selectSql = new StringBuilder("Select BrokerCode, BrokerCustID, BrokerFrom, BrokerAddrHNbr, BrokerAddrFlatNbr");
		selectSql.append(", BrokerAddrStreet, BrokerAddrLane1, BrokerAddrLane2, BrokerAddrPOBox, BrokerAddrCountry, BrokerAddrProvince,");
		selectSql.append("BrokerAddrCity, BrokerAddrZIP, BrokerAddrPhone, BrokerAddrFax, BrokerEmail, AgreementRef");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

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
			commodityBrokerDetail = null;
		}
		logger.debug("Leaving");
		return commodityBrokerDetail;
	}
	
	/**
	 * This method initialize the Record.
	 * @param CommodityBrokerDetail (commodityBrokerDetail)
 	 * @return CommodityBrokerDetail
	 */
	@Override
	public void initialize(CommodityBrokerDetail commodityBrokerDetail) {
		super.initialize(commodityBrokerDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param CommodityBrokerDetail (commodityBrokerDetail)
 	 * @return void
	 */
	@Override
	public void refresh(CommodityBrokerDetail commodityBrokerDetail) {
		
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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41003",commodityBrokerDetail.getId() 
						,commodityBrokerDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",commodityBrokerDetail.getId() 
					,commodityBrokerDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
		insertSql.append(" (BrokerCode, BrokerCustID, BrokerFrom, BrokerAddrHNbr, BrokerAddrFlatNbr, BrokerAddrStreet, BrokerAddrLane1" );
		insertSql.append(", BrokerAddrLane2, BrokerAddrPOBox, BrokerAddrCountry, BrokerAddrProvince, BrokerAddrCity, BrokerAddrZIP, BrokerAddrPhone, BrokerAddrFax, BrokerEmail, AgreementRef");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BrokerCode, :BrokerCustID, :BrokerFrom, :BrokerAddrHNbr, :BrokerAddrFlatNbr, :BrokerAddrStreet" );
		insertSql.append(", :BrokerAddrLane1, :BrokerAddrLane2, :BrokerAddrPOBox, :BrokerAddrCountry" );
		insertSql.append(", :BrokerAddrProvince, :BrokerAddrCity, :BrokerAddrZIP, :BrokerAddrPhone, :BrokerAddrFax, :BrokerEmail, :AgreementRef");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
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
	
	@SuppressWarnings("serial")
	@Override
	public void update(CommodityBrokerDetail commodityBrokerDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FCMTBrokerDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set BrokerCode = :BrokerCode, BrokerCustID = :BrokerCustID, BrokerFrom = :BrokerFrom" +
				", BrokerAddrHNbr = :BrokerAddrHNbr, BrokerAddrFlatNbr = :BrokerAddrFlatNbr, BrokerAddrStreet = :BrokerAddrStreet" +
				", BrokerAddrLane1 = :BrokerAddrLane1, BrokerAddrLane2 = :BrokerAddrLane2, BrokerAddrPOBox = :BrokerAddrPOBox, BrokerAddrCountry = :BrokerAddrCountry, BrokerAddrProvince = :BrokerAddrProvince, BrokerAddrCity = :BrokerAddrCity, BrokerAddrZIP = :BrokerAddrZIP, BrokerAddrPhone = :BrokerAddrPhone, BrokerAddrFax = :BrokerAddrFax, BrokerEmail = :BrokerEmail, AgreementRef = :AgreementRef");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus" +
				", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId" +
				", RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BrokerCode =:BrokerCode");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityBrokerDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",commodityBrokerDetail.getId() ,commodityBrokerDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String brokerCode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = brokerCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_BrokerCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}