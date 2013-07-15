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
 * FileName    		:  CustomerEmploymentDetailServiceImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.validation.CustomerEmploymentDetailValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerEmploymentDetail</b>.<br>
 * 
 */
public class CustomerEmploymentDetailServiceImpl extends GenericService<CustomerEmploymentDetail> implements CustomerEmploymentDetailService {
	
	private static Logger logger = Logger.getLogger(CustomerEmploymentDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private CustomerEmploymentDetailValidation customerEmploymentDetailValidation;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public CustomerEmploymentDetailDAO getCustomerEmploymentDetailDAO() {
		return customerEmploymentDetailDAO;
	}
	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

	public CustomerEmploymentDetail getCustomerEmploymentDetail() {
		return getCustomerEmploymentDetailDAO().getCustomerEmploymentDetail();
	}
	public CustomerEmploymentDetail getNewCustomerEmploymentDetail() {
		return getCustomerEmploymentDetailDAO().getNewCustomerEmploymentDetail();
	}

	public CustomerEmploymentDetailValidation getEmploymentDetailValidation(){
		
		if(customerEmploymentDetailValidation==null){
			this.customerEmploymentDetailValidation = new CustomerEmploymentDetailValidation(customerEmploymentDetailDAO);
		}
		return this.customerEmploymentDetailValidation;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table CustomerEmpDetails/CustomerEmpDetails_Temp 
	 * 			by using CustomerEmploymentDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CustomerEmploymentDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerEmpDetails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditHeader
				.getAuditDetail().getModelData();
		
		if (customerEmploymentDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (customerEmploymentDetail.isNew()) {
			customerEmploymentDetail.setId(getCustomerEmploymentDetailDAO().save(customerEmploymentDetail,tableType));
			auditHeader.getAuditDetail().setModelData(customerEmploymentDetail);
			auditHeader.setAuditReference(String.valueOf(customerEmploymentDetail.getId()));
		}else{
			getCustomerEmploymentDetailDAO().update(customerEmploymentDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerEmpDetails by using CustomerEmploymentDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerEmpDetails by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditHeader
					.getAuditDetail().getModelData();

		getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerEmploymentDetailById fetch the details by using CustomerEmploymentDetailDAO's getCustomerEmploymentDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerEmploymentDetail
	 */
	@Override
	public CustomerEmploymentDetail getCustomerEmploymentDetailById(long id) {
		return getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByID(id,"_View");
	}
	
	/**
	 * getApprovedCustomerEmploymentDetailById fetch the details by using CustomerEmploymentDetailDAO's getCustomerEmploymentDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the CustomerEmpDetails.
	 * @param id (String)
	 * @return CustomerEmploymentDetail
	 */
	public CustomerEmploymentDetail getApprovedCustomerEmploymentDetailById(long id) {
		return getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByID(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param CustomerEmploymentDetail (customerEmploymentDetail)
 	 * @return customerEmploymentDetail
	 */
	@Override
	public CustomerEmploymentDetail refresh(CustomerEmploymentDetail customerEmploymentDetail) {
		logger.debug("Entering");
		getCustomerEmploymentDetailDAO().refresh(customerEmploymentDetail);
		getCustomerEmploymentDetailDAO().initialize(customerEmploymentDetail);
		logger.debug("Leaving");
		return customerEmploymentDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 				getCustomerEmploymentDetailDAO().delete with parameters customerEmploymentDetail,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 				getCustomerEmploymentDetailDAO().save with parameters customerEmploymentDetail,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 				getCustomerEmploymentDetailDAO().update with parameters customerEmploymentDetail,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 				getCustomerEmploymentDetailDAO().delete with parameters customerEmploymentDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerEmpDetails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerEmpDetails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		BeanUtils.copyProperties((CustomerEmploymentDetail) auditHeader
				.getAuditDetail().getModelData(), customerEmploymentDetail);

		if (customerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail,"");
		} else {
			customerEmploymentDetail.setRoleCode("");
			customerEmploymentDetail.setNextRoleCode("");
			customerEmploymentDetail.setTaskId("");
			customerEmploymentDetail.setNextTaskId("");
			customerEmploymentDetail.setWorkflowId(0);

			if (customerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				customerEmploymentDetail.setRecordType("");
				getCustomerEmploymentDetailDAO().save(customerEmploymentDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerEmploymentDetail.setRecordType("");
				getCustomerEmploymentDetailDAO().update(customerEmploymentDetail,"");
			}
		}

		getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerEmploymentDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 			getCustomerEmploymentDetailDAO().delete with parameters customerEmploymentDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerEmpDetails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail,"_TEMP");
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
	 * 5)	for any mismatch conditions Fetch the error details from 
	 * 			getCustomerEmploymentDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		auditHeader = getEmploymentDetailValidation().employmentDetailValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}