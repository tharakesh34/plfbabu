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
 * 07-02-2012       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

/**
 * DAO methods implementation for the <b>ReturnDataSet model</b> class.<br>
 */
public class PostingsDAOImpl extends BasisCodeDAO<ReturnDataSet> implements PostingsDAO {

	private static Logger logger = Logger.getLogger(PostingsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public List<ReturnDataSet> getPostingsByFinReference(String id, String type) {
		logger.debug("Entering");
		
		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent,");
		selectSql.append(" PostDate,ValueDate,TranCode, TranDesc, RevTranCode,DrOrCr,Account, ShadowPosting,");
		selectSql.append(" PostAmount,AmountType,PostStatus,ErrorId,ErrorMsg,HostAccountNumber");
		if(type.contains("View")){
			selectSql.append(" ,LovDescEventCodeName " );
		}
		selectSql.append(" FROM Postings");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where finReference =:finReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal) {
		logger.debug("Entering");
		
		ReturnDataSet dataSet = new ReturnDataSet();
		dataSet.setFinReference(finReference);
		dataSet.setFinEvent(finEvent);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ValueDate,TranCode,TranDesc,RevTranCode,DrOrCr,Account, PostAmount, ");
		selectSql.append(" FinEvent, LovDescEventCodeName");
		selectSql.append(" FROM Postings_View");
		selectSql.append(" Where finReference =:finReference AND FinEvent IN ("+finEvent+")");
		if(!showZeroBal){
			selectSql.append(" AND PostAmount > 0");
		}
		
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
		selectSql.append(" SELECT LinkedTranId,Postref,PostingId,finReference,FinEvent,");
		selectSql.append(" PostDate,ValueDate,TranCode,TranDesc,RevTranCode,DrOrCr,Account, ShadowPosting,");
		selectSql.append(" PostAmount,AmountType,PostStatus,ErrorId,ErrorMsg,HostAccountNumber");
		selectSql.append(" FROM Postings");
		selectSql.append(" Where LinkedTranId =:LinkedTranId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		RowMapper<ReturnDataSet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public long save(ReturnDataSet dataSet,  String type) {
		logger.debug("Entering");
		getLinkedTransId(dataSet);	
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into Postings");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LinkedTranId, Postref, PostingId, finReference, FinEvent,");
		insertSql.append(" PostDate, ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, Account,ShadowPosting,");
		insertSql.append(" PostAmount, AmountType, PostStatus, ErrorId, ErrorMsg, HostAccountNumber)");
		insertSql.append(" Values(:LinkedTranId, :Postref, :PostingId, :finReference, :FinEvent,");
		insertSql.append(" :PostDate, :ValueDate, :TranCode, :TranDesc, :RevTranCode, :DrOrCr, :Account, :ShadowPosting,");
		insertSql.append(" :PostAmount, :AmountType, :PostStatus, :ErrorId, :ErrorMsg, :HostAccountNumber)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dataSet);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return dataSet.getLinkedTranId();
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
	
	/**
	 * Method for saving List of Postings in EndOfDay Process
	 * @param dataSetList
	 * @param type
	 * @param isEODPostings
	 */
	@Override
	public void saveEODBatch(List<ReturnDataSet> dataSetList, String type){
		logger.debug("Entering");
		saveBatch(dataSetList, "", true);
		saveBatch(dataSetList, "", false);
		logger.debug("Leaving");
	}
	
	@Override
	public void saveBatch(List<ReturnDataSet> dataSetList, String type, boolean isEODPostings) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into ");
		if(isEODPostings){
			insertSql.append(" EODPostingsDetail");
		}else{
			insertSql.append(" Postings");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LinkedTranId, Postref, PostingId, finReference, FinEvent,");
		insertSql.append(" PostDate, ValueDate, TranCode, TranDesc, RevTranCode, DrOrCr, Account,ShadowPosting,");
		insertSql.append(" PostAmount,AmountType, PostStatus, ErrorId, ErrorMsg, HostAccountNumber)");
		insertSql.append(" Values(:LinkedTranId, :Postref, :PostingId, :finReference, :FinEvent,");
		insertSql.append(" :PostDate, :ValueDate, :TranCode, :TranDesc, :RevTranCode, :DrOrCr, :Account, :ShadowPosting,");
		insertSql.append(" :PostAmount, :AmountType, :PostStatus, :ErrorId, :ErrorMsg, :HostAccountNumber)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(dataSetList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for saving Fee charge Details list
	 */
	@Override
	public void saveChargesBatch(List<FeeRule> chargeList, String tableType) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO FinFeeCharges");
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (FinReference , SchDate , FeeCode , FeeCodeDesc , FeeOrder , FeeAmount) ");
		insertSql.append(" VALUES (:FinReference , :SchDate , :FeeCode , :FeeCodeDesc , :FeeOrder , :FeeAmount) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(chargeList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for saving Fee charge Details list
	 */
	@Override
	public void deleteChargesBatch(String finReference, String tableType) {
		logger.debug("Entering");
		
		FeeRule feeRule = new FeeRule();
		feeRule.setFinReference(finReference);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM FinFeeCharges");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE FinReference =:FinReference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Fee charge Details list based upon Reference
	 */
	
	@Override
    public List<FeeRule> getFeeChargesByFinRef(String finReference, String tableType) {
		FeeRule feeRule = new FeeRule();
		feeRule.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , SchDate , FeeCode , FeeCodeDesc , FeeOrder , FeeAmount " );
		selectSql.append(" FROM FinFeeCharges");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE  FinReference=:FinReference ORDER BY SchDate, FeeOrder");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		RowMapper<FeeRule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeRule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	
	/**
	 * Generate Linked Transaction ID
	 */
	public long getLinkedTransId(ReturnDataSet dataSet){
		logger.debug("Entering");
		long count =0; 
		try {
			String updateSql = 	"update seqPostings  set seqNo= seqNo+1 " ;
			this.namedParameterJdbcTemplate.getJdbcOperations().update(updateSql);

			String selectCountSql = "select seqNo from seqPostings" ;
			count = this.namedParameterJdbcTemplate.getJdbcOperations().queryForLong(selectCountSql);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving"+count);
		return count;
	}

	@Override
    public FinanceSummary getTotalFeeCharges(FinanceSummary finSummary) {
		logger.debug("Entering");
		
		FinanceSummary summary = new FinanceSummary();
		summary.setFinReference(finSummary.getFinReference());
		
		StringBuilder selectSql = new StringBuilder(" select TotalFees,TotalCharges from " );
		selectSql.append(" (select SUM(PostAmount) TotalFees, Finreference from Postings  " );
		selectSql.append(" where FinReference=:FinReference  AND AmountType='F' and DrOrCr='C' Group by Finreference) A  " );
		selectSql.append(" inner join (select SUM(PostAmount) TotalCharges, Finreference from Postings " );
		selectSql.append(" where Finreference=:FinReference  AND AmountType='C' and DrOrCr='C' Group by Finreference) B " );
		selectSql.append(" on A.Finreference = B.Finreference " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSummary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		try {
			summary = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
			finSummary.setTotalCharges(summary.getTotalCharges());
			finSummary.setTotalFees(summary.getTotalFees());
		} catch (Exception e) {
			logger.error(e);
			summary = null;
		}
		logger.debug("Leaving");
		return finSummary;
    }
	@Override
	public void updateBatch(List<ReturnDataSet> dataSetList, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update Postings");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" set PostStatus=:PostStatus where Postref=:Postref");//TODO
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(dataSetList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
}
