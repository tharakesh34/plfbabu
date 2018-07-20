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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>ReturnDataSet model</b> class.<br>
 */
public class PostingsDAOImpl extends SequenceDao<ReturnDataSet> implements PostingsDAO {
	private static Logger logger = Logger.getLogger(PostingsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static NextidviewDAO nextidviewDAO;
	
	public PostingsDAOImpl() {
		super();
	}
	
	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,String postingGroupBy) {
		logger.debug("Entering");
		
		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(finReference);
		dataSet.setFinEvent(finEvent);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ValueDate,PostDate, AppDate, AppValueDate, TranCode,RevTranCode, TranDesc, RevTranCode, DrOrCr, Account, PostAmount, ");
		selectSql.append(" FinEvent, LovDescEventCodeName, AcCcy, PostBranch, UserBranch ");
		selectSql.append(" FROM Postings_View");
		selectSql.append(" Where FinReference =:FinReference AND FinEvent IN ("+finEvent+")");
		if(!showZeroBal){
			selectSql.append(" AND PostAmount != 0");
		}
		
		if(StringUtils.equals(PennantConstants.EVENTBASE, postingGroupBy)){
			selectSql.append(" ORDER BY FinEvent, LinkedTranID ");
		}
		else if(StringUtils.equals(PennantConstants.POSTDATE, postingGroupBy)){
			selectSql.append(" ORDER BY PostDate, LinkedTranID ");
		}
		else if(StringUtils.equals(PennantConstants.ACCNO, postingGroupBy)){
			selectSql.append(" ORDER BY Account, LinkedTranID ");
		}else{
			selectSql.append(" ORDER BY ValueDate, LinkedTranID ");
		}
		selectSql.append(" , TransOrder ");
	
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId) {
		logger.debug("Entering");
		
		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setLinkedTranId(linkedTranId);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT T1.LinkedTranId, T1.Postref, T1.PostingId, T1.finReference, T1.FinEvent,");
		selectSql.append(" T1.PostDate, T1.ValueDate, T1.TranCode, T1.TranDesc, T1.RevTranCode, T1.DrOrCr, T1.Account,  T1.ShadowPosting,");
		selectSql.append(" T1.PostAmount, T1.AmountType, T1.PostStatus, T1.ErrorId, T1.ErrorMsg, T1.AcCcy, T1.TranOrderId, T1.TransOrder,");
		selectSql.append(" T1.PostToSys, T1.ExchangeRate, T1.PostBranch, T1.AppDate, T1.AppValueDate, T1.UserBranch, T1.AccountType ");
		selectSql.append(" FROM Postings T1");
		//selectSql.append(" Left join Accounts T2 on Accountid = Account ");
		selectSql.append(" Where LinkedTranId =:LinkedTranId");
		selectSql.append(" Order By T1.LinkedTranId, T1.TranOrderId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public List<ReturnDataSet> getPostingsByTransIdList(List<Long> tranIdList) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LinkedTranId", tranIdList);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent, PostDate,ValueDate,TranCode, ");
		selectSql.append(" TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting, PostAmount,AmountType,PostStatus,ErrorId, ");
		selectSql.append(" ErrorMsg, AcCcy, TranOrderId, PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where LinkedTranId  IN(:LinkedTranId) ");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		List<ReturnDataSet> postings = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
		selectSql.append(" TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting, PostAmount,AmountType,PostStatus,ErrorId, ");
		selectSql.append(" ErrorMsg, AcCcy, TranOrderId, PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where Postref  =:Postref) ");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		List<ReturnDataSet> postings = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return dataSet.getLinkedTranId();
	}
	
	
	@Override
	public void saveBatch(List<ReturnDataSet> dataSetList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into Postings");
		insertSql.append(" (LinkedTranId, Postref, PostingId, finReference, FinEvent,");
		insertSql.append(" PostDate, ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, Account,ShadowPosting,");
		insertSql.append(" PostAmountLcCcy, TransOrder, DerivedTranOrder,PostToSys,ExchangeRate, ");
		insertSql.append(" PostAmount,AmountType, PostStatus, ErrorId, ErrorMsg, AcCcy, TranOrderId,");
		insertSql.append(" PostBranch, AppDate, AppValueDate, UserBranch, PostCategory , CustAppDate, AccountType, OldLinkedTranId, EntityCode )");
		insertSql.append(" Values(:LinkedTranId, :Postref, :PostingId, :finReference, :FinEvent,");
		insertSql.append(" :PostDate, :ValueDate, :TranCode, :TranDesc, :RevTranCode, :DrOrCr, :Account,");
		insertSql.append(" :ShadowPosting,:PostAmountLcCcy, :TransOrder, :DerivedTranOrder,:PostToSys,");
		insertSql.append(" :ExchangeRate,:PostAmount, :AmountType, :PostStatus, :ErrorId, :ErrorMsg, ");
		insertSql.append(" :AcCcy,:TranOrderId,:PostBranch, :AppDate, :AppValueDate, :UserBranch, ");
		insertSql.append(" :PostCategory , :CustAppDate, :AccountType, :OldLinkedTranId, :EntityCode)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(dataSetList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	//FIXME CH to be changed to Batch Update
		@Override
	public void updateStatusByLinkedTranId(long linkedTranId, String postStatus) {
		logger.debug("Entering");
		
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("LinkedTranId", linkedTranId);
		paramSource.addValue("PostStatus", postStatus);
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Update Postings SET ");
		insertSql.append(" PostStatus = :PostStatus where LinkedTranId = :LinkedTranId");
		
		logger.debug("insertSql: " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), paramSource);
		logger.debug("Leaving");
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
			this.namedParameterJdbcTemplate.update(insertSql.toString(), paramSource);
			logger.debug("Leaving");
		}

	/**
	 * Generate Linked Transaction ID
	 */
	public long getLinkedTransId() {
		logger.debug("Entering");

		long count = 0;
		try {
			count = nextidviewDAO.getNextId("SeqPostings");
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving" + count);
		return count;
	}
	
	/**
	 * Generate Posting ID
	 */
	public long getPostingId() {
		logger.debug("Entering");

		long count = 0;
		try {
			count = nextidviewDAO.getNextId("SeqPostingId");
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving" + count);
		return count;
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
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteAll(String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM Postings").append(type);

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = null;
		try{
			beanParameters = new BeanPropertySqlParameterSource("");
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		}catch(DataAccessException e){
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
    public BigDecimal getPostAmtByTranIdandEvent(String finReference, String finEvent , long linkedTranId) {
		logger.debug("Entering");
		
		BigDecimal totalPostAmount = BigDecimal.ZERO;
		
		ReturnDataSet set = new ReturnDataSet();
		set.setFinReference(finReference);
		set.setFinEvent(finEvent);
		set.setLinkedTranId(linkedTranId);

		StringBuilder selectSql = new StringBuilder(" SELECT SUM(PostAmount) " );
		selectSql.append(" FROM Postings");
		selectSql.append(" WHERE FinReference=:FinReference AND LinkedTranId=:LinkedTranId AND FinEvent =:FinEvent " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(set);
		
		try {
			totalPostAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
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

		StringBuilder selectSql = new StringBuilder(" SELECT DISTINCT LinkedTranId " );
		selectSql.append(" FROM Postings");
		selectSql.append(" WHERE FinReference=:FinReference AND FinEvent IN( :FinEvent ) " );

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Long> mapper = ParameterizedBeanPropertyRowMapper.newInstance(Long.class);
		List<Long> linkedTranIdList = null;
		try {
			linkedTranIdList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, mapper);
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
		selectSql.append(" Select * From ( SELECT T1.LinkedTranId,T1.Postref,T1.PostingId,T1.finReference,T1.FinEvent,");
		selectSql.append(" T1.PostDate,T1.ValueDate,T1.TranCode,T1.TranDesc,T1.RevTranCode,T1.DrOrCr,T1.Account, T1.ShadowPosting,");
		selectSql.append(" T1.PostAmount,T1.AmountType,T1.PostStatus,T1.ErrorId,T1.ErrorMsg, T1.AcCcy, T1.TranOrderId,");
		selectSql.append(" T1.PostToSys,T1.ExchangeRate, UserBranch  ");
		selectSql.append(" FROM Postings T1 INNER JOIN  FinanceMain_Temp T2 on T1.FinReference = T2.FinReference");
		selectSql.append(" Where T2.FinBranch = :FinBranch");
		selectSql.append(" UNION ALL ");
		selectSql.append(" SELECT T1.LinkedTranId,T1.Postref,T1.PostingId,T1.finReference,T1.FinEvent,");
		selectSql.append(" T1.PostDate,T1.ValueDate,T1.TranCode,T1.TranDesc,T1.RevTranCode,T1.DrOrCr,T1.Account, T1.ShadowPosting,");
		selectSql.append(" T1.PostAmount,T1.AmountType,T1.PostStatus,T1.ErrorId,T1.ErrorMsg, T1.AcCcy, T1.TranOrderId,");
		selectSql.append(" T1.PostToSys,T1.ExchangeRate, UserBranch  ");
		selectSql.append(" FROM Postings T1 INNER JOIN FinanceMain T2 on T1.FinReference = T2.FinReference ");
		selectSql.append(" Where NOT EXISTS (SELECT 1 FROM FinanceMain_Temp WHERE FinReference = T2.FinReference) and ");
		selectSql.append(" T2.FinBranch = :FinBranch)T1 order by T1.Account,T1.finReference,T1.TranCode ");
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), mSource, typeRowMapper);
	}
	
	/*
	 * Method to get the Trancode and DrOrCr to reversal the accounting in VasCancellation
	 */

	@Override
	public List<ReturnDataSet> getPostingsByVasref(String finReference,String finEvent) {
		logger.debug("Entering");

		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(finReference);
		dataSet.setFinEvent(finEvent);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent,");
		selectSql.append(" PostDate,ValueDate,TranCode,TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting,");
		selectSql.append(" PostAmount,AmountType,PostStatus,ErrorId,ErrorMsg, AcCcy, TranOrderId,");
		selectSql.append(" PostToSys,ExchangeRate,PostBranch, AppDate, AppValueDate, UserBranch ");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where FinReference =:FinReference And finEvent=:finEvent ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		selectSql.append(" SELECT T1.LinkedTranId, T1.Postref, T1.PostingId, T1.finReference, T1.FinEvent, T1.PostAmountLcCcy, T1.CustAppDate,");
		selectSql.append(" T1.PostDate, T1.ValueDate, T1.TranCode, T1.TranDesc, T1.RevTranCode, T1.DrOrCr, T1.Account,  T1.ShadowPosting,");
		selectSql.append(" T1.PostAmount, T1.AmountType, T1.PostStatus, T1.ErrorId, T1.ErrorMsg, T1.AcCcy, T1.TranOrderId, T1.TransOrder,");
		selectSql.append(" T1.PostToSys, T1.ExchangeRate, T1.PostBranch, T1.AppDate, T1.AppValueDate, T1.UserBranch, T1.AccountType ");
		selectSql.append(" FROM Postings T1");
		//FIX version 1.0
		//selectSql.append(" Left join Accounts T2 on Accountid = Account ");
		selectSql.append(" Where FinReference =:FinReference and PostStatus = :PostStatus");
		if(!reqReversals){
			selectSql.append(" and OldLinkedTranID = 0 ");
		}
		selectSql.append(" Order By T1.LinkedTranId, T1.TranOrderId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		List<ReturnDataSet> returnDataSetList = new ArrayList<ReturnDataSet>();
		try {
			returnDataSetList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
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
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("PostCategory", AccountConstants.POSTING_CATEGORY_ACUPDATE);
		source.addValue("PostCategory_From", AccountConstants.POSTING_CATEGORY_EOD);
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update Postings");
		updateSql.append(" set PostCategory=:PostCategory WHERE postCategory=:PostCategory_From");
		logger.debug("updateSql: " + updateSql.toString());
		
		this.namedParameterJdbcTemplate.update(updateSql.toString(),source);
		logger.debug("Leaving");
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public static void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		PostingsDAOImpl.nextidviewDAO = nextidviewDAO;
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(long postrRef) {

		logger.debug("Entering");
		
		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setPostref(String.valueOf(postrRef));
		dataSet.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
		
		StringBuilder selectSql = new StringBuilder();
		//FIX version 1.0
		selectSql.append(" SELECT T1.LinkedTranId, T1.Postref, T1.PostingId, T1.finReference, T1.FinEvent, T1.PostAmountLcCcy, T1.CustAppDate, ");
		selectSql.append(" T1.PostDate, T1.ValueDate, T1.TranCode, T1.TranDesc, T1.RevTranCode, T1.DrOrCr, T1.Account,  T1.ShadowPosting,");
		selectSql.append(" T1.PostAmount, T1.AmountType, T1.PostStatus, T1.ErrorId, T1.ErrorMsg, T1.AcCcy, T1.TranOrderId, T1.TransOrder,");
		selectSql.append(" T1.PostToSys, T1.ExchangeRate, T1.PostBranch, T1.AppDate, T1.AppValueDate, T1.UserBranch, T1.AccountType ");
		selectSql.append(" FROM Postings T1");
		//FIX version 1.0
		//selectSql.append(" Left join Accounts T2 on Accountid = Account ");
		selectSql.append(" Where PostStatus = :PostStatus and Postref =:Postref");
		selectSql.append(" Order By T1.LinkedTranId, T1.TranOrderId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	
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
		this.namedParameterJdbcTemplate.update(insertSql.toString(), paramSource);
		logger.debug("Leaving");
		
	}
}
