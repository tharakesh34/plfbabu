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
package com.pennant.backend.dao.Repayments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinanceRepaymentsDAOImpl extends SequenceDao<FinanceRepayments> implements FinanceRepaymentsDAO {
	private static Logger logger = Logger.getLogger(FinanceRepaymentsDAOImpl.class);

	public FinanceRepaymentsDAOImpl() {
		super();
	}

	/**
	 * Generate Finance Pay Sequence
	 */
	public long getFinancePaySeq(FinanceRepayments financeRepayments) {
		logger.debug("Entering");
		long repaySeq = 0;

		StringBuilder selectSql = new StringBuilder(" Select COALESCE(MAX(FinPaySeq),0) FROM FinRepayDetails");
		selectSql.append(" where FinReference=:FinReference AND  FinSchdDate=:FinSchdDate AND FinRpyFor=:FinRpyFor");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		try {
			repaySeq = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			repaySeq = 0;
		}

		repaySeq = repaySeq + 1;
		logger.debug("Leaving");
		return repaySeq;
	}

	/**
	 * This method insert new Records into FinanceRepayments .
	 * 
	 * save Finance Repayments
	 * 
	 * @param FinanceRepayments
	 *            Details (financeRepayments)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinanceRepayments financeRepayments, String type) {
		logger.debug(Literal.ENTERING);
		if (financeRepayments.getId() == Long.MIN_VALUE || financeRepayments.getId() == 0) {
			financeRepayments.setFinPaySeq(getFinancePaySeq(financeRepayments));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinRepayDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinReference, FinSchdDate, FinRpyFor, FinPaySeq,LinkedTranId,");
		sql.append(" FinRpyAmount, FinPostDate , FinValueDate, FinBranch,");
		sql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid,");
		sql.append(" SchdFeePaid , SchdInsPaid , SchdSuplRentPaid , SchdIncrCostPaid,");
		sql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund, PenaltyPaid, PenaltyWaived, ReceiptId) Values(");
		sql.append(" :FinReference, :FinSchdDate, :FinRpyFor, :FinPaySeq,:LinkedTranId,");
		sql.append(" :FinRpyAmount, :FinPostDate, :FinValueDate, :FinBranch,");
		sql.append(" :FinType, :FinCustID, :FinSchdPriPaid, :FinSchdPftPaid,:FinSchdTdsPaid,");
		sql.append(" :SchdFeePaid , :SchdInsPaid ,  :SchdSuplRentPaid , :SchdIncrCostPaid,");
		sql.append(" :FinTotSchdPaid,:FinFee, :FinWaiver, :FinRefund, :PenaltyPaid, :PenaltyWaived, :ReceiptId)");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return financeRepayments.getId();
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finReference, boolean isRpyCancelProc, String type) {
		logger.debug("Entering");

		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();

		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select T1.FinReference, T1.FinPostDate, T1.FinRpyFor, T1.FinPaySeq,");
		selectSql.append(" T1.FinRpyAmount, T1.FinSchdDate, T1.FinValueDate, T1.FinBranch,");
		selectSql.append(" T1.FinType, T1.FinCustID, T1.FinSchdPriPaid, T1.FinSchdPftPaid, T1.FinSchdTdsPaid,");
		selectSql.append(
				" T1.FinTotSchdPaid, T1.FinFee, T1.FinWaiver, T1.FinRefund, T1.SchdFeePaid , T1.SchdInsPaid , ");
		selectSql.append(" T1.SchdSuplRentPaid , T1.SchdIncrCostPaid");
		if (isRpyCancelProc) {
			selectSql.append(" ,T1.LinkedTranId");
		}
		selectSql.append(" From FinRepayDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" T1 where T1.FinReference=:FinReference ");
		if (isRpyCancelProc) {
			selectSql.append(" AND T1.LinkedTranId = (Select MAX(T2.LinkedTranId) from FinRepayDetails T2  ");
			selectSql.append(" Where T1.FinReference = T2.FinReference) ");
			selectSql.append(" AND T1.LinkedTranId != 0  ORDER BY T1.FinSchdDate DESC ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceRepayments.class);

		logger.debug("Leaving");
		repaymentList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

		if (repaymentList == null || repaymentList.isEmpty()) {
			selectSql = new StringBuilder(" Select T1.FinReference, T1.FinPostDate, T1.FinRpyFor, T1.FinPaySeq,");
			selectSql.append(" T1.FinRpyAmount, T1.FinSchdDate, T1.FinValueDate, T1.FinBranch,");
			selectSql.append(" T1.FinType, T1.FinCustID, T1.FinSchdPriPaid, T1.FinSchdPftPaid, T1.FinSchdTdsPaid, ");
			selectSql.append(" T1.FinTotSchdPaid, T1.FinFee, T1.FinWaiver, T1.FinRefund, ");
			selectSql.append(" T1.SchdFeePaid , T1.SchdInsPaid , T1.SchdSuplRentPaid , T1.SchdIncrCostPaid");
			if (isRpyCancelProc) {
				selectSql.append(" ,T1.LinkedTranId");
			}
			selectSql.append(" From FinRepayDetails");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" T1 where T1.FinReference=:FinReference ");
			if (isRpyCancelProc) {
				selectSql.append(" AND T1.FinPostDate = (Select MAX(T2.FinPostDate) from FinRepayDetails T2  ");
				selectSql.append(" Where T1.FinReference = T2.FinReference) ");
				selectSql.append(" AND T1.LinkedTranId = 0  ORDER BY T1.FinSchdDate DESC ");
			}

			logger.debug("selectSql: " + selectSql.toString());
			logger.debug("Leaving");
			repaymentList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}

		return repaymentList;
	}

	@Override
	public List<FinanceRepayments> getByFinRefAndSchdDate(String finReference, Date finSchdDate) {
		logger.debug("Entering");

		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();

		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setFinSchdDate(finSchdDate);

		StringBuilder selectSql = new StringBuilder(" Select T1.FinReference, T1.FinPostDate,T1.finSchdDate,");
		selectSql
				.append(" T1.FinValueDate,T1.FinSchdPriPaid, T1.FinSchdPftPaid, T1.FinSchdTdsPaid, T1.FinTotSchdPaid ");
		selectSql.append(" From FinRepayDetails");
		selectSql.append(" T1 where T1.FinReference=:FinReference and T1.FinSchdDate=:FinSchdDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceRepayments.class);

		logger.debug("Leaving");
		repaymentList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

		return repaymentList;
	}

	@Override
	public void deleteRpyDetailbyLinkedTranId(long linkedTranId, String finReference) {
		logger.debug("Entering");

		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setLinkedTranId(linkedTranId);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayDetails");
		deleteSql.append(" where LinkedTranId=:LinkedTranId AND FinReference =:FinReference ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteRpyDetailbyMaxPostDate(Date finPostDate, String finReference) {
		logger.debug("Entering");

		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setFinPostDate(finPostDate);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayDetails");
		deleteSql.append(" where FinPostDate=:FinPostDate AND FinReference=:FinReference ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public FinRepayHeader getFinRepayHeader(String finReference, String type) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"Select RepayID, ReceiptSeqID, FinReference , ValueDate , FinEvent , ");
		selectSql.append(" RepayAmount , PriAmount , PftAmount , LatePftAmount, TotalPenalty, TotalRefund , ");
		selectSql.append(
				" TotalWaiver , InsRefund ,RepayAccountId , EarlyPayEffMtd , EarlyPayDate, SchdRegenerated, LinkedTranId, ");
		selectSql.append(
				" TotalIns , TotalSuplRent , TotalIncrCost, TotalSchdFee, PayApportionment, RealizeUnAmz, CpzChg , AdviseAmount,FeeAmount,ExcessAmount ");
		selectSql.append(" From FinRepayHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);

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
	public FinRepayHeader getFinRepayHeader(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);
		header.setLinkedTranId(linkedTranId);

		StringBuilder selectSql = new StringBuilder("Select ValueDate, FinEvent From FinRepayHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where FinReference =:FinReference AND LinkedTranId = :LinkedTranId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);

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
	public Long saveFinRepayHeader(FinRepayHeader finRepayHeader, TableType tableType) {
		logger.debug("Entering");

		if (finRepayHeader.getRepayID() == 0 || finRepayHeader.getRepayID() == Long.MIN_VALUE) {
			finRepayHeader.setRepayID(getNextValue("SeqFinRepayHeader"));
			logger.debug("get NextID:" + finRepayHeader.getRepayID());
		}

		StringBuilder insertSql = new StringBuilder(" Insert Into FinRepayHeader");
		insertSql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		insertSql.append(
				" (RepayID, ReceiptSeqID, FinReference , ValueDate , FinEvent , RepayAmount , PriAmount , PftAmount , TotalRefund , ");
		insertSql.append(
				" TotalWaiver , InsRefund ,RepayAccountId , EarlyPayEffMtd  ,EarlyPayDate, SchdRegenerated, LinkedTranId, ");
		insertSql.append(
				" TotalIns , TotalSuplRent , TotalIncrCost, TotalSchdFee, PayApportionment,LatePftAmount, TotalPenalty, RealizeUnAmz, CpzChg, AdviseAmount,FeeAmount,ExcessAmount ) ");
		insertSql.append(
				" Values(:RepayID, :ReceiptSeqID, :FinReference , :ValueDate , :FinEvent , :RepayAmount , :PriAmount , :PftAmount , :TotalRefund , ");
		insertSql.append(
				" :TotalWaiver , :InsRefund , :RepayAccountId , :EarlyPayEffMtd , :EarlyPayDate, :SchdRegenerated, :LinkedTranId,");
		insertSql.append(
				" :TotalIns , :TotalSuplRent , :TotalIncrCost, :TotalSchdFee , :PayApportionment, :LatePftAmount, :TotalPenalty, :RealizeUnAmz, :CpzChg, :AdviseAmount , :FeeAmount,:ExcessAmount )");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return finRepayHeader.getRepayID();
	}

	@Override
	public void updateFinRepayHeader(FinRepayHeader finRepayHeader, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinRepayHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ValueDate=:ValueDate , FinEvent=:FinEvent , RepayAmount=:RepayAmount , ");
		updateSql.append(" PriAmount=:PriAmount , PftAmount=:PftAmount , TotalRefund=:TotalRefund , ");
		updateSql.append(
				" TotalWaiver=:TotalWaiver , InsRefund=:InsRefund ,RepayAccountId=:RepayAccountId , EarlyPayEffMtd=:EarlyPayEffMtd , ");
		updateSql.append(" EarlyPayDate=:EarlyPayDate, SchdRegenerated=:SchdRegenerated , LinkedTranId=:LinkedTranId,");
		updateSql.append(
				" TotalIns=:TotalIns , RealizeUnAmz=:RealizeUnAmz, CpzChg=:CpzChg,");
		updateSql.append(
				" TotalSuplRent=:TotalSuplRent , TotalIncrCost=:TotalIncrCost, TotalSchdFee=:TotalSchdFee , PayApportionment=:PayApportionment, AdviseAmount= :AdviseAmount,FeeAmount= :FeeAmount, ExcessAmount= :ExcessAmount");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	@Override
	public void deleteFinRepayHeader(FinRepayHeader finRepayHeader, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteFinRepayHeaderByTranId(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);
		header.setLinkedTranId(linkedTranId);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference AND LinkedTranId=:LinkedTranId ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<RepayScheduleDetail> getRpySchdList(String finReference, String type) {
		logger.debug("Entering");

		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select RepayID, RepaySchID, FinReference , SchDate , SchdFor , ProfitSchdBal , PrincipalSchdBal , ");
		selectSql.append(" ProfitSchd , ProfitSchdPaid , PrincipalSchd , PrincipalSchdPaid , ");
		selectSql.append(
				" ProfitSchdPayNow , TdsSchdPayNow, PrincipalSchdPayNow , PenaltyAmt , DaysLate , MaxWaiver , AllowRefund , AllowWaiver , ");
		selectSql.append(
				" RefundReq , WaivedAmt , RepayBalance, PenaltyPayNow, SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow, ");
		selectSql.append(
				" SchdIns, SchdInsPaid, SchdInsBal, SchdInsPayNow,LatePftSchd, LatePftSchdPaid, LatePftSchdBal, LatePftSchdPayNow, ");
		selectSql.append(" SchdSuplRent, SchdSuplRentPaid, SchdSuplRentBal,SchdSuplRentPayNow, SchdIncrCost, ");
		selectSql.append(
				" SchdIncrCostPaid, SchdIncrCostBal, SchdIncrCostPayNow, PftSchdWaivedNow , LatePftSchdWaivedNow, ");
		selectSql.append(
				" PriSchdWaivedNow, SchdFeeWaivedNow, SchdInsWaivedNow, SchdSuplRentWaivedNow, SchdIncrCostWaivedNow,");
		selectSql.append(" PaidPenaltyCGST, PaidPenaltySGST, PaidPenaltyUGST, PaidPenaltyIGST  ");
		selectSql.append(" From FinRepayScheduleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference=:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayScheduleDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Repayment Schedules
	 */
	@Override
	public List<RepayScheduleDetail> getRpySchedulesForDate(String finReference, Date schDate) {
		logger.debug("Entering");

		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);
		scheduleDetail.setSchDate(schDate);

		StringBuilder selectSql = new StringBuilder(
				" Select S.SchDate , H.ValueDate, S.ProfitSchdPayNow , S.PrincipalSchdPayNow , ");
		selectSql.append(
				" S.WaivedAmt , S.PenaltyPayNow, S.LatePftSchdPayNow,  S.PftSchdWaivedNow , S.LatePftSchdWaivedNow,  S.PriSchdWaivedNow ");
		selectSql.append(" From FinRepayScheduleDetail S INNER JOIN FinRepayHeader H ON S.RepayID = H.RepayID ");
		selectSql.append(" INNER JOIN FinReceiptDetail RD ON RD.ReceiptSeqID = H.ReceiptSeqID ");
		selectSql.append(
				" WHERE S.FinReference=:FinReference AND S.SchDate = :SchDate AND RD.Status NOT IN ('C', 'B') ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayScheduleDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void saveRpySchdList(List<RepayScheduleDetail> repaySchdList, TableType tableType) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinRepayScheduleDetail");
		insertSql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		insertSql.append(
				" (RepayID, RepaySchID, FinReference , SchDate , SchdFor , LinkedTranId, ProfitSchdBal , PrincipalSchdBal , ");
		insertSql.append(
				" ProfitSchdPayNow , TdsSchdPayNow, PrincipalSchdPayNow , PenaltyAmt , DaysLate , MaxWaiver , AllowRefund , AllowWaiver , ");
		insertSql.append(" ProfitSchd , ProfitSchdPaid , PrincipalSchd , PrincipalSchdPaid , ");
		insertSql.append(
				" RefundReq , WaivedAmt , RepayBalance, PenaltyPayNow, SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow,");
		insertSql.append(
				" SchdIns, SchdInsPaid, SchdInsBal, SchdInsPayNow,  LatePftSchd, LatePftSchdPaid, LatePftSchdBal, LatePftSchdPayNow,  ");
		insertSql.append(" SchdSuplRent, SchdSuplRentPaid, SchdSuplRentBal,SchdSuplRentPayNow, SchdIncrCost, ");
		insertSql.append(
				" SchdIncrCostPaid, SchdIncrCostBal, SchdIncrCostPayNow, PftSchdWaivedNow , LatePftSchdWaivedNow, ");
		insertSql.append(
				" PriSchdWaivedNow, SchdFeeWaivedNow, SchdInsWaivedNow, SchdSuplRentWaivedNow, SchdIncrCostWaivedNow,");
		insertSql.append(" PaidPenaltyCGST, PaidPenaltySGST, PaidPenaltyUGST, PaidPenaltyIGST) ");
		insertSql.append(
				" Values(:RepayID,:RepaySchID, :FinReference , :SchDate , :SchdFor , :LinkedTranId , :ProfitSchdBal , :PrincipalSchdBal , ");
		insertSql.append(
				" :ProfitSchdPayNow , :TdsSchdPayNow, :PrincipalSchdPayNow , :PenaltyAmt , :DaysLate , :MaxWaiver , :AllowRefund , :AllowWaiver , ");
		insertSql.append(" :ProfitSchd , :ProfitSchdPaid , :PrincipalSchd , :PrincipalSchdPaid  , ");
		insertSql.append(
				" :RefundReq , :WaivedAmt , :RepayBalance, :PenaltyPayNow , :SchdFee, :SchdFeePaid, :SchdFeeBal, :SchdFeePayNow,");
		insertSql.append(
				" :SchdIns, :SchdInsPaid, :SchdInsBal, :SchdInsPayNow, :LatePftSchd, :LatePftSchdPaid, :LatePftSchdBal, :LatePftSchdPayNow, ");
		insertSql.append(" :SchdSuplRent, :SchdSuplRentPaid, :SchdSuplRentBal, :SchdSuplRentPayNow, :SchdIncrCost, ");
		insertSql.append(
				" :SchdIncrCostPaid, :SchdIncrCostBal, :SchdIncrCostPayNow, :PftSchdWaivedNow , :LatePftSchdWaivedNow, ");
		insertSql.append(
				" :PriSchdWaivedNow, :SchdFeeWaivedNow, :SchdInsWaivedNow, :SchdSuplRentWaivedNow, :SchdIncrCostWaivedNow,");
		insertSql.append(" :PaidPenaltyCGST, :PaidPenaltySGST, :PaidPenaltyUGST, :PaidPenaltyIGST) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(repaySchdList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void deleteRpySchdList(String finReference, String type) {
		logger.debug("Entering");

		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayScheduleDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteFinRepaySchListByTranId(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");

		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);
		scheduleDetail.setLinkedTranId(linkedTranId);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayScheduleDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference=:FinReference AND LinkedTranId=:LinkedTranId ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Get Total Repayment Profit Amount till Date
	 */
	@Override
	public BigDecimal getPaidPft(String finReference, Date finPostDate) {
		logger.debug("Entering");

		FinanceRepayments repayment = new FinanceRepayments();
		repayment.setFinReference(finReference);
		repayment.setFinPostDate(finPostDate);

		StringBuilder selectSql = new StringBuilder(" SELECT SUM(FinSchdPftPaid) FROM FinRepayDetails ");
		selectSql.append(" where FinReference=:FinReference AND  FinPostDate < :FinPostDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayment);
		BigDecimal totalPftPaid = BigDecimal.ZERO;
		try {
			totalPftPaid = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			totalPftPaid = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return totalPftPaid;
	}

	@Override
	public List<FinRepayHeader> getFinRepayHeadersByRef(String finReference, String type) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"Select RepayID, ReceiptSeqID, FinReference , ValueDate , FinEvent , ");
		selectSql.append(" RepayAmount , PriAmount , PftAmount , LatePftAmount, TotalPenalty, TotalRefund , ");
		selectSql.append(
				" TotalWaiver , InsRefund ,RepayAccountId , EarlyPayEffMtd , EarlyPayDate, SchdRegenerated, LinkedTranId, ");
		selectSql.append(
				" TotalIns , TotalSuplRent , TotalIncrCost, TotalSchdFee, PayApportionment, RealizeUnAmz, CpzChg, AdviseAmount,FeeAmount ");
		selectSql.append(" From FinRepayHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);

		List<FinRepayHeader> rpyHeaderList = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return rpyHeaderList;
	}

	@Override
	public void deleteByRef(String finReference, TableType tableType) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayHeader");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where FinReference=:FinReference ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<RepayScheduleDetail> getDMRpySchdList(String finReference, String type) {

		// Copied from getRpySchdList and removed unwanted fields
		logger.debug("Entering");

		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" Select RepayID, RepaySchID, FinReference , SchDate , SchdFor , ProfitSchdBal , PrincipalSchdBal , ");
		selectSql.append(" ProfitSchd , ProfitSchdPaid , PrincipalSchd , PrincipalSchdPaid , ");
		selectSql.append(
				" ProfitSchdPayNow , TdsSchdPayNow, PrincipalSchdPayNow , PenaltyAmt , DaysLate , MaxWaiver , AllowRefund , AllowWaiver , ");
		selectSql.append(
				" RefundReq , WaivedAmt , RepayBalance, PenaltyPayNow, SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow, ");
		selectSql.append(" LatePftSchd, LatePftSchdPaid, LatePftSchdBal, LatePftSchdPayNow, ");
		selectSql.append(" PftSchdWaivedNow , LatePftSchdWaivedNow, ");
		selectSql.append(" PriSchdWaivedNow ");
		selectSql.append(" From FinRepayScheduleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference=:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayScheduleDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public void updateFinReference(String finReference, String extReference, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinRepayHeader");
		updateSql.append(type);
		updateSql.append(" SET  FinReference=:Reference  ");
		updateSql.append(" Where FinReference=:ExtReference");

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
	public FinRepayHeader getFinRepayHeadersByReceipt(long receiptId, String type) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setReceiptSeqID(receiptId);

		StringBuilder selectSql = new StringBuilder(
				"Select RepayID, ReceiptSeqID, FinReference , ValueDate , FinEvent , ");
		selectSql.append(" RepayAmount , PriAmount , PftAmount , LatePftAmount, TotalPenalty, TotalRefund , ");
		selectSql.append(
				" TotalWaiver , InsRefund ,RepayAccountId , EarlyPayEffMtd , EarlyPayDate, SchdRegenerated, LinkedTranId, ");
		selectSql.append(
				" TotalIns , TotalSuplRent , TotalIncrCost, TotalSchdFee, PayApportionment, RealizeUnAmz, CpzChg, RealizeUnLPI, RealizeUnLPP, RealizeUnLPIGst, RealizeUnLPPGst,AdviseAmount,FeeAmount,ExcessAmount ");
		selectSql.append(" From FinRepayHeader");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptSeqID =:ReceiptSeqID ");

		// FIXME RM: CpzChg needs to be added in FinRepayHeader

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);

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
	public List<RepayScheduleDetail> getRpySchdList(long repayId, String type) {
		logger.debug("Entering");

		RepayScheduleDetail scheduleDetail = new RepayScheduleDetail();
		scheduleDetail.setRepayID(repayId);

		StringBuilder selectSql = new StringBuilder(
				" Select RepayID, RepaySchID, FinReference , SchDate , SchdFor , ProfitSchdBal , PrincipalSchdBal , ");
		selectSql.append(" ProfitSchd , ProfitSchdPaid , PrincipalSchd , PrincipalSchdPaid , ");
		selectSql.append(
				" ProfitSchdPayNow , TdsSchdPayNow, PrincipalSchdPayNow , PenaltyAmt , DaysLate , MaxWaiver , AllowRefund , AllowWaiver , ");
		selectSql.append(
				" RefundReq , WaivedAmt , RepayBalance, PenaltyPayNow, SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow, ");
		selectSql.append(
				" SchdIns, SchdInsPaid, SchdInsBal, SchdInsPayNow,LatePftSchd, LatePftSchdPaid, LatePftSchdBal, LatePftSchdPayNow, ");
		selectSql.append(" SchdSuplRent, SchdSuplRentPaid, SchdSuplRentBal,SchdSuplRentPayNow, SchdIncrCost, ");
		selectSql.append(
				" SchdIncrCostPaid, SchdIncrCostBal, SchdIncrCostPayNow, PftSchdWaivedNow , LatePftSchdWaivedNow, ");
		selectSql.append(
				" PriSchdWaivedNow, SchdFeeWaivedNow, SchdInsWaivedNow, SchdSuplRentWaivedNow, SchdIncrCostWaivedNow,");
		selectSql.append(" PaidPenaltyCGST, PaidPenaltySGST, PaidPenaltyUGST, PaidPenaltyIGST  ");
		selectSql.append(" From FinRepayScheduleDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where RepayID=:RepayID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayScheduleDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void deleteByReceiptId(long receiptId, TableType tableType) {
		logger.debug("Entering");

		FinRepayHeader header = new FinRepayHeader();
		header.setReceiptSeqID(receiptId);

		StringBuilder deleteSql = new StringBuilder(" DELETE From FinRepayHeader");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptSeqID=:ReceiptSeqID ");

		logger.debug("selectSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinanceRepayments> getInProcessRepaymnets(String finReference, List<Long> receiptList) {
		logger.debug("Entering");

		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("ReceiptList", receiptList);

		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(
				" Finschddate,SUM(FinSchdPriPaid) FinSchdPriPaid, SUM(FinSchdPftPaid) FinSchdPftPaid, SUM(FinSchdTdsPaid) FinSchdTdsPaid, ");
		selectSql.append(" SUM(FinTotSchdPaid) FinTotSchdPaid,SUM(PenaltyPaid) PenaltyPaid From FinRepayDetails");
		selectSql.append("  where FinReference=:FinReference and ReceiptId In (:ReceiptList) group by Finschddate");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceRepayments.class);

		logger.debug("Leaving");
		repaymentList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		return repaymentList;
	}

	@Override
	public List<FinanceRepayments> getFinRepayments(String finReference, List<Long> receiptList) {
		logger.debug("Entering");

		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference, FinPostDate, FinRpyFor, FinPaySeq,");
		selectSql.append(" FinRpyAmount, FinSchdDate, FinValueDate, FinBranch,");
		selectSql.append(" FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid,");
		selectSql.append(" FinTotSchdPaid, FinFee, FinWaiver, FinRefund, SchdFeePaid , SchdInsPaid , ");
		selectSql.append(" SchdSuplRentPaid , SchdIncrCostPaid");
		selectSql.append(" From FinRepayDetails");
		selectSql.append("  where FinReference=:FinReference ");

		if (receiptList != null && receiptList.size() > 0) {
			source.addValue("ReceiptId", receiptList);
			selectSql.append(" and ReceiptId IN (=:ReceiptId) ");

		}

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceRepayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceRepayments.class);

		logger.debug("Leaving");
		repaymentList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		return repaymentList;
	}

}
