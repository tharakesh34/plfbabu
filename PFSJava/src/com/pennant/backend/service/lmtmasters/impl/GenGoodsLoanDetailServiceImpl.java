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
 * FileName    		:  GenGoodsLoanDetailServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.lmtmasters.GenGoodsLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.GenGoodsLoanDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>GenGoodsLoanDetail</b>.<br>
 * 
 */
public class GenGoodsLoanDetailServiceImpl extends GenericService<GenGoodsLoanDetail> implements GenGoodsLoanDetailService {
	private final static Logger logger = Logger.getLogger(GenGoodsLoanDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private GenGoodsLoanDetailDAO goodsLoanDetailDAO;

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
	 * @return the goodsLoanDetailDAO
	 */
	public GenGoodsLoanDetailDAO getGenGoodsLoanDetailDAO() {
		return goodsLoanDetailDAO;
	}
	/**
	 * @param goodsLoanDetailDAO the goodsLoanDetailDAO to set
	 */
	public void setGenGoodsLoanDetailDAO(GenGoodsLoanDetailDAO goodsLoanDetailDAO) {
		this.goodsLoanDetailDAO = goodsLoanDetailDAO;
	}

	/**
	 * @return the goodsLoanDetail
	 */
	@Override
	public GenGoodsLoanDetail getGenGoodsLoanDetail() {
		return getGenGoodsLoanDetailDAO().getGenGoodsLoanDetail();
	}
	/**
	 * @return the goodsLoanDetail for New Record
	 */
	@Override
	public GenGoodsLoanDetail getNewGenGoodsLoanDetail() {
		return getGenGoodsLoanDetailDAO().getNewGenGoodsLoanDetail();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LMTGenGoodsLoanDetail/LMTGenGoodsLoanDetail_Temp 
	 * 			by using GenGoodsLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using GenGoodsLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTGenGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table LMTGenGoodsLoanDetail/LMTGenGoodsLoanDetail_Temp 
	 * 			by using GenGoodsLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using GenGoodsLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTGenGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		GenGoodsLoanDetail goodsLoanDetail = (GenGoodsLoanDetail) auditHeader.getAuditDetail().getModelData();

		if (goodsLoanDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (goodsLoanDetail.isNew()) {
			getGenGoodsLoanDetailDAO().save(goodsLoanDetail,tableType);
		}else{
			getGenGoodsLoanDetailDAO().update(goodsLoanDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTGenGoodsLoanDetail by using GenGoodsLoanDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTGenGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		GenGoodsLoanDetail goodsLoanDetail = (GenGoodsLoanDetail) auditHeader.getAuditDetail().getModelData();
		getGenGoodsLoanDetailDAO().delete(goodsLoanDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGenGoodsLoanDetailById fetch the details by using GenGoodsLoanDetailDAO's getGenGoodsLoanDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return GenGoodsLoanDetail
	 */

	@Override
	public GenGoodsLoanDetail getGenGoodsLoanDetailById(String id,String itemType) {
		return getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailById(id,itemType,"_View");
	}
	/**
	 * getApprovedGenGoodsLoanDetailById fetch the details by using GenGoodsLoanDetailDAO's getGenGoodsLoanDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the LMTGenGoodsLoanDetail.
	 * @param id (String)
	 * @return GenGoodsLoanDetail
	 */

	public GenGoodsLoanDetail getApprovedGenGoodsLoanDetailById(String id,String itemType) {
		return getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailById(id,itemType,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param GenGoodsLoanDetail (goodsLoanDetail)
	 * @return goodsLoanDetail
	 */
	@Override
	public GenGoodsLoanDetail refresh(GenGoodsLoanDetail goodsLoanDetail) {
		logger.debug("Entering");
		getGenGoodsLoanDetailDAO().refresh(goodsLoanDetail);
		getGenGoodsLoanDetailDAO().initialize(goodsLoanDetail);
		logger.debug("Leaving");
		return goodsLoanDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getGenGoodsLoanDetailDAO().delete with parameters goodsLoanDetail,""
	 * 		b)  NEW		Add new record in to main table by using getGenGoodsLoanDetailDAO().save with parameters goodsLoanDetail,""
	 * 		c)  EDIT	Update record in the main table by using getGenGoodsLoanDetailDAO().update with parameters goodsLoanDetail,""
	 * 3)	Delete the record from the workFlow table by using getGenGoodsLoanDetailDAO().delete with parameters goodsLoanDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTGenGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTGenGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		GenGoodsLoanDetail goodsLoanDetail = new GenGoodsLoanDetail();
		BeanUtils.copyProperties((GenGoodsLoanDetail) auditHeader.getAuditDetail().getModelData(), goodsLoanDetail);

		if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getGenGoodsLoanDetailDAO().delete(goodsLoanDetail,"");

		} else {
			goodsLoanDetail.setRoleCode("");
			goodsLoanDetail.setNextRoleCode("");
			goodsLoanDetail.setTaskId("");
			goodsLoanDetail.setNextTaskId("");
			goodsLoanDetail.setWorkflowId(0);

			if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				goodsLoanDetail.setRecordType("");
				getGenGoodsLoanDetailDAO().save(goodsLoanDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				goodsLoanDetail.setRecordType("");
				getGenGoodsLoanDetailDAO().update(goodsLoanDetail,"");
			}
		}

		getGenGoodsLoanDetailDAO().delete(goodsLoanDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(goodsLoanDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getGenGoodsLoanDetailDAO().delete with parameters goodsLoanDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTGenGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		GenGoodsLoanDetail goodsLoanDetail = (GenGoodsLoanDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGenGoodsLoanDetailDAO().delete(goodsLoanDetail,"_TEMP");

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


	@Override
	public List<AuditDetail> validate(List<GenGoodsLoanDetail> genGoodsLoanDetailList, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(genGoodsLoanDetailList, workflowId, method, auditTranType, usrLanguage);
	}

	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getGenGoodsLoanDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		GenGoodsLoanDetail goodsLoanDetail= (GenGoodsLoanDetail) auditDetail.getModelData();

		GenGoodsLoanDetail tempGenGoodsLoanDetail= null;
		if (goodsLoanDetail.isWorkflow()){
			tempGenGoodsLoanDetail = getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailById(goodsLoanDetail.getId(),goodsLoanDetail.getItemNumber(), "_Temp");
		}
		GenGoodsLoanDetail befGenGoodsLoanDetail= getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailById(goodsLoanDetail.getId(),goodsLoanDetail.getItemNumber(), "");

		GenGoodsLoanDetail old_GenGoodsLoanDetail= goodsLoanDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=goodsLoanDetail.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

		if (goodsLoanDetail.isNew()){ // for New record or new record into work flow

			if (!goodsLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befGenGoodsLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befGenGoodsLoanDetail !=null || tempGenGoodsLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befGenGoodsLoanDetail ==null || tempGenGoodsLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!goodsLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befGenGoodsLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_GenGoodsLoanDetail!=null && !old_GenGoodsLoanDetail.getLastMntOn().equals(befGenGoodsLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempGenGoodsLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_GenGoodsLoanDetail!=null && !old_GenGoodsLoanDetail.getLastMntOn().equals(tempGenGoodsLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !goodsLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befGenGoodsLoanDetail);	
		}

		return auditDetail;
	}

	@Override
	public GenGoodsLoanDetail getApprovedGenGoodsLoanDetailById(String id) {
		GenGoodsLoanDetail detail=new GenGoodsLoanDetail();
		detail.setLoanRefNumber(id);
		detail.setGenGoodsLoanDetailList(getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailByFinRef(id, "_AView"));
		return detail;
	}

	@Override
	public GenGoodsLoanDetail getGenGoodsLoanDetailById(String id) {
		GenGoodsLoanDetail detail=new GenGoodsLoanDetail();
		detail.setLoanRefNumber(id);
		detail.setGenGoodsLoanDetailList(getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailByFinRef(id, "_View"));
		return detail;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GenGoodsLoanDetail genGoodsLoanDetail : genGoodsLoanDetailList) {
			genGoodsLoanDetail.setWorkflowId(0);

			if (StringUtils.trimToEmpty(genGoodsLoanDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				getGenGoodsLoanDetailDAO().delete(genGoodsLoanDetail, tableType);
			}else if (genGoodsLoanDetail.isNewRecord()) {
				getGenGoodsLoanDetailDAO().save(genGoodsLoanDetail, tableType);
			} else {
				getGenGoodsLoanDetailDAO().update(genGoodsLoanDetail, tableType);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(genGoodsLoanDetail, genGoodsLoanDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], genGoodsLoanDetail.getBefImage(), genGoodsLoanDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GenGoodsLoanDetail genGoodsLoanDetail : genGoodsLoanDetailList) {
			GenGoodsLoanDetail detail = new GenGoodsLoanDetail();
			BeanUtils.copyProperties(genGoodsLoanDetail, detail);

			genGoodsLoanDetail.setRoleCode("");
			genGoodsLoanDetail.setNextRoleCode("");
			genGoodsLoanDetail.setTaskId("");
			genGoodsLoanDetail.setNextTaskId("");
			genGoodsLoanDetail.setWorkflowId(0);

			getGenGoodsLoanDetailDAO().save(genGoodsLoanDetail, tableType);

			String[] fields = PennantJavaUtil.getFieldDetails(genGoodsLoanDetail, genGoodsLoanDetail.getExcludeFields());

			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], genGoodsLoanDetail.getBefImage(), genGoodsLoanDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GenGoodsLoanDetail genGoodsLoanDetail : genGoodsLoanDetailList) {
			getGenGoodsLoanDetailDAO().delete(genGoodsLoanDetail, tableType);

			String[] fields = PennantJavaUtil.getFieldDetails(genGoodsLoanDetail, genGoodsLoanDetail.getExcludeFields());
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], genGoodsLoanDetail.getBefImage(), genGoodsLoanDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		logger.debug("Leaving");
		return auditHeader;
	}

	public List<AuditDetail> doValidation(List<GenGoodsLoanDetail> genGoodsLoanDetailList, long workflowId, String method, String auditTranType, String usrLanguage){
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(genGoodsLoanDetailList, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage); 
		}

		logger.debug("Leaving");
		return auditDetails ;
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		GenGoodsLoanDetail goodsLoanDetail= (GenGoodsLoanDetail) auditDetail.getModelData();
		GenGoodsLoanDetail tempGenGoodsLoanDetail= null;
		GenGoodsLoanDetail befGenGoodsLoanDetail = null;
		GenGoodsLoanDetail old_GenGoodsLoanDetail = null;

		if (goodsLoanDetail.isWorkflow()){
			tempGenGoodsLoanDetail = getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailById(goodsLoanDetail.getLoanRefNumber(),goodsLoanDetail.getItemNumber(), "_Temp");
		}

		befGenGoodsLoanDetail = getGenGoodsLoanDetailDAO().getGenGoodsLoanDetailById(goodsLoanDetail.getLoanRefNumber(),goodsLoanDetail.getItemNumber(), "");
		old_GenGoodsLoanDetail = goodsLoanDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(goodsLoanDetail.getLoanRefNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_loanReferenceNumber")+":"+valueParm[0];

		if (goodsLoanDetail.isNew()){ // for New record or new record into work flow

			if (!goodsLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befGenGoodsLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befGenGoodsLoanDetail !=null || tempGenGoodsLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befGenGoodsLoanDetail ==null || tempGenGoodsLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!goodsLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befGenGoodsLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_GenGoodsLoanDetail!=null && !old_GenGoodsLoanDetail.getLastMntOn().equals(befGenGoodsLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempGenGoodsLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempGenGoodsLoanDetail!=null && old_GenGoodsLoanDetail!=null && !old_GenGoodsLoanDetail.getLastMntOn().equals(tempGenGoodsLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !goodsLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befGenGoodsLoanDetail);	
		}
		return auditDetail;
	}
	
	
	private List<AuditDetail> getAuditDetail(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String auditTranType, String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		GenGoodsLoanDetail object = new GenGoodsLoanDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (GenGoodsLoanDetail genGoodsLoanDetail : genGoodsLoanDetailList) {
			genGoodsLoanDetail.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (genGoodsLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				genGoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (genGoodsLoanDetail.getRecordType()
					.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				genGoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (genGoodsLoanDetail.getRecordType()
					.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				genGoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				genGoodsLoanDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (genGoodsLoanDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (genGoodsLoanDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| genGoodsLoanDetail.getRecordType().equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}


			if (!genGoodsLoanDetail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], genGoodsLoanDetail.getBefImage(), genGoodsLoanDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

}