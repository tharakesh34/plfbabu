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
 * FileName    		:  CustomerRatingServiceImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerRatingService;
import com.pennant.backend.service.customermasters.validation.CustomerRatingValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerRating</b>.<br>
 * 
 */
public class CustomerRatingServiceImpl extends GenericService<CustomerRating> implements CustomerRatingService {

	private static Logger logger = Logger.getLogger(CustomerRatingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerRatingDAO customerRatingDAO;
	private CustomerRatingValidation customerRatingValidation;
	
	public CustomerRatingServiceImpl() {
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

	public CustomerRatingDAO getCustomerRatingDAO() {
		return customerRatingDAO;
	}
	public void setCustomerRatingDAO(CustomerRatingDAO customerRatingDAO) {
		this.customerRatingDAO = customerRatingDAO;
	}
	
	public CustomerRatingValidation getRatingValidation(){
		
		if(customerRatingValidation==null){
			this.customerRatingValidation = new CustomerRatingValidation(customerRatingDAO);
		}
		return this.customerRatingValidation;
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table CustomerRatings/CustomerRatings_Temp 
	 * 			by using CustomerRatingDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CustomerRatingDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerRatings by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
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
		CustomerRating customerRating = (CustomerRating) auditHeader.getAuditDetail().getModelData();

		if (customerRating.isWorkflow()) {
			tableType="_Temp";
		}

		if (customerRating.isNew()) {
			customerRating.setId(getCustomerRatingDAO().save(customerRating,tableType));
			auditHeader.getAuditDetail().setModelData(customerRating);
		}else{
			getCustomerRatingDAO().update(customerRating,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);		
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerRatings by using 
	 * 			CustomerRatingDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerRatings by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)    
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

		CustomerRating customerRating = (CustomerRating) auditHeader.getAuditDetail().getModelData();

		getCustomerRatingDAO().delete(customerRating,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerRatingById fetch the details by using CustomerRatingDAO's
	 * getCustomerRatingById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerRating
	 */
	@Override
	public CustomerRating getCustomerRatingById(long id,String type) {
		return getCustomerRatingDAO().getCustomerRatingByID(id,type,"_View");
	}
	
	/**
	 * getApprovedCustomerRatingById fetch the details by using
	 * CustomerRatingDAO's getCustomerRatingById method . with parameter id and
	 * type as blank. it fetches the approved records from the CustomerRatings.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerRating
	 */
	public CustomerRating getApprovedCustomerRatingById(long id,String type) {
		return getCustomerRatingDAO().getCustomerRatingByID(id,type,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 				getCustomerRatingDAO().delete with parameters customerRating,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 				getCustomerRatingDAO().save with parameters customerRating,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 				getCustomerRatingDAO().update with parameters customerRating,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 				getCustomerRatingDAO().delete with parameters customerRating,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerRatings by using 
	 * 				auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerRatings by using 
	 * 				auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
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

		CustomerRating customerRating = new CustomerRating();
		BeanUtils.copyProperties((CustomerRating) auditHeader.getAuditDetail().getModelData(), customerRating);

		if (customerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCustomerRatingDAO().delete(customerRating,"");
		} else {
			customerRating.setRoleCode("");
			customerRating.setNextRoleCode("");
			customerRating.setTaskId("");
			customerRating.setNextTaskId("");
			customerRating.setWorkflowId(0);

			if (customerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				customerRating.setRecordType("");
				getCustomerRatingDAO().save(customerRating,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerRating.setRecordType("");
				getCustomerRatingDAO().update(customerRating,"");
			}
		}
		getCustomerRatingDAO().delete(customerRating,"_Temp");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerRating);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 			getCustomerRatingDAO().delete with parameters customerRating,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerRatings by using 
	 * 				auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerRating customerRating= (CustomerRating) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerRatingDAO().delete(customerRating,"_Temp");

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
		auditHeader = getRatingValidation().ratingValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}