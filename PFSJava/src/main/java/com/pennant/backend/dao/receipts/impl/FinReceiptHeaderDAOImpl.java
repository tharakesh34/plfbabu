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
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.receipts.impl;

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
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinReceiptHeaderDAOImpl extends BasisNextidDaoImpl<FinReceiptHeader> implements FinReceiptHeaderDAO {
	private static Logger	           logger	= Logger.getLogger(FinReceiptHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinReceiptHeaderDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public FinReceiptHeader getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type) {
		logger.debug("Entering");

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finReference);
		header.setRcdMaintainSts(rcdMaintainSts);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks, DepositProcess," );
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append( " ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive ");
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where Reference =:Reference AND RcdMaintainSts=:RcdMaintainSts ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);

		try {
			header = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			header = null;
		}

		logger.debug("Leaving");
		return header;
	}
	
	
	/**
	 * @param receiptHeader
	 * @return
	 */
	@Override
	public long generatedReceiptID(FinReceiptHeader receiptHeader) {
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextidviewDAO().getNextId("SeqFinReceiptHeader"));
			logger.debug("get NextID:" + receiptHeader.getId());
		}
		return receiptHeader.getId();
	}

	@Override
	public long save(FinReceiptHeader receiptHeader, TableType tableType) {
		logger.debug("Entering");
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextidviewDAO().getNextId("SeqFinReceiptHeader"));
			logger.debug("get NextID:" + receiptHeader.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinReceiptHeader");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		insertSql.append(" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate,CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks, DepositProcess,");
		insertSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId )");
		insertSql.append(" Values(:ReceiptID, :ReceiptDate , :ReceiptType, :RecAgainst, :Reference , :ReceiptPurpose,:RcdMaintainSts, ");
		insertSql.append(" :ReceiptMode, :ExcessAdjustTo , :AllocationType , :ReceiptAmount, :EffectSchdMethod, :ReceiptModeStatus, :RealizationDate, :CancelReason, :WaviedAmt, :TotFeeAmount, :BounceDate, :Remarks, :DepositProcess,");
		insertSql.append(" :Version, :LastMntOn, :LastMntBy, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return receiptHeader.getId();
	}

	@Override
	public void update(FinReceiptHeader receiptHeader, TableType tableType) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set ReceiptID=:ReceiptID, ReceiptDate=:ReceiptDate , ReceiptType=:ReceiptType, RecAgainst=RecAgainst, ");
		updateSql.append(" Reference=:Reference , ReceiptPurpose=:ReceiptPurpose , ReceiptMode=:ReceiptMode, ExcessAdjustTo=:ExcessAdjustTo , ");
		updateSql.append(" AllocationType=:AllocationType , ReceiptAmount=:ReceiptAmount, EffectSchdMethod=:EffectSchdMethod,RcdMaintainSts=:RcdMaintainSts, ");
		updateSql.append(" ReceiptModeStatus=:ReceiptModeStatus, RealizationDate=:RealizationDate,CancelReason=:CancelReason, WaviedAmt=:WaviedAmt, TotFeeAmount=:TotFeeAmount, BounceDate=:BounceDate, Remarks=:Remarks,");
		updateSql.append(" DepositProcess = :DepositProcess,");
		updateSql.append(" Version =:Version, LastMntOn=:LastMntOn, LastMntBy=:LastMntBy, RecordStatus=:RecordStatus, RoleCode=:RoleCode, ");
		updateSql.append(" NextRoleCode=:NextRoleCode, TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId  ");
		updateSql.append(" Where ReceiptID =:ReceiptID");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinReceiptHeader");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptID=:ReceiptID ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Method for Fetching Finance Receipt Header by using Receipt ID
	 */
	@Override
	public FinReceiptHeader getReceiptHeaderByID(long receiptID, String type) {
		logger.debug("Entering");

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReceiptID(receiptID);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks, DepositProcess," );
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append( " ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive ");
			if (StringUtils.trimToEmpty(type).contains("FView")) {
				selectSql.append( " ,ScheduleMethod, PftDaysBasis, CustID ");
			}
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);

		try {
			header = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			header = null;
		}

		logger.debug("Leaving");
		return header;
	}


	@Override
	public int geFeeReceiptCount(String reference, String receiptPurpose, long receiptId) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select count(*)  from (select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader union all ");
		selectSql.append("select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader_Temp)T");
		selectSql.append(" Where ReceiptID <> :ReceiptID AND Reference = :Reference AND ReceiptPurpose = :ReceiptPurpose AND ReceiptModeStatus in('A','F')");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("ReceiptPurpose", receiptPurpose);
		source.addValue("ReceiptID", receiptId);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}
	
	@Override
	public void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("DepositProcess", depositProcess);

		StringBuilder updateSql = new StringBuilder(" Update FinReceiptHeader");
		updateSql.append(type);
		updateSql.append(" Set DepositProcess = :DepositProcess ");
		updateSql.append(" Where ReceiptID = :ReceiptID");

		logger.debug("selectSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		
		logger.debug("Leaving");
	}

}
