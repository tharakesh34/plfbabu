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
 * FileName    		:  PromotionServiceImpl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.service.rmtmasters.FinTypeFeesService;
import com.pennant.backend.service.rmtmasters.FinTypeInsurancesService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Promotion</b>.<br>
 * 
 */
public class PromotionServiceImpl extends GenericService<Promotion> implements PromotionService {
	private static final Logger logger = Logger.getLogger(PromotionServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private PromotionDAO promotionDAO;
	
	//Child Services
	private FinTypeFeesService finTypeFeesService;
	private FinTypeInsurancesService finTypeInsurancesService;
	private FinTypeAccountingService finTypeAccountingService;
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Promotions/Promotions_Temp by
	 * using PromotionsDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using PromotionsDAO's update method 3) Audit the record in to AuditHeader and AdtPromotions by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();

		if (promotion.isWorkflow()) {
			tableType = "_Temp";
		}

		if (promotion.isNew()) {
			getPromotionDAO().save(promotion, tableType);
			auditHeader.getAuditDetail().setModelData(promotion);
			auditHeader.setAuditReference(promotion.getPromotionCode());
		} else {
			getPromotionDAO().update(promotion, tableType);
		}

		// Retrieving List of Audit Details For promotion related modules
		// Finance Type Fees
		if (promotion.getFinTypeFeesList() != null && promotion.getFinTypeFeesList().size() > 0) {
			List<AuditDetail> feeDetails = promotion.getAuditDetailMap().get("FinTypeFees");
			feeDetails = this.finTypeFeesService.processFinTypeFeesDetails(feeDetails, tableType);
			auditDetailsList.addAll(feeDetails);
		}
		// Finance Type Insurances
		if (promotion.getFinTypeInsurancesList() != null && promotion.getFinTypeInsurancesList().size() > 0) {
			List<AuditDetail> insuranceDetails = promotion.getAuditDetailMap().get("FinTypeInsurance");
			insuranceDetails = this.finTypeInsurancesService.processFinTypeInsuranceDetails(insuranceDetails, tableType);
			auditDetailsList.addAll(insuranceDetails);
		}
		// Finance Type Accounting
		if (promotion.getFinTypeAccountingList() != null && promotion.getFinTypeAccountingList().size() > 0) {
			List<AuditDetail> accountingDetails = promotion.getAuditDetailMap().get("FinTypeAccounting");
			accountingDetails = this.finTypeAccountingService.processFinTypeAccountingDetails(accountingDetails, tableType);
			auditDetailsList.addAll(accountingDetails);
		}

		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Promotions by using PromotionsDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtPromotions by using auditHeaderDAO.addAudit(auditHeader)
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

		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(promotion, "", auditHeader.getAuditTranType())));
		getPromotionDAO().delete(promotion, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	public List<AuditDetail> deleteChilds(Promotion promotion, String tableType, String auditTranType) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		// Fees
		if (promotion.getFinTypeFeesList() != null && !promotion.getFinTypeFeesList().isEmpty()) {
			auditDetailsList.addAll(this.finTypeFeesService.delete(promotion.getFinTypeFeesList(), tableType,
					auditTranType, promotion.getPromotionCode(), FinanceConstants.MODULEID_PROMOTION));
		}
		// Insurance Deatails
		if (promotion.getFinTypeInsurancesList() != null && !promotion.getFinTypeInsurancesList().isEmpty()) {
			auditDetailsList.addAll(this.finTypeInsurancesService.delete(promotion.getFinTypeInsurancesList(),
					tableType, auditTranType, promotion.getPromotionCode(), FinanceConstants.MODULEID_PROMOTION));
		}
		// Accounting Deatails
		if (promotion.getFinTypeAccountingList() != null && !promotion.getFinTypeAccountingList().isEmpty()) {
			auditDetailsList.addAll(this.finTypeAccountingService.delete(promotion.getFinTypeAccountingList(),
					tableType, auditTranType, promotion.getPromotionCode(), FinanceConstants.MODULEID_PROMOTION));
		}

		logger.debug("Leaving");
		
		return auditDetailsList;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetailsList;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof FinTypeFees) {
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

			auditDetailsList.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug("Leaving");

		return auditDetailsList;
	}

	/**
	 * getPromotionsById fetch the details by using PromotionsDAO's getPromotionsById method.
	 * 
	 * @param promotionCode
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Promotions
	 */
	@Override
	public Promotion getPromotionById(String promotionCode, int moduleId) {
		logger.debug("Entering");

		Promotion promotion = getPromotionDAO().getPromotionById(promotionCode, "_View");

		if (promotion != null) {
			promotion.setFinTypeFeesList(getFinTypeFeesService().getFinTypeFeesById(promotionCode, moduleId));
			promotion.setFinTypeInsurancesList(getFinTypeInsurancesService().getFinTypeInsuranceListByID(promotionCode, moduleId));
			promotion.setFinTypeAccountingList(getFinTypeAccountingService().getFinTypeAccountingListByID(promotionCode, moduleId));
		}

		logger.debug("Leaving");

		return promotion;
	}

	/**
	 * getApprovedPromotionsById fetch the details by using PromotionsDAO's getPromotionsById method . with parameter id
	 * and type as blank. it fetches the approved records from the Promotions.
	 * 
	 * @param id
	 *            (String)
	 * @return Promotions
	 */
	@Override
	public Promotion getApprovedPromotionById(String promotionCode, int moduleId, boolean childExist) {
		logger.debug("Entering");

		Promotion promotion = getPromotionDAO().getPromotionById(promotionCode, "_AView");

		if (childExist && promotion != null) {
			promotion.setFinTypeFeesList(getFinTypeFeesService().getApprovedFinTypeFeesById(promotionCode, moduleId));
			promotion.setFinTypeInsurancesList(getFinTypeInsurancesService().getApprovedFinTypeInsuranceListByID(promotionCode, moduleId));
			promotion.setFinTypeAccountingList(getFinTypeAccountingService().getApprovedFinTypeAccountingListByID(promotionCode, moduleId));
		}

		logger.debug("Leaving");

		return promotion;
	}

	/**
	 * getPromotionsById fetch the details by using PromotionsDAO's getPromotionsById method.
	 * 
	 * @param promotionCode
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Promotions
	 */
	@Override
	public boolean getPromtionExist(String promotionCode, String type) {
		logger.debug("Entering");
		
		boolean promotionExist = false;

		if (getPromotionDAO().getPromtionCodeCount(promotionCode, type) != 0) {
			promotionExist = true;
		}

		logger.debug("Leaving");

		return promotionExist;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPromotionDAO().delete with
	 * parameters promotion,"" b) NEW Add new record in to main table by using getPromotionDAO().save with parameters
	 * promotion,"" c) EDIT Update record in the main table by using getPromotionDAO().update with parameters
	 * promotion,"" 3) Delete the record from the workFlow table by using getPromotionDAO().delete with parameters
	 * promotion,"_Temp" 4) Audit the record in to AuditHeader and AdtPromotions by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtPromotions by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Promotion promotion = new Promotion();
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		BeanUtils.copyProperties((Promotion) auditHeader.getAuditDetail().getModelData(), promotion);

		if (PennantConstants.RECORD_TYPE_DEL.equals(promotion.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetailsList.addAll(processChildsAudit(deleteChilds(promotion, "", tranType)));
			getPromotionDAO().delete(promotion, "");
		} else {
			promotion.setRoleCode("");
			promotion.setNextRoleCode("");
			promotion.setTaskId("");
			promotion.setNextTaskId("");
			promotion.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(promotion.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				promotion.setRecordType("");
				getPromotionDAO().save(promotion, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				promotion.setRecordType("");
				getPromotionDAO().update(promotion, "");
			}

			// Retrieving List of Audit Details For Promotion related modules
			// Fees
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> feeDetails = promotion.getAuditDetailMap().get("FinTypeFees");

				if (feeDetails != null && !feeDetails.isEmpty()) {
					feeDetails = this.finTypeFeesService.processFinTypeFeesDetails(feeDetails, "");
					auditDetailsList.addAll(feeDetails);
				}
			}
			// Insurances
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> insuranceDetails = promotion.getAuditDetailMap().get("FinTypeInsurance");

				if (insuranceDetails != null && !insuranceDetails.isEmpty()) {
					insuranceDetails = this.finTypeInsurancesService.processFinTypeInsuranceDetails(insuranceDetails, "");
					auditDetailsList.addAll(insuranceDetails);
				}
			}
			// Accounting
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> accountingDetails = promotion.getAuditDetailMap().get("FinTypeAccounting");

				if (accountingDetails != null && !accountingDetails.isEmpty()) {
					accountingDetails = this.finTypeAccountingService.processFinTypeAccountingDetails(accountingDetails, "");
					auditDetailsList.addAll(accountingDetails);
				}
			}
		}

		getPromotionDAO().delete(promotion, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		// List
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(promotion, "_Temp", auditHeader.getAuditTranType())));

		String[] fields = PennantJavaUtil.getFieldDetails(new Promotion(), promotion.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], promotion.getBefImage(), promotion));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(promotion);

		// List
		auditHeader.setAuditDetails(processChildsAudit(auditDetailsList));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPromotionDAO().delete with parameters promotion,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtPromotions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		// List
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(promotion, "_Temp", auditHeader.getAuditTranType())));
		getPromotionDAO().delete(promotion, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
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

		// List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
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
		// HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();
		// String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (promotion.isWorkflow()) {
				// auditTranType = PennantConstants.TRAN_WF;
			}
		}

		auditHeader.getAuditDetail().setModelData(promotion);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	// =================================== List maintain
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (promotion.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Fees
		if (promotion.getFinTypeFeesList() != null && promotion.getFinTypeFeesList().size() > 0) {
			for (FinTypeFees finTypeFees : promotion.getFinTypeFeesList()) {
				finTypeFees.setWorkflowId(promotion.getWorkflowId());
				finTypeFees.setRecordStatus(promotion.getRecordStatus());
				finTypeFees.setUserDetails(promotion.getUserDetails());
				finTypeFees.setLastMntOn(promotion.getLastMntOn());
				finTypeFees.setRoleCode(promotion.getRoleCode());
				finTypeFees.setNextRoleCode(promotion.getNextRoleCode());
				finTypeFees.setTaskId(promotion.getTaskId());
				finTypeFees.setNextTaskId(promotion.getNextTaskId());
			}

			auditDetailMap.put("FinTypeFees", this.finTypeFeesService.setFinTypeFeesAuditData(
					promotion.getFinTypeFeesList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeFees"));
		}

		// Insurance Details
		if (promotion.getFinTypeInsurancesList() != null && promotion.getFinTypeInsurancesList().size() > 0) {
			for (FinTypeInsurances finTypeInsurances : promotion.getFinTypeInsurancesList()) {
				finTypeInsurances.setWorkflowId(promotion.getWorkflowId());
				finTypeInsurances.setRecordStatus(promotion.getRecordStatus());
				finTypeInsurances.setUserDetails(promotion.getUserDetails());
				finTypeInsurances.setLastMntOn(promotion.getLastMntOn());
				finTypeInsurances.setRoleCode(promotion.getRoleCode());
				finTypeInsurances.setNextRoleCode(promotion.getNextRoleCode());
				finTypeInsurances.setTaskId(promotion.getTaskId());
				finTypeInsurances.setNextTaskId(promotion.getNextTaskId());
			}

			auditDetailMap.put("FinTypeInsurance", finTypeInsurancesService.setFinTypeInsuranceDetailsAuditData(
					promotion.getFinTypeInsurancesList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeInsurance"));
		}

		// Accounting
		if (promotion.getFinTypeAccountingList() != null && promotion.getFinTypeAccountingList().size() > 0) {
			for (FinTypeAccounting finTypeAccounting : promotion.getFinTypeAccountingList()) {
				finTypeAccounting.setWorkflowId(promotion.getWorkflowId());
				finTypeAccounting.setRecordStatus(promotion.getRecordStatus());
				finTypeAccounting.setUserDetails(promotion.getUserDetails());
				finTypeAccounting.setLastMntOn(promotion.getLastMntOn());
				finTypeAccounting.setRoleCode(promotion.getRoleCode());
				finTypeAccounting.setNextRoleCode(promotion.getNextRoleCode());
				finTypeAccounting.setTaskId(promotion.getTaskId());
				finTypeAccounting.setNextTaskId(promotion.getNextTaskId());
			}

			auditDetailMap.put("FinTypeAccounting", finTypeAccountingService.setFinTypeAccountingAuditData(
					promotion.getFinTypeAccountingList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinTypeAccounting"));
		}

		promotion.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(promotion);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}


	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		Promotion promotion = (Promotion) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		if (promotion.getAuditDetailMap().get("FinTypeFees") != null) {
			auditDetails = promotion.getAuditDetailMap().get("FinTypeFees");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypeFeesService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (promotion.getAuditDetailMap().get("FinTypeInsurance") != null) {
			auditDetails = promotion.getAuditDetailMap().get("FinTypeInsurance");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypeInsurancesService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		if (promotion.getAuditDetailMap().get("FinTypeAccounting") != null) {
			auditDetails = promotion.getAuditDetailMap().get("FinTypeAccounting");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.finTypeAccountingService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		logger.debug("Leaving");

		return errorDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPromotionDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Promotion promotion = (Promotion) auditDetail.getModelData();

		Promotion tempPromotion = null;
		if (promotion.isWorkflow()) {
			tempPromotion = getPromotionDAO().getPromotionById(promotion.getPromotionCode(), "_Temp");
		}
		Promotion befPromotion = getPromotionDAO().getPromotionById(promotion.getPromotionCode(), "");

		Promotion oldPromotion = promotion.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = promotion.getPromotionCode();
		errParm[0] = PennantJavaUtil.getLabel("label_PromotionCode") + ":" + valueParm[0];

		if (promotion.isNew()) { // for New record or new record into work flow
			if (!promotion.isWorkflow()) {// With out Work flow only new records
				if (befPromotion != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (PennantConstants.RECORD_TYPE_NEW.equals(promotion.getRecordType())) { // if records type is new
					if (befPromotion != null || tempPromotion != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befPromotion == null || tempPromotion != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!promotion.isWorkflow()) { // With out Work flow for update and delete
				if (befPromotion == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldPromotion != null && !oldPromotion.getLastMntOn().equals(befPromotion.getLastMntOn())) {
						if (PennantConstants.TRAN_DEL.equalsIgnoreCase(StringUtils.trimToEmpty(auditDetail
								.getAuditTranType()))) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempPromotion == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempPromotion != null && oldPromotion != null
						&& !oldPromotion.getLastMntOn().equals(tempPromotion.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !promotion.isWorkflow()) {
			auditDetail.setBefImage(befPromotion);
		}

		return auditDetail;
	}

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
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the promotionDAO
	 */
	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}

	/**
	 * @param promotionDAO
	 *            the promotionDAO to set
	 */
	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
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

	/**
	 * Fetch record count of Promotions by using financeType
	 * 
	 * @param financeType
	 */
	@Override
	public int getFinanceTypeCountById(String finType)  {
		logger.debug("Entering");
		int financeTypeCount = 0;
		financeTypeCount = getPromotionDAO().getFinanceTypeCountById(finType);
		logger.debug("Leaving");
		
		return financeTypeCount;
	}

	/**
	 * Fetch the Promotions Based on the finType
	 * 
	 * @param productCode
	 */
	@Override
	public List<Promotion> getPromotionsByFinType(String finType,String type) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getPromotionDAO().getPromotionsByFinType(finType,type);
	}
}