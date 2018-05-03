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
 * FileName    		:  FinTypeInsurancesServiceImpl.java                                    * 	  
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeInsuranceDAO;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypeInsurancesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>FinTypeInsurances</b>.<br>
 * 
 */
public class FinTypeInsurancesServiceImpl extends GenericService<FinTypeInsurances> implements FinTypeInsurancesService {
	private static final Logger logger = Logger.getLogger(FinTypeInsurancesServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinTypeInsuranceDAO finTypeInsuranceDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * FinTypeInsurancess/FinTypeInsurancess_Temp by using FinTypeInsurancessDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinTypeInsurancessDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtFinTypeInsurancess by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
	
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType = "";
		FinTypeInsurances finTypeInsurances = (FinTypeInsurances) auditHeader.getAuditDetail().getModelData();

		if (finTypeInsurances.isWorkflow()) {
			tableType = "_Temp";
		}

		if (finTypeInsurances.isNew()) {
			finTypeInsurances.setId(getFinTypeInsuranceDAO().save(finTypeInsurances, tableType));
			auditHeader.getAuditDetail().setModelData(finTypeInsurances);
			auditHeader.setAuditReference(String.valueOf(finTypeInsurances.getFinType()));
		} else {
			getFinTypeInsuranceDAO().update(finTypeInsurances, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;

	}

	/**
	 * getFinTypeInsurancessById fetch the details by using FinTypeInsurancessDAO's getFinTypeInsurancessById
	 * method.
	 * 
	 * @param finType
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinTypeInsurancess
	 */
	@Override
	public List<FinTypeInsurances> getFinTypeInsuranceListByID(String finType, int moduleId) {
		return getFinTypeInsuranceDAO().getFinTypeInsuranceListByID(finType, moduleId, "_View");
	}

	/**
	 * getApprovedFinTypeInsurancessById fetch the details by using FinTypeInsurancessDAO's
	 * getFinTypeInsurancessById method . with parameter id and type as blank. it fetches
	 * the approved records from the FinTypeInsurancess.
	 * 
	 * @param id
	 *            (String)
	 * @return FinTypeInsurancess
	 */
	@Override
	public List<FinTypeInsurances> getApprovedFinTypeInsuranceListByID(String finType, int moduleId) {
		return getFinTypeInsuranceDAO().getFinTypeInsuranceListByID(finType, moduleId, "_View");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinTypeInsuranceDAO().delete with parameters promotionFee,"" b) NEW Add new
	 * record in to main table by using getFinTypeInsuranceDAO().save with parameters
	 * promotionFee,"" c) EDIT Update record in the main table by using
	 * getFinTypeInsuranceDAO().update with parameters promotionFee,"" 3) Delete the record
	 * from the workFlow table by using getFinTypeInsuranceDAO().delete with parameters
	 * promotionFee,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtFinTypeInsurancess by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtFinTypeInsurancess by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinTypeInsurances finTypeInsurances = new FinTypeInsurances("");
		BeanUtils.copyProperties((FinTypeInsurances) auditHeader.getAuditDetail().getModelData(), finTypeInsurances);

		if (PennantConstants.RECORD_TYPE_DEL.equals(finTypeInsurances.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			getFinTypeInsuranceDAO().delete(finTypeInsurances, "");
		} else {
			finTypeInsurances.setRoleCode("");
			finTypeInsurances.setNextRoleCode("");
			finTypeInsurances.setTaskId("");
			finTypeInsurances.setNextTaskId("");
			finTypeInsurances.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(finTypeInsurances.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				finTypeInsurances.setRecordType("");
				getFinTypeInsuranceDAO().save(finTypeInsurances, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finTypeInsurances.setRecordType("");
				getFinTypeInsuranceDAO().update(finTypeInsurances, "");
			}
		}

		getFinTypeInsuranceDAO().delete(finTypeInsurances, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finTypeInsurances);

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");

		return auditHeader;
	}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getFinTypeInsuranceDAO().delete with parameters
		 * promotionFee,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtFinTypFinTypeInsurancessing auditHeaderDAO.addAudit(auditHeader) for Work
		 * flow
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

		FinTypeInsurances finTypeInsurances = (FinTypeInsurances) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinTypeInsuranceDAO().delete(finTypeInsurances, "_Temp");

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
		auditHeader = nextProcess(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFinTypeInsuranceDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	@Override
	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
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
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypeInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinTypeInsurance != null || tempFinTypeInsurance != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinTypeInsurance == null || tempFinTypeInsurance != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeInsurance.isWorkflow()) { // With out Work flow for update and delete

				if (befFinTypeInsurance == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeinsurance != null
							&& !oldFinTypeinsurance.getLastMntOn().equals(befFinTypeInsurance.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempFinTypeInsurance == null) { // if records not exists in the Work flow table 
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypeInsurance != null && oldFinTypeinsurance != null
						&& !oldFinTypeinsurance.getLastMntOn().equals(tempFinTypeInsurance.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeInsurance.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeInsurance);
		}

		return auditDetail;
	}
	
	@Override
	public List<AuditDetail> setFinTypeInsuranceDetailsAuditData(List<FinTypeInsurances> finTypeInsurancesList, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeInsurances(), new FinTypeInsurances().getExcludeFields());
		
		for (int i = 0; i < finTypeInsurancesList.size(); i++) {
			FinTypeInsurances finTypeInsurance = finTypeInsurancesList.get(i);

			if (StringUtils.isEmpty(finTypeInsurance.getRecordType())) {
				continue;
			}

			//finTypeInsurance.setFinType(financeType.getFinType());
			//finTypeInsurance.setWorkflowId(financeType.getWorkflowId());
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
			//finTypeInsurance.setRecordStatus(financeType.getRecordStatus());
			//finTypeInsurance.setUserDetails(financeType.getUserDetails());
			//finTypeInsurance.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeInsurance.getBefImage(), finTypeInsurance));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> processFinTypeInsuranceDetails(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeInsurances finTypeInsurances = (FinTypeInsurances) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeInsurances.setRoleCode("");
				finTypeInsurances.setNextRoleCode("");
				finTypeInsurances.setTaskId("");
				finTypeInsurances.setNextTaskId("");
				finTypeInsurances.setWorkflowId(0);
			}
			//finTypeAccounting.setFinType(financeType.getFinType());
			if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeInsurances.isNewRecord()) {
				saveRecord = true;
				if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeInsurances.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeInsurances.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeInsurances.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeInsurances.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeInsurances.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeInsurances.getRecordType();
				recordStatus = finTypeInsurances.getRecordStatus();
				finTypeInsurances.setRecordType("");
				finTypeInsurances.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeInsuranceDAO().save(finTypeInsurances, type);
			}
			if (updateRecord) {
				getFinTypeInsuranceDAO().update(finTypeInsurances, type);
			}
			if (deleteRecord) {
				getFinTypeInsuranceDAO().delete(finTypeInsurances, type);
			}
			if (approveRec) {
				finTypeInsurances.setRecordType(rcdType);
				finTypeInsurances.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeInsurances);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinTypeInsurances> finTypeInsurancesList, String tableType, String auditTranType, String finType, int moduleId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finTypeInsurancesList != null && !finTypeInsurancesList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeInsurances(), new FinTypeInsurances().getExcludeFields());
			for (int i = 0; i < finTypeInsurancesList.size(); i++) {
				FinTypeInsurances finTypeInsurances = finTypeInsurancesList.get(i);
				if (StringUtils.isNotEmpty(finTypeInsurances.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeInsurances.getBefImage(), finTypeInsurances));
				}
			}
			getFinTypeInsuranceDAO().deleteByFinType(finType, moduleId, tableType);
		}

		return auditDetails;

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
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}


	public FinTypeInsuranceDAO getFinTypeInsuranceDAO() {
		return finTypeInsuranceDAO;
	}

	public void setFinTypeInsuranceDAO(FinTypeInsuranceDAO finTypeInsuranceDAO) {
		this.finTypeInsuranceDAO = finTypeInsuranceDAO;
	}

}