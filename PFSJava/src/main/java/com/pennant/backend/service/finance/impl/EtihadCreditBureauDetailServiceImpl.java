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
 * FileName    		:  EtihadCreditBureauDetailServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.finance.EtihadCreditBureauDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.EtihadCreditBureauDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.EtihadCreditBureauDetailService;
import com.pennant.backend.service.finance.validation.EtihadCreditBureauDetailValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>EtihadCreditBureauDetail</b>.<br>
 * 
 */
public class EtihadCreditBureauDetailServiceImpl extends GenericService<EtihadCreditBureauDetail>
		implements EtihadCreditBureauDetailService {
	
	private static final Logger logger = Logger.getLogger(EtihadCreditBureauDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EtihadCreditBureauDetailDAO etihadCreditBureauDetailDAO;
	
	private EtihadCreditBureauDetailValidation etihadCreditBureauDetailValidation;

	public EtihadCreditBureauDetailServiceImpl() {
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

	public EtihadCreditBureauDetailDAO getEtihadCreditBureauDetailDAO() {
		return etihadCreditBureauDetailDAO;
	}
	public void setEtihadCreditBureauDetailDAO(EtihadCreditBureauDetailDAO etihadCreditBureauDetailDAO) {
		this.etihadCreditBureauDetailDAO = etihadCreditBureauDetailDAO;
	}
	
	/**
	 * @return the etihadCreditBureauDetailValidation
	 */
	public EtihadCreditBureauDetailValidation getEtihadCreditBureauDetailValidation() {
		if(etihadCreditBureauDetailValidation==null){
			this.etihadCreditBureauDetailValidation = new EtihadCreditBureauDetailValidation(etihadCreditBureauDetailDAO);
		}
		return this.etihadCreditBureauDetailValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method if there is
	 * 		any error or warning message then return the auditHeader. 
	 * 2) Do Add or Update the Record 
	 * 		a) Add new Record for the new record in the DB table 
	 * 			LMTEtihadCreditBureauDetail/LMTEtihadCreditBureauDetail_Temp by using EtihadCreditBureauDetailDAO's save method 
	 * 		b) Update the Record in the table. based on the module workFlow Configuration. 
	 * 			by using EtihadCreditBureauDetailDAO's update method 
	 * 3) Audit the record in to AuditHeader and AdtLMTEtihadCreditBureauDetail by using 
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
		EtihadCreditBureauDetail etihadCreditBureauDetail = (EtihadCreditBureauDetail) auditHeader.getAuditDetail().getModelData();

		if (etihadCreditBureauDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (etihadCreditBureauDetail.isNew()) {
			etihadCreditBureauDetail.setId(getEtihadCreditBureauDetailDAO().save(etihadCreditBureauDetail,tableType));
			auditHeader.getAuditDetail().setModelData(etihadCreditBureauDetail);
			auditHeader.setAuditReference(etihadCreditBureauDetail.getId());
		} else {
			getEtihadCreditBureauDetailDAO().update(etihadCreditBureauDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) delete Record for the DB table LMTEtihadCreditBureauDetail by using EtihadCreditBureauDetailDAO's 
	 * 		delete method with type as Blank 
	 * 3) Audit the record in to AuditHeader and AdtLMTEtihadCreditBureauDetail by using 
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

		EtihadCreditBureauDetail etihadCreditBureauDetail = (EtihadCreditBureauDetail) auditHeader.getAuditDetail().getModelData();
		getEtihadCreditBureauDetailDAO().delete(etihadCreditBureauDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getEtihadCreditBureauDetailById fetch the details by using EtihadCreditBureauDetailDAO's
	 * getEtihadCreditBureauDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EtihadCreditBureauDetail
	 */
	@Override
	public EtihadCreditBureauDetail getEtihadCreditBureauDetailById(String id,String type) {
		return getEtihadCreditBureauDetailDAO().getEtihadCreditBureauDetailByID(id,type);
	}

	/**
	 * getApprovedEtihadCreditBureauDetailById fetch the details by using
	 * EtihadCreditBureauDetailDAO's getEtihadCreditBureauDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * LMTEtihadCreditBureauDetail.
	 * 
	 * @param id
	 *            (String)
	 * @return EtihadCreditBureauDetail
	 */

	public EtihadCreditBureauDetail getApprovedEtihadCreditBureauDetailById(String id) {
		return getEtihadCreditBureauDetailDAO().getEtihadCreditBureauDetailByID(id, "_AView");
	}
	

	/**
	 * doApprove method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) based on the Record type do following actions 
	 * 		a) DELETE Delete the record from the main table by using 
	 * 			getEtihadCreditBureauDetailDAO().delete with parameters etihadCreditBureauDetail,"" 
	 * 		b) NEW Add new record in to main table by using getEtihadCreditBureauDetailDAO().save
	 * 			with parameters etihadCreditBureauDetail,"" 
	 * 		c) EDIT Update record in the main table by using 
	 * 			getEtihadCreditBureauDetailDAO().update with parameters etihadCreditBureauDetail,""
	 * 3) Delete the record from the workFlow table by using getEtihadCreditBureauDetailDAO().delete 
	 * 		with parameters etihadCreditBureauDetail,"_Temp" 
	 * 4) Audit the record in to AuditHeader and AdtLMTEtihadCreditBureauDetail by using
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow 
	 * 5) Audit the record in to AuditHeader and AdtLMTEtihadCreditBureauDetail by using
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

		EtihadCreditBureauDetail etihadCreditBureauDetail = new EtihadCreditBureauDetail();
		BeanUtils.copyProperties((EtihadCreditBureauDetail) auditHeader.getAuditDetail()
				.getModelData(), etihadCreditBureauDetail);

		if (etihadCreditBureauDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getEtihadCreditBureauDetailDAO().delete(etihadCreditBureauDetail, "");
		} else {
			etihadCreditBureauDetail.setRoleCode("");
			etihadCreditBureauDetail.setNextRoleCode("");
			etihadCreditBureauDetail.setTaskId("");
			etihadCreditBureauDetail.setNextTaskId("");
			etihadCreditBureauDetail.setWorkflowId(0);

			if (etihadCreditBureauDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				etihadCreditBureauDetail.setRecordType("");
				getEtihadCreditBureauDetailDAO().save(etihadCreditBureauDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				etihadCreditBureauDetail.setRecordType("");
				getEtihadCreditBureauDetailDAO().update(etihadCreditBureauDetail, "");
			}
		}

		getEtihadCreditBureauDetailDAO().delete(etihadCreditBureauDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(etihadCreditBureauDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 
	 * 1) Do the Business validation by using businessValidation(auditHeader) method 
	 * 		if there is any error or warning message then return the auditHeader. 
	 * 2) Delete the record from the workFlow table by using 
	 * 		getEtihadCreditBureauDetailDAO().delete with parameters etihadCreditBureauDetail,"_Temp" 
	 * 3) Audit the record in to AuditHeader and AdtLMTEtihadCreditBureauDetail by using 
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

		EtihadCreditBureauDetail etihadCreditBureauDetail = (EtihadCreditBureauDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getEtihadCreditBureauDetailDAO().delete(etihadCreditBureauDetail, "_Temp");

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
	 * 		getEtihadCreditBureauDetailDAO().getErrorDetail with Error ID and language as parameters. 
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
	public AuditDetail validate(EtihadCreditBureauDetail etihadCreditBureauDetail, String method, String auditTranType, String  usrLanguage){
		return doValidation(etihadCreditBureauDetail, auditTranType, method, usrLanguage);
	}

		
	@Override
	public AuditDetail saveOrUpdate(EtihadCreditBureauDetail etihadCreditBureauDetail, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(etihadCreditBureauDetail, etihadCreditBureauDetail.getExcludeFields());

		etihadCreditBureauDetail.setWorkflowId(0);
		if (etihadCreditBureauDetail.isNewRecord()) {
			getEtihadCreditBureauDetailDAO().save(etihadCreditBureauDetail, tableType);
		} else {
			getEtihadCreditBureauDetailDAO().update(etihadCreditBureauDetail, tableType);
		}

		logger.debug("Leaving");
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], etihadCreditBureauDetail.getBefImage(), etihadCreditBureauDetail);

	}
	
	@Override
	public AuditDetail doApprove(EtihadCreditBureauDetail etihadCreditBureauDetail, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(etihadCreditBureauDetail, etihadCreditBureauDetail.getExcludeFields());

		etihadCreditBureauDetail.setRoleCode("");
		etihadCreditBureauDetail.setNextRoleCode("");
		etihadCreditBureauDetail.setTaskId("");
		etihadCreditBureauDetail.setNextTaskId("");
		etihadCreditBureauDetail.setWorkflowId(0);

		getEtihadCreditBureauDetailDAO().save(etihadCreditBureauDetail, tableType);

		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], etihadCreditBureauDetail.getBefImage(), etihadCreditBureauDetail);
	}
	
	@Override
	public AuditDetail delete(EtihadCreditBureauDetail etihadCreditBureauDetail, String tableType, String auditTranType) {
		logger.debug("Entering");

		String[] fields = PennantJavaUtil.getFieldDetails(etihadCreditBureauDetail, etihadCreditBureauDetail.getExcludeFields());	

		getEtihadCreditBureauDetailDAO().delete(etihadCreditBureauDetail, tableType);

		logger.debug("Leaving");
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], etihadCreditBureauDetail.getBefImage(), etihadCreditBureauDetail);
	}
	
	
	
	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditDetail doValidation(EtihadCreditBureauDetail etihadCreditBureauDetail, String auditTranType, String method,String  usrLanguage){
		logger.debug("Entering");
		String[] fields = PennantJavaUtil.getFieldDetails(etihadCreditBureauDetail, etihadCreditBureauDetail.getExcludeFields());
		
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], etihadCreditBureauDetail.getBefImage(), etihadCreditBureauDetail);
		
		logger.debug("Leaving");
		return validate(auditDetail, usrLanguage, method);
	}
	
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		EtihadCreditBureauDetail etihadCreditBureauDetail = (EtihadCreditBureauDetail) auditDetail.getModelData();

		EtihadCreditBureauDetail tempEtihadCreditBureauDetail = null;
		if (etihadCreditBureauDetail.isWorkflow()) {
			tempEtihadCreditBureauDetail = getEtihadCreditBureauDetailDAO().getEtihadCreditBureauDetailByID(
					etihadCreditBureauDetail.getId(), "_Temp");
		}
		EtihadCreditBureauDetail befEtihadCreditBureauDetail = getEtihadCreditBureauDetailDAO().getEtihadCreditBureauDetailByID(
				etihadCreditBureauDetail.getId(), "");
		EtihadCreditBureauDetail oldEtihadCreditBureauDetail = etihadCreditBureauDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(etihadCreditBureauDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (etihadCreditBureauDetail.isNew()) { // for New record or new record into work flow

			if (!etihadCreditBureauDetail.isWorkflow()) {// With out Work flow only new
												// records
				if (befEtihadCreditBureauDetail != null) { // Record Already Exists in the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (etihadCreditBureauDetail.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befEtihadCreditBureauDetail != null || tempEtihadCreditBureauDetail != null) { 
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befEtihadCreditBureauDetail == null || tempEtihadCreditBureauDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!etihadCreditBureauDetail.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befEtihadCreditBureauDetail == null) { // if records not exists in the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldEtihadCreditBureauDetail != null
							&& !oldEtihadCreditBureauDetail.getLastMntOn().equals(
									befEtihadCreditBureauDetail.getLastMntOn())) {
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

				if (tempEtihadCreditBureauDetail == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempEtihadCreditBureauDetail != null && oldEtihadCreditBureauDetail != null
						&& !oldEtihadCreditBureauDetail.getLastMntOn().equals(
								tempEtihadCreditBureauDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !etihadCreditBureauDetail.isWorkflow()) {
			etihadCreditBureauDetail.setBefImage(befEtihadCreditBureauDetail);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}