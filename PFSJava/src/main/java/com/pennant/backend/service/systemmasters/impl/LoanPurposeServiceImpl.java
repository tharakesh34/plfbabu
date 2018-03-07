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
 * FileName    		:  LoanPurposeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.systemmasters.LoanPurposeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.LoanPurposeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>LoanPurpose</b>.<br>
 * 
 */
public class LoanPurposeServiceImpl extends GenericService<LoanPurpose>implements LoanPurposeService {

	private static Logger logger = Logger
			.getLogger(LoanPurposeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LoanPurposeDAO loanPurposeDAO;
	private FinanceMainDAO financeMainDAO;

	public LoanPurposeServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTLoanPurposes/BMTLoanPurposes_Temp by using LoanPurposeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using LoanPurposeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTLoanPurposes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * 
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		LoanPurpose loanPurpose = (LoanPurpose) auditHeader.getAuditDetail()
				.getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (loanPurpose.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (loanPurpose.isNew()) {
			loanPurpose.setId(getLoanPurposeDAO().save(loanPurpose, tableType));
			auditHeader.getAuditDetail().setModelData(loanPurpose);
			auditHeader.setAuditReference(loanPurpose.getLoanPurposeCode());
		} else {
			getLoanPurposeDAO().update(loanPurpose, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTLoanPurposes by using LoanPurposeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTLoanPurposes by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		LoanPurpose addressType = (LoanPurpose) auditHeader.getAuditDetail()
				.getModelData();
		getLoanPurposeDAO().delete(addressType, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLoanPurposeById fetch the details by using LoanPurposeDAO's
	 * getLoanPurposeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return LoanPurpose
	 */
	@Override
	public LoanPurpose getLoanPurposeById(String id) {
		return getLoanPurposeDAO().getLoanPurposeById(id, "_View");
	}

	/**
	 * getApprovedLoanPurposeById fetch the details by using LoanPurposeDAO's
	 * getLoanPurposeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTLoanPurposes.
	 * 
	 * @param id
	 *            (String)
	 * @return LoanPurpose
	 */
	public LoanPurpose getApprovedLoanPurposeById(String id) {
		return getLoanPurposeDAO().getLoanPurposeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getLoanPurposeDAO().delete with parameters addressType,"" b) NEW
	 * Add new record in to main table by using getLoanPurposeDAO().save with
	 * parameters addressType,"" c) EDIT Update record in the main table by
	 * using getLoanPurposeDAO().update with parameters addressType,"" 3) Delete
	 * the record from the workFlow table by using getLoanPurposeDAO().delete
	 * with parameters addressType,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtBMTLoanPurposes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTLoanPurposes by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LoanPurpose loanPurpose = new LoanPurpose();
		BeanUtils.copyProperties((LoanPurpose) auditHeader.getAuditDetail()
				.getModelData(), loanPurpose);
		
		getLoanPurposeDAO().delete(loanPurpose, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(loanPurpose.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(getLoanPurposeDAO().getLoanPurposeById(loanPurpose.getLoanPurposeCode(), ""));
		}
		
		if (loanPurpose.getRecordType()
				.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getLoanPurposeDAO().delete(loanPurpose, TableType.MAIN_TAB);
		} else {
			loanPurpose.setRoleCode("");
			loanPurpose.setNextRoleCode("");
			loanPurpose.setTaskId("");
			loanPurpose.setNextTaskId("");
			loanPurpose.setWorkflowId(0);

			if (loanPurpose.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				loanPurpose.setRecordType("");
				getLoanPurposeDAO().save(loanPurpose, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				loanPurpose.setRecordType("");
				getLoanPurposeDAO().update(loanPurpose, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(loanPurpose);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getLoanPurposeDAO().delete with parameters
	 * addressType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTLoanPurposes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		LoanPurpose addressType = (LoanPurpose) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLoanPurposeDAO().delete(addressType, TableType.TEMP_TAB);
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
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getLoanPurposeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		LoanPurpose loanPurpose = (LoanPurpose) auditDetail.getModelData();
		String code = loanPurpose.getLoanPurposeCode();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_LoanPurposeCode") + ": " + code;

		// Check the unique keys.
		if (loanPurpose.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(loanPurpose.getRecordType())
				&& getLoanPurposeDAO().isDuplicateKey(code, loanPurpose.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		
		if (StringUtils.trimToEmpty(loanPurpose.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			boolean exist = getFinanceMainDAO().isLoanPurposeExits(loanPurpose.getLoanPurposeCode(), "_View");
			if (exist) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null), usrLanguage));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	public LoanPurposeDAO getLoanPurposeDAO() {
		return loanPurposeDAO;
	}

	public void setLoanPurposeDAO(LoanPurposeDAO loanPurposeDAO) {
		this.loanPurposeDAO = loanPurposeDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	

}