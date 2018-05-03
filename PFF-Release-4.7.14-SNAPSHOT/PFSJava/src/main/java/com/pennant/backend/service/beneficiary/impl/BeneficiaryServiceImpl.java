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
 * FileName    		:  BeneficiaryServiceImpl.java                                         	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2016    														*
 *                                                                  						*
 * Modified Date    :  01-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2016       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 * 31-10-2017       Sriram	                 0.2          PSD Ticket ID : 124632            *                                                                           * 
 *                                                        Dependency validation on Account  * 
 *                                                        Number And Branch changed to      * 
 *                                                        Account Number And CustCIF        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.beneficiary.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.beneficiary.BeneficiaryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>Beneficiary</b>.<br>
 * 
 */
public class BeneficiaryServiceImpl extends GenericService<Beneficiary> implements BeneficiaryService {
	private static final Logger logger = Logger.getLogger(BeneficiaryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BeneficiaryDAO beneficiaryDAO;

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
	 * @return the beneficiaryDAO
	 */
	public BeneficiaryDAO getBeneficiaryDAO() {
		return beneficiaryDAO;
	}

	/**
	 * @param beneficiaryDAO
	 *            the beneficiaryDAO to set
	 */
	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table BMTAcademics/BMTAcademics_Temp
	 * by using AcademicDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using AcademicDAO's update method 3) Audit the record in to AuditHeader and AdtBMTAcademics by using
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
		Beneficiary beneficiary = (Beneficiary) auditHeader.getAuditDetail().getModelData();

		if (beneficiary.isWorkflow()) {
			tableType = "_Temp";
		}

		if (beneficiary.isNew()) {
			beneficiary.setId(getBeneficiaryDAO().save(beneficiary, tableType));
			auditHeader.getAuditDetail().setModelData(beneficiary);
			auditHeader.setAuditReference(String.valueOf(beneficiary.getBeneficiaryId()));
		} else {
			getBeneficiaryDAO().update(beneficiary, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Beneficiary by using BeneficiaryDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBeneficiary by using auditHeaderDAO.addAudit(auditHeader)
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

		Beneficiary beneficiary = (Beneficiary) auditHeader.getAuditDetail().getModelData();
		getBeneficiaryDAO().delete(beneficiary, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBeneficiaryById fetch the details by using BeneficiaryDAO's getBeneficiaryById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Beneficiary
	 */

	@Override
	public Beneficiary getBeneficiaryById(long id) {
		return getBeneficiaryDAO().getBeneficiaryById(id, "_View");
	}

	/**
	 * getApprovedBeneficiaryById fetch the details by using BeneficiaryDAO's getBeneficiaryById method . with parameter
	 * id and type as blank. it fetches the approved records from the Beneficiary.
	 * 
	 * @param id
	 *            (int)
	 * @return Beneficiary
	 */

	public Beneficiary getApprovedBeneficiaryById(long id) {
		return getBeneficiaryDAO().getBeneficiaryById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getBeneficiaryDAO().delete with
	 * parameters beneficiary,"" b) NEW Add new record in to main table by using getBeneficiaryDAO().save with
	 * parameters beneficiary,"" c) EDIT Update record in the main table by using getBeneficiaryDAO().update with
	 * parameters beneficiary,"" 3) Delete the record from the workFlow table by using getBeneficiaryDAO().delete with
	 * parameters beneficiary,"_Temp" 4) Audit the record in to AuditHeader and AdtBeneficiary by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtBeneficiary by
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
			return auditHeader;
		}

		Beneficiary beneficiary = new Beneficiary();
		BeanUtils.copyProperties((Beneficiary) auditHeader.getAuditDetail().getModelData(), beneficiary);

		if (beneficiary.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getBeneficiaryDAO().delete(beneficiary, "");

		} else {
			beneficiary.setRoleCode("");
			beneficiary.setNextRoleCode("");
			beneficiary.setTaskId("");
			beneficiary.setNextTaskId("");
			beneficiary.setWorkflowId(0);

			if (beneficiary.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				beneficiary.setRecordType("");
				beneficiary.setBeneficiaryId(getBeneficiaryDAO().save(beneficiary, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				beneficiary.setRecordType("");
				getBeneficiaryDAO().update(beneficiary, "");
			}
		}
		if (!StringUtils.equals(beneficiary.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getBeneficiaryDAO().delete(beneficiary, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(beneficiary);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getBeneficiaryDAO().delete with parameters beneficiary,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBeneficiary by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Beneficiary beneficiary = (Beneficiary) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBeneficiaryDAO().delete(beneficiary, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * Get the BeneficiaryList of the given CustomerId
	 * 
	 * @param custID
	 * @return
	 */
	@Override
	public List<Beneficiary> getApprovedBeneficiaryByCustomerId(long custID) {
		return getBeneficiaryDAO().getApprovedBeneficiaryByCustomerId(custID, "_AView");
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

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getBeneficiaryDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Beneficiary beneficiary = (Beneficiary) auditDetail.getModelData();

		Beneficiary tempBeneficiary = null;
		if (beneficiary.isWorkflow()) {
			tempBeneficiary = getBeneficiaryDAO().getBeneficiaryById(beneficiary.getId(), "_Temp");
		}
		Beneficiary befBeneficiary = getBeneficiaryDAO().getBeneficiaryById(beneficiary.getId(), "");

		Beneficiary oldBeneficiary = beneficiary.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = beneficiary.getAccNumber();
		valueParm[1] = String.valueOf(beneficiary.getBankBranchID());
		errParm[0] = PennantJavaUtil.getLabel("label_AccNumber") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_BankBranchID") + ":" + valueParm[1];

		if (beneficiary.isNew()) { // for New record or new record into work flow

			if (!beneficiary.isWorkflow()) {// With out Work flow only new records
				if (befBeneficiary != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (beneficiary.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befBeneficiary != null || tempBeneficiary != null) { // if records already exists in the main
																				// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befBeneficiary == null || tempBeneficiary != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!beneficiary.isWorkflow()) { // With out Work flow for update and delete

				if (befBeneficiary == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldBeneficiary != null && !oldBeneficiary.getLastMntOn().equals(befBeneficiary.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempBeneficiary == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempBeneficiary != null && oldBeneficiary != null && !oldBeneficiary.getLastMntOn().equals(tempBeneficiary.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}
		
		//### 31-10-2017 -Ticket ID: 124632 - Start
		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& !PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(beneficiary.getRecordType())) {

			int countDuplicates = getBeneficiaryDAO().getBeneficiaryByAccNo(beneficiary, "_View");

			if (countDuplicates > 0) {
				String[] errParm1 = new String[3];
				String[] valueParm1 = new String[3];
				valueParm1[0] = beneficiary.getAccNumber();
				valueParm1[1] = beneficiary.getiFSC();
				valueParm1[2] = beneficiary.getCustCIF();

				errParm1[0] = PennantJavaUtil.getLabel("label_AccNumber") + " : " + valueParm1[0];
				errParm1[1] = PennantJavaUtil.getLabel("label_BeneficiaryDialog_BankBranchID.value") + " : " + valueParm1[1];
				errParm1[2] = PennantJavaUtil.getLabel("label_CustCIF") + " : " + valueParm1[2];

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41021", errParm1, valueParm1));
			}
		}
		//### 31-10-2017 -END
		
		if (beneficiary.isDefaultBeneficiary() && !StringUtils.equals(method, PennantConstants.method_doReject)
				&& !PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(beneficiary.getRecordType())) {

			int countDuplicates = getBeneficiaryDAO().getDefaultsBeneficiary(beneficiary.getCustID(), beneficiary.getBeneficiaryId(), "_View");

			if (countDuplicates > 0) {
				String[] errParm2 = new String[1];
				String[] valueParm2 = new String[1];
				valueParm2[0] = beneficiary.getCustCIF();
				
				errParm2[0] = PennantJavaUtil.getLabel("label_CustCIF") + " : " + valueParm2[0];

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41010", errParm2, valueParm2));
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !beneficiary.isWorkflow()) {
			auditDetail.setBefImage(befBeneficiary);
		}

		return auditDetail;
	}

}