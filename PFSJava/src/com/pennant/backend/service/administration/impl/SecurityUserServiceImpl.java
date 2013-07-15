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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.dao.administration.SecurityUserRolesDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.sec.util.PasswordEncoderImpl;

/**
 * Service implementation for methods that depends on <b>SecurityUsers</b>.<br>
 * 
 */
public class SecurityUserServiceImpl extends GenericService<SecurityUser> implements SecurityUserService {

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityUserDAO securityUsersDAO;
	private SecurityUserRolesDAO securityUserRolesDAO;
	private SecurityUserPasswordsDAO securityUserPasswordsDAO;

	private static Logger logger = Logger.getLogger(SecurityUserServiceImpl.class);


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
		//Password encryption and setting token to securityUsers
		PasswordEncoderImpl pwdEncoder=new PasswordEncoderImpl();
		securityUser = pwdEncoder.encodePassword(securityUser);

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";

		if (securityUser.isWorkflow()) {
			tableType="_TEMP";
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

		if (securityUser.isNew()) {
			securityUser.setId(getSecurityUserDAO().save(securityUser,tableType));
			getSecurityUserPasswordsDAO().save(securityUser);	
			auditHeader.getAuditDetail().setModelData(securityUser);
			auditHeader.setAuditReference(String.valueOf(securityUser.getId()));
		}else{
			getSecurityUserDAO().update(securityUser,tableType);
		}
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
		SecurityUser securityUsers = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		getSecurityUserDAO().delete(securityUsers,"");
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
	/**
	 * getApprovedSecurityUsersById fetch the details by using SecurityUsersDAO's getSecurityUsersById method .
	 * with parameter id and type as blank. it fetches the approved records from the SecUsers.
	 * @param id (int)
	 * @return SecurityUsers
	 */

	public SecurityUser getApprovedSecurityUserById(long id) {
		logger.debug("Entering ");
		return getSecurityUserDAO().getSecurityUserById(id,"_AView");
	}	

	/**
	 * This method refresh the Record.
	 * @param SecurityUsers (securityUsers)
	 * @return securityUsers
	 */
	@Override
	public SecurityUser refresh(SecurityUser securityUser) {
		logger.debug("Entering ");
		getSecurityUserDAO().refresh(securityUser);
		getSecurityUserDAO().initialize(securityUser);
		logger.debug("Leaving ");
		return securityUser;
	}

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
		SecurityUser securityUser = new SecurityUser();
		BeanUtils.copyProperties((SecurityUser) auditHeader.getModelData(), securityUser);

		String tranType="";

		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getSecurityUserDAO().delete(securityUser,"");

		} else {
			securityUser.setRoleCode("");
			securityUser.setNextRoleCode("");
			securityUser.setTaskId("");
			securityUser.setNextTaskId("");
			securityUser.setWorkflowId(0);

			if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				securityUser.setRecordType("");
				getSecurityUserDAO().save(securityUser,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				securityUser.setRecordType("");
				getSecurityUserDAO().update(securityUser,"");
			}
		}

		getSecurityUserDAO().delete(securityUser,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);

		getAuditHeaderDAO().addAudit(auditHeader);
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
		SecurityUser securityUser= (SecurityUser) auditHeader.getModelData();
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityUserDAO().delete(securityUser,"_TEMP");

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
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

		SecurityUser tempSecurityUser= null;
		if (securityUser.isWorkflow()){
			tempSecurityUser = getSecurityUserDAO().getSecurityUserById(securityUser.getId(), "_Temp");
		}
		SecurityUser befSecurityUser= getSecurityUserDAO().getSecurityUserById(securityUser.getId(), "");
		SecurityUser aBefSecurityUser= getSecurityUserDAO().getSecurityUserByLogin(securityUser.getUsrLogin(), "");
		SecurityUser oldSecurityUser= securityUser.getBefImage();

		String[] errParm= new String[4];
		errParm[0]=PennantJavaUtil.getLabel("label_UsrID");
		errParm[1]=String.valueOf(securityUser.getId());

		String[] userLoginExisted= new String[4];
		userLoginExisted[0]=PennantJavaUtil.getLabel("label_UsrLogin");
		userLoginExisted[1]=String.valueOf(securityUser.getUsrLogin());

		String[] parmUserIdAssigned= new String[10];
		parmUserIdAssigned[0]=PennantJavaUtil.getLabel("label_Roles");
		parmUserIdAssigned[1]=PennantJavaUtil.getLabel("label_User");

		if (securityUser.isNew()){ // for New record or new record into work flow

			if (!securityUser.isWorkflow()){// With out Work flow only new records  
				if (befSecurityUser !=null){	// Record Already Exists in the table with same userID then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

				}	
				if (aBefSecurityUser !=null){	// Record Already Exists in the table with same userLogin then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",userLoginExisted,null));

				}	
			}else{ // with work flow
				if (tempSecurityUser!=null ){ // if records already exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));

				}

				if (securityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befSecurityUser !=null){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

					}
				}else{ // if records not exists in the Main flow table
					if (befSecurityUser ==null){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));

					}
					if (aBefSecurityUser !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));

					}
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


		if(StringUtils.trimToEmpty(method).equals("delete")){
			/*check whether userId assigned to any Roles by calling SecurityUsersRolesDAO's getUserIdCount()*/
			int roleIdCount =getSecurityUserRolesDAO().getUserIdCount(securityUser.getUsrID());
			/*if roleId assigned for any user or group show error message*/
			if((roleIdCount>0)){
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"49002",parmUserIdAssigned,null));

			}	
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if(StringUtils.trimToEmpty(method).equals("doApprove")){
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
	public AuditHeader  changePassword(AuditHeader auditHeader){
		logger.debug("Entering ");
		SecurityUser securityUser= (SecurityUser) auditHeader.getAuditDetail().getModelData();
		securityUser.getBefImage().getLastMntOn();
		auditHeader = businessValidation(auditHeader,"changePassword");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		BigDecimal ExpDays;
		//if password is changed by user itself sets UsrAcExpDt is 30days after present date
		if(securityUser.getLastMntBy()==securityUser.getUsrID()){
			ExpDays=(BigDecimal)SystemParameterDetails.getSystemParameterValue("USR_EXPIRY_DAYS");
			securityUser.setUsrAcExpDt( DateUtility.addDays(new Date(System.currentTimeMillis()),ExpDays.intValue()));
			/*save the password for backup */
			getSecurityUserPasswordsDAO().save(securityUser);	
		}else{
			//if it is changed by admin sets UsrAcExpDt is one day before the present date
			securityUser.setUsrAcExpDt( DateUtility.addDays(new Date(System.currentTimeMillis()),-1));
		}

		getSecurityUserDAO().changePassword( securityUser);
		getAuditHeaderDAO().addAudit(auditHeader);	
		logger.debug("Leaving ");
		return auditHeader;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public void setSecurityUserRolesDAO(SecurityUserRolesDAO securityUserRolesDAO) {
		this.securityUserRolesDAO = securityUserRolesDAO;
	}

	public SecurityUserRolesDAO getSecurityUserRolesDAO() {
		return securityUserRolesDAO;
	}

}