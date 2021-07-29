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
 * FileName    		:  ExpenseTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.amtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.ExpenseTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeExpenseDAO;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.ExpenseTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ExpenseType</b>.<br>
 * 
 */
public class ExpenseTypeServiceImpl extends GenericService<ExpenseType> implements ExpenseTypeService {
	private static final Logger logger = LogManager.getLogger(ExpenseTypeServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private ExpenseTypeDAO expenseTypeDAO;
	private FinTypeExpenseDAO finTypeExpenseDAO;

	public ExpenseTypeServiceImpl() {
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

	public ExpenseTypeDAO getExpenseTypeDAO() {
		return expenseTypeDAO;
	}

	public void setExpenseTypeDAO(ExpenseTypeDAO expenseTypeDAO) {
		this.expenseTypeDAO = expenseTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * AMTExpenseType/AMTExpenseType_Temp by using ExpenseTypeDAO's save method b) Update the Record in the table. based
	 * on the module workFlow Configuration. by using ExpenseTypeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtAMTExpenseType by using auditHeaderDAO.addAudit(auditHeader)
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
		ExpenseType expenseType = (ExpenseType) auditHeader.getAuditDetail().getModelData();

		if (expenseType.isWorkflow()) {
			tableType = "_Temp";
		}

		if (expenseType.isNewRecord()) {
			expenseType.setId(getExpenseTypeDAO().save(expenseType, tableType));
			auditHeader.getAuditDetail().setModelData(expenseType);
			auditHeader.setAuditReference(String.valueOf(expenseType.getExpenseTypeId()));
		} else {
			getExpenseTypeDAO().update(expenseType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * AMTExpenseType by using ExpenseTypeDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtAMTExpenseType by using auditHeaderDAO.addAudit(auditHeader)
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

		ExpenseType expenseType = (ExpenseType) auditHeader.getAuditDetail().getModelData();
		getExpenseTypeDAO().delete(expenseType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getExpenseTypeById fetch the details by using ExpenseTypeDAO's getExpenseTypeById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ExpenseType
	 */

	@Override
	public ExpenseType getExpenseTypeById(long id) {
		return getExpenseTypeDAO().getExpenseTypeById(id, "_View");
	}

	/**
	 * getApprovedExpenseTypeById fetch the details by using ExpenseTypeDAO's getExpenseTypeById method . with parameter
	 * id and type as blank. it fetches the approved records from the AMTExpenseType.
	 * 
	 * @param id
	 *            (int)
	 * @return ExpenseType
	 */

	public ExpenseType getApprovedExpenseTypeById(long id) {
		return getExpenseTypeDAO().getExpenseTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getExpenseTypeDAO().delete with
	 * parameters expenseType,"" b) NEW Add new record in to main table by using getExpenseTypeDAO().save with
	 * parameters expenseType,"" c) EDIT Update record in the main table by using getExpenseTypeDAO().update with
	 * parameters expenseType,"" 3) Delete the record from the workFlow table by using getExpenseTypeDAO().delete with
	 * parameters expenseType,"_Temp" 4) Audit the record in to AuditHeader and AdtAMTExpenseType by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtAMTExpenseType by
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

		ExpenseType expenseType = new ExpenseType();
		BeanUtils.copyProperties((ExpenseType) auditHeader.getAuditDetail().getModelData(), expenseType);

		if (expenseType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getExpenseTypeDAO().delete(expenseType, "");

		} else {
			expenseType.setRoleCode("");
			expenseType.setNextRoleCode("");
			expenseType.setTaskId("");
			expenseType.setNextTaskId("");
			expenseType.setWorkflowId(0);

			if (expenseType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				expenseType.setRecordType("");
				getExpenseTypeDAO().save(expenseType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				expenseType.setRecordType("");
				getExpenseTypeDAO().update(expenseType, "");
			}
		}

		getExpenseTypeDAO().delete(expenseType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(expenseType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getExpenseTypeDAO().delete with parameters expenseType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtAMTExpenseType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ExpenseType expenseType = (ExpenseType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getExpenseTypeDAO().delete(expenseType, "_Temp");

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
	 * from getExpenseTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		// Get the model object.
		ExpenseType expenseType = (ExpenseType) auditDetail.getModelData();

		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_ExpenceTypeCode") + ": " + expenseType.getExpenseTypeCode();
		// Check the unique keys.
		if (expenseType.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(expenseType.getRecordType())
				&& expenseTypeDAO.isDuplicateKey(expenseType.getExpenseTypeId(), expenseType.getExpenseTypeCode(),
						expenseType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		// Checking Dependency Validation
		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(expenseType.getRecordType())) {
			// Expense Type
			boolean isExpenseExists = getFinTypeExpenseDAO()
					.expenseExistingFinTypeExpense(expenseType.getExpenseTypeId(), "_View");
			if (isExpenseExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public long getFinExpenseIdByExpType(String expTypeCode, String type) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return this.expenseTypeDAO.getFinExpenseIdByExpType(expTypeCode, type);
	}

	public FinTypeExpenseDAO getFinTypeExpenseDAO() {
		return finTypeExpenseDAO;
	}

	public void setFinTypeExpenseDAO(FinTypeExpenseDAO finTypeExpenseDAO) {
		this.finTypeExpenseDAO = finTypeExpenseDAO;
	}

	@Override
	public List<ExpenseType> getExpenseTypes() {
		return expenseTypeDAO.getExpenseTypes();
	}
}