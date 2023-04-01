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
 * * FileName : CustomerServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.customermasters.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Service implementation for methods that depends on <b>Customer</b>.<br>
 * 
 */
public class CustomerServiceImpl extends GenericService<Customer> implements CustomerService {

	private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerDAO customerDAO;
	private IncomeTypeDAO incomeTypeDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private BranchDAO branchDAO;
	private ProvinceDAO provinceDAO;
	private CityDAO cityDAO;

	public CustomerServiceImpl() {
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

	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

	public Customer getCustomer(Customer customer) {
		return getCustomerDAO().getCustomer(false, customer);
	}

	public Customer getNewCustomer(Customer customer) {
		return getCustomerDAO().getNewCustomer(true, customer);
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public ProvinceDAO getProvinceDAO() {
		return provinceDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public CityDAO getCityDAO() {
		return cityDAO;
	}

	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Customers/Customers_Temp by
	 * using CustomerDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by
	 * using CustomerDAO's update method 3) Audit the record in to AuditHeader and AdtCustomers by using
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
		Customer customer = (Customer) auditHeader.getAuditDetail().getModelData();

		if (customer.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customer.isNewRecord()) {
			customer.setId(getCustomerDAO().save(customer, tableType));
			auditHeader.getAuditDetail().setModelData(customer);
			auditHeader.setAuditReference(String.valueOf(customer.getCustID()));
		} else {
			getCustomerDAO().update(customer, tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Customers by using CustomerDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustomers by using auditHeaderDAO.addAudit(auditHeader)
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

		Customer customer = (Customer) auditHeader.getAuditDetail().getModelData();
		getCustomerDAO().delete(customer, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), "proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerById fetch the details by using CustomerDAO's getCustomerById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public Customer getCustomerById(long id) {
		return getCustomerDAO().getCustomerByID(id, "_View");
	}

	/**
	 * getApprovedCustomerById fetch the details by using CustomerDAO's getCustomerById method . with parameter id and
	 * type as blank. it fetches the approved records from the Customers.
	 * 
	 * @param id (String)
	 * @return Customer
	 */
	@Override
	public Customer getApprovedCustomerById(long id) {
		return getCustomerDAO().getCustomerByID(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerDAO().delete with
	 * parameters customer,"" b) NEW Add new record in to main table by using getCustomerDAO().save with parameters
	 * customer,"" c) EDIT Update record in the main table by using getCustomerDAO().update with parameters customer,""
	 * 3) Delete the record from the workFlow table by using getCustomerDAO().delete with parameters customer,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtCustomers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5) Audit the record in to AuditHeader and AdtCustomers by using auditHeaderDAO.addAudit(auditHeader) based on the
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
			return auditHeader;
		}

		Customer customer = (Customer) auditHeader.getAuditDetail().getModelData();
		/*
		 * BeanUtils.copyProperties((Customer) auditHeader.getAuditDetail() .getModelData(), customer);
		 */

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
		if (auditHeader.getApiHeader() == null) {
			getCustomerDAO().delete(customer, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		/*
		 * auditHeader.setAuditTranType(PennantConstants.TRAN_WF); String[] fields = PennantJavaUtil.getFieldDetails(new
		 * Customer(),"proceedToDedup,dedupFound,skipDedup"); auditHeader.setAuditDetail(new
		 * AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], customer.getBefImage(), customer));
		 * getAuditHeaderDAO().addAudit(auditHeader);
		 */

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customer);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerDAO().delete with parameters customer,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCustomers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
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
		getCustomerDAO().delete(customer, "_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), "proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		Customer customer = (Customer) auditDetail.getModelData();
		Customer tempCustomer = null;
		if (customer.isWorkflow()) {
			tempCustomer = getCustomerDAO().getCustomerByID(customer.getId(), "_Temp");
		}
		Customer befCustomer = getCustomerDAO().getCustomerByID(customer.getId(), "");

		Customer oldCustomer = customer.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = customer.getCustCIF();
		valueParm[1] = customer.getCustCtgCode();

		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustCtgCode") + ":" + valueParm[1];

		if (customer.isNewRecord()) { // for New record or new record into work flow

			if (!customer.isWorkflow()) {// With out Work flow only new records
				if (befCustomer != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																							// is new
					if (befCustomer != null || tempCustomer != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomer == null || tempCustomer == null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customer.isWorkflow()) { // With out Work flow for update and delete
				if (befCustomer == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCustomer != null && !oldCustomer.getLastMntOn().equals(befCustomer.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}

			} else {

				if (tempCustomer == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomer != null && oldCustomer != null
						&& !oldCustomer.getLastMntOn().equals(tempCustomer.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customer.isWorkflow()) {
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

	@Override
	public void updateCustSuspenseDetails(Customer aCustomer, String tableType) {
		logger.debug("Entering");
		getCustomerDAO().updateCustSuspenseDetails(aCustomer, tableType);
		logger.debug("Leaving");
	}

	@Override
	public void saveCustSuspMovements(Customer aCustomer) {
		logger.debug("Entering");
		getCustomerDAO().saveCustSuspMovements(aCustomer);
		logger.debug("Leaving");
	}

	@Override
	public String getCustSuspRemarks(long custID) {
		return getCustomerDAO().getCustSuspRemarks(custID);
	}

	@Override
	public Customer getSuspendCustomer(Long custID) {
		return getCustomerDAO().getSuspendCustomer(custID);
	}

	/**
	 * Validate Customer details
	 * 
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doCustomerValidations(AuditHeader auditHeader) {

		AuditDetail auditDetail = auditHeader.getAuditDetail();
		CustomerDetails customerDetails = (CustomerDetails) auditDetail.getModelData();

		// validate basic details
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			auditDetail.setErrorDetail(
					validateMasterCode("BMTCustCategories", "CustCtgCode", customerDetails.getCustCtgCode()));
		}
		if (StringUtils.isNotBlank(customerDetails.getCustDftBranch())) {
			auditDetail.setErrorDetail(
					validateMasterCode("RMTBranches", "BranchCode", customerDetails.getCustDftBranch()));
		}
		if (StringUtils.isNotBlank(customerDetails.getCustBaseCcy())) {
			auditDetail
					.setErrorDetail(validateMasterCode("RMTCurrencies", "CcyCode", customerDetails.getCustBaseCcy()));
		}
		if (customerDetails.getPrimaryRelationOfficer() != 0) {
			auditDetail.setErrorDetail(
					validateMasterCode("AMTVehicleDealer", "DealerId", customerDetails.getPrimaryRelationOfficer()));
		}

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getCode())) {
					return auditDetail;
				}
			}
		}

		// validate customer basic(personal info) details
		auditDetail = validatePersonalInfo(auditDetail, customerDetails.getCustomer());

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getCode())) {
					return auditDetail;
				}
			}
		}
		return auditDetail;

	}

	/**
	 * validate customer personal details.
	 * 
	 * @param auditDetail
	 * @param customer
	 * @return AuditDetail
	 */
	private AuditDetail validatePersonalInfo(AuditDetail auditDetail, Customer customer) {
		logger.debug("Entering");

		// validate conditional mandatory fields
		ErrorDetail errorDetail = new ErrorDetail();
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			if (StringUtils.isBlank(customer.getCustFName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "firstName";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
			}

			if (SysParamUtil.isAllowed(SMTParameterConstants.CUST_LASTNAME_MANDATORY)) {
				if (StringUtils.isBlank(customer.getCustLName())) {
					String[] valueParm = new String[2];
					valueParm[0] = "lastName";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
				}
			}

			if (StringUtils.isBlank(customer.getCustSalutationCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "salutation";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
			}
			if (StringUtils.isBlank(customer.getCustGenderCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "gender";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
			}
			if (StringUtils.isBlank(customer.getCustMaritalSts())) {
				String[] valueParm = new String[2];
				valueParm[0] = "maritalStatus";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
			}

			auditDetail.setErrorDetail(
					validateMasterCode("BMTSalutations", "SalutationCode", customer.getCustSalutationCode()));
			auditDetail.setErrorDetail(validateMasterCode("BMTGenders", "GenderCode", customer.getCustGenderCode()));
			auditDetail.setErrorDetail(
					validateMasterCode("BMTMaritalStatusCodes", "MaritalStsCode", customer.getCustMaritalSts()));
		}
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)
				|| StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)) {
			if (StringUtils.isBlank(customer.getCustShrtName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "shortName";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
			}
		}
		auditDetail.setErrorDetail(validateMasterCode("BMTSectors", "SectorCode", customer.getCustSector()));
		auditDetail.setErrorDetail(validateMasterCode("BMTIndustries", "IndustryCode", customer.getCustIndustry()));
		auditDetail.setErrorDetail(validateMasterCode("RMTCustTypes", "CustTypeCode", customer.getCustTypeCode()));

		if (StringUtils.isNotBlank(customer.getCustLng()))
			auditDetail.setErrorDetail(validateMasterCode("BMTLanguage", "LngCode", customer.getCustLng()));

		if (StringUtils.isNotBlank(customer.getCustCOB()))
			auditDetail.setErrorDetail(validateMasterCode("BMTCountries", "CountryCode", customer.getCustCOB()));

		if (StringUtils.isNotBlank(customer.getCustNationality()))
			auditDetail
					.setErrorDetail(validateMasterCode("BMTCountries", "CountryCode", customer.getCustNationality()));

		if (StringUtils.isNotBlank(customer.getCustSubSector()))
			auditDetail
					.setErrorDetail(validateMasterCode("BMTSubSectors", "SubSectorCode", customer.getCustSubSector()));

		if (StringUtils.isNotBlank(customer.getCustSegment()))
			auditDetail.setErrorDetail(validateMasterCode("BMTSegments", "SegmentCode", customer.getCustSegment()));

		if (StringUtils.isNotBlank(customer.getCustSubSegment()))
			auditDetail.setErrorDetail(
					validateMasterCode("BMTSubSegments", "SubSegmentCode", customer.getCustSubSegment()));

		if (StringUtils.isNotBlank(customer.getCustParentCountry()))
			auditDetail
					.setErrorDetail(validateMasterCode("BMTCountries", "CountryCode", customer.getCustParentCountry()));

		if (StringUtils.isNotBlank(customer.getCustRiskCountry()))
			auditDetail
					.setErrorDetail(validateMasterCode("BMTCountries", "CountryCode", customer.getCustRiskCountry()));

		if (StringUtils.isNotBlank(customer.getCustEmpSts()))
			auditDetail.setErrorDetail(validateMasterCode("BMTEmpStsCodes", "EmpStsCode", customer.getCustEmpSts()));

		if (StringUtils.isNotBlank(customer.getCustDSADept())) {
			auditDetail.setErrorDetail(validateMasterCode("Department", customer.getCustDSADept()));
		}
		if (customer.getCustGroupID() > 0) {
			auditDetail.setErrorDetail(validateMasterCode("CustomerGroup", customer.getCustGroupID()));
		}

		if (StringUtils.isNotBlank(customer.getCustDSA())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM));
			Matcher matcher = pattern.matcher(customer.getCustDSA());
			if (matcher.matches() == false) {
				String[] valueParm = new String[1];
				valueParm[0] = "saleAgent";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90347", "", valueParm), "EN"));
			}
		}
		if (StringUtils.isNotBlank(customer.getCustStaffID())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM));
			Matcher matcher = pattern.matcher(customer.getCustStaffID());
			if (matcher.matches() == false) {
				String[] valueParm = new String[1];
				valueParm[0] = "staffID";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90347", "", valueParm), "EN"));
			}
		}

		Date appDate = SysParamUtil.getAppDate();
		if (customer.getCustDOB() != null && (customer.getCustDOB().compareTo(appDate) >= 0
				|| SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(customer.getCustDOB()) >= 0)) {
			String[] valueParm = new String[3];
			valueParm[0] = "Date of Birth";
			valueParm[1] = DateUtil.format(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
					PennantConstants.XMLDateFormat);
			valueParm[2] = DateUtil.format(appDate, PennantConstants.XMLDateFormat);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}
		List<CustomerEmploymentDetail> customerEmploymentDetailList = customerEmploymentDetailDAO
				.getCustomerEmploymentDetailsByID(customer.getCustID(), "");
		if (CollectionUtils.isNotEmpty(customerEmploymentDetailList)) {
			for (CustomerEmploymentDetail custEmpDetails : customerEmploymentDetailList) {
				if (custEmpDetails.getCustEmpFrom() != null
						&& custEmpDetails.getCustEmpFrom().before(customer.getCustDOB())) {
					String[] valueParm = new String[2];
					valueParm[0] = "employment startDate:"
							+ DateUtil.format(custEmpDetails.getCustEmpFrom(), PennantConstants.XMLDateFormat);
					valueParm[1] = "Cust DOB:"
							+ DateUtil.format(customer.getCustDOB(), PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}
		}
		List<CustomerDocument> customerDocumentList = customerDocumentDAO
				.getCustomerDocumentByCustomerId(customer.getCustID());
		if (customerDocumentList != null) {
			for (CustomerDocument custDocDetails : customerDocumentList) {
				if (custDocDetails.getCustDocIssuedOn() != null) {
					if (custDocDetails.getCustDocIssuedOn().before(customer.getCustDOB())) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocIssuedOn:" + DateUtil.format(custDocDetails.getCustDocIssuedOn(),
								PennantConstants.XMLDateFormat);
						valueParm[1] = "Cust DOB:"
								+ DateUtil.format(customer.getCustDOB(), PennantConstants.XMLDateFormat);
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
			}
		}
		logger.debug("Leaving");
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
	private ErrorDetail validateMasterCode(String moduleName, Object fieldValue) {
		logger.debug("Entering");

		ErrorDetail errorDetail = new ErrorDetail();
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(moduleName);
		if (moduleMapping != null) {
			String[] lovFields = moduleMapping.getLovFields();
			Object[][] filters = moduleMapping.getLovFilters();
			int count = 0;
			if (filters != null) {
				count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[0],
						(String) filters[0][0], fieldValue);
			}
			if (count <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = lovFields[0];
				valueParm[1] = Objects.toString(fieldValue, "");
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm));
			}
		}

		logger.debug("Leaving");
		return errorDetail;
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
			valueParm[1] = Objects.toString(value, "");

			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
		}

		logger.debug("Leaving");
		return errorDetail;
	}

	/**
	 * prepare the GCD Customer related data
	 */
	public void prepareGCDCustomerData(CustomerDetails customerDetails) {
		logger.debug("Entering");

		Branch branch = branchDAO.getBranchById(customerDetails.getCustomer().getCustDftBranch(), "");
		customerDetails.getCustomer().setBranchRefno(branch.getBankRefNo());
		if (customerDetails.getAddressList() != null && !customerDetails.getAddressList().isEmpty()) {
			for (CustomerAddres custAddress : customerDetails.getAddressList()) {
				Province province = provinceDAO.getProvinceById(custAddress.getCustAddrCountry(),
						custAddress.getCustAddrProvince(), "");
				custAddress.setStateRefNo(province.getBankRefNo());
				City city = cityDAO.getCityById(custAddress.getCustAddrCountry(), custAddress.getCustAddrProvince(),
						custAddress.getCustAddrCity(), "");
				custAddress.setCityRefNo(city.getBankRefNo());
			}
		}
		logger.debug("Leaving");
	}

	@Override
	public List<Customer> getCustomerDetailsByCRCPR(String custCRCPR, String custCtgCode, String type) {
		return customerDAO.getCustomerDetailsByCRCPR(custCRCPR, custCtgCode, type);
	}

	@Override
	public Customer getCustomerDetailForFinancials(String custCIF, String tableType) {
		return customerDAO.getCustomerDetailForFinancials(custCIF, tableType);

	}
}