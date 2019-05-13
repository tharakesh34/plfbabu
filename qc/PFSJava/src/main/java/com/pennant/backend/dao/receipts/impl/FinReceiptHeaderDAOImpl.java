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

import java.math.BigDecimal;
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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinReceiptHeaderDAOImpl extends SequenceDao<FinReceiptHeader> implements FinReceiptHeaderDAO {
	private static Logger logger = Logger.getLogger(FinReceiptHeaderDAOImpl.class);

	public FinReceiptHeaderDAOImpl() {
		super();
	}

	@Override
	public FinReceiptHeader getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type) {
		logger.debug("Entering");

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finReference);
		header.setRcdMaintainSts(rcdMaintainSts);

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks,");
		selectSql.append(
				" GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount, CashierBranch,InitiateDate,  ");
		selectSql.append(
				" DepositProcess, DepositBranch, LppAmount, GstLpiAmount, GstLppAmount, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ExtReference, Module, FinDivision, PostBranch,ActFinReceipt");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive, ProductCategory, ");
			selectSql.append(
					" collectionAgentCode, collectionAgentDesc, PostBranchDesc, CashierBranchDesc, FinDivisionDesc, EntityCode");
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where Reference =:Reference AND RcdMaintainSts=:RcdMaintainSts ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);

		try {
			header = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			header = null;
		}

		logger.debug("Leaving");
		return header;
	}

	@Override
	public long save(FinReceiptHeader receiptHeader, TableType tableType) {
		logger.debug("Entering");
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextValue("SeqFinReceiptHeader"));
			logger.debug("get NextID:" + receiptHeader.getId());
		}

		StringBuilder sql = new StringBuilder("Insert Into FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		sql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate,CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks,");
		sql.append(
				" GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount,CashierBranch,InitiateDate, ");
		sql.append(
				" DepositProcess, DepositBranch, LppAmount, GstLpiAmount, GstLppAmount, ExtReference, Module, SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber, CollectionAgentId, ActFinReceipt,");
		sql.append(" FinDivision, PostBranch,");
		sql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId )");
		sql.append(
				" Values(:ReceiptID, :ReceiptDate , :ReceiptType, :RecAgainst, :Reference , :ReceiptPurpose,:RcdMaintainSts, ");
		sql.append(
				" :ReceiptMode, :ExcessAdjustTo , :AllocationType , :ReceiptAmount, :EffectSchdMethod, :ReceiptModeStatus, :RealizationDate, :CancelReason, :WaviedAmt, :TotFeeAmount, :BounceDate, :Remarks,");
		sql.append(
				" :GDRAvailable, :ReleaseType, :ThirdPartyName, :ThirdPartyMobileNum, :LpiAmount,:CashierBranch,:InitiateDate, ");
		sql.append(
				" :DepositProcess, :DepositBranch, :LppAmount, :GstLpiAmount, :GstLppAmount, :ExtReference, :Module, :subReceiptMode, :receiptChannel, :receivedFrom, :panNumber, :collectionAgentId,:ActFinReceipt,");
		sql.append(" :FinDivision, :PostBranch,");
		sql.append(
				" :Version, :LastMntOn, :LastMntBy, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId )");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return receiptHeader.getId();
	}

	@Override
	public void update(FinReceiptHeader receiptHeader, TableType tableType) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(tableType.getSuffix());
		updateSql.append(
				" Set ReceiptID=:ReceiptID, ReceiptDate=:ReceiptDate , ReceiptType=:ReceiptType, RecAgainst=RecAgainst, ");
		updateSql.append(
				" Reference=:Reference , ReceiptPurpose=:ReceiptPurpose , ReceiptMode=:ReceiptMode, ExcessAdjustTo=:ExcessAdjustTo , ");
		updateSql.append(
				" AllocationType=:AllocationType , ReceiptAmount=:ReceiptAmount, EffectSchdMethod=:EffectSchdMethod,RcdMaintainSts=:RcdMaintainSts, InitiateDate=:InitiateDate,");
		updateSql.append(
				" ReceiptModeStatus=:ReceiptModeStatus, RealizationDate=:RealizationDate,CancelReason=:CancelReason, WaviedAmt=:WaviedAmt, TotFeeAmount=:TotFeeAmount, BounceDate=:BounceDate, Remarks=:Remarks,");
		updateSql.append(
				" GDRAvailable = :GDRAvailable, ReleaseType = :ReleaseType, ThirdPartyName = :ThirdPartyName, ThirdPartyMobileNum = :ThirdPartyMobileNum,LpiAmount=:LpiAmount,CashierBranch=:CashierBranch,");
		updateSql.append(
				" DepositProcess = :DepositProcess, DepositBranch = :DepositBranch, LppAmount =:LppAmount, GstLpiAmount=:GstLpiAmount, GstLppAmount=:GstLppAmount, "); // for Cash Management
		updateSql.append(
				" Version =:Version, LastMntOn=:LastMntOn, LastMntBy=:LastMntBy, RecordStatus=:RecordStatus, RoleCode=:RoleCode, ");
		updateSql.append(
				" SubReceiptMode=:SubReceiptMode, ReceiptChannel=:ReceiptChannel, ReceivedFrom=:ReceivedFrom, PanNumber=:PanNumber, CollectionAgentId=:CollectionAgentId, ActFinReceipt=:ActFinReceipt,");
		updateSql.append(
				" NextRoleCode=:NextRoleCode, TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId,");
		updateSql.append(" FinDivision = :FinDivision, PostBranch = :PostBranch");
		updateSql.append(" Where ReceiptID =:ReceiptID");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptHeader);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

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
		this.jdbcTemplate.update(deleteSql.toString(), source);
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

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, InstructionUID, ");
		selectSql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks,");
		selectSql.append(
				" GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount,CashierBranch,InitiateDate, ");
		selectSql.append(
				" DepositProcess, DepositBranch, LppAmount, GstLpiAmount, GstLppAmount, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ExtReference, Module, FinDivision, PostBranch,ActFinReceipt");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, ");
			selectSql.append(
					" FinIsActive,PromotionCode,ProductCategory, NextRepayRvwDate, collectionAgentCode, collectionAgentDesc, PostBranchDesc, CashierBranchDesc, FinDivisionDesc, EntityCode");
			if (StringUtils.trimToEmpty(type).contains("FView")) {
				selectSql.append(" ,ScheduleMethod, PftDaysBasis, CustID, CustomerCIF, CustomerName");
			}
			if (StringUtils.trimToEmpty(type).contains("FEView") || StringUtils.trimToEmpty(type).contains("FCView")) {
				selectSql.append(" , CustID, CustomerCIF, CustomerName");
			}
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);

		try {
			header = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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

		StringBuilder selectSql = new StringBuilder(
				"Select count(*)  from (select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader union all ");
		selectSql.append("select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader_Temp)T");
		selectSql.append(
				" Where ReceiptID <> :ReceiptID AND Reference = :Reference AND ReceiptPurpose = :ReceiptPurpose AND ReceiptModeStatus in('A','F')");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("ReceiptPurpose", receiptPurpose);
		source.addValue("ReceiptID", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}

	@Override
	public FinReceiptHeader getServicingFinanceHeader(long receiptID, String userRole, String type) {
		logger.debug("Entering");

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReceiptID(receiptID);
		header.setNextRoleCode(userRole);

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks,");
		selectSql.append(
				" GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount,CashierBranch,InitiateDate, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId, ");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, FinDivision, PostBranch");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive, ");
			selectSql.append(
					"collectionAgentCode, collectionAgentDesc, PostBranchDesc, CashierBranchDesc, FinDivisionDesc, EntityCode");
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID AND NextRoleCode = :NextRoleCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);

		try {
			header = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			header = null;
		}

		logger.debug("Leaving");
		return header;
	}

	@Override
	public List<ReceiptCancelDetail> getReceiptCancelDetailList(Date cancelReqDate, String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("ReceivedDate", cancelReqDate);

		StringBuilder selectSql = new StringBuilder(
				" Select RH.ReceiptID ReceiptId , RD.ReceivedDate ValueDate, (RH.ReceiptAmount + Rh.WaviedAmt ) Amount, Rh.WaviedAmt ");
		selectSql.append(" From FinReceiptHeader RH INNER JOIN FinReceiptDetail RD ON RH.ReceiptID = RD.ReceiptID ");
		selectSql.append(
				" Where RH.Reference =:Reference AND  RD.ReceivedDate >= :ReceivedDate AND RH.ReceiptModeStatus NOT IN('C','B') ORDER BY RH.ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReceiptCancelDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptCancelDetail.class);
		List<ReceiptCancelDetail> rcptCancelDetails = this.jdbcTemplate.query(selectSql.toString(), source,
				typeRowMapper);
		logger.debug("Leaving");
		return rcptCancelDetails;
	}

	@Override
	public void updateReceiptStatus(long receiptID, String status) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ReceiptModeStatus", status);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(" Set ReceiptModeStatus=:ReceiptModeStatus ");
		updateSql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public long generatedReceiptID(FinReceiptHeader receiptHeader) {
		logger.debug("Entering");
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextValue("SeqFinReceiptHeader"));
			logger.debug("get NextID:" + receiptHeader.getId());
		}
		logger.debug("Leaving");
		return receiptHeader.getId();
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
		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("DepositBranch", depositBranch);

		StringBuilder updateSql = new StringBuilder(" Update FinReceiptHeader");
		updateSql.append(type);
		updateSql.append(" Set DepositBranch = :DepositBranch ");
		updateSql.append(" Where ReceiptID = :ReceiptID");

		logger.debug("selectSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	@Override
	public BigDecimal getTotalCashReceiptAmount(String depositBranch, String type) {
		logger.debug("Entering");

		BigDecimal amount = BigDecimal.ZERO;

		StringBuilder selectSql = new StringBuilder("Select Sum(Amount) from FinReceiptDetail");
		//selectSql.append(type);	//check this case when we are submit the cancel request Details not effected to Temp table
		selectSql.append(" Where PaymentType = :PaymentType And ReceiptId In (SELECT ReceiptId FROM FinReceiptHeader");
		selectSql.append(type);
		selectSql.append(
				" Where ReceiptModeStatus = :ReceiptModeStatus And RecordType != :RecordType And DepositBranch = :DepositBranch)");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentType", RepayConstants.RECEIPTMODE_CASH);
		source.addValue("ReceiptModeStatus", RepayConstants.PAYSTATUS_CANCEL);
		source.addValue("RecordType", PennantConstants.RECORD_TYPE_NEW);
		source.addValue("DepositBranch", depositBranch);

		try {
			amount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		} catch (DataAccessException e) {
			logger.error(e);
			amount = BigDecimal.ZERO;
		} finally {
			logger.debug("Leaving");
		}

		return amount;
	}

	/**
	 * 
	 */
	@Override
	public boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type,
			long receiptId) {
		logger.debug("Entering");

		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(ReceiptId) from FinReceiptDetail");
		//selectSql.append(type);	//check this case when we are submit the cancel request Details not effected to Temp table
		selectSql.append(
				" Where PaymentType In (:PaymentType) And ReceiptId In (SELECT ReceiptId FROM FinReceiptHeader");
		selectSql.append(type);
		selectSql.append(
				" Where ReceiptModeStatus = :ReceiptModeStatus And RecordType != :RecordType And DepositBranch = :DepositBranch And ReceiptId = :ReceiptId)");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentType", paymentTypes);
		source.addValue("ReceiptModeStatus", RepayConstants.PAYSTATUS_CANCEL);
		source.addValue("RecordType", PennantConstants.RECORD_TYPE_NEW);
		source.addValue("DepositBranch", depositBranch);
		source.addValue("ReceiptId", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		} finally {
			logger.debug("Leaving");
		}

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type) {

		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks,");
		selectSql.append(
				" GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount,CashierBranch,InitiateDate, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, FinDivision, PostBranch");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, ");
			selectSql.append(
					" FinIsActive,PromotionCode,ProductCategory, NextRepayRvwDate, collectionAgentCode, collectionAgentDesc, PostBranchDesc, CashierBranchDesc, FinDivisionDesc, EntityCode");
			if (StringUtils.trimToEmpty(type).contains("FView")) {
				selectSql.append(" ,ScheduleMethod, PftDaysBasis, CustID, CustomerCIF, CustomerName ");
			}
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID IN (:Receipts)  AND Reference is null ");

		logger.debug("selectSql: " + selectSql.toString());
		/*
		 * SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header); RowMapper<FinReceiptHeader>
		 * typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		 */

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Receipts", receipts);

		logger.debug("Leaving");
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

	}

	@Override
	public void updateReference(String extReference, String finReference, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(type);
		updateSql.append(" SET  Reference=:Reference  ");
		updateSql.append(" Where ExtReference=:ExtReference and Reference is null");

		logger.debug("updateSql: " + updateSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extReference);
		source.addValue("Reference", finReference);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type) {

		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, ");
		selectSql.append(
				" CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks, DepositProcess, DepositBranch, LpiAmount, LppAmount,GstLpiAmount, GstLppAmount,");
		selectSql.append("subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,ExtReference,Module, FinDivision, PostBranch");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" ,FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, ");
			selectSql.append(
					" FinIsActive,PromotionCode,ProductCategory, NextRepayRvwDate, collectionAgentCode, collectionAgentDesc, PostBranchDesc, CashierBranchDesc, FinDivisionDesc, EntityCode");
			if (StringUtils.trimToEmpty(type).contains("FView")) {
				selectSql.append(" ,ScheduleMethod, PftDaysBasis, CustID, CustomerCIF, CustomerName ");
			}
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ExtReference =:ExtReference  AND Reference is null ");

		logger.debug("selectSql: " + selectSql.toString());
		/*
		 * SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header); RowMapper<FinReceiptHeader>
		 * typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		 */

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extRef);

		logger.debug("Leaving");
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

	}

	@Override
	public void cancelReceipts(String finReference) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(" Set ReceiptModeStatus='C'  Where Reference =:Reference");

		logger.debug("updateSql: " + updateSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		this.jdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public List<Long> fetchReceiptIdList(String finreference) {

		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID from FinreceiptHeader where Reference=:Reference");

		logger.debug("selectSql: " + selectSql.toString());
		/*
		 * SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header); RowMapper<FinReceiptHeader>
		 * typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		 */

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finreference);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForList(selectSql.toString(), source, Long.class);

	}

	@Override
	public boolean isExtRefAssigned(String extReference) {

		logger.debug("Entering");
		boolean isAssigned = false;
		int count = 0;
		StringBuilder selectSql = new StringBuilder(
				" Select COUNT(*)  From FinReceiptHeader Where ExtReference =:ExtReference  AND Reference is not null");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extReference);

		logger.debug("Leaving");
		count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		if (count > 0) {
			isAssigned = true;
		}
		return isAssigned;
	}

	@Override
	public boolean checkInProcessPresentments(String reference) {
		MapSqlParameterSource source = null;
		boolean isPresentmentFound = false;

		StringBuilder selectSql = new StringBuilder("Select count(*)  from PresentmentDetails ");
		selectSql.append(" where Id In (select PresentmentId from finScheduleDetails ");
		selectSql.append("  where FinReference = :Reference and presentmentId !=0 ) ");
		selectSql.append(" AND status in ('A','I') and FinReference =:Reference");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		try {
			int count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
			if (count > 0) {
				isPresentmentFound = true;
			}

			logger.debug("Presentment Found");
		} catch (DataAccessException e) {
			logger.error(e);
		}

		return isPresentmentFound;
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeadersByRef(String finReference, String type) {
		logger.debug("Entering");

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose,RcdMaintainSts, ");
		selectSql.append(
				" ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod, ReceiptModeStatus,RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, FinDivision, PostBranch, ActFinReceipt");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(
					" , FinType, FinCcy, FinBranch, CustCIF, CustShrtName,FinTypeDesc, FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive, PostBranchDesc, CashierBranchDesc, FinDivisionDesc, EntityCode");
		}
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where Reference =:Reference order by ReceiptDate, ReceiptID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);

		List<FinReceiptHeader> finReceiptHeaders = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");

		return finReceiptHeaders;
	}

	@Override
	public boolean checkInProcessReceipts(String reference) {
		MapSqlParameterSource source = null;
		boolean isReceiptsInProcess = false;

		StringBuilder selectSql = new StringBuilder("Select count(*)  from FinReceiptHeader_Temp where ");
		selectSql.append(" Reference = :Reference");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		try {
			int count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

			if (count > 0) {
				isReceiptsInProcess = true;
			}

			logger.debug("Some other Receipts are in Process");
		} catch (DataAccessException e) {
			logger.error(e);
		}

		return isReceiptsInProcess;
	}

	/**
	 * check with below parameters in receipt details and return true if exits or false
	 * 
	 * @param reference
	 * @param receiptMode
	 * @param chequeNo
	 * @param favourNumber
	 */
	@Override
	public boolean isReceiptDetailsExits(String reference, String receiptMode, String chequeNo, String favourNumber) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select count(*) from finreceiptheader_temp t ");
		selectSql.append(" inner join finreceiptdetail_temp t1 on t1.receiptid=t.receiptid ");
		selectSql.append(
				" where REFERENCE= :REFERENCE and RECEIPTMODE= :RECEIPTMODE and T1.FAVOURNUMBER= :FAVOURNUMBER and T1.CHEQUEACNO = :CHEQUEACNO ");
		selectSql.append(" and (T.RECEIPTMODESTATUS in ('A') or T.RECEIPTMODESTATUS is null)");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", receiptMode);
		source.addValue("FAVOURNUMBER", favourNumber);
		source.addValue("CHEQUEACNO", chequeNo);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		}

		logger.debug("Leaving");
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 29-10-2018, Ticket id:124998 update receipt mode status and realization date return boolean condition
	 */
	@Override
	public void updateReceiptStatusAndRealizationDate(long receiptID, String status, Date realizationDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ReceiptModeStatus", status);
		source.addValue("RealizationDate", realizationDate);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(" Set ReceiptModeStatus=:ReceiptModeStatus,REALIZATIONDATE = :RealizationDate ");
		updateSql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public List<FinReceiptHeader> getInProcessReceipts(String Reference) {
		StringBuilder selectSql = new StringBuilder("");
		selectSql.append(" Select ReceiptID, AllocationType , ReceiptAmount ");
		selectSql.append(" From FinReceiptHeader_Temp");
		selectSql.append(" Where Reference =:Reference ");
		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", Reference);

		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);
		List<FinReceiptHeader> rchList = null;

		try {
			rchList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		return rchList;
	}

	@Override
	public String getReceiptModeStatus(long receiptID, String type) {
		logger.debug("Entering");
		String modeStatus = "";
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptModeStatus from FinReceiptHeader ");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			modeStatus = this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			modeStatus = "";
		}

		logger.debug("Leaving");
		return modeStatus;
	}

	@Override
	public List<Long> getInProcessReceiptId(String finReference) {
		StringBuilder selectSql = new StringBuilder("");
		selectSql.append(" Select ReceiptID");
		selectSql.append(" From FinReceiptHeader");
		selectSql.append(" Where Reference =:Reference and ReceiptModeStatus ='D' and receiptPurpose='SchdlRepayment'");
		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		List<Long> receiptList = null;

		try {
			receiptList = this.jdbcTemplate.queryForList(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {

		}

		return receiptList;
	}

	@Override
	public void updateLoanInActive(long receiptId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptId);
		source.addValue("LoanActive", 0);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(" Set LoanActive=:LoanActive");
		updateSql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void saveMultiReceipt(FinReceiptHeader finReceiptHeader, FinReceiptDetail finReceiptDetail) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BatchId", finReceiptHeader.getBatchId());
		source.addValue("ReceiptID", finReceiptHeader.getReceiptID());
		source.addValue("ReceiptModeStatus", finReceiptHeader.getReceiptModeStatus());
		source.addValue("BounceDate", finReceiptHeader.getBounceDate());
		source.addValue("RealizationDate", finReceiptHeader.getRealizationDate());
		source.addValue("Remarks", finReceiptHeader.getRemarks());
		source.addValue("CancelReason", finReceiptHeader.getCancelReason());
		source.addValue("DepositDate", finReceiptDetail.getDepositDate());
		source.addValue("ReceiptDate", finReceiptHeader.getReceiptDate());
		source.addValue("FinReference", finReceiptHeader.getReference());
		if (StringUtils.isNotBlank(finReceiptHeader.getRoleCode())
				&& finReceiptHeader.getRoleCode().contains("MAKER")) {
			source.addValue("Stage", "M");
		} else {
			source.addValue("Stage", "C");
		}
		source.addValue("DepositNo", finReceiptDetail.getDepositNo());
		source.addValue("FundingAc", finReceiptDetail.getFundingAc());
		source.addValue("BounceId", finReceiptHeader.getBounceReason());
		source.addValue("UploadStatus", "S");
		source.addValue("Reason", "");

		StringBuilder updateSql = new StringBuilder("Insert into MultiReceiptApproval");
		updateSql.append(
				"  (BatchId, ReceiptModeStatus, BounceDate, RealizationDate, Remarks, CancelReason, ReceiptID, ");
		updateSql.append(
				"  DepositDate, ReceiptDate, FinReference, Stage, DepositNo, FundingAc, BounceId, UploadStatus, Reason )");
		updateSql.append(
				"  values(:BatchId, :ReceiptModeStatus, :BounceDate, :RealizationDate, :Remarks, :CancelReason, :ReceiptID,");
		updateSql.append(
				"  :DepositDate, :ReceiptDate, :FinReference, :Stage, :DepositNo, :FundingAc, :BounceId, :UploadStatus, :Reason)");

		logger.debug("updateSql: " + updateSql.toString());

		//SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptHeader);
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}
}