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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  PostingsDAOImpl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  07-02-2012    
 *                                                                  
 * Modified Date    :  07-02-2012    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-02-2012       PENNANT TECHONOLOGIES	    0.1                                         * 
 * 02-06-2018		Satish						1.0			Fix for the posting reversal values mismatch                                                                                         * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.rulefactory.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.account.dao.StagePostingDAO;

/**
 * DAO methods implementation for the <b>ReturnDataSet model</b> class.<br>
 */
public class PostingsDAOImpl extends SequenceDao<ReturnDataSet> implements PostingsDAO {
	private static Logger logger = LogManager.getLogger(PostingsDAOImpl.class);

	private StagePostingDAO stagePostingDAO;

	public PostingsDAOImpl() {
		super();
	}

	/**
	 * Fetch ReturnDataSet details by finReference and TransId
	 * 
	 * @param tranIdList
	 * @param finReference
	 * 
	 * @return List<ReturnDataSet>
	 */

	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(List<Long> tranIdList, String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LinkedTranId", tranIdList);
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent, PostDate,ValueDate,TranCode, ");
		selectSql.append(
				" TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting, PostAmount,AmountType,PostStatus,ErrorId, ");
		selectSql.append(
				" ErrorMsg, AcCcy, TranOrderId, PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings ");
		selectSql.append(" Where FinReference =:FinReference AND  LinkedTranId  IN(:LinkedTranId) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		List<ReturnDataSet> postings = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return postings;
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,
			String postingGroupBy, String type) {
		logger.debug("Entering");

		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(finReference);
		dataSet.setFinEvent(finEvent);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT ValueDate,PostDate, AppDate, AppValueDate, TranCode,RevTranCode, TranDesc, RevTranCode, DrOrCr, Account, PostAmount, ");
		selectSql.append(" FinEvent, AcCcy, PostBranch, UserBranch ");

		if (StringUtils.isNotBlank(type)) {
			selectSql.append(", LovDescEventCodeName");
		} else {
			selectSql.append(", T2.AEEventCodeDesc LovDescEventCodeName");
		}

		selectSql.append(" FROM Postings");
		if (StringUtils.isBlank(type)) {
			selectSql.append(" T1 INNER JOIN BMTAEEvents T2 ON T1.FinEvent = T2.AEEventCode");
		}
		if (StringUtils.isNotBlank(type)) {
			selectSql.append(type);
		}
		selectSql.append(" Where FinReference =:FinReference AND FinEvent IN (" + finEvent + ")");
		if (!showZeroBal) {
			selectSql.append(" AND PostAmount != 0");
		}

		if (StringUtils.equals(PennantConstants.EVENTBASE, postingGroupBy)) {
			selectSql.append(" ORDER BY FinEvent, LinkedTranID ");
		} else if (StringUtils.equals(PennantConstants.POSTDATE, postingGroupBy)) {
			selectSql.append(" ORDER BY PostDate, LinkedTranID ");
		} else if (StringUtils.equals(PennantConstants.ACCNO, postingGroupBy)) {
			selectSql.append(" ORDER BY Account, LinkedTranID ");
		} else {
			selectSql.append(" ORDER BY ValueDate, LinkedTranID ");
		}
		selectSql.append(" , TransOrder ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.LinkedTranId, T1.Postref, T1.PostingId, T1.FinReference, T1.FinEvent");
		sql.append(", T1.PostDate, T1.ValueDate, T1.TranCode, T1.TranDesc, T1.RevTranCode, T1.DrOrCr");
		sql.append(", T1.Account,  T1.ShadowPosting, T1.PostAmount, T1.AmountType, T1.PostStatus, T1.ErrorId");
		sql.append(", T1.ErrorMsg, T1.AcCcy, T1.TranOrderId, T1.TransOrder, T1.PostToSys, T1.ExchangeRate");
		sql.append(", T1.PostBranch, T1.AppDate, T1.AppValueDate, T1.UserBranch, T1.AccountType");
		sql.append(" From Postings T1");
		sql.append(" Where LinkedTranId = ?");
		sql.append(" Order By T1.LinkedTranId, T1.TranOrderId");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, linkedTranId);
		}, (rs, rowNum) -> {
			ReturnDataSet rd = new ReturnDataSet();

			rd.setLinkedTranId(rs.getLong("LinkedTranId"));
			rd.setPostref(rs.getString("Postref"));
			rd.setPostingId(rs.getString("PostingId"));
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

			return rd;
		});
	}

	@Override
	public List<ReturnDataSet> getPostingsByTransIdList(List<Long> tranIdList) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LinkedTranId", tranIdList);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent, PostDate,ValueDate,TranCode, ");
		selectSql.append(
				" TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting, PostAmount,AmountType,PostStatus,ErrorId, ");
		selectSql.append(
				" ErrorMsg, AcCcy, TranOrderId, PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where LinkedTranId  IN(:LinkedTranId) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		List<ReturnDataSet> postings = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return postings;
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(Long postref) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Postref", postref);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent, PostDate,ValueDate,TranCode, ");
		selectSql.append(
				" TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting, PostAmount,AmountType,PostStatus,ErrorId, ");
		selectSql.append(
				" ErrorMsg, AcCcy, TranOrderId, PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where Postref  =:Postref) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		List<ReturnDataSet> postings = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return postings;
	}

	@Override
	public long saveHeader(ReturnDataSet dataSet, String status, String type) {
		logger.debug("Entering");

		dataSet.setPostStatus(status);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into EODPostingsHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LinkedTranId, finReference, FinEvent, ValueDate, PostStatus) ");
		insertSql.append(" Values(:LinkedTranId, :finReference, :FinEvent, :ValueDate, :PostStatus)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return dataSet.getLinkedTranId();
	}

	@Override
	public void saveBatch(List<ReturnDataSet> dataSetList, boolean isNewTranID) {
		setEntityCode(dataSetList);

		StringBuilder insertSql = new StringBuilder("insert into");
		insertSql.append(" Postings");
		insertSql.append(" (LinkedTranId, Postref, PostingId, finReference, FinEvent, PostDate");
		insertSql.append(", ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, Account, ShadowPosting");
		insertSql.append(", PostAmountLcCcy, TransOrder, DerivedTranOrder, PostToSys, ExchangeRate");
		insertSql.append(", PostAmount, AmountType, PostStatus, ErrorId, ErrorMsg, AcCcy, TranOrderId");
		insertSql.append(", PostBranch, AppDate, AppValueDate, UserBranch, PostCategory, CustAppDate");
		insertSql.append(", AccountType, OldLinkedTranId, EntityCode");
		insertSql.append(") values(");
		insertSql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		insertSql.append(", ?, ?, ?, ?, ?, ?, ?, ?");
		insertSql.append(")");

		logger.trace(Literal.SQL + insertSql.toString());

		try {
			this.jdbcOperations.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ReturnDataSet pstngs = dataSetList.get(i);

					int index = 1;

					ps.setLong(index++, pstngs.getLinkedTranId());
					ps.setString(index++, pstngs.getPostref());
					ps.setString(index++, pstngs.getPostingId());
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

			if (isNewTranID) {
				Set<Long> linkedTransactions = new HashSet<>();
				for (ReturnDataSet dataSet : dataSetList) {
					linkedTransactions.add(dataSet.getLinkedTranId());
				}

				if (!linkedTransactions.isEmpty()) {
					stagePostingDAO.saveLinkedTrnasactions(linkedTransactions);
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

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

	//FIXME CH to be changed to Batch Update
	@Override
	public void updateStatusByLinkedTranId(long linkedTranId, String postStatus) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("LinkedTranId", linkedTranId);
		paramSource.addValue("PostStatus", postStatus);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Update Postings SET ");
		insertSql.append(" PostStatus = :PostStatus where LinkedTranId = :LinkedTranId");

		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), paramSource);
	}

	//FIXME CH to be changed to Batch Update
	@Override
	public void updateStatusByFinRef(String finReference, String postStatus) {
		logger.debug("Entering");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);
		paramSource.addValue("PostStatus", postStatus);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Update Postings Set ");
		insertSql.append(" PostStatus = :PostStatus where finReference = :finReference");

		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), paramSource);
		logger.debug("Leaving");
	}

	/**
	 * Generate Linked Transaction ID
	 */
	public long getLinkedTransId() {
		return getNextValue("SeqPostings");
	}

	/**
	 * Generate Posting ID
	 */
	public long getPostingId() {
		return getNextValue("SeqPostingId");
	}

	@Override
	public void updateBatch(List<ReturnDataSet> dataSetList, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update Postings");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" set PostStatus=:PostStatus where Postref=:Postref");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(dataSetList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteAll(String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM Postings").append(type);

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = null;
		try {
			beanParameters = new BeanPropertySqlParameterSource("");
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);

		} finally {
			beanParameters = null;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Fetching Posted Amount On Particular Finance Event
	 */
	@Override
	public BigDecimal getPostAmtByTranIdandEvent(String finReference, String finEvent, long linkedTranId) {
		logger.debug("Entering");

		BigDecimal totalPostAmount = BigDecimal.ZERO;

		ReturnDataSet set = new ReturnDataSet();
		set.setFinReference(finReference);
		set.setFinEvent(finEvent);
		set.setLinkedTranId(linkedTranId);

		StringBuilder selectSql = new StringBuilder(" SELECT SUM(PostAmount) ");
		selectSql.append(" FROM Postings");
		selectSql.append(" WHERE FinReference=:FinReference AND LinkedTranId=:LinkedTranId AND FinEvent =:FinEvent ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(set);

		try {
			totalPostAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			totalPostAmount = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return totalPostAmount;
	}

	/**
	 * Method for Fetching Posted Amount On Particular Finance Event
	 */
	@Override
	public List<Long> getLinkTranIdByRef(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		List<String> eventCodeList = new ArrayList<String>();
		eventCodeList.add("ADDDBSP");
		eventCodeList.add("ADDDBSF");
		eventCodeList.add("ADDDBSN");
		eventCodeList.add("STAGE");
		source.addValue("FinEvent", eventCodeList);

		StringBuilder selectSql = new StringBuilder(" SELECT DISTINCT LinkedTranId ");
		selectSql.append(" FROM Postings");
		selectSql.append(" WHERE FinReference=:FinReference AND FinEvent IN( :FinEvent ) ");

		logger.debug("selectSql: " + selectSql.toString());
		List<Long> linkedTranIdList = null;
		try {
			linkedTranIdList = this.jdbcTemplate.queryForList(selectSql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			linkedTranIdList = null;
		}

		logger.debug("Leaving");
		return linkedTranIdList;
	}

	@Override
	public List<ReturnDataSet> getPostingsbyFinanceBranch(String branchCode) {
		logger.debug("Entering");

		MapSqlParameterSource mSource = new MapSqlParameterSource();
		mSource.addValue("FinBranch", branchCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append(" Select * From ( SELECT T1.LinkedTranId,T1.Postref,T1.PostingId,T1.finReference,T1.FinEvent,");
		selectSql.append(
				" T1.PostDate,T1.ValueDate,T1.TranCode,T1.TranDesc,T1.RevTranCode,T1.DrOrCr,T1.Account, T1.ShadowPosting,");
		selectSql
				.append(" T1.PostAmount,T1.AmountType,T1.PostStatus,T1.ErrorId,T1.ErrorMsg, T1.AcCcy, T1.TranOrderId,");
		selectSql.append(" T1.PostToSys,T1.ExchangeRate, UserBranch  ");
		selectSql.append(" FROM Postings T1 INNER JOIN  FinanceMain_Temp T2 on T1.FinReference = T2.FinReference");
		selectSql.append(" Where T2.FinBranch = :FinBranch");
		selectSql.append(" UNION ALL ");
		selectSql.append(" SELECT T1.LinkedTranId,T1.Postref,T1.PostingId,T1.finReference,T1.FinEvent,");
		selectSql.append(
				" T1.PostDate,T1.ValueDate,T1.TranCode,T1.TranDesc,T1.RevTranCode,T1.DrOrCr,T1.Account, T1.ShadowPosting,");
		selectSql
				.append(" T1.PostAmount,T1.AmountType,T1.PostStatus,T1.ErrorId,T1.ErrorMsg, T1.AcCcy, T1.TranOrderId,");
		selectSql.append(" T1.PostToSys,T1.ExchangeRate, UserBranch  ");
		selectSql.append(" FROM Postings T1 INNER JOIN FinanceMain T2 on T1.FinReference = T2.FinReference ");
		selectSql
				.append(" Where NOT EXISTS (SELECT 1 FROM FinanceMain_Temp WHERE FinReference = T2.FinReference) and ");
		selectSql.append(" T2.FinBranch = :FinBranch)T1 order by T1.Account,T1.finReference,T1.TranCode ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), mSource, typeRowMapper);
	}

	/*
	 * Method to get the Trancode and DrOrCr to reversal the accounting in VasCancellation
	 */

	@Override
	public List<ReturnDataSet> getPostingsByVasref(String finReference, String[] finEvent) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent,");
		selectSql.append(" PostDate,ValueDate,TranCode,TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting,");
		selectSql.append(" PostAmount,AmountType,PostStatus,ErrorId,ErrorMsg, AcCcy, TranOrderId,");
		selectSql.append(" PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where FinReference =:FinReference And finEvent in(:finEvent) ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("finEvent", Arrays.asList(finEvent));

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/*
	 * Method to get the Posting Details By FinRefernce
	 */

	@Override
	public List<ReturnDataSet> getPostingsByFinRef(String finReference, boolean reqReversals) {
		logger.debug("Entering");

		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(finReference);
		dataSet.setPostStatus(AccountConstants.POSTINGS_SUCCESS);

		StringBuilder selectSql = new StringBuilder();
		//FIX version 1.0
		selectSql.append(
				" SELECT T1.LinkedTranId, T1.Postref, T1.PostingId, T1.finReference, T1.FinEvent, T1.PostAmountLcCcy, T1.CustAppDate,");
		selectSql.append(
				" T1.PostDate, T1.ValueDate, T1.TranCode, T1.TranDesc, T1.RevTranCode, T1.DrOrCr, T1.Account,  T1.ShadowPosting,");
		selectSql.append(
				" T1.PostAmount, T1.AmountType, T1.PostStatus, T1.ErrorId, T1.ErrorMsg, T1.AcCcy, T1.TranOrderId, T1.TransOrder,");
		selectSql.append(
				" T1.PostToSys, T1.ExchangeRate, T1.PostBranch, T1.AppDate, T1.AppValueDate, T1.UserBranch, T1.AccountType ");
		selectSql.append(" FROM Postings T1");
		selectSql.append(" Where FinReference =:FinReference and PostStatus = :PostStatus");
		if (!reqReversals) {
			selectSql.append(" and OldLinkedTranID = 0 ");
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.REPAY_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL)) {
			selectSql.append(" and T1.FinEvent != 'REPAY' ");
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.DISB_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL)) {
			selectSql.append(
					" and T1.LinkedTranId not in (Select LINKEDTRANID from FINADVANCEPAYMENTS Where STATUS in ('REJECTED','CANCELED') ");
			selectSql.append(" and FINREFERENCE= :FinReference and  T1.FinEvent = 'DISBINS') ");
		}

		selectSql.append(" Order By T1.LinkedTranId, T1.TranOrderId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		List<ReturnDataSet> returnDataSetList = new ArrayList<ReturnDataSet>();
		try {
			returnDataSetList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return returnDataSetList;
	}

	@Override
	public void updatePostCtg() {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PostCategory", AccountConstants.POSTING_CATEGORY_ACUPDATE);
		source.addValue("PostCategory_From", AccountConstants.POSTING_CATEGORY_EOD);

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update Postings");
		updateSql.append(" set PostCategory=:PostCategory WHERE postCategory=:PostCategory_From");
		logger.debug("updateSql: " + updateSql.toString());

		this.jdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(long postrRef) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LinkedTranId, Postref, PostingId, FinReference, FinEvent, PostAmountLcCcy, CustAppDate");
		sql.append(", PostDate, ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, Account, ShadowPosting");
		sql.append(", PostAmount, AmountType, PostStatus, ErrorId, ErrorMsg, AcCcy, TranOrderId, TransOrder");
		sql.append(", PostToSys, ExchangeRate, PostBranch, AppDate, AppValueDate, UserBranch, AccountType");
		sql.append(" from Postings");
		sql.append(" Where PostStatus = ? and Postref = ?");
		sql.append(" order by LinkedTranId, TranOrderId");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, AccountConstants.POSTINGS_SUCCESS);
			ps.setString(index++, String.valueOf(postrRef));
		}, (rs, rowNum) -> {
			ReturnDataSet p = new ReturnDataSet();

			p.setLinkedTranId(rs.getLong("LinkedTranId"));
			p.setPostref(rs.getString("Postref"));
			p.setPostingId(rs.getString("PostingId"));
			p.setFinReference(rs.getString("FinReference"));
			p.setFinEvent(rs.getString("FinEvent"));
			p.setPostAmountLcCcy(rs.getBigDecimal("PostAmountLcCcy"));
			p.setCustAppDate(rs.getTimestamp("CustAppDate"));
			p.setPostDate(rs.getTimestamp("PostDate"));
			p.setValueDate(rs.getTimestamp("ValueDate"));
			p.setTranCode(rs.getString("TranCode"));
			p.setTranDesc(rs.getString("TranDesc"));
			p.setRevTranCode(rs.getString("RevTranCode"));
			p.setDrOrCr(rs.getString("DrOrCr"));
			p.setAccount(rs.getString("Account"));
			p.setShadowPosting(rs.getBoolean("ShadowPosting"));
			p.setPostAmount(rs.getBigDecimal("PostAmount"));
			p.setAmountType(rs.getString("AmountType"));
			p.setPostStatus(rs.getString("PostStatus"));
			p.setErrorId(rs.getString("ErrorId"));
			p.setErrorMsg(rs.getString("ErrorMsg"));
			p.setAcCcy(rs.getString("AcCcy"));
			p.setTranOrderId(rs.getString("TranOrderId"));
			p.setTransOrder(rs.getInt("TransOrder"));
			p.setPostToSys(rs.getString("PostToSys"));
			p.setExchangeRate(rs.getBigDecimal("ExchangeRate"));
			p.setPostBranch(rs.getString("PostBranch"));
			p.setAppDate(rs.getTimestamp("AppDate"));
			p.setAppValueDate(rs.getTimestamp("AppValueDate"));
			p.setUserBranch(rs.getString("UserBranch"));
			p.setAccountType(rs.getString("AccountType"));

			return p;
		});

	}

	@Override
	public void updateStatusByPostRef(long postRef, String postStatus) {
		logger.debug("Entering");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("PostRef", String.valueOf(postRef));
		paramSource.addValue("PostStatus", postStatus);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Update Postings SET ");
		insertSql.append(" PostStatus = :PostStatus where PostRef = :PostRef");

		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), paramSource);
		logger.debug("Leaving");

	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRef(String finReference) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent, PostDate,ValueDate,TranCode, ");
		selectSql.append(
				" TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting, PostAmount,AmountType,PostStatus,ErrorId, ");
		selectSql.append(
				" ErrorMsg, AcCcy, TranOrderId,PostBranch, AppDate, AppValueDate, UserBranch, 'Insurance Payment' as LovDescEventCodeName ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where LInkedTraniD in (Select linkedTranid from InsurancePaymentInstructions where id in (");
		selectSql.append(" select paymentinsid from vasrecording where PRIMARYlinkref = :finReference) )");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		try {
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent) {
		logger.debug("Entering");

		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(finReference);
		dataSet.setFinEvent(finEvent);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT ValueDate,PostDate, AppDate, AppValueDate, TranCode,RevTranCode, TranDesc, RevTranCode, DrOrCr, Account, PostAmount, ");
		selectSql.append(" FinEvent, AcCcy, PostBranch, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where FinReference =:FinReference AND FinEvent=:FinEvent");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = BeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");

		try {
			return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public List<ReturnDataSet> getPostingsByLinkedTranId(List<Long> linkedTranId, boolean reversal) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select * from (");
		sql.append("Select LinkedTranId, Postref, PostingId, FinReference, FinEvent, PostDate, ValueDate");
		sql.append(", TranCode, TranDesc, RevTranCode, DrOrCr, Account, ShadowPosting, PostAmount");
		sql.append(", AmountType, PostStatus, ErrorId, ErrorMsg, AcCcy, TranOrderId, PostToSys, ExchangeRate");
		sql.append(", PostBranch, AppDate, AppValueDate, UserBranch");
		sql.append(" from Postings");
		sql.append(" Where LinkedTranId in (?) ");
		if (reversal) {
			sql.append(" union all");
			sql.append(" Select LinkedTranId, Postref, PostingId, FinReference, FinEvent, PostDate, ValueDate");
			sql.append(", TranCode,  TranDesc, RevTranCode, DrOrCr, Account, ShadowPosting, PostAmount");
			sql.append(", AmountType, PostStatus, ErrorId, ErrorMsg");
			sql.append(", AcCcy, TranOrderId, PostToSys, ExchangeRate, PostBranch");
			sql.append(", AppDate, AppValueDate, UserBranch");
			sql.append(" FROM Postings");
			sql.append(" Where OldLinkedTranId  in (?)");
		}
		sql.append(")Rev");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					for (Long o : linkedTranId) {
						ps.setObject(index, o);
						if (reversal) {
							index++;
							ps.setObject(index, o);
						}
					}

				}
			}, new RowMapper<ReturnDataSet>() {
				@Override
				public ReturnDataSet mapRow(ResultSet rs, int rowNum) throws SQLException {
					ReturnDataSet pst = new ReturnDataSet();

					pst.setLinkedTranId(rs.getLong("LinkedTranId"));
					pst.setPostref(rs.getString("Postref"));
					pst.setPostingId(rs.getString("PostingId"));
					pst.setFinReference(rs.getString("FinReference"));
					pst.setFinEvent(rs.getString("FinEvent"));
					pst.setPostDate(rs.getTimestamp("PostDate"));
					pst.setValueDate(rs.getTimestamp("ValueDate"));
					pst.setTranCode(rs.getString("TranCode"));
					pst.setTranDesc(rs.getString("TranDesc"));
					pst.setRevTranCode(rs.getString("RevTranCode"));
					pst.setDrOrCr(rs.getString("DrOrCr"));
					pst.setAccount(rs.getString("Account"));
					pst.setShadowPosting(rs.getBoolean("ShadowPosting"));
					pst.setPostAmount(rs.getBigDecimal("PostAmount"));
					pst.setAmountType(rs.getString("AmountType"));
					pst.setPostStatus(rs.getString("PostStatus"));
					pst.setErrorId(rs.getString("ErrorId"));
					pst.setErrorMsg(rs.getString("ErrorMsg"));
					pst.setAcCcy(rs.getString("AcCcy"));
					pst.setTranOrderId(rs.getString("TranOrderId"));
					pst.setPostToSys(rs.getString("PostToSys"));
					pst.setExchangeRate(rs.getBigDecimal("ExchangeRate"));
					pst.setPostBranch(rs.getString("PostBranch"));
					pst.setAppDate(rs.getTimestamp("AppDate"));
					pst.setAppValueDate(rs.getTimestamp("AppValueDate"));
					pst.setUserBranch(rs.getString("UserBranch"));

					return pst;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Autowired
	public void setStagePostingDAO(StagePostingDAO stagePostingDAO) {
		this.stagePostingDAO = stagePostingDAO;
	}

	@Override
	public List<ReturnDataSet> getPostings(String postRef, String finEvent) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ValueDate, PostDate, AppDate, AppValueDate, TranCode, ");
		sql.append(" RevTranCode, TranDesc, DrOrCr, Account, PostAmount, ");
		sql.append(" FinEvent, AcCcy, PostBranch, UserBranch, PostStatus, TranOrderId ");
		sql.append(" From Postings");
		sql.append(" Where Postref = ? AND FinEvent IN (?) ");

		logger.trace(Literal.SQL + sql.toString());
		List<ReturnDataSet> list = null;

		list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, postRef);
			ps.setString(2, finEvent);
		}, (rs, i) -> {
			ReturnDataSet rds = new ReturnDataSet();

			rds.setValueDate(rs.getTimestamp("ValueDate"));
			rds.setPostDate(rs.getTimestamp("PostDate"));
			rds.setAppDate(rs.getTimestamp("AppDate"));
			rds.setAppValueDate(rs.getTimestamp("AppValueDate"));
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
			rds.setPostStatus(rs.getString("PostStatus"));
			rds.setTranOrderId(rs.getString("TranOrderId"));
			rds.setValueDate(rs.getTimestamp("ValueDate"));

			return rds;
		});

		if (CollectionUtils.isEmpty(list)) {
			logger.info(Literal.LEAVING);
			return list;
		}

		sortPostingsByDate(list);
		sortPostingsByLinkedTranId(list);
		sortPostingsByTransOrder(list);

		return list;
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
