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
						
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		Currency currency = (Currency) auditHeader.getAuditDetail().getModelData();

		if (currency.isWorkflow()) {
			tableType="_Temp";
		}

		if (currency.isNew()) {
			currency.setCcyCode(getCurrencyDAO().save(currency,tableType));
			auditHeader.getAuditDetail().setModelData(currency);
			auditHeader.setAuditReference(currency.getCcyCode());
		}else{
			getCurrencyDAO().update(currency,tableType);
		}

		CurrencyUtil.setCurrencyDetails(currency.getCcyCode(), currency);
		getAuditHeaderDAO().addAudit(auditHeader);
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
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Currency currency = (Currency) auditHeader.getAuditDetail().getModelData();
		getCurrencyDAO().delete(currency,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
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
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Currency currency = new Currency();
		BeanUtils.copyProperties((Currency) auditHeader.getAuditDetail().getModelData(), currency);

		if (currency.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCurrencyDAO().delete(currency, "");

		} else {
			currency.setRoleCode("");
			currency.setNextRoleCode("");
			currency.setTaskId("");
			currency.setNextTaskId("");
			currency.setWorkflowId(0);

			if (currency.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				currency.setRecordType("");
				getCurrencyDAO().save(currency, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				currency.setRecordType("");
				getCurrencyDAO().update(currency, "");
			}
		}

		getCurrencyDAO().delete(currency, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(currency);
		getAuditHeaderDAO().addAudit(auditHeader);
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
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Currency currency= (Currency) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCurrencyDAO().delete(currency,"_Temp");
		
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
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
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering ");
		Currency currency = (Currency) auditDetail.getModelData();
		Currency tempCurrency = null;
		if (currency.isWorkflow()) {
			tempCurrency = getCurrencyDAO().getCurrencyById(currency.getId(),
					"_Temp");
		}
		Currency befCurrency = getCurrencyDAO().getCurrencyById(
				currency.getId(), "");

		Currency oldCurrency = currency.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm= new String[3];

		valueParm[0] = currency.getCcyCode();
		valueParm[1] = currency.getCcyNumber();
		valueParm[2] = currency.getCcySwiftCode();

		errParm[0] = PennantJavaUtil.getLabel("label_CcyCode") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CcyNumber") + ":"+valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_CcySwiftCode") + ":"+valueParm[2];

		if (currency.isNew()) { // for New record or new record into work flow

			if (!currency.isWorkflow()) {// With out Work flow only new records
				if (befCurrency != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}
			} else { // with work flow
				if (currency.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCurrency != null || tempCurrency != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befCurrency == null || tempCurrency != null) {
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!currency.isWorkflow()) { // With out Work flow for update and delete

				if (befCurrency == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCurrency != null
							&& !oldCurrency.getLastMntOn().equals(
									befCurrency.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempCurrency == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCurrency != null && oldCurrency != null
						&& !oldCurrency.getLastMntOn().equals(
								tempCurrency.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
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
				parm[0] = PennantJavaUtil.getLabel("label_CcyNumber") + ":"+valueParm[1];
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",parm,null));
			}else if(getCurrencyDAO().getUniqueCurrencyByID(currency,false,true)){
				parm[0] = PennantJavaUtil.getLabel("label_CcySwiftCode") + ":"+valueParm[2];
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",parm,null));
			}
			
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !currency.isWorkflow()) {
			auditDetail.setBefImage(befCurrency);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

}