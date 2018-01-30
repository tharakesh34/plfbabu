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
 * FileName    		:  CustomerNotesTypeServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CustomerNotesTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CustomerNotesTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CustomerNotesType</b>.<br>
 * 
 */
public class CustomerNotesTypeServiceImpl extends
		GenericService<CustomerNotesType> implements CustomerNotesTypeService {
	
	private static Logger logger = Logger.getLogger(CustomerNotesTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerNotesTypeDAO customerNotesTypeDAO;

	public CustomerNotesTypeServiceImpl() {
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

	public CustomerNotesTypeDAO getCustomerNotesTypeDAO() {
		return customerNotesTypeDAO;
	}

	public void setCustomerNotesTypeDAO(
			CustomerNotesTypeDAO customerNotesTypeDAO) {
		this.customerNotesTypeDAO = customerNotesTypeDAO;
	}
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTCustNotesTypes/BMTCustNotesTypes_Temp by using CustomerNotesTypeDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using CustomerNotesTypeDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtBMTCustNotesTypes by using
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
		CustomerNotesType customerNotesType = (CustomerNotesType) auditHeader
				.getAuditDetail().getModelData();

		if (customerNotesType.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerNotesType.isNew()) {
			customerNotesType.setId(getCustomerNotesTypeDAO().save(
					customerNotesType, tableType));
			auditHeader.getAuditDetail().setModelData(customerNotesType);
			auditHeader.setAuditReference(customerNotesType.getId());
		} else {
			getCustomerNotesTypeDAO().update(customerNotesType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTCustNotesTypes by using CustomerNotesTypeDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTCustNotesTypes by using auditHeaderDAO.addAudit(auditHeader)
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
		CustomerNotesType customerNotesType = (CustomerNotesType) auditHeader
				.getAuditDetail().getModelData();

		getCustomerNotesTypeDAO().delete(customerNotesType, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerNotesTypeById fetch the details by using
	 * CustomerNotesTypeDAO's getCustomerNotesTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerNotesType
	 */
	@Override
	public CustomerNotesType getCustomerNotesTypeById(String id) {
		return getCustomerNotesTypeDAO().getCustomerNotesTypeById(id, "_View");
	}

	/**
	 * getApprovedCustomerNotesTypeById fetch the details by using
	 * CustomerNotesTypeDAO's getCustomerNotesTypeById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * BMTCustNotesTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerNotesType
	 */
	public CustomerNotesType getApprovedCustomerNotesTypeById(String id) {
		return getCustomerNotesTypeDAO().getCustomerNotesTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerNotesTypeDAO().delete with parameters
	 * customerNotesType,"" b) NEW Add new record in to main table by using
	 * getCustomerNotesTypeDAO().save with parameters customerNotesType,"" c)
	 * EDIT Update record in the main table by using
	 * getCustomerNotesTypeDAO().update with parameters customerNotesType,"" 3)
	 * Delete the record from the workFlow table by using
	 * getCustomerNotesTypeDAO().delete with parameters
	 * customerNotesType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTCustNotesTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTCustNotesTypes
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
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
		CustomerNotesType customerNotesType = new CustomerNotesType();
		BeanUtils.copyProperties((CustomerNotesType) auditHeader
				.getAuditDetail().getModelData(), customerNotesType);

		if (customerNotesType.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerNotesTypeDAO().delete(customerNotesType, "");
		} else {
			customerNotesType.setRoleCode("");
			customerNotesType.setNextRoleCode("");
			customerNotesType.setTaskId("");
			customerNotesType.setNextTaskId("");
			customerNotesType.setWorkflowId(0);

			if (customerNotesType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerNotesType.setRecordType("");
				getCustomerNotesTypeDAO().save(customerNotesType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerNotesType.setRecordType("");
				getCustomerNotesTypeDAO().update(customerNotesType, "");
			}
		}

		getCustomerNotesTypeDAO().delete(customerNotesType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerNotesType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerNotesTypeDAO().delete with
	 * parameters customerNotesType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTCustNotesTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		CustomerNotesType customerNotesType = (CustomerNotesType) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerNotesTypeDAO().delete(customerNotesType, "_Temp");

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCustomerNotesTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		CustomerNotesType customerNotesType = (CustomerNotesType) auditDetail
				.getModelData();
		CustomerNotesType tempCustomerNotesType = null;

		if (customerNotesType.isWorkflow()) {
			tempCustomerNotesType = getCustomerNotesTypeDAO()
					.getCustomerNotesTypeById(customerNotesType.getId(),
							"_Temp");
		}

		CustomerNotesType befCustomerNotesType = getCustomerNotesTypeDAO()
				.getCustomerNotesTypeById(customerNotesType.getId(), "");
		CustomerNotesType oldCustomerNotesType = customerNotesType
				.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = customerNotesType.getCustNotesTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_CustNotesTypeCode") + ":"
				+ valueParm[0];

		if (customerNotesType.isNew()) { // for New record or new record into
											// work flow

			if (!customerNotesType.isWorkflow()) {// With out Work flow only new
													// records
				if (befCustomerNotesType != null) { // Record Already Exists in
					// the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (customerNotesType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCustomerNotesType != null
							|| tempCustomerNotesType != null) { // if records
															// already exists
											               //in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerNotesType == null
							|| tempCustomerNotesType != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerNotesType.isWorkflow()) { // With out Work flow for
				// update and delete

				if (befCustomerNotesType == null) { // if records not exists in
					// the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldCustomerNotesType != null
							&& !oldCustomerNotesType.getLastMntOn().equals(
									befCustomerNotesType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003",
									errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004",
									errParm, null));
						}
					}
				}
			} else {
				if (tempCustomerNotesType == null) { // if records not exists in
					// the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempCustomerNotesType != null
						&& oldCustomerNotesType != null
						&& !oldCustomerNotesType.getLastMntOn().equals(
								tempCustomerNotesType.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !customerNotesType.isWorkflow()) {
			auditDetail.setBefImage(befCustomerNotesType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}