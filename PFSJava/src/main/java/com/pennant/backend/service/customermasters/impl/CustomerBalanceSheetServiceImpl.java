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
 * FileName    		:  CustomerBalanceSheetServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.customermasters.CustomerBalanceSheetDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerBalanceSheetService;
import com.pennant.backend.service.customermasters.validation.CustomerBalanceSheetValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerBalanceSheet</b>.<br>
 * 
 */
public class CustomerBalanceSheetServiceImpl extends GenericService<CustomerBalanceSheet> 
				implements CustomerBalanceSheetService {
	
	private static final Logger logger = Logger.getLogger(CustomerBalanceSheetServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private CustomerBalanceSheetDAO customerBalanceSheetDAO;
	private CustomerBalanceSheetValidation balanceSheetValidation;

	public CustomerBalanceSheetServiceImpl() {
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
	
	public CustomerBalanceSheetDAO getCustomerBalanceSheetDAO() {
		return customerBalanceSheetDAO;
	}
	public void setCustomerBalanceSheetDAO(CustomerBalanceSheetDAO customerBalanceSheetDAO) {
		this.customerBalanceSheetDAO = customerBalanceSheetDAO;
	}

	public CustomerBalanceSheetValidation getBalanceSheetValidation(){
		
		if(balanceSheetValidation==null){
			this.balanceSheetValidation = new CustomerBalanceSheetValidation(customerBalanceSheetDAO);
		}
		return this.balanceSheetValidation;
	}

	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table 
	 * 			CustomerBalanceSheet/CustomerBalanceSheet_Temp 
	 * 			by using CustomerBalanceSheetDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CustomerBalanceSheetDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerBalanceSheet by using 
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
		CustomerBalanceSheet customerBalanceSheet = (CustomerBalanceSheet) auditHeader.getAuditDetail().getModelData();
		
		if (customerBalanceSheet.isWorkflow()) {
			tableType="_Temp";
		}

		if (customerBalanceSheet.isNew()) {
			customerBalanceSheet.setId(getCustomerBalanceSheetDAO().save(customerBalanceSheet,tableType));
			auditHeader.getAuditDetail().setModelData(customerBalanceSheet);
		}else{
			getCustomerBalanceSheetDAO().update(customerBalanceSheet,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerBalanceSheet by using 
	 * 		CustomerBalanceSheetDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerBalanceSheet by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)    
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
		
		CustomerBalanceSheet customerBalanceSheet = (CustomerBalanceSheet) 
								auditHeader.getAuditDetail().getModelData();
		getCustomerBalanceSheetDAO().delete(customerBalanceSheet,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerBalanceSheetById fetch the details by using CustomerBalanceSheetDAO's 
	 * 		getCustomerBalanceSheetById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerBalanceSheet
	 */
	public CustomerBalanceSheet getCustomerBalanceSheetById(String id,long custId) {
		return getCustomerBalanceSheetDAO().getCustomerBalanceSheetById(id,custId,"_View");
	}

	/**
	 * getApprovedCustomerBalanceSheetById fetch the details by using
	 * CustomerBalanceSheetDAO's getCustomerBalanceSheetById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * CustomerBalanceSheet.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerBalanceSheet
	 */
	public CustomerBalanceSheet getApprovedCustomerBalanceSheetById(String id,long custId) {
		return getCustomerBalanceSheetDAO().getCustomerBalanceSheetById(id,custId,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 			getCustomerBalanceSheetDAO().delete with parameters customerBalanceSheet,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 			getCustomerBalanceSheetDAO().save with parameters customerBalanceSheet,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 			getCustomerBalanceSheetDAO().update with parameters customerBalanceSheet,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 			getCustomerBalanceSheetDAO().delete with parameters customerBalanceSheet,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerBalanceSheet by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerBalanceSheet by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		CustomerBalanceSheet customerBalanceSheet = new CustomerBalanceSheet();
		BeanUtils.copyProperties((CustomerBalanceSheet) auditHeader.getAuditDetail().getModelData(),
				customerBalanceSheet);

		if (customerBalanceSheet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCustomerBalanceSheetDAO().delete(customerBalanceSheet,"");

		} else {
			customerBalanceSheet.setRoleCode("");
			customerBalanceSheet.setNextRoleCode("");
			customerBalanceSheet.setTaskId("");
			customerBalanceSheet.setNextTaskId("");
			customerBalanceSheet.setWorkflowId(0);

			if (customerBalanceSheet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				customerBalanceSheet.setRecordType("");
				getCustomerBalanceSheetDAO().save(customerBalanceSheet,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerBalanceSheet.setRecordType("");
				getCustomerBalanceSheetDAO().update(customerBalanceSheet,"");
			}
		}

		getCustomerBalanceSheetDAO().delete(customerBalanceSheet,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerBalanceSheet);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 			getCustomerBalanceSheetDAO().delete with parameters customerBalanceSheet,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerBalanceSheet by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		CustomerBalanceSheet customerBalanceSheet = (CustomerBalanceSheet) auditHeader.
								getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerBalanceSheetDAO().delete(customerBalanceSheet,"_Temp");

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
	 * 		getCustomerBalanceSheetDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */		
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		auditHeader = getBalanceSheetValidation().balanceSheetValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}