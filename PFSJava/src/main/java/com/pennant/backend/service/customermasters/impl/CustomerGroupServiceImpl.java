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
 * FileName    		:  CustomerGroupServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.customermasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerGroupDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CustomerGroup</b>.<br>
 * 
 */
public class CustomerGroupServiceImpl extends GenericService<CustomerGroup> implements CustomerGroupService {

	private static Logger logger = Logger.getLogger(CustomerGroupServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private CustomerGroupDAO customerGroupDAO;
	private LimitHeaderDAO limitHeaderDAO;
	private CustomerDAO customerDAO;
	public CustomerGroupServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerGroupDAO getCustomerGroupDAO() {
		return customerGroupDAO;
	}
	public void setCustomerGroupDAO(CustomerGroupDAO customerGroupDAO) {
		this.customerGroupDAO = customerGroupDAO;
	}

	public LimitHeaderDAO getLimitHeaderDAO() {
		return limitHeaderDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CustomerGroups/CustomerGroups_Temp by using CustomerGroupDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CustomerGroupDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtCustomerGroups by using
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
		CustomerGroup customerGroup = (CustomerGroup) auditHeader.getAuditDetail().getModelData();

		if (customerGroup.isWorkflow()) {
			tableType = "_Temp";
		} 

		if (customerGroup.isNew()) {
			customerGroup.setId(getCustomerGroupDAO().save(customerGroup,tableType));
			auditHeader.getAuditDetail().setModelData(customerGroup);
			auditHeader.setAuditReference(String.valueOf(customerGroup.getId()));
		} else {
			getCustomerGroupDAO().update(customerGroup, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CustomerGroups by using CustomerGroupDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtCustomerGroups by
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

		CustomerGroup customerGroup = (CustomerGroup) auditHeader.getAuditDetail().getModelData();
		
		getCustomerGroupDAO().delete(customerGroup, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerGroupById fetch the details by using CustomerGroupDAO's
	 * getCustomerGroupById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerGroup
	 */
	@Override
	public CustomerGroup getCustomerGroupById(long id) {
		return getCustomerGroupDAO().getCustomerGroupByID(id, "_View");
	}

	/**
	 * getApprovedCustomerGroupById fetch the details by using
	 * CustomerGroupDAO's getCustomerGroupById method . with parameter id and
	 * type as blank. it fetches the approved records from the CustomerGroups.
	 * 
	 * @param id
	 *            (int)
	 * @return CustomerGroup
	 */
	public CustomerGroup getApprovedCustomerGroupById(long id) {
		return getCustomerGroupDAO().getCustomerGroupByID(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerGroupDAO().delete with parameters customerGroup,"" b)
	 * NEW Add new record in to main table by using getCustomerGroupDAO().save
	 * with parameters customerGroup,"" c) EDIT Update record in the main table
	 * by using getCustomerGroupDAO().update with parameters customerGroup,"" 3)
	 * Delete the record from the workFlow table by using
	 * getCustomerGroupDAO().delete with parameters customerGroup,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtCustomerGroups by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtCustomerGroups by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CustomerGroup customerGroup = new CustomerGroup();
		BeanUtils.copyProperties((CustomerGroup) auditHeader.getAuditDetail().getModelData(),customerGroup);

		if (customerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerGroupDAO().delete(customerGroup, "");

		} else {
			customerGroup.setRoleCode("");
			customerGroup.setNextRoleCode("");
			customerGroup.setTaskId("");
			customerGroup.setNextTaskId("");
			customerGroup.setWorkflowId(0);

			if (customerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerGroup.setRecordType("");
				getCustomerGroupDAO().save(customerGroup, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerGroup.setRecordType("");
				getCustomerGroupDAO().update(customerGroup, "");
			}
		}

		getCustomerGroupDAO().delete(customerGroup, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerGroup);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerGroupDAO().delete with parameters
	 * customerGroup,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtCustomerGroups by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		CustomerGroup customerGroup = (CustomerGroup) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerGroupDAO().delete(customerGroup, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,	String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCustomerIdentityDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		CustomerGroup customerGroup = (CustomerGroup) auditDetail.getModelData();
		CustomerGroup tempCustomerGroup = null;

		if (customerGroup.isWorkflow()) {
			tempCustomerGroup = getCustomerGroupDAO().getCustomerGroupByCode(
					customerGroup.getCustGrpCode(), "_Temp");
		}

		CustomerGroup befCustomerGroup = getCustomerGroupDAO().getCustomerGroupByCode(customerGroup.getCustGrpCode(), "");
		CustomerGroup oldCustomerGroup = customerGroup.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(customerGroup.getCustGrpCode());
		errParm[0] = PennantJavaUtil.getLabel("label_CustGrpID") + " : " + valueParm[0];

		if (customerGroup.isNew()) { // for New record or new record into work
			// flow

			if (!customerGroup.isWorkflow()) {// With out Work flow only new
				// records
				if (befCustomerGroup != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (customerGroup.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCustomerGroup != null || tempCustomerGroup != null) { // if records already exists
						// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerGroup == null || tempCustomerGroup != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerGroup.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befCustomerGroup == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				}else {
					if (oldCustomerGroup != null
							&& !oldCustomerGroup.getLastMntOn().equals(befCustomerGroup.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}

			} else {

				if (tempCustomerGroup == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomerGroup != null && oldCustomerGroup != null && !oldCustomerGroup.getLastMntOn().equals(
						tempCustomerGroup.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		
		// Checking Dependency Validation
		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(customerGroup.getRecordType())) {

			// Customer Group Limit SetUp
			LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(customerGroup.getCustGrpID(),
					"_View");
			if (limitHeader != null) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
			} else {

				// Customer
				boolean isCustExists = getCustomerDAO().customerExistingCustGrp(customerGroup.getCustGrpID(), "_View");
				if (isCustExists) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
				}
			}

		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !customerGroup.isWorkflow()) {
			auditDetail.setBefImage(befCustomerGroup);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Method for get customer group by group code.
	 * 
	 * @param groupCode
	 */
	@Override
	public CustomerGroup getCustomerGroupByCode(String groupCode) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerGroupDAO().getCustomerGroupByCode(groupCode, "");
	}

}
