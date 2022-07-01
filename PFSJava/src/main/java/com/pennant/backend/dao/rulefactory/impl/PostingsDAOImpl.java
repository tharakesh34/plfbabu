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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : PostingsDAOImpl.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 07-02-2012
 * 
 * Modified Date : 07-02-2012
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-02-2012 PENNANT TECHONOLOGIES 0.1 * 02-06-2018 Satish 1.0 Fix for the posting reversal values mismatch * * * * * *
 * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.rulefactory.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * DAO methods implementation for the <b>ReturnDataSet model</b> class.<br>
 */
public class PostingsDAOImpl extends SequenceDao<ReturnDataSet> implements PostingsDAO {
	private static Logger logger = LogManager.getLogger(PostingsDAOImpl.class);

	public PostingsDAOImpl() {
		super();
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String reference, String finEvent, boolean showZeroBal,
			String postingGroupBy, String type) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ValueDate, PostDate, AppDate, AppValueDate, TranCode, RevTranCode, TranDesc");
		sql.append(", RevTranCode, DrOrCr, Account, PostAmount");
		sql.append(", FinEvent, AcCcy, PostBranch, UserBranch ");

		if (StringUtils.isNotBlank(type)) {
			sql.append(", LovDescEventCodeName");
		} else {
			sql.append(", AEEventCodeDesc LovDescEventCodeName");
		}

		sql.append(" FROM Postings");

		if (StringUtils.isBlank(type)) {
			sql.append(" p Inner Join BMTAEEvents ba on ba.AEEventCode = p.FinEvent");
		}

		if (StringUtils.isNotBlank(type)) {
			sql.append(type);
		}

		sql.append(" Where FinReference = ? and FinEvent in (");
		List<String> asList = Arrays.asList(finEvent.split(","));
		sql.append(JdbcUtil.getInCondition(asList));
		sql.append(")");

		if (!showZeroBal) {
			sql.append(" and PostAmount != ?");
		}

		if (PennantConstants.EVENTBASE.equals(postingGroupBy)) {
			sql.append(" order by FinEvent, LinkedTranID, TransOrder");
		} else if (PennantConstants.POSTDATE.equals(postingGroupBy)) {
			sql.append(" order by PostDate, LinkedTranID, TransOrder");
		} else if (PennantConstants.ACCNO.equals(postingGroupBy)) {
			sql.append(" order by Account, LinkedTranID, TransOrder");
		} else {
			sql.append(" order by ValueDate, LinkedTranID, TransOrder");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, reference);

			for (String event : finEvent.split(",")) {
				ps.setString(index++, StringUtils.trimToEmpty(event));
			}

			if (!showZeroBal) {
				ps.setBigDecimal(index++, BigDecimal.ZERO);
			}

		}, (rs, rowNum) -> {
			ReturnDataSet rds = new ReturnDataSet();

			rds.setValueDate(rs.getDate("ValueDate"));
			rds.setPostDate(rs.getDate("PostDate"));
			rds.setAppDate(rs.getDate("AppDate"));
			rds.setAppValueDate(rs.getDate("AppValueDate"));
			rds.setTranCode(rs.getString("TranCode"));
			rds.setRevTranCode(rs.getString("RevTranCode"));
			rds.setTranDesc(rs.getString("TranDesc"));
			rds.setDrOrCr(rs.getString("DrOrCr"));
			rds.setAccount(rs.getString("Account"));
			rds.setPostAmount(rs.getBigDecimal("PostAmount"));
			rds.setFinEvent(rs.getString("FinEvent"));
			rds.setAcCcy(rs.getString("AcCcy"));
			rds.setPostBranch(rs.getString("PostBranch"));
			rds.setUserBranch(rs.getString("UserBranch"));
			rds.setLovDescEventCodeName(rs.getString("LovDescEventCodeName"));

			return rds;
		});
	}

	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId) {
		StringBuilder sql = getSelectQuery();
		sql.append(" Where LinkedTranId = ?");
		sql.append(" order by LinkedTranId, TranOrderId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, linkedTranId);
		}, new ReturnDataSetRowMapper());
	}

	@Override
	public List<ReturnDataSet> getPostingsByTransIdList(List<Long> tranIdList) {
		StringBuilder sql = getSelectQuery();
		sql.append(" Where LinkedTranId in (");
		sql.append(JdbcUtil.getInCondition(tranIdList));
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Long linkedTranId : tranIdList) {
				ps.setLong(index++, linkedTranId);
			}
		}, new ReturnDataSetRowMapper());
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(String postref) {
		StringBuilder sql = getSelectQuery();
		sql.append(" Where Postref  = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, postref);
		}, new ReturnDataSetRowMapper());
	}

	@Override
	public void saveBatch(List<ReturnDataSet> dataSetList) {
		setEntityCode(dataSetList);

		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" Postings");
		sql.append(" (LinkedTranId, PostRef, PostingId, FinID, FinReference, FinEvent, PostDate");
		sql.append(", ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, Account, ShadowPosting");
		sql.append(", PostAmountLcCcy, TransOrder, DerivedTranOrder, PostToSys, ExchangeRate");
		sql.append(", PostAmount, AmountType, PostStatus, ErrorId, ErrorMsg, AcCcy, TranOrderId");
		sql.append(", PostBranch, AppDate, AppValueDate, UserBranch, PostCategory, CustAppDate");
		sql.append(", AccountType, OldLinkedTranId, EntityCode");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ReturnDataSet pstngs = dataSetList.get(i);

					int index = 1;

					ps.setLong(index++, pstngs.getLinkedTranId());
					ps.setString(index++, pstngs.getPostref());
					ps.setString(index++, pstngs.getPostingId());
					ps.setObject(index++, pstngs.getFinID());
					ps.setString(index++, pstngs.getFinReference());
					ps.setString(index++, pstngs.getFinEvent());
					ps.setDate(index++, JdbcUtil.getDate(pstngs.getPostDate()));
					ps.setDate(index++, JdbcUtil.getDate(pstngs.getValueDate()));
					ps.setString(index++, pstngs.getTranCode());
					ps.setString(index++, pstngs.getTranDesc());
					ps.setString(index++, pstngs.getRevTranCode());
					ps.setString(index++, pstngs.getDrOrCr());
					ps.setString(index++, pstngs.getAccount());
					ps.setBoolean(index++, pstngs.isShadowPosting());
					ps.setBigDecimal(index++, pstngs.getPostAmountLcCcy());
					ps.setInt(index++, pstngs.getTransOrder());
					ps.setInt(index++, pstngs.getDerivedTranOrder());
					ps.setString(index++, pstngs.getPostToSys());
					ps.setBigDecimal(index++, pstngs.getExchangeRate());
					ps.setBigDecimal(index++, pstngs.getPostAmount());
					ps.setString(index++, pstngs.getAmountType());
					ps.setString(index++, pstngs.getPostStatus());
					ps.setString(index++, pstngs.getErrorId());
					ps.setString(index++, pstngs.getErrorMsg());
					ps.setString(index++, pstngs.getAcCcy());
					ps.setString(index++, pstngs.getTranOrderId());
					ps.setString(index++, pstngs.getPostBranch());
					ps.setDate(index++, JdbcUtil.getDate(pstngs.getAppDate()));
					ps.setDate(index++, JdbcUtil.getDate(pstngs.getAppValueDate()));
					ps.setString(index++, pstngs.getUserBranch());
					ps.setInt(index++, pstngs.getPostCategory());
					ps.setDate(index++, JdbcUtil.getDate(pstngs.getCustAppDate()));
					ps.setString(index++, pstngs.getAccountType());
					ps.setLong(index++, pstngs.getOldLinkedTranId());
					ps.setString(index++, pstngs.getEntityCode());
				}

				@Override
				public int getBatchSize() {
					return dataSetList.size();
				}
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	// FIXME CH to be changed to Batch Update
	@Override
	public void updateStatusByLinkedTranId(long linkedTranId, String postStatus) {
		String sql = "Update Postings Set PostStatus = ? where LinkedTranId = ?";

		logger.debug(Literal.SQL + sql);
		this.jdbcOperations.update(sql, postStatus, linkedTranId);
	}

	// FIXME CH to be changed to Batch Update
	@Override
	public void updateStatusByFinRef(String reference, String postStatus) {
		String sql = "Update Postings Set PostStatus = ? where FinReference = ?";

		logger.debug(Literal.SQL + sql);
		this.jdbcOperations.update(sql, postStatus, reference);
	}

	public long getLinkedTransId() {
		return getNextValue("SeqPostings");
	}

	public long getPostingId() {
		return getNextValue("SeqPostingId");
	}

	@Override
	public List<ReturnDataSet> getPostingsbyFinanceBranch(String branchCode) {
		StringBuilder sql = new StringBuilder("Select * From (");
		sql.append("Select p.LinkedTranId, p.Postref, p.PostingId, p.FinID, p.FinReference, p.FinEvent");
		sql.append(", p.PostDate, p.ValueDate, p.TranCode, p.TranDesc, p.RevTranCode, p.DrOrCr, p.Account");
		sql.append(", p.ShadowPosting, p.PostAmount, p.AmountType, p.PostStatus, p.ErrorId, p.ErrorMsg, p.AcCcy");
		sql.append(", p.TransOrder, p.TranOrderId, p.PostToSys, p.ExchangeRate, UserBranch");
		sql.append(", p.AppDate, p.AppValueDate, p.AccountType");
		sql.append(" From Postings p");
		sql.append(" Inner Join FinanceMain_Temp fm on fm.FinID = p.FinID");
		sql.append(" Where fm.FinBranch = ?");
		sql.append(" Union All ");
		sql.append("Select p.LinkedTranId, p.Postref, p.PostingId, p.FinID, p.FinReference, p.FinEvent");
		sql.append(", p.PostDate, p.ValueDate, p.TranCode, p.TranDesc, p.RevTranCode, p.DrOrCr, p.Account");
		sql.append(", p.ShadowPosting, p.PostAmount, p.AmountType, p.PostStatus, p.ErrorId, p.ErrorMsg, p.AcCcy");
		sql.append(", p.TransOrder, p.TranOrderId, p.PostToSys, p.ExchangeRate, UserBranch");
		sql.append(", p.AppDate, p.AppValueDate, p.AccountType");
		sql.append(" From Postings p");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = p.FinID");
		sql.append(" Where not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		sql.append(" and fm.FinBranch = ?");
		sql.append(") temp order by p.Account, p.FinReference, p.TranCode");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, branchCode);
			ps.setString(2, branchCode);

		}, new ReturnDataSetRowMapper());
	}

	@Override
	public List<ReturnDataSet> getPostingsByVasref(String vasReference, String[] finEvents) {
		List<String> list = Arrays.asList(finEvents);

		StringBuilder sql = getSelectQuery();
		sql.append(" Where FinReference = ? and FinEvent in(");
		sql.append(JdbcUtil.getInCondition(list));
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, vasReference);

			for (String finEvent : list) {
				ps.setString(index++, finEvent);
			}

		}, new ReturnDataSetRowMapper());
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRef(String reference, boolean reqReversals) {
		boolean rpayPostingRev = SysParamUtil
				.isAllowed(SMTParameterConstants.REPAY_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL);
		boolean disPostingRev = SysParamUtil.isAllowed(SMTParameterConstants.DISB_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL);

		StringBuilder sql = getSelectQuery();
		sql.append(" Where FinReference = ? and PostStatus = ?");

		if (!reqReversals) {
			sql.append(" and OldLinkedTranID = ?");
		}

		if (!rpayPostingRev) {
			sql.append(" and FinEvent != ?");
		}

		if (disPostingRev) {
			sql.append(" and LinkedTranId not in (Select LinkedTranId from FinAdvancePayments Where Status in (?, ?)");
			sql.append(" and FinReference = ? and  FinEvent = ?) ");
		}

		sql.append(" order by LinkedTranId, TranOrderId ");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, reference);
			ps.setString(index++, AccountConstants.POSTINGS_SUCCESS);

			if (!reqReversals) {
				ps.setLong(index++, 0);
			}

			if (!rpayPostingRev) {
				ps.setString(index++, "REPAY");
			}

			if (disPostingRev) {
				ps.setString(index++, "REJECTED");
				ps.setString(index++, "CANCELED");
				ps.setString(index++, reference);
				ps.setString(index++, "DISBINS");

			}

		}, new ReturnDataSetRowMapper());

	}

	@Override
	public void updatePostCtg() {
		String sql = "Update Postings Set PostCategory = ? WHERE PostCategory = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, AccountConstants.POSTING_CATEGORY_ACUPDATE,
				AccountConstants.POSTING_CATEGORY_EOD);
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(long postrRef) {
		StringBuilder sql = getSelectQuery();
		sql.append(" Where PostStatus = ? and Postref = ?");
		sql.append(" order by LinkedTranId, TranOrderId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, AccountConstants.POSTINGS_SUCCESS);
			ps.setString(index++, String.valueOf(postrRef));
		}, new ReturnDataSetRowMapper());

	}

	@Override
	public void updateStatusByPostRef(String postRef, String postStatus) {
		String sql = "Update Postings SET PostStatus = ? where PostRef = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, postStatus);
			ps.setString(2, postRef);
		});
	}

	@Override
	public List<ReturnDataSet> getPostings(String postRef, String finEvent) {
		StringBuilder sql = getSelectQuery();
		sql.append(" Where Postref = ? AND FinEvent = ?");
		sql.append(" order by LinkedTranId, TranOrderId ");

		logger.debug(Literal.SQL + sql.toString());

		List<ReturnDataSet> list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, postRef);
			ps.setString(2, finEvent);
		}, new ReturnDataSetRowMapper());

		if (CollectionUtils.isEmpty(list)) {
			return list;
		}

		sortPostingsByDate(list);
		sortPostingsByLinkedTranId(list);
		sortPostingsByTransOrder(list);

		return list;
	}

	@Override
	public List<ReturnDataSet> getDisbursementPostings(long finID) {
		StringBuilder sql = getSelectQuery();
		sql.append(" Where FinReference = ? AND FinEvent in (?, ?)");

		logger.debug(Literal.SQL + sql.toString());
		List<ReturnDataSet> list = null;

		list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, finID);
			ps.setString(2, AccountingEvent.DISBINS);
			ps.setString(3, AccountingEvent.INSPAY);
		}, new ReturnDataSetRowMapper());

		if (CollectionUtils.isEmpty(list)) {
			return list;
		}

		sortPostingsByDate(list);
		sortPostingsByLinkedTranId(list);
		sortPostingsByTransOrder(list);

		return list;
	}

	private void setEntityCode(List<ReturnDataSet> dataSetList) {
		String entityCode = null;
		for (ReturnDataSet returnDataSet : dataSetList) {
			if (returnDataSet.getEntityCode() == null) {
				if (entityCode == null) {
					entityCode = SysParamUtil.getValueAsString("ENTITYCODE");
				}

				returnDataSet.setEntityCode(entityCode);
			}
		}
	}

	private StringBuilder getSelectQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LinkedTranId, Postref, PostingId, FinID, FinReference, FinEvent");
		sql.append(", PostDate, ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr");
		sql.append(", Account,  ShadowPosting, PostAmount, AmountType, PostStatus, ErrorId");
		sql.append(", ErrorMsg, AcCcy, TranOrderId, TransOrder, PostToSys, ExchangeRate");
		sql.append(", PostBranch, AppDate, AppValueDate, UserBranch, AccountType, PostAmountLcCcy, CustAppDate");
		sql.append(" From Postings");
		return sql;
	}

	private class ReturnDataSetRowMapper implements RowMapper<ReturnDataSet> {
		private ReturnDataSetRowMapper() {
			super();
		}

		@Override
		public ReturnDataSet mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReturnDataSet rd = new ReturnDataSet();

			rd.setLinkedTranId(rs.getLong("LinkedTranId"));
			rd.setPostref(rs.getString("Postref"));
			rd.setPostingId(rs.getString("PostingId"));
			rd.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
			rd.setFinReference(rs.getString("FinReference"));
			rd.setFinEvent(rs.getString("FinEvent"));
			rd.setPostDate(JdbcUtil.getDate(rs.getDate("PostDate")));
			rd.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
			rd.setTranCode(rs.getString("TranCode"));
			rd.setTranDesc(rs.getString("TranDesc"));
			rd.setRevTranCode(rs.getString("RevTranCode"));
			rd.setDrOrCr(rs.getString("DrOrCr"));
			rd.setAccount(rs.getString("Account"));
			rd.setShadowPosting(rs.getBoolean("ShadowPosting"));
			rd.setPostAmount(rs.getBigDecimal("PostAmount"));
			rd.setAmountType(rs.getString("AmountType"));
			rd.setPostStatus(rs.getString("PostStatus"));
			rd.setErrorId(rs.getString("ErrorId"));
			rd.setErrorMsg(rs.getString("ErrorMsg"));
			rd.setAcCcy(rs.getString("AcCcy"));
			rd.setTranOrderId(rs.getString("TranOrderId"));
			rd.setTransOrder(rs.getInt("TransOrder"));
			rd.setPostToSys(rs.getString("PostToSys"));
			rd.setExchangeRate(rs.getBigDecimal("ExchangeRate"));
			rd.setPostBranch(rs.getString("PostBranch"));
			rd.setAppDate(JdbcUtil.getDate(rs.getDate("AppDate")));
			rd.setAppValueDate(JdbcUtil.getDate(rs.getDate("AppValueDate")));
			rd.setUserBranch(rs.getString("UserBranch"));
			rd.setAccountType(rs.getString("AccountType"));
			rd.setPostAmountLcCcy(rs.getBigDecimal("PostAmountLcCcy"));
			rd.setCustAppDate(rs.getDate("CustAppDate"));

			return rd;
		}

	}

	private static List<ReturnDataSet> sortPostingsByDate(List<ReturnDataSet> postings) {
		if (CollectionUtils.isNotEmpty(postings)) {
			Collections.sort(postings, new Comparator<ReturnDataSet>() {
				@Override
				public int compare(ReturnDataSet detail1, ReturnDataSet detail2) {
					return DateUtility.compare(detail1.getValueDate(), detail2.getValueDate());
				}
			});
		}

		return postings;
	}

	private static List<ReturnDataSet> sortPostingsByLinkedTranId(List<ReturnDataSet> postings) {
		if (CollectionUtils.isNotEmpty(postings)) {
			Collections.sort(postings, new Comparator<ReturnDataSet>() {
				@Override
				public int compare(ReturnDataSet detail1, ReturnDataSet detail2) {
					return Long.compare(detail1.getLinkedTranId(), detail2.getLinkedTranId());
				}
			});
		}

		return postings;
	}

	private static List<ReturnDataSet> sortPostingsByTransOrder(List<ReturnDataSet> postings) {
		if (CollectionUtils.isNotEmpty(postings)) {
			Collections.sort(postings, new Comparator<ReturnDataSet>() {
				@Override
				public int compare(ReturnDataSet detail1, ReturnDataSet detail2) {
					return Long.compare(detail1.getTransOrder(), detail2.getTransOrder());
				}
			});
		}

		return postings;
	}

}
