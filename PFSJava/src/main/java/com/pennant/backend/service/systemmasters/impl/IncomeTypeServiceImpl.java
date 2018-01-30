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
 * FileName    		:  IncomeTypeServiceImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.IncomeTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>IncomeType</b>.<br>
 * 
 */
public class IncomeTypeServiceImpl extends GenericService<IncomeType> implements IncomeTypeService {

	private static Logger logger = Logger.getLogger(IncomeTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private IncomeTypeDAO incomeTypeDAO;

	public IncomeTypeServiceImpl() {
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

	public IncomeTypeDAO getIncomeTypeDAO() {
		return incomeTypeDAO;
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTIncomeTypes/BMTIncomeTypes_Temp by using IncomeTypeDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using IncomeTypeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTIncomeTypes by using
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
		
		IncomeType incomeType = (IncomeType) auditHeader.getAuditDetail()
				.getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (incomeType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (incomeType.isNew()) {
			incomeType.setId(getIncomeTypeDAO().save(incomeType, tableType));
			auditHeader.getAuditDetail().setModelData(incomeType);
			auditHeader.setAuditReference(incomeType.getId());
		} else {
			getIncomeTypeDAO().update(incomeType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTIncomeTypes by using IncomeTypeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTIncomeTypes by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		IncomeType incomeType = (IncomeType) auditHeader.getAuditDetail()
				.getModelData();

		getIncomeTypeDAO().delete(incomeType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getIncomeTypeById fetch the details by using IncomeTypeDAO's
	 * getIncomeTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return IncomeType
	 */
	@Override
	public IncomeType getIncomeTypeById(String id, String incomeExpense, String category) {
		return getIncomeTypeDAO().getIncomeTypeById(id,incomeExpense,category, "_View");
	}

	/**
	 * getApprovedIncomeTypeById fetch the details by using IncomeTypeDAO's
	 * getIncomeTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTIncomeTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return IncomeType
	 */
	public IncomeType getApprovedIncomeTypeById(String id, String incomeExpense, String category) {
		return getIncomeTypeDAO().getIncomeTypeById(id,incomeExpense ,category,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getIncomeTypeDAO().delete with parameters incomeType,"" b) NEW Add
	 * new record in to main table by using getIncomeTypeDAO().save with
	 * parameters incomeType,"" c) EDIT Update record in the main table by using
	 * getIncomeTypeDAO().update with parameters incomeType,"" 3) Delete the
	 * record from the workFlow table by using getIncomeTypeDAO().delete with
	 * parameters incomeType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTIncomeTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTIncomeTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
		IncomeType incomeType = new IncomeType();
		BeanUtils.copyProperties((IncomeType) auditHeader.getAuditDetail()
				.getModelData(), incomeType);
		
		getIncomeTypeDAO().delete(incomeType, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(incomeType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(incomeTypeDAO.getIncomeTypeById(incomeType.getId(), incomeType.getIncomeExpense(), incomeType.getCategory(), ""));
		}

		if (incomeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getIncomeTypeDAO().delete(incomeType, TableType.MAIN_TAB);
		} else { 
			incomeType.setRoleCode("");
			incomeType.setNextRoleCode("");
			incomeType.setTaskId("");
			incomeType.setNextTaskId("");
			incomeType.setWorkflowId(0);

			if (incomeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				incomeType.setRecordType("");
				getIncomeTypeDAO().save(incomeType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				incomeType.setRecordType("");
				getIncomeTypeDAO().update(incomeType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(incomeType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getIncomeTypeDAO().delete with parameters
	 * incomeType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTIncomeTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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
		
		IncomeType incomeType = (IncomeType) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getIncomeTypeDAO().delete(incomeType, TableType.TEMP_TAB);

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
	 * getIncomeTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		IncomeType incomeType = (IncomeType) auditDetail.getModelData();

		// Check the unique keys.
		if (incomeType.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(incomeType.getRecordType())
				&& incomeTypeDAO.isDuplicateKey(incomeType.getIncomeTypeCode(), incomeType.getIncomeExpense(),
						incomeType.getCategory(), incomeType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_IncomeTypeCode") + ":" + incomeType.getIncomeTypeCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
	
}