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
 * FileName    		:  CustomerPhoneNumberServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.customermasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.service.customermasters.validation.CustomerPhoneNumberValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class CustomerPhoneNumberServiceImpl extends GenericService<CustomerPhoneNumber> implements CustomerPhoneNumberService {

	private static Logger logger = Logger.getLogger(CustomerPhoneNumberServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CustomerPhoneNumberValidation customerPhoneNumberValidation;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerPhoneNumberDAO getCustomerPhoneNumberDAO() {
		return customerPhoneNumberDAO;
	}
	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	public CustomerPhoneNumber getCustomerPhoneNumber() {
		return getCustomerPhoneNumberDAO().getCustomerPhoneNumber();
	}
	public CustomerPhoneNumber getNewCustomerPhoneNumber() {
		return getCustomerPhoneNumberDAO().getNewCustomerPhoneNumber();
	}
	
	public void setCustomerPhoneNumberValidation(
			CustomerPhoneNumberValidation customerPhoneNumberValidation) {
		this.customerPhoneNumberValidation = customerPhoneNumberValidation;
	}
	public CustomerPhoneNumberValidation getCustomerPhoneNumberValidation() {
		if(customerPhoneNumberValidation==null){
			this.customerPhoneNumberValidation = new CustomerPhoneNumberValidation(customerPhoneNumberDAO);
		}
		return this.customerPhoneNumberValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CustomerPhoneNumbers/CustomerPhoneNumbers_Temp by using
	 * CustomerPhoneNumberDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * CustomerPhoneNumberDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtCustomerPhoneNumbers by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType="";
		CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) auditHeader.getAuditDetail().getModelData();

		if (customerPhoneNumber.isWorkflow()) {
			tableType="_TEMP";
		}

		if (customerPhoneNumber.isNew()) {
			customerPhoneNumber.setId(getCustomerPhoneNumberDAO().save(customerPhoneNumber,tableType));
			auditHeader.getAuditDetail().setModelData(customerPhoneNumber);
		}else{
			getCustomerPhoneNumberDAO().update(customerPhoneNumber,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CustomerPhoneNumbers by using CustomerPhoneNumberDAO's delete
	 * method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustomerPhoneNumbers by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) auditHeader.getAuditDetail().getModelData();

		getCustomerPhoneNumberDAO().delete(customerPhoneNumber,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerPhoneNumberById fetch the details by using
	 * CustomerPhoneNumberDAO's getCustomerPhoneNumberById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerPhoneNumber
	 */
	@Override
	public CustomerPhoneNumber getCustomerPhoneNumberById(long id,String typeCode) {
		return getCustomerPhoneNumberDAO().getCustomerPhoneNumberByID(id,typeCode,"_View");
	}

	/**
	 * getApprovedCustomerPhoneNumberById fetch the details by using
	 * CustomerPhoneNumberDAO's getCustomerPhoneNumberById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * CustomerPhoneNumbers.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerPhoneNumber
	 */
	public CustomerPhoneNumber getApprovedCustomerPhoneNumberById(long id,String typeCode) {
		return getCustomerPhoneNumberDAO().getCustomerPhoneNumberByID(id,typeCode,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param CustomerPhoneNumber (customerPhoneNumber)
	 * @return customerPhoneNumber
	 */
	@Override
	public CustomerPhoneNumber refresh(CustomerPhoneNumber customerPhoneNumber) {
		logger.debug("Entering");
		getCustomerPhoneNumberDAO().refresh(customerPhoneNumber);
		getCustomerPhoneNumberDAO().initialize(customerPhoneNumber);
		logger.debug("Leaving");
		return customerPhoneNumber;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerPhoneNumberDAO().delete with parameters
	 * customerPhoneNumber,"" b) NEW Add new record in to main table by using
	 * getCustomerPhoneNumberDAO().save with parameters customerPhoneNumber,""
	 * c) EDIT Update record in the main table by using
	 * getCustomerPhoneNumberDAO().update with parameters customerPhoneNumber,""
	 * 3) Delete the record from the workFlow table by using
	 * getCustomerPhoneNumberDAO().delete with parameters
	 * customerPhoneNumber,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtCustomerPhoneNumbers by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtCustomerPhoneNumbers by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		BeanUtils.copyProperties((CustomerPhoneNumber) auditHeader.getAuditDetail().getModelData(), customerPhoneNumber);

		if (customerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCustomerPhoneNumberDAO().delete(customerPhoneNumber,"");
		} else {
			customerPhoneNumber.setRoleCode("");
			customerPhoneNumber.setNextRoleCode("");
			customerPhoneNumber.setTaskId("");
			customerPhoneNumber.setNextTaskId("");
			customerPhoneNumber.setWorkflowId(0);

			if (customerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				customerPhoneNumber.setRecordType("");
				getCustomerPhoneNumberDAO().save(customerPhoneNumber,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerPhoneNumber.setRecordType("");
				getCustomerPhoneNumberDAO().update(customerPhoneNumber,"");
			}
		}

		getCustomerPhoneNumberDAO().delete(customerPhoneNumber,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerPhoneNumber);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerPhoneNumberDAO().delete with
	 * parameters customerPhoneNumber,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCustomerPhoneNumbers by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerPhoneNumber customerPhoneNumber= (CustomerPhoneNumber) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerPhoneNumberDAO().delete(customerPhoneNumber,"_TEMP");

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
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		auditHeader = getCustomerPhoneNumberValidation().phoneNumberValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}