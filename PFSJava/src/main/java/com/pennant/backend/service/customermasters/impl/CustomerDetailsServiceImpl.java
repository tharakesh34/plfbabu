package com.pennant.backend.service.customermasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.RatingCodeDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.customermasters.CoreCustomerDAO;
import com.pennant.backend.dao.customermasters.CorporateCustomerDetailDAO;
import com.pennant.backend.dao.customermasters.CustEmployeeDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerBalanceSheetDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.customermasters.CustomerGroupDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.customermasters.CustomerPRelationDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.rmtmasters.CustomerTypeDAO;
import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.EmpStsCodeDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.dao.systemmasters.NationalityCodeDAO;
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.dao.systemmasters.SectorDAO;
import com.pennant.backend.dao.systemmasters.SubSectorDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.customermasters.CoreCustomer;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
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
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.GCDCustomerService;
import com.pennant.backend.service.customermasters.validation.CorporateCustomerValidation;
import com.pennant.backend.service.customermasters.validation.CustomerAddressValidation;
import com.pennant.backend.service.customermasters.validation.CustomerBalanceSheetValidation;
import com.pennant.backend.service.customermasters.validation.CustomerBankInfoValidation;
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
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.rits.cloning.Cloner;

public class CustomerDetailsServiceImpl extends GenericService<Customer> implements CustomerDetailsService {
	private static final Logger logger = Logger.getLogger(CustomerDetailsServiceImpl.class);

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

	private CustomerBankInfoDAO customerBankInfoDAO;
	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private CustEmployeeDetailDAO custEmployeeDetailDAO;
	private CustomerDedupDAO customerDedupDAO;
	private CoreCustomerDAO coreCustomerDAO;
	private ProvinceDAO provinceDAO;
	private CityDAO cityDAO;
	private IncomeTypeDAO incomeTypeDAO;
	private CustomerDocumentService customerDocumentService;


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
	private CustomerExtLiabilityValidation customerExtLiabilityValidation;
	private LovFieldDetailService lovFieldDetailService;
	private BankDetailService	bankDetailService;
	private ExtendedFieldRenderDAO	extendedFieldRenderDAO;
	private LimitRebuild limitRebuild;
	private GCDCustomerService gCDCustomerService;
	private PhoneTypeDAO    phoneTypeDAO;
	

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

	public CustomerExtLiabilityDAO getCustomerExtLiabilityDAO() {
		return customerExtLiabilityDAO;
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

	public CustomerExtLiabilityValidation getCustomerExtLiabilityValidation() {

		if (customerExtLiabilityValidation == null) {
			this.customerExtLiabilityValidation = new CustomerExtLiabilityValidation(customerExtLiabilityDAO);
		}
		return this.customerExtLiabilityValidation;
	}

	/**
	 * @return the customer for New Record
	 */
	@Override
	public CustomerDetails getNewCustomer(boolean createNew) {
		CustomerDetails customerDetails = new CustomerDetails();
		CustomerEmploymentDetail employmentDetail = new CustomerEmploymentDetail();
		employmentDetail.setNewRecord(true);
		CorporateCustomerDetail corporateCustomerDetail = new CorporateCustomerDetail();
		corporateCustomerDetail.setNewRecord(true);
		customerDetails.setCustomer(getCustomerDAO().getNewCustomer(createNew));
		customerDetails.setRatingsList(new ArrayList<CustomerRating>());
		customerDetails.setEmploymentDetailsList(new ArrayList<CustomerEmploymentDetail>());
		customerDetails.setAddressList(new ArrayList<CustomerAddres>());
		customerDetails.setCustomerEMailList(new ArrayList<CustomerEMail>());
		customerDetails.setCustomerPhoneNumList(new ArrayList<CustomerPhoneNumber>());
		customerDetails.setCustomerIncomeList(new ArrayList<CustomerIncome>());
		customerDetails.setCustomerDocumentsList(new ArrayList<CustomerDocument>());
		customerDetails.setNewRecord(true);
		return customerDetails;
	}

	public CustomerDetails getCustomerDetailsbyIdandPhoneType(long id, String phoneType) {
		CustomerDetails customerDetails = new CustomerDetails();

		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, ""));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomerPhoneType(id,
				"", phoneType));
		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, ""));

		return customerDetails;
	}

	/**
	 * @return the customerDetails for the given customer id.
	 * */
	private CustomerDetails getCustomerById(long id, String type) {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);
		if (ImplementationConstants.ALLOW_CUSTOMER_RATINGS) {
			customerDetails.setRatingsList(customerRatingDAO.getCustomerRatingByCustomer(id, type));
		}
		if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			customerDetails.setEmploymentDetailsList(getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailsByID(
					id, type));
		} else {
			customerDetails.setCustEmployeeDetail(getCustEmployeeDetailDAO().getCustEmployeeDetailById(id, type));
		}
		if (ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
			customerDetails.setCustomerIncomeList(customerIncomeDAO.getCustomerIncomeByCustomer(id, false, type));
		}
		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
		if (ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS) {
			customerDetails.setCustomerDirectorList(directorDetailDAO.getCustomerDirectorByCustomer(id, type));
		}
		customerDetails.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));
		customerDetails.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));
		customerDetails.setCustomerExtLiabilityList(customerExtLiabilityDAO.getExtLiabilityByCustomer(id, type));
		customerDetails.setCustFinanceExposureList(getCustomerDAO().getCustomerFinanceDetailById(id));

		logger.debug("Leaving");
		return customerDetails;
	}

	/**
	 * @return the customerDetails for the given customer id.
	 * */
	private CustomerDetails getCustomerDetailsbyID(long id, boolean reqChildDetails, String type) {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);

		if (reqChildDetails) {
			if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
				customerDetails.setEmploymentDetailsList(getCustomerEmploymentDetailDAO()
						.getCustomerEmploymentDetailsByID(id, type));
			} else {
				customerDetails.setCustEmployeeDetail(getCustEmployeeDetailDAO().getCustEmployeeDetailById(id, type));
			}
			if (ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
				customerDetails.setCustomerIncomeList(getCustomerIncomeDAO().getCustomerIncomeByCustomer(id, false,
						type));
			}
			if (StringUtils.isNotEmpty(customerDetails.getCustomer().getCustCtgCode())
					&& StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),
							PennantConstants.PFF_CUSTCTG_INDIV)) {
				if (ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS) {
					customerDetails.setCustomerDirectorList(getDirectorDetailDAO().getCustomerDirectorByCustomer(id,
							type));
				}
				if (ImplementationConstants.ALLOW_CUSTOMER_RATINGS) {
					customerDetails.setRatingsList(getCustomerRatingDAO().getCustomerRatingByCustomer(id, type));
				}
			}
			customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
			customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
			customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
			customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
			customerDetails.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));
			customerDetails.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));
			customerDetails.setCustomerExtLiabilityList(customerExtLiabilityDAO.getExtLiabilityByCustomer(id, type));
			customerDetails.setCustFinanceExposureList(getCustomerDAO().getCustomerFinanceDetailById(id));
		}

		logger.debug("Leaving");
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
	public Customer checkCustomerByCIF(String cif,String type) {
		return getCustomerDAO().checkCustomerByCIF(cif, type);
	}

	/**
	 * getApprovedCustomerById fetch the details by using CustomerDAO's getCustomerById method . with parameter id and
	 * type as blank. it fetches the approved records from the Customers.
	 * 
	 * @param id
	 *            (String)
	 * @return Customer
	 */
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
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
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
			CustEmployeeDetail custEmployeeDetail = getCustEmployeeDetailDAO().getCustEmployeeDetailById(
					customer.getCustID(), tableType);
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
		if (customerDetails.getEmploymentDetailsList() != null && customerDetails.getEmploymentDetailsList().size() > 0) {
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

		if (customerDetails.getCustomerDocumentsList() != null && customerDetails.getCustomerDocumentsList().size() > 0) {
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

		auditDetails.addAll(saveOrUpdateDedupDetails(customerDetails));

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], customer
				.getBefImage(), customer));
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);

		// TODO:remove comments for below lines of code when MDM interface is ready for update customer service
		// update core customer
		/*
		 * if(!StringUtils.isBlank(customer.getCustCoreBank())) { processUpdateCustData(customerDetails); }
		 */

		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType) throws InterfaceException {
		logger.debug("Entering");
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
		if(financeDetail.getCustomerDetails().getCustomer().isDedupFound()){
			getCustomerDAO().updateProspectCustomer(customer);
		}
		String[] fields = PennantJavaUtil.getFieldDetails(customer, customer.getExcludeFields());
		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1], customer
				.getBefImage(), customer));

		if (customerDetails.getCustEmployeeDetail() != null) {
			CustEmployeeDetail custEmployeeDetail = getCustEmployeeDetailDAO().getCustEmployeeDetailById(
					customer.getCustID(), tableType);
			CustEmployeeDetail custEmpDetail = customerDetails.getCustEmployeeDetail();
			custEmpDetail.setWorkflowId(0);
			custEmpDetail.setCustID(customer.getCustID());
			custEmpDetail.setRecordType(customer.getRecordType());
			custEmpDetail.setRecordStatus(customer.getRecordStatus());
			custEmpDetail.setBefImage(custEmployeeDetail);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(cusEmploymentDetail.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					cusEmploymentDetail.setRecordType("");
					cusEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(cusEmploymentDetail.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(customerRating.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					customerRating.setRecordType("");
					customerRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerRating.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
				customerIncome.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(customerIncome.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					customerIncome.setRecordType("");
					customerIncome.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerIncome.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerIncomeDAO.delete(customerIncome, tableType);
				} else if (customerIncome.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerIncomeDAO.save(customerIncome, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerIncomeDAO.update(customerIncome, tableType);
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(directorDetail.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					directorDetail.setRecordType("");
					directorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(directorDetail.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					customerDocument.setRecordType("");
					customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerDocumentDAO.delete(customerDocument, tableType);
				} else if (customerDocument.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					if (customerDocument.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(customerDocument.getCustDocImage());
						customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					customerDocumentDAO.save(customerDocument, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					if (customerDocument.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(customerDocument.getCustDocImage());
						customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(custaddress.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					custaddress.setRecordType("");
					custaddress.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custaddress.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(custPhoneNumber.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					custPhoneNumber.setRecordType("");
					custPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custPhoneNumber.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(customerEMail.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					customerEMail.setRecordType("");
					customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerEMail.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(custBankInfo.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					custBankInfo.setRecordType("");
					custBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custBankInfo.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerBankInfoDAO.delete(custBankInfo, tableType);
				} else if (custBankInfo.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerBankInfoDAO.save(custBankInfo, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerBankInfoDAO.update(custBankInfo, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(custBankInfo, custBankInfo.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custBankInfo.getBefImage(), custBankInfo));
			}
		}

		if (customerDetails.getCustomerChequeInfoList() != null) {
			for (CustomerChequeInfo custChequeInfo : customerDetails.getCustomerChequeInfoList()) {
				if (StringUtils.isBlank(custChequeInfo.getRecordType())) {
					continue;
				}
				custChequeInfo.setWorkflowId(0);
				custChequeInfo.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(custChequeInfo.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					custChequeInfo.setRecordType("");
					custChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custChequeInfo.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
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
			for (CustomerExtLiability custExtLiability : customerDetails.getCustomerExtLiabilityList()) {
				if (StringUtils.isBlank(custExtLiability.getRecordType())) {
					continue;
				}
				custExtLiability.setWorkflowId(0);
				custExtLiability.setCustID(customer.getCustID());
				if (StringUtils.isEmpty(tableType)
						&& !StringUtils.trimToEmpty(custExtLiability.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_DEL)) {
					custExtLiability.setRecordType("");
					custExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custExtLiability.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerExtLiabilityDAO.delete(custExtLiability, tableType);
				} else if (custExtLiability.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerExtLiabilityDAO.save(custExtLiability, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerExtLiabilityDAO.update(custExtLiability, tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(custExtLiability, custExtLiability.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						custExtLiability.getBefImage(), custExtLiability));

			}
		}

		// TODO:remove comments for below lines of code when MDM interface is ready for update customer service
		// update core customer
		/*
		 * if(!StringUtils.isBlank(customer.getCustCoreBank())) { processUpdateCustData(customerDetails); }
		 */

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(CustomerDetails customerDetails, long workflowId, String method,
			String usrLanguage) {
		return doValidation(customerDetails, workflowId, method, usrLanguage);
	}

	public List<AuditDetail> doValidation(CustomerDetails customerDetails, long workflowId, String method,
			String usrLanguage) {
		logger.debug("Entering");

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
			AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], customerDetails
					.getCustomer().getBefImage(), customerDetails);
			auditDetails.add(validation(auditDetail, usrLanguage, method));
		}

		auditDetails.addAll(getAuditDetail(customerDetails, auditTranType, method, workflowId));

		// Employment Validation
		if (customerDetails.getEmploymentDetailsList() != null && customerDetails.getEmploymentDetailsList().size() > 0) {
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
		if (customerDetails.getCustomerDocumentsList() != null && customerDetails.getCustomerDocumentsList().size() > 0) {
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
			details = getCustomerExtLiabilityValidation().extLiabilityListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		
		//customer dedup validation
		if (customerDetails.getCustomerDedupList() != null
				&& !customerDetails.getCustomerDedupList().isEmpty()) {
			for (CustomerDedup customerDedup : customerDetails.getCustomerDedupList()) {
				AuditDetail auditDetail = new AuditDetail();
				if (StringUtils.equals(customerDedup.getSourceSystem(), PennantConstants.CUSTOMER_DEDUP_SOURCE_SYSTEM_PENNANT)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("99012", null)));
					auditDetails.add(auditDetail);
				}
			}

		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getAuditDetail(CustomerDetails customerDetails, String auditTranType, String method,
			long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		if (customerDetails.getEmploymentDetailsList() != null && customerDetails.getEmploymentDetailsList().size() > 0) {
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

		if (customerDetails.getCustomerDocumentsList() != null && customerDetails.getCustomerDocumentsList().size() > 0) {
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
			auditDetailMap
					.put("CustomerExtLiability", setExtLiabilityAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerExtLiability"));
		}

		customerDetails.setAuditDetailMap(auditDetailMap);

		logger.debug("Leaving");
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
		logger.debug("Entering");

		AuditDetail auditDetail = auditHeader.getAuditDetail();
		CustomerDetails customerDetails = (CustomerDetails) auditDetail.getModelData();

		// validate basic details
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			auditDetail.setErrorDetail(validateMasterCode("CustomerCategory",customerDetails.getCustCtgCode()));
		}
		if (StringUtils.isNotBlank(customerDetails.getCustDftBranch())) {
			auditDetail.setErrorDetail(validateMasterCode("Branch",customerDetails.getCustDftBranch()));
		}
		if (StringUtils.isNotBlank(customerDetails.getCustBaseCcy())) {
			auditDetail.setErrorDetail(validateMasterCode("Currency", customerDetails.getCustBaseCcy()));
		}
		if (StringUtils.isNotBlank(customerDetails.getPrimaryRelationOfficer())) {
			auditDetail.setErrorDetail(validateMasterCode("RelationshipOfficer",customerDetails.getPrimaryRelationOfficer()));
		}

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetails errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getErrorCode())) {
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

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetails errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getErrorCode())) {
					return auditDetail;
				}
			}
		}

		// validate customer details
		auditDetail = validateCustomerDetails(auditDetail, customerDetails);

		if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
			for (ErrorDetails errDetail : auditDetail.getErrorDetails()) {
				if (StringUtils.isNotBlank(errDetail.getErrorCode())) {
					return auditDetail;
				}
			}
		}

		logger.debug("Leaving");

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
		logger.debug("Entering");

		// customer Employment details
		
		if (StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			List<CustomerEmploymentDetail> custEmpDetails = customerDetails.getEmploymentDetailsList();
			if (custEmpDetails != null) {
				for (CustomerEmploymentDetail empDetail : custEmpDetails) {
					auditDetail.setErrorDetail(validateMasterCode("EmployerDetail", String.valueOf(empDetail.getCustEmpName())));
					auditDetail.setErrorDetail(validateMasterCode("EmploymentType", empDetail.getCustEmpType()));
					auditDetail.setErrorDetail(validateMasterCode("GeneralDesignation", empDetail.getCustEmpDesg()));
					if (StringUtils.isNotBlank(empDetail.getCustEmpDept())) {
						auditDetail.setErrorDetail(validateMasterCode("GeneralDepartment", empDetail.getCustEmpDept()));
					}
					if(empDetail.getCustEmpTo() != null ){
						if (empDetail.getCustEmpFrom().compareTo(empDetail.getCustEmpTo()) > 0) {
							ErrorDetails errorDetail = new ErrorDetails();
							String[] valueParm = new String[2];
							valueParm[0] = "employment startDate:"+DateUtility.formatDate(empDetail.getCustEmpFrom(), PennantConstants.XMLDateFormat);
							valueParm[1] = "employment endDate:" +DateUtility.formatDate(empDetail.getCustEmpTo(), PennantConstants.XMLDateFormat);
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30568", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}	
						if (empDetail.getCustEmpTo().compareTo(DateUtility.getAppDate()) != -1 || SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(empDetail.getCustEmpTo()) >= 0) {
							ErrorDetails errorDetail = new ErrorDetails();
							String[] valueParm = new String[2];
							valueParm[0] = "employment endDate:" + DateUtility.formatDate(empDetail.getCustEmpTo(), PennantConstants.XMLDateFormat);
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90319", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					} else {
						empDetail.setCurrentEmployer(true);
					}
					if (empDetail.getCustEmpFrom() != null && empDetail.getCustEmpFrom().compareTo(DateUtility.getAppDate()) != -1 || SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(empDetail.getCustEmpFrom()) >= 0) {
						ErrorDetails errorDetail = new ErrorDetails();
						String[] valueParm = new String[2];
						valueParm[0] = "employment startDate:" + DateUtility.formatDate(empDetail.getCustEmpFrom(), PennantConstants.XMLDateFormat);
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90319", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					if (empDetail.getCustEmpFrom() != null && customerDetails.getCustomer() != null) {
						if (empDetail.getCustEmpFrom().before(customerDetails.getCustomer().getCustDOB())) {
							ErrorDetails errorDetail = new ErrorDetails();
							String[] valueParm = new String[1];
							valueParm[0] = "employment startDate";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90334", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
					
				}
			}
		} else {
			if (customerDetails.getEmploymentDetailsList() != null && customerDetails.getEmploymentDetailsList().size() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "employment";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			
		}

		// customer Address details
		List<CustomerAddres> custAddress = customerDetails.getAddressList();
		
		if (custAddress != null) {
			boolean isAddressPrority=false;
			ErrorDetails errorDetail = new ErrorDetails();
			for (CustomerAddres adress : custAddress) {
				auditDetail.setErrorDetail(validateMasterCode("AddressType",adress.getCustAddrType()));

				Province province = getProvinceDAO().getProvinceById(adress.getCustAddrCountry(),
						adress.getCustAddrProvince(), "");
				if (province == null) {
					String[] valueParm = new String[2];
					valueParm[0] = adress.getCustAddrProvince();
					valueParm[1] = adress.getCustAddrCountry();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				City city = getCityDAO().getCityById(adress.getCustAddrCountry(), adress.getCustAddrProvince(),
						adress.getCustAddrCity(), "");
				if (city == null) {
					String[] valueParm = new String[2];
					valueParm[0] = adress.getCustAddrCity();
					valueParm[1] = adress.getCustAddrProvince();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}

				if (!(adress.getCustAddrPriority() >= 1 && adress.getCustAddrPriority() <= 5)) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(adress.getCustAddrPriority());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90114", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if(adress.getCustAddrPriority() == Integer.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)){
					isAddressPrority = true;
				}
				int addressPriorityCount = 0;
				int addType = 0;
				for (CustomerAddres aAdress : custAddress) {
					if (aAdress.getCustAddrPriority() == adress.getCustAddrPriority()) {
						addressPriorityCount++;
						if (addressPriorityCount > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "Priority";
							valueParm[1] = String.valueOf(adress.getCustAddrPriority());
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30702", "", valueParm), "EN");
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("41001", "", valueParm), "EN");
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
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		} else {
			ErrorDetails errorDetail = new ErrorDetails();
			String[] valueParm = new String[2];
			valueParm[0] = "Address Details";
			valueParm[1] = "Address";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		// customer Phone details
		List<CustomerPhoneNumber> custPhones = customerDetails.getCustomerPhoneNumList();
		if (custPhones != null) {
			boolean isPhonePrority=false;
			ErrorDetails errorDetail = new ErrorDetails();
			for (CustomerPhoneNumber custPhoneDetail : custPhones) {
				//Validate Phone number
				String mobileNumber= custPhoneDetail.getPhoneNumber();
				PhoneType custPhoneType = phoneTypeDAO.getPhoneTypeById(custPhoneDetail.getPhoneTypeCode(), "");
				if(custPhoneType != null){
					String regex=custPhoneType.getPhoneTypeRegex();
					if(regex!=null){			
						if (!(mobileNumber.matches(regex))){
							String[] valueParm = new String[1];
							valueParm[0] = regex;
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90346", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;	
						}
					}
					
				}
				auditDetail.setErrorDetail(validateMasterCode("PhoneType",custPhoneDetail.getPhoneTypeCode()));
				if(!(custPhoneDetail.getPhoneTypePriority()>=1 && custPhoneDetail.getPhoneTypePriority()<=5)){
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(custPhoneDetail.getPhoneTypePriority());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90115", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if(custPhoneDetail.getPhoneTypePriority() == Integer.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)){
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90287", "", valueParm), "EN");
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("41001", "", valueParm), "EN");
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
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		} else {
			ErrorDetails errorDetail = new ErrorDetails();
			String[] valueParm = new String[2];
			valueParm[0] = "Phone Details";
			valueParm[1] = "Phone";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		// customer Email details
		List<CustomerEMail> custEmails = customerDetails.getCustomerEMailList();
		if (custEmails != null) {
			boolean isEmailPrority=false;
			ErrorDetails errorDetail = new ErrorDetails();
			for (CustomerEMail custEmail : custEmails) {
				auditDetail.setErrorDetail(validateMasterCode("EMailType",custEmail.getCustEMailTypeCode()));
				if (!(custEmail.getCustEMailPriority() >= 1 && custEmail.getCustEMailPriority() <= 5)) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(custEmail.getCustEMailPriority());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90110", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				boolean validRegex = EmailValidator.getInstance().isValid(custEmail.getCustEMail());

				if (!validRegex) {
					String[] valueParm = new String[1];
					valueParm[0] = custEmail.getCustEMail();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90237", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if(custEmail.getCustEMailPriority() == Integer.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)){
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90288", "", valueParm), "EN");
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
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("41001", "", valueParm), "EN");
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
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		}

		// customer income details
		List<CustomerIncome> custIncomes = customerDetails.getCustomerIncomeList();
		if (custIncomes != null) {
			for (CustomerIncome custIncome : custIncomes) {
				auditDetail.setErrorDetail(validateMasterCode("BMTIncomeTypes", "IncomeExpense",custIncome.getIncomeExpense()));
				auditDetail.setErrorDetail(validateMasterCode("BMTIncomeTypes", "Category", custIncome.getCategory()));
				auditDetail.setErrorDetail(validateMasterCode("IncomeType",custIncome.getCustIncomeType()));
				IncomeType incomeType=incomeTypeDAO.getIncomeTypeById(custIncome.getCustIncomeType(), custIncome.getIncomeExpense(), custIncome.getCategory(), "_AView");
				if (incomeType == null) {
					ErrorDetails errorDetail = new ErrorDetails();
					String[] valueParm = new String[2];
					valueParm[0] = custIncome.getLovDescCustCIF();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90113", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}
		}

		List<CustomerDocument> custDocuments = customerDetails.getCustomerDocumentsList();
		boolean panMandatory = false;
		if (custDocuments != null) {
			for (CustomerDocument custDocument : custDocuments) {
				if (StringUtils.equals(custDocument.getCustDocCategory(), "03")) {
					panMandatory = true;
				}
				AuditDetail auditDetail1 = customerDocumentService.validateCustomerDocuments(custDocument,customerDetails.getCustomer());
				if (auditDetail1 != null && auditDetail1.getErrorDetails() != null
						&& !auditDetail1.getErrorDetails().isEmpty()) {
					return auditDetail1;
				}
			}
		} 
		if(StringUtils.isBlank(customerDetails.getCustCIF()) && !panMandatory){
			String[] valueParm = new String[1];
			valueParm[0] = "PAN document";
			ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}
		
		// customer bank info details
		List<CustomerBankInfo> custBankDetails = customerDetails.getCustomerBankInfoList();
		if (custBankDetails != null) {
			ErrorDetails errorDetail = new ErrorDetails();
			for (CustomerBankInfo custBankInfo : custBankDetails) {
				auditDetail.setErrorDetail(validateMasterCode("BankDetail", custBankInfo.getBankName()));
			
				LovFieldDetail lovFieldDetail=getLovFieldDetailService().getApprovedLovFieldDetailById("ACC_TYPE",custBankInfo.getAccountType());
				if (lovFieldDetail == null) {
				
					String[] valueParm = new String[2];
					valueParm[0] = "Acctype";
					valueParm[1] = custBankInfo.getAccountType();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				//validate AccNumber length
				if(StringUtils.isNotBlank(custBankInfo.getBankName())){
					int accNoLength = bankDetailService.getAccNoLengthByCode(custBankInfo.getBankName());
					if(custBankInfo.getAccountNumber().length()!=accNoLength){
						String[] valueParm = new String[2];
						valueParm[0] = "AccountNumber";
						valueParm[1] = String.valueOf(accNoLength)+" characters";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30570", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			}
			
		}

		//
		List<CustomerExtLiability> customerExtLiabilityList = customerDetails.getCustomerExtLiabilityList();
		if (customerExtLiabilityList != null) {
			for (CustomerExtLiability customerExtLiability : customerExtLiabilityList) {
				if (customerExtLiability.getFinDate().compareTo(DateUtility.getAppDate()) >= 0 || SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(customerExtLiability.getFinDate()) >= 0){
					ErrorDetails errorDetail = new ErrorDetails();
					String[] valueParm = new String[3];
					valueParm[0] = "FinDate";
					valueParm[1] = DateUtility.formatDate(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
							PennantConstants.XMLDateFormat);
					valueParm[2] = DateUtility.formatDate(DateUtility.getAppDate(), PennantConstants.XMLDateFormat);
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm), "EN"); 
					auditDetail.setErrorDetail(errorDetail);
				}

				auditDetail.setErrorDetail(validateMasterCode("BankDetail", customerExtLiability.getBankName()));
				auditDetail.setErrorDetail(validateMasterCode("OtherBankFinanceType", customerExtLiability.getFinType()));
				auditDetail.setErrorDetail(validateMasterCode("CustomerStatusCode", customerExtLiability.getFinStatus()));
			}
		}
		logger.debug("Leaving");
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
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
			if (StringUtils.isBlank(customer.getCustFName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "firstName";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN"));
			}
			if (StringUtils.isBlank(customer.getCustLName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "lastName";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN"));
			}
			if (StringUtils.isBlank(customer.getCustSalutationCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "salutation";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN"));
			}
			if (StringUtils.isBlank(customer.getCustGenderCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = "gender";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN"));
			}
			if (StringUtils.isBlank(customer.getCustMaritalSts())) {
				String[] valueParm = new String[1];
				valueParm[0] = "maritalStatus";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90502", "", valueParm), "EN"));
			}

			auditDetail.setErrorDetail(validateMasterCode("Salutation",customer.getCustSalutationCode()));
			auditDetail.setErrorDetail(validateMasterCode("Gender", customer.getCustGenderCode()));
			auditDetail.setErrorDetail(validateMasterCode("MaritalStatusCode",customer.getCustMaritalSts()));
		}
		if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_CORP) ||
				StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)){
			if (StringUtils.isBlank(customer.getCustShrtName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "shortName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_CORP;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}	
			if (StringUtils.isNotBlank(customer.getCustFName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "firstName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			if (StringUtils.isNotBlank(customer.getCustLName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "lastName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			if (StringUtils.isNotBlank(customer.getCustMName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "middleName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			if (StringUtils.isNotBlank(customer.getCustSalutationCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "salutation";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			if (StringUtils.isNotBlank(customer.getCustMotherMaiden())) {
				String[] valueParm = new String[2];
				valueParm[0] = "motherName";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
/*			if (StringUtils.isNotBlank(customer.getCustNationality())) {
				String[] valueParm = new String[2];
				valueParm[0] = "nationality";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}*/
			if (StringUtils.isNotBlank(customer.getCustGenderCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = "gender";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			if (StringUtils.isNotBlank(customer.getCustMaritalSts())) {
				String[] valueParm = new String[2];
				valueParm[0] = "maritalStatus";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
			if (customer.getNoOfDependents() > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "numofDependents ";
				valueParm[1] = PennantConstants.PFF_CUSTCTG_INDIV;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90124", "", valueParm), "EN"));
			}
		}
		auditDetail.setErrorDetail(validateMasterCode("CustomerType", customer.getCustTypeCode()));
		
		// validate custTypeCode against the category code
		int custTypeCount = getCustomerTypeDAO().validateTypeAndCategory(customer.getCustTypeCode(), customer.getCustCtgCode());
		if(custTypeCount <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = customer.getCustTypeCode();
			valueParm[1] = customer.getCustCtgCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90108", "", valueParm), "EN"));
		}
		
		auditDetail.setErrorDetail(validateMasterCode("Sector", customer.getCustSector()));
		auditDetail.setErrorDetail(validateMasterCode("Industry", customer.getCustIndustry()));

		if (StringUtils.isNotBlank(customer.getCustLng())){
			auditDetail.setErrorDetail(validateMasterCode("BMTLanguage", "LngCode", customer.getCustLng()));
		}

		if (StringUtils.isNotBlank(customer.getCustCOB())){
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustCOB()));
		}

		if (StringUtils.isNotBlank(customer.getCustNationality())){
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustNationality()));
		}

		if (StringUtils.isNotBlank(customer.getCustSubSector())){
			auditDetail.setErrorDetail(validateMasterCode("SubSector", customer.getCustSubSector()));
		}

		if (StringUtils.isNotBlank(customer.getCustSegment())){
			auditDetail.setErrorDetail(validateMasterCode("Segment", customer.getCustSegment()));
		}

		if (StringUtils.isNotBlank(customer.getCustSubSegment())){
			auditDetail.setErrorDetail(validateMasterCode("SubSegment",customer.getCustSubSegment()));
		}

		if (StringUtils.isNotBlank(customer.getCustParentCountry() )){
			auditDetail.setErrorDetail(validateMasterCode("Country",customer.getCustParentCountry()));
		}

		if (StringUtils.isNotBlank(customer.getCustRiskCountry())){
			auditDetail.setErrorDetail(validateMasterCode("Country", customer.getCustRiskCountry()));
		}

		if (StringUtils.isNotBlank(customer.getCustEmpSts())){
			auditDetail.setErrorDetail(validateMasterCode("EmpStsCode", customer.getCustEmpSts()));
		}
		
		if (StringUtils.isNotBlank(customer.getCustDSADept())){
			auditDetail.setErrorDetail(validateMasterCode("Department", customer.getCustDSADept()));
		}
		if (StringUtils.isNotBlank(customer.getCustDSA())){
			Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(
					PennantRegularExpressions.REGEX_ALPHANUM));
			Matcher matcher = pattern.matcher(customer.getCustDSA());
			if (matcher.matches() == false) {
				ErrorDetails errorDetail = new ErrorDetails();
				String[] valueParm = new String[1];
				valueParm[0] = "saleAgent";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90347", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		if (customer.getCustGroupID()>0){
			auditDetail.setErrorDetail(validateMasterCode("CustomerGroup", String.valueOf(customer.getCustGroupID())));
		}
		if (StringUtils.isNotBlank(customer.getCustStaffID())){
			Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(
					PennantRegularExpressions.REGEX_ALPHANUM));
			Matcher matcher = pattern.matcher(customer.getCustStaffID());
			if (matcher.matches() == false) {
				ErrorDetails errorDetail = new ErrorDetails();
				String[] valueParm = new String[1];
				valueParm[0] = "staffID";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90347", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		if (customer.getCustDOB() != null && (customer.getCustDOB().compareTo(DateUtility.getAppDate()) >= 0 || SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(customer.getCustDOB()) >= 0)) {
			ErrorDetails errorDetail = new ErrorDetails();
			String[] valueParm = new String[3];
			valueParm[0] = "Date of Birth";
			valueParm[1] = DateUtility.formatDate(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
					PennantConstants.XMLDateFormat);
			valueParm[2] = DateUtility.formatDate(DateUtility.getAppDate(), PennantConstants.XMLDateFormat);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
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
	private ErrorDetails validateMasterCode(String moduleName,String fieldValue) {
		logger.debug("Entering");

		ErrorDetails errorDetail = new ErrorDetails();
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(moduleName);
		if(moduleMapping != null) {
			String[] lovFields = moduleMapping.getLovFields();
			String[][] filters = moduleMapping.getLovFilters();
			int count=0;
			if(filters !=null){
			 count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[0], filters[0][0], fieldValue);
			} 
			if(count <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = lovFields[0];
				valueParm[1] = fieldValue;
				errorDetail=ErrorUtil.getErrorDetail(new ErrorDetails("90224", "", valueParm));
			}
		}

		logger.debug("Leaving");
		return errorDetail;
	}
		private ErrorDetails validateMasterCode(String tableName, String columnName, String value) {
		logger.debug("Entering");

		ErrorDetails errorDetail = new ErrorDetails();

		// validate Master code with PLF system masters
		int count = getCustomerDAO().getLookupCount(tableName, columnName, value);
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = columnName;
			valueParm[1] = value;
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
		}

		logger.debug("Leaving");
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

	public AuditHeader delete(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "delete");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		auditDetails.addAll(getListAuditDetails(listDeletion(customerDetails, "", auditHeader.getAuditTranType())));

		getCustomerDAO().delete(customer, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], customer
				.getBefImage(), customer));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
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

		// Fetched from the approved list for rebuild. Since rebuild should after the transaction but it is happening in the transaction 
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
				// getCustomerInterfaceService().generateNewCIF("A", customer, "");
				getCustomerDAO().save(customer, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customer.setRecordType("");
				getCustomerDAO().update(customer, "");
			}

			customerDetails.setCustID(customer.getCustID());
			
			//process to send finone request and create or update the data.
				processFinOneCheck(aAuditHeader);

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

			auditDetails.addAll(saveOrUpdateDedupDetails(customerDetails));
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		if (!StringUtils.equals(customerDetails.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditDetailList.addAll(
					getListAuditDetails(listDeletion(customerDetails, "_Temp", auditHeader.getAuditTranType())));

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
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);

		processLimitRebuild(customer, appCustomer);

		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader processFinOneCheck(AuditHeader auditHeader) {
		logger.debug("Entering");

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		AuditDetail auditDetail = auditHeader.getAuditDetail();

		String[] errorParm = new String[2];
		errorParm[0] = "Customer";
		if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("GCD_FINONE_PROC_REQD"))) {
			if (!StringUtils.isEmpty(customerDetails.getCustomer().getCustCoreBank())) {
				// call the finone procedure to update a customer in Finone 
				getgCDCustomerService().processGcdCustomer(customerDetails, PennantConstants.CUSTOMER_DEDUP_UPDATE);
				if (StringUtils.equals(customerDetails.getGcdCustomer().getStatusFromFinnOne(),
						PennantConstants.CUSTOMER_DEDUP_REJECTED)) {
					errorParm[1] = customerDetails.getGcdCustomer().getRejectionReason();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "99014", errorParm, null),
							auditHeader.getUsrLanguage()));
					auditDetail.setErrorDetails(
							ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));
					auditHeader.setAuditDetail(auditDetail);
					auditHeader.setErrorList(auditDetail.getErrorDetails());
					return auditHeader;
				}

			} else {
				// call the finone procedure to create a customer in Finone 
				getgCDCustomerService().processGcdCustomer(customerDetails, PennantConstants.CUSTOMER_DEDUP_INSERT);
				if (StringUtils.equals(customerDetails.getGcdCustomer().getStatusFromFinnOne(),
						PennantConstants.CUSTOMER_DEDUP_REJECTED)) {
					errorParm[1] = customerDetails.getGcdCustomer().getRejectionReason();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "99014", errorParm, null),
							auditHeader.getUsrLanguage()));
					auditDetail.setErrorDetails(
							ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));
					auditHeader.setAuditDetail(auditDetail);
					auditHeader.setErrorList(auditDetail.getErrorDetails());
					return auditHeader;
				}
			}
		}

		logger.debug("Leaving");
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
			//new record
			if (isValid(customer.getCustGroupID())) {
				limitRebuild.processCustomerGroupRebuild(customer.getCustGroupID(), false, false);
			}
		}
	}

	private boolean isValid(Long vale){
		if (vale!=null && vale!=Long.MIN_VALUE && vale !=0) {
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

	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerDAO().delete(customer, "_Temp");
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(), customer.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], customer
				.getBefImage(), customer));
		auditDetails
				.addAll(getListAuditDetails(listDeletion(customerDetails, "_Temp", auditHeader.getAuditTranType())));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
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
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = customerDetails.getCustomer().getUserDetails().getUsrLanguage();
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
				auditDetails.addAll(getCustomerIncomeValidation().incomeListValidation(customerDetails, method,
						usrLanguage));
			}

			// Address Validation
			if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Address");
				details = getAddressValidation().addressListValidation(details, method, usrLanguage);
				auditDetails.addAll(details);
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
			details = getCustomerExtLiabilityValidation().extLiabilityListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
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

		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

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
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCustomer != null || tempCustomer != null) { // if records already exists in
						// the main table
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomer == null || tempCustomer != null) {
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
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
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCustomer != null && !oldCustomer.getLastMntOn().equals(befCustomer.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}
					}
				}

			} else {

				if (tempCustomer == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomer != null && oldCustomer != null
						&& !oldCustomer.getLastMntOn().equals(tempCustomer.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}
		}

		if (customerDAO.isDuplicateCif(customer.getCustID(), customer.getCustCIF())) {
			errParm[1] = "";
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", errParm, null));
		}

		if (!StringUtils.equals(method, PennantConstants.method_doReject) && PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(customer.getRecordType())) {
			boolean financeExistForCustomer = getCustomerDAO().financeExistForCustomer(customer.getId(), "_View");
			if (financeExistForCustomer) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, null));
			}
		}

		if (isDuplicateCrcpr(customer.getCustID(), customer.getCustCRCPR())) {
			String[] errorParameters = new String[1];
			if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, customer.getCustCtgCode())) {
				errorParameters[0] = PennantJavaUtil.getLabel("label_CustCRCPR") + ":"
						+ PennantApplicationUtil.formatEIDNumber(customer.getCustCRCPR());
			} else {
				errorParameters[0] = PennantJavaUtil.getLabel("label_CustTradeLicenseNumber") + ":"
						+ customer.getCustCRCPR();
			}

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", errorParameters, null));
		}
		
		
		//customer dedup validation
		if (customerDetails.getCustomerDedupList() != null && !customerDetails.getCustomerDedupList().isEmpty()) {
			for (CustomerDedup customerDedup : customerDetails.getCustomerDedupList()) {
				if (StringUtils.equals(customerDedup.getSourceSystem(), PennantConstants.CUSTOMER_DEDUP_SOURCE_SYSTEM_PENNANT)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("99012", null)));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customer.isWorkflow()) {
			customer.setBefImage(befCustomer);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerRating.isNewRecord()) {
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

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerEmploymentDetail.isNewRecord()) {
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

	/**
	 * Method For Preparing List of AuditDetails for Customer PRelations
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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
			customerIncome.setCustID(custId);

			if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerIncome.isNewRecord()) {
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
				customerIncomeDAO.save(customerIncome, type);
			}

			if (updateRecord) {
				customerIncomeDAO.update(customerIncome, type);
			}

			if (deleteRecord) {
				customerIncomeDAO.delete(customerIncome, type);
			}

			if (approveRec) {
				customerIncome.setRecordType(rcdType);
				customerIncome.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerIncome);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Customer PRelations
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerEMail.isNewRecord()) {
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

	/**
	 * Method For Preparing List of AuditDetails for Customer Address
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerAddres.isNewRecord()) {
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

	/**
	 * Method For Preparing List of AuditDetails for Customer Documents
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerDocument.isNewRecord()) {
				saveRecord = true;
				if (StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
						PennantConstants.RCD_DEL)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
						PennantConstants.RCD_UPD)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
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
				if (customerDocument.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(customerDocument.getCustDocImage());
					customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
				}
				customerDocumentDAO.save(customerDocument, type);
			}

			if (updateRecord) {
				if (customerDocument.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(customerDocument.getCustDocImage());
					customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
				}
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

	/**
	 * Method For Preparing List of AuditDetails for Customer Bank Information
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerBankInfo.isNewRecord()) {
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
				customerBankInfoDAO.save(customerBankInfo, type);
			}

			if (updateRecord) {
				customerBankInfoDAO.update(customerBankInfo, type);
			}

			if (deleteRecord) {
				customerBankInfoDAO.delete(customerBankInfo, type);
			}

			if (approveRec) {
				customerBankInfo.setRecordType(rcdType);
				customerBankInfo.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerBankInfo);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Bank Information
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerChequeInfo.isNewRecord()) {
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

	/**
	 * Method For Preparing List of AuditDetails for Customer Bank Information
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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
			customerExtLiability.setCustID(custId);

			if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerExtLiability.isNewRecord()) {
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
				customerExtLiabilityDAO.save(customerExtLiability, type);
			}

			if (updateRecord) {
				customerExtLiabilityDAO.update(customerExtLiability, type);
			}

			if (deleteRecord) {
				customerExtLiabilityDAO.delete(customerExtLiability, type);
			}

			if (approveRec) {
				customerExtLiability.setRecordType(rcdType);
				customerExtLiability.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerExtLiability);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Customer PhoneNumbers
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerPhoneNumber.isNewRecord()) {
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

	/**
	 * Method For Preparing List of AuditDetails for Customer Address
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
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

			if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (directorDetail.isNewRecord()) {
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

	// Method for Deleting all records related to Customer in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(CustomerDetails custDetails, String tableType, String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (custDetails.getRatingsList() != null && custDetails.getRatingsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerRating());
			for (int i = 0; i < custDetails.getRatingsList().size(); i++) {
				CustomerRating customerRating = custDetails.getRatingsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerRating.getBefImage(),
						customerRating));
			}
			getCustomerRatingDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}
		if (custDetails.getEmploymentDetailsList() != null && custDetails.getEmploymentDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerEmploymentDetail());
			for (int i = 0; i < custDetails.getEmploymentDetailsList().size(); i++) {
				CustomerEmploymentDetail employmentDetail = custDetails.getEmploymentDetailsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], employmentDetail
						.getBefImage(), employmentDetail));
			}
			getCustomerEmploymentDetailDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getAddressList() != null && custDetails.getAddressList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerAddres());
			for (int i = 0; i < custDetails.getAddressList().size(); i++) {
				CustomerAddres customerAddres = custDetails.getAddressList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerAddres.getBefImage(),
						customerAddres));
			}
			getCustomerAddresDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerEMailList() != null && custDetails.getCustomerEMailList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerEMail());
			for (int i = 0; i < custDetails.getCustomerEMailList().size(); i++) {
				CustomerEMail customerEMail = custDetails.getCustomerEMailList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerEMail.getBefImage(),
						customerEMail));
			}
			getCustomerEMailDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerPhoneNumList() != null && custDetails.getCustomerPhoneNumList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerPhoneNumber());
			for (int i = 0; i < custDetails.getCustomerPhoneNumList().size(); i++) {
				CustomerPhoneNumber customerPhoneNumber = custDetails.getCustomerPhoneNumList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerPhoneNumber
						.getBefImage(), customerPhoneNumber));
			}
			getCustomerPhoneNumberDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerDocumentsList() != null && custDetails.getCustomerDocumentsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerDocument());
			for (int i = 0; i < custDetails.getCustomerDocumentsList().size(); i++) {
				CustomerDocument customerDocument = custDetails.getCustomerDocumentsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerDocument
						.getBefImage(), customerDocument));
			}
			getCustomerDocumentDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerIncomeList() != null && custDetails.getCustomerIncomeList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerIncome());
			for (int i = 0; i < custDetails.getCustomerIncomeList().size(); i++) {
				CustomerIncome customerIncome = custDetails.getCustomerIncomeList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerIncome.getBefImage(),
						customerIncome));
			}
			getCustomerIncomeDAO().deleteByCustomer(custDetails.getCustID(), tableType, false);
		}

		if (custDetails.getCustomerDirectorList() != null && custDetails.getCustomerDirectorList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new DirectorDetail());
			for (int i = 0; i < custDetails.getCustomerDirectorList().size(); i++) {
				DirectorDetail cirectorDetail = custDetails.getCustomerDirectorList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], cirectorDetail.getBefImage(),
						cirectorDetail));
			}
			getDirectorDetailDAO().delete(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustEmployeeDetail() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustEmployeeDetail());
			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], custDetails.getCustEmployeeDetail()
					.getBefImage(), custDetails.getCustEmployeeDetail()));
			getCustEmployeeDetailDAO().delete(custDetails.getCustEmployeeDetail(), tableType);
		}

		if (custDetails.getCustomerBankInfoList() != null && custDetails.getCustomerBankInfoList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerBankInfo());
			for (int i = 0; i < custDetails.getCustomerBankInfoList().size(); i++) {
				CustomerBankInfo customerBankInfo = custDetails.getCustomerBankInfoList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerBankInfo
						.getBefImage(), customerBankInfo));
			}
			getCustomerBankInfoDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerChequeInfoList() != null && custDetails.getCustomerChequeInfoList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerChequeInfo());
			for (int i = 0; i < custDetails.getCustomerChequeInfoList().size(); i++) {
				CustomerChequeInfo customerChequeInfo = custDetails.getCustomerChequeInfoList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerChequeInfo
						.getBefImage(), customerChequeInfo));
			}
			getCustomerChequeInfoDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerExtLiabilityList() != null && custDetails.getCustomerExtLiabilityList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerExtLiability());
			for (int i = 0; i < custDetails.getCustomerExtLiabilityList().size(); i++) {
				CustomerExtLiability customerExtLiability = custDetails.getCustomerExtLiabilityList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerExtLiability
						.getBefImage(), customerExtLiability));
			}
			getCustomerExtLiabilityDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}
		return auditList;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
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
		if (customerDetails.getEmploymentDetailsList() != null && customerDetails.getEmploymentDetailsList().size() > 0) {
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

		if (customerDetails.getCustomerDocumentsList() != null && customerDetails.getCustomerDocumentsList().size() > 0) {
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

		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			auditDetailMap.put("CustomerChequeInfo",
					setChequeInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerChequeInfo"));
		}

		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			auditDetailMap
					.put("CustomerExtLiability", setExtLiabilityAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerExtLiability"));
		}

		customerDetails.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(customerDetails);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setRatingAuditData(CustomerDetails customerDetails, String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerRating());

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
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerRating
						.getBefImage(), customerRating));
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
						|| customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerEmploymentDetail.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerEmploymentDetail.setLoginDetails(customerDetails.getUserDetails());
			customerEmploymentDetail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotEmpty(customerEmploymentDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerEmploymentDetail
						.getBefImage(), customerEmploymentDetail));
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
			if (customerIncome.getCustID() <= 0) {
				customerIncome.setCustID(customerDetails.getCustID());
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

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
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
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerIncome
						.getBefImage(), customerIncome));
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
	private List<AuditDetail> setAddressAuditData(CustomerDetails customerDetails, String auditTranType, String method) {
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
	private List<AuditDetail> setDocumentAuditData(CustomerDetails customerDetails, String auditTranType, String method) {

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
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
					PennantConstants.RCD_UPD)) {
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
				if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerDocument.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerDocument.setLoginDetails(customerDetails.getUserDetails());
			customerDocument.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (StringUtils.isNotBlank(customerDocument.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], customerDocument
						.getBefImage(), customerDocument));
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
	private List<AuditDetail> setDirectorAuditData(CustomerDetails customerDetails, String auditTranType, String method) {

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
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], directorDetail
						.getBefImage(), directorDetail));
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
				if (customerDetails.getCustomer().isWorkflow()) {
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

			bankInfo.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			bankInfo.setLoginDetails(customerDetails.getUserDetails());
			bankInfo.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bankInfo.getBefImage(),
					bankInfo));
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

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], chequeInfo.getBefImage(),
					chequeInfo));
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
		CustomerExtLiability custExtLiability = new CustomerExtLiability();
		String[] fields = PennantJavaUtil.getFieldDetails(custExtLiability, custExtLiability.getExcludeFields());

		for (int i = 0; i < customerDetails.getCustomerExtLiabilityList().size(); i++) {
			CustomerExtLiability liability = customerDetails.getCustomerExtLiabilityList().get(i);

			if (StringUtils.isEmpty(liability.getRecordType())) {
				continue;
			}

			liability.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (liability.getCustID() <= 0) {
				liability.setCustID(customerDetails.getCustID());
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

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
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
			liability.setLoginDetails(customerDetails.getUserDetails());
			liability.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], liability.getBefImage(),
					liability));
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
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
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
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
								befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	/**
	 * 
	 * @param customer
	 * @return
	 * @throws InterfaceException
	 */
	public Customer fetchCoreCustomerDetails(Customer customer) throws InterfaceException {
		return getCustomerInterfaceService().fetchCustomerDetails(customer);
	}

	/**
	 * 
	 * @param customer
	 * @return
	 * @throws CustomerNotFoundException
	 */
	public void updateProspectCustomer(Customer customer) {
		getCustomerDAO().updateProspectCustomer(customer);
	}

	/**
	 * Fetch Core Banking Customer details
	 */
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
			RelationshipOfficer officer = getRelationshipOfficerDAO().getRelationshipOfficerById(customer.getCustRO1(),
					"_Aview");
			if (officer != null) {
				customer.setCustRO1(officer.getROfficerCode());
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
		logger.debug("Entering");
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
		logger.debug("Leaving");
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
	public String getEIDNumberById(String eidNumber , String type) {
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
	public int updateCustCRCPR(String custDocTitle,long custID) {
		return customerDAO.updateCustCRCPR(custDocTitle,custID);
	}
	@Override
	public void updateProspectCustCIF(String oldCustCIF, String newCustCIF) {
		logger.debug("Entering");
		getCustomerDAO().updateProspectCustCIF(oldCustCIF, newCustCIF);
		logger.debug("Leaving");
	}

	@Override
	public String getCustCoreBankIdByCIF(String custCIF) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerDAO().getCustCoreBankIdByCIF(custCIF);
	}

	@Override
	public String getNewCoreCustomerCIF() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getCustomerDAO().getNewCoreCustomerCIF();
	}

	@Override
	public void updateCorebankCustCIF(String coreCustCIF) {
		logger.debug("Entering");

		getCustomerDAO().updateCorebankCustCIF(coreCustCIF);

		logger.debug("Leaving");

	}

	@Override
	public int getCustomerCountByCIF(String custCIF) {
		return getCustomerDAO().getCustomerCountByCIF(custCIF, "");
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}

	@Override
	public boolean getCustomerByCoreBankId(String custCoreBank) {
		return getCustomerDAO().getCustomerByCoreBankId(custCoreBank);
	}

	public LovFieldDetailService getLovFieldDetailService() {
		return lovFieldDetailService;
	}

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
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

	public GCDCustomerService getgCDCustomerService() {
		return gCDCustomerService;
	}

	public void setgCDCustomerService(GCDCustomerService gCDCustomerService) {
		this.gCDCustomerService = gCDCustomerService;
	}
	public void setPhoneTypeDAO(PhoneTypeDAO phoneTypeDAO) {
		this.phoneTypeDAO = phoneTypeDAO;
	}


}