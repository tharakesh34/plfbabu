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
 * FileName    		:  PaymentInstructionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.payment.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.payment.PaymentInstructionService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentInstruction</b>.<br>
 */
public class PaymentInstructionServiceImpl extends GenericService<PaymentInstruction> implements PaymentInstructionService {
	private static final Logger logger = Logger.getLogger(PaymentInstructionServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PaymentInstructionDAO paymentInstructionDAO;

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
	 * @return the paymentInstructionDAO
	 */
	public PaymentInstructionDAO getPaymentInstructionDAO() {
		return paymentInstructionDAO;
	}

	/**
	 * @param paymentInstructionDAO
	 *            the paymentInstructionDAO to set
	 */
	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * PaymentInstructions/PaymentInstructions_Temp by using PaymentInstructionsDAO's save method b) Update the Record
	 * in the table. based on the module workFlow Configuration. by using PaymentInstructionsDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtPaymentInstructions by using auditHeaderDAO.addAudit(auditHeader)
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
		PaymentInstruction paymentInstruction = (PaymentInstruction) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (paymentInstruction.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (paymentInstruction.isNew()) {
			paymentInstruction.setId(Long.parseLong(getPaymentInstructionDAO().save(paymentInstruction, tableType)));
			auditHeader.getAuditDetail().setModelData(paymentInstruction);
			auditHeader.setAuditReference(String.valueOf(paymentInstruction.getPaymentInstructionId()));
		} else {
			getPaymentInstructionDAO().update(paymentInstruction, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PaymentInstructions by using PaymentInstructionsDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtPaymentInstructions by using auditHeaderDAO.addAudit(auditHeader)
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

		PaymentInstruction paymentInstruction = (PaymentInstruction) auditHeader.getAuditDetail().getModelData();
		getPaymentInstructionDAO().delete(paymentInstruction, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPaymentInstructions fetch the details by using PaymentInstructionsDAO's getPaymentInstructionsById method.
	 * 
	 * @param paymentInstructionId
	 *            paymentInstructionId of the PaymentInstruction.
	 * @return PaymentInstructions
	 */
	@Override
	public PaymentInstruction getPaymentInstruction(long paymentInstructionId) {
		return getPaymentInstructionDAO().getPaymentInstruction(paymentInstructionId, "_View");
	}

	/**
	 * getApprovedPaymentInstructionsById fetch the details by using PaymentInstructionsDAO's getPaymentInstructionsById
	 * method . with parameter id and type as blank. it fetches the approved records from the PaymentInstructions.
	 * 
	 * @param paymentInstructionId
	 *            paymentInstructionId of the PaymentInstruction. (String)
	 * @return PaymentInstructions
	 */
	public PaymentInstruction getApprovedPaymentInstruction(long paymentInstructionId) {
		return getPaymentInstructionDAO().getPaymentInstruction(paymentInstructionId, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPaymentInstructionDAO().delete with
	 * parameters paymentInstruction,"" b) NEW Add new record in to main table by using getPaymentInstructionDAO().save
	 * with parameters paymentInstruction,"" c) EDIT Update record in the main table by using
	 * getPaymentInstructionDAO().update with parameters paymentInstruction,"" 3) Delete the record from the workFlow
	 * table by using getPaymentInstructionDAO().delete with parameters paymentInstruction,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtPaymentInstructions by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtPaymentInstructions by using auditHeaderDAO.addAudit(auditHeader) based on
	 * the transaction Type.
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
		PaymentInstruction paymentInstruction = new PaymentInstruction();
		BeanUtils.copyProperties((PaymentInstruction) auditHeader.getAuditDetail().getModelData(), paymentInstruction);

		getPaymentInstructionDAO().delete(paymentInstruction, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentInstruction.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(paymentInstructionDAO.getPaymentInstruction(paymentInstruction.getPaymentInstructionId(), ""));
		}

		if (paymentInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPaymentInstructionDAO().delete(paymentInstruction, TableType.MAIN_TAB);
		} else {
			paymentInstruction.setRoleCode("");
			paymentInstruction.setNextRoleCode("");
			paymentInstruction.setTaskId("");
			paymentInstruction.setNextTaskId("");
			paymentInstruction.setWorkflowId(0);

			if (paymentInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				paymentInstruction.setRecordType("");
				getPaymentInstructionDAO().save(paymentInstruction, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				paymentInstruction.setRecordType("");
				getPaymentInstructionDAO().update(paymentInstruction, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(paymentInstruction);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPaymentInstructionDAO().delete with parameters paymentInstruction,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtPaymentInstructions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		PaymentInstruction paymentInstruction = (PaymentInstruction) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPaymentInstructionDAO().delete(paymentInstruction, TableType.TEMP_TAB);

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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPaymentInstructionDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<AuditDetail> setPaymentInstructionDetailsAuditData(PaymentInstruction paymentInstruction, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new PaymentInstruction(),
				new PaymentInstruction().getExcludeFields());

		if (StringUtils.isEmpty(paymentInstruction.getRecordType())) {
			return auditDetails;
		}

		boolean isRcdType = false;
		if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			paymentInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			paymentInstruction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			isRcdType = true;
		} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			paymentInstruction.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}
		if ("saveOrUpdate".equals(method) && isRcdType) {
			paymentInstruction.setNewRecord(true);
		}
		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}
		logger.debug("Leaving");
		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], paymentInstruction.getBefImage(),
				paymentInstruction));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processPaymentInstrDetails(List<AuditDetail> auditDetails, TableType type, String methodName) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			PaymentInstruction paymentInstruction = (PaymentInstruction) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			TableType tableType = TableType.TEMP_TAB;
			if (TableType.MAIN_TAB.equals(type)) {
				tableType = TableType.MAIN_TAB;
				approveRec = true;
				paymentInstruction.setRoleCode("");
				paymentInstruction.setNextRoleCode("");
				paymentInstruction.setTaskId("");
				paymentInstruction.setNextTaskId("");
				paymentInstruction.setWorkflowId(0);
			}
			if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (paymentInstruction.isNewRecord()) {
				saveRecord = true;
				if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					paymentInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					paymentInstruction.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					paymentInstruction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (paymentInstruction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (paymentInstruction.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = paymentInstruction.getRecordType();
				recordStatus = paymentInstruction.getRecordStatus();
				paymentInstruction.setRecordType("");
				paymentInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				paymentInstruction.setStatus(DisbursementConstants.STATUS_APPROVED);
			}
			if (saveRecord) {
				getPaymentInstructionDAO().save(paymentInstruction, tableType);
			}
			if (updateRecord) {
				getPaymentInstructionDAO().update(paymentInstruction, tableType);
			}
			if (deleteRecord) {
				getPaymentInstructionDAO().delete(paymentInstruction, tableType);
			}
			if (approveRec) {
				paymentInstruction.setRecordType(rcdType);
				paymentInstruction.setRecordStatus(recordStatus);
			}
			
			if ("doApprove".equals(methodName)) {
				if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentInstruction.getRecordType())) {
					paymentInstruction.setBefImage(paymentInstructionDAO.getPaymentInstruction(paymentInstruction.getPaymentInstructionId(), ""));
				}
			}
			auditDetails.get(i).setModelData(paymentInstruction);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(PaymentInstruction paymentInstruction, TableType tableType, String auditTranType, long paymentId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (paymentInstruction != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new PaymentInstruction(), new PaymentInstruction().getExcludeFields());
			if (StringUtils.isNotEmpty(paymentInstruction.getRecordType()) || StringUtils.isEmpty(tableType.toString())) {
				auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], paymentInstruction.getBefImage(), paymentInstruction));
			}
			getPaymentInstructionDAO().delete(paymentInstruction, tableType);
		}
		return auditDetails;
	}

	@Override
	public PaymentInstruction getPaymentInstructionDetails(long paymentId, String type) {
		return getPaymentInstructionDAO().getPaymentInstructionDetails(paymentId, type);
	}
}