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
 * FileName    		:  HoldDisbursementServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-10-2018    														*
 *                                                                  						*
 * Modified Date    :  09-10-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-10-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.HoldDisbursementDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.HoldDisbursementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>HoldDisbursement</b>.<br>
 */
public class HoldDisbursementServiceImpl extends GenericService<HoldDisbursement> implements HoldDisbursementService {
	private static final Logger logger = LogManager.getLogger(HoldDisbursementServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private HoldDisbursementDAO holdDisbursementDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinanceMainDAO financeMainDAO;
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
	 * @return the holdDisbursementDAO
	 */
	public HoldDisbursementDAO getHoldDisbursementDAO() {
		return holdDisbursementDAO;
	}

	/**
	 * @param holdDisbursementDAO
	 *            the holdDisbursementDAO to set
	 */
	public void setHoldDisbursementDAO(HoldDisbursementDAO holdDisbursementDAO) {
		this.holdDisbursementDAO = holdDisbursementDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * HoldDisbursement/HoldDisbursement_Temp by using HoldDisbursementDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using HoldDisbursementDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtHoldDisbursement by using auditHeaderDAO.addAudit(auditHeader)
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

		HoldDisbursement holdDisbursement = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (holdDisbursement.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (holdDisbursement.isNew()) {
			holdDisbursementDAO.save(holdDisbursement, tableType);
		} else {
			holdDisbursementDAO.update(holdDisbursement, tableType);
		}
		String rcdMaintainSts = FinServiceEvent.HOLDDISB;
		financeMainDAO.updateMaintainceStatus(holdDisbursement.getFinReference(), rcdMaintainSts);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * HoldDisbursement by using HoldDisbursementDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtHoldDisbursement by using auditHeaderDAO.addAudit(auditHeader)
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

		HoldDisbursement holdDisbursement = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();
		getHoldDisbursementDAO().delete(holdDisbursement, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getHoldDisbursement fetch the details by using HoldDisbursementDAO's getHoldDisbursementById method.
	 * 
	 * @param entityCode
	 *            entityCode of the HoldDisbursement.
	 * @param finReference
	 *            finReference of the HoldDisbursement.
	 * @return HoldDisbursement
	 */
	@Override
	public HoldDisbursement getHoldDisbursement(String finReference) {
		return getHoldDisbursementDAO().getHoldDisbursement(finReference, "_View");
	}

	/**
	 * getApprovedHoldDisbursementById fetch the details by using HoldDisbursementDAO's getHoldDisbursementById method .
	 * with parameter id and type as blank. it fetches the approved records from the HoldDisbursement.
	 * 
	 * @param entityCode
	 *            entityCode of the HoldDisbursement.
	 * @param finReference
	 *            finReference of the HoldDisbursement. (String)
	 * @return HoldDisbursement
	 */
	public HoldDisbursement getApprovedHoldDisbursement(String finReference) {
		return getHoldDisbursementDAO().getHoldDisbursement(finReference, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getHoldDisbursementDAO().delete with
	 * parameters holdDisbursement,"" b) NEW Add new record in to main table by using getHoldDisbursementDAO().save with
	 * parameters holdDisbursement,"" c) EDIT Update record in the main table by using getHoldDisbursementDAO().update
	 * with parameters holdDisbursement,"" 3) Delete the record from the workFlow table by using
	 * getHoldDisbursementDAO().delete with parameters holdDisbursement,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtHoldDisbursement by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtHoldDisbursement by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		HoldDisbursement holdDisbursement = new HoldDisbursement();
		BeanUtils.copyProperties((HoldDisbursement) auditHeader.getAuditDetail().getModelData(), holdDisbursement);

		getHoldDisbursementDAO().delete(holdDisbursement, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(holdDisbursement.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(holdDisbursementDAO.getHoldDisbursement(holdDisbursement.getFinReference(), ""));
		}

		if (holdDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getHoldDisbursementDAO().delete(holdDisbursement, TableType.MAIN_TAB);
		} else {
			holdDisbursement.setRoleCode("");
			holdDisbursement.setNextRoleCode("");
			holdDisbursement.setTaskId("");
			holdDisbursement.setNextTaskId("");
			holdDisbursement.setWorkflowId(0);

			if (holdDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				holdDisbursement.setRecordType("");
				getHoldDisbursementDAO().save(holdDisbursement, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				holdDisbursement.setRecordType("");
				getHoldDisbursementDAO().update(holdDisbursement, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		// save FinInstruction to maintain records
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(holdDisbursement.getFinReference());
		finServiceInstruction.setFromDate(DateUtility.getAppDate());
		finServiceInstruction.setFinEvent(FinServiceEvent.HOLDDISB);
		finServiceInstruction.setAmount(holdDisbursement.getHoldLimitAmount());
		getFinServiceInstructionDAO().save(finServiceInstruction, "");
		financeMainDAO.updateMaintainceStatus(holdDisbursement.getFinReference(), "");

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(holdDisbursement);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getHoldDisbursementDAO().delete with parameters holdDisbursement,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtHoldDisbursement by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		HoldDisbursement holdDisbursement = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		holdDisbursementDAO.delete(holdDisbursement, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(holdDisbursement.getFinReference(), "");

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
	 * from getHoldDisbursementDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		HoldDisbursement holdDisbursement = (HoldDisbursement) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + holdDisbursement.getFinReference();

		// Check the unique keys.
		if (holdDisbursement.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(holdDisbursement.getRecordType())
				&& getHoldDisbursementDAO().isDuplicateKey(holdDisbursement.getFinReference(),
						holdDisbursement.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		/*
		 * String[] parameter = new String[2]; parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " +
		 * holdDisbursement.getFinReference();
		 * 
		 * if (PennantApplicationUtil.unFormateAmount(holdDisbursement.getTotalLoanAmt(),
		 * PennantConstants.defaultCCYDecPos).compareTo(BigDecimal.ZERO) == 0 ||
		 * (holdDisbursement.getDisbursedAmount().compareTo(holdDisbursement.getTotalLoanAmt()) == 0) ||
		 * (holdDisbursement.getTotalLoanAmt().compareTo(holdDisbursement.getDisbursedAmount()) == -1)) {
		 * 
		 * auditDetail.setErrorDetail(new ErrorDetail("HD99018", parameter)); }
		 */

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public boolean getFinanceDisbursementById(String id) {
		FinanceDisbursement financeDisbursementById = getFinanceDisbursementDAO().getFinanceDisbursementById(id,
				"_TEMP", false);

		return (financeDisbursementById == null) ? true : false;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public boolean isFinServiceInstructionExist(String finReference, String type, String finEvent) {
		boolean isExist = false;
		List<FinServiceInstruction> finServiceInstructions = getFinServiceInstructionDAO()
				.getFinServiceInstructions(finReference, type, finEvent);
		if (finServiceInstructions.size() > 0) {
			isExist = true;
		}
		return isExist;
	}

}