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
 * FileName    		:  CustomerEMailServiceImpl.java                                                   * 	  
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.customermasters.validation.CustomerEMailValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerEMail</b>.<br>
 * 
 */
public class CustomerEMailServiceImpl extends GenericService<CustomerEMail> implements CustomerEMailService {

	private static Logger logger = Logger.getLogger(CustomerEMailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private CustomerEMailDAO customerEMailDAO;
	private CustomerEMailValidation customerEMailValidation;

	public CustomerEMailServiceImpl() {
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

	public CustomerEMailDAO getCustomerEMailDAO() {
		return customerEMailDAO;
	}
	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	public CustomerEMailValidation getEMailValidation(){

		if(customerEMailValidation==null){
			this.customerEMailValidation = new CustomerEMailValidation(customerEMailDAO);
		}
		return this.customerEMailValidation;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table CustomerEMails/CustomerEMails_Temp 
	 * 			by using CustomerEMailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CustomerEMailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerEMails by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)
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
		CustomerEMail customerEMail = (CustomerEMail) auditHeader.getAuditDetail().getModelData();

		if (customerEMail.isWorkflow()) {
			tableType="_Temp";
		}

		if (customerEMail.isNew()) {
			getCustomerEMailDAO().save(customerEMail,tableType);
			auditHeader.getAuditDetail().setModelData(customerEMail);
		}else{
			getCustomerEMailDAO().update(customerEMail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerEMails by using 
	 * 			CustomerEMailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerEMails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)    
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

		CustomerEMail customerEMail = (CustomerEMail) auditHeader.getAuditDetail().getModelData();

		getCustomerEMailDAO().delete(customerEMail,"");		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerEMailById fetch the details by using CustomerEMailDAO's getCustomerEMailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerEMail
	 */
	@Override
	public CustomerEMail getCustomerEMailById(long id,String typeCode) {
		return getCustomerEMailDAO().getCustomerEMailById(id,typeCode,"_View");
	}
	
	/**
	 * getApprovedCustomerEMailById fetch the details by using CustomerEMailDAO's getCustomerEMailById method .
	 * with parameter id and type as blank. it fetches the approved records from the CustomerEMails.
	 * @param id (String)
	 * @return CustomerEMail
	 */
	public CustomerEMail getApprovedCustomerEMailById(long id,String typeCode) {
		return getCustomerEMailDAO().getCustomerEMailById(id,typeCode,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 				getCustomerEMailDAO().delete with parameters customerEMail,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 				getCustomerEMailDAO().save with parameters customerEMail,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 				getCustomerEMailDAO().update with parameters customerEMail,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 			getCustomerEMailDAO().delete with parameters customerEMail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerEMails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerEMails by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
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

		CustomerEMail customerEMail = new CustomerEMail();
		BeanUtils.copyProperties((CustomerEMail) auditHeader.getAuditDetail().getModelData(), customerEMail);

		if (customerEMail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCustomerEMailDAO().delete(customerEMail,"");
		} else {
			customerEMail.setRoleCode("");
			customerEMail.setNextRoleCode("");
			customerEMail.setTaskId("");
			customerEMail.setNextTaskId("");
			customerEMail.setWorkflowId(0);

			if (customerEMail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				customerEMail.setRecordType("");
				getCustomerEMailDAO().save(customerEMail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerEMail.setRecordType("");
				getCustomerEMailDAO().update(customerEMail,"");
			}
		}

		if(!StringUtils.equals(customerEMail.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
		getCustomerEMailDAO().delete(customerEMail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerEMail);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 			getCustomerEMailDAO().delete with parameters customerEMail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerEMails by using 
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

		CustomerEMail customerEMail= (CustomerEMail) auditHeader.getAuditDetail().getModelData();			
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerEMailDAO().delete(customerEMail,"_Temp");

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
	 * 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		auditHeader = getEMailValidation().emailValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	@Override
    public List<String> getCustEmailsByCustId(long custId) {
	    return getCustomerEMailDAO().getCustEmailsByCustId(custId);
    }

	@Override
	public List<CustomerEMail> getApprovedCustomerEMailById(long id) {
		 return getCustomerEMailDAO().getCustomerEmailByCustomer(id,"_AView");
	}
	/**
	 * Validate the EmailType Code
	 * @param customerEMail   
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(CustomerEMail customerEMail) {
			AuditDetail auditDetail = new AuditDetail();
			ErrorDetails errorDetail = new ErrorDetails();

			// validate Master code with PLF system masters
			int count = getCustomerEMailDAO().getEMailTypeCount(customerEMail.getCustEMailTypeCode());
			if (count <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "EMailType";
				valueParm[1] = customerEMail.getCustEMailTypeCode();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			if(!(customerEMail.getCustEMailPriority()>=1 && customerEMail.getCustEMailPriority()<=5)){
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(customerEMail.getCustEMailPriority());
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90110", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
				
			}
			 boolean validRegex =  EmailValidator.getInstance().isValid(customerEMail.getCustEMail());
				
				if(!validRegex){
					String[] valueParm = new String[1];
					valueParm[0] = customerEMail.getCustEMail();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90237", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			return auditDetail;
		}

	@Override
	public int getVersion(long id, String typeCode) {
		return getCustomerEMailDAO().getVersion(id,typeCode);
	}
}