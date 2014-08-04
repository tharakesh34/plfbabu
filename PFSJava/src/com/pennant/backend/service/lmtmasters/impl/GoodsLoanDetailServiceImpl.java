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
 * FileName    		:  GoodsLoanDetailServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.lmtmasters.GoodsLoanDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.GoodsLoanDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>GoodsLoanDetail</b>.<br>
 * 
 */
public class GoodsLoanDetailServiceImpl extends GenericService<GoodsLoanDetail> implements GoodsLoanDetailService {
	private final static Logger logger = Logger.getLogger(GoodsLoanDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private GoodsLoanDetailDAO goodsLoanDetailDAO;

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
	public GoodsLoanDetailDAO getGoodsLoanDetailDAO() {
		return goodsLoanDetailDAO;
	}
	/**
	 * @param goodsLoanDetailDAO the goodsLoanDetailDAO to set
	 */
	public void setGoodsLoanDetailDAO(GoodsLoanDetailDAO goodsLoanDetailDAO) {
		this.goodsLoanDetailDAO = goodsLoanDetailDAO;
	}

	/**
	 * @return the goodsLoanDetail
	 */
	@Override
	public GoodsLoanDetail getGoodsLoanDetail() {
		return getGoodsLoanDetailDAO().getGoodsLoanDetail();
	}
	/**
	 * @return the goodsLoanDetail for New Record
	 */
	@Override
	public GoodsLoanDetail getNewGoodsLoanDetail() {
		return getGoodsLoanDetailDAO().getNewGoodsLoanDetail();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LMTGoodsLoanDetail/LMTGoodsLoanDetail_Temp 
	 * 			by using GoodsLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using GoodsLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table LMTGoodsLoanDetail/LMTGoodsLoanDetail_Temp 
	 * 			by using GoodsLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using GoodsLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		GoodsLoanDetail goodsLoanDetail = (GoodsLoanDetail) auditHeader.getAuditDetail().getModelData();

		if (goodsLoanDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (goodsLoanDetail.isNew()) {
			getGoodsLoanDetailDAO().save(goodsLoanDetail,tableType);
		}else{
			getGoodsLoanDetailDAO().update(goodsLoanDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTGoodsLoanDetail by using GoodsLoanDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		GoodsLoanDetail goodsLoanDetail = (GoodsLoanDetail) auditHeader.getAuditDetail().getModelData();
		getGoodsLoanDetailDAO().delete(goodsLoanDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGoodsLoanDetailById fetch the details by using GoodsLoanDetailDAO's getGoodsLoanDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return GoodsLoanDetail
	 */

	@Override
	public GoodsLoanDetail getGoodsLoanDetailById(String id,String itemType) {
		return getGoodsLoanDetailDAO().getGoodsLoanDetailById(id,itemType,"_View");
	}
	/**
	 * getApprovedGoodsLoanDetailById fetch the details by using GoodsLoanDetailDAO's getGoodsLoanDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the LMTGoodsLoanDetail.
	 * @param id (String)
	 * @return GoodsLoanDetail
	 */

	public GoodsLoanDetail getApprovedGoodsLoanDetailById(String id,String itemType) {
		return getGoodsLoanDetailDAO().getGoodsLoanDetailById(id,itemType,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param GoodsLoanDetail (goodsLoanDetail)
	 * @return goodsLoanDetail
	 */
	@Override
	public GoodsLoanDetail refresh(GoodsLoanDetail goodsLoanDetail) {
		logger.debug("Entering");
		getGoodsLoanDetailDAO().refresh(goodsLoanDetail);
		getGoodsLoanDetailDAO().initialize(goodsLoanDetail);
		logger.debug("Leaving");
		return goodsLoanDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getGoodsLoanDetailDAO().delete with parameters goodsLoanDetail,""
	 * 		b)  NEW		Add new record in to main table by using getGoodsLoanDetailDAO().save with parameters goodsLoanDetail,""
	 * 		c)  EDIT	Update record in the main table by using getGoodsLoanDetailDAO().update with parameters goodsLoanDetail,""
	 * 3)	Delete the record from the workFlow table by using getGoodsLoanDetailDAO().delete with parameters goodsLoanDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		GoodsLoanDetail goodsLoanDetail = new GoodsLoanDetail();
		BeanUtils.copyProperties((GoodsLoanDetail) auditHeader.getAuditDetail().getModelData(), goodsLoanDetail);

		if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getGoodsLoanDetailDAO().delete(goodsLoanDetail,"");

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
				getGoodsLoanDetailDAO().save(goodsLoanDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				goodsLoanDetail.setRecordType("");
				getGoodsLoanDetailDAO().update(goodsLoanDetail,"");
			}
		}

		getGoodsLoanDetailDAO().delete(goodsLoanDetail,"_TEMP");
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
	 * 2)	Delete the record from the workFlow table by using getGoodsLoanDetailDAO().delete with parameters goodsLoanDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTGoodsLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		GoodsLoanDetail goodsLoanDetail = (GoodsLoanDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGoodsLoanDetailDAO().delete(goodsLoanDetail,"_TEMP");

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
	public List<AuditDetail> validate(List<GoodsLoanDetail> goodsLoanDetailList, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(goodsLoanDetailList, workflowId, method, auditTranType, usrLanguage);
	}


	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getGoodsLoanDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		GoodsLoanDetail goodsLoanDetail= (GoodsLoanDetail) auditDetail.getModelData();

		GoodsLoanDetail tempGoodsLoanDetail= null;
		if (goodsLoanDetail.isWorkflow()){
			tempGoodsLoanDetail = getGoodsLoanDetailDAO().getGoodsLoanDetailById(goodsLoanDetail.getId(),goodsLoanDetail.getItemNumber(), "_Temp");
		}
		GoodsLoanDetail befGoodsLoanDetail= getGoodsLoanDetailDAO().getGoodsLoanDetailById(goodsLoanDetail.getId(),goodsLoanDetail.getItemNumber(), "");

		GoodsLoanDetail old_GoodsLoanDetail= goodsLoanDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=goodsLoanDetail.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

		if (goodsLoanDetail.isNew()){ // for New record or new record into work flow

			if (!goodsLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befGoodsLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befGoodsLoanDetail !=null || tempGoodsLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befGoodsLoanDetail ==null || tempGoodsLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!goodsLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befGoodsLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_GoodsLoanDetail!=null && !old_GoodsLoanDetail.getLastMntOn().equals(befGoodsLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempGoodsLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_GoodsLoanDetail!=null && !old_GoodsLoanDetail.getLastMntOn().equals(tempGoodsLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !goodsLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befGoodsLoanDetail);	
		}

		return auditDetail;
	}

	@Override
	public GoodsLoanDetail getApprovedGoodsLoanDetailById(String id) {
		GoodsLoanDetail detail=new GoodsLoanDetail();
		detail.setLoanRefNumber(id);
		detail.setGoodsLoanDetailList(getGoodsLoanDetailDAO().getGoodsLoanDetailByFinRef(id, "_AView"));
		return detail;
	}

	@Override
	public GoodsLoanDetail getGoodsLoanDetailById(String id) {
		GoodsLoanDetail detail=new GoodsLoanDetail();
		detail.setLoanRefNumber(id);
		detail.setGoodsLoanDetailList(getGoodsLoanDetailDAO().getGoodsLoanDetailByFinRef(id, "_View"));
		return detail;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<GoodsLoanDetail> goodsLoanDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GoodsLoanDetail goodsLoanDetail : goodsLoanDetailList) {
			goodsLoanDetail.setWorkflowId(0);

			if (StringUtils.trimToEmpty(goodsLoanDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				getGoodsLoanDetailDAO().delete(goodsLoanDetail, tableType);
			}else if(goodsLoanDetail.isNewRecord()) {
				getGoodsLoanDetailDAO().save(goodsLoanDetail, tableType);
			} else {
				getGoodsLoanDetailDAO().update(goodsLoanDetail, tableType);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(goodsLoanDetail, goodsLoanDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], goodsLoanDetail.getBefImage(), goodsLoanDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<GoodsLoanDetail> goodsLoanDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GoodsLoanDetail goodsLoanDetail : goodsLoanDetailList) {
			GoodsLoanDetail detail = new GoodsLoanDetail();
			BeanUtils.copyProperties(goodsLoanDetail, detail);

			goodsLoanDetail.setRoleCode("");
			goodsLoanDetail.setNextRoleCode("");
			goodsLoanDetail.setTaskId("");
			goodsLoanDetail.setNextTaskId("");
			goodsLoanDetail.setWorkflowId(0);

			getGoodsLoanDetailDAO().save(goodsLoanDetail, tableType);

			String[] fields = PennantJavaUtil.getFieldDetails(goodsLoanDetail, goodsLoanDetail.getExcludeFields());

			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], goodsLoanDetail.getBefImage(), goodsLoanDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<GoodsLoanDetail> goodsLoanDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GoodsLoanDetail goodsLoanDetail : goodsLoanDetailList) {
			getGoodsLoanDetailDAO().delete(goodsLoanDetail, tableType);

			String[] fields = PennantJavaUtil.getFieldDetails(goodsLoanDetail, goodsLoanDetail.getExcludeFields());
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], goodsLoanDetail.getBefImage(), goodsLoanDetail));
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

	public List<AuditDetail> doValidation(List<GoodsLoanDetail> goodsLoanDetailList, long workflowId, String method, String auditTranType, String usrLanguage){
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(goodsLoanDetailList, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage); 
		}

		logger.debug("Leaving");
		return auditDetails ;
	}

	private AuditDetail validate(AuditDetail auditDetail, String  usrLanguage, String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		GoodsLoanDetail goodsLoanDetail= (GoodsLoanDetail) auditDetail.getModelData();
		GoodsLoanDetail tempGoodsLoanDetail= null;
		if (goodsLoanDetail.isWorkflow()){
			tempGoodsLoanDetail = getGoodsLoanDetailDAO().getGoodsLoanDetailById(goodsLoanDetail.getLoanRefNumber(),goodsLoanDetail.getItemNumber(), "_Temp");
		}
		GoodsLoanDetail befGoodsLoanDetail= getGoodsLoanDetailDAO().getGoodsLoanDetailById(goodsLoanDetail.getLoanRefNumber(),goodsLoanDetail.getItemNumber(), "");

		GoodsLoanDetail old_GoodsLoanDetail= goodsLoanDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(goodsLoanDetail.getLoanRefNumber());
		errParm[0]=PennantJavaUtil.getLabel("label_loanReferenceNumber")+":"+valueParm[0];

		if (goodsLoanDetail.isNew()){ // for New record or new record into work flow

			if (!goodsLoanDetail.isWorkflow()){// With out Work flow only new records  
				if (befGoodsLoanDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (goodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befGoodsLoanDetail !=null || tempGoodsLoanDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befGoodsLoanDetail ==null || tempGoodsLoanDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!goodsLoanDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befGoodsLoanDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_GoodsLoanDetail!=null && !old_GoodsLoanDetail.getLastMntOn().equals(befGoodsLoanDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempGoodsLoanDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempGoodsLoanDetail!=null && old_GoodsLoanDetail!=null && !old_GoodsLoanDetail.getLastMntOn().equals(tempGoodsLoanDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !goodsLoanDetail.isWorkflow()){
			auditDetail.setBefImage(befGoodsLoanDetail);	
		}
		return auditDetail;
	}
	
	
	private List<AuditDetail> getAuditDetail(List<GoodsLoanDetail> goodsLoanDetailList, String auditTranType, String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		GoodsLoanDetail object = new GoodsLoanDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < goodsLoanDetailList.size(); i++) {

			GoodsLoanDetail goodsLoanDetail = goodsLoanDetailList.get(i);
			goodsLoanDetail.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (goodsLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				goodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (goodsLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				goodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (goodsLoanDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				goodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				goodsLoanDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (goodsLoanDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (goodsLoanDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| goodsLoanDetail.getRecordType().equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}


			if (!goodsLoanDetail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], goodsLoanDetail.getBefImage(), goodsLoanDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

}