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
 * FileName    		:  ManagerChequeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement.impl;


import java.math.BigDecimal;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.financemanagement.ManagerChequeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ManagerCheque model</b> class.<br>
 * 
 */

public class ManagerChequeDAOImpl extends BasisNextidDaoImpl<ManagerCheque> implements ManagerChequeDAO {

	private static Logger logger = Logger.getLogger(ManagerChequeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public ManagerChequeDAOImpl() {
		super();
	}


	/**
	 * This method set the Work Flow id based on the module name and return the new ManagerCheque 
	 * @return ManagerCheque
	 */

	@Override
	public ManagerCheque getManagerCheque() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ManagerCheque");
		ManagerCheque managerCheque= new ManagerCheque();
		if (workFlowDetails!=null){
			managerCheque.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return managerCheque;
	}


	/**
	 * This method get the module from method getManagerCheque() and set the new record flag as true and return ManagerCheque()   
	 * @return ManagerCheque
	 */


	@Override
	public ManagerCheque getNewManagerCheque() {
		logger.debug("Entering");
		ManagerCheque managerCheque = getManagerCheque();
		managerCheque.setNewRecord(true);
		logger.debug("Leaving");
		return managerCheque;
	}

	/**
	 * Method for get the Next Sequence Id
	 * In Notes table ChequeID is stored in reference field.
	 */
	public long getNextId() {
		return getNextidviewDAO().getNextId("SeqManagerCheques");
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Fetch the Record  Manager Cheques details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ManagerCheque
	 */
	@Override
	public ManagerCheque getManagerChequeById(final long id, String type) {
		logger.debug("Entering");
		ManagerCheque managerCheque = getManagerCheque();
		
		managerCheque.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select ChequeID, ChqPurposeCode, ChequeRef, ChequeNo, BeneficiaryName, CustCIF, DraftCcy, FundingCcy, FundingAccount, NostroAccount, NostroFullName, ChequeAmount, ValueDate, Narration1, Narration2, Reprint, OldChequeID, Cancel");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, BranchCode, StopOrderRef");
		selectSql.append(", IssueDate, ChargeAmount, AddressLine1, AddressLine2, AddressLine3, AddressLine4, AddressLine5");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",ChqPurposeCodeName,lovDescBranchDesc, FundingAmount, lovDescCustShrtName");
		}
		selectSql.append(" From ManagerCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ChequeID =:ChequeID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		RowMapper<ManagerCheque> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManagerCheque.class);
		
		try{
			managerCheque = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			managerCheque = null;
		}
		logger.debug("Leaving");
		return managerCheque;
	}
	
	/**
	 * Fetch the Record  Manager Cheques details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ManagerCheque
	 */
	@Override
	public ManagerCheque getReprintManagerChequeById(final long id, String type) {
		logger.debug("Entering");
		ManagerCheque managerCheque = getManagerCheque();
		
		managerCheque.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select ChequeID, ChqPurposeCode, ChequeRef, ChequeNo, BeneficiaryName, CustCIF, DraftCcy, FundingCcy, FundingAccount, NostroAccount, NostroFullName, ChequeAmount, ValueDate, Narration1, Narration2, Reprint, OldChequeID, Cancel");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, BranchCode, StopOrderRef");
		selectSql.append(", IssueDate, ChargeAmount, AddressLine1, AddressLine2, AddressLine3, AddressLine4, AddressLine5");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",ChqPurposeCodeName, lovDescBranchDesc, FundingAmount, lovDescCustShrtName");
		}
		selectSql.append(" From ManagerCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where OldChequeID = :ChequeID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		RowMapper<ManagerCheque> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManagerCheque.class);
		
		try{
			managerCheque = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			managerCheque = null;
		}
		logger.debug("Leaving");
		return managerCheque;
	}
	
	
	/**
	 * This method Deletes the Record from the ManagerCheques or ManagerCheques_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Manager Cheques by key ChequeID
	 * 
	 * @param Manager Cheques (managerCheque)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ManagerCheque managerCheque,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From ManagerCheques");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ChequeID =:ChequeID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into ManagerCheques or ManagerCheques_Temp.
	 * it fetches the available Sequence form SeqManagerCheques by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Manager Cheques 
	 * 
	 * @param Manager Cheques (managerCheque)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(ManagerCheque managerCheque,String type) {
		logger.debug("Entering");
		if (managerCheque.getId()==Long.MIN_VALUE){
			managerCheque.setId(getNextidviewDAO().getNextId("SeqManagerCheques"));
			logger.debug("get NextID:"+managerCheque.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into ManagerCheques");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ChequeID, ChqPurposeCode, ChequeRef, ChequeNo, BeneficiaryName, CustCIF, DraftCcy, FundingCcy, FundingAccount, NostroAccount, NostroFullName, ChequeAmount, ValueDate, Narration1, Narration2, Reprint, OldChequeID, Cancel");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, BranchCode, StopOrderRef");
		insertSql.append(", IssueDate, ChargeAmount, AddressLine1, AddressLine2, AddressLine3, AddressLine4, AddressLine5)");
		insertSql.append(" Values(:ChequeID, :ChqPurposeCode, :ChequeRef, :ChequeNo, :BeneficiaryName, :CustCIF, :DraftCcy, :FundingCcy, :FundingAccount, :NostroAccount, :NostroFullName, :ChequeAmount, :ValueDate, :Narration1, :Narration2, :Reprint, :OldChequeID, :Cancel");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :BranchCode, :StopOrderRef");
		insertSql.append(", :IssueDate, :ChargeAmount, :AddressLine1, :AddressLine2, :AddressLine3, :AddressLine4, :AddressLine5)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return managerCheque.getId();
	}
	
	/**
	 * This method updates the Record ManagerCheques or ManagerCheques_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Manager Cheques by key ChequeID and Version
	 * 
	 * @param Manager Cheques (managerCheque)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(ManagerCheque managerCheque,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ManagerCheques");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ChqPurposeCode = :ChqPurposeCode, ChequeRef = :ChequeRef, ChequeNo = :ChequeNo, BeneficiaryName = :BeneficiaryName");
		updateSql.append(", CustCIF = :CustCIF, DraftCcy = :DraftCcy, FundingCcy = :FundingCcy, FundingAccount = :FundingAccount, NostroAccount = :NostroAccount, NostroFullName = :NostroFullName");
		updateSql.append(", ChequeAmount = :ChequeAmount, ValueDate = :ValueDate, Narration1 = :Narration1, Narration2 = :Narration2, Reprint = :Reprint, OldChequeID = :OldChequeID, Cancel = :Cancel");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, BranchCode = :BranchCode, StopOrderRef = :StopOrderRef");
		updateSql.append(", IssueDate = :IssueDate, ChargeAmount = :ChargeAmount, AddressLine1 = :AddressLine1, AddressLine2 = :AddressLine2, AddressLine3 = :AddressLine3, AddressLine4 = :AddressLine4, AddressLine5 = :AddressLine5");
		updateSql.append(" Where ChequeID =:ChequeID");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 *  Method for get the FinancePremiumDetail Object by Key finReference
	 */
	@Override
	public BigDecimal getTotalChqAmtByFinReference(String finReference, String type) {
		logger.debug("Entering");
		
		BigDecimal totalChequeAmt = BigDecimal.ZERO;
		ManagerCheque managerCheque = getManagerCheque();
		managerCheque.setChequeRef(finReference);
	    
		StringBuilder selectSql = new StringBuilder("SELECT SUM(ChequeAmount) FROM ManagerCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ChequeRef = :ChequeRef and Reprint = 0 and Cancel = 0");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);

		try {
			totalChequeAmt = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			totalChequeAmt = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return totalChequeAmt;
	}
	/**
	 * Method for Validate Dependency with ChequePurpose
	 * @param ChqPurposeCode
	 * @param type
	 * @return count 
	 */
	@SuppressWarnings("deprecation")
	public int getMgrChqCountByChqPurposeCode(String chqPurposeCode, String type) {
		logger.debug("Entering");
		int count;
 		ManagerCheque managerCheque = getManagerCheque();
		managerCheque.setChqPurposeCode(chqPurposeCode);

		StringBuilder selectSql = new StringBuilder("Select Count(*) From ManagerCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ChqPurposeCode = :ChqPurposeCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		try{
			count = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);
 		}catch (EmptyResultDataAccessException e) {
 			logger.warn("Exception: ", e);
			count = 0;
		}
		logger.debug("Leaving");
		return count;
	}
	/**
	 * Method for Validate Cheque Number
	 * @param chequeID
	 * @param chqNo
	 * @param nostroAccount
	 * @param type
	 * @return count 
	 */
	@SuppressWarnings("deprecation")
	public int getMgrChqCountByChqNoAndAccount(long chequeID, String chqNo, String nostroAccount,  String type){
		logger.debug("Entering");
		int count;
		ManagerCheque managerCheque = getManagerCheque();
		managerCheque.setChequeID(chequeID);
		managerCheque.setChequeNo(chqNo);
		managerCheque.setNostroAccount(nostroAccount);
		
		StringBuilder selectSql = new StringBuilder("Select Count(*) From ManagerCheques");
		selectSql.append(StringUtils.trimToEmpty(type));
 		selectSql.append(" Where ChequeID <> :ChequeID And ChequeNo = :ChequeNo And NostroAccount = :NostroAccount");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(managerCheque);
		try{
			count = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		logger.debug("Leaving");
		return count;
	}
	
}