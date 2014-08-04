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
 * FileName    		:  TransactionEntryDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>TransactionEntry model</b> class.<br>
 */
public class TransactionEntryDAOImpl extends BasisNextidDaoImpl<TransactionEntry> 
			implements TransactionEntryDAO {

	private static Logger logger = Logger.getLogger(TransactionEntryDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new TransactionEntry 
	 * @return TransactionEntry
	 */
	@Override
	public TransactionEntry getTransactionEntry() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("TransactionEntry");
		TransactionEntry transactionEntry= new TransactionEntry();
		if (workFlowDetails!=null){
			transactionEntry.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return transactionEntry;
	}

	/**
	 * This method get the module from method getTransactionEntry() and set the
	 * new record flag as true and return TransactionEntry()
	 * 
	 * @return TransactionEntry
	 */
	@Override
	public TransactionEntry getNewTransactionEntry() {
		logger.debug("Entering");
		TransactionEntry transactionEntry = getTransactionEntry();
		transactionEntry.setNewRecord(true);
		logger.debug("Leaving");
		return transactionEntry;
	}

	/**
	 * Fetch the Record  Transaction Entry details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TransactionEntry
	 */
	@Override
	public TransactionEntry getTransactionEntryById(final long id, int transOrder,String type) {
		logger.debug("Entering");
		TransactionEntry transactionEntry = new TransactionEntry();
		
		transactionEntry.setId(id);
		transactionEntry.setTransOrder(transOrder);
		
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc," );
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch ,AccountSubHeadRule,");
		selectSql.append(" TranscationCode, RvsTransactionCode, AmountRule,FeeCode,ChargeType,EntryByInvestment,OpenNewFinAc,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescAccountTypeName,lovDescAccountSubHeadRuleName," );
			selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName, ");
			selectSql.append(" lovDescEventCodeName,lovDescAccSetCodeName,lovDescAccSetCodeDesc," );
			selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid and TransOrder=:TransOrder");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TransactionEntry.class);
		
		try{
			transactionEntry = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			transactionEntry = null;
		}
		logger.debug("Leaving");
		return transactionEntry;
	}
	
	/**
	 * Fetch the Record  Transaction Entry details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListTransactionEntryById(final long id, String type, boolean postingsProcess) {
		logger.debug("Entering");
		
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc," );
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch," );
		selectSql.append(" AccountSubHeadRule, TranscationCode, RvsTransactionCode, AmountRule,ChargeType,");
		selectSql.append(" FeeCode , EntryByInvestment ,OpenNewFinAc" );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" , lovDescEventCodeName, lovDescEventCodeDesc ");
			if(!postingsProcess){
				selectSql.append(" ,lovDescAccountTypeName,lovDescAccountSubHeadRuleName," );
				selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName ,");
				selectSql.append(" lovDescAccSetCodeName,lovDescAccSetCodeDesc," );
				selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName, ");
			}
		}
		if(!postingsProcess){
			selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
			selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		}
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				TransactionEntry.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * get the list of AccountSetid's
	 * 
	 * @return List
	 */
	@Override
	public List<Long> getAccountSetIds() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("select distinct AccountSetid" );
		selectSql.append(" From RMTTransactionEntry");
		
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), new BeanPropertySqlParameterSource(""), Long.class);	
	}
	
	/**
	 * Fetch the Record  Transaction Entry details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListTranEntryForBatch(final long id, String type) {
		logger.debug("Entering");
		
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc, Debitcredit, " );
		selectSql.append(" ShadowPosting, Account, AccountType,AccountBranch, AccountSubHeadRule, " );
		selectSql.append(" TranscationCode, RvsTransactionCode, AmountRule,ChargeType, FeeCode , OpenNewFinAc" );
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(" Where AccountSetid =:AccountSetid");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TransactionEntry.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Fetch the Record  Transaction Entry details by key field and RefType
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListTransactionEntryByRefType(String finType , int refType, String roleCode, 
			String type, boolean postingsProcess) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc," );
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch," );
		selectSql.append(" AccountSubHeadRule, TranscationCode, RvsTransactionCode, AmountRule,ChargeType,");
		selectSql.append(" FeeCode , EntryByInvestment ,OpenNewFinAc," );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescEventCodeName ");
			if(!postingsProcess){
				selectSql.append(" ,lovDescAccountTypeName,lovDescAccountSubHeadRuleName," );
				selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName ,");
				selectSql.append(" lovDescAccSetCodeName,lovDescAccSetCodeDesc," );
				selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName, ");
			}
		}
		if(!postingsProcess){
			selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
			selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		}
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid IN ( SELECT FinRefId from LMTFinRefDetail_ACView  " );
		selectSql.append(" where Fintype='" +finType );
		selectSql.append("' and FinRefType ='" + refType +	"' and MandInputInStage like '%" +roleCode + ",%')");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new TransactionEntry());
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				TransactionEntry.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Fetch the Record  Rule Details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Rules
	 */
	@Override
	public List<Rule> getListFeeChargeRules(long id, String ruleEvent, String type, int seqOrder) {
		logger.debug("Entering");
		
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(id);
		
		StringBuilder selectSql = new StringBuilder(" Select DISTINCT FeeCode from RMTTransactionEntry_AView");
		selectSql.append(" Where AccountSetid =:AccountSetid AND  ISNULL(Feecode,'') <> '' " );
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		List<String> feeCodeList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
		
		String feeCodes = "";
		for (int i = 0; i < feeCodeList.size(); i++) {
			
			String[] list = null;
			if(feeCodeList.get(i).contains(",")){
				list = feeCodeList.get(i).split(",");
			}

			if(list != null && list.length > 0){
				for (int j = 0; j < list.length; j++) {
					if(!feeCodes.contains("'"+list[j]+"'")){
						feeCodes = feeCodes + "'"+list[j]+"',";
					}
				}
			}else{
				if(!feeCodes.contains("'"+feeCodeList.get(i).trim()+"'")){
					feeCodes = feeCodes + "'"+feeCodeList.get(i).trim()+"',";
				}
			}
        }
		
		if(feeCodes.endsWith(",")){
			feeCodes = feeCodes.substring(0, feeCodes.length()-1);
		}
		
		if(feeCodes.length()>0){
			selectSql = new StringBuilder(" SELECT RuleCode, RuleCodeDesc,SQLRule," );
			selectSql.append(" WaiverDecider , Waiver,WaiverPerc,AddFeeCharges, SeqOrder ");
			selectSql.append(" From Rules");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" where ruleModule='FEES' AND ruleCode IN (");
			selectSql.append(feeCodes);
			selectSql.append(" ) AND RuleEvent='");
			selectSql.append(ruleEvent+"' " );
			if(seqOrder != 0){
				selectSql.append(" AND SeqOrder > "+seqOrder );
			}
			selectSql.append(" Order BY SeqOrder ");

			logger.debug("selectSql: " + selectSql.toString());
			beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
			RowMapper<Rule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Rule.class);
			logger.debug("Leaving");
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
			
		}else{
			logger.debug("Leaving");
			return new ArrayList<Rule>();
		}
	}
	
	
	/**
	 * Fetch the Record  Rule Details by key field
	 * 
	 * @param accountSetId (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Rules
	 */
	@Override
	public List<String> getListFeeCodes(long accountSetId) {
		logger.debug("Entering");
		
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(accountSetId);
		
		StringBuilder selectSql = new StringBuilder(" Select DISTINCT FeeCode from RMTTransactionEntry_AView");
		selectSql.append(" Where AccountSetid =:AccountSetid AND  ISNULL(Feecode,'') <> '' " );
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
	}
		
	/**
	 * This method initialize the Record.
	 * @param TransactionEntry (transactionEntry)
 	 * @return TransactionEntry
	 */
	@Override
	public void initialize(TransactionEntry transactionEntry) {
		super.initialize(transactionEntry);
	}
	
	/**
	 * This method refresh the Record.
	 * @param TransactionEntry (transactionEntry)
 	 * @return void
	 */
	@Override
	public void refresh(TransactionEntry transactionEntry) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTTransactionEntry or RMTTransactionEntry_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Transaction Entry by key AccountSetid
	 * 
	 * @param Transaction Entry (transactionEntry)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(TransactionEntry transactionEntry,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTTransactionEntry");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountSetid =:AccountSetid AND TransOrder =:TransOrder");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",transactionEntry.getId() ,transactionEntry.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",transactionEntry.getId() ,transactionEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Records from the RMTTransactionEntry or RMTTransactionEntry_Temp.
	 * delete Transaction Entry(s) by key AccountSetid
	 * 
	 * @param accountingSetId (long)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return 
	 */
	public void deleteByAccountingSetId(final long accountingSetId,String type) {
		logger.debug("Entering");
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(accountingSetId);
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTTransactionEntry");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountSetid =:AccountSetid");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTTransactionEntry or
	 * RMTTransactionEntry_Temp. it fetches the available Sequence form
	 * SeqRMTTransactionEntry by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Transaction Entry
	 * 
	 * @param Transaction
	 *            Entry (transactionEntry)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(TransactionEntry transactionEntry,String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder("Insert Into RMTTransactionEntry");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AccountSetid, TransOrder, TransDesc, Debitcredit, ShadowPosting," );
		insertSql.append(" Account, AccountType, AccountBranch, AccountSubHeadRule, TranscationCode," );
		insertSql.append(" RvsTransactionCode, AmountRule,FeeCode,ChargeType, EntryByInvestment ,OpenNewFinAc,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId) ");
		insertSql.append(" Values(:AccountSetid, :TransOrder, :TransDesc, :Debitcredit, :ShadowPosting," );
		insertSql.append(" :Account, :AccountType,:AccountBranch, :AccountSubHeadRule, :TranscationCode," );
		insertSql.append(" :RvsTransactionCode, :AmountRule,:FeeCode,:ChargeType, :EntryByInvestment ,:OpenNewFinAc," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return transactionEntry.getId();
	}
	
	/**
	 * This method updates the Record RMTTransactionEntry or RMTTransactionEntry_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Transaction Entry by key AccountSetid and Version
	 * 
	 * @param Transaction Entry (transactionEntry)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(TransactionEntry transactionEntry,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update RMTTransactionEntry");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AccountSetid = :AccountSetid, TransOrder = :TransOrder," );
		updateSql.append(" TransDesc = :TransDesc, Debitcredit = :Debitcredit," );
		updateSql.append(" ShadowPosting = :ShadowPosting, Account = :Account, AccountType = :AccountType," );
		updateSql.append(" AccountBranch=:AccountBranch,AccountSubHeadRule = :AccountSubHeadRule," );
		updateSql.append(" TranscationCode = :TranscationCode, RvsTransactionCode = :RvsTransactionCode," );
		updateSql.append(" AmountRule = :AmountRule,FeeCode=:FeeCode, " );
		updateSql.append(" ChargeType =:ChargeType, EntryByInvestment =:EntryByInvestment ,OpenNewFinAc=:OpenNewFinAc," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AccountSetid =:AccountSetid and TransOrder = :TransOrder");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",transactionEntry.getId() ,transactionEntry.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record  Transaction Entry details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListFeeTransEntryById(final long id, String type) {
		logger.debug("Entering");
		
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc," );
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch," );
		selectSql.append(" AccountSubHeadRule, TranscationCode, RvsTransactionCode, AmountRule,ChargeType,");
		selectSql.append(" FeeCode,EntryByInvestment,OpenNewFinAc," );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescAccountTypeName,lovDescAccountSubHeadRuleName," );
			selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName ,");
			selectSql.append(" lovDescEventCodeName,lovDescAccSetCodeName,lovDescAccSetCodeDesc," );
			selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				TransactionEntry.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	private ErrorDetails  getError(String errorId, long AccountSetid, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = String.valueOf(AccountSetid);
		parms[0][0] = PennantJavaUtil.getLabel("label_AccountSetid")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
				parms[0],parms[1]), userLanguage);
	}

	/**
	 * Method for Getting List of Transaction entries For LATEPAY event
	 */
	@Override
	public List<TransactionEntry> getODTransactionEntries() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select  TransOrder, TransDesc," );
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType, AmountRule " );
		selectSql.append(" From RMTODTransactionEntry");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new TransactionEntry());
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				TransactionEntry.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Method for Getting List of Transaction entries for Updation depends on OverDueRule
	 */
	@Override
	public List<TransactionEntry> getTransactionEntryList(String oDRuleCode) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder(" SELECT TE.AccountSetid, TE.TransOrder , ODT.AccountType " );
		selectSql.append(" FROM RMTTransactionEntry AS TE, RMTODTransactionEntry AS ODT " );
		selectSql.append(" WHERE AccountSetid = (select AccountSetid from RMTAccountingSet " );
		selectSql.append(" WHERE EventCode='LATEPAY' AND AccountSetCode ='"+oDRuleCode+"') " );
		selectSql.append(" AND TE.TransOrder = ODT.TransOrder AND ODT.AccountType != ''" );
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new TransactionEntry());
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				TransactionEntry.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Method for Update List of Transaction entries for Updation depends on OverDueRule
	 */
	@Override
	public void updateTransactionEntryList(List<TransactionEntry> entries) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder(" UPDATE RMTTransactionEntry " );
		updateSql.append(" SET AccountType = :AccountType, AccountSubHeadRule = :AccountSubHeadRule " );
		updateSql.append(" WHERE AccountSetid = :AccountSetid AND TransOrder = :TransOrder" );
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(entries.toArray());
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
	}

}