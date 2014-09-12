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
 * FileName    		:  CommidityLoanDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters.impl;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.CommidityLoanDetailDAO;
import com.pennant.backend.dao.lmtmasters.CommidityLoanHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.CommidityLoanDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CommidityLoanDetail</b>.<br>
 * 
 */
public class CommidityLoanDetailServiceImpl extends GenericService<CommidityLoanDetail> implements CommidityLoanDetailService {
	private final static Logger logger = Logger.getLogger(CommidityLoanDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private CommidityLoanDetailDAO commidityLoanDetailDAO;
	private CommidityLoanHeaderDAO commidityLoanHeaderDAO;

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
	 * @return the commidityLoanDetailDAO
	 */
	public CommidityLoanDetailDAO getCommidityLoanDetailDAO() {
		return commidityLoanDetailDAO;
	}
	/**
	 * @param commidityLoanDetailDAO the commidityLoanDetailDAO to set
	 */
	public void setCommidityLoanDetailDAO(CommidityLoanDetailDAO commidityLoanDetailDAO) {
		this.commidityLoanDetailDAO = commidityLoanDetailDAO;
	}
	

	public void setCommidityLoanHeaderDAO(CommidityLoanHeaderDAO commidityLoanHeaderDAO) {
	    this.commidityLoanHeaderDAO = commidityLoanHeaderDAO;
    }

	public CommidityLoanHeaderDAO getCommidityLoanHeaderDAO() {
	    return commidityLoanHeaderDAO;
    }

	/**
	 * @return the commidityLoanDetail
	 */
	@Override
	public CommidityLoanDetail getCommidityLoanDetail() {
		return getCommidityLoanDetailDAO().getCommidityLoanDetail();
	}
	/**
	 * @return the commidityLoanDetail for New Record
	 */
	@Override
	public CommidityLoanDetail getNewCommidityLoanDetail() {
		return getCommidityLoanDetailDAO().getNewCommidityLoanDetail();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LMTCommidityLoanDetail/LMTCommidityLoanDetail_Temp 
	 * 			by using CommidityLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommidityLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table LMTCommidityLoanDetail/LMTCommidityLoanDetail_Temp 
	 * 			by using CommidityLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommidityLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		String tableType="";
		CommidityLoanDetail commidityLoanDetail = (CommidityLoanDetail) auditHeader.getAuditDetail().getModelData();

		if (commidityLoanDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (commidityLoanDetail.isNew()) {
			getCommidityLoanDetailDAO().save(commidityLoanDetail,tableType);
		}else{
			getCommidityLoanDetailDAO().update(commidityLoanDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTCommidityLoanDetail by using CommidityLoanDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		CommidityLoanDetail commidityLoanDetail = (CommidityLoanDetail) auditHeader.getAuditDetail().getModelData();
		getCommidityLoanDetailDAO().delete(commidityLoanDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCommidityLoanDetailById fetch the details by using CommidityLoanDetailDAO's getCommidityLoanDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommidityLoanDetail
	 */

	@Override
	public CommidityLoanDetail getCommidityLoanDetailById(String id,String itemType) {
		return getCommidityLoanDetailDAO().getCommidityLoanDetailById(id,itemType,"_View");
	}
	/**
	 * getApprovedCommidityLoanDetailById fetch the details by using CommidityLoanDetailDAO's getCommidityLoanDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the LMTCommidityLoanDetail.
	 * @param id (String)
	 * @return CommidityLoanDetail
	 */

	public CommidityLoanDetail getApprovedCommidityLoanDetailById(String id,String itemType) {
		return getCommidityLoanDetailDAO().getCommidityLoanDetailById(id,itemType,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param CommidityLoanDetail (commidityLoanDetail)
	 * @return commidityLoanDetail
	 */
	@Override
	public CommidityLoanDetail refresh(CommidityLoanDetail commidityLoanDetail) {
		logger.debug("Entering");
		getCommidityLoanDetailDAO().refresh(commidityLoanDetail);
		getCommidityLoanDetailDAO().initialize(commidityLoanDetail);
		logger.debug("Leaving");
		return commidityLoanDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCommidityLoanDetailDAO().delete with parameters commidityLoanDetail,""
	 * 		b)  NEW		Add new record in to main table by using getCommidityLoanDetailDAO().save with parameters commidityLoanDetail,""
	 * 		c)  EDIT	Update record in the main table by using getCommidityLoanDetailDAO().update with parameters commidityLoanDetail,""
	 * 3)	Delete the record from the workFlow table by using getCommidityLoanDetailDAO().delete with parameters commidityLoanDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		CommidityLoanDetail commidityLoanDetail = new CommidityLoanDetail();
		BeanUtils.copyProperties((CommidityLoanDetail) auditHeader.getAuditDetail().getModelData(), commidityLoanDetail);

		if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCommidityLoanDetailDAO().delete(commidityLoanDetail,"");

		} else {
			commidityLoanDetail.setRoleCode("");
			commidityLoanDetail.setNextRoleCode("");
			commidityLoanDetail.setTaskId("");
			commidityLoanDetail.setNextTaskId("");
			commidityLoanDetail.setWorkflowId(0);

			if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				commidityLoanDetail.setRecordType("");
				getCommidityLoanDetailDAO().save(commidityLoanDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				commidityLoanDetail.setRecordType("");
				getCommidityLoanDetailDAO().update(commidityLoanDetail,"");
			}
		}

		getCommidityLoanDetailDAO().delete(commidityLoanDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commidityLoanDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCommidityLoanDetailDAO().delete with parameters commidityLoanDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		CommidityLoanDetail commidityLoanDetail = (CommidityLoanDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCommidityLoanDetailDAO().delete(commidityLoanDetail,"_TEMP");

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
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getCommidityLoanDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		CommidityLoanDetail commidityLoanDetail= (CommidityLoanDetail) auditDetail.getModelData();

		CommidityLoanDetail tempCommidityLoanDetail= null;
		if (commidityLoanDetail.isWorkflow()){
			tempCommidityLoanDetail = getCommidityLoanDetailDAO().getCommidityLoanDetailById(commidityLoanDetail.getId(),commidityLoanDetail.getItemType(), "_Temp");
		}
		CommidityLoanDetail befCommidityLoanDetail= getCommidityLoanDetailDAO().getCommidityLoanDetailById(commidityLoanDetail.getId(),commidityLoanDetail.getItemType(), "");
		CommidityLoanDetail oldCommidityLoanDetail= commidityLoanDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=commidityLoanDetail.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

		if (commidityLoanDetail.isNew()){ // for New record or new record into work flow

			if (!commidityLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befCommidityLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommidityLoanDetail !=null || tempCommidityLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommidityLoanDetail ==null || tempCommidityLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commidityLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befCommidityLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCommidityLoanDetail!=null && !oldCommidityLoanDetail.getLastMntOn().equals(befCommidityLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCommidityLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldCommidityLoanDetail!=null && !oldCommidityLoanDetail.getLastMntOn().equals(tempCommidityLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !commidityLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befCommidityLoanDetail);	
		}

		return auditDetail;
	}


	@Override
	public CommidityLoanHeader getCommidityLoanHeaderById(String id) {
		logger.debug("Entering");
		CommidityLoanHeader commidityLoanHeader = null; 
		commidityLoanHeader =  getCommidityLoanHeaderDAO().getCommidityLoanHeaderById(id, "_View");

		if(commidityLoanHeader != null) {
			commidityLoanHeader.setCommidityLoanDetails(getCommidityLoanDetailDAO().getCommidityLoanDetailByFinRef(id, "_View"));
		}

		logger.debug("Leaving");
		return commidityLoanHeader;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(CommidityLoanHeader commidityLoanHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(commidityLoanHeader, commidityLoanHeader.getExcludeFields());

		commidityLoanHeader.setWorkflowId(0);
		if (commidityLoanHeader.isNewRecord()) {
			getCommidityLoanHeaderDAO().save(commidityLoanHeader, tableType);
		} else {
			getCommidityLoanHeaderDAO().update(commidityLoanHeader, tableType);
		}

		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], commidityLoanHeader.getBefImage(), commidityLoanHeader));

		List<CommidityLoanDetail> commidityLoanDetails = commidityLoanHeader.getCommidityLoanDetails();

		if (commidityLoanDetails != null && !commidityLoanDetails.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (CommidityLoanDetail commidityLoanDetail : commidityLoanDetails) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";

				commidityLoanDetail.setWorkflowId(0);		
				if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (commidityLoanDetail.isNewRecord()) {
					saveRecord = true;
					if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (commidityLoanDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = commidityLoanDetail.getRecordType();
					recordStatus = commidityLoanDetail.getRecordStatus();
					commidityLoanDetail.setRecordType("");
					commidityLoanDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getCommidityLoanDetailDAO().save(commidityLoanDetail, tableType);
				}

				if (updateRecord) {
					getCommidityLoanDetailDAO().update(commidityLoanDetail, tableType);
				}

				if (deleteRecord) {
					getCommidityLoanDetailDAO().delete(commidityLoanDetail, tableType);
				}

				if (approveRec) {
					commidityLoanDetail.setRecordType(rcdType);
					commidityLoanDetail.setRecordStatus(recordStatus);
				}

				fields = PennantJavaUtil.getFieldDetails(commidityLoanDetail, commidityLoanDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], commidityLoanDetail.getBefImage(), commidityLoanDetail));
				i++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> doApprove(CommidityLoanHeader commidityLoanHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(commidityLoanHeader, commidityLoanHeader.getExcludeFields());
		CommidityLoanHeader header = new CommidityLoanHeader();
		BeanUtils.copyProperties(commidityLoanHeader, header);
		
		if (tableType.equals("")) {
			commidityLoanHeader.setRoleCode("");
			commidityLoanHeader.setNextRoleCode("");
			commidityLoanHeader.setTaskId("");
			commidityLoanHeader.setNextTaskId("");
		}
		
	
		commidityLoanHeader.setWorkflowId(0);

		getCommidityLoanHeaderDAO().save(commidityLoanHeader, tableType);

		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], header.getBefImage(), header));
		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], commidityLoanHeader.getBefImage(), commidityLoanHeader));

		List<CommidityLoanDetail> commidityLoanDetails = commidityLoanHeader.getCommidityLoanDetails();

		if(commidityLoanDetails !=null && !commidityLoanDetails.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			
			for (CommidityLoanDetail commidityLoanDetail : commidityLoanDetails) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = true;
				String rcdType = "";
				String recordStatus = "";

				CommidityLoanDetail detail = new CommidityLoanDetail();

				BeanUtils.copyProperties(commidityLoanDetail, detail);
				
				if (tableType.equals("")) {
					approveRec = true;
					commidityLoanDetail.setRoleCode("");
					commidityLoanDetail.setNextRoleCode("");
					commidityLoanDetail.setTaskId("");
					commidityLoanDetail.setNextTaskId("");
				}
				
				commidityLoanDetail.setWorkflowId(0);		
				if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (commidityLoanDetail.isNewRecord()) {
					saveRecord = true;
					if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (commidityLoanDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = commidityLoanDetail.getRecordType();
					recordStatus = commidityLoanDetail.getRecordStatus();
					commidityLoanDetail.setRecordType("");
					commidityLoanDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getCommidityLoanDetailDAO().save(commidityLoanDetail, tableType);
				}

				if (updateRecord) {
					getCommidityLoanDetailDAO().update(commidityLoanDetail, tableType);
				}

				if (deleteRecord) {
					getCommidityLoanDetailDAO().delete(commidityLoanDetail, tableType);
				}

				if (approveRec) {
					commidityLoanDetail.setRecordType(rcdType);
					commidityLoanDetail.setRecordStatus(recordStatus);
				}

				fields = PennantJavaUtil.getFieldDetails(commidityLoanDetail, commidityLoanDetail.getExcludeFields());
				auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], commidityLoanDetail.getBefImage(), commidityLoanDetail));
				i++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(CommidityLoanHeader commidityLoanHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(commidityLoanHeader, commidityLoanHeader.getExcludeFields());	
		String[] childFields = null;	

		List<CommidityLoanDetail> commidityLoanDetails = commidityLoanHeader.getCommidityLoanDetails(); 

		if(commidityLoanDetails != null && !commidityLoanDetails.isEmpty()) {
			for (CommidityLoanDetail commidityLoanDetail : commidityLoanDetails) {
				getCommidityLoanDetailDAO().delete(commidityLoanDetail, tableType);
				childFields = PennantJavaUtil.getFieldDetails(commidityLoanDetail, commidityLoanDetail.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, childFields[0], childFields[1], commidityLoanDetail.getBefImage(), commidityLoanDetail));
			}
		}

		getCommidityLoanHeaderDAO().delete(commidityLoanHeader, tableType);
		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], commidityLoanHeader.getBefImage(), commidityLoanHeader));

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getAuditDetail(List<CommidityLoanDetail> commidityLoanDetails, String auditTranType, String method, long workFlowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	
		for (CommidityLoanDetail commidityLoanDetail : commidityLoanDetails) {
			
			if("doApprove".equals(method) && !commidityLoanDetail.getRecordStatus().equals(PennantConstants.RCD_STATUS_SAVED))  {
				commidityLoanDetail.setWorkflowId(0);
				commidityLoanDetail.setNewRecord(true);
			} else {
				commidityLoanDetail.setWorkflowId(workFlowId);
			}
			
			boolean isRcdType = false;

			if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RCD_DEL)) {
				commidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				commidityLoanDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| commidityLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(commidityLoanDetail, commidityLoanDetail.getExcludeFields());
			if (!commidityLoanDetail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], commidityLoanDetail.getBefImage(), commidityLoanDetail));
			}

		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<CommidityLoanDetail> commidityLoanDetails, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(commidityLoanDetails, workflowId, method, auditTranType, usrLanguage);
	}
	
	private List<AuditDetail> doValidation(List<CommidityLoanDetail> commidityLoanDetails, long workflowId, String method, String auditTranType, String usrLanguage){
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(commidityLoanDetails, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage); 
		}

		logger.debug("Leaving");
		return auditDetails ;
	}
	
	private AuditDetail validate(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		CommidityLoanDetail commidityLoanDetail= (CommidityLoanDetail) auditDetail.getModelData();
		CommidityLoanDetail tempCommidityLoanDetail= null;
		if (commidityLoanDetail.isWorkflow()){
			tempCommidityLoanDetail = getCommidityLoanDetailDAO().getCommidityLoanDetailById(commidityLoanDetail.getLoanRefNumber(),commidityLoanDetail.getItemType(), "_Temp");
		}
		CommidityLoanDetail befCommidityLoanDetail= getCommidityLoanDetailDAO().getCommidityLoanDetailById(commidityLoanDetail.getLoanRefNumber(),commidityLoanDetail.getItemType(), "");
		CommidityLoanDetail oldCommidityLoanDetail= commidityLoanDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(commidityLoanDetail.getLoanRefNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_loanReferenceNumber")+":"+valueParm[0];

		if (commidityLoanDetail.isNew()){ // for New record or new record into work flow

			if (!commidityLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befCommidityLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommidityLoanDetail != null || tempCommidityLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommidityLoanDetail ==null || tempCommidityLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commidityLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befCommidityLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCommidityLoanDetail!=null && !oldCommidityLoanDetail.getLastMntOn().equals(befCommidityLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCommidityLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempCommidityLoanDetail!=null && oldCommidityLoanDetail!=null && !oldCommidityLoanDetail.getLastMntOn().equals(tempCommidityLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !commidityLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befCommidityLoanDetail);	
		}
		return auditDetail;
	}



}