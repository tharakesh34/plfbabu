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
 * FileName    		:  VerificationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-03-2018    														*
 *                                                                  						*
 * Modified Date    :  24-03-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-03-2018       PENNANT	                 0.1                                            * 
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
package com.pennanttech.pennapps.pff.verification.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.Status;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Verification</b>.<br>
 */
public class VerificationServiceImpl extends GenericService<Verification> implements VerificationService {
	private static final Logger logger = LogManager.getLogger(VerificationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private VerificationDAO verificationDAO;

	@Autowired
	private FieldInvestigationService fieldInvestigationService;

	public List<AuditDetail> saveOrUpdate(Verification verification, String tableType, String auditTranType,
			boolean isInitTab) {
		logger.debug(Literal.ENTERING);

		List<Long> idList = null;
		List<CustomerDetails> customerDetailsList = null;

		VerificationType verificationType = VerificationType.getRequestType(verification.getVerificationType());

		String[] fields = PennantJavaUtil.getFieldDetails(verification, verification.getExcludeFields());

		if (verificationType == VerificationType.FI) {
			customerDetailsList = verification.getCustomerDetailsList();
			idList = fieldInvestigationService.getFieldInvestigationIds(verification.getVerifications(),
					verification.getKeyReference());
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(verification.getWorkflowId()).getWorkFlowXml());
		int i = 0;

		for (Verification item : verification.getVerifications()) {
			item.setWorkflowId(0);
			item.setLastMntOn(verification.getLastMntOn());
			item.setLastMntBy(verification.getLastMntBy());
			item.setVersion(verification.getVersion());
			item.setRoleCode(verification.getRoleCode());
			item.setNextRoleCode(verification.getNextRoleCode());
			item.setTaskId(verification.getTaskId());
			item.setNextTaskId(verification.getNextTaskId());
			item.setRecordStatus(verification.getRecordStatus());
			item.setWorkflowId(verification.getWorkflowId());
			if (StringUtils.isEmpty(item.getRecordType())) {
				item.setRecordType(verification.getRecordType());
			}

			if (isInitTab) {
				if (item.isNew()) {
					verificationDAO.save(item, TableType.MAIN_TAB);
				} else {
					verificationDAO.update(item, TableType.MAIN_TAB);
				}

				if (!idList.contains(item.getId()) && engine.compareTo(verification.getTaskId(),
						verification.getNextTaskId().replace(";", "")) == Flow.SUCCESSOR) {

					if (verificationType == VerificationType.FI) {
						saveFI(customerDetailsList, item);
					}

				}
			} else {
				if (item.getDecision() == Decision.RE_INITIATE.getKey()) {
					item.setCreatedOn(item.getLastMntOn());
					item.setCreatedBy(item.getLastMntBy());

					Verification reInit = new Verification();
					reInit.setId(item.getId());
					reInit.setLastMntOn(item.getLastMntOn());
					reInit.setLastMntBy(item.getLastMntBy());

					if (verificationType == VerificationType.FI) {
						item.setStatus(FIStatus.SELECT.getKey());
					}

					item.setVerificationDate(null);
					item.setDecision(Decision.SELECT.getKey());
					item.setAgency(item.getReInitAgency());
					item.setRemarks(item.getReInitRemarks());
					item.setRequestType(RequestType.INITIATE.getKey());
					item.setReason(null);
					verificationDAO.save(item, TableType.MAIN_TAB);

					reInit.setReinitid(item.getId());
					verificationDAO.updateReInit(reInit, TableType.MAIN_TAB);

					if (verificationType == VerificationType.FI) {
						saveFI(customerDetailsList, item);
					}
				} else {
					verificationDAO.update(item, TableType.MAIN_TAB);
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i++, fields[0], fields[1], item.getBefImage(), item));
		}

		return auditDetails;
	}

	private void saveFI(List<CustomerDetails> customerDetailsList, Verification item) {
		if (item.getFieldInvestigation() == null) {
			for (CustomerDetails customerDetails : customerDetailsList) {
				fieldInvestigationService.save(customerDetails, customerDetails.getCustomerPhoneNumList(), item);
			}
		} else if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			item.getFieldInvestigation().setVerificationId(item.getId());
			item.getFieldInvestigation().setLastMntOn(item.getLastMntOn());
			fieldInvestigationService.save(item.getFieldInvestigation(), TableType.TEMP_TAB);
		}
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * verifications by using verificationsDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and Adtverifications by using auditHeaderDAO.addAudit(auditHeader)
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

		Verification verification = (Verification) auditHeader.getAuditDetail().getModelData();
		verificationDAO.delete(verification, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getApprovedverificationsById fetch the details by using verificationsDAO's getverificationsById method . with
	 * parameter id and type as blank. it fetches the approved records from the verifications.
	 * 
	 * @param id
	 *            id of the Verification. (String)
	 * @return verifications
	 */
	public Verification getApprovedVerification(long id) {
		return null;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using verificationDAO.delete with parameters
	 * verification,"" b) NEW Add new record in to main table by using verificationDAO.save with parameters
	 * verification,"" c) EDIT Update record in the main table by using verificationDAO.update with parameters
	 * verification,"" 3) Delete the record from the workFlow table by using verificationDAO.delete with parameters
	 * verification,"_Temp" 4) Audit the record in to AuditHeader and Adtverifications by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and Adtverifications by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		Verification verification = new Verification();
		BeanUtils.copyProperties((Verification) auditHeader.getAuditDetail().getModelData(), verification);

		verificationDAO.delete(verification, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(verification.getRecordType())) {
		}

		if (verification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			verificationDAO.delete(verification, TableType.MAIN_TAB);
		} else {
			verification.setRoleCode("");
			verification.setNextRoleCode("");
			verification.setTaskId("");
			verification.setNextTaskId("");
			verification.setWorkflowId(0);

			if (verification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				verification.setRecordType("");
				verificationDAO.save(verification, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				verification.setRecordType("");
				verificationDAO.update(verification, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(verification);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using verificationDAO.delete with parameters verification,"_Temp" 3) Audit the record in to
	 * AuditHeader and Adtverifications by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		Verification verification = (Verification) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		verificationDAO.delete(verification, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

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
	 * from verificationDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<Verification> getVerifications(String keyReference) {
		List<Verification> verifications = verificationDAO.getFiVeriFications(keyReference);
		for (Verification verification : verifications) {
			if (verification.getStatus() == Status.POSITIVE.getKey()
					|| verification.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				verification.setDecision(Decision.APPROVE.getKey());
			}
		}
		return verifications;
	}
}