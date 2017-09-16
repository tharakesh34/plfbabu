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
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.FinTypeVasDetailValidation;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.service.rmtmasters.FinTypeFeesService;
import com.pennant.backend.service.rmtmasters.FinTypeInsurancesService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.FinanceConfigCache;

/**
 * Service implementation for methods that depends on <b>FinanceType</b>.<br>
 */
public class FinanceTypeServiceImpl extends GenericService<FinanceType> implements FinanceTypeService {

	private static final Logger logger = Logger.getLogger(FinanceTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;
	private AccountingSetDAO accountingSetDAO;
	private FinTypeAccountDAO finTypeAccountDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private ProductAssetDAO productAssetDAO;
	private FinTypeVASProductsDAO 	finTypeVASProductsDAO;
	private TransactionEntryDAO transactionEntryDAO;
	// Validation Service Classes
	private FinTypeVasDetailValidation		finTypeVasDetailValidation;
	
	private FinTypeFeesService finTypeFeesService;
	private FinTypeInsurancesService finTypeInsurancesService;
	private FinTypeAccountingService finTypeAccountingService;
	private FinTypePartnerBankService finTypePartnerBankService;

	public FinanceTypeServiceImpl() {
		super();
	}

	@Override
	public FinanceType getNewFinanceType() {
		logger.debug("Entering");
		
		FinanceType financeType = new FinanceType();
		financeType.setFinCategory("");
		financeType.setNewRecord(true);

		List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt(financeType.isAllowRIAInvestment() ,"");
		for (int i = 0; i < accountEngineRules.size(); i++) {
			financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
		}		

		logger.debug("Leaving");
		
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
		
		if (StringUtils.isEmpty(tableType)) {
			FinanceConfigCache.clearFinanceTypeCache(financeType.getFinType());
		}
		
		//Customer Accounts
		if (financeType.getFinTypeAccounts() != null  && financeType.getFinTypeAccounts().size() > 0) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeCustAccount");
			details = processFinTypeCustAccountDetails(financeType, details, tableType);
			auditDetails.addAll(details);
		}

		// Finance Type Fees
		if (financeType.getFinTypeFeesList() != null && financeType.getFinTypeFeesList().size() > 0) {
			List<AuditDetail> feeDetails = financeType.getAuditDetailMap().get("FinTypeFees");
			feeDetails = this.finTypeFeesService.processFinTypeFeesDetails(feeDetails, tableType);
			auditDetails.addAll(feeDetails);
		}
		
		// Finance Type Insurances
		if (financeType.getFinTypeInsurances() != null && financeType.getFinTypeInsurances().size() > 0) {
			List<AuditDetail> insuranceDetails = financeType.getAuditDetailMap().get("FinTypeInsurance");
			insuranceDetails = this.finTypeInsurancesService.processFinTypeInsuranceDetails(insuranceDetails, tableType);
			auditDetails.addAll(insuranceDetails);
		}
		
		// Finance Type Accounting
		if (financeType.getFinTypeAccountingList() != null && financeType.getFinTypeAccountingList().size() > 0) {
			List<AuditDetail> accountingDetails = financeType.getAuditDetailMap().get("FinTypeAccounting");
			accountingDetails = this.finTypeAccountingService.processFinTypeAccountingDetails(accountingDetails, tableType);
			auditDetails.addAll(accountingDetails);
		}
		
		//FinVasTypeProduct Details
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
			details = processingVasProductDetailList(details, financeType.getFinAcType(), tableType);
			auditDetails.addAll(details);
		}

		// FinTypePartnerBank
		if (financeType.getFinTypePartnerBankList() != null && financeType.getFinTypePartnerBankList().size() > 0) {
			List<AuditDetail> partnerBankDetails = financeType.getAuditDetailMap().get("FinTypePartnerBank");
			partnerBankDetails = this.finTypePartnerBankService.processFinTypePartnerBankDetails(partnerBankDetails, tableType);
			auditDetails.addAll(partnerBankDetails);
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
		FinanceConfigCache.clearFinanceTypeCache(financeType.getFinType());
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(financeType, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * It fetches the records from RMTFinanceType_View and other details
	 * 
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeById(String finType) {
		logger.debug("Entering");
		
		FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(finType, "_View");		
	
		if (financeType != null) {
			List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt(financeType.isAllowRIAInvestment() , "");
			for (int i = 0; i < accountEngineRules.size(); i++) {
				financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
			}
			financeType.setFinTypeAccounts(getFinTypeAccountDAO().getFinTypeAccountListByID(finType, "_View"));
			//FinTypeVasProduct Details
			financeType.setFinTypeVASProductsList(getFinTypeVASProductsDAO().getVASProductsByFinType(finType,"_View"));
			financeType.setFinTypeFeesList(getFinTypeFeesService().getFinTypeFeesById(finType, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypeInsurances(getFinTypeInsurancesService().getFinTypeInsuranceListByID(finType, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypeAccountingList(getFinTypeAccountingService().getFinTypeAccountingListByID(finType, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypePartnerBankList(getFinTypePartnerBankService().getFinTypePartnerBanksList(finType, "_View"));
		}
		
		logger.debug("Leaving");
		
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
	 * It fetches the approved records from RMTFinanceTypesa and other details
	 * 
	 * @param id
	 *            (String)
	 * @return FinanceType
	 */
	public FinanceType getApprovedFinanceTypeById(String finType) {
		logger.debug("Entering");
		
		FinanceType financeType =  getFinanceTypeDAO().getFinanceTypeByID(finType, "_AView");
		if(financeType != null) {
			List<AccountingSet> accountEngineRules=getAccountingSetDAO().getListAERuleBySysDflt(financeType.isAllowRIAInvestment() , "");
			for (int i = 0; i < accountEngineRules.size(); i++) {
				financeType.setLovDescAERule(accountEngineRules.get(i).getEventCode(), accountEngineRules.get(i));
			}
			financeType.setFinTypeAccounts(getFinTypeAccountDAO().getFinTypeAccountListByID(finType, "_AView"));
			financeType.setFinTypeFeesList(getFinTypeFeesService().getApprovedFinTypeFeesById(finType, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypeInsurances(getFinTypeInsurancesService().getApprovedFinTypeInsuranceListByID(finType, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypeAccountingList(getFinTypeAccountingService().getApprovedFinTypeAccountingListByID(finType, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypePartnerBankList(getFinTypePartnerBankService().getFinTypePartnerBanksList(finType, "_AView"));
		}
		
		logger.debug("Leaving");
		
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
	 * It fetches the approved records from RMTFinanceTypes
	 * @param String finType
	 * @return FinanceType
	 */
	@Override
	public FinanceType getApprovedFinanceType(String finType) {
		return FinanceConfigCache.getFinanceType(finType);
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
					List<FinanceWorkFlow> financeWorkFlowList = getFinanceWorkFlowDAO().getFinanceWorkFlowListById(
							financeType.getProduct(), PennantConstants.WORFLOW_MODULE_FINANCE, "");
					
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
			FinanceConfigCache.clearFinanceTypeCache(financeType.getFinType());
			
			if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get( "FinTypeCustAccount");
				details = processFinTypeCustAccountDetails(financeType,details, "");
				auditDetails.addAll(details);
			}
			//FinTypeVasProduct Details
			if (financeType.getFinTypeVASProductsList() != null && financeType.getFinTypeVASProductsList().size() > 0) {
				List<AuditDetail> details = financeType.getAuditDetailMap().get("FinTypeVASProducts");
				details = processingVasProductDetailList(details, financeType.getFinAcType(),"");
				auditDetails.addAll(details);
			}
			// Fees
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> feeDetails = financeType.getAuditDetailMap().get("FinTypeFees");

				if (feeDetails != null && !feeDetails.isEmpty()) {
					feeDetails = this.finTypeFeesService.processFinTypeFeesDetails(feeDetails, "");
					auditDetails.addAll(feeDetails);
				}
			}
			// Insurances
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> insuranceDetails = financeType.getAuditDetailMap().get("FinTypeInsurance");

				if (insuranceDetails != null && !insuranceDetails.isEmpty()) {
					insuranceDetails = this.finTypeInsurancesService.processFinTypeInsuranceDetails(insuranceDetails,
							"");
					auditDetails.addAll(insuranceDetails);
				}
			}
			// FinTypePartnerBank
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> finTypePartnerBankDetails = financeType.getAuditDetailMap().get("FinTypePartnerBank");
				
				if (finTypePartnerBankDetails != null && !finTypePartnerBankDetails.isEmpty()) {
					finTypePartnerBankDetails = this.finTypePartnerBankService.processFinTypePartnerBankDetails(finTypePartnerBankDetails,
							"");
					auditDetails.addAll(finTypePartnerBankDetails);
				}
			}
			// Accounting
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> accountingDetails = financeType.getAuditDetailMap().get("FinTypeAccounting");

				if (accountingDetails != null && !accountingDetails.isEmpty()) {
					accountingDetails = this.finTypeAccountingService.processFinTypeAccountingDetails(
							accountingDetails, "");
					auditDetails.addAll(accountingDetails);
				}
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

		// List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

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

		if (financeType.isNew()) { // for New record or new record into work flow
			if (!financeType.isWorkflow()) {// With out Work flow only new records
				if (befFinanceType != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (financeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinanceType != null || tempFinanceType != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceType == null || tempFinanceType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeType.isWorkflow()) { // With out Work flow for update and delete
				if (befFinanceType == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {
					if (oldFinanceType != null
							&& !oldFinanceType.getLastMntOn().equals(befFinanceType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {
				if (tempFinanceType == null) { // if records not exists in the Work flow table
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
		
		/*if (financeType.isPlanEMIHAlw() && financeType.isStepFinance()) {
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "30573", errParm, null));
		}*/
		
		/*if(financeType.isPlanEMIHAlw() && financeType.isFinIsAlwMD()){
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "30574", errParm, null));
		}*/

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
		logger.debug("Entering");
		
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
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	//=================================== List maintain
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeType.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		if (financeType.getFinTypeAccounts() != null && financeType.getFinTypeAccounts().size() > 0) {
			auditDetailMap.put("FinTypeCustAccount", setFinTypeCustAccountsAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeCustAccount"));
		}
		// Fees
		if (financeType.getFinTypeFeesList() != null && financeType.getFinTypeFeesList().size() > 0) {
			for (FinTypeFees finTypeFees : financeType.getFinTypeFeesList()) {
				finTypeFees.setFinType(financeType.getFinType());
				finTypeFees.setWorkflowId(financeType.getWorkflowId());
				finTypeFees.setRecordStatus(financeType.getRecordStatus());
				finTypeFees.setUserDetails(financeType.getUserDetails());
				finTypeFees.setLastMntOn(financeType.getLastMntOn());
				finTypeFees.setRoleCode(financeType.getRoleCode());
				finTypeFees.setNextRoleCode(financeType.getNextRoleCode());
				finTypeFees.setTaskId(financeType.getTaskId());
				finTypeFees.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypeFees", this.finTypeFeesService.setFinTypeFeesAuditData(
					financeType.getFinTypeFeesList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeFees"));
		}

		// Insurance Details
		if (financeType.getFinTypeInsurances() != null && financeType.getFinTypeInsurances().size() > 0) {
			for (FinTypeInsurances finTypeInsurances : financeType.getFinTypeInsurances()) {
				finTypeInsurances.setFinType(financeType.getFinType());
				finTypeInsurances.setWorkflowId(financeType.getWorkflowId());
				finTypeInsurances.setRecordStatus(financeType.getRecordStatus());
				finTypeInsurances.setUserDetails(financeType.getUserDetails());
				finTypeInsurances.setLastMntOn(financeType.getLastMntOn());
				finTypeInsurances.setRoleCode(financeType.getRoleCode());
				finTypeInsurances.setNextRoleCode(financeType.getNextRoleCode());
				finTypeInsurances.setTaskId(financeType.getTaskId());
				finTypeInsurances.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypeInsurance", finTypeInsurancesService.setFinTypeInsuranceDetailsAuditData(
					financeType.getFinTypeInsurances(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeInsurance"));
		}

		// Accounting
		if (financeType.getFinTypeAccountingList() != null && financeType.getFinTypeAccountingList().size() > 0) {
			for (FinTypeAccounting finTypeAccounting : financeType.getFinTypeAccountingList()) {
				finTypeAccounting.setFinType(financeType.getFinType());
				finTypeAccounting.setWorkflowId(financeType.getWorkflowId());
				finTypeAccounting.setRecordStatus(financeType.getRecordStatus());
				finTypeAccounting.setUserDetails(financeType.getUserDetails());
				finTypeAccounting.setLastMntOn(financeType.getLastMntOn());
				finTypeAccounting.setRoleCode(financeType.getRoleCode());
				finTypeAccounting.setNextRoleCode(financeType.getNextRoleCode());
				finTypeAccounting.setTaskId(financeType.getTaskId());
				finTypeAccounting.setNextTaskId(financeType.getNextTaskId());
			}

			auditDetailMap.put("FinTypeAccounting", finTypeAccountingService.setFinTypeAccountingAuditData(
					financeType.getFinTypeAccountingList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeAccounting"));
		}
		
		// FinTypePartnerBank
		if (financeType.getFinTypePartnerBankList() != null && financeType.getFinTypePartnerBankList().size() > 0) {
			for (FinTypePartnerBank finTypePartnerBank : financeType.getFinTypePartnerBankList()) {
				finTypePartnerBank.setFinType(financeType.getFinType());
				finTypePartnerBank.setWorkflowId(financeType.getWorkflowId());
				finTypePartnerBank.setRecordStatus(financeType.getRecordStatus());
				finTypePartnerBank.setUserDetails(financeType.getUserDetails());
				finTypePartnerBank.setLastMntOn(financeType.getLastMntOn());
				finTypePartnerBank.setRoleCode(financeType.getRoleCode());
				finTypePartnerBank.setNextRoleCode(financeType.getNextRoleCode());
				finTypePartnerBank.setTaskId(financeType.getTaskId());
				finTypePartnerBank.setNextTaskId(financeType.getNextTaskId());
			}
			
			auditDetailMap.put("FinTypePartnerBank", finTypePartnerBankService.setFinTypePartnerBankDetailsAuditData(
					financeType.getFinTypePartnerBankList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypePartnerBank"));
		}
		
		//Finance Type VAS Details
		if (financeType.getFinTypeVASProductsList()!= null && financeType.getFinTypeVASProductsList().size() > 0) {
			auditDetailMap.put("FinTypeVASProducts", setFinTypeVasProcuctAuditData(financeType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeVASProducts"));
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
			} else if (object instanceof FinTypeFees) {
				rcdType = ((FinTypeFees) object).getRecordType();
			} else if (object instanceof FinTypeAccounting) {
				rcdType = ((FinTypeAccounting) object).getRecordType();
			} else if (object instanceof FinTypeInsurances) {
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

			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug("Leaving");
		
		return auditDetails;
	}

	public List<AuditDetail> deleteChilds(FinanceType financeType, String tableType, String auditTranType) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (financeType.getFinTypeAccounts() != null && !financeType.getFinTypeAccounts().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccount(),
					new FinTypeAccount().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeAccounts().size(); i++) {
				FinTypeAccount finTypeAccount = financeType.getFinTypeAccounts().get(i);
				if (StringUtils.isNotEmpty(finTypeAccount.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeAccount
							.getBefImage(), finTypeAccount));
				}
			}
			getFinTypeAccountDAO().deleteByFinType(financeType.getFinType(), tableType);
		}

		// Finance Type VAS Details
		if (financeType.getFinTypeVASProductsList() != null && !financeType.getFinTypeVASProductsList().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeVASProducts(),
					new FinTypeVASProducts().getExcludeFields());
			for (int i = 0; i < financeType.getFinTypeVASProductsList().size(); i++) {
				FinTypeVASProducts finTypeVASProducts = financeType.getFinTypeVASProductsList().get(i);
				if (StringUtils.isNotEmpty(finTypeVASProducts.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeVASProducts
							.getBefImage(), finTypeVASProducts));
				}
			}
			getFinTypeVASProductsDAO().deleteList(financeType.getFinType(), tableType);
		}

		// Fees
		if (financeType.getFinTypeFeesList() != null && !financeType.getFinTypeFeesList().isEmpty()) {
			auditDetails.addAll(this.finTypeFeesService.delete(financeType.getFinTypeFeesList(), tableType,
					auditTranType, financeType.getFinType(), FinanceConstants.MODULEID_FINTYPE));
		}
		// Insurance Deatails
		if (financeType.getFinTypeInsurances() != null && !financeType.getFinTypeInsurances().isEmpty()) {
			auditDetails.addAll(this.finTypeInsurancesService.delete(financeType.getFinTypeInsurances(), tableType,
					auditTranType, financeType.getFinType(), FinanceConstants.MODULEID_FINTYPE));
		}
		// Accounting Deatails
		if (financeType.getFinTypeAccountingList() != null && !financeType.getFinTypeAccountingList().isEmpty()) {
			auditDetails.addAll(this.finTypeAccountingService.delete(financeType.getFinTypeAccountingList(), tableType,
					auditTranType, financeType.getFinType(), FinanceConstants.MODULEID_FINTYPE));
		}
		// FinTypePartnerBank
		if (financeType.getFinTypePartnerBankList() != null && !financeType.getFinTypePartnerBankList().isEmpty()) {
			auditDetails.addAll(this.finTypePartnerBankService.delete(financeType.getFinTypePartnerBankList(), tableType,
					auditTranType, financeType.getFinType()));
		}
		
		logger.debug("Leaving");
		
		return auditDetails;
	}

	private List<ErrorDetails> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");
		
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		FinanceType financeType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;
		// FinTypeAccount
		if (financeType.getAuditDetailMap().get("FinTypeCustAccount") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeCustAccount");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details = validationFinTypeCustAccounts(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (financeType.getAuditDetailMap().get("FinTypeFees") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeFees");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details = this.finTypeFeesService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (financeType.getAuditDetailMap().get("FinTypeInsurance") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeInsurance");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details = this.finTypeInsurancesService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (financeType.getAuditDetailMap().get("FinTypeAccounting") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypeAccounting");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details = this.finTypeAccountingService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (financeType.getAuditDetailMap().get("FinTypePartnerBank") != null) {
			auditDetails = financeType.getAuditDetailMap().get("FinTypePartnerBank");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetails> details = this.finTypePartnerBankService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		
		logger.debug("Leaving");
		
		return errorDetails;
	}


	private List<AuditDetail> processFinTypeCustAccountDetails(FinanceType financeType, List<AuditDetail> auditDetails,
			String type) {
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

			boolean isRcdType = false;
			finTypeAccount.setFinType(financeType.getFinType());
			finTypeAccount.setWorkflowId(financeType.getWorkflowId());

			if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
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

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeAccount.getBefImage(), finTypeAccount));
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
	private AuditDetail validationFinTypeCustAccounts(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinTypeAccount finTypeAccount = (FinTypeAccount) auditDetail.getModelData();
		FinTypeAccount tempFinTypeAccount = null;

		if (finTypeAccount.isWorkflow()) {
			tempFinTypeAccount = getFinTypeAccountDAO().getFinTypeAccountByID(finTypeAccount, "_Temp");
		}

		FinTypeAccount befFinTypeAccount = getFinTypeAccountDAO().getFinTypeAccountByID(finTypeAccount, "");
		FinTypeAccount oldFinTypeAccountReference = finTypeAccount.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[2];
		valueParm[0] = finTypeAccount.getFinCcy();
		valueParm[1] = finTypeAccount.getEvent();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeAccountDialog_FinCcy.value") + ":" + valueParm[0] + ","
				+ PennantJavaUtil.getLabel("label_FinTypeAccountDialog_Event.value") + ":" + valueParm[1];

		if (finTypeAccount.isNew()) { // for New record or new record into work flow
			if (!finTypeAccount.isWorkflow()) {// With out Work flow only new records
				if (befFinTypeAccount != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinTypeAccount != null || tempFinTypeAccount != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinTypeAccount == null || tempFinTypeAccount != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeAccount.isWorkflow()) { // With out Work flow for update and delete
				if (befFinTypeAccount == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeAccountReference != null
							&& !oldFinTypeAccountReference.getLastMntOn().equals(befFinTypeAccount.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempFinTypeAccount == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypeAccount != null && oldFinTypeAccountReference != null
						&& !oldFinTypeAccountReference.getLastMntOn().equals(tempFinTypeAccount.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeAccount.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeAccount);
		}

		logger.debug("Leaving");
		
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
	private List<AuditDetail> processingVasProductDetailList(List<AuditDetail> auditDetails, String financeType, String type) {
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
				getFinTypeVASProductsDAO().update(finTypeVASProducts, type);
			}

			if (deleteRecord) {
				getFinTypeVASProductsDAO().delete(finTypeVASProducts.getFinType(), finTypeVASProducts.getVasProduct(),
						type);
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

			if ("saveOrUpdate".equals(method) && isRcdType) {
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
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getFinanceTypeByFinType(finType);
	}
 
	@Override
	public String getAllowedCollateralTypes(String finType) {
		logger.debug("Entering");
		logger.debug("Leaving");
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

	@Override
	public FinTypeAccount getFinTypeAccount() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinTypeAccountDAO().getFinTypeAccount();
	}

	@Override
	public FinTypeAccount getNewFinTypeAccount() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinTypeAccountDAO().getNewFinTypeAccount();
	}

	/**
	 * Fetch the FinanceTypes Based on the Product Code
	 * 
	 * @param productCode
	 */
	@Override
	public List<FinanceType> getFinanceTypeByProduct(String productCode) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceTypeDAO().getFinanceTypeByProduct(productCode);
	}
	
	/**
	 * Fetch total number of records from FinanceTypes(Promotion)
	 * 
	 * @param finType
	 */
	@Override
	public int getPromotionTypeCountById(String finType) {
		logger.debug("Entering");
	
		int promotionCount = 0;
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
		logger.debug("Entering");
	
		int productCount = 0;
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
		logger.debug("Entering");
		
		String finTypeDesc = "";
		finTypeDesc = getFinanceTypeDAO().getFinanceTypeDesc(productCode);
		
		logger.debug("Leaving");
		
		return finTypeDesc;
	}
	
	@Override
	public boolean getFinTypeExist(String finType, String type) {
		logger.debug("Entering");
		
		boolean finTypeExist = false;

		if (getFinanceTypeDAO().getFinTypeCount(finType, type) != 0) {
			finTypeExist = true;
		}

		logger.debug("Leaving");

		return finTypeExist;
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
	
	public FinTypeAccountDAO getFinTypeAccountDAO() {
		return finTypeAccountDAO;
	}
	
	public void setFinTypeAccountDAO(FinTypeAccountDAO finTypeAccountDAO) {
		this.finTypeAccountDAO = finTypeAccountDAO;
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

	public ProductAssetDAO getProductAssetDAO() {
		return productAssetDAO;
	}

	public void setProductAssetDAO(ProductAssetDAO productAssetDAO) {
		this.productAssetDAO = productAssetDAO;
	}

	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}
	
	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}
	
	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}
	
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public FinTypeFeesService getFinTypeFeesService() {
		return finTypeFeesService;
	}

	public void setFinTypeFeesService(FinTypeFeesService finTypeFeesService) {
		this.finTypeFeesService = finTypeFeesService;
	}

	public FinTypeInsurancesService getFinTypeInsurancesService() {
		return finTypeInsurancesService;
	}

	public void setFinTypeInsurancesService(FinTypeInsurancesService finTypeInsurancesService) {
		this.finTypeInsurancesService = finTypeInsurancesService;
	}

	public FinTypeAccountingService getFinTypeAccountingService() {
		return finTypeAccountingService;
	}

	public void setFinTypeAccountingService(FinTypeAccountingService finTypeAccountingService) {
		this.finTypeAccountingService = finTypeAccountingService;
	}

	public FinTypePartnerBankService getFinTypePartnerBankService() {
		return finTypePartnerBankService;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}
}
