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
 * FileName    		:  BranchServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.impl.BranchDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Branch</b>.<br>
 * 
 */
public class BranchServiceImpl extends GenericService<Branch> implements BranchService {

	private static Logger logger = Logger.getLogger(BranchDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private BranchDAO branchDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}	
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	@Override
	public Branch getBranch() {
		return getBranchDAO().getBranch();
	}

	@Override
	public Branch getNewBranch() {
		return getBranchDAO().getNewBranch();
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTBranches/RMTBranches_Temp by using BranchDAO's save method b) Update
	 * the Record in the table. based on the module workFlow Configuration. by
	 * using BranchDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtRMTBranches by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		Branch branch = (Branch) auditHeader.getAuditDetail().getModelData();

		if (branch.isWorkflow()) {
			tableType="_TEMP";
		}

		if (branch.isNew()) {
			branch.setBranchCode(getBranchDAO().save(branch,tableType));
			auditHeader.getAuditDetail().setModelData(branch);
			auditHeader.setAuditReference(String.valueOf(branch.getBranchCode()));
		}else{
			getBranchDAO().update(branch,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTBranches by using BranchDAO's delete method with type as Blank
	 * 3) Audit the record in to AuditHeader and AdtRMTBranches by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Branch branch = (Branch) auditHeader.getAuditDetail().getModelData();
		getBranchDAO().delete(branch,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBranchById fetch the details by using BranchDAO's getBranchById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Branch
	 */
	@Override
	public Branch getBranchById(String id) {
		return getBranchDAO().getBranchById(id,"_View");
	}

	/**
	 * getApprovedBranchById fetch the details by using BranchDAO's getBranchById method .
	 * with parameter id and type as blank. it fetches the approved records from the RMTBranches.
	 * @param id (String)
	 * @return Branch
	 */
	public Branch getApprovedBranchById(String id) {
		return getBranchDAO().getBranchById(id,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param Branch (branch)
	 * @return branch
	 */
	@Override
	public Branch refresh(Branch branch) {
		logger.debug("Entering");
		getBranchDAO().refresh(branch);
		getBranchDAO().initialize(branch);
		logger.debug("Leaving");
		return branch;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBranchDAO().delete with parameters branch,"" b) NEW Add new
	 * record in to main table by using getBranchDAO().save with parameters
	 * branch,"" c) EDIT Update record in the main table by using
	 * getBranchDAO().update with parameters branch,"" 3) Delete the record from
	 * the workFlow table by using getBranchDAO().delete with parameters
	 * branch,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTBranches
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtRMTBranches by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Branch branch = new Branch();
		BeanUtils.copyProperties((Branch) auditHeader.getAuditDetail().getModelData(), branch);

		if (branch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getBranchDAO().delete(branch,"");

		} else {
			branch.setRoleCode("");
			branch.setNextRoleCode("");
			branch.setTaskId("");
			branch.setNextTaskId("");
			branch.setWorkflowId(0);

			if (branch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				branch.setRecordType("");
				getBranchDAO().save(branch,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				branch.setRecordType("");
				getBranchDAO().update(branch,"");
			}
		}

		getBranchDAO().delete(branch,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(branch);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBranchDAO().delete with parameters
	 * branch,"_Temp" 3) Audit the record in to AuditHeader and AdtRMTBranches
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Branch branch= (Branch) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBranchDAO().delete(branch,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
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
	 * getBranchDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,
			String method) {
		logger.debug("Entering");

		Branch branch = (Branch) auditDetail.getModelData();
		Branch tempBranch = null;
		if (branch.isWorkflow()) {
			tempBranch = getBranchDAO().getBranchById(branch.getId(), "_Temp");
		}
		Branch befBranch = getBranchDAO().getBranchById(branch.getId(), "");

		Branch oldBranch = branch.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = branch.getBranchCode();
		errParm[0] = PennantJavaUtil.getLabel("label_BranchCode") + ":"+ valueParm[0];

		if (branch.isNew()) { // for New record or new record into work flow

			if (!branch.isWorkflow()) {// With out Work flow only new records
				if (befBranch != null) { // Record Already Exists in the table
					// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (branch.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befBranch != null || tempBranch != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}
				else { // if records not exists in the Main flow table
					if (befBranch == null || tempBranch != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!branch.isWorkflow()) { // With out Work flow for update and delete

				if (befBranch == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldBranch != null
							&& !oldBranch.getLastMntOn().equals(
									befBranch.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempBranch == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempBranch != null && oldBranch != null
						&& !oldBranch.getLastMntOn().equals(
								tempBranch.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !branch.isWorkflow()) {
			auditDetail.setBefImage(befBranch);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}