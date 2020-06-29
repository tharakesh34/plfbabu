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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
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
	private static Logger logger = LogManager.getLogger(FinReceiptHeaderDAOImpl.class);

	public FinReceiptHeaderDAOImpl() {
		super();
	}

	@Override
	public FinReceiptHeader getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ? and RcdMaintainSts = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, rcdMaintainSts },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public long save(FinReceiptHeader receiptHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextValue("SeqFinReceiptHeader"));
			logger.debug("get NextID:" + receiptHeader.getId());
		}

		StringBuilder sql = new StringBuilder("Insert Into FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append("(ReceiptID, ReceiptDate , ReceiptType, RecAgainst, Reference , ReceiptPurpose, RcdMaintainSts");
		sql.append(", ReceiptMode, ExcessAdjustTo , AllocationType , ReceiptAmount, EffectSchdMethod");
		sql.append(", ReceiptModeStatus,RealizationDate,CancelReason, WaviedAmt, TotFeeAmount, BounceDate");
		sql.append(", Remarks, GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount");
		sql.append(", CashierBranch, InitiateDate, DepositProcess, DepositBranch, LppAmount, GstLpiAmount");
		sql.append(", GstLppAmount, ExtReference, Module, SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber");
		sql.append(", CollectionAgentId, ActFinReceipt, FinDivision, PostBranch, ReasonCode, CancelRemarks, Version");
		sql.append(", LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId )");
		sql.append(" Values");
		sql.append(" (:ReceiptID, :ReceiptDate , :ReceiptType, :RecAgainst, :Reference, :ReceiptPurpose");
		sql.append(", :RcdMaintainSts, :ReceiptMode, :ExcessAdjustTo , :AllocationType, :ReceiptAmount");
		sql.append(", :EffectSchdMethod, :ReceiptModeStatus, :RealizationDate, :CancelReason, :WaviedAmt");
		sql.append(", :TotFeeAmount, :BounceDate, :Remarks, :GDRAvailable, :ReleaseType, :ThirdPartyName");
		sql.append(", :ThirdPartyMobileNum, :LpiAmount,:CashierBranch,:InitiateDate, :DepositProcess");
		sql.append(", :DepositBranch, :LppAmount, :GstLpiAmount, :GstLppAmount, :ExtReference, :Module");
		sql.append(", :subReceiptMode, :receiptChannel, :receivedFrom, :panNumber, :collectionAgentId");
		sql.append(", :ActFinReceipt, :FinDivision, :PostBranch, :ReasonCode, :CancelRemarks, :Version, :LastMntOn");
		sql.append(", :LastMntBy, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType");
		sql.append(", :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return receiptHeader.getId();
	}

	@Override
	public void update(FinReceiptHeader receiptHeader, TableType tableType) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set ReceiptID=:ReceiptID, ReceiptDate=:ReceiptDate, ReceiptType=:ReceiptType");
		sql.append(", RecAgainst=RecAgainst, Reference=:Reference , ReceiptPurpose=:ReceiptPurpose");
		sql.append(", ReceiptMode=:ReceiptMode, ExcessAdjustTo=:ExcessAdjustTo,  AllocationType=:AllocationType");
		sql.append(", ReceiptAmount=:ReceiptAmount, EffectSchdMethod=:EffectSchdMethod");
		sql.append(", RcdMaintainSts=:RcdMaintainSts, InitiateDate=:InitiateDate");
		sql.append(", ReceiptModeStatus=:ReceiptModeStatus , RealizationDate=:RealizationDate");
		sql.append(", CancelReason=:CancelReason, WaviedAmt=:WaviedAmt, TotFeeAmount=:TotFeeAmount");
		sql.append(", BounceDate=:BounceDate, Remarks=:Remarks,GDRAvailable = :GDRAvailable");
		sql.append(", ReleaseType = :ReleaseType, ThirdPartyName = :ThirdPartyName");
		sql.append(", ThirdPartyMobileNum = :ThirdPartyMobileNum ,LpiAmount=:LpiAmount,CashierBranch=:CashierBranch");
		sql.append(", DepositProcess = :DepositProcess, DepositBranch = :DepositBranch, LppAmount =:LppAmount");
		sql.append(", GstLpiAmount=:GstLpiAmount, GstLppAmount=:GstLppAmount, Version =:Version, LastMntOn=:LastMntOn");
		sql.append(", LastMntBy=:LastMntBy, RecordStatus=:RecordStatus, RoleCode=:RoleCode");
		sql.append(", SubReceiptMode=:SubReceiptMode, ReceiptChannel=:ReceiptChannel, ReceivedFrom=:ReceivedFrom");
		sql.append(", PanNumber=:PanNumber, CollectionAgentId=:CollectionAgentId, ActFinReceipt=:ActFinReceipt");
		sql.append(", NextRoleCode=:NextRoleCode, TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType");
		sql.append(", WorkflowId=:WorkflowId, FinDivision = :FinDivision, PostBranch = :PostBranch");
		sql.append(", ReasonCode = :ReasonCode, CancelRemarks = :CancelRemarks");
		sql.append(" Where ReceiptID =:ReceiptID");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptHeader);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder sql = new StringBuilder(" DELETE From FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where ReceiptID=:ReceiptID ");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Finance Receipt Header by using Receipt ID
	 */
	@Override
	public FinReceiptHeader getReceiptHeaderByID(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not available for the Receipt ID {}", receiptID);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ReceiptDate, ReceiptType, RecAgainst, Reference, ReceiptPurpose, RcdMaintainSts");
		sql.append(", InstructionUID, ReceiptMode, ExcessAdjustTo, AllocationType, ReceiptAmount, EffectSchdMethod");
		sql.append(", ReceiptModeStatus, RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate");
		sql.append(", Remarks, GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount");
		sql.append(", CashierBranch, InitiateDate, DepositProcess, DepositBranch, LppAmount, GstLpiAmount");
		sql.append(", GstLppAmount, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId");
		sql.append(", ExtReference, Module, FinDivision, PostBranch, ActFinReceipt, ReasonCode, CancelRemarks");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, FinCcy, FinBranch, CustCIF, CustShrtName, FinTypeDesc");
			sql.append(", FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive, PromotionCode, ProductCategory");
			sql.append(", NextRepayRvwDate, CollectionAgentCode, CollectionAgentDesc, PostBranchDesc");
			sql.append(", CashierBranchDesc, FinDivisionDesc, EntityCode");

			if (StringUtils.trimToEmpty(type).contains("FView")) {
				sql.append(", ScheduleMethod, PftDaysBasis, CustID, CustomerCIF");
				sql.append(", CustomerName, CustBaseCcy");
			}

			if (StringUtils.trimToEmpty(type).contains("FEView") || StringUtils.trimToEmpty(type).contains("FCView")) {
				sql.append(", CustID, CustomerCIF, CustomerName");
			}
		}

		sql.append(" from FinReceiptHeader");
		sql.append(StringUtils.trim(type));
		return sql;
	}

	@Override
	public int geFeeReceiptCount(String reference, String receiptPurpose, long receiptId) {
		int count = 0;

		StringBuilder sql = new StringBuilder("Select count(*)  from (");
		sql.append(" select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader union all ");
		sql.append(" select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader_Temp) T");
		sql.append(" Where ReceiptID <> :ReceiptID and Reference = :Reference");
		sql.append(" and ReceiptPurpose = :ReceiptPurpose and ReceiptModeStatus in('A','F')");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("ReceiptPurpose", receiptPurpose);
		source.addValue("ReceiptID", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION);
		}

		return count;
	}

	@Override
	public FinReceiptHeader getServicingFinanceHeader(long receiptID, String userRole, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID = ? and NextRoleCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID, userRole }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<ReceiptCancelDetail> getReceiptCancelDetailList(Date cancelReqDate, String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("ReceivedDate", cancelReqDate);

		StringBuilder sql = new StringBuilder(" Select RH.ReceiptID ReceiptId , RD.ReceivedDate ValueDate");
		sql.append(", (RH.ReceiptAmount + Rh.WaviedAmt ) Amount, Rh.WaviedAmt ");
		sql.append(" From FinReceiptHeader RH INNER JOIN FinReceiptDetail RD ON RH.ReceiptID = RD.ReceiptID");
		sql.append(" Where RH.Reference =:Reference AND  RD.ReceivedDate >= :ReceivedDate");
		sql.append("  AND RH.ReceiptModeStatus NOT IN('C','B') ORDER BY RH.ReceiptID ");

		logger.debug(Literal.SQL + sql.toString());
		RowMapper<ReceiptCancelDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptCancelDetail.class);
		List<ReceiptCancelDetail> rcptCancelDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return rcptCancelDetails;
	}

	@Override
	public void updateReceiptStatus(long receiptID, String status) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ReceiptModeStatus", status);

		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(" Set ReceiptModeStatus=:ReceiptModeStatus ");
		sql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public long generatedReceiptID(FinReceiptHeader receiptHeader) {
		logger.debug(Literal.ENTERING);
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextValue("SeqFinReceiptHeader"));
			logger.debug("get NextID:" + receiptHeader.getId());
		}
		logger.debug(Literal.LEAVING);
		return receiptHeader.getId();
	}

	@Override
	public void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("DepositProcess", depositProcess);

		StringBuilder sql = new StringBuilder(" Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set DepositProcess = :DepositProcess ");
		sql.append(" Where ReceiptID = :ReceiptID");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("DepositBranch", depositBranch);

		StringBuilder sql = new StringBuilder(" Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set DepositBranch = :DepositBranch ");
		sql.append(" Where ReceiptID = :ReceiptID");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	@Override
	public BigDecimal getTotalCashReceiptAmount(String depositBranch, String type) {
		logger.debug(Literal.ENTERING);

		BigDecimal amount = BigDecimal.ZERO;

		StringBuilder sql = new StringBuilder("Select Sum(Amount) from FinReceiptDetail");
		//selectSql.append(type);	//check this case when we are submit the cancel request Details not effected to Temp table
		sql.append(" Where PaymentType = :PaymentType And ReceiptId In (SELECT ReceiptId FROM FinReceiptHeader");
		sql.append(type);
		sql.append(" Where ReceiptModeStatus = :ReceiptModeStatus And RecordType != :RecordType");
		sql.append(" And DepositBranch = :DepositBranch)");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentType", RepayConstants.RECEIPTMODE_CASH);
		source.addValue("ReceiptModeStatus", RepayConstants.PAYSTATUS_CANCEL);
		source.addValue("RecordType", PennantConstants.RECORD_TYPE_NEW);
		source.addValue("DepositBranch", depositBranch);

		try {
			amount = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (DataAccessException e) {
			logger.error(e);
			amount = BigDecimal.ZERO;
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return amount;
	}

	/**
	 * 
	 */
	@Override
	public boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type,
			long receiptId) {
		logger.debug(Literal.ENTERING);

		int count = 0;

		StringBuilder sql = new StringBuilder("Select Count(ReceiptId) from FinReceiptDetail");
		//selectSql.append(type);	//check this case when we are submit the cancel request Details not effected to Temp table
		sql.append(" Where PaymentType In (:PaymentType) And ReceiptId In (");
		sql.append(" SELECT ReceiptId FROM FinReceiptHeader");
		sql.append(type);
		sql.append(" Where ReceiptModeStatus = :ReceiptModeStatus And RecordType != :RecordType");
		sql.append(" And DepositBranch = :DepositBranch And ReceiptId = :ReceiptId)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentType", paymentTypes);
		source.addValue("ReceiptModeStatus", RepayConstants.PAYSTATUS_CANCEL);
		source.addValue("RecordType", PennantConstants.RECORD_TYPE_NEW);
		source.addValue("DepositBranch", depositBranch);
		source.addValue("ReceiptId", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		} finally {
			logger.debug(Literal.LEAVING);
		}

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID IN (");
		int i = 0;
		while (i < receipts.size()) {
			sql.append(" ?,");
			i++;
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")  and Reference is null ");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					for (Long receiptId : receipts) {
						ps.setLong(index++, receiptId);
					}
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void updateReference(String extReference, String finReference, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(type);
		sql.append(" SET  Reference=:Reference  ");
		sql.append(" Where ExtReference=:ExtReference and Reference is null");

		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extReference);
		source.addValue("Reference", finReference);
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ExtReference = ? and Reference is null ");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, extRef);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void cancelReceipts(String finReference) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(" Set ReceiptModeStatus='C'  Where Reference =:Reference");

		logger.debug("updateSql: " + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> fetchReceiptIdList(String finreference) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Select ReceiptID from FinreceiptHeader where Reference=:Reference");

		logger.debug(Literal.SQL + sql.toString());
		/*
		 * SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header); RowMapper<FinReceiptHeader>
		 * typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		 */

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finreference);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(sql.toString(), source, Long.class);

	}

	@Override
	public boolean isExtRefAssigned(String extReference) {

		logger.debug(Literal.ENTERING);
		boolean isAssigned = false;
		int count = 0;
		StringBuilder sql = new StringBuilder(" Select COUNT(*)  From FinReceiptHeader");
		sql.append(" Where ExtReference =:ExtReference  AND Reference is not null");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extReference);

		logger.debug(Literal.LEAVING);
		count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		if (count > 0) {
			isAssigned = true;
		}
		return isAssigned;
	}

	@Override
	public boolean checkInProcessPresentments(String finReference) {
		StringBuilder sql = new StringBuilder("Select count(*) from PresentmentDetails");
		sql.append(" where Id In (select PresentmentId from finScheduleDetails");
		sql.append(" where FinReference = ? and presentmentId != ?)");
		sql.append(" and status in (?) and FinReference = ? and Excludereason = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { finReference, 0, "A", finReference, 0 }, Integer.class) > 0;
		} catch (DataAccessException e) {
			logger.warn("Presement not found for the Loan Reference {} with status A and Excludereason 0.",
					finReference);
		}

		return false;
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeadersByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ?");
		sql.append(" order by ReceiptDate, ReceiptID");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public boolean checkInProcessReceipts(String reference, long receiptId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(*) from FinReceiptHeader_Temp");
		sql.append(" where Reference = :Reference");
		sql.append(" and ReceiptId <> :ReceiptId");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("ReceiptId", receiptId);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
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
	public boolean isReceiptDetailsExits(String reference, String receiptMode, String chequeNo, String favourNumber,
			String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder sql = new StringBuilder("Select count(*) from finreceiptheader" + type);
		sql.append(" t inner join finreceiptdetail" + type);
		sql.append(" t1 on t1.receiptid=t.receiptid where REFERENCE= :REFERENCE and RECEIPTMODE= :RECEIPTMODE");
		sql.append(" and T1.FAVOURNUMBER= :FAVOURNUMBER and T1.CHEQUEACNO = :CHEQUEACNO ");
		sql.append(" and (T.RECEIPTMODESTATUS in ('A') or T.RECEIPTMODESTATUS is null)");
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", receiptMode);
		source.addValue("FAVOURNUMBER", favourNumber);
		source.addValue("CHEQUEACNO", chequeNo);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		}

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ReceiptModeStatus", status);
		source.addValue("RealizationDate", realizationDate);

		StringBuilder updateSql = new StringBuilder("Update FinReceiptHeader");
		updateSql.append(" Set ReceiptModeStatus=:ReceiptModeStatus,REALIZATIONDATE = :RealizationDate ");
		updateSql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug(Literal.SQL + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinReceiptHeader> getInProcessReceipts(String Reference) {
		StringBuilder sql = new StringBuilder("");
		sql.append(" Select ReceiptID, AllocationType , ReceiptAmount ");
		sql.append(" From FinReceiptHeader_Temp");
		sql.append(" Where Reference =:Reference ");
		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", Reference);

		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);
		List<FinReceiptHeader> rchList = null;

		try {
			rchList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		return rchList;
	}

	@Override
	public String getReceiptModeStatus(long receiptID, String type) {
		logger.debug(Literal.ENTERING);
		String modeStatus = "";
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder sql = new StringBuilder(" Select ReceiptModeStatus from FinReceiptHeader ");
		sql.append(StringUtils.trim(type));
		sql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			modeStatus = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			modeStatus = "";
		}

		logger.debug(Literal.LEAVING);
		return modeStatus;
	}

	@Override
	public List<Long> getInProcessReceiptId(String finReference) {
		StringBuilder sql = new StringBuilder("");
		sql.append(" Select ReceiptID");
		sql.append(" From FinReceiptHeader");
		sql.append(" Where Reference =:Reference and ReceiptModeStatus ='D' and receiptPurpose='SchdlRepayment'");
		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		List<Long> receiptList = null;

		try {
			receiptList = this.jdbcTemplate.queryForList(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {

		}

		return receiptList;
	}

	@Override
	public void updateLoanInActive(long receiptId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptId);
		source.addValue("LoanActive", 0);

		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(" Set LoanActive=:LoanActive");
		sql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveMultiReceipt(FinReceiptHeader finReceiptHeader, FinReceiptDetail finReceiptDetail,
			Map<String, String> valueMap) {
		logger.debug(Literal.ENTERING);

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
		source.addValue("UploadStatus", valueMap.get("uploadStatus"));
		source.addValue("Reason", valueMap.get("reason"));
		source.addValue("CancelRemarks", finReceiptHeader.getCancelRemarks());

		StringBuilder sql = new StringBuilder("Insert into MultiReceiptApproval");
		sql.append(" (BatchId, ReceiptModeStatus, BounceDate, RealizationDate, Remarks, CancelReason, ReceiptID");
		sql.append(", DepositDate, ReceiptDate, FinReference, Stage, DepositNo, FundingAc, BounceId, UploadStatus");
		sql.append(", Reason, CancelRemarks)");
		sql.append(" values(:BatchId, :ReceiptModeStatus, :BounceDate, :RealizationDate, :Remarks, :CancelReason");
		sql.append(", :ReceiptID, :DepositDate, :ReceiptDate, :FinReference, :Stage, :DepositNo, :FundingAc");
		sql.append(", :BounceId, :UploadStatus, :Reason, :CancelRemarks)");

		logger.debug(Literal.SQL + sql.toString());

		//SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptHeader);
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO FinReceiptQueueLog (UploadId, ReceiptId, FinReference");
		sql.append(", TransactionDate, ThreadId, Progress, StartTime)");
		sql.append(" Values( :UploadId, :ReceiptId, :FinReference, :TransactionDate");
		sql.append(", :ThreadId, :Progress, :StartTime)");

		logger.trace(Literal.SQL + sql.toString());

		jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(finReceiptQueueList.toArray()));
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateMultiReceiptLog(FinReceiptQueueLog finReceiptQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinReceiptQueueLog");
		sql.append(" Set EndTime =:EndTime, ThreadId =:ThreadId, StartTime =:StartTime, ErrorLog =:ErrorLog");
		sql.append(", Progress =:Progress Where UploadId =:UploadId And ReceiptId =:ReceiptId");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptQueue);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinReceiptQueueLog");
		sql.append(" Set EndTime =:StartTime, ThreadId =:ThreadId, StartTime =:StartTime, ErrorLog =:ErrorLog");
		sql.append(", Progress =:Progress Where UploadId =:UploadId And ReceiptId =:ReceiptId");

		logger.trace(Literal.SQL + sql.toString());

		jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(finReceiptQueueList.toArray()));
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getInProcessMultiReceiptRecord() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", 0);

		StringBuilder sql = new StringBuilder(" Select ReceiptId From FinReceiptQueueLog");
		sql.append(" Where Progress =:Progress");

		logger.debug(Literal.SQL + sql.toString());

		List<Long> receiptList = this.jdbcTemplate.queryForList(sql.toString(), source, Long.class);
		logger.debug(Literal.LEAVING);
		return receiptList;
	}

	@Override
	public boolean checkEarlySettlementInitiation(String reference) {

		MapSqlParameterSource source = null;
		boolean isReceiptsInProcess = false;

		StringBuilder sql = new StringBuilder("Select count(*)  from FinReceiptHeader_view where ");
		sql.append(" Reference = :Reference AND");
		sql.append(" Receiptpurpose IN (:Receiptpurpose) AND");
		sql.append(" ReceiptModeStatus not in ( :Status)");

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("Receiptpurpose", Arrays.asList(FinanceConstants.FINSER_EVENT_EARLYSETTLE));
		source.addValue("Status", Arrays.asList("B", "C"));

		try {
			int count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);

			if (count > 0) {
				isReceiptsInProcess = true;
			}
			logger.debug("Already Early Settlement Initiated");
		} catch (DataAccessException e) {
			logger.error(e);
		}

		return isReceiptsInProcess;

	}

	@Override
	public boolean checkPartialSettlementInitiation(String reference) {

		MapSqlParameterSource source = null;
		boolean isReceiptsInProcess = false;

		StringBuilder sql = new StringBuilder("Select count(*)  from FinReceiptHeader_Temp where ");
		sql.append(" Reference = :Reference AND");
		sql.append(" Receiptpurpose IN (:Receiptpurpose) AND");
		sql.append(" ReceiptModeStatus not in ( :Status)");

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("Receiptpurpose", Arrays.asList(FinanceConstants.FINSER_EVENT_EARLYRPY));
		source.addValue("Status", Arrays.asList("B", "C"));

		try {
			int count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);

			if (count > 0) {
				isReceiptsInProcess = true;
			}
			logger.debug("Already Partial Settlement Initiated");
		} catch (DataAccessException e) {
			logger.error(e);
		}

		return isReceiptsInProcess;

	}

	@Override
	public boolean isChequeExists(String reference, String paytypeCheque, String bankCode, String favourNumber,
			String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder sql = new StringBuilder(" Select count(*) from finreceiptdetail");
		sql.append(type);
		sql.append(" where REFERENCE= :REFERENCE and PAYMENTTYPE= :RECEIPTMODE and FAVOURNUMBER= :FAVOURNUMBER");
		sql.append(" and BANKCODE = :BANKCODE STATUS NOT IN ('B','C')  ");
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", paytypeCheque);
		source.addValue("FAVOURNUMBER", favourNumber);
		source.addValue("BANKCODE", bankCode);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		}

		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOnlineExists(String reference, String subReceiptMode, String tranRef, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder sql = new StringBuilder("Select count(*) from finreceiptdetail");
		sql.append(type);
		sql.append(" where REFERENCE= :REFERENCE and PAYMENTTYPE= :RECEIPTMODE and TRANSACTIONREF= :TRANSACTIONREF");
		sql.append(" And STATUS NOT IN ('B','C')  ");
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", subReceiptMode);
		source.addValue("TRANSACTIONREF", tranRef);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		}

		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Get Loan Reference if it is present in Temp table
	 */
	@Override
	public String getLoanReferenc(String finReference, String receiptFileName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT DISTINCT REFERENCE FROM RECEIPTUPLOADDETAILS ");
		sql.append(" WHERE UPLOADHEADERID IN (SELECT UploadHeaderId FROM RECEIPTUPLOADHEADER_view ");
		sql.append(" where FileName not in ( :FileName) and uploadprogress in ("
				+ ReceiptUploadConstants.RECEIPT_DEFAULT + "," + ReceiptUploadConstants.RECEIPT_DOWNLOADED + ") )");
		sql.append(
				" AND REFERENCE = :Reference and uploadstatus in ('" + PennantConstants.UPLOAD_STATUS_SUCCESS + "')");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("FileName", receiptFileName);

		String reference = null;

		try {
			reference = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			reference = null;
		}

		logger.debug(Literal.LEAVING);
		return reference;

	}

	private class FinReceiptHeaderRowMaper implements RowMapper<FinReceiptHeader> {
		private String type;

		FinReceiptHeaderRowMaper(String type) {
			this.type = type;
		}

		@Override
		public FinReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinReceiptHeader rh = new FinReceiptHeader();

			rh.setReceiptID(rs.getLong("ReceiptID"));
			rh.setReceiptDate(rs.getTimestamp("ReceiptDate"));
			rh.setReceiptType(rs.getString("ReceiptType"));
			rh.setRecAgainst(rs.getString("RecAgainst"));
			rh.setReference(rs.getString("Reference"));
			rh.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rh.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
			//rh.setInstructionUID(rs.getLong("InstructionUID")); ( column is not available in bean)
			rh.setReceiptMode(rs.getString("ReceiptMode"));
			rh.setExcessAdjustTo(rs.getString("ExcessAdjustTo"));
			rh.setAllocationType(rs.getString("AllocationType"));
			rh.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			rh.setEffectSchdMethod(rs.getString("EffectSchdMethod"));
			rh.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
			rh.setRealizationDate(rs.getTimestamp("RealizationDate"));
			rh.setCancelReason(rs.getString("CancelReason"));
			rh.setWaviedAmt(rs.getBigDecimal("WaviedAmt"));
			rh.setTotFeeAmount(rs.getBigDecimal("TotFeeAmount"));
			rh.setBounceDate(rs.getTimestamp("BounceDate"));
			rh.setRemarks(rs.getString("Remarks"));
			rh.setGDRAvailable(rs.getBoolean("GDRAvailable"));
			rh.setReleaseType(rs.getString("ReleaseType"));
			rh.setThirdPartyName(rs.getString("ThirdPartyName"));
			rh.setThirdPartyMobileNum(rs.getString("ThirdPartyMobileNum"));
			rh.setLpiAmount(rs.getBigDecimal("LpiAmount"));
			rh.setCashierBranch(rs.getString("CashierBranch"));
			rh.setInitiateDate(rs.getTimestamp("InitiateDate"));
			rh.setDepositProcess(rs.getBoolean("DepositProcess"));
			rh.setDepositBranch(rs.getString("DepositBranch"));
			rh.setLppAmount(rs.getBigDecimal("LppAmount"));
			rh.setGstLpiAmount(rs.getBigDecimal("GstLpiAmount"));
			rh.setGstLppAmount(rs.getBigDecimal("GstLppAmount"));
			rh.setSubReceiptMode(rs.getString("subReceiptMode"));
			rh.setReceiptChannel(rs.getString("receiptChannel"));
			rh.setReceivedFrom(rs.getString("receivedFrom"));
			rh.setPanNumber(rs.getString("panNumber"));
			rh.setCollectionAgentId(rs.getLong("collectionAgentId"));
			rh.setExtReference(rs.getString("ExtReference"));
			rh.setModule(rs.getString("Module"));
			rh.setFinDivision(rs.getString("FinDivision"));
			rh.setPostBranch(rs.getString("PostBranch"));
			rh.setActFinReceipt(rs.getBoolean("ActFinReceipt"));
			rh.setReasonCode(rs.getLong("ReasonCode"));
			rh.setVersion(rs.getInt("Version"));
			rh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			rh.setLastMntBy(rs.getLong("LastMntBy"));
			rh.setRecordStatus(rs.getString("RecordStatus"));
			rh.setRoleCode(rs.getString("RoleCode"));
			rh.setNextRoleCode(rs.getString("NextRoleCode"));
			rh.setTaskId(rs.getString("TaskId"));
			rh.setNextTaskId(rs.getString("NextTaskId"));
			rh.setRecordType(rs.getString("RecordType"));
			rh.setWorkflowId(rs.getLong("WorkflowId"));
			rh.setCancelRemarks(rs.getString("CancelRemarks"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				rh.setFinType(rs.getString("FinType"));
				rh.setFinCcy(rs.getString("FinCcy"));
				rh.setFinBranch(rs.getString("FinBranch"));
				rh.setCustCIF(rs.getString("CustCIF"));
				rh.setCustShrtName(rs.getString("CustShrtName"));
				rh.setFinTypeDesc(rs.getString("FinTypeDesc"));
				rh.setFinCcyDesc(rs.getString("FinCcyDesc"));
				rh.setFinBranchDesc(rs.getString("FinBranchDesc"));
				rh.setCancelReasonDesc(rs.getString("CancelReasonDesc"));
				rh.setFinIsActive(rs.getBoolean("FinIsActive"));
				rh.setPromotionCode(rs.getString("PromotionCode"));
				rh.setProductCategory(rs.getString("ProductCategory"));
				rh.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
				rh.setCollectionAgentCode(rs.getString("CollectionAgentCode"));
				rh.setCollectionAgentDesc(rs.getString("CollectionAgentDesc"));
				rh.setPostBranchDesc(rs.getString("PostBranchDesc"));
				rh.setCashierBranchDesc(rs.getString("CashierBranchDesc"));
				rh.setFinDivisionDesc(rs.getString("FinDivisionDesc"));
				rh.setEntityCode(rs.getString("EntityCode"));

				if (StringUtils.trimToEmpty(type).contains("FView")) {
					rh.setScheduleMethod(rs.getString("ScheduleMethod"));
					rh.setPftDaysBasis(rs.getString("PftDaysBasis"));
					rh.setCustID(rs.getLong("CustID"));
					rh.setCustomerCIF(rs.getString("CustomerCIF"));
					rh.setCustomerName(rs.getString("CustomerName"));
					rh.setCustBaseCcy(rs.getString("CustBaseCcy"));
				}

				if (StringUtils.trimToEmpty(type).contains("FEView")
						|| StringUtils.trimToEmpty(type).contains("FCView")) {
					rh.setCustID(rs.getLong("CustID"));
					rh.setCustomerCIF(rs.getString("CustomerCIF"));
					rh.setCustomerName(rs.getString("CustomerName"));
				}
			}

			return rh;
		}

	}
}