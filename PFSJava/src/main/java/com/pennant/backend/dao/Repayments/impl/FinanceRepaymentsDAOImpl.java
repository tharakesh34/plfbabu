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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
		sql.append(" (FinReference, FinSchdDate, FinRpyFor, FinPaySeq,LinkedTranId");
		sql.append(", FinRpyAmount, FinPostDate , FinValueDate, FinBranch");
		sql.append(", FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid");
		sql.append(", SchdFeePaid , SchdInsPaid , SchdSuplRentPaid , SchdIncrCostPaid");
		sql.append(", FinTotSchdPaid, FinFee, FinWaiver, FinRefund");
		sql.append(", PenaltyPaid, PenaltyWaived, ReceiptId, WaiverId) Values(");
		sql.append(" :FinReference, :FinSchdDate, :FinRpyFor, :FinPaySeq,:LinkedTranId");
		sql.append(", :FinRpyAmount, :FinPostDate, :FinValueDate, :FinBranch");
		sql.append(", :FinType, :FinCustID, :FinSchdPriPaid, :FinSchdPftPaid,:FinSchdTdsPaid");
		sql.append(", :SchdFeePaid, :SchdInsPaid,  :SchdSuplRentPaid, :SchdIncrCostPaid");
		sql.append(", :FinTotSchdPaid, :FinFee, :FinWaiver, :FinRefund");
		sql.append(", :PenaltyPaid, :PenaltyWaived, :ReceiptId, :WaiverId)");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayments);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return financeRepayments.getId();
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finReference, boolean isRpyCancelProc, String type) {
		logger.debug(Literal.ENTERING);

		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();

		StringBuilder sql = getRepayListQuery(isRpyCancelProc, type);
		sql.append(" t1 where t1.FinReference = ?");
		if (isRpyCancelProc) {
			sql.append(" and t1.LinkedTranId = (Select MAX(t2.LinkedTranId) from FinRepayDetails t2");
			sql.append(" Where t1.FinReference = t2.FinReference)");
			sql.append(" and t1.LinkedTranId != ?");
			sql.append(" order by t1.FinSchdDate desc");
		}

		logger.trace(Literal.SQL + sql.toString());

		FinRepayListRowMapper rowMapper = new FinRepayListRowMapper(isRpyCancelProc);

		try {
			repaymentList = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);

					if (isRpyCancelProc) {
						ps.setInt(index++, 0);
					}

				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (repaymentList == null || repaymentList.isEmpty()) {
			sql = new StringBuilder();
			sql = getRepayListQuery(isRpyCancelProc, type);
			sql.append(" t1 where t1.FinReference = ?");
			if (isRpyCancelProc) {
				sql.append(" and t1.FinPostDate = (Select MAX(t2.FinPostDate) from FinRepayDetails t2");
				sql.append(" Where t1.FinReference = t2.FinReference)");
				sql.append(" and t1.LinkedTranId = 0 ORDER BY t1.FinSchdDate desc");
			}

			logger.debug(Literal.SQL + sql.toString());

			try {
				repaymentList = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						int index = 1;
						ps.setString(index++, finReference);
					}
				}, rowMapper);

			} catch (EmptyResultDataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
		return repaymentList;
	}

	private StringBuilder getRepayListQuery(boolean isRpyCancelProc, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinReference, t1.FinPostDate, t1.FinRpyFor, t1.FinPaySeq, t1.FinRpyAmount");
		sql.append(", t1.FinSchdDate, t1.FinValueDate, t1.FinBranch, t1.FinType, t1.FinCustID");
		sql.append(", t1.FinSchdPriPaid, t1.FinSchdPftPaid, t1.FinSchdTdsPaid, t1.FinTotSchdPaid");
		sql.append(", t1.FinFee, t1.FinWaiver, t1.FinRefund, t1.SchdFeePaid, t1.SchdInsPaid, t1.PenaltyPaid");
		sql.append(", t1.PenaltyWaived, t1.SchdSuplRentPaid, t1.SchdIncrCostPaid");

		if (isRpyCancelProc) {
			sql.append(", t1.LinkedTranId");
		}

		sql.append(" from FinRepayDetails");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	@Override
	public List<FinanceRepayments> getByFinRefAndSchdDate(String finReference, Date finSchdDate) {
		logger.debug("Entering");

		List<FinanceRepayments> repaymentList = new ArrayList<FinanceRepayments>();

		FinanceRepayments financeRepayments = new FinanceRepayments();
		financeRepayments.setFinReference(finReference);
		financeRepayments.setFinSchdDate(finSchdDate);

		StringBuilder selectSql = new StringBuilder(" Select T1.FinReference, T1.FinPostDate,T1.finSchdDate,");
		selectSql.append(" T1.FinValueDate,T1.FinSchdPriPaid, T1.FinSchdPftPaid, T1.FinSchdTdsPaid, ");
		selectSql.append(" T1.FinTotSchdPaid, T1.PenaltyPaid, T1.PenaltyWaived ");
		selectSql.append(" From FinRepayDetails");
		selectSql.append(" T1 where T1.FinReference=:FinReference and T1.FinSchdDate=:FinSchdDate");
		selectSql.append(" order by T1.FinValueDate ");

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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getFinRepayheaderQuery(type);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinRepayHeaderRowMapper rowMapper = new FinRepayHeaderRowMapper();
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public FinRepayHeader getFinRepayHeader(String finReference, long linkedTranId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ValueDate, FinEvent");
		sql.append(" from FinRepayHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Where FinReference = ? and LinkedTranId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, linkedTranId },
					new RowMapper<FinRepayHeader>() {
						@Override
						public FinRepayHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinRepayHeader rh = new FinRepayHeader();

							rh.setValueDate(rs.getTimestamp("ValueDate"));
							rh.setFinEvent(rs.getString("FinEvent"));

							return rh;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public Long saveFinRepayHeader(FinRepayHeader finRepayHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (finRepayHeader.getRepayID() == 0 || finRepayHeader.getRepayID() == Long.MIN_VALUE) {
			finRepayHeader.setRepayID(getNextValue("SeqFinRepayHeader"));
			logger.debug("get NextID:" + finRepayHeader.getRepayID());
		}

		StringBuilder sql = new StringBuilder("Insert Into FinRepayHeader");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (RepayID, ReceiptSeqID, FinReference, ValueDate, FinEvent, RepayAmount, PriAmount");
		sql.append(", PftAmount, TotalRefund, TotalWaiver, InsRefund, RepayAccountId, EarlyPayEffMtd");
		sql.append(", EarlyPayDate, SchdRegenerated, LinkedTranId, TotalIns, TotalSuplRent, TotalIncrCost");
		sql.append(", TotalSchdFee, PayApportionment, LatePftAmount, TotalPenalty, RealizeUnAmz");
		sql.append(", CpzChg, AdviseAmount, FeeAmount, ExcessAmount");
		sql.append(", RealizeUnLPI, PartialPaidAmount, FutPriAmount, FutPftAmount"); // Merged from BFL 
		sql.append(") Values(");
		sql.append(":RepayID, :ReceiptSeqID, :FinReference, :ValueDate, :FinEvent, :RepayAmount, :PriAmount");
		sql.append(", :PftAmount, :TotalRefund, :TotalWaiver, :InsRefund, :RepayAccountId, :EarlyPayEffMtd");
		sql.append(", :EarlyPayDate, :SchdRegenerated, :LinkedTranId, :TotalIns, :TotalSuplRent, :TotalIncrCost");
		sql.append(", :TotalSchdFee, :PayApportionment, :LatePftAmount, :TotalPenalty, :RealizeUnAmz");
		sql.append(", :CpzChg, :AdviseAmount , :FeeAmount,:ExcessAmount");
		sql.append(", :RealizeUnLPI, :PartialPaidAmount, :FutPriAmount, :FutPftAmount"); // Merged from BFL 
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
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
		updateSql.append(" TotalIns=:TotalIns , RealizeUnAmz=:RealizeUnAmz, CpzChg=:CpzChg,");
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getFinRepayScheduleQuery(type);
		sql.append(" where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepaySchdRowMapper rowMapper = new FinRepaySchdRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		insertSql.append(" PaidPenaltyCGST, PaidPenaltySGST, PaidPenaltyUGST, PaidPenaltyIGST,");
		insertSql.append(
				" PenaltyWaiverCGST, PenaltyWaiverSGST, PenaltyWaiverUGST, PenaltyWaiverIGST, TaxHeaderId, WaiverId) ");
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
		insertSql.append(" :PaidPenaltyCGST, :PaidPenaltySGST, :PaidPenaltyUGST, :PaidPenaltyIGST,");
		insertSql.append(
				" :PenaltyWaiverCGST, :PenaltyWaiverSGST, :PenaltyWaiverUGST, :PenaltyWaiverIGST, :TaxHeaderId, :WaiverId) ");

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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getFinRepayheaderQuery(type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepayHeaderRowMapper rowMapper = new FinRepayHeaderRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getFinRepayScheduleQuery(type);
		sql.append(" where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepaySchdRowMapper rowMapper = new FinRepaySchdRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getFinRepayheaderQuery(type);
		sql.append(" Where ReceiptSeqID = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinRepayHeaderRowMapper rowMapper = new FinRepayHeaderRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptId }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<RepayScheduleDetail> getRpySchdList(long repayId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getFinRepayScheduleQuery(type);
		sql.append(" where RepayID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRepaySchdRowMapper rowMapper = new FinRepaySchdRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, repayId);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinPostDate, FinRpyFor, FinPaySeq, FinRpyAmount, FinSchdDate, FinValueDate");
		sql.append(", FinBranch, FinType, FinCustID, FinSchdPriPaid, FinSchdPftPaid, FinSchdTdsPaid");
		sql.append(", FinTotSchdPaid, FinFee, FinWaiver, FinRefund, SchdFeePaid, SchdInsPaid, SchdSuplRentPaid");
		sql.append(", SchdIncrCostPaid");
		sql.append(" from FinRepayDetails");
		sql.append(" where FinReference = ?");

		if (receiptList != null && receiptList.size() > 0) {
			sql.append(" and ReceiptId IN (");
			int i = 0;
			while (i < receiptList.size()) {
				sql.append(" ?,");
				i++;
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" )");
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					if (receiptList != null && receiptList.size() > 0) {
						for (Long receiptId : receiptList) {
							ps.setLong(index++, receiptId);
						}
					}
				}
			}, new RowMapper<FinanceRepayments>() {
				@Override
				public FinanceRepayments mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceRepayments rd = new FinanceRepayments();

					rd.setFinReference(rs.getString("FinReference"));
					rd.setFinPostDate(rs.getTimestamp("FinPostDate"));
					rd.setFinRpyFor(rs.getString("FinRpyFor"));
					rd.setFinPaySeq(rs.getLong("FinPaySeq"));
					rd.setFinRpyAmount(rs.getBigDecimal("FinRpyAmount"));
					rd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
					rd.setFinValueDate(rs.getTimestamp("FinValueDate"));
					rd.setFinBranch(rs.getString("FinBranch"));
					rd.setFinType(rs.getString("FinType"));
					rd.setFinCustID(rs.getLong("FinCustID"));
					rd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
					rd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
					rd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
					rd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
					rd.setFinFee(rs.getBigDecimal("FinFee"));
					rd.setFinWaiver(rs.getBigDecimal("FinWaiver"));
					rd.setFinRefund(rs.getBigDecimal("FinRefund"));
					rd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
					rd.setSchdInsPaid(rs.getBigDecimal("SchdInsPaid"));
					rd.setSchdSuplRentPaid(rs.getBigDecimal("SchdSuplRentPaid"));
					rd.setSchdIncrCostPaid(rs.getBigDecimal("SchdIncrCostPaid"));

					return rd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private StringBuilder getFinRepayScheduleQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RepayID, RepaySchID, FinReference, SchDate, SchdFor, ProfitSchdBal, PrincipalSchdBal");
		sql.append(", ProfitSchd, ProfitSchdPaid, PrincipalSchd, PrincipalSchdPaid, ProfitSchdPayNow");
		sql.append(", TdsSchdPayNow, PrincipalSchdPayNow, PenaltyAmt, DaysLate, MaxWaiver, AllowRefund");
		sql.append(", AllowWaiver, RefundReq, WaivedAmt, RepayBalance, PenaltyPayNow, SchdFee, SchdFeePaid");
		sql.append(", SchdFeeBal, SchdFeePayNow, SchdIns, SchdInsPaid, SchdInsBal, SchdInsPayNow, LatePftSchd");
		sql.append(", LatePftSchdPaid, LatePftSchdBal, LatePftSchdPayNow, SchdSuplRent, SchdSuplRentPaid");
		sql.append(", SchdSuplRentBal, SchdSuplRentPayNow, SchdIncrCost, SchdIncrCostPaid, SchdIncrCostBal");
		sql.append(", SchdIncrCostPayNow, PftSchdWaivedNow, LatePftSchdWaivedNow, PriSchdWaivedNow");
		sql.append(", SchdFeeWaivedNow, SchdInsWaivedNow, SchdSuplRentWaivedNow, SchdIncrCostWaivedNow");
		sql.append(", PaidPenaltyCGST, PaidPenaltySGST, PaidPenaltyUGST, PaidPenaltyIGST, PenaltyWaiverCGST");
		sql.append(", PenaltyWaiverSGST, PenaltyWaiverUGST, PenaltyWaiverIGST, TaxHeaderId, WaiverId");
		sql.append(" from FinRepayScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private StringBuilder getFinRepayheaderQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RepayID, ReceiptSeqID, FinReference, ValueDate, FinEvent, RepayAmount, PriAmount");
		sql.append(", PftAmount, LatePftAmount, TotalPenalty, TotalRefund, TotalWaiver, InsRefund");
		sql.append(", RepayAccountId, EarlyPayEffMtd, EarlyPayDate, SchdRegenerated, LinkedTranId");
		sql.append(", TotalIns, TotalSuplRent, TotalIncrCost, TotalSchdFee, PayApportionment, RealizeUnAmz");
		sql.append(", CpzChg, RealizeUnLPI, RealizeUnLPP, RealizeUnLPIGst, RealizeUnLPPGst, CpzChg");
		sql.append(", AdviseAmount, FeeAmount, ExcessAmount, PartialPaidAmount, FutPriAmount, FutPftAmount");
		sql.append(" from FinRepayHeader");
		sql.append(StringUtils.trim(type));
		return sql;
	}

	private class FinRepaySchdRowMapper implements RowMapper<RepayScheduleDetail> {

		@Override
		public RepayScheduleDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			RepayScheduleDetail frs = new RepayScheduleDetail();

			frs.setRepayID(rs.getLong("RepayID"));
			frs.setRepaySchID(rs.getInt("RepaySchID"));
			frs.setFinReference(rs.getString("FinReference"));
			frs.setSchDate(rs.getTimestamp("SchDate"));
			frs.setSchdFor(rs.getString("SchdFor"));
			frs.setProfitSchdBal(rs.getBigDecimal("ProfitSchdBal"));
			frs.setPrincipalSchdBal(rs.getBigDecimal("PrincipalSchdBal"));
			frs.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			frs.setProfitSchdPaid(rs.getBigDecimal("ProfitSchdPaid"));
			frs.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			frs.setPrincipalSchdPaid(rs.getBigDecimal("PrincipalSchdPaid"));
			frs.setProfitSchdPayNow(rs.getBigDecimal("ProfitSchdPayNow"));
			frs.setTdsSchdPayNow(rs.getBigDecimal("TdsSchdPayNow"));
			frs.setPrincipalSchdPayNow(rs.getBigDecimal("PrincipalSchdPayNow"));
			frs.setPenaltyAmt(rs.getBigDecimal("PenaltyAmt"));
			frs.setDaysLate(rs.getInt("DaysLate"));
			frs.setMaxWaiver(rs.getBigDecimal("MaxWaiver"));
			frs.setAllowRefund(rs.getBoolean("AllowRefund"));
			frs.setAllowWaiver(rs.getBoolean("AllowWaiver"));
			frs.setRefundReq(rs.getBigDecimal("RefundReq"));
			frs.setWaivedAmt(rs.getBigDecimal("WaivedAmt"));
			frs.setRepayBalance(rs.getBigDecimal("RepayBalance"));
			frs.setPenaltyPayNow(rs.getBigDecimal("PenaltyPayNow"));
			frs.setSchdFee(rs.getBigDecimal("SchdFee"));
			frs.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			frs.setSchdFeeBal(rs.getBigDecimal("SchdFeeBal"));
			frs.setSchdFeePayNow(rs.getBigDecimal("SchdFeePayNow"));
			frs.setSchdIns(rs.getBigDecimal("SchdIns"));
			frs.setSchdInsPaid(rs.getBigDecimal("SchdInsPaid"));
			frs.setSchdInsBal(rs.getBigDecimal("SchdInsBal"));
			frs.setSchdInsPayNow(rs.getBigDecimal("SchdInsPayNow"));
			frs.setLatePftSchd(rs.getBigDecimal("LatePftSchd"));
			frs.setLatePftSchdPaid(rs.getBigDecimal("LatePftSchdPaid"));
			frs.setLatePftSchdBal(rs.getBigDecimal("LatePftSchdBal"));
			frs.setLatePftSchdPayNow(rs.getBigDecimal("LatePftSchdPayNow"));
			frs.setSchdSuplRent(rs.getBigDecimal("SchdSuplRent"));
			frs.setSchdSuplRentPaid(rs.getBigDecimal("SchdSuplRentPaid"));
			frs.setSchdSuplRentBal(rs.getBigDecimal("SchdSuplRentBal"));
			frs.setSchdSuplRentPayNow(rs.getBigDecimal("SchdSuplRentPayNow"));
			frs.setSchdIncrCost(rs.getBigDecimal("SchdIncrCost"));
			frs.setSchdIncrCostPaid(rs.getBigDecimal("SchdIncrCostPaid"));
			frs.setSchdIncrCostBal(rs.getBigDecimal("SchdIncrCostBal"));
			frs.setSchdIncrCostPayNow(rs.getBigDecimal("SchdIncrCostPayNow"));
			frs.setPftSchdWaivedNow(rs.getBigDecimal("PftSchdWaivedNow"));
			frs.setLatePftSchdWaivedNow(rs.getBigDecimal("LatePftSchdWaivedNow"));
			frs.setPriSchdWaivedNow(rs.getBigDecimal("PriSchdWaivedNow"));
			frs.setSchdFeeWaivedNow(rs.getBigDecimal("SchdFeeWaivedNow"));
			frs.setSchdInsWaivedNow(rs.getBigDecimal("SchdInsWaivedNow"));
			frs.setSchdSuplRentWaivedNow(rs.getBigDecimal("SchdSuplRentWaivedNow"));
			frs.setSchdIncrCostWaivedNow(rs.getBigDecimal("SchdIncrCostWaivedNow"));
			frs.setPaidPenaltyCGST(rs.getBigDecimal("PaidPenaltyCGST"));
			frs.setPaidPenaltySGST(rs.getBigDecimal("PaidPenaltySGST"));
			frs.setPaidPenaltyUGST(rs.getBigDecimal("PaidPenaltyUGST"));
			frs.setPaidPenaltyIGST(rs.getBigDecimal("PaidPenaltyIGST"));
			frs.setPenaltyWaiverCGST(rs.getBigDecimal("PenaltyWaiverCGST"));
			frs.setPenaltyWaiverSGST(rs.getBigDecimal("PenaltyWaiverSGST"));
			frs.setPenaltyWaiverUGST(rs.getBigDecimal("PenaltyWaiverUGST"));
			frs.setPenaltyWaiverIGST(rs.getBigDecimal("PenaltyWaiverIGST"));
			frs.setTaxHeaderId(rs.getLong("TaxHeaderId"));
			frs.setWaiverId(rs.getLong("WaiverId"));

			return frs;
		}
	}

	private class FinRepayHeaderRowMapper implements RowMapper<FinRepayHeader> {

		@Override
		public FinRepayHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinRepayHeader rh = new FinRepayHeader();

			rh.setRepayID(rs.getLong("RepayID"));
			rh.setReceiptSeqID(rs.getLong("ReceiptSeqID"));
			rh.setFinReference(rs.getString("FinReference"));
			rh.setValueDate(rs.getTimestamp("ValueDate"));
			rh.setFinEvent(rs.getString("FinEvent"));
			rh.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			rh.setPriAmount(rs.getBigDecimal("PriAmount"));
			rh.setPftAmount(rs.getBigDecimal("PftAmount"));
			rh.setLatePftAmount(rs.getBigDecimal("LatePftAmount"));
			rh.setTotalPenalty(rs.getBigDecimal("TotalPenalty"));
			rh.setTotalRefund(rs.getBigDecimal("TotalRefund"));
			rh.setTotalWaiver(rs.getBigDecimal("TotalWaiver"));
			rh.setInsRefund(rs.getBigDecimal("InsRefund"));
			rh.setRepayAccountId(rs.getString("RepayAccountId"));
			rh.setEarlyPayEffMtd(rs.getString("EarlyPayEffMtd"));
			rh.setEarlyPayDate(rs.getTimestamp("EarlyPayDate"));
			rh.setSchdRegenerated(rs.getBoolean("SchdRegenerated"));
			rh.setLinkedTranId(rs.getLong("LinkedTranId"));
			rh.setTotalIns(rs.getBigDecimal("TotalIns"));
			rh.setTotalSuplRent(rs.getBigDecimal("TotalSuplRent"));
			rh.setTotalIncrCost(rs.getBigDecimal("TotalIncrCost"));
			rh.setTotalSchdFee(rs.getBigDecimal("TotalSchdFee"));
			rh.setPayApportionment(rs.getString("PayApportionment"));
			rh.setRealizeUnAmz(rs.getBigDecimal("RealizeUnAmz"));
			rh.setCpzChg(rs.getBigDecimal("CpzChg"));
			rh.setRealizeUnLPI(rs.getBigDecimal("RealizeUnLPI"));
			/*
			 * rh.setRealizeUnLPP(rs.getBigDecimal("RealizeUnLPP"));
			 * rh.setRealizeUnLPIGst(rs.getBigDecimal("RealizeUnLPIGst"));
			 * rh.setRealizeUnLPPGst(rs.getBigDecimal("RealizeUnLPPGst")); (these columns are not available in bean)
			 */
			rh.setAdviseAmount(rs.getBigDecimal("AdviseAmount"));
			rh.setFeeAmount(rs.getBigDecimal("FeeAmount"));
			rh.setExcessAmount(rs.getBigDecimal("ExcessAmount"));
			rh.setPartialPaidAmount(rs.getBigDecimal("PartialPaidAmount"));
			rh.setFutPriAmount(rs.getBigDecimal("FutPriAmount"));
			rh.setFutPftAmount(rs.getBigDecimal("FutPftAmount"));

			return rh;
		}

	}

	private class FinRepayListRowMapper implements RowMapper<FinanceRepayments> {
		private boolean isRpyCancelProc;

		private FinRepayListRowMapper(boolean isRpyCancelProc) {
			this.isRpyCancelProc = isRpyCancelProc;
		}

		@Override
		public FinanceRepayments mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceRepayments rd = new FinanceRepayments();

			rd.setFinReference(rs.getString("FinReference"));
			rd.setFinPostDate(rs.getTimestamp("FinPostDate"));
			rd.setFinRpyFor(rs.getString("FinRpyFor"));
			rd.setFinPaySeq(rs.getLong("FinPaySeq"));
			rd.setFinRpyAmount(rs.getBigDecimal("FinRpyAmount"));
			rd.setFinSchdDate(rs.getTimestamp("FinSchdDate"));
			rd.setFinValueDate(rs.getTimestamp("FinValueDate"));
			rd.setFinBranch(rs.getString("FinBranch"));
			rd.setFinType(rs.getString("FinType"));
			rd.setFinCustID(rs.getLong("FinCustID"));
			rd.setFinSchdPriPaid(rs.getBigDecimal("FinSchdPriPaid"));
			rd.setFinSchdPftPaid(rs.getBigDecimal("FinSchdPftPaid"));
			rd.setFinSchdTdsPaid(rs.getBigDecimal("FinSchdTdsPaid"));
			rd.setFinTotSchdPaid(rs.getBigDecimal("FinTotSchdPaid"));
			rd.setFinFee(rs.getBigDecimal("FinFee"));
			rd.setFinWaiver(rs.getBigDecimal("FinWaiver"));
			rd.setFinRefund(rs.getBigDecimal("FinRefund"));
			rd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			rd.setSchdInsPaid(rs.getBigDecimal("SchdInsPaid"));
			rd.setPenaltyPaid(rs.getBigDecimal("PenaltyPaid"));
			rd.setPenaltyWaived(rs.getBigDecimal("PenaltyWaived"));
			rd.setSchdSuplRentPaid(rs.getBigDecimal("SchdSuplRentPaid"));
			rd.setSchdIncrCostPaid(rs.getBigDecimal("SchdIncrCostPaid"));

			if (isRpyCancelProc) {
				rd.setLinkedTranId(rs.getLong("LinkedTranId"));
			}

			return rd;
		}
	}

	/**
	 * Method for Fetching new RepayID
	 * 
	 * @param finRepayHeader
	 * @return
	 */
	@Override
	public long getNewRepayID() {
		return getNextValue("SeqFinRepayHeader");
	}
}
