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
 * FileName    		:  CustomerServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Customer</b>.<br>
 * 
 */
public class CustomerServiceImpl extends GenericService<Customer> implements
		CustomerService {

	private final static Logger logger = Logger.getLogger(CustomerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerDAO customerDAO;
	private IncomeTypeDAO incomeTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
	
	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
	    this.incomeTypeDAO = incomeTypeDAO;
    }
	public IncomeTypeDAO getIncomeTypeDAO() {
	    return incomeTypeDAO;
    }

	public Customer getCustomer() {
		return getCustomerDAO().getCustomer(false);
	}
	public Customer getNewCustomer() {
		return getCustomerDAO().getNewCustomer(true);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * Customers/Customers_Temp by using CustomerDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using
	 * CustomerDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtCustomers by using auditHeaderDAO.addAudit(auditHeader)
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
		Customer customer = (Customer) auditHeader.getAuditDetail().getModelData();

		if (customer.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (customer.isNew()) {
			customer.setId(getCustomerDAO().save(customer, tableType));
			auditHeader.getAuditDetail().setModelData(customer);
			auditHeader.setAuditReference(String.valueOf(customer.getCustID()));
		} else {
			getCustomerDAO().update(customer, tableType);
		}
		
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),"proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], customer.getBefImage(), customer));
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table Customers by using CustomerDAO's delete method with type as Blank
	 * 3) Audit the record in to AuditHeader and AdtCustomers by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		Customer customer = (Customer) auditHeader.getAuditDetail().getModelData();
		getCustomerDAO().delete(customer, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),"proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], customer.getBefImage(), customer));
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerById fetch the details by using CustomerDAO's getCustomerById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public Customer getCustomerById(long id) {
		return getCustomerDAO().getCustomerByID(id, "_View");
	}

	/**
	 * getApprovedCustomerById fetch the details by using CustomerDAO's
	 * getCustomerById method . with parameter id and type as blank. it fetches
	 * the approved records from the Customers.
	 * 
	 * @param id
	 *            (String)
	 * @return Customer
	 */
	public Customer getApprovedCustomerById(long id) {
		return getCustomerDAO().getCustomerByID(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Customer
	 *            (customer)
	 * @return customer
	 */
	@Override
	public Customer refresh(Customer customer) {
		logger.debug("Entering");
		getCustomerDAO().refresh(customer);
		getCustomerDAO().initialize(customer);
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerDAO().delete with parameters customer,"" b) NEW Add new
	 * record in to main table by using getCustomerDAO().save with parameters
	 * customer,"" c) EDIT Update record in the main table by using
	 * getCustomerDAO().update with parameters customer,"" 3) Delete the record
	 * from the workFlow table by using getCustomerDAO().delete with parameters
	 * customer,"_Temp" 4) Audit the record in to AuditHeader and AdtCustomers
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtCustomers by using
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
			return auditHeader;
		}

		Customer customer = new Customer();
		BeanUtils.copyProperties((Customer) auditHeader.getAuditDetail()
				.getModelData(), customer);

		if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerDAO().delete(customer, "");
		} else {
			customer.setRoleCode("");
			customer.setNextRoleCode("");
			customer.setTaskId("");
			customer.setNextTaskId("");
			customer.setWorkflowId(0);

			if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customer.setRecordType("");
				getCustomerDAO().save(customer, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customer.setRecordType("");
				getCustomerDAO().update(customer, "");
			}
		}

		getCustomerDAO().delete(customer, "_TEMP");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),"proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], customer.getBefImage(), customer));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], customer.getBefImage(), customer));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerDAO().delete with parameters
	 * customer,"_Temp" 3) Audit the record in to AuditHeader and AdtCustomers
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Customer customer = (Customer) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerDAO().delete(customer, "_TEMP");
		
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),"proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], customer.getBefImage(), customer));
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
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		Customer customer = (Customer) auditDetail.getModelData();
		Customer tempCustomer = null;
		if (customer.isWorkflow()) {
			tempCustomer = getCustomerDAO().getCustomerByID(customer.getId(),
					"_Temp");
		}
		Customer befCustomer = getCustomerDAO().getCustomerByID(
				customer.getId(), "");

		Customer old_Customer = customer.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = customer.getCustCIF();
		valueParm[1] = customer.getCustCtgCode();

		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustCtgCode") + ":" + valueParm[1];

		if (customer.isNew()) { // for New record or new record into work flow

			if (!customer.isWorkflow()) {// With out Work flow only new records
				if (befCustomer != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (customer.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befCustomer != null  || tempCustomer != null) { // if records already exists in
												// the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomer == null || tempCustomer == null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customer.isWorkflow()) { // With out Work flow for update and delete
				if (befCustomer == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",errParm, null));
				}else{

				if (old_Customer != null
						&& !old_Customer.getLastMntOn().equals(befCustomer.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41003", errParm,null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41004", errParm,null));
					}
				}
				}

			} else {

				if (tempCustomer == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempCustomer != null
						&& old_Customer != null
						&& !old_Customer.getLastMntOn().equals(
								tempCustomer.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !customer.isWorkflow()) {
			auditDetail.setBefImage(befCustomer);
		}

		logger.debug("Leaving");
		return auditDetail;
	}
	@Override
    public boolean isJointCustExist(long custID) {
		logger.debug("Entering");
		boolean jointCustExist = getCustomerDAO().isJointCustExist(custID);
		logger.debug("Leaving");
	    return jointCustExist;
    }
	
	@Override
    public WIFCustomer getWIFCustomerByID(long custId, String custCRCPR) {
	    return getCustomerDAO().getWIFCustomerByID(custId, custCRCPR, "_AView");
    }
	@Override
    public Date getCustBlackListedDate(String custCRCPR) {
	    return getCustomerDAO().getCustBlackListedDate(custCRCPR, "_View");
    }
	
	@Override
    public String getCustomerByCRCPR(String custCRCPR, String type) {
	    return getCustomerDAO().getCustomerByCRCPR(custCRCPR, type);
    }


}