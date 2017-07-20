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
 * * FileName : CommodityInventoryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-04-2015 * *
 * Modified Date : 23-04-2015 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 23-04-2015 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.commodity.impl;

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

import com.pennant.backend.dao.commodity.CommodityInventoryDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.model.commodity.FinCommodityInventory;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CommodityInventory model</b> class.<br>
 * 
 */
public class CommodityInventoryDAOImpl extends BasisNextidDaoImpl<CommodityInventory> implements CommodityInventoryDAO {
	private static Logger logger = Logger.getLogger(CommodityInventoryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CommodityInventoryDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Commodity Inventory Details details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CommodityInventory
	 */
	@Override
	public CommodityInventory getCommodityInventoryById(final long id, String type) {
		logger.debug("Entering");
		
		CommodityInventory commodityInventory = new CommodityInventory();

		commodityInventory.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select CommodityInvId, BrokerCode,  HoldCertificateNo, CommodityCode, PurchaseDate");
		selectSql.append(", UnitPrice, FinalSettlementDate, PurchaseAmount, Units, Quantity, Location, BulkPurchase ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		selectSql.append(", NextTaskId, RecordType, WorkflowId,CommodityCcy ");
		
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BrokerShrtName,FeeOnUnsold,LocationCode,LocationDesc ");
			selectSql.append(",BrokerCustID,AccountNumber,LovDescCommodityDesc ");
		}
		selectSql.append(" From FCMTCommodityInventory");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CommodityInvId =:CommodityInvId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityInventory);
		RowMapper<CommodityInventory> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommodityInventory.class);

		try {
			commodityInventory = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			commodityInventory = null;
		}
		
		logger.debug("Leaving");
		
		return commodityInventory;
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
	 * This method Deletes the Record from the FCMTCommodityInventory or FCMTCommodityInventory_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Commodity Inventory Details by key
	 * CommodityInvId
	 * 
	 * @param Commodity
	 *            Inventory Details (commodityInventory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CommodityInventory commodityInventory, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FCMTCommodityInventory");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CommodityInvId =:CommodityInvId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityInventory);
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
	 * This method insert new Records into FCMTCommodityInventory or FCMTCommodityInventory_Temp. it fetches the
	 * available Sequence form SeqFCMTCommodityInventory by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Commodity Inventory Details
	 * 
	 * @param Commodity
	 *            Inventory Details (commodityInventory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(CommodityInventory commodityInventory, String type) {
		logger.debug("Entering");
		
		if (commodityInventory.getId() == Long.MIN_VALUE) {
			commodityInventory.setId(getNextidviewDAO().getNextId("SeqFCMTCommodityInventory"));
			logger.debug("get NextID:" + commodityInventory.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FCMTCommodityInventory");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CommodityInvId, BrokerCode,  HoldCertificateNo, CommodityCode, PurchaseDate");
		insertSql.append(", UnitPrice, FinalSettlementDate, PurchaseAmount, Units, Quantity, Location, BulkPurchase");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId,CommodityCcy )");
		insertSql.append(" Values");
		insertSql.append(" (:CommodityInvId, :BrokerCode,  :HoldCertificateNo, :CommodityCode, :PurchaseDate");
		insertSql.append(", :UnitPrice, :FinalSettlementDate, :PurchaseAmount, :Units, :Quantity, :Location, :BulkPurchase");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		insertSql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId, :CommodityCcy )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityInventory);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
		return commodityInventory.getId();
	}

	/**
	 * This method updates the Record FCMTCommodityInventory or FCMTCommodityInventory_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Commodity Inventory Details by key CommodityInvId and Version
	 * 
	 * @param Commodity
	 *            Inventory Details (commodityInventory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CommodityInventory commodityInventory, String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update FCMTCommodityInventory");
		
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set BrokerCode = :BrokerCode");
		updateSql.append(", HoldCertificateNo = :HoldCertificateNo, CommodityCode = :CommodityCode, PurchaseDate = :PurchaseDate");
		updateSql.append(", UnitPrice =:UnitPrice, FinalSettlementDate = :FinalSettlementDate, PurchaseAmount = :PurchaseAmount, Units = :Units");
		updateSql.append(", Quantity = :Quantity, Location = :Location, BulkPurchase = :BulkPurchase");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus");
		updateSql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		updateSql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId, CommodityCcy=:CommodityCcy ");
		updateSql.append(" Where CommodityInvId =:CommodityInvId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityInventory);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for fetch used Commodity Inventories list
	 * 
	 */
	@Override
    public List<FinCommodityInventory> getUsedCommInventory(String brokerCode, String holdCertificateNo) {
		logger.debug("Entering");
		
		FinCommodityInventory finCommodityInventory = new FinCommodityInventory();
		finCommodityInventory.setBrokerCode(brokerCode);
		finCommodityInventory.setHoldCertificateNo(holdCertificateNo);
		finCommodityInventory.setCommodityStatus(PennantConstants.COMMODITY_CANCELLED);
		
		StringBuilder selectSql = new StringBuilder("SELECT  FinInventoryID, Finreference, BrokerCode, HoldCertificateNo,");
		selectSql.append("  Quantity, SaleQuantity, SalePrice, UnitSalePrice, CommodityStatus, DateOfAllocation,");
		selectSql.append("  DateOfSelling, DateCancelled, FeeCalculated, FeePayableDate, FeeBalance ");
		selectSql.append("  FROM  FinCommodityInventory");
		selectSql.append("  Where BrokerCode =:BrokerCode AND HoldCertificateNo =:HoldCertificateNo AND CommodityStatus !=:CommodityStatus");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCommodityInventory);
		RowMapper<FinCommodityInventory> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCommodityInventory.class);

		List<FinCommodityInventory> finCommInventoryList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	
		logger.debug("Leaving");
		return finCommInventoryList;
    }

	/**
	 * Method for getting commodity used finances count
	 * 
	 */
	@Override
	public int getCommodityFinances(String brokerCode, String holdCertificateNo, String status) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BrokerCode", brokerCode);
		source.addValue("HoldCertificateNo", holdCertificateNo);
		source.addValue("CommodityStatus", status);
		
		StringBuilder selectSql = new StringBuilder("SELECT  Count(*) FROM  FinCommodityInventory");
		selectSql.append("  Where BrokerCode=:BrokerCode AND  HoldCertificateNo=:HoldCertificateNo AND CommodityStatus !=:CommodityStatus");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);	
		}catch(EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return 0;
		}

	}
	
	/**
	 * Method for getting CommodityInventory Data from FCMTCommodityInventory
	 * 
	 */
	@Override
    public CommodityInventory getCommodityDetails(String holdCertificateNo, String brokerCode) {
		logger.debug("Entering");
		
		CommodityInventory commodityInventory = new CommodityInventory();
		commodityInventory.setHoldCertificateNo(holdCertificateNo);
		commodityInventory.setBrokerCode(brokerCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select CommodityInvId, BrokerCode,  HoldCertificateNo, CommodityCode, PurchaseDate");
		selectSql.append(", UnitPrice, FinalSettlementDate, PurchaseAmount, Units, Quantity, Location, BulkPurchase ");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		selectSql.append(", NextTaskId, RecordType, WorkflowId, CommodityCcy ");
		selectSql.append(" From FCMTCommodityInventory");
		selectSql.append("  Where   HoldCertificateNo=:HoldCertificateNo AND  BrokerCode=:BrokerCode ");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityInventory);
		RowMapper<CommodityInventory> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommodityInventory.class);
		try {
			commodityInventory = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			commodityInventory = null;
			logger.debug(e);
		}
		logger.debug("Leaving");
		return commodityInventory;
	}

	
	/**
	 * Fetch the count of Commidity Inventories with the Broker Code and Holding number  
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CommodityInventory
	 */
	@Override
	public int getComInvCountByBrokerAndHoldCertNo(CommodityInventory commodityInventory, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select Count(*)  From FCMTCommodityInventory");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  BrokerCode =:BrokerCode And HoldCertificateNo =:HoldCertificateNo");
		if(commodityInventory.getCommodityInvId() > 0 ){										
			selectSql.append(" and commodityInvId != :commodityInvId ");
		}			
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commodityInventory);
 		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
	}

	/**
	 * Method to fetch allocated commodity inventory details
	 * 
	 */
	@Override
	public List<String> getAllocateCmdList(String cmdSts, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CommodityStatus", cmdSts);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference FROM FinCommodityInventory");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE CommodityStatus =:CommodityStatus ");

		logger.debug("selectSql: " + selectSql.toString());
 		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);	
	}
    
}