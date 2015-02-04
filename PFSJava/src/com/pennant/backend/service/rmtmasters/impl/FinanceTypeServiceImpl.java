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
import com.pennant.backend.dao.rmtmasters.FinTypeAccountDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinanceType</b>.<br>
 */
public class FinanceTypeServiceImpl extends GenericService<FinanceType> implements FinanceTypeService {

	private final static Logger logger = Logger.getLogger(FinanceTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;
	private AccountingSetDAO accountingSetDAO;
	private FinTypeAccountDAO finTypeAccountDAO;
	
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

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
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
		
		//FinTypeAccount
		if (financeType.getFinTypeAccounts() != null  && financeType.getFinTypeAccounts().size() > 0) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeAccount");
			details = processFinTypeAccountDetails(financeType, details, tableType);
			auditDetails.addAll(details);
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
		
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(financeType, "", auditHeader.getAuditTranType())));
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
		financeType.setFinTypeAccounts(getFinTypeAccountDAO().getFinTypeAccountListByID(id, "_View"));
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
		FinanceType financeType =  getFinanceTypeDAO().getFinanceTypeByID(id, "_AView");
		List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt("");
		for (int i = 0; i < accountEngineRules.size(); i++) {
			financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
		}
		financeType.setFinTypeAccounts(getFinTypeAccountDAO().getFinTypeAccountListByID(id, "_AView"));
		return financeType;
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinanceType financeType = new FinanceType("");
		BeanUtils.copyProperties((FinanceType) auditHeader.getAuditDetail().getModelData(), financeType);

		if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			//List
			auditDetails.addAll(deleteChilds(financeType, "",tranType));
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
			if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get( "FinTypeAccount");
				details = processFinTypeAccountDetails(financeType,details, "");
				auditDetails.addAll(details);
			}
		}

		getFinanceTypeDAO().delete(financeType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		//List
		auditHeader.setAuditDetails(deleteChilds(financeType, "_TEMP", auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceType(),financeType.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeType.getBefImage(), financeType));
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeType);
		
		//List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));
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
		//List
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds( financeType, "_TEMP", auditHeader.getAuditTranType())));
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
		//List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader,auditHeader.getUsrLanguage(),method));
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

		FinanceType oldFinanceType = financeType.getBefImage();

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

					if (oldFinanceType != null
							&& !oldFinanceType.getLastMntOn().equals(befFinanceType.getLastMntOn())) {
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
				if (tempFinanceType != null && oldFinanceType != null
						&& !oldFinanceType.getLastMntOn().equals(tempFinanceType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}

		}
		// To Check Whether the finance type is active or not
		if (!financeType.isFinIsActive()) {
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "81004", errParm, null));// warning
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
		HashMap<String, List<AuditDetail>> marginSlabauditDetailMap = new HashMap<String, List<AuditDetail>>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		//String auditTranType = "";
		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (financeType.isWorkflow()) {
				//auditTranType = PennantConstants.TRAN_WF;
			}
		}
		financeType.setLovDescMarginSlabAuditDetailMap(marginSlabauditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeType);
		auditHeader.setAuditDetails(auditDetails);
		return auditHeader;
	}
	
	//=================================== List maintain
		private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
			logger.debug("Entering");

			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

			FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();

			String auditTranType = "";

			if (method.equals("saveOrUpdate") || method.equals("doApprove")
			        || method.equals("doReject")) {
				if (financeType.isWorkflow()) {
					auditTranType = PennantConstants.TRAN_WF;
				}
			}
			//FinTypeAccount
			if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
				auditDetailMap.put("FinTypeAccount", setFinTypeAccountDetailsAuditData(financeType, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("FinTypeAccount"));
			}

			financeType.setAuditDetailMap(auditDetailMap);
			auditHeader.getAuditDetail().setModelData(financeType);
			auditHeader.setAuditDetails(auditDetails);

			logger.debug("Leaving");
			return auditHeader;
		}
		private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
			logger.debug("Entering");
			List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
			if (list != null & list.size() > 0) {
				for (AuditDetail auditDetail : list) {
					String transType = "";
					String rcdType = "";
					Object object = auditDetail.getModelData();
				
				   if (object instanceof FinTypeAccount) {
						//FinTypeAccount
					   FinTypeAccount finTypeAccount = (FinTypeAccount) object;
						rcdType = finTypeAccount.getRecordType();
	                }

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}
					if (!(transType.equals(""))) {
						auditDetailsList.add(new AuditDetail(transType, auditDetail.getAuditSeq(),auditDetail.getBefImage(), object));
					}

				}
			}
			logger.debug("Leaving");
			return auditDetailsList;
		}
		public List<AuditDetail> deleteChilds(FinanceType financeType, String tableType,String auditTranType) {
			List<AuditDetail> auditList = new ArrayList<AuditDetail>();
			//FinTypeAccount
			if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
				String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccount(), new FinTypeAccount().getExcludeFields());
				for (int i = 0; i < financeType.getFinTypeAccounts().size(); i++) {
					FinTypeAccount finTypeAccount = financeType.getFinTypeAccounts().get(i);
					if (!finTypeAccount.getRecordType().equals("") || tableType.equals("")) {
						auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeAccount.getBefImage(), finTypeAccount));
					}
				}
				getFinTypeAccountDAO().deleteByFinType(financeType.getFinType(), tableType);
			}    
			
			return auditList;
		}
		
		private ArrayList<ErrorDetails> validateChilds(AuditHeader auditHeader,String usrLanguage,String method){
			ArrayList<ErrorDetails> errorDetails=new ArrayList<ErrorDetails>();
			FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
			List<AuditDetail> auditDetails=null;
			//FinTypeAccount
			if (financeType.getAuditDetailMap().get("FinTypeAccount")!=null) {
				auditDetails= financeType.getAuditDetailMap().get("FinTypeAccount");
				for (AuditDetail auditDetail : auditDetails) {
					ArrayList<ErrorDetails> details=validationFinTypeAccount(auditDetail, usrLanguage, method).getErrorDetails();
					if (details!=null) {
						errorDetails.addAll(details);
					}
				}
			}
			return errorDetails;
		}
	
	
	private List<AuditDetail> processFinTypeAccountDetails(FinanceType financeType,List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeAccount finTypeAccount = (FinTypeAccount) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				finTypeAccount.setRoleCode("");
				finTypeAccount.setNextRoleCode("");
				finTypeAccount.setTaskId("");
				finTypeAccount.setNextTaskId("");
			}
			finTypeAccount.setFinType(financeType.getFinType());
			finTypeAccount.setWorkflowId(0);
			if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeAccount.isNewRecord()) {
				saveRecord = true;
				if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeAccount.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeAccount.getRecordType();
				recordStatus = finTypeAccount.getRecordStatus();
				finTypeAccount.setRecordType("");
				finTypeAccount.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeAccountDAO().save(finTypeAccount, type);
			}
			if (updateRecord) {
				getFinTypeAccountDAO().update(finTypeAccount, type);
			}
			if (deleteRecord) {
				getFinTypeAccountDAO().delete(finTypeAccount, type);
			}
			if (approveRec) {
				finTypeAccount.setRecordType(rcdType);
				finTypeAccount.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeAccount);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	private List<AuditDetail> setFinTypeAccountDetailsAuditData(FinanceType financeType, String auditTranType, String method) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccount(), new FinTypeAccount().getExcludeFields());
		for (int i = 0; i < financeType.getFinTypeAccounts().size(); i++) {
			FinTypeAccount finTypeAccount = financeType.getFinTypeAccounts().get(i);
			finTypeAccount.setFinType(financeType.getFinType());
			finTypeAccount.setWorkflowId(financeType.getWorkflowId());
			boolean isRcdType = false;
			if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				finTypeAccount.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
				        || finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			finTypeAccount.setRecordStatus(financeType.getRecordStatus());
			finTypeAccount.setUserDetails(financeType.getUserDetails());
			finTypeAccount.setLastMntOn(financeType.getLastMntOn());
			if (!StringUtils.trimToEmpty(finTypeAccount.getRecordType()).equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeAccount.getBefImage(), finTypeAccount));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getIncomeExpenseDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	
	private AuditDetail validationFinTypeAccount(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinTypeAccount finTypeAccount= (FinTypeAccount) auditDetail.getModelData();
		
		FinTypeAccount tempFinTypeAccount= null;
		if (finTypeAccount.isWorkflow()){
			tempFinTypeAccount = getFinTypeAccountDAO().getFinTypeAccountByID(finTypeAccount, "_Temp");
		}
		FinTypeAccount befFinTypeAccount= getFinTypeAccountDAO().getFinTypeAccountByID( finTypeAccount, "");
		
		FinTypeAccount oldFinTypeAccountReference= finTypeAccount.getBefImage();
		
		
		String[] errParm= new String[1];
		String[] valueParm= new String[2];
		valueParm[0]=finTypeAccount.getFinCcy();
		valueParm[1]=finTypeAccount.getEvent();
		errParm[0]=PennantJavaUtil.getLabel("label_FinTypeAccountDialog_FinCcy.value")+":"+valueParm[0]+","+
				   PennantJavaUtil.getLabel("label_FinTypeAccountDialog_Event.value")+":"+valueParm[1];
		
		if (finTypeAccount.isNew()){ // for New record or new record into work flow
			
			if (!finTypeAccount.isWorkflow()){// With out Work flow only new records  
				if (befFinTypeAccount !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (finTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinTypeAccount !=null || tempFinTypeAccount!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinTypeAccount ==null || tempFinTypeAccount!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeAccount.isWorkflow()){	// With out Work flow for update and delete
			
				if (befFinTypeAccount ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (oldFinTypeAccountReference!=null && !oldFinTypeAccountReference.getLastMntOn().equals(befFinTypeAccount.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{
			
				if (tempFinTypeAccount==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
				
				if (oldFinTypeAccountReference!=null && !oldFinTypeAccountReference.getLastMntOn().equals(tempFinTypeAccount.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if(StringUtils.trimToEmpty(method).equals("doApprove") || !finTypeAccount.isWorkflow()){
			auditDetail.setBefImage(befFinTypeAccount);	
		}

		return auditDetail;
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

	public FinTypeAccountDAO getFinTypeAccountDAO() {
    	return finTypeAccountDAO;
    }

	public void setFinTypeAccountDAO(FinTypeAccountDAO finTypeAccountDAO) {
    	this.finTypeAccountDAO = finTypeAccountDAO;
    }
	
	@Override
	public FinTypeAccount getFinTypeAccount() {
		return getFinTypeAccountDAO().getFinTypeAccount();
	}

	@Override
	public FinTypeAccount getNewFinTypeAccount() {
		return getFinTypeAccountDAO().getNewFinTypeAccount();
	}
	
}

