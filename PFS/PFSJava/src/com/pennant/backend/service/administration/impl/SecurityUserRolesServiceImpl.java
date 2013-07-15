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
 * FileName    		:  SecurityUserRolesServiceImpl.java                                                   * 	  
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.administration.SecurityUserRolesDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserRolesService;
import com.pennant.backend.util.PennantConstants;

public class SecurityUserRolesServiceImpl extends GenericService<SecurityUserRoles> implements SecurityUserRolesService {

	private final static Logger logger = Logger.getLogger(SecurityUserRolesServiceImpl.class);

	private  SecurityUserRolesDAO    securityUserRolesDAO;
	private  SecurityRoleGroupsDAO    securityRoleGroupsDAO;
	private  SecurityGroupRightsDAO  securityGroupRightsDAO;
	private  AuditHeaderDAO auditHeaderDAO;
	private  NextidviewDAO 	nextidviewDAO;

	public SecurityUserRoles getSecurityUserRoles(){
		return getSecurityUserRolesDAO().getSecurityUserRoles();

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
	public AuditHeader save(AuditHeader auditHeader){
		logger.debug("Entering ");
		auditHeader =businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		long nextID=getNextidviewDAO().getSeqNumber("SeqSecUserRoles");

		for (int i = 0; i < auditHeader.getAuditDetails().size(); i++) {
			SecurityUserRoles aSecUserRoles =  (SecurityUserRoles)auditHeader.getAuditDetails().get(i).getModelData();
			//if audit transaction type is ADD Save the record 
			if(StringUtils.equals(auditHeader.getAuditDetails().get(i).getAuditTranType()
					,PennantConstants.TRAN_ADD)){
				nextID=nextID+1;
				aSecUserRoles.setId(nextID);
				aSecUserRoles.setRecordStatus("");
				aSecUserRoles.setRoleCode("");
				aSecUserRoles.setNextRoleCode("");
				aSecUserRoles.setTaskId("");
				aSecUserRoles.setNextTaskId("");
				aSecUserRoles.setRecordType("");
				aSecUserRoles.setWorkflowId(0);
				getSecurityUserRolesDAO(). save(aSecUserRoles);
			}
			//if audit transaction type is DEL  Delete the record 
			if(StringUtils.equals(auditHeader.getAuditDetails().get(i).getAuditTranType()
					,PennantConstants.TRAN_DEL)){
				SecurityUserRoles tempSecUserRoles=getSecurityUserRolesDAO()
				              .getUserRolesByUsrAndRoleIds(aSecUserRoles.getUsrID(), aSecUserRoles.getRoleID());
				aSecUserRoles.setId(tempSecUserRoles.getUsrRoleID());
				getSecurityUserRolesDAO(). delete(aSecUserRoles);
			}
		}
		getNextidviewDAO().setSeqNumber("SeqSecUserRoles", nextID);
		auditHeader.setAuditModule("SecurityUserRoles");
		getAuditHeaderDAO().addAudit(auditHeader);
		return auditHeader;
	}
	/**
	 * This method do the following 
	 * 1)get the AuditDetails list by calling getAuditDetailsList().
	 * 2)It validate each AuditDetail in the AuditDetails list by calling validate method
	 * @param auditHeader
	 * @return auditHeader
	 */
	public AuditHeader businessValidation(AuditHeader auditHeader){
		logger.debug("Entering");

		for(int i=0;i<auditHeader.getAuditDetails().size();i++){
			AuditDetail auditDetail=new AuditDetail();
			auditDetail=validation(auditHeader.getAuditDetails().get(i), auditHeader.getUsrLanguage(), "");
			auditHeader.setErrorList((auditDetail.getErrorDetails()));	
			auditHeader=nextProcess(auditHeader);
		}
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		SecurityUserRoles secUserRoles=(SecurityUserRoles)auditDetail.getModelData();
		String[] errPrm=new String[4];
		errPrm[0]=String.valueOf(secUserRoles.getLovDescUserLogin());	
		if(StringUtils.equals(auditDetail.getAuditTranType(), PennantConstants.TRAN_ADD)){
			secUserRoles=getSecurityUserRolesDAO()
			.getUserRolesByUsrAndRoleIds(secUserRoles.getUsrID(), secUserRoles.getRoleID());
			/*check for record already exist with same usrID and RoleId if exists set error detail */
			if(secUserRoles !=null){
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errPrm,null));	
			}	   
		}	
		if(StringUtils.equals(auditDetail.getAuditTranType(), PennantConstants.TRAN_DEL)){
			secUserRoles=getSecurityUserRolesDAO()
			.getUserRolesByUsrAndRoleIds(secUserRoles.getUsrID(), secUserRoles.getRoleID());
			/*check for record already exist with same usrID and RoleId if exists set error detail */
			if(secUserRoles ==null){
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errPrm,null));	
			}	   
		}	
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		return auditDetail;		 
	}	

	/**
	 * This method fetches  List< SecurityRoleGroups > with "RoleId" condition by calling 
	 * <code>SecurityRoleGroupsDAO</code>'s <code>getSecRoleGroupsByRoleID()</code>
	 * @return List<SecurityRoleGroups>
	 */
	public List<SecurityRoleGroups> getRoleGroupsByRoleId(SecurityRole secRoles) {
		return  getSecurityRoleGroupsDAO().getSecRoleGroupsByRoleID(secRoles);
	}
	/**
	 * This method fetches List< SecurityGroupRights > with "GrpId" condition by calling SecurityGroupRightsDAO's
	 * getSecurityGroupRightsByGrpId()
	 * @param securityGroup (SecurityGroup)
	 * @return  List<SecurityGroupRights> 
	 */
	public List<SecurityGroupRights> getGroupRightsByGrpId(SecurityGroup securityGroup){
		return  getSecurityGroupRightsDAO().getSecurityGroupRightsByGrpId(securityGroup);	

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
	public  SecurityUserRoles getUserRolesByUsrAndRoleIds(long userId,long RoleId){
		return getSecurityUserRolesDAO().getUserRolesByUsrAndRoleIds(userId,RoleId);

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
}
