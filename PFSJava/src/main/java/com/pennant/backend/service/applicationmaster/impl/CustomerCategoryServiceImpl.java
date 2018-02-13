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
 * FileName    		:  CustomerCategoryServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CustomerCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>CustomerCategory</b>.<br>
 * 
 */
public class CustomerCategoryServiceImpl extends GenericService<CustomerCategory> implements CustomerCategoryService {

	private static Logger logger = Logger.getLogger(CustomerCategoryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerCategoryDAO customerCategoryDAO;

	public CustomerCategoryServiceImpl() {
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

	public CustomerCategoryDAO getCustomerCategoryDAO() {
		return customerCategoryDAO;
	}

	public void setCustomerCategoryDAO(CustomerCategoryDAO customerCategoryDAO) {
		this.customerCategoryDAO = customerCategoryDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTCustCategories/BMTCustCategories_Temp by using CustomerCategoryDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using CustomerCategoryDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtBMTCustCategories by using
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
		CustomerCategory customerCategory = (CustomerCategory) auditHeader
				.getAuditDetail().getModelData();

		if (customerCategory.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerCategory.isNew()) {
			customerCategory.setId(getCustomerCategoryDAO().save(
					customerCategory, tableType));
			auditHeader.getAuditDetail().setModelData(customerCategory);
			auditHeader.setAuditReference(customerCategory.getCustCtgCode());
		} else {
			getCustomerCategoryDAO().update(customerCategory, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTCustCategories by using CustomerCategoryDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTCustCategories by using auditHeaderDAO.addAudit(auditHeader)
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
		CustomerCategory customerCategory = (CustomerCategory) auditHeader
				.getAuditDetail().getModelData();

		getCustomerCategoryDAO().delete(customerCategory, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerCategoryById fetch the details by using CustomerCategoryDAO's
	 * getCustomerCategoryById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerCategory
	 */
	@Override
	public CustomerCategory getCustomerCategoryById(String id) {
		return getCustomerCategoryDAO().getCustomerCategoryById(id, "_View");
	}

	/**
	 * getApprovedCustomerCategoryById fetch the details by using
	 * CustomerCategoryDAO's getCustomerCategoryById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * BMTCustCategories.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerCategory
	 */
	public CustomerCategory getApprovedCustomerCategoryById(String id) {
		return getCustomerCategoryDAO().getCustomerCategoryById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerCategoryDAO().delete with parameters customerCategory,""
	 * b) NEW Add new record in to main table by using
	 * getCustomerCategoryDAO().save with parameters customerCategory,"" c) EDIT
	 * Update record in the main table by using getCustomerCategoryDAO().update
	 * with parameters customerCategory,"" 3) Delete the record from the
	 * workFlow table by using getCustomerCategoryDAO().delete with parameters
	 * customerCategory,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTCustCategories by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTCustCategories
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
		CustomerCategory customerCategory = new CustomerCategory();
		BeanUtils.copyProperties((CustomerCategory) auditHeader
				.getAuditDetail().getModelData(), customerCategory);

		if (customerCategory.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCustomerCategoryDAO().delete(customerCategory, "");

		} else {
			customerCategory.setRoleCode("");
			customerCategory.setNextRoleCode("");
			customerCategory.setTaskId("");
			customerCategory.setNextTaskId("");
			customerCategory.setWorkflowId(0);

			if (customerCategory.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerCategory.setRecordType("");
				getCustomerCategoryDAO().save(customerCategory, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerCategory.setRecordType("");
				getCustomerCategoryDAO().update(customerCategory, "");
			}
		}

		getCustomerCategoryDAO().delete(customerCategory, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerCategory);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerCategoryDAO().delete with
	 * parameters customerCategory,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTCustCategories by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
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
		CustomerCategory customerCategory = (CustomerCategory) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerCategoryDAO().delete(customerCategory, "_Temp");

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
	 * getCustomerCategoryDAO().getErrorDetail with Error ID and language as
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

		CustomerCategory customerCategory = (CustomerCategory) auditDetail
				.getModelData();
		CustomerCategory tempCustomerCategory = null;

		if (customerCategory.isWorkflow()) {
			tempCustomerCategory = getCustomerCategoryDAO()
					.getCustomerCategoryById(customerCategory.getId(), "_Temp");
		}

		CustomerCategory befCustomerCategory = getCustomerCategoryDAO()
				.getCustomerCategoryById(customerCategory.getId(), "");
		CustomerCategory oldCustomerCategory = customerCategory.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = customerCategory.getCustCtgCode();
		errParm[0] = PennantJavaUtil.getLabel("label_CustCtg_Code") + ":"
				+ valueParm[0];

		if (customerCategory.isNew()) { // for New record or new record into
										// work flow

			if (!customerCategory.isWorkflow()) {// With out Work flow only new
													// records
				if (befCustomerCategory != null) { // Record Already Exists in
														// the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (customerCategory.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCustomerCategory != null
							|| tempCustomerCategory != null) { // if records
															// already exists
															//in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerCategory == null
							|| tempCustomerCategory != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerCategory.isWorkflow()) { // With out Work flow for
												 // update and delete

				if (befCustomerCategory == null) { // if records not exists in
												  // the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldCustomerCategory != null
							&& !oldCustomerCategory.getLastMntOn().equals(
									befCustomerCategory.getLastMntOn())) {
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

				if (tempCustomerCategory == null) { // if records not exists in
					                               // the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (tempCustomerCategory != null
						&& oldCustomerCategory != null
						&& !oldCustomerCategory.getLastMntOn().equals(
								tempCustomerCategory.getLastMntOn())) {
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
				|| !customerCategory.isWorkflow()) {
			auditDetail.setBefImage(befCustomerCategory);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}