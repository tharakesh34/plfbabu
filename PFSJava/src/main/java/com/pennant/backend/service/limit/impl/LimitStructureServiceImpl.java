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
 * FileName    		:  LimitStructureServiceImpl.java                                       * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.limit.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitStructureDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.limit.LimitStructureService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>LimitStructure</b>.<br>
 * 
 */
public class LimitStructureServiceImpl extends GenericService<LimitStructure> implements LimitStructureService {
	private static final Logger logger = Logger.getLogger(LimitStructureServiceImpl.class);

	
	
	public LimitStructureServiceImpl() {
		super();
	}

	private AuditHeaderDAO auditHeaderDAO;
	private Set<String> excludeFields;
	private LimitStructureDetailDAO limitStructureDetailDAO;
	private LimitStructureDAO limitStructureDAO;
	private LimitDetailDAO limitDetailDAO;
	private LimitHeaderDAO limitHeaderDAO;


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
	 * @return the limitStructureDAO
	 */
	public LimitStructureDAO getLimitStructureDAO() {
		return limitStructureDAO;
	}
	/**
	 * @param limitStructureDAO the limitStructureDAO to set
	 */
	public void setLimitStructureDAO(LimitStructureDAO limitStructureDAO) {
		this.limitStructureDAO = limitStructureDAO;
	}

	/**
	 * @return the limitStructure
	 */
	@Override
	public LimitStructure getLimitStructure() {
		return getLimitStructureDAO().getLimitStructure();
	}
	/**
	 * @return the limitStructure for New Record
	 */
	@Override
	public LimitStructure getNewLimitStructure() {
		return getLimitStructureDAO().getNewLimitStructure();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LIMIT_STRUCTURE/LIMIT_STRUCTURE_Temp 
	 * 			by using LimitStructureDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using LimitStructureDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLIMIT_STRUCTURE by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LIMIT_STRUCTURE/LIMIT_STRUCTURE_Temp 
	 * 			by using LimitStructureDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using LimitStructureDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLIMIT_STRUCTURE by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */


	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";	
		LimitStructure limitStructure = (LimitStructure) auditHeader.getAuditDetail().getModelData();

		if (limitStructure.isWorkflow()) {
			tableType = "_Temp";
		}

		if (limitStructure.isNew()) {
			getLimitStructureDAO().save(limitStructure,tableType);
		} else {
			getLimitStructureDAO().update(limitStructure,tableType);
		}

		//Retrieving List of Audit Details For libraryArtefact  related modules
		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			auditHeader.setAuditDetails(processingLimitStructureDetailList(auditHeader.getAuditDetails(),tableType,limitStructure));
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LIMIT_STRUCTURE by using LimitStructureDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLIMIT_STRUCTURE by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LimitStructure limitStructure = (LimitStructure) auditHeader.getAuditDetail().getModelData();
		getLimitStructureDAO().delete(limitStructure,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLimitStructureById fetch the details by using LimitStructureDAO's getLimitStructureById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitStructure
	 */

	@Override
	public LimitStructure getLimitStructureById(String id) {
		logger.debug("Entering");
		LimitStructure limitStructure = getLimitStructureDAO().getLimitStructureById(id,"_View");
		if(limitStructure != null) {
			limitStructure.setLimitStructureDetailItemsList(getLimitStructureDetailDAO().getLimitStructureDetailById(id, "_View"));
		}
		logger.debug("Leaving");
		return limitStructure;
	}
	/**
	 * getApprovedLimitStructureById fetch the details by using LimitStructureDAO's getLimitStructureById method .
	 * with parameter id and type as blank. it fetches the approved records from the LIMIT_STRUCTURE.
	 * @param id (String)
	 * @return LimitStructure
	 */

	public LimitStructure getApprovedLimitStructureById(String id) {
		logger.debug("Entering");
		LimitStructure limitStructure = getLimitStructureDAO().getLimitStructureById(id,"_AView");
		if (limitStructure != null) {
			limitStructure.setLimitStructureDetailItemsList(getLimitStructureDetailDAO().getLimitStructureDetailById(id, "_AView"));
		}
		logger.debug("Leaving");
		return limitStructure;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getLimitStructureDAO().delete with
	 * parameters limitStructure,"" b) NEW Add new record in to main table by using getLimitStructureDAO().save with
	 * parameters limitStructure,"" c) EDIT Update record in the main table by using getLimitStructureDAO().update with
	 * parameters limitStructure,"" 3) Delete the record from the workFlow table by using getLimitStructureDAO().delete
	 * with parameters limitStructure,"_Temp" 4) Audit the record in to AuditHeader and AdtLIMIT_STRUCTURE by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtLIMIT_STRUCTURE
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		String tableType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LimitStructure limitStructure = new LimitStructure();
		BeanUtils.copyProperties((LimitStructure) auditHeader.getAuditDetail().getModelData(), limitStructure);
		/*if(!limitStructure.isScheduled()){
			 tableType = "_STAGING";
		}*/
		if (limitStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			if (!limitStructure.getLimitStructureDetailItemsList().isEmpty()) {
				getLimitStructureDetailDAO().deleteByStructureCode(limitStructure.getStructureCode(),"");	
			}
			getLimitStructureDAO().delete(limitStructure,"");
		} else {
			limitStructure.setRoleCode("");
			limitStructure.setNextRoleCode("");
			limitStructure.setTaskId("");
			limitStructure.setNextTaskId("");
			limitStructure.setWorkflowId(0);

			if (limitStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				limitStructure.setRecordType("");
				getLimitStructureDAO().save(limitStructure,tableType);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				limitStructure.setRecordType("");
				getLimitStructureDAO().update(limitStructure,tableType);
			}

			//Retrieving List of Audit Details For LimitStructure  related modules
			if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
				auditHeader.setAuditDetails(processingLimitStructureDetailList(auditHeader.getAuditDetails(),tableType,limitStructure));
				markRebuildOnSetUp(limitStructure.getStructureCode());
			}
		}

		getLimitStructureDetailDAO().deleteByStructureCode(limitStructure.getStructureCode(),"_TEMP");	
		getLimitStructureDAO().delete(limitStructure,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(limitStructure);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getLimitStructureDAO().delete with parameters limitStructure,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLIMIT_STRUCTURE by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LimitStructure limitStructure = (LimitStructure) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLimitStructureDetailDAO().deleteByStructureCode(limitStructure.getStructureCode(),"_TEMP");	
		getLimitStructureDAO().delete(limitStructure,"_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	validate the audit detail 
	 * 2)	if any error/Warnings  then assign the to auditHeader
	 * 3)   identify the nextprocess
	 *  
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean onlineRequest){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		LimitStructure aLimitGroup = (LimitStructure) auditHeader.getAuditDetail().getModelData();
		excludeFields = aLimitGroup.getExcludeFields();

		if(auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()){
			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				auditHeader.setErrorList(detail.getErrorDetails());	
			}
		}
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getLimitStructureDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		LimitStructure limitStructure = (LimitStructure) auditDetail.getModelData();
		String tableType = "";
		LimitStructure tempLimitStructure = null;
		if (limitStructure.isWorkflow()){
			tempLimitStructure = getLimitStructureDAO().getLimitStructureById(limitStructure.getId(), "_Temp");
		}
		/*if(!limitStructure.isScheduled()){
			tableType="_STAGING";
		}*/
		LimitStructure befLimitStructure = getLimitStructureDAO().getLimitStructureById(limitStructure.getId(),tableType);
		LimitStructure oldLimitStructure = limitStructure.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = limitStructure.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_StructureCode")+":"+valueParm[0];

		if (limitStructure.isNew()) { // for New record or new record into work flow
			if (!limitStructure.isWorkflow()) {// With out Work flow only new records  
				if (befLimitStructure != null) {	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			} else { // with work flow
				if (limitStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befLimitStructure != null || tempLimitStructure != null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befLimitStructure == null || tempLimitStructure != null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!limitStructure.isWorkflow()) {	// With out Work flow for update and delete
				if (befLimitStructure == null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				} else {
					if (oldLimitStructure != null && !oldLimitStructure.getLastMntOn().equals(befLimitStructure.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			} else {
				if (tempLimitStructure == null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				} else if (oldLimitStructure != null && !oldLimitStructure.getLastMntOn().equals(tempLimitStructure.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !limitStructure.isWorkflow()) {
			auditDetail.setBefImage(befLimitStructure);	
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method){
		logger.debug("Entering ");

		LimitStructure limitStructure = (LimitStructure) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (limitStructure.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}
		if (limitStructure.getLimitStructureDetailItemsList() != null && limitStructure.getLimitStructureDetailItemsList().size() > 0){
			auditHeader.setAuditDetails(setLimitStructureItemsAuditData(limitStructure,auditTranType,method));
		}
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param APIChannel
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setLimitStructureItemsAuditData(LimitStructure limitStructure,String auditTranType,String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete = false;

		if ((PennantConstants.RECORD_TYPE_DEL.equals(limitStructure.getRecordType()) && "doApprove".equalsIgnoreCase(method)) || "delete".equals(method)) {
			delete = true;
		}
		for (int i = 0; i < limitStructure.getLimitStructureDetailItemsList().size(); i++) {
			LimitStructureDetail limitStructureDetails  = limitStructure.getLimitStructureDetailItemsList().get(i);
			limitStructureDetails.setWorkflowId(limitStructure.getWorkflowId());
			excludeFields = limitStructureDetails.getExcludeFields();
			String[] fields = PennantJavaUtil.getFieldDetails(new LimitStructureDetail(),excludeFields);

			if (StringUtils.isEmpty(limitStructureDetails.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;

			if (delete) {
				limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			} else {
				if (limitStructureDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (limitStructureDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (limitStructureDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					limitStructureDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isRcdType = true;
				}
			}
			if("saveOrUpdate".equals(method) && (isRcdType && limitStructureDetails.isWorkflow())){
				//limitStructureDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (limitStructureDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (limitStructureDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || limitStructureDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				} else {
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}
			limitStructureDetails.setRecordStatus(limitStructure.getRecordStatus());
			limitStructureDetails.setUserDetails(limitStructure.getUserDetails());
			limitStructureDetails.setLastMntOn(limitStructure.getLastMntOn());
			limitStructureDetails.setLastMntBy(limitStructure.getLastMntBy());
			if (StringUtils.isNotEmpty(limitStructureDetails.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1],  limitStructureDetails.getBefImage(), limitStructureDetails));
			}
		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for utilityDetail
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingLimitStructureDetailList(List<AuditDetail> auditDetails, String type, LimitStructure limitStructure) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for(AuditDetail auditDetail : auditDetails){
			LimitStructureDetail limitStructureDetail = (LimitStructureDetail) auditDetail.getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			limitStructureDetail .setLimitStructureCode(limitStructure.getId());
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
			}

			if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (limitStructureDetail.isNewRecord()) {
				saveRecord = true;
				if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					limitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					limitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					limitStructureDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (limitStructureDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (limitStructureDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (StringUtils.isEmpty(type)) {
				limitStructureDetail.setRoleCode("");
				limitStructureDetail.setNextRoleCode("");
				limitStructureDetail.setTaskId("");
				limitStructureDetail.setNextTaskId("");
				limitStructureDetail.setRecordType("");
				limitStructureDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				limitStructureDetail.setWorkflowId(0);

			} else {
				limitStructureDetail.setRoleCode(limitStructure.getRoleCode());
				limitStructureDetail.setNextRoleCode(limitStructure.getNextRoleCode());
				limitStructureDetail.setTaskId(limitStructure.getTaskId());
				limitStructureDetail.setNextTaskId(limitStructure.getNextTaskId());
			}
			if (approveRec) {
				rcdType = limitStructureDetail.getRecordType();
				recordStatus = limitStructureDetail.getRecordStatus();
				limitStructureDetail.setRecordType("");
				limitStructureDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLimitStructureDetailDAO().save(limitStructureDetail, type);
			}
			if (updateRecord) {
				getLimitStructureDetailDAO().update(limitStructureDetail, type);
			}
			if (deleteRecord) {
				getLimitStructureDetailDAO().delete(limitStructureDetail, type);
			}
			
			if (approveRec) {
				limitStructureDetail.setRecordType(rcdType);
				limitStructureDetail.setRecordStatus(recordStatus);
			}
			auditDetail.setModelData(limitStructureDetail);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * @param limitStrCode
	 */
	public void markRebuildOnSetUp(String limitStrCode) {
		List<LimitHeader> header = getLimitHeaderDAO().getLimitHeaderByStructureCode(limitStrCode, "");
		for (LimitHeader limitHeader : header) {
			getLimitHeaderDAO().updateRebuild(limitHeader.getHeaderId(), true, "");
		}
	}




	@Override
	public int validationCheck(String lmtGrp, String type) {
		return getLimitStructureDetailDAO().validationCheck(lmtGrp, type);
	}

	@Override
	public int limitItemCheck(String lmtItem,  String limitCategory,String type) {
		return getLimitStructureDetailDAO().limitItemCheck(lmtItem, limitCategory, type);
	}

//	@Override
	public int limitStructureCheck(String structureCode) {
		return getLimitDetailDAO().limitStructureCheck(structureCode, "_View");
	}

	/**
	 * Method for fetch limit structure record count by structure id.
	 * 
	 * @param structureCode
	 * @return Integer
	 */
	@Override
	public int getLimitStructureCountById(String structureCode) {
		return getLimitStructureDAO().getLimitStructureCountById(structureCode, "");
	}

	@Override
	public List<LimitStructureDetail> getStructuredetailsByLimitGroup(String category,String groupCode, boolean isLine) {
		return getLimitStructureDetailDAO().getStructuredetailsByLimitGroup(category,groupCode,isLine, "_View");
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Set<String> getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(Set<String> excludeFields) {
		this.excludeFields = excludeFields;
	}

	public LimitStructureDetailDAO getLimitStructureDetailDAO() {
		return limitStructureDetailDAO;
	}

	public void setLimitStructureDetailDAO(LimitStructureDetailDAO limitStructureDetailDAO) {
		this.limitStructureDetailDAO = limitStructureDetailDAO;
	}

	public LimitDetailDAO getLimitDetailDAO() {
		return limitDetailDAO;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}

	public LimitHeaderDAO getLimitHeaderDAO() {
		return limitHeaderDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}


}