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
 * FileName    		:  MortgageLoanDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.MortgageLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.MortgageLoanDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>MortgageLoanDetail</b>.<br>
 */
public class MortgageLoanDetailServiceImpl extends GenericService<MortgageLoanDetail> implements MortgageLoanDetailService {
	private final static Logger logger = Logger.getLogger(MortgageLoanDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private MortgageLoanDetailDAO mortgageLoanDetailDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public MortgageLoanDetailDAO getMortgageLoanDetailDAO() {
		return mortgageLoanDetailDAO;
	}
	public void setMortgageLoanDetailDAO(MortgageLoanDetailDAO mortgageLoanDetailDAO) {
		this.mortgageLoanDetailDAO = mortgageLoanDetailDAO;
	}

	@Override
	public MortgageLoanDetail getMortgageLoanDetail() {
		return getMortgageLoanDetailDAO().getMortgageLoanDetail();
	}

	@Override
	public MortgageLoanDetail getNewMortgageLoanDetail() {
		return getMortgageLoanDetailDAO().getNewMortgageLoanDetail();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table 
	 * 				LMTMortgageLoanDetail/LMTMortgageLoanDetail_Temp 
	 * 				by using MortgageLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using MortgageLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTMortgageLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)
	 * 
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
		MortgageLoanDetail mortgageLoanDetail = (MortgageLoanDetail) auditHeader.getAuditDetail().getModelData();

		if (mortgageLoanDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (mortgageLoanDetail.isNew()) {
			mortgageLoanDetail.setId(getMortgageLoanDetailDAO().save(mortgageLoanDetail,tableType));
			auditHeader.getAuditDetail().setModelData(mortgageLoanDetail);
			auditHeader.setAuditReference(mortgageLoanDetail.getId());
		}else{
			getMortgageLoanDetailDAO().update(mortgageLoanDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTMortgageLoanDetail by using 
	 * 			MortgageLoanDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTMortgageLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)    
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

		MortgageLoanDetail mortgageLoanDetail = (MortgageLoanDetail) auditHeader.getAuditDetail().getModelData();
		getMortgageLoanDetailDAO().delete(mortgageLoanDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getMortgageLoanDetailById fetch the details by using
	 * MortgageLoanDetailDAO's getMortgageLoanDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MortgageLoanDetail
	 */
	@Override
	public MortgageLoanDetail getMortgageLoanDetailById(String id) {
		return getMortgageLoanDetailDAO().getMortgageLoanDetailById(id,"_View");
	}

	/**
	 * getApprovedMortgageLoanDetailById fetch the details by using
	 * MortgageLoanDetailDAO's getMortgageLoanDetailById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * LMTMortgageLoanDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return MortgageLoanDetail
	 */
	public MortgageLoanDetail getApprovedMortgageLoanDetailById(String id) {
		return getMortgageLoanDetailDAO().getMortgageLoanDetailById(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param MortgageLoanDetail (mortgageLoanDetail)
	 * @return mortgageLoanDetail
	 */
	@Override
	public MortgageLoanDetail refresh(MortgageLoanDetail mortgageLoanDetail) {
		logger.debug("Entering");
		getMortgageLoanDetailDAO().refresh(mortgageLoanDetail);
		getMortgageLoanDetailDAO().initialize(mortgageLoanDetail);
		logger.debug("Leaving");
		return mortgageLoanDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 				getMortgageLoanDetailDAO().delete with parameters mortgageLoanDetail,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 				getMortgageLoanDetailDAO().save with parameters mortgageLoanDetail,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 				getMortgageLoanDetailDAO().update with parameters mortgageLoanDetail,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 			getMortgageLoanDetailDAO().delete with parameters mortgageLoanDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTMortgageLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTMortgageLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		MortgageLoanDetail mortgageLoanDetail = new MortgageLoanDetail();
		BeanUtils.copyProperties((MortgageLoanDetail) auditHeader.getAuditDetail().getModelData(), mortgageLoanDetail);

		if (mortgageLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getMortgageLoanDetailDAO().delete(mortgageLoanDetail,"");
		} else {
			mortgageLoanDetail.setRoleCode("");
			mortgageLoanDetail.setNextRoleCode("");
			mortgageLoanDetail.setTaskId("");
			mortgageLoanDetail.setNextTaskId("");
			mortgageLoanDetail.setWorkflowId(0);

			if (mortgageLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				mortgageLoanDetail.setRecordType("");
				getMortgageLoanDetailDAO().save(mortgageLoanDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				mortgageLoanDetail.setRecordType("");
				getMortgageLoanDetailDAO().update(mortgageLoanDetail,"");
			}
		}

		getMortgageLoanDetailDAO().delete(mortgageLoanDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mortgageLoanDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}
	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 			getMortgageLoanDetailDAO().delete with parameters mortgageLoanDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTMortgageLoanDetail by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		MortgageLoanDetail mortgageLoanDetail = (MortgageLoanDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getMortgageLoanDetailDAO().delete(mortgageLoanDetail,"_TEMP");

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
	 * 5)	for any mismatch conditions Fetch the error details from 
	 * 			getMortgageLoanDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		auditHeader = doValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditDetail validate(MortgageLoanDetail mortgageLoanDetail, String method, String auditTranType, String  usrLanguage){
		return doValidation(mortgageLoanDetail, auditTranType, method, usrLanguage);
	}

	@Override
	public AuditDetail saveOrUpdate(MortgageLoanDetail mortgageLoanDetail, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(mortgageLoanDetail, mortgageLoanDetail.getExcludeFields());

		mortgageLoanDetail.setWorkflowId(0);
		if (mortgageLoanDetail.isNewRecord()) {
			getMortgageLoanDetailDAO().save(mortgageLoanDetail, tableType);
		} else {
			getMortgageLoanDetailDAO().update(mortgageLoanDetail, tableType);
		}

		logger.debug("Leaving");
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], mortgageLoanDetail.getBefImage(), mortgageLoanDetail);

	}


	@Override
	public AuditDetail doApprove(MortgageLoanDetail mortgageLoanDetail, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(mortgageLoanDetail, mortgageLoanDetail.getExcludeFields());

		mortgageLoanDetail.setRoleCode("");
		mortgageLoanDetail.setNextRoleCode("");
		mortgageLoanDetail.setTaskId("");
		mortgageLoanDetail.setNextTaskId("");
		mortgageLoanDetail.setWorkflowId(0);

		getMortgageLoanDetailDAO().save(mortgageLoanDetail, tableType);

		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], mortgageLoanDetail.getBefImage(), mortgageLoanDetail);
	}

	@Override
	public AuditDetail delete(MortgageLoanDetail mortgageLoanDetail, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(mortgageLoanDetail, mortgageLoanDetail.getExcludeFields());	

		getMortgageLoanDetailDAO().delete(mortgageLoanDetail, tableType);

		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], mortgageLoanDetail.getBefImage(), mortgageLoanDetail);
	}


	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditDetail doValidation(MortgageLoanDetail mortgageLoanDetail, String auditTranType, String method, String  usrLanguage){
		logger.debug("Entering");
		String[] fields = PennantJavaUtil.getFieldDetails(mortgageLoanDetail, mortgageLoanDetail.getExcludeFields());
		
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], mortgageLoanDetail.getBefImage(), mortgageLoanDetail);
		
		logger.debug("Leaving");
		return validate(auditDetail, method, usrLanguage);
	}


	private AuditDetail validate(AuditDetail auditDetail,  String method, String usrLanguage){
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		MortgageLoanDetail mortgageLoanDetail= (MortgageLoanDetail) auditDetail.getModelData();

		MortgageLoanDetail tempMortgageLoanDetail= null;
		if (mortgageLoanDetail.isWorkflow()){
			tempMortgageLoanDetail = getMortgageLoanDetailDAO().getMortgageLoanDetailById(
					mortgageLoanDetail.getId(), "_Temp");
		}
		MortgageLoanDetail befMortgageLoanDetail= getMortgageLoanDetailDAO().getMortgageLoanDetailById(
				mortgageLoanDetail.getId(), "");
		MortgageLoanDetail oldMortgageLoanDetail= mortgageLoanDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(mortgageLoanDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

		if (mortgageLoanDetail.isNew()){ // for New record or new record into work flow

			if (!mortgageLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befMortgageLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (mortgageLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befMortgageLoanDetail !=null || tempMortgageLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befMortgageLoanDetail ==null || tempMortgageLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!mortgageLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befMortgageLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldMortgageLoanDetail!=null && !oldMortgageLoanDetail.getLastMntOn().equals(
							befMortgageLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempMortgageLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempMortgageLoanDetail!=null  && oldMortgageLoanDetail!=null && !oldMortgageLoanDetail.getLastMntOn().equals(
						tempMortgageLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !mortgageLoanDetail.isWorkflow()){
			mortgageLoanDetail.setBefImage(befMortgageLoanDetail);	
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}