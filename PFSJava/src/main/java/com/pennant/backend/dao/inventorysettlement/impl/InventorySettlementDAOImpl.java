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
 * FileName    		:  InventorySettlementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.inventorysettlement.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.inventorysettlement.InventorySettlementDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.inventorysettlement.InventorySettlement;
import com.pennant.backend.model.inventorysettlement.InventorySettlementDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>InventorySettlement model</b> class.<br>
 * 
 */

public class InventorySettlementDAOImpl extends SequenceDao<InventorySettlement> implements InventorySettlementDAO {
   private static Logger	logger	= Logger.getLogger(InventorySettlementDAOImpl.class);

	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new InventorySettlement
	 * 
	 * @return InventorySettlement
	 */

	@Override
	public InventorySettlement getInventorySettlement() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("InventorySettlement");
		InventorySettlement inventorySettlement = new InventorySettlement();
		if (workFlowDetails != null) {
			inventorySettlement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return inventorySettlement;
	}

	/**
	 * This method get the module from method getInventorySettlement() and set
	 * the new record flag as true and return InventorySettlement()
	 * 
	 * @return InventorySettlement
	 */

	@Override
	public InventorySettlement getNewInventorySettlement() {
		logger.debug("Entering");
		InventorySettlement inventorySettlement = getInventorySettlement();
		inventorySettlement.setNewRecord(true);
		logger.debug("Leaving");
		return inventorySettlement;
	}

	/**
	 * Fetch the Record Finance Management details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InventorySettlement
	 */
	@Override
	public InventorySettlement getInventorySettlementById(final long id, String type) {
		logger.debug("Entering");
		InventorySettlement inventorySettlement = getInventorySettlement();

		inventorySettlement.setId(id);

		StringBuilder selectSql = new StringBuilder("Select Id, BrokerCode, SettlementDate");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BrokerCodeName");
		}
		selectSql.append(" From InventorySettlement");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Id =:Id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(inventorySettlement);
		RowMapper<InventorySettlement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InventorySettlement.class);

		try {
			inventorySettlement = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			inventorySettlement = null;
		}
		logger.debug("Leaving");
		return inventorySettlement;
	}

	

	/**
	 * This method Deletes the Record from the InventorySettlement or
	 * InventorySettlement_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Finance Management by key Id
	 * 
	 * @param Finance
	 *            Management (inventorySettlement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(InventorySettlement inventorySettlement, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From InventorySettlement");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Id =:Id");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(inventorySettlement);
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
	 * This method insert new Records into InventorySettlement or
	 * InventorySettlement_Temp. it fetches the available Sequence form
	 * SeqInventorySettlement by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Finance Management
	 * 
	 * @param Finance
	 *            Management (inventorySettlement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(InventorySettlement inventorySettlement, String type) {
		logger.debug("Entering");
		if (inventorySettlement.getId() == Long.MIN_VALUE) {
			inventorySettlement.setId(getNextId("SeqInventorySettlement"));
			logger.debug("get NextID:" + inventorySettlement.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into InventorySettlement");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id, BrokerCode, SettlementDate");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Id, :BrokerCode, :SettlementDate");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(inventorySettlement);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return inventorySettlement.getId();
	}

	/**
	 * This method updates the Record InventorySettlement or
	 * InventorySettlement_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Management by key Id
	 * and Version
	 * 
	 * @param Finance
	 *            Management (inventorySettlement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(InventorySettlement inventorySettlement, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update InventorySettlement");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set BrokerCode = :BrokerCode, SettlementDate = :SettlementDate");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Id =:Id");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(inventorySettlement);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fetch Unsold commodity details
	 * 
	 */
	@Override
	public List<InventorySettlementDetails> getSettlementsByBroker(String brokerCode, Date settlementDate) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CommodityStatus", PennantConstants.COMMODITY_SOLD);
		source.addValue("FinalSettlementDate", settlementDate);
		source.addValue("BrokerCode", brokerCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select T.BrokerCode, T.HoldCertificateNo, ci.Quantity, T.SaleQuantity, ");
		selectSql.append(" (ci.Quantity -T.SaleQuantity) UnsoldQty,	CI.CommodityCode, ");
		selectSql.append(" CI.UnitPrice,CI.CommodityCcy,CI.FinalSettlementDate ,FB.FeeOnUnSold,FB.BrokerCustID,FB.AccountNumber   ");
		selectSql.append("  from (select BrokerCode,HoldCertificateNo,SUM(SaleQuantity) SaleQuantity from FinCommodityInventory where BrokerCode = :BrokerCode ");
		selectSql.append(" and CommodityStatus=:CommodityStatus group by HoldCertificateNo,BrokerCode) t ");
		selectSql.append(" inner join FCMTCommodityInventory CI on CI.BrokerCode=t.BrokerCode and CI.HoldCertificateNo=t.HoldCertificateNo   ");
		selectSql.append(" inner join FCMTBrokerDetail FB on Fb.BrokerCode=t.BrokerCode ");
		selectSql.append(" where CI.FinalSettlementDate <=:FinalSettlementDate ");

		RowMapper<InventorySettlementDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InventorySettlementDetails.class);
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public void saveInventorySettelmentDetails(List<InventorySettlementDetails> details) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into InventorySettlementDetails");
		insertSql.append(" (Id,BrokerCode, HoldCertificateNo, UnsoldQty, UnsoldFee)");
		insertSql.append(" Values( ");
		insertSql.append(" :Id,:BrokerCode, :HoldCertificateNo, :UnsoldQty, :UnsoldFee)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(details.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

}