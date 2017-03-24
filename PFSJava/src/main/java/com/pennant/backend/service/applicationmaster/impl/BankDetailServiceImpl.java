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
 * FileName    		:  BankDetailServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on
 * <b>BankDetail</b>.<br>
 * 
 */
public class BankDetailServiceImpl extends
		GenericService<BankDetail> implements BankDetailService {

	private static Logger logger = Logger.getLogger(BankDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BankDetailDAO bankDetailDAO;

	public BankDetailServiceImpl() {
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

	public BankDetailDAO getBankDetailDAO() {
		return bankDetailDAO;
	}

	public void setBankDetailDAO(
			BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTBankDetail/BMTBankDetail_Temp by using
	 * BankDetailDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using
	 * BankDetailDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTBankDetail by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		String tableType = "";
		BankDetail bankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();

		if (bankDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (bankDetail.isNew()) {
			bankDetail.setId(getBankDetailDAO().save(bankDetail, tableType));
			auditHeader.getAuditDetail().setModelData(bankDetail);
			auditHeader.setAuditReference(bankDetail.getId());
		} else {
			getBankDetailDAO().update(bankDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTBankDetail by using BankDetailDAO's
	 * delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtBMTBankDetail by using
	 * auditHeaderDAO.addAudit(auditHeader)
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
		BankDetail bankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();
		getBankDetailDAO().delete(bankDetail, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBankDetailById fetch the details by using
	 * BankDetailDAO's getBankDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BankDetail
	 */
	@Override
	public BankDetail getBankDetailById(String id) {
		return getBankDetailDAO().getBankDetailById(id,	"_View");
	}

	/**
	 * getApprovedBankDetailById fetch the details by using
	 * BankDetailDAO's getBankDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * BMTBankDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return BankDetail
	 */

	public BankDetail getApprovedBankDetailById(String id) {
		return getBankDetailDAO().getBankDetailById(id,	"_AView");
	}
	
	/**
	 * fetch Account No Length by bankCode .
	 * 
	 * @param bankCode
	 * @return AccNoLength
	 */
	@Override
	public int getAccNoLengthByCode(String bankCode) {
		return getBankDetailDAO().getAccNoLengthByCode(bankCode, "_View");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBankDetailDAO().delete with parameters
	 * bankDetail,"" b) NEW Add new record in to main table by using
	 * getBankDetailDAO().save with parameters
	 * bankDetail,"" c) EDIT Update record in the main table by
	 * using getBankDetailDAO().update with parameters
	 * bankDetail,"" 3) Delete the record from the workFlow table by
	 * using getBankDetailDAO().delete with parameters
	 * bankDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTBankDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTBankDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
		BankDetail bankDetail = new BankDetail();
		BeanUtils.copyProperties((BankDetail) auditHeader.getAuditDetail().getModelData(), bankDetail);

		if (bankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBankDetailDAO().delete(bankDetail, "");
		} else {
			bankDetail.setRoleCode("");
			bankDetail.setNextRoleCode("");
			bankDetail.setTaskId("");
			bankDetail.setNextTaskId("");
			bankDetail.setWorkflowId(0);

			if (bankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				bankDetail.setRecordType("");
				getBankDetailDAO().save(bankDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				bankDetail.setRecordType("");
				getBankDetailDAO().update(bankDetail,"");
			}
		}

		getBankDetailDAO().delete(bankDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(bankDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBankDetailDAO().delete with
	 * parameters bankDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTBankDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		BankDetail bankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBankDetailDAO().delete(bankDetail, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getBankDetailDAO().getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,	String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getBankDetailDAO().getErrorDetail with Error ID and language
	 * as parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,	String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		BankDetail bankDetail = (BankDetail) auditDetail.getModelData();
		BankDetail tempBankDetail = null;

		if (bankDetail.isWorkflow()) {
			tempBankDetail = getBankDetailDAO().getBankDetailById(bankDetail.getId(), "_Temp");
		}

		BankDetail befBankDetail = getBankDetailDAO().getBankDetailById(bankDetail.getId(),	"");
		BankDetail oldBankDetail = bankDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = bankDetail.getBankCode();
		errParm[0] = PennantJavaUtil.getLabel("label_BankCode") + ":"
				+ valueParm[0];

		if (bankDetail.isNew()) { // for New record or new record
			// into work flow

			if (!bankDetail.isWorkflow()) {// With out Work flow
				// only new records
				if (befBankDetail != null) { // Record Already
					// Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (bankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befBankDetail != null || tempBankDetail != null) { // if
						  						// records already exists
							 					// in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befBankDetail == null || tempBankDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!bankDetail.isWorkflow()) { // With out Work flow
				// for update and delete

				if (befBankDetail == null) { // if records not
					// exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldBankDetail != null
							&& !oldBankDetail.getLastMntOn().equals(befBankDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}

			} else {

				if (tempBankDetail == null) { // if records not
					// exists in the
					// Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempBankDetail != null && oldBankDetail != null
						&& !oldBankDetail.getLastMntOn().equals(tempBankDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !bankDetail.isWorkflow()) {
			auditDetail.setBefImage(befBankDetail);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}