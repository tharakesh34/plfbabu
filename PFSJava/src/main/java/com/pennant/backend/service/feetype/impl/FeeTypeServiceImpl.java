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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FeeType</b>.<br>
 * 
 */
public class FeeTypeServiceImpl extends GenericService<FeeType> implements FeeTypeService {
	private static final Logger	logger	= Logger.getLogger(FeeTypeServiceImpl.class);

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
		
		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (feeType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (feeType.isNew()) {
			feeType.setFeeTypeID(Long.parseLong(getFeeTypeDAO().save(feeType, tableType)));
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
		getFeeTypeDAO().delete(feeType, TableType.MAIN_TAB);

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
		
		
		getFeeTypeDAO().delete(feeType, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(feeType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(feeTypeDAO.getFeeTypeById(feeType.getFeeTypeID(), ""));
		}

		if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			
			getFeeTypeDAO().delete(feeType,TableType.MAIN_TAB);
			
		} else {
			feeType.setRoleCode("");
			feeType.setNextRoleCode("");
			feeType.setTaskId("");
			feeType.setNextTaskId("");
			feeType.setWorkflowId(0);

			if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feeType.setRecordType("");
				getFeeTypeDAO().save(feeType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feeType.setRecordType("");
				getFeeTypeDAO().update(feeType, TableType.MAIN_TAB);
			}
		}

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
		getFeeTypeDAO().delete(feeType, TableType.TEMP_TAB);

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
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
	
	 private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug("Entering");

			// Get the model object.
			FeeType feeType = (FeeType) auditDetail.getModelData();

			// Check the unique keys.
			if (feeType.isNew()
					&& feeTypeDAO.isDuplicateKey(feeType.getFeeTypeID(),feeType.getFeeTypeCode(), feeType.isWorkflow() ? TableType.BOTH_TAB
							: TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_FeeTypeCode") + ": " + feeType.getFeeTypeCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			logger.debug("Leaving");
			return auditDetail;
		}

	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode) {
		
		return feeTypeDAO.getFinFeeTypeIdByFeeType(feeTypeCode, "_View");
	}
}