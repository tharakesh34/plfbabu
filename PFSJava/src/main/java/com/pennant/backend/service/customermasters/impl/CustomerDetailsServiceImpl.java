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
 * FileName    		:  CustomerDetailsServiceImpl.java                                      * 	  
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
 * 22-05-2018       Sai Krishna              0.2          1. PSD - Ticket 126612 LMS > PDE >* 
 *                                                           newly added shareholder are not* 
 *                                                           displayed in PDE.              * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.customermasters.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.lang.Objects;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.bmtmasters.RatingCodeDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.customermasters.CoreCustomerDAO;
import com.pennant.backend.dao.customermasters.CorporateCustomerDetailDAO;
import com.pennant.backend.dao.customermasters.CustEmployeeDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerBalanceSheetDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerGroupDAO;
import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.customermasters.CustomerPRelationDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.perfios.PerfiosTransactionDAO;
import com.pennant.backend.dao.rmtmasters.CustomerTypeDAO;
import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.EmpStsCodeDAO;
import com.pennant.backend.dao.systemmasters.GenderDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.dao.systemmasters.NationalityCodeDAO;
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.dao.systemmasters.SectorDAO;
import com.pennant.backend.dao.systemmasters.SubSectorDAO;
import com.pennant.backend.model.PrimaryAccount;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CoreCustomer;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.perfios.PerfiosHeader;
import com.pennant.backend.model.perfios.PerfiosTransaction;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.customermasters.validation.CorporateCustomerValidation;
import com.pennant.backend.service.customermasters.validation.CustomerAddressValidation;
import com.pennant.backend.service.customermasters.validation.CustomerBalanceSheetValidation;
import com.pennant.backend.service.customermasters.validation.CustomerBankInfoValidation;
import com.pennant.backend.service.customermasters.validation.CustomerCardSalesValidation;
import com.pennant.backend.service.customermasters.validation.CustomerChequeInfoValidation;
import com.pennant.backend.service.customermasters.validation.CustomerDirectorValidation;
import com.pennant.backend.service.customermasters.validation.CustomerDocumentValidation;
import com.pennant.backend.service.customermasters.validation.CustomerEMailValidation;
import com.pennant.backend.service.customermasters.validation.CustomerEmploymentDetailValidation;
import com.pennant.backend.service.customermasters.validation.CustomerExtLiabilityValidation;
import com.pennant.backend.service.customermasters.validation.CustomerIncomeValidation;
import com.pennant.backend.service.customermasters.validation.CustomerPRelationValidation;
import com.pennant.backend.service.customermasters.validation.CustomerPhoneNumberValidation;
import com.pennant.backend.service.customermasters.validation.CustomerRatingValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.service.systemmasters.CustTypePANMappingService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.dms.DMSProperties;
import com.pennanttech.pennapps.dms.DMSStorage;
import com.pennanttech.pennapps.dms.dao.DMSQueueDAO;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;
import com.pennanttech.pff.external.Crm;
import com.pennanttech.pff.external.PerfiousService;
import com.pennanttech.pff.external.pan.dao.PrimaryAccountDAO;
import com.rits.cloning.Cloner;

public class CustomerDetailsServiceImpl extends GenericService<Customer> implements CustomerDetailsService {
	private static final Logger logger = LogManager.getLogger(CustomerDetailsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerDAO customerDAO;
	private CustomerRatingDAO customerRatingDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private CustomerPRelationDAO customerPRelationDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerEMailDAO customerEMailDAO;
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CustomerIncomeDAO customerIncomeDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentManagerDAO documentManagerDAO;
	private CorporateCustomerDetailDAO corporateCustomerDetailDAO;
	private DirectorDetailDAO directorDetailDAO;
	private CustomerBalanceSheetDAO customerBalanceSheetDAO;
	private CustomerInterfaceService customerInterfaceService;
	private CustomerStatusCodeDAO customerStatusCodeDAO;

	private CustomerTypeDAO customerTypeDAO;
	private BranchDAO branchDAO;
	private CountryDAO countryDAO;
	private NationalityCodeDAO nationalityCodeDAO;
	private EmpStsCodeDAO empStsCodeDAO;
	private CurrencyDAO currencyDAO;
	private SectorDAO sectorDAO;
	private SubSectorDAO subSectorDAO;
	private CustomerCategoryDAO customerCategoryDAO;
	private CustomerGroupDAO customerGroupDAO;
	private RelationshipOfficerDAO relationshipOfficerDAO;
	private RatingCodeDAO ratingCodeDAO;
	private BankDetailDAO bankDetailDAO;

	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustEmployeeDetailDAO custEmployeeDetailDAO;
	private CustomerDedupDAO customerDedupDAO;
	private CoreCustomerDAO coreCustomerDAO;
	private ProvinceDAO provinceDAO;
	private CityDAO cityDAO;
	private IncomeTypeDAO incomeTypeDAO;
	@Autowired
	protected IncomeDetailDAO incomeDetailDAO;
	@Autowired
	protected ExternalLiabilityDAO externalLiabilityDAO;
	private CustomerGstDetailDAO customerGstDetailDAO;

	private CustomerDocumentService customerDocumentService;
	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;
	private ApprovalStatusEnquiryService approvalStatusEnquiryService;

	// Declaring Classes For validation for Lists
	private CustomerRatingValidation customerRatingValidation;
	private CustomerPhoneNumberValidation customerPhoneNumberValidation;
	private CustomerPRelationValidation customerPRelationValidation;
	private CustomerEmploymentDetailValidation customerEmploymentDetailValidation;
	private CustomerIncomeValidation customerIncomeValidation;
	private CustomerEMailValidation customerEMailValidation;
	private CustomerAddressValidation customerAddressValidation;
	private CustomerDocumentValidation customerDocumentValidation;
	private CorporateCustomerValidation corporateCustomerValidation;
	private CustomerDirectorValidation customerDirectorValidation;
	private CustomerBalanceSheetValidation customerBalanceSheetValidation;
	private CustomerBankInfoValidation customerBankInfoValidation;
	private CustomerChequeInfoValidation customerChequeInfoValidation;
	private CustomerExtLiabilityValidation externalLiabilityValidation;
	private LovFieldDetailService lovFieldDetailService;
	private LimitRebuild limitRebuild;
	private PhoneTypeDAO phoneTypeDAO;
	private CustomerCardSalesValidation customerCardSalesValidation;

	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private PinCodeDAO pinCodeDAO;
	private CustomerService customerService;

	private FinanceMainDAO financeMainDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private VASRecordingDAO vASRecordingDAO;
	private List<FinanceMain> financeMainList;

	private GenderDAO genderDAO;
	private SalutationDAO salutationDAO;
	@Autowired(required = false)
	private Crm crm;

	@Autowired(required = false)
	@Qualifier("customerPostValidationHook")
	private PostValidationHook postValidationHook;
	private PrimaryAccountDAO primaryAccountDAO;
	private BeneficiaryDAO beneficiaryDAO;
	private PerfiosTransactionDAO perfiosTransactionDAO;
	private PerfiousService perfiosService;
	protected DocumentDetailsDAO documentDetailsDAO;
	private DMSQueueDAO dMSQueueDAO;

	//PAN 4th Letter Mapping
	private CustTypePANMappingService custTypePANMappingService;

	public CustomerDetailsServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerPRelationDAO getCustomerPRelationDAO() {
		return customerPRelationDAO;
	}

	public void setCustomerPRelationDAO(CustomerPRelationDAO customerPRelationDAO) {
		this.customerPRelationDAO = customerPRelationDAO;
	}

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

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

	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO custEmplDetailDAO) {
		this.customerEmploymentDetailDAO = custEmplDetailDAO;
	}

	public CustomerEmploymentDetailDAO getCustomerEmploymentDetailDAO() {
		return customerEmploymentDetailDAO;
	}

	public void setCustomerRatingDAO(CustomerRatingDAO customerRatingDAO) {
		this.customerRatingDAO = customerRatingDAO;
	}

	public CustomerRatingDAO getCustomerRatingDAO() {
		return customerRatingDAO;
	}

	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	public CustomerEMailDAO getCustomerEMailDAO() {
		return customerEMailDAO;
	}

	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	public CustomerPhoneNumberDAO getCustomerPhoneNumberDAO() {
		return customerPhoneNumberDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public CorporateCustomerDetailDAO getCorporateCustomerDetailDAO() {
		return corporateCustomerDetailDAO;
	}

	public void setCorporateCustomerDetailDAO(CorporateCustomerDetailDAO corporateCustomerDetailDAO) {
		this.corporateCustomerDetailDAO = corporateCustomerDetailDAO;
	}

	public DirectorDetailDAO getDirectorDetailDAO() {
		return directorDetailDAO;
	}

	public void setDirectorDetailDAO(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}

	public CustomerBalanceSheetDAO getCustomerBalanceSheetDAO() {
		return customerBalanceSheetDAO;
	}

	public void setCustomerBalanceSheetDAO(CustomerBalanceSheetDAO customerBalanceSheetDAO) {
		this.customerBalanceSheetDAO = customerBalanceSheetDAO;
	}

	public void setCustomerTypeDAO(CustomerTypeDAO customerTypeDAO) {
		this.customerTypeDAO = customerTypeDAO;
	}

	public CustomerTypeDAO getCustomerTypeDAO() {
		return customerTypeDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public CountryDAO getCountryDAO() {
		return countryDAO;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public CustomerBankInfoDAO getCustomerBankInfoDAO() {
		return customerBankInfoDAO;
	}

	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	public CustomerChequeInfoDAO getCustomerChequeInfoDAO() {
		return customerChequeInfoDAO;
	}

	public void setCustomerChequeInfoDAO(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}

	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	public CustEmployeeDetailDAO getCustEmployeeDetailDAO() {
		return custEmployeeDetailDAO;
	}

	public void setCustEmployeeDetailDAO(CustEmployeeDetailDAO custEmployeeDetailDAO) {
		this.custEmployeeDetailDAO = custEmployeeDetailDAO;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public CustomerGstDetailDAO getCustomerGstDetailDAO() {
		return customerGstDetailDAO;
	}

	public void setCustomerGstDetailDAO(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	public CustomerRatingValidation getRatingValidation() {

		if (customerRatingValidation == null) {
			this.customerRatingValidation = new CustomerRatingValidation(customerRatingDAO);
		}
		return this.customerRatingValidation;
	}

	public CustomerPhoneNumberValidation getPhoneNumberValidation() {

		if (customerPhoneNumberValidation == null) {
			this.customerPhoneNumberValidation = new CustomerPhoneNumberValidation(customerPhoneNumberDAO);
		}
		return this.customerPhoneNumberValidation;
	}

	public CustomerPRelationValidation getPRelationValidation() {

		if (customerPRelationValidation == null) {
			this.customerPRelationValidation = new CustomerPRelationValidation(customerPRelationDAO);
		}
		return this.customerPRelationValidation;
	}

	public CustomerEmploymentDetailValidation getEmploymentDetailValidation() {

		if (customerEmploymentDetailValidation == null) {
			this.customerEmploymentDetailValidation = new CustomerEmploymentDetailValidation(
					customerEmploymentDetailDAO);
		}
		return this.customerEmploymentDetailValidation;
	}

	public CustomerIncomeValidation getCustomerIncomeValidation() {

		if (customerIncomeValidation == null) {
			this.customerIncomeValidation = new CustomerIncomeValidation(customerIncomeDAO);
		}
		return this.customerIncomeValidation;
	}

	public CustomerEMailValidation getCustomerEMailValidation() {

		if (customerEMailValidation == null) {
			this.customerEMailValidation = new CustomerEMailValidation(customerEMailDAO);
		}
		return this.customerEMailValidation;
	}

	public CustomerAddressValidation getAddressValidation() {

		if (customerAddressValidation == null) {
			this.customerAddressValidation = new CustomerAddressValidation(customerAddresDAO);
		}
		return this.customerAddressValidation;
	}

	public CustomerDocumentValidation getDocumentValidation() {

		if (customerDocumentValidation == null) {
			this.customerDocumentValidation = new CustomerDocumentValidation(customerDocumentDAO);
		}
		return this.customerDocumentValidation;
	}

	public CorporateCustomerValidation getCorporateCustomerValidation() {

		if (corporateCustomerValidation == null) {
			this.corporateCustomerValidation = new CorporateCustomerValidation(corporateCustomerDetailDAO);
		}
		return this.corporateCustomerValidation;
	}

	public CustomerDirectorValidation getDirectorValidation() {

		if (customerDirectorValidation == null) {
			this.customerDirectorValidation = new CustomerDirectorValidation(directorDetailDAO);
		}
		return this.customerDirectorValidation;
	}

	public CustomerBalanceSheetValidation getBalanceSheetValidation() {

		if (customerBalanceSheetValidation == null) {
			this.customerBalanceSheetValidation = new CustomerBalanceSheetValidation(customerBalanceSheetDAO);
		}
		return this.customerBalanceSheetValidation;
	}

	public CustomerBankInfoValidation getCustomerBankInfoValidation() {

		if (customerBankInfoValidation == null) {
			this.customerBankInfoValidation = new CustomerBankInfoValidation(customerBankInfoDAO);
		}
		return this.customerBankInfoValidation;
	}

	public CustomerChequeInfoValidation getCustomerChequeInfoValidation() {

		if (customerChequeInfoValidation == null) {
			this.customerChequeInfoValidation = new CustomerChequeInfoValidation(customerChequeInfoDAO);
		}
		return this.customerChequeInfoValidation;
	}

	public CustomerExtLiabilityValidation getExternalLiabilityValidation() {
		if (externalLiabilityValidation == null) {
			this.externalLiabilityValidation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		}
		return this.externalLiabilityValidation;
	}

	/**
	 * @return the customer for New Record
	 */
	@Override
	public CustomerDetails getNewCustomer(boolean createNew, CustomerDetails customerDetails) {
		if (customerDetails == null) {
			customerDetails = new CustomerDetails();
		}
		CustomerEmploymentDetail employmentDetail = new CustomerEmploymentDetail();
		employmentDetail.setNewRecord(true);
		CorporateCustomerDetail corporateCustomerDetail = new CorporateCustomerDetail();
		corporateCustomerDetail.setNewRecord(true);

		customerDetails.setCustomer(getCustomerDAO().getNewCustomer(createNew, customerDetails.getCustomer()));

		if (CollectionUtils.isEmpty(customerDetails.getRatingsList())) {
			customerDetails.setRatingsList(new ArrayList<CustomerRating>());
		}

		if (CollectionUtils.isEmpty(customerDetails.getEmploymentDetailsList())) {
			customerDetails.setEmploymentDetailsList(new ArrayList<CustomerEmploymentDetail>());
		}

		if (CollectionUtils.isEmpty(customerDetails.getAddressList())) {
			customerDetails.setAddressList(new ArrayList<CustomerAddres>());
		}

		if (CollectionUtils.isEmpty(customerDetails.getCustomerEMailList())) {
			customerDetails.setCustomerEMailList(new ArrayList<CustomerEMail>());
		}

		if (CollectionUtils.isEmpty(customerDetails.getCustomerPhoneNumList())) {
			customerDetails.setCustomerPhoneNumList(new ArrayList<CustomerPhoneNumber>());
		}

		if (CollectionUtils.isEmpty(customerDetails.getCustomerIncomeList())) {
			customerDetails.setCustomerIncomeList(new ArrayList<CustomerIncome>());
		}

		if (CollectionUtils.isEmpty(customerDetails.getCustomerDocumentsList())) {
			customerDetails.setCustomerDocumentsList(new ArrayList<CustomerDocument>());
		}
		customerDetails.setNewRecord(true);
		prepareDefaultIncomeExpenseList(customerDetails);
		return customerDetails;
	}

	/**
	 * Prepare Default Customer Income List
	 * 
	 * @param customerDetails
	 */
	@Override
	public void prepareDefaultIncomeExpenseList(CustomerDetails customerDetails) {
		List<CustomerIncome> customerIncomes = new ArrayList<CustomerIncome>();
		if (ImplementationConstants.POPULATE_DFT_INCOME_DETAILS && customerDetails.isNewRecord()) {
			List<IncomeType> incomeTypes = incomeTypeDAO.getDefaultIncomeTypeList();
			if (CollectionUtils.isNotEmpty(incomeTypes)) {
				for (IncomeType incomeType : incomeTypes) {
					CustomerIncome customerIncome = new CustomerIncome();
					customerIncome.setNewRecord(true);
					customerIncome.setWorkflowId(0);
					customerIncome.setRecordType(PennantConstants.RCD_ADD);
					customerIncome.setCategory(incomeType.getCategory());
					customerIncome.setCategoryDesc(incomeType.getLovDescCategoryName());
					customerIncome.setMargin(incomeType.getMargin());
					customerIncome.setIncomeExpense(incomeType.getIncomeExpense());
					customerIncome.setIncomeType(incomeType.getIncomeTypeCode());
					customerIncome.setIncomeTypeDesc(incomeType.getIncomeTypeDesc());
					customerIncomes.add(customerIncome);
				}
			}
			customerDetails.setCustomerIncomeList(customerIncomes);
		}
	}

	@Override
	public CustomerDetails getCustomerDetailsbyIdandPhoneType(long id, String phoneType) {
		CustomerDetails customerDetails = new CustomerDetails();

		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, ""));
		customerDetails.setCustomerPhoneNumList(
				customerPhoneNumberDAO.getCustomerPhoneNumberByCustomerPhoneType(id, "", phoneType));
		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, "_View"));

		return customerDetails;
	}

	/**
	 * @return the customerDetails for the given customer id.
	 */
	private CustomerDetails getCustomerById(long id, String type) {
		logger.debug(Literal.ENTERING);
		List<VASRecording> vasRecordingList = null;
		List<FinanceEnquiry> financeEnquiryList = null;
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);

		PrimaryAccount primaryAccount = getPrimaryAccountDAO()
				.getPrimaryAccountDetails(customerDetails.getCustomer().getCustCRCPR());
		if (primaryAccount != null) {
			customerDetails.getCustomer().setPrimaryIdName(primaryAccount.getDocumentName());
		}

		if (ImplementationConstants.ALLOW_CUSTOMER_RATINGS) {
			customerDetails.setRatingsList(customerRatingDAO.getCustomerRatingByCustomer(id, type));
		}
		if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			customerDetails.setEmploymentDetailsList(
					getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailsByID(id, type));
		} else {
			customerDetails.setCustEmployeeDetail(getCustEmployeeDetailDAO().getCustEmployeeDetailById(id, type));
		}
		if (ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			customerDetails.setCustomerIncomeList(incomeDetailDAO.getIncomesByCustomer(id, type));
		}
		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
		if (ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS) {
			customerDetails.setCustomerDirectorList(directorDetailDAO.getCustomerDirectorByCustomer(id, type));
		}
		customerDetails.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));
		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			for (CustomerBankInfo customerBankInfo : customerDetails.getCustomerBankInfoList()) {
				customerBankInfo.setBankInfoDetails(
						customerBankInfoDAO.getBankInfoDetailById(customerBankInfo.getBankId(), type));
				if (customerBankInfo.getBankInfoDetails() != null && customerBankInfo.getBankInfoDetails().size() > 0) {
					for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
						bankInfoDetail.setBankInfoSubDetails(customerBankInfoDAO.getBankInfoSubDetailById(
								bankInfoDetail.getBankId(), bankInfoDetail.getMonthYear(), type));
					}
				}
				customerBankInfo.setExternalDocuments(
						customerDocumentDAO.getExternalDocuments(customerBankInfo.getBankId(), ""));

			}
		}
		customerDetails.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(id); // F

		List<CustomerExtLiability> extLiabilities = externalLiabilityDAO.getLiabilities(liability.getCustId(), type);
		customerDetails.setCustomerExtLiabilityList(extLiabilities);

		if (CollectionUtils.isNotEmpty(extLiabilities)) {
			for (CustomerExtLiability extLiability : extLiabilities) {
				extLiability.setExtLiabilitiesPayments(
						customerExtLiabilityDAO.getExtLiabilitySubDetailById(extLiability.getId(), type));
			}
		}

		List<CustCardSales> custCardSalesDetails = customerCardSalesInfoDAO
				.getCardSalesInfoByCustomer(liability.getCustId(), type);
		customerDetails.setCustCardSales(custCardSalesDetails);

		if (CollectionUtils.isNotEmpty(custCardSalesDetails)) {
			for (CustCardSales custCardSale : custCardSalesDetails) {
				custCardSale.setCustCardMonthSales(
						customerCardSalesInfoDAO.getCardSalesInfoSubDetailById(custCardSale.getId(), type));
			}
		}

		customerDetails.setCustFinanceExposureList(getCustomerDAO().getCustomerFinanceDetailById(id));

		customerDetails.setFinanceMainList(getFinanceMainDAO().getFinanceByCustId(id, ""));
		customerDetails.setCollateraldetailList(getCollateralSetupDAO().getApprovedCollateralByCustId(id, type));

		for (FinanceMain financemain : customerDetails.getFinanceMainList()) {
			if (vasRecordingList == null) {
				vasRecordingList = new ArrayList<VASRecording>();
			}
			vasRecordingList
					.addAll(getvASRecordingDAO().getVASRecordingsByLinkRef(financemain.getFinReference(), type));
			customerDetails.setVasRecordingList(vasRecordingList);
		}

		customerDetails.setCustomerGstList(customerGstDetailDAO.getCustomerGSTById(id, type));
		List<CustomerGST> customerGstList = customerDetails.getCustomerGstList();
		if (CollectionUtils.isNotEmpty(customerGstList)) {
			for (CustomerGST customerGST : customerGstList) {
				customerGST.setCustomerGSTDetailslist(
						customerGstDetailDAO.getCustomerGSTDetailsByCustomer(customerGST.getId(), type));
			}
		}
		if (financeEnquiryList == null) {
			financeEnquiryList = new ArrayList<FinanceEnquiry>();
		}
		financeEnquiryList.addAll(getFinanceMainDAO().getAllFinanceDetailsByCustId(id));
		customerDetails.setCustomerFinances(financeEnquiryList);

		if (approvalStatusEnquiryService != null) {
			customerDetails
					.setCustomerFinanceDetailList(approvalStatusEnquiryService.getListOfCustomerFinanceById(id, null));
		}

		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	@Override
	public void setCustomerBasicDetails(CustomerDetails customer) {

		if (customer == null || customer.getCustID() == Long.MIN_VALUE) {
			throw new AppException("Customer Id cannot be blank.");
		}

		long custId = customer.getCustID();
		String tableType = "_aView";

		customer.setCustomer(customerDAO.getCustomerByID(custId, tableType));
		customer.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(custId, tableType));
		customer.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(custId, tableType));
		customer.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(custId, tableType));
	}

	/**
	 * @return the customerDetails for the given customer id.
	 */
	private CustomerDetails getCustomerDetailsbyID(long id, boolean reqChildDetails, String type) {
		logger.debug(Literal.ENTERING);

		CustomerDetails cd = new CustomerDetails();
		cd.setCustomer(customerDAO.getCustomerByID(id, type));
		cd.setCustID(id);

		Customer customer = cd.getCustomer();
		PrimaryAccount primaryAccount = primaryAccountDAO.getPrimaryAccountDetails(customer.getCustCRCPR());

		if (primaryAccount != null) {
			customer.setPrimaryIdName(primaryAccount.getDocumentName());
		}

		if (!reqChildDetails) {
			return cd;
		}
		if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			cd.setEmploymentDetailsList(customerEmploymentDetailDAO.getCustomerEmploymentDetailsByID(id, type));
		} else {
			cd.setCustEmployeeDetail(custEmployeeDetailDAO.getCustEmployeeDetailById(id, type));
		}
		if (ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			cd.setCustomerIncomeList(incomeDetailDAO.getIncomesByCustomer(id, type));
		}
		// ### Ticket 126612 LMS > PDE > newly added shareholder are not
		// displayed in PDE. Changed the condition to
		// non individual.
		if (StringUtils.isNotEmpty(customer.getCustCtgCode())
				&& !StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			if (ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS) {
				cd.setCustomerDirectorList(directorDetailDAO.getCustomerDirectorByCustomer(id, type));
			}
			if (ImplementationConstants.ALLOW_CUSTOMER_RATINGS) {
				cd.setRatingsList(customerRatingDAO.getCustomerRatingByCustomer(id, type));
			}
		}
		cd.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
		cd.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		cd.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		cd.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		cd.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));
		if (cd.getCustomerBankInfoList() != null && cd.getCustomerBankInfoList().size() > 0) {
			for (CustomerBankInfo customerBankInfo : cd.getCustomerBankInfoList()) {
				customerBankInfo.setBankInfoDetails(
						customerBankInfoDAO.getBankInfoDetailById(customerBankInfo.getBankId(), type));

				if (CollectionUtils.isNotEmpty(customerBankInfo.getBankInfoDetails())) {
					for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
						bankInfoDetail.setBankInfoSubDetails(customerBankInfoDAO.getBankInfoSubDetailById(
								bankInfoDetail.getBankId(), bankInfoDetail.getMonthYear(), type));
					}
					customerBankInfo.setExternalDocuments(
							customerDocumentDAO.getExternalDocuments(customerBankInfo.getBankId(), ""));
				}
			}
		}
		cd.setCustomerGstList(customerGstDetailDAO.getCustomerGSTById(id, type));

		if (cd.getCustomerGstList() != null && cd.getCustomerGstList().size() > 0) {
			for (CustomerGST customerGST : cd.getCustomerGstList()) {
				customerGST.setCustomerGSTDetailslist(
						customerGstDetailDAO.getCustomerGSTDetailsByCustomer(customerGST.getId(), type));
			}
		}
		cd.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));

		CustomerExtLiability liability = new CustomerExtLiability();
		liability.setCustId(id);
		cd.setCustomerExtLiabilityList(externalLiabilityDAO.getLiabilities(liability.getCustId(), type));

		if (CollectionUtils.isNotEmpty(cd.getCustomerExtLiabilityList())) {
			for (CustomerExtLiability extLiability : cd.getCustomerExtLiabilityList()) {
				extLiability.setExtLiabilitiesPayments(
						customerExtLiabilityDAO.getExtLiabilitySubDetailById(extLiability.getId(), type));
			}
		}

		cd.setCustCardSales(customerCardSalesInfoDAO.getCardSalesInfoByCustomer(id, type));
		if (cd.getCustCardSales() != null && cd.getCustCardSales().size() > 0) {
			for (CustCardSales customerCardSalesInfo : cd.getCustCardSales()) {
				customerCardSalesInfo.setCustCardMonthSales(
						customerCardSalesInfoDAO.getCardSalesInfoSubDetailById(customerCardSalesInfo.getId(), type));
			}
		}
		cd.setCustFinanceExposureList(getCustomerDAO().getCustomerFinanceDetailById(id));

		logger.debug(Literal.LEAVING);
		return cd;
	}

	/**
	 * Getting the customer Email, phone and address lisat details
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	@Override
	public CustomerDetails getCustomerChildDetails(long id, String type) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);

		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));

		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	/**
	 * getCustomerById fetch the details by using CustomerDAO's getCustomerById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Customer
	 */
	@Override
	public CustomerDetails getCustomerById(long id) {
		return getCustomerById(id, "_View");
	}

	@Override
	public CustomerDetails getCustomerDetailsById(long id, boolean reqChildDetails, String type) {
		return getCustomerDetailsbyID(id, reqChildDetails, type);
	}

	@Override
	public Customer getCustomerByCIF(String id) {
		return getCustomerDAO().getCustomerByCIF(id, "");
	}

	@Override
	public WIFCustomer getWIFCustomerByCIF(long id) {
		return getCustomerDAO().getWIFCustomerByCIF(id, "");
	}

	@Override
	public Customer getCheckCustomerByCIF(String cif) {
		return getCustomerDAO().getCustomerByCIF(cif, "_View");
	}

	@Override
	public Customer checkCustomerByCIF(String cif, String type) {
		return getCustomerDAO().checkCustomerByCIF(cif, type);
	}

	/**
	 * Get Customer and Customer Documents
	 */
	@Override
	public CustomerDetails getCustomerAndCustomerDocsById(long id, String type) {

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);

		customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));

		return customerDetails;
	}

	/**
	 * getApprovedCustomerById fetch the details by using CustomerDAO's getCustomerById method . with parameter id and
	 * type as blank. it fetches the approved records from the Customers.
	 * 
	 * @param id
	 *            (String)
	 * @return Customer
	 */
	@Override
	public CustomerDetails getApprovedCustomerById(long id) {
		return getCustomerById(id, "");
	}

	/**
	 * /** saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Customers/Customers_Temp by
	 * using CustomerDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by
	 * using CustomerDAO's update method 3) Audit the record in to AuditHeader and AdtCustomers by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		String tableType = "";
		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		if (customer.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customer.isNew()) {
			if (StringUtils.isEmpty(tableType)) {
				customer.setRecordType("");
				customer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			getCustomerDAO().save(customer, tableType);
			auditHeader.getAuditDetail().setModelData(customer);
			auditHeader.setAuditReference(String.valueOf(customer.getCustID()));

		} else {
			getCustomerDAO().update(customer, tableType);
		}

		// save CoreCustomer object
		CoreCustomer coreCustomer = customerDetails.getCoreCustomer();
		if (coreCustomer != null) {
			coreCustomer.setCustID(customerDetails.getCustomer().getCustID());
			if (coreCustomer.isNewRecord()) {
				getCoreCustomerDAO().save(coreCustomer);
			} else {
				getCoreCustomerDAO().update(coreCustomer);
			}
		}

		customerDetails.setCustID(customer.getCustID());

		if (customerDetails.getCustEmployeeDetail() != null) {
			CustEmployeeDetail custEmployeeDetail = getCustEmployeeDetailDAO()
					.getCustEmployeeDetailById(customer.getCustID(), tableType);
			CustEmployeeDetail custEmpDetail = customerDetails.getCustEmployeeDetail();
			custEmpDetail.setWorkflowId(0);
			custEmpDetail.setCustID(customer.getCustID());
			custEmpDetail.setRecordType(customer.getRecordType());
			custEmpDetail.setRecordStatus(customer.getRecordStatus());
			custEmpDetail.setBefImage(custEmployeeDetail);
			String auditTranType;
			if (custEmployeeDetail == null) {
				auditTranType = PennantConstants.TRAN_ADD;
				getCustEmployeeDetailDAO().save(custEmpDetail, tableType);
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
				getCustEmployeeDetailDAO().update(custEmpDetail, tableType);
			}
			String[] fields = PennantJavaUtil.getFieldDetails(custEmpDetail, custEmpDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
					custEmpDetail.getBefImage(), custEmpDetail));
		}

		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
			details = processingRatingList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}
		if (customerDetails.getEmploymentDetailsList() != null
				&& customerDetails.getEmploymentDetailsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
			details = processingCustomerEmploymentDetailList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerPhoneNumList() != null && customerDetails.getCustomerPhoneNumList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
			details = processingPhoneNumberList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Income");
			details = processingIncomeList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerEMailList() != null && customerDetails.getCustomerEMailList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
			details = processingEMailList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Address");
			details = processingAddressList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerDocumentsList() != null
				&& customerDetails.getCustomerDocumentsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Document");
			details = processingDocumentList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerDirectorList() != null && customerDetails.getCustomerDirectorList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Director");
			details = processingDirectorList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerBankInfo");
			details = processingBankInfoList(details, tableType, customerDetails.getCustID());
			/*
			 * List<AuditDetail> bankInfoAuditList = new ArrayList<>(); if(details != null){ for (AuditDetail
			 * auditDetail : details) { CustomerBankInfo customerBankInfo = (CustomerBankInfo)
			 * auditDetail.getModelData(); bankInfoAuditList.addAll(customerBankInfo.getAuditDetailMap().get
			 * ("BankInfoDetail")); } } auditDetails.addAll(bankInfoAuditList);
			 */
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerChequeInfo");
			details = processingChequeInfoList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerExtLiability");
			details = processingExtLiabilityList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		// Extended field Details
		if (customerDetails.getExtendedFieldRender() != null) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					customerDetails.getExtendedFieldHeader(), tableType, 0);
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSales");
			details = processingCardSalesInfoList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSalesDetails");
			details = processingCardSaleInfoDetailList(details, "", Long.MIN_VALUE);
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerGstList() != null) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerGST");
			details = processingCustomerGSTList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		auditDetails.addAll(saveOrUpdateDedupDetails(customerDetails));

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);

		// TODO:remove comments for below lines of code when MDM interface is
		// ready for update customer service
		// update core customer
		/*
		 * if(!StringUtils.isBlank(customer.getCustCoreBank())) { processUpdateCustData(customerDetails); }
		 */

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String auditTranType;
		Customer customer = customerDetails.getCustomer();

		if (StringUtils.isEmpty(tableType)) {
			customer.setRecordType("");
			customer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		customer.setWorkflowId(0);
		if (customer.isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
			long custID = customerDAO.save(customer, tableType);
			financeDetail.getFinScheduleData().getFinanceMain().setCustID(custID);
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
			customer.setVersion(customer.getVersion() + 1);
			customerDAO.update(customer, tableType);
		}
		if (financeDetail.getCustomerDetails().getCustomer().isDedupFound()) {
			getCustomerDAO().updateProspectCustomer(customer);
		}
		String[] fields = PennantJavaUtil.getFieldDetails(customer, customer.getExcludeFields());
		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
				customer.getBefImage(), customer));

		if (customerDetails.getCustEmployeeDetail() != null) {
			CustEmployeeDetail custEmployeeDetail = getCustEmployeeDetailDAO()
					.getCustEmployeeDetailById(customer.getCustID(), tableType);
			CustEmployeeDetail custEmpDetail = customerDetails.getCustEmployeeDetail();
			custEmpDetail.setWorkflowId(0);
			custEmpDetail.setCustID(customer.getCustID());
			custEmpDetail.setRecordType(customer.getRecordType());
			custEmpDetail.setRecordStatus(customer.getRecordStatus());
			custEmpDetail.setBefImage(custEmployeeDetail);

			boolean isSaveRecord = false;
			if (financeMain.isNew()) {
				if (custEmployeeDetail == null) {
					isSaveRecord = true;
				} else {
					isSaveRecord = false;
				}
			} else {
				if (custEmpDetail.isNewRecord()) {
					isSaveRecord = true;
				} else {
					isSaveRecord = false;
				}
			}
			if (isSaveRecord) {
				auditTranType = PennantConstants.TRAN_ADD;
				getCustEmployeeDetailDAO().save(custEmpDetail, tableType);
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
				custEmpDetail.setVersion(custEmpDetail.getVersion() + 1);
				getCustEmployeeDetailDAO().update(custEmpDetail, tableType);
			}

			fields = PennantJavaUtil.getFieldDetails(custEmpDetail, custEmpDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
					custEmpDetail.getBefImage(), custEmpDetail));
		}

		if (customerDetails.getEmploymentDetailsList() != null) {
			for (CustomerEmploymentDetail cusEmploymentDetail : customerDetails.getEmploymentDetailsList()) {
				if (StringUtils.isBlank(cusEmploymentDetail.getRecordType())) {
					continue;
				}
				cusEmploymentDetail.setWorkflowId(0);
				cusEmploymentDetail.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(cusEmploymentDetail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					cusEmploymentDetail.setRecordType("");
					cusEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(cusEmploymentDetail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerEmploymentDetailDAO.delete(cusEmploymentDetail, tableType);
				} else if (cusEmploymentDetail.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerEmploymentDetailDAO.save(cusEmploymentDetail, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerEmploymentDetailDAO.update(cusEmploymentDetail, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(cusEmploymentDetail, cusEmploymentDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						cusEmploymentDetail.getBefImage(), cusEmploymentDetail));
			}
		}

		if (customerDetails.getRatingsList() != null) {
			for (CustomerRating customerRating : customerDetails.getRatingsList()) {
				if (StringUtils.isBlank(customerRating.getRecordType())) {
					continue;
				}
				customerRating.setWorkflowId(0);
				customerRating.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(customerRating.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					customerRating.setRecordType("");
					customerRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerRating.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerRatingDAO.delete(customerRating, tableType);
				} else if (customerRating.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerRatingDAO.save(customerRating, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerRatingDAO.update(customerRating, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(customerRating, customerRating.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						customerRating.getBefImage(), customerRating));
			}
		}

		if (customerDetails.getCustomerIncomeList() != null) {
			for (CustomerIncome customerIncome : customerDetails.getCustomerIncomeList()) {
				if (StringUtils.isBlank(customerIncome.getRecordType())) {
					continue;
				}
				customerIncome.setWorkflowId(0);
				customerIncome.setCustId(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(customerIncome.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					customerIncome.setRecordType("");
					customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerIncome.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					incomeDetailDAO.delete(customerIncome.getId(), tableType);
				} else if (customerIncome.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerIncomeDAO.setLinkId(customerIncome);
					incomeDetailDAO.save(customerIncome, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					incomeDetailDAO.update(customerIncome, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(customerIncome, customerIncome.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						customerIncome.getBefImage(), customerIncome));
			}
		}

		if (customerDetails.getCustomerDirectorList() != null) {
			for (DirectorDetail directorDetail : customerDetails.getCustomerDirectorList()) {
				if (StringUtils.isBlank(directorDetail.getRecordType())) {
					continue;
				}
				directorDetail.setWorkflowId(0);
				directorDetail.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(directorDetail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					directorDetail.setRecordType("");
					directorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(directorDetail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					directorDetailDAO.delete(directorDetail, tableType);
				} else if (directorDetail.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					directorDetailDAO.save(directorDetail, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					directorDetailDAO.update(directorDetail, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(directorDetail, directorDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						directorDetail.getBefImage(), directorDetail));
			}
		}

		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument customerDocument : customerDetails.getCustomerDocumentsList()) {
				if (StringUtils.isBlank(customerDocument.getRecordType())) {
					continue;
				}
				customerDocument.setWorkflowId(0);
				customerDocument.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					customerDocument.setRecordType("");
					customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerDocumentDAO.delete(customerDocument, tableType);
				} else if (customerDocument.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerDocument.setFinReference(financeMain.getFinReference());
					customerDocument.setOfferId(StringUtils.trimToEmpty(financeMain.getOfferId()));
					customerDocument.setApplicationNo(StringUtils.trimToEmpty(financeMain.getApplicationNo()));
					saveDocument(DMSModule.CUSTOMER, null, customerDocument);

					customerDocumentDAO.save(customerDocument, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerDocument.setFinReference(financeMain.getFinReference());
					customerDocument.setOfferId(StringUtils.trimToEmpty(financeMain.getOfferId()));
					customerDocument.setApplicationNo(StringUtils.trimToEmpty(financeMain.getApplicationNo()));
					customerDocument.setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
					saveDocument(DMSModule.CUSTOMER, null, customerDocument);
					customerDocumentDAO.update(customerDocument, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(customerDocument, customerDocument.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						customerDocument.getBefImage(), customerDocument));
			}
		}

		if (customerDetails.getAddressList() != null) {
			for (CustomerAddres custaddress : customerDetails.getAddressList()) {
				if (StringUtils.isBlank(custaddress.getRecordType())) {
					continue;
				}
				custaddress.setWorkflowId(0);
				custaddress.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(custaddress.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					custaddress.setRecordType("");
					custaddress.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custaddress.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerAddresDAO.delete(custaddress, tableType);
				} else if (custaddress.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerAddresDAO.save(custaddress, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerAddresDAO.update(custaddress, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(custaddress, custaddress.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custaddress.getBefImage(), custaddress));
			}
		}

		if (customerDetails.getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhoneNumber : customerDetails.getCustomerPhoneNumList()) {
				if (StringUtils.isBlank(custPhoneNumber.getRecordType())) {
					continue;
				}
				custPhoneNumber.setWorkflowId(0);
				custPhoneNumber.setPhoneCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(custPhoneNumber.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					custPhoneNumber.setRecordType("");
					custPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custPhoneNumber.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerPhoneNumberDAO.delete(custPhoneNumber, tableType);
				} else if (custPhoneNumber.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerPhoneNumberDAO.save(custPhoneNumber, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerPhoneNumberDAO.update(custPhoneNumber, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(custPhoneNumber, custPhoneNumber.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custPhoneNumber.getBefImage(), custPhoneNumber));
			}
		}

		if (customerDetails.getCustomerEMailList() != null) {
			for (CustomerEMail customerEMail : customerDetails.getCustomerEMailList()) {
				if (StringUtils.isBlank(customerEMail.getRecordType())) {
					continue;
				}
				customerEMail.setWorkflowId(0);
				customerEMail.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(customerEMail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					customerEMail.setRecordType("");
					customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerEMail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerEMailDAO.delete(customerEMail, tableType);
				} else if (customerEMail.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerEMailDAO.save(customerEMail, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerEMailDAO.update(customerEMail, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(customerEMail, customerEMail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						customerEMail.getBefImage(), customerEMail));
			}
		}

		if (customerDetails.getCustomerBankInfoList() != null) {
			for (CustomerBankInfo custBankInfo : customerDetails.getCustomerBankInfoList()) {
				if (StringUtils.isBlank(custBankInfo.getRecordType())) {
					continue;
				}
				custBankInfo.setWorkflowId(0);
				custBankInfo.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(custBankInfo.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					custBankInfo.setRecordType("");
					custBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custBankInfo.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;

					if (CollectionUtils.isNotEmpty(custBankInfo.getBankInfoDetails())) {
						for (BankInfoDetail bankInfoDetail : custBankInfo.getBankInfoDetails()) {
							if (CollectionUtils.isNotEmpty(bankInfoDetail.getBankInfoSubDetails())) {
								customerBankInfoDAO.delete(bankInfoDetail.getBankInfoSubDetails(), tableType);
							}
							customerBankInfoDAO.delete(bankInfoDetail, tableType);
						}
					}
					customerBankInfoDAO.delete(custBankInfo, tableType);
				} else if (custBankInfo.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerBankInfoDAO.save(custBankInfo, tableType);
					// BankInfoDetails
					if (custBankInfo.getBankInfoDetails().size() > 0) {
						for (BankInfoDetail bankInfoDetail : custBankInfo.getBankInfoDetails()) {
							bankInfoDetail.setBankId(custBankInfo.getBankId());
							customerBankInfoDAO.save(bankInfoDetail, tableType);
							// Audit
							fields = PennantJavaUtil.getFieldDetails(bankInfoDetail, bankInfoDetail.getExcludeFields());
							auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0],
									fields[1], bankInfoDetail.getBefImage(), bankInfoDetail));
						}
					}
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerBankInfoDAO.update(custBankInfo, tableType);
					// BankInfoDetails
					if (custBankInfo.getBankInfoDetails().size() > 0) {
						for (BankInfoDetail bankInfoDetail : custBankInfo.getBankInfoDetails()) {
							if (StringUtils.isBlank(tableType)) {
								bankInfoDetail.setRecordType("");
								bankInfoDetail.setRoleCode("");
								bankInfoDetail.setNextRoleCode("");
								bankInfoDetail.setTaskId("");
								bankInfoDetail.setNextTaskId("");
							}
							if (bankInfoDetail.isNewRecord()) {
								bankInfoDetail.setBankId(custBankInfo.getBankId());
								customerBankInfoDAO.save(bankInfoDetail, tableType);

								/*
								 * for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
								 * bankInfoSubDetail.setBankId(custBankInfo. getBankId()); if
								 * (StringUtils.isBlank(tableType)) { bankInfoSubDetail.setRecordType("");
								 * bankInfoSubDetail.setRoleCode(""); bankInfoSubDetail.setNextRoleCode("");
								 * bankInfoSubDetail.setTaskId(""); bankInfoSubDetail.setNextTaskId(""); } }
								 * 
								 * if (CollectionUtils.isNotEmpty(bankInfoDetail. getBankInfoSubDetails())) {
								 * customerBankInfoDAO.save(bankInfoDetail. getBankInfoSubDetails(), tableType); }
								 */

							} else {
								customerBankInfoDAO.update(bankInfoDetail, tableType);

								/*
								 * if (CollectionUtils.isNotEmpty(bankInfoDetail. getBankInfoSubDetails())) { for
								 * (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
								 * bankInfoSubDetail.setBankId(custBankInfo. getBankId()); if
								 * (!bankInfoSubDetail.isNewRecord()) { if (StringUtils.isBlank(tableType)) {
								 * bankInfoSubDetail.setRecordType(""); bankInfoSubDetail.setRoleCode("");
								 * bankInfoSubDetail.setNextRoleCode(""); bankInfoSubDetail.setTaskId("");
								 * bankInfoSubDetail.setNextTaskId(""); } customerBankInfoDAO.update(bankInfoSubDetail,
								 * tableType); } } }
								 */
							}

							// Audit
							fields = PennantJavaUtil.getFieldDetails(bankInfoDetail, bankInfoDetail.getExcludeFields());
							auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0],
									fields[1], bankInfoDetail.getBefImage(), bankInfoDetail));
						}
					}
				}

				// TOTO : TEMPERORY FIX, NEED TO HANDLE PERMENANTLY
				if (CollectionUtils.isNotEmpty(custBankInfo.getBankInfoDetails())
						&& !PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(custBankInfo.getRecordType())) {
					for (BankInfoDetail bankInfoDetail : custBankInfo.getBankInfoDetails()) {
						if (CollectionUtils.isNotEmpty(bankInfoDetail.getBankInfoSubDetails())) {
							for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
								bankInfoSubDetail.setBankId(custBankInfo.getBankId());
								if (StringUtils.isBlank(tableType)) {
									bankInfoSubDetail.setRecordType("");
									bankInfoSubDetail.setRoleCode("");
									bankInfoSubDetail.setNextRoleCode("");
									bankInfoSubDetail.setTaskId("");
									bankInfoSubDetail.setNextTaskId("");
								}
							}

							customerBankInfoDAO.delete(bankInfoDetail.getBankInfoSubDetails(), tableType);
							customerBankInfoDAO.save(bankInfoDetail.getBankInfoSubDetails(), tableType);
						}
					}
				}
				if (CollectionUtils.isNotEmpty(custBankInfo.getExternalDocuments())) {

					for (ExternalDocument externalDocument : custBankInfo.getExternalDocuments()) {
						if (StringUtils.isEmpty(externalDocument.getRecordType())) {
							continue;
						}
						externalDocument.setBankId(custBankInfo.getBankId());
						externalDocument.setCustId(custBankInfo.getCustID());
						externalDocument.setFinReference(financeMain.getFinReference());

						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(externalDocument.getDocImage());
						externalDocument.setDocRefId(documentManagerDAO.save(documentManager));

						customerDocumentDAO.save(externalDocument, "");
					}
				}
				//Beneficiary Details save when customer created through loan queue
				if (StringUtils.equals(custBankInfo.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					if (custBankInfo.isAddToBenficiary()) {
						addToCustomerBeneficiary(custBankInfo, custBankInfo.getCustID());
					}
				}
				fields = PennantJavaUtil.getFieldDetails(custBankInfo, custBankInfo.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custBankInfo.getBefImage(), custBankInfo));
			}
		}
		if (customerDetails.getCustomerGstList() != null) {
			for (CustomerGST customerGST : customerDetails.getCustomerGstList()) {
				if (StringUtils.isBlank(customerGST.getRecordType())) {
					continue;
				}
				customerGST.setWorkflowId(0);
				customerGST.setCustId(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(customerGST.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					customerGST.setRecordType("");
					customerGST.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerGST.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerGstDetailDAO.delete(customerGST, tableType);
				} else if (customerGST.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					long headerId = customerGstDetailDAO.save(customerGST, tableType);

					if (customerGST.getCustomerGSTDetailslist().size() > 0) {
						for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
							if (StringUtils.isEmpty(tableType)
									&& !StringUtils.trimToEmpty(customerGSTDetails.getRecordType())
											.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
								customerGSTDetails.setRecordType("");
								customerGSTDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							}
							customerGSTDetails.setHeaderId(headerId);
							customerGstDetailDAO.save(customerGSTDetails, tableType);
							// Audit
							fields = PennantJavaUtil.getFieldDetails(customerGSTDetails,
									customerGSTDetails.getExcludeFields());
							auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0],
									fields[1], customerGSTDetails.getBefImage(), customerGSTDetails));
						}
					}
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerGstDetailDAO.update(customerGST, tableType);
					// BankInfoDetails
					if (customerGST.getCustomerGSTDetailslist().size() > 0) {
						for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
							if (customerGSTDetails.isNewRecord()) {
								if (StringUtils.isEmpty(tableType)
										&& !StringUtils.trimToEmpty(customerGSTDetails.getRecordType())
												.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
									customerGSTDetails.setRecordType("");
									customerGSTDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
								}
								customerGSTDetails.setHeaderId(customerGST.getId());
								customerGstDetailDAO.save(customerGSTDetails, tableType);
							} else {
								if (StringUtils.isEmpty(tableType)
										&& !StringUtils.trimToEmpty(customerGSTDetails.getRecordType())
												.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
									customerGSTDetails.setRecordType("");
									customerGSTDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
								}
								customerGstDetailDAO.update(customerGSTDetails, tableType);
							}

							// Audit
							fields = PennantJavaUtil.getFieldDetails(customerGSTDetails,
									customerGSTDetails.getExcludeFields());
							auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0],
									fields[1], customerGSTDetails.getBefImage(), customerGSTDetails));
						}
					}
				}
				fields = PennantJavaUtil.getFieldDetails(customerGST, customerGST.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						customerGST.getBefImage(), customerGST));
			}
		}

		if (customerDetails.getCustomerChequeInfoList() != null) {
			for (CustomerChequeInfo custChequeInfo : customerDetails.getCustomerChequeInfoList()) {
				if (StringUtils.isBlank(custChequeInfo.getRecordType())) {
					continue;
				}
				custChequeInfo.setWorkflowId(0);
				custChequeInfo.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(custChequeInfo.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					custChequeInfo.setRecordType("");
					custChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custChequeInfo.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerChequeInfoDAO.delete(custChequeInfo, tableType);
				} else if (custChequeInfo.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerChequeInfoDAO.save(custChequeInfo, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerChequeInfoDAO.update(custChequeInfo, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(custChequeInfo, custChequeInfo.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custChequeInfo.getBefImage(), custChequeInfo));
			}
		}

		if (customerDetails.getCustomerExtLiabilityList() != null) {
			for (CustomerExtLiability liability : customerDetails.getCustomerExtLiabilityList()) {
				if (StringUtils.isBlank(liability.getRecordType())) {
					continue;
				}
				liability.setWorkflowId(0);
				liability.setCustId(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(liability.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					liability.setRecordType("");
					liability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(liability.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;

					externalLiabilityDAO.delete(liability.getId(), tableType);
				} else if (liability.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerExtLiabilityDAO.setLinkId(liability);
					externalLiabilityDAO.save(liability, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					externalLiabilityDAO.update(liability, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(liability, liability.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						liability.getBefImage(), liability));
				processingExtLiabilittySubDetailList(liability, tableType, liability.getId());
			}
		}

		if (customerDetails.getCustCardSales() != null) {
			for (CustCardSales custCardSales : customerDetails.getCustCardSales()) {
				if (StringUtils.isBlank(custCardSales.getRecordType())) {
					continue;
				}
				custCardSales.setWorkflowId(0);
				custCardSales.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType) && !StringUtils.trimToEmpty(custCardSales.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					custCardSales.setRecordType("");
					custCardSales.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custCardSales.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerCardSalesInfoDAO.delete(custCardSales, tableType);
				} else if (custCardSales.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerCardSalesInfoDAO.save(custCardSales, tableType);
					// CardSaleDetails
					if (custCardSales.getCustCardMonthSales().size() > 0) {
						for (CustCardSalesDetails custCardMonthSalesInfoDetail : custCardSales
								.getCustCardMonthSales()) {
							customerCardSalesInfoDAO.save(custCardMonthSalesInfoDetail, tableType);
							// Audit
							fields = PennantJavaUtil.getFieldDetails(custCardMonthSalesInfoDetail,
									custCardMonthSalesInfoDetail.getExcludeFields());
							auditDetails
									.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
											custCardMonthSalesInfoDetail.getBefImage(), custCardMonthSalesInfoDetail));
						}
					}
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerCardSalesInfoDAO.update(custCardSales, tableType);
					// CardSaleDetails
					if (custCardSales.getCustCardMonthSales().size() > 0) {
						for (CustCardSalesDetails custCardMnthSalesInfoDetail : custCardSales.getCustCardMonthSales()) {
							customerCardSalesInfoDAO.update(custCardMnthSalesInfoDetail, tableType);
							// Audit
							fields = PennantJavaUtil.getFieldDetails(custCardMnthSalesInfoDetail,
									custCardMnthSalesInfoDetail.getExcludeFields());
							auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0],
									fields[1], custCardMnthSalesInfoDetail.getBefImage(), custCardMnthSalesInfoDetail));
						}
					}
				}
				fields = PennantJavaUtil.getFieldDetails(custCardSales, custCardSales.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custCardSales.getBefImage(), custCardSales));
			}
		}

		// Extended Fields
		if (customerDetails.getExtendedFieldRender() != null) {

			boolean isSaveRecord = false;
			ExtendedFieldHeader extendedFieldHeader = customerDetails.getExtendedFieldHeader();
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			ExtendedFieldRender extendedFieldRender = customerDetails.getExtendedFieldRender();
			if (StringUtils.isEmpty(tableType)) {
				extendedFieldRender.setRoleCode("");
				extendedFieldRender.setNextRoleCode("");
				extendedFieldRender.setTaskId("");
				extendedFieldRender.setNextTaskId("");
			}

			// Table Name addition for Audit
			extendedFieldRender.setTableName(tableName.toString());
			extendedFieldRender.setWorkflowId(0);

			// Add Common Fields
			HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();

			Customer aCustomer = customerDetails.getCustomer();

			if ((aCustomer.isNewRecord() && extendedFieldRender.getReference() == null)) {
				isSaveRecord = true;
			} else if (!aCustomer.isNewRecord() && extendedFieldRender.getReference() == null) {
				isSaveRecord = true;
			}

			if (isSaveRecord) {
				extendedFieldRender.setReference(customerDetails.getCustomer().getCustCIF());
				mapValues.put("Reference", extendedFieldRender.getReference());
				mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
			}

			mapValues.put("Version", extendedFieldRender.getVersion());
			mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
			mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
			mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
			mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
			mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
			mapValues.put("TaskId", extendedFieldRender.getTaskId());
			mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
			mapValues.put("RecordType", extendedFieldRender.getRecordType());
			mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());

			// Audit Details Preparation
			HashMap<String, Object> auditMapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			auditMapValues.put("Reference", extendedFieldRender.getReference());
			auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
			auditMapValues.put("Version", extendedFieldRender.getVersion());
			auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
			auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
			auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
			auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
			auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
			auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
			auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
			auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
			auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			extendedFieldRender.setAuditMapValues(auditMapValues);

			if (isSaveRecord) {
				auditTranType = PennantConstants.TRAN_ADD;
				extendedFieldRender.setRecordType("");
				extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), tableType, tableName.toString());
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
				extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						extendedFieldRender.getMapValues(), tableType, tableName.toString());
			}
			if (StringUtils.isNotBlank(extendedFieldRender.getReference())) {
				String[] extFields = PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender);
				AuditDetail auditDetail = new AuditDetail(auditTranType, 1, extFields[0], extFields[1],
						extendedFieldRender.getBefImage(), extendedFieldRender);
				auditDetail.setExtended(true);
				auditDetails.add(auditDetail);
			}
		}
		String recordStatus = financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
		if (StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_APPROVED)
				|| StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_SAVED)
				|| StringUtils.containsIgnoreCase(recordStatus, PennantConstants.RCD_STATUS_SUBMITTED)) {
			for (CustomerBankInfo customerBankInfo : financeDetail.getCustomerDetails().getCustomerBankInfoList()) {
				if (customerBankInfo.isAddToBenficiary()) {
					addToCustomerBeneficiary(customerBankInfo, customerBankInfo.getCustID());
				}

			}
		}

		// TODO:remove comments for below lines of code when MDM interface is
		// ready for update customer service
		// update core customer
		/*
		 * if(!StringUtils.isBlank(customer.getCustCoreBank())) { processUpdateCustData(customerDetails); }
		 */

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(CustomerDetails customerDetails, long workflowId, String method,
			String usrLanguage) {
		return doValidation(customerDetails, workflowId, method, usrLanguage);
	}

	public List<AuditDetail> doValidation(CustomerDetails customerDetails, long workflowId, String method,
			String usrLanguage) {
		logger.debug(Literal.ENTERING);

		String auditTranType;
		if (customerDetails.getCustomer().isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		// Customer Validation
		if (customerDetails.getCustomer() != null) {
			Customer customer = customerDetails.getCustomer();
			String[] fields = PennantJavaUtil.getFieldDetails(customer, customer.getExcludeFields());
			AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1],
					customerDetails.getCustomer().getBefImage(), customerDetails);
			auditDetails.add(validation(auditDetail, usrLanguage, method));

			if (postValidationHook != null) {
				AuditHeader auditHeader = new AuditHeader(String.valueOf(customerDetails.getCustID()),
						String.valueOf(customerDetails.getCustID()), null, null, auditDetail,
						customerDetails.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
				List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);
				if (CollectionUtils.isNotEmpty(errorDetails)) {
					if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
						auditDetail.getErrorDetails().addAll(ErrorUtil.getErrorDetails(errorDetails, usrLanguage));
					} else {
						auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(errorDetails, usrLanguage));
					}
				}
			}
		}

		auditDetails.addAll(getAuditDetail(customerDetails, auditTranType, method, workflowId));

		// Employment Validation
		if (customerDetails.getEmploymentDetailsList() != null
				&& customerDetails.getEmploymentDetailsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
			details = getEmploymentDetailValidation().employmentDetailListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Ratings Validation
		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
			details = getRatingValidation().ratingListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Incomes Validation
		if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Income");
			details = getCustomerIncomeValidation().incomeListValidation(customerDetails, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Directors Validation
		if (customerDetails.getCustomerDirectorList() != null && customerDetails.getCustomerDirectorList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Director");
			details = getDirectorValidation().directorListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// PhoneNumber Validation
		if (customerDetails.getCustomerPhoneNumList() != null && customerDetails.getCustomerPhoneNumList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
			details = getPhoneNumberValidation().phoneNumberListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Address Validation
		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Address");
			details = getAddressValidation().addressListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Document Validation
		if (customerDetails.getCustomerDocumentsList() != null
				&& customerDetails.getCustomerDocumentsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Document");
			details = getDocumentValidation().documentListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Email Validation
		if (customerDetails.getCustomerEMailList() != null && customerDetails.getCustomerEMailList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
			details = getCustomerEMailValidation().emailListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Bank Information Validation
		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerBankInfo");
			details = getCustomerBankInfoValidation().bankInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Cheque Information Validation
		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerChequeInfo");
			details = getCustomerChequeInfoValidation().chequeInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Bank Information Validation
		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerExtLiability");
			details = getExternalLiabilityValidation().extLiabilityListValidation(details, 0, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Card Sales Information Validation
		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSales");
			//details = getCustomerBankInfoValidation().bankInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSalesDetails");
			details = processingCardSaleInfoDetailList(details, "", Long.MIN_VALUE);
			auditDetails.addAll(details);
		}

		// customer dedup validation
		if (customerDetails.getCustomerDedupList() != null && !customerDetails.getCustomerDedupList().isEmpty()) {
			for (CustomerDedup customerDedup : customerDetails.getCustomerDedupList()) {
				AuditDetail auditDetail = new AuditDetail();
				if (StringUtils.equals(customerDedup.getSourceSystem(),
						PennantConstants.CUSTOMER_DEDUP_SOURCE_SYSTEM_PENNANT)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99012", null)));
					auditDetails.add(auditDetail);
				}
			}

		}

		// Extended field details Validation
		if (customerDetails.getExtendedFieldRender() != null) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.validateExtendedDdetails(customerDetails.getExtendedFieldHeader(),
					details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getAuditDetail(CustomerDetails customerDetails, String auditTranType, String method,
			long workflowId) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		if (customerDetails.getEmploymentDetailsList() != null
				&& customerDetails.getEmploymentDetailsList().size() > 0) {
			auditDetailMap.put("Employment",
					setCustomerEmploymentDetailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Employment"));
		}

		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			auditDetailMap.put("Rating", setRatingAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Rating"));
		}

		if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
			auditDetailMap.put("Income", setIncomeAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Income"));
		}

		if (customerDetails.getCustomerDirectorList() != null && customerDetails.getCustomerDirectorList().size() > 0) {
			auditDetailMap.put("Director", setDirectorAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Director"));
		}

		if (customerDetails.getCustomerPhoneNumList() != null && customerDetails.getCustomerPhoneNumList().size() > 0) {
			auditDetailMap.put("PhoneNumber", setPhoneNumberAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PhoneNumber"));
		}

		if (customerDetails.getCustomerEMailList() != null && customerDetails.getCustomerEMailList().size() > 0) {
			auditDetailMap.put("EMail", setEMailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("EMail"));
		}

		if (customerDetails.getCustomerDocumentsList() != null
				&& customerDetails.getCustomerDocumentsList().size() > 0) {
			auditDetailMap.put("Document", setDocumentAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Document"));
		}

		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			auditDetailMap.put("Address", setAddressAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Address"));
		}

		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			auditDetailMap.put("CustomerBankInfo", setBankInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerBankInfo"));
		}

		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			auditDetailMap.put("CustomerChequeInfo",
					setChequeInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerChequeInfo"));
		}

		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			auditDetailMap.put("CustomerExtLiability",
					setExtLiabilityAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerExtLiability"));
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			auditDetailMap.put("CustCardSales",
					setCardSalesInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustCardSales"));
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			for (int i = 0; i < customerDetails.getCustCardSales().size(); i++) {
				auditDetailMap.put("CustCardSalesDetails", setCardMonthSalesInfoDetailAuditData(
						customerDetails.getCustCardSales().get(i), auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CustCardSalesDetails"));
			}
		}

		customerDetails.setAuditDetailMap(auditDetailMap);

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Validate customer details, This method can perform below list of validations.<br>
	 * - customer Personal Info.<br>
	 * - customer Employment details.<br>
	 * 
	 * @param customerDetails
	 * 
	 * @return WSReturnStatus
	 */
	@Override
	public AuditDetail doCustomerValidations(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = auditHeader.getAuditDetail();
		CustomerDetails customerDetails = (CustomerDetails) auditDetail.getModelData();

		// validate basic details
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			auditDetail.setErrorDetail(validateMasterCode("CustomerCategory", customerDetails.getCustCtgCode()));
		}
		if (StringUtils.isNotBlank(customerDetails.getCustDftBranch())) {
			auditDetail.setErrorDetail(validateMasterCode("Branch", customerDetails.getCustDftBranch()));
		}
		if (StringUtils.isNotBlank(customerDetails.getCustBaseCcy())) {
			auditDetail.setErrorDetail(validateMasterCode("Currency", customerDetails.getCustBaseCcy()));
		}
		auditDetail.setErrorDetail(validateMasterCode("SourceOfficer", customerDetails.getPrimaryRelationOfficer()));

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getCode())) {
					return auditDetail;
				}
			}
		}

		Customer customer = customerDetails.getCustomer();
		customer.setCustCtgCode(customerDetails.getCustCtgCode());
		customer.setCustDftBranch(customerDetails.getCustDftBranch());
		customer.setCustCoreBank(customerDetails.getCustCoreBank());
		customer.setCustBaseCcy(customerDetails.getCustBaseCcy());
		customer.setCustRO1(customerDetails.getPrimaryRelationOfficer());

		// validate customer basic(personal info) details
		auditDetail = validatePersonalInfo(auditDetail, customerDetails.getCustomer());

		//Validate Customer Salutation Code
		auditDetail = validateSalutationCode(customer, auditDetail);

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getCode())) {
					return auditDetail;
				}
			}
		}

		// validate customer details
		auditDetail = validateCustomerDetails(auditDetail, customerDetails);

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetail errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getCode())) {
					return auditDetail;
				}
			}
		}
		// validate ExtendedFieldDetails
		List<ErrorDetail> errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(
				customerDetails.getExtendedDetails(), ExtendedFieldConstants.MODULE_CUSTOMER,
				customerDetails.getCustCtgCode(), null);
		auditDetail.getErrorDetails().addAll(errorDetails);

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	/**
	 * Validate customer details object
	 * 
	 * @param auditDetail
	 * @param customerDetails
	 * @return AuditDetail
	 */
	private AuditDetail validateCustomerDetails(AuditDetail auditDetail, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		// customer Employment details
		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			List<CustomerEmploymentDetail> custEmpDetails = customerDetails.getEmploymentDetailsList();
			if (custEmpDetails != null) {
				for (CustomerEmploymentDetail empDetail : custEmpDetails) {
					auditDetail.setErrorDetail(validateMasterCode("EmployerDetail", empDetail.getCustEmpName()));
					auditDetail.setErrorDetail(validateMasterCode("EmploymentType", empDetail.getCustEmpType()));
					auditDetail.setErrorDetail(validateMasterCode("GeneralDesignation", empDetail.getCustEmpDesg()));
					if (StringUtils.isNotBlank(empDetail.getCustEmpDept())) {
						auditDetail.setErrorDetail(validateMasterCode("GeneralDepartment", empDetail.getCustEmpDept()));
					}
					auditDetail.setErrorDetail(
							validateDatesWithDefaults(empDetail.getCustEmpTo(), App.getLabel("EMP_TO_DATE")));

					if (empDetail.getCustEmpTo() != null) {
						if (empDetail.getCustEmpFrom().compareTo(empDetail.getCustEmpTo()) > 0) {
							ErrorDetail errorDetail = new ErrorDetail();
							String[] valueParm = new String[2];
							valueParm[0] = "employment startDate:"
									+ DateUtility.format(empDetail.getCustEmpFrom(), PennantConstants.XMLDateFormat);
							valueParm[1] = "employment endDate:"
									+ DateUtility.format(empDetail.getCustEmpTo(), PennantConstants.XMLDateFormat);
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
						}
					} else {
						empDetail.setCurrentEmployer(true);
					}

					auditDetail.setErrorDetail(
							validateDatesWithDefaults(empDetail.getCustEmpFrom(), App.getLabel("EMP_FROM_DATE")));

					if (empDetail.getCustEmpFrom() != null && customerDetails.getCustomer() != null) {
						if (empDetail.getCustEmpFrom().before(customerDetails.getCustomer().getCustDOB())) {
							ErrorDetail errorDetail = new ErrorDetail();
							String[] valueParm = new String[1];
							valueParm[0] = "employment startDate";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90334", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}

				}
			}
		} else {
			if (customerDetails.getEmploymentDetailsList() != null
					&& customerDetails.getEmploymentDetailsList().size() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "employment";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}

		}

		// customer Director Details

		if (!StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			List<DirectorDetail> customerDirectorList = customerDetails.getCustomerDirectorList();
			if (customerDirectorList != null) {
				for (DirectorDetail directorList : customerDirectorList) {
					if (genderDAO.isValidGenderCode(directorList.getCustGenderCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = directorList.getCustGenderCode();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
					}

					// salutation validation
					int salutationByCount = salutationDAO.getSalutationByCount(directorList.getCustSalutationCode(),
							directorList.getCustGenderCode());

					if (salutationByCount <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = directorList.getCustSalutationCode();
						valueParm[1] = directorList.getCustGenderCode();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
					}

					// Id type validation
					int docTypeCount = customerDocumentDAO.getDocTypeCount(directorList.getIdType());

					if (docTypeCount <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = directorList.getIdType();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
					}

					// Nationality
					NationalityCode nationalityCodeById = nationalityCodeDAO
							.getNationalityCodeById(directorList.getNationality(), "");
					if (nationalityCodeById == null) {
						String[] valueParm = new String[2];
						valueParm[0] = directorList.getCustAddrCountry();
						valueParm[1] = directorList.getCustAddrProvince();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
					}

					// country,state,city validation

					City city = cityDAO.getCityById(directorList.getCustAddrCountry(),
							directorList.getCustAddrProvince(), directorList.getCustAddrCity(), "");
					if (city == null) {
						String[] valueParm = new String[2];
						valueParm[0] = directorList.getCustAddrCountry();
						valueParm[1] = directorList.getCustAddrProvince();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
					}

				}
			}
		}
		// customer Address details
		List<CustomerAddres> custAddress = customerDetails.getAddressList();

		if (custAddress != null) {
			boolean isAddressPrority = false;
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustomerAddres adress : custAddress) {
				auditDetail.setErrorDetail(validateMasterCode("AddressType", adress.getCustAddrType()));
				if (adress.getPinCodeId() != null && adress.getPinCodeId() < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "PinCodeId";
					valueParm[1] = "0";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				} else {
					if (StringUtils.isNotBlank(adress.getCustAddrZIP()) && (adress.getPinCodeId() != null)) {
						auditDetail.setErrorDetail(validateMasterCode("PinCode", adress.getCustAddrZIP()));
						PinCode pincode = pinCodeDAO.getPinCodeById(adress.getPinCodeId(), "_AView");
						if (pincode != null) {
							if (pincode.getPinCode().equals(adress.getCustAddrZIP())) {
								pinCodeValidation(pincode, adress, auditDetail, errorDetail);
							} else {
								String[] valueParm = new String[2];
								valueParm[0] = "PinCode " + adress.getCustAddrZIP();
								valueParm[1] = "PinCodeId " + String.valueOf(adress.getPinCodeId());
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("99017", "", valueParm));
								auditDetail.setErrorDetail(errorDetail);
							}
						} else {
							String[] valueParm = new String[1];
							valueParm[0] = "PinCodeId " + String.valueOf(adress.getPinCodeId());
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
						}
					} else {
						if (StringUtils.isNotBlank(adress.getCustAddrZIP()) && (adress.getPinCodeId() == null)) {
							int pinCodeCount = pinCodeDAO.getPinCodeCount(adress.getCustAddrZIP(), "_AView");
							String[] valueParm = new String[1];
							switch (pinCodeCount) {
							case 0:
								valueParm[0] = "PinCode " + adress.getCustAddrZIP();
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm));
								auditDetail.setErrorDetail(errorDetail);
								break;
							case 1:
								PinCode pincode = pinCodeDAO.getPinCode(adress.getCustAddrZIP(), "_AView");
								adress.setPinCodeId(pincode.getPinCodeId());
								pinCodeValidation(pincode, adress, auditDetail, errorDetail);
								break;
							default:
								valueParm[0] = "PinCodeId";
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm));
								auditDetail.setErrorDetail(errorDetail);
							}
						} else if (adress.getPinCodeId() != null && StringUtils.isBlank(adress.getCustAddrZIP())) {
							PinCode pincode = pinCodeDAO.getPinCodeById(adress.getPinCodeId(), "_AView");
							if (pincode != null) {
								adress.setCustAddrZIP(pincode.getPinCode());
								pinCodeValidation(pincode, adress, auditDetail, errorDetail);
								;
							} else {
								String[] valueParm = new String[1];
								valueParm[0] = "PinCodeId " + String.valueOf(adress.getPinCodeId());
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm));
								auditDetail.setErrorDetail(errorDetail);
							}
						}
					}
				}
				if (StringUtils.isNotBlank(adress.getCustAddrZIP())) {
					if (adress.getCustAddrZIP().length() < 3 || adress.getCustAddrZIP().length() > 6) {
						String[] valueParm = new String[3];
						valueParm[0] = "pinCode";
						valueParm[1] = "2 digits";
						valueParm[2] = "7 digits";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
					}
				}
				if (!(adress.getCustAddrPriority() >= 1 && adress.getCustAddrPriority() <= 5)) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(adress.getCustAddrPriority());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90114", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
				if (adress.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					isAddressPrority = true;
				}
				int addressPriorityCount = 0;
				int addType = 0;
				for (CustomerAddres aAdress : custAddress) {
					if (aAdress.getCustAddrPriority() == adress.getCustAddrPriority()) {
						addressPriorityCount++;
						if (addressPriorityCount > 1 && aAdress.getCustAddrPriority() == Integer
								.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							String[] valueParm = new String[2];
							valueParm[0] = "Priority";
							valueParm[1] = String.valueOf(adress.getCustAddrPriority());
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30702", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					if (StringUtils.equals(aAdress.getCustAddrType(), adress.getCustAddrType())) {
						addType++;
						if (addType > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "AddressType";
							valueParm[1] = aAdress.getCustAddrType();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}

			}
			if (!isAddressPrority) {
				String[] valueParm = new String[2];
				valueParm[0] = "Address Details";
				valueParm[1] = "Address";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		} else {
			ErrorDetail errorDetail = new ErrorDetail();
			String[] valueParm = new String[2];
			valueParm[0] = "Address Details";
			valueParm[1] = "Address";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		// customer Phone details
		List<CustomerPhoneNumber> custPhones = customerDetails.getCustomerPhoneNumList();
		if (custPhones != null) {
			boolean isPhonePrority = false;
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustomerPhoneNumber custPhoneDetail : custPhones) {
				// Validate Phone number
				String mobileNumber = custPhoneDetail.getPhoneNumber();
				PhoneType custPhoneType = phoneTypeDAO.getPhoneTypeById(custPhoneDetail.getPhoneTypeCode(), "");
				if (custPhoneType != null) {
					String regex = custPhoneType.getPhoneTypeRegex();
					if (regex != null) {
						if (!(mobileNumber.matches(regex))) {
							String[] valueParm = new String[1];
							valueParm[0] = regex;
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90346", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					} else {
						if (mobileNumber.length() > 20) {
							String[] valueParm = new String[2];
							valueParm[0] = "PhoneNumber lenght";
							valueParm[1] = "20";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90220", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}

				}
				auditDetail.setErrorDetail(validateMasterCode("PhoneType", custPhoneDetail.getPhoneTypeCode()));
				if (!(custPhoneDetail.getPhoneTypePriority() >= 1 && custPhoneDetail.getPhoneTypePriority() <= 5)) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(custPhoneDetail.getPhoneTypePriority());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90115", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
				if (custPhoneDetail.getPhoneTypePriority() == Integer
						.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					isPhonePrority = true;
				}
				int phonePriorityCount = 0;
				int phoneType = 0;
				for (CustomerPhoneNumber aPhones : custPhones) {
					if (aPhones.getPhoneTypePriority() == custPhoneDetail.getPhoneTypePriority()) {
						phonePriorityCount++;
						if (phonePriorityCount > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "Priority";
							valueParm[1] = String.valueOf(aPhones.getPhoneTypePriority());
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90287", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					if (StringUtils.equals(aPhones.getPhoneTypeCode(), custPhoneDetail.getPhoneTypeCode())) {
						phoneType++;
						if (phoneType > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "PhoneType";
							valueParm[1] = aPhones.getPhoneTypeCode();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
			}
			if (!isPhonePrority) {
				String[] valueParm = new String[2];
				valueParm[0] = "Phone Details";
				valueParm[1] = "Phone";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		} else {
			ErrorDetail errorDetail = new ErrorDetail();
			String[] valueParm = new String[2];
			valueParm[0] = "Phone Details";
			valueParm[1] = "Phone";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		// customer Email details
		List<CustomerEMail> custEmails = customerDetails.getCustomerEMailList();
		if (custEmails != null) {
			boolean isEmailPrority = false;
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustomerEMail custEmail : custEmails) {
				auditDetail.setErrorDetail(validateMasterCode("EMailType", custEmail.getCustEMailTypeCode()));
				if (!(custEmail.getCustEMailPriority() >= 1 && custEmail.getCustEMailPriority() <= 5)) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(custEmail.getCustEMailPriority());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90110", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
				boolean validRegex = EmailValidator.getInstance().isValid(custEmail.getCustEMail());

				if (!validRegex) {
					String[] valueParm = new String[1];
					valueParm[0] = custEmail.getCustEMail();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90237", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
				if (custEmail.getCustEMailPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					isEmailPrority = true;
				}
				int emailPriorityCount = 0;
				int emailType = 0;
				for (CustomerEMail acustEmail : custEmails) {
					if (acustEmail.getCustEMailPriority() == custEmail.getCustEMailPriority()) {
						emailPriorityCount++;
						if (emailPriorityCount > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "Priority";
							valueParm[1] = String.valueOf(acustEmail.getCustEMailPriority());
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90288", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					if (StringUtils.equals(acustEmail.getCustEMailTypeCode(), custEmail.getCustEMailTypeCode())) {
						emailType++;
						if (emailType > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "EmailType";
							valueParm[1] = acustEmail.getCustEMailTypeCode();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
			}
			if (!isEmailPrority) {
				String[] valueParm = new String[2];
				valueParm[0] = "Email Details";
				valueParm[1] = "Email";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		}

		// customer income details
		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {

			List<CustomerIncome> custIncomes = customerDetails.getCustomerIncomeList();
			if (custIncomes != null) {
				for (CustomerIncome custIncome : custIncomes) {
					auditDetail.setErrorDetail(
							validateMasterCode("BMTIncomeTypes", "IncomeExpense", custIncome.getIncomeExpense()));
					auditDetail
							.setErrorDetail(validateMasterCode("BMTIncomeTypes", "Category", custIncome.getCategory()));
					auditDetail.setErrorDetail(validateMasterCode("IncomeType", custIncome.getIncomeType()));
					IncomeType incomeType = incomeTypeDAO.getIncomeTypeById(custIncome.getIncomeType(),
							custIncome.getIncomeExpense(), custIncome.getCategory(), "_AView");
					if (incomeType == null) {
						ErrorDetail errorDetail = new ErrorDetail();
						String[] valueParm = new String[2];
						valueParm[0] = custIncome.getCustCif();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90113", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					if (incomeType != null && (custIncome.getMargin() == null
							|| custIncome.getMargin().compareTo(BigDecimal.ZERO) == 0)) {
						if (incomeType.getMargin() == null) {
							custIncome.setMargin(BigDecimal.ZERO);
						} else {
							custIncome.setMargin(incomeType.getMargin());
						}
					}
					if (incomeType.getMargin() != null && custIncome.getMargin().compareTo(new BigDecimal(10000)) > 0) {
						ErrorDetail errorDetail = new ErrorDetail();
						String[] valueParm = new String[2];
						valueParm[0] = "margin";
						valueParm[1] = "10000";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90220", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
					}
				}
			}

			//Customer Residential Status validation
			Customer customer = customerDetails.getCustomer();
			if (customer != null) {
				String custResidentialSts = customer.getCustResidentialSts();
				if (StringUtils.isNotBlank(custResidentialSts)) {
					final List<ValueLabel> residentialList = PennantStaticListUtil.getResidentialStsList();
					boolean isSource = false;
					for (ValueLabel source : residentialList) {
						if (StringUtils.equals(custResidentialSts, source.getValue())) {
							isSource = true;
							break;
						}
					}
					if (!isSource) {
						ErrorDetail errorDetail = new ErrorDetail();
						String[] valueParam = new String[2];
						valueParam[0] = "custResidentialSts";
						valueParam[1] = custResidentialSts;
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}
				}

			}
		} else {
			if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "customerIncome";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
				return auditDetail;
			}

		}
		List<CustomerDocument> custDocuments = customerDetails.getCustomerDocumentsList();
		boolean panMandatory = false;
		if (custDocuments != null) {
			for (CustomerDocument custDocument : custDocuments) {
				if (StringUtils.equals(custDocument.getCustDocCategory(), "03")) {
					panMandatory = true;
				}
				AuditDetail auditDetail1 = customerDocumentService.validateCustomerDocuments(custDocument,
						customerDetails.getCustomer());
				if (auditDetail1 != null && auditDetail1.getErrorDetails() != null
						&& !auditDetail1.getErrorDetails().isEmpty()) {
					return auditDetail1;
				}
			}
		}
		/*
		 * if (StringUtils.isBlank(customerDetails.getCustCIF()) && !panMandatory) { String[] valueParm = new String[1];
		 * valueParm[0] = "PAN document"; ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502",
		 * "", valueParm)); auditDetail.setErrorDetail(errorDetail); return auditDetail; }
		 */

		// customer bank info details
		List<CustomerBankInfo> custBankDetails = customerDetails.getCustomerBankInfoList();
		if (custBankDetails != null) {
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustomerBankInfo custBankInfo : custBankDetails) {
				int count = 0;
				auditDetail.setErrorDetail(validateMasterCode("BankDetail", custBankInfo.getBankName()));

				LovFieldDetail lovFieldDetail = getLovFieldDetailService().getApprovedLovFieldDetailById("ACC_TYPE",
						custBankInfo.getAccountType());
				if (lovFieldDetail == null) {

					String[] valueParm = new String[2];
					valueParm[0] = "Acctype";
					valueParm[1] = custBankInfo.getAccountType();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
				// validate AccNumber length
				if (StringUtils.isNotBlank(custBankInfo.getBankName())
						&& StringUtils.isNotBlank(custBankInfo.getAccountNumber())) {
					BankDetail bankDetail = bankDetailDAO.getAccNoLengthByCode(custBankInfo.getBankName(), "");
					if (bankDetail != null) {
						int maxAccNoLength = bankDetail.getAccNoLength();
						int minAccNoLength = bankDetail.getMinAccNoLength();
						if (custBankInfo.getAccountNumber().length() < minAccNoLength
								|| custBankInfo.getAccountNumber().length() > maxAccNoLength) {
							String[] valueParm = new String[3];
							valueParm[0] = "AccountNumber";
							valueParm[1] = String.valueOf(minAccNoLength) + " characters";
							valueParm[2] = String.valueOf(maxAccNoLength) + " characters";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("BNK001", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
				for (CustomerBankInfo aCustBankDetails : customerDetails.getCustomerBankInfoList()) {
					if (custBankInfo.getAccountNumber().equals(aCustBankDetails.getAccountNumber())
							&& custBankInfo.getBankName().equals(aCustBankDetails.getBankName())) {
						count++;
					}
					if (count > 1) {
						String[] valueParm = new String[2];
						valueParm[0] = "accountNumber " + custBankInfo.getAccountNumber();
						valueParm[1] = "bankName " + custBankInfo.getBankName();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
				// Added below code for validating external documents
				auditDetail = validateExternalDocuments(custBankInfo, auditDetail);
				auditDetail = validateBankInfoDetail(custBankInfo, auditDetail);
			}
		}

		// customer bank info details
		List<CustomerGST> custGstDetails = customerDetails.getCustomerGstList();
		if (custGstDetails != null) {
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustomerGST customerGST : custGstDetails) {
				// auditDetail.setErrorDetail(validateMasterCode("CustomerGST",
				// customerGST.getGstNumber()));

				/*
				 * LovFieldDetail lovFieldDetail = getLovFieldDetailService().getApprovedLovFieldDetailById( "ACC_TYPE",
				 * customerGST.getFrequencytype());
				 */
				/*
				 * if (lovFieldDetail == null) {
				 * 
				 * String[] valueParm = new String[2]; valueParm[0] = "Acctype"; valueParm[1] =
				 * custBankInfo.getAccountType(); errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "",
				 * valueParm)); auditDetail.setErrorDetail(errorDetail); }
				 */ // validate AccNumber length
				if (StringUtils.isNotBlank(customerGST.getGstNumber())) {
					BankDetail bankDetail = bankDetailDAO.getAccNoLengthByCode(customerGST.getGstNumber(), "");
					int gstNoLength = bankDetail.getAccNoLength();
					int minAccNoLength = bankDetail.getMinAccNoLength();
					if (bankDetail != null) {
						if (customerGST.getGstNumber().length() < minAccNoLength
								|| customerGST.getGstNumber().length() > gstNoLength) {
							String[] valueParm = new String[2];
							valueParm[0] = "GSTNumber";
							valueParm[1] = String.valueOf(gstNoLength) + " characters";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30570", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
				auditDetail = validateGstInfoDetail(customerGST, auditDetail);
			}
		}

		List<CustomerExtLiability> libailities = customerDetails.getCustomerExtLiabilityList();
		if (libailities != null) {
			for (CustomerExtLiability liability : libailities) {
				if (liability.getFinDate().compareTo(DateUtility.getAppDate()) >= 0
						|| SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(liability.getFinDate()) >= 0) {
					ErrorDetail errorDetail = new ErrorDetail();
					String[] valueParm = new String[3];
					valueParm[0] = "FinDate";
					valueParm[1] = DateUtility.format(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
							PennantConstants.XMLDateFormat);
					valueParm[2] = DateUtility.format(SysParamUtil.getAppDate(), PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}

				if (StringUtils.isNotBlank(String.valueOf(liability.getSource()))) {
					final List<ValueLabel> sourceInfoList = PennantStaticListUtil.getSourceInfoList();
					boolean isSource = false;
					for (ValueLabel source : sourceInfoList) {
						if (liability.getSource() == Integer.valueOf(source.getValue())) {
							isSource = true;
							break;
						}
					}
					if (!isSource) {
						ErrorDetail errorDetail = new ErrorDetail();
						String[] valueParam = new String[2];
						valueParam[0] = "source";
						valueParam[1] = String.valueOf(liability.getSource());
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}
				}

				if (StringUtils.isNotBlank(String.valueOf(liability.getCheckedBy()))) {
					final List<ValueLabel> trackCheckList = PennantStaticListUtil.getTrackCheckList();
					boolean isCheckedBy = false;
					for (ValueLabel checkedBy : trackCheckList) {
						if (liability.getCheckedBy() == Integer.valueOf(checkedBy.getValue())) {
							isCheckedBy = true;
							break;
						}
					}
					if (!isCheckedBy) {
						ErrorDetail errorDetail = new ErrorDetail();
						String[] valueParam = new String[2];
						valueParam[0] = "checkedBy";
						valueParam[1] = String.valueOf(liability.getCheckedBy());
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}
				}

				if (StringUtils.isNotBlank(liability.getLoanPurpose())) {
					auditDetail.setErrorDetail(
							validateMasterCode("LoanPurposes", "LoanPurposeCode", liability.getLoanPurpose()));
				}

				if (StringUtils.isNotBlank(liability.getRepayBank())) {
					auditDetail
							.setErrorDetail(validateMasterCode("BMTBankDetail", "BankCode", liability.getRepayBank()));
				}

				auditDetail.setErrorDetail(validateMasterCode("BankDetail", liability.getLoanBank()));
				auditDetail.setErrorDetail(validateMasterCode("OtherBankFinanceType", liability.getFinType()));
				auditDetail.setErrorDetail(validateMasterCode("CustomerStatusCode", liability.getFinStatus()));
				if (CollectionUtils.isNotEmpty(liability.getExtLiabilitiesPayments())) {
					auditDetail.setErrorDetail(validateExtLiabilitiesPayments(liability));
				}
			}
		}

		List<CustCardSales> custCardSalesDetails = customerDetails.getCustCardSales();
		if (custCardSalesDetails != null) {
			// duplicate merchant id validation
			if (customerDetails.getCustCardSales().size() > 0) {
				List<CustCardSales> cardSaleDetailsList = customerCardSalesInfoDAO
						.getCardSalesInfoByCustomer(customerDetails.getCustID(), "_View");
				for (int i = 0; i < customerDetails.getCustCardSales().size(); i++) {
					auditDetail.setErrorDetail(validateCardSalesListData(customerDetails.getCustCardSales(),
							customerDetails.getCustCardSales().get(i)));
					auditDetail.setErrorDetail(
							validateCardSalesData(cardSaleDetailsList, customerDetails.getCustCardSales().get(i)));
				}
			}
			for (CustCardSales custCArdSaleInfo : custCardSalesDetails) {
				auditDetail = validateCardMnthInfoDetail(custCArdSaleInfo, auditDetail);
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private void pinCodeValidation(PinCode pincode, CustomerAddres adress, AuditDetail auditDetail,
			ErrorDetail errorDetail) {
		if (pincode != null) {
			if (StringUtils.isNotBlank(adress.getCustAddrCountry())
					&& !adress.getCustAddrCountry().equalsIgnoreCase(pincode.getpCCountry())) {

				String[] valueParm = new String[2];
				valueParm[0] = adress.getCustAddrCountry();
				valueParm[1] = adress.getCustAddrZIP();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
			} else {
				adress.setCustAddrCountry(pincode.getpCCountry());
			}

			Province province = getProvinceDAO().getProvinceById(adress.getCustAddrCountry(), pincode.getpCProvince(),
					"");
			if (province != null && StringUtils.isNotBlank(adress.getCustAddrProvince())
					&& !adress.getCustAddrProvince().equalsIgnoreCase(province.getCPProvince())) {

				String[] valueParm = new String[2];
				valueParm[0] = adress.getCustAddrProvince();
				valueParm[1] = adress.getCustAddrZIP();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
			} else {
				adress.setCustAddrProvince(pincode.getpCProvince());
			}

			if (StringUtils.isNotBlank(adress.getCustAddrCity())
					&& !adress.getCustAddrCity().equalsIgnoreCase(pincode.getCity())) {

				String[] valueParm = new String[2];
				valueParm[0] = adress.getCustAddrCity();
				valueParm[1] = adress.getCustAddrZIP();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);

			} else {
				adress.setCustAddrCity(pincode.getCity());
			}

		}

	}

	private ErrorDetail validateCardSalesListData(List<CustCardSales> cardSaleDetailsList,
			CustCardSales aCustCardSales) {
		logger.debug(Literal.ENTERING);
		ErrorDetail errorDetail = new ErrorDetail();
		int count = 0;
		if (cardSaleDetailsList.size() > 0) {
			for (int j = 0; j < cardSaleDetailsList.size(); j++) {
				if (aCustCardSales.getMerchantId().equals(cardSaleDetailsList.get(j).getMerchantId())) {
					count++;
					String[] valueParm = new String[2];
					valueParm[0] = "Merchant Id";
					valueParm[1] = "Cust Id";
					if (count > 1) {
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30570", "", valueParm));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorDetail;
	}

	private ErrorDetail validateCardSalesData(List<CustCardSales> cardSaleDetailsList, CustCardSales aCustCardSales) {
		logger.debug(Literal.ENTERING);
		ErrorDetail errorDetail = new ErrorDetail();
		if (cardSaleDetailsList.size() > 0) {
			for (int j = 0; j < cardSaleDetailsList.size(); j++) {
				if (aCustCardSales.getMerchantId().equals(cardSaleDetailsList.get(j).getMerchantId())) {
					String[] valueParm = new String[2];
					valueParm[0] = "Merchant Id";
					valueParm[1] = "Cust Id";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30570", "", valueParm));
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorDetail;
	}

	private ErrorDetail validateExtLiabilitiesPayments(CustomerExtLiability liability) {
		logger.debug(Literal.ENTERING);
		// List grater than tenure
		ErrorDetail errorDetail = new ErrorDetail();
		if (liability.getExtLiabilitiesPayments().size() > liability.getTenure()) {
			String[] valueParm = new String[2];
			valueParm[0] = "No of instalment Details ";
			valueParm[1] = "Tenure";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90220", "", valueParm));
			return errorDetail;
		}
		// EMIType invalidate validation
		String date = DateUtility.format(liability.getFinDate(), PennantConstants.DBDateFormat);
		List<ExtLiabilityPaymentdetails> paymentDetails = getPaymentDetails(DateUtility.getDBDate(date),
				liability.getTenure());
		if (CollectionUtils.isNotEmpty(paymentDetails)) {
			for (int i = 0; i < liability.getExtLiabilitiesPayments().size(); i++) {
				int emiCount = 0;
				for (int j = 0; j < paymentDetails.size(); j++) {
					if (liability.getExtLiabilitiesPayments().get(i).getEmiType()
							.equals((paymentDetails.get(j).getEmiType()))) {
						emiCount++;
					}
				}
				if (emiCount == 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Emi Type";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91123", "", valueParm));
					return errorDetail;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorDetail;
	}

	public List<ExtLiabilityPaymentdetails> getPaymentDetails(Date startDate, int noOfMonths) {
		Date dtStartDate = DateUtility.addMonths(startDate, 1);
		Date dtEndDate = DateUtility.addMonths(dtStartDate, noOfMonths);
		List<ExtLiabilityPaymentdetails> months = getFrequency(dtStartDate, dtEndDate, noOfMonths);
		return months;
	}

	private List<ExtLiabilityPaymentdetails> getFrequency(final Date startDate, final Date endDate, int noOfMonths) {
		List<ExtLiabilityPaymentdetails> list = new ArrayList<>();
		if (startDate == null || endDate == null) {
			return list;
		}

		Date tempStartDate = (Date) startDate.clone();
		Date tempEndDate = (Date) endDate.clone();

		while (DateUtility.compare(tempStartDate, tempEndDate) < 0) {
			ExtLiabilityPaymentdetails temp = new ExtLiabilityPaymentdetails();
			String key = DateUtil.format(tempStartDate, DateFormat.LONG_MONTH);
			temp.setEmiType(key);
			tempStartDate = DateUtil.addMonths(tempStartDate, 1);
			list.add(temp);
		}

		return list;
	}

	private AuditDetail validateBankInfoDetail(CustomerBankInfo custBankInfo, AuditDetail auditDetail) {
		ArrayList<Date> dateList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(custBankInfo.getBankInfoDetails())) {
			ErrorDetail errorDetail = new ErrorDetail();
			for (BankInfoDetail detail : custBankInfo.getBankInfoDetails()) {
				List<String> daysList = new ArrayList<>();
				List<String> daysInputlis = new ArrayList<>();
				if (dateList.contains(detail.getMonthYear())) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:MonthYear " + detail.getMonthYear();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
				if (detail.getMonthYear() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:MonthYear";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				} else {
					dateList.add(detail.getMonthYear());
				}
				if (detail.getDebitAmt() == null || detail.getDebitAmt().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:DebitAmt";
					valueParm[1] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}

				if (detail.getDebitNo() < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:DebitNo";
					valueParm[0] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (detail.getCreditAmt() == null || detail.getCreditAmt().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:CreditAmt";
					valueParm[1] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (detail.getCreditNo() < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:CreditNo";
					valueParm[0] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (detail.getBounceIn() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:BounceIn";
					valueParm[1] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (detail.getBounceOut() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:BounceOut";
					valueParm[1] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
					String configDay = SysParamUtil.getValueAsString(SMTParameterConstants.BANKINFO_DAYS);
					String[] days = configDay.split(PennantConstants.DELIMITER_COMMA);
					for (String type : days) {
						daysList.add(type);
					}
					if (detail.getBankInfoSubDetails().size() != daysList.size()) {
						String[] valueParm = new String[2];
						valueParm[0] = "BankInfoSubDetails";
						valueParm[1] = SysParamUtil.getValueAsString("BANKINFO_DAYS");
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30540", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;

					}
				}
				for (BankInfoSubDetail subDetail : detail.getBankInfoSubDetails()) {
					if (subDetail.getMonthYear() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "BankInfoSubDetails :monthYear";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					} else {
						if (DateUtility.compare(subDetail.getMonthYear(), detail.getMonthYear()) != 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "bankInfoDetails:MonthYear";
							valueParm[1] = "bankInfoSubDetails:MonthYear";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90277", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}
					if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
						if (subDetail.getDay() <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "BankInfoSubDetails:Day";
							valueParm[0] = "Zero";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						} else {
							daysInputlis.add(String.valueOf(subDetail.getDay()));
						}
					}
					if (subDetail.getBalance() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "BankInfoSubDetails:Balance";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("WFEE08", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					} else if (!(StringUtils.equalsIgnoreCase(custBankInfo.getAccountType(), "CC")
							|| StringUtils.equalsIgnoreCase(custBankInfo.getAccountType(), "OD"))
							&& subDetail.getBalance().compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "BankInfoSubDetails:Balance";
						valueParm[1] = "Zero";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
				if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
					for (String day : daysInputlis) {
						boolean flag = true;
						for (String detai : daysList) {
							if (StringUtils.equals(day, detai)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							String[] valueParm = new String[2];
							valueParm[0] = "day";
							valueParm[1] = SysParamUtil.getValueAsString("BANKINFO_DAYS");
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30540", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
			}

		}
		return auditDetail;
	}

	private AuditDetail validateCardMnthInfoDetail(CustCardSales custCardSalesInfo, AuditDetail auditDetail) {

		if (CollectionUtils.isNotEmpty(custCardSalesInfo.getCustCardMonthSales())) {
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustCardSalesDetails detail : custCardSalesInfo.getCustCardMonthSales()) {
				if (detail.getMonth() != null && !detail.getMonth().equals("")) {
					if (detail.getMonth().after(SysParamUtil.getAppDate())) {
						String[] valueParm = new String[2];
						valueParm[0] = "custCardSalesDetails:Month";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30527", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
				if (detail.getSalesAmount() == null || detail.getSalesAmount().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "custCardSalesDetails:SalesAmount";
					valueParm[1] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				/*
				 * if (detail.getNoOfSettlements() <= 0) { String[] valueParm = new String[2]; valueParm[0] =
				 * "custCardSalesDetails:NoOfSettlements"; valueParm[0] = "Zero"; errorDetail =
				 * ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
				 * auditDetail.setErrorDetail(errorDetail); return auditDetail; } if (detail.getTotalNoOfCredits() <= 0)
				 * { String[] valueParm = new String[2]; valueParm[0] = "custCardSalesDetails:TotalNoOfCredits";
				 * valueParm[0] = "Zero"; errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "",
				 * valueParm)); auditDetail.setErrorDetail(errorDetail); return auditDetail; } if
				 * (detail.getTotalCreditValue()== null || detail.getTotalCreditValue().compareTo(BigDecimal.ZERO) <= 0)
				 * { String[] valueParm = new String[2]; valueParm[0] = "custCardSalesDetails:TotalCreditValue";
				 * valueParm[1] = "Zero"; errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "",
				 * valueParm)); auditDetail.setErrorDetail(errorDetail); return auditDetail; } if
				 * (detail.getTotalNoOfDebits() <= 0) { String[] valueParm = new String[2]; valueParm[0] =
				 * "custCardSalesDetails:TotalNoOfDebits"; valueParm[0] = "Zero"; errorDetail =
				 * ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
				 * auditDetail.setErrorDetail(errorDetail); return auditDetail; } if (detail.getTotalDebitValue()== null
				 * || detail.getTotalDebitValue().compareTo(BigDecimal.ZERO) <= 0) { String[] valueParm = new String[2];
				 * valueParm[0] = "custCardSalesDetails:TotalDebitValue"; valueParm[1] = "Zero"; errorDetail =
				 * ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
				 * auditDetail.setErrorDetail(errorDetail); return auditDetail; } if (detail.getInwardBounce()==null ||
				 * detail.getInwardBounce().compareTo(BigDecimal.ZERO) <= 0) { String[] valueParm = new String[2];
				 * valueParm[0] = "custCardSalesDetails:InwardBounce"; valueParm[1] = "Zero"; errorDetail =
				 * ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
				 * auditDetail.setErrorDetail(errorDetail); return auditDetail; } if (detail.getOutwardBounce()== null
				 * ||detail.getOutwardBounce().compareTo(BigDecimal.ZERO) <= 0) { String[] valueParm = new String[2];
				 * valueParm[0] = "custCardSalesDetails:OutwardBounce"; valueParm[1] = "Zero"; errorDetail =
				 * ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
				 * auditDetail.setErrorDetail(errorDetail); return auditDetail; }
				 */
			}
		}
		return auditDetail;
	}

	private AuditDetail validateGstInfoDetail(CustomerGST customerGSTInfo, AuditDetail auditDetail) {

		if (CollectionUtils.isNotEmpty(customerGSTInfo.getCustomerGSTDetailslist())) {
			ErrorDetail errorDetail = new ErrorDetail();
			for (CustomerGSTDetails detail : customerGSTInfo.getCustomerGSTDetailslist()) {
				if (detail.getFrequancy() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "gstInfoDetails:Frequency";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (detail.getHeaderId() == Long.MIN_VALUE) {
					String[] valueParm = new String[2];
					valueParm[0] = "gstInfoDetails:GSTNumber ";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				if (detail.getSalAmount() == null || detail.getSalAmount().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "bankInfoDetails:GstAmt";
					valueParm[1] = "Zero";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}

			}
		}
		return auditDetail;
	}

	private ErrorDetail validateDatesWithDefaults(Date date, String label) {
		ErrorDetail errorDetail = new ErrorDetail();
		if (date != null) {
			Date defaultAppDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
			if (date.compareTo(DateUtility.getAppDate()) != -1 || defaultAppDate.compareTo(date) >= 0) {
				String[] valueParm = new String[3];
				valueParm[0] = label;
				valueParm[1] = DateUtility.format(defaultAppDate, PennantConstants.XMLDateFormat);
				valueParm[2] = DateUtility.format(DateUtility.getAppDate(), PennantConstants.XMLDateFormat);
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm));
			}
		}
		return errorDetail;
	}

	private AuditDetail validatePersonalInfo(AuditDetail auditDetail, Customer customer) {
		logger.debug(Literal.ENTERING);

		// validate conditional mandatory fields
		String custCtgCode = customer.getCustCtgCode();
		if (StringUtils.equals(custCtgCode, PennantConstants.PFF_CUSTCTG_INDIV)) {
			if (StringUtils.isBlank(customer.getCustFName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "firstName";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
			}
			// For Auxilo Customer Last name field non-mandatory
			if (SysParamUtil.isAllowed(SMTParameterConstants.CUST_LASTNAME_MANDATORY)) {
				if (StringUtils.isBlank(customer.getCustLName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "lastName";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				}
			}
			if (StringUtils.isBlank(customer.getCustSalutationCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "salutation";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				return auditDetail;
			}
			if (StringUtils.isBlank(customer.getCustGenderCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "gender";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				return auditDetail;
			}
			if (StringUtils.isBlank(customer.getCustMaritalSts())) {
				String[] valueParm = new String[1];
				valueParm[0] = "maritalStatus";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				return auditDetail;
			}

			if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				if (StringUtils.isBlank(customer.getCustResidentialSts())) {
					String[] valueParm = new String[1];
					valueParm[0] = "residentialSts";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
					return auditDetail;
				} else {
					boolean isResExists = false;
					List<ValueLabel> fieldType = PennantStaticListUtil.getResidentialStsList();

					for (ValueLabel fe : fieldType) {
						if ((customer.getCustResidentialSts().equals(fe.getValue()))) {
							isResExists = true;
						}
					}
					if (!isResExists) {
						String[] valueParm = new String[1];
						valueParm[0] = "residentialSts";
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm)));
						return auditDetail;
					}
				}
			}
			auditDetail.setErrorDetail(validateMasterCode("Salutation", customer.getCustSalutationCode()));
			auditDetail.setErrorDetail(validateMasterCode("Gender", customer.getCustGenderCode()));
			auditDetail.setErrorDetail(validateMasterCode("MaritalStatusCode", customer.getCustMaritalSts()));
			if (StringUtils.isNotBlank(customer.getQualification())) {
				auditDetail.setErrorDetail(validateMasterCode("Qualification", customer.getQualification()));
			}
		}
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)
				|| StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)) {
			if (StringUtils.isBlank(customer.getCustShrtName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "shortName";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
			} else {
				Pattern pattern = null;
				if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP)
						|| StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)) {
					pattern = Pattern.compile(
							PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_CORP_CUST_NAME));
				} else {
					pattern = Pattern.compile(
							PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_RETAIL_CUST_NAME));
				}
				Matcher matcher = pattern.matcher(customer.getCustShrtName());
				if (!matcher.matches()) {
					String[] valueParm = new String[1];
					valueParm[0] = "shortName";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90237", "", valueParm), "EN"));
				}
			}
			if (StringUtils.isNotBlank(customer.getCustFName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "firstName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (StringUtils.isNotBlank(customer.getCustLName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "lastName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (StringUtils.isNotBlank(customer.getCustMName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "middleName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (StringUtils.isNotBlank(customer.getCustSalutationCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "salutation";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (StringUtils.isNotBlank(customer.getCustMotherMaiden())) {
				String[] valueParm = new String[2];
				valueParm[0] = "motherName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			/*
			 * if (StringUtils.isNotBlank(customer.getCustNationality())) { String[] valueParm = new String[2];
			 * valueParm[0] = "nationality"; valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
			 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm))); }
			 */
			if (StringUtils.isNotBlank(customer.getCustGenderCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "gender";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (StringUtils.isNotBlank(customer.getCustMaritalSts())) {
				String[] valueParm = new String[2];
				valueParm[0] = "maritalStatus";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (customer.getNoOfDependents() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "numofDependents ";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (customer.getCasteId() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Caste";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (customer.getReligionId() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Religion";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
			if (StringUtils.isNotBlank(customer.getSubCategory())) {
				String[] valueParm = new String[2];
				valueParm[0] = "SubCategory";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90124", "", valueParm)));
			}
		}
		auditDetail.setErrorDetail(validateMasterCode("CustomerType", customer.getCustTypeCode()));
		// validating the CoreBank
		if (StringUtils.isNotBlank(customer.getCustCoreBank())) {
			Customer cust = customerDAO.getCustomerByCoreBankId(customer.getCustCoreBank(), "");
			if (cust != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "CustCoreBankId: ";
				valueParm[1] = customer.getCustCoreBank();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", valueParm)));
				return auditDetail;
			}
		}

		// validate custTypeCode against the category code
		int custTypeCount = getCustomerTypeDAO().validateTypeAndCategory(customer.getCustTypeCode(),
				customer.getCustCtgCode());
		if (custTypeCount <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = customer.getCustTypeCode();
			valueParm[1] = customer.getCustCtgCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90108", "", valueParm)));
		}

		if (StringUtils.isNotBlank(customer.getCustSector())) {
			auditDetail.setErrorDetail(validateMasterCode("Sector", customer.getCustSector()));
		}

		if (StringUtils.isNotBlank(customer.getCustIndustry())) {
			auditDetail.setErrorDetail(validateMasterCode("Industry", customer.getCustIndustry()));
		}

		if (StringUtils.isNotBlank(customer.getCustLng())) {
			auditDetail.setErrorDetail(validateMasterCode("BMTLanguage", "LngCode", customer.getCustLng()));
		}

		if (StringUtils.isNotBlank(customer.getCustCOB())) {
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustCOB()));
		}

		if (StringUtils.isNotBlank(customer.getCustNationality())) {
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustNationality()));
		}

		if (StringUtils.isNotBlank(customer.getCustSubSector())) {
			auditDetail.setErrorDetail(validateMasterCode("SubSector", customer.getCustSubSector()));
		}

		if (StringUtils.isNotBlank(customer.getCustSegment())) {
			auditDetail.setErrorDetail(validateMasterCode("Segment", customer.getCustSegment()));
		}

		if (StringUtils.isNotBlank(customer.getCustSubSegment())) {
			auditDetail.setErrorDetail(validateMasterCode("SubSegment", customer.getCustSubSegment()));
		}

		if (StringUtils.isNotBlank(customer.getCustParentCountry())) {
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustParentCountry()));
		}

		if (StringUtils.isNotBlank(customer.getCustRiskCountry())) {
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustRiskCountry()));
		}

		if (StringUtils.isNotBlank(customer.getCustEmpSts())) {
			auditDetail.setErrorDetail(validateMasterCode("EmpStsCode", customer.getCustEmpSts()));
		}

		if (StringUtils.isNotBlank(customer.getCustDSADept())) {
			auditDetail.setErrorDetail(validateMasterCode("Department", customer.getCustDSADept()));
		}

		if (customer.getCasteId() > 0) {
			auditDetail.setErrorDetail(validateMasterCode("Caste", "CasteId", customer.getCasteId()));
		}
		if (customer.getReligionId() > 0) {
			auditDetail.setErrorDetail(validateMasterCode("Religion", "ReligionId", customer.getReligionId()));
		}
		if (StringUtils.isNotBlank(customer.getSubCategory())) {
			List<ValueLabel> subCategories = PennantStaticListUtil.getSubCategoriesList();
			boolean categorieSts = false;
			for (ValueLabel value : subCategories) {
				if (StringUtils.equals(value.getValue(), customer.getSubCategory())) {
					categorieSts = true;
					break;
				}
			}
			if (!categorieSts) {
				ErrorDetail errorDetail = new ErrorDetail();
				String[] valueParm = new String[2];
				valueParm[0] = customer.getSubCategory();
				valueParm[1] = PennantConstants.SUBCATEGORY_DOMESTIC + ", " + PennantConstants.SUBCATEGORY_NRI;
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90337", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		if (StringUtils.isNotBlank(customer.getCustDSA())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM));
			Matcher matcher = pattern.matcher(customer.getCustDSA());
			if (matcher.matches() == false) {
				ErrorDetail errorDetail = new ErrorDetail();
				String[] valueParm = new String[1];
				valueParm[0] = "saleAgent";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90347", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		if (customer.getCustGroupID() > 0) {
			auditDetail.setErrorDetail(validateMasterCode("CustomerGroup", String.valueOf(customer.getCustGroupID())));
		}
		if (StringUtils.isNotBlank(customer.getCustStaffID())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM));
			Matcher matcher = pattern.matcher(customer.getCustStaffID());
			if (matcher.matches() == false) {
				ErrorDetail errorDetail = new ErrorDetail();
				String[] valueParm = new String[1];
				valueParm[0] = "staffID";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90347", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		if (customer.getCustDOB() != null && (customer.getCustDOB().compareTo(DateUtility.getAppDate()) >= 0
				|| SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(customer.getCustDOB()) >= 0)) {
			ErrorDetail errorDetail = new ErrorDetail();
			String[] valueParm = new String[3];
			valueParm[0] = "Date of Birth";
			valueParm[1] = DateUtility.format(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
					PennantConstants.XMLDateFormat);
			valueParm[2] = DateUtility.format(DateUtility.getAppDate(), PennantConstants.XMLDateFormat);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm));
			auditDetail.setErrorDetail(errorDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private ErrorDetail validateMasterCode(String moduleName, Object fieldValue) {
		logger.debug(Literal.ENTERING);

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
				valueParm[1] = Objects.toString(fieldValue.toString());
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm));
			}
		}

		logger.debug(Literal.LEAVING);
		return errorDetail;
	}

	private ErrorDetail validateMasterCode(String tableName, String columnName, Object value) {
		logger.debug(Literal.ENTERING);

		ErrorDetail errorDetail = new ErrorDetail();

		// validate Master code with PLF system masters
		int count = getCustomerDAO().getLookupCount(tableName, columnName, value);
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = columnName;
			valueParm[1] = Objects.toString(value);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
		}

		logger.debug(Literal.LEAVING);
		return errorDetail;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Customers by using CustomerDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustomers by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "delete");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		auditDetails.addAll(getListAuditDetails(listDeletion(customerDetails, "", auditHeader.getAuditTranType())));

		getCustomerDAO().delete(customer, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
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
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws CustomerNotFoundException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		// process to send finone request and create or update the data.
		createOrUpdateCrmCustomer(aAuditHeader);

		if (!aAuditHeader.isNextProcess()) {
			logger.debug("isNextProcess");
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		if (aAuditHeader.getAuditDetail().getErrorDetails() != null
				&& !aAuditHeader.getAuditDetail().getErrorDetails().isEmpty()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		// Fetched from the approved list for rebuild. Since rebuild should
		// after the transaction but it is happening in the transaction
		Customer appCustomer = getCustomerDAO().getCustomerByID(customer.getCustID());

		if (PennantConstants.RECORD_TYPE_DEL.equals(customer.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(customerDetails, "", tranType));
			getCustomerDAO().delete(customer, "");
		} else {

			customer.setRoleCode("");
			customer.setNextRoleCode("");
			customer.setTaskId("");
			customer.setNextTaskId("");
			customer.setWorkflowId(0);
			if (PennantConstants.RECORD_TYPE_NEW.equals(customer.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				customer.setRecordType("");
				// Customer Creation Core Banking
				// getCustomerInterfaceService().generateNewCIF("A", customer,
				// "");
				getCustomerDAO().save(customer, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customer.setRecordType("");
				// Version increased to fix the issue when version got increased
				// while creating the loan with the customer
				Customer mainCustomer = getCustomerDAO().getCustomerByCIF(customer.getCustCIF(), "");
				if (mainCustomer != null) {
					customer.setVersion(mainCustomer.getVersion() + 1);
				}
				getCustomerDAO().update(customer, "");
			}

			customerDetails.setCustID(customer.getCustID());

			if (customerDetails.getCustEmployeeDetail() != null) {
				CustEmployeeDetail custEmpDetail = customerDetails.getCustEmployeeDetail();
				custEmpDetail.setWorkflowId(0);
				custEmpDetail.setCustID(customer.getCustID());
				custEmpDetail.setRecordType(customer.getRecordType());
				custEmpDetail.setRecordStatus(customer.getRecordStatus());
				CustEmployeeDetail custEmployeeDetail = getCustEmployeeDetailDAO()
						.getCustEmployeeDetailById(customer.getCustID(), "");
				if (custEmployeeDetail == null) {
					getCustEmployeeDetailDAO().save(custEmpDetail, "");
				} else {
					getCustEmployeeDetailDAO().update(custEmpDetail, "");
				}
			}

			// Retrieving List of Audit Details For Customer related modules
			if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
				details = processingRatingList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getEmploymentDetailsList() != null
					&& customerDetails.getEmploymentDetailsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
				details = processingCustomerEmploymentDetailList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerPhoneNumList() != null
					&& customerDetails.getCustomerPhoneNumList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
				details = processingPhoneNumberList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Income");
				details = processingIncomeList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerEMailList() != null && customerDetails.getCustomerEMailList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
				details = processingEMailList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}
			if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Address");
				details = processingAddressList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerDocumentsList() != null
					&& customerDetails.getCustomerDocumentsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Document");
				details = processingDocumentList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerDirectorList() != null
					&& customerDetails.getCustomerDirectorList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Director");
				details = processingDirectorList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerBankInfoList() != null
					&& customerDetails.getCustomerBankInfoList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerBankInfo");
				details = processingBankInfoList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerGstList() != null && customerDetails.getCustomerGstList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerGST");
				details = processingCustomerGSTList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);

			}

			if (customerDetails.getCustomerChequeInfoList() != null
					&& customerDetails.getCustomerChequeInfoList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerChequeInfo");
				details = processingChequeInfoList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerExtLiabilityList() != null
					&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerExtLiability");
				details = processingExtLiabilityList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			// Extended field Details
			if (customerDetails.getExtendedFieldRender() != null) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("ExtendedFieldDetails");
				for (AuditDetail auditDetail : details) {
					ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
					extendedFieldRender.setNewRecord(false);
					if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						customerDetails.getExtendedFieldHeader(), "", 0);
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSales");
				details = processingCardSalesInfoList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSalesDetails");
				details = processingCardSaleInfoDetailList(details, "", Long.MIN_VALUE);
				auditDetails.addAll(details);
			}

			auditDetails.addAll(saveOrUpdateDedupDetails(customerDetails));
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		if (!StringUtils.equals(customerDetails.getSourceId(), PennantConstants.FINSOURCE_ID_API)
				&& !customer.isNewRecord()) {
			auditDetailList.addAll(listDeletion(customerDetails, "_Temp", auditHeader.getAuditTranType()));
			getCustomerDAO().delete(customer, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		processLimitRebuild(customer, appCustomer);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader createOrUpdateCrmCustomer(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		if (crm == null) {
			return auditHeader;
		}

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		AuditDetail auditDetail = auditHeader.getAuditDetail();

		String[] errorParm = new String[2];
		errorParm[0] = "Customer";

		try {

			// begin 09-05-18
			if (!SysParamUtil.isAllowed("GCD_FINONE_PROC_REQD")) {
				customerDetails.setReturnStatus(new WSReturnStatus());
				customerDetails.getReturnStatus().setReturnCode(InterfaceConstants.SUCCESS_CODE);
				return auditHeader;
			}
			// end
			customerService.prepareGCDCustomerData(customerDetails);
			crm.create(customerDetails);

			WSReturnStatus status = customerDetails.getReturnStatus();

			if ((status != null) && (!InterfaceConstants.SUCCESS_CODE.equals(status.getReturnCode()))) {
				logger.debug("Failure Entering");
				errorParm[1] = status.getReturnText();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, status.getReturnCode(), errorParm, null),
						auditHeader.getUsrLanguage()));
				auditDetail.setErrorDetails(
						ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));
				auditHeader.setAuditDetail(auditDetail);
				auditHeader.setErrorList(auditDetail.getErrorDetails());
				auditHeader = nextProcess(auditHeader);

				return auditHeader;
			}

		} catch (InterfaceException e) {
			logger.debug("InterfaceException Entering");
			errorParm[1] = e.getMessage();
			auditDetail.setErrorDetail(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "99014", errorParm, null),
							auditHeader.getUsrLanguage()));
			auditDetail.setErrorDetails(
					ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader = nextProcess(auditHeader);
			return auditHeader;
		}

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private void processLimitRebuild(Customer customer, Customer appCustomer) {
		if (appCustomer != null) {
			// removed from group
			if (isValid(appCustomer.getCustGroupID()) && !isValid(customer.getCustGroupID())) {
				limitRebuild.processCustomerGroupRebuild(appCustomer.getCustGroupID(), true, false);
			}

			// newly added to group
			if (!isValid(appCustomer.getCustGroupID()) && isValid(customer.getCustGroupID())) {
				limitRebuild.processCustomerGroupRebuild(customer.getCustGroupID(), false, true);
			}
			// group swapped
			if (isValid(appCustomer.getCustGroupID()) && isValid(customer.getCustGroupID())
					&& appCustomer.getCustGroupID() != customer.getCustGroupID()) {
				limitRebuild.processCustomerGroupSwap(customer.getCustGroupID(), appCustomer.getCustGroupID());
				limitRebuild.processCustomerGroupRebuild(appCustomer.getCustGroupID(), true, false);
			}

		} else {
			// new record
			if (isValid(customer.getCustGroupID())) {
				limitRebuild.processCustomerGroupRebuild(customer.getCustGroupID(), false, false);
			}
		}
	}

	private boolean isValid(Long vale) {
		if (vale != null && vale != Long.MIN_VALUE && vale != 0) {
			return true;
		}
		return false;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerDAO().delete with parameters customer,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCustomers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails
				.addAll(getListAuditDetails(listDeletion(customerDetails, "_Temp", auditHeader.getAuditTranType())));

		getCustomerDAO().delete(customer, "_Temp");
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				customer.getBefImage(), customer));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getCustomerDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);

		doPostHookValidation(auditHeader);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = customerDetails.getCustomer().getUserDetails().getLanguage();
		String custctg = customerDetails.getCustomer().getCustCtgCode();

		// Rating Validation
		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
			details = getRatingValidation().ratingListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		if (custctg.equals(PennantConstants.PFF_CUSTCTG_INDIV)) {
			// EmploymentDetail Validation
			if (customerDetails.getCustomer().getCustEmpSts() != null) {
				if ("EMPLOY".equals(customerDetails.getCustomer().getCustEmpSts())) {
					List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EmploymentDetail");
					details = getEmploymentDetailValidation().employmentDetailListValidation(details, method,
							usrLanguage);
					auditDetails.addAll(details);
				}
			}

			// Rating Validation
			if (customerDetails.getEmploymentDetailsList() != null
					&& customerDetails.getEmploymentDetailsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
				details = getEmploymentDetailValidation().employmentDetailListValidation(details, method, usrLanguage);
				auditDetails.addAll(details);
			}

			// PhoneNumber Validation
			if (customerDetails.getCustomerPhoneNumList() != null
					&& customerDetails.getCustomerPhoneNumList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
				details = getPhoneNumberValidation().phoneNumberListValidation(details, method, usrLanguage);
				auditDetails.addAll(details);
			}

			// Income Validation
			if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
				auditDetails.addAll(
						getCustomerIncomeValidation().incomeListValidation(customerDetails, method, usrLanguage));
			}

			// PRelation Validation
			if (customerDetails.getCustomerEMailList() != null && customerDetails.getCustomerEMailList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
				details = getCustomerEMailValidation().emailListValidation(details, method, usrLanguage);
				auditDetails.addAll(details);
			}

			// Document Validation
			if (customerDetails.getCustomerDocumentsList() != null
					&& customerDetails.getCustomerDocumentsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Document");
				details = getDocumentValidation().documentListValidation(details, method, usrLanguage);
				auditDetails.addAll(details);
			}

		}

		// Address Validation
		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Address");
			details = getAddressValidation().addressListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		// Director Validation
		if (customerDetails.getCustomerDirectorList() != null && customerDetails.getCustomerDirectorList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Director");
			details = getDirectorValidation().directorListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Bank Information Validation
		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerBankInfo");
			details = getCustomerBankInfoValidation().bankInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Cheque Information Validation
		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerChequeInfo");
			details = getCustomerChequeInfoValidation().chequeInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Bank Information Validation
		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerExtLiability");
			details = getExternalLiabilityValidation().extLiabilityListValidation(details, 0, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Extended field details Validation
		if (customerDetails.getExtendedFieldRender() != null) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = customerDetails.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Customer Card sale Information Validation
		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustCardSales");
			details = getCustomerCardSalesValidation().cardSaleInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	// ### 19-06-2018 PSD 127035
	/**
	 * To handle service level validations before calling service task
	 * 
	 */
	@Override
	public AuditHeader preValidate(AuditHeader auditHeader) {
		return businessValidation(auditHeader, "Validate");
	}

	// ### 19-06-2018 -End

	private void doPostHookValidation(AuditHeader auditHeader) {
		if (postValidationHook != null) {
			List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);

			if (errorDetails != null) {
				errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
				auditHeader.setErrorList(errorDetails);
			}
		}
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from Fetch the error details from the ErrorUtil. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {

		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		CustomerDetails customerDetails = (CustomerDetails) auditDetail.getModelData();
		Customer customer = customerDetails.getCustomer();

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

		if (customer.isNew()) { // for New record or new record into work flow

			if (!customer.isWorkflow()) {// With out Work flow only new records
				if (befCustomer != null) { // Record Already Exists in the table
					// then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																								// records
																							// type
																							// is
																							// new
					if (befCustomer != null || tempCustomer != null) { // if
																			// records
																		// already
																		// exists
																		// in
																		// the
																		// main
																		// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomer == null || tempCustomer != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customer.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befCustomer == null) { // if records not exists in the main
					// table
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

				if (tempCustomer == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomer != null && oldCustomer != null
						&& !oldCustomer.getLastMntOn().equals(tempCustomer.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}
		}

		boolean isDuplicateCif = false;
		if (SysParamUtil.isAllowed(SMTParameterConstants.CUST_PAN_VALIDATION)) {
			isDuplicateCif = customerDAO.isDuplicateCif(customer.getCustID(), customer.getCustCIF(),
					customer.getCustCtgCode());
		} else {
			isDuplicateCif = customerDAO.isDuplicateCif(customer.getCustID(), customer.getCustCIF());
		}

		if (isDuplicateCif) {
			errParm[1] = "";
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errParm, null));
		}

		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(customer.getRecordType())) {
			boolean financeExistForCustomer = getCustomerDAO().financeExistForCustomer(customer.getId(), "_View");
			if (financeExistForCustomer) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, null));
			}
		}

		boolean isDuplicateCoreBankId = false;
		if (StringUtils.isNotBlank(customer.getCustCoreBank())) {
			isDuplicateCoreBankId = customerDAO.isDuplicateCoreBankId(customer.getCustID(), customer.getCustCoreBank());
		}
		if (isDuplicateCoreBankId) {
			String[] errorParameters = new String[1];
			errorParameters[0] = PennantJavaUtil.getLabel("label_CustCoreBank") + ":" + customer.getCustCoreBank();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errorParameters, null));
		}
		//Checking duplicate CRCPR  with cust category
		if (StringUtils.isNotBlank(customer.getCustCRCPR())//PSD#151343 while overriding customer getting pan validation from coapp screen
				&& SysParamUtil.isAllowed(SMTParameterConstants.CUST_PAN_VALIDATION) && !customer.isSkipDedup()) {
			isDuplicateCRCPR(auditDetail, customer, false);
		} else if (StringUtils.isNotBlank(customer.getCustCRCPR()) && !customer.isSkipDedup()) {
			//Checking duplicate CRCPR with cust category
			isDuplicateCRCPR(auditDetail, customer, true);
		}

		//PAN 4th Letter Mapping check
		if (StringUtils.isNotBlank(customer.getCustCRCPR()) && StringUtils.isNotBlank(customer.getCustTypeCode())) {
			String[] errorParameters = new String[2];
			CustTypePANMapping custTypePANMapping = new CustTypePANMapping();
			custTypePANMapping.setCustCategory(customer.getCustCtgCode());
			custTypePANMapping.setCustType(customer.getCustTypeCode());
			CustTypePANMapping approvedPANMapping = custTypePANMappingService.getApprovedPANMapping(custTypePANMapping,
					TableType.MAIN_TAB.getSuffix());
			if (approvedPANMapping != null) {
				String panFourthLetter = StringUtils.substring(customer.getCustCRCPR(), 3, 4);
				if (!StringUtils.equals(approvedPANMapping.getPanLetter(), panFourthLetter)) {
					errorParameters[0] = Labels.getLabel("label_PAN_FourthLetter.label");
					errorParameters[1] = approvedPANMapping.getPanLetter();

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41000", errorParameters)));

				}
			}
		}
		// Employment type other than NON-WORKING setting below fields as Mandatory
		if (StringUtils.isNotBlank(customer.getSubCategory()) && !"#".equals(customer.getSubCategory())) {
			if (!PennantConstants.EMPLOYMENTTYPE_NONWORKING.equals(customer.getSubCategory())) {
				if (StringUtils.isBlank(customer.getCustSector())) {
					valueParm = new String[1];
					valueParm[0] = Labels.getLabel("label_CustomerDialog_CustSector.value");
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
					return auditDetail;
				}
				if (StringUtils.isBlank(customer.getCustIndustry())) {
					valueParm = new String[1];
					valueParm[0] = Labels.getLabel("label_CustomerDialog_CustIndustry.value");
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
					return auditDetail;
				}
				if (StringUtils.isBlank(customer.getCustSegment())
						&& PennantConstants.PFF_CUSTCTG_INDIV.equals(customerDetails.getCustCtgCode())) {
					valueParm = new String[1];
					valueParm[0] = Labels.getLabel("label_CustomerDialog_CustSegment.value");
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
					return auditDetail;
				}
			}
		}

		// customer dedup validation
		if (customerDetails.getCustomerDedupList() != null && !customerDetails.getCustomerDedupList().isEmpty()) {
			for (CustomerDedup customerDedup : customerDetails.getCustomerDedupList()) {
				if (StringUtils.equals(customerDedup.getSourceSystem(),
						PennantConstants.CUSTOMER_DEDUP_SOURCE_SYSTEM_PENNANT)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("99012", null)));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customer.isWorkflow()) {
			customer.setBefImage(befCustomer);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private List<AuditDetail> processingRatingList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerRating customerRating = (CustomerRating) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerRating.setRoleCode("");
				customerRating.setNextRoleCode("");
				customerRating.setTaskId("");
				customerRating.setNextTaskId("");
			}

			customerRating.setWorkflowId(0);
			customerRating.setCustID(custId);

			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerRating.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerRating.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerRating.getRecordType();
				recordStatus = customerRating.getRecordStatus();
				customerRating.setRecordType("");
				customerRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerRatingDAO.save(customerRating, type);
			}

			if (updateRecord) {
				customerRatingDAO.update(customerRating, type);
			}

			if (deleteRecord) {
				customerRatingDAO.delete(customerRating, type);
			}

			if (approveRec) {
				customerRating.setRecordType(rcdType);
				customerRating.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerRating);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingCustomerEmploymentDetailList(List<AuditDetail> auditDetails, String type,
			long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditDetails.get(i)
					.getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerEmploymentDetail.setRoleCode("");
				customerEmploymentDetail.setNextRoleCode("");
				customerEmploymentDetail.setTaskId("");
				customerEmploymentDetail.setNextTaskId("");
			}

			customerEmploymentDetail.setWorkflowId(0);
			customerEmploymentDetail.setCustID(custId);

			if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)
					&& !approveRec) {
				deleteRecord = true;
			} else if (customerEmploymentDetail.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerEmploymentDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerEmploymentDetail.getRecordType();
				recordStatus = customerEmploymentDetail.getRecordStatus();
				customerEmploymentDetail.setRecordType("");
				customerEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerEmploymentDetailDAO.save(customerEmploymentDetail, type);
			}

			if (updateRecord) {
				customerEmploymentDetailDAO.update(customerEmploymentDetail, type);
			}

			if (deleteRecord) {
				customerEmploymentDetailDAO.delete(customerEmploymentDetail, type);
			}

			if (approveRec) {
				customerEmploymentDetail.setRecordType(rcdType);
				customerEmploymentDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerEmploymentDetail);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingIncomeList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerIncome customerIncome = (CustomerIncome) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerIncome.setRoleCode("");
				customerIncome.setNextRoleCode("");
				customerIncome.setTaskId("");
				customerIncome.setNextTaskId("");
			}

			customerIncome.setWorkflowId(0);
			customerIncome.setCustId(custId);

			if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerIncome.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerIncome.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerIncome.getRecordType();
				recordStatus = customerIncome.getRecordStatus();
				customerIncome.setRecordType("");
				customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerIncomeDAO.setLinkId(customerIncome);
				incomeDetailDAO.save(customerIncome, type);
			}

			if (updateRecord) {
				incomeDetailDAO.update(customerIncome, type);
			}

			if (deleteRecord) {
				incomeDetailDAO.delete(customerIncome.getId(), type);
			}

			if (approveRec) {
				customerIncome.setRecordType(rcdType);
				customerIncome.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerIncome);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingEMailList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerEMail customerEMail = (CustomerEMail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerEMail.setRoleCode("");
				customerEMail.setNextRoleCode("");
				customerEMail.setTaskId("");
				customerEMail.setNextTaskId("");
			}

			customerEMail.setWorkflowId(0);
			customerEMail.setCustID(custId);

			if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerEMail.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerEMail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerEMail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerEMail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerEMail.getRecordType();
				recordStatus = customerEMail.getRecordStatus();
				customerEMail.setRecordType("");
				customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerEMailDAO.save(customerEMail, type);
			}

			if (updateRecord) {
				customerEMailDAO.update(customerEMail, type);
			}

			if (deleteRecord) {
				customerEMailDAO.delete(customerEMail, type);
			}

			if (approveRec) {
				customerEMail.setRecordType(rcdType);
				customerEMail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerEMail);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingAddressList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerAddres customerAddres = (CustomerAddres) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerAddres.setRoleCode("");
				customerAddres.setNextRoleCode("");
				customerAddres.setTaskId("");
				customerAddres.setNextTaskId("");
			}

			customerAddres.setWorkflowId(0);
			customerAddres.setCustID(custId);

			if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerAddres.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerAddres.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerAddres.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerAddres.getRecordType();
				recordStatus = customerAddres.getRecordStatus();
				customerAddres.setRecordType("");
				customerAddres.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerAddresDAO.save(customerAddres, type);
			}

			if (updateRecord) {
				customerAddresDAO.update(customerAddres, type);
			}

			if (deleteRecord) {
				customerAddresDAO.delete(customerAddres, type);
			}

			if (approveRec) {
				customerAddres.setRecordType(rcdType);
				customerAddres.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerAddres);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingDocumentList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerDocument customerDocument = (CustomerDocument) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerDocument.setRoleCode("");
				customerDocument.setNextRoleCode("");
				customerDocument.setTaskId("");
				customerDocument.setNextTaskId("");
			}

			customerDocument.setWorkflowId(0);
			customerDocument.setCustID(custId);

			if (StringUtils.trimToEmpty(customerDocument.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerDocument.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerDocument.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = StringUtils.trimToEmpty(customerDocument.getRecordType());
				recordStatus = customerDocument.getRecordStatus();
				customerDocument.setRecordType("");
				customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				saveDocument(DMSModule.CUSTOMER, null, customerDocument);
				customerDocumentDAO.save(customerDocument, type);
			}

			if (updateRecord) {
				saveDocument(DMSModule.CUSTOMER, null, customerDocument);
				customerDocumentDAO.update(customerDocument, type);
			}

			if (deleteRecord) {
				customerDocumentDAO.delete(customerDocument, type);
			}

			if (approveRec) {
				customerDocument.setRecordType(rcdType);
				customerDocument.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerDocument);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingBankInfoList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerBankInfo customerBankInfo = (CustomerBankInfo) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerBankInfo.setRoleCode("");
				customerBankInfo.setNextRoleCode("");
				customerBankInfo.setTaskId("");
				customerBankInfo.setNextTaskId("");
			}

			customerBankInfo.setWorkflowId(0);
			customerBankInfo.setCustID(custId);

			if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerBankInfo.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerBankInfo.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerBankInfo.getRecordType();
				recordStatus = customerBankInfo.getRecordStatus();
				customerBankInfo.setRecordType("");
				customerBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerBankInfo.setBankId(customerBankInfoDAO.save(customerBankInfo, type));
			}

			if (updateRecord) {
				customerBankInfoDAO.update(customerBankInfo, type);
			}

			if (deleteRecord) {

				if (StringUtils.isBlank(type)) {
					if (CollectionUtils.isNotEmpty(customerBankInfo.getBankInfoDetails())) {
						for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
							if (CollectionUtils.isNotEmpty(bankInfoDetail.getBankInfoSubDetails())) {
								customerBankInfoDAO.delete(bankInfoDetail.getBankInfoSubDetails(), type);
							}
							customerBankInfoDAO.delete(bankInfoDetail, type);
						}
					}
				}

				customerBankInfoDAO.delete(customerBankInfo, type);
			}

			if (approveRec) {
				customerBankInfo.setRecordType(rcdType);
				customerBankInfo.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerBankInfo);

			if (customerBankInfo.isAddToBenficiary() && approveRec) {
				addToCustomerBeneficiary(customerBankInfo, customerBankInfo.getCustID());
			}

			// Bank Info Details
			boolean bankInfoUpdate = true;
			if (StringUtils.isBlank(type) && deleteRecord && approveRec) {
				bankInfoUpdate = false;
			}

			if (bankInfoUpdate) {
				List<AuditDetail> details = customerBankInfo.getAuditDetailMap().get("BankInfoDetail");
				if (details != null) {
					details = processingBankInfoDetailList(details, type, customerBankInfo.getBankId());
				}
			}

			processingExternalDocuments(customerBankInfo);
		}

		return auditDetails;

	}

	private void addToCustomerBeneficiary(CustomerBankInfo customerBankInfo, long cusID) {
		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(customerBankInfo.getAccountNumber(),
				customerBankInfo.getBankBranchID(), "_View");
		if (count == 0) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(cusID);
			beneficiary.setBankBranchID(customerBankInfo.getBankBranchID());
			beneficiary.setAccNumber(customerBankInfo.getAccountNumber());
			beneficiary.setAccHolderName(customerBankInfo.getAccountHolderName());
			beneficiary.setPhoneNumber(customerBankInfo.getPhoneNumber());
			beneficiary.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			beneficiaryDAO.save(beneficiary, "");
		}
	}

	private List<AuditDetail> processingBankInfoDetailList(List<AuditDetail> auditDetails, String type, long bankId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			BankInfoDetail bankInfoDetail = (BankInfoDetail) auditDetails.get(i).getModelData();
			bankInfoDetail.setBankId(bankId);

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				bankInfoDetail.setRoleCode("");
				bankInfoDetail.setNextRoleCode("");
				bankInfoDetail.setTaskId("");
				bankInfoDetail.setNextTaskId("");
			}

			bankInfoDetail.setWorkflowId(0);

			if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (bankInfoDetail.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					bankInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					bankInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					bankInfoDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (bankInfoDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (bankInfoDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = bankInfoDetail.getRecordType();
				recordStatus = bankInfoDetail.getRecordStatus();
				bankInfoDetail.setRecordType("");
				bankInfoDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerBankInfoDAO.save(bankInfoDetail, type);
			}

			if (updateRecord) {
				customerBankInfoDAO.update(bankInfoDetail, type);
			}

			if (deleteRecord) {
				customerBankInfoDAO.delete(bankInfoDetail, type);
			}

			if (approveRec) {
				bankInfoDetail.setRecordType(rcdType);
				bankInfoDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(bankInfoDetail);
			processingBankInfoSubDetailList(bankInfoDetail, type, bankId);
		}
		return auditDetails;
	}

	private void processingBankInfoSubDetailList(BankInfoDetail bankInfoDetail, String type, long bankId) {
		logger.debug(Literal.ENTERING);

		for (BankInfoSubDetail detail : bankInfoDetail.getBankInfoSubDetails()) {
			detail.setBankId(bankId);
			detail.setMonthYear(bankInfoDetail.getMonthYear());
			detail.setVersion(bankInfoDetail.getVersion());
			detail.setWorkflowId(bankInfoDetail.getWorkflowId());
		}

		if (type.isEmpty()) {
			customerBankInfoDAO.delete(bankInfoDetail.getBankInfoSubDetails(), "_Temp");
			if (PennantConstants.RECORD_TYPE_UPD.equals(bankInfoDetail.getRecordType())) {
				customerBankInfoDAO.delete(bankInfoDetail.getBankInfoSubDetails(), type);
			}
		} else {
			if (!bankInfoDetail.isNewRecord()) {
				customerBankInfoDAO.delete(bankInfoDetail.getBankInfoSubDetails(), type);
			}
		}

		if (!StringUtils.equals(bankInfoDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)
				&& !StringUtils.equals(bankInfoDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
			customerBankInfoDAO.save(bankInfoDetail.getBankInfoSubDetails(), type);
		}

		logger.debug(Literal.LEAVING);
	}

	private void processingExternalDocuments(CustomerBankInfo custBankInfo) {
		if (CollectionUtils.isNotEmpty(custBankInfo.getExternalDocuments())) {
			for (ExternalDocument externalDocument : custBankInfo.getExternalDocuments()) {
				if (StringUtils.isEmpty(externalDocument.getRecordType())) {
					continue;
				}
				externalDocument.setBankId(custBankInfo.getBankId());
				externalDocument.setCustId(custBankInfo.getCustID());

				DocumentManager documentManager = new DocumentManager();
				documentManager.setDocImage(externalDocument.getDocImage());
				externalDocument.setDocRefId(documentManagerDAO.save(documentManager));
				if (!externalDocument.isBankReport()) { //To identify the ExternalDocument is statement or report
					customerDocumentDAO.save(externalDocument, "");
				}
			}
			//Added below code for storing perfios reports into PerfiosHeader and PerfiosDetails tables
			perfiosService.savePerfiosDocuments(custBankInfo);
		}
	}

	private List<AuditDetail> processingCardSalesInfoList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustCardSales customerCardSalesInfo = (CustCardSales) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerCardSalesInfo.setRoleCode("");
				customerCardSalesInfo.setNextRoleCode("");
				customerCardSalesInfo.setTaskId("");
				customerCardSalesInfo.setNextTaskId("");
			}

			customerCardSalesInfo.setWorkflowId(0);
			customerCardSalesInfo.setCustID(custId);

			if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)
					&& !approveRec) {
				deleteRecord = true;
			} else if (customerCardSalesInfo.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerCardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerCardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerCardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerCardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerCardSalesInfo.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerCardSalesInfo.getRecordType();
				recordStatus = customerCardSalesInfo.getRecordStatus();
				customerCardSalesInfo.setRecordType("");
				customerCardSalesInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerCardSalesInfoDAO.save(customerCardSalesInfo, type);
			}

			if (updateRecord) {
				customerCardSalesInfoDAO.update(customerCardSalesInfo, type);
			}

			if (deleteRecord) {
				customerCardSalesInfoDAO.delete(customerCardSalesInfo, type);
			}

			if (approveRec) {
				customerCardSalesInfo.setRecordType(rcdType);
				customerCardSalesInfo.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerCardSalesInfo);

			// Bank Info Details
			List<AuditDetail> details = customerCardSalesInfo.getAuditDetailMap().get("CustCardSalesDetails");
			if (details != null) {
				details = processingCardSaleInfoDetailList(details, type, customerCardSalesInfo.getId());
			}
		}

		return auditDetails;

	}

	private List<AuditDetail> processingCardSaleInfoDetailList(List<AuditDetail> auditDetails, String type,
			long merchantId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustCardSalesDetails custCardMonthSales = (CustCardSalesDetails) auditDetails.get(i).getModelData();
			if (merchantId != Long.MIN_VALUE) {
				custCardMonthSales.setCardSalesId(merchantId);
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				custCardMonthSales.setRoleCode("");
				custCardMonthSales.setNextRoleCode("");
				custCardMonthSales.setTaskId("");
				custCardMonthSales.setNextTaskId("");
			}

			custCardMonthSales.setWorkflowId(0);

			if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (custCardMonthSales.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					custCardMonthSales.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					custCardMonthSales.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					custCardMonthSales.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (custCardMonthSales.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (custCardMonthSales.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (merchantId != Long.MIN_VALUE) {
				if (approveRec) {
					rcdType = custCardMonthSales.getRecordType();
					recordStatus = custCardMonthSales.getRecordStatus();
					custCardMonthSales.setRecordType("");
					custCardMonthSales.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					customerCardSalesInfoDAO.save(custCardMonthSales, type);
				}

				if (updateRecord) {
					customerCardSalesInfoDAO.update(custCardMonthSales, type);
				}

				if (deleteRecord) {
					customerCardSalesInfoDAO.delete(custCardMonthSales, type);
				}

				if (approveRec) {
					custCardMonthSales.setRecordType(rcdType);
					custCardMonthSales.setRecordStatus(recordStatus);
				}
			}
			auditDetails.get(i).setModelData(custCardMonthSales);
		}
		return auditDetails;
	}

	private List<AuditDetail> processingChequeInfoList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerChequeInfo customerChequeInfo = (CustomerChequeInfo) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerChequeInfo.setRoleCode("");
				customerChequeInfo.setNextRoleCode("");
				customerChequeInfo.setTaskId("");
				customerChequeInfo.setNextTaskId("");
			}

			customerChequeInfo.setWorkflowId(0);
			customerChequeInfo.setCustID(custId);

			if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerChequeInfo.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerChequeInfo.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerChequeInfo.getRecordType();
				recordStatus = customerChequeInfo.getRecordStatus();
				customerChequeInfo.setRecordType("");
				customerChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerChequeInfoDAO.save(customerChequeInfo, type);
			}

			if (updateRecord) {
				customerChequeInfoDAO.update(customerChequeInfo, type);
			}

			if (deleteRecord) {
				customerChequeInfoDAO.delete(customerChequeInfo, type);
			}

			if (approveRec) {
				customerChequeInfo.setRecordType(rcdType);
				customerChequeInfo.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerChequeInfo);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingExtLiabilityList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerExtLiability customerExtLiability = (CustomerExtLiability) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerExtLiability.setRoleCode("");
				customerExtLiability.setNextRoleCode("");
				customerExtLiability.setTaskId("");
				customerExtLiability.setNextTaskId("");
			}

			customerExtLiability.setWorkflowId(0);
			customerExtLiability.setCustId(custId);

			if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)
					&& !approveRec) {
				deleteRecord = true;
			} else if (customerExtLiability.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerExtLiability.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerExtLiability.getRecordType();
				recordStatus = customerExtLiability.getRecordStatus();
				customerExtLiability.setRecordType("");
				customerExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				customerExtLiabilityDAO.setLinkId(customerExtLiability);
				externalLiabilityDAO.save(customerExtLiability, type);
			}

			if (updateRecord) {
				externalLiabilityDAO.update(customerExtLiability, type);
			}

			if (deleteRecord) {
				externalLiabilityDAO.delete(customerExtLiability.getId(), type);
			}

			if (approveRec) {
				customerExtLiability.setRecordType(rcdType);
				customerExtLiability.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerExtLiability);
			processingExtLiabilittySubDetailList(customerExtLiability, type, customerExtLiability.getId());

		}

		return auditDetails;

	}

	private void processingExtLiabilittySubDetailList(CustomerExtLiability customerExtLiability, String type,
			long liabilityId) {
		for (ExtLiabilityPaymentdetails installmentDetails : customerExtLiability.getExtLiabilitiesPayments()) {
			installmentDetails.setLiabilityId(liabilityId);
			installmentDetails.setEmiType(installmentDetails.getEmiType());
			installmentDetails.setVersion(installmentDetails.getVersion());
			installmentDetails.setWorkflowId(installmentDetails.getWorkflowId());
			installmentDetails.setEmiClearance(installmentDetails.getEmiClearance());

			installmentDetails.setLastMntBy(customerExtLiability.getLastMntBy());
			installmentDetails.setLastMntOn(customerExtLiability.getLastMntOn());
			installmentDetails.setRecordStatus(customerExtLiability.getRecordStatus());
			installmentDetails.setRecordType(customerExtLiability.getRecordType());
		}
		if (type.isEmpty()) {
			customerExtLiabilityDAO.delete(customerExtLiability.getExtLiabilitiesPayments(), "_Temp");
		} else {
			if (!customerExtLiability.isNewRecord()) {
				customerExtLiabilityDAO.delete(customerExtLiability.getExtLiabilitiesPayments(), type);
			}
		}
		if (!StringUtils.equals(customerExtLiability.getRecordType(), PennantConstants.RECORD_TYPE_CAN)
				&& !StringUtils.equals(customerExtLiability.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
			customerExtLiabilityDAO.delete(customerExtLiability.getExtLiabilitiesPayments(), type);
			customerExtLiabilityDAO.save(customerExtLiability.getExtLiabilitiesPayments(), type);
		}
	}

	private List<AuditDetail> processingPhoneNumberList(List<AuditDetail> auditDetails, String type, long custId) {
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerPhoneNumber.setRoleCode("");
				customerPhoneNumber.setNextRoleCode("");
				customerPhoneNumber.setTaskId("");
				customerPhoneNumber.setNextTaskId("");
			}

			customerPhoneNumber.setWorkflowId(0);
			customerPhoneNumber.setPhoneCustID(custId);

			if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerPhoneNumber.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerPhoneNumber.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerPhoneNumber.getRecordType();
				recordStatus = customerPhoneNumber.getRecordStatus();
				customerPhoneNumber.setRecordType("");
				customerPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerPhoneNumberDAO.save(customerPhoneNumber, type);
			}

			if (updateRecord) {
				customerPhoneNumberDAO.update(customerPhoneNumber, type);
			}

			if (deleteRecord) {
				customerPhoneNumberDAO.delete(customerPhoneNumber, type);
			}

			if (approveRec) {
				customerPhoneNumber.setRecordType(rcdType);
				customerPhoneNumber.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerPhoneNumber);
		}

		return auditDetails;

	}

	private List<AuditDetail> processingDirectorList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DirectorDetail directorDetail = (DirectorDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				directorDetail.setRoleCode("");
				directorDetail.setNextRoleCode("");
				directorDetail.setTaskId("");
				directorDetail.setNextTaskId("");
			}

			directorDetail.setWorkflowId(0);
			directorDetail.setCustID(custId);

			if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (directorDetail.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					directorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					directorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					directorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (directorDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = directorDetail.getRecordType();
				recordStatus = directorDetail.getRecordStatus();
				directorDetail.setRecordType("");
				directorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				directorDetailDAO.save(directorDetail, type);
			}

			if (updateRecord) {
				directorDetailDAO.update(directorDetail, type);
			}

			if (deleteRecord) {
				directorDetailDAO.delete(directorDetail, type);
			}

			if (approveRec) {
				directorDetail.setRecordType(rcdType);
				directorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(directorDetail);
		}

		return auditDetails;

	}

	// Method for Deleting all records related to Customer in _Temp/Main tables
	// depend on method type
	public List<AuditDetail> listDeletion(CustomerDetails custDetails, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (custDetails.getRatingsList() != null && custDetails.getRatingsList().size() > 0) {

			CustomerRating custRating = new CustomerRating();
			String[] fields = PennantJavaUtil.getFieldDetails(custRating, custRating.getExcludeFields());

			for (int i = 0; i < custDetails.getRatingsList().size(); i++) {
				CustomerRating customerRating = custDetails.getRatingsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerRating.getBefImage(),
						customerRating));
			}
			getCustomerRatingDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}
		if (custDetails.getEmploymentDetailsList() != null && custDetails.getEmploymentDetailsList().size() > 0) {

			CustomerEmploymentDetail custEmpDet = new CustomerEmploymentDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(custEmpDet, custEmpDet.getExcludeFields());

			for (int i = 0; i < custDetails.getEmploymentDetailsList().size(); i++) {
				CustomerEmploymentDetail employmentDetail = custDetails.getEmploymentDetailsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						employmentDetail.getBefImage(), employmentDetail));
			}
			getCustomerEmploymentDetailDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getAddressList() != null && custDetails.getAddressList().size() > 0) {

			CustomerAddres custAddress = new CustomerAddres();
			String[] fields = PennantJavaUtil.getFieldDetails(custAddress, custAddress.getExcludeFields());

			for (int i = 0; i < custDetails.getAddressList().size(); i++) {
				CustomerAddres customerAddres = custDetails.getAddressList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerAddres.getBefImage(),
						customerAddres));
			}
			getCustomerAddresDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerEMailList() != null && custDetails.getCustomerEMailList().size() > 0) {

			CustomerEMail custEmail = new CustomerEMail();
			String[] fields = PennantJavaUtil.getFieldDetails(custEmail, custEmail.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerEMailList().size(); i++) {
				CustomerEMail customerEMail = custDetails.getCustomerEMailList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerEMail.getBefImage(),
						customerEMail));
			}
			getCustomerEMailDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerPhoneNumList() != null && custDetails.getCustomerPhoneNumList().size() > 0) {

			CustomerPhoneNumber customerPhoneNum = new CustomerPhoneNumber();
			String[] fields = PennantJavaUtil.getFieldDetails(customerPhoneNum, customerPhoneNum.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerPhoneNumList().size(); i++) {
				CustomerPhoneNumber customerPhoneNumber = custDetails.getCustomerPhoneNumList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerPhoneNumber.getBefImage(), customerPhoneNumber));
			}
			getCustomerPhoneNumberDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerDocumentsList() != null && custDetails.getCustomerDocumentsList().size() > 0) {

			CustomerDocument custDocument = new CustomerDocument();
			String[] fields = PennantJavaUtil.getFieldDetails(custDocument, custDocument.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerDocumentsList().size(); i++) {
				CustomerDocument customerDocument = custDetails.getCustomerDocumentsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerDocument.getBefImage(), customerDocument));
			}
			getCustomerDocumentDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerIncomeList() != null && custDetails.getCustomerIncomeList().size() > 0) {

			CustomerIncome custIncome = new CustomerIncome();
			String[] fields = PennantJavaUtil.getFieldDetails(custIncome, custIncome.getExcludeFields());

			long linkId = 0;

			for (int i = 0; i < custDetails.getCustomerIncomeList().size(); i++) {
				CustomerIncome customerIncome = custDetails.getCustomerIncomeList().get(i);
				linkId = customerIncome.getLinkId();
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerIncome.getBefImage(),
						customerIncome));
			}
			incomeDetailDAO.deletebyLinkId(linkId, tableType);
		}

		if (custDetails.getCustomerDirectorList() != null && custDetails.getCustomerDirectorList().size() > 0) {

			DirectorDetail aDierDetail = new DirectorDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(aDierDetail, aDierDetail.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerDirectorList().size(); i++) {
				DirectorDetail cirectorDetail = custDetails.getCustomerDirectorList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], cirectorDetail.getBefImage(),
						cirectorDetail));
			}
			getDirectorDetailDAO().delete(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustEmployeeDetail() != null) {

			CustEmployeeDetail aCustEmpDetail = new CustEmployeeDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(aCustEmpDetail, aCustEmpDetail.getExcludeFields());

			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
					custDetails.getCustEmployeeDetail().getBefImage(), custDetails.getCustEmployeeDetail()));
			getCustEmployeeDetailDAO().delete(custDetails.getCustEmployeeDetail(), tableType);
		}

		if (custDetails.getCustomerBankInfoList() != null && custDetails.getCustomerBankInfoList().size() > 0) {

			CustomerBankInfo custBankInfo = new CustomerBankInfo();
			String[] fields = PennantJavaUtil.getFieldDetails(custBankInfo, custBankInfo.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerBankInfoList().size(); i++) {
				CustomerBankInfo customerBankInfo = custDetails.getCustomerBankInfoList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerBankInfo.getBefImage(), customerBankInfo));
			}
			// BankInfoDetails
			deleteList(custDetails, tableType);
			getCustomerBankInfoDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}
		if (custDetails.getCustomerGstList() != null && custDetails.getCustomerGstList().size() > 0) {
			CustomerGST custGST = new CustomerGST();
			String[] fields = PennantJavaUtil.getFieldDetails(custGST, custGST.getExcludeFields());
			for (int i = 0; i < custDetails.getCustomerGstList().size(); i++) {
				CustomerGST customerGST = custDetails.getCustomerGstList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerGST.getBefImage(),
						customerGST));
			}
			// delete Customer gst details
			deletegstList(custDetails, tableType);
			getCustomerGstDetailDAO().deleteCustomerGSTByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerChequeInfoList() != null && custDetails.getCustomerChequeInfoList().size() > 0) {

			CustomerChequeInfo custChequeInfo = new CustomerChequeInfo();
			String[] fields = PennantJavaUtil.getFieldDetails(custChequeInfo, custChequeInfo.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerChequeInfoList().size(); i++) {
				CustomerChequeInfo customerChequeInfo = custDetails.getCustomerChequeInfoList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerChequeInfo.getBefImage(), customerChequeInfo));
			}
			getCustomerChequeInfoDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		List<CustomerExtLiability> liabilities = custDetails.getCustomerExtLiabilityList();
		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(liabilities)) {
			CustomerExtLiability custExtLiability = new CustomerExtLiability();
			String[] fields = PennantJavaUtil.getFieldDetails(custExtLiability, custExtLiability.getExcludeFields());

			for (int i = 0; i < custDetails.getCustomerExtLiabilityList().size(); i++) {
				custExtLiability = liabilities.get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						custExtLiability.getBefImage(), custExtLiability));
			}
			externalLiabilityDAO.deleteByLinkId(custExtLiability.getLinkId(), tableType);
		}

		if (custDetails.getCustCardSales() != null && custDetails.getCustCardSales().size() > 0) {

			CustCardSales custCardSalesInfo = new CustCardSales();
			String[] fields = PennantJavaUtil.getFieldDetails(custCardSalesInfo, custCardSalesInfo.getExcludeFields());

			for (int i = 0; i < custDetails.getCustCardSales().size(); i++) {
				CustCardSales customerBankInfo = custDetails.getCustCardSales().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerBankInfo.getBefImage(), customerBankInfo));
			}
			// CardSaleInfodetails
			deleteCustCardMonthSalesList(custDetails, tableType);
			customerCardSalesInfoDAO.deleteByCustomer(custDetails.getCustID(), tableType);
		}

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = custDetails.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditList.addAll(extendedFieldDetailsService.delete(custDetails.getExtendedFieldHeader(),
					custDetails.getCustomer().getCustCIF(), tableType, auditTranType, extendedDetails));
		}

		return auditList;
	}

	private void deletegstList(CustomerDetails custDetails, String tableType) {
		List<CustomerGST> customerGSTList = custDetails.getCustomerGstList();
		for (CustomerGST customerGST : customerGSTList) {
			if (customerGST.getCustomerGSTDetailslist() != null) {
				for (int i = 0; i < customerGST.getCustomerGSTDetailslist().size(); i++) {
					CustomerGSTDetails customerGSTDetails = customerGST.getCustomerGSTDetailslist().get(i);
					customerGSTDetails.setHeaderId(customerGST.getId());
					/*
					 * String[] fields = PennantJavaUtil.getFieldDetails(bankInfoDetail,
					 * bankInfoDetail.getExcludeFields()); auditList.add(new AuditDetail(auditTranType, i + 1,
					 * fields[0], fields[1], bankInfoDetail.getBefImage(), bankInfoDetail));
					 */
					getCustomerGstDetailDAO().delete(customerGSTDetails, tableType);
				}
			}
		}
	}

	private void deleteList(CustomerDetails custDetails, String tableType) {
		List<CustomerBankInfo> customerBankInfoList = custDetails.getCustomerBankInfoList();
		for (CustomerBankInfo customerBankInfo : customerBankInfoList) {
			if (customerBankInfo.getBankInfoDetails() != null) {
				for (int i = 0; i < customerBankInfo.getBankInfoDetails().size(); i++) {
					BankInfoDetail bankInfoDetail = customerBankInfo.getBankInfoDetails().get(i);
					/*
					 * String[] fields = PennantJavaUtil.getFieldDetails(bankInfoDetail,
					 * bankInfoDetail.getExcludeFields()); auditList.add(new AuditDetail(auditTranType, i + 1,
					 * fields[0], fields[1], bankInfoDetail.getBefImage(), bankInfoDetail));
					 */
					getCustomerBankInfoDAO().delete(bankInfoDetail, tableType);
				}
			}
		}
	}

	private void deleteCustCardMonthSalesList(CustomerDetails custDetails, String tableType) {
		List<CustCardSales> custCardSalesInfoList = custDetails.getCustCardSales();
		for (CustCardSales customerCardSalesInfo : custCardSalesInfoList) {
			if (customerCardSalesInfo.getCustCardMonthSales() != null) {
				for (int i = 0; i < customerCardSalesInfo.getCustCardMonthSales().size(); i++) {
					CustCardSalesDetails cardMnthSaleInfoDetail = customerCardSalesInfo.getCustCardMonthSales().get(i);
					customerCardSalesInfoDAO.delete(cardMnthSaleInfoDetail, tableType);
				}
			}
		}
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (customer.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			auditDetailMap.put("Rating", setRatingAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Rating"));
		}
		if (customerDetails.getEmploymentDetailsList() != null
				&& customerDetails.getEmploymentDetailsList().size() > 0) {
			auditDetailMap.put("Employment",
					setCustomerEmploymentDetailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Employment"));
		}

		if (customerDetails.getCustomerPhoneNumList() != null && customerDetails.getCustomerPhoneNumList().size() > 0) {
			auditDetailMap.put("PhoneNumber", setPhoneNumberAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PhoneNumber"));
		}

		if (customerDetails.getCustomerIncomeList() != null && customerDetails.getCustomerIncomeList().size() > 0) {
			auditDetailMap.put("Income", setIncomeAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Income"));
		}

		if (customerDetails.getCustomerEMailList() != null && customerDetails.getCustomerEMailList().size() > 0) {
			auditDetailMap.put("EMail", setEMailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("EMail"));
		}

		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			auditDetailMap.put("Address", setAddressAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Address"));
		}

		if (customerDetails.getCustomerDocumentsList() != null
				&& customerDetails.getCustomerDocumentsList().size() > 0) {
			auditDetailMap.put("Document", setDocumentAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Document"));
		}

		if (customerDetails.getCustomerDirectorList() != null && customerDetails.getCustomerDirectorList().size() > 0) {
			auditDetailMap.put("Director", setDirectorAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Director"));
		}

		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			auditDetailMap.put("CustomerBankInfo", setBankInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerBankInfo"));
		}

		if (customerDetails.getCustomerGstList() != null) {
			auditDetailMap.put("CustomerGST", setCustomerGSTAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerGST"));
		}

		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			auditDetailMap.put("CustomerChequeInfo",
					setChequeInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerChequeInfo"));
		}

		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			auditDetailMap.put("CustomerExtLiability",
					setExtLiabilityAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerExtLiability"));
		}

		// Extended Field Details
		if (customerDetails.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(customerDetails.getExtendedFieldRender(), auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			auditDetailMap.put("CustCardSales",
					setCardSalesInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustCardSales"));
		}

		if (customerDetails.getCustCardSales() != null && customerDetails.getCustCardSales().size() > 0) {
			for (int i = 0; i < customerDetails.getCustCardSales().size(); i++) {
				auditDetailMap.put("CustCardSalesDetails", setCardMonthSalesInfoDetailAuditData(
						customerDetails.getCustCardSales().get(i), auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CustCardSalesDetails"));
			}
		}
		customerDetails.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(customerDetails);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
	}

	private List<AuditDetail> setRatingAuditData(CustomerDetails customerDetails, String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// String[] fields = PennantJavaUtil.getFieldDetails(new
		// CustomerRating());
		CustomerRating custRating = new CustomerRating();
		String[] fields = PennantJavaUtil.getFieldDetails(custRating, custRating.getExcludeFields());

		for (int i = 0; i < customerDetails.getRatingsList().size(); i++) {

			CustomerRating customerRating = customerDetails.getRatingsList().get(i);
			customerRating.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerRating.getCustID() <= 0) {
				customerRating.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerRating.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				customerRating.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerRating.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerRating.setLoginDetails(customerDetails.getUserDetails());
			customerRating.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotEmpty(customerRating.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerRating.getBefImage(), customerRating));
			}
		}

		return auditDetails;
	}

	private List<AuditDetail> setCustomerEmploymentDetailAuditData(CustomerDetails customerDetails,
			String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerEmploymentDetail custEmpDet = new CustomerEmploymentDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(custEmpDet, custEmpDet.getExcludeFields());

		for (int i = 0; i < customerDetails.getEmploymentDetailsList().size(); i++) {

			CustomerEmploymentDetail customerEmploymentDetail = customerDetails.getEmploymentDetailsList().get(i);
			if (StringUtils.isEmpty(customerEmploymentDetail.getRecordType())) {
				continue;
			}
			customerEmploymentDetail.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerEmploymentDetail.getCustID() <= 0) {
				customerEmploymentDetail.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				customerEmploymentDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerEmploymentDetail.getRecordType()
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerEmploymentDetail.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerEmploymentDetail.setLoginDetails(customerDetails.getUserDetails());
			customerEmploymentDetail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotEmpty(customerEmploymentDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerEmploymentDetail.getBefImage(), customerEmploymentDetail));
			}
		}

		return auditDetails;
	}

	private List<AuditDetail> setPhoneNumberAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerPhoneNumber customerPhoneNum = new CustomerPhoneNumber();
		String[] fields = PennantJavaUtil.getFieldDetails(customerPhoneNum, customerPhoneNum.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerPhoneNumList().size(); i++) {
			CustomerPhoneNumber phoneNumber = customerDetails.getCustomerPhoneNumList().get(i);

			if (StringUtils.isEmpty(phoneNumber.getRecordType())) {
				continue;
			}

			phoneNumber.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (phoneNumber.getPhoneCustID() <= 0) {
				phoneNumber.setPhoneCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (phoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				phoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (phoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				phoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (phoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				phoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				phoneNumber.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (phoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (phoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| phoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			phoneNumber.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			phoneNumber.setLoginDetails(customerDetails.getUserDetails());
			phoneNumber.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], phoneNumber.getBefImage(),
					phoneNumber));
		}

		return auditDetails;
	}

	private List<AuditDetail> setIncomeAuditData(CustomerDetails customerDetails, String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerIncome custIncome = new CustomerIncome();

		String[] fields = PennantJavaUtil.getFieldDetails(custIncome, custIncome.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerIncomeList().size(); i++) {

			CustomerIncome customerIncome = customerDetails.getCustomerIncomeList().get(i);
			customerIncome.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerIncome.getCustId() <= 0) {
				customerIncome.setCustId(customerDetails.getCustID());
			}

			if (StringUtils.isEmpty(customerIncome.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;

			if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (("saveOrUpdate".equals(method) || "Validate".equals(method)) && (isRcdType)) {
				customerIncome.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerIncome.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerIncome.setLoginDetails(customerDetails.getUserDetails());
			customerIncome.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotEmpty(customerIncome.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerIncome.getBefImage(), customerIncome));
			}
		}

		return auditDetails;
	}

	private List<AuditDetail> setEMailAuditData(CustomerDetails customerDetails, String auditTranType, String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerEMail custEmail = new CustomerEMail();
		String[] fields = PennantJavaUtil.getFieldDetails(custEmail, custEmail.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerEMailList().size(); i++) {
			CustomerEMail eMail = customerDetails.getCustomerEMailList().get(i);

			if (StringUtils.isEmpty(eMail.getRecordType())) {
				continue;
			}

			eMail.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (eMail.getCustID() <= 0) {
				eMail.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (eMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				eMail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (eMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				eMail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (eMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				eMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				eMail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (eMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (eMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| eMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			eMail.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			eMail.setLoginDetails(customerDetails.getUserDetails());
			eMail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], eMail.getBefImage(), eMail));
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setAddressAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerAddres custAddress = new CustomerAddres();
		String[] fields = PennantJavaUtil.getFieldDetails(custAddress, custAddress.getExcludeFields());

		for (int i = 0; i < customerDetails.getAddressList().size(); i++) {
			CustomerAddres address = customerDetails.getAddressList().get(i);

			if (StringUtils.isEmpty(address.getRecordType())) {
				continue;
			}

			address.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (address.getCustID() <= 0) {
				address.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (address.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				address.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (address.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				address.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (address.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				address.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				address.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (address.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (address.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| address.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			address.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			address.setLoginDetails(customerDetails.getUserDetails());
			address.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails
					.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], address.getBefImage(), address));
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setDocumentAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerDocument custDocument = new CustomerDocument();
		String[] fields = PennantJavaUtil.getFieldDetails(custDocument, custDocument.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerDocumentsList().size(); i++) {

			CustomerDocument customerDocument = customerDetails.getCustomerDocumentsList().get(i);
			customerDocument.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerDocument.getCustID() <= 0) {
				customerDocument.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
					.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (StringUtils.trimToEmpty(StringUtils.trimToEmpty(customerDocument.getRecordType()))
					.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				customerDocument.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| StringUtils.trimToEmpty(customerDocument.getRecordType())
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerDocument.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerDocument.setLoginDetails(customerDetails.getUserDetails());
			customerDocument.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotBlank(customerDocument.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerDocument.getBefImage(), customerDocument));
			}
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setDirectorAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DirectorDetail aDierDetail = new DirectorDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(aDierDetail, aDierDetail.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerDirectorList().size(); i++) {

			DirectorDetail directorDetail = customerDetails.getCustomerDirectorList().get(i);
			directorDetail.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (directorDetail.getCustID() <= 0) {
				directorDetail.setCustID(customerDetails.getCustID());
			}

			if (StringUtils.isEmpty(directorDetail.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;

			if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				directorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				directorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				directorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				directorDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			directorDetail.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			directorDetail.setLoginDetails(customerDetails.getUserDetails());
			directorDetail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotEmpty(directorDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						directorDetail.getBefImage(), directorDetail));
			}
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setBankInformationAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerBankInfo custBankInfo = new CustomerBankInfo();
		String[] fields = PennantJavaUtil.getFieldDetails(custBankInfo, custBankInfo.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerBankInfoList().size(); i++) {
			CustomerBankInfo bankInfo = customerDetails.getCustomerBankInfoList().get(i);

			if (StringUtils.isEmpty(bankInfo.getRecordType())) {
				continue;
			}

			bankInfo.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (bankInfo.getCustID() <= 0) {
				bankInfo.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				bankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				bankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				/*
				 * if (customerDetails.getCustomer().isWorkflow()) { isRcdType = true; }
				 */
			} else if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				bankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				bankInfo.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			bankInfo.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			bankInfo.setLoginDetails(customerDetails.getUserDetails());
			bankInfo.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails
					.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bankInfo.getBefImage(), bankInfo));

			// Audit Bank Info Details
			if (bankInfo.getBankInfoDetails() != null && bankInfo.getBankInfoDetails().size() > 0) {
				bankInfo.getAuditDetailMap().put("BankInfoDetail",
						setBankInfoDetailAuditData(bankInfo, auditTranType, method));
				// auditDetails.addAll(bankInfo.getAuditDetailMap().get("BankInfoDetail"));
			}

		}

		return auditDetails;
	}

	private List<AuditDetail> setCardSalesInformationAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustCardSales custCardSales = new CustCardSales();
		String[] fields = PennantJavaUtil.getFieldDetails(custCardSales, custCardSales.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustCardSales().size(); i++) {
			CustCardSales custCardSalesData = customerDetails.getCustCardSales().get(i);

			if (StringUtils.isEmpty(custCardSalesData.getRecordType())) {
				continue;
			}

			custCardSalesData.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (custCardSalesData.getCustID() <= 0) {
				custCardSalesData.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (custCardSalesData.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				custCardSalesData.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (custCardSalesData.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				custCardSalesData.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (custCardSalesData.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				custCardSalesData.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (("saveOrUpdate".equals(method) || "Validate".equals(method)) && (isRcdType)) {
				custCardSalesData.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (custCardSalesData.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (custCardSalesData.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| custCardSalesData.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			custCardSalesData.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			custCardSalesData.setLoginDetails(customerDetails.getUserDetails());
			custCardSalesData.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					custCardSalesData.getBefImage(), custCardSalesData));

			// Audit Card Sales Info Details
			if (custCardSalesData.getCustCardMonthSales() != null
					&& custCardSalesData.getCustCardMonthSales().size() > 0) {
				custCardSalesData.getAuditDetailMap().put("CustCardSalesDetails",
						setCardMonthSalesInfoDetailAuditData(custCardSalesData, auditTranType, method));
			}

		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCustomerGSTAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// CustomerBankInfo custBankInfo = new CustomerBankInfo();
		CustomerGST customerGSTdt = new CustomerGST();
		String[] fields = PennantJavaUtil.getFieldDetails(customerGSTdt, customerGSTdt.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerGstList().size(); i++) {
			CustomerGST customerGST = customerDetails.getCustomerGstList().get(i);

			if (StringUtils.isEmpty(customerGST.getRecordType())) {
				continue;
			}

			customerGST.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerGST.getCustId() <= 0) {
				customerGST.setCustId(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerGST.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerGST.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerGST.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (("saveOrUpdate".equals(method) || "Validate".equals(method)) && isRcdType) {
				customerGST.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerGST.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerGST.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerGST.getBefImage(),
					customerGST));

			// Audit Bank Info Details
			if (customerGST.getCustomerGSTDetailslist() != null && customerDetails.getCustomerGstList().size() > 0) {
				customerGST.getAuditDetailMap().put("CustomerGSTDetails",
						setCustomerGSTDetailsAuditData(customerGST, auditTranType, method));
			}

		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCustomerGSTDetailsAuditData(CustomerGST customerGST, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// BankInfoDetail bankInfoDetail = new BankInfoDetail();
		CustomerGSTDetails customerGSTDetail = new CustomerGSTDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(customerGSTDetail, customerGSTDetail.getExcludeFields());

		for (int i = 0; i < customerGST.getCustomerGSTDetailslist().size(); i++) {

			CustomerGSTDetails customerGSTDetails = customerGST.getCustomerGSTDetailslist().get(i);
			customerGSTDetails.setRecordStatus(customerGST.getRecordStatus());

			if (StringUtils.isEmpty(customerGSTDetails.getRecordType())) {
				continue;
			}

			customerGSTDetails.setWorkflowId(customerGST.getWorkflowId());

			boolean isRcdType = false;

			if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerGST.isWorkflow()) {
					isRcdType = true;
				}
			} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (("saveOrUpdate".equals(method) || "Validate".equals(method)) && (isRcdType)) {
				customerGSTDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			// auditTranType.setRecordStatus(custBankInfo.getRecordStatus());
			// auditTranType.setLastMntOn(custBankInfo.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					customerGSTDetails.getBefImage(), customerGSTDetails));
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setBankInfoDetailAuditData(CustomerBankInfo custBankInfo, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		BankInfoDetail bankInfoDetail = new BankInfoDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(bankInfoDetail, bankInfoDetail.getExcludeFields());

		for (int i = 0; i < custBankInfo.getBankInfoDetails().size(); i++) {
			BankInfoDetail bankInfo = custBankInfo.getBankInfoDetails().get(i);

			if (StringUtils.isEmpty(bankInfo.getRecordType())) {
				continue;
			}

			bankInfo.setWorkflowId(custBankInfo.getWorkflowId());

			boolean isRcdType = false;

			if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				bankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				bankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (custBankInfo.isWorkflow()) {
					isRcdType = true;
				}
			} else if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				bankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				bankInfo.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| bankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			bankInfo.setRecordStatus(custBankInfo.getRecordStatus());
			bankInfo.setLastMntOn(custBankInfo.getLastMntOn());

			auditDetails
					.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bankInfo.getBefImage(), bankInfo));
		}

		return auditDetails;
	}

	private List<AuditDetail> setCardMonthSalesInfoDetailAuditData(CustCardSales custCardSalesInfo,
			String auditTranType, String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustCardSalesDetails custCardMonthSalesInfoDetail = new CustCardSalesDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(custCardMonthSalesInfoDetail,
				custCardMonthSalesInfoDetail.getExcludeFields());

		for (int i = 0; i < custCardSalesInfo.getCustCardMonthSales().size(); i++) {
			CustCardSalesDetails cardSalesInfo = custCardSalesInfo.getCustCardMonthSales().get(i);

			if (StringUtils.isEmpty(cardSalesInfo.getRecordType())) {
				continue;
			}

			cardSalesInfo.setWorkflowId(custCardSalesInfo.getWorkflowId());

			boolean isRcdType = false;

			if (cardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				cardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (cardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				cardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (custCardSalesInfo.isWorkflow()) {
					isRcdType = true;
				}
			} else if (cardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				cardSalesInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (("saveOrUpdate".equals(method) || "Validate".equals(method)) && (isRcdType)) {
				cardSalesInfo.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (cardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (cardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| cardSalesInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			cardSalesInfo.setRecordStatus(custCardSalesInfo.getRecordStatus());
			cardSalesInfo.setLastMntOn(custCardSalesInfo.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], cardSalesInfo.getBefImage(),
					cardSalesInfo));
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setChequeInformationAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerChequeInfo custChequeInfo = new CustomerChequeInfo();
		String[] fields = PennantJavaUtil.getFieldDetails(custChequeInfo, custChequeInfo.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerChequeInfoList().size(); i++) {
			CustomerChequeInfo chequeInfo = customerDetails.getCustomerChequeInfoList().get(i);

			if (StringUtils.isEmpty(chequeInfo.getRecordType())) {
				continue;
			}

			chequeInfo.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (chequeInfo.getCustID() <= 0) {
				chequeInfo.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (chequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				chequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (chequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				chequeInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (chequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				chequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				chequeInfo.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (chequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (chequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| chequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			chequeInfo.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			chequeInfo.setLoginDetails(customerDetails.getUserDetails());
			chequeInfo.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], chequeInfo.getBefImage(), chequeInfo));
		}

		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setExtLiabilityAuditData(CustomerDetails customerDetails, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CustomerExtLiability liability = new CustomerExtLiability();
		String[] fields = PennantJavaUtil.getFieldDetails(liability, liability.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerExtLiabilityList().size(); i++) {
			liability = customerDetails.getCustomerExtLiabilityList().get(i);

			if (liability.getInputSource() == null) {
				liability.setInputSource("customer");
			}

			if (StringUtils.isEmpty(liability.getRecordType())) {
				continue;
			}

			liability.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (liability.getCustId() <= 0) {
				liability.setCustId(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				liability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				liability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
				}
			} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				liability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (("saveOrUpdate".equals(method) || "Validate".equals(method)) && (isRcdType)) {
				liability.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| liability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			liability.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			liability.setUserDetails(customerDetails.getUserDetails());
			liability.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], liability.getBefImage(), liability));
		}

		return auditDetails;
	}

	/**
	 * Common Method for Customers list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String transType = "";
				String rcdType = "";
				Object object = list.get(i).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());
						AuditDetail auditDetail = new AuditDetail(transType, list.get(i).getAuditSeq(), befImg, object);
						if (auditDetail.getModelData() instanceof ExtendedFieldRender) {
							auditDetail.setExtended(true);
							auditDetail.setAuditField(list.get(i).getAuditField());
							auditDetail.setAuditValue(list.get(i).getAuditValue());
						}
						auditDetailsList.add(auditDetail);
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	/**
	 * 
	 * @param customer
	 * @return
	 * @throws InterfaceException
	 */
	@Override
	public Customer fetchCoreCustomerDetails(Customer customer) throws InterfaceException {
		return getCustomerInterfaceService().fetchCustomerDetails(customer);
	}

	/**
	 * 
	 * @param customer
	 * @return
	 * @throws CustomerNotFoundException
	 */
	@Override
	public void updateProspectCustomer(Customer customer) {
		getCustomerDAO().updateProspectCustomer(customer);
	}

	/**
	 * Fetch Core Banking Customer details
	 */
	@Override
	public Customer fetchCustomerDetails(Customer customer) {

		if (StringUtils.isNotBlank(customer.getCustTypeCode())) {
			CustomerType customerType = getCustomerTypeDAO().getCustomerTypeById(customer.getCustTypeCode(), "_Aview");
			if (customerType != null) {
				customer.setLovDescCustTypeCodeName(customerType.getCustTypeDesc());
				customer.setLovDescCustCtgType(customerType.getCustTypeCtg());
			}
		}
		if (StringUtils.isNotBlank(customer.getCustDftBranch())) {
			Branch branch = getBranchDAO().getBranchById(customer.getCustDftBranch(), "_Aview");
			if (branch != null) {
				customer.setLovDescCustDftBranchName(branch.getBranchDesc());
			}
		}
		if (StringUtils.isNotBlank(customer.getCustParentCountry())) {
			Country country = getCountryDAO().getCountryById(customer.getCustParentCountry(), "_Aview");
			if (country != null) {
				customer.setLovDescCustParentCountryName(country.getCountryDesc());
				customer.setLovDescCustCOBName(country.getCountryDesc());
			}
		}
		if (StringUtils.isNotBlank(customer.getCustRiskCountry())) {
			Country country = getCountryDAO().getCountryById(customer.getCustRiskCountry(), "_Aview");
			if (country != null) {
				customer.setLovDescCustRiskCountryName(country.getCountryDesc());
				customer.setLovDescCustResdCountryName(country.getCountryDesc());
			}
		}
		if (StringUtils.isNotBlank(customer.getCustNationality())) {
			NationalityCode nationality = getNationalityCodeDAO().getNationalityCodeById(customer.getCustNationality(),
					"_Aview");
			if (nationality != null) {
				customer.setLovDescCustNationalityName(nationality.getNationalityDesc());
			}
		}
		if (StringUtils.isNotBlank(customer.getCustEmpSts())) {
			EmpStsCode empStsCode = getEmpStsCodeDAO().getEmpStsCodeById(customer.getCustEmpSts(), "_Aview");
			if (empStsCode != null) {
				customer.setLovDescCustEmpStsName(empStsCode.getEmpStsDesc());
			}
		}

		// Sector
		if (StringUtils.isNotBlank(customer.getCustSector())) {
			Sector sector = getSectorDAO().getSectorById(customer.getCustSector(), "_Aview");
			if (sector != null) {
				customer.setLovDescCustSectorName(sector.getSectorDesc());
			}
			// Sub sector
			if (StringUtils.isNotBlank(customer.getCustSubSector())) {
				SubSector subSector = getSubSectorDAO().getSubSectorById(customer.getCustSector(),
						customer.getCustSubSector(), "_Aview");
				if (subSector != null) {
					customer.setLovDescCustSubSectorName(subSector.getSubSectorDesc());
				}
			}
		}

		if (StringUtils.isNotBlank(customer.getLovDescCustGroupCode())) {
			CustomerGroup custGroup = getCustomerGroupDAO().getCustomerGroupByID(customer.getCustGroupID(), "_AView");
			if (custGroup != null) {
				customer.setCustGroupID(custGroup.getCustGrpID());
				customer.setLovDesccustGroupIDName(custGroup.getCustGrpDesc());
				customer.setLovDescCustGroupCode(custGroup.getCustGrpCode());
			}
		}
		if (StringUtils.isNotBlank(customer.getLovDescCustRO1Name())) {
			RelationshipOfficer officer = getRelationshipOfficerDAO()
					.getRelationshipOfficerById(String.valueOf(customer.getCustRO1()), "_Aview");
			if (officer != null) {
				customer.setCustRO1(Long.parseLong(officer.getROfficerCode()));
				customer.setLovDescCustRO1Name(officer.getROfficerDesc());
			}
		}
		if (StringUtils.isNotBlank(customer.getLovDescCustRO2Name())) {
			RelationshipOfficer officer = getRelationshipOfficerDAO().getRelationshipOfficerById(customer.getCustRO2(),
					"_Aview");
			if (officer != null) {
				customer.setCustRO2(officer.getROfficerCode());
				customer.setLovDescCustRO2Name(officer.getROfficerDesc());
			}
		}

		return customer;
	}

	@Override
	public CustomerCategory getCustomerCategoryById(String custCtgCode) {
		return getCustomerCategoryDAO().getCustomerCategoryById(custCtgCode, "_Aview");
	}

	/**
	 * Method for Fetching Past Due Details By CustID
	 */
	@Override
	public AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy) {
		return getCustomerDAO().getCustPastDueDetailByCustId(pastDue, limitCcy);
	}

	/**
	 * @return the customer
	 */
	@Override
	public Customer getCustomerForPostings(long custId) {
		return getCustomerDAO().getCustomerForPostings(custId);
	}

	@Override
	public String getNewProspectCustomerCIF() {
		return getCustomerDAO().getNewProspectCustomerCIF();
	}

	@Override
	public CustomerStatusCode getCustStatusByMinDueDays() {
		return getCustomerStatusCodeDAO().getCustStatusByMinDueDays("");
	}

	@Override
	public DirectorDetail getNewDirectorDetail() {
		return getDirectorDetailDAO().getNewDirectorDetail();
	}

	public void setEmpStsCodeDAO(EmpStsCodeDAO empStsCodeDAO) {
		this.empStsCodeDAO = empStsCodeDAO;
	}

	public EmpStsCodeDAO getEmpStsCodeDAO() {
		return empStsCodeDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setSectorDAO(SectorDAO sectorDAO) {
		this.sectorDAO = sectorDAO;
	}

	public SectorDAO getSectorDAO() {
		return sectorDAO;
	}

	public void setSubSectorDAO(SubSectorDAO subSectorDAO) {
		this.subSectorDAO = subSectorDAO;
	}

	public SubSectorDAO getSubSectorDAO() {
		return subSectorDAO;
	}

	public void setCustomerCategoryDAO(CustomerCategoryDAO customerCategoryDAO) {
		this.customerCategoryDAO = customerCategoryDAO;
	}

	public CustomerCategoryDAO getCustomerCategoryDAO() {
		return customerCategoryDAO;
	}

	public CustomerGroupDAO getCustomerGroupDAO() {
		return customerGroupDAO;
	}

	public void setCustomerGroupDAO(CustomerGroupDAO customerGroupDAO) {
		this.customerGroupDAO = customerGroupDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setRelationshipOfficerDAO(RelationshipOfficerDAO relationshipOfficerDAO) {
		this.relationshipOfficerDAO = relationshipOfficerDAO;
	}

	public RelationshipOfficerDAO getRelationshipOfficerDAO() {
		return relationshipOfficerDAO;
	}

	public void setNationalityCodeDAO(NationalityCodeDAO nationalityCodeDAO) {
		this.nationalityCodeDAO = nationalityCodeDAO;
	}

	public NationalityCodeDAO getNationalityCodeDAO() {
		return nationalityCodeDAO;
	}

	public RatingCodeDAO getRatingCodeDAO() {
		return ratingCodeDAO;
	}

	public void setRatingCodeDAO(RatingCodeDAO ratingCodeDAO) {
		this.ratingCodeDAO = ratingCodeDAO;
	}

	@Override
	public CustomerDetails setCustomerDetails(CustomerDetails customer) {
		logger.debug(Literal.ENTERING);
		if (customer != null && customer.getRatingsList() != null && !customer.getRatingsList().isEmpty()) {
			for (CustomerRating customerRating : customer.getRatingsList()) {
				try {
					if (StringUtils.isNotBlank(customerRating.getCustRatingType())
							&& StringUtils.isNotBlank(customerRating.getCustRatingCode())) {
						RatingCode countryRating = getRatingCodeDAO().getRatingCodeById(
								customerRating.getCustRatingType(), customerRating.getCustRatingCode(), "");
						if (countryRating != null) {
							customerRating.setLovDesccustRatingCodeDesc(countryRating.getRatingCodeDesc());
						}
					}
					if (StringUtils.isNotBlank(customerRating.getCustRatingType())
							&& StringUtils.isNotBlank(customerRating.getCustRating())) {
						RatingCode obligotrating = getRatingCodeDAO().getRatingCodeById(
								customerRating.getCustRatingType(), customerRating.getCustRating(), "");
						if (obligotrating != null) {
							customerRating.setLovDescCustRatingName(obligotrating.getRatingCodeDesc());
						}
					}
				} catch (Exception e) {
					logger.debug(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return customer;
	}

	private List<AuditDetail> saveOrUpdateDedupDetails(CustomerDetails customerDetails) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		long lastmntby = customerDetails.getCustomer().getLastMntBy();
		String roleCode = customerDetails.getCustomer().getRoleCode();
		String recordSts = customerDetails.getCustomer().getRecordStatus();

		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
			if (customerDetails.getCustomerDedupList() != null && !customerDetails.getCustomerDedupList().isEmpty()) {

				List<CustomerDedup> insertList = new ArrayList<CustomerDedup>();
				List<CustomerDedup> updateList = new ArrayList<CustomerDedup>();

				CustomerDedup deDupCustomer = new CustomerDedup();
				String[] blFields = PennantJavaUtil.getFieldDetails(deDupCustomer, deDupCustomer.getExcludeFields());

				for (int i = 0; i < customerDetails.getCustomerDedupList().size(); i++) {

					deDupCustomer = customerDetails.getCustomerDedupList().get(i);
					deDupCustomer.setFinReference(deDupCustomer.getCustCIF());
					deDupCustomer.setLastMntBy(lastmntby);
					deDupCustomer.setRoleCode(roleCode);
					deDupCustomer.setRecordStatus(recordSts);
					if (!deDupCustomer.isNewCustDedupRecord()) {
						updateList.add(deDupCustomer);
					} else {
						insertList.add(deDupCustomer);
					}

					auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i + 1, blFields[0], blFields[1], null,
							deDupCustomer));
				}

				if (!insertList.isEmpty()) {
					getCustomerDedupDAO().saveList(insertList, "");
				}
				if (!updateList.isEmpty()) {
					getCustomerDedupDAO().updateList(updateList);
				}

				deDupCustomer = null;
				insertList = null;
				updateList = null;
			}
		}
		return auditDetails;
	}

	@Override
	public List<CustomerRating> getCustomerRatingByCustId(long id, String type) {
		return getCustomerRatingDAO().getCustomerRatingByCustId(id, type);
	}

	@Override
	public String getEIDNumberById(String eidNumber, String type) {
		return getCustomerDAO().getCustomerByCRCPR(eidNumber, type);
	}

	public CustomerDedupDAO getCustomerDedupDAO() {
		return customerDedupDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		this.customerDedupDAO = customerDedupDAO;
	}

	public CoreCustomerDAO getCoreCustomerDAO() {
		return coreCustomerDAO;
	}

	public void setCoreCustomerDAO(CoreCustomerDAO coreCustomerDAO) {
		this.coreCustomerDAO = coreCustomerDAO;
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

	@Override
	public long getEIDNumberByCustId(String eidNumber, String type) {
		return getCustomerDAO().getCustCRCPRByCustId(eidNumber, type);
	}

	@Override
	public WIFCustomer getWIFByEIDNumber(String eidNumber, String type) {
		return getCustomerDAO().getWIFByCustCRCPR(eidNumber, type);
	}

	@Override
	public boolean isDuplicateCrcpr(long custId, String custCRCPR) {
		return customerDAO.isDuplicateCrcpr(custId, custCRCPR);
	}

	@Override
	public int updateCustCRCPR(String custDocTitle, long custID) {
		return customerDAO.updateCustCRCPR(custDocTitle, custID);
	}

	@Override
	public void updateProspectCustCIF(String oldCustCIF, String newCustCIF) {
		logger.debug(Literal.ENTERING);
		getCustomerDAO().updateProspectCustCIF(oldCustCIF, newCustCIF);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public String getCustCoreBankIdByCIF(String custCIF) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return getCustomerDAO().getCustCoreBankIdByCIF(custCIF);
	}

	@Override
	public String getNewCoreCustomerCIF() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return getCustomerDAO().getNewCoreCustomerCIF();
	}

	@Override
	public void updateCorebankCustCIF(String coreCustCIF) {
		logger.debug(Literal.ENTERING);

		getCustomerDAO().updateCorebankCustCIF(coreCustCIF);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * 
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingCustomerGSTList(List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerGST customerGST = (CustomerGST) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerGST.setRoleCode("");
				customerGST.setNextRoleCode("");
				customerGST.setTaskId("");
				customerGST.setNextTaskId("");
			}
			customerGST.setWorkflowId(0);
			customerGST.setCustId(custId);

			if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerGST.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerGST.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerGST.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerGST.setRecordType(PennantConstants.RCD_UPD);
				}

			} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerGST.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerGST.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerGST.getRecordType();
				recordStatus = customerGST.getRecordStatus();
				customerGST.setRecordType("");
				customerGST.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerGstDetailDAO.save(customerGST, type);
			}

			if (updateRecord) {
				customerGstDetailDAO.update(customerGST, type);
			}

			if (deleteRecord) {
				customerGstDetailDAO.delete(customerGST, type);
			}

			if (approveRec) {
				customerGST.setRecordType(rcdType);
				customerGST.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerGST);

			// Bank Info Details
			List<AuditDetail> details = customerGST.getAuditDetailMap().get("CustomerGSTDetails");
			if (details != null) {
				details = processingCustomerGstDetailList(details, type, customerGST.getId());
			}

		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Bank Information Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingCustomerGstDetailList(List<AuditDetail> auditDetails, String type, long id) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerGSTDetails customerGSTDetails = (CustomerGSTDetails) auditDetails.get(i).getModelData();
			customerGSTDetails.setHeaderId(id);

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerGSTDetails.setRoleCode("");
				customerGSTDetails.setNextRoleCode("");
				customerGSTDetails.setTaskId("");
				customerGSTDetails.setNextTaskId("");
			}

			customerGSTDetails.setWorkflowId(0);

			if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (customerGSTDetails.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerGSTDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerGSTDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerGSTDetails.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = customerGSTDetails.getRecordType();
				recordStatus = customerGSTDetails.getRecordStatus();
				customerGSTDetails.setRecordType("");
				customerGSTDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerGstDetailDAO.save(customerGSTDetails, type);
			}

			if (updateRecord) {
				customerGstDetailDAO.update(customerGSTDetails, type);
			}

			if (deleteRecord) {
				customerGstDetailDAO.delete(customerGSTDetails, type);
			}

			if (approveRec) {
				customerGSTDetails.setRecordType(rcdType);
				customerGSTDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerGSTDetails);
		}
		return auditDetails;
	}

	@Override
	public String processPerfiosReport(PerfiosTransaction perfiosTransaction) {
		logger.debug(Literal.ENTERING);

		String response = "";
		try {
			PerfiosHeader perfiosHeader = new PerfiosHeader();
			if (perfiosTransaction != null) {

				String status = perfiosTransaction.getStatus();

				if (StringUtils.equals(status, "COMPLETED")) {
					perfiosTransaction.setStatus("S");
				} else {
					perfiosTransaction.setStatus("E");
				}
				perfiosTransaction.setStatusDesc(status);

				response = perfiosTransactionDAO.updatePerfiosStatus(perfiosTransaction);

				if (StringUtils.equals(status, "COMPLETED")) {
					String transationId = perfiosTransaction.getTransationId();
					perfiosHeader.setTransactionId(transationId);
					processPerfiosDocumentAndBankInfoDetails(transationId);
				}
			}

			logger.debug(Literal.LEAVING);
			return response;
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		return response;
	}

	@Override
	public PerfiosHeader processPerfiosDocumentAndBankInfoDetails(String transationId) {
		logger.debug(Literal.ENTERING);

		PerfiosHeader perfiosHeader = null;
		try {
			if (perfiosService != null) {
				perfiosHeader = perfiosService.getPerfiosReport(transationId);

				if (StringUtils.equalsIgnoreCase(perfiosHeader.getStatusCode(), "S") && perfiosHeader != null) {
					List<CustomerBankInfo> custBankinfo = perfiosHeader.getCustBankInfoList();

					Map<String, Object> mapValues = perfiosService.fetchCustomerBankInfoId(transationId);
					long bankId = NumberUtils.toLong(mapValues.get("bankid").toString());

					if (CollectionUtils.isNotEmpty(custBankinfo)) {
						for (CustomerBankInfo customerBankInfo : custBankinfo) {
							List<BankInfoDetail> bankInfoDetails = customerBankInfo.getBankInfoDetails();
							if (CollectionUtils.isNotEmpty(bankInfoDetails)) {
								for (BankInfoDetail bankInfoDetail : bankInfoDetails) {
									String recordType = bankInfoDetail.getRecordType();
									bankInfoDetail.setRecordType("");
									bankInfoDetail.setBankId(bankId);
									customerBankInfoDAO.save(bankInfoDetail, "");
									bankInfoDetail.setRecordType(recordType);
									processingBankInfoSubDetailList(bankInfoDetail, "", bankId);
								}
							}
						}
					}

					if (StringUtils.equalsIgnoreCase(perfiosHeader.getStatusCode(), "S")
							&& perfiosHeader.getDocImage() != null) {
						long docRefId = 0;
						DocumentDetails documentDetails = new DocumentDetails();
						DMSQueue dmsQueue = new DMSQueue();

						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(perfiosHeader.getDocImage());
						docRefId = documentManagerDAO.save(documentManager);

						dmsQueue.setDocManagerID(docRefId);
						dmsQueue.setModule(DMSModule.FINANCE);
						dmsQueue.setSubModule(DMSModule.FINANCE);
						dmsQueue.setDocCategory(DocumentCategories.getType("PERFIOS").getKey());
						dmsQueue.setFinReference(mapValues.get("finreference").toString());
						dmsQueue.setReference(perfiosHeader.getTransactionId());
						dmsQueue.setDocName(perfiosHeader.getDocName());
						dmsQueue.setCustCif(perfiosHeader.getCustomerCIF());
						dmsQueue.setCreatedOn(DateUtility.getTimestamp(SysParamUtil.getAppDate()));
						dmsQueue.setOfferId(perfiosHeader.getOfferId());
						dmsQueue.setApplicationNo(perfiosHeader.getApplicationNo());
						if (SessionUserDetails.getLogiedInUser() != null) {
							dmsQueue.setCreatedBy(SessionUserDetails.getLogiedInUser().getUserId());
						} else {
							dmsQueue.setCreatedBy(1000);
						}

						documentDetails.setDocModule(FinanceConstants.MODULE_NAME);
						documentDetails.setDocCategory(DocumentCategories.getType("PERFIOS").getKey());
						documentDetails.setReferenceId(mapValues.get("finreference").toString());
						documentDetails.setDocRefId(docRefId);

						if (StringUtils.contains(perfiosHeader.getDocName(), ".")) {
							String[] docName = StringUtils.split(perfiosHeader.getDocName(), ".");
							documentDetails.setDocName(docName[0]);
							documentDetails.setDoctype(docName[1]);
							dmsQueue.setDocExt(docName[1]);
							dmsQueue.setDocType(docName[1]);
						} else {
							documentDetails.setDocName(perfiosHeader.getDocName());
						}

						documentDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						documentDetails.setWorkflowId(0);
						documentDetails.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);

						if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))
								|| DMSStorage.EXTERNAL == DMSStorage
										.getStorage(App.getProperty(DMSProperties.STORAGE))) {
							dMSQueueDAO.log(dmsQueue);
						}
						documentDetailsDAO.save(documentDetails, "");

						perfiosHeader.setDocRefId(docRefId);
					}
					perfiosService.updatePerfiosHeader(perfiosHeader);
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

		return perfiosHeader;
	}

	@Override
	public CustomerDetails getCustomerDetails(long id, String type, boolean extDtsReq) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);

		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));

		ExtendedFieldRender extendedFieldRender = extendedFieldDetailsService.getExtendedFieldRender(
				ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustomer().getCustCtgCode(),
				customerDetails.getCustomer().getCustCIF());

		customerDetails.setExtendedFieldRender(extendedFieldRender);

		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	// Add below code for validating external documents
	private AuditDetail validateExternalDocuments(CustomerBankInfo custBankInfo, AuditDetail auditDetail) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(custBankInfo.getExternalDocuments())) {
			ErrorDetail errorDetail = new ErrorDetail();
			if (StringUtils.isBlank(custBankInfo.getPerfiosTransId())) {
				String[] valueParm = new String[2];
				valueParm[0] = "customerBankInfo : perfiosTransactionId ";
				//valueParm[1] = "Zero";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}

			if (StringUtils.isBlank(custBankInfo.getTransactionId())) {
				String[] valueParm = new String[2];
				valueParm[0] = "customerBankInfo : TransactionId ";
				//valueParm[1] = "Zero";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
			for (ExternalDocument detail : custBankInfo.getExternalDocuments()) {

				if (StringUtils.isNotBlank(detail.getDocType())
						&& PennantConstants.DOC_TYPE_EXCEL.equals(detail.getDocType()) && !detail.isBankReport()) {
					String[] valueParm = new String[2];
					valueParm[0] = "ExternalDocument : bankReport is true for Reports";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30550", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (!detail.isBankReport()) {
					if (StringUtils.isBlank(detail.getPasswordProtected())) {
						String[] valueParm = new String[2];
						valueParm[0] = "ExternalDocument : PasswordProtected ";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					} else {
						if (StringUtils.equals(detail.getPasswordProtected(), "P")
								&& StringUtils.isBlank(detail.getPassword())) {
							String[] valueParm = new String[2];
							valueParm[0] = "ExternalDocument : password ";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}
					if (detail.getFromDate() == null) {
						String[] valueParm = new String[2];
						valueParm[0] = "ExternalDocument : fromDate ";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}

					if (detail.getToDate() == null) {
						String[] valueParm = new String[2];
						valueParm[0] = "ExternalDocument : toDate ";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
				if (detail.getDocImage() == null && detail.getDocImage().length == 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ExternalDocument : DocImage ";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}

				String docName = java.util.Objects.toString(detail.getDocName(), "").toLowerCase();
				if (StringUtils.isNotBlank(detail.getDocType())) {
					String custDoc = detail.getDocType();
					if (!(PennantConstants.DOC_TYPE_PDF.equals(custDoc) || PennantConstants.DOC_TYPE_DOC.equals(custDoc)
							|| PennantConstants.DOC_TYPE_DOCX.equals(custDoc)
							|| PennantConstants.DOC_TYPE_IMAGE.equals(custDoc)
							|| PennantConstants.DOC_TYPE_ZIP.equals(custDoc)
							|| PennantConstants.DOC_TYPE_7Z.equals(custDoc)
							|| PennantConstants.DOC_TYPE_RAR.equals(custDoc)
							|| PennantConstants.DOC_TYPE_EXCEL.equals(custDoc)
							|| PennantConstants.DOC_TYPE_TXT.equals(custDoc))) {
						String[] valueParm = new String[1];
						valueParm[0] = "ExternalDocuments : document type: " + detail.getDocType();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90122", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}

				if (StringUtils.isNotBlank(docName)) {
					boolean isImage = false;
					if (StringUtils.equals(detail.getDocType(), PennantConstants.DOC_TYPE_IMAGE)) {
						isImage = true;
						if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")) {
							String[] valueParm = new String[2];
							valueParm[0] = "ExternalDocuments : document type: " + detail.getDocName();
							valueParm[1] = detail.getDocType();
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}

					//if docName has no extension.
					if (!docName.contains(".")) {
						String[] valueParm = new String[1];
						valueParm[0] = "ExternalDocuments : document Name: " + docName;
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					} else {
						// document name is only extension
						String docNameExtension = docName.substring(docName.lastIndexOf("."));
						if (StringUtils.equalsIgnoreCase(detail.getDocName(), docNameExtension)) {
							String[] valueParm = new String[1];
							valueParm[0] = "docName: ";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}
					String docExtension = docName.substring(docName.lastIndexOf(".") + 1);
					//if doc type and doc Extension are invalid
					if (!isImage) {
						if (StringUtils.equalsIgnoreCase(detail.getDocType(), PennantConstants.DOC_TYPE_EXCEL)) {
							String docExtention = detail.getDocName().toLowerCase();
							if (!docExtention.endsWith(".xls") && !docExtention.endsWith(".xlsx")) {
								String[] valueParm = new String[2];
								valueParm[0] = "ExternalDocuments : document Type: " + docName;
								valueParm[1] = detail.getDocType();
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN");
								auditDetail.setErrorDetail(errorDetail);
							}
						} else {
							if (!StringUtils.equalsIgnoreCase(detail.getDocType(), docExtension)) {
								String[] valueParm = new String[2];
								valueParm[0] = "ExternalDocuments : document Type: " + detail.getDocName();
								valueParm[1] = detail.getDocType();
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN");
								auditDetail.setErrorDetail(errorDetail);
							}
						}
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public int getCustomerCountByCIF(String custCIF, String type) {
		return getCustomerDAO().getCustomerCountByCIF(custCIF, type);
	}

	@Override
	public String getExternalCibilResponse(String cif, String tableName) {
		return customerDAO.getExternalCibilResponse(cif, tableName);
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}

	@Override
	public boolean getCustomerByCoreBankId(String custCoreBank) {
		return getCustomerDAO().getCustomerByCoreBankId(custCoreBank);
	}

	@Override
	public CustomerDetails getCustById(long id) {
		return getCustomerById(id, "_AView");
	}

	private void isDuplicateCRCPR(AuditDetail auditDetail, Customer customer, boolean categoryReq) {
		List<String> cifs;
		String[] errorParameters = new String[2];
		String custCtgCode = null;
		if (categoryReq) {
			custCtgCode = customer.getCustCtgCode();
		}
		cifs = customerDAO.isDuplicateCRCPR(customer.getCustID(), customer.getCustCRCPR(), custCtgCode);
		if (CollectionUtils.isNotEmpty(cifs)) {
			for (String detail : cifs) {
				if (!StringUtils.equals(customer.getCustCIF(), detail)) {
					errorParameters[0] = PennantJavaUtil.getLabel("label_CustCRCPR") + ":"
							+ PennantApplicationUtil.formatEIDNumber(customer.getCustCRCPR());
					errorParameters[1] = PennantJavaUtil.getLabel("label_Cif") + ": {" + detail + "}";

					auditDetail.setErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41018", errorParameters, null));
				}
			}
		}
	}

	public LovFieldDetailService getLovFieldDetailService() {
		return lovFieldDetailService;
	}

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public LimitRebuild getLimitRebuild() {
		return limitRebuild;
	}

	public void setLimitRebuild(LimitRebuild limitRebuild) {
		this.limitRebuild = limitRebuild;
	}

	public void setPhoneTypeDAO(PhoneTypeDAO phoneTypeDAO) {
		this.phoneTypeDAO = phoneTypeDAO;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public Customer getCustomerShrtName(long id) {
		return getCustomerDAO().getCustomerByID(id);
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public PinCodeDAO getPinCodeDAO() {
		return pinCodeDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public List<FinanceMain> getFinanceMainList() {
		return financeMainList;
	}

	public void setFinanceMainList(List<FinanceMain> financeMainList) {
		this.financeMainList = financeMainList;
	}

	public VASRecordingDAO getvASRecordingDAO() {
		return vASRecordingDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	@Override
	public Customer checkCustomerByID(long custID, String type) {
		return getCustomerDAO().checkCustomerByID(custID, type);
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

	public CustomerCardSalesValidation getCustomerCardSalesValidation() {
		if (customerCardSalesValidation == null) {
			this.customerCardSalesValidation = new CustomerCardSalesValidation(customerCardSalesInfoDAO);
		}
		return this.customerCardSalesValidation;
	}

	public void setCustomerCardSalesValidation(CustomerCardSalesValidation customerCardSalesValidation) {
		if (customerCardSalesValidation == null) {
			this.customerCardSalesValidation = new CustomerCardSalesValidation(customerCardSalesInfoDAO);
		}
		this.customerCardSalesValidation = customerCardSalesValidation;
	}

	public ApprovalStatusEnquiryService getApprovalStatusEnquiryService() {
		return approvalStatusEnquiryService;
	}

	public void setApprovalStatusEnquiryService(ApprovalStatusEnquiryService approvalStatusEnquiryService) {
		this.approvalStatusEnquiryService = approvalStatusEnquiryService;
	}

	@Override
	public String getEIDNumberById(String eidNumber, String custCtgCode, String type) {
		return getCustomerDAO().getCustomerByCRCPR(eidNumber, custCtgCode, type);
	}

	@Override
	public boolean isDuplicateCrcpr(long custId, String custCRCPR, String custCtgCode) {
		return customerDAO.isDuplicateCrcpr(custId, custCRCPR, custCtgCode);
	}

	private AuditDetail validateSalutationCode(Customer customer, AuditDetail auditDetail) {
		String custSalCode = customer.getCustSalutationCode();
		String custGendCode = customer.getCustGenderCode();
		if (custGendCode == null || custSalCode == null) {
			return auditDetail;
		}

		int salutationByCount = salutationDAO.getSalutationByCount(custSalCode, custGendCode);

		if (salutationByCount > 0) {
			return auditDetail;
		}

		ErrorDetail errorDetail = new ErrorDetail();
		String[] valueParm = new String[2];

		valueParm[0] = Labels.getLabel("label_DirectorDetailDialog_CustSalutationCode.value") + " " + custSalCode;
		valueParm[1] = Labels.getLabel("label_DirectorDetailDialog_CustGenderCode.value") + " " + custGendCode;

		errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90329", "", valueParm));
		auditDetail.setErrorDetail(errorDetail);

		return auditDetail;
	}

	public GenderDAO getGenderDAO() {
		return genderDAO;
	}

	public void setGenderDAO(GenderDAO genderDAO) {
		this.genderDAO = genderDAO;
	}

	public SalutationDAO getSalutationDAO() {
		return salutationDAO;
	}

	public void setSalutationDAO(SalutationDAO salutationDAO) {
		this.salutationDAO = salutationDAO;
	}

	@Override
	public int getCrifScorevalue(String tablename, String reference) {
		return customerDAO.getCrifScoreValue(tablename, reference);
	}

	public PrimaryAccountDAO getPrimaryAccountDAO() {
		return primaryAccountDAO;
	}

	@Autowired
	public void setPrimaryAccountDAO(PrimaryAccountDAO primaryAccountDAO) {
		this.primaryAccountDAO = primaryAccountDAO;
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	@Override
	public boolean isCrifDeroge(String tablename, String reference) {
		return customerDAO.isCrifDeroge(tablename, reference);
	}

	public void setPerfiosTransactionDAO(PerfiosTransactionDAO perfiosTransactionDAO) {
		this.perfiosTransactionDAO = perfiosTransactionDAO;
	}

	@Autowired(required = false)
	public void setPerfiosService(PerfiousService perfiosService) {
		this.perfiosService = perfiosService;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public CustTypePANMappingService getCustTypePANMappingService() {
		return custTypePANMappingService;
	}

	public void setCustTypePANMappingService(CustTypePANMappingService custTypePANMappingService) {
		this.custTypePANMappingService = custTypePANMappingService;
	}

	public void setdMSQueueDAO(DMSQueueDAO dMSQueueDAO) {
		this.dMSQueueDAO = dMSQueueDAO;
	}

}