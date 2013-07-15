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
 * FileName    		:  FinanceTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceMarginSlabDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.ProductFinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rmtmasters.commodityFinanceType.validation.FinanceMarginSlabValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinanceType</b>.<br>
 */
public class FinanceTypeServiceImpl extends GenericService<FinanceType> implements FinanceTypeService {

	private final static Logger logger = Logger.getLogger(FinanceTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ProductFinanceTypeDAO productFinanceTypeDAO;
	private FinanceMarginSlabDAO financeMarginSlabDAO;
	private AccountingSetDAO accountingSetDAO;
	

	//Declaring Classes For validation for Lists
	private FinanceMarginSlabValidation financeMarginSlabValidation;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceMarginSlabDAO getFinanceMarginSlabDAO() {
		return financeMarginSlabDAO;
	}
	public void setFinanceMarginSlabDAO(FinanceMarginSlabDAO financeMarginSlabDAO) {
		this.financeMarginSlabDAO = financeMarginSlabDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public void setProductFinanceTypeDAO(ProductFinanceTypeDAO productFinanceTypeDAO) {
		this.productFinanceTypeDAO = productFinanceTypeDAO;
	}
	public ProductFinanceTypeDAO getProductFinanceTypeDAO() {
		return productFinanceTypeDAO;
	}
	
	public FinanceMarginSlabValidation getMarginSlabValidation(){
		
		if(financeMarginSlabValidation == null){
			this.financeMarginSlabValidation = new FinanceMarginSlabValidation(financeMarginSlabDAO);
		}
		return this.financeMarginSlabValidation;
	}
	
	@Override
	public FinanceType getFinanceType() {
		return getFinanceTypeDAO().getFinanceType();
	}

	@Override
	public FinanceType getNewFinanceType() {		
		FinanceType financeType=getFinanceTypeDAO().getNewFinanceType();
		
		List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt("");
		for (int i = 0; i < accountEngineRules.size(); i++) {
			financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
		}		
		return financeType;
		
	}
	
	@Override
	public FinanceType getCommodityFinanceType() {
		return getFinanceTypeDAO().getCommodityFinanceType();
	}

	@Override
	public FinanceType getNewCommodityFinanceType() {
		return getFinanceTypeDAO().getNewCommodityFinanceType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTFinanceTypes/RMTFinanceTypes_Temp by using FinanceTypeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinanceTypeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTFinanceTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		String tableType = "";
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		if (financeType.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (financeType.isNew()) {
			financeType.setId(getFinanceTypeDAO().save(financeType, tableType));
			auditHeader.getAuditDetail().setModelData(financeType);
			auditHeader.setAuditReference(financeType.getId());
		} else {
			getFinanceTypeDAO().update(financeType, tableType);
		}
		
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTFinanceTypes by using FinanceTypeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtRMTFinanceTypes by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		getFinanceTypeDAO().delete(financeType, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * getFinanceTypeById fetch the details by using FinanceTypeDAO's
	 * getFinanceTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeById(String id) {
		FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(id, "_View");		
		List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt("");
		for (int i = 0; i < accountEngineRules.size(); i++) {
			financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
		}
		return financeType;

	}

	/**
	 * getApprovedFinanceTypeById fetch the details by using FinanceTypeDAO's
	 * getFinanceTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the RMTFinanceTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return FinanceType
	 */
	public FinanceType getApprovedFinanceTypeById(String id) {
		return getFinanceTypeDAO().getFinanceTypeByID(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceType
	 *            (financeType)
	 * @return financeType
	 */
	@Override
	public FinanceType refresh(FinanceType financeType) {
		logger.debug("Entering");
		getFinanceTypeDAO().refresh(financeType);
		getFinanceTypeDAO().initialize(financeType);
		logger.debug("Leaving");
		return financeType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinanceTypeDAO().delete with parameters financeType,"" b) NEW
	 * Add new record in to main table by using getFinanceTypeDAO().save with
	 * parameters financeType,"" c) EDIT Update record in the main table by
	 * using getFinanceTypeDAO().update with parameters financeType,"" 3) Delete
	 * the record from the workFlow table by using getFinanceTypeDAO().delete
	 * with parameters financeType,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtRMTFinanceTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtRMTFinanceTypes by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinanceType financeType = new FinanceType("");
		BeanUtils.copyProperties((FinanceType) auditHeader.getAuditDetail().getModelData(), financeType);

		if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFinanceTypeDAO().delete(financeType, "");

		} else {
			financeType.setRoleCode("");
			financeType.setNextRoleCode("");
			financeType.setTaskId("");
			financeType.setNextTaskId("");
			financeType.setWorkflowId(0);

			if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeType.setRecordType("");
				getFinanceTypeDAO().save(financeType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeType.setRecordType("");
				getFinanceTypeDAO().update(financeType, "");
			}
		}

		getFinanceTypeDAO().delete(financeType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFinanceTypeDAO().delete with parameters
	 * financeType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTFinanceTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceTypeDAO().delete(financeType, "_TEMP");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {

		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		FinanceType financeType = (FinanceType) auditDetail.getModelData();
		FinanceType tempFinanceType = null;
		if (financeType.isWorkflow()) {
			tempFinanceType = getFinanceTypeDAO().getFinanceTypeByID(financeType.getId(), "_Temp");
		}
		FinanceType befFinanceType = getFinanceTypeDAO().getFinanceTypeByID(financeType.getId(), "");

		FinanceType old_FinanceType = financeType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = financeType.getFinType();

		errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParm[0];

		if (financeType.isNew()) { // for New record or new record into work
			// flow

			if (!financeType.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceType != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));

				}
			} else { // with work flow

				if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
					// is new
					if (befFinanceType != null || tempFinanceType != null) { // if
																				// records
																				// already
																				// exists
						// in the main table
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));

					}
				} else { // if records not exists in the Main flow table
					if (befFinanceType == null || tempFinanceType != null) {
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));

					}
				}
			}

		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeType.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceType == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, null));

				} else {

					if (old_FinanceType != null
							&& !old_FinanceType.getLastMntOn().equals(befFinanceType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}

					}
				}

			} else {
				if (tempFinanceType == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
				if (tempFinanceType != null && old_FinanceType != null
						&& !old_FinanceType.getLastMntOn().equals(tempFinanceType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}

		}
		// To Check Whether the finance type is active or not
		if (!financeType.isFinIsActive()) {
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "81004", errParm, null));// warning
		}
		if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			if (getProductFinanceTypeDAO().checkFinanceType(financeType.getFinType(), "_View")) {

				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41011", errParm, null));// error
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove")	|| !financeType.isWorkflow()) {
			auditDetail.setBefImage(befFinanceType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {//TODO check for need of this method--siva

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> MarginSlabauditDetailMap = new HashMap<String, List<AuditDetail>>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";
		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (financeType.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		financeType.setLovDescMarginSlabAuditDetailMap(MarginSlabauditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeType);
		auditHeader.setAuditDetails(auditDetails);
		return auditHeader;
	}
	
	/**
	 * Method to get FinanceType based on finance type
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeByFinType(String finType) {		
		return getFinanceTypeDAO().getFinanceTypeByFinType(finType);
	}

	@Override
    public boolean checkRIAFinance(String finType) {
		return getFinanceTypeDAO().checkRIAFinance(finType);
    }
	
}

