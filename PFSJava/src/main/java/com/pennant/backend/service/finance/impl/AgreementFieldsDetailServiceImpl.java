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
 * FileName    		:  BundledProductsDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.AgreementFieldsDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AgreementFieldDetails;
import com.pennant.backend.model.finance.BundledProductsDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.AgreementFieldsDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>BundledProductsDetail</b>.<br>
 * 
 */
public class AgreementFieldsDetailServiceImpl extends GenericService<AgreementFieldDetails>
		implements AgreementFieldsDetailService {
	private static final Logger logger = Logger.getLogger(AgreementFieldsDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AgreementFieldsDetailDAO agreementFieldsDetailDAO;

	public AgreementFieldsDetailServiceImpl() {
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

	public AgreementFieldsDetailDAO getAgreementFieldsDetailDAO() {
		return agreementFieldsDetailDAO;
	}

	public void setAgreementFieldsDetailDAO(
			AgreementFieldsDetailDAO agreementFieldsDetailDAO) {
		this.agreementFieldsDetailDAO = agreementFieldsDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method if there is
	 * 		any error or warning message then return the auditHeader. 
	 * 2) Do Add or Update the Record 
	 * 		a) Add new Record for the new record in the DB table 
	 * 			LMTBundledProductsDetail/LMTBundledProductsDetail_Temp by using BundledProductsDetailDAO's save method 
	 * 		b) Update the Record in the table. based on the module workFlow Configuration. 
	 * 			by using BundledProductsDetailDAO's update method 
	 * 3) Audit the record in to AuditHeader and AdtLMTBundledProductsDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)
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
		AgreementFieldDetails agreementFieldDetails = (AgreementFieldDetails) auditHeader.getAuditDetail().getModelData();

		if (agreementFieldDetails.isWorkflow()) {
			tableType = "_Temp";
		}

		if (agreementFieldDetails.isNew()) {
			agreementFieldDetails.setId(getAgreementFieldsDetailDAO().save(agreementFieldDetails,tableType));
			auditHeader.getAuditDetail().setModelData(agreementFieldDetails);
			auditHeader.setAuditReference(agreementFieldDetails.getId());
		} else {
			getAgreementFieldsDetailDAO().update(agreementFieldDetails, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) delete Record for the DB table LMTBundledProductsDetail by using BundledProductsDetailDAO's 
	 * 		delete method with type as Blank 
	 * 3) Audit the record in to AuditHeader and AdtLMTBundledProductsDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)
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

		AgreementFieldDetails agreementFieldDetails = (AgreementFieldDetails) auditHeader.getAuditDetail().getModelData();
		getAgreementFieldsDetailDAO().delete(agreementFieldDetails, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBundledProductsDetailById fetch the details by using BundledProductsDetailDAO's
	 * getBundledProductsDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BundledProductsDetail
	 */
	

	/**
	 * getApprovedBundledProductsDetailById fetch the details by using
	 * BundledProductsDetailDAO's getBundledProductsDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * LMTBundledProductsDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return BundledProductsDetail
	 */

	public AgreementFieldDetails getApprovedBundledProductsDetailById(String id) {
		return getAgreementFieldsDetailDAO().getAgreementFieldsDetailByID(id, "_AView");
	}
	

	/**
	 * doApprove method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) based on the Record type do following actions 
	 * 		a) DELETE Delete the record from the main table by using 
	 * 			getBundledProductsDetailDAO().delete with parameters bundledProductsDetail,"" 
	 * 		b) NEW Add new record in to main table by using getBundledProductsDetailDAO().save
	 * 			with parameters bundledProductsDetail,"" 
	 * 		c) EDIT Update record in the main table by using 
	 * 			getBundledProductsDetailDAO().update with parameters bundledProductsDetail,""
	 * 3) Delete the record from the workFlow table by using getBundledProductsDetailDAO().delete 
	 * 		with parameters bundledProductsDetail,"_Temp" 
	 * 4) Audit the record in to AuditHeader and AdtLMTBundledProductsDetail by using
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow 
	 * 5) Audit the record in to AuditHeader and AdtLMTBundledProductsDetail by using
	 * 		auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		AgreementFieldDetails agreementFieldDetails = new AgreementFieldDetails();
		BeanUtils.copyProperties((BundledProductsDetail) auditHeader.getAuditDetail()
				.getModelData(), agreementFieldDetails);

		if (agreementFieldDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAgreementFieldsDetailDAO().delete(agreementFieldDetails, "");
		} else {
			agreementFieldDetails.setRoleCode("");
			agreementFieldDetails.setNextRoleCode("");
			agreementFieldDetails.setTaskId("");
			agreementFieldDetails.setNextTaskId("");
			agreementFieldDetails.setWorkflowId(0);

			if (agreementFieldDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				agreementFieldDetails.setRecordType("");
				getAgreementFieldsDetailDAO().save(agreementFieldDetails, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				agreementFieldDetails.setRecordType("");
				getAgreementFieldsDetailDAO().update(agreementFieldDetails, "");
			}
		}

		getAgreementFieldsDetailDAO().delete(agreementFieldDetails, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(agreementFieldDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) Delete the record from the workFlow table by using 
	 * 		getBundledProductsDetailDAO().delete with parameters bundledProductsDetail,"_Temp" 
	 * 3) Audit the record in to AuditHeader and AdtLMTBundledProductsDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AgreementFieldDetails agreementFieldDetails = (AgreementFieldDetails) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAgreementFieldsDetailDAO().delete(agreementFieldDetails, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 
	 * 1) get the details from the auditHeader.
	 * 2) fetch the details from the tables 
	 * 3) Validate the Record based on the record details. 
	 * 4) Validate for any business validation. 
	 * 5) for any mismatch conditions Fetch the error details from
	 * 		getBundledProductsDetailDAO().getErrorDetail with Error ID and language as parameters. 
	 * 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
		logger.debug("Entering");
		auditHeader = doValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	

		
	@Override
	public AuditDetail saveOrUpdate(AgreementFieldDetails agreementFieldDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(agreementFieldDetails, agreementFieldDetails.getExcludeFields());

		agreementFieldDetails.setWorkflowId(0);
		if (agreementFieldDetails.isNewRecord()) {
			getAgreementFieldsDetailDAO().save(agreementFieldDetails, tableType);
		} else {
			getAgreementFieldsDetailDAO().update(agreementFieldDetails, tableType);
		}

		logger.debug("Leaving");
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], agreementFieldDetails.getBefImage(), agreementFieldDetails);

	}
	
	@Override
	public AuditDetail doApprove(AgreementFieldDetails agreementFieldDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(agreementFieldDetails, agreementFieldDetails.getExcludeFields());

		agreementFieldDetails.setRoleCode("");
		agreementFieldDetails.setNextRoleCode("");
		agreementFieldDetails.setTaskId("");
		agreementFieldDetails.setNextTaskId("");
		agreementFieldDetails.setWorkflowId(0);

		getAgreementFieldsDetailDAO().save(agreementFieldDetails, tableType);

		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], agreementFieldDetails.getBefImage(), agreementFieldDetails);
	}
	
	@Override
	public AuditDetail delete(AgreementFieldDetails agreementFieldDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(agreementFieldDetails, agreementFieldDetails.getExcludeFields());	

		getAgreementFieldsDetailDAO().delete(agreementFieldDetails, tableType);

		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], agreementFieldDetails.getBefImage(), agreementFieldDetails);
	}
	
	
	
	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditDetail doValidation(BundledProductsDetail bundledProductsDetail, String auditTranType, String method,String  usrLanguage){
		logger.debug("Entering");
		String[] fields = PennantJavaUtil.getFieldDetails(bundledProductsDetail, bundledProductsDetail.getExcludeFields());
		
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], bundledProductsDetail.getBefImage(), bundledProductsDetail);
		
		logger.debug("Leaving");
		return validate(auditDetail, usrLanguage, method);
	}
	
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		AgreementFieldDetails agreementFieldDetails = (AgreementFieldDetails) auditDetail.getModelData();

		AgreementFieldDetails tempAgreementFieldDetails = null;
		if (agreementFieldDetails.isWorkflow()) {
			tempAgreementFieldDetails = getAgreementFieldsDetailDAO().getAgreementFieldsDetailByID(
					agreementFieldDetails.getId(), "_Temp");
		}
		AgreementFieldDetails befAgreementFieldDetails = getAgreementFieldsDetailDAO().getAgreementFieldsDetailByID(
				agreementFieldDetails.getId(), "");
		AgreementFieldDetails oldAgreementFieldDetails = agreementFieldDetails.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(agreementFieldDetails.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (agreementFieldDetails.isNew()) { // for New record or new record into work flow

			if (!agreementFieldDetails.isWorkflow()) {// With out Work flow only new
												// records
				if (agreementFieldDetails != null) { // Record Already Exists in the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (agreementFieldDetails.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befAgreementFieldDetails != null || tempAgreementFieldDetails != null) { 
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befAgreementFieldDetails == null || tempAgreementFieldDetails != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!agreementFieldDetails.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befAgreementFieldDetails == null) { // if records not exists in the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldAgreementFieldDetails != null
							&& !oldAgreementFieldDetails.getLastMntOn().equals(
									befAgreementFieldDetails.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD,"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD,"41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempAgreementFieldDetails == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempAgreementFieldDetails != null && oldAgreementFieldDetails != null
						&& !oldAgreementFieldDetails.getLastMntOn().equals(
								tempAgreementFieldDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !agreementFieldDetails.isWorkflow()) {
			agreementFieldDetails.setBefImage(befAgreementFieldDetails);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public AgreementFieldDetails getAgreementFieldDetailsById(String id,String type) {
		return getAgreementFieldsDetailDAO().getAgreementFieldsDetailByID(id,type);
	}

	@Override
	public AgreementFieldDetails getApprovedAgreementFieldDetailsById(String id) {
		return getAgreementFieldsDetailDAO().getAgreementFieldsDetailByID(id, "_AView");
	}

	@Override
	public AuditDetail validate(AgreementFieldDetails agreementFieldDetails,
			String method, String auditTranType, String usrLanguage) {
		return doValidation(agreementFieldDetails, auditTranType, method, usrLanguage);
	}

	public AuditDetail doValidation(AgreementFieldDetails agreementFieldDetails, String auditTranType, String method,String  usrLanguage){
		logger.debug("Entering");
		String[] fields = PennantJavaUtil.getFieldDetails(agreementFieldDetails, agreementFieldDetails.getExcludeFields());
		
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], agreementFieldDetails.getBefImage(), agreementFieldDetails);
		
		logger.debug("Leaving");
		return validate(auditDetail, usrLanguage, method);
	}
}