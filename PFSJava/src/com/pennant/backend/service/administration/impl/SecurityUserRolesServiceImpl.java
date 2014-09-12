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
 * FileName    		:  SecurityUserRolesServiceImpl.java                                    *
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  2-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 2-08-2011        Pennant	                 0.1                                            *
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
import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserRolesDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserRolesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class SecurityUserRolesServiceImpl extends GenericService<SecurityUserRoles> implements SecurityUserRolesService {

	private final static Logger logger = Logger.getLogger(SecurityUserRolesServiceImpl.class);

	private  SecurityUserRolesDAO    securityUserRolesDAO;
	private  SecurityRoleGroupsDAO    securityRoleGroupsDAO;
	private  SecurityGroupRightsDAO  securityGroupRightsDAO;
	private  SecurityRoleDAO securityRoleDAO;
	private SecurityUserDAO securityUserDAO;
	
	private  AuditHeaderDAO auditHeaderDAO;
	private  NextidviewDAO 	nextidviewDAO;
	//private SecurityUser secUser;

	public SecurityUserRoles getSecurityUserRoles(){
		return getSecurityUserRolesDAO().getSecurityUserRoles();

	}
	
	/**
	 * @return the securityUsersDAO
	 */
	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}

	/**
	 * @param securityUsersDAO the securityUsersDAO to set
	 */
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	/**
	 *This method do the following
	 * 1)Gets the AuditDetails list by calling businessValidation() method 
	 * 2)a)it checks for each AuditDetail  if "AuditTranType" is "A" it saves the record by calling SecurityUsersRolesDAO's
	 *   save() method
	 *   b)if "AuditTranType" is "D" it Deletes the record by calling SecurityUsersRolesDAO's
	 *   delete() method
	 *
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader){
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType="";
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();

		if (securityUser.isWorkflow()) {
			tableType="_TEMP";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		} else {
			securityUser.setRecordType("");
			securityUser.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			auditHeader.getAuditDetail().setModelData(securityUser);		
		}

		if(securityUser.isWorkflow()) {
			if (securityUser.isNewRecord()) {
					getSecurityUserDAO().save(securityUser,"_RTEMP"); //_RTEMP
					auditHeader.getAuditDetail().setModelData(securityUser);
					auditHeader.setAuditReference(String.valueOf(securityUser.getUsrID()));
			} else {
					getSecurityUserDAO().update(securityUser,"_RTEMP"); //_RTEMP
			}
		}
		
		// PROCESS DETAILS
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			auditHeader.setAuditDetails(processingDetailList(auditHeader.getAuditDetails(), tableType, securityUser));
			auditHeader.setAuditDetail(null);
		}
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
	public AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean online){
		logger.debug("Entering");		
		
		// FIXME validation for the SecurityUser;
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,online);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails =getAuditUserRoles(securityUser, auditHeader.getAuditTranType(), method, auditHeader.getUsrLanguage(), online);   
		
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
	 * getSecurityUserRoleDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,String method,boolean online) {
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		SecurityUser secUser=(SecurityUser)auditDetail.getModelData();
		
		SecurityUser tempSecurityUser= null;
		if (secUser.isWorkflow()) {
			tempSecurityUser = getSecurityUserDAO().getSecurityUserById(secUser.getId(), "_RTemp"); //_RTemp
		}
		
		SecurityUser befSecurityUser= getSecurityUserDAO().getSecurityUserById(secUser.getId(), "");
		SecurityUser oldSecurityUser= secUser.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(secUser.getUsrLogin());
		errParm[0]=PennantJavaUtil.getLabel("label_UsrLogin")+":"+valueParm[0];

		if (secUser.isNewRecord()){    			// for New record or new record into work flow

			if (!secUser.isWorkflow()) { 		// With out Work flow only new records  
				if (befSecurityUser !=null) {	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			} else { 							// with work flow
				if (secUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSecurityUser !=null || tempSecurityUser!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				} else { 						// if records not exists in the Main flow table
					if (befSecurityUser ==null || tempSecurityUser!=null ) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!secUser.isWorkflow()) {		// With out Work flow for update and delete

				if (befSecurityUser ==null){ 	// if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldSecurityUser!=null && !oldSecurityUser.getLastMntOn().equals(befSecurityUser.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempSecurityUser==null ) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldSecurityUser!=null && !oldSecurityUser.getLastMntOn().equals(tempSecurityUser.getLastMntOn())) { 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !secUser.isWorkflow()){
			auditDetail.setBefImage(befSecurityUser);	
		}
		return auditDetail;		 
	}	

	public List<AuditDetail> getAuditUserRoles(SecurityUser securityUser,String auditTranType,String method,String language,boolean online){
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete=false;
		/*if ((PennantConstants.RECORD_TYPE_DEL.equals(securityUser.getRecordType()) && method.equalsIgnoreCase("doApprove")) || method.equals("delete")) {
			delete=true;
		}*/
		
		for (int i = 0; i < securityUser.getSecurityUserRolesList().size(); i++) {
			SecurityUserRoles securityUserRoles  = securityUser.getSecurityUserRolesList().get(i);
			securityUserRoles.setWorkflowId(securityUser.getWorkflowId());
			
			boolean isNewRecord= false;

			if(delete){
				securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			}else{
			if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isNewRecord=true;
			}else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isNewRecord=true;
			}else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isNewRecord=true;
			}
			}
			if(method.equals("saveOrUpdate") && (isNewRecord && securityUserRoles.isWorkflow())){
				if(!securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)){
				securityUserRoles.setNewRecord(true);
				}
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			securityUserRoles.setRecordStatus(securityUser.getRecordStatus());
			securityUserRoles.setUserDetails(securityUser.getUserDetails());
			securityUserRoles.setLastMntOn(securityUser.getLastMntOn());
			securityUserRoles.setLastMntBy(securityUser.getLastMntBy());

			if(!securityUserRoles.getRecordType().equals("")){
				auditDetails.add(new AuditDetail(auditTranType, i+1, securityUserRoles.getBefImage(), securityUserRoles));
			}
		}
		
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for securityUserRoles
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDetailList(List<AuditDetail> auditDetails, String type,SecurityUser securityUser) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		long seqNumber = getNextidviewDAO().getSeqNumber("SeqSecUserRoles"); 
		int count = 0;
		
		
		List<AuditDetail> list= new ArrayList<AuditDetail>();
		
		for (AuditDetail auditDetail : auditDetails) {

			SecurityUserRoles securityUserRoles = (SecurityUserRoles) auditDetail.getModelData();
			
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			
			if (type.equals("")) {
				securityUserRoles.setVersion(securityUserRoles.getVersion()+1);
				securityUserRoles.setWorkflowId(0);
				approveRec=true;
			}else{
				securityUserRoles.setRoleCode(securityUser.getRoleCode());
				securityUserRoles.setNextRoleCode(securityUser.getNextRoleCode());
				securityUserRoles.setTaskId(securityUser.getTaskId());
				securityUserRoles.setNextTaskId(securityUser.getNextTaskId());
			}

			if (!type.equals("") && securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(securityUserRoles.isNewRecord()){
				saveRecord=true;
				if(securityUserRoles.getId()==Long.MIN_VALUE){
					count++;
					securityUserRoles.setId(seqNumber+count);
				}
				if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					securityUserRoles.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (securityUserRoles.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(securityUserRoles.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			SecurityUserRoles tempDetail=new SecurityUserRoles();
			BeanUtils.copyProperties(securityUserRoles,tempDetail);
			
			
			if(approveRec){
				securityUserRoles.setRoleCode("");
				securityUserRoles.setNextRoleCode("");
				securityUserRoles.setTaskId("");
				securityUserRoles.setNextTaskId("");
				securityUserRoles.setRecordType("");
				securityUserRoles.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getSecurityUserRolesDAO().save(securityUserRoles, type);
			}

			if (updateRecord) {
				getSecurityUserRolesDAO().update(securityUserRoles, type);
			}

			if (deleteRecord) {
				getSecurityUserRolesDAO().delete(securityUserRoles, type);
			}
			
			if(saveRecord || updateRecord || deleteRecord){
				if(!securityUserRoles.isWorkflow()){
					auditDetail.setModelData(securityUserRoles);
				}else{
					auditDetail.setModelData(tempDetail);
				}
				list.add(auditDetail);
			}
		}
		
		if(count!=0){
			getNextidviewDAO().setSeqNumber("SeqSecUserRoles", seqNumber+count);
		}
		
		logger.debug("Leaving ");
		return list;	
	}
	
	
	/**
	 * This method fetches  List< SecurityRoleGroups > with "RoleId" condition by calling 
	 * <code>SecurityRoleGroupsDAO</code>'s <code>getSecRoleGroupsByRoleID()</code>
	 * @return List<SecurityRoleGroups>
	 */
	public List<SecurityRoleGroups> getApprovedRoleGroupsByRoleId(long roleId) {
		return  getSecurityRoleGroupsDAO().getRoleGroupsByRoleID(roleId, "_AView");
	}
	
	/**
	 * This method fetches List< SecurityGroupRights > with "GrpId" condition by calling SecurityGroupRightsDAO's
	 * getSecurityGroupRightsByGrpId()
	 * @param securityGroup (SecurityGroup)
	 * @return  List<SecurityGroupRights> 
	 */
	public List<SecurityGroupRights> getGroupRightsByGrpId(SecurityGroup securityGroup){
		return  getSecurityGroupRightsDAO().getSecurityGroupRightsByGrpId(securityGroup.getGrpID());	

	}
	/**
	 * This method fetches <code>List< SecurityRole > </code> with "userId" condition by calling 
	 * <code>SecurityUsersRolesDAO</code>'s <code>getRolesByUserId()</code>
	 * @param userId(long)
	 * @param isAssigned(boolean)
	 */
	public  List<SecurityRole> getRolesByUserId(long userId,boolean isAssigned){
		return getSecurityUserRolesDAO(). getRolesByUserId(userId,isAssigned);
	}
	/**
	 * This method fetches SecurityUserRoles record with "userId" and "RoleId" condition by 
	 * calling SecurityUsersRolesDAO's getUserRolesByUsrAndRoleIds)
	 */
	public  SecurityUserRoles getUserRolesByUsrAndRoleIds(long userId,long roleId){
		return getSecurityUserRolesDAO().getUserRolesByUsrAndRoleIds(userId,roleId);

	}

	@Override
    public List<String> getUsrMailsByRoleCd(String roleCode) {
	    return getSecurityUserRolesDAO().getUsrMailsByRoleCd(roleCode);
    }
	@Override
	public List<String> getUsrMailsByRoleIds(String roleCode) {
		return getSecurityUserRolesDAO().getUsrMailsByRoleIds(roleCode);
	}
	@Override
	public  List<SecurityRole> getApprovedRoles(){
		return getSecurityRoleDAO().getApprovedSecurityRole();
	}

	public SecurityUserRoles getNewSecurityUserRoles() {
		return getSecurityUserRolesDAO().getNewSecurityUserRoles();
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSecurityUserDAO().delete with parameters securityUser,""
	 * 		b)  NEW		Add new record in to main table by using getSecurityUserDAO().save with parameters securityUser,""
	 * 		c)  EDIT	Update record in the main table by using getSecurityUserDAO().update with parameters securityUser,""
	 * 3)	Delete the record from the workFlow table by using getSecurityUserDAO().delete with parameters securityUser,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSecurityUsers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSecurityUsers by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		SecurityUser securityUser = new SecurityUser();
		BeanUtils.copyProperties((SecurityUser) auditHeader.getAuditDetail().getModelData(), securityUser);
		tranType=PennantConstants.TRAN_UPD;

			//Retrieving List of Audit Details For Security user roles details modules
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			auditDetails = processingAuditDetailList(auditHeader.getAuditDetails(),"",securityUser);
		}

		getSecurityUserRolesDAO().deleteById(securityUser.getUsrID(), "_TEMP");
		getSecurityUserDAO().delete(securityUser,"_RTEMP"); //_RTemp
		auditHeader.setAuditDetail(null);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader=resetAuditDetails(auditHeader, securityUser, tranType);
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
	
	private List<AuditDetail> processingAuditDetailList(List<AuditDetail> auditDetails, String type,SecurityUser secUser) {
		logger.debug("Entering ");

		List<AuditDetail> userRolesAuditDetails = new ArrayList<AuditDetail>();
			
		
		for (AuditDetail auditDetail : auditDetails) {
			Object object= auditDetail.getModelData();
			
			if(object.getClass().isInstance(new SecurityUserRoles())){
				userRolesAuditDetails.add(auditDetail);
			}
		}

		if(userRolesAuditDetails!=null && !userRolesAuditDetails.isEmpty()){			
			userRolesAuditDetails = processingDetailList(userRolesAuditDetails, type, secUser);
		}
		
		return userRolesAuditDetails;
	}

	private AuditHeader resetAuditDetails(AuditHeader auditHeader, SecurityUser securityUser,String tranType){
		
		auditHeader.setAuditTranType(tranType);
		
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			List<AuditDetail> auditDetails= new ArrayList<AuditDetail>();
			
			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				SecurityUserRoles userRoles=(SecurityUserRoles) detail.getModelData(); 
				detail.setAuditTranType(tranType);
				userRoles.setRecordType("");
				userRoles.setRoleCode("");
				userRoles.setNextRoleCode("");
				userRoles.setTaskId("");
				userRoles.setNextTaskId("");
				userRoles.setWorkflowId(0);
				detail.setModelData(userRoles);
				auditDetails.add(detail);
			}
			auditHeader.setAuditDetails(auditDetails);
		}
		
		
		return auditHeader;
	} 

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSecurityUserDAO().delete with parameters securityUser,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtChannelDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityUserDAO().delete(securityUser,"_RTEMP"); //_RTemp
		getSecurityUserRolesDAO().deleteById(securityUser.getUsrID(), "_TEMP");
		auditHeader.setAuditDetail(null);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public SecurityUserRolesDAO getSecurityUserRolesDAO() {
		return securityUserRolesDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}

	public SecurityRoleGroupsDAO getSecurityRoleGroupsDAO() {
		return securityRoleGroupsDAO;
	}

	public SecurityGroupRightsDAO getSecurityGroupRightsDAO() {
		return securityGroupRightsDAO;
	}

	public void setSecurityGroupRightsDAO(
			SecurityGroupRightsDAO securityGroupRightsDAO) {
		this.securityGroupRightsDAO = securityGroupRightsDAO;
	}
	public void setSecurityUserRolesDAO(SecurityUserRolesDAO securityUserRolesDAO) {
		this.securityUserRolesDAO = securityUserRolesDAO;
	}
	public void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		this.nextidviewDAO = nextidviewDAO;
	}
	public NextidviewDAO getNextidviewDAO() {
		return nextidviewDAO;
	}
	/**
	 * @return the securityRoleDAO
	 */
	public SecurityRoleDAO getSecurityRoleDAO() {
		return securityRoleDAO;
	}
	/**
	 * @param securityRoleDAO the securityRoleDAO to set
	 */
	public void setSecurityRoleDAO(SecurityRoleDAO securityRoleDAO) {
		this.securityRoleDAO = securityRoleDAO;
	}
	
}
