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
 * FileName    		:  FinTypeFeeServiceImpl.java                                         * 	  
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
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypeFeesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinTypeFees</b>.<br>
 * 
 */
public class FinTypeFeesServiceImpl extends GenericService<FinTypeFees> implements FinTypeFeesService {
	private static final Logger logger = Logger.getLogger(FinTypeFeesServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinTypeFeesDAO finTypeFeeDAO;


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
	 * @return the finTypeFeeDAO
	 */
	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeeDAO;
	}
	/**
	 * @param finTypeFeeDAO the finTypeFeeDAO to set
	 */
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeeDAO) {
		this.finTypeFeeDAO = finTypeFeeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * FinTypeFeess/FinTypeFeess_Temp by using FinTypeFeessDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinTypeFeessDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtFinTypeFeess by using
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
		FinTypeFees finTypeFees = (FinTypeFees) auditHeader.getAuditDetail().getModelData();

		if (finTypeFees.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (finTypeFees.isNew()) {
			finTypeFees.setId(getFinTypeFeesDAO().save(finTypeFees, tableType));
			auditHeader.getAuditDetail().setModelData(finTypeFees);
			auditHeader.setAuditReference(String.valueOf(finTypeFees.getFinType()));
		} else {
			getFinTypeFeesDAO().update(finTypeFees, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;

	}

	/**
	 * getFinTypeFeessById fetch the details by using FinTypeFeessDAO's getFinTypeFeessById
	 * method.
	 * 
	 * @param finType
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinTypeFeess
	 */
	@Override
	public List<FinTypeFees> getFinTypeFeesById(String finType, int moduleId) {
		return getFinTypeFeesDAO().getFinTypeFeesListByID(finType, moduleId, "_View");
	}

	/**
	 * getApprovedFinTypeFeessById fetch the details by using FinTypeFeessDAO's
	 * getFinTypeFeessById method . with parameter id and type as blank. it fetches
	 * the approved records from the FinTypeFeess.
	 * 
	 * @param id
	 *            (String)
	 * @return FinTypeFeess
	 */
	@Override
	public List<FinTypeFees> getApprovedFinTypeFeesById(String id, int moduleId) {
		return getFinTypeFeesDAO().getFinTypeFeesListByID(id, moduleId, "_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinTypeFeesDAO().delete with parameters promotionFee,"" b) NEW Add new
	 * record in to main table by using getFinTypeFeesDAO().save with parameters
	 * promotionFee,"" c) EDIT Update record in the main table by using
	 * getFinTypeFeesDAO().update with parameters promotionFee,"" 3) Delete the record
	 * from the workFlow table by using getFinTypeFeesDAO().delete with parameters
	 * promotionFee,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtFinTypeFeess by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtFinTypeFeess by using
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

		FinTypeFees finTypeFees = new FinTypeFees("");
		BeanUtils.copyProperties((FinTypeFees) auditHeader.getAuditDetail().getModelData(), finTypeFees);

		if (PennantConstants.RECORD_TYPE_DEL.equals(finTypeFees.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			getFinTypeFeesDAO().delete(finTypeFees, "");
		} else {
			finTypeFees.setRoleCode("");
			finTypeFees.setNextRoleCode("");
			finTypeFees.setTaskId("");
			finTypeFees.setNextTaskId("");
			finTypeFees.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(finTypeFees.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				finTypeFees.setRecordType("");
				getFinTypeFeesDAO().save(finTypeFees, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finTypeFees.setRecordType("");
				getFinTypeFeesDAO().update(finTypeFees, "");
			}
		}

		getFinTypeFeesDAO().delete(finTypeFees, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finTypeFees);

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");

		return auditHeader;
	}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getFinTypeFeesDAO().delete with parameters
		 * promotionFee,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtFinTypeFeess by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		FinTypeFees finTypeFees = (FinTypeFees) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinTypeFeesDAO().delete(finTypeFees, "_TEMP");

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
	 * from getFinTypeFeesDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
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
		FinTypeFees finTypeFees = (FinTypeFees) auditDetail.getModelData();

		FinTypeFees tempFinTypeFees = null;
		if (finTypeFees.isWorkflow()) {
			tempFinTypeFees = getFinTypeFeesDAO().getFinTypeFeesByID(finTypeFees, "_Temp");
		}
		FinTypeFees befFinTypeFees = getFinTypeFeesDAO().getFinTypeFeesByID(finTypeFees, "");

		FinTypeFees oldFinTypeFeesReference = finTypeFees.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[2];
		valueParm[0] = finTypeFees.getFeeTypeCode();
		valueParm[1] = finTypeFees.getFinEvent();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeFeesDialog_FeeType.value") + ":" + valueParm[0] + ","
				+ PennantJavaUtil.getLabel("label_FinTypeFeesDialog_FinEvent.value") + ":" + valueParm[1];

		if (finTypeFees.isNew()) { // for New record or new record into work flow
			if (!finTypeFees.isWorkflow()) {// With out Work flow only new records
				if (befFinTypeFees != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinTypeFees != null || tempFinTypeFees != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinTypeFees == null || tempFinTypeFees != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeFees.isWorkflow()) { // With out Work flow for update and delete
				if (befFinTypeFees == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeFeesReference != null
							&& !oldFinTypeFeesReference.getLastMntOn().equals(befFinTypeFees.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempFinTypeFees == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypeFees != null && oldFinTypeFeesReference != null
						&& !oldFinTypeFeesReference.getLastMntOn().equals(tempFinTypeFees.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeFees.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeFees);
		}

		return auditDetail;
	}
	
	@Override
	public List<AuditDetail> setFinTypeFeesAuditData(List<FinTypeFees> finTypeFeesList, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeFees(), new FinTypeFees().getExcludeFields());
		for (int i = 0; i < finTypeFeesList.size(); i++) {
			FinTypeFees finTypeFees = finTypeFeesList.get(i);

			if (StringUtils.isEmpty(finTypeFees.getRecordType())) {
				continue;
			}

			//finTypeFees.setFinType(financeType.getFinType());
			//finTypeFees.setWorkflowId(financeType.getWorkflowId());
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
			//finTypeFees.setRecordStatus(financeType.getRecordStatus());
			//finTypeFees.setUserDetails(financeType.getUserDetails());
			//finTypeFees.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeFees.getBefImage(), finTypeFees));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> processFinTypeFeesDetails(List<AuditDetail> auditDetails, String type) {
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
				finTypeFees.setWorkflowId(0);
			}
			//finTypeFees.setFinType(financeType.getFinType());
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

	@Override
	public List<AuditDetail> delete(List<FinTypeFees> finTypeFeesList, String tableType, String auditTranType, String finType, int moduleId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeFees(), new FinTypeFees().getExcludeFields());
			for (int i = 0; i < finTypeFeesList.size(); i++) {
				FinTypeFees finTypeFees = finTypeFeesList.get(i);
				if (StringUtils.isNotEmpty(finTypeFees.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeFees.getBefImage(), finTypeFees));
				}
			}
			getFinTypeFeesDAO().deleteByFinType(finType, tableType, moduleId);
		}

		return auditDetails;

	}

}