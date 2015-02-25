package com.pennant.backend.service.customermasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.applicationmaster.RelationshipOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.RatingCodeDAO;
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
import com.pennant.backend.dao.rmtmasters.CustomerTypeDAO;
import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.dao.systemmasters.EmpStsCodeDAO;
import com.pennant.backend.dao.systemmasters.NationalityCodeDAO;
import com.pennant.backend.dao.systemmasters.SectorDAO;
import com.pennant.backend.dao.systemmasters.SubSectorDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
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
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
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
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.rits.cloning.Cloner;

public class CustomerDetailsServiceImpl extends GenericService<Customer> implements
        CustomerDetailsService {

	private final static Logger logger = Logger.getLogger(CustomerDetailsServiceImpl.class);

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

	//Declaring Classes For validation for Lists
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public CustomerRatingValidation getRatingValidation() {

		if (customerRatingValidation == null) {
			this.customerRatingValidation = new CustomerRatingValidation(customerRatingDAO);
		}
		return this.customerRatingValidation;
	}

	public CustomerPhoneNumberValidation getPhoneNumberValidation() {

		if (customerPhoneNumberValidation == null) {
			this.customerPhoneNumberValidation = new CustomerPhoneNumberValidation(
			        customerPhoneNumberDAO);
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
			this.corporateCustomerValidation = new CorporateCustomerValidation(
			        corporateCustomerDetailDAO);
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
			this.customerBalanceSheetValidation = new CustomerBalanceSheetValidation(
			        customerBalanceSheetDAO);
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
	 * @return the customer
	 */
	@Override
	public CustomerDetails getCustomer(boolean createNew) {
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomer(createNew));
		return customerDetails;
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

	
	public CustomerDetails getCustomerDetailsbyIdandPhoneType(long id, String phoneType){
		CustomerDetails customerDetails = new CustomerDetails();
		
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, ""));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomerPhoneType(id, "", phoneType));
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
		customerDetails.setRatingsList(customerRatingDAO.getCustomerRatingByCustomer(id, type));
		customerDetails.setEmploymentDetailsList(getCustomerEmploymentDetailDAO()
		        .getCustomerEmploymentDetailsByID(id, type));
		customerDetails.setCustomerIncomeList(customerIncomeDAO.getCustomerIncomeByCustomer(id,false,
		        type));
		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO
		        .getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(
		        id, type));
		customerDetails.setCustomerDirectorList(directorDetailDAO.getCustomerDirectorByCustomer(id, type));

		logger.debug("Leaving");
		return customerDetails;
	}

	/**
	 * @return the customerDetails for the given customer id.
	 * */
	private CustomerDetails getCustomerDetailsbyID(long id, String type) {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(getCustomerDAO().getCustomerByID(id, type));
		customerDetails.setCustID(id);
		
		customerDetails.setCustEmployeeDetail(getCustEmployeeDetailDAO().getCustEmployeeDetailById(id, type));
		customerDetails.setCustomerDocumentsList(customerDocumentDAO.getCustomerDocumentByCustomer(id, type));
		customerDetails.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(id, type));
		customerDetails.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(id, type));
		customerDetails.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(id, type));
		customerDetails.setCustomerBankInfoList(customerBankInfoDAO.getBankInfoByCustomer(id, type));
		customerDetails.setCustomerChequeInfoList(customerChequeInfoDAO.getChequeInfoByCustomer(id, type));
		customerDetails.setCustomerExtLiabilityList(customerExtLiabilityDAO.getExtLiabilityByCustomer(id, type));
		customerDetails.setCustFinanceExposureList(getCustomerDAO().getCustomerFinanceDetailById(id));
		
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
	public CustomerDetails getCustomerDetailsById(long id,String type) {
		return getCustomerDetailsbyID(id, type);
	}
	
	@Override
	public Customer getCustomerByCIF(String id) {
		return getCustomerDAO().getCustomerByCIF(id, "");
	}

	@Override
	public Customer getCheckCustomerByCIF(String cif) {
		return getCustomerDAO().getCustomerByCIF(cif, "_View");
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
	 */
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
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
			tableType = "_TEMP";
		}

		if (customer.isNew()) {
			getCustomerDAO().save(customer, tableType);
			auditHeader.getAuditDetail().setModelData(customer);
			auditHeader.setAuditReference(String.valueOf(customer.getCustID()));

		} else {
			getCustomerDAO().update(customer, tableType);
		}

		customerDetails.setCustID(customer.getCustID());

		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
			details = processingRatingList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}
		if (customerDetails.getEmploymentDetailsList() != null
		        && customerDetails.getEmploymentDetailsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
			details = processingCustomerEmploymentDetailList(details, tableType,
			        customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerPhoneNumList() != null
		        && customerDetails.getCustomerPhoneNumList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
			details = processingPhoneNumberList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerIncomeList() != null
		        && customerDetails.getCustomerIncomeList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Income");
			details = processingIncomeList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		if (customerDetails.getCustomerEMailList() != null
		        && customerDetails.getCustomerEMailList().size() > 0) {
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
		
		if (customerDetails.getCustomerDirectorList() != null
				&& customerDetails.getCustomerDirectorList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Director");
			details = processingDirectorList(details, tableType, customerDetails.getCustID());
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),"proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], customer.getBefImage(), customer));
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}
	
	
	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String auditTranType;
		Customer customer = customerDetails.getCustomer();
		if(tableType.equals("")){
			customer.setRecordType("");
			customer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		customer.setWorkflowId(0);
		if(customer.isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
			long custID = customerDAO.save(customer, tableType);
			financeDetail.getFinScheduleData().getFinanceMain().setCustID(custID);
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
			customer.setVersion(customer.getVersion()+1);
			customerDAO.update(customer, tableType);
		}
		
		String[] fields = PennantJavaUtil.getFieldDetails(customer, customer.getExcludeFields());
		auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], customer.getBefImage(), customer));
		
		if(customerDetails.getCustEmployeeDetail() != null){
			CustEmployeeDetail custEmpDetail = customerDetails.getCustEmployeeDetail();
			custEmpDetail.setWorkflowId(0);
			custEmpDetail.setCustID(customer.getCustID());
			if(tableType.equals("")){
				custEmpDetail.setRecordType("");
				custEmpDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if(custEmpDetail.isNewRecord()) {
				auditTranType = PennantConstants.TRAN_ADD;
				custEmployeeDetailDAO.save(custEmpDetail, tableType);
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
				custEmpDetail.setVersion(custEmpDetail.getVersion()+1);
				custEmployeeDetailDAO.update(custEmpDetail, tableType);
			}

			fields = PennantJavaUtil.getFieldDetails(custEmpDetail, custEmpDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], custEmpDetail.getBefImage(), custEmpDetail));
		}
		
		if(customerDetails.getCustomerDocumentsList() != null){
			for (CustomerDocument customerDocument : customerDetails.getCustomerDocumentsList()) {
				customerDocument.setWorkflowId(0);
				customerDocument.setCustID(customer.getCustID());
				if(tableType.equals("")  && !StringUtils.trimToEmpty(customerDocument.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					customerDocument.setRecordType("");
					customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerDocumentDAO.delete(customerDocument, tableType);
				}else if(customerDocument.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerDocumentDAO.save(customerDocument, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerDocument.setVersion(customerDocument.getVersion()+1);
					customerDocumentDAO.update(customerDocument, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(customerDocument, customerDocument.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], customerDocument.getBefImage(), customerDocument));
			}
		}
		
		if(customerDetails.getAddressList() != null){
			for (CustomerAddres custaddress : customerDetails.getAddressList()) {
				custaddress.setWorkflowId(0);
				custaddress.setCustID(customer.getCustID());
				if(tableType.equals("")  && !StringUtils.trimToEmpty(custaddress.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					custaddress.setRecordType("");
					custaddress.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custaddress.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerAddresDAO.delete(custaddress, tableType);
				}else if(custaddress.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerAddresDAO.save(custaddress, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					custaddress.setVersion(custaddress.getVersion()+1);
					customerAddresDAO.update(custaddress, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(custaddress, custaddress.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], custaddress.getBefImage(), custaddress));
			}
		}
		
		if(customerDetails.getCustomerPhoneNumList() != null){
			for (CustomerPhoneNumber custPhoneNumber : customerDetails.getCustomerPhoneNumList()) {
				custPhoneNumber.setWorkflowId(0);
				custPhoneNumber.setPhoneCustID(customer.getCustID());
				if(tableType.equals("")  && !StringUtils.trimToEmpty(custPhoneNumber.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					custPhoneNumber.setRecordType("");
					custPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custPhoneNumber.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerPhoneNumberDAO.delete(custPhoneNumber, tableType);
				}else if(custPhoneNumber.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerPhoneNumberDAO.save(custPhoneNumber, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					custPhoneNumber.setVersion(custPhoneNumber.getVersion()+1);
					customerPhoneNumberDAO.update(custPhoneNumber, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(custPhoneNumber, custPhoneNumber.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], custPhoneNumber.getBefImage(), custPhoneNumber));
			}
		}
		
		if(customerDetails.getCustomerEMailList() != null){
			for (CustomerEMail customerEMail : customerDetails.getCustomerEMailList()) {
				customerEMail.setWorkflowId(0);
				customerEMail.setCustID(customer.getCustID());
				if(tableType.equals("") && !StringUtils.trimToEmpty(customerEMail.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					customerEMail.setRecordType("");
					customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(customerEMail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerEMailDAO.delete(customerEMail, tableType);
				}else if(customerEMail.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerEMailDAO.save(customerEMail, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					customerEMail.setVersion(customerEMail.getVersion()+1);
					customerEMailDAO.update(customerEMail, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(customerEMail, customerEMail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], customerEMail.getBefImage(), customerEMail));
			}
		}
		
		if(customerDetails.getCustomerBankInfoList() != null){
			for (CustomerBankInfo custBankInfo : customerDetails.getCustomerBankInfoList()) {
				custBankInfo.setWorkflowId(0);
				custBankInfo.setCustID(customer.getCustID());
				if(tableType.equals("") && !StringUtils.trimToEmpty(custBankInfo.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					custBankInfo.setRecordType("");
					custBankInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custBankInfo.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerBankInfoDAO.delete(custBankInfo, tableType);
				}else if(custBankInfo.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerBankInfoDAO.save(custBankInfo, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					custBankInfo.setVersion(custBankInfo.getVersion()+1);
					customerBankInfoDAO.update(custBankInfo, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(custBankInfo, custBankInfo.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], custBankInfo.getBefImage(), custBankInfo));
			}
		}
		
		if(customerDetails.getCustomerChequeInfoList() != null){
			for (CustomerChequeInfo custChequeInfo : customerDetails.getCustomerChequeInfoList()) {
				custChequeInfo.setWorkflowId(0);
				custChequeInfo.setCustID(customer.getCustID());
				if(tableType.equals("") && !StringUtils.trimToEmpty(custChequeInfo.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					custChequeInfo.setRecordType("");
					custChequeInfo.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custChequeInfo.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerChequeInfoDAO.delete(custChequeInfo, tableType);
				}else if(custChequeInfo.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerChequeInfoDAO.save(custChequeInfo, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					custChequeInfo.setVersion(custChequeInfo.getVersion()+1);
					customerChequeInfoDAO.update(custChequeInfo, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(custChequeInfo, custChequeInfo.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], custChequeInfo.getBefImage(), custChequeInfo));
			}
		}
		
		if(customerDetails.getCustomerExtLiabilityList() != null){
			for (CustomerExtLiability custExtLiability : customerDetails.getCustomerExtLiabilityList()) {
				custExtLiability.setWorkflowId(0);
				custExtLiability.setCustID(customer.getCustID());
				if(tableType.equals("") && !StringUtils.trimToEmpty(custExtLiability.getRecordType())
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)){
					custExtLiability.setRecordType("");
					custExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (StringUtils.trimToEmpty(custExtLiability.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					auditTranType = PennantConstants.TRAN_DEL;
					customerExtLiabilityDAO.delete(custExtLiability, tableType);
				}else if(custExtLiability.isNewRecord()) {
					auditTranType = PennantConstants.TRAN_ADD;
					customerExtLiabilityDAO.save(custExtLiability, tableType);
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
					custExtLiability.setVersion(custExtLiability.getVersion()+1);
					customerExtLiabilityDAO.update(custExtLiability, tableType);
				}

				fields = PennantJavaUtil.getFieldDetails(custExtLiability, custExtLiability.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], custExtLiability.getBefImage(), custExtLiability));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> validate(CustomerDetails customerDetails, long workflowId, String method, String  usrLanguage){
		return doValidation(customerDetails, workflowId, method, usrLanguage);
	}
	
	public List<AuditDetail> doValidation(CustomerDetails customerDetails, long workflowId, 
			String method, String usrLanguage) {
		logger.debug("Entering");

		String auditTranType;
		if(customerDetails.getCustomer().isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
		}
		List<AuditDetail> auditDetails = getAuditDetail(customerDetails, auditTranType, method, workflowId);

		// Employment Validation
		if (customerDetails.getEmploymentDetailsList() != null
				&& customerDetails.getEmploymentDetailsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
			details = getEmploymentDetailValidation().employmentDetailListValidation(details,
					method, usrLanguage);
			auditDetails.addAll(details);
		}

		//PhoneNumber Validation
		if (customerDetails.getCustomerPhoneNumList() != null
				&& customerDetails.getCustomerPhoneNumList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
			details = getPhoneNumberValidation().phoneNumberListValidation(details, method,
					usrLanguage);
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

		//Email Validation
		if (customerDetails.getCustomerEMailList() != null
				&& customerDetails.getCustomerEMailList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
			details = getCustomerEMailValidation()
					.emailListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		
		//Customer Bank Information Validation
		if (customerDetails.getCustomerBankInfoList() != null
				&& customerDetails.getCustomerBankInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerBankInfo");
			details = getCustomerBankInfoValidation().bankInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Customer Cheque Information Validation
		if (customerDetails.getCustomerChequeInfoList() != null
				&& customerDetails.getCustomerChequeInfoList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerChequeInfo");
			details = getCustomerChequeInfoValidation().chequeInfoListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Customer Bank Information Validation
		if (customerDetails.getCustomerExtLiabilityList() != null
				&& customerDetails.getCustomerExtLiabilityList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("CustomerExtLiability");
			details = getCustomerExtLiabilityValidation().extLiabilityListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	
	private List<AuditDetail> getAuditDetail(CustomerDetails customerDetails, String auditTranType, String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		
		if (customerDetails.getEmploymentDetailsList() != null
		        && customerDetails.getEmploymentDetailsList().size() > 0) {
			auditDetailMap.put("Employment",
			        setCustomerEmploymentDetailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Employment"));
		}

		if (customerDetails.getCustomerPhoneNumList() != null
		        && customerDetails.getCustomerPhoneNumList().size() > 0) {
			auditDetailMap.put("PhoneNumber",
			        setPhoneNumberAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PhoneNumber"));
		}


		if (customerDetails.getCustomerEMailList() != null
		        && customerDetails.getCustomerEMailList().size() > 0) {
			auditDetailMap.put("EMail", setEMailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("EMail"));
		}

		if (customerDetails.getCustomerDocumentsList() != null
		        && customerDetails.getCustomerDocumentsList().size() > 0) {
			auditDetailMap.put("Document",
			        setDocumentAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Document"));
		}
		
		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			auditDetailMap.put("Address",
			        setAddressAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Address"));
		}

		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			auditDetailMap.put("CustomerBankInfo",
					setBankInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerBankInfo"));
		}

		if (customerDetails.getCustomerChequeInfoList() != null && customerDetails.getCustomerChequeInfoList().size() > 0) {
			auditDetailMap.put("CustomerChequeInfo",
					setChequeInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerChequeInfo"));
		}
		

		if (customerDetails.getCustomerExtLiabilityList() != null && customerDetails.getCustomerExtLiabilityList().size() > 0) {
			auditDetailMap.put("CustomerExtLiability",
					setExtLiabilityAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerExtLiability"));
		}
		
		customerDetails.setAuditDetailMap(auditDetailMap);
		
		logger.debug("Leaving");
		return auditDetails;
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


		auditDetails.addAll(getListAuditDetails(listDeletion(customerDetails, "",
		        auditHeader.getAuditTranType())));

		getCustomerDAO().delete(customer, "");
		
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),
		        "proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], customer.getBefImage(), customer));
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws CustomerNotFoundException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}
		
		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer customer = customerDetails.getCustomer();

		if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(customerDetails, "", tranType));
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
				//Customer Creation Core Banking
				//getCustomerInterfaceService().generateNewCIF("A", customer, "");
				getCustomerDAO().save(customer, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customer.setRecordType("");
				getCustomerDAO().update(customer, "");
			}

			//Retrieving List of Audit Details For Customer related modules
			if (customerDetails.getRatingsList() != null
			        && customerDetails.getRatingsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
				details = processingRatingList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getEmploymentDetailsList() != null
			        && customerDetails.getEmploymentDetailsList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
				details = processingCustomerEmploymentDetailList(details, "",
				        customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerPhoneNumList() != null
			        && customerDetails.getCustomerPhoneNumList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
				details = processingPhoneNumberList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}


			if (customerDetails.getCustomerIncomeList() != null
			        && customerDetails.getCustomerIncomeList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Income");
				details = processingIncomeList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}

			if (customerDetails.getCustomerEMailList() != null
			        && customerDetails.getCustomerEMailList().size() > 0) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
				details = processingEMailList(details, "", customerDetails.getCustID());
				auditDetails.addAll(details);
			}
			if (customerDetails.getAddressList() != null
			        && customerDetails.getAddressList().size() > 0) {
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

		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		getCustomerDAO().delete(customer, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditDetailList.addAll(getListAuditDetails(listDeletion(customerDetails, "_TEMP",
		        auditHeader.getAuditTranType())));

		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),
		        "proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], customer.getBefImage(), customer));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], customer.getBefImage(), customer));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
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

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail()
		        .getModelData();
		Customer customer = customerDetails.getCustomer();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerDAO().delete(customer, "_TEMP");
		String[] fields = PennantJavaUtil.getFieldDetails(new Customer(),
		        "proceedToDedup,dedupFound,skipDedup");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
		        fields[1], customer.getBefImage(), customer));
		auditDetails.addAll(getListAuditDetails(listDeletion(customerDetails, "_TEMP",
		        auditHeader.getAuditTranType())));

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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = customerDetails.getCustomer().getUserDetails().getUsrLanguage();
		String custctg=customerDetails.getCustomer().getCustCtgCode();
		
		// Rating Validation
		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Rating");
			details = getRatingValidation().ratingListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		if (custctg.equals(PennantConstants.PFF_CUSTCTG_INDIV)) {
		// EmploymentDetail Validation
		if (customerDetails.getCustomer().getCustEmpSts() != null) {
			if (customerDetails.getCustomer().getCustEmpSts().equals("EMPLOY")) {
				List<AuditDetail> details = customerDetails.getAuditDetailMap().get(
				        "EmploymentDetail");
				details = getEmploymentDetailValidation().employmentDetailListValidation(details,
				        method, usrLanguage);
				auditDetails.addAll(details);
			}
		}

		// Rating Validation
		if (customerDetails.getEmploymentDetailsList() != null
		        && customerDetails.getEmploymentDetailsList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Employment");
			details = getEmploymentDetailValidation().employmentDetailListValidation(details,
			        method, usrLanguage);
			auditDetails.addAll(details);
		}

		//PhoneNumber Validation
		if (customerDetails.getCustomerPhoneNumList() != null
		        && customerDetails.getCustomerPhoneNumList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("PhoneNumber");
			details = getPhoneNumberValidation().phoneNumberListValidation(details, method,
			        usrLanguage);
			auditDetails.addAll(details);
		}


		//Income Validation
		if (customerDetails.getCustomerIncomeList() != null
		        && customerDetails.getCustomerIncomeList().size() > 0) {
			auditDetails.addAll( getCustomerIncomeValidation().incomeListValidation(customerDetails, method,
			        usrLanguage));
 		}

		// Address Validation
		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Address");
			details = getAddressValidation().addressListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//PRelation Validation
		if (customerDetails.getCustomerEMailList() != null
		        && customerDetails.getCustomerEMailList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("EMail");
			details = getCustomerEMailValidation()
			        .emailListValidation(details, method, usrLanguage);
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
		if (customerDetails.getCustomerDirectorList() != null
				&& customerDetails.getCustomerDirectorList().size() > 0) {
			List<AuditDetail> details = customerDetails.getAuditDetailMap().get("Director");
			details = getDirectorValidation().directorListValidation(details, method, usrLanguage);
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
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41001", errParm, null));
				}
			} else { // with work flow
				if (customer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCustomer != null || tempCustomer != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						        "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomer == null || tempCustomer != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						        "41005", errParm, null));
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
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41002", errParm, null));
				} else {

					if (oldCustomer != null
					        && !oldCustomer.getLastMntOn().equals(befCustomer.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
						        .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							        "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							        "41004", errParm, null));
						}
					}
				}

			} else {

				if (tempCustomer == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41005", errParm, null));
				}

				if (tempCustomer != null && oldCustomer != null
				        && !oldCustomer.getLastMntOn().equals(tempCustomer.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41005", errParm, null));
				}

			}
		}
		List<Customer> dupCIFCustomerList = new ArrayList<Customer>();
		dupCIFCustomerList.addAll(getCustomerDAO().getCustomerByCif(customer.getCustID(),
		        customer.getCustCIF()));
		if (dupCIFCustomerList.size() > 0) {
			errParm[1] = "";
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014",
			        errParm, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !customer.isWorkflow()) {
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
	private List<AuditDetail> processingRatingList(List<AuditDetail> auditDetails, String type,
	        long custId) {

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
			if (type.equals("")) {
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
				} else if (customerRating.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerRating.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerRating.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerRating.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerRating.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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
	private List<AuditDetail> processingCustomerEmploymentDetailList(
	        List<AuditDetail> auditDetails, String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) auditDetails
			        .get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				customerEmploymentDetail.setRoleCode("");
				customerEmploymentDetail.setNextRoleCode("");
				customerEmploymentDetail.setTaskId("");
				customerEmploymentDetail.setNextTaskId("");
			}

			customerEmploymentDetail.setWorkflowId(0);
			customerEmploymentDetail.setCustID(custId);

			if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerEmploymentDetail.isNewRecord()) {
				saveRecord = true;
				if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_ADD)) {
					customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_DEL)) {
					customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_UPD)) {
					customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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
	private List<AuditDetail> processingIncomeList(List<AuditDetail> auditDetails, String type,
	        long custId) {

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
			if (type.equals("")) {
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
				} else if (customerIncome.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerIncome.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerIncome.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerIncome.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerIncome.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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
	private List<AuditDetail> processingEMailList(List<AuditDetail> auditDetails, String type,
	        long custId) {

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
			if (type.equals("")) {
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

			} else if (customerEMail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerEMail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerEMail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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
	private List<AuditDetail> processingAddressList(List<AuditDetail> auditDetails, String type,
	        long custId) {

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
			if (type.equals("")) {
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
				} else if (customerAddres.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerAddres.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerAddres.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerAddres.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerAddres.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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
	private List<AuditDetail> processingDocumentList(List<AuditDetail> auditDetails, String type,
	        long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerDocument customerDocument = (CustomerDocument) auditDetails.get(i)
			        .getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				customerDocument.setRoleCode("");
				customerDocument.setNextRoleCode("");
				customerDocument.setTaskId("");
				customerDocument.setNextTaskId("");
			}

			customerDocument.setWorkflowId(0);
			customerDocument.setCustID(custId);

			if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerDocument.isNewRecord()) {
				saveRecord = true;
				if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RCD_ADD)) {
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
				customerDocumentDAO.save(customerDocument, type);
			}

			if (updateRecord) {
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
	 * Method For Preparing List of AuditDetails for Customer PhoneNumbers
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingPhoneNumberList(List<AuditDetail> auditDetails,
	        String type, long custId) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) auditDetails.get(i)
			        .getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				customerPhoneNumber.setRoleCode("");
				customerPhoneNumber.setNextRoleCode("");
				customerPhoneNumber.setTaskId("");
				customerPhoneNumber.setNextTaskId("");
			}

			customerPhoneNumber.setWorkflowId(0);
			customerPhoneNumber.setPhoneCustID(custId);

			if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerPhoneNumber.isNewRecord()) {
				saveRecord = true;
				if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_DEL)) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
				        PennantConstants.RCD_UPD)) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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
	private List<AuditDetail> processingDirectorList(List<AuditDetail> auditDetails, String type,
	        long custId) {

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
			if (type.equals("")) {
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
				} else if (directorDetail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					directorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (directorDetail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					directorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (directorDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (directorDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (directorDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
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

	

	//Method for Deleting all records related to Customer in _Temp/Main tables  depend on method type
	public List<AuditDetail> listDeletion(CustomerDetails custDetails, String tableType,
	        String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (custDetails.getRatingsList() != null && custDetails.getRatingsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerRating());
			for (int i = 0; i < custDetails.getRatingsList().size(); i++) {
				CustomerRating customerRating = custDetails.getRatingsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerRating.getBefImage(), customerRating));
			}
			getCustomerRatingDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}
		if (custDetails.getEmploymentDetailsList() != null
		        && custDetails.getEmploymentDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerEmploymentDetail());
			for (int i = 0; i < custDetails.getEmploymentDetailsList().size(); i++) {
				CustomerEmploymentDetail employmentDetail = custDetails.getEmploymentDetailsList()
				        .get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        employmentDetail.getBefImage(), employmentDetail));
			}
			getCustomerEmploymentDetailDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}


		if (custDetails.getAddressList() != null && custDetails.getAddressList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerAddres());
			for (int i = 0; i < custDetails.getAddressList().size(); i++) {
				CustomerAddres customerAddres = custDetails.getAddressList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerAddres.getBefImage(), customerAddres));
			}
			getCustomerAddresDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerEMailList() != null
		        && custDetails.getCustomerEMailList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerEMail());
			for (int i = 0; i < custDetails.getCustomerEMailList().size(); i++) {
				CustomerEMail customerEMail = custDetails.getCustomerEMailList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerEMail.getBefImage(), customerEMail));
			}
			getCustomerEMailDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerPhoneNumList() != null
		        && custDetails.getCustomerPhoneNumList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerPhoneNumber());
			for (int i = 0; i < custDetails.getCustomerPhoneNumList().size(); i++) {
				CustomerPhoneNumber customerPhoneNumber = custDetails.getCustomerPhoneNumList()
				        .get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerPhoneNumber.getBefImage(), customerPhoneNumber));
			}
			getCustomerPhoneNumberDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerDocumentsList() != null
		        && custDetails.getCustomerDocumentsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerDocument());
			for (int i = 0; i < custDetails.getCustomerDocumentsList().size(); i++) {
				CustomerDocument customerDocument = custDetails.getCustomerDocumentsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerDocument.getBefImage(), customerDocument));
			}
			getCustomerDocumentDAO().deleteByCustomer(custDetails.getCustID(), tableType);
		}

		if (custDetails.getCustomerIncomeList() != null
		        && custDetails.getCustomerIncomeList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerIncome());
			for (int i = 0; i < custDetails.getCustomerIncomeList().size(); i++) {
				CustomerIncome customerIncome = custDetails.getCustomerIncomeList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerIncome.getBefImage(), customerIncome));
			}
			getCustomerIncomeDAO().deleteByCustomer(custDetails.getCustID(), tableType, false);
		}

		if (custDetails.getCustomerDirectorList() != null && custDetails.getCustomerDirectorList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new DirectorDetail());
			for (int i = 0; i < custDetails.getCustomerDirectorList().size(); i++) {
				DirectorDetail cirectorDetail = custDetails.getCustomerDirectorList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						cirectorDetail.getBefImage(), cirectorDetail));
			}
			getDirectorDetailDAO().delete(custDetails.getCustID(), tableType);
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

		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getAuditDetail()
		        .getModelData();
		Customer customer = customerDetails.getCustomer();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove")
		        || method.equals("doReject")) {
			if (customer.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (customerDetails.getRatingsList() != null && customerDetails.getRatingsList().size() > 0) {
			auditDetailMap
			        .put("Rating", setRatingAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Rating"));
		}
		if (customerDetails.getEmploymentDetailsList() != null
		        && customerDetails.getEmploymentDetailsList().size() > 0) {
			auditDetailMap.put("Employment",
			        setCustomerEmploymentDetailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Employment"));
		}

		if (customerDetails.getCustomerPhoneNumList() != null
		        && customerDetails.getCustomerPhoneNumList().size() > 0) {
			auditDetailMap.put("PhoneNumber",
			        setPhoneNumberAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PhoneNumber"));
		}


		if (customerDetails.getCustomerIncomeList() != null
		        && customerDetails.getCustomerIncomeList().size() > 0) {
			auditDetailMap
			        .put("Income", setIncomeAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Income"));
		}

		if (customerDetails.getCustomerEMailList() != null
		        && customerDetails.getCustomerEMailList().size() > 0) {
			auditDetailMap.put("EMail", setEMailAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("EMail"));
		}

		if (customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0) {
			auditDetailMap.put("Address",
			        setAddressAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Address"));
		}

		if (customerDetails.getCustomerDocumentsList() != null
		        && customerDetails.getCustomerDocumentsList().size() > 0) {
			auditDetailMap.put("Document",
			        setDocumentAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Document"));
		}
		
		if (customerDetails.getCustomerDirectorList() != null
				&& customerDetails.getCustomerDirectorList().size() > 0) {
			auditDetailMap.put("Director",
					setDirectorAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Director"));
		}


		if (customerDetails.getCustomerBankInfoList() != null && customerDetails.getCustomerBankInfoList().size() > 0) {
			auditDetailMap.put("CustomerBankInfo",
					setBankInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerBankInfo"));
		}

		if (customerDetails.getCustomerChequeInfoList() != null && customerDetails.getCustomerChequeInfoList().size() > 0) {
			auditDetailMap.put("CustomerChequeInfo",
					setChequeInformationAuditData(customerDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerChequeInfo"));
		}
		

		if (customerDetails.getCustomerExtLiabilityList() != null && customerDetails.getCustomerExtLiabilityList().size() > 0) {
			auditDetailMap.put("CustomerExtLiability",
					setExtLiabilityAuditData(customerDetails, auditTranType, method));
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
	private List<AuditDetail> setRatingAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

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

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerRating.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerRating.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerRating.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerRating.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerRating.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerRating.setLoginDetails(customerDetails.getUserDetails());
			customerRating.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerRating.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerRating.getBefImage(), customerRating));
			}
		}

		return auditDetails;
	}

	private List<AuditDetail> setCustomerEmploymentDetailAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerEmploymentDetail());

		for (int i = 0; i < customerDetails.getEmploymentDetailsList().size(); i++) {

			CustomerEmploymentDetail customerEmploymentDetail = customerDetails
			        .getEmploymentDetailsList().get(i);
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

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerEmploymentDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerEmploymentDetail.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerEmploymentDetail.setRecordStatus(customerDetails.getCustomer()
			        .getRecordStatus());
			customerEmploymentDetail.setLoginDetails(customerDetails.getUserDetails());
			customerEmploymentDetail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerEmploymentDetail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerEmploymentDetail.getBefImage(), customerEmploymentDetail));
			}
		}

		return auditDetails;
	}



	private List<AuditDetail> setPhoneNumberAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerPhoneNumber());

		for (int i = 0; i < customerDetails.getCustomerPhoneNumList().size(); i++) {

			CustomerPhoneNumber customerPhoneNumber = customerDetails.getCustomerPhoneNumList()
			        .get(i);
			customerPhoneNumber.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerPhoneNumber.getPhoneCustID() <= 0) {
				customerPhoneNumber.setPhoneCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
			        PennantConstants.RCD_UPD)) {
				customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
			        PennantConstants.RCD_DEL)) {
				customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerPhoneNumber.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerPhoneNumber.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerPhoneNumber.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerPhoneNumber.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerPhoneNumber.setLoginDetails(customerDetails.getUserDetails());
			customerPhoneNumber.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerPhoneNumber.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerPhoneNumber.getBefImage(), customerPhoneNumber));
			}
		}

		return auditDetails;
	}

	private List<AuditDetail> setIncomeAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerIncome());

		for (int i = 0; i < customerDetails.getCustomerIncomeList().size(); i++) {

			CustomerIncome customerIncome = customerDetails.getCustomerIncomeList().get(i);
			customerIncome.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerIncome.getCustID() <= 0) {
				customerIncome.setCustID(customerDetails.getCustID());
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

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerIncome.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerIncome.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerIncome.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerIncome.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerIncome.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerIncome.setLoginDetails(customerDetails.getUserDetails());
			customerIncome.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerIncome.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerIncome.getBefImage(), customerIncome));
			}
		}

		return auditDetails;
	}

	private List<AuditDetail> setEMailAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerEMail());

		for (int i = 0; i < customerDetails.getCustomerEMailList().size(); i++) {

			CustomerEMail customerEMail = customerDetails.getCustomerEMailList().get(i);
			customerEMail.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerEMail.getCustID() <= 0) {
				customerEMail.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerEMail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerEMail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (customerEMail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerEMail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerEMail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerEMail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerEMail.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerEMail.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerEMail.setLoginDetails(customerDetails.getUserDetails());
			customerEMail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerEMail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerEMail.getBefImage(), customerEMail));
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
	private List<AuditDetail> setAddressAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerAddres());

		for (int i = 0; i < customerDetails.getAddressList().size(); i++) {

			CustomerAddres customerAddres = customerDetails.getAddressList().get(i);
			customerAddres.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerAddres.getCustID() <= 0) {
				customerAddres.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerAddres.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (customerAddres.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerAddres.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerAddres.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerAddres.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerAddres.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerAddres.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerAddres.setLoginDetails(customerDetails.getUserDetails());
			customerAddres.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerAddres.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerAddres.getBefImage(), customerAddres));
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
	private List<AuditDetail> setDocumentAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerDocument());

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
			} else if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (StringUtils.trimToEmpty(StringUtils.trimToEmpty(customerDocument.getRecordType())).equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
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

			if (!StringUtils.trimToEmpty(customerDocument.getRecordType()).equals("")) {
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
	private List<AuditDetail> setDirectorAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new DirectorDetail());

		for (int i = 0; i < customerDetails.getCustomerDirectorList().size(); i++) {

			DirectorDetail directorDetail = customerDetails.getCustomerDirectorList().get(i);
			directorDetail.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (directorDetail.getCustID() <= 0) {
				directorDetail.setCustID(customerDetails.getCustID());
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

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				directorDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (directorDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (directorDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || directorDetail.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			directorDetail.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			directorDetail.setLoginDetails(customerDetails.getUserDetails());
			directorDetail.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!directorDetail.getRecordType().equals("")) {
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
	private List<AuditDetail> setBankInformationAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerAddres());

		for (int i = 0; i < customerDetails.getCustomerBankInfoList().size(); i++) {

			CustomerBankInfo customerBankInfo = customerDetails.getCustomerBankInfoList().get(i);
			customerBankInfo.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerBankInfo.getCustID() <= 0) {
				customerBankInfo.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (customerBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerBankInfo.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerBankInfo.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerBankInfo.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerBankInfo.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerBankInfo.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerBankInfo.setLoginDetails(customerDetails.getUserDetails());
			customerBankInfo.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerBankInfo.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerBankInfo.getBefImage(), customerBankInfo));
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
	private List<AuditDetail> setChequeInformationAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerAddres());

		for (int i = 0; i < customerDetails.getCustomerChequeInfoList().size(); i++) {

			CustomerChequeInfo customerChequeInfo = customerDetails.getCustomerChequeInfoList().get(i);
			customerChequeInfo.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerChequeInfo.getCustID() <= 0) {
				customerChequeInfo.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerChequeInfo.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerChequeInfo.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerChequeInfo.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerChequeInfo.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerChequeInfo.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerChequeInfo.setLoginDetails(customerDetails.getUserDetails());
			customerChequeInfo.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerChequeInfo.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerChequeInfo.getBefImage(), customerChequeInfo));
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
	private List<AuditDetail> setExtLiabilityAuditData(CustomerDetails customerDetails,
	        String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerAddres());

		for (int i = 0; i < customerDetails.getCustomerExtLiabilityList().size(); i++) {

			CustomerExtLiability customerExtLiability = customerDetails.getCustomerExtLiabilityList().get(i);
			customerExtLiability.setWorkflowId(customerDetails.getCustomer().getWorkflowId());
			if (customerExtLiability.getCustID() <= 0) {
				customerExtLiability.setCustID(customerDetails.getCustID());
			}

			boolean isRcdType = false;

			if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (customerDetails.getCustomer().isWorkflow()) {
					isRcdType = true;
                }
			} else if (customerExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				customerExtLiability.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerExtLiability.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerExtLiability.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || customerExtLiability.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			customerExtLiability.setRecordStatus(customerDetails.getCustomer().getRecordStatus());
			customerExtLiability.setLoginDetails(customerDetails.getUserDetails());
			customerExtLiability.setLastMntOn(customerDetails.getCustomer().getLastMntOn());

			if (!customerExtLiability.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        customerExtLiability.getBefImage(), customerExtLiability));
			}
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

		if (list != null & list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType")
					        .invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					        || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (!(transType.equals(""))) {
						//check and change below line for Complete code
						Object befImg = object.getClass()
						        .getMethod("getBefImage", object.getClass().getClasses())
						        .invoke(object, object.getClass().getClasses());
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i))
						        .getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error(e);
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
	 * @throws CustomerNotFoundException 
	 */
	public Customer fetchCoreCustomerDetails(Customer customer) throws CustomerNotFoundException {
		return getCustomerInterfaceService().fetchCustomerDetails(customer);
	}	
	
	/**
	 * 
	 * @param customer
	 * @return
	 * @throws CustomerNotFoundException 
	 */
	public void updateProspectCustomer(Customer customer){
		getCustomerDAO().updateProspectCustomer(customer);
	}	

	/**
	 * Fetch Core Banking Customer details
	 */
	public Customer fetchCustomerDetails(Customer customer) {

		if (!StringUtils.trimToEmpty(customer.getCustTypeCode()).equals("")) {
			CustomerType customerType = getCustomerTypeDAO().getCustomerTypeById(
			        customer.getCustTypeCode(), "_Aview");
			if (customerType != null) {
				customer.setLovDescCustTypeCodeName(customerType.getCustTypeDesc());
				customer.setLovDescCustCtgType(customerType.getCustTypeCtg());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getCustDftBranch()).equals("")) {
			Branch branch = getBranchDAO().getBranchById(customer.getCustDftBranch(), "_Aview");
			if (branch != null) {
				customer.setLovDescCustDftBranchName(branch.getBranchDesc());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getCustParentCountry()).equals("")) {
			Country country = getCountryDAO().getCountryById(customer.getCustParentCountry(),
			        "_Aview");
			if (country != null) {
				customer.setLovDescCustParentCountryName(country.getCountryDesc());
				customer.setLovDescCustCOBName(country.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getCustRiskCountry()).equals("")) {
			Country country = getCountryDAO().getCountryById(customer.getCustRiskCountry(),
			        "_Aview");
			if (country != null) {
				customer.setLovDescCustRiskCountryName(country.getCountryDesc());
				customer.setLovDescCustResdCountryName(country.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getCustNationality()).equals("")) {
			NationalityCode nationality = getNationalityCodeDAO().getNationalityCodeById(customer.getCustNationality(),
			        "_Aview");
			if (nationality != null) {
				customer.setLovDescCustNationalityName(nationality.getNationalityDesc());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getCustEmpSts()).equals("")) {
			EmpStsCode empStsCode = getEmpStsCodeDAO().getEmpStsCodeById(customer.getCustEmpSts(),
			        "_Aview");
			if (empStsCode != null) {
				customer.setLovDescCustEmpStsName(empStsCode.getEmpStsDesc());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getCustBaseCcy()).equals("")) {
			Currency currency = getCurrencyDAO().getCurrencyById(customer.getCustBaseCcy(),
			        "_Aview");
			if (currency != null) {
				customer.setLovDescCustBaseCcyName(currency.getCcyDesc());
				customer.setLovDescCcyFormatter(currency.getCcyEditField());
			}
		}
		//Sector
		if (!StringUtils.trimToEmpty(customer.getCustSector()).equals("")) {
			Sector sector = getSectorDAO().getSectorById(customer.getCustSector(), "_Aview");
			if (sector != null) {
				customer.setLovDescCustSectorName(sector.getSectorDesc());
			}
			//Sub sector
			if (!StringUtils.trimToEmpty(customer.getCustSubSector()).equals("")) {
				SubSector subSector = getSubSectorDAO().getSubSectorById(customer.getCustSector(),
				        customer.getCustSubSector(), "_Aview");
				if (subSector != null) {
					customer.setLovDescCustSubSectorName(subSector.getSubSectorDesc());
				}
			}
		}
		
		if (!StringUtils.trimToEmpty(customer.getLovDescCustGroupCode()).equals("")) {
			CustomerGroup custGroup = getCustomerGroupDAO().getCustomerGroupByID(customer.getCustGroupID(), "_AView");
			if (custGroup!=null) {
				customer.setCustGroupID(custGroup.getCustGrpID());
				customer.setLovDesccustGroupIDName(custGroup.getCustGrpDesc()); 
				customer.setLovDescCustGroupCode( custGroup.getCustGrpCode()); 
			}
		}
		if (!StringUtils.trimToEmpty(customer.getLovDescCustRO1Name()).equals("")) {
			RelationshipOfficer officer = getRelationshipOfficerDAO().getRelationshipOfficerById(customer.getCustRO1(),  "_Aview");
			if (officer!=null) {
				customer.setCustRO1(officer.getROfficerCode());
				customer.setLovDescCustRO1Name(officer.getROfficerDesc());
			}
		}
		if (!StringUtils.trimToEmpty(customer.getLovDescCustRO2Name()).equals("")) {
			RelationshipOfficer officer = getRelationshipOfficerDAO().getRelationshipOfficerById(customer.getCustRO2(),  "_Aview");
			if (officer!=null) {
				customer.setCustRO2(officer.getROfficerCode());
				customer.setLovDescCustRO2Name(officer.getROfficerDesc());
			}
		}
		
		
		return customer;
	}

	    

	@Override
	public CustomerCategory getCustomerCategoryById(String custCtgCode){
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
	public String getNewProspectCustomerCIF(){
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
					if (!StringUtils.trimToEmpty(customerRating.getCustRatingType()).equals("") && !StringUtils.trimToEmpty(customerRating.getCustRatingCode()).equals("")) {
						RatingCode countryRating = getRatingCodeDAO().getRatingCodeById(customerRating.getCustRatingType(), customerRating.getCustRatingCode(),"");
						if (countryRating != null) {
							customerRating.setLovDesccustRatingCodeDesc(countryRating.getRatingCodeDesc());
						}
					}
					if (!StringUtils.trimToEmpty(customerRating.getCustRatingType()).equals("") && !StringUtils.trimToEmpty(customerRating.getCustRating()).equals("")) {	
						RatingCode obligotrating = getRatingCodeDAO().getRatingCodeById(customerRating.getCustRatingType(), customerRating.getCustRating(), "");
						if (obligotrating!=null) {
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

	@Override
    public List<CustomerRating> getCustomerRatingByCustId(long id, String type) {
	    return getCustomerRatingDAO().getCustomerRatingByCustId(id, type);
    }

}
