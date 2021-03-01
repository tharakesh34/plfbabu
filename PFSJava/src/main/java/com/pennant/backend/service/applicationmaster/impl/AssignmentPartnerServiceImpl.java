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
 * FileName    		:  AssignmentPartnerServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2018    														*
 *                                                                  						*
 * Modified Date    :  12-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2018       PENNANT	                 0.1                                            * 
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentPartnerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.AssignmentPartner;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AssignmentPartnerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AssignmentPartner</b>.<br>
 */
public class AssignmentPartnerServiceImpl extends GenericService<AssignmentPartner>
		implements AssignmentPartnerService {
	private static final Logger logger = LogManager.getLogger(AssignmentPartnerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssignmentPartnerDAO assignmentPartnerDAO;
	private AssignmentDealDAO assignmentDealDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the assignmentPartnerDAO
	 */
	public AssignmentPartnerDAO getAssignmentPartnerDAO() {
		return assignmentPartnerDAO;
	}

	/**
	 * @param assignmentPartnerDAO
	 *            the assignmentPartnerDAO to set
	 */
	public void setAssignmentPartnerDAO(AssignmentPartnerDAO assignmentPartnerDAO) {
		this.assignmentPartnerDAO = assignmentPartnerDAO;
	}

	public AssignmentDealDAO getAssignmentDealDAO() {
		return assignmentDealDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * AssignmentPartner/AssignmentPartner_Temp by using AssignmentPartnerDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using AssignmentPartnerDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtAssignmentPartner by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssignmentPartner assignmentPartner = (AssignmentPartner) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (assignmentPartner.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assignmentPartner.isNew()) {
			assignmentPartner.setId(Long.parseLong(getAssignmentPartnerDAO().save(assignmentPartner, tableType)));
			auditHeader.getAuditDetail().setModelData(assignmentPartner);
			auditHeader.setAuditReference(String.valueOf(assignmentPartner.getId()));
		} else {
			getAssignmentPartnerDAO().update(assignmentPartner, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * AssignmentPartner by using AssignmentPartnerDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtAssignmentPartner by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssignmentPartner assignmentPartner = (AssignmentPartner) auditHeader.getAuditDetail().getModelData();
		getAssignmentPartnerDAO().delete(assignmentPartner, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getAssignmentPartner fetch the details by using AssignmentPartnerDAO's getAssignmentPartnerById method.
	 * 
	 * @param id
	 *            id of the AssignmentPartner.
	 * @return AssignmentPartner
	 */
	@Override
	public AssignmentPartner getAssignmentPartner(long id) {
		return getAssignmentPartnerDAO().getAssignmentPartner(id, "_View");
	}

	/**
	 * getApprovedAssignmentPartnerById fetch the details by using AssignmentPartnerDAO's getAssignmentPartnerById
	 * method . with parameter id and type as blank. it fetches the approved records from the AssignmentPartner.
	 * 
	 * @param id
	 *            id of the AssignmentPartner. (String)
	 * @return AssignmentPartner
	 */
	public AssignmentPartner getApprovedAssignmentPartner(long id) {
		return getAssignmentPartnerDAO().getAssignmentPartner(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAssignmentPartnerDAO().delete with
	 * parameters assignmentPartner,"" b) NEW Add new record in to main table by using getAssignmentPartnerDAO().save
	 * with parameters assignmentPartner,"" c) EDIT Update record in the main table by using
	 * getAssignmentPartnerDAO().update with parameters assignmentPartner,"" 3) Delete the record from the workFlow
	 * table by using getAssignmentPartnerDAO().delete with parameters assignmentPartner,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtAssignmentPartner by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtAssignmentPartner by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssignmentPartner assignmentPartner = new AssignmentPartner();
		BeanUtils.copyProperties((AssignmentPartner) auditHeader.getAuditDetail().getModelData(), assignmentPartner);

		getAssignmentPartnerDAO().delete(assignmentPartner, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(assignmentPartner.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(assignmentPartnerDAO.getAssignmentPartner(assignmentPartner.getId(), ""));
		}

		if (assignmentPartner.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAssignmentPartnerDAO().delete(assignmentPartner, TableType.MAIN_TAB);
		} else {
			assignmentPartner.setRoleCode("");
			assignmentPartner.setNextRoleCode("");
			assignmentPartner.setTaskId("");
			assignmentPartner.setNextTaskId("");
			assignmentPartner.setWorkflowId(0);

			if (assignmentPartner.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				assignmentPartner.setRecordType("");
				getAssignmentPartnerDAO().save(assignmentPartner, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assignmentPartner.setRecordType("");
				getAssignmentPartnerDAO().update(assignmentPartner, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assignmentPartner);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAssignmentPartnerDAO().delete with parameters assignmentPartner,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtAssignmentPartner by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssignmentPartner assignmentPartner = (AssignmentPartner) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAssignmentPartnerDAO().delete(assignmentPartner, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAssignmentPartnerDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		AssignmentPartner assignmentPartner = (AssignmentPartner) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + assignmentPartner.getCode();

		// Check the unique keys.
		if (assignmentPartner.isNew()
				&& assignmentPartnerDAO.isDuplicateKey(assignmentPartner.getId(), assignmentPartner.getCode(),
						assignmentPartner.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (StringUtils.trimToEmpty(assignmentPartner.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int count = getAssignmentDealDAO().getMappedAssignmentDeals(assignmentPartner.getId());
			if (count != 0) {
				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null)));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}