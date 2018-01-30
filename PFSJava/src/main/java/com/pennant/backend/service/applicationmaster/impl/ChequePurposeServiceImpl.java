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
 * FileName    		:  ChequePurposeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ChequePurposeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.ManagerChequeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.ChequePurpose;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ChequePurposeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ChequePurpose</b>.<br>
 */
public class ChequePurposeServiceImpl extends GenericService<ChequePurpose> implements ChequePurposeService {
	private static final Logger logger = Logger.getLogger(ChequePurposeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private ChequePurposeDAO chequePurposeDAO;
	private ManagerChequeDAO managerChequeDAO;

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
	 * @return the chequePurposeDAO
	 */
	public ChequePurposeDAO getChequePurposeDAO() {
		return chequePurposeDAO;
	}
	/**
	 * @param chequePurposeDAO the chequePurposeDAO to set
	 */
	public void setChequePurposeDAO(ChequePurposeDAO chequePurposeDAO) {
		this.chequePurposeDAO = chequePurposeDAO;
	}

	/**
	 * @return the managerChequeDAO
	 */
	public ManagerChequeDAO getManagerChequeDAO() {
		return managerChequeDAO;
	}
	/**
	 * @param managerChequeDAO the managerChequeDAO to set
	 */
	public void setManagerChequeDAO(ManagerChequeDAO managerChequeDAO) {
		this.managerChequeDAO = managerChequeDAO;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table ChequePurpose/ChequePurpose_Temp 
	 * 			by using ChequePurposeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ChequePurposeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtChequePurpose by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table ChequePurpose/ChequePurpose_Temp 
	 * 			by using ChequePurposeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ChequePurposeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtChequePurpose by using auditHeaderDAO.addAudit(auditHeader)
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
		ChequePurpose chequePurpose = (ChequePurpose) auditHeader.getAuditDetail().getModelData();

		if (chequePurpose.isWorkflow()) {
			tableType="_Temp";
		}

		if (chequePurpose.isNew()) {
			getChequePurposeDAO().save(chequePurpose,tableType);
		}else{
			getChequePurposeDAO().update(chequePurpose,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table ChequePurpose by using ChequePurposeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtChequePurpose by using auditHeaderDAO.addAudit(auditHeader)    
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

		ChequePurpose chequePurpose = (ChequePurpose) auditHeader.getAuditDetail().getModelData();
		getChequePurposeDAO().delete(chequePurpose,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getChequePurposeById fetch the details by using ChequePurposeDAO's getChequePurposeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ChequePurpose
	 */

	@Override
	public ChequePurpose getChequePurposeById(String id) {
		return getChequePurposeDAO().getChequePurposeById(id,"_View");
	}
	/**
	 * getApprovedChequePurposeById fetch the details by using ChequePurposeDAO's getChequePurposeById method .
	 * with parameter id and type as blank. it fetches the approved records from the ChequePurpose.
	 * @param id (String)
	 * @return ChequePurpose
	 */

	public ChequePurpose getApprovedChequePurposeById(String id) {
		return getChequePurposeDAO().getChequePurposeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getChequePurposeDAO().delete with parameters chequePurpose,""
	 * 		b)  NEW		Add new record in to main table by using getChequePurposeDAO().save with parameters chequePurpose,""
	 * 		c)  EDIT	Update record in the main table by using getChequePurposeDAO().update with parameters chequePurpose,""
	 * 3)	Delete the record from the workFlow table by using getChequePurposeDAO().delete with parameters chequePurpose,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtChequePurpose by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtChequePurpose by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		ChequePurpose chequePurpose = new ChequePurpose();
		BeanUtils.copyProperties((ChequePurpose) auditHeader.getAuditDetail().getModelData(), chequePurpose);

		if (chequePurpose.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getChequePurposeDAO().delete(chequePurpose,"");

		} else {
			chequePurpose.setRoleCode("");
			chequePurpose.setNextRoleCode("");
			chequePurpose.setTaskId("");
			chequePurpose.setNextTaskId("");
			chequePurpose.setWorkflowId(0);

			if (chequePurpose.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				chequePurpose.setRecordType("");
				getChequePurposeDAO().save(chequePurpose,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				chequePurpose.setRecordType("");
				getChequePurposeDAO().update(chequePurpose,"");
			}
		}

		getChequePurposeDAO().delete(chequePurpose,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(chequePurpose);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getChequePurposeDAO().delete with parameters chequePurpose,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtChequePurpose by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ChequePurpose chequePurpose = (ChequePurpose) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getChequePurposeDAO().delete(chequePurpose,"_Temp");

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
	 * 5)	for any mismatch conditions Fetch the error details from getChequePurposeDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ChequePurpose chequePurpose = (ChequePurpose) auditDetail.getModelData();

		ChequePurpose tempChequePurpose= null;
		if (chequePurpose.isWorkflow()){
			tempChequePurpose = getChequePurposeDAO().getChequePurposeById(chequePurpose.getId(), "_Temp");
		}
		ChequePurpose befChequePurpose= getChequePurposeDAO().getChequePurposeById(chequePurpose.getId(), "");

		ChequePurpose oldChequePurpose = chequePurpose.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=chequePurpose.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_Code")+":"+valueParm[0];

		if (chequePurpose.isNew()){ // for New record or new record into work flow

			if (!chequePurpose.isWorkflow()){// With out Work flow only new records  
				if (befChequePurpose !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (chequePurpose.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befChequePurpose !=null || tempChequePurpose!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befChequePurpose ==null || tempChequePurpose!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!chequePurpose.isWorkflow()){	// With out Work flow for update and delete

				if (befChequePurpose ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldChequePurpose!=null && !oldChequePurpose.getLastMntOn().equals(befChequePurpose.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempChequePurpose==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempChequePurpose!=null && oldChequePurpose!=null && !oldChequePurpose.getLastMntOn().equals(tempChequePurpose.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		if(!(PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(chequePurpose.getRecordStatus()) || 
				PennantConstants.RCD_STATUS_RESUBMITTED.equalsIgnoreCase(chequePurpose.getRecordStatus()) ||
				PennantConstants.RCD_STATUS_REJECTED.equalsIgnoreCase(chequePurpose.getRecordStatus()))) {
			int count;
			if(!chequePurpose.isActive()){
				count = getManagerChequeDAO().getMgrChqCountByChqPurposeCode(chequePurpose.getCode(), "_Temp");
				if(count > 0){
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41017", errParm,valueParm), usrLanguage));
				}
			}else if(PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(chequePurpose.getRecordType())) {
				count = getManagerChequeDAO().getMgrChqCountByChqPurposeCode(chequePurpose.getCode(), "_View");
				if(count > 0){
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm,valueParm), usrLanguage));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !chequePurpose.isWorkflow()){
			auditDetail.setBefImage(befChequePurpose);	
		}

		return auditDetail;
	}
}