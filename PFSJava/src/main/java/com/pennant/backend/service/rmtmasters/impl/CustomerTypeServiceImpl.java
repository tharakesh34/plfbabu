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
 * FileName    		:  CustomerTypeServiceImpl.java                                                   * 	  
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
package com.pennant.backend.service.rmtmasters.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.CustomerTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CustomerType</b>.<br>
 * 
 */
public class CustomerTypeServiceImpl extends GenericService<CustomerType> implements CustomerTypeService {
	private static Logger logger = LogManager.getLogger(CustomerTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerTypeDAO customerTypeDAO;

	public CustomerTypeServiceImpl() {
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

	public CustomerTypeDAO getCustomerTypeDAO() {
		return customerTypeDAO;
	}

	public void setCustomerTypeDAO(CustomerTypeDAO customerTypeDAO) {
		this.customerTypeDAO = customerTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table RMTCustTypes/RMTCustTypes_Temp
	 * by using CustomerTypeDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CustomerTypeDAO's update method 3) Audit the record in to AuditHeader and AdtRMTCustTypes
	 * by using auditHeaderDAO.addAudit(auditHeader)
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
		CustomerType customerType = (CustomerType) auditHeader.getAuditDetail().getModelData();

		if (customerType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (customerType.isNewRecord()) {
			customerType.setCustTypeCode(getCustomerTypeDAO().save(customerType, tableType));
			auditHeader.getAuditDetail().setModelData(customerType);
			auditHeader.setAuditReference(customerType.getCustTypeCode());
		} else {
			getCustomerTypeDAO().update(customerType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTCustTypes by using CustomerTypeDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtRMTCustTypes by using auditHeaderDAO.addAudit(auditHeader)
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

		CustomerType customerType = (CustomerType) auditHeader.getAuditDetail().getModelData();
		getCustomerTypeDAO().delete(customerType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerTypeById fetch the details by using CustomerTypeDAO's getCustomerTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerType
	 */
	@Override
	public CustomerType getCustomerTypeById(String id) {
		return getCustomerTypeDAO().getCustomerTypeById(id, "_View");
	}

	/**
	 * getApprovedCustomerTypeById fetch the details by using CustomerTypeDAO's getCustomerTypeById method . with
	 * parameter id and type as blank. it fetches the approved records from the RMTCustTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerType
	 */
	public CustomerType getApprovedCustomerTypeById(String id) {
		return getCustomerTypeDAO().getCustomerTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerTypeDAO().delete with
	 * parameters customerType,"" b) NEW Add new record in to main table by using getCustomerTypeDAO().save with
	 * parameters customerType,"" c) EDIT Update record in the main table by using getCustomerTypeDAO().update with
	 * parameters customerType,"" 3) Delete the record from the workFlow table by using getCustomerTypeDAO().delete with
	 * parameters customerType,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTCustTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtRMTCustTypes by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CustomerType customerType = new CustomerType();
		BeanUtils.copyProperties((CustomerType) auditHeader.getAuditDetail().getModelData(), customerType);
		getCustomerTypeDAO().delete(customerType, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(customerType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(customerTypeDAO.getCustomerTypeById(customerType.getId(), ""));
		}

		if (customerType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCustomerTypeDAO().delete(customerType, TableType.MAIN_TAB);

		} else {
			customerType.setRoleCode("");
			customerType.setNextRoleCode("");
			customerType.setTaskId("");
			customerType.setNextTaskId("");
			customerType.setWorkflowId(0);

			if (customerType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerType.setRecordType("");
				getCustomerTypeDAO().save(customerType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerType.setRecordType("");
				getCustomerTypeDAO().update(customerType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerTypeDAO().delete with parameters customerType,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtRMTCustTypes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		CustomerType customerType = (CustomerType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerTypeDAO().delete(customerType, TableType.TEMP_TAB);

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
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getCustomerTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		CustomerType customerType = (CustomerType) auditDetail.getModelData();
		// Check the unique keys.
		if (customerType.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(customerType.getRecordType())
				&& customerTypeDAO.isDuplicateKey(customerType.getId(),
						customerType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_CustTypeCode") + ":" + customerType.getCustTypeCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

}