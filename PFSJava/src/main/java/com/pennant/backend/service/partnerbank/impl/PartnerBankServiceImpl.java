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
 * FileName    		:  PartnerBankServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.service.partnerbank.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentHeaderDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PartnerBank</b>.<br>
 * 
 */
public class PartnerBankServiceImpl extends GenericService<PartnerBank> implements PartnerBankService {
	private static final Logger	logger	= Logger.getLogger(PartnerBankServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;

	private PartnerBankDAO		partnerBankDAO;
	
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;

	private FinTypePartnerBankDAO finTypePartnerBankDAO;

	private PaymentInstructionDAO paymentInstructionDAO;

	private FeePostingsDAO feePostingsDAO;

	private PresentmentHeaderDAO presentmentHeaderDAO;
	
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
	 * @return the partnerBankDAO
	 */
	public PartnerBankDAO getPartnerBankDAO() {
		return partnerBankDAO;
	}

	/**
	 * @param partnerBankDAO
	 *            the partnerBankDAO to set
	 */
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table PartnerBank/PartnerBank_Temp by
	 * using PartnerBankDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using PartnerBankDAO's update method 3) Audit the record in to AuditHeader and AdtPartnerBank by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		PartnerBank partnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (partnerBank.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (partnerBank.isNew()) {
			getPartnerBankDAO().save(partnerBank, tableType);
			if (partnerBank.getPartnerBankModesList() != null && partnerBank.getPartnerBankModesList().size() > 0) {
				getPartnerBankDAO().deletePartner(partnerBank);
				getPartnerBankDAO().saveList(partnerBank.getPartnerBankModesList(), partnerBank.getPartnerBankId());

			}
			if (partnerBank.getPartnerBranchModesList() != null && partnerBank.getPartnerBranchModesList().size() > 0) {
				getPartnerBankDAO().deletePartnerBranch(partnerBank);
				getPartnerBankDAO().saveBranchList(partnerBank.getPartnerBranchModesList(),
						partnerBank.getPartnerBankId());
			}

		} else {
			getPartnerBankDAO().update(partnerBank, tableType);
			if (partnerBank.getPartnerBankModesList() != null && partnerBank.getPartnerBankModesList().size() > 0) {
				getPartnerBankDAO().deletePartner(partnerBank);
				getPartnerBankDAO().saveList(partnerBank.getPartnerBankModesList(), partnerBank.getPartnerBankId());
			}
			if (partnerBank.getPartnerBranchModesList() != null && partnerBank.getPartnerBranchModesList().size() > 0) {
				getPartnerBankDAO().deletePartnerBranch(partnerBank);
				getPartnerBankDAO().saveBranchList(partnerBank.getPartnerBranchModesList(),
						partnerBank.getPartnerBankId());
			}
		}
		

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PartnerBank by using PartnerBankDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtPartnerBank by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		PartnerBank partnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();
		getPartnerBankDAO().delete(partnerBank, TableType.MAIN_TAB);
		getPartnerBankDAO().deletePartner(partnerBank);
		getPartnerBankDAO().deletePartnerBranch(partnerBank);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPartnerBankById fetch the details by using PartnerBankDAO's getPartnerBankById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PartnerBank
	 */

	@Override
	public PartnerBank getPartnerBankById(long id) {
		return getPartnerBankDAO().getPartnerBankById(id, "_View");
	}

	
	@Override
	public List<PartnerBankModes> getPartnerBankModesId(long partnerBankId) {
		return getPartnerBankDAO().getPartnerBankModesId(partnerBankId);

	}
	/**
	 * getApprovedPartnerBankById fetch the details by using PartnerBankDAO's getPartnerBankById method . with parameter
	 * id and type as blank. it fetches the approved records from the PartnerBank.
	 * 
	 * @param id
	 *            (String)
	 * @return PartnerBank
	 */

	public PartnerBank getApprovedPartnerBankById(long id) {
		return getPartnerBankDAO().getPartnerBankById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPartnerBankDAO().delete with
	 * parameters partnerBank,"" b) NEW Add new record in to main table by using getPartnerBankDAO().save with
	 * parameters partnerBank,"" c) EDIT Update record in the main table by using getPartnerBankDAO().update with
	 * parameters partnerBank,"" 3) Delete the record from the workFlow table by using getPartnerBankDAO().delete with
	 * parameters partnerBank,"_Temp" 4) Audit the record in to AuditHeader and AdtPartnerBank by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtPartnerBank by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		PartnerBank partnerBank = new PartnerBank();
		BeanUtils.copyProperties((PartnerBank) auditHeader.getAuditDetail().getModelData(), partnerBank);
		getPartnerBankDAO().delete(partnerBank, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(partnerBank.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(partnerBankDAO.getPartnerBankById(partnerBank.getPartnerBankId(), ""));
		}
		
		if (partnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getPartnerBankDAO().delete(partnerBank, TableType.MAIN_TAB);

		} else {
			partnerBank.setRoleCode("");
			partnerBank.setNextRoleCode("");
			partnerBank.setTaskId("");
			partnerBank.setNextTaskId("");
			partnerBank.setWorkflowId(0);

			if (partnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				partnerBank.setRecordType("");
				getPartnerBankDAO().save(partnerBank, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				partnerBank.setRecordType("");
				getPartnerBankDAO().update(partnerBank, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(partnerBank);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPartnerBankDAO().delete with parameters partnerBank,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtPartnerBank by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		PartnerBank partnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPartnerBankDAO().delete(partnerBank, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getPartnerBankDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		PartnerBank partnerBank = (PartnerBank) auditDetail.getModelData();
		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = partnerBank.getPartnerBankCode();
		errParm[0] = PennantJavaUtil.getLabel("label_PartnerBankCode") + ":" + valueParm[0];

		// Check the unique keys.
		if (partnerBank.isNew()
				&& partnerBankDAO.isDuplicateKey(partnerBank.getPartnerBankId(), partnerBank.getPartnerBankCode(),
						partnerBank.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
		}

		// Duplicate Partner Bank Code
		if (partnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && partnerBank.isNewRecord()
				&& getPartnerCodeExist(partnerBank.getPartnerBankCode(), "_View")) {
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm));
		}
		
		// Check Dependency Validation
		if (PennantConstants.RECORD_TYPE_DEL.equals(partnerBank.getRecordType())) {
			boolean isPartnerBankUsed = checkDependencyValidation(partnerBank.getPartnerBankId());

			if (isPartnerBankUsed) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public boolean getPartnerCodeExist(String partnerBankCodeValue, String type) {
		logger.debug("Entering");

		boolean codeExist = false;

		if (getPartnerBankDAO().geBankCodeCount(partnerBankCodeValue, type) != 0) {
			codeExist = true;
		}

		logger.debug("Leaving");

		return codeExist;
	}

	/**
	 * Checking wile record deletion if partnerBank used in other area's or not.
	 * If it used we cannot allow to delete that record.
	 * @param partnerBankId
	 * @return
	 */
	public boolean checkDependencyValidation(long partnerBankId) {
		logger.debug("Entering");

		int count = 0;

		count = getFinTypePartnerBankDAO().getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = getFinAdvancePaymentsDAO().getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = getPaymentInstructionDAO().getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = getFeePostingsDAO().getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = getPresentmentHeaderDAO().getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}
	
	@Override
	public List<PartnerBranchModes> getPartnerBranchModesId(long id) {
		return getPartnerBankDAO().getPartnerBranchModesId(id);
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public FinTypePartnerBankDAO getFinTypePartnerBankDAO() {
		return finTypePartnerBankDAO;
	}

	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	public PaymentInstructionDAO getPaymentInstructionDAO() {
		return paymentInstructionDAO;
	}

	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	public FeePostingsDAO getFeePostingsDAO() {
		return feePostingsDAO;
	}

	public void setFeePostingsDAO(FeePostingsDAO feePostingsDAO) {
		this.feePostingsDAO = feePostingsDAO;
	}

	public PresentmentHeaderDAO getPresentmentHeaderDAO() {
		return presentmentHeaderDAO;
	}

	public void setPresentmentHeaderDAO(PresentmentHeaderDAO presentmentHeaderDAO) {
		this.presentmentHeaderDAO = presentmentHeaderDAO;
	}
}