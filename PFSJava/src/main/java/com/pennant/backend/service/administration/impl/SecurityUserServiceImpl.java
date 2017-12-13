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
 * FileName    		:  SecurityUserServiceImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  30-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  30-07-2011      Pennant	                 0.1                                            * 
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;

/**
 * Service implementation for methods that depends on <b>SecurityUsers</b>.<br>
 * 
 */
public class SecurityUserServiceImpl extends GenericService<SecurityUser> implements SecurityUserService {

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityUserDAO securityUsersDAO;
	private SecurityUserPasswordsDAO securityUserPasswordsDAO;
	private QueueAssignmentDAO queueAssignmentDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;

	private static Logger logger = Logger.getLogger(SecurityUserServiceImpl.class);

	public SecurityUserServiceImpl() {
		super();
	}
	
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)   Encode the password and sets usrToken by calling 
	 * 2)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 3)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SecUsers/SecUsers_Temp 
	 * 			by using SecurityUsersDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SecurityUsersDAO's update method
	 * 4)	Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");

		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";

		if (securityUser.isWorkflow()) {
			tableType="_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}else{

			if (securityUser.isNew()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			}else{
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityUser.setRecordStatus("");
			securityUser.setRoleCode("");
			securityUser.setNextRoleCode("");
			securityUser.setTaskId("");
			securityUser.setNextTaskId("");
			securityUser.setRecordType("");
			securityUser.setWorkflowId(0);
		}

		if (securityUser.isNewRecord()) {
			securityUser.setId(getSecurityUserDAO().save(securityUser, tableType));
			if (AuthenticationType.DAO.name().equals(securityUser.getAuthType())) {
				getSecurityUserPasswordsDAO().save(securityUser);
			}
			auditHeader.getAuditDetail().setModelData(securityUser);
			auditHeader.setAuditReference(String.valueOf(securityUser.getId()));
		} else {
			getSecurityUserDAO().update(securityUser, tableType);
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// set SecUser Division Branch Details Audit
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			auditDetails.addAll(processingDetailList(auditHeader.getAuditDetails(), tableType, securityUser));
		}
		auditHeader.setAuditDetails(auditDetails);
		
		getAuditHeaderDAO().addAudit(auditHeader);		
		logger.debug("Leaving ");
		return auditHeader;

	}
	

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SecUsers by using SecurityUsersDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		SecurityUser securityUsers = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		getSecurityUserDAO().delete(securityUsers,"");
		auditDetails.addAll(secUserDivBranchDeletion(securityUsers, "", auditHeader.getAuditTranType()));
		auditHeader.setAuditDetails(auditDetails);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}
	
	
	/**
	 * getSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityUsers
	 */

	public SecurityUser getSecurityUserById(long id) {
		logger.debug("Entering ");
		return getSecurityUserDAO().getSecurityUserById(id,"_View");
	}
//	/**
//	 * getApprovedSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method .
//	 * with parameter id and type as blank. it fetches the approved records from the SecUsers.
//	 * @param id (int)
//	 * @return SecurityUsers
//	 */
//
//	public SecurityUser getApprovedSecurityUserById(long id) {
//		logger.debug("Entering ");
//		return getSecurityUserDAO().getSecurityUserById(id,"_AView");
//	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSecurityUserDAO().delete with parameters secUsers,""
	 * 		b)  NEW		Add new record in to main table by using getSecurityUserDAO().save with parameters secUsers,""
	 * 		c)  EDIT	Update record in the main table by using getSecurityUserDAO().update with parameters secUsers,""
	 * 3)	Delete the record from the workFlow table by using getSecurityUserDAO().delete with parameters secUsers,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		SecurityUser securityUser = new SecurityUser();
		BeanUtils.copyProperties((SecurityUser) auditHeader.getAuditDetail().getModelData(), securityUser);

		String tranType="";

		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteDivBranchs(securityUser.getSecurityUserDivBranchList(), "", tranType));
			getSecurityUserDAO().delete(securityUser,"");

		} else {
			securityUser.setRoleCode("");
			securityUser.setNextRoleCode("");
			securityUser.setTaskId("");
			securityUser.setNextTaskId("");
			securityUser.setWorkflowId(0);

			if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				securityUser.setRecordType("");
				getSecurityUserDAO().save(securityUser,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				securityUser.setRecordType("");
				getSecurityUserDAO().update(securityUser,"");
			}
			
			// set the Audit Details & Save / Update Security User DivBranch Details
			if (securityUser.getSecurityUserDivBranchList() != null  && !securityUser.getSecurityUserDivBranchList().isEmpty()) {
				auditDetails.addAll(doApproveDivBrDetails(securityUser, "", tranType));
			}
		}

		getSecurityUserDAO().delete(securityUser,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(securityUser);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		// If any records exists in this user queue re-assign them to next available users
		if(!securityUser.isUsrEnabled()) {
			getQueueAssignmentDAO().executeStoredProcedure(securityUser.getUsrID());
		}
		
		logger.debug("Leaving ");
		return auditHeader;
	}
	
	
	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSecurityUserDAO().delete with parameters securityUsers,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSecUsers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		SecurityUser securityUser= (SecurityUser) auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(deleteDivBranchs(securityUser.getSecurityUserDivBranchList(), "_Temp", auditHeader.getAuditTranType()));
		getSecurityUserDAO().delete(securityUser,"_Temp");
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		String auditTranType = auditHeader.getAuditTranType();
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = securityUser.getUserDetails().getUsrLanguage();
		List<SecurityUserDivBranch> securityUserDivBranchList = securityUser.getSecurityUserDivBranchList();
		if (securityUserDivBranchList != null  && !securityUserDivBranchList.isEmpty()) {
			auditDetails = getAuditUserDivBranchs(securityUser, auditTranType, method, usrLanguage, false);
		}
		for (AuditDetail detail:auditDetails) {
			auditHeader.addAuditDetail(detail);
			auditHeader.setErrorList(detail.getErrorDetails());
		}
		
		auditHeader=nextProcess(auditHeader);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getSecurityUserDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		SecurityUser securityUser= (SecurityUser) auditDetail.getModelData();
		SecurityUser befSecurityUser= getSecurityUserDAO().getSecurityUserByLogin(securityUser.getUsrLogin(), "");
		
		if ("changePassword".equals(StringUtils.trimToEmpty(method))) {
			auditDetail.setBefImage(befSecurityUser);	
			logger.debug("Leaving ");
			return auditDetail;
		}
		
		SecurityUser tempSecurityUser= null;
		if (securityUser.isWorkflow()){
			tempSecurityUser = getSecurityUserDAO().getSecurityUserByLogin(securityUser.getUsrLogin(), "_Temp");
		}
		//SecurityUser aBefSecurityUser= getSecurityUserDAO().getSecurityUserByLogin(securityUser.getUsrLogin(), "");
		SecurityUser oldSecurityUser= securityUser.getBefImage();

		String[] errParm= new String[4];
		errParm[0]=PennantJavaUtil.getLabel("label_UsrLogin");
		errParm[1]=String.valueOf(securityUser.getUsrLogin());

		String[] userLoginExisted= new String[4];
		userLoginExisted[0]=PennantJavaUtil.getLabel("label_UsrLogin");
		userLoginExisted[1]=String.valueOf(securityUser.getUsrLogin());

		String[] parmUserIdAssigned= new String[10];
		parmUserIdAssigned[0]=PennantJavaUtil.getLabel("label_Roles");
		parmUserIdAssigned[1]=PennantJavaUtil.getLabel("label_User");

		if (securityUser.isNewRecord()){ // for New record or new record into work flow

			if (!securityUser.isWorkflow()){// With out Work flow only new records  
				if (befSecurityUser !=null){	// Record Already Exists in the table with same userID then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

				}	
				/*if (aBefSecurityUser !=null){	// Record Already Exists in the table with same userLogin then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",userLoginExisted,null));

				}*/
			}else{ // with work flow
				if (tempSecurityUser!=null ){ // if records already exists in the Work flow table 
					//auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

				}

				if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befSecurityUser !=null){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

					}
				}else{ // if records not exists in the Main flow table
					if (befSecurityUser ==null){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));

					}
					/*if (aBefSecurityUser !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

					}*/
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!securityUser.isWorkflow()){	// With out Work flow for update and delete

				if (befSecurityUser ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));

				}

				if (befSecurityUser!=null && oldSecurityUser!=null && !oldSecurityUser.getLastMntOn().equals(befSecurityUser.getLastMntOn())){
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));	
					}else{
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
					}

				}

			}else{

				if (tempSecurityUser==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));

				}

				if (tempSecurityUser!=null && oldSecurityUser!=null && !oldSecurityUser.getLastMntOn().equals(tempSecurityUser.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));

				}
			}
		}


		if("delete".equals(StringUtils.trimToEmpty(method))){
			/*check whether userId assigned to any Roles by calling SecurityUsersRolesDAO's getUserIdCount()*/
			int roleIdCount =getSecurityUserOperationsDAO().getUserIdCount(securityUser.getUsrID());
			/*if roleId assigned for any user or group show error message*/
			if(roleIdCount>0){
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"49002",parmUserIdAssigned,null));

			}	
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if("doApprove".equals(StringUtils.trimToEmpty(method))){
			securityUser.setBefImage(befSecurityUser);	
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	/**
	 * changePassword method do the following
	 * <br>1.Takes input SecurityUser Object ,sets the userAcExpDt with the condition whether user himself changing password 
	 * or admin resetting  the password
	 * <br>2.Update the Password by calling  SecurityUsersDAO's changePassword(SecurityUser) method<br>
	 * <br>3.Save the usrId,password,token,lastMntBy in "SecUserPasswords" table by calling SecurityUsersDAO's saveRecentPassword()
	 * <br>4.If records are more than USR_MAX_PWD_BACKUP for single user delete the oldest record by calling SecurityUsersDAO' 
	 *  deleteOldestPassword
	 * @param auditHeader (AuditHeader)
	 * @return auditHeaders (AuditHeader)
	 *
	 */
	public AuditHeader changePassword(AuditHeader auditHeader) {
		logger.trace(Literal.ENTERING);
		SecurityUser securityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		securityUser.getBefImage().getLastMntOn();
		auditHeader = businessValidation(auditHeader, "changePassword");
		
		if (!auditHeader.isNextProcess()) {
			logger.trace(Literal.LEAVING);
			return auditHeader;
		}

		int expDays;
		//if password is changed by user itself sets UsrAcExpDt is 30days after present date
		if (securityUser.getLastMntBy() == securityUser.getUsrID()) {
			expDays = SysParamUtil.getValueAsInt("USR_EXPIRY_DAYS");
			securityUser.setUsrAcExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), expDays));
			/* save the password for backup */
			getSecurityUserPasswordsDAO().save(securityUser);
		} else {
			//if it is changed by admin sets UsrAcExpDt is one day before the present date
			securityUser.setUsrAcExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), -1));
		}

		getSecurityUserDAO().changePassword(securityUser);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.trace(Literal.LEAVING);
		return auditHeader;
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

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUsersDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUsersDAO) {
		this.securityUsersDAO = securityUsersDAO;
	}

	@Override
	public SecurityUser getSecurityUser() {
		return getSecurityUserDAO().getSecurityUser();
	}

	@Override
	public SecurityUser getNewSecurityUser() {
		return getSecurityUserDAO().getNewSecurityUser();
	}

	public void setSecurityUserPasswordsDAO(SecurityUserPasswordsDAO securityUserPasswordsDAO) {
		this.securityUserPasswordsDAO = securityUserPasswordsDAO;
	}

	public SecurityUserPasswordsDAO getSecurityUserPasswordsDAO() {
		return securityUserPasswordsDAO;
	}

	
	// Security User Division Branch Details	

	/**
	 * This method is to fetch division branch details for current user
	 */
	@Override
	public List<SecurityUserDivBranch> getSecUserDivBrList(long usrID,String type) {
		return getSecurityUserDAO().getSecUserDivBrList(usrID, type);
	}
	
/**
	 *  This method is to Delete division branch details for current user
	 * @param securityUsers
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	public List<AuditDetail> secUserDivBranchDeletion(SecurityUser securityUsers, String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<SecurityUserDivBranch> securityUserDivBranchList = securityUsers.getSecurityUserDivBranchList();

		if (securityUserDivBranchList != null && !securityUserDivBranchList.isEmpty()) {
			auditDetails.addAll(deleteDivBranchs(securityUserDivBranchList, tableType, auditTranType));
		}
		logger.debug("Leaving ");
		return auditDetails;
	}
	
	/**
	 * This method is to Delete division branch details for current user
	 */
	@Override
	public List<AuditDetail> deleteDivBranchs(List<SecurityUserDivBranch> securityUserDivBranchList, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if(securityUserDivBranchList != null){
			for (SecurityUserDivBranch securityUserDivBranch : securityUserDivBranchList) {
				SecurityUserDivBranch recordExistsTemp = getSecurityUserDAO().getSecUserDivBrDetailsById(securityUserDivBranch, "_Temp");
				if(recordExistsTemp != null){
					getSecurityUserDAO().deleteDivBranchDetails(securityUserDivBranch,"_Temp");
				}
				
				SecurityUserDivBranch recordExistsMain = getSecurityUserDAO().getSecUserDivBrDetailsById(securityUserDivBranch, tableType);
				if(recordExistsMain != null){
				getSecurityUserDAO().deleteDivBranchDetails(securityUserDivBranch,tableType);
				}
				String[] fields = PennantJavaUtil.getFieldDetails(securityUserDivBranch, securityUserDivBranch.getExcludeFields());
				auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], securityUserDivBranch.getBefImage(), securityUserDivBranch));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

    /**
	 * This method is to Approve division branch details for current user
	 */
	public List<AuditDetail> doApproveDivBrDetails(SecurityUser securityUser, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<SecurityUserDivBranch> securityUserDivBranchList = securityUser.getSecurityUserDivBranchList();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<SecurityUserDivBranch> secUserDivBranchList = getSecurityUserDAO().getSecUserDivBrList(securityUser.getUsrID(), tableType);
		if(!secUserDivBranchList.isEmpty()){
			getSecurityUserDAO().deleteBranchs(securityUser, tableType);
		}

		for (SecurityUserDivBranch securityUserDivBranch : securityUserDivBranchList) {
			if (securityUserDivBranch.getId()==Long.MIN_VALUE){
				securityUserDivBranch.setUsrID(securityUser.getUsrID());
			}
			SecurityUserDivBranch detail = new SecurityUserDivBranch();
			BeanUtils.copyProperties(securityUserDivBranch, detail);

			securityUserDivBranch.setRoleCode("");
			securityUserDivBranch.setNextRoleCode("");
			securityUserDivBranch.setTaskId("");
			securityUserDivBranch.setNextTaskId("");
			securityUserDivBranch.setWorkflowId(0);

			SecurityUserDivBranch recordExistMain = getSecurityUserDAO().getSecUserDivBrDetailsById(securityUserDivBranch, tableType);
			if(recordExistMain == null){
				getSecurityUserDAO().saveDivBranchDetails(securityUserDivBranch, tableType);
			}
			getSecurityUserDAO().deleteDivBranchDetails(securityUserDivBranch, "_Temp");

			String[] fields = PennantJavaUtil.getFieldDetails(securityUserDivBranch, securityUserDivBranch.getExcludeFields());
			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], securityUserDivBranch.getBefImage(), securityUserDivBranch));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	/**
	 * @param securityUser
	 * @param auditTranType
	 * @param method
	 * @param language
	 * @param online
	 * @return
	 */
	public List<AuditDetail> getAuditUserDivBranchs(SecurityUser securityUser, String auditTranType, String method,
			String language, boolean online) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete=false;
		/*if ((PennantConstants.RECORD_TYPE_DEL.equals(securityUser.getRecordType()) && method.equalsIgnoreCase("doApprove")) || method.equals("delete")) {
			delete=true;
		}*/
		
		for (int i = 0; i < securityUser.getSecurityUserDivBranchList().size(); i++) {
			SecurityUserDivBranch securityUserDivBranch  = securityUser.getSecurityUserDivBranchList().get(i);
			securityUserDivBranch.setWorkflowId(securityUser.getWorkflowId());
			
			boolean isNewRecord= false;

			if(delete){
				securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			}else{
			if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isNewRecord=true;
			}else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isNewRecord=true;
			}else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isNewRecord=true;
			}
			}
			if("saveOrUpdate".equals(method) && (isNewRecord && securityUserDivBranch.isWorkflow())){
				if(!securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)){
				securityUserDivBranch.setNewRecord(true);
				}
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			securityUserDivBranch.setRecordStatus(securityUser.getRecordStatus());
			securityUserDivBranch.setUserDetails(securityUser.getUserDetails());
			securityUserDivBranch.setLastMntOn(securityUser.getLastMntOn());
			securityUserDivBranch.setLastMntBy(securityUser.getLastMntBy());

			if(StringUtils.isNotEmpty(securityUserDivBranch.getRecordType())){
				auditDetails.add(new AuditDetail(auditTranType, i+1, securityUserDivBranch.getBefImage(), securityUserDivBranch));
			}
		}
		
		return auditDetails;
	}
	
	/**
	 * getApprovedSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method . with
	 * parameter id and type as blank. it fetches the approved records from the SecUsers.
	 * 
	 * @param id
	 *            (int)
	 * @return SecurityUsers
	 */
	@Override
	public SecurityUser getApprovedSecurityUserById(long id) {
		logger.debug("Entering ");

		return getSecurityUserDAO().getSecurityUserById(id, "_AView");
	}
	
	/**
	 * Method For Preparing List of AuditDetails for securityUser
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

		
		List<AuditDetail> list= new ArrayList<AuditDetail>();
		
		for (AuditDetail auditDetail : auditDetails) {

			SecurityUserDivBranch securityUserDivBranch = (SecurityUserDivBranch) auditDetail.getModelData();
			
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			
			if (StringUtils.isEmpty(type)) {
				securityUserDivBranch.setVersion(securityUserDivBranch.getVersion()+1);
				approveRec=true;
			}else{
				securityUserDivBranch.setRoleCode(securityUser.getRoleCode());
				securityUserDivBranch.setNextRoleCode(securityUser.getNextRoleCode());
				securityUserDivBranch.setTaskId(securityUser.getTaskId());
				securityUserDivBranch.setNextTaskId(securityUser.getNextTaskId());
			}

			if (StringUtils.isNotEmpty(type) && securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(securityUserDivBranch.isNewRecord()){
				saveRecord=true;
				if(securityUserDivBranch.getId()==Long.MIN_VALUE){
					securityUserDivBranch.setUsrID(securityUser.getUsrID());
				}
				if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					securityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (securityUserDivBranch.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				deleteRecord=true;
			}

			SecurityUserDivBranch tempDetail=new SecurityUserDivBranch();
			BeanUtils.copyProperties(securityUserDivBranch,tempDetail);
			
			
			if(approveRec){
				securityUserDivBranch.setRoleCode("");
				securityUserDivBranch.setNextRoleCode("");
				securityUserDivBranch.setTaskId("");
				securityUserDivBranch.setNextTaskId("");
				securityUserDivBranch.setRecordType("");
				securityUserDivBranch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			SecurityUserDivBranch recordExist = getSecurityUserDAO().getSecUserDivBrDetailsById(securityUserDivBranch, type);
			if (saveRecord) {
				if(recordExist == null){
				getSecurityUserDAO().saveDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if (updateRecord) {
				if(recordExist != null){
					getSecurityUserDAO().updateDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if(deleteRecord){
				if(recordExist != null){
				getSecurityUserDAO().deleteDivBranchDetails(securityUserDivBranch, type);
				}
			}

			if(saveRecord || updateRecord || deleteRecord){
				if(!securityUserDivBranch.isWorkflow()){
					auditDetail.setModelData(securityUserDivBranch);
				}else{
					auditDetail.setModelData(tempDetail);
				}
				list.add(auditDetail);
			}
		}
		
		logger.debug("Leaving ");
		return list;	
	}


	public QueueAssignmentDAO getQueueAssignmentDAO() {
	    return queueAssignmentDAO;
    }


	public void setQueueAssignmentDAO(QueueAssignmentDAO queueAssignmentDAO) {
	    this.queueAssignmentDAO = queueAssignmentDAO;
    }
 
	
	

	public SecurityUserOperationsDAO getSecurityUserOperationsDAO() {
		return securityUserOperationsDAO;
	}


	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}


	@Override
	public SecurityUser getSecurityUserOperationsById(long id) {
		logger.debug("Entering ");
		return getSecurityUserOperationById(id,"_RView", true);
	}
	
	@Override
    public SecurityUser getApprovedSecurityUserOperationsById(long id) {
		logger.debug("Entering ");
		return getSecurityUserOperationById(id,"_AView", true);
	}
	
	
	private SecurityUser getSecurityUserOperationById(long id,String type, boolean getOperations) {
		logger.debug("Entering ");
		SecurityUser securityUser =getSecurityUserDAO().getSecurityUserById(id,type);
		if(securityUser!=null && getOperations){
			if("_RView".equals(type)){
				type = "_View";
			}
			securityUser.setSecurityUserOperationsList(getSecurityUserOperationsDAO().getSecUserOperationsByUsrID(securityUser,type));
		}
		
		logger.debug("Leaving ");
		return securityUser;
	}
	
}