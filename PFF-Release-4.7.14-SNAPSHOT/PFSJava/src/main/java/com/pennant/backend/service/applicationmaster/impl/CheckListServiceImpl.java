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
 * FileName    		:  CheckListServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CheckListDAO;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CheckListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>CheckList</b>.<br>
 * 
 */
public class CheckListServiceImpl extends GenericService<CheckList> implements CheckListService {
	private static final Logger logger = Logger.getLogger(CheckListServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private CheckListDAO checkListDAO;
	private CheckListDetailDAO checkListDetailDAO;

	public CheckListServiceImpl() {
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

	public CheckListDAO getCheckListDAO() {
		return checkListDAO;
	}
	public void setCheckListDAO(CheckListDAO checkListDAO) {
		this.checkListDAO = checkListDAO;
	}

	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
		this.checkListDetailDAO = checkListDetailDAO;
	}
	public CheckListDetailDAO getCheckListDetailDAO() {
		return checkListDetailDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table BMTCheckList/BMTCheckList_Temp 
	 * 			by using CheckListDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CheckListDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtBMTCheckList by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		CheckList checkList = (CheckList) auditHeader.getAuditDetail().getModelData();

		if (checkList.isWorkflow()) {
			tableType="_Temp";
		}

		if (checkList.isNew()) {
			checkList.setCheckListId(getCheckListDAO().save(checkList,tableType));
			auditHeader.getAuditDetail().setModelData(checkList);
			auditHeader.setAuditReference(String.valueOf(checkList.getCheckListId()));
		}else{
			getCheckListDAO().update(checkList,tableType);
		}

		//Retrieving List of Audit Details For check list detail  related modules
		if(checkList.getChkListList()!=null &&checkList.getChkListList().size()>0){
			List<AuditDetail> details = checkList.getLovDescAuditDetailMap().get("CheckListDetail");
			details = processingChkListDetailList(details,tableType,checkList.getCheckListId());
			auditDetails.addAll(details);
		}
		
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table BMTCheckList by using CheckListDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtBMTCheckList by using auditHeaderDAO.addAudit(auditHeader)    
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

		CheckList checkList = (CheckList) auditHeader.getAuditDetail().getModelData();
		getCheckListDAO().delete(checkList,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(checkList, ""
				,auditHeader.getAuditTranType())));
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCheckListById fetch the details by using CheckListDAO's getCheckListById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CheckList
	 */
	@Override
	public CheckList getCheckListById(long id) {
		CheckList checkList =  getCheckListDAO().getCheckListById(id,"_View");
		checkList.setChkListList(getCheckListDetailDAO().getCheckListDetailByChkList(id, "_View"));
		return checkList;
	}

	/**
	 * getApprovedCheckListById fetch the details by using CheckListDAO's getCheckListById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTCheckList.
	 * @param id (int)
	 * @return CheckList
	 */
	public CheckList getApprovedCheckListById(long id) {
		return getCheckListDAO().getCheckListById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCheckListDAO().delete with parameters checkList,""
	 * 		b)  NEW		Add new record in to main table by using getCheckListDAO().save with parameters checkList,""
	 * 		c)  EDIT	Update record in the main table by using getCheckListDAO().update with parameters checkList,""
	 * 3)	Delete the record from the workFlow table by using getCheckListDAO().delete with parameters checkList,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtBMTCheckList by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtBMTCheckList by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		CheckList checkList = new CheckList();
		BeanUtils.copyProperties((CheckList) auditHeader.getAuditDetail().getModelData(), checkList);

		if (checkList.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCheckListDAO().delete(checkList,"");
			auditDetails.addAll(listDeletion(checkList, "",auditHeader.getAuditTranType()));

		} else {
			checkList.setRoleCode("");
			checkList.setNextRoleCode("");
			checkList.setTaskId("");
			checkList.setNextTaskId("");
			checkList.setWorkflowId(0);

			if (checkList.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				checkList.setRecordType("");
				getCheckListDAO().save(checkList,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				checkList.setRecordType("");
				getCheckListDAO().update(checkList,"");
			}
		}

		getCheckListDAO().delete(checkList,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
	
		//Retrieving List of Audit Details For checkList details modules
		if(checkList.getChkListList()!=null && checkList.getChkListList().size()>0){
			List<AuditDetail> details = checkList.getLovDescAuditDetailMap().get("CheckListDetail");
			details = processingChkListDetailList(details,"",checkList.getCheckListId());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(checkList
				, "_Temp",auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(checkList);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCheckListDAO().delete with parameters checkList,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtBMTCheckList by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		CheckList checkList = (CheckList) auditHeader.getAuditDetail().getModelData();
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCheckListDAO().delete(checkList,"_Temp");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(checkList
				, "_Temp",auditHeader.getAuditTranType())));
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
	 * 5)	for any mismatch conditions Fetch the error details from getCheckListDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCheckListDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		CheckList checkList= (CheckList) auditDetail.getModelData();

		CheckList tempCheckList= null;
		if (checkList.isWorkflow()){
			tempCheckList = getCheckListDAO().getCheckListById(checkList.getId(), "_Temp");
		}
		CheckList befCheckList= getCheckListDAO().getCheckListById(checkList.getId(), "");

		CheckList oldCheckList= checkList.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(checkList.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_CheckListId")+":"+valueParm[0];

		if (checkList.isNew()){ // for New record or new record into work flow

			if (!checkList.isWorkflow()){// With out Work flow only new records  
				if (befCheckList !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (checkList.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCheckList !=null || tempCheckList!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCheckList ==null || tempCheckList!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!checkList.isWorkflow()){	// With out Work flow for update and delete

				if (befCheckList ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCheckList!=null && !oldCheckList.getLastMntOn().equals(befCheckList.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCheckList==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempCheckList!=null && oldCheckList!=null && !oldCheckList.getLastMntOn().equals(tempCheckList.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !checkList.isWorkflow()){
			checkList.setBefImage(befCheckList);	
		}

		return auditDetail;
	}
	
	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method ){
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		CheckList checkList = (CheckList) auditHeader.getAuditDetail().getModelData();

		String auditTranType="";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (checkList.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(checkList.getChkListList()!=null && checkList.getChkListList().size()>0){
			auditDetailMap.put("CheckListDetail", setChkListDetailAuditData(checkList,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("CheckListDetail"));
		}

		checkList.setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(checkList);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param educationalLoan
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setChkListDetailAuditData(CheckList checkList,String auditTranType,String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CheckListDetail(),new CheckListDetail().getExcludeFields());

		for (int i = 0; i < checkList.getChkListList().size(); i++) {
			CheckListDetail checkListDetail  = checkList.getChkListList().get(i);
			
			// Skipping the process of current iteration when the child was not modified to avoid unnecessary processing
			if (StringUtils.isEmpty(checkListDetail.getRecordType())) {
				continue;
			}

			checkListDetail.setWorkflowId(checkList.getWorkflowId());
			checkListDetail.setCheckListId(checkList.getCheckListId());

			boolean isRcdType= false;

			if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (checkList.isWorkflow()) {
					isRcdType=true;
                }
			}else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				checkListDetail.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			checkListDetail.setRecordStatus(checkList.getRecordStatus());
			checkListDetail.setUserDetails(checkList.getUserDetails());
			checkListDetail.setLastMntOn(checkList.getLastMntOn());
			checkListDetail.setLastMntBy(checkList.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], checkListDetail.getBefImage(), checkListDetail));
		}
		
		logger.debug("Leaving ");
		return auditDetails;
	}
	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingChkListDetailList(List<AuditDetail> auditDetails, String type,long checkListId) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CheckListDetail checkListDetail = (CheckListDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			checkListDetail.setCheckListId(checkListId);
			if (StringUtils.isEmpty(type)) {
				approveRec=true;
				checkListDetail.setVersion(checkListDetail.getVersion()+1);
				checkListDetail.setRoleCode("");
				checkListDetail.setNextRoleCode("");
				checkListDetail.setTaskId("");
				checkListDetail.setNextTaskId("");
			}

			checkListDetail.setWorkflowId(0);

			if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(checkListDetail.isNewRecord()){
				saveRecord=true;
				if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(checkListDetail.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			if(approveRec){
				rcdType= checkListDetail.getRecordType();
				recordStatus = checkListDetail.getRecordStatus();
				checkListDetail.setRecordType("");
				checkListDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getCheckListDetailDAO().save(checkListDetail, type);
			}

			if (updateRecord) {
				getCheckListDetailDAO().update(checkListDetail, type);
			}

			if (deleteRecord) {
				getCheckListDetailDAO().delete(checkListDetail, type);
			}

			if(approveRec){
				checkListDetail.setRecordType(rcdType);
				checkListDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(checkListDetail);
		}
		logger.debug("Leaving ");
		return auditDetails;	
	}
	
	/**
	 * Method deletion of CheckListDetail list with existing fee type
	 * @param fee
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(CheckList checkList, String tableType, String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if(checkList.getChkListList()!=null && checkList.getChkListList().size()>0){
			String[] fields = PennantJavaUtil.getFieldDetails(new CheckListDetail());
			for (int i = 0; i <checkList.getChkListList().size(); i++) {
				CheckListDetail checkListDetail = checkList.getChkListList().get(i);
				if(!StringUtils.isEmpty(checkListDetail.getRecordType()) || StringUtils.isEmpty(tableType)){
					auditList.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], checkListDetail.getBefImage(), checkListDetail));
				}
			}
			CheckListDetail checkListDetail = checkList.getChkListList().get(0);
			getCheckListDetailDAO().delete(checkListDetail.getCheckListId(),tableType);
		}
		
		logger.debug("Leaving ");
		return auditList;
	}
	
	/** 
	 * Common Method for CheckList list validation
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list){
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList =new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType="";
				String rcdType = "";
				CheckListDetail checkListDetail = (CheckListDetail) ((AuditDetail)list.get(i)).getModelData();			
				rcdType = checkListDetail.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType= PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType) || 
						PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType= PennantConstants.TRAN_DEL;
				}else{
					transType= PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isNotEmpty(transType)){
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail)list.get(i)).getAuditSeq(),
							checkListDetail.getBefImage(), checkListDetail));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}
}