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
 * FileName    		:  ExtendedFieldDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.solutionfactory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ExtendedFieldDetail</b>.<br>
 * 
 */
public class ExtendedFieldDetailServiceImpl extends GenericService<ExtendedFieldDetail> implements ExtendedFieldDetailService {
	private final static Logger logger = Logger.getLogger(ExtendedFieldDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}
	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}
	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}
	
	@Override
	public ExtendedFieldDetail getExtendedFieldDetail() {
		return getExtendedFieldDetailDAO().getExtendedFieldDetail();
	}
	
	@Override
	public ExtendedFieldDetail getNewExtendedFieldDetail() {
		return getExtendedFieldDetailDAO().getNewExtendedFieldDetail();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table ExtendedFieldDetail/ExtendedFieldDetail_Temp 
	 * 			by using ExtendedFieldDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ExtendedFieldDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtExtendedFieldDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		if (extendedFieldHeader.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (extendedFieldHeader.isNew()) {
			getExtendedFieldHeaderDAO().save(extendedFieldHeader, tableType);
		} else {
			getExtendedFieldHeaderDAO().update(extendedFieldHeader,tableType);
		}

		if(extendedFieldHeader.getExtendedFieldDetails()!=null && extendedFieldHeader.getExtendedFieldDetails().size()>0){
			List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldHeader");
			details = processingExtendeFieldList(details,tableType);
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditDetail(null);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method ){
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType="";

		if(method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject") ){
			if (extendedFieldHeader.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(extendedFieldHeader.getExtendedFieldDetails()!=null && extendedFieldHeader.getExtendedFieldDetails().size()>0){
			auditDetailMap.put("ExtendedFieldHeader", setExtendedFieldsAuditData(extendedFieldHeader,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldHeader"));
		}

		extendedFieldHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(extendedFieldHeader);
		auditHeader.setAuditDetails(auditDetails);
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldHeader extendedFldHeader,String auditTranType,String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());

		for (int i = 0; i < extendedFldHeader.getExtendedFieldDetails().size(); i++) {

			ExtendedFieldDetail extendedFieldDetail = extendedFldHeader.getExtendedFieldDetails().get(i);
			
			if (extendedFieldDetail.getFieldName().equals("null")) {
				extendedFieldDetail.setFieldName(extendedFieldDetail.getFieldName());
			}

			boolean isRcdType= false;

			if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if(method.equals("saveOrUpdate") && (isRcdType==true)){
				extendedFieldDetail.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			extendedFieldDetail.setRecordStatus(extendedFldHeader.getRecordStatus());
			extendedFieldDetail.setLastMntOn(extendedFldHeader.getLastMntOn());

			if(!StringUtils.trimToEmpty(extendedFieldDetail.getRecordType()).equals("")){
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], extendedFieldDetail.getBefImage(), extendedFieldDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingExtendeFieldList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			if (type.equals("")) {
				approveRec=true;
				extendedFieldDetail.setRoleCode("");
				extendedFieldDetail.setNextRoleCode("");
				extendedFieldDetail.setTaskId("");
				extendedFieldDetail.setNextTaskId("");
			}

			extendedFieldDetail.setWorkflowId(0);

			if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(extendedFieldDetail.isNewRecord()){
				saveRecord=true;
				if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(extendedFieldDetail.isNew()){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}
			if(approveRec){
				rcdType= extendedFieldDetail.getRecordType();
				recordStatus = extendedFieldDetail.getRecordStatus();
				extendedFieldDetail.setRecordType("");
				extendedFieldDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				extendedFieldDetailDAO.save(extendedFieldDetail, type);
			}

			if (updateRecord) {
				extendedFieldDetailDAO.update(extendedFieldDetail, type);
			}

			if (deleteRecord) {
				extendedFieldDetailDAO.delete(extendedFieldDetail, type);
			}

			if(approveRec){
				extendedFieldDetail.setRecordType(rcdType);
				extendedFieldDetail.setRecordStatus(recordStatus);
				if(!deleteRecord){
					extendedFieldDetailDAO.alter(extendedFieldDetail,"_Temp",false,true);
					extendedFieldDetailDAO.alter(extendedFieldDetail,"",false,true);
				}else{
					extendedFieldDetailDAO.alter(extendedFieldDetail,"_Temp",true,false);
					extendedFieldDetailDAO.alter(extendedFieldDetail,"",true,false);
				}
			}
			auditDetails.get(i).setModelData(extendedFieldDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table ExtendedFieldDetail by using ExtendedFieldDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtExtendedFieldDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) auditHeader.getAuditDetail().getModelData();
		getExtendedFieldDetailDAO().delete(extendedFieldDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getExtendedFieldDetailById fetch the details by using
	 * ExtendedFieldDetailDAO's getExtendedFieldDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ExtendedFieldDetail
	 */
	@Override
	public ExtendedFieldHeader getExtendedFieldHeaderById(ExtendedFieldHeader extendedFieldHeader) {
		extendedFieldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(extendedFieldHeader.getId(),"_View"));
		return extendedFieldHeader;
	}
	
	/**
	 * getExtendedFieldDetailById fetch the details by using ExtendedFieldDetailDAO's getExtendedFieldDetailById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ExtendedFieldDetail
	 */
	@Override
	public ExtendedFieldDetail getExtendedFieldDetailById(long id,String fieldName) {
		return getExtendedFieldDetailDAO().getExtendedFieldDetailById(id,fieldName,"_View");
	}	

	/**
	 * getApprovedExtendedFieldDetailById fetch the details by using
	 * ExtendedFieldDetailDAO's getExtendedFieldDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * ExtendedFieldDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return ExtendedFieldDetail
	 */
	public ExtendedFieldDetail getApprovedExtendedFieldDetailById(long id,String fieldName) {
		return getExtendedFieldDetailDAO().getExtendedFieldDetailById(id,fieldName,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param ExtendedFieldDetail (extendedFieldDetail)
	 * @return extendedFieldDetail
	 */
	@Override
	public ExtendedFieldDetail refresh(ExtendedFieldDetail extendedFieldDetail) {
		logger.debug("Entering");
		getExtendedFieldDetailDAO().refresh(extendedFieldDetail);
		getExtendedFieldDetailDAO().initialize(extendedFieldDetail);
		logger.debug("Leaving");
		return extendedFieldDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getExtendedFieldDetailDAO().delete with parameters extendedFieldDetail,""
	 * 		b)  NEW		Add new record in to main table by using getExtendedFieldDetailDAO().save with parameters extendedFieldDetail,""
	 * 		c)  EDIT	Update record in the main table by using getExtendedFieldDetailDAO().update with parameters extendedFieldDetail,""
	 * 3)	Delete the record from the workFlow table by using getExtendedFieldDetailDAO().delete with parameters extendedFieldDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtExtendedFieldDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtExtendedFieldDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		String tranType="";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ExtendedFieldHeader extendedFieldHeader =(ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"");

		} else {
			extendedFieldHeader.setRoleCode("");
			extendedFieldHeader.setNextRoleCode("");
			extendedFieldHeader.setTaskId("");
			extendedFieldHeader.setNextTaskId("");
			extendedFieldHeader.setWorkflowId(0);

			if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().save(extendedFieldHeader,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().update(extendedFieldHeader,"");
			}
			if(extendedFieldHeader.getExtendedFieldDetails()!=null && extendedFieldHeader.getExtendedFieldDetails().size()>0){
				List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldHeader");
				details = processingExtendeFieldList(details,"");
				auditDetails.addAll(details);
			}
		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"_TEMP");
		auditDetailList.addAll(getListAuditDetails(listDeletion(extendedFieldHeader, "_TEMP",auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(extendedFieldHeader);
		auditHeader.setAuditDetails(auditDetails);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	public List<AuditDetail> listDeletion(ExtendedFieldHeader extendedFieldHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if(extendedFieldHeader.getExtendedFieldDetails()!=null && extendedFieldHeader.getExtendedFieldDetails().size()>0){
			String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());
			for (int i = 0; i < extendedFieldHeader.getExtendedFieldDetails().size(); i++) {
				ExtendedFieldDetail extendedFieldDetail = extendedFieldHeader.getExtendedFieldDetails().get(i);
				if (!StringUtils.trimToEmpty(extendedFieldDetail.getRecordType()).equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], extendedFieldDetail.getBefImage(), extendedFieldDetail));
				}
			}
			getExtendedFieldDetailDAO().deleteByExtendedFields(extendedFieldHeader.getId(), tableType);
		}
		logger.debug("Leaving");
		return auditList;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getExtendedFieldDetailDAO().delete with parameters extendedFieldDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtExtendedFieldDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader,"_TEMP");
		auditDetails.addAll(getListAuditDetails(listDeletion(extendedFieldHeader, "_TEMP",auditHeader.getAuditTranType())));
		auditHeader.setAuditDetails(auditDetails);
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
	 * 5)	for any mismatch conditions Fetch the error details from getExtendedFieldDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");

		//AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		//auditHeader.setAuditDetail(auditDetail);
		//auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		// Rating Validations
		if(extendedFieldHeader.getExtendedFieldDetails()!=null &&extendedFieldHeader.getExtendedFieldDetails().size()>0){
			List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldHeader");
			details = fieldListValidation(details, method,auditHeader.getUsrLanguage());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());	
		}

		auditHeader=nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	public List<AuditDetail> fieldListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){

		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		ExtendedFieldDetail extendedFieldDetail= (ExtendedFieldDetail) auditDetail.getModelData();

		ExtendedFieldDetail tempExtendedFieldDetail= null;
		if (extendedFieldDetail.isWorkflow()){
			tempExtendedFieldDetail = getExtendedFieldDetailDAO().getExtendedFieldDetailById(extendedFieldDetail.getId(),extendedFieldDetail.getFieldName(), "_Temp");
			
		}
		ExtendedFieldDetail befExtendedFieldDetail= getExtendedFieldDetailDAO().getExtendedFieldDetailById(extendedFieldDetail.getId(),extendedFieldDetail.getFieldName(), "");
		
		ExtendedFieldDetail old_ExtendedFieldDetail= extendedFieldDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(extendedFieldDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_ModuleId")+":"+valueParm[0];

		if (extendedFieldDetail.isNew()){ // for New record or new record into work flow

			if (!extendedFieldDetail.isWorkflow()){// With out Work flow only new records  
				if (befExtendedFieldDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (extendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befExtendedFieldDetail !=null || tempExtendedFieldDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befExtendedFieldDetail ==null || tempExtendedFieldDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befExtendedFieldDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_ExtendedFieldDetail!=null && !old_ExtendedFieldDetail.getLastMntOn().equals(befExtendedFieldDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempExtendedFieldDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_ExtendedFieldDetail!=null && !old_ExtendedFieldDetail.getLastMntOn().equals(tempExtendedFieldDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !extendedFieldDetail.isWorkflow()){
			auditDetail.setBefImage(befExtendedFieldDetail);	
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	/** 
	 * Common Method for Extended Details list validation
	 * @param list
	 * @param method
	 * @param Details
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException 
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetailsList =new ArrayList<AuditDetail>();

		if(list!=null & list.size()>0){

			for (int i = 0; i < list.size(); i++) {

				String transType="";
				String rcdType = "";
				Object object = ((AuditDetail)list.get(i)).getModelData();			
				try {

					rcdType = object.getClass().getMethod( "getRecordType").invoke( object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType= PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || rcdType.equalsIgnoreCase(PennantConstants.
							RECORD_TYPE_CAN)) {
						transType= PennantConstants.TRAN_DEL;
					}else{
						transType= PennantConstants.TRAN_UPD;
					}

					if(!(transType.equals(""))){
						//check and change below line for Complete code
						Object befImg = object.getClass().getMethod( "getBefImage",object.getClass().getClasses()).invoke( object, object.getClass
								().getClasses());
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail)list.get(i)).getAuditSeq(),befImg, object));
					}
				}catch (Exception e) {
					logger.error(e);
				} 
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

}