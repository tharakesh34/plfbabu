/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerStatusCodeServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CustomerStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>CustomerStatusCode</b>.<br>
 * 
 */
public class CustomerStatusCodeServiceImpl extends GenericService<CustomerStatusCode>
		implements CustomerStatusCodeService {

	private static Logger logger = LogManager.getLogger(CustomerStatusCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;

	public CustomerStatusCodeServiceImpl() {
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

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTCustStatusCodes/BMTCustStatusCodes_Temp by using CustomerStatusCodeDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using CustomerStatusCodeDAO's update method 3) Audit
	 * the record in to AuditHeader and AdtBMTCustStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		CustomerStatusCode customerStatusCode = (CustomerStatusCode) auditHeader.getAuditDetail().getModelData();

		if (customerStatusCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerStatusCode.isNewRecord()) {
			customerStatusCode.setId(getCustomerStatusCodeDAO().save(customerStatusCode, tableType));
			auditHeader.getAuditDetail().setModelData(customerStatusCode);
			auditHeader.setAuditReference(customerStatusCode.getId());
		} else {
			getCustomerStatusCodeDAO().update(customerStatusCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTCustStatusCodes by using CustomerStatusCodeDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtBMTCustStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		CustomerStatusCode customerStatusCode = (CustomerStatusCode) auditHeader.getAuditDetail().getModelData();

		getCustomerStatusCodeDAO().delete(customerStatusCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerStatusCodeById fetch the details by using CustomerStatusCodeDAO's getCustomerStatusCodeById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerStatusCode
	 */
	@Override
	public CustomerStatusCode getCustomerStatusCodeById(String id) {
		return getCustomerStatusCodeDAO().getCustomerStatusCodeById(id, "_View");
	}

	/**
	 * getApprovedCustomerStatusCodeById fetch the details by using CustomerStatusCodeDAO's getCustomerStatusCodeById
	 * method . with parameter id and type as blank. it fetches the approved records from the BMTCustStatusCodes.
	 * 
	 * @param id (String)
	 * @return CustomerStatusCode
	 */
	public CustomerStatusCode getApprovedCustomerStatusCodeById(String id) {
		return getCustomerStatusCodeDAO().getCustomerStatusCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerStatusCodeDAO().delete with
	 * parameters customerStatusCode,"" b) NEW Add new record in to main table by using getCustomerStatusCodeDAO().save
	 * with parameters customerStatusCode,"" c) EDIT Update record in the main table by using
	 * getCustomerStatusCodeDAO().update with parameters customerStatusCode,"" 3) Delete the record from the workFlow
	 * table by using getCustomerStatusCodeDAO().delete with parameters customerStatusCode,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtBMTCustStatusCodes by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtBMTCustStatusCodes by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		BeanUtils.copyProperties((CustomerStatusCode) auditHeader.getAuditDetail().getModelData(), customerStatusCode);

		if (customerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerStatusCodeDAO().delete(customerStatusCode, "");
		} else {
			customerStatusCode.setRoleCode("");
			customerStatusCode.setNextRoleCode("");
			customerStatusCode.setTaskId("");
			customerStatusCode.setNextTaskId("");
			customerStatusCode.setWorkflowId(0);

			if (customerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerStatusCode.setRecordType("");
				getCustomerStatusCodeDAO().save(customerStatusCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerStatusCode.setRecordType("");
				getCustomerStatusCodeDAO().update(customerStatusCode, "");
			}
		}

		getCustomerStatusCodeDAO().delete(customerStatusCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerStatusCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerStatusCodeDAO().delete with parameters customerStatusCode,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtBMTCustStatusCodes by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		CustomerStatusCode customerStatusCode = (CustomerStatusCode) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerStatusCodeDAO().delete(customerStatusCode, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * from getCustomerStatusCodeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		CustomerStatusCode customerStatusCode = (CustomerStatusCode) auditDetail.getModelData();
		CustomerStatusCode tempCustomerStatusCode = null;

		if (customerStatusCode.isWorkflow()) {
			tempCustomerStatusCode = getCustomerStatusCodeDAO().getCustomerStatusCodeById(customerStatusCode.getId(),
					"_Temp");
		}

		CustomerStatusCode befCustomerStatusCode = getCustomerStatusCodeDAO()
				.getCustomerStatusCodeById(customerStatusCode.getId(), "");
		CustomerStatusCode oldCustomerStatusCode = customerStatusCode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = customerStatusCode.getCustStsCode();
		errParm[0] = PennantJavaUtil.getLabel("label_CustStsCode") + ":" + valueParm[0];

		if (customerStatusCode.isNewRecord()) { // for New record or new record into work
												// flow

			if (!customerStatusCode.isWorkflow()) {// With out Work flow only new
													// records
				if (befCustomerStatusCode != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (customerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																									// is new
					if (befCustomerStatusCode != null || tempCustomerStatusCode != null) {
						// if records already
						// exists in the main
						// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerStatusCode == null || tempCustomerStatusCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerStatusCode.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befCustomerStatusCode == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCustomerStatusCode != null
							&& !oldCustomerStatusCode.getLastMntOn().equals(befCustomerStatusCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}

			} else {

				if (tempCustomerStatusCode == null) { // if records not exists in
					// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
				if (tempCustomerStatusCode != null && oldCustomerStatusCode != null
						&& !oldCustomerStatusCode.getLastMntOn().equals(tempCustomerStatusCode.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerStatusCode.isWorkflow()) {
			auditDetail.setBefImage(befCustomerStatusCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}