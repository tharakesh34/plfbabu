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
 * FileName    		:  SecurityOperationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.dao.administration.SecurityOperationRolesDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityOperationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>SecurityOperation</b>.<br>
 * 
 */
public class SecurityOperationServiceImpl extends GenericService<SecurityOperation> implements SecurityOperationService {
	private static Logger logger = Logger.getLogger(SecurityOperationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityOperationDAO securityOperationDAO;
	private SecurityOperationRolesDAO securityOperationRolesDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SecOperations/SecOperations_Temp 
	 * 			by using SecurityOperationDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SecurityOperationDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSecGroups by using auditHeaderDAO.addAudit(auditHeader)
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
		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();
		if (securityOperation.isWorkflow()) {
			tableType="_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}else{

			if (securityOperation.isNew()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			}else{
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityOperation.setRecordStatus("");
			securityOperation.setRoleCode("");
			securityOperation.setNextRoleCode("");
			securityOperation.setTaskId("");
			securityOperation.setNextTaskId("");
			securityOperation.setRecordType("");
			securityOperation.setWorkflowId(0);

		}

		if (securityOperation.isNew()) {
			securityOperation.setId(getSecurityOperationDAO().save(securityOperation,tableType));
			auditHeader.setModelData(securityOperation);
			auditHeader.setAuditReference(String.valueOf(securityOperation.getOprID()));
		}else{
			getSecurityOperationDAO().update(securityOperation,tableType);
		}

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
	 * getSecurityOperationDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage
			,String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		SecurityOperation securityOperation = (SecurityOperation) auditDetail.getModelData();
		SecurityOperation tempSecurityOperation = null;

		if (securityOperation.isWorkflow()) {
			tempSecurityOperation = getSecurityOperationDAO().getSecurityOperationByCode(securityOperation.getOprCode(),"_Temp");
		}

		SecurityOperation befSecurityOperation = getSecurityOperationDAO().getSecurityOperationByCode(securityOperation.getOprCode(), "");
		SecurityOperation oldSecurityOperation = securityOperation.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = securityOperation.getOprCode();
		errParm[0] = PennantJavaUtil.getLabel("label_OprCode") + ":"
		+ valueParm[0];

		if (securityOperation.isNew()) { // for New record or new record into work flow

			if (!securityOperation.isWorkflow()) {// With out Work flow only new records
				if (befSecurityOperation != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (securityOperation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSecurityOperation != null || tempSecurityOperation != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befSecurityOperation == null || tempSecurityOperation != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!securityOperation.isWorkflow()) { // With out Work flow for update and delete

				if (befSecurityOperation == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {

					if (oldSecurityOperation != null
							&& !oldSecurityOperation.getLastMntOn().equals(befSecurityOperation.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}

			} else {

				if (tempSecurityOperation == null) { // if records not exists in the Work
					// flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
				if (tempSecurityOperation != null
						&& oldSecurityOperation != null
						&& !oldSecurityOperation.getLastMntOn().equals(tempSecurityOperation.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}
		if ((StringUtils.equals(securityOperation.getRecordStatus(),PennantConstants.RCD_STATUS_SUBMITTED) || "Save".equals(securityOperation.getUserAction()))
				&& StringUtils.equals(securityOperation.getRecordType(),PennantConstants.RECORD_TYPE_DEL)) {
			
			if (getSecurityOperationRolesDAO().getOprById(securityOperation.getOprID(), "_View") > 0) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
			} else {
				if (getSecurityUserOperationsDAO().getOprById(securityOperation.getOprID(), "_View") > 0) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006",errParm, null));
				}
			}			
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !securityOperation.isWorkflow()) {
			auditDetail.setBefImage(befSecurityOperation);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	/**
	 * getSecurityOperationById fetch the details by using SecurityOperationDAO's getSecurityOperationById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityOperation
	 */
	@Override
	public SecurityOperation getSecurityOperationById(long id) {
		return getSecurityOperationDAO().getSecurityOperationById(id, "_View");
	}
	/**
	 * getApprovedSecurityOperationById fetch the details by using SecurityOperationDAO's getSecurityoperationById method .
	 * with parameter id and type as blank. it fetches the approved records from the SecOperations.
	 * @param id (int)
	 * @return SecurityOperation
	 */
	@Override
	public SecurityOperation getApprovedSecurityOperationById(long id) {
		return getSecurityOperationDAO().getSecurityOperationById(id, "_AView");
	}
	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SecOperations by using SecurityOperationDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSecOperations by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();

		getSecurityOperationDAO().delete(securityOperation, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSecurityOperaionDAO().delete with parameters securityOperation,""
	 * 		b)  NEW		Add new record in to main table by using getSecurityOperaionDAO().save with parameters securityOperation,""
	 * 		c)  EDIT	Update record in the main table by using getSecurityOperaionDAO().update with parameters securityOperation,""
	 * 3)	Delete the record from the workFlow table by using getSecurityOperaionDAO().delete with parameters securityOperation,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtSecOperations by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtSecOperations by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SecurityOperation securityOperation = new SecurityOperation();
		BeanUtils.copyProperties((SecurityOperation) auditHeader.getAuditDetail().getModelData(), securityOperation);

		if (securityOperation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSecurityOperationDAO().delete(securityOperation, "");

		} else {
			securityOperation.setRoleCode("");
			securityOperation.setNextRoleCode("");
			securityOperation.setTaskId("");
			securityOperation.setNextTaskId("");
			securityOperation.setWorkflowId(0);

			if (securityOperation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				securityOperation.setRecordType("");
				getSecurityOperationDAO().save(securityOperation, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				securityOperation.setRecordType("");
				getSecurityOperationDAO().update(securityOperation, "");
			}
		}

		getSecurityOperationDAO().delete(securityOperation, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(securityOperation);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}
	
	
	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSecurityOperationDAO().delete 
	 *      with parameters securityOperation,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSecOperations by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SecurityOperation securityOperation = (SecurityOperation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityOperationDAO().delete(securityOperation, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	//This method is used to get the operation roles  from _view

	@Override
	public SecurityOperation getSecurityOperationRolesById(long id) {
		logger.debug("Entering ");
		return getSecurityOperationRoleById(id,"_View", true);
	}
	//This method is used to get the Approved operation roles  from _Aview
	@Override
	public SecurityOperation getApprovedSecurityOperationRolesById(long id) {
		logger.debug("Entering ");
		return getSecurityOperationRoleById(id,"_AView", true);
	}	
	//This method is used to get the operation roles  by id from _view
	@Override
	public SecurityOperation getSecurityOperationRoleById(long id,String type, boolean getRoles) {
		logger.debug("Entering ");
		SecurityOperation securityOperation =getSecurityOperationDAO().getSecurityOperationById(id,type);
		if(securityOperation!=null && getRoles){
			if("_RView".equals(type)){
				type = "_View";
			}
			securityOperation.setSecurityOperationRolesList(getSecurityOperationRolesDAO().getSecOperationRolesByOprID(securityOperation,type));
		}
		
		logger.debug("Leaving ");
		return securityOperation;
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
	
	public SecurityOperationDAO getSecurityOperationDAO() {
		return securityOperationDAO;
	}
	public void setSecurityOperationDAO(SecurityOperationDAO securityOperationDAO) {
		this.securityOperationDAO = securityOperationDAO;
	}
	public SecurityOperationRolesDAO getSecurityOperationRolesDAO() {
		return securityOperationRolesDAO;
	}
	public void setSecurityOperationRolesDAO(
			SecurityOperationRolesDAO securityOperationRolesDAO) {
		this.securityOperationRolesDAO = securityOperationRolesDAO;
	}
	public SecurityUserOperationsDAO getSecurityUserOperationsDAO() {
		return securityUserOperationsDAO;
	}
	public void setSecurityUserOperationsDAO(
			SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

}