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
 * FileName    		:  CustomerPRelationServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.customermasters.CustomerPRelationDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerPRelationService;
import com.pennant.backend.service.customermasters.validation.CustomerPRelationValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerPRelation</b>.<br>
 * 
 */
public class CustomerPRelationServiceImpl extends GenericService<CustomerPRelation> implements CustomerPRelationService {

	private static Logger logger = Logger.getLogger(CustomerPRelationServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private CustomerPRelationDAO customerPRelationDAO;
	private CustomerPRelationValidation customerPRelationValidation;

	public CustomerPRelationServiceImpl() {
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
	
	public CustomerPRelationDAO getCustomerPRelationDAO() {
		return customerPRelationDAO;
	}
	public void setCustomerPRelationDAO(CustomerPRelationDAO customerPRelationDAO) {
		this.customerPRelationDAO = customerPRelationDAO;
	}

	public CustomerPRelationValidation getPRelationValidation(){
		
		if(customerPRelationValidation==null){
			this.customerPRelationValidation = new CustomerPRelationValidation(customerPRelationDAO);
		}
		return this.customerPRelationValidation;
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CustomersPRelations/CustomersPRelations_Temp by using
	 * CustomerPRelationDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * CustomerPRelationDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtCustomersPRelations by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		CustomerPRelation customerPRelation = (CustomerPRelation) auditHeader.getAuditDetail().getModelData();
		
		if (customerPRelation.isWorkflow()) {
			tableType="_Temp";
		}
		
		if (customerPRelation.isNew()) {
			getCustomerPRelationDAO().save(customerPRelation,tableType);
			auditHeader.getAuditDetail().setModelData(customerPRelation);
			auditHeader.setAuditReference(String.valueOf(customerPRelation.getPRCustID())
					+PennantConstants.KEY_SEPERATOR+String.valueOf(customerPRelation.getPRCustPRSNo()));
		}else{
			getCustomerPRelationDAO().update(customerPRelation,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);	
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CustomersPRelations by using CustomerPRelationDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustomersPRelations by using auditHeaderDAO.addAudit(auditHeader)
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

		CustomerPRelation customerPRelation = (CustomerPRelation) auditHeader.getAuditDetail().getModelData();
		getCustomerPRelationDAO().delete(customerPRelation,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerPRelationById fetch the details by using
	 * CustomerPRelationDAO's getCustomerPRelationById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerPRelation
	 */
	@Override
	public CustomerPRelation getCustomerPRelationById(long pRCustID,int pRCustPRSNo) {
		return getCustomerPRelationDAO().getCustomerPRelationByID(pRCustID,pRCustPRSNo,"_View");
	}

	/**
	 * getApprovedCustomerPRelationById fetch the details by using
	 * CustomerPRelationDAO's getCustomerPRelationById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * CustomersPRelations.
	 * 
	 * @param id
	 *            (int)
	 * @return CustomerPRelation
	 */
	public CustomerPRelation getApprovedCustomerPRelationById(long pRCustID,int pRCustPRSNo) {
		return getCustomerPRelationDAO().getCustomerPRelationByID(pRCustID,pRCustPRSNo,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerPRelationDAO().delete with parameters
	 * customerPRelation,"" b) NEW Add new record in to main table by using
	 * getCustomerPRelationDAO().save with parameters customerPRelation,"" c)
	 * EDIT Update record in the main table by using
	 * getCustomerPRelationDAO().update with parameters customerPRelation,"" 3)
	 * Delete the record from the workFlow table by using
	 * getCustomerPRelationDAO().delete with parameters
	 * customerPRelation,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtCustomersPRelations by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtCustomersPRelations by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
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

		CustomerPRelation customerPRelation = new CustomerPRelation();
		BeanUtils.copyProperties((CustomerPRelation) auditHeader.getAuditDetail().getModelData(), customerPRelation);

		if (customerPRelation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getCustomerPRelationDAO().delete(customerPRelation,"");
		} else {
			customerPRelation.setRoleCode("");
			customerPRelation.setNextRoleCode("");
			customerPRelation.setTaskId("");
			customerPRelation.setNextTaskId("");
			customerPRelation.setWorkflowId(0);

			if (customerPRelation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				customerPRelation.setRecordType("");
				getCustomerPRelationDAO().save(customerPRelation,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerPRelation.setRecordType("");
				getCustomerPRelationDAO().update(customerPRelation,"");
			}
		}

		getCustomerPRelationDAO().delete(customerPRelation,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerPRelation);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerPRelationDAO().delete with
	 * parameters customerPRelation,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCustomersPRelations by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		
		CustomerPRelation customerPRelation= (CustomerPRelation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerPRelationDAO().delete(customerPRelation,"_Temp");

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
		auditHeader = getPRelationValidation().pRelationValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}