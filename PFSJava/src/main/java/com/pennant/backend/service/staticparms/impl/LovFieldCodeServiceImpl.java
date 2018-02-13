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
 * FileName    		:  LovFieldCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.LovFieldCodeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.LovFieldCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>LovFieldCode</b>.<br>
 * 
 */
public class LovFieldCodeServiceImpl extends GenericService<LovFieldCode> implements LovFieldCodeService {
	private static final Logger logger = Logger.getLogger(LovFieldCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private LovFieldCodeDAO lovFieldCodeDAO;

	public LovFieldCodeServiceImpl() {
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

	public LovFieldCodeDAO getLovFieldCodeDAO() {
		return lovFieldCodeDAO;
	}
	public void setLovFieldCodeDAO(LovFieldCodeDAO lovFieldCodeDAO) {
		this.lovFieldCodeDAO = lovFieldCodeDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table BMTLovFieldCode/BMTLovFieldCode_Temp 
	 * 			by using LovFieldCodeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using LovFieldCodeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtBMTLovFieldCode by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		LovFieldCode lovFieldCode = (LovFieldCode) auditHeader.getAuditDetail().getModelData();

		if (lovFieldCode.isWorkflow()) {
			tableType="_Temp";
		}

		if (lovFieldCode.isNew()) {
			getLovFieldCodeDAO().save(lovFieldCode,tableType);
		}else{
			getLovFieldCodeDAO().update(lovFieldCode,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table BMTLovFieldCode by using LovFieldCodeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtBMTLovFieldCode by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LovFieldCode lovFieldCode = (LovFieldCode) auditHeader.getAuditDetail().getModelData();
		getLovFieldCodeDAO().delete(lovFieldCode,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLovFieldCodeById fetch the details by using LovFieldCodeDAO's getLovFieldCodeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LovFieldCode
	 */

	@Override
	public LovFieldCode getLovFieldCodeById(String id) {
		return getLovFieldCodeDAO().getLovFieldCodeById(id,"_View");
	}
	/**
	 * getApprovedLovFieldCodeById fetch the details by using LovFieldCodeDAO's getLovFieldCodeById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTLovFieldCode.
	 * @param id (String)
	 * @return LovFieldCode
	 */

	public LovFieldCode getApprovedLovFieldCodeById(String id) {
		return getLovFieldCodeDAO().getLovFieldCodeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getLovFieldCodeDAO().delete with
	 * parameters lovFieldCode,"" b) NEW Add new record in to main table by using getLovFieldCodeDAO().save with
	 * parameters lovFieldCode,"" c) EDIT Update record in the main table by using getLovFieldCodeDAO().update with
	 * parameters lovFieldCode,"" 3) Delete the record from the workFlow table by using getLovFieldCodeDAO().delete with
	 * parameters lovFieldCode,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTLovFieldCode by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtBMTLovFieldCode
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LovFieldCode lovFieldCode = new LovFieldCode();
		BeanUtils.copyProperties((LovFieldCode) auditHeader.getAuditDetail().getModelData(), lovFieldCode);

		if (lovFieldCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getLovFieldCodeDAO().delete(lovFieldCode,"");

		} else {
			lovFieldCode.setRoleCode("");
			lovFieldCode.setNextRoleCode("");
			lovFieldCode.setTaskId("");
			lovFieldCode.setNextTaskId("");
			lovFieldCode.setWorkflowId(0);

			if (lovFieldCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				lovFieldCode.setRecordType("");
				getLovFieldCodeDAO().save(lovFieldCode,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				lovFieldCode.setRecordType("");
				getLovFieldCodeDAO().update(lovFieldCode,"");
			}
		}

		getLovFieldCodeDAO().delete(lovFieldCode,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(lovFieldCode);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getLovFieldCodeDAO().delete with parameters lovFieldCode,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtBMTLovFieldCode by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LovFieldCode lovFieldCode = (LovFieldCode) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLovFieldCodeDAO().delete(lovFieldCode,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getLovFieldCodeDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		LovFieldCode lovFieldCode= (LovFieldCode) auditDetail.getModelData();

		LovFieldCode tempLovFieldCode= null;
		if (lovFieldCode.isWorkflow()){
			tempLovFieldCode = getLovFieldCodeDAO().getLovFieldCodeById(lovFieldCode.getId(), "_Temp");
		}
		LovFieldCode befLovFieldCode= getLovFieldCodeDAO().getLovFieldCodeById(lovFieldCode.getId(), "");

		LovFieldCode oldLovFieldCode= lovFieldCode.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=lovFieldCode.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FieldCode")+":"+valueParm[0];

		if (lovFieldCode.isNew()){ // for New record or new record into work flow

			if (!lovFieldCode.isWorkflow()){// With out Work flow only new records  
				if (befLovFieldCode !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (lovFieldCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befLovFieldCode !=null || tempLovFieldCode!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befLovFieldCode ==null || tempLovFieldCode!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!lovFieldCode.isWorkflow()){	// With out Work flow for update and delete

				if (befLovFieldCode ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldLovFieldCode!=null && !oldLovFieldCode.getLastMntOn().equals(befLovFieldCode.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempLovFieldCode==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempLovFieldCode!=null && oldLovFieldCode!=null && !oldLovFieldCode.getLastMntOn().equals(tempLovFieldCode.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !lovFieldCode.isWorkflow()){
			lovFieldCode.setBefImage(befLovFieldCode);	
		}

		return auditDetail;
	}

}