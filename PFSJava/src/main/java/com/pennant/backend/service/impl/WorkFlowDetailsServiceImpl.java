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
 * FileName    		:  WorkFlowDetailsServiceImpl.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
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

package com.pennant.backend.service.impl;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.ErrorDetailsDAO;
import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;


public class WorkFlowDetailsServiceImpl extends GenericService<WorkFlowDetails> implements WorkFlowDetailsService {
	
	private static Logger logger = Logger.getLogger(WorkFlowDetailsServiceImpl.class);

	private WorkFlowDetailsDAO workFlowDetailsDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private ErrorDetailsDAO errorDetailsDAO;

	public WorkFlowDetailsServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public WorkFlowDetailsDAO getWorkFlowDetailsDAO() {
		return workFlowDetailsDAO;
	}
	public void setWorkFlowDetailsDAO(WorkFlowDetailsDAO workFlowDetailsDAO) {
		this.workFlowDetailsDAO = workFlowDetailsDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public ErrorDetailsDAO getErrorDetailsDAO() {
		return errorDetailsDAO;
	}
	public void setErrorDetailsDAO(ErrorDetailsDAO errorDetailsDAO) {
		this.errorDetailsDAO = errorDetailsDAO;
	}
	
	
	public WorkFlowDetails getWorkFlowDetailsByFlowType(String workFlowType) {
		return getWorkFlowDetailsDAO().getWorkFlowDetailsByFlowType(workFlowType);
	}
	
	public WorkFlowDetails getWorkFlowDetailsByID(long id) {
		return getWorkFlowDetailsDAO().getWorkFlowDetailsByID(id);	
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTEMailTypes/BMTEMailTypes_Temp by using WorkFlowDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using WorkFlowDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtWorkFlowDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
		long workFlowId=0;
		
		WorkFlowDetails workFlowDetails= (WorkFlowDetails)auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()){
			return auditHeader;
		}
		
		if (workFlowDetails.isNew()){
			workFlowId = getWorkFlowDetailsDAO().save(workFlowDetails);
			workFlowDetails.setId(workFlowId);
			auditHeader.setModelData(workFlowDetails);
			auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
		}else{

			WorkFlowDetails flowDetails=new WorkFlowDetails();
			BeanUtils.copyProperties(workFlowDetails.getBefImage(), flowDetails);
			flowDetails.setVersion(flowDetails.getVersion()+1);
			flowDetails.setWorkFlowActive(false);
			getWorkFlowDetailsDAO().update(flowDetails);
			
			workFlowId = getWorkFlowDetailsDAO().save(workFlowDetails);
			workFlowDetails.setId(workFlowId);
			auditHeader.setModelData(workFlowDetails);
			auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
		}
		
		getAuditHeaderDAO().addAudit(auditHeader);
		WorkFlowUtil.loadWorkFlowData(workFlowDetails);
		return auditHeader;
	}
	
	public List<WorkFlowDetails> getActiveWorkFlowDetails(){
		return getWorkFlowDetailsDAO().getActiveWorkFlowDetails();
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getEMailTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
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
		WorkFlowDetails workFlowDetails= (WorkFlowDetails) auditDetail.getModelData();
		WorkFlowDetails flowDetails = getWorkFlowDetailsDAO()
				.getWorkFlowDetailsByFlowType(workFlowDetails.getWorkFlowType());
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = workFlowDetails.getWorkFlowType();
		errParm[0] = PennantJavaUtil.getLabel("label_WorkFlowType") + ":"
				+ valueParm[0];
		

		if (workFlowDetails.isNew()){
			if (flowDetails!=null){
				auditDetail.setErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41001",errParm, null));		
			}
		}else{
			if (flowDetails==null){
				auditDetail.setErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41002",errParm, null));		
			}
			
		}
		
		return auditDetail;
	}

	@Override
	public long getWorkFlowDetailsCountByID(long id) {
		return getWorkFlowDetailsDAO().getWorkFlowDetailsCountByID(id);
	}

	@Override
	public List<ErrorDetails> doValidations(WorkFlowDetails workFlowDetails, String flag) {
		List<ErrorDetails> errDetails = new ArrayList<ErrorDetails>();
		Long workflowId = workFlowDetails.getWorkflowId();
		if (workflowId == 0 && !"create".equals(flag)) {// empty check for workflowId in get
			String[] valueParm = new String[1];
			valueParm[0] = "WorkflowDesignId";
			ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm), "EN");
			errDetails.add(errorDetail);
		}
		if ((workflowId != 0L) && (getWorkFlowDetailsCountByID(workflowId) == 0L)) {// record not found in get or update
			String[] valueParm = new String[2];
			valueParm[0] = "Workflow";
			valueParm[1] = String.valueOf(workflowId);
			ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90256", valueParm), "EN");
			errDetails.add(errorDetail);
		}
		if (!"get".equals(flag)) {
			if (StringUtils.isBlank(workFlowDetails.getWorkFlowType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "WorkFlowType";
				ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm), "EN");
				errDetails.add(errorDetail);
			}
			if (StringUtils.isBlank(workFlowDetails.getWorkFlowRoles())) {
				String[] valueParm = new String[1];
				valueParm[0] = "WorkFlowRoles";
				ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm), "EN");
				errDetails.add(errorDetail);
			}
			if (StringUtils.isNotBlank(workFlowDetails.getWorkFlowType())
					&& workFlowDetails.getWorkFlowType().length() > 50) {
				String[] valueParm = new String[2];
				valueParm[0] = "WorkFlowType Length";
				valueParm[1] = "50.";
				ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30508", valueParm), "EN");
				errDetails.add(errorDetail);
			}
			if (StringUtils.isNotBlank(workFlowDetails.getWorkFlowSubType())
					&& workFlowDetails.getWorkFlowSubType().length() > 50) {
				String[] valueParm = new String[2];
				valueParm[0] = "WorkFlowSubType Length";
				valueParm[1] = "50.";
				ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30508", valueParm), "EN");
				errDetails.add(errorDetail);
			}
			if (StringUtils.isNotBlank(workFlowDetails.getWorkFlowDesc())
					&& workFlowDetails.getWorkFlowDesc().length() > 200) {
				String[] valueParm = new String[2];
				valueParm[0] = "WorkFlowDesc Length";
				valueParm[1] = "200.";
				ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30508", valueParm), "EN");
				errDetails.add(errorDetail);
			}
			if (StringUtils.isNotBlank(workFlowDetails.getFirstTaskOwner())
					&& workFlowDetails.getFirstTaskOwner().length() > 50) {
				String[] valueParm = new String[2];
				valueParm[0] = "FirstTaskOwner Length";
				valueParm[1] = "50.";
				ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30508", valueParm), "EN");
				errDetails.add(errorDetail);
			}
		}
		return errDetails;
	}

	@Override
	public int getWorkFlowDetailsVersionByID(long id) {
		return getWorkFlowDetailsDAO().getWorkFlowDetailsVersionByID(id);
	}
}
