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
 * FileName    		:  CasteServiceImpl.java                                                * 	  
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

package com.pennant.backend.service.systemmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.systemmasters.CasteDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Caste;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.CasteService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Caste</b>.<br>
 * 
 */
public class CasteServiceImpl extends GenericService<Caste>
		implements CasteService {

	private static Logger logger = Logger.getLogger(CasteServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CasteDAO casteDAO;
	private CustomerDAO customerDAO;

	public CasteServiceImpl() {
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

	public CasteDAO getCasteDAO() {
		return casteDAO;
	}

	public void setCasteDAO(CasteDAO casteDAO) {
		this.casteDAO = casteDAO;
	}

	public void setCustomerAddresDAO(CasteDAO casteDAO) {
		this.casteDAO = casteDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTCastes/BMTCastes_Temp by using CasteDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CasteDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTCastes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * 
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
		
		Caste caste = (Caste) auditHeader.getAuditDetail()
				.getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (caste.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (caste.isNew()) {
			caste.setId(getCasteDAO().save(caste, tableType));
			auditHeader.getAuditDetail().setModelData(caste);
			auditHeader.setAuditReference(caste.getCasteCode());
		} else {
			getCasteDAO().update(caste, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTCastes by using CasteDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTCastes by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		Caste caste = (Caste) auditHeader.getAuditDetail()
				.getModelData();
		getCasteDAO().delete(caste, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCasteById fetch the details by using CasteDAO's
	 * getCasteById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Caste
	 */
	@Override
	public Caste getCasteById(long id) {
		return getCasteDAO().getCasteById(id, "_View");
	}

	/**
	 * getApprovedCasteById fetch the details by using CasteDAO's
	 * getCasteById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTCastes.
	 * 
	 * @param id
	 *            (String)
	 * @return Caste
	 */
	public Caste getApprovedCasteById(long id) {
		return getCasteDAO().getCasteById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCasteDAO().delete with parameters caste,"" b) NEW
	 * Add new record in to main table by using getCasteDAO().save with
	 * parameters caste,"" c) EDIT Update record in the main table by
	 * using getCasteDAO().update with parameters caste,"" 3) Delete
	 * the record from the workFlow table by using getCasteDAO().delete
	 * with parameters caste,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtBMTCastes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTCastes by
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
			logger.debug("Leaving");
			return auditHeader;
		}

		Caste caste = new Caste();
		BeanUtils.copyProperties((Caste) auditHeader.getAuditDetail()
				.getModelData(), caste);
		
		getCasteDAO().delete(caste, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(caste.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(casteDAO.getCasteById(caste.getCasteId(), ""));
		}
		
		if (caste.getRecordType()
				.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCasteDAO().delete(caste, TableType.MAIN_TAB);
		} else {
			caste.setRoleCode("");
			caste.setNextRoleCode("");
			caste.setTaskId("");
			caste.setNextTaskId("");
			caste.setWorkflowId(0);

			if (caste.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				caste.setRecordType("");
				getCasteDAO().save(caste, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				caste.setRecordType("");
				getCasteDAO().update(caste, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(caste);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCasteDAO().delete with parameters
	 * caste,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTCastes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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
		Caste caste = (Caste) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCasteDAO().delete(caste, TableType.TEMP_TAB);
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCasteDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		Caste caste = (Caste) auditDetail.getModelData();
		String code = caste.getCasteCode();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_CasteCode") + ": " + code;

		// Check the unique keys.
		if (caste.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(caste.getRecordType())
				&& casteDAO.isDuplicateKey(code, caste.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		
		if (StringUtils.trimToEmpty(caste.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			boolean exist = this.customerDAO.isCasteExist(caste.getCasteId(), "_View");
			if (exist) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null), usrLanguage));
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
}