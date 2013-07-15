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
 * FileName    		:  SecurityRightServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-07-2011    														*
 *                                                                  						*
 * Modified Date    :  2-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 2-08-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityRightService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>SecurityRight</b>.<br>
 * 
 */
public class SecurityRightServiceImpl  extends GenericService<SecurityRight> implements SecurityRightService {
	private final static Logger logger = Logger.getLogger(SecurityRightServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private SecurityRightDAO securityRightDAO;
	private SecurityGroupRightsDAO securityGroupRightsDAO;
	@Override
	/**
	 * @return the securityRight
	 */
	public SecurityRight getSecurityRight() {
		logger.debug("Entering ");
		return getSecurityRightDAO().getSecurityRight();
	}
	/**
	 * @return the securityRight for New Record
	 */
	@Override
	public SecurityRight getNewSecurityRight() {
		logger.debug("Leaving ");
		return getSecurityRightDAO().getNewSecurityRight();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SecRights/SecRights_Temp 
	 * 			by using SecurityRightDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SecurityRightDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSecRights by using auditHeaderDAO.addAudit(auditHeader)
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
		SecurityRight securityRight = (SecurityRight) auditHeader.getAuditDetail().getModelData();

		if (securityRight.isWorkflow()) {
			tableType="_TEMP";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}else{

			if (securityRight.isNew()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			}else{
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityRight.setRecordStatus("");
			securityRight.setRoleCode("");
			securityRight.setNextRoleCode("");
			securityRight.setTaskId("");
			securityRight.setNextTaskId("");
			securityRight.setRecordType("");
			securityRight.setWorkflowId(0);
		}

		if (securityRight.isNew()) {
			securityRight.setId(getSecurityRightDAO().save(securityRight,tableType));
			auditHeader.getAuditDetail().setModelData(securityRight);
			auditHeader.setAuditReference(String.valueOf(securityRight.getId()));
		}else{
			getSecurityRightDAO().update(securityRight,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);		
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SecRights by using SecurityRightDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSecRights by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SecurityRight securityRight = (SecurityRight) auditHeader.getAuditDetail().getModelData();
		getSecurityRightDAO().delete(securityRight,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getSecurityRightById fetch the details by using SecurityRightDAO's getSecurityRightById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRight
	 */

	@Override
	public SecurityRight getSecurityRightById(long id) {
		logger.debug("Entering ");
		return getSecurityRightDAO().getSecurityRightByID(id,"_View");
	}
	/**
	 * getApprovedSecurityRightById fetch the details by using SecurityRightDAO's getSecurityRightById method .
	 * with parameter id and type as blank. it fetches the approved records from the SecRights.
	 * @param id (int)
	 * @return SecurityRight
	 */

	public SecurityRight getApprovedSecurityRightById(long id) {
		logger.debug("Entering ");
		return getSecurityRightDAO().getSecurityRightByID(id,"");
	}	

	/**
	 * This method refresh the Record.
	 * @param SecurityRight (securityRight)
	 * @return securityRight
	 */
	@Override
	public SecurityRight refresh(SecurityRight securityRight) {
		logger.debug("Entering ");
		getSecurityRightDAO().refresh(securityRight);
		getSecurityRightDAO().initialize(securityRight);
		logger.debug("Leaving ");
		return securityRight;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSecurityRightDAO().delete with parameters securityRight,""
	 * 		b)  NEW		Add new record in to main table by using getSecurityRightDAO().save with parameters securityRight,""
	 * 		c)  EDIT	Update record in the main table by using getSecurityRightDAO().update with parameters securityRight,""
	 * 3)	Delete the record from the workFlow table by using getSecurityRightDAO().delete with parameters securityRight,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSecRights by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSecRights by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SecurityRight securityRight = new SecurityRight();
		BeanUtils.copyProperties((SecurityRight) auditHeader.getModelData(), securityRight);
		if (securityRight.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getSecurityRightDAO().delete(securityRight,"");

		} else {
			securityRight.setRoleCode("");
			securityRight.setNextRoleCode("");
			securityRight.setTaskId("");
			securityRight.setNextTaskId("");
			securityRight.setWorkflowId(0);

			if (securityRight.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				securityRight.setRecordType("");
				getSecurityRightDAO().save(securityRight,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				securityRight.setRecordType("");
				getSecurityRightDAO().update(securityRight,"");
			}
		}

		getSecurityRightDAO().delete(securityRight,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(securityRight);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSecurityRightDAO().delete with parameters securityRight,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSecRights by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SecurityRight securityRight= (SecurityRight) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityRightDAO().delete(securityRight,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getSecurityRightDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
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
	 * getSecurityRightDAO().getErrorDetail with Error ID and language as parameters.
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
		SecurityRight securityRight= (SecurityRight) auditDetail.getModelData();
		SecurityRight tempSecurityRight= null;
		if (securityRight.isWorkflow()){
			tempSecurityRight = getSecurityRightDAO().getSecurityRightByID(securityRight.getId(), "_Temp");
		}
		SecurityRight befSecurityRight= getSecurityRightDAO().getSecurityRightByID(securityRight.getId(), "");
		SecurityRight aBefSecurityRight = getSecurityRightDAO().getSecurityRightByRightName(securityRight.getRightName(), "");
		SecurityRight old_SecurityRight= securityRight.getBefImage();

		String[] parm= new String[4];
		parm[0]=PennantJavaUtil.getLabel("label_RightID");
		parm[1]=String.valueOf(securityRight.getId());

		String[] parmRtNameExisted= new String[10];
		parmRtNameExisted[0]=PennantJavaUtil.getLabel("label_RightName");
		parmRtNameExisted[1]=String.valueOf(securityRight.getRightName());

		String[] parmRightAssigned= new String[10];
		parmRightAssigned[0]=PennantJavaUtil.getLabel("label_RightID");
		parmRightAssigned[1]=PennantJavaUtil.getLabel("label_Groups");


		if (securityRight.isNew()){ // for New record or new record into work flow

			if (!securityRight.isWorkflow()){// With out Work flow only new records  
				if (befSecurityRight !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",parm,null));
				}
				if(aBefSecurityRight != null){// if records already exists in the table with same right name then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",parmRtNameExisted,null));		
				}

			}else{
				// with work flow
				if (tempSecurityRight!=null ){ // if records already exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",parm,null));

				}

				if (securityRight.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befSecurityRight !=null){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",parm,null));

					}
				}else{ // if records not exists in the Main flow table
					if (befSecurityRight ==null){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",parm,null));

					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!securityRight.isWorkflow()){	// With out Work flow for update and delete

				if (befSecurityRight ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",parm,null));

				}

				if (befSecurityRight!=null && old_SecurityRight!=null && !old_SecurityRight.getLastMntOn().equals(befSecurityRight.getLastMntOn())){
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",parm,null));	
					}else{
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",parm,null));
					}

				}

			}else{

				if (tempSecurityRight==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",parm,null));

				}

				if (tempSecurityRight !=null && old_SecurityRight!=null && !old_SecurityRight.getLastMntOn().equals(tempSecurityRight.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",parm,null));

				}

			}
		}	
		if(StringUtils.trimToEmpty(method).equals("delete")){
			/*Check whether rightId is assigned with any Group by calling SecurityGroupRightsDAO's getRightIdCount()*/
			int rightIdCount=getSecurityGroupRightsDAO().getRightIdCount(securityRight.getId());
			if(rightIdCount>0){
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"49001",parmRightAssigned,null));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if(StringUtils.trimToEmpty(method).equals("doApprove")){
			securityRight.setBefImage(befSecurityRight);	
		}
		logger.debug("Leaving ");
		return auditDetail;
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
	public SecurityRightDAO getSecurityRightDAO() {
		return securityRightDAO;
	}
	public void setSecurityRightDAO(SecurityRightDAO securityRightDAO) {
		this.securityRightDAO = securityRightDAO;
	}
	public void setSecurityGroupRightsDAO(SecurityGroupRightsDAO securityGroupRightsDAO) {
		this.securityGroupRightsDAO = securityGroupRightsDAO;
	}
	public SecurityGroupRightsDAO getSecurityGroupRightsDAO() {
		return securityGroupRightsDAO;
	}
}