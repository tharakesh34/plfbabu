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
 * FileName    		: SecurityGroupRightsServiceImpl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    :  26-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityGroupRightsService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class SecurityGroupRightsServiceImpl extends GenericService<SecurityGroupRights> implements SecurityGroupRightsService{
	private static Logger logger = Logger.getLogger(SecurityGroupRightsServiceImpl.class);
	
	private SecurityGroupRightsDAO securityGroupRightsDAO;
	private AuditHeaderDAO auditHeaderDAO;

	public SecurityGroupRightsServiceImpl() {
		super();
	}
	
	public SecurityGroupRights getSecurityGroupRights(){
		return getSecurityGroupRightsDAO().getSecurityGroupRights();
	}


	/**

	 *This method do the following
	 * 1)Gets the AuditDetails list by calling businessValidation() method 
	 * 2)a)it checks for each AuditDetail  if "AuditTranType" is PennantConstants.TRAN_ADD it saves the record by calling SecurityGroupRightsDAO's
	 *   save() method
	 *   b)if "AuditTranType" is PennantConstants.TRAN_DEL  it Deletes the record by calling SecurityGroupRightsDAO's
	 *   delete() method

	 **/
	public AuditHeader save(AuditHeader auditHeader){
		logger.debug("Entering ");

		auditHeader =businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < auditHeader.getAuditDetails().size(); i++) {

			SecurityGroupRights aSecGroupRights=(SecurityGroupRights)auditHeader.getAuditDetails().get(i).getModelData();
			//for save	
			AuditDetail auditDetail = auditHeader.getAuditDetails().get(i);

			if(StringUtils.equals(auditDetail.getAuditTranType()
					,PennantConstants.TRAN_ADD)){
				aSecGroupRights.setId(securityGroupRightsDAO.getNextValue());
				aSecGroupRights.setRecordStatus("");
				aSecGroupRights.setRoleCode("");
				aSecGroupRights.setNextRoleCode("");
				aSecGroupRights.setTaskId("");
				aSecGroupRights.setNextTaskId("");
				aSecGroupRights.setRecordType("");
				aSecGroupRights.setWorkflowId(0);
				getSecurityGroupRightsDAO().save(aSecGroupRights);
				auditDetail.setModelData(aSecGroupRights);
				auditDetails.add(auditDetail);
			}

			//for delete
			if(StringUtils.equals(auditDetail.getAuditTranType()
					,PennantConstants.TRAN_DEL)){
				SecurityGroupRights tempSecGroupRights=getGroupRightsByGrpAndRightIds(aSecGroupRights.getGrpID(),aSecGroupRights.getRightID());
				aSecGroupRights.setGrpRightID(tempSecGroupRights.getGrpRightID());
				getSecurityGroupRightsDAO(). delete(aSecGroupRights);
				auditDetails.add(auditDetail);
			}
		}
		
		auditHeader.setAuditModule("SecurityGroupRights");
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
	public AuditHeader businessValidation(AuditHeader auditHeader){
		logger.debug("Entering");
		/* Check if record is already modified */
			for(int i=0;i<auditHeader.getAuditDetails().size();i++){
				AuditDetail auditDetail=validation(auditHeader.getAuditDetails().get(i), "", "");
				auditHeader.setErrorList(auditDetail.getErrorDetails());	
			}
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail  validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		String[] errPrmRecordExisted= new String[4];
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		SecurityGroupRights asecGrouprights=(SecurityGroupRights) auditDetail.getModelData();
		errPrmRecordExisted[0]=asecGrouprights.getLovDescGrpCode();
		//check if record exist with same GrpID and RightId 
		if(StringUtils.equals(auditDetail.getAuditTranType(),PennantConstants.TRAN_ADD)){
			{

				asecGrouprights =getSecurityGroupRightsDAO().getGroupRightsByGrpAndRightIds(asecGrouprights.getGrpID()
						, asecGrouprights.getRightID());
				if(asecGrouprights!=null){
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errPrmRecordExisted,null));
				}		
			}
		}
		if(StringUtils.equals(auditDetail.getAuditTranType(),PennantConstants.TRAN_DEL)){
			{
				asecGrouprights =getSecurityGroupRightsDAO().getGroupRightsByGrpAndRightIds(asecGrouprights.getGrpID()
						, asecGrouprights.getRightID());
				if(asecGrouprights==null){
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errPrmRecordExisted,null));
				}		
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		return auditDetail;
	}

	/**
	 * This method fetches {@link List} of {@link SecurityRight} records by calling SecurityGroupRightsDAO's getRightsByGroupId()
	 * @param grpID(long)
	 * @param isAssigned(boolean)
	 * @return  {@link List} of {@link SecurityRight}
	 */
	public List<SecurityRight> getRightsByGroupId(long grpID,boolean isAssigned){
		return getSecurityGroupRightsDAO().getRightsByGroupId(grpID, isAssigned);
	}
	/**
	 * This method fetches SecurityGroupRights record by calling with "grpId and RightId"s condition
	 * SecurityGroupRightsDAO's getGroupRightsByGrpAndRightIds()method 
	 * @param grpId(long)
	 * @param rightId(long)
	 * @return SecurityGroupRights
	 */
	@Override
	public SecurityGroupRights getGroupRightsByGrpAndRightIds(long grpId,long rightId) {
		return getSecurityGroupRightsDAO().getGroupRightsByGrpAndRightIds(grpId, rightId);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public SecurityGroupRightsDAO getSecurityGroupRightsDAO() {
		return securityGroupRightsDAO;
	}

	public void setSecurityGroupRightsDAO(
			SecurityGroupRightsDAO securityGroupRightsDAO) {
		this.securityGroupRightsDAO = securityGroupRightsDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
}
