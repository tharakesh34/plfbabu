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
 * FileName    		:  FinTypeAccountingServiceImpl.java                                    * 	  
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
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;

/**
 * Service implementation for methods that depends on <b>FinTypeAccounting</b>.<br>
 * 
 */
public class FinTypeAccountingServiceImpl extends GenericService<FinTypeAccounting> implements FinTypeAccountingService {
	private static final Logger logger = Logger.getLogger(FinTypeAccountingServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private FinTypeAccountingDAO finTypeAccountingDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * FinTypeAccountings/FinTypeAccountings_Temp by using FinTypeAccountingsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinTypeAccountingsDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtFinTypeAccountings by using
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
		FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditHeader.getAuditDetail().getModelData();

		if (finTypeAccounting.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (finTypeAccounting.isNew()) {
			finTypeAccounting.setId(getFinTypeAccountingDAO().save(finTypeAccounting, tableType));
			auditHeader.getAuditDetail().setModelData(finTypeAccounting);
			auditHeader.setAuditReference(String.valueOf(finTypeAccounting.getFinType()));
		} else {
			getFinTypeAccountingDAO().update(finTypeAccounting, tableType);
		}

		if (StringUtils.isEmpty(tableType)) {
			AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(), finTypeAccounting.getEvent(), finTypeAccounting.getModuleId());
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		
		return auditHeader;

	}

	/**
	 * getFinTypeAccountingsById fetch the details by using FinTypeAccountingsDAO's getFinTypeAccountingsById
	 * method.
	 * 
	 * @param finType
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinTypeAccountings
	 */
	@Override
	public List<FinTypeAccounting> getFinTypeAccountingListByID(String finType, int moduleId) {
		return getFinTypeAccountingDAO().getFinTypeAccountingListByID(finType, moduleId, "_View");
	}

	/**
	 * getApprovedFinTypeAccountingsById fetch the details by using FinTypeAccountingsDAO's
	 * getFinTypeAccountingsById method . with parameter id and type as blank. it fetches
	 * the approved records from the FinTypeAccountings.
	 * 
	 * @param id
	 *            (String)
	 * @return FinTypeAccountings
	 */
	@Override
	public List<FinTypeAccounting> getApprovedFinTypeAccountingListByID(String finType, int moduleId) {
		return getFinTypeAccountingDAO().getFinTypeAccountingListByID(finType, moduleId, "_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinTypeAccountingDAO().delete with parameters promotionFee,"" b) NEW Add new
	 * record in to main table by using getFinTypeAccountingDAO().save with parameters
	 * promotionFee,"" c) EDIT Update record in the main table by using
	 * getFinTypeAccountingDAO().update with parameters promotionFee,"" 3) Delete the record
	 * from the workFlow table by using getFinTypeAccountingDAO().delete with parameters
	 * promotionFee,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtFinTypeAccountings by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtFinTypeAccountings by using
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

		FinTypeAccounting finTypeAccounting = new FinTypeAccounting("");
		BeanUtils.copyProperties((FinTypeAccounting) auditHeader.getAuditDetail().getModelData(), finTypeAccounting);

		if (PennantConstants.RECORD_TYPE_DEL.equals(finTypeAccounting.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			getFinTypeAccountingDAO().delete(finTypeAccounting, "");
		} else {
			finTypeAccounting.setRoleCode("");
			finTypeAccounting.setNextRoleCode("");
			finTypeAccounting.setTaskId("");
			finTypeAccounting.setNextTaskId("");
			finTypeAccounting.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(finTypeAccounting.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				finTypeAccounting.setRecordType("");
				getFinTypeAccountingDAO().save(finTypeAccounting, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finTypeAccounting.setRecordType("");
				getFinTypeAccountingDAO().update(finTypeAccounting, "");
			}
		}
		AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(), finTypeAccounting.getEvent(), finTypeAccounting.getModuleId());
		getFinTypeAccountingDAO().delete(finTypeAccounting, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finTypeAccounting);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getFinTypeAccountingDAO().delete with parameters
		 * promotionFee,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtFinTypeAccountings by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinTypeAccountingDAO().delete(finTypeAccounting, "_TEMP");

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
	 * from getFinTypeAccountingDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
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
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (finTypeAccounting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinTypeAccounting !=null || tempFinTypeAccounting!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinTypeAccounting ==null || tempFinTypeAccounting!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeAccounting.isWorkflow()){	// With out Work flow for update and delete

				if (befFinTypeAccounting ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail( PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (oldFinTypeAccounting!=null && !oldFinTypeAccounting.getLastMntOn().equals(befFinTypeAccounting.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{

				if (tempFinTypeAccounting==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}

				if (tempFinTypeAccounting!=null && oldFinTypeAccounting!=null && !oldFinTypeAccounting.getLastMntOn().equals(tempFinTypeAccounting.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeAccounting.isWorkflow()){
			auditDetail.setBefImage(befFinTypeAccounting);	
		}

		return auditDetail;
	}
	
	@Override
	public List<AuditDetail> setFinTypeAccountingAuditData(List<FinTypeAccounting> finTypeAccountingList, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccounting(), new FinTypeAccounting().getExcludeFields());
		for (int i = 0; i < finTypeAccountingList.size(); i++) {
			FinTypeAccounting finTypeAccounting = finTypeAccountingList.get(i);

			if (StringUtils.isEmpty(finTypeAccounting.getRecordType())) {
				continue;
			}

			//finTypeAccounting.setFinType(financeType.getFinType());
			//finTypeAccounting.setWorkflowId(financeType.getWorkflowId());
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
			//finTypeAccounting.setRecordStatus(financeType.getRecordStatus());
			//finTypeAccounting.setUserDetails(financeType.getUserDetails());
			//finTypeAccounting.setLastMntOn(financeType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],finTypeAccounting.getBefImage(), finTypeAccounting));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> processFinTypeAccountingDetails(List<AuditDetail> auditDetails, String type) {
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
				finTypeAccounting.setWorkflowId(0);
			}
			//finTypeAccounting.setFinType(financeType.getFinType());
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
			if (StringUtils.isEmpty(type)) {
				AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(), finTypeAccounting.getEvent(), finTypeAccounting.getModuleId());
			}

			auditDetails.get(i).setModelData(finTypeAccounting);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinTypeAccounting> finTypeAccountingList, String tableType, String auditTranType, String finType, int moduleId) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finTypeAccountingList != null && !finTypeAccountingList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccounting(), new FinTypeAccounting().getExcludeFields());
			for (int i = 0; i < finTypeAccountingList.size(); i++) {
				FinTypeAccounting finTypeAccounting = finTypeAccountingList.get(i);
				if (StringUtils.isNotEmpty(finTypeAccounting.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finTypeAccounting.getBefImage(), finTypeAccounting));
				}
				if (StringUtils.isEmpty(tableType)) {
					AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(), finTypeAccounting.getEvent(), finTypeAccounting.getModuleId());
				}
			}
			getFinTypeAccountingDAO().deleteByFinType(finType, moduleId, tableType);
		}

		logger.debug("Leaving");

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

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}


}