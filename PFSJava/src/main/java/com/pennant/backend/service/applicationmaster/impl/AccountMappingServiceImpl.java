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
 * FileName    		:  AccountMappingServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>AccountMapping</b>.<br>
 */
public class AccountMappingServiceImpl extends GenericService<AccountMapping> implements AccountMappingService {
	private final static Logger logger = Logger.getLogger(AccountMappingServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private AccountMappingDAO accountMappingDAO;
	private TransactionEntryDAO transactionEntryDAO;


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	
	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the accountMappingDAO
	 */
	public AccountMappingDAO getAccountMappingDAO() {
		return accountMappingDAO;
	}
	/**
	 * @param accountMappingDAO the accountMappingDAO to set
	 */
	public void setAccountMappingDAO(AccountMappingDAO accountMappingDAO) {
		this.accountMappingDAO = accountMappingDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * AccountMapping/AccountMapping_Temp by using AccountMappingDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using AccountMappingDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtAccountMapping by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (accountMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (accountMapping.isNew()) {
			getAccountMappingDAO().save(accountMapping,tableType);
		}else{
			getAccountMappingDAO().update(accountMapping,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table AccountMapping by using AccountMappingDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtAccountMapping by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		
		AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		getAccountMappingDAO().delete(accountMapping,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getAccountMapping fetch the details by using AccountMappingDAO's getAccountMappingById
	 * method.
	 * 
	 * @param account
	 *            account of the AccountMapping.
	 * @return AccountMapping
	 */
	@Override
	public AccountMapping getAccountMapping(String account) {
		return getAccountMappingDAO().getAccountMapping(account,"_View");
	}

	/**
	 * getApprovedAccountMappingById fetch the details by using AccountMappingDAO's
	 * getAccountMappingById method . with parameter id and type as blank. it fetches
	 * the approved records from the AccountMapping.
	 * 
	 * @param account
	 *            account of the AccountMapping.
	 *            (String)
	 * @return AccountMapping
	 */
	public AccountMapping getApprovedAccountMapping(String account) {
		return getAccountMappingDAO().getAccountMapping(account,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAccountMappingDAO().delete with parameters accountMapping,"" b) NEW Add new
	 * record in to main table by using getAccountMappingDAO().save with parameters
	 * accountMapping,"" c) EDIT Update record in the main table by using
	 * getAccountMappingDAO().update with parameters accountMapping,"" 3) Delete the record
	 * from the workFlow table by using getAccountMappingDAO().delete with parameters
	 * accountMapping,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtAccountMapping by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtAccountMapping by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AccountMapping accountMapping = new AccountMapping();
		BeanUtils.copyProperties((AccountMapping) auditHeader.getAuditDetail().getModelData(), accountMapping);

		getAccountMappingDAO().delete(accountMapping, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(accountMapping.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(accountMappingDAO.getAccountMapping(accountMapping.getAccount(), ""));
		}

		if (accountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAccountMappingDAO().delete(accountMapping, TableType.MAIN_TAB);
		} else {
			accountMapping.setRoleCode("");
			accountMapping.setNextRoleCode("");
			accountMapping.setTaskId("");
			accountMapping.setNextTaskId("");
			accountMapping.setWorkflowId(0);

			if (accountMapping.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountMapping.setRecordType("");
				getAccountMappingDAO().save(accountMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountMapping.setRecordType("");
				getAccountMappingDAO().update(accountMapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountMapping);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getAccountMappingDAO().delete with parameters
		 * accountMapping,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtAccountMapping by using auditHeaderDAO.addAudit(auditHeader) for Work
		 * flow
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		@Override
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.info(Literal.ENTERING);
			
			auditHeader = businessValidation(auditHeader,"doApprove");
			if (!auditHeader.isNextProcess()) {
				logger.info(Literal.LEAVING);
				return auditHeader;
			}

			AccountMapping accountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAccountMappingDAO().delete(accountMapping,TableType.TEMP_TAB);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps. 1) get the details from
		 * the auditHeader. 2) fetch the details from the tables 3) Validate the
		 * Record based on the record details. 4) Validate for any business
		 * validation.
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug(Literal.ENTERING);
			
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);

			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		/**
		 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
		 * from getAccountMappingDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Write the required validation over hear.
			
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

		public TransactionEntryDAO getTransactionEntryDAO() {
			return transactionEntryDAO;
		}
		
		public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
			this.transactionEntryDAO = transactionEntryDAO;
		}
		
		@Override
		public List<TransactionEntry> getTransactionEntriesByFintype(String finType) {
			return transactionEntryDAO.getTransactionEntriesbyFinType(finType, "");
		}

		@Override
		public Map<String, Rule> getSubheadRules(List<String> subHeadRules) {
			List<Rule> rulesList = this.transactionEntryDAO.getSubheadRules(subHeadRules, "_AView");
			Map<String, Rule> rulesMap = new HashMap<String, Rule>();
			if(rulesList != null) {
				for(Rule rule : rulesList) {
					rulesMap.put(rule.getRuleCode(), rule);
				}
			}
			return rulesMap;
		}

		@Override
		public void save(List<AccountMapping> accountMappingList, String finType) {
			List<AccountMapping> accMapList = this.accountMappingDAO.getAccountMappingFinType(finType, "");
			if(accMapList != null && !accMapList.isEmpty()) {
				this.accountMappingDAO.delete(finType, TableType.MAIN_TAB);
			}
			
			for(AccountMapping accountMapping : accountMappingList) {
				this.accountMappingDAO.save(accountMapping, TableType.MAIN_TAB);
			}
		}
}