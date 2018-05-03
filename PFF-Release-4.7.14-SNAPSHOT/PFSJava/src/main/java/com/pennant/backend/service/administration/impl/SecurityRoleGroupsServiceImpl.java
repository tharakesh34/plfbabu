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
 * FileName    		: SecurityRoleGroupsServiceImpl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    : 03-08-2011														*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-08-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityRoleGroupsService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class SecurityRoleGroupsServiceImpl extends GenericService<SecurityRoleGroups> implements  SecurityRoleGroupsService{

	private SecurityRoleGroupsDAO   securityRoleGroupsDAO; 
	private SecurityGroupRightsDAO securityGroupRightsDAO;
	private AuditHeaderDAO         auditHeaderDAO;
	private NextidviewDAO 		   nextidviewDAO;


	private static final Logger logger = Logger.getLogger(SecurityRoleGroupsServiceImpl.class);

	public SecurityRoleGroupsServiceImpl() {
		super();
	}
	
	public AuditHeader save(AuditHeader auditHeader) {
		logger.debug("Entering ");
		auditHeader=businessValidation(auditHeader);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		long nextID=getNextidviewDAO().getSeqNumber("SeqSecRoleGroups");
		for (int i = 0; i < auditHeader.getAuditDetails().size(); i++) {
			SecurityRoleGroups aSecRoleGroup =(SecurityRoleGroups)auditHeader.getAuditDetails().get(i).getModelData();
			AuditDetail auditDetail = auditHeader.getAuditDetails().get(i);
			//for save	
			if(auditHeader.getAuditDetails().get(i).getAuditTranType().equals(PennantConstants.TRAN_ADD)){
				nextID=nextID+1;
				aSecRoleGroup.setId(nextID);
				aSecRoleGroup.setRecordStatus("");
				aSecRoleGroup.setRoleCode("");
				aSecRoleGroup.setNextRoleCode("");
				aSecRoleGroup.setTaskId("");
				aSecRoleGroup.setNextTaskId("");
				aSecRoleGroup.setRecordType("");
				aSecRoleGroup.setWorkflowId(0);
				getSecurityRoleGroupsDAO().save((SecurityRoleGroups)auditHeader.getAuditDetails().get(i).getModelData());
				auditDetail.setModelData(aSecRoleGroup);
				auditDetails.add(auditDetail);
			}
			//for delete		
			if(auditHeader.getAuditDetails().get(i).getAuditTranType().equals(PennantConstants.TRAN_DEL)){
				SecurityRoleGroups tempSecRoleGroup =getRoleGroupsByRoleAndGrpId(aSecRoleGroup.getRoleID(), aSecRoleGroup.getGrpID());
				aSecRoleGroup.setRoleGrpID(tempSecRoleGroup.getRoleGrpID());
				getSecurityRoleGroupsDAO(). delete(aSecRoleGroup);	
				auditDetails.add(auditDetail);
			}
		}
		getNextidviewDAO().setSeqNumber("SeqSecRoleGroups", nextID);
		auditHeader.setAuditModule("SecurityRoleGroups");
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		return auditHeader;
	}
	/**
	 * This method validates list of AuditDetails by calling validation() method and
	 *  adds error details of each audit detail into auditHeader
	 * @param auditHeader's ErrorList and gets nextProcess by calling nextProcess()
	 * @return
	 */
	public AuditHeader businessValidation(AuditHeader auditHeader){
		logger.debug("Entering ");

		for(int i=0;i<auditHeader.getAuditDetails().size();i++){
			AuditDetail auditDetail=validation(auditHeader.getAuditDetails().get(i),auditHeader.getUsrLanguage(), "");
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
		}
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getSecurityRoleGroupsDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail  validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering ");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		String[] errPrmRecordExisted= new String[4];
		/*if already record exist with same RoleeId and GrpId add error detail*/
		SecurityRoleGroups rolesGroups=(SecurityRoleGroups) auditDetail.getModelData();
		errPrmRecordExisted[0]=rolesGroups.getLovDescRoleCode();
		if(StringUtils.equals(auditDetail.getAuditTranType(),PennantConstants.TRAN_ADD)){
			rolesGroups =getSecurityRoleGroupsDAO()
			.getRoleGroupsByRoleAndGrpId(rolesGroups.getRoleID()
					, rolesGroups.getGrpID());
			if(rolesGroups!=null){
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errPrmRecordExisted,null));
			}		
		}
		/*if already record delete with same RoleeId and GrpId add error detail*/
		if(StringUtils.equals(auditDetail.getAuditTranType(),PennantConstants.TRAN_DEL)){
			rolesGroups =getSecurityRoleGroupsDAO()
			.getRoleGroupsByRoleAndGrpId(rolesGroups.getRoleID()
					, rolesGroups.getGrpID());
			if(rolesGroups==null){
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errPrmRecordExisted,null));
			}		
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));	
		return auditDetail;
	}

	/**
	 * This method fetches {@link List} of {@link SecurityGroup} by calling SecurityRoleGroupsDAO's 
	 * getGroupsByRoleId() method
	 * @param roleId (long)
	 * @param isAssigned (boolean)
	 * @return {@link List} of {@link SecurityGroup}
	 */
	@Override
	public List<SecurityGroup> getGroupsByRoleId(long roleId,boolean isAssigned) {
		return getSecurityRoleGroupsDAO().getGroupsByRoleId(roleId,isAssigned);
	}
	/**
	 * This method fetches {@link List} of {@link SecurityGroupRights} by GrpId by calling SecurityGroupRightsDAO's
	 *  getSecurityGroupRightsByGrpId()
	 *  @return {@link List} of {@link SecurityGroupRights}
	 */
	public List<SecurityGroupRights> getSecurityGroupRightsByGrpId(SecurityGroup securityGroup ){
		return getSecurityGroupRightsDAO().getSecurityGroupRightsByGrpId(securityGroup);
	}
	/**
	 * This method fetches SecurityRoleGroups record with "roleId and groupId" condition
	 */
	public SecurityRoleGroups getRoleGroupsByRoleAndGrpId(long roleID,long groupId){

		return getSecurityRoleGroupsDAO().getRoleGroupsByRoleAndGrpId(roleID, groupId);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public SecurityRoleGroupsDAO getSecurityRoleGroupsDAO() {
		return securityRoleGroupsDAO;
	}

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public SecurityGroupRightsDAO getSecurityGroupRightsDAO() {
		return securityGroupRightsDAO;
	}
	public void setSecurityGroupRightsDAO(
			SecurityGroupRightsDAO securityGroupRightsDAO) {
		this.securityGroupRightsDAO = securityGroupRightsDAO;
	}

	public void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		this.nextidviewDAO = nextidviewDAO;
	}

	public NextidviewDAO getNextidviewDAO() {
		return nextidviewDAO;
	}
}
