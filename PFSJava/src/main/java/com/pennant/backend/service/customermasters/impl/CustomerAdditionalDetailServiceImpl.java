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
 * FileName    		:  CustomerAdditionalDetailServiceImpl.java                                                   * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAdditionalDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerAdditionalDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CustomerAdditionalDetail</b>.<br>
 * 
 */
public class CustomerAdditionalDetailServiceImpl extends GenericService<CustomerAdditionalDetail> implements
		CustomerAdditionalDetailService {
	private static Logger logger = Logger.getLogger(CustomerAdditionalDetailServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private CustomerAdditionalDetailDAO customerAdditionalDetailDAO;

	public CustomerAdditionalDetailServiceImpl() {
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

	public CustomerAdditionalDetailDAO getCustomerAdditionalDetailDAO() {
		return customerAdditionalDetailDAO;
	}
	public void setCustomerAdditionalDetailDAO(CustomerAdditionalDetailDAO customerAdditionalDetailDAO) {
		this.customerAdditionalDetailDAO = customerAdditionalDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CustAdditionalDetails/CustAdditionalDetails_Temp by using
	 * CustomerAdditionalDetailDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using
	 * CustomerAdditionalDetailDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtCustAdditionalDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType="";
		CustomerAdditionalDetail customerAdditionalDetail = (CustomerAdditionalDetail) auditHeader
		.getAuditDetail().getModelData();

		if (customerAdditionalDetail.isWorkflow()) {
			tableType="_Temp";
		}
		if (customerAdditionalDetail.isNew()) {
			customerAdditionalDetail.setId(getCustomerAdditionalDetailDAO()
					.save(customerAdditionalDetail, tableType));
			auditHeader.getAuditDetail().setModelData(customerAdditionalDetail);
			auditHeader.setAuditReference(String.valueOf(customerAdditionalDetail.getId()));
		}else{
			getCustomerAdditionalDetailDAO().update(customerAdditionalDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);	
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CustAdditionalDetails by using CustomerAdditionalDetailDAO's delete
	 * method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustAdditionalDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAdditionalDetail customerAdditionalDetail = (CustomerAdditionalDetail) auditHeader
									.getAuditDetail().getModelData();
		getCustomerAdditionalDetailDAO().delete(customerAdditionalDetail, "");
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getCustomerAdditionalDetailById fetch the details by using
	 * CustomerAdditionalDetailDAO's getCustomerAdditionalDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerAdditionalDetail
	 */
	@Override
	public CustomerAdditionalDetail getCustomerAdditionalDetailById(long id) {
		return getCustomerAdditionalDetailDAO().getCustomerAdditionalDetailById(id,"_View");
	}

	/**
	 * getApprovedCustomerAdditionalDetailById fetch the details by using
	 * CustomerAdditionalDetailDAO's getCustomerAdditionalDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from
	 * the CustAdditionalDetails.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerAdditionalDetail
	 */
	public CustomerAdditionalDetail getApprovedCustomerAdditionalDetailById(long id) {
		return getCustomerAdditionalDetailDAO().getCustomerAdditionalDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerAdditionalDetailDAO().delete with parameters
	 * customerAdditionalDetail,"" b) NEW Add new record in to main table by
	 * using getCustomerAdditionalDetailDAO().save with parameters
	 * customerAdditionalDetail,"" c) EDIT Update record in the main table by
	 * using getCustomerAdditionalDetailDAO().update with parameters
	 * customerAdditionalDetail,"" 3) Delete the record from the workFlow table
	 * by using getCustomerAdditionalDetailDAO().delete with parameters
	 * customerAdditionalDetail,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtCustAdditionalDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtCustAdditionalDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAdditionalDetail customerAdditionalDetail = new CustomerAdditionalDetail();
		BeanUtils.copyProperties(
				(CustomerAdditionalDetail) auditHeader.getAuditDetail()
				.getModelData(),customerAdditionalDetail);
		
		if (customerAdditionalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCustomerAdditionalDetailDAO().delete(customerAdditionalDetail,"");

		} else {
			customerAdditionalDetail.setRoleCode("");
			customerAdditionalDetail.setNextRoleCode("");
			customerAdditionalDetail.setTaskId("");
			customerAdditionalDetail.setNextTaskId("");
			customerAdditionalDetail.setWorkflowId(0);

			if (customerAdditionalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				customerAdditionalDetail.setRecordType("");
				getCustomerAdditionalDetailDAO().save(customerAdditionalDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerAdditionalDetail.setRecordType("");
				getCustomerAdditionalDetailDAO().update(customerAdditionalDetail,"");
			}
		}

		getCustomerAdditionalDetailDAO().delete(customerAdditionalDetail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerAdditionalDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerAdditionalDetailDAO().delete with
	 * parameters customerAdditionalDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCustAdditionalDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAdditionalDetail customerAdditionalDetail = (CustomerAdditionalDetail) auditHeader
								.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerAdditionalDetailDAO().delete(customerAdditionalDetail,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
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
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering ");
		CustomerAdditionalDetail customerAdditionalDetail = (CustomerAdditionalDetail) auditDetail
				.getModelData();
		CustomerAdditionalDetail tempCustomerAdditionalDetail = null;

		if (customerAdditionalDetail.isWorkflow()) {
			tempCustomerAdditionalDetail = getCustomerAdditionalDetailDAO()
					.getCustomerAdditionalDetailById(customerAdditionalDetail.getId(), "_Temp");
		}
		CustomerAdditionalDetail befCustomerAdditionalDetail = getCustomerAdditionalDetailDAO()
				.getCustomerAdditionalDetailById(customerAdditionalDetail.getId(), "");

		CustomerAdditionalDetail oldCustomerAdditionalDetail = customerAdditionalDetail
				.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = String.valueOf(customerAdditionalDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];

		if (customerAdditionalDetail.isNew()) { // for New record or new record
												// into work flow

			if (!customerAdditionalDetail.isWorkflow()) {// With out Work flow
															// only new records
				if (befCustomerAdditionalDetail != null) { // Record Already
															// Exists in the
															// table then error
					auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (customerAdditionalDetail.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befCustomerAdditionalDetail != null
							|| tempCustomerAdditionalDetail != null) { // if records already
																		// exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerAdditionalDetail == null
							|| tempCustomerAdditionalDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerAdditionalDetail.isWorkflow()) { // With out Work flow
															// for update and
				// delete
				if (befCustomerAdditionalDetail == null) { // if records not
															// exists in the
															// main table
					auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldCustomerAdditionalDetail != null
							&& !oldCustomerAdditionalDetail.getLastMntOn()
									.equals(befCustomerAdditionalDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempCustomerAdditionalDetail == null) { // if records not
															// exists in the
															// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (tempCustomerAdditionalDetail != null
						&& oldCustomerAdditionalDetail != null
						&& !oldCustomerAdditionalDetail.getLastMntOn().equals(
								tempCustomerAdditionalDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !customerAdditionalDetail.isWorkflow()) {
			auditDetail.setBefImage(befCustomerAdditionalDetail);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

}