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
 * FileName    		:  CustomerIdentityServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerIdentityDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerIdentityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CustomerIdentity</b>.<br>
 * 
 */
public class CustomerIdentityServiceImpl extends GenericService<CustomerIdentity> implements CustomerIdentityService {

	private static Logger logger = Logger.getLogger(CustomerIdentityServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerIdentityDAO customerIdentityDAO;

	public CustomerIdentityServiceImpl() {
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

	public CustomerIdentityDAO getCustomerIdentityDAO() {
		return customerIdentityDAO;
	}
	public void setCustomerIdentityDAO(CustomerIdentityDAO customerIdentityDAO) {
		this.customerIdentityDAO = customerIdentityDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table CustIdentities/CustIdentities_Temp 
	 * 			by using CustomerIdentityDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CustomerIdentityDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustIdentities by using auditHeaderDAO.addAudit(auditHeader)
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

		String tableType="";
		CustomerIdentity customerIdentity = (CustomerIdentity) auditHeader.getAuditDetail().getModelData();

		if (customerIdentity.isWorkflow()) {
			tableType="_Temp";
		}

		if (customerIdentity.isNew()) {
			customerIdentity.setId(getCustomerIdentityDAO().save(customerIdentity,tableType));
			auditHeader.getAuditDetail().setModelData(customerIdentity);
		}else{
			getCustomerIdentityDAO().update(customerIdentity,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);		
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustIdentities by using CustomerIdentityDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustIdentities by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
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

		CustomerIdentity customerIdentity = (CustomerIdentity) auditHeader.getAuditDetail().getModelData();

		getCustomerIdentityDAO().delete(customerIdentity,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerIdentityById fetch the details by using CustomerIdentityDAO's getCustomerIdentityById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerIdentity
	 */
	@Override
	public CustomerIdentity getCustomerIdentityById(long id,String idType) {
		return getCustomerIdentityDAO().getCustomerIdentityByID(id,idType,"_View");
	}

	/**
	 * getApprovedCustomerIdentityById fetch the details by using CustomerIdentityDAO's getCustomerIdentityById method .
	 * with parameter id and type as blank. it fetches the approved records from the CustIdentities.
	 * @param id (String)
	 * @return CustomerIdentity
	 */
	public CustomerIdentity getApprovedCustomerIdentityById(long id,String idType) {
		return getCustomerIdentityDAO().getCustomerIdentityByID(id,idType,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerIdentityDAO().delete with parameters customerIdentity,""
	 * b) NEW Add new record in to main table by using
	 * getCustomerIdentityDAO().save with parameters customerIdentity,"" c) EDIT
	 * Update record in the main table by using getCustomerIdentityDAO().update
	 * with parameters customerIdentity,"" 3) Delete the record from the
	 * workFlow table by using getCustomerIdentityDAO().delete with parameters
	 * customerIdentity,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtCustIdentities by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtCustIdentities by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CustomerIdentity customerIdentity = new CustomerIdentity();
		BeanUtils.copyProperties((CustomerIdentity) auditHeader.getAuditDetail().getModelData(), customerIdentity);

		if (customerIdentity.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCustomerIdentityDAO().delete(customerIdentity,"");
		} else {
			customerIdentity.setRoleCode("");
			customerIdentity.setNextRoleCode("");
			customerIdentity.setTaskId("");
			customerIdentity.setNextTaskId("");
			customerIdentity.setWorkflowId(0);

			if (customerIdentity.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				customerIdentity.setRecordType("");
				getCustomerIdentityDAO().save(customerIdentity,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerIdentity.setRecordType("");
				getCustomerIdentityDAO().update(customerIdentity,"");
			}
		}

		getCustomerIdentityDAO().delete(customerIdentity,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerIdentity);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerIdentityDAO().delete with
	 * parameters customerIdentity,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtCustIdentities by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
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

		CustomerIdentity customerIdentity= (CustomerIdentity) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerIdentityDAO().delete(customerIdentity,"_Temp");

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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		CustomerIdentity customerIdentity= (CustomerIdentity)auditDetail.getModelData();
		CustomerIdentity tempCustomerIdentity= null;
		if (customerIdentity.isWorkflow()){
			tempCustomerIdentity = getCustomerIdentityDAO().getCustomerIdentityByID(customerIdentity.getId(),customerIdentity.getIdType(), "_Temp");
		}
		CustomerIdentity befCustomerIdentity= getCustomerIdentityDAO().getCustomerIdentityByID(customerIdentity.getId(),customerIdentity.getIdType(), "");

		CustomerIdentity oldCustomerIdentity= customerIdentity.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(customerIdentity.getIdCustID());
		valueParm[1] = customerIdentity.getIdType();

		errParm[0] = PennantJavaUtil.getLabel("label_IdCustID") + ":"
				+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_IdType") + ":"
				+ valueParm[1];

		if (customerIdentity.isNew()) { // for New record or new record into
										// work flow

			if (!customerIdentity.isWorkflow()) {// With out Work flow only new
													// records
				if (befCustomerIdentity != null) { // Record Already Exists in
													// the table then error
					auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (customerIdentity.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befCustomerIdentity != null
							|| tempCustomerIdentity != null) { // if records
																// already
																// exists in the
																// main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerIdentity == null
							|| tempCustomerIdentity != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerIdentity.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befCustomerIdentity == null) { // if records not exists in
													// the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldCustomerIdentity != null
							&& !oldCustomerIdentity.getLastMntOn().equals(
									befCustomerIdentity.getLastMntOn())) {
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
				if (tempCustomerIdentity == null) { // if records not exists in
													// the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempCustomerIdentity != null
						&& oldCustomerIdentity != null
						&& !oldCustomerIdentity.getLastMntOn().equals(
								tempCustomerIdentity.getLastMntOn())) {
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
				|| !customerIdentity.isWorkflow()) {
			auditDetail.setBefImage(befCustomerIdentity);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}