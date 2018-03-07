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
 * FileName    		:  FinTypeExpenseServiceImpl.java                                       * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.rmtmasters.FinTypeExpenseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypeExpenseService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>FinTypeExpenseServiceImpl</b>.<br>
 * 
 */
public class FinTypeExpenseServiceImpl extends GenericService<FinTypeExpense> implements FinTypeExpenseService {
	private static final Logger	logger	= Logger.getLogger(FinTypeExpenseServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private FinTypeExpenseDAO finTypeExpenseDAO;

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

	public FinTypeExpenseDAO getFinTypeExpenseDAO() {
		return finTypeExpenseDAO;
	}

	public void setFinTypeExpenseDAO(FinTypeExpenseDAO finTypeExpenseDAO) {
		this.finTypeExpenseDAO = finTypeExpenseDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinTypeExpense/FinTypeExpense_Temp
	 * by using FinTypeExpenseDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinTypeExpenseDAO's update method 3) Audit the record in to AuditHeader and AdtFinTypeExpense
	 * by using auditHeaderDAO.addAudit(auditHeader)
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
		FinTypeExpense finTypeExpense = (FinTypeExpense) auditHeader.getAuditDetail().getModelData();

		if (finTypeExpense.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (finTypeExpense.isNew()) {
			finTypeExpense.setId(getFinTypeExpenseDAO().save(finTypeExpense, tableType));
			auditHeader.getAuditDetail().setModelData(finTypeExpense);
			auditHeader.setAuditReference(String.valueOf(finTypeExpense.getFinType()));
		} else {
			getFinTypeExpenseDAO().update(finTypeExpense, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;

	}

	/**
	 * getFinTypeExpenseById fetch the details by using FinTypeExpenseDAO's getFinTypeExpenseById method.
	 * 
	 * @param finType
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinTypeExpense
	 */
	@Override
	public List<FinTypeExpense> getFinTypeExpenseById(String finType) {
		return finTypeExpenseDAO.getFinTypeExpenseListByFinType(finType, "_View");

	}

	/**
	 * getApprovedFinTypeExpenseById fetch the details by using FinTypeExpenseDAO's getFinTypeExpenseById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinTypeExpense.
	 * 
	 * @param id
	 *            (String)
	 * @return FinTypeExpense
	 */
	@Override
	public List<FinTypeExpense> getApprovedFinTypeExpenseById(String finType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinTypeFeesDAO().delete with
	 * parameters promotionFee,"" b) NEW Add new record in to main table by using getFinTypeFeesDAO().save with
	 * parameters promotionFee,"" c) EDIT Update record in the main table by using getFinTypeFeesDAO().update with
	 * parameters promotionFee,"" 3) Delete the record from the workFlow table by using getFinTypeFeesDAO().delete with
	 * parameters promotionFee,"_Temp" 4) Audit the record in to AuditHeader and AdtFinTypeExpense by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinTypeExpense by
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
		//auditHeader = businessValidation(auditHeader, "doApprove");

		/*if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}*/

		FinTypeExpense finTypeExpense = new FinTypeExpense("");
		BeanUtils.copyProperties((FinTypeExpense) auditHeader.getAuditDetail().getModelData(), finTypeExpense);

		if (PennantConstants.RECORD_TYPE_DEL.equals(finTypeExpense.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			getFinTypeExpenseDAO().delete(finTypeExpense, "");
		} else {
			finTypeExpense.setRoleCode("");
			finTypeExpense.setNextRoleCode("");
			finTypeExpense.setTaskId("");
			finTypeExpense.setNextTaskId("");
			finTypeExpense.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(finTypeExpense.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				finTypeExpense.setRecordType("");
				getFinTypeExpenseDAO().save(finTypeExpense, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finTypeExpense.setRecordType("");
				getFinTypeExpenseDAO().update(finTypeExpense, "");
			}
		}
		if(finTypeExpense.isWorkflow()){
		getFinTypeExpenseDAO().delete(finTypeExpense, "_TEMP");
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finTypeExpense);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinTypeFeesDAO().delete with parameters promotionFee,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinTypeExpense by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinTypeExpense finTypeExpense = (FinTypeExpense) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinTypeExpenseDAO().delete(finTypeExpense, "_TEMP");

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
		FinTypeExpense finTypeExpense = (FinTypeExpense) auditDetail.getModelData();

		FinTypeExpense tempFinTypeExpense = null;
		if (finTypeExpense.isWorkflow()) {
			tempFinTypeExpense = getFinTypeExpenseDAO().getFinTypeExpenseByID(finTypeExpense, "_Temp");
		}
		FinTypeExpense befFinTypeExpense = getFinTypeExpenseDAO().getFinTypeExpenseByID(finTypeExpense, "");

		FinTypeExpense oldFinTypeExpenseReference = finTypeExpense.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finTypeExpense.getExpenseTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_ExpenseCode") + ":" + valueParm[0];

		if (finTypeExpense.isNew()) { // for New record or new record into work flow
			if (!finTypeExpense.isWorkflow()) {// With out Work flow only new records
				if (befFinTypeExpense != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "4`1001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypeExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinTypeExpense != null || tempFinTypeExpense != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinTypeExpense == null || tempFinTypeExpense != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeExpense.isWorkflow()) { // With out Work flow for update and delete
				if (befFinTypeExpense == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeExpenseReference != null
							&& !oldFinTypeExpenseReference.getLastMntOn().equals(befFinTypeExpense.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempFinTypeExpense == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypeExpense != null && oldFinTypeExpenseReference != null
						&& !oldFinTypeExpenseReference.getLastMntOn().equals(tempFinTypeExpense.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeExpense.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeExpense);
		}

		return auditDetail;
	}

	@Override
	public List<AuditDetail> setFinTypeExpenseAuditData(List<FinTypeExpense> finTypeExpenseList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeExpense(), new FinTypeExpense().getExcludeFields());
		for (int i = 0; i < finTypeExpenseList.size(); i++) {
			FinTypeExpense finTypeExpense = finTypeExpenseList.get(i);

			if (StringUtils.isEmpty(finTypeExpense.getRecordType())) {
				continue;
			}

			//finTypeFees.setFinType(financeType.getFinType());
			//finTypeFees.setWorkflowId(financeType.getWorkflowId());
			boolean isRcdType = false;
			if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTypeExpense.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			//finTypeFees.setRecordStatus(financeType.getRecordStatus());
			//finTypeFees.setUserDetails(financeType.getUserDetails());
			//finTypeFees.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeExpense.getBefImage(),
					finTypeExpense));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processFinTypeExpenseDetails(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeExpense finTypeExpense = (FinTypeExpense) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeExpense.setRoleCode("");
				finTypeExpense.setNextRoleCode("");
				finTypeExpense.setTaskId("");
				finTypeExpense.setNextTaskId("");
				finTypeExpense.setWorkflowId(0);
			}
			//finTypeFees.setFinType(financeType.getFinType());
			if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeExpense.isNewRecord()) {
				saveRecord = true;
				if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeExpense.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeExpense.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeExpense.getRecordType();
				recordStatus = finTypeExpense.getRecordStatus();
				finTypeExpense.setRecordType("");
				finTypeExpense.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinTypeExpenseDAO().save(finTypeExpense, type);
			}
			if (updateRecord) {
				getFinTypeExpenseDAO().update(finTypeExpense, type);
			}
			if (deleteRecord) {
				getFinTypeExpenseDAO().delete(finTypeExpense, type);
			}
			if (approveRec) {
				finTypeExpense.setRecordType(rcdType);
				finTypeExpense.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finTypeExpense);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinTypeExpense> finTypeExpenseList, String tableType, String auditTranType,
			String finType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finTypeExpenseList != null && !finTypeExpenseList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeExpense(), new FinTypeExpense().getExcludeFields());
			for (int i = 0; i < finTypeExpenseList.size(); i++) {
				FinTypeExpense finTypeExpense = finTypeExpenseList.get(i);
				if (StringUtils.isNotEmpty(finTypeExpense.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeExpense.getBefImage(), finTypeExpense));
				}
			}
			getFinTypeExpenseDAO().deleteByFinType(finType, tableType);
		}

		return auditDetails;

	}

	@Override
	public FinTypeExpense getFinExpensesByFinType(String finType, long expenseTypeId) {
		
		return finTypeExpenseDAO.getFinTypeExpenseByFinType(finType, expenseTypeId, "_View");
	}

}