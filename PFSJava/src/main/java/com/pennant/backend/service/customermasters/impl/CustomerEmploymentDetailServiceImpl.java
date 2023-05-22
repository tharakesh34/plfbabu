/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerEmploymentDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 06-05-2011 * * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.customermasters.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.validation.CustomerEmploymentDetailValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Service implementation for methods that depends on <b>CustomerEmploymentDetail</b>.<br>
 * 
 */
public class CustomerEmploymentDetailServiceImpl extends GenericService<CustomerEmploymentDetail>
		implements CustomerEmploymentDetailService {

	private static Logger logger = LogManager.getLogger(CustomerEmploymentDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private CustomerEmploymentDetailValidation customerEmploymentDetailValidation;
	private CustomerDAO customerDAO;

	public CustomerEmploymentDetailServiceImpl() {
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

	public CustomerEmploymentDetailDAO getCustomerEmploymentDetailDAO() {
		return customerEmploymentDetailDAO;
	}

	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

	public CustomerEmploymentDetailValidation getEmploymentDetailValidation() {

		if (customerEmploymentDetailValidation == null) {
			this.customerEmploymentDetailValidation = new CustomerEmploymentDetailValidation(
					customerEmploymentDetailDAO);
		}
		return this.customerEmploymentDetailValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CustomerEmpDetails/CustomerEmpDetails_Temp by using CustomerEmploymentDetailDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using CustomerEmploymentDetailDAO's update
	 * method 3) Audit the record in to AuditHeader and AdtCustomerEmpDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
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
		String tableType = "";
		CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditHeader.getAuditDetail()
				.getModelData();

		if (customerEmploymentDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerEmploymentDetail.isNewRecord()) {
			customerEmploymentDetail.setId(getCustomerEmploymentDetailDAO().save(customerEmploymentDetail, tableType));
			auditHeader.getAuditDetail().setModelData(customerEmploymentDetail);
			auditHeader.setAuditReference(String.valueOf(customerEmploymentDetail.getId()));
		} else {
			getCustomerEmploymentDetailDAO().update(customerEmploymentDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CustomerEmpDetails by using CustomerEmploymentDetailDAO's delete method with type as Blank 3) Audit the record in
	 * to AuditHeader and AdtCustomerEmpDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
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

		CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditHeader.getAuditDetail()
				.getModelData();

		getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerEmploymentDetailById fetch the details by using CustomerEmploymentDetailDAO's
	 * getCustomerEmploymentDetailById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerEmploymentDetail
	 */
	/*
	 * @Override public CustomerEmploymentDetail getCustomerEmploymentDetailById(long id,long custEmpName) { return
	 * getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByID(id,custEmpName,"_View"); }
	 */

	@Override
	public CustomerEmploymentDetail getCustomerEmploymentDetailByCustEmpId(long custEmpId) {
		return getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByCustEmpId(custEmpId, "_View");
	}

	public CustomerEmploymentDetail getApprovedCustomerEmploymentDetailByCustEmpId(long custEmpId) {
		return getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByCustEmpId(custEmpId, "_AView");
	}
	/**
	 * getApprovedCustomerEmploymentDetailById fetch the details by using CustomerEmploymentDetailDAO's
	 * getCustomerEmploymentDetailById method . with parameter id and type as blank. it fetches the approved records
	 * from the CustomerEmpDetails.
	 * 
	 * @param id (String)
	 * @return CustomerEmploymentDetail
	 */
	/*
	 * public CustomerEmploymentDetail getApprovedCustomerEmploymentDetailById(long id,long custEmpName) { return
	 * getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByID(id,custEmpName,"_AView"); }
	 */

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using
	 * getCustomerEmploymentDetailDAO().delete with parameters customerEmploymentDetail,"" b) NEW Add new record in to
	 * main table by using getCustomerEmploymentDetailDAO().save with parameters customerEmploymentDetail,"" c) EDIT
	 * Update record in the main table by using getCustomerEmploymentDetailDAO().update with parameters
	 * customerEmploymentDetail,"" 3) Delete the record from the workFlow table by using
	 * getCustomerEmploymentDetailDAO().delete with parameters customerEmploymentDetail,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtCustomerEmpDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtCustomerEmpDetails by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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

		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		BeanUtils.copyProperties((CustomerEmploymentDetail) auditHeader.getAuditDetail().getModelData(),
				customerEmploymentDetail);

		if (customerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail, "");
		} else {
			customerEmploymentDetail.setRoleCode("");
			customerEmploymentDetail.setNextRoleCode("");
			customerEmploymentDetail.setTaskId("");
			customerEmploymentDetail.setNextTaskId("");
			customerEmploymentDetail.setWorkflowId(0);

			if (customerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerEmploymentDetail.setRecordType("");
				getCustomerEmploymentDetailDAO().save(customerEmploymentDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerEmploymentDetail.setRecordType("");
				getCustomerEmploymentDetailDAO().update(customerEmploymentDetail, "");
			}
		}
		if (!StringUtils.equals(customerEmploymentDetail.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerEmploymentDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerEmploymentDetailDAO().delete with parameters customerEmploymentDetail,"_Temp"
	 * 3) Audit the record in to AuditHeader and AdtCustomerEmpDetails by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerEmploymentDetailDAO().delete(customerEmploymentDetail, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getCustomerEmploymentDetailDAO().getErrorDetail with
	 * Error ID and language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		auditHeader = getEmploymentDetailValidation().employmentDetailValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<CustomerEmploymentDetail> getApprovedCustomerEmploymentDetailById(long custID) {
		return getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailsByID(custID, "_AView");
	}

	/**
	 * Validate CustomerEmploymentDetail.
	 * 
	 * @param customerEmploymentDetail
	 * 
	 * 
	 * 
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(CustomerEmploymentDetail custEmpDetails, Customer customer) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		if (custEmpDetails != null) {
			if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				auditDetail.setErrorDetail(
						validateMasterCode("EmployerDetail", "EmployerId", custEmpDetails.getCustEmpName()));
				auditDetail
						.setErrorDetail(validateMasterCode("RMTEmpTypes", "EmpType", custEmpDetails.getCustEmpType()));
				auditDetail.setErrorDetail(
						validateMasterCode("RMTGenDesignations", "Gendesignation", custEmpDetails.getCustEmpDesg()));
				if (custEmpDetails.getCustEmpDept() != null) {
					auditDetail.setErrorDetail(
							validateMasterCode("RMTGenDepartments", "GenDepartment", custEmpDetails.getCustEmpDept()));
				}
				if (custEmpDetails.getCustEmpTo() != null) {
					if (custEmpDetails.getCustEmpFrom().compareTo(custEmpDetails.getCustEmpTo()) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "employment startDate:"
								+ DateUtil.format(custEmpDetails.getCustEmpFrom(), PennantConstants.XMLDateFormat);
						valueParm[1] = "employment endDate:"
								+ DateUtil.format(custEmpDetails.getCustEmpTo(), PennantConstants.XMLDateFormat);
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
					if (custEmpDetails.getCustEmpTo().compareTo(SysParamUtil.getAppDate()) != -1 || SysParamUtil
							.getValueAsDate("APP_DFT_START_DATE").compareTo(custEmpDetails.getCustEmpTo()) >= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "employment endDate"
								+ DateUtil.format(custEmpDetails.getCustEmpFrom(), PennantConstants.XMLDateFormat);
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90319", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				} else {
					custEmpDetails.setCurrentEmployer(true);
				}

				if (custEmpDetails.getCustEmpFrom() != null
						&& custEmpDetails.getCustEmpFrom().compareTo(SysParamUtil.getAppDate()) != -1
						|| SysParamUtil.getValueAsDate("APP_DFT_START_DATE")
								.compareTo(custEmpDetails.getCustEmpFrom()) >= 0) {

					String[] valueParm = new String[2];
					valueParm[0] = "employment startDate"
							+ DateUtil.format(custEmpDetails.getCustEmpFrom(), PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90319", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (custEmpDetails.getCustEmpFrom() != null && customer.getCustDOB() != null) {
					if (custEmpDetails.getCustEmpFrom().before(customer.getCustDOB())) {
						String[] valueParm = new String[2];
						valueParm[0] = "employment startDate:"
								+ DateUtil.format(custEmpDetails.getCustEmpFrom(), PennantConstants.XMLDateFormat);
						valueParm[1] = "Cust DOB:"
								+ DateUtil.format(customer.getCustDOB(), PennantConstants.XMLDateFormat);
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "employment";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm), "EN"));
			}
		}
		return auditDetail;
	}

	/**
	 * Validate code or Id value with available masters in system.
	 * 
	 * @param tableName
	 * @param columnName
	 * @param value
	 * 
	 * @return WSReturnStatus
	 */
	private ErrorDetail validateMasterCode(String tableName, String columnName, Object value) {
		logger.debug("Entering");

		ErrorDetail errorDetail = new ErrorDetail();

		// validate Master code with PLF system masters
		int count = getCustomerDAO().getLookupCount(tableName, columnName, value);
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = columnName;
			valueParm[1] = Objects.toString(value);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
		}

		logger.debug("Leaving");
		return errorDetail;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Override
	public int getVersion(long custID, long custEmpId) {
		return getCustomerEmploymentDetailDAO().getVersion(custID, custEmpId);
	}

}