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
 * FileName    		:  FinFeeDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;

/**
 * DAO methods implementation for the <b>FinFeeDetail model</b> class.<br>
 * 
 */

public class FinFeeDetailDAOImpl extends SequenceDao<FinFeeDetail> implements FinFeeDetailDAO {
	private static Logger logger = Logger.getLogger(FinFeeDetailDAOImpl.class);

	public FinFeeDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Goods Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinFeeDetail
	 */
	@Override
	public FinFeeDetail getFinFeeDetailById(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" where FeeID = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finFeeDetail.getFeeID() },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, reference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	public List<FinFeeDetail> getAMZFinFeeDetails(String finRef, String type) {
		logger.debug("Entering");

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(finRef);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT T1.FeeID, T1.FinReference, T1.OriginationFee, T1.FinEvent, T1.FeeTypeID, T1.FeeSeq");
		sql.append(", T1.FeeOrder, T1.CalculatedAmount, T1.ActualAmount, T1.WaivedAmount, T1.PaidAmount");
		sql.append(", T1.FeeScheduleMethod, T1.Terms, T1.RemainingFee, T1.PaymentRef, T1.CalculationType");
		sql.append(", T1.VasReference, T1.Status, T1.RuleCode, T1.FixedAmount, T1.Percentage");
		sql.append(", T1.CalculateOn, T1.AlwDeviation, T1.MaxWaiverPerc, T1.AlwModifyFee");
		sql.append(", T1.AlwModifyFeeSchdMthd, T1.TaxPercent, T1.Refundable, T1.InstructionUID, T1.ActPercentage");
		sql.append(", T1.WaivedGST, T1.ReferenceId, T1.PaidTDS, T1.RemTDS, T1.NetTDS");
		sql.append(" From FinFeeDetail T1 ");
		sql.append(" INNER JOIN FeeTypes T2 ON T1.FeeTypeID = T2.FeeTypeID AND T2.AmortzReq = 1");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where T1.FinReference = :FinReference");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final String reference, boolean isWIF, String type,
			String finEvent) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = ? and FinEvent = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, reference);
					ps.setString(index++, finEvent);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByReferenceId(long referenceId, String finEvent, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(false, type);
		sql.append(" Where ReferenceId = ? and FinEvent = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, false);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, referenceId);
					ps.setString(index++, finEvent);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<FinFeeDetail> getFinScheduleFees(final String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = ? and FeeScheduleMethod IN (?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, reference);
					ps.setString(index++, "STFI");
					ps.setString(index++, "STNI");
					ps.setString(index++, "STET");
					ps.setString(index++, "POSP");
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for Fetching Fee Details only Paid by Customer upfront
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<FinFeeDetail> getPaidFinFeeDetails(final String reference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FeeID, FeeOrder, FinEvent, CalculatedAmount, ActualAmountOriginal");
		sql.append(", ActualAmount, WaivedAmount, WaivedGST, NetAmountOriginal, NetAmountGST, NetAmount");
		sql.append(", PaidAmount, PaidAmountGST, PaidAmountOriginal, RemainingFee, RemainingFeeGST");
		sql.append(", RemainingFeeOriginal, VasReference, Status, ReferenceId");
		sql.append(", FeeScheduleMethod, ActPercentage, PaidTDS, RemTDS, NetTDS");
		sql.append(", TaxHeaderId, TaxApplicable, ActualAmount");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxComponent, TdsReq");
		}

		sql.append(", WaivedGST, ReferenceId");
		sql.append(" from FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and ActualAmount > ?");
		sql.append(" and FeeScheduleMethod = ? and OriginationFee = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setString(index++, reference);
					ps.setInt(index++, 0);
					ps.setString(index++, "DISB");
					ps.setInt(index, 1);
				}
			}, new RowMapper<FinFeeDetail>() {
				@Override
				public FinFeeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinFeeDetail ffd = new FinFeeDetail();

					ffd.setFinReference(rs.getString("FinReference"));
					ffd.setFeeID(rs.getLong("FeeID"));
					ffd.setFeeOrder(rs.getInt("FeeOrder"));
					ffd.setFinEvent(rs.getString("FinEvent"));
					ffd.setCalculatedAmount(rs.getBigDecimal("CalculatedAmount"));
					ffd.setActualAmountOriginal(rs.getBigDecimal("ActualAmountOriginal"));
					ffd.setActualAmount(rs.getBigDecimal("ActualAmount"));
					ffd.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					ffd.setWaivedGST(rs.getBigDecimal("WaivedGST"));
					ffd.setNetAmountOriginal(rs.getBigDecimal("NetAmountOriginal"));
					ffd.setNetAmountGST(rs.getBigDecimal("NetAmountGST"));
					ffd.setNetAmount(rs.getBigDecimal("NetAmount"));
					ffd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					ffd.setPaidAmountGST(rs.getBigDecimal("PaidAmountGST"));
					ffd.setPaidAmountOriginal(rs.getBigDecimal("PaidAmountOriginal"));
					ffd.setRemainingFee(rs.getBigDecimal("RemainingFee"));
					ffd.setRemainingFeeGST(rs.getBigDecimal("RemainingFeeGST"));
					ffd.setRemainingFeeOriginal(rs.getBigDecimal("RemainingFeeOriginal"));
					ffd.setVasReference(rs.getString("VasReference"));
					ffd.setStatus(rs.getString("Status"));
					ffd.setReferenceId(rs.getLong("ReferenceId"));
					ffd.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
					ffd.setTaxApplicable(rs.getBoolean("TaxApplicable"));
					ffd.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
					ffd.setActPercentage(rs.getBigDecimal("ActPercentage"));
					ffd.setPaidTDS(rs.getBigDecimal("PaidTDS"));
					ffd.setRemTDS(rs.getBigDecimal("RemTDS"));
					ffd.setNetTDS(rs.getBigDecimal("NetTDS"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						ffd.setFeeTypeCode(rs.getString("FeeTypeCode"));
						ffd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						ffd.setTaxComponent(rs.getString("TaxComponent"));
						ffd.setTdsReq(rs.getBoolean("TdsReq"));
					}

					ffd.setWaivedGST(rs.getBigDecimal("WaivedGST"));
					ffd.setReferenceId(JdbcUtil.getLong(rs.getLong("ReferenceId")));

					return ffd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param finFeeDetail
	 *            (FinFeeDetail)
	 * @return void
	 */
	@Override
	public void refresh(FinFeeDetail finFeeDetail) {

	}

	/**
	 * This method Deletes the Record from the FinFeeDetail or FinFeeDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods
	 *            Details (FinFeeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		if (isWIF) {
			sql.append("Delete From WIFFinFeeDetail");
		} else {
			sql.append("Delete From FinFeeDetail");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference And OriginationFee = :OriginationFee");
		sql.append(" And FinEvent = :FinEvent And FeeTypeID = :FeeTypeID And FeeID = :FeeID");

		if (StringUtils.isNotBlank(finFeeDetail.getVasReference())) {
			sql.append(" And VasReference = :VasReference");
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into FinFeeDetail or FinFeeDetail_Temp.
	 *
	 * save Goods Details
	 * 
	 * @param Goods
	 *            Details (FinFeeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		if (finFeeDetail.getFeeID() == Long.MIN_VALUE) {
			finFeeDetail.setFeeID(getNextValue("SeqFinFeeDetail"));
		}

		// Post date is added for the SOA purpose. It will always be replaced
		// with application date at the approval.
		finFeeDetail.setPostDate(SysParamUtil.getAppDate());

		StringBuilder sql = new StringBuilder();
		if (isWIF) {
			sql.append("Insert Into WIFFinFeeDetail");
		} else {
			sql.append("Insert Into FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(FeeID, FinReference, OriginationFee , FinEvent, FeeTypeID, FeeSeq, FeeOrder");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, FeeScheduleMethod");
		sql.append(", Terms, RemainingFee, PaymentRef, CalculationType, VasReference, Status");
		sql.append(", RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc");
		sql.append(", AlwModifyFee, AlwModifyFeeSchdMthd, PostDate, Refundable, PaidAmountOriginal");
		sql.append(", PaidAmountGST, NetAmountOriginal, NetAmountGST, NetAmount, RemainingFeeOriginal");
		sql.append(", RemainingFeeGST, TaxApplicable, TaxComponent, ActualAmountOriginal");
		sql.append(", ActualAmountGST, TransactionId, InstructionUID");
		sql.append(", NetTDS, PaidTDS, RemTDS");
		if (!isWIF) {
			sql.append(", ActPercentage, TaxPercent");
		}
		sql.append(", WaivedGST, ReferenceId, TaxHeaderId");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (:FeeID, :FinReference, :OriginationFee , :FinEvent, :FeeTypeID, :FeeSeq, :FeeOrder");
		sql.append(", :CalculatedAmount, :ActualAmount, :WaivedAmount, :PaidAmount, :FeeScheduleMethod");
		sql.append(", :Terms, :RemainingFee, :PaymentRef, :CalculationType, :VasReference, :Status");
		sql.append(", :RuleCode, :FixedAmount, :Percentage, :CalculateOn, :AlwDeviation, :MaxWaiverPerc");
		sql.append(", :AlwModifyFee, :AlwModifyFeeSchdMthd, :PostDate, :Refundable, :PaidAmountOriginal");
		sql.append(", :PaidAmountGST, :NetAmountOriginal, :NetAmountGST, :NetAmount, :RemainingFeeOriginal");
		sql.append(", :RemainingFeeGST, :TaxApplicable, :TaxComponent, :ActualAmountOriginal");
		sql.append(", :ActualAmountGST, :TransactionId, :InstructionUID");
		sql.append(", :NetTDS , :PaidTDS, :RemTDS");
		if (!isWIF) {
			sql.append(", :ActPercentage, :TaxPercent");
		}
		sql.append(", :WaivedGST, :ReferenceId, :TaxHeaderId");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);

		return finFeeDetail.getFeeID();
	}

	/**
	 * This method updates the Record FinFeeDetail or FinFeeDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods
	 *            Details (FinFeeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder sql = new StringBuilder();
		if (isWIF) {
			sql.append("Update WIFFinFeeDetail");
		} else {
			sql.append("Update FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set FinReference=:FinReference");
		sql.append(", FeeSeq = :FeeSeq");
		sql.append(", FeeOrder = :FeeOrder");
		sql.append(", CalculatedAmount = :CalculatedAmount");
		sql.append(", ActualAmount = :ActualAmount");
		sql.append(", WaivedAmount = :WaivedAmount");
		sql.append(", PaidAmount = :PaidAmount");
		sql.append(", FeeScheduleMethod = :FeeScheduleMethod");
		sql.append(", Terms = :Terms");
		sql.append(", RemainingFee = :RemainingFee");
		sql.append(", PaymentRef = :PaymentRef");
		sql.append(", CalculationType = :CalculationType");
		sql.append(", VasReference=:VasReference");
		sql.append(", RuleCode = :RuleCode");
		sql.append(", Status=:Status");
		sql.append(", FixedAmount = :FixedAmount");
		sql.append(", Percentage = :Percentage");
		sql.append(", CalculateOn = :CalculateOn");
		sql.append(", AlwDeviation = :AlwDeviation");
		sql.append(", MaxWaiverPerc = :MaxWaiverPerc");
		sql.append(", AlwModifyFee = :AlwModifyFee");
		sql.append(", AlwModifyFeeSchdMthd = :AlwModifyFeeSchdMthd");
		sql.append(", Refundable = :Refundable");
		sql.append(", TaxPercent = :TaxPercent");
		sql.append(", PaidAmountOriginal = :PaidAmountOriginal");
		sql.append(", PaidAmountGST = :PaidAmountGST");
		sql.append(", NetAmountOriginal = :NetAmountOriginal");
		sql.append(", NetAmountGST = :NetAmountGST");
		sql.append(", NetAmount = :NetAmount");
		sql.append(", RemainingFeeOriginal = :RemainingFeeOriginal");
		sql.append(", RemainingFeeGST = :RemainingFeeGST");
		sql.append(", TaxApplicable = :TaxApplicable");
		sql.append(", TaxComponent = :TaxComponent");
		sql.append(", ActualAmountOriginal = :ActualAmountOriginal");
		sql.append(", ActualAmountGST = :ActualAmountGST");
		sql.append(", NetTDS = :NetTDS");
		sql.append(", PaidTDS = :PaidTDS");
		sql.append(", RemTDS = :RemTDS");
		sql.append(", InstructionUID = :InstructionUID");
		if (isWIF) {
			sql.append(", ActPercentage = :ActPercentage");
		}
		sql.append(", WaivedGST = :WaivedGST");
		sql.append(", ReferenceId = :ReferenceId");
		sql.append(", TaxHeaderId = :TaxHeaderId");
		sql.append(", Version = :Version");
		sql.append(", LastMntBy = :LastMntBy");
		sql.append(", LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus");
		sql.append(", RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId");
		sql.append(", NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType");
		sql.append(", WorkflowId = :WorkflowId");
		sql.append("  Where FeeID = :FeeID ");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record FinFeeDetail or FinFeeDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods
	 *            Details (FinFeeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void statusUpdate(long feeID, String status, boolean isWIF, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFeeID(feeID);
		finFeeDetail.setStatus(status);

		StringBuilder updateSql = new StringBuilder();
		if (isWIF) {
			updateSql.append("Update WIFFinFeeDetail");
		} else {
			updateSql.append("Update FinFeeDetail");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set Status = :Status Where FeeID = :FeeID ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	@Override
	public void deleteByFinRef(String loanReference, boolean isWIF, String tableType) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(loanReference);

		StringBuilder sql = new StringBuilder();
		if (isWIF) {
			sql.append("Delete From WIFFinFeeDetail");
		} else {
			sql.append("Delete From FinFeeDetail");
		}
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinReference = :FinReference ");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteServiceFeesByFinRef(String loanReference, boolean isWIF, String tableType) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(loanReference);

		StringBuilder sql = new StringBuilder();
		if (isWIF) {
			sql.append("Delete From WIFFinFeeDetail");
		} else {
			sql.append("Delete From FinFeeDetail");
		}
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinReference = :FinReference AND OriginationFee = 0");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getFeeSeq(FinFeeDetail finFeeDetail, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		int finSeq = 0;

		StringBuilder sql = new StringBuilder();
		sql.append("Select Max(FeeSeq) From ");

		if (isWIF) {
			sql.append("WIFFinFeeDetail");
		} else {
			sql.append("FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference AND FinEvent = :FinEvent");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameter = new MapSqlParameterSource();
		parameter.addValue("FinReference", finFeeDetail.getFinReference());
		parameter.addValue("FinEvent", finFeeDetail.getFinEvent());

		try {
			finSeq = this.jdbcTemplate.queryForObject(sql.toString(), parameter, Integer.class);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		} finally {

		}

		return finSeq;
	}

	/**
	 * Fetch the Record Goods Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinFeeDetail
	 */
	@Override
	public FinFeeDetail getVasFeeDetailById(String vasReference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" where VasReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { vasReference }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinFeeDetail");
		sql.append("  Set TaxPercent = :TaxPercent");
		sql.append(" Where FinReference = :FinReference And FeeTypeId = :FeeTypeId ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxPercent);
		int recordCount = this.jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode, String finReference, String type) {
		logger.debug("Entering");

		long feeTypeId = Long.MIN_VALUE;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT feeTypeID From FinFeeDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql
				.append(" WHERE FeeTypeCode = :FeeTypeCode and FinReference = :FinReference and FinEvent != :FinEvent");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeCode", feeTypeCode);
		source.addValue("FinReference", finReference);
		source.addValue("FinEvent", AccountEventConstants.ACCEVENT_VAS_FEE);

		try {
			feeTypeId = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			feeTypeId = Long.MIN_VALUE;
		}

		logger.debug("Leaving");

		return feeTypeId;
	}

	@Override
	public FinFeeDetail getFeeDetailByExtReference(String extReference, long feeTypeId, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(false, tableType);
		sql.append(" where TransactionId = ? and FeeTypeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(tableType, false);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { extReference, feeTypeId },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailsByTran(String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where TransactionId = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, reference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<FinFeeDetail> getDMFinFeeDetailByFinRef(String id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(false, type);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, false);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, id);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public boolean isFinTypeFeeExists(long feeTypeId, String finType, int moduleId, boolean originationFee) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select FeeTypeId from fintypefees where FeeTypeId  = :FeeTypeId");
		sql.append(" and FinType = :FinType and ModuleId = :ModuleId and OriginationFee = :OriginationFee");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("FeeTypeId", feeTypeId);
		beanParameters.addValue("FinType", finType);
		beanParameters.addValue("ModuleId", moduleId);
		beanParameters.addValue("OriginationFee", originationFee);

		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return count > 0;
	}

	private StringBuilder getSelectQuery(boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, FeeScheduleMethod, Terms");
		sql.append(", RemainingFee, PaymentRef, CalculationType, VasReference, Status");
		sql.append(", RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc");
		sql.append(", AlwModifyFee, AlwModifyFeeSchdMthd,Refundable");
		sql.append(", PaidAmountOriginal, PaidAmountGST, NetAmountOriginal, NetAmountGST");
		sql.append(", NetAmount, RemainingFeeOriginal, RemainingFeeGST");
		sql.append(", TaxApplicable, TaxComponent, ActualAmountOriginal, ActualAmountGST, InstructionUID");
		sql.append(", WaivedGST, ReferenceId, TaxHeaderId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", NetTDS, PaidTDS, RemTDS");

		if (!isWIF) {
			sql.append(", ActPercentage");
			if (StringUtils.trimToEmpty(type).contains("View")) {
				sql.append(", VasProductCode");
			}
		}

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc");
		}

		if (isWIF) {
			sql.append(" From WIFFinFeeDetail");
		} else {
			sql.append(" From FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	@Override
	public List<FinFeeDetail> getPreviousAdvPayments(String finReferee) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select ft.FeeTypeId, FeeTypeCode, sum(CalculatedAmount) CalculatedAmount");
		sql.append(" from FinFeeDetail fd");
		sql.append(" inner join FeeTypes ft on ft.FeeTypeID = fd.FeeTypeID");
		sql.append(" where FinReference = :FinReference and FinEvent in (:FinEvent) and FeeTypeCode in (:FeeTypeCode)");
		sql.append(" group by ft.FeeTypeId, FeeTypeCode");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReferee);
		source.addValue("FinEvent",
				Arrays.asList(AccountEventConstants.ACCEVENT_ADDDBSP, AccountEventConstants.ACCEVENT_ADDDBSN));
		source.addValue("FeeTypeCode", Arrays.asList(AdvanceRuleCode.ADVINT.name(), AdvanceRuleCode.ADVEMI.name()));

		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<FinFeeDetail> getFeeDetails(String finReference, String feetypeCode, List<String> finEvents) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from FinFeeDetail");
		sql.append(" where FinReference = :FinReference and FinEvent in (:FinEvent) ");
		sql.append(" and FeeTypeID = (select FeeTypeID from feetypes where FeetypeCode=:FeeTypeCode)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinEvent", finEvents);
		source.addValue("FeeTypeCode", feetypeCode);

		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	private class FinFeeDetailsRowMapper implements RowMapper<FinFeeDetail> {
		private String type;
		private boolean wIf;

		private FinFeeDetailsRowMapper(String type, boolean wIf) {
			this.type = type;
			this.wIf = wIf;
		}

		@Override
		public FinFeeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinFeeDetail fd = new FinFeeDetail();

			fd.setFeeID(rs.getLong("FeeID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setOriginationFee(rs.getBoolean("OriginationFee"));
			fd.setFinEvent(rs.getString("FinEvent"));
			fd.setFeeTypeID(rs.getLong("FeeTypeID"));
			fd.setFeeSeq(rs.getInt("FeeSeq"));
			fd.setFeeOrder(rs.getInt("FeeOrder"));
			fd.setCalculatedAmount(rs.getBigDecimal("CalculatedAmount"));
			fd.setActualAmount(rs.getBigDecimal("ActualAmount"));
			fd.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fd.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
			fd.setTerms(rs.getInt("Terms"));
			fd.setRemainingFee(rs.getBigDecimal("RemainingFee"));
			fd.setPaymentRef(rs.getString("PaymentRef"));
			fd.setCalculationType(rs.getString("CalculationType"));
			fd.setVasReference(rs.getString("VasReference"));
			fd.setStatus(rs.getString("Status"));
			fd.setRuleCode(rs.getString("RuleCode"));
			fd.setFixedAmount(rs.getBigDecimal("FixedAmount"));
			fd.setPercentage(rs.getBigDecimal("Percentage"));
			fd.setCalculateOn(rs.getString("CalculateOn"));
			fd.setAlwDeviation(rs.getBoolean("AlwDeviation"));
			fd.setMaxWaiverPerc(rs.getBigDecimal("MaxWaiverPerc"));
			fd.setAlwModifyFee(rs.getBoolean("AlwModifyFee"));
			fd.setAlwModifyFeeSchdMthd(rs.getBoolean("AlwModifyFeeSchdMthd"));
			fd.setRefundable(rs.getBoolean("Refundable"));
			fd.setPaidAmountOriginal(rs.getBigDecimal("PaidAmountOriginal"));
			fd.setPaidAmountGST(rs.getBigDecimal("PaidAmountGST"));
			fd.setNetAmountOriginal(rs.getBigDecimal("NetAmountOriginal"));
			fd.setNetAmountGST(rs.getBigDecimal("NetAmountGST"));
			fd.setNetAmount(rs.getBigDecimal("NetAmount"));
			fd.setRemainingFeeOriginal(rs.getBigDecimal("RemainingFeeOriginal"));
			fd.setRemainingFeeGST(rs.getBigDecimal("RemainingFeeGST"));
			fd.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			fd.setTaxComponent(rs.getString("TaxComponent"));
			fd.setActualAmountOriginal(rs.getBigDecimal("ActualAmountOriginal"));
			fd.setActualAmountGST(rs.getBigDecimal("ActualAmountGST"));
			fd.setInstructionUID(rs.getLong("InstructionUID"));
			fd.setWaivedGST(rs.getBigDecimal("WaivedGST"));
			fd.setReferenceId(rs.getLong("ReferenceId"));
			fd.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			fd.setVersion(rs.getInt("Version"));
			fd.setLastMntBy(rs.getLong("LastMntBy"));
			fd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fd.setRecordStatus(rs.getString("RecordStatus"));
			fd.setRoleCode(rs.getString("RoleCode"));
			fd.setNextRoleCode(rs.getString("NextRoleCode"));
			fd.setTaskId(rs.getString("TaskId"));
			fd.setNextTaskId(rs.getString("NextTaskId"));
			fd.setRecordType(rs.getString("RecordType"));
			fd.setWorkflowId(rs.getLong("WorkflowId"));

			if (!wIf) {
				fd.setNetTDS(rs.getBigDecimal("NetTDS"));
				fd.setPaidTDS(rs.getBigDecimal("PaidTDS"));
				fd.setRemTDS(rs.getBigDecimal("RemTDS"));
				fd.setActPercentage(rs.getBigDecimal("ActPercentage"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					fd.setVasProductCode(rs.getString("VasProductCode"));
				}
			}

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fd.setFeeTypeCode(rs.getString("FeeTypeCode"));
				fd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			}

			return fd;
		}

	}

	@Override
	public void updateFeesFromUpfront(FinFeeDetail finFeeDetail, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set PaidAmount = :PaidAmount, ");
		sql.append(" PaidAmountOriginal = :PaidAmountOriginal, ");
		sql.append(" PaidAmountGST = :PaidAmountGST,");
		sql.append(" RemainingFee = :RemainingFee,");
		sql.append(" RemainingFeeOriginal = :RemainingFeeOriginal, ");
		sql.append(" RemainingFeeGST = :RemainingFeeGST ");
		sql.append(", PaidTDS = :PaidTDS, RemTDS = :RemTDS");
		sql.append(" Where FeeID = :FeeID ");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByTransactionId(String transactionId, boolean isWIF, String tableType) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setTransactionId(transactionId);

		StringBuilder sql = new StringBuilder();
		if (isWIF) {
			sql.append("Delete From WIFFinFeeDetail");
		} else {
			sql.append("Delete From FinFeeDetail");
		}
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where TransactionId = :TransactionId ");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

}