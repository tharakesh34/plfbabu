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
 * FileName    		:  CurrencyServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Currency</b>.<br>
 * 
 */
public class CurrencyServiceImpl extends GenericService<Currency> implements CurrencyService {
	private static Logger logger = Logger.getLogger(CurrencyServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;	
	private CurrencyDAO currencyDAO;

	public CurrencyServiceImpl() {
		super();
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}	
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}	
	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTCurrencies/RMTCurrencies_Temp by using CurrencyDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using CurrencyDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtRMTCurrencies by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
						
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		Currency currency = (Currency) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (currency.isWorkflow()) {
			tableType= TableType.TEMP_TAB;
		}

		if (currency.isNew()) {
			currency.setCcyCode(getCurrencyDAO().save(currency,tableType));
			auditHeader.getAuditDetail().setModelData(currency);
			auditHeader.setAuditReference(currency.getCcyCode());
		}else{
			getCurrencyDAO().update(currency,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		
		if (StringUtils.isEmpty("")) {
			CurrencyUtil.register(currency, PennantConstants.TRAN_UPD);
		}
		
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTCurrencies by using CurrencyDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtRMTCurrencies by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Currency currency = (Currency) auditHeader.getAuditDetail().getModelData();
		getCurrencyDAO().delete(currency, TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);

		CurrencyUtil.register(currency, PennantConstants.TRAN_DEL);

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getCurrencyById fetch the details by using CurrencyDAO's getCurrencyById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Currency
	 */
	@Override
	public Currency getCurrencyById(String id) {
		return getCurrencyDAO().getCurrencyById(id,"_View");
	}
	
	/**
	 * getApprovedCurrencyById fetch the details by using CurrencyDAO's
	 * getCurrencyById method . with parameter id and type as blank. it fetches
	 * the approved records from the RMTCurrencies.
	 * 
	 * @param id
	 *            (String)
	 * @return Currency
	 */
	public Currency getApprovedCurrencyById(String id) {
		return getCurrencyDAO().getCurrencyById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCurrencyDAO().delete with
	 * parameters currency,"" b) NEW Add new record in to main table by using getCurrencyDAO().save with parameters
	 * currency,"" c) EDIT Update record in the main table by using getCurrencyDAO().update with parameters currency,""
	 * 3) Delete the record from the workFlow table by using getCurrencyDAO().delete with parameters currency,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTCurrencies by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtRMTCurrencies by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");

		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Currency currency = new Currency();
		BeanUtils.copyProperties((Currency) auditHeader.getAuditDetail().getModelData(), currency);
		getCurrencyDAO().delete(currency, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(currency.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(currencyDAO.getCurrencyById(currency.getCcyCode(), ""));
		}
		
		if (currency.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCurrencyDAO().delete(currency, TableType.MAIN_TAB);

		} else {
			currency.setRoleCode("");
			currency.setNextRoleCode("");
			currency.setTaskId("");
			currency.setNextTaskId("");
			currency.setWorkflowId(0);

			if (currency.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				currency.setRecordType("");
				getCurrencyDAO().save(currency, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				currency.setRecordType("");
				getCurrencyDAO().update(currency, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(currency);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		CurrencyUtil.register(currency, tranType);
		
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCurrencyDAO().delete with parameters
	 * currency,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTCurrencies by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Currency currency= (Currency) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCurrencyDAO().delete(currency, TableType.TEMP_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCurrencyDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage){
		logger.debug(Literal.ENTERING);

		// Get the model object.
		Currency currency = (Currency) auditDetail.getModelData();

		// Check the unique keys.
		if (currency.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(currency.getRecordType())
				&& currencyDAO
						.isDuplicateKey(currency.getCcyCode(), currency.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];
			parameters[0] = PennantJavaUtil.getLabel("label_CcyCode") + ": " + currency.getCcyCode();
			parameters[1] = PennantJavaUtil.getLabel("label_CcyNumber") + ": " + currency.getCcyNumber();
			parameters[2] = PennantJavaUtil.getLabel("label_CcySwiftCode") + ": " + currency.getCcySwiftCode();
			
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		
		boolean isEnableWorkFlow ;
		if (!currency.isWorkflow()) {
			isEnableWorkFlow = currency.isNew() ;
		}else{
			isEnableWorkFlow = currency.isNew() && currency.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW);
		}
		if(isEnableWorkFlow){
			String[] parm= new String[1];
			
			if(getCurrencyDAO().getUniqueCurrencyByID(currency,true,false)){
				parm[0] = PennantJavaUtil.getLabel("label_CcyNumber") + ":"+ currency.getCcyNumber();
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",parm,null));
			}else if(getCurrencyDAO().getUniqueCurrencyByID(currency,false,true)){
				parm[0] = PennantJavaUtil.getLabel("label_CcySwiftCode") + ":"+ currency.getCcySwiftCode();
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",parm,null));
			}
			
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
}