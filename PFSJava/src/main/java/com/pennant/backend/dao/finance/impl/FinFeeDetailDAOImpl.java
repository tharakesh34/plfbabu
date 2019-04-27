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

import java.util.ArrayList;
import java.util.Arrays;
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

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
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
		sql.append(" WHERE FeeID = :FeeID ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);

		return null;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
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
		sql.append(", T1.AlwModifyFeeSchdMthd, T1.TaxPercent, T1.Refundable, T1.InstructionUID");
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
		logger.debug("Entering");

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);
		finFeeDetail.setFinEvent(finEvent);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = :FinReference And FinEvent = :FinEvent");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		logger.debug("Leaving");

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinScheduleFees(final String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = :FinReference And FeeScheduleMethod IN ('STFI', 'STNI', 'STET', 'POSP')");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<FinFeeDetail>();
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

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(reference);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select FeeOrder, CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, RemainingFee");
		sql.append(", VasReference, Status, Refundable");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxComponent");
		}
		sql.append(" From FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference And PaidAmount > 0 ");
		logger.debug("selectSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
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

		//Post date is added for the SOA purpose. It will always be replaced with application date at the approval. 
		finFeeDetail.setPostDate(DateUtility.getAppDate());

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
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (:FeeID, :FinReference, :OriginationFee , :FinEvent, :FeeTypeID, :FeeSeq, :FeeOrder");
		sql.append(", :CalculatedAmount, :ActualAmount, :WaivedAmount, :PaidAmount, :FeeScheduleMethod");
		sql.append(", :Terms, :RemainingFee, :PaymentRef, :CalculationType, :VasReference, :Status");
		sql.append(", :RuleCode, :FixedAmount, :Percentage, :CalculateOn, :AlwDeviation, :MaxWaiverPerc");
		sql.append(", :AlwModifyFee, :AlwModifyFeeSchdMthd, :PostDate, :Refundable, :PaidAmountOriginal");
		sql.append(", :RemainingFeeGST, :PaidAmountGST, :NetAmountOriginal, :NetAmountGST, :NetAmount");
		sql.append(", :RemainingFeeOriginal, :TaxApplicable, :TaxComponent, :ActualAmountOriginal");
		sql.append(", :ActualAmountGST, :TransactionId, :InstructionUID");
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
		sql.append("  MaxWaiverPerc = :MaxWaiverPerc");
		sql.append(", AlwModifyFee = :AlwModifyFee");
		sql.append(", AlwModifyFeeSchdMthd = :AlwModifyFeeSchdMthd");
		sql.append(", Refundable = :Refundable");
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
		//sql.append(", InstructionUID = :InstructionUID");
		sql.append(", Version = :Version");
		sql.append(", LastMntBy = :LastMntBy");
		sql.append(", LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus");
		sql.append(", RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId");
		sql.append(", NextTaskId = :NextTaskId");
		sql.append("  RecordType = :RecordType");
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
		updateSql.append("  Set Status=:Status Where FeeID = :FeeID ");

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
		sql.append(" WHERE VasReference = :VasReference ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameter = new MapSqlParameterSource();
		parameter.addValue("VasReference", vasReference);

		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForObject(sql.toString(), parameter, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinFeeDetail");
		sql.append("  Set TaxPercent = :TaxPercent");
		sql.append(" Where FinReference =:FinReference And FeeTypeId = :FeeTypeId ");

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
		selectSql.append(" WHERE FeeTypeCode = :FeeTypeCode and FinReference =:FinReference and FinEvent != :FinEvent");

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
		sql.append("where TransactionId=:ExtReference and FeeTypeID=:FeeTypeID ");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeID", feeTypeId);
		source.addValue("ExtReference", extReference);

		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailsByTran(String reference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where TransactionId = :reference ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);

		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<FinFeeDetail> getDMFinFeeDetailByFinRef(String id, String type) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFinReference(id);

		StringBuilder sql = getSelectQuery(false, type);
		sql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

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
		StringBuilder sql = new StringBuilder();
		sql.append("Select FeeID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, FeeScheduleMethod, Terms");
		sql.append(", RemainingFee, PaymentRef, CalculationType, VasReference, Status");
		sql.append(", RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc");
		sql.append(", AlwModifyFee, AlwModifyFeeSchdMthd,Refundable");
		sql.append(", PaidAmountOriginal, PaidAmountGST, NetAmountOriginal, NetAmountGST");
		sql.append(", NetAmount, RemainingFeeOriginal, RemainingFeeGST");
		sql.append(", TaxApplicable, TaxComponent, ActualAmountOriginal, ActualAmountGST, InstructionUID");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
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
}