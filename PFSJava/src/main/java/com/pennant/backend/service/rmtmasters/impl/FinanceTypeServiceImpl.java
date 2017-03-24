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
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeInsuranceDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.ProductAssetDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.FinTypeVasDetailValidation;
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
	private FinTypeFeesDAO finTypeFeesDAO;
	private FinTypeAccountingDAO finTypeAccountingDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private ProductAssetDAO productAssetDAO;
	private FinTypeInsuranceDAO	finTypeInsuranceDAO;
	private FinTypeVASProductsDAO 	finTypeVASProductsDAO;
	private TransactionEntryDAO transactionEntryDAO;
	// Validation Service Classes
	private FinTypeVasDetailValidation		finTypeVasDetailValidation;
	

	public FinanceTypeServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	public FinanceType getNewFinanceType() {			
		FinanceType financeType = new FinanceType();
		financeType.setFinCategory("");
		financeType.setNewRecord(true);

		List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt(financeType.isAllowRIAInvestment() ,"");
		for (int i = 0; i < accountEngineRules.size(); i++) {
			financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
		}		
		return financeType;

	}
	// To get finPurpose details by assestId 
	@Override
	public List<ProductAsset> getFinPurposeByAssetId(ArrayList<String> list, String type) {		
		return getProductAssetDAO().getFinPurposeByAssetId(list,type);
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
			tableType = "_Temp";
		}

		if (financeType.isNew()) {
			financeType.setId(getFinanceTypeDAO().save(financeType, tableType));
			auditHeader.getAuditDetail().setModelData(financeType);
			auditHeader.setAuditReference(financeType.getId());
		} else {
			getFinanceTypeDAO().update(financeType, tableType);
		}

		//Customer Accounts
		if (financeType.getFinTypeAccounts() != null  && financeType.getFinTypeAccounts().size() > 0) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeCustAccount");
			details = processFinTypeCustAccountDetails(financeType, details, tableType);
			auditDetails.addAll(details);
		}

		//Finance Type Fees
		if (financeType.getFinTypeFeesList() != null  && financeType.getFinTypeFeesList().size() > 0) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeFees");
			details = processFinTypeFeesDetails(financeType, details, tableType);
			auditDetails.addAll(details);
		}
		
		//Finance Type Accounting
		if (financeType.getFinTypeAccountingList() != null  && financeType.getFinTypeAccountingList().size() > 0) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeAccounting");
			details = processFinTypeAccountingDetails(financeType, details, tableType);
			auditDetails.addAll(details);
		}

		//FinTypeInsurance
		if (financeType.getFinTypeInsurances() != null && financeType.getFinTypeInsurances().size() > 0) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeInsurance");
			details = processFinTypeInsuranceDetails(financeType, details, tableType);
			auditDetails.addAll(details);
		}
		
		//FinVasTypeProduct Details
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
			details = processingVasProductDetailList(details, financeType.getFinAcType(), tableType);
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
		List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt(financeType.isAllowRIAInvestment() , "");
		for (int i = 0; i < accountEngineRules.size(); i++) {
			financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
		}
		financeType.setFinTypeAccounts(getFinTypeAccountDAO().getFinTypeAccountListByID(id, "_View"));
		financeType.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesListByID(id, "_View"));
		//FinTypeVasProduct Details
		financeType.setFinTypeVASProductsList(getFinTypeVASProductsDAO().getVASProductsByFinType(id,"_View"));
		financeType.setFinTypeAccountingList(getFinTypeAccountingDAO().getFinTypeAccountingListByID(id, "_View"));
		financeType.setFinTypeInsurances(getFinTypeInsuranceDAO().getFinTypeInsuranceListByID(id, "_View"));
		return financeType;
	}
	
	/**
	 * Method for Fetching List of Allowed VAS Products by Finance Type
	 * @return
	 */
	@Override
	public List<FinTypeVASProducts> getFinTypeVasProducts(String finType){
		return getFinTypeVASProductsDAO().getVASProductsByFinType(finType,"_AView");
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
		if(financeType != null) {
			List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt(financeType.isAllowRIAInvestment() , "");
			for (int i = 0; i < accountEngineRules.size(); i++) {
				financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
			}
			financeType.setFinTypeAccounts(getFinTypeAccountDAO().getFinTypeAccountListByID(id, "_AView"));
			financeType.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesListByID(id, "_AView"));
			financeType.setFinTypeAccountingList(getFinTypeAccountingDAO().getFinTypeAccountingListByID(id, "_AView"));
			financeType.setFinTypeInsurances(getFinTypeInsuranceDAO().getFinTypeInsuranceListByID(id, "_View"));
		}
		return financeType;
	}
	
	/**
	 * getApprovedFinanceTypeById fetch the details by using FinanceTypeDAO's
	 * getFinanceTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the RMTFinanceTypes.
	 * 
	 * @param finType
	 *            (String)
	 * @return FinanceType
	 */
	@Override
	public FinanceType getOrgFinanceTypeById(String finType) {
		return getFinanceTypeDAO().getOrgFinanceTypeByID(finType, "_ORGView");
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
		FinanceType financeType = new FinanceType();
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

				// Copy of Finance Workflow & Process Editor Details to Promotion by Finance Type(Product Code)
				if(StringUtils.isNotBlank(financeType.getProduct())){

					//Finance Workflow Details 
					List<FinanceWorkFlow> financeWorkFlowList = getFinanceWorkFlowDAO().getFinanceWorkFlowListById(financeType.getProduct(), "");
					
					if(financeWorkFlowList != null && !financeWorkFlowList.isEmpty()){
						for (FinanceWorkFlow financeWorkFlow : financeWorkFlowList) {

							financeWorkFlow.setFinType(financeType.getFinType());
							financeWorkFlow.setModuleName(PennantConstants.WORFLOW_MODULE_PROMOTION);
							financeWorkFlow.setVersion(0);
							financeWorkFlow.setLastMntBy(financeType.getLastMntBy());
							financeWorkFlow.setLastMntOn(financeType.getLastMntOn());
						}
						getFinanceWorkFlowDAO().saveList(financeWorkFlowList,"");

						//Process Editor Details
						List<FinanceReferenceDetail> refList = getFinanceReferenceDetailDAO().getFinanceRefListByFinType(
								financeType.getProduct(),"");
						for (FinanceReferenceDetail refDetail : refList) {
							refDetail.setFinType(financeType.getFinType());
							refDetail.setFinRefDetailId(Long.MIN_VALUE);
							refDetail.setVersion(0);
							refDetail.setLastMntBy(financeType.getLastMntBy());
							refDetail.setLastMntOn(financeType.getLastMntOn());
							getFinanceReferenceDetailDAO().save(refDetail, "");
						}
					}

				}

			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeType.setRecordType("");
				getFinanceTypeDAO().update(financeType, "");
			}
			if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get( "FinTypeCustAccount");
				details = processFinTypeCustAccountDetails(financeType,details, "");
				auditDetails.addAll(details);
			}
			if (financeType.getFinTypeFeesList() != null && financeType.getFinTypeFeesList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get( "FinTypeFees");
				details = processFinTypeFeesDetails(financeType,details, "");
				auditDetails.addAll(details);
			}
			//FinTypeVasProduct Details
			if (financeType.getFinTypeVASProductsList() != null && financeType.getFinTypeVASProductsList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
				details = processingVasProductDetailList(details, financeType.getFinAcType(),"");
				auditDetails.addAll(details);
			}
			if (financeType.getFinTypeAccountingList() != null && financeType.getFinTypeAccountingList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeAccounting");
				details = processFinTypeAccountingDetails(financeType,details, "");
				auditDetails.addAll(details);
			}
			if (financeType.getFinTypeInsurances() != null && financeType.getFinTypeInsurances().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeInsurance");
				details = processFinTypeInsuranceDetails(financeType, details, "");
				auditDetails.addAll(details);
			}
		}

		getFinanceTypeDAO().delete(financeType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		//List
		auditHeader.setAuditDetails(deleteChilds(financeType, "_Temp", auditHeader.getAuditTranType()));
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
		getFinanceTypeDAO().delete(financeType, "_Temp");
		//List
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds( financeType, "_Temp", auditHeader.getAuditTranType())));
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
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = financeType.getUserDetails().getUsrLanguage();
		

		//List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader,auditHeader.getUsrLanguage(),method));
		
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
			details = getFinTypeVasDetailValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
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
		
		if (financeType.isPlanEMIHAlw() && financeType.isStepFinance()) {
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "30573", errParm, null));
		}
		
		if(financeType.isPlanEMIHAlw() && financeType.isFinIsAlwMD()){
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "30574", errParm, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))	|| !financeType.isWorkflow()) {
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
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		//HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		//String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeType.isWorkflow()) {
				//auditTranType = PennantConstants.TRAN_WF;
			}
		}
		
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

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method)
				|| "doReject".equals(method)) {
			if (financeType.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
			auditDetailMap.put("FinTypeCustAccount", setFinTypeCustAccountsAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeCustAccount"));
		}
		if (financeType.getFinTypeFeesList() != null && financeType.getFinTypeFeesList().size() > 0) {
			auditDetailMap.put("FinTypeFees", setFinTypeFeesAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeFees"));
		}
		//Finance Type VAS Details
		if (financeType.getFinTypeVASProductsList()!= null && financeType.getFinTypeVASProductsList().size() > 0) {
			auditDetailMap.put("FinTypeVASProducts", setFinTypeVasProcuctAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeVASProducts"));
		}
		if (financeType.getFinTypeAccountingList() != null && financeType.getFinTypeAccountingList().size() > 0) {
			auditDetailMap.put("FinTypeAccounting", setFinTypeAccountingAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeAccounting"));
		}
		if (financeType.getFinTypeInsurances() != null && financeType.getFinTypeInsurances().size() > 0) {
			auditDetailMap.put("FinTypeInsurance",setFinTypeInsuranceDetailsAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeInsurance"));
		}
		
		financeType.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeType);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof FinTypeAccount) {
				rcdType = ((FinTypeAccount) object).getRecordType();
			}else if (object instanceof FinTypeFees) {
				rcdType = ((FinTypeFees) object).getRecordType();
			}else if (object instanceof FinTypeAccounting) {
				rcdType = ((FinTypeAccounting) object).getRecordType();
			}else if (object instanceof FinTypeInsurances) {
				rcdType = ((FinTypeInsurances) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(),
					object));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> deleteChilds(FinanceType financeType, String tableType,String auditTranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (financeType.getFinTypeAccounts() != null && !financeType.getFinTypeAccounts().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccount(),new FinTypeAccount().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeAccounts().size(); i++) {
				FinTypeAccount finTypeAccount = financeType.getFinTypeAccounts().get(i);
				if (StringUtils.isNotEmpty(finTypeAccount.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeAccount.getBefImage(), finTypeAccount));
				}
			}
			getFinTypeAccountDAO().deleteByFinType(financeType.getFinType(), tableType);
		}
		if (financeType.getFinTypeFeesList()!= null && !financeType.getFinTypeFeesList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeFees(),new FinTypeFees().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeFeesList().size(); i++) {
				FinTypeFees finTypeFees = financeType.getFinTypeFeesList().get(i);
				if (StringUtils.isNotEmpty(finTypeFees.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeFees.getBefImage(), finTypeFees));
				}
			}
			getFinTypeFeesDAO().deleteByFinType(financeType.getFinType(), tableType);
		}
		if (financeType.getFinTypeAccountingList() != null && !financeType.getFinTypeAccountingList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccounting(),new FinTypeAccounting().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeAccountingList().size(); i++) {
				FinTypeAccounting finTypeAccounting = financeType.getFinTypeAccountingList().get(i);
				if (StringUtils.isNotEmpty(finTypeAccounting.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeAccounting.getBefImage(), finTypeAccounting));
				}
			}
			getFinTypeAccountingDAO().deleteByFinType(financeType.getFinType(), tableType);
		}
		
		//Finance Type VAS Details
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeVASProducts(),new FinTypeVASProducts().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeVASProductsList().size(); i++) {
				FinTypeVASProducts finTypeVASProducts = financeType.getFinTypeVASProductsList().get(i);
				if (StringUtils.isNotEmpty(finTypeVASProducts.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeVASProducts.getBefImage(), finTypeVASProducts));
				}
			}
			getFinTypeVASProductsDAO().deleteList(financeType.getFinType(), tableType);
		}
		if (financeType.getFinTypeInsurances() != null && !financeType.getFinTypeInsurances().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeInsurances(),new FinTypeInsurances().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeInsurances().size(); i++) {
				FinTypeInsurances finTypeInsurance = financeType.getFinTypeInsurances().get(i);
				if (StringUtils.isNotEmpty(finTypeInsurance.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeInsurance.getBefImage(), finTypeInsurance));
				}
			}
			getFinTypeInsuranceDAO().deleteByFinType(financeType.getFinType(), tableType);
		}
		return auditDetails;
	}

	private List<ErrorDetails> validateChilds(AuditHeader auditHeader,String usrLanguage,String method){
		List<ErrorDetails> errorDetails=new ArrayList<ErrorDetails>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails=null;
		//FinTypeAccount
		if (financeType.getAuditDetailMap().get("FinTypeCustAccount")!=null) {
			auditDetails= financeType.getAuditDetailMap().get("FinTypeCustAccount");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details=validationFinTypeCustAccounts(auditDetail, usrLanguage, method).getErrorDetails();
				if (details!=null) {
					errorDetails.addAll(details);
				}
			}
		}
		if (financeType.getAuditDetailMap().get("FinTypeFees")!=null) {
			auditDetails= financeType.getAuditDetailMap().get("FinTypeFees");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details=validationFinTypeFees(auditDetail, usrLanguage, method).getErrorDetails();
				if (details!=null) {
					errorDetails.addAll(details);
				}
			}
		}
		if (financeType.getAuditDetailMap().get("FinTypeAccounting")!=null) {
			auditDetails= financeType.getAuditDetailMap().get("FinTypeAccounting");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details=validationFinTypeAccounting(auditDetail, usrLanguage, method).getErrorDetails();
				if (details!=null) {
					errorDetails.addAll(details);
				}
			}
		}
		if (financeType.getAuditDetailMap().get("FinTypeInsurance") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeInsurance");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details = validationFinTypeInsurance(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		return errorDetails;
	}


	private List<AuditDetail> processFinTypeCustAccountDetails(FinanceType financeType,List<AuditDetail> auditDetails, String type) {
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
			if (StringUtils.isEmpty(type)) {
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
	private List<AuditDetail> setFinTypeCustAccountsAuditData(FinanceType financeType, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccount(), new FinTypeAccount().getExcludeFields());
		for (int i = 0; i < financeType.getFinTypeAccounts().size(); i++) {
			FinTypeAccount finTypeAccount = financeType.getFinTypeAccounts().get(i);

			if (StringUtils.isEmpty(finTypeAccount.getRecordType())) {
				continue;
			}

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
			if ("saveOrUpdate".equals(method) && isRcdType ) {
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

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeAccount.getBefImage(), finTypeAccount));
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

	private AuditDetail validationFinTypeCustAccounts(AuditDetail auditDetail,String usrLanguage,String method){
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

				if (tempFinTypeAccount!=null  && oldFinTypeAccountReference!=null && !oldFinTypeAccountReference.getLastMntOn().equals(tempFinTypeAccount.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeAccount.isWorkflow()){
			auditDetail.setBefImage(befFinTypeAccount);	
		}

		return auditDetail;
	}

	
	private List<AuditDetail> processFinTypeFeesDetails(FinanceType financeType,List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeFees finTypeFees = (FinTypeFees) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeFees.setRoleCode("");
				finTypeFees.setNextRoleCode("");
				finTypeFees.setTaskId("");
				finTypeFees.setNextTaskId("");
			}
			finTypeFees.setFinType(financeType.getFinType());
			finTypeFees.setWorkflowId(0);
			if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeFees.isNewRecord()) {
				saveRecord = true;
				if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeFees.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeFees.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeFees.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeFees.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeFees.getRecordType();
				recordStatus = finTypeFees.getRecordStatus();
				finTypeFees.setRecordType("");
				finTypeFees.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeFeesDAO().save(finTypeFees, type);
			}
			if (updateRecord) {
				getFinTypeFeesDAO().update(finTypeFees, type);
			}
			if (deleteRecord) {
				getFinTypeFeesDAO().delete(finTypeFees, type);
			}
			if (approveRec) {
				finTypeFees.setRecordType(rcdType);
				finTypeFees.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeFees);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	private List<AuditDetail> setFinTypeFeesAuditData(FinanceType financeType, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeFees(), new FinTypeFees().getExcludeFields());
		for (int i = 0; i < financeType.getFinTypeFeesList().size(); i++) {
			FinTypeFees finTypeFees = financeType.getFinTypeFeesList().get(i);

			if (StringUtils.isEmpty(finTypeFees.getRecordType())) {
				continue;
			}

			finTypeFees.setFinType(financeType.getFinType());
			finTypeFees.setWorkflowId(financeType.getWorkflowId());
			boolean isRcdType = false;
			if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeFees.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeFees.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeFees.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType ) {
				finTypeFees.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeFees.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			finTypeFees.setRecordStatus(financeType.getRecordStatus());
			finTypeFees.setUserDetails(financeType.getUserDetails());
			finTypeFees.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeFees.getBefImage(), finTypeFees));
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

	private AuditDetail validationFinTypeFees(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinTypeFees finTypeFees= (FinTypeFees) auditDetail.getModelData();

		FinTypeFees tempFinTypeFees= null;
		if (finTypeFees.isWorkflow()){
			tempFinTypeFees = getFinTypeFeesDAO().getFinTypeFeesByID(finTypeFees, "_Temp");
		}
		FinTypeFees befFinTypeFees= getFinTypeFeesDAO().getFinTypeFeesByID( finTypeFees, "");

		FinTypeFees oldFinTypeFeesReference= finTypeFees.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[2];
		valueParm[0]=finTypeFees.getFeeTypeCode();
		valueParm[1]=finTypeFees.getFinEvent();
		errParm[0]=PennantJavaUtil.getLabel("label_FinTypeFeesDialog_FeeType.value")+":"+valueParm[0]+","+
				PennantJavaUtil.getLabel("label_FinTypeFeesDialog_FinEvent.value")+":"+valueParm[1];

		if (finTypeFees.isNew()){ // for New record or new record into work flow

			if (!finTypeFees.isWorkflow()){// With out Work flow only new records  
				if (befFinTypeFees !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (finTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinTypeFees !=null || tempFinTypeFees!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinTypeFees ==null || tempFinTypeFees!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeFees.isWorkflow()){	// With out Work flow for update and delete

				if (befFinTypeFees ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (oldFinTypeFeesReference!=null && !oldFinTypeFeesReference.getLastMntOn().equals(befFinTypeFees.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{

				if (tempFinTypeFees==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}

				if (tempFinTypeFees!=null  && oldFinTypeFeesReference!=null && !oldFinTypeFeesReference.getLastMntOn().equals(tempFinTypeFees.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeFees.isWorkflow()){
			auditDetail.setBefImage(befFinTypeFees);	
		}

		return auditDetail;
	}
	

	private List<AuditDetail> processFinTypeAccountingDetails(FinanceType financeType,List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeAccounting.setRoleCode("");
				finTypeAccounting.setNextRoleCode("");
				finTypeAccounting.setTaskId("");
				finTypeAccounting.setNextTaskId("");
			}
			finTypeAccounting.setFinType(financeType.getFinType());
			finTypeAccounting.setWorkflowId(0);
			if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeAccounting.isNewRecord()) {
				saveRecord = true;
				if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeAccounting.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeAccounting.getRecordType();
				recordStatus = finTypeAccounting.getRecordStatus();
				finTypeAccounting.setRecordType("");
				finTypeAccounting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeAccountingDAO().save(finTypeAccounting, type);
			}
			if (updateRecord) {
				getFinTypeAccountingDAO().update(finTypeAccounting, type);
			}
			if (deleteRecord) {
				getFinTypeAccountingDAO().delete(finTypeAccounting, type);
			}
			if (approveRec) {
				finTypeAccounting.setRecordType(rcdType);
				finTypeAccounting.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeAccounting);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	private List<AuditDetail> setFinTypeAccountingAuditData(FinanceType financeType, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccounting(), new FinTypeAccounting().getExcludeFields());
		for (int i = 0; i < financeType.getFinTypeAccountingList().size(); i++) {
			FinTypeAccounting finTypeAccounting = financeType.getFinTypeAccountingList().get(i);

			if (StringUtils.isEmpty(finTypeAccounting.getRecordType())) {
				continue;
			}

			finTypeAccounting.setFinType(financeType.getFinType());
			finTypeAccounting.setWorkflowId(financeType.getWorkflowId());
			boolean isRcdType = false;
			if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType ) {
				finTypeAccounting.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			finTypeAccounting.setRecordStatus(financeType.getRecordStatus());
			finTypeAccounting.setUserDetails(financeType.getUserDetails());
			finTypeAccounting.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeAccounting.getBefImage(), finTypeAccounting));
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

	private AuditDetail validationFinTypeAccounting(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinTypeAccounting finTypeAccounting= (FinTypeAccounting) auditDetail.getModelData();

		FinTypeAccounting tempFinTypeAccounting= null;
		if (finTypeAccounting.isWorkflow()){
			tempFinTypeAccounting = getFinTypeAccountingDAO().getFinTypeAccountingByID(finTypeAccounting, "_Temp");
		}
		FinTypeAccounting befFinTypeAccounting= getFinTypeAccountingDAO().getFinTypeAccountingByID( finTypeAccounting, "");

		FinTypeAccounting oldFinTypeAccounting= finTypeAccounting.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[2];
		valueParm[0]=finTypeAccounting.getEvent();
		valueParm[1]=finTypeAccounting.getLovDescEventAccountingName();
		errParm[0]=PennantJavaUtil.getLabel("label_FinTypeAccountingDialog_Event.value")+":"+valueParm[0]+","+
				PennantJavaUtil.getLabel("label_FinTypeAccountingDialog_AccountSetCode.value")+":"+valueParm[1];

		if (finTypeAccounting.isNew()){ // for New record or new record into work flow

			if (!finTypeAccounting.isWorkflow()){// With out Work flow only new records  
				if (befFinTypeAccounting !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (finTypeAccounting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinTypeAccounting !=null || tempFinTypeAccounting!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinTypeAccounting ==null || tempFinTypeAccounting!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeAccounting.isWorkflow()){	// With out Work flow for update and delete

				if (befFinTypeAccounting ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (oldFinTypeAccounting!=null && !oldFinTypeAccounting.getLastMntOn().equals(befFinTypeAccounting.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{

				if (tempFinTypeAccounting==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}

				if (tempFinTypeAccounting!=null && oldFinTypeAccounting!=null && !oldFinTypeAccounting.getLastMntOn().equals(tempFinTypeAccounting.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeAccounting.isWorkflow()){
			auditDetail.setBefImage(befFinTypeAccounting);	
		}

		return auditDetail;
	}

	private List<AuditDetail> processFinTypeInsuranceDetails(FinanceType financeType, List<AuditDetail> auditDetails,
			String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeInsurances finTypeInsurance = (FinTypeInsurances) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeInsurance.setRoleCode("");
				finTypeInsurance.setNextRoleCode("");
				finTypeInsurance.setTaskId("");
				finTypeInsurance.setNextTaskId("");
			}
			finTypeInsurance.setFinType(financeType.getFinType());
			finTypeInsurance.setWorkflowId(0);
			if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeInsurance.isNewRecord()) {
				saveRecord = true;
				if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeInsurance.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeInsurance.getRecordType();
				recordStatus = finTypeInsurance.getRecordStatus();
				finTypeInsurance.setRecordType("");
				finTypeInsurance.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeInsuranceDAO().save(finTypeInsurance, type);
			}
			if (updateRecord) {
				getFinTypeInsuranceDAO().update(finTypeInsurance, type);
			}
			if (deleteRecord) {
				getFinTypeInsuranceDAO().delete(finTypeInsurance, type);
			}
			if (approveRec) {
				finTypeInsurance.setRecordType(rcdType);
				finTypeInsurance.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeInsurance);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> setFinTypeInsuranceDetailsAuditData(FinanceType financeType, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil
				.getFieldDetails(new FinTypeInsurances(), new FinTypeInsurances().getExcludeFields());
		for (int i = 0; i < financeType.getFinTypeInsurances().size(); i++) {
			FinTypeInsurances finTypeInsurance = financeType.getFinTypeInsurances().get(i);

			if (StringUtils.isEmpty(finTypeInsurance.getRecordType())) {
				continue;
			}

			finTypeInsurance.setFinType(financeType.getFinType());
			finTypeInsurance.setWorkflowId(financeType.getWorkflowId());
			boolean isRcdType = false;
			if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeInsurance.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTypeInsurance.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeInsurance.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			finTypeInsurance.setRecordStatus(financeType.getRecordStatus());
			finTypeInsurance.setUserDetails(financeType.getUserDetails());
			finTypeInsurance.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finTypeInsurance.getBefImage(), finTypeInsurance));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getIncomeExpenseDetailDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validationFinTypeInsurance(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinTypeInsurances finTypeInsurance = (FinTypeInsurances) auditDetail.getModelData();

		FinTypeInsurances tempFinTypeInsurance = null;
		if (finTypeInsurance.isWorkflow()) {
			tempFinTypeInsurance = getFinTypeInsuranceDAO().getFinTypeInsuranceByID(finTypeInsurance, "_Temp");
		}
		FinTypeInsurances befFinTypeInsurance = getFinTypeInsuranceDAO().getFinTypeInsuranceByID(finTypeInsurance, "");

		FinTypeInsurances oldFinTypeinsurance = finTypeInsurance.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[2];
		valueParm[0] = finTypeInsurance.getFinType();
		valueParm[1] = finTypeInsurance.getInsuranceType();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeAccountDialog_FinCcy.value") + ":" + valueParm[0] + ","
				+ PennantJavaUtil.getLabel("label_FinTypeAccountDialog_Event.value") + ":" + valueParm[1];

		if (finTypeInsurance.isNew()) { // for New record or new record into work flow

			if (!finTypeInsurance.isWorkflow()) {// With out Work flow only new records  
				if (befFinTypeInsurance != null) { // Record Already Exists in the table then error  
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypeInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinTypeInsurance != null || tempFinTypeInsurance != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinTypeInsurance == null || tempFinTypeInsurance != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeInsurance.isWorkflow()) { // With out Work flow for update and delete

				if (befFinTypeInsurance == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeinsurance != null
							&& !oldFinTypeinsurance.getLastMntOn().equals(befFinTypeInsurance.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempFinTypeInsurance == null) { // if records not exists in the Work flow table 
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypeInsurance != null && oldFinTypeinsurance != null
						&& !oldFinTypeinsurance.getLastMntOn().equals(tempFinTypeInsurance.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeInsurance.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeInsurance);
		}

		return auditDetail;
	}
	
	/**
	 * Method For Preparing List of AuditDetails for Check List for Finance Type VAS Details
	 * 
	 * @param auditDetails
	 * @param financeType
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingVasProductDetailList(List<AuditDetail> auditDetails,
			String financeType, String type) {
		logger.debug("Entering");


		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeVASProducts finTypeVASProducts = (FinTypeVASProducts) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeVASProducts.setRoleCode("");
				finTypeVASProducts.setNextRoleCode("");
				finTypeVASProducts.setTaskId("");
				finTypeVASProducts.setNextTaskId("");
			}

			finTypeVASProducts.setWorkflowId(0);

			if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeVASProducts.isNewRecord()) {
				saveRecord = true;
				if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeVASProducts.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeVASProducts.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeVASProducts.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeVASProducts.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeVASProducts.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finTypeVASProducts.getRecordType();
				recordStatus = finTypeVASProducts.getRecordStatus();
				finTypeVASProducts.setRecordType("");
				finTypeVASProducts.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeVASProductsDAO().save(finTypeVASProducts, type);
			}

			if (updateRecord) {
				getFinTypeVASProductsDAO().update(finTypeVASProducts,type);
			}

			if (deleteRecord) {
				getFinTypeVASProductsDAO().delete(finTypeVASProducts.getFinType(),finTypeVASProducts.getVasProduct(), type);
			}

			if (approveRec) {
				finTypeVASProducts.setRecordType(rcdType);
				finTypeVASProducts.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeVASProducts);
		}
		
		logger.debug("Leaving");
		return auditDetails;

	}
	
	/**
	 * Methods for Creating List Finance Type VAS Details of Audit Details with detailed fields
	 * 
	 * @param financeType
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinTypeVasProcuctAuditData(FinanceType financeType, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		String[] fields = PennantJavaUtil.getFieldDetails(finTypeVASProducts, finTypeVASProducts.getExcludeFields());

		for (int i = 0; i < financeType.getFinTypeVASProductsList().size(); i++) {
			FinTypeVASProducts finTypeVASProduct = financeType.getFinTypeVASProductsList().get(i);

			if (StringUtils.isEmpty(finTypeVASProduct.getRecordType())) {
				continue;
			}

			finTypeVASProduct.setFinType(financeType.getFinType());
			finTypeVASProduct.setWorkflowId(financeType.getWorkflowId());

			boolean isRcdType = false;

			if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeVASProduct.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeVASProduct.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeType.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeVASProduct.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType ) {
				finTypeVASProduct.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeVASProduct.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finTypeVASProduct.setRecordStatus(financeType.getRecordStatus());
			finTypeVASProduct.setUserDetails(financeType.getUserDetails());
			finTypeVASProduct.setLastMntOn(financeType.getLastMntOn());
			finTypeVASProduct.setLastMntBy(financeType.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeVASProduct.getBefImage(), finTypeVASProduct));
		}

		logger.debug("Leaving");
		return auditDetails;
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
	
	@Override
	public String getAllowedCollateralTypes(String finType) {
		return getFinanceTypeDAO().getAllowedCollateralTypes(finType);
	}

	/**
	 * Fetch total number of records from FinanceTypes
	 * 
	 * @param finType
	 */
	@Override
	public int getFinanceTypeCountById(String finType) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getFinanceTypeCountById(finType);
	}

	public FinTypeAccountDAO getFinTypeAccountDAO() {
		return finTypeAccountDAO;
	}
	public void setFinTypeAccountDAO(FinTypeAccountDAO finTypeAccountDAO) {
		this.finTypeAccountDAO = finTypeAccountDAO;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}
	
	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}
	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}
	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	@Override
	public FinTypeAccount getFinTypeAccount() {
		return getFinTypeAccountDAO().getFinTypeAccount();
	}

	@Override
	public FinTypeAccount getNewFinTypeAccount() {
		return getFinTypeAccountDAO().getNewFinTypeAccount();
	}

	@Override
	public FinTypeAccounting getNewFinTypeAccounting() {
		return getFinTypeAccountingDAO().getNewFinTypeAccounting();
	}
	
	@Override
	public FinTypeFees getFinTypeFees() {
		return getFinTypeFeesDAO().getFinTypeFees();
	}

	@Override
	public FinTypeFees getNewFinTypeFees() {
		return getFinTypeFeesDAO().getNewFinTypeFees();
	}
	
	/**
	 * Fetch the FinanceTypes Based on the Product Code
	 * 
	 * @param productCode
	 */
	@Override
	public List<FinanceType> getFinanceTypeByProduct(String productCode) {
		return getFinanceTypeDAO().getFinanceTypeByProduct(productCode);
	}
	
	/**
	 * Fetch total number of records from FinanceTypes(Promotion)
	 * 
	 * @param finType
	 */
	@Override
	public int getPromotionTypeCountById(String finType) {
		int promotionCount = 0;
		logger.debug("Entering");
		promotionCount =  getFinanceTypeDAO().getPromotionTypeCountById(finType);
		logger.debug("Leaving");
		return promotionCount;
	}

	/**
	 * Fetch record count of Promotions by using product code
	 * 
	 * @param productCode
	 */
	@Override
	public int getProductCountById(String productCode) {
		int productCount = 0;
		logger.debug("Entering");
		productCount = getFinanceTypeDAO().getProductCountById(productCode);
		logger.debug("Leaving");
		return productCount;
	}
	
	/**
	 * Validate finance type fees against fees configured in accounting
	 * 
	 * @param productCode
	 */
	@Override
	public List<String> fetchFeeCodeList(Long accountSetId) {
		logger.debug("Entering");
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.add(accountSetId);
		List<String> feeCodeList = getTransactionEntryDAO().getFeeCodeList(accSetIdList);
		logger.debug("Leaving");
		return  feeCodeList;
	}

	/**
	 * 
	 */
	@Override
	public String getFinanceTypeDesc(String productCode) {
		String finTypeDesc = "";
		logger.debug("Entering");
		finTypeDesc = getFinanceTypeDAO().getFinanceTypeDesc(productCode);
		logger.debug("Leaving");
		return finTypeDesc;
	}
	
	public ProductAssetDAO getProductAssetDAO() {
		return productAssetDAO;
	}

	public void setProductAssetDAO(ProductAssetDAO productAssetDAO) {
		this.productAssetDAO = productAssetDAO;
	}

	public FinTypeInsuranceDAO getFinTypeInsuranceDAO() {
		return finTypeInsuranceDAO;
	}

	public void setFinTypeInsuranceDAO(FinTypeInsuranceDAO finTypeInsuranceDAO) {
		this.finTypeInsuranceDAO = finTypeInsuranceDAO;
	}
	
	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}
	
	// Validation Service Classes

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}
	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	/**
	 * FinTypeVasDetail Validation
	 * 
	 * @return
	 */
	public FinTypeVasDetailValidation getFinTypeVasDetailValidation() {
		if (finTypeVasDetailValidation == null) {
			this.finTypeVasDetailValidation = new FinTypeVasDetailValidation(finTypeVASProductsDAO);
		}
		return this.finTypeVasDetailValidation;
	}

}
