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
 * FileName    		:  BranchCashLimitServiceImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-01-2018    														*
 *                                                                  						*
 * Modified Date    :  29-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-01-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.cashmanagement.impl;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cashmanagement.BranchCashDetailDAO;
import com.pennant.backend.dao.cashmanagement.BranchCashLimitDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cashmanagement.BranchCashDetail;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BranchCashLimit</b>.<br>
 */
public class BranchCashLimitServiceImpl extends GenericService<BranchCashLimit> implements BranchCashLimitService {
	private static final Logger logger = LogManager.getLogger(BranchCashLimitServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BranchCashLimitDAO branchCashLimitDAO;
	private BranchCashDetailDAO branchCashDetailDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BranchCashLimit/BranchCashLimit_Temp by using BranchCashLimitDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using BranchCashLimitDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBranchCashLimit by using auditHeaderDAO.addAudit(auditHeader)
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

		BranchCashLimit branchCashLimit = (BranchCashLimit) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (branchCashLimit.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (branchCashLimit.isNew()) {
			branchCashLimitDAO.save(branchCashLimit, tableType);
			if (TableType.MAIN_TAB.equals(tableType)) {
				addBranchCashDetail(branchCashLimit.getBranchCode());
			}

		} else {
			branchCashLimitDAO.update(branchCashLimit, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	private void addBranchCashDetail(String branchCode) {
		BranchCashDetail cashDetail = branchCashDetailDAO.getBranchCashDetail(branchCode);
		if (cashDetail == null) {
			cashDetail = new BranchCashDetail();
			cashDetail.setBranchCode(branchCode);
			cashDetail.setBranchCash(BigDecimal.ZERO);
			cashDetail.setAdhocInitiationAmount(BigDecimal.ZERO);
			cashDetail.setAdhocProcessingAmount(BigDecimal.ZERO);
			cashDetail.setAdhocTransitAmount(BigDecimal.ZERO);
			cashDetail.setAutoProcessingAmount(BigDecimal.ZERO);
			cashDetail.setAutoTransitAmount(BigDecimal.ZERO);
			branchCashDetailDAO.save(cashDetail);
		}

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BranchCashLimit by using BranchCashLimitDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtBranchCashLimit by using auditHeaderDAO.addAudit(auditHeader)
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

		BranchCashLimit branchCashLimit = (BranchCashLimit) auditHeader.getAuditDetail().getModelData();
		branchCashLimitDAO.delete(branchCashLimit, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBranchCashLimit fetch the details by using BranchCashLimitDAO's getBranchCashLimitById method.
	 * 
	 * @param branchCode
	 *            branchCode of the BranchCashLimit.
	 * @return BranchCashLimit
	 */
	@Override
	public BranchCashLimit getBranchCashLimit(String branchCode) {
		return getBranchCashLimit(branchCode, "_View");
	}

	/**
	 * getApprovedBranchCashLimitById fetch the details by using BranchCashLimitDAO's getBranchCashLimitById method .
	 * with parameter id and type as blank. it fetches the approved records from the BranchCashLimit.
	 * 
	 * @param branchCode
	 *            branchCode of the BranchCashLimit. (String)
	 * @return BranchCashLimit
	 */
	public BranchCashLimit getApprovedBranchCashLimit(String branchCode) {
		return getBranchCashLimit(branchCode, "_AView");
	}

	private BranchCashLimit getBranchCashLimit(String branchCode, String tableType) {
		BranchCashLimit branchCashLimit = branchCashLimitDAO.getBranchCashLimit(branchCode, tableType);
		if (branchCashLimit != null) {
			branchCashLimit.setBranchCashDetail(getBranchCashDetail(branchCode));
		}
		return branchCashLimit;
	}

	/**
	 * getBranchCashDetail fetch the details by using getBranchCashDetailDAO's getBranchCashDetail method . with
	 * parameter branchCode.
	 * 
	 * @param branchCode
	 *            branchCode of the BranchCashDetail. (String)
	 * @return BranchCashDetail
	 */

	@Override
	public BranchCashDetail getBranchCashDetail(String branchCode) {
		return branchCashDetailDAO.getBranchCashDetail(branchCode);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using branchCashLimitDAO.delete with
	 * parameters branchCashLimit,"" b) NEW Add new record in to main table by using branchCashLimitDAO.save with
	 * parameters branchCashLimit,"" c) EDIT Update record in the main table by using branchCashLimitDAO.update with
	 * parameters branchCashLimit,"" 3) Delete the record from the workFlow table by using branchCashLimitDAO.delete
	 * with parameters branchCashLimit,"_Temp" 4) Audit the record in to AuditHeader and AdtBranchCashLimit by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtBranchCashLimit
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		BranchCashLimit branchCashLimit = new BranchCashLimit();
		BeanUtils.copyProperties((BranchCashLimit) auditHeader.getAuditDetail().getModelData(), branchCashLimit);

		branchCashLimitDAO.delete(branchCashLimit, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(branchCashLimit.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(branchCashLimitDAO.getBranchCashLimit(branchCashLimit.getBranchCode(), ""));
		}

		if (branchCashLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			branchCashLimitDAO.delete(branchCashLimit, TableType.MAIN_TAB);
		} else {
			branchCashLimit.setRoleCode("");
			branchCashLimit.setNextRoleCode("");
			branchCashLimit.setTaskId("");
			branchCashLimit.setNextTaskId("");
			branchCashLimit.setWorkflowId(0);

			if (branchCashLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				branchCashLimit.setRecordType("");
				branchCashLimit.setPreviousDate(branchCashLimit.getCurLimitSetDate());
				branchCashLimit.setPreviousAmount(branchCashLimit.getCashLimit());
				branchCashLimitDAO.save(branchCashLimit, TableType.MAIN_TAB);
				addBranchCashDetail(branchCashLimit.getBranchCode());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				branchCashLimit.setRecordType("");
				branchCashLimitDAO.update(branchCashLimit, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(branchCashLimit);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using branchCashLimitDAO.delete with parameters branchCashLimit,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtBranchCashLimit by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		BranchCashLimit branchCashLimit = (BranchCashLimit) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		branchCashLimitDAO.delete(branchCashLimit, TableType.TEMP_TAB);

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
	 * from branchCashLimitDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		// Get the model object.
		BranchCashLimit branchCashLimit = (BranchCashLimit) auditDetail.getModelData();

		// Check the unique keys.
		if (branchCashLimit.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(branchCashLimit.getRecordType())
				&& branchCashLimitDAO.isDuplicateKey(branchCashLimit.getBranchCode(),
						branchCashLimit.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];

			parameters[0] = PennantJavaUtil.getLabel("label_BranchCashLimitDialog_BranchCode.value") + ":"
					+ branchCashLimit.getBranchCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setBranchCashLimitDAO(BranchCashLimitDAO branchCashLimitDAO) {
		this.branchCashLimitDAO = branchCashLimitDAO;
	}

	public void setBranchCashDetailDAO(BranchCashDetailDAO branchCashDetailDAO) {
		this.branchCashDetailDAO = branchCashDetailDAO;
	}

}