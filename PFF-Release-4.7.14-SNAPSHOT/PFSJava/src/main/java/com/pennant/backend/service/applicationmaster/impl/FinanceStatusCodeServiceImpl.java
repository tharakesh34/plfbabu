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
 * FileName    		:  FinanceStatusCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-04-2017    														*
 *                                                                  						*
 * Modified Date    :  18-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-04-2017       PENNANT	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.FinanceStatusCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.FinanceStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.FinanceStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinanceStatusCode</b>.<br>
 */
public class FinanceStatusCodeServiceImpl extends GenericService<FinanceStatusCode> implements FinanceStatusCodeService {
	private static final Logger		logger	= Logger.getLogger(FinanceStatusCodeServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;
	private FinanceStatusCodeDAO	financeStatusCodeDAO;

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
	 * @return the financeStatusCodeDAO
	 */
	public FinanceStatusCodeDAO getFinanceStatusCodeDAO() {
		return financeStatusCodeDAO;
	}

	/**
	 * @param financeStatusCodeDAO
	 *            the financeStatusCodeDAO to set
	 */
	public void setFinanceStatusCodeDAO(FinanceStatusCodeDAO financeStatusCodeDAO) {
		this.financeStatusCodeDAO = financeStatusCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FINANCESTATUSCODES/FINANCESTATUSCODES_Temp by using FINANCESTATUSCODESDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using FINANCESTATUSCODESDAO's update method 3) Audit
	 * the record in to AuditHeader and AdtFINANCESTATUSCODES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceStatusCode financeStatusCode = (FinanceStatusCode) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (financeStatusCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (financeStatusCode.isNew()) {
			financeStatusCode.setId(Long.parseLong(getFinanceStatusCodeDAO().save(financeStatusCode, tableType)));
			auditHeader.getAuditDetail().setModelData(financeStatusCode);
			auditHeader.setAuditReference(String.valueOf(financeStatusCode.getStatusId()));
		} else {
			getFinanceStatusCodeDAO().update(financeStatusCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FINANCESTATUSCODES by using FINANCESTATUSCODESDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFINANCESTATUSCODES by using auditHeaderDAO.addAudit(auditHeader)
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

		FinanceStatusCode financeStatusCode = (FinanceStatusCode) auditHeader.getAuditDetail().getModelData();
		getFinanceStatusCodeDAO().delete(financeStatusCode, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getFINANCESTATUSCODES fetch the details by using FINANCESTATUSCODESDAO's getFINANCESTATUSCODESById method.
	 * 
	 * @param statusID
	 *            statusID of the FinanceStatusCode.
	 * @return FINANCESTATUSCODES
	 */
	public FinanceStatusCode getFinanceStatusCode(long statusID) {
		return getFinanceStatusCodeDAO().getFinanceStatusCode(statusID, "_View");
	}

	/**
	 * getApprovedFINANCESTATUSCODESById fetch the details by using FINANCESTATUSCODESDAO's getFINANCESTATUSCODESById
	 * method . with parameter id and type as blank. it fetches the approved records from the FINANCESTATUSCODES.
	 * 
	 * @param statusID
	 *            statusID of the FinanceStatusCode. (String)
	 * @return FINANCESTATUSCODES
	 */
	public FinanceStatusCode getApprovedFinanceStatusCode(long statusID) {
		return getFinanceStatusCodeDAO().getFinanceStatusCode(statusID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceStatusCodeDAO().delete with
	 * parameters financeStatusCode,"" b) NEW Add new record in to main table by using getFinanceStatusCodeDAO().save
	 * with parameters financeStatusCode,"" c) EDIT Update record in the main table by using
	 * getFinanceStatusCodeDAO().update with parameters financeStatusCode,"" 3) Delete the record from the workFlow
	 * table by using getFinanceStatusCodeDAO().delete with parameters financeStatusCode,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtFINANCESTATUSCODES by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtFINANCESTATUSCODES by using auditHeaderDAO.addAudit(auditHeader) based on the
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

		FinanceStatusCode financeStatusCode = new FinanceStatusCode();
		BeanUtils.copyProperties((FinanceStatusCode) auditHeader.getAuditDetail().getModelData(), financeStatusCode);

		getFinanceStatusCodeDAO().delete(financeStatusCode, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(financeStatusCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					financeStatusCodeDAO.getFinanceStatusCode(financeStatusCode.getStatusId(), ""));
		}

		if (financeStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceStatusCodeDAO().delete(financeStatusCode, TableType.MAIN_TAB);
		} else {
			financeStatusCode.setRoleCode("");
			financeStatusCode.setNextRoleCode("");
			financeStatusCode.setTaskId("");
			financeStatusCode.setNextTaskId("");
			financeStatusCode.setWorkflowId(0);

			if (financeStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeStatusCode.setRecordType("");
				getFinanceStatusCodeDAO().save(financeStatusCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeStatusCode.setRecordType("");
				getFinanceStatusCodeDAO().update(financeStatusCode, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeStatusCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceStatusCodeDAO().delete with parameters financeStatusCode,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFINANCESTATUSCODES by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinanceStatusCode financeStatusCode = (FinanceStatusCode) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceStatusCodeDAO().delete(financeStatusCode, TableType.TEMP_TAB);

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
	 * from getFinanceStatusCodeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinanceStatusCode financeStatusCode = (FinanceStatusCode) auditDetail.getModelData();

		// Check the unique keys.
		if (financeStatusCode.isNew()
				&& financeStatusCodeDAO.isDuplicateKey(financeStatusCode.getStatusId(), financeStatusCode
						.getStatusCode(), financeStatusCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_StatusCode") + ": " + financeStatusCode.getStatusCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}