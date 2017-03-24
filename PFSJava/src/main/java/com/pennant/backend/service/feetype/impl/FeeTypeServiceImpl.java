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
 * FileName    		:  FeeTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.service.feetype.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FeeType</b>.<br>
 * 
 */
public class FeeTypeServiceImpl extends GenericService<FeeType> implements FeeTypeService {
	private final static Logger	logger	= Logger.getLogger(FeeTypeServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private FeeTypeDAO			feeTypeDAO;

	public FeeTypeServiceImpl() {
		super();
	}

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
	 * @return the feeTypeDAO
	 */
	public FeeTypeDAO getFeeTypeDAO() {
		return feeTypeDAO;
	}

	/**
	 * @param feeTypeDAO
	 *            the feeTypeDAO to set
	 */
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FeeTypes/FeeTypes_Temp by using
	 * FeeTypesDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by using
	 * FeeTypesDAO's update method 3) Audit the record in to AuditHeader and AdtFeeTypes by using
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
		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();

		if (feeType.isWorkflow()) {
			tableType = "_Temp";
		}

		if (feeType.isNew()) {
			feeType.setId(getFeeTypeDAO().save(feeType, tableType));
			auditHeader.getAuditDetail().setModelData(feeType);
			auditHeader.setAuditReference(String.valueOf(feeType.getFeeTypeID()));
		} else {
			getFeeTypeDAO().update(feeType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FeeTypes by using FeeTypesDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFeeTypes by using auditHeaderDAO.addAudit(auditHeader)
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

		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();
		getFeeTypeDAO().delete(feeType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFeeTypesById fetch the details by using FeeTypesDAO's getFeeTypesById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FeeTypes
	 */
	@Override
	public FeeType getFeeTypeById(long id) {
		return getFeeTypeDAO().getFeeTypeById(id, "_View");
	}

	/**
	 * getApprovedFeeTypesById fetch the details by using FeeTypesDAO's getFeeTypesById method . with parameter id and
	 * type as blank. it fetches the approved records from the FeeTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return FeeTypes
	 */
	public FeeType getApprovedFeeTypeById(long id) {
		return getFeeTypeDAO().getFeeTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFeeTypeDAO().delete with parameters
	 * feeType,"" b) NEW Add new record in to main table by using getFeeTypeDAO().save with parameters feeType,"" c)
	 * EDIT Update record in the main table by using getFeeTypeDAO().update with parameters feeType,"" 3) Delete the
	 * record from the workFlow table by using getFeeTypeDAO().delete with parameters feeType,"_Temp" 4) Audit the
	 * record in to AuditHeader and AdtFeeTypes by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtFeeTypes by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FeeType feeType = new FeeType();
		BeanUtils.copyProperties((FeeType) auditHeader.getAuditDetail().getModelData(), feeType);

		if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFeeTypeDAO().delete(feeType, "");
		} else {
			feeType.setRoleCode("");
			feeType.setNextRoleCode("");
			feeType.setTaskId("");
			feeType.setNextTaskId("");
			feeType.setWorkflowId(0);

			if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feeType.setRecordType("");
				getFeeTypeDAO().save(feeType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feeType.setRecordType("");
				getFeeTypeDAO().update(feeType, "");
			}
		}

		getFeeTypeDAO().delete(feeType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(feeType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFeeTypeDAO().delete with parameters feeType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFeeTypes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFeeTypeDAO().delete(feeType, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

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
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFeeTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FeeType feeType = (FeeType) auditDetail.getModelData();

		FeeType tempFeeType = null;
		if (feeType.isWorkflow()) {
			tempFeeType = getFeeTypeDAO().getFeeTypeById(feeType.getId(), "_Temp");
		}
		FeeType befFeeType = getFeeTypeDAO().getFeeTypeById(feeType.getId(), "");

		FeeType oldFeeType = feeType.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(feeType.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FeeTypeID") + ":" + valueParm[0];

		if (feeType.isNew()) { // for New record or new record into work flow

			if (!feeType.isWorkflow()) {// With out Work flow only new records  
				if (befFeeType != null) { // Record Already Exists in the table then error  
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFeeType != null || tempFeeType != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFeeType == null || tempFeeType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!feeType.isWorkflow()) { // With out Work flow for update and delete

				if (befFeeType == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFeeType != null && !oldFeeType.getLastMntOn().equals(befFeeType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempFeeType == null) { // if records not exists in the Work flow table 
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFeeType != null && oldFeeType != null && !oldFeeType.getLastMntOn().equals(tempFeeType.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !feeType.isWorkflow()) {
			auditDetail.setBefImage(befFeeType);
		}

		return auditDetail;
	}

}