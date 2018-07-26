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
 *
 * FileName    		: SecurityOperationRolesServiceImpl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  11-03-2014															*
 *                                                                  
 * Modified Date    :  11-03-2014															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-03-2014       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.administration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.dao.administration.SecurityOperationRolesDAO;
import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityOperationRolesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class SecurityOperationRolesServiceImpl extends GenericService<SecurityOperationRoles> implements
		SecurityOperationRolesService {
	private static Logger logger = Logger.getLogger(SecurityOperationRolesServiceImpl.class);

	private SecurityOperationRolesDAO securityOperationRolesDAO;
	private SecurityRoleGroupsDAO securityRoleGroupsDAO;
	private SecurityRoleDAO securityRoleDAO;
	private SecurityOperationDAO securityOperationDAO;
	private SecurityGroupRightsDAO securityGroupRightsDAO;
	private AuditHeaderDAO auditHeaderDAO;

	public SecurityOperationRoles getSecurityOperationRoles(){
		return getSecurityOperationRolesDAO().getSecurityOperationRoles();
	}

	
	
	/**
	 *This method do the following
	 * 1)Gets the AuditDetails list by calling businessValidation() method 
	 * 2)a)it checks for each AuditDetail  if "AuditTranType" is "A" it saves the record by calling SecurityUsersOperationsDAO's
	 *   save() method
	 *   b)if "AuditTranType" is "D" it Deletes the record by calling SecurityUsersOperationsDAO's
	 *   delete() method
	 *
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType="";
		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();

		if (securityOperation.isWorkflow()) {
			tableType="_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		} else {
			securityOperation.setRecordType("");
			securityOperation.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			auditHeader.getAuditDetail().setModelData(securityOperation);		
		}

		if(securityOperation.isWorkflow()) {
			if (securityOperation.isNewRecord()) {
					getSecurityOperationDAO().save(securityOperation,"_RTEMP");
					auditHeader.getAuditDetail().setModelData(securityOperation);
					auditHeader.setAuditReference(String.valueOf(securityOperation.getOprID()));
			} else {
					getSecurityOperationDAO().update(securityOperation,"_RTEMP");
			}
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// PROCESS DETAILS
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
		auditDetails.addAll(processingDetailList(auditHeader.getAuditDetails(), tableType, securityOperation));
		}
        auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);		
		logger.debug("Leaving ");
		return auditHeader;
		
		}
	/**
	 * This method do the following 
	 * 1)get the AuditDetails list by calling getAuditDetailsList().
	 * 2)It validate each AuditDetail in the AuditDetails list by calling validate method
	 * @param auditHeader
	 * @return auditHeader
	 */
	public AuditHeader businessValidation(AuditHeader auditHeader,String method,boolean online){
		logger.debug("Entering");		
		
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails =getAuditUserRoles(securityOperation, auditHeader.getAuditTranType(), method, online);   
		
		for (AuditDetail detail:auditDetails) {
			auditHeader.addAuditDetail(detail);
			auditHeader.setErrorList(detail.getErrorDetails());
		}
		
		auditHeader=nextProcess(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getSecurityOperationRoleDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */	
     private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,String method) {
 		
 		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
 		SecurityOperation secOperation=(SecurityOperation)auditDetail.getModelData();
 		
 		SecurityOperation tempSecurityUser= null;
 		if (secOperation.isWorkflow()) {
 			tempSecurityUser = getSecurityOperationDAO().getSecurityOperationById(secOperation.getId(), "_RTemp");
 		}
 		
 		SecurityOperation befSecurityOperation= getSecurityOperationDAO().getSecurityOperationById(secOperation.getId(), "");
		SecurityOperation oldSecurityOperation = secOperation.getBefImage();


 		String[] errParm= new String[1];
 		String[] valueParm= new String[1];
 		valueParm[0]=String.valueOf(secOperation.getId());
 		errParm[0]=PennantJavaUtil.getLabel("label_UsrID")+":"+valueParm[0];

 		if (secOperation.isNewRecord()){    			// for New record or new record into work flow

 			if (!secOperation.isWorkflow()) { 		// With out Work flow only new records  
 				if (befSecurityOperation !=null) {	// Record Already Exists in the table then error  
 					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
 				}	
 			} else { 							// with work flow
 				if (secOperation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
 					if (befSecurityOperation !=null || tempSecurityUser!=null ){ // if records already exists in the main table
 						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
 					}
 				} else { 						// if records not exists in the Main flow table
 					if (befSecurityOperation ==null || tempSecurityUser!=null ) {
 						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
 					}
 				}
 			}
 		} else {
 			// for work flow process records or (Record to update or Delete with out work flow)
 			if (!secOperation.isWorkflow()) {		// With out Work flow for update and delete

 				if (befSecurityOperation ==null){ 	// if records not exists in the main table
 					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
 				}else{
 					if (oldSecurityOperation!=null && !oldSecurityOperation.getLastMntOn().equals(befSecurityOperation.getLastMntOn())){
 						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
 							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
 						}else{
 							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
 						}
 					}
 				}
 			} else {

 				if (tempSecurityUser==null ) { // if records not exists in the Work flow table 
 					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
 				}

 				if (oldSecurityOperation!=null && !oldSecurityOperation.getLastMntOn().equals(tempSecurityUser.getLastMntOn())) { 
 					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
 				}
 			}
 		}

 		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

 		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !secOperation.isWorkflow()){
 			auditDetail.setBefImage(befSecurityOperation);	
 		}
 		return auditDetail;		 
 	}	

     // This Method is used to get the user roles.
     public List<AuditDetail> getAuditUserRoles(SecurityOperation securityOperation,String auditTranType,String method,boolean online){
 		
 		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
 		boolean delete=false;
 		
 		for (int i = 0; i < securityOperation.getSecurityOperationRolesList().size(); i++) {
 			SecurityOperationRoles securityOperationRoles  = securityOperation.getSecurityOperationRolesList().get(i);
 			securityOperationRoles.setWorkflowId(securityOperation.getWorkflowId());
 			
 			boolean isNewRecord= false;

 			if(delete){
 				securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
 			}else{
 			if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
 				securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_NEW);
 				isNewRecord=true;
 			}else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
 				securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_UPD);
 				isNewRecord=true;
 			}else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
 				securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_DEL);
 				isNewRecord=true;
 			}
 			}
 			if("saveOrUpdate".equals(method) && (isNewRecord && securityOperationRoles.isWorkflow())){
 				securityOperationRoles.setNewRecord(true);
 			}

 			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
 				if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
 					auditTranType= PennantConstants.TRAN_ADD;
 				} else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
 					auditTranType= PennantConstants.TRAN_DEL;
 				}else{
 					auditTranType= PennantConstants.TRAN_UPD;
 				}
 			}

 			securityOperationRoles.setRecordStatus(securityOperation.getRecordStatus());
 			securityOperationRoles.setUserDetails(securityOperation.getUserDetails());
 			securityOperationRoles.setLastMntOn(securityOperation.getLastMntOn());
 			securityOperationRoles.setLastMntBy(securityOperation.getLastMntBy());

 			if(StringUtils.isNotEmpty(securityOperationRoles.getRecordType())){
 				auditDetails.add(new AuditDetail(auditTranType, i+1, securityOperationRoles.getBefImage(), securityOperationRoles));
 			}
 		}
 		
 		return auditDetails;
 	}
     
     /**
 	 * Method For Preparing List of AuditDetails for SecurityOperationRoles
 	 * @param auditDetails
 	 * @param type
 	 * @return
 	 */
 	private List<AuditDetail> processingDetailList(List<AuditDetail> auditDetails, String type,SecurityOperation securityOperation) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		List<AuditDetail> list= new ArrayList<AuditDetail>();
		
		for (AuditDetail auditDetail : auditDetails) {

			SecurityOperationRoles securityOperationRoles = (SecurityOperationRoles) auditDetail.getModelData();
			
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			
			if (StringUtils.isEmpty(type)) {
				securityOperationRoles.setVersion(securityOperationRoles.getVersion()+1);
				approveRec=true;
			}else{
				securityOperationRoles.setRoleCode(securityOperation.getRoleCode());
				securityOperationRoles.setNextRoleCode(securityOperation.getNextRoleCode());
				securityOperationRoles.setTaskId(securityOperation.getTaskId());
				securityOperationRoles.setNextTaskId(securityOperation.getNextTaskId());
			}

			if (StringUtils.isNotEmpty(type) && securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(securityOperationRoles.isNewRecord()){
				saveRecord=true;
				if(securityOperationRoles.getId()==Long.MIN_VALUE){
					securityOperationRoles.setId(securityOperationRolesDAO.getNextValue());
				}
				if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					securityOperationRoles.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (securityOperationRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (securityOperationRoles.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			SecurityOperationRoles tempDetail=new SecurityOperationRoles();
			BeanUtils.copyProperties(securityOperationRoles,tempDetail);
			
			
			if(approveRec){
				securityOperationRoles.setRoleCode("");
				securityOperationRoles.setNextRoleCode("");
				securityOperationRoles.setTaskId("");
				securityOperationRoles.setNextTaskId("");
				securityOperationRoles.setRecordType("");
				securityOperationRoles.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getSecurityOperationRolesDAO().save(securityOperationRoles, type);
			}

			if (updateRecord) {
				getSecurityOperationRolesDAO().update(securityOperationRoles, type);
			}

			if (deleteRecord) {
				getSecurityOperationRolesDAO().delete(securityOperationRoles, type);
			}
			
			if(saveRecord || updateRecord || deleteRecord){
				if(!securityOperationRoles.isWorkflow()){
					auditDetail.setModelData(securityOperationRoles);
				}else{
					auditDetail.setModelData(tempDetail);
				}
				list.add(auditDetail);
			}
		}
		
		logger.debug("Leaving ");
		return list;	
	}
	

 	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSecurityOperationDAO().delete with parameters securityUser,""
	 * 		b)  NEW		Add new record in to main table by using getSecurityOperationDAO().save with parameters securityUser,""
	 * 		c)  EDIT	Update record in the main table by using getSecurityOperationDAO().update with parameters securityUser,""
	 * 3)	Delete the record from the workFlow table by using getSecurityOperationDAO().delete with parameters securityUser,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSecurityOperation by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSecurityOperation by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		SecurityOperation securityOperation = new SecurityOperation();
		BeanUtils.copyProperties((SecurityOperation) auditHeader.getAuditDetail().getModelData(), securityOperation);
		tranType=PennantConstants.TRAN_UPD;

			//Retrieving List of Audit Details For Security user roles details modules
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			auditDetails = processingAuditDetailList(auditHeader.getAuditDetails(),"",securityOperation);
		}

		getSecurityOperationRolesDAO().deleteById(securityOperation.getOprID(), "_Temp");
		getSecurityOperationDAO().delete(securityOperation,"_RTEMP");
		auditHeader.setAuditDetail(null);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader=resetAuditDetails(auditHeader, tranType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * Method For Preparing List of AuditDetails for authorizationDetail
	 * @param auditDetails
	 * @param type
	 * @param channelId
	 * @return
	 */
	
	private List<AuditDetail> processingAuditDetailList(List<AuditDetail> auditDetails, String type,SecurityOperation secOperation) {
		logger.debug("Entering ");

		List<AuditDetail> userRolesAuditDetails = new ArrayList<AuditDetail>();
			
		
		for (AuditDetail auditDetail : auditDetails) {
			Object object= auditDetail.getModelData();
			
			if(object.getClass().isInstance(new SecurityOperationRoles())){
				userRolesAuditDetails.add(auditDetail);
			}
		}

		if(!userRolesAuditDetails.isEmpty()){			
			userRolesAuditDetails = processingDetailList(userRolesAuditDetails, type, secOperation);
		}
		
		return userRolesAuditDetails;
	}

	private AuditHeader resetAuditDetails(AuditHeader auditHeader,String tranType){
		
		auditHeader.setAuditTranType(tranType);
		
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			List<AuditDetail> auditDetails= new ArrayList<AuditDetail>();
			
			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				SecurityOperationRoles oprRoles=(SecurityOperationRoles) detail.getModelData(); 
				detail.setAuditTranType(tranType);
				oprRoles.setRecordType("");
				oprRoles.setRoleCode("");
				oprRoles.setNextRoleCode("");
				oprRoles.setTaskId("");
				oprRoles.setNextTaskId("");
				oprRoles.setWorkflowId(0);
				detail.setModelData(oprRoles);
				auditDetails.add(detail);
			}
			auditHeader.setAuditDetails(auditDetails);
		}
		
		
		return auditHeader;
	} 
	
	/**
	 * This method fetches  List< SecurityRoleGroups > with "RoleId" condition by calling 
	 * <code>SecurityRoleGroupsDAO</code>'s <code>getSecRoleGroupsByRoleID()</code>
	 * @return List<SecurityRoleGroups>
	 */
	@Override
	public List<SecurityRoleGroups> getApprovedRoleGroupsByRoleId(long roleId) {
		return  getSecurityRoleGroupsDAO().getRoleGroupsByRoleID(roleId, "_AView");
	}
	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSecurityUserDAO().delete with parameters securityOperation,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtChannelDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityOperationDAO().delete(securityOperation,"_RTEMP");
		getSecurityOperationRolesDAO().deleteById(securityOperation.getOprID(), "_Temp");
		auditHeader.setAuditDetail(null);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}
	/**
	 * This method fetches SecurityRoleGroups record with "roleId and groupId" condition
	 */
	public SecurityOperationRoles getOprRolesByRoleAndOprId(long roleID,long oprId){

		return getSecurityOperationRolesDAO().getOprRolesByRoleAndOprId(roleID, oprId);
	}
	/**
	 * This method fetches List< SecurityGroupRights > with "GrpId" condition by calling SecurityGroupRightsDAO's
	 * getSecurityGroupRightsByGrpId()
	 * @param securityGroup (SecurityGroup)
	 * @return  List<SecurityGroupRights> 
	 */
	@Override
	public List<SecurityGroupRights> getGroupRightsByGrpId(
			SecurityGroup securityGroup) {
		return  getSecurityGroupRightsDAO().getSecurityGroupRightsByGrpId(securityGroup.getGrpID());	

	}
	/**
	 * This method fetches <code>List< SecurityRole > </code> with "userId" condition by calling 
	 * <code>SecurityUsersRolesDAO</code>'s <code>getRolesByroleId()</code>
	 * @param userId(long)
	 * @param isAssigned(boolean)
	 */
	@Override
	public List<SecurityRole> getRolesByroleId(long roleId, boolean isAssigned) {
		return getSecurityOperationRolesDAO(). getRolesByUserId(roleId,isAssigned);
	}

	@Override
	public SecurityOperationRoles getOperationRolesByOprAndRoleIds(long oprId) {
		return getSecurityOperationRolesDAO().getOperationRolesByOprAndRoleIds(oprId);
	}

	@Override
	public List<SecurityRole> getApprovedRoles() {
		return getSecurityRoleDAO().getApprovedSecurityRole();
	}
	@Override
	public int getSecurityOprRoleInQueue(long oprID, String tableType) {
		return getSecurityOperationRolesDAO().getOprById(oprID, "_View");
	}
	/**
	 * This method is used to set the validation for the assigned roles
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityOperationDAO().delete(securityOperation,"_RTEMP");
		getSecurityOperationRolesDAO().deleteById(securityOperation.getOprID(), "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//	//getters and setters 
	
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public SecurityOperationRolesDAO getSecurityOperationRolesDAO() {
		return securityOperationRolesDAO;
	}

	public void setSecurityOperationRolesDAO(
			SecurityOperationRolesDAO securityOperationRolesDAO) {
		this.securityOperationRolesDAO = securityOperationRolesDAO;
	}


	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public SecurityRoleGroupsDAO getSecurityRoleGroupsDAO() {
		return securityRoleGroupsDAO;
	}

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}

	public SecurityRoleDAO getSecurityRoleDAO() {
		return securityRoleDAO;
	}

	public void setSecurityRoleDAO(SecurityRoleDAO securityRoleDAO) {
		this.securityRoleDAO = securityRoleDAO;
	}

	public SecurityOperationDAO getSecurityOperationDAO() {
		return securityOperationDAO;
	}

	public void setSecurityOperationDAO(SecurityOperationDAO securityOperationDAO) {
		this.securityOperationDAO = securityOperationDAO;
	}
	

	public SecurityGroupRightsDAO getSecurityGroupRightsDAO() {
		return securityGroupRightsDAO;
	}

	public void setSecurityGroupRightsDAO(
			SecurityGroupRightsDAO securityGroupRightsDAO) {
		this.securityGroupRightsDAO = securityGroupRightsDAO;
	}

}
