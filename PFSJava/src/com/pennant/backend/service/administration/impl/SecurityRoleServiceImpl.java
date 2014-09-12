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
 * FileName    		:  SecurityRoleServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-08-2011      Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.administration.SecurityUserRolesDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>SecurityRole</b>.<br>
 * 
 */
public class SecurityRoleServiceImpl extends GenericService<SecurityRole> implements SecurityRoleService {
	private final static Logger logger = Logger.getLogger(SecurityRoleServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;

	private SecurityRoleDAO securityRoleDAO;
	private SecurityUserRolesDAO securityUserRolesDAO;
	private SecurityRoleGroupsDAO securityRoleGroupsDAO;

	@Override
	public SecurityRole getSecurityRole() {
		logger.debug("Entering ");
		return getSecurityRoleDAO().getSecurityRole();
	}
	/**
	 * @return the securityRole for New Record
	 */
	@Override
	public SecurityRole getNewSecurityRole() {
		logger.debug("Entering ");
		return getSecurityRoleDAO().getNewSecurityRole();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SecRoles/SecRoles_Temp 
	 * 			by using SecurityRoleDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SecurityRoleDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSecRoles by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType="";
		SecurityRole securityRole = (SecurityRole) auditHeader.getAuditDetail().getModelData();
		if (securityRole.isWorkflow()) {
			tableType="_TEMP";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}else{

			if (securityRole.isNew()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			}else{
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityRole.setRecordStatus("");
			securityRole.setRoleCode("");
			securityRole.setNextRoleCode("");
			securityRole.setTaskId("");
			securityRole.setNextTaskId("");
			securityRole.setRecordType("");
			securityRole.setWorkflowId(0);

		}

		if (securityRole.isNew()) {
			securityRole.setId(getSecurityRoleDAO().save(securityRole,tableType));
			auditHeader.setModelData(securityRole);
			auditHeader.setAuditReference(String.valueOf(securityRole.getRoleID()));
		}else{
			getSecurityRoleDAO().update(securityRole,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);	
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)   Checks whether Role assigned for any user by calling SecurityUsersRolesDAO's getAllAssignedRoleIds() 
	 * 3)   Checks whether any Group assigned to this  role by calling SecurityRoleGroupsDAO's getAllAssignedRoleIds() 
	 * 4)	delete Record for the DB table SecRoles by using SecurityRoleDAO's delete method with type as Blank 
	 * 5)	Audit the record in to AuditHeader and AdtSecRoles by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityRole secRoles = (SecurityRole)auditHeader.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}


		getSecurityRoleDAO().delete(secRoles,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getSecurityRoleById fetch the details by using SecurityRoleDAO's getSecurityRoleById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRole
	 */

	@Override
	public SecurityRole getSecurityRoleById(long id) {
		logger.debug("Entering ");
		return getSecurityRoleDAO().getSecurityRoleById(id,"_View");
	}
	/**
	 * getApprovedSecurityRoleById fetch the details by using SecurityRoleDAO's getSecurityRoleById method .
	 * with parameter id and type as blank. it fetches the approved records from the SecRoles.
	 * @param id (int)
	 * @return SecurityRole
	 */

	public SecurityRole getApprovedSecurityRoleById(long id) {
		logger.debug("Entering ");
		return getSecurityRoleDAO().getSecurityRoleById(id,"");
	}	

	/**
	 * This method refresh the Record.
	 * @param SecurityRole (securityRole)
	 * @return securityRole
	 */
	@Override
	public SecurityRole refresh(SecurityRole secRoles) {
		logger.debug("Entering ");
		getSecurityRoleDAO().refresh(secRoles);
		getSecurityRoleDAO().initialize(secRoles);
		logger.debug("Leaving ");
		return secRoles;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSecurityRoleDAO().delete with parameters securityRole,""
	 * 		b)  NEW		Add new record in to main table by using getSecurityRoleDAO().save with parameters securityRole,""
	 * 		c)  EDIT	Update record in the main table by using getSecurityRoleDAO().update with parameters securityRole,""
	 * 3)	Delete the record from the workFlow table by using getSecurityRoleDAO().delete with parameters securityRole,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSecRoles by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSecRoles by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityRole secRoles = new SecurityRole();
		BeanUtils.copyProperties((SecurityRole)auditHeader.getAuditDetail().getModelData(), secRoles);

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}


		if (secRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getSecurityRoleDAO().delete(secRoles,"");

		} else {
			secRoles.setRoleCode("");
			secRoles.setNextRoleCode("");
			secRoles.setTaskId("");
			secRoles.setNextTaskId("");
			secRoles.setWorkflowId(0);

			if (secRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				secRoles.setRecordType("");
				getSecurityRoleDAO().save(secRoles,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				secRoles.setRecordType("");
				getSecurityRoleDAO().update(secRoles,"");
			}
		}

		getSecurityRoleDAO().delete(secRoles,"_TEMP");
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
	 * 2)	Delete the record from the workFlow table by using getSecurityRoleDAO().delete with parameters securityRole,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSecRoles by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityRole secRoles= (SecurityRole) auditHeader.getModelData();

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}


		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityRoleDAO().delete(secRoles,"_TEMP");
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
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getSecurityRoleDAO().getErrorDetail with Error ID and language as parameters.
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
		SecurityRole secRoles= (SecurityRole) auditDetail.getModelData();
		SecurityRole tempSecurityRole= null;
		if (secRoles.isWorkflow()){
			tempSecurityRole = getSecurityRoleDAO().getSecurityRoleById(secRoles.getId(), "_Temp");
		}
		SecurityRole befSecurityRole= getSecurityRoleDAO().getSecurityRoleById(secRoles.getId(), "");
		SecurityRole aBefSecurityRole= getSecurityRoleDAO().getSecurityRoleByRoleCd(secRoles.getRoleCd(), "");
		SecurityRole oldSecurityRole= secRoles.getBefImage();


		String[] errParm= new String[4];
		errParm[0]=PennantJavaUtil.getLabel("label_RoleID");
		errParm[1]=String.valueOf(secRoles.getId());

		String[] parmRoleCdExisted= new String[10];
		parmRoleCdExisted[0]=PennantJavaUtil.getLabel("label_RoleCode");
		parmRoleCdExisted[1]=String.valueOf(secRoles.getRoleCd());

		String[] parmRoleIdAssigned= new String[10];
		parmRoleIdAssigned[0]=PennantJavaUtil.getLabel("label_Role");
		parmRoleIdAssigned[1]=PennantJavaUtil.getLabel("label_User_Or_Groups");



		if (secRoles.isNew()){ // for New record or new record into work flow

			if (!secRoles.isWorkflow()){// With out Work flow only new records  
				if (befSecurityRole !=null){	// Record Already Exists in the table with same roleID  then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					
				}	

				if (aBefSecurityRole!=null ){ // Record Already Exists in the table with same roleCode then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",parmRoleCdExisted,null));
					
				}
			}else{ // with work flow
				if (tempSecurityRole!=null ){ // if records already exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
			
				}

				if (secRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befSecurityRole !=null){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
						
					}
				}else{ // if records not exists in the Main flow table
					if (befSecurityRole ==null){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
						
					}
				}
				if (aBefSecurityRole!=null ){ // if records already exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",parmRoleCdExisted,null));
					
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!secRoles.isWorkflow()){	// With out Work flow for update and delete

				if (befSecurityRole ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
					
				}

				if (befSecurityRole!=null && oldSecurityRole!=null && !oldSecurityRole.getLastMntOn().equals(befSecurityRole.getLastMntOn())){
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));	
					}else{
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
					}
					
				}

			}else{

				if (tempSecurityRole==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					return auditDetail;
				}

				if (tempSecurityRole!=null && oldSecurityRole!=null && !oldSecurityRole.getLastMntOn().equals(tempSecurityRole.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));		
				}
			}
		}
		if(StringUtils.trimToEmpty(method).equals("delete")){
			/*check whether roleId assigned to any user by calling SecurityUsersRolesDAO's getRoleIdCount()*/
			int roleIdCount =getSecurityUserRolesDAO().getRoleIdCount(secRoles.getRoleID());
			/*check whether roleId assigned to any group by calling SecurityRoleGroupsDAO's getRoleIdCount()*/
			int aRoleIdCount=getSecurityRoleGroupsDAO().getRoleIdCount(secRoles.getRoleID());
			/*if roleId assigned for any user or group show error message*/
			if((roleIdCount>0) ||( 0< aRoleIdCount)){

				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"49001",parmRoleIdAssigned,null));

			}	
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if(StringUtils.trimToEmpty(method).equals("doApprove")){
			secRoles.setBefImage(befSecurityRole);	
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}

	public SecurityRoleGroupsDAO getSecurityRoleGroupsDAO() {
		return securityRoleGroupsDAO;
	}

	public void setSecurityUserRolesDAO(SecurityUserRolesDAO securityUserRolesDAO) {
		this.securityUserRolesDAO = securityUserRolesDAO;
	}

	public SecurityUserRolesDAO getSecurityUserRolesDAO() {
		return securityUserRolesDAO;
	}
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	public SecurityRoleDAO getSecurityRoleDAO() {
		return securityRoleDAO;
	}
	public void setSecurityRoleDAO(SecurityRoleDAO securityRoleDAO) {
		this.securityRoleDAO = securityRoleDAO;
	}
}