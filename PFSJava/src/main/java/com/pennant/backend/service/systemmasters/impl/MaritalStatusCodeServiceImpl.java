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
 * FileName    		:  MaritalStatusCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.MaritalStatusCodeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.MaritalStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>MaritalStatusCode</b>.<br>
 * 
 */
public class MaritalStatusCodeServiceImpl extends GenericService<MaritalStatusCode> implements MaritalStatusCodeService {

	private static Logger logger = Logger.getLogger(MaritalStatusCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private MaritalStatusCodeDAO maritalStatusCodeDAO;

	public MaritalStatusCodeServiceImpl() {
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

	public MaritalStatusCodeDAO getMaritalStatusCodeDAO() {
		return maritalStatusCodeDAO;
	}

	public void setMaritalStatusCodeDAO(MaritalStatusCodeDAO maritalStatusCodeDAO) {
		this.maritalStatusCodeDAO = maritalStatusCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTMaritalStatusCodes/BMTMaritalStatusCodes_Temp by using
	 * MaritalStatusCodeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * MaritalStatusCodeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTMaritalStatusCodes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		MaritalStatusCode maritalStatusCode = (MaritalStatusCode) auditHeader
		.getAuditDetail().getModelData();

		if (maritalStatusCode.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}

		if (maritalStatusCode.isNew()) {
			maritalStatusCode.setId(getMaritalStatusCodeDAO().save(maritalStatusCode, tableType));
			auditHeader.getAuditDetail().setModelData(maritalStatusCode);
			auditHeader.setAuditReference(maritalStatusCode.getId());
		} else {
			getMaritalStatusCodeDAO().update(maritalStatusCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTMaritalStatusCodes by using MaritalStatusCodeDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTMaritalStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		MaritalStatusCode maritalStatusCode = (MaritalStatusCode) auditHeader
		.getAuditDetail().getModelData();

		getMaritalStatusCodeDAO().delete(maritalStatusCode, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getMaritalStatusCodeById fetch the details by using
	 * MaritalStatusCodeDAO's getMaritalStatusCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MaritalStatusCode
	 */
	@Override
	public MaritalStatusCode getMaritalStatusCodeById(String id) {
		return getMaritalStatusCodeDAO().getMaritalStatusCodeById(id, "_View");
	}

	/**
	 * getApprovedMaritalStatusCodeById fetch the details by using
	 * MaritalStatusCodeDAO's getMaritalStatusCodeById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * BMTMaritalStatusCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return MaritalStatusCode
	 */
	public MaritalStatusCode getApprovedMaritalStatusCodeById(String id) {
		return getMaritalStatusCodeDAO().getMaritalStatusCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getMaritalStatusCodeDAO().delete with parameters
	 * maritalStatusCode,"" b) NEW Add new record in to main table by using
	 * getMaritalStatusCodeDAO().save with parameters maritalStatusCode,"" c)
	 * EDIT Update record in the main table by using
	 * getMaritalStatusCodeDAO().update with parameters maritalStatusCode,"" 3)
	 * Delete the record from the workFlow table by using
	 * getMaritalStatusCodeDAO().delete with parameters
	 * maritalStatusCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTMaritalStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTMaritalStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {

		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		MaritalStatusCode maritalStatusCode = new MaritalStatusCode();
		BeanUtils.copyProperties((MaritalStatusCode) auditHeader.getAuditDetail().getModelData(), maritalStatusCode);
		
		getMaritalStatusCodeDAO().delete(maritalStatusCode, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(maritalStatusCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					maritalStatusCodeDAO.getMaritalStatusCodeById(maritalStatusCode.getId(),""));
		}
		
		if (maritalStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getMaritalStatusCodeDAO().delete(maritalStatusCode, TableType.MAIN_TAB);

		} else {
			maritalStatusCode.setRoleCode("");
			maritalStatusCode.setNextRoleCode("");
			maritalStatusCode.setTaskId("");
			maritalStatusCode.setNextTaskId("");
			maritalStatusCode.setWorkflowId(0);

			if (maritalStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				maritalStatusCode.setRecordType("");
				getMaritalStatusCodeDAO().save(maritalStatusCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				maritalStatusCode.setRecordType("");
				getMaritalStatusCodeDAO().update(maritalStatusCode, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(maritalStatusCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getMaritalStatusCodeDAO().delete with
	 * parameters maritalStatusCode,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTMaritalStatusCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		MaritalStatusCode maritalStatusCode = (MaritalStatusCode) auditHeader
		.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getMaritalStatusCodeDAO().delete(maritalStatusCode, TableType.TEMP_TAB);

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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getMaritalStatusCodeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		MaritalStatusCode maritalStatusCode = (MaritalStatusCode) auditDetail.getModelData();
		// Check the unique keys.
		if (maritalStatusCode.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(maritalStatusCode.getRecordType())
				&& maritalStatusCodeDAO.isDuplicateKey(maritalStatusCode.getMaritalStsCode(),
						maritalStatusCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];

			parameters[0] = PennantJavaUtil.getLabel("label_MaritalStsCode") + ":"+ maritalStatusCode.getMaritalStsCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001", parameters, null));
		}
		
		if (maritalStatusCode.isSystemDefault()) {
			String dftMaritalStsCode = getMaritalStatusCodeDAO().getSystemDefaultCount(maritalStatusCode.getMaritalStsCode());
			if (StringUtils.isNotEmpty(dftMaritalStsCode)) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60501",
						new String[]{dftMaritalStsCode,PennantJavaUtil.getLabel("MaritalStatusCode")}, null));
			}
        }

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}